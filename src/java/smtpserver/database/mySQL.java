package smtpserver.database;

import smtpserver.storage.Mail;

import java.sql.*;

public class mySQL {
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://47.96.139.38:3306/邮箱系统?useSSL=false&serverTimezone=UTC&useUnicode=true&characterEncoding=utf8";
    private static final String user = "root";
    private static final String pwd = "Lijin1125.";
    private Connection connection = null;
    private Statement statement = null;
    public mySQL() throws ClassNotFoundException, SQLException {
        Class.forName(JDBC_DRIVER);
        connection = DriverManager.getConnection(DB_URL,user,pwd);
        statement = connection.createStatement();
    }

    public boolean getUser(String address,String password) throws ClassNotFoundException, SQLException {
        String sql = "SELECT * FROM `user` WHERE address='"+address+"' AND `password`='"+password+"'";
        ResultSet resultSet= statement.executeQuery(sql);
        return resultSet.next();
    }

    public boolean addMail(Mail mail) throws ClassNotFoundException, SQLException {
        ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) AS cnt FROM mail");
        if(resultSet.next()) {
            String sql = "INSERT INTO mail VALUES('"
                    + Integer.toString(resultSet.getInt("cnt")+1)
                    + "','" + mail.getMailFrom()
                    + "','" + mail.getRcptTo()
                    + "','" + mail.getSubject()
                    + "','" + mail.getDate()
                    + "',0,'" + mail.getData()
                    + "'," + mail.getLength() + ",0,0)";
            statement.executeUpdate(sql);
        }
        return true;
    }

    static public void main(String[] args) throws SQLException, ClassNotFoundException {
        Mail mail = new Mail();
        mail.setMailFrom("root@diker.xyz");
        mail.setRcptTo("test@diker.xyz");
        mail.setSubject("Test");
        mail.setData("Hello World!\r\n换行测试。\r\n中文测试：我**你个**");
        mail.commit();
        Class.forName(JDBC_DRIVER);
        Connection connection = DriverManager.getConnection(DB_URL,user,pwd);
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) AS cnt FROM mail");
        if(resultSet.next()){
            String sql = "INSERT INTO mail VALUES('"
                    + Integer.toString(resultSet.getInt("cnt")+1)
                    + "','" + mail.getMailFrom()
                    + "','" + mail.getRcptTo()
                    + "','" + mail.getSubject()
                    + "','" + mail.getDate()
                    + "',0,'" + mail.getData()
                    + "'," + mail.getLength() + ",0,0)";
            System.out.println(sql);
            //sql = new String(sql.getBytes(),"utf8");
            //sql = new String(sql.getBytes("utf8"),"GB2312");
            //sql = new String(sql.getBytes("ISO-8859-1"),"ISO-8859-1");
            //sql = new String(sql.getBytes("gbk"),"gbk");
            //sql = new String(sql.getBytes("unicode"),"unicode");
            //URLEncoder.encode(sql,"utf8");
            System.out.println(sql);
            statement.executeUpdate(sql);
        }
    }
}
