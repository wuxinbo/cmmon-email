package com.wu.common.email.parse;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.SimpleEmail;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 通过解析spring配置中心配置的数据来配置邮件发送者
 * 应用启动时将会将配置中心的值注入到bean中
 * Created by wuxinbo on 18-1-26.
 */
@Component
public class ConfigCenterParser extends AbstractConfigParser{
    /**
     * 主机名
     */
    @Value("${email.hostName}")
    private String hostName;
    /**
     * 编码
     */
    @Value("${email.charset}")
    private String charset;
    /**
     * 加密通信端口
     */
    @Value("${email.sslSmtpPort}")
    private String sslSmtpPort;
    /**
     * 用户名
     */
    @Value("${email.userName}")
    private String userName;
    /**
     * 密码
     */
    @Value("${email.password}")
    private String password;
    /**
     * 邮件发送来自于
     */
    @Value("${email.from}")
    private String from;
    /**
     * 邮件类型
     */
    @Value("${email.type}")
    private String type;
    /**
     * 调式模式
     */
    @Value("${email.debug}")
    private boolean debug;
    @Override
    protected EmailType parseEmailType() throws Exception {
        if (StringUtils.isEmpty(type)){ //如果没有设置值，默认发送简单文本邮件
            return EmailType.SIMPLE;
        }
        return EmailType.valueOf(type);
    }

    @Override
    protected Email doParse(EmailType type) throws Exception {
        Email email = type.getInstClass().newInstance();
        email.setHostName(hostName);
        email.setCharset(charset);
        email.setSslSmtpPort(sslSmtpPort);
        email.setSSLOnConnect(!StringUtils.isEmpty(sslSmtpPort)?true:false);
        email.setAuthentication(userName,password);
        email.setFrom(from);
        email.setDebug(debug);
        return email;
    }

}
