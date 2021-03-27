<%@ page language="java" contentType="text/html; charset=utf-8"
         pageEncoding="utf-8" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head><meta content="webkit" name="renderer" />
    <title></title>
 <%--   <script src="https://unpkg.com/babel-standalone@6/babel.min.js"></script>
    <script type="text/javascript" src="static/js/polyfill.min.js"></script>--%>
    <script type="text/javascript" src="static/js/jquery-3.3.1.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/static/js/vue.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/static/element/element-ui.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/static/js/moment.js"></script>
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/static/element/element-ui.css"/>

    <script type="text/javascript"
            src="<%=request.getContextPath()%>/static/js/IndexInfo.js"></script>
    <script type="text/javascript">
        $(function () {
            const trim=(val)=>{
                return Number(val.replace(/px/g,''));
            };
            const resizeDiv=()=>{
                $('.index-right').css('width',($(window).outerWidth()-2*trim($('.main').css('marginLeft'))-4*trim($('.index-right').css('borderWidth')))/2-1);
                $('.index-right').css('height',($(window).outerHeight()-2*trim($('.main').css('marginTop'))-4*trim($('.index-right').css('borderWidth')))/2-1);
            }
            resizeDiv();
        })
        $(function () {


          /*  for(let moduleRight of _userRight.split(";")){
                if(moduleRight.indexOf('040200')>=0){
                    $('.index-content-link1').removeAttr("disabled"); //移除disabled属性
                }
                else if(moduleRight.indexOf('030500')>=0)       //重大风险告知
                {
                    $('.index-content-link2').removeAttr("disabled"); //移除disabled属性
                }
                else if(moduleRight.indexOf('050500')>=0)       //未处理隐患
                {
                    $('.index-content-link3').removeAttr("disabled"); //移除disabled属性
                }
                else if(moduleRight.indexOf('050500')>=0)      //已逾期隐患
                {
                    $('.index-content-link4').removeAttr("disabled"); //移除disabled属性
                }
            }*/
        })
    </script>
    <%
        String UserNum = session.getAttribute("UserNum").toString();
    %>
    <style>
        html, body {
            height: 100%;
            width: 100%;
            background-color: #ecf0f1;
            margin:0;
        }
        html::-webkit-scrollbar{
            width: 0;
        }
        .index-right{
            /*width: 50%;*/
            /*height:320px;*/
            display: inline-block;
            border: 1px solid #e6e6e6;
            background-color: white;
            overflow: auto;
        }
        .main{
            overflow: hidden;
            margin:4px;
        }
        .index-header{
            height:3em;
            line-height: 3em;
            font-size: 1.5em;
            margin:0em 1em;
            border-bottom: 1px solid #e6e6e6;
            align-items: center;
            display: flex;

        }

        .index-content{
            margin-top: 1em;
        }

        .index-content-link1,.index-content-link2,.index-content-link3,.index-content-link4{
            color: black;
            text-decoration: unset;
        }

    </style>
</head>

<body>
        <!-- 内容 -->
        <div class="main"><div class="index-right">
                <div class="index-header">
                    <img src="static/image/right-todo-logo.png">
                    <span style="margin-left: 0.5em">待办事宜</span>
                    <%if(!UserNum.equals("admin")) {%>
                    <i style="margin-left: auto">
                        <a :href="Href[0]" id="backlog"><img src="static/image/more01.png"></a>
                    </i>
                    <%}%>
                </div>

                <div class="main_txt">
                    <ul>
                        <%if(!UserNum.equals("admin")) {%>
                        <li class="index-content"  v-for="(record,index) in approvalList" :key="index">
                            <a class="index-content-link1" :href="Href[0]" >{{record.DangerTaskName+'，'+record.ApplicantName+'，'+record.ApplicantInstitutionName}}</a>
                        </li>
                        <%}%>
                    </ul>
                </div>
            </div><div class="index-right">
                <div class="index-header">
                    <img src="static/image/right-risk-logo.png">
                    <span style="margin-left: 0.5em">重大风险</span>
                    <i style="margin-left: auto">
                            <%if(!UserNum.equals("admin")) {%>
                        <a :href="Href[1]"><img src="static/image/more03.png"></a>
                            <%}%>
                    </i>


                </div>
            <div class="main_txt">
                <div class="main_txt">
                    <ul>
                        <%if(!UserNum.equals("admin")) {%>
                        <li class="index-content"  v-for="(record,index) in riskInfoList" :key="index">
                            <a class="index-content-link2" :href="Href[1]" >{{record.sectionName+'，'+record.EquipTrouble+'，'+record.TroubleResult+'，'+record.RiskLevelDescription}}</a>
                        </li>
                        <%}%>
                    </ul>
                </div>
            </div>
            </div><div class="index-right">
            <div class="index-header">
                <div>
                    <img src="static/image/right-hiddenDanger-logo.png">
                </div>

                <span style="margin-left: 0.5em">未处理整改</span>
                <i style="margin-left: auto">
                    <%if(!UserNum.equals("admin")) {%>
                    <a id="before" :href="Href[2]"><img src="static/image/more02.png"></a>
                    <%}%>
                </i>


            </div>
            <div class="main_txt">
                <div class="main_txt">
                    <ul>
                        <%if(!UserNum.equals("admin")) {%>
                        <li class="index-content"  v-for="(record,index) in safetyList" :key="index">
                            <a class="index-content-link3" :href="Href[2]" >{{record.checkMemo+'，'+record.personInChargeName+'，'+record.institutionCheckedName}}</a>
                        </li>
                        <%}%>
                    </ul>
                </div>
            </div>
        </div><div class="index-right">
                <div class="index-header">
                    <div>
                    <img src="static/image/right-safetyRisk-logo.png">
                    </div>
                    <span style="margin-left: 0.5em">已逾期整改</span>
                    <i style="margin-left: auto">
                        <%if(!UserNum.equals("admin")) {%>
                        <a id="before" :href="Href[3]"><img src="static/image/more04.png"></a>
                        <%}%>
                    </i>
                    <%--<i><a href="safetyItemMan/safetyItemMan.aspx">更多>></a></i>--%>



                </div>
            <div class="main_txt">
                <div class="main_txt">
                    <ul>
                        <%if(!UserNum.equals("admin")) {%>
                        <li class="index-content"  v-for="(record,index) in safetyList2" :key="index">
                            <a class="index-content-link4" :href="Href[3]">{{record.InstructionNum+' , '+record.RectifactionChargeInstiName+' , '+record.RectifactionChargerName+' , '+record.RectifactionEndDate}}</a>
                        </li>
                        <%}%>
                    </ul>
                </div>
            </div>
            </div>
        </div>

</body>
</html>
