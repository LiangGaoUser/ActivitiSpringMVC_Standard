package com.lianggao.utils;

import org.apache.commons.dbutils.*;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DBUtils {

    private static DataSource dataSource;
    private static int capacity=10;

    static {
        ApplicationContext ac = new FileSystemXmlApplicationContext("classpath:applicationContext.xml");
        dataSource = (DataSource) ac.getBean("dataSource");
    }

    private static Connection getConn(){
        return DataSourceUtils.getConnection(dataSource);
    }

    public static int update(String sql){
        System.out.println(sql);
        QueryRunner queryRunner=new QueryRunner();
        int row=0;
        try {
            Connection conn=getConn();
            row=queryRunner.update(conn,sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return row;
    }

    public static int update(List<String> sqls){
        int[] rows=null;
        try {
            Connection conn=getConn();
            conn.setAutoCommit(false);
            Statement statement = conn.createStatement();
            for(String sql:sqls){
                System.out.println(sql);
                statement.addBatch(sql);
            }
            rows=statement.executeBatch();
            conn.setAutoCommit(true);
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(rows==null){
            return 0;
        }else{
            int count=0;
            for (int row:rows){
                count+=row;
            }
            return count;
        }

    }

    public static List<Map<String,Object>> query(String sql){
        System.out.println(sql);
        QueryRunner queryRunner=new QueryRunner();
        List<Map<String,Object>> list=new ArrayList<>();
        try {
            Connection conn=getConn();
            list=queryRunner.query(conn,sql,new MapListHandler());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list!=null?list:new ArrayList<>();
    }

    public static List<Map<String,Object>> getListByPage(String sql,int pageIndex){
        if(pageIndex!=0) {
            sql="select * from ("+sql+") A where rn>"+ (pageIndex-1)*capacity+" and rn<="+pageIndex*capacity;
        }

        return query(sql);
    }

    public static int getListNum(String sql) {
        sql="select count(*) as _sum from ("+sql+") A;";
        List<Map<String,Object>> list=query(sql);
        if(list.size()==0) return 0;
        String _sum=list.get(0).get("_sum").toString();
        int count=Integer.parseInt(_sum);
        return count % capacity == 0 ? count / capacity : count / capacity + 1;
    }

    public static int execProc(String name,String[] params){
        String sql=String.format("{call dbo.%s(%s)}",name,String.join(",",params));
        return update(sql);
    }

    public static Map<String,Object> getRecord(String sql){
        List<Map<String,Object>> list=query(sql);
        if(list.size()>0){
            return list.get(0);
        }else{
            return null;
        }
    }
    public static Object getColumn(String sql,String field){
        List<Map<String,Object>> list=query(sql);
        if(list.size()>0){
            return list.get(0).get(field);
        }else{
            return null;
        }
    }

    public static boolean test(String sql){
        List<Map<String,Object>> list=query(sql);
        return list.size()>0;
    }
}
