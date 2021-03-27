<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page pageEncoding="UTF-8" %>
<html>
<head>
    <title>待我审批</title>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <script language="javascript" src="../script/jquery.js"></script>
    <script language="javascript" src="../script/pageCommon.js" charset="utf-8"></script>
    <script language="javascript" src="../script/PageUtils.js" charset="utf-8"></script>
    <link type="text/css" rel="stylesheet" href="../style/blue/pageCommon.css"/>
    <script type="text/javascript">
    </script>
</head>

<body>
<table cellspacing="0" cellpadding="0" class="TableStyle">
    <!-- 表头-->
    <thead>
    <tr align="CENTER" valign="MIDDLE" id="TableTitle">
        <td width="100">危险作业id</td>
        <td width="100">危险作业名称</td>
        <td width="100">危险作业申请人id</td>
        <td width="100">开始时间</td>
        <td width="100">结束时间</td>
        <td width="100">文件名称</td>
        <td width="100">状态</td>
        <td width="100">操作</td>
    </tr>
    </thead>
    <!--显示数据列表-->
    <tbody id="TableData" class="dataContainer" datakey="formList">
    <c:if test="${applicationInstanceList != null}">
        <c:forEach items="${applicationInstanceList}" var="item">
            <tr class="TableDetail1 template">
                <td><a href="approveUI.html">${item.application.dangerTaskId}</a></td>
                <td>${item.application.dangerTaskName}&nbsp;</td>
                <td>${item.application.applicant}&nbsp;</td>
                <td>${item.application.startTime}&nbsp;</td>
                <td>${item.application.endTime}&nbsp;</td>
                <td>${item.application.fileName}&nbsp;</td>
                <td>${item.application.state}&nbsp;</td>
                <td>
                    <a href="/ActivitiSpringMVC_war/DangerTaskApplyMan/GetSingelApproveInformation.do?InstanceId=${item.instanceId}">查看流转记录</a>
                </td>
            </tr>
        </c:forEach>
    </c:if>
    <h2>所有用户的申请作业</h2>
    <Button><a href="../home/main.jsp">返回主页面</a></Button>
    </tbody>
</table>
<%--<table>
    <tr>table</tr>
    <c:forEach items="${list}" var="item">
        <tr >
            <td><a href="approveUI.html">${item.fileName}</a></td>
        </tr>
    </c:forEach>
</table>--%>
</body>

</html>

