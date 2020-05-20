package smtpserver;

import smtpserver.utils.ClientUtil;

import java.io.IOException;

public class SmtpClientRunner {

    public static void main(String[] args){
        try {
            if(ClientUtil.sendMail("root@diker.xyz","test@diker.xyz","疯狂测试","测试测试测试测试疯狂测试","root")){
                System.out.println("Send mail successful.");
            }
            else{
                System.out.println("Send mail failed.");
            }
            System.out.println(ClientUtil.login("test@diker.xyz","test"));
            System.out.println(ClientUtil.changeName("test@diker.xyz","test","洛阳"));
            System.out.println(ClientUtil.changePassword("test@diker.xyz","test","123qwe"));
            System.out.println(ClientUtil.login("test@diker.xyz","test"));
            System.out.println(ClientUtil.changeName("test@diker.xyz","test","洛阳2"));
            System.out.println(ClientUtil.login("zzz","zz"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
