<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>

<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"/>
    <meta name="renderer" content="webkit|ie-comp|ie-stand" />
    <title>安全信息管理与预警系统</title>
    <link href="static/bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css"/>
    <link href="static/css/jqModal.css" rel="stylesheet" type="text/css"/>

    <script type="text/javascript" src="static/js/jquery-3.3.1.min.js"></script>
    <script type="text/javascript" src="static/js/jqModal.min.js"></script>
    <script type="text/javascript" src="static/js/vue.min.js"></script>
    <link rel="stylesheet" type="text/css" href="static/element/element-ui.css"/>
    <script type="text/javascript" src="static/element/element-ui.js"></script>
    <script type="text/javascript" src="static/main/js/login.js"></script>

    <style>
        .yellow-button {
            border: none;
            padding: 0.1% 1.3%;
            color: white;
            font-family: '黑体';
            font-weight: bold;
            font-size: 1.3vw;
            position: absolute;
            top: 160vh;
            background-color: #DFB721;
        }

        #show-login-box {
            border: 0.05vw solid #037dfa;
            padding: 0.1% 1.5%;
            color: #037dfa;
            font-family: '微软雅黑';
            font-size: 1vw;
            position: absolute;
            left: 92%;
            top: 1%;
            background-color: #ffffff;
        }

        #close-login-box {
            z-index: 11111;
            position: absolute;
            left: 90%;
            top: 3%;
            font-size: 1.2vw;
            font-weight: bold;
            cursor: pointer;
        }

        .login-box {
            padding:3% 1%;
            background-color:#ffffff;
            position: absolute;
            left:37%;
            top:25%;
            width: 24%;
            height:  61.2%;
            text-align:center;
        }

        .login-box .login-box-input{
            padding-left:2%;
            background-color:white;
            height:18%;
            line-height:18%;
            font-family:'微软雅黑';
            font-size:0.9vw;
            margin-bottom:5%;
        }
        .login-box .contain{
            height:55%;
            margin:0 auto;
            width:80%;
        }
        .login-box .form-control{
            width:100%;
        }

        .login-box-bottom {
            height: 18%;
            line-height: 18%;
            color: white;
            font-family: '微软雅黑';
            font-size: 0.9vw;
            background-color: #03A2FD;
            margin-top: 5%;
        }

        #login-box-bg {
            height: 45%;
            width: auto;
            margin-bottom: 5%;
        }

        #forget-password {
            margin-left: 2%;
            margin-top: 2%;
            float: right;
            padding: 0;
            display: inline-block;
            vertical-align: top;
            color: #FF9E29;
            background-color: white;
            text-decoration: underline;
            font-size: 0.8vw;
        }

        #forget-password:hover, #forget-password:active {
            margin-left: 2%;
            margin-top: 2%;
            float: right;
            padding: 0;
            display: inline-block;
            vertical-align: top;
            color: #FF9E29;
            background-color: white;
            text-decoration: underline;
            font-size: 0.8vw;
        }

    </style>
    <script>
        $(function () {
            var imgheight = parseInt($("#bgimage").css("height"));
            var btnheight = imgheight * 0.83;
            $(".yellow-button").css("top", btnheight);
            $('#login-box').jqm({
                modal: true
            });
        });

        function closeLoginBox() {
            $("#login-box").jqmHide();
            $("#txtUserName").val("");
            $("#txtPSD").val("");
            $("#Tips").html("");
        }
        function login() {
            let userid=$('#userid').val()
            let password=$('#password').val()
            $.ajax({
                type: 'post',
                url: 'user_login.do',
                data: {
                    userid:userid,
                    password:password,
                },
                dataType: 'json',
                success:function (data) {
                    if(data.status==='ok'){
                        sessionStorage.setItem("userRight",data.userRight);
                        sessionStorage.setItem("userName",data.userName)
                        sessionStorage.setItem("userInstitutionCategoryNum",data.userInstitutionCategoryNum)
                        sessionStorage.setItem("userInstitution",data.userInstitution)
                        sessionStorage.setItem("userInstitutionName",data.userInstitutionName)
                        sessionStorage.setItem("userNum",data.userNum)
                        window.location.href='main2.jsp';
                    }else{
                        /* ELEMENT.Message({
                            message: data.text,
                            type: 'error'
                        });*/
                        alert(data.text);
                    }
                }
            })
        }
    </script>
</head>
<body class="login-body" id="login-body">

<div style="width: 100%">
    <%--<img src="static/image/login-bg.png" alt="" style="width: 100%;"/>--%>
    <img src="static/image/login-banner.png" alt="" style="width: 100%;padding-top: 4em"/>
    <img src="static/image/login-bg01.png" alt="" style="width: 100%;"/>
    <img src="static/image/login-bottom.png" alt="" style="width: 100%;"/>
</div>

<button id="show-login-box" onclick="$('#login-box').jqmShow()" style="margin-top: 0.28em">登录</button>


<el-dialog :modal="true" :visible.sync="DialogVisible" width="24%" height="61.2%"  left="37%"
           top="25%"
           :close-on-click-modal="false" :close-on-press-escape="false" :show-close="false">

</el-dialog>

<form>
    <div>
        <div class="login-box modal" style="display: none" id="login-box">
            <span id="close-login-box" onclick="closeLoginBox()">×</span>
            <img id="login-box-bg" src="static/image/login-box-bg.png" alt="Alternate value"/>
            <div class="contain">
                <input id="userid" type="text" placeholder="用户名" class="form-control login-box-input"/>
                <%--<el-input  clearable v-model="password" placeholder="密码"></el-input>--%>
                <input id="password" type="password" placeholder="密码" class="form-control login-box-input"/>
                <input type="button" value="立即登录" class="form-control login-box-bottom" onclick="login()"/>
                <a href="#" id="forget-password" onclick="alert('请联系管理员')">忘记密码</a>
            </div>
        </div>
    </div>
</form>


</body>
</html>

