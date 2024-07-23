<%--
  Created by IntelliJ IDEA.
  User: matianyu
  Date: 2021/2/23
  Time: 14:57
  To change this template use File | Settings | File Templates.
--%>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>列表管理</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/rap/oilList.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px;display: none">
                    <span class="text" style="width:auto">国家区域: </span>
                    <input class="mini-textbox" id="oilNation" name="oilNation"/>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">油液类型: </span>
                    <input id="oilType" name="oilType" class="mini-combobox" style="width:150px;" textField="text"
                           valueField="id" emptyText="请选择..."
                           data="[{id:'尿素',text:'尿素'},{id:'防冻液',text:'防冻液'}
                           ,{id:'柴油',text:'柴油'},{id:'机油',text:'机油'}]"
                           required="true" allowInput="true" showNullItem="true" nullItemText="请选择..."/>
                <li style="margin-left: 10px">
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">检测机构: </span>
                    <input class="mini-textbox" id="oilTest" name="oilTest"/>
                <li style="margin-left: 10px">
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">采集日期: </span>
                    <input class="mini-datepicker" id="createtime1" name="createtime1" showTime="true"/>
                </li>
                <li>
                    <span class="text-to">至：</span>
                    <input class="mini-datepicker" id="createtime2" name="createtime2" showTime="true"/>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <a id="editMsg" class="mini-button " style="margin-right: 5px" plain="true"
                       onclick="addNew()">新增</a>
                    <a class="mini-button" id="importId" style="margin-left: 5px" onclick="openImportWindow()">导入</a>
                </li>
            </ul>
        </form>
        <span class="searchSelectionBtn" onclick="no_search(this ,'searchForm')">
				<i class="icon-sc-lower"></i>
			</span>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="oilListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         sizeList="[50,100,200]" pageSize="50"
         url="${ctxPath}/environment/core/Oil/queryList.do"
         idField="id" allowAlternating="true" showPager="true" multiSelect="true">
        <div property="columns">
            <div type="checkcolumn" width="10"></div>
            <div type="indexcolumn" align="center" width="10">序号</div>
            <div name="action" cellCls="actionIcons" width="25" headerAlign="center" align="center"
                 renderer="onMessageActionRenderer" cellStyle="padding:0;">操作
            </div>
            <div field="oilNation" headerAlign='center' align='center' width="30">国家区域</div>
            <div field="oilType" headerAlign='center' align='center' width="15">油液类型</div>
            <div field="oilDate" width="25" dateFormat="yyyy-MM-dd" headerAlign='center' align="center">采集日期</div>
            <div field="oilTest" width="25" headerAlign="center" align="center" allowSort="true">检测机构</div>
            <div field="testStandard" width="25" headerAlign="center" align="center" allowSort="true">检测标准</div>
            <div field="oilCompliance" width="25" headerAlign="center" align="center" allowSort="true">合规性</div>
            <div field="fujian" width="20" headerAlign="center" align="center" allowSort="true" renderer="fujian">附件
            </div>
            <div field="auditStatus" width="20" headerAlign="center" align="center" allowSort="true"
                 renderer="auditStatus">状态
            </div>
            <div field="userName" width="20" headerAlign="center" align="center" allowSort="true">创建人</div>

        </div>
    </div>
</div>
<div id="importWindow" title="环保信息法规政策导入窗口" class="mini-window" style="width:750px;height:280px;"
     showModal="true" showFooter="false" allowResize="true">
    <p style="display: inline-block;color: red;font-size:15px;vertical-align: middle">
    </p>
    <div style="display: inline-block;float: right;">
        <a id="importBtn" class="mini-button" onclick="importProduct()">导入</a>
        <a id="closeWindow" class="mini-button btn-red" onclick="closeImportWindow()">关闭</a>
    </div>
    <div class="mini-fit" style="font-size: 14px;margin-top: 30px">
        <form id="formImport" method="post">
            <table class="table-detail" cellspacing="1" cellpadding="0">
                <tr>
                    <td style="width: 30%">上传模板：</td>
                    <td style="width: 70%;">
                        <a href="#" style="color:blue;text-decoration:underline;" onclick="downImportOil()">全球油液调研导入模板.xlsx</a>
                    </td>
                </tr>
                <tr>
                    <td style="width: 30%">操作：</td>
                    <td>
                        <input class="mini-textbox" style="width:60%;float: left" id="fileName" name="fileName"
                               readonly/>
                        <input id="inputFile" style="display:none;" type="file" onchange="getSelectFile()"
                               accept="application/xls"/>
                        <a id="uploadFileBtn" class="mini-button" style="float: left;margin-left: 10px"
                           onclick="uploadFile">选择文件</a>
                        <a id="clearFileBtn" class="mini-button" style="float: left;margin-left: 10px"
                           onclick="clearUploadFile">清除</a>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var importWindow = mini.get("importWindow");
    var jsUseCtxPath = "${ctxPath}";
    var oilListGrid = mini.get("oilListGrid");
    var currentUserId = "${currentUserId}";
    var currentUserDep = "${currentUserDep}";
    var currentUserNo = "${currentUserNo}";
    var isYELR =${isYELR};
    var deptname = "${deptname}";
    if (!isYELR && currentUserNo != "admin") {
        mini.get("editMsg").setEnabled(false);
        mini.get("importId").setEnabled(false);
    }

    function onMessageActionRenderer(e) {
        var record = e.record;
        var oilId = record.oilId;
        var status = record.auditStatus;
        var s = '';
        s += '<span  title="查看" onclick="oilDetail(\'' + oilId + '\')">查看</span>';
        if ((status == '待提交' || status == '驳回待提交') && currentUserId == record.CREATE_BY_) {
            s += '<span  title="编辑" onclick="addNew(\'' + oilId + '\')">编辑</span>';
            s += '<span  title="删除" onclick="removeOil(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
        }
        if (status == '正在审核' && (deptname=='质量保证部'||deptname=='工程中心')) {
            s += '<span  title="办理" onclick="task(\'' + oilId + '\')">办理</span>';
        }
        if (status == '审核通过' && currentUserId == record.CREATE_BY_) {
            s += '<span  title="删除" onclick="removeOil(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
        }
        return s;
    }

    function fujian(e) {
        var record = e.record;
        var oilId = record.oilId;
        var s = '';
        s += '<span style="color:dodgerblue" title="附件列表" onclick="oilFile(\'' + oilId + '\')">附件列表</span>';
        return s;
    }

    function auditStatus(e) {
        var record = e.record;
        var status = record.auditStatus;
        var result = '';
        if (status == '审核通过') {
            result = '<span style="color: deepskyblue;cursor: pointer;">审核通过</span>';
        }
        if (status == "待提交") {
            result = '<span style="color: gold;cursor: pointer;">待提交</span>';
        }
        if (status == "正在审核") {
            result = '<span style="color: orange;cursor: pointer;">正在审核</span>';
        }
        if (status == "驳回待提交") {
            result = '<span style="color: red;cursor: pointer;">驳回待提交</span>';
        }
        return result;
    }
</script>
<redxun:gridScript gridId="oilListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
