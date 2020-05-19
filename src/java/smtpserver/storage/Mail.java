package smtpserver.storage;

import smtpserver.utils.Base64Util;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Mail {
    private static final String path = "SmtpServer//EmailBox//";
    private String mailFrom; //发件人邮箱
    private String rcptTo; //收件人邮箱
    private String subject; //邮件主题
    private String data = ""; //邮件内容
    private int length;//长度
    private String date;//日期

    public void setMailFrom(String from){
        mailFrom = from.split("\r\n")[0];
    }

    public void setRcptTo(String rcpt){
        rcptTo = rcpt.split("\r\n")[0];
    }

    public void setData(String msg){
        data = msg+"\r\n";
    }

    public void setSubject(String sub){
        subject = sub;
    }

    public String getMailFrom(){
        return mailFrom;
    }
    public String getRcptTo(){
        return rcptTo;
    }
    public String getSubject(){
        return subject;
    }
    public  String getData(){
        return data;
    }
    public int getLength(){
        return length;
    }
    public String getDate(){
        return date;
    }

    public void dataAdd(String msg){
        data += msg;
        data += "\r\n";
    }

    public void reset(){
        mailFrom = "";
        rcptTo = "";
        subject = "";
        data = "";
        length = 0;
    }

    public void commit(){
        //data = new String(data.getBytes(), StandardCharsets.UTF_8);
        length = mailFrom.length()+rcptTo.length()+subject.length()+data.length();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        date = df.format(new Date());
    }

    //保存邮件至path/rcptTo/receiveBox/time.txt
    public void solveToFile() throws IOException {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");//设置日期格式
        String fileNameString = df.format(new Date());// new Date()为获取当前系统时间
        String dirString = path + rcptTo + "/receiveBox";
        File dir = new File(dirString);
        if(!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dirString + "/" + fileNameString + ".txt");

        FileWriter fileWriter = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        bufferedWriter.write("From:"+mailFrom);
        bufferedWriter.newLine();
        bufferedWriter.write("To:"+rcptTo+"\r\n");
        bufferedWriter.write("Subject:"+subject+"\r\n");
        bufferedWriter.write(data);
        bufferedWriter.close();

    }

    //客户端向服务器发送邮件
    public boolean sendMail(String account,String password) throws IOException {
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress("mail.diker.xyz",25),5000);
        if(!socket.isConnected()){
            System.out.println("Connected failed.");
            return false;
        }
        boolean ret = true;
        if(0==mailFrom.length()||0==rcptTo.length()||0==data.length()){
            return false;
        }
        else{
            String readline;
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
            assert printStream != null;
            assert bufferedReader != null;
            //220
            readline = bufferedReader.readLine();
            if(!readline.split(" ")[0].equals("220")){
                System.out.println("connect returned error");
                return false;
            }
            //helo
            printStream.println("helo client");
            readline = bufferedReader.readLine();
            if(readline.split("-")[0].equals("250")){
                readline = bufferedReader.readLine();
            }
            if(!readline.split(" ")[0].equals("250")){
                System.out.println("helo returned error.");
                return false;
            }
            //auth login
            printStream.println("auth login");
            readline = bufferedReader.readLine();
            if(!readline.split(" ")[0].equals("334")){
                System.out.println("auth login returned error.");
                return false;
            }
            //account
            printStream.println(Base64Util.EncodeBase64(account.getBytes()));
            readline = bufferedReader.readLine();
            if(!readline.split(" ")[0].equals("334")){
                System.out.println("account returned error.");
                return false;
            }
            //password
            printStream.println(Base64Util.EncodeBase64(password.getBytes()));
            readline = bufferedReader.readLine();
            if(!readline.split(" ")[0].equals("235")){
                System.out.println("password returned error.");
                return false;
            }
            //mail from
            printStream.println("mail from:<"+mailFrom+">");
            readline = bufferedReader.readLine();
            if(!readline.split(" ")[0].equals("250")){
                System.out.println("mail from returned error.");
                return false;
            }
            //rcpt to
            printStream.println("rcpt to:<"+rcptTo+">");
            readline = bufferedReader.readLine();
            if(!readline.split(" ")[0].equals("250")){
                System.out.println("rcpt to returned error.");
                return false;
            }
            //data
            printStream.println("data");
            readline = bufferedReader.readLine();
            if(!readline.split(" ")[0].equals("354")){
                System.out.println("data returned error.");
                return false;
            }
            //mail body
            printStream.println(subject+"\r\n"+data+".");
            readline = bufferedReader.readLine();
            if(!readline.split(" ")[0].equals("250")){
                System.out.println("mail body returned error.");
                return false;
            }
            //quit
            printStream.println("quit");
            readline = bufferedReader.readLine();
            if(!readline.split(" ")[0].equals("221")){
                System.out.println("quit returned error.");
                return false;
            }

        }
        socket.close();
        return true;
    }
}
