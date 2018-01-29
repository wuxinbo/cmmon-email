package com.wu.common.email.parse;

import org.apache.commons.mail.Email;

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
public class AbstractConfigParser implements EmailConfigParser{

    protected enum EmailType{
        /**
         * 附件邮件
         */
        MUTIPART,
        /**
         * html 邮件
         */
        HTML,
        /**
         * 文本邮件
         */
        SIMPLE
    }

    public Email parse() throws Exception {
        return null;
    }
}
