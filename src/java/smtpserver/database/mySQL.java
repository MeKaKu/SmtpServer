package smtpserver.database;

import smtpserver.storage.Mail;

import java.sql.*;

public class mySQL {
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://47.96.139.38:3306/邮箱系统?useSSL=false&serverTimezone=UTC";
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
        Class.forName(JDBC_DRIVER);
        Connection connection = DriverManager.getConnection(DB_URL,user,pwd);
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) AS cnt FROM mail");
        if(resultSet.next()){
            String sql = "INSERT INTO mail VALUES('"+(resultSet.getInt("cnt")+1)+"','test01','test02','test','2020/5/19',0,'tests',200,0,0)";
            statement.executeUpdate(sql);
        }
    }
}
