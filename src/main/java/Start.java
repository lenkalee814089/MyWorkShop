import Model.Person;
import Model.Record;
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
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
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

        JavaRDD<Person> personJavaRDD = hBaseRDD.map(tuple -> {
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
        }).groupBy(x -> x.getZjhm()).map(group -> {

            String zjhm = group._1;
            Person person = new Person();
            person.setZjhm(zjhm);
            Iterator<Record> iterator = group._2.iterator();
            while (iterator.hasNext()) {
                Record rec = iterator.next();
                System.out.println("记录：" + rec.getRowKey());
                person.addRecord(rec);
            }
            return person;
        });

        List<Person> personList = personJavaRDD.collect();


        FindUtils.dealPersons(personList);

        String togeOfOne;
        String sameRoomOfOne;
        for (Person person : personList) {
            togeOfOne="";
            sameRoomOfOne="";
            for (Person togetherPerson : person.getTogether()) {
                togeOfOne+=togetherPerson.getZjhm()+"   ";
            }
            for (Person sameRoomPerson : person.getSameRoom()) {
                sameRoomOfOne+=sameRoomPerson.getZjhm() +"   ";
            }
            System.out.println(person.getZjhm()+"的同行人："+ togeOfOne);
            System.out.println(person.getZjhm()+"的同住宿人："+sameRoomOfOne);
        }

        jsc.stop();
        admin.close();

    }
}
