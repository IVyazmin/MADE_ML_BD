package linerModel

import breeze.linalg._
import scala.io.StdIn
import java.io._
import com.typesafe.scalalogging.Logger


object Main{
  def main(args: Array[String]): Unit = {

    val logger = Logger("Name")
    var filePath = StdIn.readLine("Enter train path: ")
    var file = new File(filePath)
    val data = breeze.linalg.csvread(file, skipLines = 1)
    var X = data(::, 0 to -2)
    val y = data(::, -1)
    var ones = DenseMatrix(DenseVector.ones[Double](X.rows)).t
    X = DenseMatrix.horzcat(X, ones)

    filePath = StdIn.readLine("Enter test path: ")
    file = new File(filePath)
    var X_test = breeze.linalg.csvread(file, skipLines = 1)
    ones = DenseMatrix(DenseVector.ones[Double](X_test.rows)).t
    X_test = DenseMatrix.horzcat(X_test, ones)
    filePath = StdIn.readLine("Enter result path: ")

    val folds_number = 5
    val fold_size = (X.rows / folds_number).round
    for(i <- 0 until folds_number) {
      val X1_fold = X(0 until (i * fold_size), ::)
      val X2_fold = X((i + 1) * fold_size to -1, ::)
      val X_fold = DenseMatrix.vertcat(X1_fold, X2_fold)
      val X_test_fold = X(i * fold_size until (i + 1) * fold_size, ::)

      val y1_fold = y(0 until (i * fold_size))
      val y2_fold = y((i + 1) * fold_size to -1)
      val y_fold = DenseVector.vertcat(y1_fold, y2_fold)
      val y_test_fold = y(i * fold_size until (i + 1) * fold_size)

      val b_fold = inv(X_fold.t * X_fold) * X_fold.t * y_fold
      val y_pred_fold = X_test_fold * b_fold

      val diff = y_pred_fold - y_test_fold
      val sqr_diff = diff *:* diff
      val mse = sum(sqr_diff) / sqr_diff.length
      logger.info(mse.toString)
    }
    val b = inv(X.t * X) * X.t * y
    val y_pred = DenseMatrix(X_test * b).t
    file = new File(filePath)
    breeze.linalg.csvwrite(file, y_pred)
  }
}