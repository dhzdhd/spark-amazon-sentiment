#! /bin/bash

spark-submit \
    --class dev.dhzdhd.amazonSentiment.SentimentAnalysisApp \
    --name review_sentiment_analysis \
    /home/app/target/scala-2.12/amazonSentiment-assembly-0.0.1.jar \
    /home/app/data/software_mini.json \
    /home/output/"$(date +%s)"
