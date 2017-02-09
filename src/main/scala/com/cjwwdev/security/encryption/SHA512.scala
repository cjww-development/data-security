// Copyright (C) 2016-2017 the original author or authors.
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

import java.security.MessageDigest

object SHA512 extends SHA512

trait SHA512 {
  def encrypt(plainText: String) : String = {
    val sha512 = MessageDigest.getInstance("SHA-512")
    val bytes = plainText.getBytes()
    val hash = sha512.digest(bytes)

    var result = ""

    def loopArray(increment: Int): String = {
      if(increment >= hash.length - 1) {
        val x = hash(increment)
        result ++= "%02x".format(x).toString
        result
      } else {
        val b = hash(increment)
        result ++= "%02x".format(b).toString
        loopArray(increment + 1)
      }
    }
    loopArray(0)
  }
}
