package com.cuit.mete.jobs.grib.domain;

import com.cuit.mete.DButils.DruidDBOper;
import com.cuit.mete.jobs.grib.db.ModelGeoServer;

import java.util.List;

public class ModelGeoServerDao extends DruidDBOper {
	/**
	 * 插入数据
	 * @param data
	 * @return
	 */
	public boolean insertData(ModelGeoServer data){
		return Insert2DB(ModelGeoServer.class, data);
	}
	/**
	 * 查询已发布的M
	 * @param id
	 * @return
	 */
	public List<ModelGeoServer> queryPublishedById(Long id){
		String sql = "select ID," +
				"       publishdate," +
				"       predatetime," +
				"       pretime," +
				"       dataclass," +
				"       datatype," +
				"       name," +
				"       datadesc," +
				"       GeoServerWorkspaces," +
				"       GeoServerLayers," +
				"       GeoServerStyles," +
				"       Srs," +
				"       DownLoadFile," +
				"       FileSource," +
				"       createuser," +
				"       createdate from t_geoserverlist where id = ? ";
		List<ModelGeoServer> list = queryRows(sql, ModelGeoServer.class, id);
		return list;
	}
	/**
	 * 查询已发布的M
	 * @param id
	 * @return
	 */
	public List<ModelGeoServer> queryPublishedByLastId(Long id){
		String sql = "select ID," +
				"       publishdate," +
				"       predatetime," +
				"       pretime," +
				"       dataclass," +
				"       datatype," +
				"       name," +
				"       datadesc," +
				"       GeoServerWorkspaces," +
				"       GeoServerLayers," +
				"       GeoServerStyles," +
				"       Srs," +
				"       DownLoadFile," +
				"       FileSource," +
				"       createuser," +
				"       createdate from t_geoserverlist where id <= ? ";
		List<ModelGeoServer> list = queryRows(sql, ModelGeoServer.class, id);
		return list;
	}

	/**
	 * 根据最大的日期，查询未删除的发布记录
	 * @param date
	 * @return
	 */
	public List<ModelGeoServer> queryPublishedModelByLastDate(String date){
		String sql = "select ID," +
				"       publishdate," +
				"       predatetime," +
				"       pretime," +
				"       dataclass," +
				"       datatype," +
				"       name," +
				"       datadesc," +
				"       GeoServerWorkspaces," +
				"       GeoServerLayers," +
				"       GeoServerStyles," +
				"       Srs," +
				"       DownLoadFile," +
				"       FileSource," +
				"       createuser," +
				"       createdate from t_geoserverlist where createdate < ? ";
		List<ModelGeoServer> list =queryRows(sql, ModelGeoServer.class, date);
		return list;
	}


	/**
	 * 设置删除标志
	 * @param id
	 * @return
	 */
	public int setMDeleted(Long id){
		String sql = "delete from t_geoserverlist where id = ?";
		return excuteUpdate(sql, id);
	}
}


