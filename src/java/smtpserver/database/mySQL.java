package smtpserver.database;

import smtpserver.storage.Mail;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class mySQL {
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://47.96.139.38:3306/邮箱系统?useSSL=false&serverTimezone=UTC&useUnicode=true&characterEncoding=utf8";
    private static final String user = "root";
    private static final String pwd = "Lijin1125.";
    private Connection connection = null;
    private Statement statement = null;
    private int cnt;
    public mySQL() throws ClassNotFoundException, SQLException {
        Class.forName(JDBC_DRIVER);
        connection = DriverManager.getConnection(DB_URL,user,pwd);
        statement = connection.createStatement();
    }

    public boolean getUser(String address,String password) throws SQLException {
        String sql = "SELECT * FROM `user` WHERE address='"+address+"' AND `password`='"+password+"'";
        ResultSet resultSet= statement.executeQuery(sql);
        return resultSet.next();
    }

    public boolean isUserExist(String address) throws SQLException {
        String sql = "SELECT * FROM `user` WHERE address='"+address+"'";
        ResultSet resultSet= statement.executeQuery(sql);
        return resultSet.next();
    }

    public void setPwd(String account,String nPwd) throws SQLException {
        String sql = "UPDATE `user` SET `password`='"+nPwd+"' WHERE address='"+account+"'";
        statement.executeUpdate(sql);
    }

    public void setName(String account,String name) throws SQLException {
        String sql = "UPDATE `user` SET rname='"+name+"' WHERE address='"+account+"'";
        statement.executeUpdate(sql);
    }

    public boolean addMail(Mail mail) throws SQLException {
        ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) AS cnt FROM mail");
        if(resultSet.next()) {
            cnt = resultSet.getInt("cnt");
            resultSet.close();
            if(!mail.getRcptTo().equals("*@diker.xyz")) {
                addMailSql(mail,mail.getRcptTo());
            }
            else{ //群发
                ResultSet rcptToSet = statement.executeQuery("SELECT address FROM `user` WHERE address!='"+mail.getMailFrom()+"'");
                //System.out.println("SELECT address FROM `user` WHERE address!='"+mail.getMailFrom()+"'");
                List<String> list = new ArrayList<>();
                while(rcptToSet.next()){
                    String rcptTo = rcptToSet.getString("address");
                    list.add(rcptTo);
                }
                for (String s : list) {
                    addMailSql(mail, s);
                }
            }
        }
        return true;
    }

    private void addMailSql(Mail mail,String rcptTo) throws SQLException {
//        if(rcptTo.split("@").length!=2||!rcptTo.split("@")[1].equals("diker.xyz")){
//            return;
//        }
        cnt++;
        String sql = "INSERT INTO mail VALUES('"
                + cnt
                + "','" + mail.getMailFrom()
                + "','" + rcptTo
                + "','" + mail.getSubject()
                + "','" + mail.getDate()
                + "',0,'" + mail.getData()
                + "'," + mail.getLength() + ",0,0)";
        statement.executeUpdate(sql);
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
