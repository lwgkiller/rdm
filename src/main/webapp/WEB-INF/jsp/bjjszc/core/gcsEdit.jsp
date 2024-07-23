<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>备件开发申请流程信息</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
</head>

<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 5px">
            <ul>
                <li style="margin-left: 10px">
                    <a id="addGcsDetail" style="margin-right: 5px" class="mini-button" onclick="addGcsDetail()">添加</a>
                    <a id="removeGcsDetail" style="margin-right: 5px" class="mini-button" onclick="removeGcsDetail()">删除</a>
                    <div style="display: inline-block" class="separator"></div>
                    <a id="saveGcs" style="margin-right: 5px" class="mini-button" onclick="saveGcs()">保存</a>
                </li>
            </ul>
        </form>
    </div>
</div>
<div class="mini-fit">
    <div id="detailListGrid" class="mini-datagrid" style="width: 100%; height: 100%"
         allowResize="false" allowCellWrap="true"
         idField="id" url="${ctxPath}/rdm/core/Kfsq/queryGcs.do?" autoload="true"
         allowCellEdit="true" allowCellSelect="true" oncellbeginedit="OnCellBeginEditDetail"
         multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
        <div property="columns">
            <div type="checkcolumn" width="10"></div>
            <div field="bjType" width="50" headerAlign="center" align="center">备件品类
                <input property="editor" class="mini-combobox" style="width:98%;"
                       textField="value" valueField="key" emptyText="请选择..."
                       required="false" allowInput="false" showNullItem="true"
                       nullItemText="请选择..."
                       data="[
                            {'key' : '油品','value' : '油品'},{'key' : '钎杆','value' : '钎杆'}
                           ,{'key' : '滤芯','value' : '滤芯'},{'key' : '属具','value' : '属具'}
                           ,{'key' : '斗齿','value' : '斗齿'},{'key' : '液压件','value' : '液压件'}
                           ,{'key' : '动力件','value' : '动力件'},{'key' : '底盘件','value' : '底盘件'}
                           ,{'key' : '电器件','value' : '电器件'},{'key' : '橡胶件','value' : '橡胶件'}
                           ,{'key' : '其他','value' : '其他'}]"
                /></div>
            <div field="resId" displayField="resName" width="50" headerAlign="center"
                 align="center">服务工程师
                <input property="editor" class="mini-user rxc" plugins="mini-user"
                       style="width:90%;height:34px;" allowinput="false" length="100" maxlength="100"
                       mainfield="no" single="false" name="resId" textname="resName"/></div>
        </div>
    </div>
    </td>
</div>
</div>


<script type="text/javascript">
    mini.parse();
    var action = "${action}";
    var jsUseCtxPath = "${ctxPath}";
    var currentUserId = "${currentUserId}";
    var currentTime = "${currentTime}";
    var detailListGrid = mini.get("detailListGrid");
    var gcsId = "${gcsId}";
    var currentUserMainDepId = "${currentUserMainDepId}";


    $(function () {
        //变更入口
        if (action == "detail") {
            mini.get("addGcsDetail").setEnabled(false);
            mini.get("removeGcsDetail").setEnabled(false);
        }
    });

    function OnCellBeginEditDetail(e) {
        var record = e.record, field = e.field;
        e.cancel = true;

        if (field == "resId") {
            e.cancel = false;
        }
    }

    function saveGcs() {
        var formValid = validFirst();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        var formData = detailListGrid.getChanges();
        $.ajax({
            url: jsUseCtxPath + '/rdm/core/Kfsq/saveGcs.do',
            type: 'post',
            async: false,
            data: mini.encode(formData),
            contentType: 'application/json',
            success: function (returnData) {
                if (returnData && returnData.message) {
                    mini.alert(returnData.message, '提示', function () {
                        if (returnData.success) {
                            detailListGrid.reload();
                        }
                    });
                }
            }
        });
    }


    function validFirst() {
        var detail = detailListGrid.getData();
        for (var i = 0; i < detail.length; i++) {
            if (detail[i].bjType == undefined || detail[i].bjType == "") {
                return {"result": false, "message": "请选择第" + (i + 1) + "行备件品类！"};
            }
            if (detail[i].resId == undefined || detail[i].resId == "") {
                return {"result": false, "message": "请填写第" + (i + 1) + "行服务工程师！"};
            }
        }
        return {"result": true};

    }


    function addGcsDetail() {
        var row = {};
        detailListGrid.addRow(row);
    }

    function removeGcsDetail() {
        var selecteds = detailListGrid.getSelecteds();
        var deleteArr = [];
        for (var i = 0; i < selecteds.length; i++) {
            var row = selecteds[i];
            deleteArr.push(row);
        }
        detailListGrid.removeRows(deleteArr);
    }


</script>
</body>
</html>
