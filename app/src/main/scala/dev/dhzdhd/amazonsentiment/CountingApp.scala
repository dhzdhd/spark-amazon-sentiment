package dev.dhzdhd.amazonSentiment

import org.apache.spark.{SparkConf, SparkContext}
import com.johnsnowlabs.nlp.SparkNLP

// local testing - sbt "run inputFile.txt outputFile.txt"
object CountingLocalApp extends App {
  val (inputFile, outputFile) = (args(0), args(1))
  val conf = new SparkConf()
    .setMaster("local")
    .setAppName("my awesome app")

  Runner.run(conf, inputFile, outputFile)
}

// spark-submit app
object CountingApp extends App {
  val (inputFile, outputFile) = (args(0), args(1))

  Runner.run(new SparkConf(), inputFile, outputFile)
}

object Runner {
  def run(conf: SparkConf, inputFile: String, outputFile: String): Unit = {
    val sc = new SparkContext(conf)
    val rdd = sc.textFile(inputFile)
    val counts = WordCount.withStopWordsFiltered(rdd)
    counts.saveAsTextFile(outputFile)
  }
}
