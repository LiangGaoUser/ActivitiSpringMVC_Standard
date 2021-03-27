$(function () {
    var padDate = function (va) {
        va = va < 10 ? '0' + va : va;
        return va
    }
    new Vue({
        el: ".main",
        data:{
            approvalList:[],
            safetyList:[],
            riskInfoList:[],
            safetyList2:[],
            userRight:'',
            Href:[],
        },
        //日期格式化
        filters: {
            formatDate: function (val) {
                var value = new Date(val);
                var year = value.getFullYear();
                var month = padDate(value.getMonth() + 1);
                var day = padDate(value.getDate());
                var hour = padDate(value.getHours());
                var minutes = padDate(value.getMinutes());
                var seconds = padDate(value.getSeconds());
                return year + '-' + month + '-' + day + ' ' + hour + ':' + minutes + ':' + seconds;

            }
        },
        methods:{
            formatDate: function (val) {
                var value = new Date(val);
                var year = value.getFullYear();
                var month = padDate(value.getMonth() + 1);
                var day = padDate(value.getDate());
                var hour = padDate(value.getHours());
                var minutes = padDate(value.getMinutes());
                var seconds = padDate(value.getSeconds());
                return year + '-' + month + '-' + day + ' ' + hour + ':' + minutes + ':' + seconds;

            },
            getList: function () {
                let vm = this;

                //代办事宜
                $.ajax({
                    url: '/kuangshanJava/AssociateManagement/TaskApproveMan/getApprovalList',
                    type: "post",
                    data: {
                        tag: 'Web'
                    },
                    dataType: 'json',
                    success: function (result) {
                        vm.approvalList = result.approvalList;
                        //alert(JSON.stringify(vm.approvalList))
                    },
                    error: function (XMLHttpRequest, textStatus, errorThrown) {
                        console.log(XMLHttpRequest.status);
                        console.log(XMLHttpRequest.readyState);
                        console.log(textStatus);
                    },
                });

                //未处理隐患
                $.ajax({
                    url: '/kuangshanJava/SafetyManagement/RectificationManagement/getList_UnprocessedRectification',
                    type: "post",
                    data: {
                        pageindex: 1,
                        conditions: ""
                    },
                    dataType: 'json',
                    success: function (result) {
                        vm.safetyList = result.list;
                        //alert(JSON.stringify(vm.approvalList))
                    },
                    error: function (XMLHttpRequest, textStatus, errorThrown) {
                        console.log(XMLHttpRequest.status);
                        console.log(XMLHttpRequest.readyState);
                        console.log(textStatus);
                    },
                });


                //重大风险告知
                $.ajax({
                    url: '/kuangshanJava/SecurityRiskGradingControl/SignificantRiskInfo/GetList_vue',
                    type: "post",
                    data: {
                    },
                    dataType: 'json',
                    success: function (result) {
                        vm.riskInfoList = result.list;
                        for(let i=0;i<vm.riskInfoList.length;i++){
                            if(!vm.riskInfoList[i].RiskLevelDescription){//RiskLevelDescription=""或者RiskLevelDescription=0或者RiskLevelDescription=null
                                if(vm.riskInfoList[i].RiskLevelDescription!=0){
                                    vm.riskInfoList[i].RiskLevelDescription = "";
                                }
                            }
                        }
                    },
                    error: function (XMLHttpRequest, textStatus, errorThrown) {
                        console.log(XMLHttpRequest.status);
                        console.log(XMLHttpRequest.readyState);
                        console.log(textStatus);
                    },
                });


                //已逾期整改
                $.ajax({
                    url: '/kuangshanJava/SafetyManagement/RectificationManagement/getList_OverdueRectification',
                    type: "post",
                    data: {
                        pageindex: 1,
                        conditions: ""
                    },
                    dataType: 'json',
                    success: function (result) {
                        vm.safetyList2 = result.list;
                        vm.safetyList2.map(function (item) {
                            item.RectifactionEndDate=vm.formatDate( item.RectifactionEndDate)
                        })
                        //alert(JSON.stringify(vm.approvalList))
                    },
                    error: function (XMLHttpRequest, textStatus, errorThrown) {
                        console.log(XMLHttpRequest.status);
                        console.log(XMLHttpRequest.readyState);
                        console.log(textStatus);
                    },
                });


            },

        },
        created: function () {
            this.pageLoading = true;
            this.getList();

            this.Href=[]
            let userRight=sessionStorage.getItem("userRight");
            this.userRight=userRight.substring(0,userRight.length-1);
            for(let moduleRight of this.userRight.split(";")){
                if(moduleRight.indexOf('040200')>=0){
                    this.Href[0]='AssociateManagement/TaskApproveMan/Approval_Vue';
                }
                else if(moduleRight.indexOf('030400')>=0)       //重大风险告知
                {
                    this.Href[1]='SecurityRiskGradingControl/SignificantRiskInfo/List_vue';
                }
                else if(moduleRight.indexOf('050500')>=0)
                {
                    this.Href[2]='SafetyManagement/RectificationManagement/vue_UnprocessedRectification';  //未处理隐患
                    this.Href[3]='SafetyManagement/RectificationManagement/vue_OverdueRectification';      //已逾期整改
                }
            }
            if(this.Href[0]==''||this.Href[0]==undefined||this.Href[0]==null)
            {
                this.Href[0]='javascript:void(0)';
            }
            else if(this.Href[1]==''||this.Href[1]==undefined||this.Href[1]==null)
            {
                this.Href[1]='javascript:void(0)';
            }
            else if(this.Href[2]==''||this.Href[2]==undefined||this.Href[2]==null)
            {
                this. Href[2]='javascript:void(0)';
            }
            else if(this.Href[3]==''||this.Href[3]==undefined||this.Href[3]==null)
            {
                this.Href[3]='javascript:void(0)';
            }

        },
    });
})
