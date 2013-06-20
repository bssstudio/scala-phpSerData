package si.bss.tools.phpSerData

/**
 * User: bss
 * Date: 6/20/13
 * Time: 12:25 PM
 */

trait Writes[-A] {

  def writes(o: A): PHPValue

}

object Writes extends DefaultWrites


trait DefaultWrites {

  implicit object IntWrites extends Writes[Int] {
    def writes(o: Int): PHPValue = PHPInt(o)
  }

  implicit object DoubleWrites extends Writes[Double] {
    def writes(o: Double): PHPValue = PHPDouble(o)
  }

  implicit object StringWrites extends Writes[String] {
    def writes(o: String): PHPValue = PHPString(o)
  }

  implicit object BooleanWrites extends Writes[Boolean] {
    def writes(o: Boolean): PHPValue = PHPInt( if (o) {1} else {0} )
  }

  implicit def SeqWrites[A: Writes] = new Writes[Seq[A]] {
    def writes(o: Seq[A]): PHPValue = {
      PHPArray {
        o.zipWithIndex.map(el => (PHPInt(el._2) -> PHPVal.toPHPVal[A](el._1)))
      }
    }
  }

  implicit def MapWrites[A: Writes] = new Writes[Map[String, A]] {
    def writes(o: Map[String, A]): PHPValue = {
      PHPArray {
        o.toSeq.map(el => (PHPString(el._1) -> PHPVal.toPHPVal[A](el._2)))
      }
    }
  }

}
