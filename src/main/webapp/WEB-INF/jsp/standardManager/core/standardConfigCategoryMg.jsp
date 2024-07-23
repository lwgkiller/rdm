
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
	<title>标准类别配置</title>
	<%@include file="/commons/list.jsp"%>
	<script src="${ctxPath}/scripts/standardManager/standardUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar" >
	<ul class="toolBtnBox">
		<li style="float: left">
			<a class="mini-button" iconCls="icon-add" onclick="addData()"><spring:message code="page.standardConfigCategoryMg.name1" /></a>
			<a class="mini-button" iconCls="icon-reload" onclick="refreshData()" plain="true"><spring:message code="page.standardConfigCategoryMg.name2" /></a>
			<a class="mini-button btn-red" plain="true" onclick="removeData()"><spring:message code="page.standardConfigCategoryMg.name3" /></a>
		</li>
		<span class="separator"></span>
		<li>
			<a class="mini-button" iconCls="icon-save" onclick="saveData()"><spring:message code="page.standardConfigCategoryMg.name4" /></a>
			<p style="display: inline-block;color: #888;font-size:15px;vertical-align: middle">（<image src="${ctxPath}/styles/images/notice.png" style="margin-right:5px;vertical-align: middle;height: 15px"/><spring:message code="page.standardConfigCategoryMg.name5" />）</p>
		</li>

	</ul>
</div>
<div class="mini-fit" style="height: 100%;">
	<div id="standardCategoryGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" url="${ctxPath}/standardManager/core/standardConfig/categoryQuery.do"
		  idField="id" allowAlternating="true" showPager="false" multiSelect="true" allowCellEdit="true" allowCellSelect="true"
		 editNextOnEnterKey="true"  editNextRowCell="true">
		<div property="columns">
			<div type="checkcolumn" width="20"></div>
			<div field="categoryCode" name="categoryCode" width="60" headerAlign="center" align="center" ><spring:message code="page.standardConfigCategoryMg.name6" /><span style="color: #ff0000">*</span>
				<input property="editor" class="mini-textbox"/>
			</div>
			<div field="categoryName" name="categoryName" width="60" headerAlign="center" align="center" ><spring:message code="page.standardConfigCategoryMg.name7" /><span style="color: #ff0000">*</span>
				<input property="editor" class="mini-textbox"/>
			</div>
			<div field="creator" width="80" headerAlign="center" align="center" ><spring:message code="page.standardConfigCategoryMg.name8" />
				<input property="editor" class="mini-textbox" readonly/>
			</div>
			<div field="CREATE_TIME_" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" width="120" headerAlign="center" ><spring:message code="page.standardConfigCategoryMg.name9" />
				<input property="editor" class="mini-textbox" readonly/>
			</div>
			<div field="updator" width="80" headerAlign="center" align="center" ><spring:message code="page.standardConfigCategoryMg.name10" />
				<input property="editor" class="mini-textbox" readonly/>
			</div>
			<div field="UPDATE_TIME_" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" width="120" headerAlign="center" ><spring:message code="page.standardConfigCategoryMg.name11" />
				<input property="editor" class="mini-textbox" readonly/>
			</div>
		</div>
	</div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var isManager=whetherIsStandardManager(${currentUserRoles});

    var standardCategoryGrid = mini.get("standardCategoryGrid");
    standardCategoryGrid.load();

    standardCategoryGrid.on("cellbeginedit", function (e) {
        var record = e.record;
        if(!isManager) {
            e.editor.setEnabled(false);
        }
    });

    function addData() {
        if(!isManager) {
            mini.alert(standardConfigCategoryMg_my);
            return;
		}
        var newRow = { };
        standardCategoryGrid.addRow(newRow, 0);
        standardCategoryGrid.beginEditCell(newRow, "categoryCode");
    }

    function removeData() {
        if(!isManager) {
            mini.alert(standardConfigCategoryMg_my);
            return;
        }
        var rows = standardCategoryGrid.getSelecteds();
        if (rows.length > 0) {
            standardCategoryGrid.removeRows(rows, false);
        } else {
            mini.alert(standardConfigCategoryMg_qzs);
            return;
		}
    }

    function refreshData() {
        standardCategoryGrid.load();
    }

    standardCategoryGrid.on("beforeload", function (e) {
        if (standardCategoryGrid.getChanges().length > 0) {
            if (confirm(standardConfigCategoryMg_yzs)) {
                e.cancel = true;
            }
        }
    });
    
    function saveData() {
        if(!isManager) {
            mini.alert(standardConfigCategoryMg_my);
            return;
        }
        var data = standardCategoryGrid.getChanges();
        var message = standardConfigCategoryMg_sj;
        var needReload = true;
        if (data.length > 0) {
            for (var i = 0; i < data.length; i++) {
                if(data[i]._state=='removed') {
                    continue;
                }
                if (!data[i].categoryCode || !data[i].categoryName) {
                    message = standardConfigCategoryMg_qtx;
                    needReload = false;
                    break;
                }
            }
            if(needReload) {
                var json = mini.encode(data);
                $.ajax({
                    url: jsUseCtxPath+"/standardManager/core/standardConfig/categorySave.do",
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
            title: standardConfigCategoryMg_tsxx,
            iconCls: "mini-messagebox-info",
            buttons: ["ok"],
            message: message,
            callback: function (action) {
                if (action == "ok" && needReload) {
                    standardCategoryGrid.reload();
                }
            }
        });
    }

</script>
</body>
</html>