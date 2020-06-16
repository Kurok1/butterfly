package indi.butterfly.template;

import java.io.Serializable;

/**
 * 数据库读写配置模板
 *
 * @author <a href="mailto:maimengzzz@gmail.com">kuroky</a>
 * @version 2020.06.15
 */
public class DatabaseWriteTemplate implements Serializable {

    private static final long serialVersionUID = 4160892788651358907L;

    private String datasource;//数据源

    private String sql;//执行的sql语句

    private String format = "JSON";//输入格式,默认为json

    private Integer paramType = 0 ; //参数类型 0为?参数 1为named参数

    private boolean batchExecute = false; //是否批量执行,默认为false

    public String getDatasource() {
        return datasource;
    }

    public void setDatasource(String datasource) {
        this.datasource = datasource;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Integer getParamType() {
        return paramType;
    }

    public void setParamType(Integer paramType) {
        this.paramType = paramType;
    }

    public boolean isBatchExecute() {
        return batchExecute;
    }

    public void setBatchExecute(boolean batchExecute) {
        this.batchExecute = batchExecute;
    }
}
