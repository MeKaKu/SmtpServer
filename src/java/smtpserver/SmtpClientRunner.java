package smtpserver;

import smtpserver.storage.Mail;

import java.io.IOException;

public class SmtpClientRunner {
    public static void main(String[] args){
        Mail mail = new Mail();
        mail.setMailFrom("root@diker.xyz");
        mail.setRcptTo("*@diker.xyz");
        mail.setSubject("群发测试");
        mail.setData("2ce新年快乐!\r\n网络编程第二次会议\r\n会议时间：2020/5/21 09:30-12:00 \r\n点击链接入会，或添加至会议列表： https://meeting.tencent.com/s/WLjAq4UTohPM \r\n会议 ID：583 429 531");
        try {
            if(mail.sendMail("root@diker.xyz","root")){
                System.out.println("Send mail successful.");
            }
            else{
                System.out.println("Send mail failed.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
