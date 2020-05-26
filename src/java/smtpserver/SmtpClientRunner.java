package smtpserver;

import smtpserver.utils.ClientUtil;

public class SmtpClientRunner {

    public static void main(String[] args){

        String subject = "换行测试";
        String data = "第一行\\\\r\\\\n第二行。";
        subject=subject.trim();
        //System.out.println(ClientUtil.register("diker@diker.xyz","diker","diker"));
        //System.out.println(ClientUtil.register("diker@diker.xyz","diker","diker"));
//        System.out.println(ClientUtil.isSmtpPause("root@diker.xyz","root"));
//        System.out.println(ClientUtil.pauseSMTP("root@diker.xyz","root"));
//        System.out.println(ClientUtil.isSmtpPause("root@diker.xyz","root"));
//        System.out.println(ClientUtil.resumeSMTP("root@diker.xyz","root"));
//        System.out.println(ClientUtil.isSmtpPause("root@diker.xyz","root"));
//        System.out.println(ClientUtil.login("t@diker.xyz","t"));
//        System.out.println(ClientUtil.sendMail("diker@diker.xyz","t@diker.xyz","封禁测试","正常用户向封禁用户发邮件","diker"));
//        System.out.println(ClientUtil.sendMail("t@diker.xyz","diker@diker.xyz","封禁测试","封禁用户向正常用户发邮件","t"));
        int ret;
        if((ret=ClientUtil.sendMail("joe@diker.xyz","test@diker.xyz",subject,data,"727899"))==0){
            System.out.println("Send mail successful.");
        }
        else{
            System.out.println("Send mail failed:"+ret);
        }

//        System.out.println(ClientUtil.login("zzz","zz")); //1
//        System.out.println(ClientUtil.login("test@diker.xyz","test")); //0
//        System.out.println(ClientUtil.changeName("test@diker.xyz","test","TEST")); //0
//        System.out.println(ClientUtil.changePassword("test@diker.xyz","test","123qwe")); //0
//        System.out.println(ClientUtil.login("test@diker.xyz","test")); //2
//        System.out.println(ClientUtil.changeName("test@diker.xyz","test","test")); //2
//        System.out.println(ClientUtil.changePassword("test@diker.xyz","123qwe","test")); //0
//        System.out.println(ClientUtil.changeName("test@diker.xyz","test","test")); //0
//
//        if(0==ClientUtil.sendGroupMail("test@diker.xyz","群发测试2","大噶好","test"))
//            System.out.println("群发成功");
//        else
//            System.out.println("群发失败");
//
//        System.out.println(ClientUtil.pauseSMTP("root@diker.xyz","root"));
//        if(0==ClientUtil.sendMail("root@diker.xyz","test@diker.xyz","测试","测试测试测试测试测试","root")){
//            System.out.println("Send mail successful.");
//        }
//        else{
//            System.out.println("Send mail failed.");
//        }
//        System.out.println(ClientUtil.resumeSMTP("root@diker.xyz","root"));
        //System.out.println(ClientUtil.pauseSMTP("root@diker.xyz","root"));
    }
}
