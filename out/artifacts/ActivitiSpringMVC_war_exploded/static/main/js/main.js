$(function () {

    new Vue({
        el: ".wrapper",
        data: {
            DialogVisible1: false,
            DialogVisible2: false,
            EmployeeName: '',
            disabled_clear:true,
            pageLoading:false,
            dialogLoading:false,
            ruleForm: {
                EmployeeID: '',
                EmployeeName: '',
                UserInstitutionName: '',
                RoleName: '',
                oldPassword: '',
                Password: '',
                pass: '',
                checkPass: '',
                imageUrl: '',
                imageName: '',
            },
            rules: {
                Password: [{required: true, message: '请输入旧登陆密码', trigger: 'blur',}],
                pass: [{required: true, message: '请输入新登陆密码', trigger: 'blur',}],
                checkPass: [{required: true, message: '再次输入新登陆密码', trigger: 'blur'}]
            },


            ruleForm2: {
                EmployeeID: '',
                EmployeeName: '',
                UserInstitutionName: '',
                RoleName: '',
                oldPassword: '',
                Password: '',
                pass: '',
                checkPass: '',
                tag:'Web',
            },
            rules2: {
                Password: [{required: true, message: '请输入旧登陆密码', trigger: 'blur',}],
                pass: [{required: true, message: '请输入新登陆密码', trigger: 'blur',}],
                checkPass: [{required: true, message: '再次输入新登陆密码', trigger: 'blur'}]
            }

        },
        methods: {
            //含签名照
            handleAvatarSuccess:function(res, file) {
                let vm = this;

                vm.ruleForm.imageUrl = URL.createObjectURL(file.raw);
                file.name = res.name;
                //alert(file.name)
                vm.ruleForm.imageName = file.name;
               // debugger
                // alert(JSON.stringify(file))
                if (file.status == 'success') {
                    this.$message({
                        message: '签名照更换成功！',
                        type: 'success'
                    });
                    vm.disabled_clear = false
                } else {
                    this.$message({
                        message: '签名照更换失败！',
                        type: 'error'
                    });
                }
            },
            beforeAvatarUpload:function(file) {
                const isJPG = file.type === 'image/jpeg';
                const isLt2M = file.size / 1024 / 1024 < 10;

                if (!isJPG) {
                    this.$message.error('上传图片只能是 JPG 格式!');
                }
                if (!isLt2M) {
                    this.$message.error('上传图片大小不能超过 2MB!');
                }
                return isJPG && isLt2M;
            },

            //删除照片
            removePhoto:function()
            {
                let vm=this;
                vm.ruleForm.imageUrl  =''
                vm.ruleForm.imageName =''
                vm.disabled_clear=true
                $.ajax({
                    type: 'post',
                    url: '/kuangshanJava/SystemMaintenance/UserManage/deletePhoto',
                    data: vm.ruleForm,
                    // dataType: 'json',
                    success: function (response) {
                        if (response === 'success') {
                            ELEMENT.Message({
                                message: '签名照删除成功！',
                                type: 'success'
                            });

                        } else {
                            ELEMENT.Message({
                                message: '签名照删除失败',
                                type: 'error'
                            });
                        }
                    },
                    error: handleError
                })

            },
            //表单重置
            resetForm(formName) {

                let vm=this;
                //debugger
                if (this.$refs[formName]!==undefined) {
                    this.$refs[formName].resetFields();
                    this.$refs[formName].clearValidate();
                }
            },


            _close: function () {
                let vm = this;
                vm.DialogVisible1 = false;
                vm.resetForm('ruleForm')
            },

            passwordChange: function () {
                let vm = this;
                if (vm.ruleForm.oldPassword != vm.ruleForm.Password) {
                    vm.ruleForm.Password = ''
                    this.$message({
                        showClose: true,
                        message: '旧密码错误！',
                        type: 'error'
                    });
                }
            },

            passChange: function () {
                let vm = this;
                vm.ruleForm.checkPass = ''
            },
            checkPassChange: function () {
                let vm = this;
                if (vm.ruleForm.pass != vm.ruleForm.checkPass&&vm.ruleForm.pass !='') {
                    vm.ruleForm.pass = ''
                    vm.ruleForm.checkPass = ''
                    this.$message({
                        showClose: true,
                        message: '两次输入密码不一致！',
                        type: 'warning'
                    });
                }
            },


            submitAdd: function () {
                let vm = this
                this.$refs['ruleForm'].validate(function (valid) {
                    if (valid) {
               /* if(vm.ruleForm.pass!=''&&vm.ruleForm.checkPass=='')
                {
                    this.$message({
                        message: '请再次输入新登陆密码！',
                        type: 'warning'
                    });
                }
                else if(vm.ruleForm.pass==''&&vm.ruleForm.checkPass!="")
                {
                    this.$message({
                        message: '请输入新登陆密码!',
                        type: 'warning'
                    });
                }
                else {*/
                    $.ajax({
                        type: 'post',
                        url: '/kuangshanJava/SystemMaintenance/UserManage/Save_ChangePWD_1',
                        data: vm.ruleForm,
                        // dataType: 'json',
                        success: function (response) {
                            if (response === 'success') {
                                ELEMENT.Message({
                                    message: '保存成功',
                                    type: 'success'
                                });

                            } else {
                                ELEMENT.Message({
                                    message: '保存失败',
                                    type: 'error'
                                });
                            }
                        },
                        error: handleError
                    })
               // }

                    } else {
                        ELEMENT.Message(
                            {
                                message: '请先完成输入！',
                                type: 'error'
                            }
                        );
                    }
                })
            },


            //不含签名照

            _close2: function () {
                let vm = this;
                vm.DialogVisible2 = false
                vm.resetForm('ruleForm2')
            },

            passwordChange2: function () {
                let vm = this;
                if (vm.ruleForm2.oldPassword != vm.ruleForm2.Password) {
                    vm.ruleForm2.Password = ''
                    this.$message({
                        showClose: true,
                        message: '旧密码错误！',
                        type: 'error'
                    });
                }
            },

            passChange2: function () {
                let vm = this;
                vm.ruleForm2.checkPass = ''
            },
            checkPassChange2: function () {
                let vm = this;
                if (vm.ruleForm2.pass != vm.ruleForm2.checkPass) {
                    vm.ruleForm2.pass = ''
                    vm.ruleForm2.checkPass = ''
                    this.$message({
                        showClose: true,
                        message: '两次输入密码不一致！',
                        type: 'warning'
                    });
                }
            },


            submitAdd2: function () {
                let vm = this
                this.$refs['ruleForm2'].validate(function (valid) {
                    if (valid) {
                        $.ajax({
                            type: 'post',
                            url: '/kuangshanJava/SystemMaintenance/UserManage/Save_ChangePWD_2',
                            data: vm.ruleForm2,
                            // dataType: 'json',
                            success: function (response) {
                                if (response === 'success') {
                                    ELEMENT.Message({
                                        message: '保存成功',
                                        type: 'success'
                                    });

                                } else {
                                    ELEMENT.Message({
                                        message: '保存失败',
                                        type: 'error'
                                    });
                                }
                            },
                            error: handleError
                        })
                    } else {
                        ELEMENT.Message(
                            {
                                message: '请先完成输入！',
                                type: 'error'
                            }
                        );
                    }
                })
            },

            getList: function () {
                let vm = this;
                vm.pageLoading = true;
                $.ajax({
                    url: '/kuangshanJava/SystemMaintenance/UserManage/getUserState',
                    type: "post",
                    data: {
                        tag: 'Web'
                    },
                    dataType: 'json',
                    success: function (result) {
                        if (result.userState == '1')    //含签名照
                        {
                            vm.DialogVisible1 = true;
                            vm.dialogLoading = true;
                            vm.ruleForm.imageUrl = '';
                            vm.disabled_clear=true;
                            vm.ruleForm.imageName = '';
                            vm.ruleForm.EmployeeID = '';
                            vm.ruleForm.EmployeeName = '';
                            vm.ruleForm.UserInstitutionName = '';
                            vm.ruleForm.RoleName = '';
                            vm.ruleForm.Password = '';
                            vm.ruleForm.pass = '';
                            vm.ruleForm.checkPass = '';
                            vm.ruleForm.oldPassword = '';

                            $.ajax({
                                url: '/kuangshanJava/SystemMaintenance/UserManage/Info_ChangePWD_1',
                                type: "post",
                                data: {},
                                dataType: 'json',
                                success: function (result) {
                                    vm.ruleForm.imageUrl = result.name;
                                    if(vm.ruleForm.imageUrl=='')
                                    {
                                        vm.disabled_clear=true
                                    }
                                    else {
                                        vm.disabled_clear=false
                                    }
                                    vm.ruleForm.imageName = result.name;
                                    vm.ruleForm.EmployeeID = result.EmployeeID;
                                    vm.ruleForm.EmployeeName = result.EmployeeName;
                                    vm.ruleForm.UserInstitutionName = result.UserInstitutionName;
                                    for (let i = 0; i < result.RoleNameList.length; i++) {
                                        vm.ruleForm.RoleName += result.RoleNameList[i].RoleName + " ";
                                    }

                                    vm.ruleForm.oldPassword = result.oldPassword
                                    vm.dialogLoading =false;
                                    vm.pageLoading = false;

                                    $('body.el-popup-parent--hidden').css('paddingRight', '0px');

                                },
                                error: handleError
                            });
                        } else {
                            vm.DialogVisible2 = true;
                            vm.dialogLoading = true;
                            vm.ruleForm2.EmployeeID = '';
                            vm.ruleForm2.EmployeeName = '';
                            vm.ruleForm2.UserInstitutionName = '';
                            vm.ruleForm2.RoleName = '';
                            vm.ruleForm2.Password = '';
                            vm.ruleForm2.pass = '';
                            vm.ruleForm2.checkPass = '';
                            vm.ruleForm2.oldPassword = ''


                            $.ajax({
                                url: '/kuangshanJava/SystemMaintenance/UserManage/Info_ChangePWD_2',
                                type: "post",
                                data: {tag:'Web'},
                                dataType: 'json',
                                success: function (result) {
                                    vm.ruleForm2.EmployeeID = result.EmployeeID;
                                    vm.ruleForm2.EmployeeName = result.EmployeeName;
                                    vm.ruleForm2.UserInstitutionName = result.UserInstitutionName;
                                    for (let i = 0; i < result.RoleNameList.length; i++) {
                                        vm.ruleForm2.RoleName += result.RoleNameList[i].RoleName + " ";
                                    }

                                    if(result.EmployeeName=='管理员')
                                    {
                                        vm.ruleForm2.EmployeeID='admin'
                                        vm.ruleForm2.UserInstitutionName = '乌龙泉矿'
                                        vm.ruleForm2.RoleName='系统管理员'
                                    }


                                    vm.ruleForm2.oldPassword = result.oldPassword
                                    vm.dialogLoading = false;
                                    vm.pageLoading = false;

                                    $('body.el-popup-parent--hidden').css('paddingRight', '0px');


                                },
                                error: handleError
                            });
                        }

                        //debugger
                        //alert(JSON.stringify(vm.approvalList))
                    },
                    error: handleError
                });
            },

            logout: function () {
                top.location.href = 'login.jsp';
            },
            _help:function(){
                window.open("HelpFile/help.jsp",'');
            },
            getMyName:function() {
                let vm =this;
                vm.EmployeeName= sessionStorage.getItem("userName");

            }
        },
        created: function () {
            this.getMyName();
            //this.pageLoading = true;
            //this.getList();
        },
    });
})