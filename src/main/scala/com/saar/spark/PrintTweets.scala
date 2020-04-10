

package com.saar.spark

import java.sql.DriverManager

import Utilities._
import org.apache.spark.streaming.twitter.TwitterUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

/** Simple application to listen to a stream of Tweets and print them out */
object PrintTweets {
 
  /** Our main function where the action happens */
  def main(args: Array[String]) {

    // Configure Twitter credentials using twitter.txt
    setupTwitter()
    
    // Set up a Spark streaming context named "PrintTweets" that runs locally using
    // all CPU cores and one-second batches of data
    val ssc = new StreamingContext("local[*]", "PrintTweets", Seconds(1))
    
    // Get rid of log spam (should be called after the context is set up)
    setupLogging()

    // Create a DStream from Twitter using our streaming context
    val tweets = TwitterUtils.createStream(ssc, None)

//    // Now extract the text of each status update into RDD's using map()
//    val statuses = tweets.map(status => status.getText())
//
////    // Print out the first ten
////    statuses.print()
//    val statusesCount = tweets.count()

    tweets.foreachRDD{
      rdd =>
        rdd.foreachPartition { part =>
          val conn= DriverManager.getConnection("jdbc:sqlserver://DESKTOP-S42L7FK\\MSSQLSERVER01;databaseName=d", "sa", "sa")
          val batchInsert = conn.prepareStatement ("INSERT INTO dt (value) VALUES (?) ")

          //        batchInsert.setString(1,"%s".format(rdd.id))
          //        batchInsert.addBatch()
          //        batchInsert.clearParameters()
          for (row <- part)
          {
            batchInsert.setString(1,"%s".format(row.getText))
            batchInsert.addBatch()
            batchInsert.clearParameters()
          }

          println("lines inserted: "+ batchInsert.executeBatch().sum)
        }
    }

    // Kick it all off
    ssc.start()
    ssc.awaitTermination()
  }  
}