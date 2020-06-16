package indi.butterfly.template;

/**
 * XSLT 转换配置模板
 *
 * @author <a href="mailto:maimengzzz@gmail.com">kuroky</a>
 * @version 2020.06.16
 */
public class XsltTransformTemplate {

    private String xsltCode;

    private String outputFormat;

    public String getXsltCode() {
        return xsltCode;
    }

    public void setXsltCode(String xsltCode) {
        this.xsltCode = xsltCode;
    }

    public String getOutputFormat() {
        return outputFormat;
    }

    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }
}
