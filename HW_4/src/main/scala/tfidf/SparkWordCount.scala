package tfidf

import org.apache.spark.sql._
import org.apache.spark.sql.functions._
import org.apache.spark.sql.expressions.Window

object SparkWordCount {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .master("local[*]")
      .appName("tf-idf")
      .getOrCreate()

    val df = spark.read
      .option("header", "true")
      .option("inferSchema", "true")
      .csv("tripadvisor_hotel_reviews.csv")

    val window = Window.orderBy("Rating")
    val pattern = "[.,<>?'/\";:|\\{}=+_)(*&^%$#@!~]"

    val pure = df
      .withColumn("value", regexp_replace(lower(col("Review")), pattern, ""))
      .withColumn("rowNumber", row_number().over(window))

    val flatten = pure
      .withColumn("word", explode(split(trim(col("value")), " {1,}")))

    val len_review = flatten
      .select(col("word"), col("rowNumber"))
      .groupBy(col("rowNumber"))
      .count()
      .withColumnRenamed("count", "len_review")

    val countInReview = flatten
      .select(col("word"), col("rowNumber"))
      .groupBy(col("rowNumber"), col("word"))
      .count()
      .withColumnRenamed("count", "countInReview")

    val tf = len_review
      .join(countInReview, Seq("rowNumber"), "inner")
      .withColumn("tf", col("countInReview") / col("len_review"))

    val corpusSize = len_review.count()

    val countInDocs = countInReview
      .select(col("word"))
      .groupBy(col("word"))
      .count()
      .orderBy(desc("count"))
      .limit(100)
      .withColumnRenamed("count", "countInDocs")
      .withColumn("corpusSize", lit(corpusSize))

    val idf = countInDocs
      .withColumn("idf", log(col("corpusSize") / col("countInDocs")))

    val tfidf = tf
      .join(idf, Seq("word"), joinType = "Inner")
      .withColumn("tfidf", (col("tf") * col("idf")))

    val pivot_tfidf = tfidf
      .groupBy(col("rowNumber"))
      .pivot("word")
      .sum("tfidf")
      .withColumnRenamed("sum", "tfidf")
    pivot_tfidf.show
  }
}
