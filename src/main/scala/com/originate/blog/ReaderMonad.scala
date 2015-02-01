package com.originate.blog

/**
 * Created by michal on 2/1/15.
 */
object ReaderMonad extends App {
  {
    val triple = (i: Int) => i * 3
    triple(3)   // => 9

    val thricePlus2 = triple andThen (i => i + 2)
    thricePlus2(3)  // => 11

    val f = thricePlus2 andThen (i => i.toString)
    f(3)  // => "11"
  }
  {
    import scalaz.Reader

    val triple = Reader((i: Int) => i * 3)
    triple(3)   // => 9

    val thricePlus2 = triple map (i => i + 2)
    thricePlus2(3)  // => 11

    val f = for (i <- thricePlus2) yield i.toString
    f(3)  // => "11"


    val f2 = thricePlus2 map (i => i.toString)
    f2(3)  // => "11"
  }

}
