package com.wu.common.email.parse;

import com.wu.common.email.receiver.EmailRecevier;
import org.apache.commons.mail.Email;

import java.util.List;

/**
 * 邮箱配置信息解析接口
 * @author wuxinbo
 * @since 1.0
 */
public interface EmailConfigParser {
    /**
     * 解析配置信息,从配置文件读取
     * @return 返回封装好的邮件配置信息
     */
    Email parse() throws Exception;

    /**
     * 解析配置信息之后在解析收件人和抄送人信息
     * @param receiver 收件人或抄送人信息
     * @return 返回封装好的邮件配置信息
     * @throws Exception 解析邮件失败抛出异常
     */
    Email parseAndattachRecevier(EmailRecevier receiver)throws Exception;

}
