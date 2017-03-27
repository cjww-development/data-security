// Copyright (C) 2011-2012 the original author or authors.
// See the LICENCE.txt file distributed with this work for additional
// information regarding copyright ownership.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.cjwwdev.security.encryption

import java.util
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

import org.apache.commons.codec.binary.Base64
import play.api.libs.json.{Format, JsValue, Json}
import com.typesafe.config.ConfigFactory
import play.api.Logger

import scala.util.{Failure, Success, Try}

object DataSecurity extends DataSecurity

trait DataSecurity extends DataCommon {
  def encryptData[T](data: T)(implicit format: Format[T]): Option[String] = {
    def scramble(json: JsValue): Option[String] = {
      val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
      cipher.init(Cipher.ENCRYPT_MODE, keyToSpec)
      Try(Base64.encodeBase64URLSafeString(cipher.doFinal(json.toString.getBytes("UTF-8")))) match {
        case Success(encrypted) => Some(encrypted)
        case Failure(ex) =>
          Logger.error("[DataSecurity] - [encryptData] : INPUT FAILED ENCRYPTION")
          ex.printStackTrace()
          None
      }
    }
    scramble(Json.toJson(data))
  }

  def decryptInto[T](data: String)(implicit format: Format[T]): Option[T] = {
    val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
    cipher.init(Cipher.DECRYPT_MODE, keyToSpec)
    Try(cipher.doFinal(Base64.decodeBase64(data))) match {
      case Success(decrypted) => validate[T](new String(decrypted))
      case Failure(ex) =>
        Logger.error("[DataSecurity] - [decryptInto] : DECRYPTION FAILED")
        ex.printStackTrace()
        None
    }
  }
}

trait DataCommon {

  private val BACKUP_KEY = "BACK_UP_KEY"
  private val BACKUP_SALT = "BACK_UP_SALT"

  private val KEY : String = {
    Try(ConfigFactory.load.getString("data-security.key")) match {
      case Success(config) => config
      case Failure(ex) =>
        Logger.error("[DataCommon] - [KEY] : Security key not found; reverting to back up key")
        BACKUP_KEY
    }
  }

  private val SALT : String = {
    Try(ConfigFactory.load.getString("data-security.salt")) match {
      case Success(config) => config
      case Failure(ex) =>
        Logger.error("[DataCommon] - [SALT] : Security salt not found; reverting to back up salt")
        BACKUP_SALT
    }
  }

  private val LENGTH = 16

  def keyToSpec: SecretKeySpec = {
    var keyBytes = (SALT + KEY).getBytes("UTF-8")
    val sha = MessageDigest.getInstance("SHA-1")
    keyBytes = sha.digest(keyBytes)
    keyBytes = util.Arrays.copyOf(keyBytes, LENGTH)
    new SecretKeySpec(keyBytes, "AES")
  }

  def validate[T](unlocked: String)(implicit format: Format[T]): Option[T] = {
    Json.parse(unlocked).validate[T].fold(
      errors => None,
      valid => Some(valid)
    )
  }
}
