<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <title>040100</title>
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/static/element/element-ui.css"/>
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/static/css/module.css"/>
    <script type="text/javascript" src="<%=request.getContextPath()%>/static/js/jquery-3.3.1.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/static/js/vue.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/static/element/element-ui.js"></script>

    <script type="text/javascript" src="<%=request.getContextPath()%>/static/js/moment.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/static/js/systemFile.js"></script>

    <script type="text/javascript" src="<%=request.getContextPath()%>/AssociateManagement/DangerTaskApplyMan/scripts/dangerTaskApplyEvents.js?<%=Math.random()%>"></script>
    <style type="text/css">

        .pop-dialog .el-form-item__label {
            width: 9em;
        }

        .el-dialog__body .el-container .dialog-fieldset2 {
            /* padding-top: 20px; */
            border: none;

        }
        .edit-dialog .el-dialog .el-table__row .el-button + .el-button {
            margin-left: 0px;
        }
        .edit-dialog .el-dialog .el-table__row .el-button{
            padding: 6px 6px;
        }
        .el-transfer-panel__filter
        {
            width: auto !important;
        }

    </style>
</head>
<body class="right_wap">
<div id="right">
    <el-container>
        <el-header height="48px">
            <el-breadcrumb separator="/">
                <el-breadcrumb-item>??????????????????</el-breadcrumb-item>
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
                                    <el-input v-model="params[1].value" clearable placeholder="?????????????????????"></el-input>
                                </el-col>
                            </el-col>
                            <el-col :span="7">
                                <el-col class="filter-label" :span="6">
                                    {{params[2].label}}
                                </el-col>
                                <el-col :span="16">
                                    <el-select v-model="params[2].value" filterable clearable placeholder="?????????">
                                        <el-option v-for="item in InstitutionList" :value="item.institutionNum"
                                                   :label="item.institutionName"></el-option>
                                    </el-select>
                                </el-col>
                            </el-col>

                            <el-col :span="2">
                                <el-button @click="filter" type="primary" style="float: right">??? ???</el-button>
                            </el-col>
                        </el-row>
                        <el-row>
                            <el-col :span="7">
                                <el-col class="filter-label" :span="6">
                                    {{params[3].label}}
                                </el-col>
                                <el-col :span="16">
                                    <el-cascader
                                            v-model="params[3].value"
                                            :options="CategoryList"
                                            :props="{ expandTrigger: 'hover' }"></el-cascader>
                                </el-col>
                            </el-col>
                            <el-col :span="7">
                                <el-col class="filter-label" :span="6">
                                    {{params[4].label}}
                                </el-col>
                                <el-col :span="16">
                                    <el-select v-model="params[4].value" filterable clearable placeholder="?????????">
                                        <el-option value="0" label="??????"></el-option>
                                        <el-option value="1" label="?????????"></el-option>
                                        <el-option value="2" label="??????"></el-option>
                                        <el-option value="3" label="?????????"></el-option>
                                        <el-option value="4" label="??????"></el-option>
                                    </el-select>
                                </el-col>
                            </el-col>
                            <el-col :span="7">
                                <el-col class="filter-label" :span="6">
                                    {{params[5].label}}
                                </el-col>
                                <el-col :span="16">
                                    <el-select v-model="params[5].value" filterable clearable placeholder="?????????">
                                        <el-option value="0" label="?????????"></el-option>
                                        <el-option value="1" label="?????????"></el-option>
                                    </el-select>
                                </el-col>
                            </el-col>

                        </el-row>
                    </div>
                </div>
                <div class="section">
                    <div class="list-operation">
                        <el-button class="operation-editable" type="primary" @click="getInfoForAdd();correctTransferStyle">??????</el-button>
                        <el-button class="operation-editable" type="primary" @click="_delete" id="btn_delete">??????</el-button>
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
                                <th width="20%">
                                    <div>
                                        <span>????????????</span>
                                    </div>
                                </th>
                                <th width="13%">
                                    <div>
                                        <span>????????????</span>
                                    </div>
                                </th>
                                <th width="18%">
                                    <div>
                                        <span>????????????</span>
                                    </div>
                                </th>
                                <th width="18%">
                                    <div>
                                        <span>????????????</span>
                                    </div>
                                </th>
                                <th width="10%">
                                    <div>
                                        <span>????????????</span>
                                    </div>
                                </th>
                                <th width="19%">
                                    <div>
                                        <span>??????</span>
                                    </div>
                                </th>
                            </tr>
                            <tr v-for="(record,index) in taskList" :id="record.TaskID" align="center">
                                <td>
                                    <el-checkbox size="medium" :true-label="record.TaskID"
                                                 v-model="id_list[index]"></el-checkbox>
                                </td>
                                <td :title="record.DangerTaskNum">{{record.DangerTaskNum}}</td>
                                <td :title="record.DangerTaskName">{{record.DangerTaskName}}</td>
                                <td :title="record.InstitutionName">{{record.InstitutionName}}</td>
                                <td :title="record.StartTime">{{record.StartTime == '1970-01-01'?'':record.StartTime}}
                                </td>
                                <td :title="record.EndTime">{{record.EndTime == '1970-01-01'?'':record.EndTime}}</td>
                                <td>{{record.State == '0'?'??????':record.State ==
                                    '1'?'?????????':record.State ==
                                    '2'?'??????':record.State == '3'?'?????????':record.State == '4'?'??????':''}}
                                </td>
                                <td>
                                    <el-button type="text" @click="getInfoForDetail(record.TaskID)">??????</el-button>
                                    <el-button class="operation-editable" type="text" @click="getInfoForEdit(record.TaskID);correctTransferStyle">??????</el-button>
                                    <el-dropdown trigger="hover">
                                    <span class="el-dropdown-link">
                                       ??????<i class="el-icon-arrow-down el-icon--right"></i>
                                   </span>
                                        <el-dropdown-menu slot="dropdown">
                                            <el-dropdown-item class="operation-editable" @click.native="_archive(record.TaskID)">??????
                                            </el-dropdown-item>
                                            <el-dropdown-item @click.native="_historyRecord(record.TaskID)">????????????
                                            </el-dropdown-item>
                                            <el-dropdown-item @click.native="_print(record.TaskID)">??????
                                            </el-dropdown-item>
                                        </el-dropdown-menu>
                                    </el-dropdown>
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
                    <div class="add-dialog">
                        <el-dialog title="????????????????????????" v-loading="addLoading" top="2.5vh" :visible.sync="addDialogVisible"
                                   :modal="false"
                                   :close-on-click-modal="false" :close-on-press-escape="false" :show-close="false"
                                   width="50em">
                            <el-form label-position="right" :model="addFrom" ref="addFrom"
                                     :rules="rules" class="" v-loading="dialogLoading">
                                <el-container>
                                    <%--<el-container width="50%">--%>
                                    <fieldset class="dialog-fieldset">
                                        <legend>????????????</legend>
                                        <div>
                                            <el-form-item label="????????????" prop="Applicant" size="small">
                                                <el-input type="input" v-model="addFrom.Applicant"
                                                          disabled="true"
                                                          clearable></el-input>
                                            </el-form-item>
                                            <el-form-item label="?????????????????????" prop="DangerTaskNum"
                                                          size="small">
                                                <el-input type="input" v-model="addFrom.DangerTaskNum"
                                                          clearable autocomplete="off"></el-input>
                                            </el-form-item>
                                            <el-form-item label="??????????????????" prop="DangerTaskName"
                                                          size="small">
                                                <el-input type="input" v-model="addFrom.DangerTaskName"
                                                          clearable autocomplete="off"></el-input>
                                            </el-form-item>
                                            <el-form-item label="?????????????????????" prop="DangerTaskLevel" size="small">
                                                <el-select v-model="addFrom.DangerTaskLevel" filterable="true" @change="taskLevelChange"
                                                           placeholder="?????????">
                                                    <el-option value="CompanyDangerTask" label="?????????"></el-option>
                                                    <el-option value="WorkShopDangerTask" label="????????????"></el-option>
                                                </el-select>
                                            </el-form-item>
                                            <el-form-item label="?????????????????????" prop="Category" size="small">
                                                <el-select v-model="addFrom.Category" filterable="true" :disabled="categoryDisabled"
                                                           placeholder="?????????">
                                                    <el-option v-for="item in SecondaryCategory"
                                                               :value="item.value"
                                                               :label="item.label"></el-option>
                                                </el-select>
                                            </el-form-item>
                                            <el-form-item label="???????????????" prop="ApplyInstitution" size="small">
                                                <el-input type="input" v-model="addFrom.ApplyInstitution"
                                                          disabled="true"
                                                          clearable></el-input>
                                            </el-form-item>
                                            <el-form-item label="???????????????" prop="ApplyingTime" size="small">
                                                <el-date-picker
                                                        v-model="addFrom.ApplyingTime"
                                                        value-format="yyyy-MM-dd HH:mm:ss"
                                                        align="right"
                                                        type="datetime"
                                                        placeholder="??????????????????"
                                                        disabled="true"
                                                        :picker-options="pickerOptions1">
                                                </el-date-picker>
                                            </el-form-item>
                                            <el-form-item label="?????????????????????" prop="StartTime" size="small">
                                                <%--<el-input type="input" v-model="addFrom.implementtime" autocomplete="off"></el-input>--%>
                                                <el-date-picker
                                                        v-model="addFrom.StartTime"
                                                        align="right"
                                                        type="datetime"
                                                        placeholder="????????????" clearable
                                                        :picker-options="pickerOptions1">
                                                </el-date-picker>
                                            </el-form-item>
                                            <el-form-item label="?????????????????????" prop="EndTime" size="small">
                                                <el-date-picker
                                                        v-model="addFrom.EndTime"
                                                        align="right"
                                                        type="datetime"
                                                        placeholder="????????????" clearable
                                                        :picker-options="pickerOptions1"
                                                        @change="EndTimeChanged"
                                                >
                                                </el-date-picker>
                                            </el-form-item>
                                            <el-form-item label="??????????????????" prop="Archived" size="small">
                                                <el-select v-model="addFrom.Archived" filterable
                                                           placeholder="?????????"
                                                           disabled="true">
                                                    <el-option value="1" label="?????????"></el-option>
                                                    <el-option value="0" label="?????????"></el-option>
                                                </el-select>
                                            </el-form-item>

                                        </div>
                                    </fieldset>
                                    <%-- </el-container>
                                     <el-container width="50%">--%>
                                    <fieldset class="dialog-fieldset">
                                        <legend>????????????</legend>
                                        <div>
                                            <el-upload ref="my-upload"
                                                       class="upload" size="small"
                                                       drag
                                                       action="upload.do"
                                                       :before-upload="beforeAvatarUpload"
                                                       :data="filedata"
                                                       :on-remove="removeFile"
                                                       :on-success="successUpload"
                                                       :on-error="errorUpload"
                                                       multiple>
                                                <i class="el-icon-upload"></i>
                                                <div class="el-upload__text">???????????????????????????<em>????????????</em></div>
                                                <div class="el-upload__tip" slot="tip">?????????????????????500Mb</div>
                                            </el-upload>
                                        </div>
                                    </fieldset>
                                    <%--</el-container>--%>


                                </el-container>
                                <el-container>
                                    <fieldset class="dialog-fieldset-select">
                                        <legend>????????????</legend>
                                        <div>
                                            <el-transfer v-model="addFrom.rightList" :data="addFrom.leftData"
                                                         filterable
                                                         target-order="push"
                                                         filter-placeholder="???????????????????????????"
                                                         :titles="['??????????????????', '??????????????????']"
                                                         :button-texts="['??????', '??????']"
                                            ></el-transfer>
                                        </div>
                                    </fieldset>
                                </el-container>

                            </el-form>

                            <%--</div>--%>

                            <div slot="footer" class="dialog-footer">
                                <el-button type="primary" :disabled="disabled_add" @click="_addActiviti">??????</el-button>
                                <el-button :disabled="disabled_add_submit" type="primary"
                                           @click="_submit_add">??????</el-button>
                                <%--<el-button @click="resetForm('addFrom')">??????</el-button>--%>
                                <el-button
                                        @click="addDialogVisible=false"
                                        type="danger">??????
                                </el-button>
                            </div>
                            <%--</div>--%>
                        </el-dialog>
                    </div>

                    <%--????????????--%>
                    <div class="detail-dialog">
                        <el-dialog title="??????????????????????????????" v-loading="detailLoading" top="2.5vh"
                                   :visible.sync="detailDialogVisible"
                                   :modal="false"
                                   :close-on-click-modal="false" :close-on-press-escape="false" :show-close="false"
                                   width="50em">
                            <el-form label-position="right" :model="addFrom" ref="addFrom"
                                     :rules="rules" class="">
                                <el-container>
                                    <%--<el-container width="50%">--%>
                                    <fieldset class="dialog-fieldset">
                                        <legend>????????????</legend>
                                        <div>
                                            <el-form-item label="????????????" prop="Applicant" size="small">
                                                <el-input type="input" v-model="addFrom.Applicant"
                                                          disabled="true"
                                                          clearable></el-input>
                                            </el-form-item>
                                            <el-form-item label="?????????????????????" prop="DangerTaskNum"
                                                          size="small">
                                                <el-input type="input" v-model="addFrom.DangerTaskNum"
                                                          disabled="true"
                                                          clearable autocomplete="off"></el-input>
                                            </el-form-item>
                                            <el-form-item label="??????????????????" prop="DangerTaskName"
                                                          size="small">
                                                <el-input type="input" v-model="addFrom.DangerTaskName"
                                                          disabled="true"
                                                          clearable autocomplete="off"></el-input>
                                            </el-form-item>
                                            <el-form-item label="?????????????????????" prop="DangerTaskLevel" size="small">
                                                <el-select v-model="addFrom.DangerTaskLevel" filterable="true"
                                                           disabled="true"
                                                           placeholder="?????????">
                                                    <el-option value="CompanyDangerTask" label="?????????"></el-option>
                                                    <el-option value="WorkShopDangerTask" label="????????????"></el-option>
                                                </el-select>
                                            </el-form-item>
                                            <el-form-item label="?????????????????????" prop="Category" size="small">
                                                <el-select v-model="addFrom.Category" filterable="true"
                                                           disabled="true"
                                                           placeholder="?????????">
                                                    <el-option v-for="item in SecondaryCategory"
                                                               :value="item.value"
                                                               :label="item.label"></el-option>
                                                </el-select>
                                            </el-form-item>

                                            <el-form-item label="???????????????" prop="ApplyInstitution" size="small">
                                                <el-input type="text" v-model="addFrom.ApplyInstitution"
                                                          disabled="true"
                                                          clearable autocomplete="off"></el-input>
                                                <%--<el-select v-model="addFrom.TaskInstitution" filterable="true" disabled="true"
                                                           placeholder="?????????">
                                                    <el-option v-for="item in TaskInstitutionList"
                                                               :value="item.institutionNum"
                                                               :label="item.institutionName"></el-option>
                                                </el-select>--%>
                                            </el-form-item>
                                            <el-form-item label="???????????????" prop="ApplyingTime" size="small"
                                                          disabled="true">
                                                <el-date-picker
                                                        v-model="addFrom.ApplyingTime"
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
                                                        v-model="addFrom.StartTime"
                                                        align="right"
                                                        type="datetime"
                                                        placeholder="????????????" clearable disabled="true"
                                                        :picker-options="pickerOptions1">
                                                </el-date-picker>
                                            </el-form-item>
                                            <el-form-item label="?????????????????????" prop="EndTime" size="small">
                                                <el-date-picker
                                                        v-model="addFrom.EndTime"
                                                        align="right"
                                                        type="datetime"
                                                        placeholder="????????????" clearable disabled="true"
                                                        :picker-options="pickerOptions1">
                                                </el-date-picker>
                                            </el-form-item>
                                            <el-form-item label="??????????????????" prop="Archived" size="small"
                                                          disabled="true">
                                                <el-select v-model="addFrom.Archived" filterable
                                                           placeholder="?????????"
                                                           disabled="true">
                                                    <el-option value="1" label="?????????"></el-option>
                                                    <el-option value="0" label="?????????"></el-option>
                                                </el-select>
                                            </el-form-item>

                                        </div>
                                    </fieldset>
                                    <%--</el-container>--%>
                                    <%--<el-container width="50%">--%>
                                    <fieldset class="dialog-fieldset">
                                        <legend>????????????</legend>
                                        <div>
                                            <el-table :data="addFrom.filetable">
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
                                                                @click="_viewFile(addFrom.TaskID,scope.row.name)"
                                                                type="text" size="small">??????
                                                        </el-button>

                                                        <el-button
                                                                @click="_downloadone(addFrom.TaskID,scope.row.name)"
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
                            <div slot="footer" class="dialog-footer">
                                <el-button @click="getDetail(listindex-1)" :disabled="listindex==0" type="primary"
                                           style="margin-right: 10px;float: left">
                                    ?????????
                                </el-button>
                                <el-button @click="getDetail(listindex+1)" :disabled="listindex+1==listsize"
                                           type="primary"
                                           style="margin-right: 10px;float: left">
                                    ?????????
                                </el-button>
                                <el-button
                                        @click="detailDialogVisible=false;resetForm('addFrom');"
                                        type="danger">??????
                                </el-button>
                            </div>


                        </el-dialog>
                    </div>

                    <%--???????????? --%>
                    <div class="edit-dialog">
                        <el-dialog :title="editTitle" v-loading="editLoading" top="2.5vh"
                                   :visible.sync="editDialogVisible"
                                   :modal="false"
                                   :close-on-click-modal="false" :close-on-press-escape="false" :show-close="false"
                                   width="50em">
                            <div class="right_content">
                                <el-form label-position="right" :model="addFrom" ref="addFrom"
                                         :rules="rules" class="">
                                    <el-container>
                                        <%--<el-container width="50%">--%>
                                        <fieldset class="dialog-fieldset">
                                            <legend>????????????</legend>
                                            <div>
                                                <el-form-item label="????????????" prop="Applicant" size="small">
                                                    <el-input type="input" v-model="addFrom.Applicant"
                                                              disabled="true"
                                                              clearable></el-input>
                                                </el-form-item>
                                                <el-form-item label="?????????????????????" prop="DangerTaskNum"
                                                              size="small">
                                                    <el-input type="input" v-model="addFrom.DangerTaskNum"
                                                              clearable autocomplete="off"></el-input>
                                                </el-form-item>
                                                <el-form-item label="??????????????????" prop="DangerTaskName"
                                                              size="small">
                                                    <el-input type="input" v-model="addFrom.DangerTaskName"
                                                              clearable autocomplete="off"></el-input>
                                                </el-form-item>
                                                <el-form-item label="?????????????????????" prop="DangerTaskLevel" size="small">
                                                    <el-select v-model="addFrom.DangerTaskLevel" filterable="true" @change="taskLevelChange"
                                                               placeholder="?????????">
                                                        <el-option value="CompanyDangerTask" label="?????????"></el-option>
                                                        <el-option value="WorkShopDangerTask" label="????????????"></el-option>
                                                    </el-select>
                                                </el-form-item>
                                                <el-form-item label="?????????????????????" prop="Category" size="small">
                                                    <el-select v-model="addFrom.Category" filterable="true"
                                                               placeholder="?????????">
                                                        <el-option v-for="item in SecondaryCategory"
                                                                   :value="item.value"
                                                                   :label="item.label"></el-option>
                                                    </el-select>
                                                </el-form-item>
                                                <el-form-item label="???????????????" prop="ApplyInstitution" size="small">
                                                    <el-input type="input" v-model="addFrom.ApplyInstitution"
                                                              disabled="true"
                                                              clearable></el-input>
                                                </el-form-item>
                                                <el-form-item label="???????????????" prop="ApplyingTime" size="small">
                                                    <el-date-picker
                                                            v-model="addFrom.ApplyingTime"
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
                                                            v-model="addFrom.StartTime"
                                                            align="right"
                                                            type="datetime"
                                                            placeholder="????????????" clearable
                                                            :picker-options="pickerOptions1">
                                                    </el-date-picker>
                                                </el-form-item>
                                                <el-form-item label="?????????????????????" prop="EndTime" size="small">
                                                    <el-date-picker
                                                            v-model="addFrom.EndTime"
                                                            align="right"
                                                            type="datetime"
                                                            placeholder="????????????" clearable
                                                            :picker-options="pickerOptions1">
                                                    </el-date-picker>
                                                </el-form-item>
                                                <el-form-item label="??????????????????" prop="Archived" size="small">
                                                    <el-select v-model="addFrom.Archived" filterable
                                                               placeholder="?????????"
                                                               disabled="true">
                                                        <el-option value="1" label="?????????"></el-option>
                                                        <el-option value="0" label="?????????"></el-option>
                                                    </el-select>
                                                </el-form-item>

                                            </div>
                                        </fieldset>
                                        <%-- </el-container>
                                         <el-container width="50%">--%>
                                        <fieldset class="dialog-fieldset">
                                            <legend>????????????</legend>
                                            <div>
                                                <el-table :data="addFrom.filetable">
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
                                                                    @click="_viewFile(addFrom.TaskID,scope.row.name)"
                                                                    type="text" size="small">??????
                                                            </el-button>

                                                            <el-button
                                                                    @click="_downloadone(addFrom.TaskID,scope.row.name)"
                                                                    type="text" size="small">??????
                                                            </el-button>
                                                            <el-button
                                                                    @click="_deleteFile(addFrom.TaskID,scope.row.name)"
                                                                    type="text" size="small">??????
                                                            </el-button>
                                                        </template>
                                                    </el-table-column>
                                                </el-table>
                                            </div>
                                            <div>
                                                <el-upload ref="my-upload"
                                                           class="upload" size="small"
                                                           action="upload"
                                                           :before-upload="beforeAvatarUpload"
                                                           :data="filedata"
                                                           :file-list="fileList"
                                                           :on-remove="removeFile1"
                                                           :on-success="successUpload1"
                                                           multiple>
                                                    <el-button size="small" type="primary">????????????<i
                                                            class="el-icon-upload el-icon--right"></i></el-button>
                                                    <div slot="tip" class="el-upload__tip">???????????????????????????500Mb</div>
                                                </el-upload>

                                            </div>
                                        </fieldset>
                                        <%--</el-container>--%>


                                    </el-container>
                                    <el-container>
                                        <fieldset class="dialog-fieldset-select">
                                            <legend>????????????</legend>
                                            <div>
                                                <el-transfer v-model="addFrom.rightList" :data="addFrom.leftData"
                                                             filterable
                                                             target-order="push"
                                                             filter-placeholder="???????????????????????????"
                                                             :titles="['??????????????????', '??????????????????']"
                                                             :button-texts="['??????', '??????']"
                                                ></el-transfer>
                                            </div>
                                        </fieldset>
                                    </el-container>

                                </el-form>
                            </div>
                            <div slot="footer" class="dialog-footer">
                                <el-button id="btn-add" :disabled="disabled_save" type="primary" @click="_save">??????
                                </el-button>
                                <el-button id="btn-submit" :disabled="disabled_edit_submit" type="primary"
                                           @click="_submit_edit">??????
                                </el-button>
                                <el-button
                                        @click="editDialogVisible=false;resetForm('addFrom')"
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
                                    <tr v-for="(record,index) in HistoryRecord" :id="record.ApprovalID" align="center">
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
                                        <td >{{record.Finished == '0'?'??????':record.Finished ==
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
