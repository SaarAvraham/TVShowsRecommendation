package com.saar.spark

object App1 {
  def main(args : Array[String]) {

    val favNums = new Array[Int](20)

    for(i <- 0 to 19){
      favNums(i) = i;
    }

    favNums.sortWith((i1,i2) => i1 > i2)
      .foreach(i => println(i))
  }
}
