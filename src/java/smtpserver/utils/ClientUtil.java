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
     * 暂停SMTP服务
     * @param account 账号（邮箱地址）
     * @param password 密码
     * @return  0 操作成功
     *          1 账号不存在
     *          2 密码错误
     *         -1 连接失败
     *         -2 异常
     *         -3 参数不正确
     * */
    public static int pauseSMTP(String account,String password){
        return exe(account,password,"555");
    }

    /**
     * 开启SMTP服务
     * @param account 账号（邮箱地址）
     * @param password 密码
     * @return  0 操作成功
     *          1 账号不存在
     *          2 密码错误
     *         -1 连接失败
     *         -2 异常
     *         -3 参数不正确
     * */
    public static int resumeSMTP(String account,String password){
        return exe(account,password,"556");
    }

    /**
     * 查看SMTP服务状态
     * @param account 账号（邮箱地址）
     * @param password 密码
     * @return  0 SMTP已启动（启动）
     *          1 SMTP未启动（暂停）
     *          2 认证失败(账号不存在或者密码错误)
     *         -1 连接失败
     *         -2 异常
     *         -3 参数不正确
     * */
    public static int isSmtpPause(String account,String password){
        return exe(account,password,"557");
    }

    /**
     * 登录验证
     * @param account 账号（邮箱地址）
     * @param password 密码
     * @return  0 操作成功
     *          1 账号不存在
     *          2 密码错误
     *         -1 连接失败
     *         -2 异常
     *         -3 参数不正确
     * */
    public static int login(String account, String password) {
        return exe(account,password,"501");
    }

    /**
     * 修改密码
     * @param account 账号（邮箱地址）
     * @param password 密码
     * @param newPassword 新密码
     * @return  0 操作成功
     *          1 账号不存在
     *          2 密码错误
     *         -1 连接失败
     *         -2 异常
     *         -3 参数不正确
     * */
    public static int changePassword(String account,String password,String newPassword) {
        if(account.length()==0||password.length()==0||newPassword.length()==0) return -3;
        if(!connect("diker.xyz",2525)) return -1;
        printStream.println("502");
        printStream.println(Base64Util.EncodeBase64(account.getBytes()));
        printStream.println(Base64Util.EncodeBase64(password.getBytes()));
        printStream.println(Base64Util.EncodeBase64(newPassword.getBytes()));
        try {
            readline = bufferedReader.readLine();
            disconnect();
            return Integer.parseInt(readline.split("-")[0]);
        } catch (IOException e) {
            e.printStackTrace();
            return -2;
        }
    }

    /**
     * 修改用户名
     * @param account 账号（邮箱地址）
     * @param password 密码
     * @param name 用户名
     * @return  0 操作成功
     *          1 账号不存在
     *          2 密码错误
     *         -1 连接失败
     *         -2 异常
     *         -3 参数不正确
     * */
    public static int changeName(String account,String password,String name) {
        if(account.length()==0||password.length()==0||name.length()==0) return -3;
        if(!connect("diker.xyz",2525)) return -1;
        printStream.println("503");
        printStream.println(Base64Util.EncodeBase64(account.getBytes()));
        printStream.println(Base64Util.EncodeBase64(password.getBytes()));
        printStream.println(name);
        try {
            readline = bufferedReader.readLine();
            disconnect();
            return Integer.parseInt(readline.split("-")[0]);
        } catch (IOException e) {
            e.printStackTrace();
            return -2;
        }
    }

    /**
    * 客户端群发邮件
     * @param mailFrom 发件人邮箱地址
     * @param subject 邮件主题
     * @param data 邮件内容
     * @param password 发件人邮箱密码
     * @return  0 操作成功
     *          1 服务器状态异常
     *          2 认证失败(账号不存在或者密码错误)
     *          3 操作失败
     *          4 用户不存在
     *         -1 连接失败
     *         -2 异常
     *         -3 参数不正确
    * */
    public static int sendGroupMail(String mailFrom,String subject,String data,String password) {
        return sendMail(mailFrom,"*@diker.xyz",subject,data,password);
    }

    /**
     * 客户端发邮件
     * @param mailFrom 发件人邮箱地址
     * @param rcptTo 收件人邮箱地址
     * @param subject 邮件主题
     * @param data 邮件内容
     * @param password 发件人邮箱密码
     * @return  0 操作成功
     *          1 服务器状态异常
     *          2 认证失败(账号不存在或者密码错误)
     *          3 操作失败
     *          4 用户不存在
     *         -1 连接失败
     *         -2 异常
     *         -3 参数不正确
     *         -4 SMTP未开启
     * */
    public static int sendMail(String mailFrom,String rcptTo,String subject,String data,String password) {
        if(isSmtpPause(mailFrom,password)!=0) return -4;
        if (0 == mailFrom.length() || 0 == rcptTo.length() ||0==password.length()) return -3;
        if (!connect("mail.diker.xyz",25)){
            System.out.println("连接失败");
            return -1;
        }
        try {
            //220
            readline = bufferedReader.readLine();
            if (!readline.split(" ")[0].equals("220")) {
                System.out.println("connect returned error");
                disconnect();
                return 1;
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
                return 3;
            }
            //auth login
            printStream.println("auth login");
            readline = bufferedReader.readLine();
            if (!readline.split(" ")[0].equals("334")) {
                System.out.println("auth login returned error.");
                disconnect();
                return 3;
            }
            //account
            printStream.println(Base64Util.EncodeBase64(mailFrom.getBytes()));
            readline = bufferedReader.readLine();
            if (!readline.split(" ")[0].equals("334")) {
                System.out.println("account returned error.");
                disconnect();
                return 3;
            }
            //password
            printStream.println(Base64Util.EncodeBase64(password.getBytes()));
            readline = bufferedReader.readLine();
            if (!readline.split(" ")[0].equals("235")) {
                System.out.println("password returned error.");
                disconnect();
                return 2;
            }
            //mail from
            printStream.println("mail from:<" + mailFrom + ">");
            readline = bufferedReader.readLine();
            if (!readline.split(" ")[0].equals("250")) {
                System.out.println("mail from returned error.");
                disconnect();
                return 3;
            }
            //rcpt to
            printStream.println("rcpt to:<" + rcptTo + ">");
            readline = bufferedReader.readLine();
            if (!readline.split(" ")[0].equals("250")) {
                System.out.println("rcpt to returned error.");
                disconnect();
                return 4;
            }
            //data
            printStream.println("data");
            readline = bufferedReader.readLine();
            if (!readline.split(" ")[0].equals("354")) {
                System.out.println("data returned error.");
                disconnect();
                return 3;
            }
            //mail body
            printStream.println(subject + "\r\n" + data + "\r\n.");
            readline = bufferedReader.readLine();
            if (!readline.split(" ")[0].equals("250")) {
                System.out.println("mail body returned error.");
                disconnect();
                return 3;
            }
            //quit
            printStream.println("quit");
            readline = bufferedReader.readLine();
            if (!readline.split(" ")[0].equals("221")) {
                System.out.println("quit returned error.");
                disconnect();
                return 3;
            }
            disconnect();
            return 0;
        } catch (IOException e) {
            e.printStackTrace();
            return -2;
        }
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
            printStream = new PrintStream(socket.getOutputStream(),true,"UTF-8");
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

    private static int exe(String account, String password,String code) {
        if(account.length()==0||password.length()==0) return -3;
        if(!connect("diker.xyz",2525)) return -1;
        printStream.println(code);
        printStream.println(Base64Util.EncodeBase64(account.getBytes()));
        printStream.println(Base64Util.EncodeBase64(password.getBytes()));
        try {
            readline = bufferedReader.readLine();
            disconnect();
            return Integer.parseInt(readline.split("-")[0]);
        } catch (IOException e) {
            e.printStackTrace();
            return -2;
        }
    }
}
