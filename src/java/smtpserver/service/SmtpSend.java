package smtpserver.service;

import smtpserver.utils.Base64Util;

import java.io.*;
import java.net.Socket;

public class SmtpSend {

    public static void main(String[] args) {
        /*
         *用户名和密码
         */
        String SendUser="OUJIANJAPAN@163.com";
        String SendPassword="diy123@,.0924";
        String ReceiveUser="OUJIANJAPAN@163.com";

        //对用户名和密码进行Base64编码
        String UserBase64= Base64Util.EncodeBase64(SendUser.getBytes());
        String PasswordBase64=Base64Util.EncodeBase64(SendPassword.getBytes());

        try {
            /*
             *远程连接smtp.163.com服务器的25号端口
             *并定义输入流和输出流(输入流读取服务器返回的信息、输出流向服务器发送相应的信息)
             */
            Socket socket=new Socket("smtp.163.com", 25);
            InputStream inputStream=socket.getInputStream();//读取服务器返回信息的流
            InputStreamReader isr=new InputStreamReader(inputStream);//字节解码为字符
            BufferedReader br=new BufferedReader(isr);//字符缓冲

            OutputStream outputStream=socket.getOutputStream();//向服务器发送相应信息
            PrintWriter pw=new PrintWriter(outputStream, true);//true代表自带flush
            System.out.println(br.readLine());

            /*
             *向服务器发送信息以及返回其相应结果
             */

            //helo
            pw.println("helo diker.xyz");
            System.out.println(br.readLine());

            //auth login
            pw.println("auth login");
            System.out.println(br.readLine());
            pw.println(UserBase64);
            System.out.println(br.readLine());
            pw.println(PasswordBase64);
            System.out.println(br.readLine());

            //Set "mail from" and  "rect to"
            pw.println("mail from:<"+SendUser+">");
            System.out.println(br.readLine());
            pw.println("rcpt to:<"+ReceiveUser+">");
            System.out.println(br.readLine());

            //Set "data"
            pw.println("data");
            System.out.println(br.readLine());

            //正文主体(包括标题,发送方,接收方,内容,点)
            pw.println("subject:diker");
            pw.println("from:"+SendUser);
            pw.println("to:"+ReceiveUser);
            //pw.println("Content-Type: text/plain;charset=\"gb2312\"");//设置编码格式可发送中文内容
            pw.println();
            pw.println("测试。Thanks!");
            pw.println(".");
            pw.print("");
            System.out.println(br.readLine());

            /*
             *发送完毕,中断与服务器连接
             */
            pw.println("rset");
            System.out.println(br.readLine());
            pw.println("quit");
            System.out.println(br.readLine());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
