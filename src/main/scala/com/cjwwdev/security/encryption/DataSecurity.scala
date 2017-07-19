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
import play.api.libs.json.{JsResult, Json, Reads, Writes}
import play.api.Logger
import com.typesafe.config.ConfigFactory

import scala.util.{Failure, Success, Try}

object DataSecurity extends DataSecurity

trait DataSecurity extends DataCommon {

  private val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")

  def encryptType[T](data: T)(implicit writes: Writes[T]): String = {
    val json = Json.toJson(data).toString
    cipher.init(Cipher.ENCRYPT_MODE, keyToSpec)
    Try(Base64.encodeBase64URLSafeString(cipher.doFinal(json.getBytes("UTF-8")))) match {
      case Success(enc) => enc
      case Failure(e)   =>
        Logger.error("[DataSecurity] - [encryptType]: The input data type failed to be encrypted")
        throw e
    }
  }

  def decryptIntoType[T](data: String)(implicit reads: Reads[T]): JsResult[T] = {
    cipher.init(Cipher.DECRYPT_MODE, keyToSpec)
    Try(cipher.doFinal(Base64.decodeBase64(data))) match {
      case Success(decrypted) => Json.parse(new String(decrypted)).validate[T](reads)
      case Failure(e) =>
        Logger.error("[DataSecurity] - [decryptIntoType] : The input string has been failed decryption")
        throw e
    }
  }

  def encryptString(data: String): String = {
    cipher.init(Cipher.ENCRYPT_MODE, keyToSpec)
    Try(Base64.encodeBase64URLSafeString(cipher.doFinal(data.getBytes("UTF-8")))) match {
      case Success(encrypted) => encrypted
      case Failure(e) =>
        Logger.error("[DataSecurity] - [encryptString]: The input string has been failed encryption")
        throw e
    }
  }

  def decryptString(data: String): String = {
    cipher.init(Cipher.DECRYPT_MODE, keyToSpec)
    Try(cipher.doFinal(Base64.decodeBase64(data))) match {
      case Success(decrypted) => new String(decrypted)
      case Failure(e) =>
        Logger.error("[DataSecurity] - [decryptString]: The input string has failed decryption")
        throw e
    }
  }
}

trait DataCommon {
  private val env = ConfigFactory.load.getString("environment")

  private val LENGTH = 16

  private val KEY : String = Try(ConfigFactory.load.getString(s"$env.data-security.key")) match {
    case Success(conf) => conf
    case Failure(e) => throw e
  }

  private val SALT : String = Try(ConfigFactory.load.getString(s"$env.data-security.salt")) match {
    case Success(conf) => conf
    case Failure(e) => throw e
  }

  private[encryption] def keyToSpec: SecretKeySpec = {
    var keyBytes = (SALT + KEY).getBytes("UTF-8")
    val sha = MessageDigest.getInstance("SHA-512")
    keyBytes = sha.digest(keyBytes)
    keyBytes = util.Arrays.copyOf(keyBytes, LENGTH)
    new SecretKeySpec(keyBytes, "AES")
  }
}
