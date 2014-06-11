package si.bss.tools.phpSerData

/**
 * User: bss
 * Date: 6/19/13
 * Time: 7:23 PM
 */

sealed trait PHPValue
sealed trait PHPLiteral extends PHPValue

case class PHPInt(i: Int) extends PHPLiteral
case class PHPDouble(d: Double) extends PHPLiteral
case class PHPString(s: String) extends PHPLiteral
case class PHPBoolean(b: Boolean) extends PHPLiteral
case class PHPArray(a: Seq[(PHPLiteral, PHPValue)]) extends PHPValue
