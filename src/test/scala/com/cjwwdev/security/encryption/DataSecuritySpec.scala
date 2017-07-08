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

import org.joda.time.{DateTime, DateTimeZone}
import org.scalatestplus.play.PlaySpec
import play.api.libs.json._

class DataSecuritySpec extends PlaySpec {

  val now = DateTime.now(DateTimeZone.UTC)
  
  class Setup {
    val testSecurity = DataSecurity

    case class TestModel(string: String, int: Int)
    object TestModel {
      implicit val format = Json.format[TestModel]
    }

    case class PartialModel(string: String, int: Int, createdAt: DateTime, builder: Option[String])
    object PartialModel {
      implicit val partialModelReads = new Reads[PartialModel] {
        override def reads(json: JsValue) = JsSuccess(
          PartialModel(
            string = json.\("string").as[String],
            int    = json.\("int").as[Int],
            createdAt = now,
            builder = Some("testUser")
          )
        )
      }
    }
  }

  "DataSecurity" should {
    "encrypt a case class and back again" in new Setup {
      val testModel = TestModel("testString", 12345)

      val enc = testSecurity.encryptType[TestModel](testModel)(TestModel.format)
      val dec = testSecurity.decryptIntoType[TestModel](enc)(TestModel.format)

      dec mustBe testModel
    }

    "encrypt a string and back again" in new Setup {
      val testString = "testString"

      val enc = testSecurity.encryptString(testString)
      val dec = testSecurity.decryptString(enc)

      dec mustBe testString
    }

    "encrypt a TestModel and decrypt as a PartialModel" in new Setup {
      val enc = testSecurity.encryptType[TestModel](TestModel("testString", 616))
      val dec = testSecurity.decryptIntoType[PartialModel](enc)

      dec.string mustBe "testString"
      dec.int mustBe 616
      dec.createdAt mustBe now
      dec.builder mustBe Some("testUser")
    }
  }
}
