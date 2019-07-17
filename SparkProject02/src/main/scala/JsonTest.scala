
import org.apache.spark.{SparkConf, SparkContext}

import scala.util.parsing.json.JSON



object JsonTest {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("sougo").setMaster("local")
    val sc = new SparkContext(conf)
    val lines = sc.textFile("file:///D:\\HZ1803\\day11\\students.json")
    val maybeType = JSON.parseFull(lines.toString())
    val map = maybeType.get.asInstanceOf[Map[String,String]]
    println(map.get("id").get)
    println(map.get("name").get)

    //val str = JSONFormat.quoteString()
    //println(str)
  }
}
