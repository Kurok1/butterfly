package indi.butterfly.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.DigestUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.Key;

/**
 * 文本操作工具
 * @author <a href="mailto:maimengzzz@gmail.com">kuroky</a>
 * @version 2020.06.09
 * @since 1.0.0
 */
public class TextUtil {

    private final static Logger logger = LoggerFactory.getLogger(TextUtil.class);

    private final static ObjectMapper JSON = new ObjectMapper();

    private final static XmlMapper XML = new XmlMapper();

    private static volatile boolean inited = false;

    public static String toPassword(String text) {
        return DigestUtils.md5DigestAsHex(text.getBytes());
    }

    private static void init() {
        if (inited)
            return;

        try {
            mEncryptCipher = Cipher.getInstance("DES");
            mEncryptCipher.init(Cipher.ENCRYPT_MODE, getKey(KEY.getBytes()));
            mDecryptCipher = Cipher.getInstance("DES");
            mDecryptCipher.init(Cipher.DECRYPT_MODE, getKey(KEY.getBytes()));
        } catch (Exception e) {
            logger.error("初始化失败");
            return;
        }

        inited = true;
    }

    /** 对称加解密DES密钥Key*/
    public final static String KEY = "abcbutterflyzxy";

    private static Cipher mEncryptCipher = null;
    private static Cipher mDecryptCipher = null;


//   ****** 加密 ******

    /**
     * 对 字符串 加密
     * */
    public static String encrypt(String strIn) {
        if (!inited)
            init();

        try {
            return byte2HexStr(encrypt(strIn.getBytes()));
        } catch (Throwable t) {
            logger.error("加密失败: {}", t.getMessage());
            return "";
        }

    }

    /**
     * 对 字节数组 加密
     */
    private static byte[] encrypt(byte[] arrB) throws Exception {
        return mEncryptCipher.doFinal(arrB);
    }


//   ****** 解密 ******

    /**
     * 解密 字符串
     * */
    public static String decrypt(String strIn) {
        if (!inited)
            init();

        try {
            return new String(decrypt(hexStr2Byte(strIn)));
        } catch (Throwable t) {
            logger.error("解密失败:{}", t.getMessage());
            return "";
        }

    }

    /**
     * 解密 字节数组
     */
    private static byte[] decrypt(byte[] arrB) throws Exception {
        return mDecryptCipher.doFinal(arrB);
    }


    /**
     * 解密用的密钥（字节数组）长度必须为8个字节否则返回null, 不足8位时后面补0，超出8位只取前8位
     *
     * @param arrBTmp 构成该字符串的字节数组
     * @return 生成的密钥
     * @throws Exception
     */

    private static Key getKey(byte[] arrBTmp) throws Exception {
        // 创建一个空的8位字节数组（默认值为0）
        byte[] arrB = new byte[8];

        // 将原始字节数组转换为8位
        for (int i = 0; i < arrBTmp.length && i < arrB.length; i++) {
            arrB[i] = arrBTmp[i];
        }

        // 生成密钥
        Key key = new SecretKeySpec(arrB, "DES");

        return key;
    }

    /**
     * HEX转码 String to Byte
     */
    private static byte[] hexStr2Byte(String strIn) throws Exception {
        byte[] arrB = strIn.getBytes();
        int iLen = arrB.length;

        // 两个字符表示一个字节，所以字节数组长度是字符串长度除以2
        byte[] arrOut = new byte[iLen / 2];
        for (int i = 0; i < iLen; i = i + 2) {
            String strTmp = new String(arrB, i, 2);
            arrOut[i / 2] = (byte) Integer.parseInt(strTmp, 16);
        }
        return arrOut;
    }

    /**
     * HEX转码 Byte to  String
     */
    private static String byte2HexStr(byte[] arrB) throws Exception {
        int iLen = arrB.length;
        // 每个byte用两个字符才能表示，所以字符串的长度是数组长度的两倍
        StringBuffer sb = new StringBuffer(iLen * 2);
        for (int i = 0; i < iLen; i++) {
            int intTmp = arrB[i];
            // 把负数转换为正数
            while (intTmp < 0) {
                intTmp = intTmp + 256;
            }
            // 小于0F的数需要在前面补0
            if (intTmp < 16) {
                sb.append("0");
            }
            sb.append(Integer.toString(intTmp, 16));
        }
        return sb.toString();
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
