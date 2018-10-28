package SparkImp;

import Model.Record;
import org.apache.spark.Partitioner;

public class LgbmPartionner extends Partitioner {

    private int partionNum;

    public LgbmPartionner(int partionNum){
        this.partionNum=partionNum;
    }

    @Override
    public int numPartitions() {
        return partionNum;
    }

    @Override
    public int getPartition(Object key) {
        return Integer.parseInt(((Record) key).getLgbm()) % partionNum;
    }
}
