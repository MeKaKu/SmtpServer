package smtpserver.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ClientUtil {

    private static Socket socket;
    private static String readline;
    private static BufferedReader bufferedReader;
    private static PrintStream printStream;

    /**
     * 登录验证
     * @param account 账号（邮箱地址）
     * @param password 密码
     * @return 验证结果
     * */
    public static String login(String account, String password) throws IOException {
        if(!connect("diker.xyz",2525)) return "连接超时";
        printStream.println("501");
        printStream.println(Base64Util.EncodeBase64(account.getBytes()));
        printStream.println(Base64Util.EncodeBase64(password.getBytes()));
        readline = bufferedReader.readLine();
        disconnect();
        return readline.split("-")[1];
    }
    /**
     * 登录验证
     * @param account 账号（邮箱地址）
     * @param password 密码
     * @param newPassword 新密码
     * @return 验证结果
     * */
    public static String changePassword(String account,String password,String newPassword) throws IOException {
        if(!connect("diker.xyz",2525)) return "连接超时";
        printStream.println("502");
        printStream.println(Base64Util.EncodeBase64(account.getBytes()));
        printStream.println(Base64Util.EncodeBase64(password.getBytes()));
        printStream.println(Base64Util.EncodeBase64(newPassword.getBytes()));
        readline = bufferedReader.readLine();
        disconnect();
        return readline.split("-")[1];
    }

    /**
     * 登录验证
     * @param account 账号（邮箱地址）
     * @param password 密码
     * @param name 用户名
     * @return 验证结果
     * */
    public static String changeName(String account,String password,String name) throws IOException {
        if(!connect("diker.xyz",2525)) return "连接超时";
        printStream.println("503");
        printStream.println(Base64Util.EncodeBase64(account.getBytes()));
        printStream.println(Base64Util.EncodeBase64(password.getBytes()));
        printStream.println(name);
        readline = bufferedReader.readLine();
        disconnect();
        return readline.split("-")[1];
    }

    /**
    * 客户端群发邮件
     * @param mailFrom 发件人邮箱地址
     * @param subject 邮件主题
     * @param data 邮件内容
     * @param password 发件人邮箱密码
    * */
    public static boolean sendGroupMail(String mailFrom,String subject,String data,String password) throws IOException {
        return sendMail(mailFrom,"*@diker.xyz",subject,data,password);
    }

    /**
     * 客户端发邮件
     * @param mailFrom 发件人邮箱地址
     * @param rcptTo 收件人邮箱地址
     * @param subject 邮件主题
     * @param data 邮件内容
     * @param password 发件人邮箱密码
     * */
    public static boolean sendMail(String mailFrom,String rcptTo,String subject,String data,String password) throws IOException {
        boolean ret = true;
        if (0 == mailFrom.length() || 0 == rcptTo.length() || 0 == data.length()||!connect("mail.diker.xyz",25)) {
            return false;
        } else {
            //220
            readline = bufferedReader.readLine();
            if (!readline.split(" ")[0].equals("220")) {
                System.out.println("connect returned error");
                disconnect();
                return false;
            }
            //helo
            printStream.println("helo client");
            readline = bufferedReader.readLine();
            if (readline.split("-")[0].equals("250")) {
                readline = bufferedReader.readLine();
            }
            if (!readline.split(" ")[0].equals("250")) {
                System.out.println("helo returned error.");
                disconnect();
                return false;
            }
            //auth login
            printStream.println("auth login");
            readline = bufferedReader.readLine();
            if (!readline.split(" ")[0].equals("334")) {
                System.out.println("auth login returned error.");
                disconnect();
                return false;
            }
            //account
            printStream.println(Base64Util.EncodeBase64(mailFrom.getBytes()));
            readline = bufferedReader.readLine();
            if (!readline.split(" ")[0].equals("334")) {
                System.out.println("account returned error.");
                disconnect();
                return false;
            }
            //password
            printStream.println(Base64Util.EncodeBase64(password.getBytes()));
            readline = bufferedReader.readLine();
            if (!readline.split(" ")[0].equals("235")) {
                System.out.println("password returned error.");
                disconnect();
                return false;
            }
            //mail from
            printStream.println("mail from:<" + mailFrom + ">");
            readline = bufferedReader.readLine();
            if (!readline.split(" ")[0].equals("250")) {
                System.out.println("mail from returned error.");
                disconnect();
                return false;
            }
            //rcpt to
            printStream.println("rcpt to:<" + rcptTo + ">");
            readline = bufferedReader.readLine();
            if (!readline.split(" ")[0].equals("250")) {
                System.out.println("rcpt to returned error.");
                disconnect();
                return false;
            }
            //data
            printStream.println("data");
            readline = bufferedReader.readLine();
            if (!readline.split(" ")[0].equals("354")) {
                System.out.println("data returned error.");
                disconnect();
                return false;
            }
            //mail body
            printStream.println(subject + "\r\n" + data + "\r\n.");
            readline = bufferedReader.readLine();
            if (!readline.split(" ")[0].equals("250")) {
                System.out.println("mail body returned error.");
                disconnect();
                return false;
            }
            //quit
            printStream.println("quit");
            readline = bufferedReader.readLine();
            if (!readline.split(" ")[0].equals("221")) {
                System.out.println("quit returned error.");
                disconnect();
                return false;
            }
        }
        disconnect();
        return true;
    }

    private static boolean connect(String address,int port){
        socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(address, port), 5000);
            if (!socket.isConnected()) {
                System.out.println("Connected failed.");
                return false;
            }
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            printStream = new PrintStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        assert bufferedReader != null;
        return true;
    }

    private static void disconnect() throws IOException {
        bufferedReader.close();
        printStream.close();
        socket.close();
    }
}
