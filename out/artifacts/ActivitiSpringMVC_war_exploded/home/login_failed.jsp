<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page pageEncoding="UTF-8" %>
<%
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
%>
<html>
<head>

    <title>主界面</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <script language="javascript" src="../script/jquery.js"></script>
    <script language="javascript" src="../script/pageCommon.js" charset="utf-8"></script>
    <script language="javascript" src="../script/PageUtils.js" charset="utf-8"></script>
    <link type="text/css" rel="stylesheet" href="../style/blue/pageCommon.css" />
</head>
<body>

<div id="Title_bar">
    <div id="Title_bar_Head">
        <div id="Title_Head"></div>
        <div id="Title"><!--页面标题-->
            <img border="0" width="13" height="13" src="../style/images/title_arrow.gif"/> 审批流程管理
        </div>
        <div id="Title_End"></div>
    </div>
</div>

<div id="MainArea">
    <table cellspacing="0" cellpadding="0" class="TableStyle">

        <!-- 表头-->
        <thead>
        <tr align=center valign=middle id=TableTitle>
            <%--            <td width="200px"><a href="/ActivitiWeb_war/flow/approveInfoList.jsp">申请列表</a></td>--%>
            <%--            <td width="80px"><a href="/ActivitiWeb_war/flow/approveUI.jsp">申请UI</a></td>--%>
            <td width="300px"><a href="flowAction_myApplicationList.do">我的申请列表</a></td>
            <td width="200px"><a href="flowAction_myTaskList.do">我的任务列表</a></td>
            <td width="80px"><a href="../flow/submitUI.jsp">提交UI</a></td>
            <td width="300px"><a href="templateAction_list.do">模板列表</a></td>
        </tr>
        <tr align=center valign=middle id=TableTitle2>

        </tr>
        </thead>


    </table>


</div>



</body>
</html>
