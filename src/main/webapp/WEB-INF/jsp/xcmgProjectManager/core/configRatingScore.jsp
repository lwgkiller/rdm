
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
	<title>项目评价等级配置</title>
	<%@include file="/commons/list.jsp"%>
	<script src="${ctxPath}/scripts/xcmgProjectManager/projectConfig.js?version=${static_res_version}" type="text/javascript"></script>
	<script src="${ctxPath}/scripts/xcmgProjectManager/projectUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar" >
	<ul class="toolBtnBox">
		<li style="float: left">
			<a class="mini-button" iconCls="icon-add" onclick="addRatingScore()"><spring:message code="page.configRatingScore.name" /></a>
			<a class="mini-button" iconCls="icon-reload" onclick="refreshRatingScore()" plain="true"><spring:message code="page.configRatingScore.name1" /></a>
			<a class="mini-button btn-red" plain="true" onclick="removeRatingScore()"><spring:message code="page.configRatingScore.name2" /></a>
		</li>
		<span class="separator"></span>
		<li>
			<a class="mini-button" iconCls="icon-save" onclick="saveRatingScore()"><spring:message code="page.configRatingScore.name3" /></a>
			<p style="display: inline-block;color: #888;font-size:15px;vertical-align: middle">（<image src="${ctxPath}/styles/images/warn.png" style="margin-right:5px;vertical-align: middle;height: 15px"/><spring:message code="page.configRatingScore.name4" />）</p>
		</li>

	</ul>
</div>
<div class="mini-fit" style="height: 100%;">
	<div id="ratingScoreGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" url="${ctxPath}/xcmgProjectManager/core/config/ratingScoreList.do"
		  idField="ratingId" allowAlternating="true" showPager="false" multiSelect="true" allowCellEdit="true" allowCellSelect="true"
		 editNextOnEnterKey="true"  editNextRowCell="true">
		<div property="columns">
			<div type="checkcolumn" width="20"></div>
			<div field="ratingName" name="ratingName" width="100" headerAlign="center" align="center" ><spring:message code="page.configRatingScore.name5" /><span style="color: #ff0000">*</span>
				<input property="editor" class="mini-textbox"/>
			</div>
			<div field="minScore" name="minScore" width="60" headerAlign="center" align="center" ><spring:message code="page.configRatingScore.name6" /><span style="color: #ff0000">*</span>
				<input property="editor" class="mini-textbox"/>
			</div>
			<div field="maxScore" name="maxScore" width="60" headerAlign="center" align="center" ><spring:message code="page.configRatingScore.name7" /><span style="color: #ff0000">*</span>
				<input property="editor" class="mini-textbox"/>
			</div>
			<div field="minRatio" name="minRatio" width="60" headerAlign="center" align="center" ><spring:message code="page.configRatingScore.name8" /><span style="color: #ff0000">*</span>
				<input property="editor" class="mini-textbox"/>
			</div>
			<div field="maxRatio" name="maxRatio" width="60" headerAlign="center" align="center" ><spring:message code="page.configRatingScore.name9" /><span style="color: #ff0000">*</span>
				<input property="editor" class="mini-textbox"/>
			</div>
			<div field="creator" width="80" headerAlign="center" align="center" ><spring:message code="page.configRatingScore.name10" />
				<input property="editor" class="mini-textbox" readonly/>
			</div>
			<div field="CREATE_TIME_" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" width="120" headerAlign="center" ><spring:message code="page.configRatingScore.name11" />
				<input property="editor" class="mini-textbox" readonly/>
			</div>
			<div field="updator" width="80" headerAlign="center" align="center" ><spring:message code="page.configRatingScore.name12" />
				<input property="editor" class="mini-textbox" readonly/>
			</div>
			<div field="UPDATE_TIME_" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" width="120" headerAlign="center" ><spring:message code="page.configRatingScore.name13" />
				<input property="editor" class="mini-textbox" readonly/>
			</div>
		</div>
	</div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var isManager=whetherIsProjectManager(${currentUserRoles});

    var ratingScoreGrid = mini.get("ratingScoreGrid");
    ratingScoreGrid.load();

    ratingScoreGrid.on("cellbeginedit", function (e) {
        var record = e.record;
        if(!isManager) {
            e.editor.setEnabled(false);
        }
    });

    function addRatingScore() {
        if(!isManager) {
            mini.alert(configRatingScore_name);
            return;
		}
        var newRow = { name: "New Row" };
        ratingScoreGrid.addRow(newRow, 0);
        ratingScoreGrid.beginEditCell(newRow, "ratingName");
    }

    function removeRatingScore() {
        if(!isManager) {
            mini.alert(configRatingScore_name);
            return;
        }
        var rows = ratingScoreGrid.getSelecteds();
        if (rows.length > 0) {
            ratingScoreGrid.removeRows(rows, false);
        } else {
            mini.alert(configRatingScore_name1);
            return;
		}
    }

    function refreshRatingScore() {
        ratingScoreGrid.load();
    }

    ratingScoreGrid.on("beforeload", function (e) {
        if (ratingScoreGrid.getChanges().length > 0) {
            if (confirm(configRatingScore_name2)) {
                e.cancel = true;
            }
        }
    });
    
    function saveRatingScore() {
        var data = ratingScoreGrid.getChanges();
        var message = configRatingScore_name3;
        var needReload = true;
        if (data.length > 0) {
            for (var i = 0; i < data.length; i++) {
                if(data[i]._state=='removed') {
                    continue;
                }
                if (!data[i].minScore || !data[i].maxScore || !data[i].minRatio || !data[i].maxRatio || !data[i].ratingName) {
                    message = "请填写必填项！";
                    needReload = false;
                    break;
                }
                if (data[i].minScore > data[i].maxScore) {
                    message = "等级‘" + data[i].ratingName + "’的评价最低分需要小于等于评价最高分！";
                    needReload = false;
                    break;
                }
                if (data[i].minRatio > data[i].maxRatio) {
                    message = "等级‘" + data[i].ratingName + "’的系数最低分需要小于等于系数最高分！";
                    needReload = false;
                    break;
                }
            }
            if(needReload) {
                var json = mini.encode(data);
                $.ajax({
                    url: jsUseCtxPath+"/xcmgProjectManager/core/config/saveRatingScore.do",
                    data: json,
                    type: "post",
                    contentType: 'application/json',
                    async:false,
                    success: function (text) {
                        if (text && text.message) {
                            message = text.message;
                        }
                    }
                });
			}
        }
        mini.showMessageBox({
            title: configRatingScore_name4,
            iconCls: "mini-messagebox-info",
            buttons: ["ok"],
            message: message,
            callback: function (action) {
                if (action == "ok" && needReload) {
                    ratingScoreGrid.reload();
                }
            }
        });
    }

    ratingScoreGrid.on("cellcommitedit", function (e) {
        if (e.field == "minScore"||e.field == "maxScore") {
            var r = /^\+?[1-9][0-9]*$/;　　//正整数
            if(!r.test(e.value)){
                mini.alert(configRatingScore_name5);
                e.value='';
            }
        } else if(e.field=="minRatio"||e.field=="maxRatio") {
            var r = /^\d+(\.\d+)?$/;　　//非负浮点数
            if(!r.test(e.value)){
                mini.alert(configRatingScore_name6);
                e.value='';
            }
		}
    });

</script>
</body>
</html>