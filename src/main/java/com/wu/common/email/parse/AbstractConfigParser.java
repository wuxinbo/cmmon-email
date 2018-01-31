package com.wu.common.email.parse;

import com.wu.common.email.receiver.EmailRecevier;
import org.apache.commons.mail.*;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.List;

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
     * 解析YAML配置的加密方式，决定加密方式
     * @param encrypt 加密方式
     * @param email 邮件信息
     * @return 解析好的配置信息
     */
    protected Email setEncrypt(String encrypt, Email email){
        if (!StringUtils.isEmpty(encrypt)) {
            if (Encrypt.SSL.name().equals(encrypt)){ //SSL 加密方式
                email.setSSLOnConnect(true);
            }else if (Encrypt.TLS.name().equals(encrypt)){ //TLS 加密方式
                email.setStartTLSEnabled(true);
            }
        }
        return email;

    }

    /**
     * 邮件类型
     */
    protected enum EmailType{
        /**
         * 附件邮件
         */
        MUTIPART(MultiPartEmail.class),
        /**
         * html 邮件
         */
        HTML(HtmlEmail.class),
        /**
         * 文本邮件
         */
        SIMPLE(SimpleEmail.class);
        private Class<? extends Email> instClass;
        EmailType(Class<? extends Email> instClass) {
            this.instClass = instClass;
        }

        public Class<? extends Email> getInstClass() {
            return instClass;
        }
    }

    /**
     * smtp 邮件服务器加密方式
     */
    protected enum Encrypt{
        /**
         * 不加密
         */
        NONE,
        /**
         * SSL 加密方式
         */
        SSL,
        /**
         * TLS 加密方式
         */
        TLS;

    }
     /**
     * 追加收件人信息和邮件信息，如果之前配置文件里面有定义，在这里将会覆盖之前定义的。
     * @param toList 收件人列表
     * @param ccList 抄送列表
     * @param email  邮件信息
     * @throws EmailException 如果收件人列表为空将会抛出异常
     */
    protected void addToAndCc(List<String> toList, List<String> ccList, Email email) throws EmailException {
        if (toList==null||toList.isEmpty()){
            throw new IllegalArgumentException("收件人列表为空");
        }
        for (String to : toList) {
            email.addTo(to);
        }
        //解析抄送人邮箱
        if (ccList!=null&&!ccList.isEmpty()){ //抄送列表可能为空，所以需要做非空判断
            for (String cc : ccList) {
                email.addCc(cc);
            }
        }
    }

    /**
     * 初步实现解析配置文件，如果在解析配置信息时没有得到发送邮件类型，默认采用发送文本邮件方案。
     * @return 解析好的配置文件
     * @throws Exception 解析失败抛出异常
     */
    public Email parse() throws Exception {
        //首先需要解析出基础配置信息，来判断当前是发送哪种类型的邮件
        EmailType type =parseEmailType() == null ? EmailType.SIMPLE : parseEmailType();
        Email email = doParse(type);
        afterParse(type,email,null);
        return email;
    }

    /**
     * 在完成初步解析之后可以调用此方法来进行二次加工
     * @param type 邮件类型
     * @param email 在{@link AbstractConfigParser#doParse(EmailType)}方法得到的邮件配置
     * @param recevier 收件信息
     * @return 解析完成后的对象
     * @throws Exception 解析失败抛出异常
     */
    protected Email afterParse(EmailType type,Email email,EmailRecevier recevier) throws Exception {
         switch (type){
            case SIMPLE:
                return doParseSimpleEmail((SimpleEmail) email);
            case MUTIPART:
                return doParseMutiPartEmail((MultiPartEmail) email);
            case HTML:
                return recevier==null?doParseHtmlEmail((HtmlEmail) email):doParseHtmlEmail((HtmlEmail) email,recevier);
        }
        return null;
    }

    @Override
    public Email parseAndattachRecevier(EmailRecevier receiver) throws Exception {
        EmailType type =parseEmailType() == null ? EmailType.SIMPLE : parseEmailType();
        Email email = doParse(type);
        addToAndCc(receiver.getTo(),receiver.getCc(),email);
        afterParse(type,email,receiver);
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

    /**
     * 解析带html邮件配置信息，不提供默认实现，由子类去实现
     * @param email 初步解析的配置信息
     * @return 解析好的html配置信息
     * @throws Exception 解析失败抛出错误
     */
    protected HtmlEmail doParseHtmlEmail(HtmlEmail email)throws Exception{
        return null;
    }

    /**
     * 解析收件人信息，
     * @param email
     * @param recevier
     * @return
     * @throws Exception
     */
    protected HtmlEmail doParseHtmlEmail(HtmlEmail email,EmailRecevier recevier)throws Exception{
        email.setHtmlMsg(recevier.getMsg());
        email.setSubject(recevier.getSubject());
        return email;
    }
    /**
     * 解析普通文本邮件的配置信息，如果有需要可以重写该方法，同样本类不提供实现，由子类去实现
     * @param email 初步解析的邮件配置信息
     * @return 解析并封装好的配置信息
     * @throws Exception 解析失败抛出异常
     */
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
