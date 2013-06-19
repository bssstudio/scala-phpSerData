import org.scalatest.FunSuite
import si.bss.tools.phpSerData._

/**
 * User: bss
 * Date: 6/19/13
 * Time: 11:25 PM
 */

class PHPValueParserTest extends FunSuite {

  test("Parsing Int") {
    val should = PHPInt(1234)
    val string = "i:1234;"
    val parsed = PHPValParser.parseAll(PHPValParser.phpvalue, string)

    assert(parsed.successful)
    val got = parsed.get
    assert(got == should)
  }

  test("Parsing Double") {
    val should = PHPDouble(1234.567)
    val string = "d:1234.567;"
    val parsed = PHPValParser.parseAll(PHPValParser.phpvalue, string)

    assert(parsed.successful)
    val got = parsed.get
    assert(got == should)
  }

  test("Parsing String") {
    val should = PHPString("I am a little tea pot's holder")
    val string = """s:30:"I am a little tea pot's holder";"""
    val parsed = PHPValParser.parseAll(PHPValParser.phpvalue, string)

    assert(parsed.successful)
    val got = parsed.get
    assert(got == should)
  }

  test("Parsing Array") {
    val should = PHPArray(List( (PHPInt(0), PHPDouble(5)), (PHPInt(1), PHPDouble(6)) ))
    val string = """a:2:{i:0;d:5.0;i:1;d:6.0;}"""
    val parsed = PHPValParser.parseAll(PHPValParser.phpvalue, string)

    assert(parsed.successful)
    val got = parsed.get
    assert(got == should)
  }

  test("Parsing Associative Array") {
    val should = PHPArray(List( (PHPString("one") -> PHPDouble(5)), (PHPString("two") -> PHPDouble(6)) ))
    val string = """a:2:{s:3:"one";d:5.0;s:3:"two";d:6.0;}"""
    val parsed = PHPValParser.parseAll(PHPValParser.phpvalue, string)

    assert(parsed.successful)
    val got = parsed.get
    assert(got == should)
  }

  test("Parse-Stringify idempotence") {
    val string = """a:4:{s:4:"accs";a:33:{i:0;s:10:"*#!TEST!#*";i:1;s:11:"38628220571";i:2;s:11:"38628280200";i:3;s:11:"38628280571";i:4;s:11:"38628280828";i:5;s:11:"38628280829";i:6;s:11:"38628842911";i:7;s:11:"38634922500";i:8;s:11:"38634922501";i:9;s:11:"38635777028";i:10;s:11:"38635777056";i:11;s:11:"38635777082";i:12;s:11:"38682800010";i:13;s:11:"38682800035";i:14;s:11:"38682800700";i:15;s:11:"38682800804";i:16;s:11:"38682800808";i:17;s:11:"38682800818";i:18;s:11:"38682800819";i:19;s:11:"38682800837";i:20;s:10:"aeBuquoh4e";i:21;s:10:"e7kkR2KxwY";i:22;s:10:"eG6d9F6w3L";i:23;s:10:"gzt7ir6rdz";i:24;s:10:"jl8JZt4Bgv";i:25;s:10:"laihie2Ohr";i:26;s:10:"no3Eexahph";i:27;s:10:"ofi2chaiTh";i:28;s:10:"qDC4eec16I";i:29;s:10:"tf0mTIXVA3";i:30;s:10:"TOod9HWZQE";i:31;s:11:"w4JF7pvqLZM";i:32;s:11:"w4tmfQzha8y";}s:14:"pregled_porabe";i:1;s:14:"pregled_klicev";i:1;s:10:"nastavitve";i:1;}"""
    val parsed = PHPValParser.parseAll(PHPValParser.phpvalue, string)
    assert(parsed.successful)
    val stringified = PHPVal.stringify(parsed.get)
    assert(stringified == string)
  }

  test("Parsing some garbage") {
    val string = "asdjasldd sdfsdaf"
    val parsed = PHPVal.parse(string)
    assert(!parsed.successful)
  }
}
