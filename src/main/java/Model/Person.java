package Model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Person implements Serializable {

    private String zjhm;

    /**
     * 同行人员的证件号
     */
    private LinkedList<Person> together=together=new LinkedList<Person>();
    /**
     *　ｋ：旅馆编码，ｖ：该旅馆的ｒｅｃｏｒｄ
     */
    private HashMap<String,LinkedList<Record>> records =  new HashMap<String,LinkedList<Record>>();

    private LinkedList<Person> sameRoom=new LinkedList<Person>();

    public LinkedList<Person> getSameRoom() {
        return sameRoom;
    }

    public void setSameRoom(LinkedList<Person> sameRoom) {
        this.sameRoom = sameRoom;
    }
    public void addSameRoom(Person person){
        sameRoom.add(person);
    }

    public HashMap<String,LinkedList<Record>> getRecords() {
        return records;
    }



    public String getZjhm() {
        return zjhm;
    }

    public void setZjhm(String zjhm) {
        this.zjhm = zjhm;
    }

    public void addRecord(Record record){
        LinkedList<Record> recordList = records.getOrDefault(record.getLgbm(), new LinkedList<Record>());
        recordList.add(record);
        records.put(record.getLgbm(),recordList );
    }

    public LinkedList<Person> getTogether() {
        return together;
    }

    public void addTogether(Person person){

        together.add(person);
    }

    public boolean isAlreadyTogeter(Person person){

        return together.contains(person);
    }

    public boolean isAlreadySameRoom(Person person){
        return sameRoom.contains(person);
    }


}
