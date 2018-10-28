package DataMake;

import Utils.DateUtils;
import Utils.StringUtil;
import scala.Tuple2;

import java.util.Random;

/**
 * 造数据
 */
public class DataProduce {

    public static Random random = new Random();
    public static int randomInt(){
        return random.nextInt(9);
    }

    public static long getZjhm(int bits){
        String num="2";
        for (int i = 1; i < bits; i++) {

            num+=randomInt();
        }
        return Long.parseLong(num) ;
    }
    public static Tuple2<String,String> getRanDateTuple(){
        String year = "2018";
        String month = "01";
        String day = "1"+randomInt();
        String hour = StringUtil.getFixedLengthStr(""+random.nextInt(24), 2) ;
        String min = StringUtil.getFixedLengthStr(""+random.nextInt(60), 2) ;
        String dateString1 = year + month + day + hour + min;
        long date2MillSeconds = DateUtils.parseYYYYMMDDHHMM2Date(dateString1).getTimeInMillis() + DateUtils.ONE_HOURS_SECONDS * 8;
        String dateString2 = DateUtils.getDateStringByMillisecond(DateUtils.HOUR_FORMAT, date2MillSeconds)+min;

        return new Tuple2<>(dateString1, dateString2) ;
    }
    public static String getRzfh(){
        String rzfh = ""+random.nextInt(9)+ random.nextInt(1)+ random.nextInt(10);
        return rzfh;
    }

    public static String getLgbm(){
        String lgbm = ""+random.nextInt(1)+ random.nextInt(9)+ random.nextInt(9);
        return lgbm;
    }


}
