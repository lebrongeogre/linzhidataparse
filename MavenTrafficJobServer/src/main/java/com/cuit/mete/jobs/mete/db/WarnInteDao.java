package com.cuit.mete.jobs.mete.db;


 import com.cuit.mete.DButils.DruidDBOper;
 import com.cuit.mete.jobs.mete.domain.WarningItem;

 import java.util.List;

public class WarnInteDao extends DruidDBOper {

    public int insert(WarningItem warnInte) {
        return excuteUpdate("INSERT INTO t_warninginterface(rid,wid,mtype,etypechn,lvl,title,des,ext,ft,send,p1,p2,p3,pdt,serialid,status,sid,lat,lng) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
                warnInte.getRid(), warnInte.getWid(), warnInte.getMtype(),  warnInte.getEtypechn(),warnInte.getLvl(), warnInte.getTitle(), warnInte.getDes(), warnInte.getExt(), warnInte.getFt(),
                warnInte.getSend(), warnInte.getP1(), warnInte.getP2(),warnInte.getP3(), warnInte.getPdt(),warnInte.getSerialid(), warnInte.getStatus(),warnInte.getSid(), warnInte.getLat(), warnInte.getLng());
    }

    public int update(WarningItem warnInte) {
        return excuteUpdate("UPDATE t_warninginterface SET rid =?,  mtype = ?,etypechn = ?,lvl = ?,title = ?,des = ?,ext = ?,ft = ?,send = ?,p1 = ?,p2 = ?,p3 = ?,pdt = ?,serialid = ?,status =?,sid = ?,lat =?,lng = ? WHERE wid= ?",
                warnInte.getRid(),  warnInte.getMtype(), warnInte.getEtypechn(),warnInte.getLvl(), warnInte.getTitle(), warnInte.getDes(), warnInte.getExt(), warnInte.getFt(),
                warnInte.getSend(), warnInte.getP1(), warnInte.getP2(),warnInte.getP3(), warnInte.getPdt(),warnInte.getSerialid(), warnInte.getStatus(),warnInte.getSid(), warnInte.getLat(), warnInte.getLng(),warnInte.getWid());
    }

    public int delete(WarningItem warnInte) {
        return excuteUpdate("DELETE FROM t_warninginterface WHERE wid=?",  warnInte.getWid());
    }

    public int delete( int wid) {
        return excuteUpdate("DELETE FROM t_warninginterface WHERE wid=?",  wid);
    }


    public List<WarningItem> selectDataByDate(String ext) {
        return queryRows("SELECT rid,wid,mtype,etypechn,lvl,title,des,ext,ft,send,p1,p2,p3,pdt,serialid,status,sid,lat,lng FROM t_warninginterface WHERE ext=?", WarningItem.class, ext);
    }

    public WarningItem selectOneDataByID( int id) {
        return queryOneRow("SELECT rid,wid,mtype,etypechn,lvl,title,des,ext,ft,send,p1,p2,p3,pdt,serialid,status,sid,lat,lng FROM t_warninginterface WHERE id=? ", WarningItem.class, id);
    }

    public WarningItem selectOneDataByWID( String wid) {
        return queryOneRow("SELECT rid,wid,mtype,etypechn,lvl,title,des,ext,ft,send,p1,p2,p3,pdt,serialid,status,sid,lat,lng FROM t_warninginterface WHERE wid=? ", WarningItem.class, wid);
    }
    public static void main(String[] args) {
        WarningItem warningItem = new WarnInteDao().selectOneDataByID(1);
        System.out.println(warningItem.toString());
    }
}
