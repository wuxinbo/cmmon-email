package com.wu.common.email.send;

import com.wu.common.email.parse.ConfigCenterParser;
import com.wu.common.email.parse.EmailConfigParser;
import com.wu.common.email.parse.YamlConfigParser;
import org.apache.commons.mail.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static com.wu.common.email.send.EmailSender.ConfigSource.CONFIG_CENTER;
import static com.wu.common.email.send.EmailSender.ConfigSource.YAML;

/**
 * 邮件发送器
 * Created by wuxinbo on 18-1-26.
 */
public class EmailSender {
    /**
     * 日志打印
     */
    private Logger logger = LoggerFactory.getLogger(EmailSender.class);
    /**
     * 配置来源
     */
    public enum ConfigSource{
        /**
         * 来自于本地JSON方式
         */
        JSON,
        /**
         * 来自于本地YAML格式
         */
        YAML,
        /**
         * 来自于统一配置中心（需要引入spring）
         */
        CONFIG_CENTER;
    }

    private static final Map<String,Class<? extends EmailConfigParser>> PARSER_MAP =new HashMap();
    static {
        PARSER_MAP.put(YAML.name(),YamlConfigParser.class); //YAML 配置文件
        PARSER_MAP.put(CONFIG_CENTER.name(), ConfigCenterParser.class); //配置中心
    }
    /**
     * 初始化邮件，
     * @param source  配置信息来源，如果没有来源将会使用本地YAML格式
     */
    private Email init(ConfigSource source) throws Exception {
        if (source==null){ //如果没有传
            source = YAML;
        }
        EmailConfigParser emailConfigParser = PARSER_MAP.get(source.name()).newInstance();
        return emailConfigParser.parse();

    }
//    public void sendEmail(ConfigSource source,)
    public void sendEmail(ConfigSource source){
        try {
            Email email = init(source);
            String result = email.send();
            logger.debug("send result is "+result);
        } catch (Exception e) {
            logger.error("email send fail reason is ",e);
        }
    }

}
