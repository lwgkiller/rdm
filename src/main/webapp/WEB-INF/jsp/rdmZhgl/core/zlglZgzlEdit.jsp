<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2021/4/13
  Time: 19:08
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>创建中国专利</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmZhgl/zlglZgzlEdit.js?version=${static_res_version}"
            type="text/javascript"></script>
</head>
<body>
<div id="toolBar" class="topToolBar">
    <div>
        <a id="saveNewZgzl" class="mini-button" style="" onclick="saveNewZgzl()">保存</a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin: 0 auto;width: 100%;">
        <form id="formZgzl" method="post">
            <input id="planId" name="planId" class="mini-hidden"/>
            <input id="zgzlId" name="zgzlId" class="mini-hidden"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0">
                <tr>
                    <td style="width: 12%">部门：</td>
                    <td style="width: 20%">
                        <input id="departmentId" name="departmentId" class="mini-dep rxc" plugins="mini-dep"
                               data-options="{'single':'true','config':{'type':'','grouplevel':'','groupid':'','groupidText':''}}"
                               style="width:98%;height:34px" allowinput="false" label="部门" textname="deptName"
                               length="500"
                               maxlength="500" minlen="0" single="true" required="false" initlogindep="false"
                               showclose="false"
                               mwidth="160" wunit="px" mheight="34" hunit="px"/>
                    </td>
                    <td style="width: 12%">专业：</td>
                    <td style="width: 20%;">
                        <input id="zhuanYe" name="zhuanYe" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="width: 12%">提案号：</td>
                    <td style="width: 20%">
                        <input id="billNo" name="billNo" class="mini-textbox" style="width:98%;"/>
                    </td>

                </tr>
                <tr>
                    <td style="width: 12%">提案名称：</td>
                    <td style="width: 20%;">
                        <input id="reportName" name="reportName" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="width: 12%">专利名称：</td>
                    <td style="width: 20%;min-width:140px">
                        <input id="patentName" name="patentName" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="width: 12%">专利类型：</td>
                    <td style="width: 20%;">
                        <input id="zllxId" name="zllxId" class="mini-combobox" style="width:98%;"
                               textField="enumName" valueField="id" emptyText="请选择..."
                               required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 12%">专利(申请)号：</td>
                    <td style="width: 20%;">
                        <input id="applicationNumber" name="applicationNumber" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="width: 12%">发明人：</td>
                    <td style="width: 20%;">
                        <input id="theInventors" name="theInventors" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="width: 12%">专利权人/申请人：</td>
                    <td style="width: 20%;">
                        <input id="thepatentee" name="thepatentee" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 12%">审批日：</td>
                    <td style="width: 20%;">
                        <input id="examinationApproval" name="examinationApproval" class="mini-datepicker"
                               format="yyyy-MM-dd"
                               showTime="false" showOkButton="true" showClearButton="false" style="width:98%;"/>
                    </td>
                    <td style="width: 12%">委案日：</td>
                    <td style="width: 20%;min-width:140px">
                        <input id="byCase" name="byCase" class="mini-datepicker" format="yyyy-MM-dd"
                               showTime="true" showOkButton="true" showClearButton="false" style="width:98%;"/>
                    </td>
                    <td style="width: 12%">申请日：</td>
                    <td style="width: 20%;">
                        <input id="filingdate" name="filingdate" class="mini-datepicker" format="yyyy-MM-dd"
                               showTime="true" showOkButton="true" showClearButton="false" style="width:98%;"/>
                    </td>

                </tr>
                <tr>
                    <td style="width: 12%">授权通知发文日：</td>
                    <td style="width: 20%;min-width:140px">
                        <input id="authorizationDate" name="authorizationDate" class="mini-datepicker"
                               format="yyyy-MM-dd"
                               showTime="true" showOkButton="true" showClearButton="false" style="width:98%;"/>
                    </td>
                    <td style="width: 12%">授权日期：</td>
                    <td style="width: 20%;">
                        <input id="authionization" name="authionization" class="mini-datepicker" format="yyyy-MM-dd"
                               showTime="true" showOkButton="true" showClearButton="false" style="width:98%;"/>
                    </td>
                    <td style="width: 12%">专利证书号：</td>
                    <td style="width: 20%;">
                        <input id="specifyCountry" name="specifyCountry" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 12%">是否存在许可他人使用：</td>
                    <td style="width: 20%;min-width:140px">
                        <input id="permissionOthers" name="permissionOthers" class="mini-combobox" style="width:98%;"
                               textField="enumName" valueField="id" emptyText="请选择..."
                               required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."/>
                    </td>
                    <td style="width: 12%">被许可实用的单位或人员名称：</td>
                    <td style="width: 20%;min-width:140px">
                        <input id="personPermitted" name="personPermitted" class="mini-textbox" style="width:98%;"/>
                    </td>

                    <td style="width: 12%">是否质押：</td>
                    <td style="width: 20%;">
                        <input id="whetherPledge" name="whetherPledge" class="mini-combobox" style="width:98%;"
                               textField="enumName" valueField="id" emptyText="请选择..."
                               required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."/>
                    </td>

                </tr>
                <tr>
                    <td style="width: 12%">被转让公司名称：</td>
                    <td style="width: 20%;">
                        <input id="transferredCompany" name="transferredCompany" class="mini-textbox"
                               style="width:98%;"/>
                    </td>
                    <td style="width: 12%">权利要求项数：</td>
                    <td style="width: 20%;min-width:140px">
                        <input id="claimsNumber" name="claimsNumber" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="width: 12%">案件状态：</td>
                    <td style="width: 20%;">
                        <input id="gnztId" name="gnztId" class="mini-combobox" style="width:98%;"
                               textField="enumName" valueField="id" emptyText="请选择..."
                               required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."/>
                    </td>

                </tr>
                <tr>
                    <td style="width: 12%">专利权丧失发文日：</td>
                    <td style="width: 20%;min-width:140px">
                        <input id="patentDate" name="patentDate" class="mini-datepicker" dateFormat="yyyy-MM-dd"
                               showTime="true" showOkButton="true" showClearButton="false" style="width:98%;"/>
                    </td>
                    <td style="width: 12%">失效日期：</td>
                    <td style="width: 20%;">
                        <input id="expiryDate" name="expiryDate" class="mini-datepicker" dateFormat="yyyy-MM-dd"
                               showTime="true" showOkButton="true" showClearButton="false" style="width:98%;"/>
                    </td>
                    <td style="width: 12%">失效原因：</td>
                    <td style="width: 20%;min-width:140px">
                        <input id="failureReason" name="failureReason" class="mini-textbox" style="width:98%;"/>
                    </td>

                </tr>
                <tr>
                    <td style="width: 12%">代理机构名称：</td>
                    <td style="width: 20%;">
                        <input id="agencyName" name="agencyName" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="width: 12%">代理人：</td>
                    <td style="width: 20%;min-width:140px">
                        <input id="agentThe" name="agentThe" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="width: 14%">技术分支：</td>
                    <td style="width: 30%;">
                        <input id="jsfz" name="jsfz" style="width:98%;" class="mini-buttonedit" showClose="true"
                               oncloseclick="onJsfzCloseClick()" textname="选择技术分支" allowInput="true"
                               onbuttonclick="selectJsfz()"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 12%">专利所属公司项目或产品所处阶段：</td>
                    <td style="width: 20%;min-width:140px">
                        <input id="companyBelongs" name="companyBelongs" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="width: 12%">IPC分类号：</td>
                    <td style="width: 20%;min-width:140px">
                        <input id="ipcFlh" name="ipcFlh" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="width: 12%">IPC主分类号：</td>
                    <td style="width: 20%;min-width:140px">
                        <input id="ipcZflh" name="ipcZflh" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 12%">奖励类别：</td>
                    <td style="width: 20%;">
                        <input id="jllb" name="jllb" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="width: 12%">授权奖励金额：</td>
                    <td style="width: 20%;">
                        <input id="authorizedReward" name="authorizedReward" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="width: 12%">授权奖励时间：</td>
                    <td style="width: 20%;min-width:140px">
                        <input id="authirizedTime" name="authirizedTime" class="mini-datepicker" dateFormat="yyyy-MM-dd"
                               showTime="true" showOkButton="true" showClearButton="false" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 12%">员工画像相关发明人：</td>
                    <td style="width: 20%;min-width:140px">
                        <input id="myCompanyUserIds" name="myCompanyUserIds" textname="myCompanyUserNames"
                               class="mini-user rxc" plugins="mini-user" style="width:98%;"
                               allowinput="false" label="可见范围" length="1000" maxlength="1000" mainfield="no"
                               single="false"/>
                    </td>
                    <td style="width: 12%">备注：</td>
                    <td style="width: 20%;">
                        <input id="beiZhu" name="beiZhu" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="width: 12%">专利证书附件：</td>
                    <td style="width: 20%;">
                        <a id="yyaluploadFile" class="mini-button" style="margin-bottom: 5px;margin-top: 5px;"
                           onclick="addZhengShuFile">附件</a>
                    </td>
                </tr>
                <tr>
                    <td style="  width: 12%">应用该专利的产品<br>(至多选择60个产品)：</td>
                    <td style="width: 20%">
                        <input id="model" style="width:98%;" class="mini-buttonedit" showClose="true"
                               oncloseclick="onRelModelCloseClick()"
                               name="modelId" textname="modelName" allowInput="false"
                               onbuttonclick="selectModelClick()"/>
                    </td>
                    <td style="width: 12%">产出该专利的项目名称：</td>
                    <td style="width: 20%;">
                        <input id="project" name="projectId" textname="projectName" class="mini-buttonedit"
                               style="width:98%;height:34px"
                               allowInput="false" onbuttonclick="addRelatedProject()" showClose="true"
                               oncloseclick="relatedCloseClick()"/>
                    </td>
                    <td style="width: 10%">成果计划：</td>
                    <td style="width:auto">
                        <input id="plan" name="planId" textname="planName" class="mini-buttonedit"
                               style="width:98%;height:34px"
                               allowInput="false" onbuttonclick="addRelatedPlan()" showClose="true"
                               oncloseclick="relatedPlanCloseClick()"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 7%">关联技术交底书：</td>
                    <td>
                        <input  id="jsjdsId" name="jsjdsId" textname="zlName" style="width:98%;" property="editor"
                                class="mini-buttonedit" showClose="true"
                                oncloseclick="onSelectJdsCloseClick" allowInput="false"
                                onbuttonclick="selectJds()"/>
                    </td>
                </tr>
                <tr id="jiaofeiTr" style="display: none">
                    <td style="text-align: center;height: 300px">缴费部分：</td>
                    <td colspan="5">
                        <div class="mini-toolbar" id="planButtons">
                            <a id="addJiaoFei" class="mini-button" style="margin-bottom: 5px;margin-top: 5px"
                               onclick="addZGJiaoFei">添加缴费</a>
                            <a id="saveJiaoFei" class="mini-button btn-yellow"
                               style="margin-bottom: 5px;margin-top: 5px" onclick="saveZGJiaoFei">保存缴费</a>
                            <a id="deleteMJiaoFei" class="mini-button btn-red"
                               style="margin-bottom: 5px;margin-top: 5px" onclick="deleteMJiaoFei">删除缴费</a>
                        </div>
                        <div id="jiaoFeiListGrid" class="mini-datagrid" style="width: 98%; height: 85%"
                             allowResize="false" allowCellWrap="true"
                             idField="id" allowCellEdit="true" allowCellSelect="true" allowSortColumn="false"
                             multiSelect="false" showPager="false" showColumnsMenu="false" allowcelledit="true"
                             allowcellselect="true"
                             allowAlternating="true" oncellvalidation="onCellValidation">
                            <div property="columns">
                                <div type="checkcolumn" width="10"></div>
                                <div type="indexcolumn" headerAlign="center" align="center" width="10">序号</div>
                                <div field="jflbId" displayField="enumName" align="center" headerAlign="center"
                                     width="50" renderer="render">缴纳费用类别
                                    <input property="editor" class="mini-combobox" style="width:98%;"
                                           textField="enumName" valueField="id" emptyText="请选择..."
                                           required="false" allowInput="false" showNullItem="true"
                                           nullItemText="请选择..."/>
                                </div>
                                <div field="paymenAmount" align="center" headerAlign="center" width="50"
                                     renderer="render">缴费金额
                                    <input property="editor" class="mini-textarea"/>
                                </div>
                                <div field="paymentTime" align="center" headerAlign="center" width="50"
                                     dateFormat="yyyy-MM-dd">缴费时间
                                    <input property="editor" class="mini-datepicker" dateFormat="yyyy-MM-dd"
                                           showTime="true" showOkButton="true" showClearButton="false"/>
                                </div>
                                <div field="meetingPlanCompletion" align="center" headerAlign="center" width="50"
                                     renderer="renderAttachFile">缴费票据
                                    <%--<input property="editor"  class="mini-button" style="margin-bottom: 5px;margin-top: 5px;" onclick="addJiaoFeiFile" />--%>
                                </div>
                            </div>
                        </div>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>
