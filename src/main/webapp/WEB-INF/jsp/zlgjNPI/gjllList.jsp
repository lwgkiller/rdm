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

    <script src="${ctxPath}/scripts/zlgjNPI/gjllList.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">问题类型: </span>
                    <input id="wtlx" name="wtlx" class="mini-combobox" style="width:98%;"
                           textField="value" valueField="key" emptyText="请选择..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                           data="[{'key' : 'XPSZ','value' : '新品试制'},{'key' : 'XPZDSY','value' : '新品整机试验'}
                           ,{'key' : 'XPLS','value' : '新品路试'},{'key' : 'CNWT','value' : '厂内问题'}
                           ,{'key' : 'SCWT','value' : '市场问题'},{'key' : 'HWWT','value' : '海外问题'}
                           ,{'key' : 'WXBLX','value' : '维修便利性'},{'key' : 'LBJSY','value' : '新品零部件试验'}]"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">机型: </span>
                    <input class="mini-textbox" id="smallJiXing" name="smallJiXing" />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">机型类别: </span>
                    <input id="jiXing" name="jiXing" class="mini-combobox" style="width:100px;"
                           textField="value" valueField="key" emptyText="请选择..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                           data="[{'key' : '微挖','value' : '微挖'},{'key' : '轮挖','value' : '轮挖'},{'key' : '小挖','value' : '小挖'},
                                   {'key' : '中挖','value' : '中挖'},{'key' : '大挖','value' : '大挖'},{'key' : '特挖','value' : '特挖'},
                                   {'key' : '属具','value' : '属具'},{'key' : '新能源','value' : '新能源'}
                                   ,{'key' : '海外','value' : '海外'}]"
                    />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">零部件名称: </span>
                    <input class="mini-textbox" id="gzlj" name="gzlj" />
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <a id="exportGjll" class="mini-button" style="margin-right: 5px" plain="true" onclick="exportGjll()">导出</a>
                    <a id="editMsg" class="mini-button " style="margin-right: 5px" plain="true"
                       onclick="addNew()">新增</a>
                    <a id="link" class="mini-button" style="margin-right: 5px;display: none" plain="true"
                       onclick="linkGjll()">关联改进履历</a>
                </li>
            </ul>
        </form>
        <span class="searchSelectionBtn" onclick="no_search(this ,'searchForm')">
				<i class="icon-sc-lower"></i>
			</span>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="gjllListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         sizeList="[50,100,200]" pageSize="50" allowCellWrap="true"
         url="${ctxPath}/zlgjNPI/core/Gjll/queryList.do"
         idField="id" allowAlternating="true" showPager="true" multiSelect="true">
        <div property="columns">
            <div type="checkcolumn" width="10"></div>
            <div type="indexcolumn" align="center" width="10">序号</div>
            <div name="action" cellCls="actionIcons" width="25" headerAlign="center" align="center"
                 renderer="onMessageActionRenderer" cellStyle="padding:0;">操作
            </div>
            <div field="wtlx" headerAlign='center' align='center'  width="20" renderer="wtlxRenderer">问题类型</div>
            <div field="zlgjNumber" headerAlign='center' align='center'  width="40" renderer="linkZlgj">问题编号</div>
            <div field="jiXing" headerAlign='center' align='center'  width="15">机型类别</div>
            <div field="smallJiXing" headerAlign='center' align='center' width="20">机型</div>
            <div field="gzlj" width="20" headerAlign="center" align="center" allowSort="true">零部件名称</div>
            <div field="lbjgys" width="30" headerAlign="center" align="center" allowSort="true">零部件供应商</div>
            <div field="wtms" width="40" dateFormat="yyyy-MM-dd" headerAlign='center' align="center">问题描述</div>
            <div field="fujian" width="20" headerAlign="center" align="center" allowSort="true" renderer="fujian">问题图片
            </div>
            <div field="zrrName" width="15" headerAlign="center" align="center" allowSort="true">责任人</div>
            <div field="ssbmName" width="20" headerAlign="center" align="center" allowSort="true">责任部门</div>
            <div field="cqcs" width="40" headerAlign="center" align="center" allowSort="true">改进方案</div>
            <div field="tzdh" width="20" headerAlign="center" align="center" allowSort="true">标准化文件</div>
            <div field="yjqhch" width="20" headerAlign="center" align="center" allowSort="true">预计切换车号</div>
            <div field="qhTime" sortField="qhTime"  width="20" align="center" headerAlign="center" allowSort="true" dateFormat="yyyy-MM-dd HH:mm:ss">实际完成时间</div>
            <div field="creator" width="15" headerAlign="center" align="center" allowSort="true">创建人</div>
        </div>
    </div>
