$(function () {
    new Vue({
        el: "#login-body",
        data: {
            DialogVisible:'',
            userid:'',
            password:'',
        },
        methods:{
            login:function(){
                $.ajax({
                    type: 'post',
                    url: 'user_login',
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
                            ELEMENT.Message({
                                message: data.text,
                                type: 'error'
                            });
                            //alert(data.text);
                        }
                    }
                })
            }
        },
        created:function (){
            //this.login();
        },
    });
})