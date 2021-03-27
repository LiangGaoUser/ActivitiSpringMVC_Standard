$(function () {
    new Vue({
        el: "#right",
        data: {
            InstitutionList: [],
            taskList: [],
            templist: '',
            tag: 'Web',

            pagenum: 0,
            pageindex: 0,
            conditions: '',
            listindex: '',
            listsize: '',
            id_list: [], //多选选中的id数组，以,隔开

            viewDialogVisible1: false,
            viewDialogVisible0: false,
            audioDialogVisible: false,
            picviewDialogVisible: false,
            filedetailDialogVisible: false,
            filetype: '',
            filePath: '',
            fileurl: '',
            fileList: [],
            filedata: {id: 'add'},
            taskIDList: [],

            pageLoading: false,
            addLoading: false,
            tableLoading: false,
            dialogLoading: false,


            addDialogVisible: false,
            disabled_add: false,
            disabled_add_submit: false,

            detailDialogVisible: false,
            detailLoading: false,

            editDialogVisible: false,
            editLoading: false,
            editTitle: '',
            disabled_save: false,
            disabled_edit_submit: false,

            submitDialogVisible: false,
            submitLoading: false,
            disabled_submit_submit: false,

            historyDialogVisible: false,
            historyLoading: false,
            HistoryRecord: [],

            FirstInstitutionList: [],
            categoryDisabled: true,
            CategoryList: [],     //危险作业类型集合  ——查询框
            SecondaryCategory: [],  //  危险作业类型 ——添加、修改、详细等界面
            companyCategoryList: [],  //公司级危险作业类型集合
            workshopCategoryList: [],  //车间级危险作业类型集合
            pickerOptions1: {
                shortcuts: [{
                    text: '今天',
                    onClick(picker) {
                        picker.$emit('pick', new Date());
                    }
                }, {
                    text: '明天',
                    onClick(picker) {
                        const date = new Date();
                        date.setTime(date.getTime() + 3600 * 1000 * 24);
                        picker.$emit('pick', date);
                    }
                }, {
                    text: '一周后',
                    onClick(picker) {
                        const date = new Date();
                        date.setTime(date.getTime() + 3600 * 1000 * 24 * 7);
                        picker.$emit('pick', date);
                    }
                }]
            },

            params: [{
                label: '作业编号：',
                type: 'plain',
                model: 'DangerTaskNum',
            }, {
                label: '作业名称：',
                type: 'plain',
                model: 'DangerTaskName',
            },
                {
                    label: '申请单位：',
                    type: 'select',
                    model: 'TaskInstitution',
                },
                {
                    label: '作业类型：',
                    type: 'select',
                    model: 'Category',
                }, {
                    label: '任务状态：',
                    type: 'select',
                    model: 'State',
                },
                {
                    label: '是否归档：',
                    type: 'select',
                    model: 'Archived',
                }],
            addFrom: {
                TaskID: '',
                Applicant: '',               //申请人
                DangerTaskName: '',
                DangerTaskNum: '',           //危险作业编号
                ApplyInstitution: '',       //申请单位
                Archived: '0',
                DangerTaskLevel: '',      //危险作业级别
                Category: '',
                State: '',
                ApplyingTime: new Date(),
                StartTime: '',
                EndTime: '',
                uploadfilename: '',
                filetable: [],

                leftData: [],
                rightList: [],
                ApproverList: '',
                tag: 'Web',
            },
            rules: {

                DangerTaskName: [
                    {required: true, message: '请输入作业项目名', trigger: 'blur'}
                ],
                DangerTaskNum: [
                    {required: true, message: '请输入危险作业编号', trigger: 'blur'}
                ],
                /*ApplyInstitution: [
                    {required: true, message: '请选择申请单位', trigger: 'blur'}
                ],*/
                DangerTaskLevel: [
                    {required: true, message: '请选择危险作业级别', trigger: 'blur'}
                ],
                Category: [
                    {required: true, message: '请选择危险作业类型', trigger: 'blur'}
                ],
                ApplyingTime: [
                    {required: true, message: '请选择申请时间', trigger: 'blur'}
                ],
                StartTime: [
                    {required: true, message: '请选择作业开始时间', trigger: 'blur'}
                ],
                EndTime: [
                    {required: true, message: '请选择作业截止时间', trigger: 'blur'}
                ]
            },
        },
        computed: {
            idList: function () {
                let vm = this;
                let idlist = [];
                vm.id_list.forEach(function (val) {
                    if (val) {
                        idlist.push(val);
                    }
                })
                return idlist;
            }
        },
        methods: {
            filter: function () {
                let params = [];
                for (col of this.params) {
                    switch (col.type) {
                        case 'plain':
                            if (col.value && col.value.replace(/ /g, '')) {
                                col.value = col.value.replace(/ /g, '');
                                params.push(` ${col.model} like '%${col.value}%' `);
                            }
                            break
                        case 'time':
                            if (col.value && col.value[0] && col.value[1]) {
                                params.push(` ${col.model} between '${moment(col.value[0]).format('YYYY-MM-DD')}' and '${moment(col.value[1]).format('YYYY-MM-DD')}'`);
                            }
                            break
                        case 'select':
                            if (col.value) {
                                if (col.label == '作业类型：') {
                                    params.push(` DangerTaskLevel = '${col.value[0]}' `);
                                    params.push(` ${col.model} = '${col.value[1]}' `);
                                } else {
                                    params.push(` ${col.model} = '${col.value}' `);
                                }

                            }
                            break
                        case 'number':
                            if (col.value && col.value.replace(/ /g, '')) {
                                let num = Number(col.value.replace(/ /g, ''))
                                if (num) {
                                    params.push(` ${col.model} > ${num} `);
                                }
                            }
                            break
                    }
                }
                alert(params);
                this.getList(1, params.join('and'))
            },

            //表单重置
            resetForm(formName) {

                let vm = this;
                //debugger
                if (this.$refs[formName] !== undefined) {
                    this.$refs[formName].resetFields();
                    this.$refs[formName].clearValidate();
                    this.fileList = [];
                }

                for (let key in vm.addFrom) {
                    if (typeof vm.addFrom[key] === 'object') {
                        vm.addFrom[key] = []
                    } else {
                        vm.addFrom[key] = ''
                    }
                }

                vm.addFrom.uploadfilename = ''
                vm.categoryDisabled = true
                if (this.$refs['my-upload'] !== undefined) {
                    this.$refs['my-upload'].clearFiles();
                }
            },

            beforeAvatarUpload(file) {
                const isLt500M = file.size / 1024 / 1024 < 500;
                if (!isLt500M) {
                    window.alert("上传图片大小不能超过 500MB!");
                    this.common.errorTip('上传图片大小不能超过 500MB!');
                }
                return isLt500M;
            },
            successUpload(response, file, fileList) {
                this.addFrom.uploadfilename = ""
                for (let j = 0; j < fileList.length; j++) {
                    this.addFrom.uploadfilename += fileList[j].name;
                    this.addFrom.uploadfilename += '?';
                }
            },
            removeFile(file, fileList) {
                let vm = this;
                vm.addFrom.uploadfilename = ""
                for (let j = 0; j < fileList.length; j++) {
                    vm.addFrom.uploadfilename += fileList[j].name;
                    vm.addFrom.uploadfilename += '?';
                }
            },
            successUpload1(response, file, fileList) {
                this.templist = "";
                for (j = 0; j < fileList.length; j++) {
                    this.templist += fileList[j].name;
                    this.templist += '?';
                }
            },
            errorUpload(response, file, fileList) {
                window.alert("上传失败");
            },
            removeFile1(file, fileList) {
                this.templist = "";
                for (j = 0; j < fileList.length; j++) {
                    this.templist += fileList[j].name;
                    this.templist += '?';
                }


            },
            DemoUploadSucceed(response, file, fileList){
                window.alert("文件上传成功");
            },
            DemoUploadError(response, file, fileList){
                window.alert("文件上传失败");
            },
            action(){
                return "D:\\workspace\\idea_workspace_2019\\ActivitiWeb\\out\\artifacts\\kuangshanJava_war_exploded\\upload/AssociateManagement/DangerTaskApplyMan/temp/"
            },



            _downloadone: function (ID, name) {
                httpPost('download.do', {
                    filename: name,
                    id: ID
                })
            },
            _viewFile: function (ID, name) {
                window.alert("进入了_viewFile"+ID+" "+name);
                let self = this;
                //alert(name)
                const loading = this.$loading({
                    lock: true,
                    text: '加载中...',
                    spinner: 'el-icon-loading',
                    background: 'rgba(0, 0, 0, 0.7)'
                });
                $.ajax({
                    url: 'viewFile.do',
                    type: "post",
                    data: {
                        filename: name,
                        id: ID,
                        tag: 'Web'
                    },
                    // contentType: 'application/x-www-form-urlencoded',
                    success: function (data) {
                        if (data != "") {
                            loading.close();
                            window.alert("成功获得数据");
                            /*var result = JSON.parse(data);

                            loading.close();
                            window.alert("成功获得数据");
                            self.filePath = '../../' + result.filepath;

                            if (result.type == 'mp4' || result.type == 'webm' || result.type == 'ogg') {
                                self.filetype = 'video/' + result.type;
                                self.viewDialogVisible1 = true;
                                //   var url = '../../' + result.filepath
                                var Player = videojs("videoex");  //初始化视频
                                Player.src(self.filePath);  //重置video的src
                                Player.load(self.filePath);  //使video重新加载
                                Player.play();
                            } else if (result.type == 'pdf' || result.type == 'html') {
                                self.viewDialogVisible0 = true;
                            } else if (result.type == 'jpg' || result.type == 'png' || result.type == 'gif') {
                                //
                                self.picviewDialogVisible = true;
                            } else if (result.type == 'mp3' || result.type == 'wav' || result.type == 'flac') {
                                //
                                self.filetype = 'audio/' + result.type;

                                self.audioDialogVisible = true;
                                var music = document.getElementById('audio');
                                music.load();
                                // music.src(self.filePath);
                                //   music.load(self.filePath);
                                //   music.play();
                            } else {
                                self.$message({
                                    type: 'info',
                                    message: '该文件暂不支持浏览!'
                                })
                            }*/
                        } else {
                            loading.close();
                            window.alert("加载失败了");
                            self.$message({
                                type: 'info',
                                message: '加载失败!'
                            })
                        }
                    },
                    error: handleError

                });
            },

            _deleteFile: function (ID, name) {
                var self = this;
                $.ajax({
                    url: "deleteFile.do",
                    type: "post",

                    data: {
                        id: ID,
                        name: name
                    },
                    success: function (data) {
                        if (data === "success") {

                            self.addFrom.filetable = self.addFrom.filetable.filter(function (item) {
                                return item.name !== name;
                            });
                            self.addFrom.uploadfilename = self.addFrom.uploadfilename.replace(name + '?', '');
                            self.templist = "";
                            self.$message({
                                type: 'success',
                                message: '文件' + name + '已删除!'
                            });
                        } else {
                            self.$message({
                                type: 'fail',
                                message: '文件' + name + '删除失败!'
                            });
                        }


                    },
                    error: handleError

                });
                //  this.editFrom.filetable.remove({name:name});

            },

            closevideoDialog: function () {

                var Player = videojs("videoex");  //初始化视频

                Player.pause();

            },
            closevideoDialog1: function () {
                var music = document.getElementById('audio');
                music.pause();


            },

            taskLevelChange: function () {
                let vm = this;
                vm.categoryDisabled = false;
                if (vm.addFrom.DangerTaskLevel == 'CompanyDangerTask') {
                    vm.SecondaryCategory = vm.companyCategoryList;
                } else {
                    vm.SecondaryCategory = vm.workshopCategoryList;
                }
            },

            //添加
            getInfoForAdd: function () {
                let vm = this;
                let leftList = [];
                vm.resetForm('addFrom');

                vm.addFrom.leftData = []


                vm.tableLoading = true;
                $.ajax({
                    url: 'getInfoForAdd.do',
                    type: "post",
                    data: {tag: vm.tag},
                    dataType: 'json',
                    success: function (result) {
                        vm.addFrom.Applicant = result.UserName
                        vm.addFrom.Archived = '0'
                        vm.addFrom.ApplyingTime = new Date()
                        vm.addFrom.ApplyInstitution = result.UserInstitutionName
                        //vm.TaskInstitutionList = result.TaskInstitutionList
                        vm.FirstInstitutionList = result.FirstInstitutionList
                        //vm.CategoryList = result.CategoryList
                        leftList = result.FirstInstitutionList
                        for (let i = 0; i < leftList.length; i++) {
                            vm.addFrom.leftData.push({
                                key: leftList[i].employeeNum,
                                label: leftList[i].institutionName
                            });
                        }
                        console.log(vm.addFrom.leftData)
                        //vm.leftData = result.FirstInstitutionList
                        vm.addFrom.rightList = []
                        vm.disabled_add_submit = false
                        vm.disabled_add = false
                        vm.addDialogVisible = true;
                        vm.tableLoading = false;
                    },
                    error: handleError
                });
            },

            EndTimeChanged() {
                let vm = this;
                if (vm.addFrom.StartTime > vm.addFrom.EndTime) {
                    ELEMENT.Message({
                        message: '结束时间不能早于开始时间！',
                        type: 'success'
                    });
                    vm.addFrom.EndTime = ''
                }
            },

            _add: function () {
                let self = this;
                this.$refs['addFrom'].validate(function (valid) {
                    if (valid) {
                        window.alert("进入了_add");
                        self.tableLoading = true;
                        self.addFrom.ApproverList = self.addFrom.rightList.join(",")
                        self.disabled_add = true;
                        self.disabled_add_submit = true;
                        $.ajax({
                            url: 'SubmitAdd.do',
                            type: "post",
                            data: self.addFrom,
                            success: function (response) {
                                ELEMENT.Message({
                                    message: '添加成功',
                                    type: 'success'
                                });
                                self.disabled_add = true;
                                self.disabled_add_submit = false;
                                self.addFrom.TaskID = JSON.parse(response)[0].data

                                self.getList(self.pageindex, self.conditions);
                            },
                            error: handleError

                        });
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
            _addActiviti: function () {
                let self = this;
                this.$refs['addFrom'].validate(function (valid) {
                    if (valid) {
                        window.alert("Activiti进入了_add");
                        self.tableLoading = true;
                        self.addFrom.ApproverList = self.addFrom.rightList.join(",")
                        self.disabled_add = true;
                        self.disabled_add_submit = true;
                        $.ajax({
                            url: 'SubmitAddActiviti.do',
                            type: "post",
                            data: self.addFrom,
                            success: function (response) {
                                ELEMENT.Message({
                                    message: '添加成功',
                                    type: 'success'
                                });
                                self.disabled_add = true;
                                self.disabled_add_submit = false;
                                self.addFrom.TaskID = JSON.parse(response)[0].data

                                self.getListActiviti(self.pageindex, self.conditions);
                            },
                            error: handleError

                        });
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

            _submit_add() {
                let vm = this;
                this.$refs['addFrom'].validate(function (valid) {
                    if (valid) {
                        // self.addFrom.ApproverList = self.addFrom.rightList.join(",")

                        if ((vm.addFrom.rightList.length == 0) || (vm.addFrom.rightList == "")) {
                            ELEMENT.Message(
                                {
                                    message: '请先选择审批部门！',
                                    type: 'error'
                                }
                            );
                        } else {
                            let templist = []
                            console.log(JSON.stringify(vm.addFrom.leftData))
                            console.log(JSON.stringify(vm.addFrom.rightList))
                            if (vm.addFrom.rightList[0] == '') {
                                vm.addFrom.rightList.splice(0, 1);
                            }
                            console.log(JSON.stringify(vm.addFrom.leftData))
                            console.log(JSON.stringify(vm.addFrom.rightList))
                            vm.addFrom.ApproverList = vm.addFrom.rightList.join(",")
                            for (let j = 0; j < vm.addFrom.leftData.length; j++) {
                                for (let i = 0; i < vm.addFrom.rightList.length; i++) {
                                    if (vm.addFrom.leftData[j].key == vm.addFrom.rightList[i]) {
                                        templist[i] = vm.addFrom.leftData[j].label
                                    }
                                }
                            }
                            vm.$confirm('审批部门顺序为' + templist.join("——>") + ', 且提交后无法修改，是否继续提交?', '提示', {
                                confirmButtonText: '确定',
                                cancelButtonText: '取消',
                                type: 'warning'
                            }).then(() => {
                                vm.tableLoading = true;
                                if (vm.disabled_add == false) {
                                    vm.addFrom.TaskID = ''
                                }
                                vm.disabled_add = true
                                vm.disabled_add_submit = true

                                $.ajax({
                                    url: 'Add_Submit.do',
                                    type: "post",
                                    data: vm.addFrom,
                                    success: function (response) {
                                        if (response === 'success') {
                                            vm.$message({
                                                type: 'success',
                                                message: '提交成功!'
                                            });
                                            vm.disabled_add = true;
                                            //vm.disabled_submit = true;
                                        } else {
                                            ELEMENT.Message({
                                                message: '提交失败',
                                                type: 'error'
                                            });
                                        }
                                        vm.disabled_add = true
                                        vm.disabled_add_submit = true
                                        vm.getList(vm.pageindex, vm.conditions);
                                        vm.addDialogVisible = false;
                                    },
                                    error: handleError

                                });

                            }).catch(() => {
                                this.$message({
                                    type: 'info',
                                    message: '已取消提交'
                                });
                            });
                        }
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

            //修改
            getInfoForEdit: function (TaskID) {
                let vm = this;
                let flag = true

                vm.disabled_save = false
                vm.disabled_edit_submit = false
                vm.tableLoading = true;
                $.ajax({
                    url: 'check.do',
                    type: "post",
                    data: {
                        TaskID: TaskID,
                        tag: vm.tag
                    },
                    //async: false,
                    dataType: 'json',
                    success: function (result) {
                        let infoList = result.infoList
                        let applicant = result.applicant
                        //alert(applicant)
                        if (applicant == false) {
                            flag = false
                            ELEMENT.Message(
                                {
                                    message: '不是申请人，不能修改！',
                                    type: 'error'
                                }
                            );
                            vm.tableLoading = false;
                        } else {
                            for (let i = 0; i < infoList.length; i++) {
                                if (infoList[i].Archived == '1') {
                                    flag = false
                                    ELEMENT.Message(
                                        {
                                            message: '该申请已经归档，不可以修改！',
                                            type: 'error'
                                        }
                                    );
                                    vm.tableLoading = false;
                                    break;
                                } else if (infoList[i].State == '1' || infoList[i].State == '2' || infoList[i].State == '3') {
                                    flag = false
                                    ELEMENT.Message(
                                        {
                                            message: '该申请已提交，故不允许修改!',
                                            type: 'error'
                                        }
                                    );
                                    vm.tableLoading = false;
                                    break;
                                }
                            }

                        }

                        //允许操作
                        if (flag) {
                            let leftList = [];
                            vm.resetForm('addFrom');
                            vm.tableLoading = true;
                            vm.disabled_save = false
                            vm.disabled_edit_submit = false

                            $.ajax({
                                url: 'getInfoForEdit.do',
                                type: "post",
                                data: {TaskID: TaskID, tag: vm.tag},
                                dataType: 'json',
                                success: function (response) {
                                    //vm.TaskInstitutionList = response.TaskInstitutionList
                                    vm.FirstInstitutionList = response.FirstInstitutionList
                                    //vm.CategoryList = response.CategoryList

                                    vm.addFrom.Applicant = response.taskInfo[0].ApplicantName
                                    vm.addFrom.Archived = '0'
                                    vm.addFrom.ApplyingTime = response.taskInfo[0].ApplyingTime
                                    vm.addFrom.ApplyInstitution = response.UserInstitutionName

                                    vm.addFrom.DangerTaskName = response.taskInfo[0].DangerTaskName
                                    vm.addFrom.DangerTaskNum = response.taskInfo[0].DangerTaskNum

                                    vm.addFrom.DangerTaskLevel = response.taskInfo[0].DangerTaskLevel

                                    if (vm.addFrom.DangerTaskLevel == 'CompanyDangerTask') {
                                        vm.SecondaryCategory = vm.companyCategoryList;
                                    } else {
                                        vm.SecondaryCategory = vm.workshopCategoryList;
                                    }
                                    vm.addFrom.Category = response.taskInfo[0].Category

                                    vm.addFrom.TaskID = TaskID
                                    vm.addFrom.StartTime = response.taskInfo[0].StartTime
                                    vm.addFrom.EndTime = response.taskInfo[0].EndTime
                                    vm.addFrom.uploadfilename = response.taskInfo[0].UploadFileName
                                    vm.addFrom.State = response.taskInfo[0].State
                                    let filenamelist = vm.addFrom.uploadfilename.split('?');
                                    vm.addFrom.filetable = [];
                                    for (let i = 0; i < filenamelist.length; i++) {
                                        if (filenamelist[i] != "") {
                                            vm.addFrom.filetable.push({name: filenamelist[i]});
                                        }
                                    }

                                    leftList = response.FirstInstitutionList
                                    vm.addFrom.leftData = []
                                    for (let i = 0; i < leftList.length; i++) {
                                        vm.addFrom.leftData.push({
                                            key: leftList[i].employeeNum,
                                            label: leftList[i].institutionName
                                        });
                                    }
                                    //vm.leftData = result.FirstInstitutionList
                                    vm.addFrom.rightList = response.taskInfo[0].ApproverList.split(',')

                                    vm.templist = ''
                                    vm.disabled_edit_submit = false;

                                    if (response.taskInfo[0].State == '0') {
                                        vm.editTitle = '修改危险作业申请'
                                    } else if (response.taskInfo[0].State == '4') {
                                        ELEMENT.Message(
                                            {
                                                message: '该申请是被退回的申请，请重新选择正确的审批部门!',
                                                type: 'warning'
                                            }
                                        );
                                        vm.editTitle = '重新修改危险作业申请'
                                    }

                                    vm.tableLoading = false;
                                    vm.editDialogVisible = true;
                                    //vm.correctTransferStyle();
                                },
                                error: handleError

                            });
                        }

                    },
                    error: handleError
                });


            },
            //修改——保存
            _save: function () {
                let self = this;
                this.$refs['addFrom'].validate(function (valid) {
                    self.addFrom.uploadfilename += self.templist
                    if (self.addFrom.rightList[0] == '') {
                        self.addFrom.rightList.splice(0, 1);
                    }
                    /*console.log(JSON.stringify(vm.addFrom.leftData))
                    console.log(JSON.stringify(vm.addFrom.rightList))*/
                    self.addFrom.ApproverList = self.addFrom.rightList.join(",")
                    if (valid) {
                        self.tableLoading = true;
                        $.ajax({
                            url: 'SubmitSave.do',
                            type: "post",
                            data: self.addFrom,
                            success: function (response) {
                                if (response === 'success') {
                                    ELEMENT.Message({
                                        message: '保存成功',
                                        type: 'success'
                                    });
                                    //self.disabled_save = true;
                                    //self.disabled_submit = false;
                                } else {
                                    ELEMENT.Message({
                                        message: '保存失败',
                                        type: 'error'
                                    });
                                }
                                self.getList(self.pageindex, self.conditions);

                            },
                            error: handleError

                        });
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
            //修改界面上的提交
            _submit_edit: function () {
                let vm = this;
                this.$refs['addFrom'].validate(function (valid) {
                    if (valid) {
                        vm.addFrom.uploadfilename += vm.templist


                        if ((vm.addFrom.rightList.length == 0) || (vm.addFrom.rightList == "")) {
                            ELEMENT.Message(
                                {
                                    message: '请先选择审批部门！',
                                    type: 'error'
                                }
                            );
                        } else {
                            let templist = []
                            console.log(JSON.stringify(vm.addFrom.leftData))
                            console.log(JSON.stringify(vm.addFrom.rightList))
                            if (vm.addFrom.rightList[0] == '') {
                                vm.addFrom.rightList.splice(0, 1);
                            }
                            console.log(JSON.stringify(vm.addFrom.leftData))
                            console.log(JSON.stringify(vm.addFrom.rightList))
                            vm.addFrom.ApproverList = vm.addFrom.rightList.join(",")
                            for (let j = 0; j < vm.addFrom.leftData.length; j++) {
                                for (let i = 0; i < vm.addFrom.rightList.length; i++) {
                                    if (vm.addFrom.leftData[j].key == vm.addFrom.rightList[i]) {
                                        templist[i] = vm.addFrom.leftData[j].label
                                    }
                                }
                            }
                            vm.$confirm('审批部门顺序为' + templist.join("——>") + ', 且提交后无法修改，是否继续提交?', '提示', {
                                confirmButtonText: '确定',
                                cancelButtonText: '取消',
                                type: 'warning'
                            }).then(() => {
                                vm.tableLoading = true;
                                vm.disabled_save = true
                                vm.disabled_edit_submit = true
                                vm.taskList = true;
                                $.ajax({
                                    url: 'Edit_SubmitActiviti.do',
                                    type: "post",
                                    data: vm.addFrom,
                                    success: function (response) {
                                        if (response === 'success') {
                                            vm.$message({
                                                type: 'success',
                                                message: '提交成功!'
                                            });
                                            vm.disabled_add = true;
                                            //vm.disabled_submit = true;
                                        } else {
                                            ELEMENT.Message({
                                                message: '提交失败',
                                                type: 'error'
                                            });
                                        }
                                        vm.disabled_save = true
                                        vm.disabled_edit_submit = true
                                        vm.resetForm('addFrom')
                                        //vm.getList(vm.pageindex, vm.conditions);
                                        vm.editDialogVisible = false
                                    },
                                    error: handleError

                                });

                            }).catch(() => {
                                this.$message({
                                    type: 'info',
                                    message: '已取消提交'
                                });
                            });
                        }
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

            //删除
            _delete: function () {
                let vm = this;
                if (vm.idList.length == 0) {
                    ELEMENT.Message(
                        {
                            message: '请先选择要删除的审批任务！',
                            type: 'error'
                        }
                    );
                } else {
                    let flag = true
                    $.ajax({
                        url: 'check',
                        type: "post",
                        data: {
                            TaskID: vm.idList.join(","),
                            tag: vm.tag
                        },
                        dataType: 'json',
                        success: function (result) {
                            let infoList = result.infoList
                            let applicant = result.applicant
                            if (applicant == false) {
                                flag = false
                                ELEMENT.Message(
                                    {
                                        message: '不是申请人，不能删除！',
                                        type: 'error'
                                    }
                                );
                            } else {
                                for (let i = 0; i < infoList.length; i++) {
                                    if (infoList[i].Archived == '1') {
                                        flag = false
                                        ELEMENT.Message(
                                            {
                                                message: '该申请已经归档，不可以删除！',
                                                type: 'error'
                                            }
                                        );
                                        break;
                                    } else if (infoList[i].State == '1' || infoList[i].State == '2' || infoList[i].State == '3' || infoList[i].State == '4') {
                                        flag = false
                                        ELEMENT.Message(
                                            {
                                                message: '该申请已提交，故不允许删除!',
                                                type: 'error'
                                            }
                                        );
                                        break;
                                    }
                                }


                            }
                            if (flag) {
                                //alert("test")
                                vm.$confirm('您确定要删除吗?', '提示', {
                                    confirmButtonText: '确定',
                                    cancelButtonText: '取消',
                                    type: 'warning'
                                }).then(() => {
                                    $.ajax({
                                        url: 'delete',
                                        type: "post",
                                        data: {idList: vm.idList.join(","), tag: 'Web'},
                                        success: function (response) {
                                            if (response === 'success') {
                                                vm.$message({
                                                    type: 'success',
                                                    message: '删除成功!'
                                                });
                                                vm.disabled_add = true;
                                                //vm.disabled_submit = true;
                                            } else {
                                                vm.$message({
                                                    message: '删除失败',
                                                    type: 'error'
                                                });
                                            }
                                            vm.getList(vm.pageindex, vm.conditions);
                                        },
                                        error: handleError

                                    });

                                }).catch(() => {
                                    vm.$message({
                                        type: 'info',
                                        message: '已取消删除'
                                    });
                                });
                            }
                        },
                        error: handleError
                    });
                }
            },


            //详细
            getInfoForDetail: function (TaskID) {
                let vm = this;
                vm.detailDialogVisible = true;
                vm.detailLoading = true;
                $.ajax({
                    url: 'getInfoForDetail.do',
                    type: "post",
                    data: {
                        TaskID: TaskID,
                        tag: vm.tag
                    },
                    dataType: 'json',
                    success: function (result) {
                        let TaskInfo = result.TaskInfo;
                        if (TaskInfo[0].ApplicantName == null) {
                            vm.addFrom.Applicant = TaskInfo[0].Applicant
                        } else {
                            vm.addFrom.Applicant = TaskInfo[0].ApplicantName
                        }

                        vm.addFrom.TaskID = TaskID
                        vm.addFrom.DangerTaskName = TaskInfo[0].DangerTaskName
                        vm.addFrom.ApplyInstitution = TaskInfo[0].InstitutionName
                        vm.addFrom.DangerTaskNum = TaskInfo[0].DangerTaskNum
                        vm.addFrom.DangerTaskLevel = TaskInfo[0].DangerTaskLevel
                        if (vm.addFrom.DangerTaskLevel == 'CompanyDangerTask') {
                            vm.SecondaryCategory = vm.companyCategoryList;
                        } else {
                            vm.SecondaryCategory = vm.workshopCategoryList;
                        }
                        vm.addFrom.Category = TaskInfo[0].Category
                        vm.addFrom.ApplyingTime = TaskInfo[0].ApplyingTime
                        vm.addFrom.StartTime = TaskInfo[0].StartTime
                        vm.addFrom.EndTime = TaskInfo[0].EndTime
                        vm.addFrom.Archived = TaskInfo[0].Archived
                        vm.addFrom.uploadfilename = TaskInfo[0].UploadFileName
                        for (let i = 0; i < vm.taskIDList.length; i++) {
                            if (TaskInfo[0].TaskID == vm.taskIDList[i]) {
                                vm.listindex = i
                            }
                        }
                        let filenamelist = vm.addFrom.uploadfilename.split('?');
                        vm.addFrom.filetable = [];
                        for (let i = 0; i < filenamelist.length; i++) {
                            if (filenamelist[i] != "") {
                                vm.addFrom.filetable.push({name: filenamelist[i]});
                            }
                        }

                        vm.detailLoading = false;
                    },
                    error: handleError
                });
            },
            //上一页、下一页的点击事件
            getDetail: function (index) {
                let vm = this;
                vm.detailDialogVisible = true;
                vm.detailLoading = true;
                vm.listindex = index;
                //alert(index)
                $.ajax({
                    url: 'getInfoForDetail.do',
                    type: "post",
                    data: {
                        TaskID: vm.taskIDList[index],
                        tag: vm.tag
                    },
                    dataType: 'json',
                    success: function (result) {
                        let TaskInfo = result.TaskInfo;
                        if (TaskInfo[0].ApplicantName == null) {
                            vm.addFrom.Applicant = TaskInfo[0].Applicant
                        } else {
                            vm.addFrom.Applicant = TaskInfo[0].ApplicantName
                        }

                        vm.addFrom.TaskID = vm.taskIDList[index]
                        vm.addFrom.DangerTaskName = TaskInfo[0].DangerTaskName
                        vm.addFrom.ApplyInstitution = TaskInfo[0].InstitutionName
                        vm.addFrom.DangerTaskNum = TaskInfo[0].DangerTaskNum
                        vm.addFrom.DangerTaskLevel = TaskInfo[0].DangerTaskLevel
                        if (vm.addFrom.DangerTaskLevel == 'CompanyDangerTask') {
                            vm.SecondaryCategory = vm.companyCategoryList;
                        } else {
                            vm.SecondaryCategory = vm.workshopCategoryList;
                        }
                        vm.addFrom.Category = TaskInfo[0].Category
                        vm.addFrom.ApplyingTime = TaskInfo[0].ApplyingTime
                        vm.addFrom.StartTime = TaskInfo[0].StartTime
                        vm.addFrom.EndTime = TaskInfo[0].EndTime
                        vm.addFrom.Archived = TaskInfo[0].Archived
                        vm.addFrom.uploadfilename = TaskInfo[0].UploadFileName
                        for (let i = 0; i < vm.taskIDList.length; i++) {
                            if (TaskInfo[0].TaskID == vm.taskIDList[i]) {
                                vm.listindex = i
                            }
                        }
                        let filenamelist = vm.addFrom.uploadfilename.split('?');
                        vm.addFrom.filetable = [];
                        for (let i = 0; i < filenamelist.length; i++) {
                            if (filenamelist[i] != "") {
                                vm.addFrom.filetable.push({name: filenamelist[i]});
                            }
                        }

                        vm.detailLoading = false;
                    },
                    error: handleError
                });
            },

            //归档
            _archive: function (TaskID) {
                let vm = this;
                let flag = true
                $.ajax({
                    url: 'check',
                    type: "post",
                    data: {
                        TaskID: TaskID, tag: vm.tag
                    },
                    async: false,
                    dataType: 'json',
                    success: function (result) {
                        let infoList = result.infoList
                        let applicant = result.applicant
                        //alert(applicant)
                        if (applicant == false) {
                            flag = false
                            ELEMENT.Message(
                                {
                                    message: '不是申请人，不能归档！',
                                    type: 'error'
                                }
                            );
                        } else {
                            for (let i = 0; i < infoList.length; i++) {
                                if (infoList[i].Archived == '1') {
                                    flag = false
                                    ELEMENT.Message(
                                        {
                                            message: '该申请已经归档，不需要归档！',
                                            type: 'error'
                                        }
                                    );
                                    break;
                                } else if (infoList[i].State == '0' || infoList[i].State == '1' || infoList[i].State == '4') {
                                    flag = false
                                    ELEMENT.Message(
                                        {
                                            message: '该申请审批未完成，不可以归档！',
                                            type: 'error'
                                        }
                                    );
                                    break;
                                }
                            }

                        }
//允许操作
                        if (flag) {
                            $.ajax({
                                url: 'archive',
                                type: "post",
                                data: {TaskID: TaskID, tag: vm.tag},
                                success: function (response) {
                                    if (response === 'success') {
                                        vm.$message({
                                            type: 'success',
                                            message: '归档成功!'
                                        });
                                    } else {
                                        vm.$message({
                                            message: '归档失败',
                                            type: 'error'
                                        });
                                    }
                                    vm.getList(vm.pageindex, vm.conditions);
                                },
                                error: handleError

                            });
                        }
                    },
                    error: handleError
                });


            },

            //批复记录
            _historyRecord: function (TaskID) {
                let vm = this;
                vm.historyDialogVisible = true;
                vm.historyLoading = true;
                $.ajax({
                    url: 'getHistoryRecord.do',
                    type: "post",
                    data: {TaskID: TaskID, tag: 'Apply'},
                    dataType: 'json',
                    success: function (result) {

                        vm.HistoryRecord = JSON.parse(result[0].data);
                        /* for (let i = 0; i < vm.HistoryRecord.length; i++) {
                             if (vm.HistoryRecord[i].Finished == '0' || vm.HistoryRecord[i].Finished == '3' || vm.HistoryRecord[i].Finished == '4') {
                                 vm.HistoryRecord.splice(i + 1);
                             }
                         }*/
                        if (vm.HistoryRecord.length == 0) {
                            ELEMENT.Message({
                                message: '无历史批复记录',
                                type: 'warning'
                            });
                            vm.historyLoading = false
                            vm.historyDialogVisible = false
                        } else {
                            vm.historyLoading = false
                        }


                    },
                    error: handleError

                });
            },

            _print: function (TaskID) {
                //alert(TaskID)
                let vm = this;
                let flag = true

                $.ajax({
                    url: 'check',
                    type: "post",
                    data: {
                        TaskID: TaskID, tag: vm.tag
                    },
                    async: false,
                    dataType: 'json',
                    success: function (result) {
                        let infoList = result.infoList
                        let applicant = result.applicant
                        //alert(applicant)
                        if (applicant == false) {
                            flag = false
                            ELEMENT.Message(
                                {
                                    message: '不是申请人，不能打印！',
                                    type: 'error'
                                }
                            );
                        } else {
                            for (let i = 0; i < infoList.length; i++) {
                                if (infoList[i].State == '0' || infoList[i].State == '1' || infoList[i].State == '4') {
                                    flag = false
                                    ELEMENT.Message(
                                        {
                                            message: '该申请审批未完成，不可以打印！',
                                            type: 'error'
                                        }
                                    );
                                    break;
                                }
                            }

                        }

                    },
                    error: handleError
                });


                //允许操作
                if (flag) {

                    window.location.href = "print?TaskID=" + TaskID;
                }
            },


            getInfoList2: function () {
                let vm = this;
                /*window.alert("进入了getInfoList方法中, vm.tag:"+vm.tag+" vm.conditions: "+vm.conditions);*/

                $.when($.ajax({
                    url: 'getInfoList.do',
                    type: "post",
                    data: {tag: vm.tag},
                    dataType: 'json',
                    error: handleError
                }), $.ajax({
                    url: 'getList.do',
                    type: "post",
                    data: {
                        conditions: vm.conditions,
                        pageindex: 1,
                        tag: vm.tag
                    },
                    dataType: 'json',
                    error: handleError,
                }))
                    .done((result1, result2) => {
                        //window.alert("done"+result1[0].InstitutionList+"  "+result2[0].taskList);
                        vm.InstitutionList = result1[0].InstitutionList;
                        let companyChildren = []
                        result1[0].CategoryList.CompanyDangerTask.map(function (item, index) {
                            companyChildren.push({
                                value: item.DDItemNum,
                                label: item.DDItemValue
                            })
                        })
                        vm.companyCategoryList = companyChildren
                        vm.CategoryList.push({
                            value: 'CompanyDangerTask',
                            label: '公司级',
                            children: companyChildren
                        });

                        let workshopChildren = []
                        result1[0].CategoryList.WorkShopDangerTask.map(function (item, index) {
                            workshopChildren.push({
                                value: item.DDItemNum,
                                label: item.DDItemValue
                            })
                        })
                        vm.workshopCategoryList = workshopChildren
                        vm.CategoryList.push({
                            value: 'WorkShopDangerTask',
                            label: '分公司级',
                            children: workshopChildren
                        });

                        vm.taskList = result2[0].taskList;
                        vm.pagenum = result2[0].pagenum
                        /* vm.pageindex = pageindex
                         vm.conditions = conditions*/
                        vm.taskIDList = [];
                        for (let i = 0; i < vm.taskList.length; i++) {
                            vm.taskIDList.push(vm.taskList[i].TaskID);

                            if (vm.taskList[i].InstitutionPrefix !== null) {
                                for (let j = 0; j < vm.InstitutionList.length; j++) {

                                    if (vm.taskList[i].TaskInstitution == vm.InstitutionList[j].institutionNum) {

                                        vm.taskList[i].InstitutionName = vm.InstitutionList[j].institutionName
                                    }
                                }

                            }
                        }
                        vm.listsize = vm.taskList.length
                        vm.id_list = vm.taskList.map((val) => '')
                        vm.pageLoading = false;
                        vm.tableLoading = false;
                    })
            },
            getInfoList2Activiti: function () {
                let vm = this;
                /*window.alert("进入了getInfoList方法中, vm.tag:"+vm.tag+" vm.conditions: "+vm.conditions);*/

                $.when($.ajax({
                    url: 'getInfoListActiviti.do',
                    type: "post",
                    data: {tag: vm.tag},
                    dataType: 'json',
                    error: handleError
                }), $.ajax({
                    url: 'getListActiviti.do',
                    type: "post",
                    data: {
                        conditions: vm.conditions,
                        pageindex: 1,
                        tag: vm.tag
                    },
                    dataType: 'json',
                    error: handleError,
                }))
                    .done((result1, result2) => {
                        //window.alert("done"+result1[0].InstitutionList+"  "+result2[0].taskList);
                        vm.InstitutionList = result1[0].InstitutionList;
                        let companyChildren = []
                        result1[0].CategoryList.CompanyDangerTask.map(function (item, index) {
                            companyChildren.push({
                                value: item.DDItemNum,
                                label: item.DDItemValue
                            })
                        })
                        vm.companyCategoryList = companyChildren
                        vm.CategoryList.push({
                            value: 'CompanyDangerTask',
                            label: '公司级',
                            children: companyChildren
                        });

                        let workshopChildren = []
                        result1[0].CategoryList.WorkShopDangerTask.map(function (item, index) {
                            workshopChildren.push({
                                value: item.DDItemNum,
                                label: item.DDItemValue
                            })
                        })
                        vm.workshopCategoryList = workshopChildren
                        vm.CategoryList.push({
                            value: 'WorkShopDangerTask',
                            label: '分公司级',
                            children: workshopChildren
                        });

                        vm.taskList = result2[0].taskList;
                        vm.pagenum = result2[0].pagenum
                        /* vm.pageindex = pageindex
                         vm.conditions = conditions*/
                        vm.taskIDList = [];
                        for (let i = 0; i < vm.taskList.length; i++) {
                            vm.taskIDList.push(vm.taskList[i].TaskID);

                            if (vm.taskList[i].InstitutionPrefix !== null) {
                                for (let j = 0; j < vm.InstitutionList.length; j++) {

                                    if (vm.taskList[i].TaskInstitution == vm.InstitutionList[j].institutionNum) {

                                        vm.taskList[i].InstitutionName = vm.InstitutionList[j].institutionName
                                    }
                                }

                            }
                        }
                        vm.listsize = vm.taskList.length
                        vm.id_list = vm.taskList.map((val) => '')
                        vm.pageLoading = false;
                        vm.tableLoading = false;
                    })
            },
            getList: function (pageindex, conditions) {
                let vm = this;
                vm.pageindex = pageindex || 1;
                if (conditions !== undefined) {
                    vm.conditions = conditions;
                }
                vm.tableLoading = true;
                $.ajax({
                    url: 'getList.do',
                    type: "post",
                    data: {
                        conditions: vm.conditions,
                        pageindex: vm.pageindex,
                        tag: vm.tag
                    },
                    dataType: 'json',

                    success: function (result) {
                        vm.taskList = result.taskList;
                        vm.pagenum = result.pagenum
                        vm.taskIDList = [];
                        for (let i = 0; i < vm.taskList.length; i++) {
                            vm.taskIDList.push(vm.taskList[i].TaskID);

                            if (vm.taskList[i].InstitutionPrefix !== null) {
                                for (let j = 0; j < vm.InstitutionList.length; j++) {

                                    if (vm.taskList[i].TaskInstitution == vm.InstitutionList[j].institutionNum) {

                                        vm.taskList[i].InstitutionName = vm.InstitutionList[j].institutionName
                                    }
                                }

                            }
                        }
                        vm.listsize = vm.taskList.length
                        vm.id_list = vm.taskList.map((val) => '')
                        vm.pageLoading = false;
                        vm.tableLoading = false;
                    },
                    error: handleError
                });
            },
            getListActiviti: function (pageindex, conditions) {
                let vm = this;
                vm.pageindex = pageindex || 1;
                if (conditions !== undefined) {
                    vm.conditions = conditions;
                }
                vm.tableLoading = true;
                $.ajax({
                    url: 'getListActiviti.do',
                    type: "post",
                    data: {
                        conditions: vm.conditions,
                        pageindex: vm.pageindex,
                        tag: vm.tag
                    },
                    dataType: 'json',

                    success: function (result) {
                        vm.taskList = result.taskList;
                        vm.pagenum = result.pagenum
                        vm.taskIDList = [];
                        for (let i = 0; i < vm.taskList.length; i++) {
                            vm.taskIDList.push(vm.taskList[i].TaskID);

                            if (vm.taskList[i].InstitutionPrefix !== null) {
                                for (let j = 0; j < vm.InstitutionList.length; j++) {

                                    if (vm.taskList[i].TaskInstitution == vm.InstitutionList[j].institutionNum) {

                                        vm.taskList[i].InstitutionName = vm.InstitutionList[j].institutionName
                                    }
                                }

                            }
                        }
                        vm.listsize = vm.taskList.length
                        vm.id_list = vm.taskList.map((val) => '')
                        vm.pageLoading = false;
                        vm.tableLoading = false;
                    },
                    error: handleError
                });
            },
            correctTransferStyle: function () {

                $('.el-transfer').css('marginLeft', ($('.dialog-fieldset-select').width() / 2 - ($('.el-transfer-panel').width() + $('.el-transfer__buttons').width() / 2 + 30)) + 'px');
            },

            myFunction: function(param){
                let vm = this;
                vm.pageLoading = false;
                vm.tableLoading = false;
                window.alert("myfunction"+param);
            }

        },

        mounted() {
            $('.el-transfer').css('marginLeft', ($('.dialog-fieldset-select').width() / 2 - ($('.el-transfer-panel').width() + $('.el-transfer__buttons').width() / 2 + 30)) + 'px');
        },
        created: function () {
            this.tableLoading = true;
            //this.getInfoList2("AQB");
            let userInstitution = sessionStorage.getItem("userInstitution");
            this.getInfoList2Activiti(userInstitution);
        }
    });
})