</div>
<form id="excelForm" action="${ctxPath}/zlgjNPI/core/Gjll/exportGjllList.do" method="post" target="excelIFrame">
    <input type="hidden" name="filter" id="filter"/>
</form>
<script type="text/javascript">
    mini.parse();
    var importWindow = mini.get("importWindow");
    var jsUseCtxPath = "${ctxPath}";
    var gjllListGrid = mini.get("gjllListGrid");
    var currentUserId = "${currentUserId}";
    var currentUserDep = "${currentUserDep}";
    var currentUserNo = "${currentUserNo}";
    var belongbj = "${belongbj}";
    var type = "${type}";

    if (type != "" && belongbj != "") {
        $("#link").show();
        $("#exportGjll").hide();
        $("#editMsg").hide();
    }

    function linkZlgj(e) {
        var record = e.record;
        var wtId = record.wtId;
        var zlgjNumber = record.zlgjNumber;
        var status = "SUCCESS_END"
        if(zlgjNumber){
            var linkStr='<span  style="color: #409EFF;cursor: pointer;" title="明细" onclick="linkDetail(\'' + wtId +'\',\''+status+ '\')">'+zlgjNumber+'</span>';
        }else {
            linkStr='';
        }
        return linkStr;
    }

    function onMessageActionRenderer(e) {
        var record = e.record;
        var gjId = record.gjId;
        var s = '';
        s += '<span  title="查看" onclick="gjllDetail(\'' + gjId + '\')">查看</span>';
        if (currentUserId == record.CREATE_BY_) {
            s += '<span  title="编辑" onclick="addNew(\'' + gjId + '\')">编辑</span>';
            s += '<span  title="删除" onclick="removeGjll(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
        }
        return s;
    }

    function linkDetail(wtId, status) {
        var action = "detail";
        var url = jsUseCtxPath + "/xjsdr/core/zlgj/editPage.do?action=" + action + "&wtId=" + wtId + "&status=" + status;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (gjllListGrid) {
                    gjllListGrid.reload()
                }
            }
        }, 1000);
    }

    function wtlxRenderer(e) {
        var record = e.record;
        var wtlx = record.wtlx;
        var wtlxName='';
        switch (wtlx) {
            case 'XPSZ':
                wtlxName='新品试制';
                break;
            case 'XPZDSY':
                wtlxName='新品整机试验';
                break;
            case 'XPLS':
                wtlxName='新品路试';
                break;
            case 'CNWT':
                wtlxName='厂内问题';
                break;
            case 'SCWT':
                wtlxName='市场问题';
                break;
            case 'HWWT':
                wtlxName='海外问题';
                break;
            case 'WXBLX':
                wtlxName='维修便利性';
                break;
            case 'LBJSY':
                wtlxName='新品零部件试验';
                break;
        }
        return wtlxName;
    }

    function fujian(e) {
        var record = e.record;
        var gjId = record.gjId;
        var s = '';
        s += '<span style="color:dodgerblue" title="图片列表" onclick="gjllFile(\'' + gjId + '\')">附件列表</span>';
        return s;
    }
    function exportGjll() {
        var parent = $(".search-form");
        var inputAry = $("input", parent);
        var params = [];
        inputAry.each(function (i) {
            var el = $(this);
            var obj = {};
            obj.name = el.attr("name");
            if (!obj.name) return true;
            obj.value = el.val();
            params.push(obj);
        });
        $("#filter").val(mini.encode(params));
        var excelForm = $("#excelForm");
        excelForm.submit();
    }


    function linkGjll() {
        var rows = gjllListGrid.getSelecteds();
        if (rows.length <= 0) {
            mini.alert("请至少选中一条记录");
            return;
        }
        var ids = [];
        for (var i = 0, l = rows.length; i < l; i++) {
            var r = rows[i];
            ids.push(r.gjId);
        }
        var url = jsUseCtxPath + "/Gjll/linkGjll.do?belongbj=" + belongbj;
        _SubmitJson({
            url: url,
            method: 'POST',
            showMsg: false,
            data: {ids: ids.join(',')},
            success: function (data) {
                if (data) {
                    mini.alert(data.message);
                    searchStandard();
                }
            }
        });
    }
</script>
<redxun:gridScript gridId="gjllListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
