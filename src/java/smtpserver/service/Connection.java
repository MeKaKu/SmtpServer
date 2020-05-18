package smtpserver.service;

import smtpserver.storage.Mail;
import smtpserver.utils.Base64Util;
import smtpserver.utils.DnsMxUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class Connection implements Runnable {
    private String address; //主机地址（域名）
    private final Socket socket;
    private Mail mail = new Mail();
    private String account;
    private String password;
    public Connection(Socket socket, String address) {
        this.address = address;
        this.socket = socket;
    }

    @Override
    public void run() {
        boolean isHelo = false;
        boolean isMail = false;
        boolean isRcpt = false;
        boolean isData = false;
        boolean isEnd = false;
        boolean isTitle = false;
        boolean isDataEnd = false;
        boolean isServer = false;
        boolean isAuth = false;
        boolean isAuthing = false;
        boolean isAuthAccount = false;
        boolean isAuthPwd = false;
        String readline = null;
        BufferedReader bufferedReader = null;
        PrintStream printStream = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            printStream = new PrintStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        //返回服务器状态
        assert printStream != null;
        printStream.println("220 diker.xyz Smtp Mail Server.");

        while(!isEnd){
            assert bufferedReader != null;
            try {
                readline = bufferedReader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            assert readline != null;
            readline = readline.trim();
            System.out.println("R:" + readline);
            String[] inputs = readline.split(":");
            //System.out.println(readline);
            inputs[0] = inputs[0].trim();
            inputs[0] = inputs[0].toLowerCase();//命令不区分大小写
            String[] heads = inputs[0].split(" ");//命令头
            System.out.println("msgHead:" + inputs[0]);
            if(heads.length==2&&(heads[0].equals("helo")||heads[0].equals("ehlo"))){ ////helo/ehlo hostname
                //isServer = DnsMxUtil.isMailServer(heads[1],socket.getInetAddress().getHostAddress());
                try {
                    isServer = DnsMxUtil.isMailServer(heads[1],socket.getInetAddress().getHostAddress());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(isServer){
                    printStream.println("250-mail.diker.xyz-ready for transport\n250 OK");
                    System.out.println("MailServer");
                }
                else{
                    printStream.println("250-mail.diker.xyz\n250 OK");
                }
                isHelo = true;
            }
            else if(readline.equals("auth login")){
                printStream.println("334 auth login");
                isAuthing = true;
            }
            else if(inputs.length==2&&heads.length==2&&heads[0].equals("mail")&&heads[1].equals("from")){ //mail from : <123>
                if(!isHelo){
                    printStream.println("503 Send command helo/ehlo first.");
                }
                if(!isServer&&!isAuth){
                    printStream.println("503 Need auth login first.");
                }
                inputs[1]=inputs[1].trim();
                String[] tails = inputs[1].split(" ");
                if(tails.length==1){
                    mail.setMailFrom(tails[0].substring(1,tails[0].length()-1));
                    printStream.println("250 OK");
                    isMail = true;
                }
                else{
                    printStream.println("503 ERR");
                }
            }
            else if(inputs.length==2&&heads.length==2&&heads[0].equals("rcpt")&&heads[1].equals("to")){ //rcpt to : <234>
                inputs[1]=inputs[1].trim();
                String[] tails = inputs[1].split(" ");
                if(tails.length==1){
                    mail.setRcptTo(tails[0].substring(1,tails[0].length()-1));
                    printStream.println("250 OK");
                    isRcpt = true;
                }
            }
            else if(inputs.length==1&&heads.length==1&&heads[0].equals("data")){ //data
                if(!isMail||!isRcpt){
                    printStream.println("503 is not mail or rcpt");
                }
                else{
                    printStream.println("354 end with <CRLF>.<CRLF>");
                    isData = true;
                }
            }
            else if(inputs.length==1&&heads.length==1&&heads[0].equals("quit")){//quit
                printStream.println("221 Bye");
                isEnd = true;
            }
            else{
                if(isAuthing){
                    if(!isAuthAccount){
                        account = Base64Util.DecodeBase64(readline);
                        isAuthAccount = true;
                    }
                    else{
                        password = Base64Util.DecodeBase64(readline);
                        if(authRight()){
                            isAuth = true;
                        }
                        isAuthing=isAuthAccount=false;
                    }
                }
                else if(isData){
                    if(readline.equals(".")){
                        //存邮件
                        try {
                            mail.solveToFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mail.reset();
                        printStream.println("250 OK");
                        isDataEnd = true;
                        System.out.println("Email saved.");
                    }
                    else{
                        mail.dataAdd(readline);
                    }

                }
                else{
                    printStream.println("502 Invalid input");
                }
            }
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean authRight(){

        return true;
    }

}
