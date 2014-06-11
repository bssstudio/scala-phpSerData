import org.scalatest.FunSuite
import si.bss.tools.phpSerData._
import si.bss.tools.phpSerData.PHPArray
import si.bss.tools.phpSerData.PHPDouble
import si.bss.tools.phpSerData.PHPInt
import util.{Failure, Try}

/**
 * User: bss
 * Date: 6/20/13
 * Time: 1:23 PM
 */

class PHPValReadsTest extends FunSuite {

  test("Int reads") {
    val value = PHPInt(1234)
    val should = 1234
    val read = PHPVal.fromPHPVal[Int](value)
    assert(read.isSuccess)
    assert(read.get == should)
  }

  test("Double reads") {
    val value = PHPDouble(345.65)
    val should = 345.65
    val read = PHPVal.fromPHPVal[Double](value)
    assert(read.isSuccess)
    assert(read.get == should)
  }

  test("Boolean reads") {
    val value = PHPBoolean(true)
    val should = true
    val read = PHPVal.fromPHPVal[Boolean](value)
    assert(read.isSuccess)
    assert(read.get == should)
  }

  test("Boolean reads from int") {
    val value = PHPInt(1)
    val should = true
    val read = PHPVal.fromPHPVal[Boolean](value)
    assert(read.isSuccess)
    assert(read.get == should)
  }

  test("String reads") {
    val value = PHPInt(1234)
    val should = 1234
    val read = PHPVal.fromPHPVal[Int](value)
    assert(read.isSuccess)
    assert(read.get == should)
  }


  test("List reads") {
    val should = List("one", "two", "three")
    val value = PHPArray(List(
      (PHPInt(0) -> PHPString("one")),
      (PHPInt(1) -> PHPString("two")),
      (PHPInt(2) -> PHPString("three"))
    ))

    val read = PHPVal.fromPHPVal[Seq[String]](value)
    assert(read.isSuccess)
    assert(read.get == should)
  }

  test("Map (assoc array) reads") {
    val should = Map( ("zero" -> "smth0"), ("one" -> "smth1") )
    val value = PHPArray(List(
      (PHPString("zero") -> PHPString("smth0")),
      (PHPString("one") -> PHPString("smth1"))
    ))

    val read = PHPVal.fromPHPVal[Map[String,String]](value)
    assert(read.isSuccess)
    assert(read.get == should)
  }

  test("Custom class reads") {

    case class ReadsTest(a: Int, b: String)
    val should = ReadsTest(123, "onetwothree")

    implicit val writeTestReads = new Reads[ReadsTest] {

      def reads(phpValue: PHPValue): Try[ReadsTest] = {
        phpValue match {
          case a: PHPArray => {
            val atribs = a.a.toMap
            val atribAT = Try(atribs(PHPString("a")))
            val atribBT = Try(atribs(PHPString("b")))
            for {
              atribA <- atribAT
              atribB <- atribBT
              atrA <- PHPVal.fromPHPVal[Int](atribA)
              atrB <- PHPVal.fromPHPVal[String](atribB)
            } yield ReadsTest(atrA,atrB)
          }
          case _ => Failure(new Exception("Not an array, can not parse my type out"))
        }
      }
    }

    val value = PHPArray(List(
      (PHPString("a") -> PHPInt(123)),
      (PHPString("b") -> PHPString("onetwothree"))
    ))

    val read = PHPVal.fromPHPVal[ReadsTest](value)
    assert(read.isSuccess)
    assert(read.get == should)

  }
}
