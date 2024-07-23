<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>认证过程审批单</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/wwrz/wwrzTestApplyEdit.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
    <style type="text/css">
        fieldset {
            border: solid 1px #aaaaaab3;
            min-width: 920px;
        }

        .hideFieldset {
            border-left: 0;
            border-right: 0;
            border-bottom: 0;
            margin-top: 15px;
        }

        .hideFieldset .fieldset-body {
            display: none;
        }
    </style>
</head>

<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="processInfo" style="display: none" class="mini-button" onclick="processInfo()">流程信息</a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin:0 auto; width: 100%;">
        <form id="applyForm" method="post">
            <input id="id" name="id" class="mini-hidden"/>
            <input id="taskId_" name="taskId_" class="mini-hidden"/>
            <input id="instId" name="instId" class="mini-hidden"/>
            <input id="applyType" name="applyType" class="mini-hidden"/>
            <fieldset id="fdBaseInfo">
                <legend>
                    <label style="font-size:17px">
                        <input type="checkbox" checked id="checkboxBaseInfo"
                               onclick="toggleFieldSet(this, 'fdBaseInfo')" hideFocus/>
                        项目基本信息
                    </label>
                </legend>
                <div class="fieldset-body" style="margin: 10px 10px 10px 10px">
                    <table class="table-detail grey" cellspacing="1" cellpadding="0">
                        <caption>
                            认证过程审批单
                        </caption>
                        <tr>
                            <td style="text-align: center;width: 20%">认证计划：</td>
                            <td colspan="1">
                                <input id="planId" style="width:98%;" class="mini-buttonedit" showClose="true"
                                       name="planId" textname="planCode" allowInput="false"
                                       onbuttonclick="selectPlanName()"/>
                            </td>
                            <td style="text-align: center;width: 20%">销售型号<span style="color: #ff0000">*</span>：</td>
                            <td colspan="1">
                                <input id="productModel" required name="productModel" class="mini-textbox"
                                       style="width:98%;"/>
                            </td>
                        </tr>
                        <tr>
                            <td align="center" style="white-space: nowrap;">
                                产品类型<span style="color: #ff0000">*</span>：
                            </td>
                            <td align="center" rowspan="1">
                                <input id="productType" name="productType" class="mini-combobox rxc"
                                       plugins="mini-combobox"
                                       style="width:100%;height:34px" label="产品类型："
                                       length="50"
                                       only_read="false" required="true" allowinput="false" mwidth="100"
                                       wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                                       textField="text" valueField="key_" emptyText="请选择..."
                                       url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=CPLX"
                                       nullitemtext="请选择..." emptytext="请选择..."/>
                            </td>
                            <td align="center" style="white-space: nowrap;">
                                司机室形式<span style="color: #ff0000">*</span>：
                            </td>
                            <td align="center" rowspan="1">
                                <input id="cabForm" name="cabForm" class="mini-combobox rxc" plugins="mini-combobox"
                                       style="width:100%;height:34px" label="司机室形式："
                                       length="50"
                                       only_read="false" required="true" allowinput="false" mwidth="100"
                                       wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                                       textField="text" valueField="key_" emptyText="请选择..."
                                       url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=SJSXS"
                                       nullitemtext="请选择..." emptytext="请选择..."/>
                            </td>
                        </tr>
                        <tr>
                            <td align="center" style="white-space: nowrap;">
                                产品主管<span style="color: #ff0000">*</span>：
                            </td>
                            <td align="center" colspan="1" rowspan="1">
                                <input id="productLeader" name="productLeader" textname="productLeaderName"
                                       class="mini-user rxc" plugins="mini-user" style="width:98%;height:34px;"
                                       allowinput="false" required="true"
                                       label="产品主管" length="50" mainfield="no" single="true"/>
                            </td>
                            <td style="text-align: center;width: 20%">TDM申请单号<span style="color: #ff0000">*</span>：</td>
                            <td colspan="1">
                                <input id="applyNo" required name="applyNo" class="mini-textbox" style="width:98%;"/>
                            </td>
                        </tr>
                        <tr>
                            <td align="center" style="white-space: nowrap;">
                                认证项目<span style="color: #ff0000">*</span>：
                            </td>
                            <td align="center" rowspan="1">
                                <input id="items" name="items" class="mini-combobox rxc" plugins="mini-combobox"
                                       style="width:100%;height:34px" label="认证项目："
                                       length="50"
                                       only_read="false" required="true" allowinput="false" mwidth="100"
                                       wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="true"
                                       textField="text" valueField="key_" emptyText="请选择..."
                                       url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=RZXM"
                                       nullitemtext="请选择..." emptytext="请选择..."/>
                            </td>
                            <td align="center" style="white-space: nowrap;">
                                测试是否通过<span style="color: #ff0000">*</span>：
                            </td>
                            <td align="center" rowspan="1">
                                <input id="pass" name="pass" class="mini-combobox rxc" plugins="mini-combobox"
                                       style="width:100%;height:34px" label="测试是否通过："
                                       length="50"
                                       only_read="false" required="true" allowinput="false" mwidth="100"
                                       wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                                       textField="text" valueField="key_" emptyText="请选择..."
                                       url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=PASSED"
                                       nullitemtext="请选择..." emptytext="请选择..."/>
                            </td>
                        </tr>
                        <tr>
                            <td align="center" colspan="1" rowspan="1" style="white-space: nowrap;">
                                测试开始日期<span style="color: #ff0000">*</span>：
                            </td>
                            <td align="center" colspan="1" rowspan="1">
                                <input id="startDate" name="startDate" class="mini-datepicker" allowInput="false"
                                       required="true" style="width:100%;height:34px">
                            </td>
                            <td align="center" colspan="1" rowspan="1" style="white-space: nowrap;">
                                测试结束日期<span style="color: #ff0000">*</span>：
                            </td>
                            <td align="center" colspan="1" rowspan="1">
                                <input id="endDate" name="endDate" class="mini-datepicker" allowInput="false"
                                       required="true" style="width:100%;height:34px">
                            </td>
                        </tr>
                        <tr>
                            <td align="center" colspan="1" rowspan="1" style="white-space: nowrap;">
                                复测开始日期：
                            </td>
                            <td align="center" colspan="1" rowspan="1">
                                <input id="reStartDate" name="reStartDate" class="mini-datepicker" allowInput="false"
                                       style="width:100%;height:34px">
                            </td>
                            <td align="center" colspan="1" rowspan="1" style="white-space: nowrap;">
                                复测结束日期：
                            </td>
                            <td align="center" colspan="1" rowspan="1">
                                <input id="reEndDate" name="reEndDate" class="mini-datepicker" allowInput="false"
                                       style="width:100%;height:34px">
                            </td>
                        </tr>
                        <tr>
                            <td align="center" colspan="1" rowspan="1" style="white-space: nowrap;">
                                传递时间：
                            </td>
                            <td align="center" colspan="1" rowspan="1">
                                <input id="sendDate" name="sendDate" class="mini-datepicker" allowInput="false"
                                       style="width:100%;height:34px">
                            </td>
                            <td align="center" style="white-space: nowrap;">
                                是否有反馈资料问题：
                            </td>
                            <td align="center" rowspan="1">
                                <input id="docOk" name="docOk" class="mini-combobox rxc" plugins="mini-combobox"
                                       style="width:100%;height:34px" label="是否有反馈资料问题："
                                       length="50"
                                       only_read="false" required="true" allowinput="false" mwidth="100"
                                       wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                                       textField="text" valueField="key_" emptyText="请选择..."
                                       url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=YESORNO"
                                       nullitemtext="请选择..." emptytext="请选择..."/>
                            </td>
                        </tr>
                        <tr>
                            <td align="center" style="white-space: nowrap;">
                                设计型号：
                            </td>
                            <td colspan="1">
                                <input id="designModel" required="true" enabled="false" name="designModel" class="mini-textbox" style="width:98%;"/>
                            </td>
                            <td align="center" style="white-space: nowrap;">
                                资料已具备：
                            </td>
                            <td align="center" rowspan="1">
                                <input id="docComplete" name="docComplete" class="mini-combobox rxc"
                                       plugins="mini-combobox"
                                       style="width:100%;height:34px" label="资料已具备："
                                       length="50"
                                       only_read="false" required="true" allowinput="false" mwidth="100"
                                       wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                                       textField="text" valueField="key_" emptyText="请选择..."
                                       url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=YESORNO"
                                       nullitemtext="请选择..." emptytext="请选择..."/>
                            </td>
                        </tr>
                        <tr>
                            <td align="center" style="white-space: nowrap;">
                                备注：
                            </td>
                            <td colspan="3">
                                <input id="remark"  name="remark" class="mini-textbox" style="width:100%;"/>
                            </td>
                        </tr>
                        <tr>
                            <td style="text-align: center;height: 250px">测试照片：</td>
                            <td colspan="3">
                                <div style="margin-top: 10px;margin-bottom: 2px">
                                    <a id="addProblemFile" class="mini-button" onclick="addTestProblemFile()">添加文件</a>
                                    <span style="color: red">注：添加文件前，请先进行草稿的保存</span>
                                </div>
                                <div id="testProblemListGrid" class="mini-datagrid" style="width: 100%; height: 85%"
                                     allowResize="false"
                                     idField="id"
                                     url="${ctxPath}/wwrz/core/file/files.do?fileType=testProblem&detailId=${applyId}"
                                     autoload="true"
                                     multiSelect="true" showPager="false" showColumnsMenu="false"
                                     allowAlternating="true">
                                    <div property="columns">
                                        <div type="indexcolumn" align="center" width="20">序号</div>
                                        <div field="fileName" align="center" headerAlign="center" width="150">文件名</div>
                                        <div field="fileSize" align="center" headerAlign="center" width="60">文件大小</div>
                                        <div field="fileDesc" align="center" headerAlign="center" width="100">文件描述</div>
                                        <div field="action" width="100" headerAlign='center' align="center"
                                             renderer="operationRendererProblem">操作
                                        </div>
                                    </div>
                                </div>
                            </td>
                        </tr>
                    </table>
                </div>
            </fieldset>
            <br>
            <fieldset id="fdTestProblem" class="hideFieldset">
                <legend>
                    <label style="font-size:17px">
                        <input type="checkbox" id="checkboxTestProblem" onclick="toggleFieldSet(this, 'fdTestProblem')"
                               hideFocus/>
                        测试问题
                    </label>
                </legend>
                <div class="fieldset-body"  style="margin: 10px 10px 10px 10px;width: 98% ;height: auto">
                    <div class="mini-toolbar" id="problemButtons">
                        <a class="mini-button" id="addProblemButton" plain="true" onclick="addProblem()">添加</a>
                        <a class="mini-button btn-red" id="delProblemButton" plain="true" onclick="delProblem()">删除</a>
                        <a class="mini-button" id="viewStandardButton" plain="true" onclick="viewStandard()">标准查看</a>
                    </div>
                    <div id="grid_problem" class="mini-datagrid" allowResize="false" style="margin-top: 5px"
                         idField="id" allowCellEdit="true" allowCellSelect="true" allowSortColumn="false"
                         multiSelect="true" showColumnsMenu="false" showPager="false" allowCellWrap="true"
                         showVGridLines="true">
                        <div property="columns">
                            <div type="checkcolumn" width="10"></div>
                            <div type="indexcolumn" headerAlign="center" align="center" width="15">序号</div>
                            <div field="id" align="center" width="1" headerAlign="left" visible="false">id</div>
                            <div field="problem" headerAlign="center" align="center" width="150">测试问题
                                <input property="editor" class="mini-textbox"/>
                            </div>
                            <div field="standardId" displayField="standardNumber" headerAlign="center" align="center"
                                 width="100">对应标准
                                <input property="editor" class="mini-combobox" textField="standardNumber"
                                       valueField="id"
                                       emptyText="请选择..." onvaluechanged="standardChange(this)"
                                       showNullItem="false" nullItemText="请选择..."
                                       url="${ctxPath}/wwrz/core/standard/listStandard.do"/>
                            </div>
                            <div field="sectorId" displayField="standardCode" width="80" headerAlign="center"
                                 align="center">对应章节
                                <div name="sectorCombo" property="editor" class="mini-combobox" textField="standardCode"
                                     valueField="id" emptyText="请选择..."
                                     showNullItem="false" nullItemText="请选择..." onbeforeshowpopup="beforeShowSector"
                                ></div>
                            </div>
                            <%--                            <div field="remark"  headerAlign="center" align="center" width="100">备注--%>
                            <%--                                <input property="editor" class="mini-textbox" />--%>
                            <%--                            </div>--%>
                            <div field="charger" displayField="chargerName" headerAlign="center" align="center"
                                 width="50">负责人
                                <input property="editor" class="mini-user rxc" plugins="mini-user"
                                       style="width:90%;height:34px;" allowinput="false" length="50" maxlength="50"
                                       mainfield="no" single="true"/>
                            </div>
                            <div field="plan" headerAlign="center" align="left" width="150">整改方案
                                <input property="editor" class="mini-textbox"/>
                            </div>
                            <div field="testPlan" renderer="testPlan" align="center" width="50" headerAlign="center">
                                方案附件
                            </div>
                            <div field="rectifyApprove" renderer="rectifyApprove" align="center" width="50"
                                 headerAlign="center">
                                整改证明
                            </div>
                            <div field="passed" displayField="passed" headerAlign="center" align="center" width="50">
                                是否通过
                                <input property="editor" class="mini-combobox" textField="text" valueField="key_"
                                       emptyText="请选择..."
                                       showNullItem="false" nullItemText="请选择..."
                                       url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=YESORNO"/>
                            </div>
                        </div>
                    </div>
                </div>
            </fieldset>
            <fieldset id="fdDocumentInfo" class="hideFieldset">
                <legend>
                    <label style="font-size:17px">
                        <input type="checkbox" id="checkboxDocument" onclick="toggleFieldSet(this, 'fdDocumentInfo')"
                               hideFocus/>
                        认证资料明细
                    </label>
                </legend>
                <div class="fieldset-body"  style="margin: 10px 10px 10px 10px;width: 98% ;height: auto">
                    <div class="mini-toolbar" id="docButtons">
                        <a class="mini-button" id="addDocumentButton" plain="true" onclick="addDocument()">添加</a>
                        <a class="mini-button btn-red" id="delDocumentButton" plain="true"
                           onclick="delDocument()">删除</a>
                        <a class="mini-button " id="downDocument" plain="true" onclick="fileDown()">认证资料下载</a>
                    </div>
                    <div id="grid_document" class="mini-datagrid" allowResize="false" style="margin-top: 5px"
                         idField="id" allowCellEdit="true" allowCellSelect="true" allowSortColumn="false"
                         multiSelect="true" showColumnsMenu="false" showPager="false" allowCellWrap="true"
                         showVGridLines="true">
                        <div property="columns">
                            <div type="checkcolumn" width="10"></div>
                            <div type="indexcolumn" headerAlign="center" align="center" width="15">序号</div>
                            <div field="id" align="center" width="1" headerAlign="left" visible="false">id</div>
                            <div field="docType" displayField="docType" headerAlign="center" align="center" width="50">
                                资料类型
                                <input property="editor" class="mini-combobox" textField="text" valueField="key_"
                                       emptyText="请选择..." required onvaluechanged="genDocumentList"
                                       showNullItem="false" nullItemText="请选择..."
                                       url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=RZZLLX"/>
                            </div>
                            <div field="docName" displayField="docName" headerAlign="center" align="center" width="150">
                                资料名称
                                <input name="itemCombo" property="editor" class="mini-combobox" textField="docName"
                                       valueField="id" emptyText="请选择..."
                                       onbeforeshowpopup="beforeShowItem"
                                       showNullItem="false" nullItemText="请选择..."/>
                            </div>
                            <div field="charger" displayField="chargerName" headerAlign="center" align="center"
                                 width="50">负责人
                                <input property="editor" class="mini-user rxc" plugins="mini-user"
                                       style="width:90%;height:34px;" allowinput="false" length="50" maxlength="50"
                                       mainfield="no" single="true"/>
                            </div>
                            <div field="used" displayField="used" headerAlign="center" align="center" width="50">本产品是否适用
                                <input property="editor" class="mini-combobox" textField="text" valueField="key_"
                                       emptyText="请选择..."
                                       showNullItem="false" nullItemText="请选择..."
                                       url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=YESORNO"/>
                            </div>
                            <div field="testDoc" renderer="testDoc" align="center" width="50" headerAlign="center">
                                认证资料
                            </div>
                            <div field="passed" displayField="passed" headerAlign="center" align="center" width="50">
                                是否有问题
                                <input property="editor" class="mini-combobox" textField="text" valueField="key_"
                                       emptyText="请选择..."
                                       showNullItem="false" nullItemText="请选择..."
                                       url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=YESORNO"/>
                            </div>
                            <div field="problem" headerAlign="center" align="left" width="150">反馈问题
                                <input property="editor" class="mini-textbox"/>
                            </div>
                        </div>
                    </div>
                </div>
            </fieldset>
            <fieldset id="fdReportInfo" class="hideFieldset">
                <legend>
                    <label style="font-size:17px">
                        <input type="checkbox" id="checkboxReport" onclick="toggleFieldSet(this, 'fdReportInfo')"
                               hideFocus/>
                        认证报告证书
                    </label>
                </legend>
                <div class="fieldset-body"  style="margin: 10px 10px 10px 10px;width: 98% ;height: auto">
                    <div style="margin-top: 10px;margin-bottom: 2px">
                        <a id="addReportFile" class="mini-button" onclick="addReportFile()">添加认证报告</a>
                    </div>
                    <div id="fileListGrid" class="mini-datagrid" allowResize="true"
                         idField="id" url="${ctxPath}/wwrz/core/file/files.do?fileType=report&detailId=${applyId}"
                         autoload="true"
                         multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
                        <div property="columns">
                            <div type="indexcolumn" align="center" width="20">序号</div>
                            <div field="fileName" align="center" headerAlign="center" width="150">附件名称</div>
                            <div field="reportType" align="center" headerAlign="center" width="50">报告类型</div>
                            <div field="reportName" align="center" headerAlign="center" width="150">文件名称</div>
                            <div field="reportCode" align="center" headerAlign="center" width="80">编号</div>
                            <div field="reportDate" align="center" headerAlign="center" width="80">日期</div>
                            <div field="reportValidity" align="center" headerAlign="center" width="80">有效期</div>
                            <div field="fileSize" align="center" headerAlign="center" width="60">文件大小</div>
                            <div field="fileDesc" align="center" headerAlign="center" width="100">文件描述</div>
                            <div field="action" width="100" headerAlign='center' align="center"
                                 renderer="operationRendererSq">操作
                            </div>
                        </div>
                    </div>
                </div>
            </fieldset>
        </form>
    </div>
    <div id="selectPlanWindow" title="选择试验计划" class="mini-window" style="width:1000px;height:450px;"
         showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
        <div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px"
             borderStyle="border-left:0;border-top:0;border-right:0;">
            <span class="text" style="width:auto">年度: </span> <input id="yearSelect" name="reportYear"
                                                                     class="mini-combobox rxc" plugins="mini-combobox"
                                                                     style="width:80px;height:34px" label="年度："
                                                                     length="50" onvaluechanged="searchPlanList()"
                                                                     only_read="false" allowinput="false" mwidth="80"
                                                                     wunit="%" mheight="34" hunit="px"
                                                                     shownullitem="true" multiSelect="false"
                                                                     textField="text" valueField="value"
                                                                     emptyText="请选择..."
                                                                     url="${ctxPath}/sys/core/commonInfo/getYearList.do"
                                                                     nullitemtext="请选择..." emptytext="请选择..."/>
            <span class="text" style="width:auto">计划编号: </span><input class="mini-textbox" style="width: 80px"
                                                                    name="planCode" id="planCode"/>
            <span class="text" style="width:auto">部门: </span><input class="mini-textbox" style="width: 80px"
                                                                    name="deptName" id="deptName"/>
            <span class="text" style="width:auto">型号: </span><input class="mini-textbox" style="width: 80px"
                                                                    name="planModel" id="planModel"/>
            <span class="text" style="width:auto">产品主管: </span><input class="mini-textbox" style="width: 80px"
                                                                      name="chargerName" id="chargerName"/>
            <span class="text" style="width:auto">是否显示所有: </span>
            <input id="isShowAll" name="isShowAll" class="mini-combobox rxc" plugins="mini-combobox"
                   style="width:80px;height:34px" label="是否显示所有："
                   length="50" onvaluechanged="searchPlanList()"
                   only_read="false" required="false" allowinput="false" mwidth="100"
                   wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                   textField="text" valueField="key_" emptyText="请选择..."
                   url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=YESORNO"
                   nullitemtext="请选择..." emptytext="请选择..."/>
            <a class="mini-button" iconCls="icon-search" plain="true" onclick="searchPlanList()">查询</a>
        </div>
        <div class="mini-fit">
            <div id="planListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
                 onrowdblclick="onRowDblClick"
                 idField="id" showColumnsMenu="false" sizeList="[5,10,20,50,100,200]" pageSize="5"
                 allowAlternating="true" pagerButtons="#pagerButtons" url="${ctxPath}/wwrz/core/testPlan/listData.do">
                <div property="columns">
                    <div type="checkcolumn" width="30"></div>
                    <div field="applyId" width="60" headerAlign="center" align="center" allowSort="false"
                         renderer="onStatus">绑定状态
                    </div>
                    <div field="planCode" width="120" headerAlign="center" align="center" allowSort="false">计划编号</div>
                    <div field="deptName" width="100" headerAlign="center" align="center" allowSort="false">部门</div>
                    <div field="productModel" width="80" headerAlign="center" align="center" allowSort="false">型号</div>
                    <div field="chargerName" width="80" headerAlign="center" align="center" allowSort="false">产品主管</div>
                    <div field="certType" width="80" headerAlign="center" align="center" allowSort="true"
                         renderer="onCertType">认证类别
                    </div>
                    <div field="testType" width="80" headerAlign="center" align="center" allowSort="true"
                         renderer="onTestType">全新/补测
                    </div>
                    <div field="yearMonth" width="80" headerAlign="center" align="center" allowSort="true">测试月份</div>
                    <div field="remark" width="120" headerAlign="center" align="center" allowSort="true">备注</div>
                </div>
            </div>
        </div>
        <div property="footer" style="padding:5px;height: 35px">
            <table style="width:100%;height: 100%">
                <tr>
                    <td style="width:120px;text-align:center;">
                        <input type="button" style="height: 25px;width: 70px" value="确定" onclick="selectPlanOK()"/>
                        <input type="button" style="height: 25px;width: 70px" value="取消" onclick="selectPlanHide()"/>
                    </td>
                </tr>
            </table>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    let nodeVarsStr = '${nodeVars}';
    var action = "${action}";
    let jsUseCtxPath = "${ctxPath}";
    let ApplyObj =${applyObj};
    let status = "${status}";
    let applyForm = new mini.Form("#applyForm");
    var currentUserName = "${currentUserName}";
    var currentUserId = "${currentUserId}";
    var currentTime = "${currentTime}";
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";
    var grid_problem = mini.get("#grid_problem");
    var grid_document = mini.get("#grid_document");
    var fileListGrid = mini.get("fileListGrid");
    var certTypeList = getDics("RZLB");
    var planStatusList = getDics("JHZT");
    var testTypeList = getDics("CSLB");
    var selectPlanWindow = mini.get("selectPlanWindow");
    var planListGrid = mini.get("planListGrid");
    var testProblemListGrid = mini.get("testProblemListGrid");

    function operationRendererSq(e) {
        var record = e.record;
        var cellHtml = '';
        cellHtml += returnPreviewSpan(record.fileName, record.id, record.mainId, coverContent, "wwrzFileUrl");
        cellHtml += '&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downFile(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">下载</span>';
        if (!BGZSSC) {
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title="删除" style="color:silver;">删除</span>';
        } else {
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
        }
        return cellHtml;
    }

    function operationRendererProblem(e) {
        var record = e.record;
        var cellHtml = '';
        cellHtml += returnPreviewSpan(record.fileName, record.id, record.mainId, coverContent, "wwrzFileUrl");
        cellHtml += '&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downFile(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">下载</span>';
        if (!isFirstNode && action != 'edit') {
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title="删除" style="color:silver;">删除</span>';
        } else {
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
        }
        return cellHtml;
    }

    grid_problem.on("cellbeginedit", function (e) {
        if (e.field != 'testPlan' && e.field != 'rectifyApprove') {
            e.editor.setEnabled(false);
        }
        if (action == 'edit') {
            if (e.field == 'problem' || e.field == 'remark' || e.field == 'standardId' || e.field == 'sectorId') {
                e.editor.setEnabled(true);
            }
        }
        if (action == 'task') {
            if (isFirstNode || YYFC) {
                if (e.field == 'problem' || e.field == 'remark' || e.field == 'standardId' || e.field == 'sectorId' || e.field == 'passed') {
                    e.editor.setEnabled(true);
                }
            }
            if (ZGFP) {
                if (e.field == 'charger') {
                    e.editor.setEnabled(true);
                }
            }
            if (ZGFA) {
                if (e.field == 'plan') {
                    if (currentUserId == e.record.charger) {
                        e.editor.setEnabled(true);
                    }
                }
            }
        }
    })
    grid_document.on("cellbeginedit", function (e) {
        if (e.field != 'testDoc') {
            e.editor.setEnabled(false);
        }
        if (RZZL) {
            if (e.field == 'docType' || e.field == 'docName') {
                e.editor.setEnabled(true);
            }
        } else if (RZZLFP) {
            if (e.field == 'charger') {
                e.editor.setEnabled(true);
            }
        } else if (RZZLBZ) {
            if (e.field == 'used' && currentUserId == e.record.charger) {
                e.editor.setEnabled(true);
            }
        } else if (RZZLWT) {
            if (e.field == 'passed' || e.field == 'problem') {
                e.editor.setEnabled(true);
            }
        }
    })

    function onCertType(e) {
        var record = e.record;
        var certType = record.certType;
        var resultText = '';
        for (var i = 0; i < certTypeList.length; i++) {
            if (certTypeList[i].key_ == certType) {
                resultText = certTypeList[i].text;
                break
            }
        }
        return resultText;
    }

    function onTestType(e) {
        var record = e.record;
        var testType = record.testType;
        var resultText = '';
        for (var i = 0; i < testTypeList.length; i++) {
            if (testTypeList[i].key_ == testType) {
                resultText = testTypeList[i].text;
                break
            }
        }
        return resultText;
    }

    function onStatus(e) {
        var record = e.record;
        var applyId = record.applyId
        var _html = '';
        var color = '';
        var resultText = "";
        if (applyId) {
            resultText = "已绑定";
            color = 'red'
        } else {
            resultText = "未绑定";
            color = 'green'
        }
        _html = '<span style="color: ' + color + '">' + resultText + '</span>'
        return _html;
    }

    /**
     * 选择
     */
    function selectPlanName() {
        selectPlanWindow.show();
        searchPlanList();
    }

    function searchPlanList() {
        var queryParam = [];
        //其他筛选条件
        var planYear = $.trim(mini.get('yearSelect').getValue());
        if (planYear) {
            queryParam.push({name: "planYear", value: planYear});
        }
        var deptName = $.trim(mini.get("deptName").getValue());
        if (deptName) {
            queryParam.push({name: "deptName", value: deptName});
        }
        var planModel = $.trim(mini.get("planModel").getValue());
        if (planModel) {
            queryParam.push({name: "productModel", value: planModel});
        }
        var chargerName = $.trim(mini.get("chargerName").getValue());
        if (chargerName) {
            queryParam.push({name: "chargerName", value: chargerName});
        }
        var isShowAll = $.trim(mini.get('isShowAll').getValue())
        if (isShowAll == 0) {
            queryParam.push({name: "isShowAll", value: true});
        }
        queryParam.push({name: "planStatus", value: "ysp"});
        var data = {};
        data.filter = mini.encode(queryParam);
        data.pageIndex = planListGrid.getPageIndex();
        data.pageSize = planListGrid.getPageSize();
        data.sortField = planListGrid.getSortField();
        data.sortOrder = planListGrid.getSortOrder();
        //查询
        planListGrid.load(data);
    }

    function selectPlanOK() {
        var selectRow = planListGrid.getSelected();
        if (selectRow) {
            mini.get("planId").setValue(selectRow.id);
            mini.get("planId").setText(selectRow.planCode);
            mini.get("productModel").setValue(selectRow.productModel);
            mini.get("productLeader").setValue(selectRow.charger);
            mini.get("productLeader").setText(selectRow.chargerName);
        }
        selectPlanHide();
    }

    function selectPlanHide() {
        selectPlanWindow.hide();
        mini.get("planModel").setValue('');
        mini.get("chargerName").setValue('');
        mini.get("deptName").setValue('');
    }
</script>
</body>
</html>
