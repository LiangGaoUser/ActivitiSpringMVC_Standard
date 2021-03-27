<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head><meta content="webkit" name="renderer" />
    <script type="text/javascript" src="static/js/jquery-3.3.1.min.js"></script>
    <script type="text/javascript" src="static/js/vue.min.js"></script>
    <script type="text/javascript" src="static/element/element-ui.js"></script>
    <script type="text/javascript" src="static/main/js/main.js"></script>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>乌龙泉矿双重预防机制预警系统</title>
    <!-- Tell the browser to be responsive to screen width -->
    <meta content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" name="viewport">
    <!-- Bootstrap 3.3.7 -->
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/static/bootstrap/css/bootstrap.min.css"/>
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/static/element/element-ui.css"/>
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/static/css/module.css"/>
    <!-- Font Awesome -->
    <link rel="stylesheet" href="<%=request.getContextPath()%>/static/main/css/font-awesome.min.css">
    <!-- Ionicons -->
    <link rel="stylesheet" href="<%=request.getContextPath()%>/static/main/css/ionicons.min.css">
    <!-- Theme style -->
    <link rel="stylesheet" href="<%=request.getContextPath()%>/static/main/css/AdminLTE.min.css">
    <!-- AdminLTE Skins. Choose a skin from the css/skins
         folder instead of downloading all of them to reduce the load. -->
    <link rel="stylesheet" href="<%=request.getContextPath()%>/static/main/css/_all-skins.min.css">
    <style>
        html {
            overflow: hidden;
        }

        body {
            overflow: hidden;
            font-size: 16px;
        }

        .wrapper {
            overflow: hidden;
        }

        .sidebar {
            overflow-y: auto;
        }

        .sidebar::-webkit-scrollbar {
            width: 0;
        }

        .treeview-menu > li {
            height: 48px;
        }

        .treeview > a {
            height: 48px;
            font-size: 18px;
        }

        .treeview-menu > li.active {
            background-color: #36c2fd;
        }

        .treeview-menu > li:hover {
            background: #36c2fd;
            transition: 0.2s;
        }

        .navbar-button {
            font-size: 16px;
        }

        .menu-logo {
            width: 22px;
            height: 22px;
        }

        .left-menu {
            margin-left: 8px;
        }

        .left-menu-2 {
            margin-left: 50px;
        }

        input#search-input::-webkit-input-placeholder {
            color: #1781ed;
        }

        .navbar.navbar-static-top > .navbar-custom-menu {
            margin-left: auto;
        }

        .navbar.navbar-static-top {
            display: flex;
            align-items: center;
            height: 75px;
        }

        .fa-circle-o {
            margin-right: 1em;
        }

        .wrapper {
            margin-top: 0px;
        }

        .top-button {
            margin-right: 1em;
            font-size: 1.1em;
            padding: 0px;
        }

        .el-dialog__body .el-textarea__inner {
            width: 15em;
        }
    </style>
    <%
        String right = "040100 040200";//session.getAttribute("UserRight").toString();
    %>
    <script type="text/javascript">
        // 点击“检查表数据录入”时收缩
        $(function(){
            $("#leftno").click(function(){
                $(".addlei").addClass("sidebar-collapse");
                $(".sidebar-mini.sidebar-collapse .main-header .logo > .logo-mini").css("display","block");
                $(".sidebar-mini.sidebar-collapse .main-header .logo > .logo-mini").css("margin-left","-15px");
                $(".sidebar-mini.sidebar-collapse .main-header .logo > .logo-mini").css("margin-right","-15px");
                $(".sidebar-mini.sidebar-collapse .main-header .logo > .logo-mini").css("font-size","18px");
                $(".sidebar-mini.sidebar-collapse .main-header .logo > .logo-lg").css("display","none");

                $(".sidebar-mini.sidebar-collapse .main-sidebar .user-panel > .info").css("display","none");
                $(".sidebar-mini.sidebar-collapse .sidebar-form").css("display","none");
                $(".sidebar-mini.sidebar-collapse .sidebar-menu > li > a > span").css("display","none");
                $(".sidebar-mini.sidebar-collapse .sidebar-menu > li > .treeview-menu").css("display","none");
                $(".sidebar-mini.sidebar-collapse .sidebar-menu > li > a > .pull-right").css("display","none");
                $(".sidebar-mini.sidebar-collapse .sidebar-menu li.header").css("display","none");
            })
            $(".navbar a").click(function(){
                if($(".logo-mini").css("display")=="block"){
                    $(".sidebar-mini.sidebar-collapse .main-header .logo > .logo-mini").css("display","none");
                    $(".sidebar-mini.sidebar-collapse .main-header .logo > .logo-lg").css("display","block");
                    $(".sidebar-mini.sidebar-collapse .main-sidebar .user-panel > .info").css("display","");
                    $(".sidebar-mini.sidebar-collapse .sidebar-form").css("display","");
                    $(".sidebar-mini.sidebar-collapse .sidebar-menu > li > a > span").css("display","");
                    $(".sidebar-mini.sidebar-collapse .sidebar-menu > li > .treeview-menu").css("display","");
                    $(".sidebar-mini.sidebar-collapse .sidebar-menu > li > a > .pull-right").css("display","");
                    $(".sidebar-mini.sidebar-collapse .sidebar-menu li.header").css("display","");

                    $(".sidebar-mini.sidebar-collapse .sidebar-menu .menu-open .treeview-menu").css("display","block");
                } else if($(".logo-mini").css("display")=="none"){
                    $(".logo-lg").css("display","none");
                    $(".logo-mini").css("display","block");
                }
            })
        })
    </script>
