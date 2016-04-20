package sparkflow.layer

import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.Dataset

import scala.reflect.ClassTag
import sparkflow.serialization.Hashing

/**
  * Created by ngoehausen on 4/19/16.
  */
class MultiInputDC[T: ClassTag, K:ClassTag](inputs: Seq[DC[_ <: Product2[K, _]]],
                                                     f: (Seq[RDD[_ <: Product2[K, _]]]) => RDD[T])
extends DC[T](inputs){

  override def computeHash() = {
    Hashing.hashString(inputs.map(_.getHash).mkString("") + Hashing.hashClass(f))
  }

  override def computeRDD(sc: SparkContext) = {
    f(inputs.map(_.getRDD(sc)))
  }

  override def computeDataset(sc: SparkContext): Option[Dataset[T]]= {
    val ds = inputs(0).getDataset(sc).get
    val thing =ds.asInstanceOf[Dataset[(Int,Int)]]
    thing.
    f(inputs.map(_.getRDD(sc)))
  }

}
