package com.wu.common.email.parse;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.EmailException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

/**
 * yaml 格式配置文件解析器
 * Created by wuxinbo on 18-1-25.
 */
public class YamlConfigParser extends AbstractConfigParser{
    private Logger logger =LoggerFactory.getLogger(getClass());
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
     * 收件人
     */
    private static final String TO_KEY="To";

    /**
     * 配置文件名字
     */
    private static final String COINFIG_NAME="email.yaml";
    /**
     * 在将map转化成Email对象时不参与解析
     */
    private static final String[] EXCLUDE_FIELD=new String []{"To"};
    public Email parse() throws Exception{
        InputStream is =getClass().getClassLoader().getResourceAsStream(COINFIG_NAME);
        Map<String,Object> config = (Map<String, Object>) new Yaml().load(is);
        Email email =mananuParse(config,map2Email(config));
        return email;
    }

    /**
     * 将map转化为email对象，利用反射将email的成员变量和map进行一一对应
     * @param config map结构的config对象
     * @return 解析好的Email对象
     */
    private Email map2Email(Map config) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        return (Email) map2Object(Email.class,config);
    }

    /**
     * 将map转化为object
     * @param convertClass 需要转化的类
     * @param config 配置信息
     */
    private Object map2Object(Class convertClass,Map config) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        Method[] methods = convertClass.getDeclaredMethods();
        Object o = convertClass.newInstance();
        for (Method method : methods) {
            String methodName =method.getName();
            if (methodName.startsWith("set")){ //只检查set方法
                String methodNameKey =methodName.substring(METHODNAME_BEGINDEX);
                Object value =config.get(methodNameKey);
                //目前只支持一个参数的set方法，由于Email有些字段不能直接调用set方法，所以需要手工指定
                if (value!=null&&method.getParameterTypes().length==1&& !Arrays.asList(EXCLUDE_FIELD).contains(methodNameKey)) { //mapkey和对象一一对应
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
        email.addTo((String) config.get(TO_KEY));
        //解析邮件附件信息
         EmailAttachment attachment = (EmailAttachment) map2Object(EmailAttachment.class,config);
//         email.set
        return email;
    }
}
