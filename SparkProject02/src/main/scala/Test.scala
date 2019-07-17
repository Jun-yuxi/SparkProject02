import java.lang.Class
import java.text.SimpleDateFormat
import java.util.Locale

import Utils.JedisConnectionPool

object Test {
  def main(args: Array[String]): Unit = {
//    val s = "{\"id\":\"0375\",\"city\":\"平顶山\"}";
//    //将JsonObject数据转换为Json
//    val bject = JSON.parseObject(s)
//    println(bject.get("city"))
   val a = 2.0
    val b = 3.0
    val c =(a/b*100).formatted("%.2f")+"%"
    println(c)
    val jedis = JedisConnectionPool.getConnection()
    val str: String = jedis.get(c)
    println(str==null)
    val str12 = "20170412030039004"
    println(ParseTime(str12))
    val loc = new Locale("en")
    val fm = new SimpleDateFormat("yyyyMMddmmSSS",loc)
    val tm = "20170412030033803458667086740652".substring(0,13)
    val dt2 = fm.parse(tm).getTime;
    val s = fm.format(dt2)
    println(dt2)
    println(s)
  }
  def ParseTime(str:String): String ={
    val year = str.substring(0,4)
    val month = str.substring(4,6)
    val day = str.substring(6,8)
    val minute = str.substring(8,10)
    val second = str.substring(10,12)
    return year+"-"+month+"-"+day
  }


}