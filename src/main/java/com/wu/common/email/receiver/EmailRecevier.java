package com.wu.common.email.receiver;

import java.util.List;

/**
 * 收件人和抄送人信息,包含邮件内容
 * @author wuxinbo
 */
public class EmailRecevier {
    /**
     * 收件人列表
     */
    private List<String> to;
    /**
     * 抄送列表
     */
    private List<String> cc;
    /**
     * 邮件内容
     */
    private String msg;
    /**
     * 邮件主题
     */
    private String subject;

    public EmailRecevier(List<String> to, String msg,String subject) {
        this.to = to;
        this.msg = msg;
        this.subject =subject;
    }
    public EmailRecevier() {
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public List<String> getCc() {
        return cc;
    }

    public void setCc(List<String> cc) {
        this.cc = cc;
    }

    public List<String> getTo() {
        return to;
    }

    public void setTo(List<String> to) {
        this.to = to;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
