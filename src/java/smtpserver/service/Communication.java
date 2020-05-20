package smtpserver.service;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

public class Communication implements Runnable {
    private final ServerSocket serverSocket; //服务socket

    public Communication(int port) throws IOException {
        this.serverSocket = new ServerSocket(port); //创建socket
    }

    @Override
    public void run() {
        while (true) {
            try {
                //System.out.println("Listening for a connection.");
                Socket socket = serverSocket.accept(); //监听
                //System.out.println("Connection accepted by." + socket.getInetAddress().getHostName());
                new Thread(new Task(socket)).start(); //创建新的线程处理连接请求
            } catch (IOException ex) {
                ex.printStackTrace();
                break;
            } catch (SQLException | ClassNotFoundException throwables) {
                throwables.printStackTrace();
            }
        }
    }
}
