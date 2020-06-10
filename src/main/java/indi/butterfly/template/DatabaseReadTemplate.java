package indi.butterfly.template;

/**
 * 数据库读写配置模板
 *
 * @author <a href="mailto:maimengzzz@gmail.com">kuroky</a>
 * @version 2020.06.09
 */
public class DatabaseReadTemplate {

    private String datasource;//指定数据源

    private String sql;//执行的sql语句

    private String groupField;//对结果集合进行合并的依据,如果没有指定,则不进行合并

    private String format = "JSON";//输出格式,默认为json

    private Integer paramType = 0 ; //参数类型 0为?参数 1为named参数

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

    public String getGroupField() {
        return groupField;
    }

    public void setGroupField(String groupField) {
        this.groupField = groupField;
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
}
