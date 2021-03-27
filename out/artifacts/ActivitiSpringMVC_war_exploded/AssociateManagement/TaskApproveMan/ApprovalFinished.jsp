<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" isELIgnored="false" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <title>040200</title>
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/static/element/element-ui.css"/>
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/static/css/module.css"/>
    <script type="text/javascript" src="<%=request.getContextPath()%>/static/js/jquery-3.3.1.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/static/js/vue.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/static/element/element-ui.js"></script>

    <script type="text/javascript" src="<%=request.getContextPath()%>/static/js/moment.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/static/js/systemFile.js"></script>

    <%--<script type="text/javascript" src="/kuangshanJava/SafetyManagement/lawAndRule/LRJS/ItemManage.js"></script>--%>
    <script type="text/javascript"
            src="<%=request.getContextPath()%>/AssociateManagement/TaskApproveMan/scripts/ApprovalFinished.js?<%=Math.random()%>"></script>
    <style type="text/css">


        .el-dialog__body .el-textarea__inner {
            width: 15em;
            height: 2.5em;
        }

        .pop-dialog .el-form-item__label {
            width: 9em;
        }

    </style>
</head>
<body class="right_wap">
<div class="right">
    <el-container>
        <el-header height="48px">
            <el-breadcrumb separator="/">
                <el-breadcrumb-item><a href="Approval_Vue">待批作业</a></el-breadcrumb-item>
                <el-breadcrumb-item>已批作业</el-breadcrumb-item>
            </el-breadcrumb>
        </el-header>

        <div class="wrapper">
            <div class="content" v-loading="tableLoading" v-cloak>
                <div class="section">
                    <div class="list-filter">
                        <el-row style="margin-bottom: 15px">
                            <el-col :span="7">
                                <el-col class="filter-label" :span="6">
                                    {{params[0].label}}
                                </el-col>
                                <el-col :span="16">
                                    <el-input v-model="params[0].value" clearable placeholder="请输入作业名称"></el-input>
                                </el-col>
                            </el-col>
                            <el-col :span="7">
                                <el-col class="filter-label" :span="6">
                                    {{params[1].label}}
                                </el-col>
                                <el-col :span="16">
                                    <el-select v-model="params[1].value" filterable clearable placeholder="请选择">
                                        <el-option v-for="item in InstitutionList" :value="item.institutionNum"
                                                   :label="item.institutionName"></el-option>
                                    </el-select>
                                </el-col>
                            </el-col>
                            <el-col :span="7">
                                <el-col class="filter-label" :span="6">
                                    {{params[2].label}}
                                </el-col>
                                <el-col :span="16">
                                    <el-cascader
                                            v-model="params[2].value"
                                            :options="CategoryList"
                                            :props="{ expandTrigger: 'hover' }"></el-cascader>
                                </el-col>
                            </el-col>
                            <el-col :span="2">
                                <el-button @click="filter" type="primary" style="float: right">查 询</el-button>
                            </el-col>
                        </el-row>
                    </div>
                </div>
                <div class="section">
                    <div class="list-table">
                        <table>
                            <tr>
                                <%--<th width="5%">
                                    <div>
                                        <span>#</span>
                                    </div>
                                </th>--%>
                                <th width="25%">
                                    <div>
                                        <span>作业编号</span>
                                    </div>
                                </th>
                                <th width="25%">
                                    <div>
                                        <span>作业名称</span>
                                    </div>
                                </th>
                                <th width="15%">
                                    <div>
                                        <span>申请单位</span>
                                    </div>
                                </th>
                                <th width="15%">
                                    <div>
                                        <span>申请时间</span>
                                    </div>
                                </th>
                                <th width="15%">
                                    <div>
                                        <span>任务状态</span>
                                    </div>
                                </th>
                                <th width="15%">
                                    <div>
                                        <span>操作</span>
                                    </div>
                                </th>
                            </tr>
                            <tr v-for="(record,index) in approvalList" align="center">
                                <td style="display: none">
                                    <el-checkbox size="medium" :true-label="record.ApprovalID"
                                                 v-model="id_list[index]"></el-checkbox>
                                </td>
                                <td :title="record.DangerTaskNum">{{record.DangerTaskNum}}</td>
                                <td :title="record.DangerTaskName">{{record.DangerTaskName}}</td>
                                <td :title="record.ApplicantInstitutionName">{{record.ApplicantInstitutionName}}</td>
                                <td :title="record.ApplyingTime">{{record.ApplyingTime ==
                                    '1970-01-01'?'':record.ApplyingTime}}
                                </td>
                                <td>{{record.Finished == '0'?'待批':record.Finished == '1'?'已批':record.Finished ==
                                    '2'?'结束':record.Finished == '3'?'被退回':record.Finished == '4'?'已退回':'转发'}}
                                </td>
                                <td>
                                    <el-button type="text" @click="getInfoForDetail(record.ApprovalID)">详细</el-button>
                                    <el-button type="text" @click="_historyRecord(record.ApprovalID)">批复记录</el-button>

                                </td>
                            </tr>

                        </table>
                    </div>
                </div>

                <div class="section">
                    <div class="list-pagination">
                        <el-pagination
                                v-if="pagenum>0"
                                background
                                page-size="10"
                                layout="prev, pager, next"
                                @current-change="getList"
                                :current-page="pageindex"
                                :total="pagenum*10">
                        </el-pagination>
                        <div class="list-hint" v-else>提示：列表中无数据</div>
                    </div>
                </div>

                <div class="pop-dialog">
                    <%--详细界面--%>
                    <div class="detail-dialog">
                        <el-dialog title="审批危险作业申请" v-loading="detailLoading" top="2.5vh"
                                   :visible.sync="detailDialogVisible"
                                   :modal="false"
                                   :close-on-click-modal="false" :close-on-press-escape="false" :show-close="false"
                                   width="60%">
                            <el-collapse v-model="activeNames" @change="handleChange">
                                <el-collapse-item title="危险作业申请信息" name="1">
                                    <div class="right_content">
                                        <el-form label-position="right" :model="approveFrom">
                                            <el-container>
                                                <fieldset class="dialog-fieldset">
                                                    <legend>基本信息</legend>
                                                    <div>
                                                        <el-form-item label="申请人：" prop="Applicant" size="small">
                                                            <el-input type="input" v-model="approveFrom.Applicant"
                                                                      disabled="true"
                                                                      clearable></el-input>
                                                        </el-form-item>
                                                        <el-form-item label="作业项目编号：" prop="DangerTaskNum"
                                                                      size="small">
                                                            <el-input type="input" v-model="approveFrom.DangerTaskNum"
                                                                      disabled="true"
                                                                      clearable autocomplete="off"></el-input>
                                                        </el-form-item>
                                                        <el-form-item label="作业项目名：" prop="DangerTaskName"
                                                                      size="small">
                                                            <el-input type="input"
                                                                      v-model="approveFrom.DangerTaskName"
                                                                      disabled="true"
                                                                      clearable autocomplete="off"></el-input>
                                                        </el-form-item>
                                                        <el-form-item label="危险作业级别：" prop="DangerTaskLevel"
                                                                      size="small">
                                                            <el-select v-model="approveFrom.DangerTaskLevel"
                                                                       filterable="true"
                                                                       disabled="true"
                                                                       placeholder="请选择">
                                                                <el-option value="CompanyDangerTask"
                                                                           label="公司级"></el-option>
                                                                <el-option value="WorkShopDangerTask"
                                                                           label="分公司级"></el-option>
                                                            </el-select>
                                                        </el-form-item>
                                                        <el-form-item label="危险作业类型：" prop="Category" size="small">
                                                            <el-select v-model="approveFrom.Category"
                                                                       filterable="true"
                                                                       disabled="true"
                                                                       placeholder="请选择">
                                                                <el-option v-for="item in SecondaryCategory"
                                                                           :value="item.value"
                                                                           :label="item.label"></el-option>
                                                            </el-select>
                                                        </el-form-item>
                                                        <el-form-item label="申请单位：" prop="ApplyInstitution"
                                                                      size="small">
                                                            <el-input type="text"
                                                                      v-model="approveFrom.ApplyInstitution"
                                                                      disabled="true"
                                                                      clearable autocomplete="off"></el-input>

                                                        </el-form-item>
                                                        <el-form-item label="申请时间：" prop="ApplyingTime" size="small"
                                                                      disabled="true">
                                                            <el-date-picker
                                                                    v-model="approveFrom.ApplyingTime"
                                                                    align="right"
                                                                    type="datetime"
                                                                    placeholder="选择日期"
                                                                    disabled="true"
                                                                    :picker-options="pickerOptions1">
                                                            </el-date-picker>
                                                        </el-form-item>
                                                        <el-form-item label="作业开始时间：" prop="StartTime" size="small">
                                                            <%--<el-input type="input" v-model="addFrom.implementtime" autocomplete="off"></el-input>--%>
                                                            <el-date-picker
                                                                    v-model="approveFrom.StartTime"
                                                                    align="right"
                                                                    type="datetime"
                                                                    placeholder="选择日期" clearable disabled="true"
                                                                    :picker-options="pickerOptions1">
                                                            </el-date-picker>
                                                        </el-form-item>
                                                        <el-form-item label="作业截止时间：" prop="EndTime" size="small">
                                                            <el-date-picker
                                                                    v-model="approveFrom.EndTime"
                                                                    align="right"
                                                                    type="datetime"
                                                                    placeholder="选择日期" clearable disabled="true"
                                                                    :picker-options="pickerOptions1">
                                                            </el-date-picker>
                                                        </el-form-item>
                                                        <el-form-item label="是否已归档：" prop="Archived" size="small"
                                                                      disabled="true">
                                                            <el-select v-model="approveFrom.Archived" filterable
                                                                       placeholder="请选择"
                                                                       disabled="true">
                                                                <el-option value="1" label="已归档"></el-option>
                                                                <el-option value="0" label="未归档"></el-option>
                                                            </el-select>
                                                        </el-form-item>

                                                    </div>
                                                </fieldset>
                                                <fieldset class="dialog-fieldset">
                                                    <legend>相关文件</legend>
                                                    <div>
                                                        <el-table :data="approveFrom.filetable">
                                                            <el-table-column
                                                                    prop="name"
                                                                    label="文件名称"
                                                                    width="50%">
                                                            </el-table-column>
                                                            <el-table-column
                                                                    label="操作"
                                                                    width="50%">
                                                                <template slot-scope="scope">
                                                                    <el-button
                                                                            @click="_viewFile(approveFrom.TaskID,scope.row.name)"
                                                                            type="text" size="small">浏览
                                                                    </el-button>

                                                                    <el-button
                                                                            @click="_downloadone(approveFrom.TaskID,scope.row.name)"
                                                                            type="text" size="small">下载
                                                                    </el-button>
                                                                </template>
                                                            </el-table-column>
                                                        </el-table>
                                                    </div>
                                                </fieldset>
                                            </el-container>

                                        </el-form>
                                    </div>
                                </el-collapse-item>
                            </el-collapse>

                            <el-row type="flex" class="row-bg" justify="center">
                                <el-col :span="8">
                                </el-col>
                                <el-col class="filter-label" :span="8" readonly="true"
                                        style="font-size: 18px;text-align: center;padding: 0.5em 0em;">
                                    审批危险作业
                                </el-col>
                                <el-col :span="8">

                                </el-col>
                            </el-row>
                            <div class="right_content">
                                <el-form :model="approveFrom" label-position="right">

                                    <el-container>
                                        <fieldset class="dialog-fieldset-select">
                                            <%-- <legend>审批信息</legend>--%>
                                            <div>
                                                <el-row>
                                                    <el-col :span="12">
                                                        <el-form-item label="审批部门" prop="ApproveInstitution"
                                                                      size="small">
                                                            <el-input type="input"
                                                                      v-model="approveFrom.ApproveInstitution"
                                                                      disabled="true"
                                                                      clearable></el-input>
                                                        </el-form-item>
                                                        <el-form-item label="审批结果" prop="ApprovalResult"
                                                                      size="small">
                                                            <el-select v-model="approveFrom.ApprovalResult" filterable
                                                                       disabled="true"
                                                                       placeholder="请选择">
                                                                <el-option value="1" label="同意"></el-option>
                                                                <el-option value="0" label="不同意"></el-option>
                                                            </el-select>
                                                        </el-form-item>
                                                        <el-form-item label="审批意见" prop="ApproveSuggestion"
                                                                      size="small">
                                                            <el-input type="textarea"
                                                                      v-model="approveFrom.ApproveSuggestion"
                                                                      disabled="true"

                                                                      clearable autocomplete="off"></el-input>

                                                        </el-form-item>
                                                    </el-col>
                                                    <el-col :span="12">
                                                        <el-form-item label="审批人" prop="Approver" size="small"
                                                                      disabled="true">
                                                            <el-input type="input" v-model="approveFrom.Approver"
                                                                      disabled="true"
                                                                      clearable></el-input>
                                                        </el-form-item>
                                                        <el-form-item label="审批人签字时间" prop="ApproveTime" size="small">
                                                            <el-date-picker
                                                                    v-model="approveFrom.ApproveTime"
                                                                    align="right"
                                                                    type="datetime"
                                                                    placeholder="选择日期" clearable disabled="true"
                                                                    :picker-options="pickerOptions1">
                                                            </el-date-picker>
                                                        </el-form-item>
                                                        <el-form-item label="审批人签字" prop="Archived" size="small"
                                                                      disabled="true">
                                                            <el-upload
                                                                    class="avatar-uploader"
                                                                    drag
                                                                    action="uploadPhoto"
                                                                    :show-file-list="false"
                                                                    disabled="true">
                                                                <img v-if="approveFrom.imageUrl"
                                                                     :src="approveFrom.imageUrl" class="avatar">
                                                            </el-upload>
                                                        </el-form-item>
                                                    </el-col>
                                                </el-row>

                                            </div>
                                        </fieldset>
                                    </el-container>


                                </el-form>

                            </div>
                            <div slot="footer" class="dialog-footer">
                                <el-button @click="getDetail(listindex-1)" :disabled="listindex==0" type="primary"
                                           style="margin-right: 10px;float: left">
                                    上一条
                                </el-button>
                                <el-button @click="getDetail(listindex+1)" :disabled="listindex+1==listsize"
                                           type="primary"
                                           style="margin-right: 10px;float: left">
                                    下一条
                                </el-button>

                                <el-button
                                        @click="detailDialogVisible=false"
                                        type="danger">关闭
                                </el-button>
                            </div>
                        </el-dialog>
                    </div>

                    <%--批复记录界面--%>
                    <div class="history-dialog">
                        <el-dialog title="检查表申请批复记录" v-loading="historyLoading" top="2.5vh"
                                   :visible.sync="historyDialogVisible"
                                   :modal="false"
                                   :close-on-click-modal="false" :close-on-press-escape="false" :show-close="false"
                                   width="60%">
                            <div class="list-table">
                                <table>
                                    <tr>
                                        <th width="14%" style="text-align: center;">
                                            <div>
                                                <span>审批序号</span>
                                            </div>
                                        </th>
                                        <th width="14%" style="text-align: center;">
                                            <div>
                                                <span>审批部门</span>
                                            </div>
                                        </th>
                                        <th width="14%" style="text-align: center;">
                                            <div>
                                                <span>审批人</span>
                                            </div>
                                        </th>
                                        <th width="14%" style="text-align: center;">
                                            <div>
                                                <span>审批结论</span>
                                            </div>
                                        </th>
                                        <th width="15%" style="text-align: center;">
                                            <div>
                                                <span>审批时间</span>
                                            </div>
                                        </th>
                                        <th width="15%" style="text-align: center;">
                                            <div>
                                                <span>审批意见</span>
                                            </div>
                                        </th>
                                        <th width="15%" style="text-align: center;">
                                            <div>
                                                <span>是否结束</span>
                                            </div>
                                        </th>
                                    </tr>
                                    <tr v-for="(record,index) in HistoryRecord" :id="record.ApprovalID" align="center">
                                        <%--<td>
                                            <el-checkbox size="medium" :true-label="record.ApprovalID" style="display: none"
                                                         v-model="id_list[index]"></el-checkbox>
                                        </td>--%>
                                        <td :title="index+1">{{index+1}}</td>
                                        <td :title="record.ApproveInstitutionName">{{record.ApproveInstitutionName}}
                                        </td>
                                        <td :title="record.ApprovalPeopleName">{{record.ApprovalPeopleName}}</td>
                                        <td :title="record.ApproveResult == '0'?'不同意':record.ApproveResult == '1'?'同意':''">
                                            {{record.ApproveResult == '0'?'不同意':record.ApproveResult == '1'?'同意':''}}
                                        </td>

                                        <td :title="record.ApproveTime">{{record.ApproveTime ==
                                            '1970-01-01'?'':record.ApproveTime}}
                                        </td>
                                        <td :title="record.ApproveSuggestion">{{record.ApproveSuggestion}}</td>
                                        <td>{{record.Finished == '0'?'待批':record.Finished == '1'?'已批':record.Finished ==
                                            '2'?'结束':record.Finished == '3'?'被退回':record.Finished == '4'?'已退回':'转发'}}
                                        </td>

                                    </tr>
                                </table>

                            </div>
                            <div slot="footer" class="dialog-footer">
                                <el-button
                                        @click="historyDialogVisible=false"
                                        type="danger">关闭
                                </el-button>
                            </div>
                        </el-dialog>
                    </div>
                </div>
                <div class="view-dialog0 viewbady">
                    <el-dialog title="查看文件信息" top="2.5vh" :visible.sync="viewDialogVisible0" :modal="false"
                               width="100%"
                               :close-on-click-modal="true" :close-on-press-escape="true" :show-close="true"
                               :fullscreen="true">
                        <div style="height: 100%">

                            <iframe id="pdfRead" :src="filePath" width="100%" height="100%">
                            </iframe>
                            <%--<embed id="pdfRead" :src="filePath" type="" width="100%" height="100%">--%>
                            <%--<iframe :src="'../../static/pdf/web/viewer.html?file=../../../FileManagement/DualPreventionMechanism/'+ fileurl"--%>
                            <%--width="100%" height="800"></iframe>--%>
                        </div>

                    </el-dialog>
                </div>
                <div class="view-dialog1 viewbady">
                    <el-dialog id="videodia" title="查看文件信息" top="2.5vh" :visible.sync="viewDialogVisible1"
                               :modal="false" width="100%"
                               :close-on-click-modal="true" :close-on-press-escape="true" :show-close="true"
                               :fullscreen="true" @close="closevideoDialog">
                        <%--<embed :src="filePath" type="" width="100%" height="100%">--%>
                        <div id="mydiv" style="height: 100%;height: 100%">
                            <%--<video id="my-video" class="video-js vjs-default-skin " controls preload="auto" data-setup="{}">--%>
                            <%--<source :src="filePath" :type="filetype">--%>
                            <%--</video>--%>
                            <video id="videoex" class="video-js vjs-default-skin" controls preload="none" width="100%"
                                   height="100%">
                                <source :src="filePath">
                            </video>
                        </div>

                    </el-dialog>
                </div>
                <div class="audio-dialog audiobady">
                    <el-dialog id="picview" :visible.sync="audioDialogVisible"
                               :modal="false" width="20%" height="20%"
                               :modal-append-to-body="true"
                               :center="true"
                               :close-on-click-modal="false" :close-on-press-escape="false" :show-close="true"
                               @close="closevideoDialog1"
                    >
                        <audio id="audio" controls>
                            <source :src="filePath" :type="filetype">
                            <%--<source src="horse.mp3" type="audio/mpeg">--%>
                            <%--Your browser does not support the audio element.--%>
                        </audio>

                    </el-dialog>
                </div>
                <div class="picview-dialog viewbady">
                    <el-dialog id="picview" title="查看文件信息" top="2.5vh" :visible.sync="picviewDialogVisible"
                               :modal="false" width="100%"
                               :close-on-click-modal="true" :close-on-press-escape="true" :show-close="true"
                               :fullscreen="true">
                        <%--<embed :src="filePath" type="" width="100%" height="100%">--%>
                        <div id="mydiv" style="height: 100%;height: 100%">
                            <img :src="filePath" style="margin-left: auto;
    margin-right:auto;
    display:block;" height="100%"/>
                        </div>
                    </el-dialog>
                </div>
            </div>

        </div>

    </el-container>
</div>

</body>
</html>
