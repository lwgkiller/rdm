<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>战略规划-活动年份</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/xcmgProjectManager/projectConfig.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/xcmgProjectManager/projectUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
    <style type="text/css">
        .cellColor div {
            color: red;
        }
        .mini-grid-cell-inner, .mini-grid-headerCell-inner{
            line-height: 25px;
        }
    </style>
</head>
<body>
<div class="mini-toolbar">
    <ul class="toolBtnBox">
        <li>
            <span class="text" style="width:auto">活动名称: </span>
            <input id="key" name="KPI_name" class="mini-textbox" style="width: 150px" onenter="onHanlerEnter"/>
            <input id="test" class="mini-textbox" style="width: 150px;display: none"/>
            <a class="mini-button" style="margin-right: 5px" plain="true" id="KPI_name" onclick="searchByName()">查询</a>
            <a class="mini-button btn-red" onclick="refreshAchievementType()" plain="true">清空查询</a>
            <%--<input id="loadTableHeader" class="mini-textbox" style="width: 150px;display: none" name="loadTableHeader" />--%>
        </li>
        <span class="separator"></span>
        <li>
            <%--<a id="addKpi" class="mini-button" iconCls="icon-add" onclick="openEditorWindow()">新增</a>--%>
            <%--<a id="removeKpi" class="mini-button btn-red" plain="true" onclick="removeAchievementType()">删除</a>--%>
        </li>
        <span class="separator"></span>
        <li>
            <a id="saveKpi" class="mini-button" iconCls="icon-save" onclick="saveAchievementType()">保存</a>
            <p style="display: inline-block;color: #f6253e;font-size:15px;vertical-align: middle">（
                <image src="${ctxPath}/styles/images/warn.png"
                       style="margin-right:5px;vertical-align: middle;height: 15px"/>
                编辑、删除后都需要进行保存操作）
            </p>
        </li>

    </ul>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="listGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         url="${ctxPath}/strategicPlanning/core/zlghPlanning/zlgh/list.do"
         idField="id" multiSelect="true" showColumnsMenu="false" sizeList="[10,20,50,100,200]" pageSize="15"
         allowAlternating="true" allowCellEdit="true" allowCellSelect="true" editNextOnEnterKey="false"
         allowCellValid="true" allowCellWrap="true" oncellvalidation="onCellValidation"
         allowResizeColumn="false" pagerButtons="#pagerButtons">
        <div property="columns">
            <div name="zljcName" field="zljcName" width="200" headerAlign="center" align="center">
                战略举措名称
            </div>
            <div name="zlktId" filed="zlktId" visible="false"></div>
            <div name="ktName" field="ktName" width="200" headerAlign="center" align="center">
                战略课题名称
            </div>
            <div name="zyhdId" filed="zyhdId" visible="false"></div>
            <div name="moveName" field="moveName" width="120" sortField="move_name" headerAlign="center" align="center"
                 allowSort="true">
                活动名称<span style="color: #ff0000">*</span>
            </div>
            <div name="overallGoals" field="overallGoals" width="120" headerAlign="center" align="center">
                总体目标<span style="color: #ff0000">*</span>
            </div>
            <div name="initiatorId" filed="initiatorId" visible="false"></div>
            <div name="initiatorName" field="initiatorName" width="80" headerAlign="center" align="center">
                牵头人<span style="color: #ff0000">*</span>
            </div>
            <div name="respUserIds" filed="respUserIds" visible="false"></div>
            <div name="respUserNames" field="respUserNames" width="80" headerAlign="center" align="center">
                责任人<span style="color: #ff0000">*</span>
            </div>
            <div name="respDeptIds" filed="respDeptIds" visible="false"></div>
            <div name="respDeptNames" field="respDeptNames" width="80" headerAlign="center" align="center">
                责任部门<span style="color: #ff0000">*</span>
            </div>
            <div header="前1年" name="pre1" headerAlign="center">
                <div property="columns">
                    <div name="preHdnfId" field="preHdnfId" visible="false"></div>
                    <div name="preYear" field="preYear" visible="false"></div>
                    <div name="preJhTarget" field="preJhTarget" width="150" headerAlign="center" align="center">
                        计划目标
                        <input property="editor" class="mini-textarea" minWidth="200" minHeight="50"/>
                    </div>
                    <div name="preNzXsjdl" field="preNzXsjdl" vtype="required;int" width="80" headerAlign="center"
                         align="center">
                        年中进度%
                        <input property="editor" class="mini-textbox" style="width:100%;"/>
                    </div>
                    <div name="preNzWczp" field="preNzWczp" width="120" headerAlign="center" align="center">
                        年中自评
                        <%--<input property="editor" class="mini-textbox"/>--%>
                        <input property="editor" class="mini-textarea" minHeight="50"/>
                    </div>
                    <div name="preNdXsjdl" field="preNdXsjdl" width="80" headerAlign="center" align="center">
                        年底进度%
                        <input property="editor" class="mini-textbox" style="width:100%;"/>
                    </div>
                    <div name="preNdWczp" field="preNdWczp" width="120" headerAlign="center" align="center">
                        年底自评
                        <%--<input property="editor" class="mini-textbox"/>--%>
                        <input property="editor" class="mini-textarea" minHeight="50"/>
                    </div>
                </div>
            </div>
            <div header="当前年" name="now" headerAlign="center" align="center">
                <div property="columns">
                    <div name="currentHdnfId" field="currentHdnfId" visible="false"></div>
                    <div name="currentYear" field="currentYear" visible="false"></div>
                    <div name="currentJhTarget" field="currentJhTarget" width="150" headerAlign="center" align="center">
                        计划目标
                        <input property="editor" class="mini-textarea" minWidth="200" minHeight="50"/>
                    </div>
                    <div name="currentNzXsjdl" field="currentNzXsjdl" width="80" headerAlign="center" align="center">
                        年中进度%
                        <input property="editor" class="mini-textbox" style="width:100%;"/>
                    </div>
                    <div name="currentNzWczp" field="currentNzWczp" width="120" headerAlign="center" align="center">
                        年中自评
                        <%--<input property="editor" class="mini-textbox"/>--%>
                        <input property="editor" class="mini-textarea" minHeight="50"/>
                    </div>
                    <div name="currentNdXsjdl" field="currentNdXsjdl" width="80" headerAlign="center" align="center">
                        年底进度%
                        <input property="editor" class="mini-textbox" style="width:100%;"/>
                    </div>
                    <div name="currentNdWczp" field="currentNdWczp" width="120" headerAlign="center" align="center">
                        年底自评
                        <%--<input property="editor" class="mini-textbox"/>--%>
                        <input property="editor" class="mini-textarea" minHeight="50"/>
                    </div>
                </div>
            </div>
            <div header="后1年" name="after1" headerAlign="center">
                <div property="columns">
                    <div name="aftOneHdnfId" field="aftOneHdnfId" visible="false"></div>
                    <div name="aftOneYear" field="aftOneYear" visible="false"></div>
                    <div name="aftOneJhTarget" field="aftOneJhTarget" width="150" headerAlign="center" align="center">
                        计划目标
                        <%--<input property="editor" class="mini-textbox"/>--%>
                        <input property="editor" class="mini-textarea" minWidth="200" minHeight="50"/>
                    </div>
                    <div name="aftOneNzXsjdl" field="aftOneNzXsjdl" width="80" headerAlign="center" align="center">
                        年中进度%
                        <input property="editor" class="mini-textbox" style="width:100%;"/>
                    </div>
                    <div name="aftOneNzWczp" field="aftOneNzWczp" width="120" headerAlign="center" align="center">
                        年中自评
                        <%--<input property="editor" class="mini-textbox"/>--%>
                        <input property="editor" class="mini-textarea" minHeight="50"/>
                    </div>
                    <div name="aftOneNdXsjdl" field="aftOneNdXsjdl" width="80" headerAlign="center" align="center">
                        年底进度%
                        <input property="editor" class="mini-textbox" style="width:100%;"/>
                    </div>
                    <div name="aftOneNdWczp" field="aftOneNdWczp" width="120" headerAlign="center" align="center">
                        年底自评
                        <%--<input property="editor" class="mini-textbox"/>--%>
                        <input property="editor" class="mini-textarea" minHeight="50"/>
                    </div>
                </div>
            </div>
            <div header="后2年" name="after2" headerAlign="center">
                <div property="columns">
                    <div name="aftTwoHdnfId" field="aftTwoHdnfId" visible="false"></div>
                    <div name="aftTwoYear" field="aftTwoYear" visible="false"></div>
                    <div name="aftTwoJhTarget" field="aftTwoJhTarget" width="150" headerAlign="center" align="center">
                        计划目标
                        <%--<input property="editor" class="mini-textbox"/>--%>
                        <input property="editor" class="mini-textarea" minWidth="200" minHeight="50"/>
                    </div>
                    <div name="aftTwoNzXsjdl" field="aftTwoNzXsjdl" width="80" headerAlign="center" align="center">
                        年中进度%
                        <input property="editor" class="mini-textbox" style="width:100%;"/>
                    </div>
                    <div name="aftTwoNzWczp" field="aftTwoNzWczp" width="120" headerAlign="center" align="center">
                        年中自评
                        <%--<input property="editor" class="mini-textbox"/>--%>
                        <input property="editor" class="mini-textarea" minHeight="50"/>
                    </div>
                    <div name="aftTwoNdXsjdl" field="aftTwoNdXsjdl" width="80" headerAlign="center" align="center">
                        年底进度%
                        <input property="editor" class="mini-textbox" style="width:100%;"/>
                    </div>
                    <div name="aftTwoNdWczp" field="aftTwoNdWczp" width="120" headerAlign="center" align="center">
                        年底自评
                        <%--<input property="editor" class="mini-textbox"/>--%>
                        <input property="editor" class="mini-textarea" minHeight="50"/>
                    </div>
                </div>
            </div>
            <%--<div field="creator" width="60" headerAlign='center' align="center">创建人</div>--%>
            <%--<div field="createTime" width="120" dateFormat="yyyy-MM-dd HH:mm:ss" headerAlign='center' align="center"--%>
            <%--sortField="zh.CREATE_TIME_"--%>
            <%--allowSort="true">--%>
            <%--创建时间--%>
            <%--</div>--%>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var isManager = whetherIsProjectManager(${currentUserRoles});
    var url = "${ctxPath}/strategicPlanning/core/zlghPlanning/zlgh/list.do";
    var currentUserName = "${currentUserName}";
    var currentTime = "${currentTime}";
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";
    var currentUserNo = "${currentUserNo}";
    var currentUserId = "${currentUserId}";
    var isZLGHZY = ${isZLGHZY};
    var listGrid = mini.get("listGrid");
    var key = mini.get("#key");
    qxkz();
    loadTableHeader();

    // 冻结 startColumnIndex至endColumnIndex中包含隐藏的列表
    listGrid.frozenColumns(0, 11);

    // 自评选择
    var mySelfEvaluates = [{text: "完成"}, {text: "基本完成"}, {text: "滞后"}, {text: "暂停"}];

    function onMySelfEvaluate(e) {
        for (var i = 0; i < mySelfEvaluates.length; i++) {
            var mySelfEvaluate = mySelfEvaluates[i];
            if (mySelfEvaluate.text == e.value) return mySelfEvaluate.text;
        }
        return "";
    }

    // 更新表头
    function loadTableHeader() {
        var nowYear = new Date().getFullYear();
        var pre1Col = listGrid.getColumn("pre1");
        listGrid.updateColumn(pre1Col, {
            header: (nowYear - 1) + '年'
        });
        var nowCol = listGrid.getColumn("now");
        listGrid.updateColumn(nowCol, {
            header: (nowYear) + '年'
        });
        var af1Col = listGrid.getColumn("after1");
        listGrid.updateColumn(af1Col, {
            header: (nowYear + 1) + '年'
        });
        var af2Col = listGrid.getColumn("after2");
        listGrid.updateColumn(af2Col, {
            header: (nowYear + 2) + '年'
        });
    }

    // 自定义验证
    function onCellValidation(e) {
        switch (e.field) {
            case "preNzXsjdl":
                verifyValue(e);
                break;
            case "preNdXsjdl":
                verifyValue(e);
                break;
            case "currentNzXsjdl":
                verifyValue(e);
                break;
            case "currentNdXsjdl":
                verifyValue(e);
                break;
            case "aftOneNzXsjdl":
                verifyValue(e);
                break;
            case "aftOneNdXsjdl":
                verifyValue(e);
                break;
            case "aftTwoNzXsjdl":
                verifyValue(e);
                break;
            case "aftTwoNdXsjdl":
                verifyValue(e);
                break;
            default:
                break;
        }
    }

    // 单元格数字验证
    function verifyValue(e) {
        let value = e.value;
        let isNotEmpty = !(value === undefined || value === null || value === "");
        let isRange = value >= 0 && value <= 100;
        if (isNotEmpty && !isRange ) {
            e.isValid = false;
            e.errorText = "必须输入正整数";
        } else {
            e.isValid = true
        }
    }

    // 单元格绘制事件
    listGrid.on("drawcell", function (e) {
        let field = e.field, value = e.value;
        if (!(value === undefined || value === null || value === "")) {
            switch (field) {
                case "preNzXsjdl":
                case "currentNzXsjdl":
                case "aftOneNzXsjdl":
                case "aftTwoNzXsjdl":
                    if (value < 50) {
                        e.cellCls = "cellColor"
                    }
                    break;
                case "preNdXsjdl":
                case "currentNdXsjdl":
                case "aftOneNdXsjdl":
                case "aftTwoNdXsjdl":
                    if (value < 100) {
                        e.cellCls = "cellColor"
                    }
                    break;
                default:
                    break;
            }
        }
    });

    // 单元格编辑事件
    listGrid.on("cellbeginedit", function (e) {
        let record = e.record;
        // if (currentUserId === record.initiatorId || isZLGHZY) {
        if (currentUserId === record.initiatorId || isZLGHZY) {
            // 当前登录人是牵头人 或是战略规划专员 ===> 允许
            e.cancel = false;
        } else {
            mini.showTips({
                content: "<span style=\'font-size: 16px\'>只允许牵头人和战略规划专员编辑</span>",
                state: 'warning',
                x: 'right',
                y: 'top',
                timeout: 1500
            });
            // 不允许
            e.cancel = true;
        }
    });

    // 回车搜索
    function onHanlerEnter() {
        searchByName();
    }

    // 搜索
    function searchByName() {
        var queryParam = [];
        //其他筛选条件
        var cxName = $.trim(key.getValue());
        if (cxName) {
            queryParam.push({
                name: "moveName",
                value: cxName
            });
        }
        var data = {};
        data.filter = JSON.stringify(queryParam);
        data.pageIndex = listGrid.getPageIndex();
        data.pageSize = listGrid.getPageSize();
        data.sortField = listGrid.getSortField();
        data.sortOrder = listGrid.getSortOrder();
        listGrid.load(data);
    }

    // 刷新
    function refreshAchievementType() {
        // searchByName();
        listGrid.load();
    }

    // 权限控制
    function qxkz() {
        if (!isZLGHZY) {
            $("p").hide();
            mini.get("saveKpi").hide();
            return;
        }

    }

    // 刷新提示
    listGrid.on("beforeload", function (e) {
        if (listGrid.getChanges().length > 0) {
            if (confirm("有增删改的数据未保存，是否取消本次操作？")) {
                e.cancel = true;
            }
        }
    });

    // 保存数据
    function saveAchievementType() {
        // 验证表格
        listGrid.validate();
        // 验证是否通过
        if (listGrid.isValid() === false) {
            showTips();
            var error = listGrid.getCellErrors()[0];
            listGrid.beginEditCell(error.record, error.column);
            return;
        }
        // 获取变化数据
        var data = listGrid.getChanges();
        var nowYear = new Date().getFullYear();
        var message = "数据保存成功";
        var needReload = true;
        if (data.length > 0) {
            for (var i = 0; i < data.length; i++) {
                // if (data[i]._state == 'removed') {
                //     continue;
                // }
                // if (!data[i].ktName) {
                //     message = "请填写必填项！";
                //     needReload = false;
                //     break;
                // }
                // 补齐年份
                data[i].preYear = nowYear - 1;
                data[i].currentYear = nowYear;
                data[i].aftOneYear = nowYear + 1;
                data[i].aftTwoYear = nowYear + 2;
            }
            if (needReload) {
                var json = mini.encode(data);
                $.ajax({
                    url: jsUseCtxPath + "/strategicPlanning/core/zlghPlanning/zlgh/batchOptions.do",
                    data: json,
                    type: "post",
                    contentType: 'application/json',
                    async: false,
                    success: function (text) {
                        if (text && text.message) {
                            message = text.message;
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
                    listGrid.reload();
                }
            }
        });
    }

    // 合并单元格
    listGrid.on("load", function () {
        listGrid.mergeColumns(["zljcName", "ktName"])
    });

    // 展示提示
    function showTips() {
        mini.showMessageBox({
            showModal: false,
            width: 250,
            title: "提示",
            iconCls: "mini-messagebox-warning",
            message: "只能输入正整数",
            timeout: 2000,
            x: "right",
            y: "bottom"
        });
    }
</script>
<redxun:gridScript gridId="listGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
