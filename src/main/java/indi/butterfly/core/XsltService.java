package indi.butterfly.core;

import indi.butterfly.domain.XsltDefinition;
import indi.butterfly.repository.XsltDefinitionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

/**
 * Xslt转换服务
 *
 * @author <a href="mailto:maimengzzz@gmail.com">kuroky</a>
 * @version 2020.06.16
 */
@Service
public class XsltService {

    public final RedisService redisService;

    private final XsltDefinitionRepository xsltDefinitionRepository;

    private final Charset charset = StandardCharsets.UTF_8;

    private final static String XSLT_KEY_PREFIX = "xslt|";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public XsltService(RedisService redisService, XsltDefinitionRepository xsltDefinitionRepository) {
        this.redisService = redisService;
        this.xsltDefinitionRepository = xsltDefinitionRepository;
    }

    /**
     * 读取xslt文件 加载到redis中
     */
    public void loadXslt() {
        Iterable<XsltDefinition> list = this.xsltDefinitionRepository.findAll();
        Iterator<XsltDefinition> iterator = list.iterator();
        if (!iterator.hasNext()) {
            logger.warn("没有需要加载的xslt");
            return;
        }
        while (iterator.hasNext()) {
            XsltDefinition xslt = iterator.next();
            this.redisService.set(XSLT_KEY_PREFIX + xslt.getCode(), xslt.getXsltValue());
            int index = xslt.getXsltValue().length() > 50 ? 50 : xslt.getXsltValue().length() -1;
            String subXsltValue = xslt.getXsltValue().substring(0, index);
            logger.info("xslt loaded finish: [code: {}, value; {}]", xslt.getCode(), subXsltValue);
        }
    }

    /**
     * 获取xslt数据根据code
     * @param code xslt定义的code
     * @return xslt数据
     */
    public String getXslt(String code) {
        return this.redisService.get(XSLT_KEY_PREFIX + code);
    }

    /**
     * 清楚所有xslt缓存
     */
    public void clearCache() {
        this.redisService.batchDelete(XSLT_KEY_PREFIX + "*");
    }

    /**
     * 将一个xml数据通过xslt进行转换
     * @param xml xml数据
     * @param xsltValue xslt数据
     * @return 转换后的数据
     */
    public String transform(String xml, String xsltValue) {
        if (StringUtils.isEmpty(xsltValue))
            return xml;
        try {
            TransformerFactory factory = TransformerFactory.newInstance();
            ByteArrayInputStream xsltInputStream = new ByteArrayInputStream(xsltValue.getBytes(charset));
            ByteArrayInputStream xmlInputStream = new ByteArrayInputStream(xml.getBytes(charset));
            Transformer transformer = factory.newTransformer(new StreamSource(xsltInputStream));
            StringWriter writer = new StringWriter();
            StreamResult streamResult = new StreamResult(writer);

            transformer.transform(new StreamSource(xmlInputStream), streamResult);
            String result = "";
            writer.write(result);
            return result;
        } catch (TransformerException e) {
            e.printStackTrace();
            return "";
        }
    }
}
