package smtpserver.service;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Server implements Runnable {
    private final int port; //端口号
    private final ServerSocket serverSocket; //服务socket
    private final String address; //服务器地址（域名）

    public Server(int port, String address) throws IOException {
        this.port = port;
        this.address = address;
        this.serverSocket = new ServerSocket(port); //创建socket
    }

    private String[] getAnonOnly(String[] suites) {
        List<String> resultList = new ArrayList<>();
        for (String s : suites) {
            if (s.contains("anon")) {
                resultList.add(s);
            }
        }
        return resultList.toArray(new String[resultList.size()]);
    }

    @Override
    public void run() {
        try {
            System.out.println(String.format("Server \"%s\" started.\nAddress: %s.\nListening on port %d.",
                    address, InetAddress.getLocalHost(), port));
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        }
        while (true) {
            try {
                System.out.println("Listening for a connection.");
                Socket socket = serverSocket.accept(); //监听
                System.out.println("Connection accepted by." + socket.getInetAddress().getHostName());
                new Thread(new Connection(socket, address)).start(); //创建新的线程处理连接请求
            } catch (IOException | SQLException | ClassNotFoundException ex) {
                ex.printStackTrace();
                break;
            }
        }
    }
}
