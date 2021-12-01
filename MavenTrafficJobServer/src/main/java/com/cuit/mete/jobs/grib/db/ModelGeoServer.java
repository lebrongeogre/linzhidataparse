package com.cuit.mete.jobs.grib.db;

public class ModelGeoServer {
	private Long id;
	private String publishdate;
	private String predatetime;
	private Integer pretime;
	private String dataclass;
	private String datatype;
	private String name;
	private String datadesc;
	private String geoserverworkspaces;
	private String geoserverlayers;
	private String geoserverstyles;
	private String srs;
	private String filesource;
	private String downLoadFile;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPublishdate() {
		return publishdate;
	}

	public void setPublishdate(String publishdate) {
		this.publishdate = publishdate;
	}

	public String getPredatetime() {
		return predatetime;
	}

	public void setPredatetime(String predatetime) {
		this.predatetime = predatetime;
	}

	public Integer getPretime() {
		return pretime;
	}

	public void setPretime(Integer pretime) {
		this.pretime = pretime;
	}

	public String getDataclass() {
		return dataclass;
	}

	public void setDataclass(String dataclass) {
		this.dataclass = dataclass;
	}

	public String getDatatype() {
		return datatype;
	}

	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDatadesc() {
		return datadesc;
	}

	public void setDatadesc(String datadesc) {
		this.datadesc = datadesc;
	}

	public String getGeoserverworkspaces() {
		return geoserverworkspaces;
	}

	public void setGeoserverworkspaces(String geoserverworkspaces) {
		this.geoserverworkspaces = geoserverworkspaces;
	}

	public String getGeoserverlayers() {
		return geoserverlayers;
	}

	public void setGeoserverlayers(String geoserverlayers) {
		this.geoserverlayers = geoserverlayers;
	}

	public String getGeoserverstyles() {
		return geoserverstyles;
	}

	public void setGeoserverstyles(String geoserverstyles) {
		this.geoserverstyles = geoserverstyles;
	}

	public String getSrs() {
		return srs;
	}

	public void setSrs(String srs) {
		this.srs = srs;
	}

	public String getFilesource() {
		return filesource;
	}

	public void setFilesource(String filesource) {
		this.filesource = filesource;
	}

	public String getDownLoadFile() {
		return downLoadFile;
	}

	public void setDownLoadFile(String downLoadFile) {
		this.downLoadFile = downLoadFile;
	}

	public String getTableName() {
        return "t_geoserverlist_data";
    }
}
