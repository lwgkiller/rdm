
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
	<title>标准归口部门配置</title>
	<%@include file="/commons/list.jsp"%>
	<script src="${ctxPath}/scripts/standardManager/standardUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar" >
	<ul class="toolBtnBox">
		<li style="float: left">
			<a class="mini-button" iconCls="icon-add" onclick="addData()"><spring:message code="page.standardConfigBelongDepMg.name1" /></a>
			<a class="mini-button" iconCls="icon-reload" onclick="refreshData()" plain="true"><spring:message code="page.standardConfigBelongDepMg.name2" /></a>
			<a class="mini-button btn-red" plain="true" onclick="removeData()"><spring:message code="page.standardConfigBelongDepMg.name3" /></a>
		</li>
		<span class="separator"></span>
		<li>
			<a class="mini-button" iconCls="icon-save" onclick="saveData()"><spring:message code="page.standardConfigBelongDepMg.name4" /></a>
			<p style="display: inline-block;color: #888;font-size:15px;vertical-align: middle">（<image src="${ctxPath}/styles/images/notice.png" style="margin-right:5px;vertical-align: middle;height: 15px"/><spring:message code="page.standardConfigBelongDepMg.name5" />）</p>
		</li>

	</ul>
</div>
<div class="mini-fit" style="height: 100%;">
	<div id="standardBelongDepGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" url="${ctxPath}/standardManager/core/standardConfig/belongDepQuery.do"
		  idField="id" allowAlternating="true" showPager="false" multiSelect="true" allowCellEdit="true" allowCellSelect="true"
		 editNextOnEnterKey="true"  editNextRowCell="true">
		<div property="columns">
			<div type="checkcolumn" width="20"></div>
			<div field="belongDepName" name="belongDepName" width="60" headerAlign="center" align="center" ><spring:message code="page.standardConfigBelongDepMg.name6" /><span style="color: #ff0000">*</span>
				<input property="editor" class="mini-textbox"/>
			</div>
			<div field="creator" width="80" headerAlign="center" align="center" ><spring:message code="page.standardConfigBelongDepMg.name7" />
				<input property="editor" class="mini-textbox" readonly/>
			</div>
			<div field="CREATE_TIME_" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" width="120" headerAlign="center" ><spring:message code="page.standardConfigBelongDepMg.name8" />
				<input property="editor" class="mini-textbox" readonly/>
			</div>
			<div field="updator" width="80" headerAlign="center" align="center" ><spring:message code="page.standardConfigBelongDepMg.name9" />
				<input property="editor" class="mini-textbox" readonly/>
			</div>
			<div field="UPDATE_TIME_" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" width="120" headerAlign="center" ><spring:message code="page.standardConfigBelongDepMg.name10" />
				<input property="editor" class="mini-textbox" readonly/>
			</div>
		</div>
	</div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var isManager=whetherIsStandardManager(${currentUserRoles});

    var standardBelongDepGrid = mini.get("standardBelongDepGrid");
    standardBelongDepGrid.load();

    standardBelongDepGrid.on("cellbeginedit", function (e) {
        var record = e.record;
        if(!isManager) {
            e.editor.setEnabled(false);
        }
    });

    function addData() {
        if(!isManager) {
            mini.alert(standardConfigBelongDepMg_myczqx);
            return;
		}
        var newRow = { };
        standardBelongDepGrid.addRow(newRow, 0);
        standardBelongDepGrid.beginEditCell(newRow, "belongDepName");
    }

    function removeData() {
        if(!isManager) {
            mini.alert(standardConfigBelongDepMg_myczqx);
            return;
        }
        var rows = standardBelongDepGrid.getSelecteds();
        if (rows.length > 0) {
            standardBelongDepGrid.removeRows(rows, false);
        } else {
            mini.alert(standardConfigBelongDepMg_qzsxz);
            return;
		}
    }

    function refreshData() {
        standardBelongDepGrid.load();
    }

    standardBelongDepGrid.on("beforeload", function (e) {
        if (standardBelongDepGrid.getChanges().length > 0) {
            if (confirm(standardConfigBelongDepMg_yzs)) {
                e.cancel = true;
            }
        }
    });
    
    function saveData() {
        if(!isManager) {
            mini.alert(standardConfigBelongDepMg_myczqx);
            return;
        }
        var data = standardBelongDepGrid.getChanges();
        var message = standardConfigBelongDepMg_sjbc;
        var needReload = true;
        if (data.length > 0) {
            for (var i = 0; i < data.length; i++) {
                if(data[i]._state=='removed') {
                    continue;
                }
                if (!data[i].belongDepName) {
                    message = standardConfigBelongDepMg_qtxb;
                    needReload = false;
                    break;
                }
            }
            if(needReload) {
                var json = mini.encode(data);
                $.ajax({
                    url: jsUseCtxPath+"/standardManager/core/standardConfig/belongDepSave.do",
                    data: json,
                    type: "post",
                    contentType: 'application/json',
                    async:false,
                    success: function (result) {
                        if (result && result.message) {
                            message = result.message;
                        }
                    }
                });
			}
        }
        mini.showMessageBox({
            title: standardConfigBelongDepMg_tsxx,
            iconCls: "mini-messagebox-info",
            buttons: ["ok"],
            message: message,
            callback: function (action) {
                if (action == "ok" && needReload) {
                    standardBelongDepGrid.reload();
                }
            }
        });
    }

</script>
</body>
</html>