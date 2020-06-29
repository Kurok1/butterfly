package indi.butterfly.template;

/**
 * 本地文件写入配置模板
 *
 * @author <a href="mailto:maimengzzz@gmail.com">kuroky</a>
 * @version 2020.06.29
 * @since 1.0.0
 */
public class LocalFileWriteTemplate {

    private String targetDirectory;//文件输出文件夹

    private String backupDirectory;//备份文件夹

    private Integer useOriginData = 1;//是否使用原数据,不做任何处理

    private String originFormat = "JSON";//原数据格式(这里支持string,即直接toString输出)

    private String fieldDelimiter; //字段分隔符 useOriginData=0时为必填项

    private String rowDelimiter;//一行分隔符,当且仅当输入数据为list时可用

    private String title;//文件标题,也就是第一个输出的内容

    private String fileName;//文件名

    public String getTargetDirectory() {
        return targetDirectory;
    }

    public void setTargetDirectory(String targetDirectory) {
        this.targetDirectory = targetDirectory;
    }

    public String getBackupDirectory() {
        return backupDirectory;
    }

    public void setBackupDirectory(String backupDirectory) {
        this.backupDirectory = backupDirectory;
    }

    public Integer getUseOriginData() {
        return useOriginData;
    }

    public void setUseOriginData(Integer useOriginData) {
        this.useOriginData = useOriginData;
    }

    public String getOriginFormat() {
        return originFormat;
    }

    public void setOriginFormat(String originFormat) {
        this.originFormat = originFormat;
    }

    public String getFieldDelimiter() {
        return fieldDelimiter;
    }

    public void setFieldDelimiter(String fieldDelimiter) {
        this.fieldDelimiter = fieldDelimiter;
    }

    public String getRowDelimiter() {
        return rowDelimiter;
    }

    public void setRowDelimiter(String rowDelimiter) {
        this.rowDelimiter = rowDelimiter;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
