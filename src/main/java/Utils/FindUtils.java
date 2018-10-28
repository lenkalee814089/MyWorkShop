package Utils;


import Model.Person;
import Model.Record;
import scala.Tuple2;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class FindUtils {

    /**
     * 两条记录是否行迹相似
     * @param recordA
     * @param recordB
     * @return
     */
    public static boolean ifSimilarRecord(Record recordA, Record recordB){
        long personRzsj = recordA.getComeTimeMillSecond();
        long personTfsj = recordA.getLeaveTimeMillSecond();
        long personStayLong = recordA.getStayMillSecond();

        long otherRzsj = recordB.getComeTimeMillSecond();
        long otherTfsj =  recordB.getLeaveTimeMillSecond();
        long otherStayLong = recordB.getStayMillSecond();


        long togetherTime =Math.abs(personTfsj - otherRzsj)  ;
        if ((togetherTime/ ((double) personStayLong))>=0.8&&(togetherTime/ ((double) otherStayLong)>=0.8)){
         //   System.out.println(recordA.getZjhm()+" "+recordB.getZjhm()+"   行迹相似～～～");

            return true;
        }else {
            return false;
        }


    }

    /**
     * 获取任意两公民在某个旅馆的行踪相似度和同住宿次数
     * @param records
     * @return
     */
    public static Tuple2<HashMap<String,Integer>, HashMap<String,Integer>> getSimalarAndSleepTogetherFromRecords(LinkedList<Record> records){
        HashMap<String, Integer> similarMap = new HashMap<>();
        HashMap<String, Integer> sleepTogetherMap = new HashMap<>();
        int count=0;
        String key= null;
        for (Record recordA : records) {
            //把被判断的记录从集合移除，提高效率
            records.remove(recordA);
            for (Record recordB : records) {
                //同一人的记录不要进行判定
                if(!recordA.getZjhm().equals(recordB.getZjhm())){

                    //相似判断：如果某两条记录判定相似，则把记录对应两个所属人的相似度+1
                    if(ifSimilarRecord(recordA, recordB)){

                        key= StringUtil.combine2Zjhm(recordA.getZjhm(), recordB.getZjhm());
                        count =similarMap.getOrDefault(key,0 );
                        similarMap.put(key,++count );
                    }
                    //同宿判断：
                    if(isSleeptogether(recordA, recordB)){
                        key= StringUtil.combine2Zjhm(recordA.getZjhm(), recordB.getZjhm());
                        count = sleepTogetherMap.getOrDefault(key, 0);
                        sleepTogetherMap.put(key,++count );
                    }

                }

            }
        }

        return new Tuple2<HashMap<String,Integer>,HashMap<String,Integer>>(similarMap,sleepTogetherMap);

    }



    /**
     * 是否时间交集
     * @param a
     * @param b
     * @return
     */
    public static boolean isIntersect(Record a,Record b){
       return !(a.getComeTimeMillSecond()>b.getLeaveTimeMillSecond()||b.getComeTimeMillSecond()>a.getLeaveTimeMillSecond()) ;
    }

    /**
     * 判断两条记录是否同宿
     * @param recordA
     * @param recordB
     * @return
     */
    public static boolean isSleeptogether(Record recordA,Record recordB){

        return isIntersect(recordA, recordB)&&recordA.getrzfh().equals(recordB.getrzfh());

    }

    public static boolean ifSleepSameRoom(Person a,Person b){
        HashMap<String, LinkedList<Record>> recordsA = a.getRecords();
        HashMap<String, LinkedList<Record>> recordsB = b.getRecords();
        for (Map.Entry<String, LinkedList<Record>> entryA : recordsA.entrySet()) {
            String lgbh = entryA.getKey();

            // 如果ａ住过的旅馆ｂ也有记录
            if (recordsB.containsKey(lgbh)) {
                LinkedList<Record> recordListB = recordsB.get(lgbh);
                for (Record recordA : entryA.getValue()) {
                    for (Record recordB : recordListB) {
                        if (isSleeptogether(recordA, recordB)){
                            System.out.println("同行");
                            return true;
                        }
                    }
                }

            }

        }
        return false;
    }




    public static List<Person> dealPersons(List<Person> persons){
        for (Person a : persons) {
            for (Person b : persons) {

                /**
                 * 判定是否住离时间相似
                 */
                if (!a.isAlreadyTogeter(b)&&ifTogether(a, b)) {
                     System.out.println(a.getZjhm()+"与"+b.getZjhm()+"判定为为同行");
                     a.addTogether(b);
                     b.addTogether(a);
                 }
                /**
                 * 判定是否住同住宿
                 */
                 if (!a.isAlreadySameRoom(b)&&ifSleepSameRoom(a, b)){
                         System.out.println(a.getZjhm()+"与"+b.getZjhm()+"判定为为同住宿");
                         a.addSameRoom(b);
                         b.addSameRoom(a);


                 }


            }

        }
        return persons;

    }

    /**
     * 对比两人的记录，判断是否同行
     * @param a
     * @param b
     * @return
     */
    public static boolean ifTogether(Person a,Person b){
        HashMap<String, LinkedList<Record>> MapB = b.getRecords();
        LinkedList<Record> recordsB =null;
        LinkedList<Record> recordsA =null;
        int count=0;
        for (Map.Entry<String, LinkedList<Record>> lgbm2RecordsA : a.getRecords().entrySet()) {
            String lgbm = lgbm2RecordsA.getKey();
            if (MapB.containsKey(lgbm)){
                recordsB = ((LinkedList<Record>) MapB.get(lgbm).clone());
                recordsA = ((LinkedList<Record>) lgbm2RecordsA.getValue().clone());

                for (Record recordA : recordsA) {
                    for (Record recordB : recordsB) {
                        if (ifSimilarRecord(recordA, recordB)){
                            count++;
                            //删去已验证过相似的记录，加快遍历
                            recordsB.remove(recordB);
                            if (count>=1){
                                System.out.println(a.getZjhm()+"与"+b.getZjhm()+"两人同行！！");
                                return true;
                            }
                            break;
                        }
                    }
                }

            }

        }
        return false;
    }

}
