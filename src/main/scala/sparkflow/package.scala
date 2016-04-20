import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{SQLContext, Dataset}
import sparkflow.layer.{SourceDC, ParallelCollectionDC, DC}
import java.io.File

import scala.reflect.ClassTag
import scala.util.Try

/**
  * Created by ngoehausen on 3/24/16.
  */
package object sparkflow {

  val sentinelInt = -1

  def parallelize[T:ClassTag](seq: Seq[T]): DC[T] = {
    new ParallelCollectionDC(seq)
  }

  def textFile(path: String,
               minPartitions: Int = sentinelInt) = {
    val sourceFunc = if(minPartitions == sentinelInt){
      (sc: SparkContext) => sc.textFile(path)
    } else {
      (sc: SparkContext) => sc.textFile(path, minPartitions)
    }
    new SourceDC[String](path, sourceFunc, "textFile")
  }

  def objectFile[T:ClassTag](path: String,
                             minPartitions: Int = sentinelInt) = {
    val sourceFunc = if(minPartitions == sentinelInt){
      (sc: SparkContext) => sc.objectFile[T](path)
    } else {
      (sc: SparkContext) => sc.objectFile[T](path, minPartitions)
    }
    new SourceDC[T](path, sourceFunc, "objectFile")
  }


  private var checkpointDir = "/tmp/sparkflow"
  def setCheckpointDir(s: String) = {checkpointDir = s}

  private[sparkflow] def saveRDDCheckpoint[T:ClassTag](hash: String, rdd: RDD[T]) = {
    rdd.saveAsObjectFile(new File(checkpointDir, hash).toString)
  }

  private[sparkflow] def loadRDDCheckpoint[T:ClassTag](hash: String, sc: SparkContext): Option[RDD[T]] = {
    Try{
      val attemptRDD = sc.objectFile[T](new File(checkpointDir, hash).toString)
      attemptRDD.first()
      attemptRDD
    }.toOption
  }


  private[sparkflow] def loadDSCheckpoint[T:ClassTag](hash: String, sc: SparkContext): Option[Dataset[T]] = {
    Try{
      val sqlContext = SQLContext.getOrCreate(sc)
      val attempt = sqlContext.read.parquet(new File(checkpointDir, hash).toString).as[T]
      attempt.first()
      attempt
    }.toOption
  }
}
