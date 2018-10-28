import Model.Person;
import Model.Record;
import SparkImp.LgbmPartionner;
import Utils.FindUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableInputFormat;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.spark.HashPartitioner;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class Start {
    public static void main(String[] args) throws IOException {
        JavaSparkContext jsc=null;
        HBaseAdmin admin=null;

        SparkConf sparkConf = new SparkConf();
        sparkConf.setAppName("HBaseTest").setMaster("local");

        jsc = new JavaSparkContext(sparkConf);
        String tablename = "a";
        Configuration Hconf = HBaseConfiguration.create();
        //设置zooKeeper集群地址，也可以通过将hbase-site.xml导入classpath，但是建议在程序里这样设置
        Hconf.set("hbase.zookeeper.quorum","127.0.0.1");
        //设置zookeeper连接端口，默认2181
        Hconf.set("hbase.zookeeper.property.clientPort", "2181");
        Hconf.set(TableInputFormat.INPUT_TABLE, tablename);

        // 如果表不存在则创建表
        admin = new HBaseAdmin(Hconf);
        if (!admin.isTableAvailable(tablename)) {
            HTableDescriptor tableDesc = new HTableDescriptor(TableName.valueOf(tablename));
            admin.createTable(tableDesc);
        }

        //读取数据并转化成rdd
        JavaPairRDD<ImmutableBytesWritable, Result> hBaseRDD
                = jsc.newAPIHadoopRDD(Hconf, TableInputFormat.class, ImmutableBytesWritable.class, Result.class);




        long count = hBaseRDD.count();
        System.out.println(count);

//        JavaRDD<Person> personJavaRDD =
        JavaRDD<Tuple2<HashMap<String,Integer>,HashMap<String,Integer>>> tuple2JavaRDD
                = hBaseRDD.map(tuple -> {
            Result result = tuple._2;
            //获取行键
            //通过列族和列名获取列

            Record record = new Record(
                    Bytes.toString(result.getValue("f".getBytes(), "zjhm".getBytes())),
                    Bytes.toString(result.getRow()),
                    Bytes.toString(result.getValue("f".getBytes(), "rzfh".getBytes())),
                    Bytes.toString(result.getValue("f".getBytes(), "tfsj".getBytes())),
                    Bytes.toString(result.getValue("f".getBytes(), "rzsj".getBytes())),
                    Bytes.toString(result.getValue("f".getBytes(), "lgbm".getBytes())));

            return record;
            //根据不同的证件号划分ｒｅｃｏｒｄ
        }).mapToPair(record -> new Tuple2<String, Record>(record.getLgbm(), record))
                //让同旅馆的记录都到同一个区
                .partitionBy(new HashPartitioner(hBaseRDD.getNumPartitions()))
                //同个分区的record 进行分析处运算
                .mapPartitions(it -> {
                    HashMap<String, LinkedList<Record>> map = new HashMap<>();
                    LinkedList<Record> recordLinkedList = null;
                    Iterator iterator = null;

                    while (it.hasNext()) {
                        Record record = it.next()._2;
                        recordLinkedList = map.getOrDefault(record.getLgbm(), new LinkedList<Record>());
                        recordLinkedList.add(record);
                        map.put(record.getLgbm(), recordLinkedList);
                    }
                    LinkedList<Record> recordsOfHotel = null;

                    LinkedList<Tuple2<HashMap<String,Integer>,HashMap<String,Integer>>> tuple2s = new LinkedList<>();
                    //遍历map的每个list，放入处理函数
                    for (Map.Entry<String, LinkedList<Record>> listEntry : map.entrySet()) {
                        recordsOfHotel = listEntry.getValue();
                        Tuple2<HashMap<String, Integer>, HashMap<String, Integer>> tuple2 =
                                FindUtils.getSimalarAndSleepTogetherFromRecords(recordsOfHotel);
                        tuple2s.add(tuple2);

                    }
                    return tuple2s.iterator();

                });
//                map(group -> {
//
//            String zjhm = group._1;
//            Person person = new Person();
//            person.setZjhm(zjhm);
//            Iterator<Record> iterator = group._2.iterator();
//            while (iterator.hasNext()) {
//                Record rec = iterator.next();
//                System.out.println("记录：" + rec.getRowKey());
//                person.addRecord(rec);
//            }
//            return person;
//        });

        //把每个partition的计算结果汇总
        List<Tuple2<HashMap<String, Integer>, HashMap<String, Integer>>> list = tuple2JavaRDD.collect();
        HashMap<String, Integer> similarMap =new HashMap<String, Integer> () ;
        HashMap<String, Integer> sleepMap =new HashMap<String, Integer> () ;

       //汇总同行次数和同宿次数
        for (Tuple2<HashMap<String, Integer>, HashMap<String, Integer>> tuple2 : list) {
            String key =null;
            int similarCounts = 0;
            int  similarSum=0;
            int sleepCounts =0;
            int  sleepSum =0;
            for (Map.Entry<String, Integer> similarKV : tuple2._1.entrySet()) {
                   key = similarKV.getKey();
                   similarCounts =similarKV.getValue();
                   similarSum = similarMap.getOrDefault(key, 0);
                   similarSum+=similarCounts;
                   similarMap.put(key, similarSum);
            }
            for (Map.Entry<String, Integer> sleepKV : tuple2._2.entrySet()) {
                key = sleepKV.getKey();
                sleepCounts =sleepKV.getValue();
                sleepSum = sleepMap.getOrDefault(key, 0);
                sleepSum+=sleepCounts;
                sleepMap.put(key, sleepSum);
            }

        }
        //
        similarMap.entrySet().forEach(x -> {
            if(x.getValue()>=5){
                System.out.println(x.getKey() +"的同行次数："+x.getValue());
            }
        });
        sleepMap.entrySet().forEach(x -> {
            if(x.getValue()>=1){
                System.out.println(x.getKey() +"的同宿次数："+x.getValue());
            }
        });


        jsc.stop();
        admin.close();

    }
}
