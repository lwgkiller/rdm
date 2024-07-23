<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>常规招标编辑</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmZhgl/jszb_commonEdit.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>

<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="processInfo" class="mini-button" style="display: none" onclick="processInfo()">流程信息</a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div id="changeToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="saveChange" class="mini-button" onclick="saveChange()">保存变更</a>
        <a class="mini-button" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin: 0 auto; width: 80%;">
        <form id="formJszb" method="post">
            <input id="jszbId" name="jszbId" class="mini-hidden"/>
            <input id="instId" name="instId" class="mini-hidden"/>
            <input id="CREATE_BY_" name="CREATE_BY_" class="mini-hidden"/>
            <input id="gslclb" name="gslclb" class="mini-hidden"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0">
                <caption style="font-weight: bold">
                    常规招标文件审批
                </caption>

                <tr>
                    <td style="width: 14%">招标部门：</td>
                    <td style="width: 30%">
                        <input id="zbbmId" name="zbbmId" textname="zbbmName" class="mini-dep rxc" plugins="mini-dep"
                               style="width:98%;height:34px" allowinput="false" textname="zbbmName" length="500" maxlength="500" minlen="0"
                               single="true" initlogindep="false"/>
                    </td>
                    <td style="width: 14%">经办人：</td>
                    <td style="width: 30%">
                        <input id="jbrId" name="jbrId" textname="jbrName" class="mini-user rxc" plugins="mini-user"
                               style="width:98%;height:34px;" allowinput="false" label="可见范围" length="100"
                               maxlength="100" mainfield="no" single="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%">招标项目名称：</td>
                    <td style="width: 30%;">
                        <input id="zbName" name="zbName" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="width: 14%">项目编号：
                        <span style="font-size:10px;color:red">(当前没有的可后续阶段维护)</span>
                    </td>
                    <td style="width: 30%">
                        <input id="xmNum" name="xmNum" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%">招标总价或项目预算：</td>
                    <td style="width: 30%;">
                        <input id="zbjg" name="zbjg" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="width: 14%">招标组织形式：</td>
                    <td style="width: 30%;">
                        <input id="zbzzxs" name="zbzzxs" class="mini-combobox" style="width:98%;"
                               textField="value" valueField="key" emptyText="请选择..."
                               required="false" allowInput="false" showNullItem="false" nullItemText="请选择..."
                               data="[{'key' : '自行招标','value' : '自行招标'},{'key' : '委托招标代理机构招标','value' : '委托招标代理机构招标'}]"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%">招标形式：</td>
                    <td style="width: 30%;">
                        <input id="zbxs" name="zbxs" class="mini-combobox" style="width:98%;"
                               textField="value" valueField="key" emptyText="请选择..."
                               required="false" allowInput="false" showNullItem="false" nullItemText="请选择..."
                               data="[{'key' : '公开招标','value' : '公开招标'},{'key' : '邀请招标','value' : '邀请招标'}]"/>
                    </td>
                    <td style="width: 14%">立项时间：</td>
                    <td style="width: 30%">
                        <input id="lxTime" name="lxTime" class="mini-datepicker" format="yyyy-MM-dd" allowinput="false"
                               style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%">信息发布方式：</td>
                    <td style="width: 30%;">
                        <input id="publishType" name="publishType" class="mini-combobox" style="width:98%;"
                               textField="value" valueField="key" emptyText="请选择..."
                               required="false" allowInput="false" showNullItem="false" nullItemText="请选择..."
                               data="[{'key' : '集团电子采购系统','value' : '集团电子采购系统'}
							   ,{'key' : '专业网站','value' : '专业网站'},{'key' : '其他载体','value' : '其他载体'}]"/>
                    </td>
                    <td style="width: 14%">预计完成时间：</td>
                    <td style="width: 30%">
                        <input id="yjFinishTime" name="yjFinishTime" class="mini-datepicker" format="yyyy-MM-dd" allowinput="false"
                               style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%;height: auto">附件列表：</td>
                    <td colspan="3">
                        <div style="margin-top: 10px;margin-bottom: 2px">
                            <span style="color: red">（注：添加附件前，请先进行草稿的保存）</span>
                        </div>
                        <div id="fileListGrid" class="mini-datagrid" style="width: 100%; height:auto"
                             allowResize="false" allowCellWrap="true"
                             idField="ckId" url="${ctxPath}/zhgl/core/jszb/getJszbFileTypeList.do?type=招标流程"
                             autoload="true" multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
                            <div property="columns">
                                <div field="ssjd" name="ssjd" width="40" headerAlign="center" align="center">阶段</div>
                                <div field="num" width="40" headerAlign="center" align="center">序号</div>
                                <div field="lcwjlx" width="120" headerAlign="center" align="center">文件类别</div>
                                <div field="action" width="60" headerAlign='center' align="center"
                                     renderer="operationRenderer">操作
                                </div>
                            </div>
                        </div>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>


<script type="text/javascript">
    mini.parse();
    var action = "${action}";
    var status = "${status}";
    var jsUseCtxPath = "${ctxPath}";
    var fileListGrid = mini.get("fileListGrid");
    var formJszb = new mini.Form("#formJszb");
    var jszbId = "${jszbId}";
    var nodeVarsStr = '${nodeVars}';
    var currentUserId = "${currentUserId}";
    var currentUserRoles =${currentUserRoles};

    var type="招标流程";

    fileListGrid.on("load", function () {
        fileListGrid.mergeColumns(["ssjd"]);
    });

    function operationRenderer(e) {
        var record = e.record;

        var cellHtml = '<span title="操作" style="color:#409EFF;cursor: pointer;" ' +
            'onclick="operateJszbFile(\''+record.ckId+'\',\''+record.ssjd+'\')">文件查看&操作</span>';

        return cellHtml;
    }


</script>
</body>
</html>
