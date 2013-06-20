import org.scalatest.FunSuite
import si.bss.tools.phpSerData._
import si.bss.tools.phpSerData.PHPDouble
import si.bss.tools.phpSerData.PHPInt
import si.bss.tools.phpSerData.PHPString

/**
 * User: bss
 * Date: 6/20/13
 * Time: 12:44 PM
 */

class PHPValWritesTest extends FunSuite {

  test("Writes Int") {
    val value: Int = 12345
    val should = PHPInt(value)
    val writen = PHPVal.toPHPVal(value)
    assert(writen === should)
  }

  test("Writes Double") {
    val value = 456.64
    val should = PHPDouble(value)
    val writen = PHPVal.toPHPVal(value)
    assert(writen === should)
  }

  test("Writes String") {
    val value = "Crazy, right?"
    val should = PHPString(value)
    val writen = PHPVal.toPHPVal(value)
    assert(writen === should)
  }

  test("Writes Boolean") {
    val value = true
    val should = PHPInt(1)
    val writen = PHPVal.toPHPVal(value)
    assert(writen === should)
  }

  test("Writes List") {
    val value = List("one", "two", "three")
    val should = PHPArray(List(
                    (PHPInt(0) -> PHPString("one")),
                    (PHPInt(1) -> PHPString("two")),
                    (PHPInt(2) -> PHPString("three"))
                ))

    val writen = PHPVal.toPHPVal(value)
    assert(writen === should)
  }

  test("Writes Map (assoc array)") {
    val value = Map( ("zero" -> "smth0"), ("one" -> "smth1") )
    val should = PHPArray(List(
      (PHPString("zero") -> PHPString("smth0")),
      (PHPString("one") -> PHPString("smth1"))
    ))

    val writen = PHPVal.toPHPVal(value)
    assert(writen === should)
  }

  test("Custom class writes") {

    case class WriteTest(a: Int, b: String)
    val value = WriteTest(123, "onetwothree")

    implicit val writeTestWrites = new Writes[WriteTest] {
      def writes(o: WriteTest): PHPValue = {
        PHPArray(List(
          (PHPString("a") -> PHPInt(o.a)),
          (PHPString("b") -> PHPString(o.b))
        ))
      }
    }

    val should = PHPArray(List(
                  (PHPString("a") -> PHPInt(123)),
                  (PHPString("b") -> PHPString("onetwothree"))
                ))

    val writen = PHPVal.toPHPVal(value)
    assert(writen === should)
  }

}
