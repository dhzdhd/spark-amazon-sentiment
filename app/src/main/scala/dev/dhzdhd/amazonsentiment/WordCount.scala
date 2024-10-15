package dev.dhzdhd.amazonSentiment

import org.apache.spark.rdd._

object WordCount {
  def withStopWordsFiltered(
      rdd: RDD[String],
      separators: Array[Char] = " ".toCharArray,
      stopWords: Set[String] = Set("the")
  ): RDD[(String, Int)] = {

    val tokens: RDD[String] =
      rdd.flatMap(_.split(separators).map(_.trim.toLowerCase))
    val lcStopWords = stopWords.map(_.trim.toLowerCase)
    val words =
      tokens.filter(token => !lcStopWords.contains(token) && (token.length > 0))
    val wordPairs = words.map((_, 1))
    val wordCounts = wordPairs.reduceByKey(_ + _)
    wordCounts

  }
}
