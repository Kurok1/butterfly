package indi.butterfly.executor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import indi.butterfly.Message;
import indi.butterfly.MessageFactory;
import indi.butterfly.ResponseMessage;
import indi.butterfly.core.ButterflyMessage;
import indi.butterfly.template.LocalFileWriteTemplate;
import indi.butterfly.util.TextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

/**
 * 本地输出文件执行器
 *
 * @author <a href="mailto:maimengzzz@gmail.com">kuroky</a>
 * @version 2020.06.29
 * @since 1.0.0
 * @see IExecutor
 */
@Component
public class LocalFileWriteExecutor implements IExecutor {

    private LocalFileWriteTemplate template = null;

    private final Logger logger = LoggerFactory.getLogger(LocalFileWriteExecutor.class);

    private final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    @Override
    @KafkaListener(topics = {"butterfly-localFile-write"}, id = "butterfly.localFile.write")
    public ResponseMessage<Object> execute(ButterflyMessage message) {
        Message result = beforeExecute(message);
        if (result.isError()) {
            //记录日志
            logger.error("预处理发生错误: {}", result.getMsg());
            //记录处理日志
            return MessageFactory.errorResponse(result.getMsg());
        }


        File director = new File(template.getTargetDirectory());
        if (!director.exists()) {
            boolean flag = director.mkdirs();
            if (!flag) {
                //记录日志
                logger.error("创建输出目录失败: {}", template.getTargetDirectory());
                //记录处理日志
                return MessageFactory.errorResponse(String.format("创建输出目录失败: %s", template.getTargetDirectory()));
            }
        }

        String fileName = getFormattedFileName(template.getFileName());

        File file = new File(director, fileName);
        boolean flag = false;
        try {
            flag = file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!flag) {
            //记录日志
            logger.error("创建输出文件失败: {}", file.getAbsolutePath());
            return MessageFactory.errorResponse(String.format("创建输出文件失败: %s", file.getAbsolutePath()));
        }

        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            //记录日志
            logger.error("创建输出文件输出流失败: {}", file.getAbsolutePath());
            return MessageFactory.errorResponse(String.format("创建输出文件输出流失败: %s", file.getAbsolutePath()));
        }

        Assert.notNull(fileOutputStream, String.format("创建输出文件输出流失败: %s", file.getAbsolutePath()));

        if (StringUtils.hasLength(this.template.getTitle())) {
            try {
                StreamUtils.copy(this.template.getTitle().getBytes(DEFAULT_CHARSET), fileOutputStream);
            } catch (IOException e) {
                e.printStackTrace();
                logger.error("输出文件失败");
                return MessageFactory.errorResponse("输出文件失败");
            }
        }

        if (template.getUseOriginData() == 1) {
            //直接输出就完事了
            byte[] bytes = message.getRequestBody().getBytes(DEFAULT_CHARSET);//全部按照UTF-8处理
            try {
                StreamUtils.copy(bytes, fileOutputStream);
            } catch (IOException e) {
                e.printStackTrace();
                logger.error("输出文件失败");
                return MessageFactory.errorResponse("输出文件失败");
            }

        } else {
            switch (template.getOriginFormat().toUpperCase()) {
                case "JSON": {
                    ObjectMapper mapper = TextUtil.getMapper("JSON");
                    result = outputToFile(mapper, message.getRequestBody(), fileOutputStream);
                    if (result.isError()) {
                        //记录日志
                        logger.error(result.getMsg());
                        //记录处理日志
                        return MessageFactory.errorResponse(result.getMsg());
                    }
                }break;
                case "XML": {
                    ObjectMapper mapper = TextUtil.getMapper("XML");
                    result = outputToFile(mapper, message.getRequestBody(), fileOutputStream);
                    if (result.isError()) {
                        //记录日志
                        logger.error(result.getMsg());
                        //记录处理日志
                        return MessageFactory.errorResponse(result.getMsg());
                    }
                }break;
                default: //默认按string处理
                case "STRING" : {
                    //直接输出就完事了
                    byte[] bytes = message.getRequestBody().getBytes(DEFAULT_CHARSET);//全部按照UTF-8处理
                    try {
                        StreamUtils.copy(bytes, fileOutputStream);
                    } catch (IOException e) {
                        e.printStackTrace();
                        logger.error("输出文件失败");
                        return MessageFactory.errorResponse("输出文件失败");
                    }

                }break;

            }

        }
        //正常来说文件输出完成后就没有下一步的操作了
        return MessageFactory.successResponse("success", null);
    }

    @Override
    public String getExecutorId() {
        return "butterfly.localFile.write";
    }

    private Message outputToFile(ObjectMapper objectMapper, String source, FileOutputStream outputStream) {
        try {
            JsonNode node = objectMapper.readTree(source);
            if (node.getNodeType() == JsonNodeType.ARRAY) {
                for (JsonNode element : node) {
                    outputToFile(element, outputStream);
                }
            } else outputToFile(node, outputStream);
            outputStream.close();
            return MessageFactory.success();
        } catch (Exception e) {
            e.printStackTrace();
            return MessageFactory.error(String.format("输出发生异常: %s", e.getMessage()));
        }
    }

    private void outputToFile(JsonNode node, FileOutputStream outputStream) throws Exception {
        Iterator<String> fields = node.fieldNames();
        StringBuffer buffer = new StringBuffer();
        while (fields.hasNext()) {
            String field = fields.next();
            buffer.append(node.get(field).asText(" "));
            buffer.append(this.template.getFieldDelimiter());
        }
        buffer.deleteCharAt(buffer.length() - this.template.getFieldDelimiter().length());
        buffer.append(this.template.getRowDelimiter());
        byte[] bytes = buffer.toString().getBytes(DEFAULT_CHARSET);
        StreamUtils.copy(bytes, outputStream);
    }

    /**
     * 获取格式化之后的文件名
     * @param fileName 文件名模板
     * @return 格式化之后的文件名
     */
    private String getFormattedFileName(String fileName) {
        //TODO 模板处理
        return fileName;
    }

    @Override
    public Message beforeExecute(ButterflyMessage message) {
        //读取配置
        template = TextUtil.readJson(message.getConfigJson(), LocalFileWriteTemplate.class);
        if (template == null) {
            logger.error("配置数据错误: {}", message.getConfigJson());
            return MessageFactory.error("读取配置数据错误");
        }

        if (StringUtils.isEmpty(template.getTargetDirectory())) {
            logger.error("未配置输出目录");
            return MessageFactory.error("未配置输出目录");
        }

        if (StringUtils.isEmpty(template.getFileName())) {
            logger.error("未配置输出文件名");
            return MessageFactory.error("未配置输出文件名");
        }

        return MessageFactory.success();
    }
}
