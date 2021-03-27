package com.lianggao.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.servlet.jsp.jstl.sql.Result;
import javax.servlet.jsp.jstl.sql.ResultSupport;
import javax.sql.DataSource;
import java.sql.*;
import java.util.List;
import java.util.Map;


public class DBOperator {

	private static DataSource dataSource;

	static {
		ApplicationContext ac = new FileSystemXmlApplicationContext("classpath:applicationContext.xml");
		dataSource = (DataSource) ac.getBean("dataSource");
	}

	private String sql; // 要传入的sql语句

	public void setSql(String sql) {
		this.sql = sql;
	}

	private List sqlValues; // sql语句的参数

	public void setSqlValues(List sqlValues) {
		this.sqlValues = sqlValues;
	}

	private Connection con; // 连接对象

	public void setCon(Connection con) {
		this.con = con;
	}

	public DBOperator(String sql) {
		System.out.println(sql);
		this.sql = sql;
		try {
			this.con = getConnection(); // 给Connection的对象赋初值
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public DBOperator() {
		//this.sql = sql;
		try {
			this.con = getConnection(); // 给Connection的对象赋初值
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private Connection getConnection() throws SQLException {

//		ApplicationContext ac = new FileSystemXmlApplicationContext("classpath:applicationContext.xml");
//		DataSource datasource = (DataSource) ac.getBean("dataSource");
//		Connection conn = datasource.getConnection();
//		return conn;
		return DataSourceUtils.getConnection(dataSource);

	}

	private void closeAll(Connection con, PreparedStatement pst, ResultSet rst) {
		if (rst != null) {
			try {
				rst.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (pst != null) {
			try {
				pst.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

//		if (con != null) {
//			try {
//				con.close();
//			} catch (SQLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}

	}
	public Map[] executeQuery( String sql) {
		//if(this.con)
		this.sql=sql;
		Result result = null;
		ResultSet rst = null;
		PreparedStatement pst = null;
		try {
			pst = con.prepareStatement(sql);
			if (sqlValues != null && sqlValues.size() > 0) { // 当sql语句中存在占位符时
				setSqlValues(pst, sqlValues);
			}
			rst = pst.executeQuery();
			result = ResultSupport.toResult(rst); // 一定要在关闭数据库之前完成转换

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(sql);
		} finally {
			this.closeAll(con, pst, rst);
		}
		if (result == null) {
			return null;
		} else {
			return result.getRows();
		}

	}


	public Map[] executeQuery() {
		Result result = null;
		ResultSet rst = null;
		PreparedStatement pst = null;
		try {

			pst = con.prepareStatement(sql);
			if (sqlValues != null && sqlValues.size() > 0) { // 当sql语句中存在占位符时
				setSqlValues(pst, sqlValues);
			}
			rst = pst.executeQuery();
			result = ResultSupport.toResult(rst); // 一定要在关闭数据库之前完成转换

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(sql);
		} finally {
			this.closeAll(con, pst, rst);
		}
		if (result == null) {
			return null;
		} else {
			return result.getRows();
		}

	}

	public String getString(String aim) {
		Map[] maps = executeQuery();
		if (maps == null || maps.length == 0) {
			return null;
		} else {
			if (maps[0].get(aim) != null) {
				return maps[0].get(aim).toString();
			} else {
				return "";
			}

		}


	}

	/**
	 * 增删改
	 *
	 * @return
	 */
	public int executeUpdate() {
		int result = -1;
		PreparedStatement pst = null;
		try {
			pst = con.prepareStatement(sql);
			if (sqlValues != null && sqlValues.size() > 0) { // 当sql语句中存在占位符时
				setSqlValues(pst, sqlValues);
			}
			result = pst.executeUpdate();
		} catch (SQLException e) {

			// TODO Auto-generated catch block
			System.out.println(sql);
			e.printStackTrace();
		} finally {
			this.closeAll(con, pst, null);
		}

		return result;
	}

	public int executeUpdate(String sql) {
		this.sql=sql;
		int result = -1;
		PreparedStatement pst = null;
		try {
			pst = con.prepareStatement(sql);
			if (sqlValues != null && sqlValues.size() > 0) { // 当sql语句中存在占位符时
				setSqlValues(pst, sqlValues);
			}
			result = pst.executeUpdate();
		} catch (SQLException e) {

			// TODO Auto-generated catch block
			System.out.println(sql);
			e.printStackTrace();
		} finally {
			this.closeAll(con, pst, null);
		}

		return result;
	}

	/**
	 * 给sql语句中的占位符赋值
	 *
	 * @param pst
	 * @param sqlValues
	 */
	private void setSqlValues(PreparedStatement pst, List sqlValues) {
		for (int i = 0; i < sqlValues.size(); i++) {
			try {
				pst.setObject(i + 1, sqlValues.get(i));
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public Map[] getListByPage(int pageindex) {
		if (pageindex != 0) {
			sql = "select * from (" + sql + ") A where rn>" + (pageindex - 1) * 10 + "and rn<=" + pageindex * 10;
		}
		//System.out.println("sql:"+sql);
		return executeQuery();
	}

	public int getListPageNum() {
		sql = "select count(*) as _sum from (" + sql + ") A;";
		String _sum = getString("_sum");
		if (_sum == null) {
			return 0;
		} else {
			int count = Integer.parseInt(_sum);
			//System.out.println("count:"+count);
			return count % 10 == 0 ? count / 10 : count / 10 + 1;
		}

	}

	//更改机构操作模块权限的存储过程调用
	public int updateModuleInstitution_T(String institution, String decrease, String change, String increase, String userNum, String userRight, String roleID, String roleIDRight) {
		int rowCount = 0;
		try {
			CallableStatement  pstmt = con.prepareCall("{call ModuleInstitution_updateProc(?,?,?,?,?,?,?,?,?)}");
			pstmt.setString(1, institution);
			pstmt.setString(2, decrease);
			pstmt.setString(3, change);
			pstmt.setString(4, increase);
			pstmt.setString(5, userNum);
			pstmt.setString(6, userRight);
			pstmt.setString(7, roleID);
			pstmt.setString(8, roleIDRight);
			pstmt.registerOutParameter(9, Types.INTEGER);
			pstmt.execute();
			rowCount = pstmt.getInt(9);
			pstmt.close();
			return rowCount;
		} catch (Exception ex) {
			//TODO: handle exception
			System.out.println("error");
			return rowCount;
		}
	}

	//更改机构操作模块权限的存储过程调用
	public int InsertModuleInstitution_T(String institution, String roleRight) {
		int val = 0;
		try {
			PreparedStatement pstmt = con.prepareStatement("{call ModuleInstitution_insertProc(?,?)}");
			pstmt.setString(1, institution);
			pstmt.setString(2, roleRight);
			val = pstmt.executeUpdate();
			pstmt.close();
			return val;
		} catch (Exception ex) {
			return val;

		}
	}

	/**
	 * 带参的存储过程  返回查询结果
	 */
	//查询信息
	public Map[] getQueryResult(String sql, int pageIndex, int pageSize, String orderBy) {
		ResultSet rs=null;
		Result result=null;
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement("{call commonPageQueryProc(?,?,?,?,?)}");
			pstmt.setString(1,"");
			pstmt.setString(2,sql);
			pstmt.setInt(3, pageSize * (pageIndex - 1) + 1);
			pstmt.setInt(4,pageSize * pageIndex);
			pstmt.setString(5,orderBy);
			rs = pstmt.executeQuery();
			result = ResultSupport.toResult(rs);
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			this.closeAll(con, pstmt, rs);
		}
		if (result == null) {
			return null;
		} else {
			return result.getRows();
		}
	}

	//查询签名信息
	public Map[] getSignQueryResult(String conditions, int pageIndex, int pageSize) {
		ResultSet rs=null;
		Result result=null;
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement("{call GetSignPageInfoProc(?,?,?)}");
			pstmt.setString(1,conditions);
			pstmt.setInt(2, pageSize * (pageIndex - 1) + 1);
			pstmt.setInt(3,pageSize * pageIndex);
			rs = pstmt.executeQuery();
			result = ResultSupport.toResult(rs);
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			this.closeAll(con, pstmt, rs);
		}
		if (result == null) {
			return null;
		} else {
			return result.getRows();
		}
	}

	//更新一个角色信息
	public int updateRole(String RoleID, String RoleName, String RoleRight,String RoleMemo,String RoleInstitution,String RolePosition,String userNum, String userRight,String change,  String ExceptModule) {
		int val = 0;
		try {
			PreparedStatement pstmt = con.prepareStatement("{call Role_updateProc(?,?,?,?,?,?,?,?,?,?)}");
			pstmt.setString(1, RoleID);
			pstmt.setString(2, RoleName);
			pstmt.setString(3, RoleRight);
			pstmt.setString(4, RoleMemo);
			pstmt.setString(5, RoleInstitution);
			pstmt.setString(6, RolePosition);
			pstmt.setString(7, userNum);
			pstmt.setString(8, userRight);
			pstmt.setString(9, change);
			pstmt.setString(10, ExceptModule);
			val = pstmt.executeUpdate();
			pstmt.close();
			return val;
		} catch (Exception ex) {
			//TODO: handle exception
			System.out.println("error");
			return val;
		}
	}

	//删除一条用户信息，同时删除对应的角色记录，存储过程
	public Boolean DeleteUser(String proc, String parm1,String parm2)
	{
		int val = 0;
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement("{call "+proc+"(?,?)}");
			pstmt.setString(1,parm1);
			pstmt.setString(2,parm2);
			val = pstmt.executeUpdate();
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return val>0;
	}

	//更新用户信息
	public int updateUser(String EmployeeNum, String Institution, String UserRight,String ChangeCando) {
		int val = 0;
		try {
			PreparedStatement pstmt = con.prepareStatement("{call UpdateUserRightProc(?,?,?,?)}");
			pstmt.setString(1, EmployeeNum);
			pstmt.setString(2, Institution);
			pstmt.setString(3, UserRight);
			pstmt.setString(4, ChangeCando);
			val = pstmt.executeUpdate();
			pstmt.close();
			return val;
		} catch (Exception ex) {
			//TODO: handle exception
			System.out.println("error");
			return val;
		}
	}

	// 查询某个大类下面所有的小类，如查询WorkStyleNum下面的子项。
	public Map[] getOneItemByName(String name) {
		ResultSet rs=null;
		Result result=null;
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement("{call datadictionProc(?)}");
			pstmt.setString(1,name);
			rs = pstmt.executeQuery();
			result = ResultSupport.toResult(rs);
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			this.closeAll(con, pstmt, rs);
		}
		if (result == null) {
			return null;
		} else {
			return result.getRows();
		}
	}

	//通过存储过程获得数据,存储过程不带参数
	public Map[] getTableByProc(String proc) {
		ResultSet rs=null;
		Result result=null;
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement("{call "+proc+"()}");
			rs = pstmt.executeQuery();
			result = ResultSupport.toResult(rs);
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			this.closeAll(con, pstmt, rs);
		}
		if (result == null) {
			return null;
		} else {
			return result.getRows();
		}
	}

	//直接从存储过程获取结果集，只有一个参数的情况，即通用查询
	public Map[] getTableByOneProc(String proc, String parm) {
		ResultSet rs=null;
		Result result=null;
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement("{call "+proc+"(?)}");
			pstmt.setString(1,parm);
			rs = pstmt.executeQuery();
			result = ResultSupport.toResult(rs);
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			this.closeAll(con, pstmt, rs);
		}
		if (result == null) {
			return null;
		} else {
			return result.getRows();
		}
	}

	//获取可以指定的上一级审批单位
	public Map[] getNextApprovalProc(String proc, String parm1,String parm2) {
		ResultSet rs=null;
		Result result=null;
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement("{call "+proc+"(?,?)}");
			pstmt.setString(1,parm1);
			pstmt.setString(2,parm2);
			rs = pstmt.executeQuery();
			result = ResultSupport.toResult(rs);
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			this.closeAll(con, pstmt, rs);
		}
		if (result == null) {
			return null;
		} else {
			return result.getRows();
		}
	}

	//获得所有记录的方式，传入存储过程名和页码
	/// <summary>
	/// 获得所有记录的方式，传入存储过程名和页码
	/// </summary>
	/// <param name="proc">存储过程名</param>
	/// <param name="pageIndex">页码</param>
	/// <returns>返回结果集</returns>
	public Map[] getAllInfo(String proc,int pageIndex,int pageSize) {
		ResultSet rs = null;
		Result result = null;
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement("{call " + proc + "(?,?)}");
			pstmt.setInt(1, pageIndex);
			pstmt.setInt(2,pageSize);
			rs = pstmt.executeQuery();
			result = ResultSupport.toResult(rs);
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.closeAll(con, pstmt, rs);
		}
		if (result == null) {
			return null;
		} else {
			return result.getRows();
		}
	}

	public Map[] getAllEmployeeInfo(String proc) {
		ResultSet rs = null;
		Result result = null;
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement("{call " + proc + "}");

			rs = pstmt.executeQuery();
			result = ResultSupport.toResult(rs);
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			this.closeAll(con, pstmt, rs);
		}
		if (result == null) {
			return null;
		} else {
			return result.getRows();
		}
	}

	//执行添加员工信息的插入语句
	public int insertEmployee(String EmployeeID, String EmployeeName, String EmployeeSex, Date EmployeeBirth,String PhotoName,String IDNum,String TelephoneNum,
							  String InstitutionNum,int currentWork,int workStyle,int workPosition,Date inStation,Date outStation,int workType,
							  String home,Date joinComp,Date outComp,String onSta,String insuranceNum,Date insuredTime,String threeEdu) {
		int val = 0;
		try {
			PreparedStatement pstmt = con.prepareStatement("{call employee_insertProc(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
			pstmt.setString(1,EmployeeID);
			pstmt.setString(2,EmployeeName);
			pstmt.setString(3,EmployeeSex);
			pstmt.setDate(4,EmployeeBirth);
			pstmt.setString(5, PhotoName);
			pstmt.setString(6,IDNum);
			pstmt.setString(7,TelephoneNum);
			pstmt.setString(8,InstitutionNum);
			pstmt.setInt(9, currentWork);
			pstmt.setInt(10,workStyle);
			pstmt.setInt(11,workPosition);
			pstmt.setDate(12,inStation);
			pstmt.setDate(13,outStation);
			pstmt.setInt(14,workType);
			pstmt.setString(15,home);
			pstmt.setDate(16,joinComp);
			pstmt.setDate(17,outComp);
			pstmt.setString(18,onSta);
			pstmt.setString(19, insuranceNum);
			pstmt.setDate(20,insuredTime);
			pstmt.setString(21,threeEdu);
			val = pstmt.executeUpdate();
			pstmt.close();
			return val;
		} catch (Exception ex) {
			//TODO: handle exception
			System.out.println("error");
			return val;
		}
	}

	//执行修改员工信息
	public int updateEmployee(String EmployeeNum, String EmployeeName, String EmployeeSex, Date EmployeeBirth,String PhotoName,String IDNum,String TelephoneNum,
							  String InstitutionNum,int currentWork,int workStyle,int workPosition,Date inStation,Date outStation,int workType,
							  String home,Date joinComp,Date outComp,String onSta,String outCompanyOrNot,String FG,String DG,String insuranceNum,Date insuredTime,String EmployeeID,String IsRetire,Date RetireTime) {
		int val = 0;
		try {
			PreparedStatement pstmt = con.prepareStatement("{call employee_updateProc(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
			pstmt.setString(1,EmployeeNum);
			pstmt.setString(2,EmployeeName);
			pstmt.setString(3,EmployeeSex);
			pstmt.setDate(4,EmployeeBirth);
			pstmt.setString(5, PhotoName);
			pstmt.setString(6,IDNum);
			pstmt.setString(7,TelephoneNum);
			pstmt.setString(8,InstitutionNum);
			pstmt.setInt(9, currentWork);
			pstmt.setInt(10,workStyle);
			pstmt.setInt(11,workPosition);
			pstmt.setDate(12,inStation);
			pstmt.setDate(13,outStation);
			pstmt.setInt(14,workType);
			pstmt.setString(15,home);
			pstmt.setDate(16,joinComp);
			pstmt.setDate(17,outComp);
			pstmt.setString(18,onSta);
			pstmt.setString(19,outCompanyOrNot);
			pstmt.setString(20,FG);    //判断是否添加复工安全教育
			pstmt.setString(21,DG);    //判断是否添加调岗安全教育

			pstmt.setString(22, insuranceNum);
			pstmt.setDate(23,insuredTime);
			pstmt.setString(24,EmployeeID);
			pstmt.setString(25, IsRetire);
			pstmt.setDate(26,RetireTime);
			val = pstmt.executeUpdate();
			pstmt.close();
			return val;
		} catch (Exception ex) {
			//TODO: handle exception
			System.out.println("error");
			return val;
		}
	}

	 //获取危险作业申请
	public Map[] getApplyList(int pageIndex, String archive,String UserInstitution) { //没有归档的作业申请
		ResultSet rs=null;
		Result result=null;
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement("{call dangerTask_applyProc(?,?,?,?)}");
			pstmt.setInt(1,pageIndex);
			pstmt.setInt(2,10);
			pstmt.setString(3,archive);
			pstmt.setString(4,UserInstitution);
			rs = pstmt.executeQuery();
			result = ResultSupport.toResult(rs);
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			this.closeAll(con, pstmt, rs);
		}
		if (result == null) {
			return null;
		} else {
			return result.getRows();
		}
	}

	//获取所有的危险作业申请
	public Map[] getAllApplyList(String archive,String UserInstitution) { //没有归档的作业申请
		ResultSet rs=null;
		Result result=null;
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement("{call dangerTask_applyAllListProc(?,?)}");
			pstmt.setString(1,archive);
			pstmt.setString(2,UserInstitution);
			rs = pstmt.executeQuery();
			result = ResultSupport.toResult(rs);
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			this.closeAll(con, pstmt, rs);
		}
		if (result == null) {
			return null;
		} else {
			return result.getRows();
		}
	}


	//执行添加危险作业信息的插入语句
	public int insertDangerTask(String ApplicantNum, String TaskInstitution, Date ApplyingTime,String DangerTaskName,Date StartTime,Date EndTime,
							  String Archived,String firstInstitution,Date UploadTime,String uploadfilename,int taskID) {
		int val = 0;
		try {
			PreparedStatement pstmt = con.prepareStatement("{call dangerTask_addProc(?,?,?,?,?,?,?,?,?,?,?)}");
			pstmt.setString(1,ApplicantNum);
			pstmt.setString(2,TaskInstitution);
			pstmt.setDate(3,ApplyingTime);
			pstmt.setString(4, DangerTaskName);
			pstmt.setDate(5,StartTime);
			pstmt.setDate(6,EndTime);
			pstmt.setString(7,Archived);
			pstmt.setString(8, firstInstitution);
			pstmt.setDate(9,UploadTime);
			pstmt.setString(10,uploadfilename);
			pstmt.setInt(11,taskID);
			val = pstmt.executeUpdate();
			pstmt.close();
			return val;
		} catch (Exception ex) {
			//TODO: handle exception
			System.out.println("error");
			return val;
		}
	}

	//获取检查标准申请
	public Map[] getApplyList_CheckStandard(int pageIndex, String archive,String UserInstitution) { //没有归档的作业申请
		ResultSet rs=null;
		Result result=null;
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement("{call checkStandard_applyProc(?,?,?,?)}");
			pstmt.setInt(1,pageIndex);
			pstmt.setInt(2,10);
			pstmt.setString(3,archive);
			pstmt.setString(4,UserInstitution);
			rs = pstmt.executeQuery();
			result = ResultSupport.toResult(rs);
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			this.closeAll(con, pstmt, rs);
		}
		if (result == null) {
			return null;
		} else {
			return result.getRows();
		}
	}
	//获取所有的检查标准申请
	public Map[] getAllApplyList_CheckStandard(String archive,String UserInstitution) { //没有归档的作业申请
		ResultSet rs=null;
		Result result=null;
		PreparedStatement pstmt = null;
		try {
			pstmt = con.prepareStatement("{call checkStandard_applyAllListProc(?,?)}");
			pstmt.setString(1,archive);
			pstmt.setString(2,UserInstitution);
			rs = pstmt.executeQuery();
			result = ResultSupport.toResult(rs);
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			this.closeAll(con, pstmt, rs);
		}
		if (result == null) {
			return null;
		} else {
			return result.getRows();
		}
	}



    public int insertCheckApplicationTask(String ApplicantNum, String TaskInstitution, Date ApplyingTime,
                                          String checkTableNum, String checkTableName,String Category,
                                String Archived,String firstInstitution,Date UploadTime,String uploadfilename,int taskID) {
        int val = 0;
        try {
            PreparedStatement pstmt = con.prepareStatement("{call CheckApplication_addProc(?,?,?,?,?,?,?,?,?,?,?)}");
            pstmt.setString(1,ApplicantNum);
            pstmt.setString(2,TaskInstitution);
            pstmt.setDate(3,ApplyingTime);
            pstmt.setString(4, checkTableNum);
            pstmt.setString(5,checkTableName);
            pstmt.setString(6,Category);
            pstmt.setString(7,Archived);
            pstmt.setString(8, firstInstitution);
            pstmt.setDate(9,UploadTime);
            pstmt.setString(10,uploadfilename);
            pstmt.setInt(11,taskID);
            val = pstmt.executeUpdate();
            pstmt.close();
            return val;
        } catch (Exception ex) {
            //TODO: handle exception
            System.out.println("error");
            return val;
        }
    }

}
