<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" isELIgnored="false" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <title>040200</title>
    <%-- <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/static/bootstrap/css/bootstrap.min.css"/>
     <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/static/iview/styles/iview.css"/>
     <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/static/css/apply.css"/>
     <script type="text/javascript" src="<%=request.getContextPath()%>/static/js/jquery-3.3.1.min.js"></script>
     <script type="text/javascript" src="<%=request.getContextPath()%>/static/js/vue.min.js"></script>
     <script type="text/javascript" src="<%=request.getContextPath()%>/static/iview/iview.min.js"></script>
     <script type="text/javascript"
             src="<%=request.getContextPath()%>/static/uploadify/jquery.uploadify.wzModifyForViewProof.js"></script>
     <script type="text/javascript" src="<%=request.getContextPath()%>/static/uploadify/swfobject.js"></script>
     <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/static/element/element-ui.css"/>
     <script type="text/javascript" src="<%=request.getContextPath()%>/static/element/element-ui.js"></script>--%>

    <%--

        <script type="text/javascript" src="../../Scripts/jquery-2.2.4.min.js"></script>
        <script type="text/javascript" src="../../Scripts/vue-js/vue.min.js"></script>

        <script type="text/javascript" src="../../Scripts/layer-js/layer.js"></script>

        <script type="text/javascript"
                src="../../Scripts/bootstrap/js/bootstrap.min.js"></script>
        <link rel="stylesheet" type="text/css"
              href="../../Scripts/bootstrap/css/bootstrap.min.css"/>
        <link href="../../Styles/apply.css" rel="stylesheet" type="text/css"/>
        <script type="text/javascript" src="<%=request.getContextPath()%>/static/element/element-ui.js"></script>
        <script type="text/javascript" src="<%=request.getContextPath()%>/static/js/moment.js"></script>
        <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/static/element/element-ui.css"/>
        <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/static/css/right.css"/>
    --%>

    <%--<script type="text/javascript" src="/kuangshanJava/SafetyManagement/lawAndRule/LRJS/ItemManage.js"></script>--%>

    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/static/element/element-ui.css"/>
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/static/css/module.css"/>
    <script type="text/javascript" src="<%=request.getContextPath()%>/static/js/jquery-3.3.1.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/static/js/vue.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/static/element/element-ui.js"></script>

    <script type="text/javascript" src="<%=request.getContextPath()%>/static/js/moment.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/static/js/systemFile.js"></script>

    <script type="text/javascript"
            src="<%=request.getContextPath()%>/AssociateManagement/TaskApproveMan/scripts/dangerTaskApproval.js?<%=Math.random()%>"></script>
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
<div id="right">
    <el-container>
        <el-header height="48px">
            <el-breadcrumb separator="/">
                <el-breadcrumb-item>????????????</el-breadcrumb-item>
                <el-breadcrumb-item><a href="ApprovalFinished_Vue">????????????</a></el-breadcrumb-item>
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
                                    <el-input v-model="params[0].value" clearable placeholder="?????????????????????"></el-input>
                                </el-col>
                            </el-col>
                            <el-col :span="7">
                                <el-col class="filter-label" :span="6">
                                    {{params[1].label}}
                                </el-col>
                                <el-col :span="16">
                                    <el-select v-model="params[1].value" filterable clearable placeholder="?????????">
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
                                <el-button @click="filter" type="primary" style="float: right">??? ???</el-button>
                            </el-col>
                        </el-row>
                    </div>
                </div>
                <div class="section">
                    <div class="list-operation">
                        <el-button class="operation-editable" type="primary" @click="relay" id="btn_relay">??????</el-button>
                    </div>
                </div>
                <div class="section">
                    <div class="list-table">
                        <table>
                            <tr>
                                <th width="5%">
                                    <div>
                                        <span>#</span>
                                    </div>
                                </th>
                                <th width="20%">
                                    <div>
                                        <span>????????????</span>
                                    </div>
                                </th>
                                <th width="25%">
                                    <div>
                                        <span>????????????</span>
                                    </div>
                                </th>
                                <th width="15%">
                                    <div>
                                        <span>????????????</span>
                                    </div>
                                </th>
                                <th width="15%">
                                    <div>
                                        <span>????????????</span>
                                    </div>
                                </th>
                                <th width="15%">
                                    <div>
                                        <span>????????????</span>
                                    </div>
                                </th>
                                <th width="20%">
                                    <div>
                                        <span>??????</span>
                                    </div>
                                </th>
                            </tr>
                            <tr v-for="(record,index) in approvalList" :id="record.ApprovalID" align="center">
                                <td>
                                    <el-checkbox size="medium" :true-label="record.ApprovalID"
                                                 v-model="id_list[index]"></el-checkbox>
                                </td>
                                <td :title="record.DangerTaskNum">{{record.DangerTaskNum}}</td>
                                <td :title="record.DangerTaskName">{{record.DangerTaskName}}</td>
                                <td :title="record.ApplicantInstitutionName">{{record.ApplicantInstitutionName}}</td>
                                <td :title="record.ApplyingTime">{{record.ApplyingTime ==
                                    '1970-01-01'?'':record.ApplyingTime}}
                                </td>
                                <td>{{record.Finished == '0'?'??????':record.Finished == '1'?'??????':record.Finished ==
                                    '2'?'??????':record.Finished == '3'?'?????????':record.Finished == '4'?'?????????':'??????'}}
                                </td>
                                <td>
                                    <el-button class="operation-editable" type="text" @click="getInfoForApprove(record.ApprovalID)">??????</el-button>
                                    <el-button class="operation-editable" type="text" @click="callback(record.ApprovalID)">??????</el-button>
                                    <el-button class="operation-editable" type="text" @click="_historyRecord(record.ApprovalID)">????????????</el-button>
                                    <%--<el-dropdown trigger="hover">
                                    <span class="el-dropdown-link">
                                       ??????<i class="el-icon-arrow-down el-icon--right"></i>
                                   </span>
                                        <el-dropdown-menu slot="dropdown">
                                            <el-dropdown-item @click.native="_historyRecord(record.ApprovalID)">????????????
                                            </el-dropdown-item>
                                        </el-dropdown-menu>
                                    </el-dropdown>--%>
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
                        <div class="list-hint" v-else>???????????????????????????</div>
                    </div>
                </div>

                <div class="pop-dialog">

                    <%--????????????--%>
                    <div class="approve-dialog">
                        <el-dialog title="????????????????????????" v-loading="approveLoading" top="2.5vh"
                                   :visible.sync="approveDialogVisible"
                                   :modal="false"
                                   :close-on-click-modal="false" :close-on-press-escape="false" :show-close="false"
                                   width="60%">
                            <el-collapse v-model="activeNames" @change="handleChange">
                                <%-- <el-row type="flex" class="row-bg" justify="center">
                                     <el-col :span="8">
                                     </el-col>
                                     <el-col class="filter-label" :span="8" readonly="true"
                                             style="font-size: 20px;text-align: center">
                                         ??????????????????????????????
                                     </el-col>
                                     <el-col :span="8">

                                     </el-col>
                                 </el-row>--%>
                                <el-collapse-item title="????????????????????????" name="1">
                                    <div class="right_content">
                                        <el-form label-position="right" :model="approveFrom" ref="addRuleFrom"
                                                 :rules="rules" class="">
                                            <el-container>
                                                <%--<el-container width="50%">--%>
                                                    <fieldset  class="dialog-fieldset">
                                                        <legend>????????????</legend>
                                                        <div>
                                                            <el-form-item label="????????????" prop="Applicant" size="small">
                                                                <el-input type="input" v-model="approveFrom.Applicant"
                                                                          disabled="true"
                                                                          clearable></el-input>
                                                            </el-form-item>
                                                            <el-form-item label="?????????????????????" prop="DangerTaskNum"
                                                                          size="small">
                                                                <el-input type="input" v-model="approveFrom.DangerTaskNum"
                                                                          disabled="true"
                                                                          clearable autocomplete="off"></el-input>
                                                            </el-form-item>
                                                            <el-form-item label="??????????????????" prop="DangerTaskName"
                                                                          size="small">
                                                                <el-input type="input"
                                                                          v-model="approveFrom.DangerTaskName"
                                                                          disabled="true"
                                                                          clearable autocomplete="off"></el-input>
                                                            </el-form-item>
                                                            <el-form-item label="?????????????????????" prop="DangerTaskLevel" size="small">
                                                                <el-select v-model="approveFrom.DangerTaskLevel" filterable="true"
                                                                           disabled="true"
                                                                           placeholder="?????????">
                                                                    <el-option value="CompanyDangerTask" label="?????????"></el-option>
                                                                    <el-option value="WorkShopDangerTask" label="????????????"></el-option>
                                                                </el-select>
                                                            </el-form-item>
                                                            <el-form-item label="?????????????????????" prop="Category" size="small">
                                                                <el-select v-model="approveFrom.Category"
                                                                           filterable="true"
                                                                           disabled="true"
                                                                           placeholder="?????????">
                                                                    <el-option v-for="item in SecondaryCategory"
                                                                               :value="item.value"
                                                                               :label="item.label"></el-option>
                                                                </el-select>
                                                            </el-form-item>
                                                            <el-form-item label="???????????????" prop="ApplyInstitution"
                                                                          size="small">
                                                                <el-input type="text"
                                                                          v-model="approveFrom.ApplyInstitution"
                                                                          disabled="true"
                                                                          clearable autocomplete="off"></el-input>

                                                            </el-form-item>
                                                            <el-form-item label="???????????????" prop="ApplyingTime" size="small"
                                                                          disabled="true">
                                                                <el-date-picker
                                                                        v-model="approveFrom.ApplyingTime"
                                                                        align="right"
                                                                        type="datetime"
                                                                        placeholder="????????????"
                                                                        disabled="true"
                                                                        :picker-options="pickerOptions1">
                                                                </el-date-picker>
                                                            </el-form-item>
                                                            <el-form-item label="?????????????????????" prop="StartTime" size="small">
                                                                <%--<el-input type="input" v-model="addFrom.implementtime" autocomplete="off"></el-input>--%>
                                                                <el-date-picker
                                                                        v-model="approveFrom.StartTime"
                                                                        align="right"
                                                                        type="datetime"
                                                                        placeholder="????????????" clearable disabled="true"
                                                                        :picker-options="pickerOptions1">
                                                                </el-date-picker>
                                                            </el-form-item>
                                                            <el-form-item label="?????????????????????" prop="EndTime" size="small">
                                                                <el-date-picker
                                                                        v-model="approveFrom.EndTime"
                                                                        align="right"
                                                                        type="datetime"
                                                                        placeholder="????????????" clearable disabled="true"
                                                                        :picker-options="pickerOptions1">
                                                                </el-date-picker>
                                                            </el-form-item>
                                                            <el-form-item label="??????????????????" prop="Archived" size="small"
                                                                          disabled="true">
                                                                <el-select v-model="approveFrom.Archived" filterable
                                                                           placeholder="?????????"
                                                                           disabled="true">
                                                                    <el-option value="1" label="?????????"></el-option>
                                                                    <el-option value="0" label="?????????"></el-option>
                                                                </el-select>
                                                            </el-form-item>

                                                        </div>
                                                    </fieldset>
                                                <%--</el-container>
                                                <el-container width="50%">--%>
                                                    <fieldset  class="dialog-fieldset">
                                                        <legend>????????????</legend>
                                                        <div>
                                                            <el-table :data="approveFrom.filetable">
                                                                <el-table-column
                                                                        prop="name"
                                                                        label="????????????"
                                                                        width="50%">
                                                                </el-table-column>
                                                                <el-table-column
                                                                        label="??????"
                                                                        width="50%">
                                                                    <template slot-scope="scope">
                                                                        <el-button
                                                                                @click="_viewFile(approveFrom.TaskID,scope.row.name)"
                                                                                type="text" size="small">??????
                                                                        </el-button>

                                                                        <el-button
                                                                                @click="_downloadone(approveFrom.TaskID,scope.row.name)"
                                                                                type="text" size="small">??????
                                                                        </el-button>
                                                                    </template>
                                                                </el-table-column>
                                                            </el-table>
                                                        </div>
                                                    </fieldset>
                                                <%--</el-container>--%>


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
                                    ??????????????????&nbsp;&nbsp;&nbsp;
                                </el-col>
                                <el-col :span="8">

                                </el-col>
                            </el-row>
                            <div class="right_content">
                                <el-form label-position="right" :model="approveFrom" ref="ApprovalRuleFrom"
                                         :rules="rulesForApproval" class="">

                                    <el-container>
                                        <fieldset  class="dialog-fieldset-select">
                                            <%-- <legend>????????????</legend>--%>
                                            <div>
                                                <el-row>
                                                    <el-col :span="12">
                                                        <el-form-item label="???????????????" prop="ApproveInstitution"
                                                                      size="small">
                                                            <el-input type="input"
                                                                      v-model="approveFrom.ApproveInstitution"
                                                                      disabled="true"
                                                                      clearable></el-input>
                                                        </el-form-item>
                                                        <el-form-item label="???????????????" prop="ApprovalResult"
                                                                      size="small">
                                                            <el-select v-model="approveFrom.ApprovalResult" @change="ApprovalResultChange"
                                                                       filterable
                                                                       placeholder="?????????">
                                                                <el-option value="1" label="??????"></el-option>
                                                                <el-option value="0" label="?????????"></el-option>
                                                            </el-select>
                                                        </el-form-item>
                                                        <el-form-item label="?????????????????????" prop="nextApproveInstitution"
                                                                      size="small">
                                                            <el-select v-model="approveFrom.nextApproveInstitution"
                                                                       filterable="true" multiple
                                                                       :disabled="disabled_nextInstitution"
                                                                       placeholder="?????????">
                                                                <el-option
                                                                        v-for="item in approveFrom.ApproveInstitutionList"
                                                                        :value="item.employeeNum"
                                                                        :label="item.institutionName"></el-option>
                                                            </el-select>
                                                        </el-form-item>
                                                        <el-form-item label="???????????????" prop="ApproveSuggestion"
                                                                      size="small">
                                                            <el-input type="textarea"   autosize
                                                                      v-model="approveFrom.ApproveSuggestion"

                                                                      clearable autocomplete="off"></el-input>

                                                        </el-form-item>
                                                    </el-col>
                                                    <el-col :span="12">
                                                        <el-form-item label="????????????" prop="Approver" size="small"
                                                                      disabled="true">
                                                            <el-input type="input" v-model="approveFrom.Approver"
                                                                      disabled="true"
                                                                      clearable></el-input>
                                                        </el-form-item>
                                                        <el-form-item label="???????????????" prop="ApprovalFinished"
                                                                      size="small">
                                                            <el-select v-model="approveFrom.ApprovalFinished"
                                                                       filterable
                                                                       placeholder="?????????"
                                                                       @change="ApprovalFinished_Change"
                                                                       :disabled="disabled_Finished">
                                                                <el-option value="2" label="???"></el-option>
                                                                <el-option value="0" label="???"></el-option>
                                                            </el-select>
                                                        </el-form-item>
                                                        <el-form-item label="????????????????????????" prop="ApproveTime"
                                                                      size="small">
                                                            <el-date-picker
                                                                    v-model="approveFrom.ApproveTime"
                                                                    align="right"
                                                                    type="datetime"
                                                                    placeholder="????????????" clearable disabled="true"
                                                                    :picker-options="pickerOptions1">
                                                            </el-date-picker>
                                                        </el-form-item>
                                                        <el-form-item label="??????????????????" prop="Archived" size="small"
                                                                      disabled="true">
                                                            <el-upload
                                                                    class="avatar-uploader"
                                                                    drag
                                                                    action="uploadPhoto"
                                                                    :show-file-list="false"
                                                                    disabled="true"
                                                                    >
                                                                <img v-if="approveFrom.imageUrl"
                                                                     :src="approveFrom.imageUrl"  class="avatar">
                                                                <% System.out.println( "counter= " + pageContext.findAttribute("approveFrom.imageUrl") ); %>
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

                                <el-button
                                        @click="ApprovalSubmit" :disabled="disabled_approval_save"
                                        type="primary">??????
                                </el-button>
                                <el-button
                                        @click="approveDialogVisible=false"
                                        type="danger">??????
                                </el-button>
                            </div>

                        </el-dialog>
                    </div>

                    <%--???????????? --%>
                    <div class="callback-dialog">
                        <el-dialog title="????????????????????????" v-loading="callbackLoading" top="2.5vh"
                                   :visible.sync="callbackDialogVisible"
                                   :modal="false"
                                   :close-on-click-modal="false" :close-on-press-escape="false" :show-close="false" width="50em"
                        >
                            <el-row type="flex" class="row-bg" justify="center" style="margin-bottom: 2em">
                                <el-col :span="8">
                                </el-col>
                                <el-col class="filter-label" :span="8" readonly="true"
                                        style="font-size: 20px;text-align: center">
                                    ????????????
                                </el-col>
                                <el-col :span="8">

                                </el-col>
                            </el-row>
                            <div class="right_content">
                                <el-form label-position="right" :model="callbackFrom" ref="callbackRuleFrom"
                                         :rules="rules" class="">

                                    <el-row type="flex" class="row-bg2" justify="center">
                                        <el-col :span="12">

                                            <el-form-item :label="txtLabel" prop="Submitter" size="small">
                                                <el-input type="input" v-model="callbackFrom.Submitter" disabled="true"
                                                          clearable></el-input>
                                            </el-form-item>
                                        </el-col>
                                        <el-col :span="12">

                                            <el-form-item label="???????????????" prop="ApproveSuggestion"
                                                          size="small">
                                                <el-input type="textarea" v-model="callbackFrom.ApproveSuggestion"

                                                          clearable autocomplete="off"></el-input>
                                            </el-form-item>
                                        </el-col>
                                    </el-row>

                                </el-form>

                            </div>
                            <div slot="footer" class="dialog-footer"
                            >
                                <el-button id="btn-add" :disabled="disabled_callback_save" type="primary"
                                           @click="_save">??????
                                </el-button>
                                <el-button
                                        @click="callbackDialogVisible=false"
                                        type="danger">??? ???
                                </el-button>
                            </div>
                        </el-dialog>
                    </div>

                    <%--???????????? --%>
                    <div class="relay-dialog">
                        <el-dialog title="????????????????????????" v-loading="relayLoading" top="2.5vh"
                                   :visible.sync="relayDialogVisible"
                                   :modal="false"
                                   :close-on-click-modal="false" :close-on-press-escape="false" :show-close="false" width="50em"
                        >
                            <el-row type="flex" class="row-bg" justify="center" style="margin-bottom: 2em">
                                <el-col :span="8">
                                </el-col>
                                <el-col class="filter-label" :span="8" readonly="true"
                                        style="font-size: 20px;text-align: center">
                                    ????????????
                                </el-col>
                                <el-col :span="8">

                                </el-col>
                            </el-row>
                            <div class="right_content">
                                <el-form label-position="right" :model="relayFrom" ref="relayRuleFrom"
                                         :rules="rulesForRelay" class="">

                                    <el-row type="flex" class="row-bg2" justify="center">
                                        <el-col :span="12">
                                            <el-form-item label="???????????????" prop="relayPeople" size="small">
                                                <el-select v-model="relayFrom.relayPeople" filterable="true"
                                                           placeholder="?????????">
                                                    <el-option v-for="item in relayPeopleList"
                                                               :value="item.EmployeeNum"
                                                               :label="item.EmployeeName"></el-option>
                                                </el-select>
                                            </el-form-item>
                                        </el-col>
                                        <el-col :span="12">
                                            <el-form-item label="???????????????" prop="ApproveSuggestion"
                                                          size="small">
                                                <el-input type="textarea" v-model="relayFrom.ApproveSuggestion"

                                                          clearable autocomplete="off"></el-input>
                                            </el-form-item>
                                        </el-col>
                                    </el-row>

                                </el-form>

                            </div>
                            <div slot="footer" class="dialog-footer">
                                <el-button id="btn-add" :disabled="disabled_relay_save" type="primary"
                                           @click="_relay_save">??????
                                </el-button>
                                <el-button
                                        @click="relayDialogVisible=false"
                                        type="danger">??? ???
                                </el-button>
                            </div>
                        </el-dialog>
                    </div>

                    <%--??????????????????--%>
                    <div class="history-dialog">
                        <el-dialog title="??????????????????????????????" v-loading="historyLoading" top="2.5vh"
                                   :visible.sync="historyDialogVisible"
                                   :modal="false"
                                   :close-on-click-modal="false" :close-on-press-escape="false" :show-close="false"
                                   width="60%">
                            <div class="list-table">
                                <table>
                                    <tr>
                                        <th width="14%" style="text-align: center;">
                                            <div>
                                                <span>????????????</span>
                                            </div>
                                        </th>
                                        <th width="14%" style="text-align: center;">
                                            <div>
                                                <span>????????????</span>
                                            </div>
                                        </th>
                                        <th width="14%" style="text-align: center;">
                                            <div>
                                                <span>?????????</span>
                                            </div>
                                        </th>
                                        <th width="14%" style="text-align: center;">
                                            <div>
                                                <span>????????????</span>
                                            </div>
                                        </th>
                                        <th width="15%" style="text-align: center;">
                                            <div>
                                                <span>????????????</span>
                                            </div>
                                        </th>
                                        <th width="15%" style="text-align: center;">
                                            <div>
                                                <span>????????????</span>
                                            </div>
                                        </th>
                                        <th width="15%" style="text-align: center;">
                                            <div>
                                                <span>????????????</span>
                                            </div>
                                        </th>
                                    </tr>
                                    <tr v-for="(record,index) in HistoryRecord"  align="center">
                                        <%--<td>
                                            <el-checkbox size="medium" :true-label="record.ApprovalID" style="display: none"
                                                         v-model="id_list[index]"></el-checkbox>
                                        </td>--%>
                                        <td :title="index+1">{{index+1}}</td>
                                        <td :title="record.ApproveInstitutionName">{{record.ApproveInstitutionName}}
                                        </td>
                                        <td :title="record.ApprovalPeopleName">{{record.ApprovalPeopleName}}</td>
                                        <td :title="record.ApproveResult == '0'?'?????????':record.ApproveResult == '1'?'??????':''">
                                            {{record.ApproveResult == '0'?'?????????':record.ApproveResult == '1'?'??????':''}}
                                        </td>

                                        <td :title="record.ApproveTime">{{record.ApproveTime ==
                                            '1970-01-01'?'':record.ApproveTime}}
                                        </td>
                                        <td :title="record.ApproveSuggestion">{{record.ApproveSuggestion}}</td>
                                        <td>{{record.Finished == '0'?'??????':record.Finished ==
                                            '1'?'??????':record.Finished ==
                                            '2'?'??????':record.Finished == '3'?'?????????':record.Finished == '4'?'?????????':'??????'}}
                                        </td>

                                    </tr>
                                </table>

                            </div>
                            <div slot="footer" class="dialog-footer">
                                <el-button
                                        @click="historyDialogVisible=false"
                                        type="danger">??????
                                </el-button>
                            </div>
                        </el-dialog>
                    </div>


                </div>
                <div class="view-dialog0 viewbady">
                    <el-dialog title="??????????????????" top="2.5vh" :visible.sync="viewDialogVisible0" :modal="false"
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
                    <el-dialog id="videodia" title="??????????????????" top="2.5vh" :visible.sync="viewDialogVisible1"
                               :modal="false" width="100%"
                               :close-on-click-modal="true" :close-on-press-escape="true" :show-close="true"
                               :fullscreen="true" @close="closevideoDialog">
                        <%--<embed :src="filePath" type="" width="100%" height="100%">--%>
                        <div id="mydiv" style="height: 100%;height: 100%">
                            <%--<video id="my-video" class="video-js vjs-default-skin " controls preload="auto" data-setup="{}">--%>
                            <%--<source :src="filePath" :type="filetype">--%>
                            <%--</video>--%>
                            <video id="videoex" class="video-js vjs-default-skin" controls preload="none"
                                   width="100%"
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
                    <el-dialog id="picview" title="??????????????????" top="2.5vh" :visible.sync="picviewDialogVisible"
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
