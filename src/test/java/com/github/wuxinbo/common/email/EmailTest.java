package com.github.wuxinbo.common.email;

import com.github.wuxinbo.common.email.parse.YamlConfigParser;
import com.github.wuxinbo.common.email.send.EmailSender;
import org.apache.commons.mail.Email;
import org.junit.Assert;
import org.junit.Test;



/**
 * 邮件发送测试
 * Created by wuxinbo on 18-1-25.
 */
public class EmailTest {

    @Test
    public void parse() throws Exception{
        Email yamlEmail = new YamlConfigParser().parse();
        Assert.assertNotNull("不能为空",yamlEmail);
    }
//    @Test
    public void sendEmail() {
        new EmailSender().sendEmail(EmailSender.ConfigSource.YAML);
    }

}
