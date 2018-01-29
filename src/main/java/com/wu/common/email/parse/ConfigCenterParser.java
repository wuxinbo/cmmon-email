package com.wu.common.email.parse;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.SimpleEmail;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 通过解析spring配置中心配置的数据来配置邮件发送者
 * 应用启动时将会将配置中心的值注入到bean中
 * Created by wuxinbo on 18-1-26.
 */
@Component
public class ConfigCenterParser implements EmailConfigParser {
    /**
     * 主机名
     */
    @Value("${email.hostName}")
    private String hostName;
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
    public Email parse() throws Exception {
        Email email =new SimpleEmail();
        email.setHostName(hostName);
        email.setSslSmtpPort(sslSmtpPort);
        email.setAuthentication(userName,password);
        email.setFrom(from);
        return email;
    }
}
