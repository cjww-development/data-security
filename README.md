[![Apache-2.0 license](http://img.shields.io/badge/license-Apache-brightgreen.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)
[![Build Status](https://travis-ci.org/cjww-development/data-security.svg?branch=master)](https://travis-ci.org/cjww-development/data-security)
[ ![Download](https://api.bintray.com/packages/cjww-development/releases/data-security/images/download.svg) ](https://bintray.com/cjww-development/releases/data-security/_latestVersion)

data-security
=================

Mechanisms for encrypting and decrypting data

To utilise this library add this to your sbt build file

```sbtshell
"com.cjww-dev.libs" % "data-security_2.11" % "2.7.0" 
```

## About
#### DataSecurity.scala

**encryptType**<br>
Takes a parameter of type **T** and an implicit Json Writes[T] and returns an encrypted **String**.

***Example***

```scala
    case class ExampleCaseClass(str: String, int: Int)
    
    implicit val writer = Json.writes[ExampleCaseClass]

    val encString: String = DataSecurity.encryptType[ExampleCaseClass](ExampleCaseClass("exp", 616))
```


**decryptIntoType**<br>
Takes an encrypted **String** parameter and an implicit Json Reads[T] and returns a **T**.

***Example***

```scala
    case class ExampleCaseClass(str: String, int: Int)
    
    implicit val writer = Json.reads[ExampleCaseClass]

    val string: ExampleCaseClass = DataSecurity.decryptIntoType[ExampleCaseClass]("ENCRYPTED_STRING")
```


**encryptString**<br>
Takes a **String** parameter and returns an encrypted **String**.

***Example***

```scala
    val string: String = DataSecurity.encryptString("string")
```


**decryptString**<br>
Takes an encrypted **String** parameter and returns a **String**.

***Example***

```scala
    val string: String = DataSecurity.decryptString("ENCRYPTED_STRING")
```


#### SHA512.scala

Unlike DataSecurity the function in SHA512 in **not** reversible.

**encrypt**<br>
Takes a **String** parameter and returns an encrypted **String**.

***Example***

```scala
    val string: String = SHA512.encrypt("string")
```


#### Configuration

Add this config to your conf/application.conf file in your scala play project.

```hocon
    microservice {
      data-security {
        key = ""
        salt = ""
      }
    }
```

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html")


