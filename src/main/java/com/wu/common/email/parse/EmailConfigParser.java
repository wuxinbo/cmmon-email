package com.wu.common.email.parse;

import org.apache.commons.mail.Email;

/**
 * 邮箱配置信息解析接口
 * @author wuxinbo
 * @since 1.0
 */
public interface EmailConfigParser {
    /**
     * 解析配置信息
     * @return 返回封装好的邮件配置信息
     */
    Email parse() throws Exception;

}
