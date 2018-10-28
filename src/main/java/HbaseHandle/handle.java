package HbaseHandle;

import DataMake.DataProduce;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.mapred.TableOutputFormat;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.mapred.JobConf;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import scala.Tuple2;

import java.io.IOException;
import java.util.LinkedList;

public class handle {
    /**
     * 向ｈｂａｓｅ插入数据
     * @throws IOException
     */
    public static void putsData() throws IOException {


        SparkContext sc = new SparkContext(new SparkConf().setMaster("local[1]").setAppName("hbase"));
        JobConf jobConf =null;
        jobConf = new JobConf(HBaseConfiguration.create());
        jobConf.set("hbase.zookeeper.quorum", "127.0.0.1");
        // jobConf.set("zookeeper.znode.parent", "/hbase");
        jobConf.setOutputFormat(TableOutputFormat.class);
        HTable table = new HTable(jobConf, "a");


        LinkedList<Put> puts = new LinkedList<>();
        Put put=null;
        for (int i = 0; i < 10000; i++) {
            Tuple2<String, String> tuple = DataProduce.getRanDateTuple();
            String rzsj = tuple._1;
            String tfsj =tuple._2;
            long zjhm = DataProduce.getZjhm(3);
            String rzfh = DataProduce.getRzfh();
            String lgbm = DataProduce.getLgbm();

            put = new Put(Bytes.toBytes(zjhm+lgbm+rzsj)) ;//rowKey
            put.addColumn(Bytes.toBytes("f"), Bytes.toBytes("rzfh"), Bytes.toBytes(""+rzfh));
            put.addColumn(Bytes.toBytes("f"), Bytes.toBytes("rzsj"), Bytes.toBytes(rzsj));
            put.addColumn(Bytes.toBytes("f"), Bytes.toBytes("tfsj"), Bytes.toBytes(tfsj));
            put.addColumn(Bytes.toBytes("f"), Bytes.toBytes("lgbm"), Bytes.toBytes(lgbm));
            put.addColumn(Bytes.toBytes("f"), Bytes.toBytes("zjhm"), Bytes.toBytes(""+zjhm));
            puts.add(put);
        }

        table.put(puts);
    }

    public static void main(String[] args) throws IOException {
        putsData();

        
    }
}
