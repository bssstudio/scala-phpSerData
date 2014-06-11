package si.bss.tools.phpSerData

import org.apache.commons.lang.StringEscapeUtils.escapeJava
import util.Try

/**
 * User: bss
 * Date: 6/19/13
 * Time: 7:42 PM
 */

object PHPVal {

  def stringify(value: PHPValue): String = {
    value match {
      case i: PHPInt => "i:"+i.i+";"
      case d: PHPDouble => "d:"+d.d+";"
      case b: PHPBoolean => "b:"+{if (b.b) "1" else "0"}+";"
      case s: PHPString => "s:"+s.s.length+":"+'"'+escapeString(s.s)+'"'+";"
      case a: PHPArray => {
        "a:"+a.a.length + ":{"+
        (
          a.a.map {
            case (i,v) => stringify(i)+stringify(v)
          }.mkString
        )+"}"
      }

    }
  }

  def prettyPrint(value: PHPValue, depth: Int = 0): String = {

    (value match {
      case i: PHPInt => "PHPInt("+i.i+")"
      case d: PHPDouble => "PHPDouble("+d.d+")"
      case b: PHPBoolean => "PHPBoolean("+b.b+")"
      case s: PHPString => "PHPString("+'"'+escapeString(s.s)+'"'+")"
      case a: PHPArray => {
        "PHPArray {\n"+
          (a.a.map {
            case (key, value) => {
              val header = "  " * (depth + 1) + prettyPrint(key) + " => "
              val vals = prettyPrint(value,depth + 1).lines.toList
              header + vals.head +
              (
                if (vals.tail.length > 0) {
                  "\n" + vals.tail.mkString("\n"+"  "*(depth))
                } else { "" }
              )
            }
          }.mkString(",\n"))+
          "\n"+"  "*depth +"}"
      }

    })
  }

  def escapeString(s: String): String = {
    escapeJava(s)
  }

  def parse(str: String): PHPValParser.ParseResult[PHPValue] = {
    val parsed = PHPValParser.parseAll(PHPValParser.phpvalue, str)
    parsed
  }

  def toPHPVal[T](o: T)(implicit tphp: Writes[T]): PHPValue = tphp.writes(o)

  def fromPHPVal[T](phpValue: PHPValue)(implicit fphp: Reads[T]): Try[T] = fphp.reads(phpValue)

}