<div id="projectWindow" title="关联项目信息选择窗口" class="mini-window" style="width:1200px;height:700px;"
     showModal="true" showFooter="false" allowResize="true">
    <div class="mini-toolbar">
        <div class="searchBox">
            <form id="searchGrid" class="search-form" style="margin-bottom: 25px">
                <ul>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">项目名称: </span>
                        <input class="mini-textbox" id="relatedProjectName" name="relatedProjectName"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">项目编号: </span>
                        <input class="mini-textbox" id="projectNumber" name="projectNumber">
                    </li>
                    <li style="margin-right: 15px">
                        <a class="mini-button" style="margin-right: 5px" plain="true"
                           onclick="searchProcessData()">查询</a>
                        <a class="mini-button btn-red" style="margin-right: 5px" plain="true"
                           onclick="cleanProcessData()">清空查询</a>
                    </li>
                    <li style="display: inline-block;float: right;">
                        <a id="importBtn" class="mini-button" onclick="choseRelatedProject()">确认</a>
                        <a id="closeProjectWindow" class="mini-button btn-red" onclick="closeProjectWindow()">关闭</a>
                    </li>
                </ul>
            </form>
        </div>
    </div>
    <div class="mini-fit" style="height: 100%;">
        <div id="projectListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
             onlyCheckSelection="true" idField="projectId" allowCellWrap="true"
             url="${ctxPath}/xcmgProjectManager/core/xcmgProject/getProjectList.do"
             multiSelect="false" showColumnsMenu="true" sizeList="[20,50,100,200]" pageSize="20" allowAlternating="true"
             pagerButtons="#pagerButtons">
            <div property="columns">
                <div type="checkcolumn" width="20"></div>
                <div field="projectId" visible="false"></div>
                <div type="indexcolumn" headerAlign="center" align="center" width="30">序号</div>
                <div field="projectName" sortField="projectName" width="170" headerAlign="center" align="center"
                     allowSort="true">项目名称
                </div>
                <div field="number" sortField="number" width="110" headerAlign="center" align="center" allowSort="true">
                    项目编号
                </div>
                <div field="respMan" sortField="respMan" width="50" headerAlign="center" align="center"
                     allowSort="false">项目负责人
                </div>
                <div field="status" sortField="status" width="50" allowSort="true" headerAlign="center" align="center"
                     renderer="onStatusRenderer">项目状态
                </div>
            </div>
        </div>
    </div>
