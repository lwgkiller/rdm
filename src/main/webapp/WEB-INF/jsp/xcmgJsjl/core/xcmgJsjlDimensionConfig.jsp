
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
	<title>业务交流维度配置</title>
	<%@include file="/commons/list.jsp"%>
	<script src="${ctxPath}/scripts/xcmgJsjl/xcmgJsjlDimensionConfig.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar" >
	<ul class="toolBtnBox">
		<li style="float: left">
			<a class="mini-button" iconCls="icon-add" onclick="addData()">新增</a>
			<a class="mini-button" iconCls="icon-reload" onclick="refreshData()" plain="true">刷新</a>
			<a class="mini-button btn-red" plain="true" onclick="removeData()">删除</a>
		</li>
		<span class="separator"></span>
		<li>
			<a class="mini-button" iconCls="icon-save" onclick="saveData()">保存</a>
			<p style="display: inline-block;color: #888;font-size:15px;vertical-align: middle">（<image src="${ctxPath}/styles/images/notice.png" style="margin-right:5px;vertical-align: middle;height: 15px"/>新增、删除、编辑后都需要进行保存操作）</p>
		</li>

	</ul>
</div>
<div class="mini-fit" style="height: 100%;">
	<div id="dimensionGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" url="${ctxPath}/jsjl/core/config/dimensionListQuery.do"
		  idField="id" allowAlternating="true" showPager="false" multiSelect="true" allowCellEdit="true" allowCellSelect="true"
		 editNextOnEnterKey="true"  editNextRowCell="true">
		<div property="columns">
			<div type="checkcolumn" width="20"></div>
			<div field="dimensionName" name="dimensionName" width="60" headerAlign="center" align="center" >维度名称<span style="color: #ff0000">*</span>
				<input property="editor" class="mini-textbox"/>
			</div>
            <div field="descp" name="descp" width="60" headerAlign="center" align="center" >维度标识<span style="color: #ff0000">*</span>
                <input property="editor" class="mini-textbox"/>
            </div>
			<div field="creator" width="80" headerAlign="center" align="center" >创建人
				<input property="editor" class="mini-textbox" readonly/>
			</div>
			<div field="CREATE_TIME_" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" width="120" headerAlign="center" >创建时间
				<input property="editor" class="mini-textbox" readonly/>
			</div>
			<div field="updator" width="80" headerAlign="center" align="center" >更新人
				<input property="editor" class="mini-textbox" readonly/>
			</div>
			<div field="UPDATE_TIME_" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" width="120" headerAlign="center" >更新时间
				<input property="editor" class="mini-textbox" readonly/>
			</div>
		</div>
	</div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
	var currentUserNo="${currentUserNo}";

    var dimensionGrid = mini.get("dimensionGrid");
    dimensionGrid.load();

    dimensionGrid.on("cellbeginedit", function (e) {
        var record = e.record;
        if(currentUserNo!='admin') {
            e.editor.setEnabled(false);
        }
    });

    function addData() {
        if(currentUserNo!='admin' && currentUserNo!='zhujia') {
            mini.alert("没有操作权限!");
            return;
		}
        var newRow = { };
        dimensionGrid.addRow(newRow, 0);
        dimensionGrid.beginEditCell(newRow, "dimensionName");
    }

    function removeData() {
        if(currentUserNo!='admin') {
            mini.alert("没有操作权限!");
            return;
        }
        var rows = dimensionGrid.getSelecteds();
        if (rows.length > 0) {
            dimensionGrid.removeRows(rows, false);
        } else {
            mini.alert("请至少选中一条记录");
            return;
		}
    }

    function refreshData() {
        dimensionGrid.load();
    }

    dimensionGrid.on("beforeload", function (e) {
        if (dimensionGrid.getChanges().length > 0) {
            if (confirm("有增删改的数据未保存，是否取消本次操作？")) {
                e.cancel = true;
            }
        }
    });
    
    function saveData() {
        if(currentUserNo!='admin') {
            mini.alert("没有操作权限!");
            return;
        }
        var data = dimensionGrid.getChanges();
        var message = "数据保存成功";
        var needReload = true;
        if (data.length > 0) {
            for (var i = 0; i < data.length; i++) {
                if(data[i]._state=='removed') {
                    continue;
                }
                if (!data[i].dimensionName) {
                    message = "请填写必填项！";
                    needReload = false;
                    break;
                }
            }
            if(needReload) {
                var json = mini.encode(data);
                $.ajax({
                    url: jsUseCtxPath+"/jsjl/core/config/dimensionConfigSave.do",
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
            title: "提示信息",
            iconCls: "mini-messagebox-info",
            buttons: ["ok"],
            message: message,
            callback: function (action) {
                if (action == "ok" && needReload) {
                    dimensionGrid.reload();
                }
            }
        });
    }

</script>
</body>
</html>