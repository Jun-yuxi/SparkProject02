package Utils

import java.text.SimpleDateFormat
import java.util.Locale

import Constant.Constants
import com.alibaba.fastjson.JSON
import org.apache.spark.rdd.RDD

/**
  * 方法类
  */
class Utisl extends Serializable {
  // 计算成交总额
  def SumMoney(rdd: RDD[String]): Unit = {
    rdd.map(t => {
      JSON.parseObject(t)
    }).filter(_.get("serviceName").toString.equalsIgnoreCase("reChargeNotifyReq"))
      .map(
               tm=> {
                 val t = JSON.parseObject(tm.toString)
                 val time = ParseTime(t.get("requestId").toString)
                 val bussinessRst = t.get("bussinessRst")
                 val jedis = JedisConnectionPool.getConnection() //获取redis连接池
                 if (bussinessRst.equals("0000")) {
                   val sumMoneyTotal = t.get("chargefee").toString.toFloat
                   jedis.incrByFloat(time + Constants.SUM_MONEY_TOTAL, sumMoneyTotal)
                   jedis.incrBy(time+(Constants.ORDER_SUCCESS_NUM), 1)
                 } else {
                   jedis.incrBy(time+Constants.ORDER_FAILED_NUM, 1)
                 }
                 var num1 = jedis.get(time + Constants.ORDER_FAILED_NUM)
                 if (num1 == null) num1 = "0"
                 var num2 = jedis.get(time + Constants.ORDER_SUCCESS_NUM)
                 if (num2 == null) num2 = "0"
                 jedis.incrByFloat(time + Constants.COUNT_ORDER_TOTAL, num1.toInt + num2.toInt)
                 val number1 = num1.toDouble
                 var number2 = (jedis.get(time + Constants.COUNT_ORDER_TOTAL))
                 if (number2=="") {val num = (number1 / 1 * 100).formatted("%.2f") + "%"
                   jedis.set(time + Constants.SUCCESS_RATE_TOTAL, num)}
                 else {val num = (number1 / (number2.toDouble) * 100).formatted("%.2f") + "%"
                   jedis.set(time + Constants.SUCCESS_RATE_TOTAL, num)}
                 //时间
                 val loc = new Locale("en")
                 val fm = new SimpleDateFormat("yyyyMMddmmSSS",loc)
                 val str1 = t.get("requestId").toString.substring(0,13)
                 val  str2 = t.get("receiveNotifyTime").toString.substring(0,13)
                  val requestId = fm.parse(str1).getTime;
                  val receiveNotifyTime = fm.parse(str2).getTime;
                 val total_time_diff = receiveNotifyTime-requestId
                 jedis.incrBy(time+Constants.TOTAL_TIME_DIFF,total_time_diff)
                 val total_time = jedis.get(time+Constants.TOTAL_TIME_DIFF)
                 if (num2==0||total_time.equals("")) {val avelrage_time_diff="0.0"
                   jedis.set(time+Constants.AVELRAGE_TIME_DIFF,avelrage_time_diff)}
                 else {val avelrage_time_diff=""+(total_time_diff.toDouble/num2.toDouble)
                   jedis.set(time+Constants.AVELRAGE_TIME_DIFF,avelrage_time_diff)}

               }).foreach(println)
    // 计算商品分类成交量
  }
  // 计算省份成交总额
  //日期解析
  def ParseTime(str:String): String = {
    val year = str.substring(0, 4)
     val month = str.substring(4,6)
    val day = str.substring(6,8)
    val minute = str.substring(8,10)
    val second = str.substring(10,12)
    return year+"-"+month+"-"+day
  }
  }

