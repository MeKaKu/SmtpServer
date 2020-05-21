package smtpserver.service;

import smtpserver.database.mySQL;
import smtpserver.utils.Base64Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

public class Task implements  Runnable{

    private String account;
    private BufferedReader bufferedReader = null;
    private PrintStream printStream = null;
    private final Socket socket;
    private final mySQL mysql = new mySQL();

    public Task(Socket socket) throws SQLException, ClassNotFoundException {
        this.socket = socket;
    }

    @Override
    public void run() {

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

        assert bufferedReader != null;
        assert printStream != null;
        try {
            String readline = bufferedReader.readLine();

            if(readline.equals("501")){ //登录验证
                login();
            }
            else if(readline.equals("502")){ //修改密码
                if(login()){
                    readline = bufferedReader.readLine();
                    mysql.setPwd(account,Base64Util.DecodeBase64(readline));
                }
            }
            else if(readline.equals("503")){
                if(login()){
                    readline = bufferedReader.readLine();
                    mysql.setName(account, readline);
                }
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean login() throws SQLException, IOException {
        account = Base64Util.DecodeBase64(bufferedReader.readLine());
        String password = Base64Util.DecodeBase64(bufferedReader.readLine());
        if(!mysql.isUserExist(account)){
            printStream.println("1-用户不存在"); //用户不存在
        }
        else if(!mysql.getUser(account, password)){
            printStream.println("2-密码错误"); //密码错误
        }
        else{
            printStream.println("0-操作成功"); //验证成功
            return true;
        }
        return false;
    }
}
