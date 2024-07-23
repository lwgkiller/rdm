<%--
  Created by IntelliJ IDEA.
  User: zhangwentao
  Date: 2022-8-29
  Time: 10:01
  To change this template use File | Settings | File Templates.
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>出口产品零件图册机型待制作列表</title>
    <%@include file="/commons/list.jsp"%>
</head>
<body>
<div class="mini-toolbar" >
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul >
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">需求通知单编号: </span>
                    <input class="mini-textbox" id="demandNo" name="demandNo">
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">设计物料编码: </span>
                    <input class="mini-textbox" id="matCode" name="matCode">
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">设计型号: </span>
                    <input class="mini-textbox" id="designType" name="designType" />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">销售型号: </span>
                    <input class="mini-textbox" id="saleType" name="saleType" />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">随机文件语言: </span>
                    <input class="mini-textbox" id="languages" name="languages" />
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px"  plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px"  plain="true" onclick="clearForm()">清空查询</a>
                    <a class="mini-button" style="margin-right: 5px"  plain="true" onclick="exportmodelMadeWait()">导出</a>
                </li>
            </ul>
        </form>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="modelMadeWaitListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onlyCheckSelection="true"
         url="${ctxPath}/serviceEngineering/core/exportPartsAtlas/modelMadeWaitListQuery.do" idField="id"
         multiSelect="true" showColumnsMenu="true" sizeList="[50,100,200]" pageSize="50" allowAlternating="true" pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div name="action" cellCls="actionIcons" width="100" headerAlign="center" align="center" renderer="onActionRenderer" cellStyle="padding:0;">操作</div>
            <div field="demandNo"  width="80" headerAlign="center" align="center" allowSort="true"  renderer="onDemandNoRenderer">需求通知单编号</div>
            <div field="matCode"  width="80" headerAlign="center" align="center" allowSort="true">设计物料编码</div>
            <div field="designType"  width="100" headerAlign="center" align="center" allowSort="false">设计型号</div>
            <div field="saleType"   width="80" headerAlign="center" align="center"allowSort="false">销售型号</div>
            <div field="languages"  width="120" headerAlign="center" align="center" allowSort="false">随机文件语言</div>
            <div field="needTime"  width="70" headerAlign="center" align="center" allowSort="false">交货时间</div>
        </div>
    </div>
</div>
<!--导出Excel相关HTML-->
<form id="excelForm" action="${ctxPath}/serviceEngineering/core/exportPartsAtlas/exportModelMadeWaitList.do" method="post" target="excelIFrame">
    <input type="hidden" name="filter" id="filter"/>
</form>
<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var modelMadeWaitListGrid=mini.get("modelMadeWaitListGrid");
    var currentUserId="${currentUserId}";
    var currentUserNo="${currentUserNo}";
    var fwgcsUser=${fwgcsUser};
    $(function () {
        searchFrm();
        if(!fwgcsUser && currentUserNo != 'admin') {
            mini.get("addModelMadeBtn").setEnabled(false);
        }
    });
    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var id = record.id;
        var instId=record.instId;
        var matcode = record.matCode;
        var designType = record.designType;
        var s = '<span  title="新增机型制作" onclick="addModelMade(\'' + matcode + '\',\'' + designType + '\')">新增机型制作</span>';
        return s;
    }
    //新增流程（后台根据配置的表单进行跳转）
    function addModelMade(matcode,designType) {
        var url = jsUseCtxPath + "/bpm/core/bpmInst/LJTCJXZZ/start.do?matCode="+matcode+"&designType="+designType;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (modelMadeWaitListGrid) {
                    modelMadeWaitListGrid.reload()
                }
            }
        }, 1000);
    }
    function onDemandNoRenderer(e) {
        var record = e.record;
        var demandNo = record.demandNo;
        if(!demandNo) {
            return;
        }
        var demandId = record.demandId;
        return '<span style="text-decoration:underline;cursor: pointer;color:#409EFF" onclick="demandDetail(\''+demandId+'\')">'+demandNo+'</span>';
    }
    function demandDetail(demandId) {
        var url = jsUseCtxPath + "/serviceEngineering/core/exportPartsAtlas/demandEditPage.do?demandId=" + demandId;
        var winObj = window.open(url);
    }
    //导出
    function exportmodelMadeWait(){
        var queryParam = [];
        var demandNo = $.trim(mini.get("demandNo").getValue());
        if (demandNo) {
            queryParam.push({name: "demandNo", value: demandNo});
        }
        var matCode = $.trim(mini.get("matCode").getValue());
        if (matCode) {
            queryParam.push({name: "matCode", value: matCode});
        }
        var designType = $.trim(mini.get("designType").getValue());
        if (designType) {
            queryParam.push({name: "designType", value: designType});
        }
        var languages = $.trim(mini.get("languages").getValue());
        if (languages) {
            queryParam.push({name: "languages", value: languages});
        }

        $("#filter").val(mini.encode(queryParam));
        var excelForm = $("#excelForm");
        excelForm.submit();
    }

</script>
<redxun:gridScript gridId="modelMadeWaitListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl="" />
</body>
</html>
