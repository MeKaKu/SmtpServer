package smtpserver;

import smtpserver.storage.Mail;

import java.io.IOException;

public class SmtpClientRunner {
    public static void main(String[] args){
        Mail mail = new Mail();
        mail.setMailFrom("root@diker.xyz");
        mail.setRcptTo("test@diker.xyz");
        mail.setSubject("Test");
        mail.setData("Hello World!\r\n换行测试。\r\n中文测试：我**你个**");
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
