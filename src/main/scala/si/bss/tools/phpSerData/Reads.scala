package si.bss.tools.phpSerData

import util.{Failure, Success, Try}

/**
 * User: bss
 * Date: 6/20/13
 * Time: 12:23 PM
 */

case class PHPReadsException(msg: String) extends Exception

trait Reads[A] {

  def reads(phpValue: PHPValue): Try[A]

}

object Reads extends DefaultReads

trait DefaultReads {

  implicit object IntReads extends Reads[Int] {
    def reads(phpValue: PHPValue): Try[Int] = {
      phpValue match {
        case PHPInt(i) => Success(i)
        case PHPString(s) => Try(s.toInt)
        case _ => Failure(new PHPReadsException("Wrong type"))
      }
    }
  }

  implicit object DoubleReads extends Reads[Double] {
    def reads(phpValue: PHPValue): Try[Double] = {
      phpValue match {
        case PHPDouble(d) => Success(d)
        case PHPString(s) => Try(s.toDouble)
        case _ => Failure(new PHPReadsException("Wrong type"))
      }
    }
  }

  implicit object StringReads extends Reads[String] {
    def reads(phpValue: PHPValue): Try[String] = {
      phpValue match {
        case PHPString(s) => Success(s)
        case _ => Failure(new PHPReadsException("Wrong type"))
      }
    }
  }

  implicit object BooleanReads extends Reads[Boolean] {
    def reads(phpValue: PHPValue): Try[Boolean] = {
      phpValue match {
        case PHPInt(i) => Success(i != 0)
        case PHPDouble(d) => Success(d != 0.0)
        case PHPString(s) => Success(s != "false" && s != "" && s != "0" && s != "0.0")
        case _ => Failure(new PHPReadsException("Unable to derive a boolean value from this type"))
      }
    }
  }


  implicit def SeqReads[A: Reads] = new Reads[Seq[A]] {
    def reads(phpValue: PHPValue): Try[Seq[A]] = {
      phpValue match {
        case PHPArray(a) => Try {
          a.map {
            case (_, value) => PHPVal.fromPHPVal[A](value).get
          }.toSeq
        }
        case _ => Failure(new PHPReadsException("Wrong type"))
      }
    }
  }

  implicit def MapReads[K: Reads, V: Reads] = new Reads[Map[K, V]] {
    def reads(phpValue: PHPValue): Try[Map[K, V]] = {
      phpValue match {
        case PHPArray(a) => Try {
          a.map {
            case (key, value) => {
              (PHPVal.fromPHPVal[K](key).get -> PHPVal.fromPHPVal[V](value).get)
            }
          }.toMap
        }
        case _ => Failure(new PHPReadsException("Wrong type"))
      }
    }
  }

  implicit def StringMapReads[A: Reads] = MapReads[String, A]
  implicit def IntMapReads[A: Reads] = MapReads[Int, A]

}