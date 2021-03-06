# Jackson Case Class Module

**This project is currently unused by Mesosphere projects and will no longer be maintained. We will not be accepting contributions or releasing new versions of this. You are welcome to fork the repo for your own use.**

Provides deserialization support for Scala case classes, including proper handling of default values.

Assumes that the default case class constructor's arguments are sufficient for deserialization. 

**SBT:**

```scala
resolvers += "Mesosphere Public Repository" at "http://downloads.mesosphere.io/maven"
libraryDependencies += "mesosphere" %% "jackson-case-class-module" % "0.1.2"
```

**Maven:**

```xml
<repository>
  <id>mesosphere-public-repo</id>
  <name>Mesosphere Public Repo</name>
  <url>http://downloads.mesosphere.io/maven</url>
</repository>

<dependency>
  <groupId>mesosphere</groupId>
  <artifactId>jackson-case-class-module_2.10</artifactId>
  <version>0.1.2</version>
</dependency>
```

## Usage
_NOTE: when using Jackson that case classes should be at the top level of your file to avoid issues that prevent jackson from being able to instantiate a new instance of your case class_

```scala
import mesosphere.jackson.CaseClassModule
import com.fasterxml.jackson.module.scala.DefaultScalaModule

val mapper = new ObjectMapper
mapper.registerModule(DefaultScalaModule)
mapper.registerModule(CaseClassModule)

case class Person(name: String, age: Integer = 30)

val readResult = mapper.readValue(
  """{ "name": "Alfonso" }""",
  classOf[Person]
)

assert(readResult.age == 30) // hooray
```
