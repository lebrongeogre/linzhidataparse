package com.cuit.mete.DButils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Sequences {
    public static Long ERRORSEQUENCES = -999999L;
    private String typeName = "NoName";

    public Sequences(String typeName) {
        this.typeName = typeName;
    }

    public Long currVal() {
        String sql = "select currval('" + typeName + "') as val";
        Long retVal = getDBSequences(sql);
        return retVal;
    }

    public Long nextVal() {

        String sql = "select nextval('" + typeName + "') as val";
        Long retVal = getDBSequences(sql);
        return retVal;
    }

    private Long getDBSequences(String sql) {
        Long retVal = ERRORSEQUENCES;
        Connection conn = null;
        try {
            conn = DruidJDBCUtils.getConnection();

            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            if (rs != null) {
                while (rs.next()) {
                    retVal = rs.getLong("val");
                }
            }
            DruidJDBCUtils.close(rs, statement, conn);
        } catch (SQLException e) {
            //e.printStackTrace();
            System.out.println("取序列" + typeName + "出错！");
        }
        return retVal;
    }

    /**
     * 生成下一个序列串
     *
     * @param prefix    前缀字符串
     * @param strLength 总长度
     * @return 生成的序列串
     */
    public String getSequences(String prefix, int strLength) {
        String s = "";
        Long val = nextVal();
        if (val == this.ERRORSEQUENCES) return "";
        if (prefix != null) s = prefix.trim();
        String sVal = "" + val;
        int len = strLength - s.length() - sVal.length();

        if (len > 0) {
            for (int i = 0; i < len; i++) {
                s = s + "0";
            }
            s = s + sVal;
        } else {
            //长度超后，减少前缀部分
            int len1 = strLength - sVal.length();
            s = s.substring(0, len1) + sVal;
        }
        return s;
    }

    public static void main(String[] args) {
        int seqLen = 32;
        Sequences sequences = new Sequences("MSGID");
        String seq = sequences.getSequences("LZQXJDZZH", seqLen);
        if (seq.length() == seqLen) {
            System.out.println(seq);
        } else {
            System.out.println("ERROR");
        }
    }
}