</div>
<div id="selectModelWindow" title="选择设计型号" class="mini-window" style="width:1200px;height:550px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="true">
    <div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 25px"
         borderStyle="border-left:0;border-top:0;border-right:0;">
        <span class="text" style="width:auto">设计型号: </span><input
            class="mini-textbox" style="width: 120px" id="designModel" name="designModel"/>
        <span class="text" style="width:auto">销售型号: </span><input
            class="mini-textbox" style="width: 120px" id="saleModel" name="saleModel"/>
        <span class="text" style="width:auto">产品所: </span><input
            class="mini-textbox" style="width: 120px" id="departName" name="departName"/>
        <span class="text" style="width:auto">产品主管: </span><input
            class="mini-textbox" style="width: 120px" id="productManagerName" name="productManagerName"/>
        <a class="mini-button" iconCls="icon-search" plain="true" onclick="searchModel()">查询</a>
        <li style="display: inline-block;float: right;">
            <a class="mini-button"  onclick="selectModelOK()">确认</a>
            <a class="mini-button btn-red"  onclick="selectModelHide()">关闭</a>
        </li>
    </div>
    <div class="mini-fit">
        <div id="selectModelListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
             idField="id" showColumnsMenu="false" onload="onGridLoad"
             allowAlternating="true" showPager="true" multiSelect="true"
             url="${ctxPath}/world/core/productSpectrum/dataListQuery.do?">
            <div property="columns">
                <div type="checkcolumn" width="20"></div>
                <div field="designModel" width="150" headerAlign="center" align="center" renderer="render"
                     allowSort="true">
                    设计型号
                </div>
                <div field="departName" width="120" headerAlign="center" align="center" renderer="render"
                     allowSort="true">
                    产品所
                </div>
                <div field="productManagerName" width="80" headerAlign="center" align="center" renderer="render"
                     allowSort="true">
                    产品主管
                </div>
                <div field="saleModel" width="120" headerAlign="center" align="center" renderer="render"
                     allowSort="true">
                    销售型号
                </div>
            </div>
        </div>
    </div>
