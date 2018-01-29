package com.wu.common;

import com.wu.common.email.parse.YamlConfigParser;
import com.wu.common.email.send.EmailSender;
import org.apache.commons.mail.MultiPartEmail;
import org.junit.Test;

import java.lang.reflect.Method;


/**
 * Created by wuxinbo on 18-1-25.
 */
public class EmailTest {

    @Test
    public void parse() throws Exception{
            new YamlConfigParser().parse();
    }
    @Test
    public void sendEmail() {
        new EmailSender().sendEmail(EmailSender.ConfigSource.YAML);
    }

}