package si.bss.tools.phpSerData

import scala.util.{Failure, Try}

trait Format[A] extends Reads[A] with Writes[A] {
  self =>
  import FormatImpl._

  def readsEntry(a: PHPArray): Try[A]

  def reads(a: PHPValue) = a match {
    case a: PHPArray => readsEntry(a)
    case other => Failure(new ClassCastException(s"$other cannot be cast to PHPArray"))
  }

  def writesArray(a: A): Seq[(PHPLiteral, PHPValue)]

  def writes(a: A) = PHPArray(writesArray(a))

  def map[B](f: A => B, fi: B => Option[A]) = new Format[B] {
    def readsEntry(a: PHPArray): Try[B] = self readsEntry a map f
    def writesArray(a: B): Seq[(PHPLiteral, PHPValue)] = self writesArray fi(a).get
  }

  def ~[B,C](that: Format[B])(implicit conversion: Conversion[A,B,C]): Format[C] = new Format[C]{

    def readsEntry(phpValue: PHPArray): Try[C] = for {
      a <- self readsEntry phpValue //todo
      b <- that reads phpValue
    } yield conversion.f(a,b)

    def writesArray(c: C) = {
      val (a,b) = conversion fi c
      val head = self writesArray a
      val tail = that writesArray b
      head ++: tail
    }
  }
}

object Format {
  def apply[A](field: PHPLiteral)(implicit r: Reads[A], w: Writes[A]): Format[A] = new Format[A]{
    def readsEntry(a: PHPArray): Try[A] = for {
      raw <- Try(a.a.toMap.apply(field))
      value <- r reads raw
    } yield value

    def writesArray(a: A): Seq[(PHPLiteral, PHPValue)] = Seq( (field, w writes a))
  }
}

object FormatImpl {

  case class Conversion[A,B,C](f: (A,B) => C, fi: C => (A,B))

  trait C2 {
    implicit def c2[A,B] = Conversion[A,B,(A,B)]( (_,_), x => x )
  }
  trait C3 extends C2 {
    implicit def c3[A,B,C] = Conversion[(A,B),C,(A,B,C)]( { case ((a,b),c)=>(a,b,c) }, { case (a,b,c) => ((a,b),c) } )
  }
  trait C4 extends C3 {
    implicit def c4[A,B,C,D] = Conversion[(A,B,C),D,(A,B,C,D)]( { case ((a,b,c),d)=>(a,b,c,d) }, { case (a,b,c,d) => ((a,b,c),d) } )
  }
  trait C5 extends C4 {
    implicit def c5[A,B,C,D,E] = Conversion[(A,B,C,D),E,(A,B,C,D,E)]( { case ((a,b,c,d),e)=>(a,b,c,d,e) }, { case (a,b,c,d,e) => ((a,b,c,d),e) } )
  }

  object Conversion extends C5
}
