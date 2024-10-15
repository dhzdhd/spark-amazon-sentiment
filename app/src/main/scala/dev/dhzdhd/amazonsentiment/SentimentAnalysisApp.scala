package dev.dhzdhd.amazonSentiment

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.{SparkSession}
// import com.johnsnowlabs.nlp.SparkNLP
import org.apache.spark.rdd._

// local testing - sbt "run inputFile.txt outputFile.txt"
object SentimentAnalysisLocalApp extends App {
  val (inputFile, outputFile) = (args(0), args(1))
  val conf = new SparkConf()
    .setMaster("local")
    .setAppName("my awesome app")

  Runner.run(conf, inputFile, outputFile)
}

// spark-submit app
object SentimentAnalysisApp extends App {
  val (inputFile, outputFile) = (args(0), args(1))

  Runner.run(new SparkConf(), inputFile, outputFile)
}

object Runner {
  def run(conf: SparkConf, inputFile: String, outputFile: String): Unit = {
    val sc = new SparkContext(conf)
    sc.setLogLevel("ERROR")

    val session = SparkSession.builder.getOrCreate()

    val df = session.read.json(inputFile)
    df.printSchema()

    df.show(5)

    // val rdd: RDD[String] = sc.objectFile(inputFile)
    // val counts = WordCount.withStopWordsFiltered(rdd)
    // counts.saveAsTextFile(outputFile)

  }
}
