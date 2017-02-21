package si.bss.tools.phpSerData

import util.parsing.combinator.JavaTokenParsers

/**
 * User: bss
 * Date: 6/19/13
 * Time: 8:11 PM
 */

object PHPValParser extends JavaTokenParsers {
  override def skipWhitespace: Boolean = true

  def int: Parser[PHPInt] = "i:" ~ wholeNumber ~ ";" ^^ {
    case _ ~ num ~ _ => PHPInt(num.toInt)
  }

  def double: Parser[PHPDouble] = "d:" ~ decimalNumber ~ ";" ^^ {
    case _ ~ num ~ _ => PHPDouble(num.toDouble)
  }

  def string: Parser[PHPString] = "s:" ~ wholeNumber ~ ":" ~ stringLiteral ~ ";" ^^ {
    case _ ~ strSize ~ _ ~ string ~ _ => {
      val str = string.drop(1).reverse.drop(1).reverse
      if (str.length == strSize.toInt) {
        PHPString(str)
      } else {
        throw new Exception("String length doesn't match")
      }
    }
  }

  def boolean: Parser[PHPBoolean] = "b:" ~ wholeNumber ~ ";" ^^ {
    case _ ~ num ~ _ => PHPBoolean {if (num == "0") false else true}
  }

  def nullVal: Parser[PHPNull] = "N;" ^^ {
    case _ => PHPNull()
  }

  def literal: Parser[PHPLiteral] = (int | double | string | boolean | nullVal)
  def phpvalue: Parser[PHPValue] = literal | array

  def arrayElement: Parser[(PHPLiteral, PHPValue)] = literal ~ phpvalue ^^ {
    case key ~ value => (key, value)
  }

  def array: Parser[PHPArray] = "a:" ~ wholeNumber ~ ":{" ~ rep(arrayElement) ~ "}" ^^ {
    case _ ~ arraySizeStr ~ _ ~ listElements ~ _ => {
      val arraySize = arraySizeStr.toInt

      if (arraySize == listElements.length) {
        PHPArray(listElements)
      } else {
        throw new Exception("Array element count does not match")
      }
    }
  }
}
