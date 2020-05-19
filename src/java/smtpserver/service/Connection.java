package smtpserver.service;

import smtpserver.database.mySQL;
import smtpserver.storage.Mail;
import smtpserver.utils.Base64Util;
import smtpserver.utils.DnsMxUtil;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

public class Connection implements Runnable {
    private String address; //主机地址（域名）
    private final Socket socket;
    private Mail mail = new Mail();
    private String account;
    private String password;
    private boolean isHelo = false;
    private boolean isMail = false;
    private boolean isRcpt = false;
    private boolean isData = false;
    private boolean isEnd = false;
    private boolean isTitle = false;
    private boolean isServer = false;
    private boolean isAuth = false;
    private boolean isAuthing = false;
    private boolean isAuthAccount = false;
    private final mySQL mysql = new mySQL();
    public Connection(Socket socket, String address) throws SQLException, ClassNotFoundException {
        this.address = address;
        this.socket = socket;
    }

    @Override
    public void run() {
//        try {
//            socket.setSoTimeout(30000); //读超时时间
//        } catch (SocketException e) {
//            e.printStackTrace();
//        }
        String readline = null;
        BufferedReader bufferedReader = null;
        PrintStream printStream = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
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

        while(!isEnd&&socket.isConnected()){
            assert bufferedReader != null;
            try {
                readline = bufferedReader.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            assert readline != null;
            readline = readline.trim();
            System.out.println("Client:" + readline);
            String[] inputs = readline.split(":");
            //System.out.println(readline);
            inputs[0] = inputs[0].trim();
            inputs[0] = inputs[0].toLowerCase();//命令不区分大小写
            String[] heads = inputs[0].split(" ");//命令头
            //System.out.println("msgHead:" + inputs[0]);
            if(heads.length==2&&(heads[0].equals("helo")||heads[0].equals("ehlo"))){ ////helo/ehlo hostname
                //isServer = DnsMxUtil.isMailServer(heads[1],socket.getInetAddress().getHostAddress());
                try {
                    isServer = DnsMxUtil.isMailServer(heads[1],socket.getInetAddress().getHostAddress());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(isServer){
                    printStream.println("250-mail.diker.xyz-ready for transport\r\n250 OK");
                    System.out.println("MailServer");
                }
                else{
                    printStream.println("250-mail.diker.xyz\r\n250 OK");
                }
                isHelo = true;
                isAuthing=isAuth=isAuthAccount=false;
            }
            else if(readline.equals("auth login")){ //auth login
                printStream.println("334 auth login");
                isAuthing = true;
            }
            else if(inputs.length==2&&heads.length==2&&heads[0].equals("mail")&&heads[1].equals("from")){ //mail from : <123@diker.com>
                if(!heloAndAuth(printStream)) continue;
                inputs[1]=inputs[1].trim();
                String[] tails = inputs[1].split(" ");
                if(tails.length==1){
                    mail.setMailFrom(tails[0].substring(1,tails[0].length()-1));
                    printStream.println("250 OK");
                    isMail = true;
                }
                else{
                    printStream.println("503 Invalid input.");
                }
            }
            else if(inputs.length==2&&heads.length==2&&heads[0].equals("rcpt")&&heads[1].equals("to")){ //rcpt to : <234@diker.com>
                if(!heloAndAuth(printStream)) continue;
                if(!isMail){
                    printStream.println("503 Send command mail from first.");
                }
                inputs[1]=inputs[1].trim();
                String[] tails = inputs[1].split(" ");
                if(tails.length==1){
                    mail.setRcptTo(tails[0].substring(1,tails[0].length()-1));
                    printStream.println("250 OK");
                    isRcpt = true;
                }
                else{
                    printStream.println("503 Invalid input.");
                }
            }
            else if(inputs.length==1&&heads.length==1&&heads[0].equals("data")){ //data
                if(!heloAndAuth(printStream)) continue;
                if(!isMail||!isRcpt){
                    printStream.println("503 Send Command mail from and rcpt to first.");
                }
                else{
                    printStream.println("354 End data with <CRLF>.<CRLF>");
                    isData = true;
                }
            }
            else if(inputs.length==1&&heads.length==1&&heads[0].equals("quit")){//quit
                printStream.println("221 Bye");
                isEnd = true;
            }
            else{
                if(isAuthing&&inputs.length==1){
                    if(!isAuthAccount){
                        account = Base64Util.DecodeBase64(readline);
                        System.out.println("account:"+account);
                        printStream.println("334 diker");
                        isAuthAccount = true;
                    }
                    else{
                        password = Base64Util.DecodeBase64(readline);
                        System.out.println("password:"+password);
                        try {
                            if(mysql.getUser(account,password)){
                                printStream.println("235 Authentication successful");
                                isAuth = true;
                            }
                            else{
                                printStream.println("535 Login failed.");
                            }
                        } catch (ClassNotFoundException | SQLException e) {
                            e.printStackTrace();
                        }
                        isAuthing=isAuthAccount=false;
                    }
                }
                else if(isData){
                    if(readline.equals(".")){ //内容结束
                        mail.commit();
                        //存邮件
                        try {
                            mail.solveToFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            mysql.addMail(mail);
                        } catch (ClassNotFoundException | SQLException e) {
                            System.out.println("Save mail to database failed.");
                            e.printStackTrace();
                        }
                        mail.reset();
                        printStream.println("250 OK");
                        isMail=isRcpt=isData=isTitle=false;
                    }
                    else{
                        if(!isServer&&!isTitle){
                            mail.setSubject(readline);
                            isTitle = true;
                        }
                        else{
                            mail.dataAdd(readline);
                        }
                    }

                }
                else{
                    printStream.println("502 Invalid input.");
                }
            }
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean heloAndAuth(PrintStream printStream){
        if(!isHelo){
            printStream.println("503 Send command helo/ehlo first.");
            return false;
        }
        if(!isServer&&!isAuth){
            printStream.println("503 Need auth login first.");
            return false;
        }
        return true;
    }
}
