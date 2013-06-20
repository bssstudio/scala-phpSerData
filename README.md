scala-phpSerData
================

Scala library for interfacing with php serialized format (data only - no objects)

## Usage
```scala
import si.bss.tools.phpSerData._
```


## Parsing
```scala
val phpSerializedContent = """a:2:{s:3:"one";d:5.0;s:3:"two";d:6.0;}"""
val parsed = PHPVal.parse(phpSerializedContent)

if (parsed.successful) {
  val phpValue: PHPValue = parsed.get
  val prettyPrintedString = PHPVal.prettyPrint(phpValue)
  
  println(prettyPrintedString)
  /* will print:
  PHPArray {
    PHPString("one") => PHPDouble(5.0),
    PHPString("two") => PHPDouble(6.0)
  }
  */
}
```

## Serializing
```scala
val phpValue = PHPArray(
                  List(
                    (PHPString("one") -> PHPDouble(5)), 
                    (PHPString("two") -> PHPDouble(6)) 
                  )
              )
              
val serialized = PHPVal.stringify(phpValue)
println(serialized)
/* will print:
a:2:{s:3:"one";d:5.0;s:3:"two";d:6.0;}
*/
```

## Reading to Scala types

This is similar to JSON Reads in Play framework.
```scala
// The same thing as above
val phpSerializedContent = """a:2:{s:3:"one";d:5.0;s:3:"two";d:6.0;}"""
val parsed = PHPVal.parse(phpSerializedContent)

if (parsed.successful) {
  val phpValue: PHPValue = parsed.get
  
  //but now we want to get a map out of it
  val assocMap: Map[String,Double] = PHPVal.fromPHPVal[Map[String,Double]](phpValue)
}
```

You can also define Reads for your own type
```scala
//Your ususal case class
case class ReadsTest(a: Int, b: String)

//Implement the Reads[A] for your type
implicit val myTypeReads = new Reads[ReadsTest] {
  def reads(phpValue: PHPValue): Try[ReadsTest] = {
    phpValue match {
      case PHPArray(ar) => {
        val atribs = ar.toMap
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

//You get this from the parsing step
val parsedValue = PHPArray(List(
  (PHPString("a") -> PHPInt(123)),
  (PHPString("b") -> PHPString("onetwothree"))
))

//And voila:
val read = PHPVal.fromPHPVal[ReadsTest](parsedValue)
```


## Writing from Scala types

Again, this is very similar to JSON in Play

```scala
val aList = List("one", "two", "three")
val phpValue = PHPVal.toPHPVal(value)
/* It now contains:
PHPArray(List(
            (PHPInt(0) -> PHPString("one")),
            (PHPInt(1) -> PHPString("two")),
            (PHPInt(2) -> PHPString("three"))
        ))
*/
```

And of course custom Writers ala carte

```scala
//Your type again ;)
case class WriteTest(a: Int, b: String)

//define the custom Writer
implicit val writeTestWrites = new Writes[WriteTest] {
  def writes(o: WriteTest): PHPValue = {
    PHPArray(List(
      (PHPString("a") -> PHPInt(o.a)),
      (PHPString("b") -> PHPString(o.b))
    ))
  }
}

//Instance of your type
val value = WriteTest(123, "onetwothree")

//And you transform into PHPValue ready to be serialized
val phpValue = PHPVal.toPHPVal(value)

//Serialized
val serialized = PHPVal.stringify(phpValue)
```

## Combinators

Parser combinators for arrays

```scala
val i = Format[Int](PHPString("i"))
i.writes(1) //PHPArray(List((PHPString(i),PHPInt(1))))

val s = Format[String](PHPString("s"))
val is = i ~ s

val x1 = is.writes( (1, "hai") ) //PHPArray(List((PHPString(i),PHPInt(1)), (PHPString(s),PHPString(hai))))

is reads x1 //Success((1,hai))

//or even
case class Wrap(n: Int, msg: String)

val w = is.map((Wrap.apply _).tupled, Wrap.unapply _)

w reads x1 //Success(Wrap(1,hai))

w writes res2.get //PHPArray(List((PHPString(i),PHPInt(1)), (PHPString(s),PHPString(hai))))
```

