$(function () {

    new Vue({
        el: ".right",
        data: {
            InstitutionList: [],
            approvalList: [],

            templist: '',
            txtLabel: '',
            tag: 'Web',

            pagenum: 0,
            pageindex: 0,
            conditions: '',
            listindex: '',
            listsize: '',
            id_list: [], //多选选中的id数组，以,隔开
            fileList: [],
            filedata: {id: 'add'},
            taskIDList: [],

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

            pageLoading: false,
            addLoading: false,
            tableLoading: false,


            detailDialogVisible: false,
            detailLoading: false,
            activeNames: [''],


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
            approveFrom: {
                TaskID:'',
                ApprovalID: '',
                Applicant: '',              //申请人
                DangerTaskNum:'',           //危险作业编号
                DangerTaskLevel:'',      //危险作业级别
                DangerTaskName: '',
                ApplyInstitution: '',       //申请单位
                Archived: '0',
                Category: '',
                StartTime: '',
                EndTime: '',
                uploadfilename: '',
                filetable: [],

                imageUrl: '',
                //imageUrl:'https://fuss10.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg',

                Approver: '',         //审批人
                ApproveInstitution: '',     //审批单位
                ApprovalResult: '',
                ApproveSuggestion: '',
                ApproveTime: '',
                tag: 'Web',
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

            handleChange(val) {
                console.log(val);
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
                debugger
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
                        id: ID
                    },
                    contentType: 'application/x-www-form-urlencoded',
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


            //详细——加载信息
            getInfoForDetail: function (ApprovalID) {
                let vm = this;
                vm.detailDialogVisible = true;
                vm.detailLoading = true;
                vm.approveFrom.TaskID = ''
                vm.approveFrom.ApprovalID = ''
                vm.approveFrom.Applicant = ''
                vm.approveFrom.DangerTaskNum = ''
                vm.approveFrom.DangerTaskName = ''
                vm.approveFrom.DangerTaskLevel = ''
                vm.approveFrom.ApplyInstitution = ''
                vm.approveFrom.Archived = '0'
                vm.approveFrom.Category = ''
                vm.approveFrom.StartTime = ''
                vm.approveFrom.EndTime = ''
                vm.approveFrom.uploadfilename = ''
                vm.approveFrom.filetable = []

                vm.approveFrom.Approver = '',         //审批人
                    vm.approveFrom.ApproveInstitution = '',     //审批单位
                    vm.approveFrom.ApprovalResult = '',
                    vm.approveFrom.ApproveSuggestion = '',
                    vm.approveFrom.ApproveTime = '',
                    vm.approveFrom.imageUrl = ''
                $.ajax({
                    url: 'ApproveInfoDetail',
                    type: "post",
                    data: {ApprovalID: ApprovalID, tag: vm.tag},
                    dataType: 'json',
                    success: function (result) {
                        let TaskInfo = result.ApplyInfo;
                        vm.approveFrom.TaskID = TaskInfo[0].TaskID
                        vm.approveFrom.ApprovalID = ApprovalID
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
                        let filenamelist = vm.approveFrom.uploadfilename.split('?');
                        vm.approveFrom.filetable = [];
                        for (let i = 0; i < filenamelist.length; i++) {
                            if (filenamelist[i] != "") {
                                vm.approveFrom.filetable.push({name: filenamelist[i]});
                            }
                        }

                        for (let i = 0; i < vm.taskIDList.length; i++) {
                            if (ApprovalID == vm.taskIDList[i]) {
                                vm.listindex = i
                            }
                        }


                        vm.approveFrom.Approver = result.username
                        vm.approveFrom.ApproveInstitution = result.UserInstitutionName

                        let ApprovalInfo = result.ApprovalInfo
                        vm.approveFrom.ApprovalResult=ApprovalInfo[0].ApproveResult
                        vm.approveFrom.ApproveTime = ApprovalInfo[0].ApproveTime
                        vm.approveFrom.ApproveSuggestion = ApprovalInfo[0].ApproveSuggestion

                        if(ApprovalInfo[0].ApproveSignature=='')
                        {
                            vm.approveFrom.imageUrl = '../../Images/white.jpg'
                        }
                        else
                        {
                            vm.approveFrom.imageUrl = ApprovalInfo[0].ApproveSignature
                        }

                        vm.detailLoading = false
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
                vm.approveFrom.TaskID = ''
                vm.approveFrom.ApprovalID = ''
                vm.approveFrom.Applicant = ''
                vm.approveFrom.DangerTaskNum = ''
                vm.approveFrom.DangerTaskName = ''
                vm.approveFrom.DangerTaskLevel = ''
                vm.approveFrom.ApplyInstitution = ''
                vm.approveFrom.Archived = '0'
                vm.approveFrom.Category = ''
                vm.approveFrom.StartTime = ''
                vm.approveFrom.EndTime = ''
                vm.approveFrom.uploadfilename = ''
                vm.approveFrom.filetable = []

                vm.approveFrom.Approver = '',         //审批人
                    vm.approveFrom.ApproveInstitution = '',     //审批单位
                    vm.approveFrom.ApprovalResult = '',
                    vm.approveFrom.ApproveSuggestion = '',
                    vm.approveFrom.ApproveTime = '',
                    vm.approveFrom.imageUrl = ''
                $.ajax({
                    url: 'ApproveInfoDetail',
                    type: "post",
                    data: {
                        ApprovalID: vm.taskIDList[index],

                        tag: vm.tag
                    },
                    dataType: 'json',
                    success: function (result) {
                        let TaskInfo = result.ApplyInfo;

                        vm.approveFrom.ApprovalID = vm.taskIDList[index]
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
                        let filenamelist = vm.approveFrom.uploadfilename.split('?');
                        vm.approveFrom.filetable = [];
                        for (let i = 0; i < filenamelist.length; i++) {
                            if (filenamelist[i] != "") {
                                vm.approveFrom.filetable.push({name: filenamelist[i]});
                            }
                        }


                        //vm.approveFrom.imageUrl = result.WaterMarkSignPath
                        vm.approveFrom.Approver = result.username
                        vm.approveFrom.ApproveInstitution = result.UserInstitutionName

                        let ApprovalInfo = result.ApprovalInfo
                        vm.approveFrom.ApprovalResult=ApprovalInfo[0].ApproveResult
                        vm.approveFrom.ApproveTime = ApprovalInfo[0].ApproveTime
                        vm.approveFrom.ApproveSuggestion = ApprovalInfo[0].ApproveSuggestion
                        if(ApprovalInfo[0].ApproveSignature=='')
                        {
                            vm.approveFrom.imageUrl = '../../Images/white.jpg'
                        }
                        else
                        {
                            vm.approveFrom.imageUrl = ApprovalInfo[0].ApproveSignature
                        }


                        vm.detailLoading = false
                    },
                    error: handleError
                });
            },

            //批复记录
            _historyRecord: function (ApprovalID) {
                let vm = this;
                vm.historyDialogVisible = true;
                vm.historyLoading = true;
                $.ajax({
                    url: '../DangerTaskApplyMan/getHistoryRecord',
                    type: "post",
                    data: {ApprovalID: ApprovalID, tag: 'Approve'},
                    dataType: 'json',
                    success: function (result) {
                        vm.HistoryRecord = result.HistoryRecord;
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
                let vm = this;
                vm.tableLoading=true
                $.when($.ajax({
                    url: 'getInfoList',
                    type: "post",
                    data: {tag: vm.tag},
                    dataType: 'json',
                    error: handleError
                }), $.ajax({
                    url: 'getApprovalListFinished',
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
                        vm.pagenum = result2[0].pagenum
                        /*  vm.pageindex = pageindex
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
                    url: 'getApprovalListFinished',
                    type: "post",
                    data: {
                        conditions:vm.conditions,
                        pageindex:vm.pageindex,
                        tag: vm.tag
                    },
                    dataType: 'json',
                    success: function (result) {
                        vm.approvalList = result.approvalList;
                        vm.pagenum = result.pagenum
                      /*  vm.pageindex = pageindex
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
            this.pageLoading = true;
            this.getInfoList();
        },
    });
})