</head>
<body class="hold-transition skin-blue sidebar-mini addlei">
<div class="wrapper" v-loading="pageLoading">

    <header class="main-header">
        <!-- Logo -->

        <a href="main2.jsp" class="logo">
            <span class="logo-mini"><img src="static/image/logo-mini.png"></span>
            <span class="logo-lg"><img src="static/image/logo.png"></span>

        </a>
        <!-- Header Navbar: style can be found in header.less -->
        <nav class="navbar navbar-static-top">
            <%--<div class="navbar-header">--%>
            <!-- Sidebar toggle button-->
            <a href="#" data-toggle="push-menu" role="button">
                <img src="static/image/menu-indentation.png" style="margin-left: 1em;">
                <span class="sr-only">Toggle navigation</span>
            </a>

            <div class="navbar-custom-menu">
                <ul class="nav navbar-nav">
                    <li class="dropdown user user-menu">
                        <%--<el-button @click="getList">111</el-button>--%>
                        <el-button type="text" @click="getList();correctStyle();" data-toggle="dropdown" class="top-button">
                            <img src="static/image/userPicture.png" class="user-image" alt="User Image">
                            <span class="hidden-xs">${EmployeeName}</span>
                        </el-button>
                    </li>
                    <li class="navbar-button">
                        <el-button type="text" @click="_help" data-toggle="dropdown" class="top-button">帮助
                        </el-button>
                        <%--<a>帮助</a>--%>
                    </li>
                    <li class="navbar-button">
                        <el-button type="text" @click="logout" data-toggle="dropdown" class="top-button">退出
                        </el-button>
                        <%--<a href="javascript:top.location='login.jsp'">登出</a>--%>
                    </li>
                </ul>
            </div>
            <%--</div>--%>
        </nav>
    </header>
    <!-- Left side column. contains the logo and sidebar -->
    <aside class="main-sidebar">
        <!-- sidebar: style can be found in sidebar.less -->
        <section class="sidebar">
            <ul class="sidebar-menu" data-widget="tree" style="margin-top: 2em">
                <li class="treeview">
                    <%if (right.contains("010100") || right.contains("010200") || right.contains("010300") || right.contains("010400")) {%>
                    <a href="#">
                        <i class="fa"><img class="menu-logo" src="static/image/menu-employeeMan.png"></i>
                        <span class="left-menu">员工安全信息管理</span>
                        <span class="pull-right-container">
                            <i class="fa fa-angle-left pull-right"></i>
                      </span>
                    </a>
                    <ul class="treeview-menu">
                        <%if (right.contains("010100")) {%>
                        <li>
                            <a target="right-frame" href="EmployeeManagement/EmployeeMan/EmployeeList_vue"><span
                                    class="left-menu-2">公司员工基本信息</span></a>
                        </li>
                        <%}%>
                        <%if (right.contains("010200")) {%>
                        <li><a target="right-frame"
                               href="EmployeeManagement/SpecialEquipMan/List_vue"><span class="left-menu-2">特种设备作业人员台账</span></a></li>
                        <%}%>

                        <%if (right.contains("010400")) {%>
                        <li><a target="right-frame"
                               href="EmployeeManagement/SpecialMan/List_vue"><span class="left-menu-2">特种作业人员台账</span></a></li>
                        <%}%>


                        <%if (right.contains("010300")) {%>
                        <li><a target="right-frame"
                               href="EmployeeManagement/SecurityMan/List_vue"><span class="left-menu-2">安全管理人员台账</span></a></li>
                        <%}%>

                    </ul>
                    <%}%>
                </li>
                <li class="treeview">
                    <%if (right.contains("020100") || right.contains("020200") || right.contains("020300") || right.contains("020400") || right.contains("020500")) {%>
                    <a href="#">
                        <i class="fa"><img class="menu-logo" src="static/image/menu-safetyFileMan.png"></i>
                        <span class="left-menu">安全文件信息管理</span>
                        <span class="pull-right-container">
                            <i class="fa fa-angle-left pull-right"></i>
                      </span>
                    </a>
                    <ul class="treeview-menu">

                        <%-- <%if (right.contains("020100")) {%><li><a target="right-frame"
                                                               href="FileManagement/LawAndRule/List"><span
                             class="left-menu-2">法律法规</span></a></li><%}%>--%>


                        <%if (right.contains("020200")) {%> <li><a target="right-frame"
                                                                   href="FileManagement/LawsRegulations/LawsRegulations"><span
                            class="left-menu-2">法律法规</span></a></li><%}%>


                        <%if (right.contains("020300")) {%><li><a target="right-frame"
                                                                  href="FileManagement/TechnicalStandard/List_vue"><span
                            class="left-menu-2">技术标准规范</span></a></li><%}%>


                        <%if (right.contains("020400")) {%><li><a target="right-frame" href="FileManagement/GroupDocument/List_vue"><span
                            class="left-menu-2">集团文件</span></a></li><%}%>


                        <%if (right.contains("020500")) {%><li><a target="right-frame" href="FileManagement/SystemFile/SFList_vue"><span
                            class="left-menu-2">体系文件</span></a></li><%}%>


                        <%--   <%if (right.contains("020600")) {%><li><a target="right-frame" href="404.jsp"><span
                               class="left-menu-2">过程文件</span></a></li><%}%>--%>


                    </ul>
                    <%}%>
                </li>
                <li class="treeview">
                    <%if (right.contains("030100") || right.contains("030200") || right.contains("030300") || right.contains("030400") || right.contains("030500") || right.contains("030600")) {%>
                    <a href="#">
                        <i class="fa"><img class="menu-logo" src="static/image/menu-risk.png"></i>
                        <span class="left-menu">安全风险分级管控</span>
                        <span class="pull-right-container">
                            <i class="fa fa-angle-left pull-right"></i>
                      </span>
                    </a>
                    <ul class="treeview-menu">

                        <%if (right.contains("030100")) {%><li><a target="right-frame"
                                                                  href="risk-control/section-partition/sectionPartition"><span
                            class="left-menu-2">风险评价单元划分</span></a></li><%}%>

                        <%--<%if (right.contains("030700")) {%> <li><a target="right-frame"
                                                                   href="riskcontrol/riskmanagement/section_description.jsp"><span
                            class="left-menu-2">岗位风险描述</span></a></li><%}%>--%>

                        <%if (right.contains("030200")) {%><li><a target="right-frame"
                                                                  href="risk-control/section-identification/sectionIdentification"><span
                            class="left-menu-2">危险有害因素辨识</span></a></li><%}%>


                        <%if (right.contains("030300")) {%><li><a target="right-frame"
                                                                  href="risk-control/section-evaluation/sectionEvaluation"><span
                            class="left-menu-2">安全风险分级管控</span></a></li><%}%>


                        <%if (right.contains("030400")) {%><li><a target="right-frame"
                                                                  href="SecurityRiskGradingControl/RiskManageInfoManage/List_vue"><span
                            class="left-menu-2">安全风险告知</span></a></li><%}%>


                        <%-- <%if (right.contains("030500")) {%><li><a target="right-frame"
                                                               href="SecurityRiskGradingControl/SignificantRiskInfo/List_vue"><span
                             class="left-menu-2">重大风险告知</span></a></li><%}%>
--%>

                        <%if (right.contains("030600")) {%><li><a target="right-frame"
                                                                  href="riskcontrol/RiskAnalysis/RegionalRiskAssessment"><span
                            class="left-menu-2">区域风险评估与统计</span></a></li><%}%>

                    </ul>
                    <%}%>
                </li>
                <li class="treeview">
                    <%if (right.contains("040100") || right.contains("040200")) {%>
                    <a href="#">
                        <i class="fa"><img class="menu-logo" src="static/image/menu-dangerTask.png"></i>
                        <span class="left-menu">关联危险作业管理</span>
                        <span class="pull-right-container">
                            <i class="fa fa-angle-left pull-right"></i>
                      </span>
                    </a>
                    <ul class="treeview-menu">

                        <%if (right.contains("040100")) {%><li><a target="right-frame"
                                                                  href="AssociateManagement/DangerTaskApplyMan/DangerTaskList_vue.do"><span
                            class="left-menu-2">关联危险作业申请</span></a></li><%}%>


                        <%if (right.contains("040200")) {%><li><a target="right-frame"
                                                                  href="AssociateManagement/TaskApproveMan/Approval_Vue.do"><span
                            class="left-menu-2">关联危险作业审批</span></a></li><%}%>

                    </ul>
                    <%}%>
                </li>
                <li class="treeview">
                    <%if (right.contains("050100") || right.contains("050200") || right.contains("050300") || right.contains("050400") || right.contains("050500") || right.contains("050600")) {%>
                    <a href="#">
                        <i class="fa"><img class="menu-logo" src="static/image/menu-safeyCheck.png"></i>
                        <span class="left-menu">安全检查与隐患管理</span>
                        <span class="pull-right-container">
                            <i class="fa fa-angle-left pull-right"></i>
                      </span>
                    </a>
                    <ul class="treeview-menu">
                        <%-- <%if (right.contains("050100")) {%><li><a target="right-frame"
                                                                   href="SafetyManagement/lawAndRule/lawAndRule8"><span
                                 class="left-menu-2">安全检查标准管理</span></a></li><%}%>--%>

                        <%if (right.contains("050700")) {%><li><a target="right-frame"
                                                                  href="CheckStandardManagement/CheckStandardApplyMan/CheckStandardList_vue"><span
                            class="left-menu-2">安全检查标准申请</span></a></li><%}%>
                        <%if (right.contains("050800")) {%><li><a target="right-frame"
                                                                  href="CheckStandardManagement/TaskApproveMan/Approval_Vue"><span
                            class="left-menu-2">安全检查标准审批</span></a></li><%}%>

                        <%if (right.contains("050100")) {%><li><a target="right-frame"
                                                                  href="SafetyManagement/SafetyStandardManagement/vue_CheckTableManagement"><span
                            class="left-menu-2">安全检查标准管理</span></a></li><%}%>
                        <%-- <%if (right.contains("050200")) {%><li id="leftno"><a target="right-frame"
                                                               href="loadZTreeNodes?num=1"><span class="left-menu-2">检查表数据录入</span></a></li><%}%>
                     --%>    <%if (right.contains("050200")) {%><li id="leftno"><a target="right-frame" href="SafetyManagement/SafetyEntryManagement/SafetyEntryTree.jsp"><span class="left-menu-2">检查表数据录入</span></a></li><%}%>
                        <%--<%if (right.contains("050300")) {%><li><a target="right-frame"
                                                                  href="loadZTreeNodes2"><span class="left-menu-2">安全检查结果管理</span></a></li><%}%>--%>
                        <%if (right.contains("050300")) {%><li><a target="right-frame"
                                                                  href="SafetyManagement/SafetyResultManagement/SafetyResult.jsp"><span class="left-menu-2">安全检查结果管理</span></a></li><%}%>
                        <%if (right.contains("050400")) {%><li><a target="right-frame"
                                                                  href="SafetyManagement/statistics/hidden_category_statistics.jsp"><span
                            class="left-menu-2">安全检查隐患分析</span></a></li><%}%>


                        <%-- <%if (right.contains("050500")) {%><li><a target="right-frame"
                                                               href="SafetyManagement/RectifyManage/rectifyBefore"><span
                             class="left-menu-2">安全整改信息处理</span></a></li><%}%>--%>

                        <%if (right.contains("050500")) {%><li><a target="right-frame"
                                                                  href="SafetyManagement/RectificationManagement/vue_UnprocessedRectification"><span
                            class="left-menu-2">安全整改信息处理</span></a></li><%}%>

                        <%if (right.contains("050600")) {%><li><a target="right-frame"
                                                                  href="SafetyManagement/statistics/RectificationCommandOfStatistics.jsp"><span
                            class="left-menu-2">安全整改信息统计</span></a></li><%}%>

                        <%--<li><a target="right-frame" href="SafetyManagement/lawAndRule/lawAndRule7.jsp"><span class="left-menu-2">隐患违章管理</span></a>
                        </li>--%>


                    </ul>
                    <%}%>
                </li>
                <li class="treeview">
                    <%if (right.contains("060100") || right.contains("060200") || right.contains("060300")) {%>
                    <a href="#">
                        <i class="fa"><img class="menu-logo" src="static/image/menu-Emergency.png"></i>
                        <span class="left-menu">应急救援信息管理</span>
                        <span class="pull-right-container">
                            <i class="fa fa-angle-left pull-right"></i>
                      </span>
                    </a>
                    <ul class="treeview-menu">

                        <%if (right.contains("060100")) {%><li><a target="right-frame"
                                                                  href="EmergencyRescueManagement/SystemManagement/List_vue"><span class="left-menu-2">应急体系管理</span></a></li>
                        <%}%>

                        <%if (right.contains("060200")) {%><li><a target="right-frame"
                                                                  href="EmergencyRescueManagement/MaterialManagement/RMSWList_vue"><span class="left-menu-2">应急物资管理</span></a></li>
                        <%}%>

                        <%if (right.contains("060300")) {%><li><a target="right-frame"
                                                                  href="EmergencyRescueManagement/TrainManagement/List_vue"><span class="left-menu-2">应急演练管理</span></a></li>
                        <%}%>

                    </ul>
                    <%}%>
                </li>
                <li class="treeview">
                    <%if (right.contains("070100") || right.contains("070200") || right.contains("070300") || right.contains("070400") || right.contains("070500")) {%>
                    <a href="#">
                        <i class="fa"><img class="menu-logo" src="static/image/menu-safetyAssessment.png"></i>
                        <span class="left-menu">安全履职评价管理</span>
                        <span class="pull-right-container">
                            <i class="fa fa-angle-left pull-right"></i>
                      </span>
                    </a>
                    <ul class="treeview-menu">

                        <%-- <%if (right.contains("070100")) {%><li><a target="right-frame"
                                                               href="404.jsp"><span class="left-menu-2">考核标准管理</span></a></li>
                         <%}%>--%>


                        <%if (right.contains("070200")) {%><li><a target="right-frame"
                                                                  href="PerformanceAssessment/CompanyAssessment/companyAssessment.jsp"><span class="left-menu-2">公司级考核</span></a></li>
                        <%}%>


                        <%if (right.contains("070300")) {%><li><a target="right-frame"
                                                                  href="PerformanceAssessment/WorkshopAssessment/workshopAssessment.jsp"><span class="left-menu-2">车间级考核</span></a></li>
                        <%}%>


                        <%if (right.contains("070400")) {%><li><a target="right-frame"
                                                                  href="PerformanceAssessment/ClassAssessment/classAssessment.jsp">
                        <span class="left-menu-2">班组级考核</span></a></li>
                        <%}%>


                        <%-- <%if (right.contains("070500")) {%><li><a target="right-frame"
                                                               href="404.jsp"><span class="left-menu-2">统计分析</span></a></li>
                         <%}%>--%>

                    </ul>
                    <%}%>
                </li>
                <li class="treeview">
                    <%if (right.contains("080100") || right.contains("080200") || right.contains("080300") || right.contains("080400") || right.contains("080500") || right.contains("080600") || right.contains("080700")|| right.contains("080800")|| right.contains("080900")) {%>
                    <a href="#">
                        <i class="fa"><img class="menu-logo" src="static/image/menu-systemMan.png"></i>
                        <span class="left-menu">系统维护与管理</span>
                        <span class="pull-right-container">
                            <i class="fa fa-angle-left pull-right"></i>
                      </span>
                    </a>
                    <ul class="treeview-menu">

                        <%if (right.contains("080100")) {%>
                        <li><a target="right-frame"
                               href="SystemMaintenance/institutionManager/InstitutionList_vue"><span
                                class="left-menu-2">机构管理</span></a></li>
                        <%}%>


                        <%if (right.contains("080200")) {%>
                        <li><a target="right-frame"
                               href="SystemMaintenance/RoleManager/RoleList_vue"><span
                                class="left-menu-2">角色管理</span></a></li>
                        <%}%>


                        <%if (right.contains("080300")) {%>
                        <li><a target="right-frame"
                               href="SystemMaintenance/UserManage/UserList_vue"><span
                                class="left-menu-2">用户管理</span></a></li>
                        <%}%>


                        <%if (right.contains("080400")) {%>
                        <li><a target="right-frame"
                               href="SystemMaintenance/DataDictionary/DirectoryCategory"><span
                                class="left-menu-2">数据字典</span></a></li>
                        <%}%>


                        <%if (right.contains("080500")) {%>
                        <li><a target="right-frame"
                               href="riskcontrol/riskmanagement/risk_level_main"><span
                                class="left-menu-2">风险管理</span></a></li>
                        <%}%>

                        <%if (right.contains("080900")) {%>
                        <li><a target="right-frame"
                               href="PerformanceAssessment/StandardManagement/List_Vue"><span class="left-menu-2">考核等级管理</span></a></li>
                        <%}%>


                        <%if (right.contains("080600")) {%>
                        <li><a target="right-frame"
                               href="SystemMaintenance/SignManage/SignList_vue"><span
                                class="left-menu-2">签名管理</span></a></li>
                        <%}%>


                        <%if (right.contains("080700")) {%>
                        <li><a target="right-frame"
                               href="SystemMaintenance/ParameterManager/List"><span class="left-menu-2">参数管理</span></a></li>
                        <%}%>


                        <%if (right.contains("080800")) {%>
                        <li><a target="right-frame"
                               href="SystemMaintenance/DataBaseBackup/page"><span class="left-menu-2">数据库维护</span></a></li>
                        <%}%>

                    </ul>
                    <%}%>
                </li>
            </ul>
        </section>
        <!-- /.sidebar -->
    </aside>

    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper" style="height:100%">
        <h1>你好</h1>
        <iframe name="right-frame" id="right-frame" src="404.jsp"
                style="width:100%;height:100%;box-sizing: border-box;overflow: scroll;border-width: 1px;border-top-color: #e4e2e3;"></iframe>
    </div>
    <div class="control-sidebar-bg"></div>

    <div class="pop-dialog">
        <%--修改密码——信息加载（含签名照）--%>
        <div class="ChangePasswordAndSign-dialog">
            <el-dialog title="用户信息" :modal="true" :visible.sync="DialogVisible1" width="45em"
                       :close-on-click-modal="false" :close-on-press-escape="false" :show-close="false">
                <el-form :inline="true" :model="ruleForm" ref="ruleForm" :rules="rules" v-loading="dialogLoading">
                    <el-row>

                        <el-col :span="12">
                            <el-form-item label="用户编号：" prop="EmployeeID">
                                <el-input v-model="ruleForm.EmployeeID" readonly="true" disabled="true"></el-input>
                            </el-form-item>
                            <el-form-item label="用户姓名：" prop="EmployeeName">
                                <el-input v-model="ruleForm.EmployeeName" readonly="true" disabled="true"></el-input>
                            </el-form-item>
                            <el-form-item label="所属机构：" prop="UserInstitutionName">
                                <el-input v-model="ruleForm.UserInstitutionName" readonly="true"
                                          disabled="true"></el-input>
                            </el-form-item>
                            <el-form-item label="所属角色：" prop="RoleName">
                                <el-input type="textarea" autosize v-model="ruleForm.RoleName" readonly="true"
                                          disabled="true"></el-input>
                            </el-form-item>
                            <el-form-item label="旧密码：" prop="Password">
                                <%--<el-input  v-model="ruleForm.Password" show-password></el-input>--%>
                                <el-input type="password" placeholder="请输入旧密码" v-model="ruleForm.Password"
                                          show-password @change="passwordChange"></el-input>
                            </el-form-item>
                            <el-form-item label="新密码：" prop="pass">
                                <el-input type="password" placeholder="请输入新密码" v-model="ruleForm.pass" show-password
                                          @change="passChange"></el-input>
                            </el-form-item>
                            <el-form-item label="确认密码：" prop="checkPass">
                                <el-input type="password" placeholder="请确认新密码" v-model="ruleForm.checkPass"
                                          show-password
                                          @change="checkPassChange"></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col :span="12">
                            <el-row type="flex" justify="center">
                                <el-upload
                                        class="avatar-uploader"
                                        drag
                                        action="/kuangshanJava/SystemMaintenance/UserManage/uploadPhoto"

                                        :show-file-list="false"
                                        :on-success="handleAvatarSuccess"
                                        :before-upload="beforeAvatarUpload"
                                        style="margin-bottom: 1.5em">
                                    <img v-if="ruleForm.imageUrl" :src="ruleForm.imageUrl" class="avatar">
                                    <div class="el-upload__text" style="margin-top: 5em"><em>点击上传签名照片</em></div>
                                </el-upload>
                            </el-row>
                        </el-col>
                    </el-row>
                </el-form>
                <div slot="footer" class="dialog-footer">

                    <el-button id="btn-add" type="primary" @click="submitAdd">保存
                    </el-button>
                    <el-button :disabled="disabled_clear" type="primary" @click="removePhoto">删除签名照</el-button>
                    <el-button @click="_close" type="danger">关 闭
                    </el-button>
                </div>

            </el-dialog>
        </div>

        <%--修改密码——信息加载（不含签名照）--%>
        <div class="ChangePassword-dialog">
            <el-dialog title="用户信息" :modal="true" :visible.sync="DialogVisible2" width="45em"
                       :close-on-click-modal="false" :close-on-press-escape="false" :show-close="false">
                <el-form :inline="true" :model="ruleForm2" ref="ruleForm2" :rules="rules2" v-loading="dialogLoading">
                    <el-row>

                        <el-col :span="24">
                            <el-form-item label="用户编号：" prop="EmployeeID">
                                <el-input v-model="ruleForm2.EmployeeID" readonly="true" disabled="true"></el-input>
                            </el-form-item>
                            <el-form-item label="用户姓名：" prop="EmployeeName">
                                <el-input v-model="ruleForm2.EmployeeName" readonly="true" disabled="true"></el-input>
                            </el-form-item>
                            <el-form-item label="所属机构：" prop="UserInstitutionName">
                                <el-input v-model="ruleForm2.UserInstitutionName" readonly="true"
                                          disabled="true"></el-input>
                            </el-form-item>
                            <el-form-item label="所属角色：" prop="RoleName">
                                <el-input type="textarea" autosize v-model="ruleForm2.RoleName" readonly="true"
                                          disabled="true"></el-input>
                            </el-form-item>
                            <el-form-item label="旧密码：" prop="Password">
                                <%--<el-input  v-model="ruleForm.Password" show-password></el-input>--%>
                                <el-input type="password" placeholder="请输入旧密码" v-model="ruleForm2.Password"
                                          show-password @change="passwordChange2"></el-input>
                            </el-form-item>
                            <el-form-item label="新密码：" prop="pass">
                                <el-input type="password" placeholder="请输入新密码" v-model="ruleForm2.pass" show-password
                                          @change="passChange2"></el-input>
                            </el-form-item>
                            <el-form-item label="确认密码：" prop="checkPass">
                                <el-input type="password" placeholder="请确认新密码" v-model="ruleForm2.checkPass"
                                          show-password
                                          @change="checkPassChange2"></el-input>
                            </el-form-item>
                        </el-col>
                    </el-row>
                </el-form>
                <div slot="footer" class="dialog-footer">

                    <el-button id="btn-add" type="primary" @click="submitAdd2">保存
                    </el-button>
                    <el-button @click="_close2" type="danger">关 闭
                    </el-button>
                </div>

            </el-dialog>
        </div>

    </div>
</div>


<script type="text/javascript" src="static/bootstrap/js/bootstrap.min.js"></script>
<script type="text/javascript" src="static/main/js/adminlte.min.js"></script>
<script type="text/javascript" src="static/main/js/demo.js"></script>
<script>
    $(function () {
        const Selector = {
            wrapper: '.wrapper',
            contentWrapper: '.content-wrapper',
            layoutBoxed: '.layout-boxed',
            mainFooter: '.main-footer',
            mainHeader: '.main-header',
            sidebar: '.sidebar',
            controlSidebar: '.control-sidebar',
            fixed: '.fixed',
            sidebarMenu: '.sidebar-menu',
            logo: '.main-header .logo'
        };
        $('#right-frame').css('height', $(window).height() - $(Selector.mainHeader).outerHeight());
        $('ul.treeview-menu>li>a').click(function () {
            $('ul.treeview-menu>li.active').removeClass('active');
            $(this).closest('li').addClass('active');
        });
        $('section.sidebar').css('height', $('aside.main-sidebar').height())

    })
</script>


</body>
</html>

