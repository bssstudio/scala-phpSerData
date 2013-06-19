scala-phpSerData
================

Scala library for interfacing with php serialized format (data only - no objects)


## Usage - parsing
```scala
import si.bss.tools.phpSerData._

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

## Usage - serializing
```scala
import si.bss.tools.phpSerData._

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
