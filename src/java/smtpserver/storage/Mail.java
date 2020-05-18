package smtpserver.storage;

import smtpserver.utils.Base64Util;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Mail {
    private static final String path = "SmtpServer//EmailBox//";
    private static String mailFrom; //发件人邮箱
    private static String rcptTo; //收件人邮箱
    private static String subject; //邮件主题
    private static String data; //邮件内容

    public void setMailFrom(String from){
        mailFrom = from.split("\r\n")[0];
    }

    public void setRcptTo(String rcpt){
        rcptTo = rcpt.split("\r\n")[0];
    }

    public void setData(String msg){
        data = msg;
    }

    public void setSubject(String sub){
        subject = sub;
    }

    public void dataAdd(String msg){
        data += msg;
        data += "\r\n";
    }

    public void reset(){
        mailFrom = "";
        rcptTo = "";
        data = "";
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
        bufferedWriter.write(data);
        bufferedWriter.close();

    }

    //客户端向服务器发送邮件
    public boolean sendMail(String account,String password) throws IOException {
        Socket socket = new Socket("mail.diker.xyz",25);
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

            //helo
            printStream.println("helo client");
            readline = bufferedReader.readLine();
            if(!readline.split(" ")[0].equals("250")){
                System.out.println("helo returned error.");
                return false;
            }
            //auth login
            printStream.println("auth login");
            readline = bufferedReader.readLine();
            if(!readline.split(" ")[0].equals("334")){
                System.out.println("auth login returned error.");
            }
            //account
            printStream.println(Base64Util.EncodeBase64(account.getBytes()));
            readline = bufferedReader.readLine();
            if(!readline.split(" ")[0].equals("334")){
                System.out.println("account returned error.");
            }
            //password
            printStream.println(Base64Util.EncodeBase64(password.getBytes()));
            readline = bufferedReader.readLine();
            if(!readline.split(" ")[0].equals("235")){
                System.out.println("password returned error.");
            }
            //mail from
            printStream.println("mail from:<"+mailFrom+">");
            readline = bufferedReader.readLine();
            if(!readline.split(" ")[0].equals("250")){
                System.out.println("mail from returned error.");
            }
            //rcpt to
            printStream.println("rcpt to:<"+rcptTo+">");
            readline = bufferedReader.readLine();
            if(!readline.split(" ")[0].equals("250")){
                System.out.println("rcpt to returned error.");
            }
            //data
            printStream.println("data");
            readline = bufferedReader.readLine();
            if(!readline.split(" ")[0].equals("354")){
                System.out.println("data returned error.");
            }
            //mail body
            printStream.println(subject+"\r\n"+data+".");
            readline = bufferedReader.readLine();
            if(!readline.split(" ")[0].equals("250")){
                System.out.println("mail body returned error.");
            }
            //quit
            printStream.println("quit");
            readline = bufferedReader.readLine();
            if(!readline.split(" ")[0].equals("221")){
                System.out.println("quit returned error.");
            }

        }
        return ret;
    }
}
