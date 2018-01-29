package com.wu.common.email.parse;

import org.apache.commons.mail.Email;
import org.apache.commons.mail.HtmlEmail;
import org.apache.commons.mail.MultiPartEmail;
import org.apache.commons.mail.SimpleEmail;

import java.lang.reflect.Method;

/**
 * 配置信息解析器抽象基类，负责对所有的解析器进行抽象。目前使用的邮件发送是apache-common，而且发送邮件都是需要生成Email的实例，
 * 在现实环境中有三种类型的邮件是很普遍的，例如：
 * <ul>
 *     <li>发送带有附件的邮件使用的是{@link org.apache.commons.mail.MultiPartEmail}</li>
 *     <li>发送普通邮件使用的是{@link org.apache.commons.mail.SimpleEmail}</li>
 *     <li>发送html邮件是使用的是{@linkplain org.apache.commons.mail.HtmlEmail}</li>
 * </ul>
 * 而解析器的功能就是根据配置文件来选择构建上面哪种实例。
 * @author wuxinbo
 * @since 1.0
 */
public abstract class AbstractConfigParser implements EmailConfigParser{

    /**
     * 邮件类型
     */
    protected enum EmailType{
        /**
         * 附件邮件
         */
        MUTIPART(MultiPartEmail.class,"doParseMutiPartEmail"),
        /**
         * html 邮件
         */
        HTML(HtmlEmail.class,"doParseHtmlEmail"),
        /**
         * 文本邮件
         */
        SIMPLE(SimpleEmail.class,"doParseSimpleEmail");
        private Class<? extends Email> instClass;
        private String methodName;
        EmailType(Class<? extends Email> instClass,String parseMthodName) {
            this.instClass = instClass;
            methodName =parseMthodName;
        }

        public Class<? extends Email> getInstClass() {
            return instClass;
        }

        public String getMethodName() {
            return methodName;
        }
    }

    /**
     * 实现解析配置文件，如果在解析配置信息时没有得到发送邮件类型，默认采用发送文本邮件方案。
     * @return 解析好的配置文件
     * @throws Exception 解析失败抛出异常
     */
    public Email parse() throws Exception {
        //首先需要解析出基础配置信息，来判断当前是发送哪种类型的邮件
        EmailType type =parseEmailType() == null ? EmailType.SIMPLE : parseEmailType();
        Email email = doParse(type);
        switch (type){
            case SIMPLE:
                return doParseSimpleEmail((SimpleEmail) email);
            case MUTIPART:
                return doParseMutiPartEmail((MultiPartEmail) email);
            case HTML:
                return doParseHtmlEmail((HtmlEmail) email);
        }
        return email;
    }

    protected abstract EmailType parseEmailType()throws Exception;

    /**
     * 解析带附件的邮件配置信息,该方法留给子类实现
     * @return 解析好的附件邮件
     * @throws Exception 解析失败抛出异常
     */
    protected  MultiPartEmail doParseMutiPartEmail(MultiPartEmail email) throws Exception{
        return null;
    }
    protected HtmlEmail doParseHtmlEmail(HtmlEmail email)throws Exception{
        return null;
    }
    protected SimpleEmail doParseSimpleEmail(SimpleEmail email)throws Exception{
        return null;
    }
    /**
     * 真正解析的配置文件不由父类实现，由子类去实现不同的解析。
     * @return 返回解析后的Email实例
     * @throws Exception 解析失败抛出异常
     */
    protected abstract  Email doParse(EmailType type)throws Exception;
}
