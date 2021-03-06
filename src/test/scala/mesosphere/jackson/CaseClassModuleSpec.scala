package mesosphere.jackson

import com.fasterxml.jackson.module.scala.DefaultScalaModule
import java.lang.{ Double ⇒ JDouble, Integer ⇒ JInt }

object CaseClassModuleSpec {
  case class Person(name: String, age: JInt)
  case class Defaults(x: JDouble = math.E, y: JDouble = math.Pi, z: String = "foobar")
  case class ComplexDefaults(xs: Seq[JInt] = Seq(1, 2, 3))
  case class NestedDefaults(defaults: ComplexDefaults = ComplexDefaults(Seq(5)))
  case class WithOption(n: Option[JInt])
  case class WithBoolean(b: Boolean = true)
  case class WithBooleanOpt(b: Option[Boolean] = Some(true))
  case class OptionDefault(x: Option[Int] = Some(5))
  case class WithArray(a: Array[JInt])
  case class WithArrayDefault(a: Array[JInt] = Array(1, 2, 3))
  case class GenericHolder[T](holder: T)
}

class CaseClassModuleSpec extends Spec with JacksonHelpers {

  import CaseClassModuleSpec._

  val module = new DefaultScalaModule with CaseClassModule

  "CaseClassModule" should "deserialize basic case classes" in {
    deserialize[Person]("""{"name": "Jaime", "age": 17}""") should equal (Person("Jaime", 17))
  }

  it should "respect default values for basic case classes" in {
    deserialize[Defaults]("{}") should equal (Defaults(math.E, math.Pi, "foobar"))
  }

  it should "respect default values for complex case classes" in {
    deserialize[ComplexDefaults]("{}") should equal (ComplexDefaults(Seq(1, 2, 3)))
  }

  it should "respect default values for nested case classes" in {
    deserialize[NestedDefaults]("{}") should equal (NestedDefaults(ComplexDefaults(Seq(5))))
    deserialize[NestedDefaults]("""{"defaults": {} }""") should equal (NestedDefaults(ComplexDefaults(Seq(1, 2, 3))))
  }

  it should "be liberal in its interpretation of numbers" in {
    deserialize[Defaults]("""{ "x": "5.0" }""") should equal (Defaults(x = 5.0))
  }

  it should "read optional values" in {
    deserialize[WithOption]("""{ "n": 5 }""") should equal (WithOption(Some(5)))
    deserialize[WithOption]("""{ "n": "5" }""") should equal (WithOption(Some(5)))
    deserialize[WithOption]("""{}""") should equal (WithOption(None))
  }

  it should "respect default values for optional arguments" in {
    deserialize[OptionDefault]("{}") should equal (OptionDefault(Some(5)))
    deserialize[OptionDefault]("""{ "x": 5 }""") should equal (OptionDefault(Some(5)))
  }

  it should "deserialize array" in {
    deserialize[WithArray]("""{ "a": [1, 2, 3] }""").a should contain theSameElementsAs Array(1, 2, 3)
  }

  it should "respect default values for arrays" in {
    deserialize[WithArrayDefault]("{}").a should contain theSameElementsAs Array(1, 2, 3)
  }

  it should "deserialize generic holder with Int list" in {
    deserialize[GenericHolder[List[Int]]]("""{ "holder": [1, 2, 3]}""") should equal(GenericHolder(List(1, 2, 3)))
  }

  it should "deserialize generic holder with case class without default values" in {
    val json = """{ "holder": [
      |{ "name": "name1", "age": 1},
      |{ "name": "name2", "age": 2}
      |]}""".stripMargin

    val expected = GenericHolder(List(Person("name1", 1), Person("name2", 2)))

    deserialize[GenericHolder[List[Person]]](json) should equal(expected)
  }

  it should "deserialize generic holder with case class with default values" in {
    val json = """{ "holder": [
                 |{ "x": 1, "y": 2},
                 |{ "z": "test"}
                 |]}""".stripMargin

    val expected = GenericHolder(Seq(Defaults(x = 1.0, y = 2.0), Defaults(z = "test")))

    deserialize[GenericHolder[Seq[Defaults]]](json) should equal(expected)
  }

  it should "deserialize boolean with default true value" ignore {
    val json = "{}"

    deserialize[WithBoolean](json).b shouldBe true
  }

  it should "deserialize boolean opt" in {
    val json = "{}"

    println(deserialize[WithBooleanOpt](json).b)
  }
}
