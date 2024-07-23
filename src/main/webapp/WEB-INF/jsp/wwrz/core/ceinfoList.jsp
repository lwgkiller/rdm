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
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">销售型号: </span>
                    <input class="mini-textbox" id="saleModel" name="saleModel" />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">配置信息识别码: </span>
                    <input class="mini-textbox" id="onlyNum" name="onlyNum" />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">噪声指令证书编号: </span>
                    <input class="mini-textbox" id="zsNum" name="zsNum" />
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <a id="editMsg" class="mini-button " style="margin-right: 5px" plain="true"
                       onclick="addNew()">新增</a>
                </li>
            </ul>
        </form>
        <span class="searchSelectionBtn" onclick="no_search(this ,'searchForm')">
				<i class="icon-sc-lower"></i>
			</span>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="ceinfoListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         sizeList="[50,100,200]" pageSize="50"
         url="${ctxPath}/wwrz/core/CE/queryInfoList.do"
         idField="id" allowAlternating="true" showPager="true" multiSelect="true">
        <div property="columns">
            <div type="checkcolumn" width="10"></div>
            <div type="indexcolumn" headerAlign="center" align="center" width="10">序号</div>
            <div name="action" cellCls="actionIcons" width="25" headerAlign="center" align="center"
                 renderer="onMessageActionRenderer" cellStyle="padding:0;">操作
            </div>
            <div field="onlyNum" width="15" headerAlign="center" align="center" allowSort="true">配置信息识别码</div>
            <div field="saleModel" width="15" headerAlign="center" align="center" allowSort="true">销售型号</div>
            <div field="designModel" width="15" dateFormat="yyyy-MM-dd" headerAlign='center' align="center">设计型号</div>
            <div field="materialNum" width="20" headerAlign="center" align="center" allowSort="true">物料号</div>
            <div field="emission" width="10" headerAlign="center" align="center" allowSort="true">排放标准</div>
            <div field="zsNum" width="20" headerAlign="center" align="center" allowSort="true">噪声指令证书编号</div>
            <div field="zsStartDate"   width="20" align="center" headerAlign="center" allowSort="true" dateFormat="yyyy-MM-dd">证书签发日期</div>
            <div field="zsEndDate"   width="20" align="center" headerAlign="center" allowSort="true" dateFormat="yyyy-MM-dd">证书有效期</div>
            <div field="noteStatus" headerAlign='center' align='center' width="20" renderer="onValidStatus">台账状态</div>
            <div field="userName" headerAlign='center' align='center' width="20">创建人</div>
            <div field="CREATE_TIME_"   width="20" align="center" headerAlign="center" allowSort="true" dateFormat="yyyy-MM-dd">创建时间</div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var ceinfoListGrid = mini.get("ceinfoListGrid");
    var currentUserId = "${currentUserId}";
    var currentUserDep = "${currentUserDep}";
    var currentUserNo = "${currentUserNo}";

    function onMessageActionRenderer(e) {
        var record = e.record;
        var ceinfoId = record.ceinfoId;
        var s = '';
        s += '<span  title="查看" onclick="ceinfoDetail(\'' + ceinfoId + '\')">查看</span>';
        if (currentUserId == record.CREATE_BY_||currentUserNo=='admin') {
            s += '<span  title="编辑" onclick="ceinfoEdit(\'' + ceinfoId + '\')">编辑</span>';
            s += '<span  title="删除" onclick="removeCeinfo(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
        }
        return s;
    }

    $(function () {
        searchFrm();
        if(currentUserId!='1'){
            $("#editMsg").hide();
        }
    });

    function onValidStatus(e) {
        var record = e.record;
        var valid = record.noteStatus;
        var _html = '';
        var color = '';
        var text = '';
        if (valid == '0') {
            color = '#2edfa3';
            text = "有效";
        } else if (valid == '1') {
            color = 'red';
            text = "作废";
        }
        _html = '<span style="color: ' + color + '">' + text + '</span>'
        return _html;
    }

    function addNew() {
        mini.open({
            title: "新增",
            url: jsUseCtxPath + "/wwrz/core/CE/edit.do?action=add",
            width: 1050,
            height: 550,
            allowResize: true,
            onload: function () {
                searchFrm();
            },
            ondestroy:function () {
                searchFrm();
            }
        });
    }

    function ceinfoEdit(ceinfoId) {
        mini.open({
            title: "新增",
            url: jsUseCtxPath + "/wwrz/core/CE/edit.do?action=edit&ceinfoId=" + ceinfoId,
            width: 1050,
            height: 550,
            allowResize: true,
            onload: function () {
                searchFrm();
            },
            ondestroy:function () {
                searchFrm();
            }
        });
    }

    function ceinfoDetail(ceinfoId) {
        var action = "detail";
        mini.open({
            title: "明细",
            url: jsUseCtxPath + "/wwrz/core/CE/edit.do?action=" + action + "&ceinfoId=" + ceinfoId,
            width: 1050,
            height: 550,
            allowResize: true,
            onload: function () {
                searchFrm();
            }
        });
    }


    function removeCeinfo(record) {
        var rows = [];
        if (record) {
            rows.push(record);
        } else {
            rows = ceinfoListGrid.getSelecteds();
        }
        if (rows.length <= 0) {
            mini.alert("请至少选中一条记录");
            return;
        }
        mini.confirm("确定删除选中记录？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                var rowIds = [];
                for (var i = 0, l = rows.length; i < l; i++) {
                    var r = rows[i];
                    rowIds.push(r.ceinfoId);
                }
                _SubmitJson({
                    url: jsUseCtxPath + "/wwrz/core/CE/deleteCeinfo.do",
                    method: 'POST',
                    showMsg:false,
                    data: {ids: rowIds.join(',')},
                    success: function (data) {
                        if (data) {
                            mini.alert(data.message);
                            searchFrm();
                        }
                    }
                });
            }
        });
    }

    function addCeinfo() {
        var url = jsUseCtxPath + "/zlgjNPI/core/wwrz/core/CE/list.do?type=keyDesign&belongbj=" + type;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (ceinfoListGrid) {
                    ceinfoListGrid.reload()
                }
            }
        }, 1000);
    }
</script>
<redxun:gridScript gridId="ceinfoListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
