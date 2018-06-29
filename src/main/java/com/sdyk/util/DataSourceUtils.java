package com.sdyk.util;

import java.beans.PropertyVetoException;
import java.sql.*;
import java.util.Date;
import javax.sql.DataSource;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.sdyk.constants.Constants;


public class DataSourceUtils {
    private static DataSource ds;

    static {
                 /*
          * 从配置文件读取配置信息 <?xml version="1.0" encoding="UTF-8"?> <c3p0-config> <!--
          * 默认配置，如果没有指定则使用这个配置 --> <default-config> <property
          * name="checkoutTimeout">30000</property> <property
          * name="idleConnectionTestPeriod">30</property> <property
          * name="initialPoolSize">3</property> <property
          * name="maxIdleTime">30</property> <property
          * name="maxPoolSize">10</property> <property
          * name="minPoolSize">3</property> <property
          * name="maxStatements">50</property> <property
          * name="acquireIncrement">3</property><!-- 如果池中数据连接不够时一次增长多少个 -->
          * <property name="driverClass">com.mysql.jdbc.Driver</property>
          * <property name="jdbcUrl">
          * <![CDATA[jdbc:mysql://127.0.0.1:3306/project?useUnicode=true&
          * characterEncoding=UTF-8]]> </property> <property
          * name="user">root</property> <property
          * name="password">789123</property> </default-config> </c3p0-config>
          *
          * ds = // 默认的读取c3p0-config.xml中默认配置 new ComboPooledDataSource();
          */
        ComboPooledDataSource cpds = null;
        try {
            cpds = new ComboPooledDataSource();
            cpds.setCheckoutTimeout(30000);
            cpds.setIdleConnectionTestPeriod(30);
            cpds.setInitialPoolSize(3);
            cpds.setMaxIdleTime(30);
            cpds.setMaxPoolSize(5);
            cpds.setMaxStatementsPerConnection(100);
            cpds.setMinPoolSize(3);
            cpds.setMaxStatements(75);
            cpds.setAcquireIncrement(3);
            cpds.setDriverClass(Constants.CONNECTION_DRIVER);
            cpds.setJdbcUrl(Constants.CONNECTION_URL);
            cpds.setUser(Constants.CONNECTION_USER);
            cpds.setPassword(Constants.CONNECTION_PASSWORD);
            ds = cpds;
        } catch (PropertyVetoException e) {
            System.out.println("与MySQL数据库连接失败！");
        }
    }

    private DataSourceUtils() {

    }

    public static DataSource getDatasSource() {
        return ds;
    }


    public static void updateLastChatInfo(Date lastUpdateTime,String msg,String from,String to){
        String updateLastChatInfoSql="update chat_communication_list set last_update_time=?,last_words=? where (senderId=? and acceptId=?) or (senderId=? and acceptId=?)";

        PreparedStatement pst = null;
        Connection connection=getConnection();
        try {

            pst = connection.prepareStatement(updateLastChatInfoSql);
            pst.setTimestamp(1,new Timestamp(lastUpdateTime.getTime()));
            pst.setString(2,msg );
            pst.setString(3, from);
            pst.setString(4, to);

            pst.setString(5, to);
            pst.setString(6, from);
            pst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally{
            closeDB(connection,pst,null);
        }

    }
    //释放资源，将数据库连接还给数据库连接池
    public static void closeDB(Connection conn,PreparedStatement ps,ResultSet rs) {
        try {
            if (rs!=null) {
                rs.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            try {
                if (ps!=null) {
                    ps.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally{
                try {
                    if (conn!=null) {
                        conn.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public static Connection getConnection() {
        Connection con = null;
        try {
            con = ds.getConnection();// 每一次从ds中获取一个新的连接
        } catch (Exception e) {
            e.printStackTrace();
        }
        return con;
    }
}