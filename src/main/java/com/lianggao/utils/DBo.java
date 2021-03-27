package com.lianggao.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

public class DBo {
    //  private Connection conn;
    private static DataSource dataSource;
    static {
        ApplicationContext ac = new FileSystemXmlApplicationContext("classpath:applicationContext.xml");
        dataSource = (DataSource) ac.getBean("dataSource");
    }
    public static Connection getConn() throws SQLException {
//        ApplicationContext ac = new FileSystemXmlApplicationContext("classpath:applicationContext.xml");
//        DataSource datasource = (DataSource) ac.getBean("dataSource");
//        Connection conn=datasource.getConnection();
//        return conn;
        return DataSourceUtils.getConnection(dataSource);
    }
//    ApplicationContext ac = new FileSystemXmlApplicationContext("classpath:applicationContext.xml");
//    DataSource datasource= (DataSource) ac.getBean("dataSource");
    public DBo() {



//        ApplicationContext ac = new FileSystemXmlApplicationContext("classpath:applicationContext.xml");
//        DataSource datasource = (DataSource) ac.getBean("dataSource");
//        try {
//            conn= datasource.getConnection();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
    }

    /*
     * 查询数据
     * */
    public List<Map<String, Object>> executeQuery(String sql) throws SQLException {
        System.out.println("执行sql：");
        System.out.println(sql);
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Connection conn = getConn();
        Statement statement = conn.createStatement();
        ResultSet resultSet = null;
        try {
            resultSet = statement.executeQuery(sql);
            ResultSetMetaData md = resultSet.getMetaData(); //获得结果集结构信息,元数据
            int columnCount = md.getColumnCount();   //获得列数
            while (resultSet.next()) {
                Map<String, Object> rowData = new HashMap<String, Object>();
                for (int i = 1; i <= columnCount; i++) {
                    rowData.put(md.getColumnName(i), resultSet.getObject(i));
                }
                list.add(rowData);

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            resultSet.close();
            statement.close();

        }
        return list;
    }

    /*
     * 获取对应字段的值
     * */
    public String getString(String sql, String name) throws SQLException {
        List<Map<String, Object>> list = executeQuery(sql);
        if (list.size() != 0) {
            Set set1 = list.get(0).keySet();
            Iterator it1 = set1.iterator();
            while (it1.hasNext()) {
                String temp = it1.next().toString();
                if (temp.toLowerCase().equals(name.toLowerCase())) {
                    return list.get(0).get(temp).toString();
                }

            }
            return "";
        } else {

            return "";
        }

    }

    private static int capacity = 10;

    public List<Map<String, Object>> getListByPage(String sql, int pageIndex) throws SQLException {
        if (pageIndex != 0) {
            sql = "select * from (" + sql + ") A where rn>" + (pageIndex - 1) * capacity + " and rn<=" + pageIndex * capacity;
        }

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
//       DBConnectionPoll dbConnectionPoll=new DBConnectionPoll();
//       Connection conn=dbConnectionPoll.getConnection();
        Connection conn = getConn();
        Statement statement = conn.createStatement();
        ResultSet resultSet = null;
        try {
            resultSet = statement.executeQuery(sql);
            ResultSetMetaData md = resultSet.getMetaData(); //获得结果集结构信息,元数据
            int columnCount = md.getColumnCount();   //获得列数
            while (resultSet.next()) {
                Map<String, Object> rowData = new HashMap<String, Object>();
                for (int i = 1; i <= columnCount; i++) {
                    rowData.put(md.getColumnName(i), resultSet.getObject(i));
                }
                list.add(rowData);

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            resultSet.close();
            statement.close();
          //  dbConnectionPoll.releaseConnection(conn);
        }
        return list;
    }

    /*
     * 对数据进行更新数据
     * 更新成功返回 影响的行数
     * */
    public int executeUpdate(String sql) throws SQLException {
//        DBConnectionPoll dbConnectionPoll=new DBConnectionPoll();
//        Connection conn=dbConnectionPoll.getConnection();
        Connection conn = getConn();
        Statement statement = conn.createStatement();
        int result = 0;
        try {
            result = statement.executeUpdate(sql);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            statement.close();
          //  dbConnectionPoll.releaseConnection(conn);
        }
        return result;
    }

    /**
     * 执行sql语句
     * @param sql sql语句
     * @throws Exception sql异常
     */
    public void execute(String sql) throws Exception{
        Connection conn = getConn();
        PreparedStatement pstm = null;

        pstm = conn.prepareStatement(sql);
        pstm.execute();
    }

}
