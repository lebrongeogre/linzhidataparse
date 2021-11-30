package com.cuit.linzhi.parse;

import java.util.List;

/**
 * 表转化接口
 *      定义表间转换的方法
 */
public interface ITableTransfer<NEW,OLD> {

    /**
     * 传入旧表集合，返回新表集合
     * @param oldList
     * @return
     */
    List<NEW> creatNewTableList(List<OLD> oldList);

    /**
     * 处理旧表字段
     * @return
     */
     Object parseFiles(Object o);

}
