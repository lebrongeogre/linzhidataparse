package com.cuit.mete.jobs.mete.domain;

public class WarningItem {
    private String wid;
    private String rid;
    private String mtype;
    private String etype;
    private String etypechn;
    private String lvl;
    private String title;
    private String des;
    private String ext;
    private String ft;
    private String send;
    private String p1;
    private String p2;
    private String p3;
    private String status;
    private String serialid;
    private String pdt;
    private String sid;
    private String lat;
    private String lng;

    public WarningItem() {
    }

    public WarningItem(String wid, String rid, String mtype, String etype, String etypechn, String lvl, String title, String des, String ext, String ft, String send, String p1, String p2, String p3, String status, String serialid, String pdt, String sid, String lat, String lng) {
        this.wid = wid;
        this.rid = rid;
        this.mtype = mtype;
        this.etype = etype;
        this.etypechn = etypechn;
        this.lvl = lvl;
        this.title = title;
        this.des = des;
        this.ext = ext;
        this.ft = ft;
        this.send = send;
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        this.status = status;
        this.serialid = serialid;
        this.pdt = pdt;
        this.sid = sid;
        this.lat = lat;
        this.lng = lng;
    }

    public String getWid() {
        return wid;
    }

    public void setWid(String wid) {
        this.wid = wid;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public String getMtype() {
        return mtype;
    }

    public void setMtype(String mtype) {
        this.mtype = mtype;
    }

    public String getEtype() {
        return etype;
    }

    public void setEtype(String etype) {
        this.etype = etype;
    }

    public String getEtypechn() {
        return etypechn;
    }

    public void setEtypechn(String etypechn) {
        this.etypechn = etypechn;
    }

    public String getLvl() {
        return lvl;
    }

    public void setLvl(String lvl) {
        this.lvl = lvl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getFt() {
        return ft;
    }

    public void setFt(String ft) {
        this.ft = ft;
    }

    public String getSend() {
        return send;
    }

    public void setSend(String send) {
        this.send = send;
    }

    public String getP1() {
        return p1;
    }

    public void setP1(String p1) {
        this.p1 = p1;
    }

    public String getP2() {
        return p2;
    }

    public void setP2(String p2) {
        this.p2 = p2;
    }

    public String getP3() {
        return p3;
    }

    public void setP3(String p3) {
        this.p3 = p3;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSerialid() {
        return serialid;
    }

    public void setSerialid(String serialid) {
        this.serialid = serialid;
    }

    public String getPdt() {
        return pdt;
    }

    public void setPdt(String pdt) {
        this.pdt = pdt;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    @Override
    public String toString() {
        return "WarningItem{" +
                "wid='" + wid + '\'' +
                ", rid='" + rid + '\'' +
                ", mtype='" + mtype + '\'' +
                ", etype='" + etype + '\'' +
                ", etypechn='" + etypechn + '\'' +
                ", lvl='" + lvl + '\'' +
                ", title='" + title + '\'' +
                ", des='" + des + '\'' +
                ", ext='" + ext + '\'' +
                ", ft='" + ft + '\'' +
                ", send='" + send + '\'' +
                ", p1='" + p1 + '\'' +
                ", p2='" + p2 + '\'' +
                ", p3='" + p3 + '\'' +
                ", status='" + status + '\'' +
                ", serialid='" + serialid + '\'' +
                ", pdt='" + pdt + '\'' +
                ", sid='" + sid + '\'' +
                ", lat='" + lat + '\'' +
                ", lng='" + lng + '\'' +
                '}';
    }
}