package indi.butterfly.util;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.mysql.cj.util.Base64Decoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.io.IOException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

/**
 * 文本操作工具
 * @author <a href="mailto:maimengzzz@gmail.com">kuroky</a>
 * @version 2020.06.09
 * @since 1.0.0
 */
public class TextUtil {

    private final static String key = "abutterz";

    private final static Logger logger = LoggerFactory.getLogger(TextUtil.class);

    private final static ObjectMapper JSON = new ObjectMapper();

    private final static XmlMapper XML = new XmlMapper();

    static {
        random = new SecureRandom();
        try {
            keySpec = new DESKeySpec(key.getBytes());
            keyFactory = SecretKeyFactory.getInstance("des");

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("初始化异常", e);
        }
    }

    private static SecureRandom random = null;
    private static DESKeySpec keySpec = null;
    private static SecretKeyFactory keyFactory = null;
    private static SecretKey secretKey = null;

    public static String toPassword(String text) {
        return DigestUtils.md5DigestAsHex(text.getBytes());
    }

    /**
     * des 解密
     * @param bytes 解密的数据
     * @return 解密结果
     */
    public static String decode(byte[] bytes) {

        if (bytes == null || bytes.length == 0)
            return "";

        if (secretKey == null) {
            try {
                secretKey = keyFactory.generateSecret(keySpec);
            } catch (InvalidKeySpecException e) {
                e.printStackTrace();
                logger.error("初始化异常", e);
                return "";
            }
        }
        Cipher cipher = null;
        try {
            cipher.init(Cipher.DECRYPT_MODE, secretKey, random);
            byte[] plainData = cipher.doFinal(bytes);
            return new String(plainData);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("解密失败", e);
            return "";
        }
    }


    /**
     * des加密
     * @param text 要加密的文本
     * @return 加密结果
     */
    public static String encode(String text) {
        if (StringUtils.isEmpty(text))
            return "";

        if (secretKey == null) {
            try {
                secretKey = keyFactory.generateSecret(keySpec);
            } catch (InvalidKeySpecException e) {
                e.printStackTrace();
                logger.error("初始化异常", e);
                return "";
            }
        }

        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("des");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, random);
            byte[] cipherData = cipher.doFinal(text.getBytes());
            return new BASE64Encoder().encode(cipherData);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("解密失败", e);
            return "";
        }

    }

    /**
     * 将一个对象转换为json
     * @param object 要转换的对象
     * @return 对象的json序列化格式
     */
    public static String transform2Json(Object object) {
        try {
            return JSON.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            logger.error("对象转换json失败", e);
            return "";
        }
    }

    /**
     * 将一个对象转换为xml
     * @param object 要转换的对象
     * @return 对象的xml序列化格式
     */
    public static String transform2Xml(Object object) {
        try {
            return XML.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            logger.error("对象转换xml失败", e);
            return "";
        }
    }

    public static ObjectMapper getMapper(String format) {
        if ("json".equals(format.toLowerCase()))
            return JSON;
        else if ("xml".equals(format.toLowerCase()))
            return XML;
        else return JSON;//不支持的格式按json处理
    }

    public static <T> T readJson(String json, Class<T> clazz) {
        try {
            return JSON.readValue(json, clazz);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("读取json失败", e);
            return null;
        }
    }

    public static <T> T readXml(String xml, Class<T> clazz) {
        try {
            return XML.readValue(xml, clazz);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("读取XML失败", e);
            return null;
        }
    }

    public static Object[] asArray(String json) {
        try {
            return JSON.readValue(json, Object[].class);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("读取json失败", e);
            return null;
        }
    }

    public static String writeObjectByFormat(Object object, String format) {
        try {
            if ("json".equals(format.toLowerCase()))
                return JSON.writeValueAsString(object);
            else if ("xml".equals(format.toLowerCase()))
                return XML.writeValueAsString(object);
            else return JSON.writeValueAsString(object);//不支持的格式按json处理
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }

}
