package com.wu.common.email.send;

import com.wu.common.email.parse.ConfigCenterParser;
import com.wu.common.email.parse.EmailConfigParser;
import com.wu.common.email.parse.YamlConfigParser;
import com.wu.common.email.receiver.EmailRecevier;
import org.apache.commons.mail.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
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
       return init(source,null);
    }
    private Email init(ConfigSource source,EmailRecevier receviers)throws Exception{
        if (source==null){ //如果没有传
            source = YAML;
        }
        EmailConfigParser emailConfigParser = PARSER_MAP.get(source.name()).newInstance();
        return receviers==null?emailConfigParser.parse():emailConfigParser.parseAndattachRecevier(receviers);
    }
    /**
     * 该方法是{@link EmailSender#sendEmail(ConfigSource)}的重载，用于将收件人和抄送者列表提取出来，由调用者指定需要发送的内容。
     * 将收件信息和需要发送的内容单独提取出来，不从配置里面读取，这是最大的不同
     * @param source 配置方式来源
     * @param receviers 收件人和抄送人
     * @see #sendEmail(ConfigSource)
     */
    public void sendEmail(ConfigSource source, List<EmailRecevier> receviers){
        try {
            if (receviers==null&&receviers.isEmpty()){ //如果为空抛出异常，程序终止
                throw new IllegalArgumentException("receviers 不能为空");
            }
            for (EmailRecevier recevier : receviers) { //如果有多个需要发送的邮件可以循环发送
                Email email = init(source,recevier);
                String result = email.send();
                logger.debug("send result is "+result);
            }
        } catch (Exception e) {
            logger.error("email send fail reason is ",e);
        }
    }

    /**
     * 调用者只需要提供一个配置来源就可以完成邮件的发送，而其余收件人信息和需要发送的内容都将会从配置文件里面读取。在某些固定的场景中，如果
     * 收件人和每次需要发送的内容比较固定则可以使用该方法。如果需要灵活性可以使用{@link EmailSender#sendEmail(ConfigSource, List)}
     * @param source 配置方式来源
     * @see #sendEmail(ConfigSource, List)
     */
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
