<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>物料预留号申请</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmZhgl/materialApplyEdit.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/mini/CopyExcel.js?version=${static_res_version}" type="text/javascript"></script>
    <style type="text/css">
        fieldset {
            border: solid 1px #aaaaaab3;
            min-width: 920px;
        }

        .hideFieldset {
            border-left: 0;
            border-right: 0;
            border-bottom: 0;
        }

        .hideFieldset .fieldset-body {
            display: none;
        }

        .processStage {
            background-color: #ccc !important;
            font-size: 15px !important;
            font-family: '微软雅黑' !important;
            text-align: center !important;
            vertical-align: middle !important;
            color: #201f35 !important;
            height: 30px !important;
            border-right: solid 0.5px #666;
        }

        .rmMem .mini-grid-cell-inner {
            color: red !important;
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
    <div class="form-container" style="margin:0 auto; width: 80%;">
        <form id="applyForm" method="post">
            <input id="id" name="id" class="mini-hidden"/>
            <input id="taskId_" name="taskId_" class="mini-hidden"/>
            <input id="instId" name="instId" class="mini-hidden"/>
            <fieldset id="fdBaseInfo">
                <legend>
                    <label style="font-size:17px">
                        基本信息
                    </label>
                </legend>
                <div class="fieldset-body" style="margin: 10px 10px 10px 10px">
                    <table class="table-detail grey" cellspacing="1" cellpadding="0">
                        <caption>
                            物料预留号申请单
                        </caption>
                        <tr>
                            <td style="text-align: center;width: 20%">申请人：</td>
                            <td style="min-width:170px">
                                <input id="CREATE_BY_" name="CREATE_BY_" textname="userName"
                                       class="mini-user rxc" plugins="mini-user" style="width:98%;height:34px;" allowinput="false"
                                       label="编制人" length="50" mainfield="no" single="true" enabled="false"/>
                            </td>
                            <td style="text-align: center;width: 20%">申请部门：</td>
                            <td style="min-width:170px">
                                <input id="deptId" name="deptId" class="mini-dep rxc" plugins="mini-dep"
                                       data-options="{'single':'true','config':{'type':'','grouplevel':'','groupid':'','groupidText':''}}"
                                       style="width:98%;height:34px" allowinput="false" label="部门" textname="deptName" length="500"
                                       maxlength="500" minlen="0" single="true" required="false" initlogindep="false" showclose="false"
                                       mwidth="160" wunit="px" mheight="34" hunit="px" enabled="false"/>
                            </td>
                        </tr>
                        <tr>
                            <td align="center" colspan="1" rowspan="1" style="white-space: nowrap;">
                                到期日期<span style="color: #ff0000">*</span>：
                            </td>
                            <td align="center" colspan="1" rowspan="1">
                                <input id="finalDate" name="finalDate" class="mini-datepicker" allowInput="false"
                                       required="true" style="width:98%;height:34px">
                            </td>
                            <td style="text-align: center;width: 20%">物料预留号：</td>
                            <td colspan="1">
                                <input id="materialCode" emptyText="SAP自动生成" readonly name="materialCode" class="mini-textbox" style="width:98%;"/>
                            </td>
                        </tr>
                        <tr>
                            <td align="center" style="white-space: nowrap;">
                                物料类型：
                            </td>
                            <td align="center" rowspan="1">
                                <input id="applyType" name="applyType" class="mini-combobox rxc"
                                       plugins="mini-combobox"
                                       style="width:98%;height:34px" label="物料类型："
                                       length="50"  onValuechanged="applyTypeChange"
                                       only_read="false" required="true" allowinput="false" mwidth="100"
                                       wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                                       textField="text" valueField="key_" emptyText="请选择..."
                                       url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=materialType"
                                       nullitemtext="请选择..." emptytext="请选择..."/>
                            </td>
                            <td align="center" style="white-space: nowrap;">
                                成本中心：
                            </td>
                            <td align="center" rowspan="1">
                                <input id="costCenter"  name="costCenter" class="mini-combobox" style="width:98%;"
                                       textField="text" valueField="value" emptyText="请选择..." url="${ctxPath}/rdmZhgl/core/material/costCenter/centerCostDic.do"
                                       allowInput="false" showNullItem="true" nullItemText="请选择..." />
                            </td>
                        </tr>
                        <tr>
                            <td style="text-align: center;width: 20%">科技项目财务订单号：</td>
                            <td colspan="1">
                                <input id="orderCode"  name="orderCode" class="mini-textbox" style="width:98%;"/>
                            </td>
                            <td style="text-align: center;width: 20%">收货存储地点：</td>
                            <td colspan="1">
                                <input id="storageLocation"  name="storageLocation" class="mini-textbox" style="width:98%;"/>
                            </td>
                        </tr>
                        <tr id="ledgerAccountTitle">
                            <td align="center" style="white-space: nowrap;">
                                总账科目：
                            </td>
                            <td align="center" rowspan="1">
                                <input id="ledgerAccount"  name="ledgerAccount" allowinput="false" class="mini-textbox" style="width:98%;"/>
                            </td>
                        </tr>
                        <tr>
                            <td align="center" style="white-space: nowrap;">
                                领用事由：
                            </td>
                            <td colspan="3">
						        <textarea id="reason" name="reason" class="mini-textarea rxc" plugins="mini-textarea"
                                  style="width:99%;height:100px;line-height:25px;" required
                                  label="领用事由" datatype="varchar" length="500" vtype="length:500" minlen="0"
                                  allowinput="true"
                                  mwidth="80" wunit="%" mheight="200" hunit="px"></textarea>
                            </td>
                        </tr>
                    </table>
                </div>
            </fieldset>
            <fieldset id="fdTestProblem" >
                <legend>
                    <label style="font-size:17px">
                        物料信息
                    </label>
                </legend>
                <div class="fieldset-body" style="margin: 10px 10px 10px 10px">
                    <div class="mini-toolbar" id="problemButtons">
                        <a class="mini-button" id="addDetailButton" plain="true" onclick="addDetail()">添加</a>
                        <a class="mini-button btn-red" id="delDetailButton" plain="true" onclick="delDetail()">删除</a>
                        <span style="margin-left: 10px;color: red">注：物料类型如选择311移库则存储地点必填，其他类型存储地点不填.(若添加物料较多时可用“框选Excel数据复制，点击添加，点击物料列Ctrl+V”方法)</span>
                    </div>
                    <div id="grid_detail" class="mini-datagrid" allowResize="false" style="margin-top: 5px" allowRowSelect="true" enableHotTrack="false"
                         idField="id" allowCellEdit="true" allowCellSelect="true" allowSortColumn="false" cellEditAction="celldblclick" onlyCheckSelection="true"
                         multiSelect="true" showColumnsMenu="false" showPager="false" allowCellWrap="true"
                         showVGridLines="true">
                        <div property="columns">
                            <div type="checkcolumn" width="25"></div>
                            <div type="indexcolumn" headerAlign="center" align="center" width="30">序号</div>
                            <div field="id" align="center" width="1" headerAlign="left" visible="false">id</div>
                            <div field="itemCode" headerAlign="center" align="center" width="80">物料号
                                <input property="editor" class="mini-textbox"/>
                            </div>
                            <div field="itemName" headerAlign="center" align="center" width="120">物料描述
                                <input property="editor" class="mini-textbox"/>
                            </div>
                            <div field="totalNum" headerAlign="center" align="center" width="80">总数量
                                <input property="editor" class="mini-spinner" allowLimitValue="false"  allowNull="false"/>
                            </div>
                            <div field="delaNum" headerAlign="center" align="center" width="80">未清数量</div>
                         <%--   <div field="unit" headerAlign="center" align="center" width="100">计量单位
                                <input property="editor" class="mini-textbox"/>
                            </div>--%>
                            <div field="storage" headerAlign="center" align="center" width="100">存储地点
                                <input property="editor" class="mini-textbox"/>
                            </div>
                            <div field="finishFlagText" headerAlign="center" align="center" width="80">完成标志</div>
                            <div field="delFlagText" headerAlign="center" align="center" width="80">删除标志</div>
                            <div field="lineNo" headerAlign="center" align="center" width="100">SAP行号</div>
                        </div>
                    </div>
                </div>
            </fieldset>
        </form>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    let nodeVarsStr = '${nodeVars}';
    var action = "${action}";
    let jsUseCtxPath = "${ctxPath}";
    let ApplyObj =${applyObj};
    let status = "${status}";
    var currentDay = "${currentDay}";
    let applyForm = new mini.Form("#applyForm");
    var grid_detail = mini.get("#grid_detail");
    new CopyExcel(grid_detail)
    grid_detail.on("cellbeginedit", function (e) {
    });
</script>
</body>
</html>
