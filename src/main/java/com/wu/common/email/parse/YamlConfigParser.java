package com.wu.common.email.parse;

import org.apache.commons.mail.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * yaml 格式配置文件解析器
 * Created by wuxinbo on 18-1-25.
 */
public class YamlConfigParser extends AbstractConfigParser{
    private static Logger logger =LoggerFactory.getLogger(YamlConfigParser.class);
    /**
     * 字符串截取长度
     */
    private static final int METHODNAME_BEGINDEX=3;
    /**
     * 用户名key对应map
     */
    private static final String USERNAME_KEY="userName";
    /**
     * map 中的密码key
     */
    private static final String PASSWD_KEY="password";
    /**
     * 邮件类型
     */
    private static final String EMAIL_TYPE_KEY="Type";
    /**
     * 附件信息
     */
    private static final String ATTACHMENT_KEY="attachment";
    /**
     * html内容
     */
    private static final String HTMLMSG_KEY="htmlMsg";

    /**
     * 附件网络地址
     */
    private static final String ATTACHMENT_URL_KEY="Url";

    /**
     * 收件人
     */
    private static final String TO_KEY="To";
    /**
     * 抄送人
     */
    private static final String CC_KEY="Cc";
    /**
     * 加密方式
     */
    private static final String ENCRYPT_KEY="Encrypt";



    /**
     * 配置文件名字
     */
    private static final String COINFIG_NAME="email.yaml";
    /**
     * 在将map转化成Email对象时不参与解析
     */
    private static final String[] EXCLUDE_FIELD=new String []{TO_KEY,CC_KEY};
    /**
     * 原始的config配置信息map结构
     */
    private Map<String,Object> configMap ;


    /**
     * 从yaml配置中读取配置信息并初步转化为map
     * @return 解析好的map对象
     */
    private Map<String,Object> parseConfig2Map(){
        InputStream is =getClass().getClassLoader().getResourceAsStream(COINFIG_NAME);
        return (Map<String, Object>) new Yaml().load(is);
    }
    protected EmailType parseEmailType() throws Exception {
        configMap =parseConfig2Map();
        return EmailType.valueOf((String) configMap.get(EMAIL_TYPE_KEY));
    }

    protected Email doParse(EmailType type) throws Exception {
      Email email=  (Email) map2Object(type.getInstClass(), configMap);
       return mananuParse(configMap, email);
    }

    @Override
    protected MultiPartEmail doParseMutiPartEmail(MultiPartEmail email) throws Exception {
        Map attachconfig = (Map) configMap.get(ATTACHMENT_KEY);
        EmailAttachment attachment = (EmailAttachment) map2Object(EmailAttachment.class,attachconfig );
        attachment.setDisposition(EmailAttachment.ATTACHMENT);
        //附件支持本地文件和网络文件发送，这里做一个兼容处理如果设置了url，就使用url从网络读取文件来发送
        attachment.setURL(new URL((String)attachconfig.get(ATTACHMENT_URL_KEY)));
        return email.attach(attachment);
    }

    @Override
    protected HtmlEmail doParseHtmlEmail(HtmlEmail email) throws Exception {
        email.setHtmlMsg((String) configMap.get(HTMLMSG_KEY));
        return email;
    }

    /**
     * 将map转化为object
     * @param convertClass 需要转化的类
     * @param config 配置信息
     */
    private Object map2Object(Class convertClass, Map config) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        Method[] methods = convertClass.getMethods();
        Object o = convertClass.newInstance();
        for (Method method : methods) {
            String methodName =method.getName();
            if (methodName.startsWith("set")){ //只检查set方法
                String methodNameKey =methodName.substring(METHODNAME_BEGINDEX);
                Object value =config.get(methodNameKey);
                //目前只支持一个参数的set方法，由于Email有些字段不能直接调用set方法，所以需要手工指定
                if (    value!=null
                        &&method.getParameterTypes().length==1
                        &&!Arrays.asList(EXCLUDE_FIELD).contains(methodNameKey)) { //mapkey和对象一一对应
                    logger.debug("parse config key is "+methodName+" value is "+value);
                    method.invoke(o,value);
                }
            }
        }
        return o;
    }
    /**
     * 由于map2email方法并不能完成全部的解析工作，所以剩下的解析只能通过手动编码来解析
     * @param config map结构配置信息
     * @param email 初次转化后的邮件对象
     * @return 完成解析的email
     */
    private Email mananuParse(Map config,Email email) throws EmailException, IllegalAccessException, InstantiationException, InvocationTargetException {
        //解析用户名和密码
        email.setAuthentication((String) config.get(USERNAME_KEY),(String) config.get(PASSWD_KEY));
        //解析收件人邮箱
        addToAndCc((List) config.get(TO_KEY),(List) config.get(CC_KEY),email);
        //解析加密方式，根据需要选择加密方式或者不加密
        setEncrypt((String) config.get(ENCRYPT_KEY),email);
        return email;
    }

}
