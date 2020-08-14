package indi.butterfly.template;

/**
 * 本地文件读取配置模板
 *
 * @author <a href="mailto:maimengzzz@gmail.com">kuroky</a>
 * @version 2020.06.30
 * @since 1.0.0
 */
public class LocalFileReadTemplate {

    private Integer directlyRead = 1;//直接读取,不做任何处理,当成字符串

    private String fieldDelimiter = ""; //字段分隔符

    private String rowDelimiter = "\n";//行分隔符

    private Integer ignoreHeader = 0;//忽视第一行

    private String outputFormat = "JSON";//输出格式,仅当directlyRead==1时可用

    private String directory;//指定目录

    private String fileName;//指定文件名

    public Integer getDirectlyRead() {
        return directlyRead;
    }

    public void setDirectlyRead(Integer directlyRead) {
        this.directlyRead = directlyRead;
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

    public Integer getIgnoreHeader() {
        return ignoreHeader;
    }

    public void setIgnoreHeader(Integer ignoreHeader) {
        this.ignoreHeader = ignoreHeader;
    }

    public String getOutputFormat() {
        return outputFormat;
    }

    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