</div>
<div id="planWindow" title="成果计划选择窗口" class="mini-window" style="width:1200px;height:700px;"
     showModal="true" showFooter="false" allowResize="true">
    <div class="mini-toolbar">
        <div class="searchBox">
            <form class="search-form" style="margin-bottom: 25px">
                <ul>
                    <li style="display: inline-block;float: right;">
                        <a id="choseRelatedPlan" class="mini-button" onclick="choseRelatedPlan()">确认</a>
                        <a id="closePlanWindow" class="mini-button btn-red" onclick="closePlanWindow()">关闭</a>
                    </li>
                </ul>
            </form>
        </div>
    </div>
    <div class="mini-fit" style="height: 100%;">
        <div id="planListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
             onlyCheckSelection="true" idField="id" allowCellWrap="true"
             url="${ctxPath}/zhgl/core/jsjds/selectProjectPlan.do"
             multiSelect="false" showColumnsMenu="true" showPager="false">
            <div property="columns">
                <div type="checkcolumn" width="20"></div>
                <div field="id" visible="false"></div>
                <div field="projectId" visible="false"></div>
                <div type="indexcolumn" headerAlign="center" align="center" width="30">序号</div>
                <div field="description"  width="170" headerAlign="center" align="center"
                     allowSort="true">计划描述
                </div>
                <div field="deptName" width="110" headerAlign="center" align="center" allowSort="true">
                    部门
                </div>
                <div field="typeName"  width="50" headerAlign="center" align="center"
                     allowSort="false">类别
                </div>
                <div field="output_time"  headerAlign="center" align="center"  dateFormat="yyyy-MM-dd" width="80">
                    预计产出时间
                </div>
            </div>
        </div>
    </div>
