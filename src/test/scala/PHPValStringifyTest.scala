import org.scalatest.FunSuite
import si.bss.tools.phpSerData._

/**
 * User: bss
 * Date: 6/19/13
 * Time: 7:52 PM
 */

class PHPValStringifyTest extends  FunSuite {

  test("Int Serialization") {
    val phpvalue = PHPInt(123)
    val strshould = "i:123;"
    val strgot = PHPVal.stringify(phpvalue)
    assert(strgot == strshould)
  }

  test("Double Serialization") {
    val phpvalue = PHPDouble(-123.123)
    val strshould = "d:-123.123;"
    val strgot = PHPVal.stringify(phpvalue)
    assert(strgot == strshould)
  }

  test("Boolean Serialization") {
    val phpvalue = PHPBoolean(false)
    val strshould = "b:0;"
    val strgot = PHPVal.stringify(phpvalue)
    assert(strgot == strshould)
  }

  test("String Serialization") {
    val phpvalue = PHPString("some crazy guy did php in scala! That's just sick!")
    val strshould = """s:50:"some crazy guy did php in scala! That's just sick!";"""
    val strgot = PHPVal.stringify(phpvalue)
    assert(strgot == strshould)
  }

  test("Array Serialization") {
    val phpvalue = PHPArray(List( (PHPInt(0) -> PHPDouble(5)), (PHPInt(1) -> PHPDouble(6)) ))
    val strshould = """a:2:{i:0;d:5.0;i:1;d:6.0;}"""
    val strgot = PHPVal.stringify(phpvalue)
    assert(strgot == strshould)
  }

  test("Associative array Serialization") {
    val phpvalue = PHPArray(List( (PHPString("one") -> PHPDouble(5)), (PHPString("two") -> PHPDouble(6)) ))
    val strshould = """a:2:{s:3:"one";d:5.0;s:3:"two";d:6.0;}"""
    val strgot = PHPVal.stringify(phpvalue)
    assert(strgot == strshould)
  }

}
