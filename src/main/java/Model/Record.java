package Model;


import Utils.DateUtils;

import java.io.Serializable;

public class Record implements Serializable {
    private String zjhm;
    private String rowKey;
    private String lgjd;
    private String lgwd;
    private String rzfh;
    private String tfsj;
    private String rzsj;
    private long leaveTimeMillSecond;
    private long comeTimeMillSecond;
    private long stayMillSecond;

    public Record(){

    }

    public Record(String zjhm, String rowKey, String rzfh, String tfsj, String rzsj, String lgbm) {
        this.zjhm = zjhm;
        this.rowKey = rowKey;
        this.rzfh = rzfh;
        this.tfsj = tfsj;
        this.rzsj = rzsj;
        this.lgbm = lgbm;
        setLeaveTimeMillSecond(DateUtils.parseYYYYMMDDHHMM2Date(tfsj).getTimeInMillis()/1000);
        setComeTimeMillSecond(DateUtils.parseYYYYMMDDHHMM2Date(rzsj).getTimeInMillis()/1000);
        stayMillSecond=leaveTimeMillSecond-comeTimeMillSecond;
    }

    public long getStayMillSecond() {

        return stayMillSecond;
    }

    public void setStayMillSecond(long stayMillSecond) {
        this.stayMillSecond = stayMillSecond;
    }

    private String lgbm;

    public String getRzfh() {
        return rzfh;
    }

    public void setRzfh(String rzfh) {
        this.rzfh = rzfh;
    }

    public String getRowKey() {
        return rowKey;
    }

    public String getTfsj() {
        return tfsj;
    }

    public void setTfsj(String tfsj) {
        this.tfsj = tfsj;
    }

    public String getRzsj() {
        return rzsj;
    }

    public void setRzsj(String rzsj) {
        this.rzsj = rzsj;
    }

    public String getLgbm() {
        return lgbm;
    }

    public void setLgbm(String lgbm) {
        this.lgbm = lgbm;
    }

    public void setRowKey(String rowKey) {
        this.rowKey = rowKey;
    }

    public String getLgjd() {
        return lgjd;
    }

    public void setLgjd(String lgjd) {
        this.lgjd = lgjd;
    }

    public String getLgwd() {
        return lgwd;
    }

    public void setLgwd(String lgwd) {
        this.lgwd = lgwd;
    }

    public String getrzfh() {
        return rzfh;
    }

    public void setrzfh(String rzfh) {
        this.rzfh = rzfh;
    }


    public long getLeaveTimeMillSecond() {
        return leaveTimeMillSecond;
    }

    public void setLeaveTimeMillSecond(long leaveTimeMillSecond) {
        this.leaveTimeMillSecond = leaveTimeMillSecond;
    }

    public long getComeTimeMillSecond() {
        return comeTimeMillSecond;
    }

    public void setComeTimeMillSecond(long comeTimeMillSecond) {
        this.comeTimeMillSecond = comeTimeMillSecond;
    }

    public String getZjhm() {
        return zjhm;
    }

    public void setZjhm(String zjhm) {
        this.zjhm = zjhm;
    }
}
