package sparkflow.layer

import com.holdenkarau.spark.testing.SharedSparkContext
import org.apache.spark.mllib.clustering.LDA
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.linalg.Vector
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.catalyst.encoders.ExpressionEncoder
import org.apache.spark.sql.{Encoder, SQLContext}
import org.scalatest._

import scala.util.Random
import sparkflow._

/**
  * Created by ngoehausen on 3/23/16.
  */
class DRTest extends FunSuite with SharedSparkContext with ShouldMatchers {

  test("normalize"){

    val numbers: DC[Int] = parallelize(1 to 10)
    val doubles: DC[Double] = numbers.map(_.toDouble)
    val sum: DR[Double] = doubles.mapRDDToResult(rdd => rdd.sum())
    val normalized: DC[Double] = doubles.mapWith(sum){case (number, s) => number / s}

    val normalizedRDD = normalized.getRDD(sc)
    normalizedRDD.foreach(println)
  }


  test("machineLearning"){

    /* Regular spark

    val randomVecs = sc.parallelize(1 to 100).map(i => Vectors.dense(Seq.fill(10)(Random.nextDouble()).toArray))
    val corpus = randomVecs.zipWithUniqueId().map{case (k,v) => (v,k)}
    val ldaModel = new LDA().setK(3).run(corpus)
    println(ldaModel)
     */

    //TODO: upgrade to spark 2.0 for encoder for mllib vectors
//    val randomVecs = parallelize(1 to 100).map(i => Vectors.dense(Seq.fill(10)(Random.nextDouble()).toArray))
//    val corpus = randomVecs.zipWithUniqueId.map{case (k,v) => (v,k)}
//    val ldaModel = corpus.mapToResult(new LDA().setK(3).run)
//    println(ldaModel.get(sc).topicsMatrix)

  }

  test("regularSpark"){
    val numbers: RDD[Int] = sc.parallelize(1 to 10)
    val doubles: RDD[Double] = numbers.map(_.toDouble)
    val sum: Double = doubles.sum()
    val normalized: RDD[Double] = doubles.map(_ / sum)
  }
}