</div>
<div id="selectZlWindow" title="选择关联的技术交底书" class="mini-window" style="width:750px;height:450px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
    <input id="parentInputScene" style="display: none"/>
    <div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px"
         borderStyle="border-left:0;border-top:0;border-right:0;">
        <span style="font-size: 14px;color: #777">提案名称: </span>
        <input class="mini-textbox" width="130" id="zlName" style="margin-right: 15px"/>
        <a class="mini-button" iconCls="icon-search" plain="true" onclick="searchJds()">查询</a>
    </div>
    <div class="mini-fit">
        <div id="zlListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
             onrowdblclick="onRowDblClick"
             idField="id" showColumnsMenu="false" sizeList="[20,50,100,200]" pageSize="20"
             allowAlternating="true" pagerButtons="#pagerButtons" url="${ctxPath}/zhgl/core/jsjds/getJsjdsList.do">
            <div property="columns">
                <div type="checkcolumn" width="25px"></div>
                <div field="zlName" sortField="zlName" width="100" headerAlign="center" align="center" allowSort="true">
                    提案名称
                </div>
                <div field="zllx" width="50" headerAlign="center" align="center" allowSort="false"
                     renderer="onLXRenderer">专利类型
                </div>
            </div>
        </div>
    </div>
    <div property="footer" style="padding:5px;height: 35px">
        <table style="width:100%;height: 100%">
            <tr>
                <td style="width:120px;text-align:center;">
                    <input type="button" style="height: 25px;width: 70px" value="确定" onclick="selectJdsOK()"/>
                    <input type="button" style="height: 25px;width: 70px" value="取消" onclick="selectJdsHide()"/>
                </td>
            </tr>
        </table>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var status = "${status}";
    var action = "${action}";
    var zlUseCtxPath = "${ctxPath}";
    var formZgzl = new mini.Form("#formZgzl");
    var zgzlId = "${zgzlId}";
    var nodeVarsStr = '${nodeVars}';
    var currentUserName = "${currentUserName}";
    var currentTime = "${currentTime}";
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";
    var zgzlObj =${zgzlObj};
    var currentUserId = "${currentUserId}";
    var currentUserName = "${currentUserName}";
    var currentUserMainGroupId = "${currentUserMainGroupId}";
    var currentUserMainGroupName = "${currentUserMainGroupName}";
    var jiaoFeiListGrid = mini.get("jiaoFeiListGrid");
    var showJiaofei =${showJiaofei};
    var projectWindow = mini.get("projectWindow");
    var projectListGrid = mini.get("projectListGrid");
    var planWindow = mini.get("planWindow");
    var planListGrid = mini.get("planListGrid");
    var selectModelWindow = mini.get("selectModelWindow");
    var selectModelListGrid = mini.get("selectModelListGrid");
    var selectRowIds = [];
    var selectRowNames = [];
    var selectZlWindow = mini.get("selectZlWindow");
    var zlListGrid=mini.get("zlListGrid");


    function onLXRenderer(e) {
        var record = e.record;
        var zllx = record.zllx;

        var arr = [ {'key' : 'FM','value' : '发明'},
            {'key' : 'SYXX','value' : '实用新型'},
            {'key' : 'WGSJ','value' : '外观设计'}
        ];
        return $.formatItemValue(arr,zllx);
    }

    jiaoFeiListGrid.on("cellbeginedit", function (e) {
        var record = e.record;
        if (action == 'edit' || action == '') {
            if (e.field == "jflbId") {
                e.editor.setData(achieveTypeList);
            }
        } else {
            e.editor.setEnabled(false);
        }

    });

    function renderAttachFile(e) {
        var record = e.record;
        var fyid = record.fyId;
        var zlId = record.zlId;
        if (!fyid) {
            return '';
        }
        var s = '<a href="#" style="color:#44cef6;text-decoration:underline;" ' +
            'onclick="addJiaoFeiFile(\'' + fyid + '\',\'' + zlId + '\')">附件</a>';
        return s;
    }


    function selectModelClick() {
        var modelId = mini.get("model").getValue();
        var modelName = mini.get("model").getText();
        if(modelId){
            selectRowIds = modelId.split(',');
            selectRowNames = modelName.split(',');
        }
        selectModelWindow.show();
        searchModel();
    }

    function searchModel() {
        var queryParam = [];
        //其他筛选条件
        var designModel = $.trim(mini.get("designModel").getValue());
        if (designModel) {
            queryParam.push({name: "designModel", value: designModel});
        }
        var departName = $.trim(mini.get("departName").getValue());
        if (departName) {
            queryParam.push({name: "departName", value: departName});
        }
        var productManagerName = $.trim(mini.get("productManagerName").getValue());
        if (productManagerName) {
            queryParam.push({name: "productManagerName", value: productManagerName});
        }
        var saleModel = $.trim(mini.get("saleModel").getValue());
        if (saleModel) {
            queryParam.push({name: "saleModel", value: saleModel});
        }
        var data = {};
        data.filter = mini.encode(queryParam);
        //查询
        selectModelListGrid.load(data);
    }

    function selectModelOK() {
        if(selectRowIds.length>60){
            mini.alert("最多选择60个产品!")
            return;
        }
        if (selectRowIds) {
            mini.get("model").setValue(selectRowIds.toString());
            mini.get("model").setText(selectRowNames.toString());
            selectModelHide();
        }
    }

    function selectModelHide() {
        selectModelWindow.hide();
        mini.get("saleModel").setValue('');
        mini.get("productManagerName").setValue('');
        mini.get("departName").setValue('');
        mini.get("designModel").setValue('');
        selectRowIds.splice(0, selectRowIds.length);
        selectRowNames.splice(0, selectRowNames.length);
        selectModelListGrid.clearSelect();
    }

    function onRelModelCloseClick() {
        mini.get("model").setValue('');
        mini.get("model").setText('');
        selectRowIds.splice(0, selectRowIds.length);
        selectRowNames.splice(0, selectRowNames.length);
        selectModelListGrid.clearSelect();
    }

    function onGridLoad(e) {
        if (selectRowIds.length>0) {
            for (var i = 0; i < selectModelListGrid.data.length; i++) {
                if (selectRowIds.indexOf(selectModelListGrid.data[i].id) != -1) {
                    selectModelListGrid.setSelected(selectModelListGrid.getRow(i));
                }
            }
        }
    }


    selectModelListGrid.on("select", function (e) {
        var rec = e.record;
        var designModel = rec.designModel;
        var id = rec.id;
        if (selectRowIds.indexOf(id) == -1) {
            selectRowIds.push(id);
            selectRowNames.push(designModel);
        }
    });

    selectModelListGrid.on("deselect", function (e) {
        var rec = e.record;
        var designModel = rec.designModel;
        var id = rec.id;
        delItem(designModel, selectRowNames);
        delItem(id, selectRowIds);
    });

    function delItem(item, list) {
        // 表示先获取这个元素的下标，然后从这个下标开始计算，删除长度为1的元素
        list.splice(list.indexOf(item), 1)
    }

    //添加关联项目信息展开窗口
    function addRelatedProject() {
        projectWindow.show();
        searchProcessData();
    }

    //关闭关联项目选址页面
    function closeProjectWindow() {
        mini.get("relatedProjectName").setValue('');
        mini.get("projectNumber").setValue('');
        projectWindow.hide();
    }

    function cleanProcessData() {
        mini.get("relatedProjectName").setValue('');
        mini.get("projectNumber").setValue('');
        searchProcessData();
    }

    //关联项目查询
    function searchProcessData() {
        var projectName = mini.get("relatedProjectName").getValue();
        var number = mini.get("projectNumber").getValue();
        var paramArray = [{name: "projectName", value: projectName}, {name: "number", value: number}];
        var data = {};
        data.filter = mini.encode(paramArray);
        projectListGrid.load(data);
    }

    //关联项目写入
    function choseRelatedProject(e) {
        var row = projectListGrid.getSelected();
        mini.get("project").setValue(row.projectId);
        mini.get("project").setText(row.projectName);
        mini.get("plan").setValue('');
        mini.get("plan").setText('');
        closeProjectWindow();
    }

    function relatedCloseClick() {
        mini.get("project").setValue("");
        mini.get("project").setText("");
        mini.get("plan").setValue("");
        mini.get("plan").setText("");
    }

    function onStatusRenderer(e) {
        var record = e.record;
        var status = record.status;
        var arr = [{'key': 'DRAFTED', 'value': '草稿', 'css': 'orange'},
            {'key': 'RUNNING', 'value': '运行中', 'css': 'green'},
            {'key': 'SUCCESS_END', 'value': '成功结束', 'css': 'blue'},
            {'key': 'DISCARD_END', 'value': '作废', 'css': 'red'},
            {'key': 'ABORT_END', 'value': '异常中止结束', 'css': 'red'},
            {'key': 'PENDING', 'value': '挂起', 'css': 'gray'}
        ];

        return $.formatItemValue(arr, status);
    }

    function addRelatedPlan() {
        var projectId = mini.get("project").getValue();
        if(!projectId){
            mini.alert("请先选择产出该专利的项目名称")
            return;
        }
        planWindow.show();
        searchPlan(projectId);
    }

    function searchPlan(projectId) {
        var queryParam = [];
        //其他筛选条件
        var data = {};
        queryParam.push({name: "projectId", value: projectId});
        data.filter = mini.encode(queryParam);
        //查询
        planListGrid.load(data);
    }

    function choseRelatedPlan() {
        var row = planListGrid.getSelected();
        mini.get("plan").setValue(row.id);
        mini.get("plan").setText(row.description);
        closePlanWindow();
    }

    function closePlanWindow() {
        planWindow.hide();
    }

    function relatedPlanCloseClick() {
        mini.get("plan").setValue('');
        mini.get("plan").setText('');
    }

    function selectJds(inputScene) {
        $("#parentInputScene").val(inputScene);
        selectZlWindow.show();
        searchJds();
    }

    //查询
    function searchJds() {
        var queryParam = [];
        //其他筛选条件
        var zlName = $.trim(mini.get("zlName").getValue());
        if (zlName) {
            queryParam.push({name: "zlName", value: zlName});
        }
        var data = {};
        data.filter = mini.encode(queryParam);
        data.pageIndex = zlListGrid.getPageIndex();
        data.pageSize = zlListGrid.getPageSize();
        data.sortField = zlListGrid.getSortField();
        data.sortOrder = zlListGrid.getSortOrder();
        //查询
        zlListGrid.load(data);
    }

    function onRowDblClick() {
        selectJdsOK();
    }

    function selectJdsOK() {
        var selectRow=zlListGrid.getSelected();
        if(!selectRow || selectRow.length==0) {
            mini.alert("请选择一条数据");
            return;
        }
        mini.get("jsjdsId").setValue(selectRow.jsjdsId);
        mini.get("jsjdsId").setText(selectRow.zlName);
        selectJdsHide();
    }
    function selectJdsHide() {
        selectZlWindow.hide();
        mini.get("zlName").setValue('');
    }

    function onSelectJdsCloseClick(e) {
        mini.get("jsjdsId").setValue('');
        mini.get("jsjdsId").setText('');
    }
</script>
</body>
</html>
