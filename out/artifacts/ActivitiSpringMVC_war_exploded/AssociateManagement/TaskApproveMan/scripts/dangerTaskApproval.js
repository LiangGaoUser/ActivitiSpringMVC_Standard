$(function () {

    new Vue({
        el: "#right",
        data: {
            InstitutionList: [],
            approvalList: [],

            templist: '',
            txtLabel: '',
            tag: 'Web',

            pagenum: 0,
            pageindex: 0,
            conditions: '',
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
            id_list: [], //多选选中的id数组，以,隔开

            pageLoading: false,
            addLoading: false,
            tableLoading: false,
            addDialogVisible: false,
            disabled_add: false,
            disabled_submit: true,

            approveDialogVisible: false,
            approveLoading: false,
            disabled_Finished: true,
            disabled_nextInstitution: true,
            disabled_approval_save: false,
            activeNames: [''],

            callbackDialogVisible: false,
            callbackLoading: false,
            disabled_callback_save: false,

            relayDialogVisible: false,
            relayLoading: false,
            disabled_relay_save: false,
            relayPeopleList: [],

            historyDialogVisible: false,
            historyLoading: false,
            HistoryRecord: [],

            leftData: [],
            rightList: [],
            TaskInstitutionList: [],
            FirstInstitutionList: [],
            CategoryList: [],     //危险作业类型集合  ——查询框
            SecondaryCategory:[],  //  危险作业类型 ——详细界面
            companyCategoryList:[],  //公司级危险作业类型集合
            workshopCategoryList:[],  //车间级危险作业类型集合
            pickerOptions1: {
                disabledDate(time) {
                    return time.getTime() <= Date.now();
                },
            },
            params: [{
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
                }],
            callbackFrom: {
                ApprovalID: '',
                SubmitterID: '',
                Submitter: '',              //申请人或提交人
                Applicant: '',
                ApproveSuggestion: '发错审批部门',        //退回原因
                Finished: '',
                tag: 'Web',
            },
            relayFrom: {
                ApprovalID: '',
                TaskID: '',
                SubmitterID: '',
                relayPeople: '',            //被转发人ID
                ApproveSuggestion: '请代为处理！',        //退回原因
                Finished: '',
                Approver: '',
                ApproverList: [],
                tag: 'Web',
            },
            approveFrom: {
                TaskID: '',
                ApprovalID: '',
                Applicant: '',              //申请人
                DangerTaskNum:'',           //危险作业编号
                DangerTaskName: '',
                ApplyInstitution: '',       //申请单位
                Archived: '0',
                DangerTaskLevel:'',      //危险作业级别
                Category: '',
                ApproveTime: new Date(),
                StartTime: '',
                EndTime: '',
                uploadfilename: '',
                filetable: [],
                ApproverList: [],

                imageUrl:'',
                //imageUrl:'https://fuss10.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg',

                Approver: '',         //审批人
                ApproveInstitution: '',     //审批单位
                ApprovalResult: '',
                ApprovalFinished: '',
                ApproveInstitutionList: [],
                nextApproveInstitution: [],
                nextApproveInstitutionList: '',
                ApproveSuggestion: '',
                flag: false,    //标志当前审批人是否为ApproverList的最后一位
                tag: 'Web',

                Finished:'',
            },
            rules: {
                ApproveSuggestion: [
                    {required: true, message: '请输入退回原因！', trigger: 'blur'}
                ],
            },
            rulesForRelay: {
                //请选择被转发人
                relayPeople: [
                    {required: true, message: '请选择转发对象！', trigger: 'blur'}
                ],

                ApproveSuggestion: [
                    {required: true, message: '请输入转发理由！', trigger: 'blur'}
                ],

            },
            rulesForApproval: {

                ApprovalResult: [
                    {required: true, message: '请选择审批结果！', trigger: 'blur'}
                ],
                ApprovalFinished: [
                    {required: true, message: '请选择是否结束！', trigger: 'blur'}
                ],
                //转发理由
                ApproveSuggestion: [
                    {required: true, message: '请输入审批意见！', trigger: 'blur'}
                ],

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
                                if(col.label == '作业类型：')
                                {
                                    params.push(` DangerTaskLevel = '${col.value[0]}' `);
                                    params.push(` ${col.model} = '${col.value[1]}' `);
                                }
                                else {
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
                this.getList(1,params.join('and'))
            },

            closeFrom(formName) {
                //this.clearValidate(formName);
                //this.$refs[formName].clearValidate();
                // this.$refs[formName].resetFields();
                let vm = this;
                vm.fileList = "";
                vm.filelist = "";
                vm.$refs['my-upload'].clearFiles();
                console.log('XX1XXX1');
            },
            beforeAvatarUpload(file) {
                const isLt500M = file.size / 1024 / 1024 < 500;
                if (!isLt500M) {
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
            removeFile1(file, fileList) {
                this.templist = "";
                for (j = 0; j < fileList.length; j++) {
                    this.templist += fileList[j].name;
                    this.templist += '?';
                }


            },

            _downloadone: function (ID, name) {
                httpPost('download', {
                    filename: name,
                    id: ID
                })
            },
            _viewFile: function (ID, name) {
                let self = this;
                const loading = this.$loading({
                    lock: true,
                    text: '加载中...',
                    spinner: 'el-icon-loading',
                    background: 'rgba(0, 0, 0, 0.7)'
                });
                $.ajax({
                    url: 'viewFile',
                    type: "post",
                    data: {
                        filename: name,
                        id: ID,
                        tag:'Web'
                    },
                    //contentType: 'application/x-www-form-urlencoded',
                    success: function (data) {
                        if (data != "") {
                            var result = JSON.parse(data);
                            loading.close();
                            self.filePath = '../../' + result.filepath;
                            if (result.type == 'mp4' || result.type == 'webm' || result.type == 'ogg') {
                                self.filetype = 'video/' + result.type;
                                self.viewDialogVisible1 = true;
                                //   var url = '../../' + result.filepath
                                var Player = videojs("videoex");  //初始化视频
                                Player.src(self.filePath);  //重置video的src
                                Player.load(self.filePath);  //使video重新加载
                                Player.play();
                            } else if (result.type == 'pdf'|| result.type == 'html') {
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
                            }
                        } else {
                            loading.close();
                            self.$message({
                                type: 'info',
                                message: '加载失败!'
                            })
                        }
                    },
                   error: handleError

                });
            },
            closevideoDialog: function () {

                var Player = videojs("videoex");  //初始化视频

                Player.pause();

            },
            closevideoDialog1: function () {
                var music = document.getElementById('audio');
                music.pause();


            },

            handleChange(val) {
                console.log(val);
            },

            //审批——加载信息
            getInfoForApprove: function (ApprovalID) {
                let vm = this;
                vm.disabled_approval_save=false;
                vm.approveDialogVisible = true;
                vm.approveLoading = true;
                vm.approveFrom.TaskID = ''
                vm.approveFrom.ApprovalID = ''
                vm.approveFrom.Applicant = ''
                vm.approveFrom.DangerTaskNum = ''
                vm.approveFrom.DangerTaskName = ''
                vm.approveFrom.DangerTaskLevel = ''
                vm.approveFrom.ApplyInstitution = ''
                vm.approveFrom.Archived = '0'
                vm.approveFrom.Category = ''
                vm.approveFrom.ApproveTime = new Date()
                vm.approveFrom.StartTime = ''
                vm.approveFrom.EndTime = ''
                vm.approveFrom.uploadfilename = ''
                vm.approveFrom.filetable = []
                vm.approveFrom.ApproverList = [],

                    vm.approveFrom.Approver = '',         //审批人
                    vm.approveFrom.ApproveInstitution = '',     //审批单位
                    vm.approveFrom.ApprovalResult = '',
                    vm.approveFrom.ApprovalFinished = '',
                    vm.approveFrom.ApproveInstitutionList = [],
                    vm.approveFrom.nextApproveInstitution = [],
                    vm.approveFrom.nextApproveInstitutionList = '',
                    vm.approveFrom.ApproveSuggestion = '',
                    vm.approveFrom.imageUrl=''
                    vm.approveFrom.Finished=''
                    $.ajax({
                        url: 'ApproveInfo.do',
                        type: "post",
                        data: {ApprovalID: ApprovalID, tag: vm.tag},
                        dataType: 'json',
                        success: function (result) {
                            let TaskInfo = result.ApplyInfo;

                            vm.approveFrom.ApprovalID = ApprovalID
                            vm.approveFrom.TaskID = TaskInfo[0].TaskID
                            vm.approveFrom.Applicant = TaskInfo[0].ApplicantName
                            vm.approveFrom.DangerTaskNum = TaskInfo[0].DangerTaskNum
                            vm.approveFrom.DangerTaskName = TaskInfo[0].DangerTaskName
                            vm.approveFrom.ApplyInstitution = TaskInfo[0].InstitutionName
                            vm.approveFrom.DangerTaskLevel = TaskInfo[0].DangerTaskLevel
                            if(vm.approveFrom.DangerTaskLevel=='CompanyDangerTask')
                            {
                                vm.SecondaryCategory = vm.companyCategoryList ;
                            }
                            else
                            {
                                vm.SecondaryCategory = vm.workshopCategoryList;
                            }
                            vm.approveFrom.Category = TaskInfo[0].Category
                            vm.approveFrom.ApplyingTime = TaskInfo[0].ApplyingTime
                            vm.approveFrom.StartTime = TaskInfo[0].StartTime
                            vm.approveFrom.EndTime = TaskInfo[0].EndTime
                            vm.approveFrom.Archived = TaskInfo[0].Archived
                            vm.approveFrom.uploadfilename = TaskInfo[0].UploadFileName
                            vm.approveFrom.ApproverList = TaskInfo[0].ApproverList.split(',')

                            if(result.WaterMarkSignPath=='')
                            {
                                vm.approveFrom.imageUrl = '../../Images/white.jpg'
                            }
                            else
                            {
                                vm.approveFrom.imageUrl = '../../'+result.WaterMarkSignPath
                            }


                            vm.approveFrom.Approver = result.username
                            vm.approveFrom.ApproveInstitutionList = result.InstitutionList
                            for (let i = 0; i < vm.approveFrom.ApproveInstitutionList.length; i++) {
                                if (result.UserInstitution == vm.approveFrom.ApproveInstitutionList[i]) {
                                    vm.approveFrom.ApproveInstitutionList.splice(i, 1);
                                }
                            }
                            vm.approveFrom.ApproveInstitution = result.UserInstitutionName
                            vm.approveFrom.Finished = result.Finished
                            if(result.Finished=='3')    //被退回的审批
                            {
                                vm.disabled_Finished = false;
                                vm.disabled_nextInstitution = false;
                                //vm.approveFrom.flag = true;
                                vm.approveFrom.ApprovalFinished = ''
                                ELEMENT.Message(
                                    {
                                        message: '由于上次的提交被退回，请重新审批!',
                                        type: 'warning'
                                    }
                                );
                            }
                            else {
                                for (let i = 0; i < vm.approveFrom.ApproverList.length; i++) {
                                    if (result.usernum == vm.approveFrom.ApproverList[i]) {
                                        if ((i == vm.approveFrom.ApproverList.length - 1) ||(i==vm.approveFrom.ApproverList.length - 2&&vm.approveFrom.ApproverList[i+1]==''))   //当前审批人是ApproverList的最后一个
                                        {
                                            vm.disabled_Finished = false;
                                            vm.disabled_nextInstitution = false;
                                            vm.approveFrom.flag = true;
                                            vm.approveFrom.ApprovalFinished = ''
                                            break;
                                        } else {
                                            vm.disabled_Finished = true;
                                            vm.disabled_nextInstitution = true;
                                            vm.approveFrom.flag = false;
                                            vm.approveFrom.ApprovalFinished = '0'
                                            break;
                                        }
                                    }
                                }
                            }

                            let filenamelist = vm.approveFrom.uploadfilename.split('?');
                            vm.approveFrom.filetable = [];
                            for (let i = 0; i < filenamelist.length; i++) {
                                if (filenamelist[i] != "") {
                                    vm.approveFrom.filetable.push({name: filenamelist[i]});
                                }
                            }

                            vm.approveLoading = false
                        },
                       error: handleError

                    });

            },
            ApprovalFinished_Change:function(){              //是否结束
                let vm=this;
                if(vm.disabled_Finished==false&&vm.approveFrom.ApprovalFinished=='2')    //结束
                {
                    vm.disabled_nextInstitution=true
                    vm.approveFrom.nextApproveInstitution=[]
                    //vm.approveFrom.nextApproveInstitution=[]
                }
                else {                                                                  //  不结束
                    vm.disabled_nextInstitution=false
                    //vm.approveFrom.nextApproveInstitution=[]
                }
            },

            ApprovalResultChange(){             //审批结果
                let vm =this;
                if(vm.approveFrom.ApprovalResult==0)          //不同意
                {
                    vm.approveFrom.ApprovalFinished='2'
                    vm.disabled_Finished=true
                    vm.disabled_nextInstitution=true
                    vm.approveFrom.nextApproveInstitution=[]
                }
                else {                                          //  同意
                    vm.approveFrom.ApprovalFinished='0'
                    vm.approveFrom.nextApproveInstitution=[]
                    //debugger
                    if(vm.approveFrom.flag===true||vm.approveFrom.Finished=='3')                  //当前审批人是ApproverList的最后一个
                    {
                        vm.disabled_Finished=false
                        vm.disabled_nextInstitution=false
                    }
                    else {
                        vm.disabled_Finished=true
                        vm.disabled_nextInstitution=true
                    }

                }
            },
            //审批——提交
            ApprovalSubmit: function () {
                let vm = this;
                vm.$refs['ApprovalRuleFrom'].validate(function (valid) {
                    if (valid) {
                        if (vm.approveFrom.ApprovalFinished == '0' && vm.disabled_Finished == false && vm.approveFrom.nextApproveInstitution.length == 0) {
                            ELEMENT.Message({
                                message: '请指定下一审批部门！',
                                type: 'warning'
                            });
                        } else if (vm.disabled_nextInstitution == true || vm.disabled_Finished == true) {
                            //vm.approveFrom.nextApproveInstitutionList = vm.approveFrom.nextApproveInstitution.join(',')
                            vm.disabled_approval_save = true;
                            vm.tableLoading = true;
                            $.ajax({
                                url: 'ApprovalSubmit.do',
                                type: "post",
                                data: vm.approveFrom,
                                success: function (response) {
                                    if (response === 'success') {
                                        ELEMENT.Message({
                                            message: '审批成功！',
                                            type: 'success'
                                        });
                                        vm.disabled_approval_save = true;
                                    } else {
                                        ELEMENT.Message({
                                            message: '审批失败！',
                                            type: 'error'
                                        });
                                        vm.disabled_approval_save = false;
                                    }
                                    vm.getList(vm.pageindex,vm.conditions);
                                    vm.approveDialogVisible=false
                                },
                               error: handleError

                            });
                        } else {


                            let templist = []
                            console.log(JSON.stringify(vm.approveFrom.ApproveInstitutionList))
                            console.log(JSON.stringify(vm.approveFrom.nextApproveInstitution))
                            for (let j = 0; j < vm.approveFrom.ApproveInstitutionList.length; j++) {
                                for (let i = 0; i < vm.approveFrom.nextApproveInstitution.length; i++) {
                                    if (vm.approveFrom.ApproveInstitutionList[j].employeeNum == vm.approveFrom.nextApproveInstitution[i]) {
                                        templist[i] = vm.approveFrom.ApproveInstitutionList[j].institutionName
                                    }
                                }
                            }
                            vm.$confirm('审批部门顺序为' + templist.join("——>") + ', 且提交后无法修改，是否继续提交?', '提示', {
                                confirmButtonText: '确定',
                                cancelButtonText: '取消',
                                type: 'warning'
                            }).then(() => {
                                vm.tableLoading = true;
                                vm.approveFrom.nextApproveInstitutionList = vm.approveFrom.nextApproveInstitution.join(',')
                                $.ajax({
                                    url: 'ApprovalSubmit',
                                    type: "post",
                                    data: vm.approveFrom,
                                    success: function (response) {
                                        if (response === 'success') {
                                            ELEMENT.Message({
                                                message: '审批成功',
                                                type: 'success'
                                            });
                                            vm.disabled_approval_save = true;
                                        } else {
                                            ELEMENT.Message({
                                                message: '审批失败',
                                                type: 'error'
                                            });
                                        }
                                        vm.getList( vm.pageindex,vm.conditions);
                                        vm.approveDialogVisible=false
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

            //退回
            callback: function (ApprovalID) {
                let vm = this;
                vm.callbackDialogVisible = true;
                vm.callbackLoading = true;
                vm.callbackFrom.ApprovalID = ''
                vm.callbackFrom.SubmitterID = ''
                vm.callbackFrom.Submitter = ''
                vm.callbackFrom.Applicant = ''
                vm.callbackFrom.Finished = ''
                vm.callbackFrom.ApproveSuggestion = '发错审批部门'
                $.ajax({
                    url: 'callbackInfo.do',
                    type: "post",
                    data: {ApprovalID: ApprovalID, tag: vm.tag},
                    dataType: 'json',
                    success: function (result) {
                        vm.callbackFrom.ApprovalID = ApprovalID
                        vm.callbackFrom.SubmitterID = result.SubmitterInfo[0].SubmitterID
                        vm.callbackFrom.Submitter = result.SubmitterInfo[0].SubmitterName
                        vm.callbackFrom.Applicant = result.SubmitterInfo[0].Applicant
                        vm.callbackFrom.Finished = result.SubmitterInfo[0].Finished
                        if (vm.callbackFrom.Applicant == vm.callbackFrom.SubmitterID) {
                            vm.txtLabel = '退回申请人：'
                        } else {
                            vm.txtLabel = '退回提交人：'
                        }
                        vm.callbackLoading = false
                    },
                   error: handleError

                });

            },

            //退回——保存
            _save: function () {
                let vm = this;
                vm.$refs['callbackRuleFrom'].validate(function (valid) {
                    if (valid) {
                        vm.tableLoading = true;
                        vm.disabled_callback_save = true;
                        $.ajax({
                            url: 'callbackSave.do',
                            type: "post",
                            data: vm.callbackFrom,
                            success: function (response) {
                                if (response === 'success') {
                                    ELEMENT.Message({
                                        message: '退回成功',
                                        type: 'success'
                                    });
                                    vm.disabled_callback_save = true;
                                } else {
                                    ELEMENT.Message({
                                        message: '退回失败',
                                        type: 'error'
                                    });
                                    vm.disabled_callback_save = false;
                                }
                                vm.getList(vm.pageindex,vm.conditions);
                                vm.callbackDialogVisible=false
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


            //转发
            relay: function () {
                let vm = this;
                if(vm.idList.length>0)
                {
                    vm.relayDialogVisible = true;
                    vm.relayLoading = true;
                    /*vm.relayFrom.ApprovalID = ''
                    vm.relayFrom.TaskID = ''
                    vm.relayFrom.Applicant = ''
                    vm.relayFrom.SubmitterID = ''
                    vm.relayFrom.relayPeople = ''
                    vm.relayFrom.ApproverList = []
                    vm.relayFrom.Finished = ''*/
                    vm.relayFrom.ApproveSuggestion = '请代为处理！'
                    vm.disabled_relay_save = false;
                    $.ajax({
                        url: 'relayInfo.do',
                        type: "post",
                        data: {tag: vm.tag},
                        dataType: 'json',
                        success: function (result) {
                            /* vm.relayFrom.ApprovalID = ApprovalID
                             vm.relayFrom.TaskID = result.taskInfo.TaskID
                             vm.relayFrom.SubmitterID = result.taskInfo.SubmitterID
                             vm.relayFrom.ApproverList = result.taskInfo.ApproverList.split(',')
                             vm.relayFrom.Applicant = result.taskInfo.Applicant
                             vm.relayFrom.Finished = result.taskInfo.Finished*/

                            vm.relayPeopleList = result.relayPeopleList

                            vm.relayLoading = false
                        },
                       error: handleError

                    });
                }
                else {
                    ELEMENT.Message(
                        {
                            message: '请先选择待转发的作业！',
                            type: 'error'
                        }
                    );
                }
            },

            //转发——保存
            _relay_save: function () {
                let vm = this;
                vm.$refs['relayRuleFrom'].validate(function (valid) {
                    if (valid) {
                       /* if (vm.relayFrom.Finished = '0')     //待批
                        {
                            for (let i = 0; i < vm.relayFrom.ApproverList; i++) {
                                if (vm.relayFrom.ApproverList[i] == vm.relayFrom.Applicant) {
                                    vm.relayFrom.ApproverList.splice(i + 1, 0, vm.relayFrom.relayPeople);
                                    break;
                                }
                            }
                        } else {                      //被退回  Finished=3
                            for (let i = 0; i < vm.relayFrom.ApproverList; i++) {
                                if (vm.relayFrom.ApproverList[i] == vm.relayFrom.Applicant) {
                                    vm.relayFrom.ApproverList.splice(i + 1);
                                    vm.relayFrom.ApproverList.splice(i + 1, 0, vm.relayFrom.relayPeople);
                                    break;
                                }
                            }
                        }*/

                        //vm.relayFrom.Approver = vm.relayFrom.ApproverList.join(',')
                        vm.tableLoading = true;
                        vm.disabled_relay_save = true;
                        $.ajax({
                            url: 'relaySave.do',
                            type: "post",
                            data: {ApprovalIDList:vm.idList.join(','),
                                   relayPeople:  vm.relayFrom.relayPeople,
                                   ApproveSuggestion:  vm.relayFrom.ApproveSuggestion,
                                    tag:'Web'},
                            success: function (response) {
                                if (response === 'success') {
                                    ELEMENT.Message({
                                        message: '转发成功',
                                        type: 'success'
                                    });
                                    vm.disabled_relay_save = true;
                                } else {
                                    ELEMENT.Message({
                                        message: '转发失败',
                                        type: 'error'
                                    });
                                    vm.disabled_relay_save = false;
                                }
                                vm.getList( vm.pageindex,vm.conditions);
                                vm.relayDialogVisible=false
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


            //批复记录
            _historyRecord: function (ApprovalID) {
                let vm = this;
                vm.historyDialogVisible = true;
                vm.historyLoading = true;
                $.ajax({
                    url: '../DangerTaskApplyMan/getHistoryRecord.do',
                    type: "post",
                    data: {ApprovalID: ApprovalID, tag: 'Approve'},
                    dataType: 'json',
                    success: function (result) {
                        vm.HistoryRecord = JSON.parse(result[0].data);
                        /*for(let i=0;i<vm.HistoryRecord.length;i++)
                        {
                            if(vm.HistoryRecord[i].Finished=='0'||vm.HistoryRecord[i].Finished=='3'||vm.HistoryRecord[i].Finished=='4')
                            {
                                vm.HistoryRecord.splice(i+1);
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


           /* getInfoList: function () {
                let vm = this;
                $.ajax({
                    url: 'getInfoList',
                    type: "post",
                    data: {tag: vm.tag},
                    dataType: 'json',
                    success: function (result) {
                        vm.InstitutionList = result.InstitutionList;
                        let companyChildren = []
                        result.CategoryList.CompanyDangerTask.map(function (item,index) {
                            companyChildren.push({
                                value:item.DDItemNum,
                                label:item.DDItemValue
                            })
                        })
                        vm.companyCategoryList = companyChildren
                        vm.CategoryList.push({
                            value: 'CompanyDangerTask',
                            label: '公司级',
                            children:companyChildren
                        });

                        let workshopChildren = []
                        result.CategoryList.WorkShopDangerTask.map(function (item,index) {
                            workshopChildren.push({
                                value:item.DDItemNum,
                                label:item.DDItemValue
                            })
                        })
                        vm.workshopCategoryList = workshopChildren
                        vm.CategoryList.push({
                            value: 'WorkShopDangerTask',
                            label: '分公司级',
                            children:workshopChildren
                        });
                    },
                   error: handleError
                });
            }
            ,*/
            getInfoList: function () {
                window.alert("进入了getInfoList")
                let vm = this;
                vm.tableLoading = true;
                $.when($.ajax({
                    url: 'getInfoList.do',
                    type: "post",
                    data: {tag: vm.tag},
                    dataType: 'json',
                    error: handleError
                }), $.ajax({
                    url: 'getApprovalList.do',
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
                        vm.InstitutionList = result1[0].InstitutionList;
                        let companyChildren = []
                        result1[0].CategoryList.CompanyDangerTask.map(function (item,index) {
                            companyChildren.push({
                                value:item.DDItemNum,
                                label:item.DDItemValue
                            })
                        })
                        vm.companyCategoryList = companyChildren
                        vm.CategoryList.push({
                            value: 'CompanyDangerTask',
                            label: '公司级',
                            children:companyChildren
                        });

                        let workshopChildren = []
                        result1[0].CategoryList.WorkShopDangerTask.map(function (item,index) {
                            workshopChildren.push({
                                value:item.DDItemNum,
                                label:item.DDItemValue
                            })
                        })
                        vm.workshopCategoryList = workshopChildren
                        vm.CategoryList.push({
                            value: 'WorkShopDangerTask',
                            label: '分公司级',
                            children:workshopChildren
                        });

                        vm.approvalList = result2[0].approvalList;
                        //alert(JSON.stringify(vm.approvalList))
                        vm.pagenum = result2[0].pagenum
                        /*   vm.pageindex = pageindex
                           vm.conditions = conditions*/
                        vm.taskIDList = [];
                        for (let i = 0; i < vm.approvalList.length; i++) {
                            vm.taskIDList.push(vm.approvalList[i].ApprovalID);
                            if(vm.approvalList[i].InstitutionPrefix!==null)
                            {
                                for(let j=0;j<vm.InstitutionList.length;j++)
                                {
                                    if(vm.approvalList[i].TaskInstitution==vm.InstitutionList[j].institutionNum)
                                    {
                                        vm.approvalList[i].ApplicantInstitutionName=vm.InstitutionList[j].institutionName
                                    }
                                }

                            }
                        }
                        vm.listsize = vm.approvalList.length
                        vm.id_list = vm.approvalList.map((val) => '')
                        vm.pageLoading = false;
                        vm.tableLoading = false;
                    })
            },

            getList: function (pageindex,conditions) {
                let vm = this;
                vm.pageindex = pageindex || 1;
                if(conditions!==undefined)
                {
                    vm.conditions = conditions;
                }
                vm.tableLoading = true;
                $.ajax({
                    url: 'getApprovalList.do',
                    type: "post",
                    data: {
                        conditions:vm.conditions,
                        pageindex:vm.pageindex,
                        tag: vm.tag
                    },
                    dataType: 'json',
                    success: function (result) {
                        vm.approvalList = result.approvalList;
                        //alert(JSON.stringify(vm.approvalList))
                        vm.pagenum = result.pagenum
                     /*   vm.pageindex = pageindex
                        vm.conditions = conditions*/
                        vm.taskIDList = [];
                        for (let i = 0; i < vm.approvalList.length; i++) {
                            vm.taskIDList.push(vm.approvalList[i].ApprovalID);
                            if(vm.approvalList[i].InstitutionPrefix!==null)
                            {
                                for(let j=0;j<vm.InstitutionList.length;j++)
                                {
                                    if(vm.approvalList[i].TaskInstitution==vm.InstitutionList[j].institutionNum)
                                    {
                                        vm.approvalList[i].ApplicantInstitutionName=vm.InstitutionList[j].institutionName
                                    }
                                }

                            }
                        }
                        vm.listsize = vm.approvalList.length
                        vm.id_list = vm.approvalList.map((val) => '')
                        vm.pageLoading = false;
                        vm.tableLoading = false;
                    },
                   error: handleError
                });
            }
            ,
        },
        created: function () {

            this.getInfoList();
        },
    });
})
