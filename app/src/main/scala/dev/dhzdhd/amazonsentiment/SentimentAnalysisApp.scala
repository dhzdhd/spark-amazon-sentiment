package dev.dhzdhd.amazonSentiment

import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.{SparkSession}
import com.johnsnowlabs.nlp.annotators.classifier.dl.MultiClassifierDLApproach
import com.johnsnowlabs.nlp.base.DocumentAssembler
import com.johnsnowlabs.nlp.embeddings.UniversalSentenceEncoder
import org.apache.spark.rdd._
import org.apache.spark.ml.Pipeline

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
    val session = SparkSession.builder.getOrCreate()
    session.sparkContext.setLogLevel("ERROR")

    val df = session.read.json(inputFile)
    val trimmedDf = df
      .drop(
        "image",
        "vote",
        "style",
        "reviewerName",
        "reviewerID",
        "reviewTime",
        "unixReviewTime",
        "verified",
        "asin"
      )
      .dropDuplicates()
    trimmedDf.printSchema()

    val documentAssembler = new DocumentAssembler()
      .setInputCol("reviewText")
      .setOutputCol("document")
      .setCleanupMode("shrink")

    val embeddings = UniversalSentenceEncoder
      .pretrained()
      .setInputCols("document")
      .setOutputCol("embeddings")

    val docClassifier = new MultiClassifierDLApproach()
      .setInputCols("embeddings")
      .setOutputCol("category")
      .setLabelColumn("overall")
      .setBatchSize(128)
      .setMaxEpochs(2)
      .setLr(1e-3f)
      .setThreshold(0.5f)
      .setValidationSplit(0.1f)

    val pipeline = new Pipeline()
      .setStages(
        Array(
          documentAssembler,
          embeddings,
          docClassifier
        )
      )

    val pipelineModel = pipeline.fit(trimmedDf)

  }
}
