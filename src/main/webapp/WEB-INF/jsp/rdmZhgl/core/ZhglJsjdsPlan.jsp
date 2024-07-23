<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>技术交底书计划</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/xcmgProjectManager/projectConfig.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/xcmgProjectManager/projectUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
    <style type="text/css">
        .custom-hidden{
            visibility: hidden;
        }
    </style>
</head>
<body>
<div class="mini-toolbar">
    <ul class="toolBtnBox">
        <li>
            <span class="text" style="width:auto">年份: </span>
            <input id="currentYear" style="width: 80px;" class="mini-spinner" minValue="2000" maxValue="3000" value=""/>
            <span class="text" style="width:auto">部门名称: </span>
            <input id="key" name="key" required="true" class="mini-dep rxc" plugins="mini-dep"
            style="width:200px;height:34px" allowinput="false" textname="respDeptNames" length="40" maxlength="40" minlen="0" single="true" required="false" initlogindep="false"/>
            <%--<input id="key" name="KPI_name" class="mini-textbox" style="width: 150px" onenter="onHanlerEnter"/>--%>
            <%--<input id="key" name="key" style="width:200px;height:34px" class="mini-combobox" required="true"--%>
                   <%--requiredErrorText="部门不能为空" style="width:98%;"--%>
                   <%--textField="name" valueField="groupId" emptyText="请选择..." allowInput="false" showNullItem="true"--%>
                   <%--nullItemText="请选择..."/>--%>
            <input id="test" class="mini-textbox" style="width: 150px;display: none"/>
            <a class="mini-button" style="margin-right: 5px" plain="true" id="searchButton"
               onclick="searchName()">查询</a>
            <a class="mini-button btn-red" plain="true" onclick="refreshAchievementType()" plain="true">清空查询</a>
            <%--<input id="loadTableHeader" class="mini-textbox" style="width: 150px;display: none" name="loadTableHeader" />--%>
        </li>
        <span class="separator"></span>
        <li>
            <a id="addKpi" class="mini-button" iconCls="icon-add" onclick="addAchievementType()">新增</a>
            <%--<a id="removeKpi" class="mini-button btn-red" plain="true" onclick="removeAchievementType()">删除</a>--%>
        </li>
        <span id="twoSeparator" class="separator"></span>
        <li>
            <a id="saveKpi" class="mini-button" iconCls="icon-save" onclick="saveAchievementType()">保存</a>
            <p style="display: inline-block;color: #f6253e;font-size:15px;vertical-align: middle">（
                <image src="${ctxPath}/styles/images/warn.png"
                       style="margin-right:5px;vertical-align: middle;height: 15px"/>
                新增、编辑后都需要进行保存操作）
            </p>
        </li>

    </ul>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="listGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" idField="id"
         url="${ctxPath}/zhgl/core/jsjds/plan/list.do"
         multiSelect="true" showColumnsMenu="false" sizeList="[10,20,50,100,200]" pageSize="15" allowAlternating="true"
         showPager="true" pagerButtons="#pagerButtons" allowCellEdit="true" allowCellSelect="true"
         editNextOnEnterKey="true" editNextRowCell="true" oncellvalidation="onCellValidation">
        <div property="columns">
            <div type="checkcolumn" width="10"></div>
            <div type="indexcolumn" align="center" headerAlign="center" width="15">序号</div>
            <div name="deptId" field="deptId" visible="false"></div>
            <div name="deptName" field="deptName" width="45" headerAlign="center" align="center">
                部门名称
            </div>
            <div name="year" field="year" width="30" headerAlign="center" align="center">
                年份</div>
            <div name="month" field="month" width="30" headerAlign="center" align="center">
                月份</div>
            <div name="total" field="total" width="30" headerAlign="center" align="center">
                交底书计划数<span style="color: #ff0000">*</span><input property="editor" class="mini-textbox"/></div>
            <div name="inventTotal" field="inventTotal" width="30" headerAlign="center" align="center">
                发明计划数<span style="color: #ff0000">*</span><input property="editor" class="mini-textbox"/></div>
            <div field="creator" width="30" headerAlign='center' align="center">创建人</div>
            <div field="createTime" width="50" dateFormat="yyyy-MM-dd HH:mm:ss" headerAlign='center' align="center">
                创建时间
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var isManager = whetherIsProjectManager(${currentUserRoles});
    var url = "${ctxPath}/zhgl/core/jsjds/plan/list.do";
    var currentUserName = "${currentUserName}";
    var currentTime = "${currentTime}";
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";
    var currentUserNo = "${currentUserNo}";
    var currentUserId = "${currentUserId}";
    var isOperator =${isOperator};
    var listGrid = mini.get("listGrid");
    var currentYear = mini.get("#currentYear");
    currentYear.setValue(new Date().getFullYear());
    // 查询关键字
    var key = mini.get("#key");
    // 部门插件
    key.setValue('161416982793248776');
    key.setText('小挖研究所');
    // 初始化数据量
    // var initData = {};
    // 设置下拉框url
    <%--key.setAjaxOptions({
        type: 'post',
        async: false,
        contentType: "application/x-www-form-urlencoded; charset=UTF-8",
        data: {
            pageIndex: 0,
            pageSize: 10,
            sortField: '',
            sortOrder: '',
            parentId: '87212403321741318',
            groupId: '87212403321741318',
        },
    });
    key.setUrl("${ctxPath}/sys/org/sysOrg/getInitData.do?config=%7B%7D");
    key.select(1);--%>
    searchName();
    qxkz();

    function onHanlerEnter() {
        searchName();
    }

    function searchName() {
        var queryParam = [];
        //其他筛选条件
        let cxName = $.trim(key.getValue());
        let year = $.trim(currentYear.getValue());
        if (!cxName) {
            mini.alert("请选择部门！");
            return;
        }
        queryParam.push({name: "deptId", value: cxName});
        if (year) {
            queryParam.push({name: "year", value: year});
        }
        var data = {};
        data.filter = JSON.stringify(queryParam);
        data.pageIndex = listGrid.getPageIndex();
        data.pageSize = listGrid.getPageSize();
        data.sortField = listGrid.getSortField();
        data.sortOrder = listGrid.getSortOrder();
        listGrid.load(data, function (e) {
            isShowEmpty(e.data);
            /*initData = listGrid.getResultObject();
            // 返回值大于等于12
            if (initData.data.length >= 12) {
                //隐藏新增按钮
                mini.get("#addKpi").hide();
                if (!$("#twoSeparator").hasClass("custom-hidden")) {
                    $("#twoSeparator").addClass("custom-hidden")
                }
            } else {
                mini.get("#addKpi").show();
                if ($("#twoSeparator").hasClass("custom-hidden")) {
                    $("#twoSeparator").removeClass("custom-hidden")
                }
            }*/
        });
    }


    function qxkz() {
        if (!isOperator) {
            $("p").hide();
            mini.get("#addKpi").hide();
            if (!$("#twoSeparator").hasClass("custom-hidden")) {
                $("#twoSeparator").addClass("custom-hidden")
            }
            mini.get("#saveKpi").hide();
            return;
        }
    }

    // 新增
    function addAchievementType() {
        let deptId = key.getValue();
        let deptName = key.getText();
        // (0,12)
        if (listGrid.data.length >0 && listGrid.data.length < 12) {
            let data = {
                deptId: deptId,
                year: currentYear.getValue(),
                deptName: deptName,
                total: 0,
                inventTotal: 0
            };
            listGrid.addRow(data, listGrid.data.length);
        } else if (listGrid.data.length === 0) {
            let newRows = [];
            for (let i = 1; i <= 12; i++) {
                let data = {
                    deptId: deptId,
                    year: currentYear.getValue(),
                    deptName: deptName,
                    total: 0,
                    inventTotal: 0
                };
                data.month = i + "月";
                newRows.push(data);
            }
            listGrid.addRows(newRows);
        } else {
            mini.showMessageBox({
                showModal: false,
                width: 250,
                title: "提示",
                iconCls: "mini-messagebox-warning",
                message: "不能再增加了",
                timeout: 2000,
                x: "center",
                y: "middle"
            });
        }
        isShowEmpty(listGrid.data)
    }


    // 清空查询
    function refreshAchievementType() {
        key.setValue('161416982793248776');
        key.setText('小挖研究所');
        currentYear.setValue(new Date().getFullYear());
        searchName()
        // listGrid.load();
    }

    // 刷新
    listGrid.on("beforeload", function (e) {
        if (listGrid.getChanges().length > 0) {
            if (confirm("有增删改的数据未保存，是否取消本次操作？")) {
                e.cancel = true;
            }
        }
    });

    // 单元格编辑事件
    listGrid.on("cellbeginedit", function (e) {
        let record = e.record;
        if (isOperator) {
            // 专利工程师或超级管理员 ===> 允许
            e.cancel = false;
        } else {
            mini.showTips({
                content: "<span style=\'font-size: 16px\'>只允许专利工程师编辑</span>",
                state: 'warning',
                x: 'right',
                y: 'top',
                timeout: 1500
            });
            // 不允许
            e.cancel = true;
        }
    });

    // 单元格数字验证
    function verifyValue(e) {
        let value = e.value;
        let isNotEmpty = !(value === undefined || value === null || value === "");
        let isRange = value >= 0;
        if (isNotEmpty && !isRange ) {
            e.isValid = false;
            e.errorText = "必须输入正整数";
        } else {
            e.isValid = true
        }
    }

    // 单元格必填验证
    function verifyRequiredValue(e, headerName) {
        let value = e.value;
        if (value === undefined || value === null || value === "") {
            e.isValid = false;
            e.errorText = headerName + "必填";
        } else {
            e.isValid = true
        }
    }

    // 自定义验证
    function onCellValidation(e) {
        switch (e.field) {
            case "total":
                verifyValue(e);
                break;
            case "month":
                verifyRequiredValue(e, "月份");
                break;
            default:
                break;
        }
    }

    // 保存
    function saveAchievementType() {
        // 验证表格
        listGrid.validate();
        // 验证是否通过
        if (listGrid.isValid() === false) {
            var error = listGrid.getCellErrors()[0];
            listGrid.beginEditCell(error.record, error.column);
            showTips(error.errorText);
            return;
        }
        // 获取变化数据
        var data = listGrid.getChanges();
        var message = "数据保存成功";
        if (data.length > 0) {
            for (var i = 0; i < data.length; i++) {
                if (data[i]._state == 'removed') {
                    continue;
                }
            }
            var json = mini.encode(data);
            $.ajax({
                url: jsUseCtxPath + "/zhgl/core/jsjds/plan/batchOptions.do",
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
        mini.showMessageBox({
            title: "提示信息",
            iconCls: "mini-messagebox-info",
            buttons: ["ok"],
            message: message,
            callback: function (action) {
                if (action == "ok") {
                    listGrid.reload();
                }
            }
        });
    }

    // 展示提示
    function showTips(message) {
        mini.showMessageBox({
            showModal: false,
            width: 250,
            title: "提示",
            iconCls: "mini-messagebox-warning",
            message: message,
            timeout: 2000,
            x: "right",
            y: "bottom"
        });
    }

    // 暂无数据
    function isShowEmpty(data){
        if (data.length === 0 && !$('.listEmpty').length) {
            $('#' + listGrid.id + ' .mini-panel-body .mini-grid-rows-view').append("<div class='listEmpty'><img src='" + __rootPath + "/styles/images/index/empty2.png'/><span>暂无数据</span></div>");
        } else if (data.length > 0 && $('.listEmpty').length) {
            $('.listEmpty').remove();
        }
    }

    // 操作列样式渲染
    listGrid.on("update", function (e) {
        handGridButtons(e.sender.el);
    });

</script>
<%--<redxun:gridScript gridId="listGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>--%>
</body>
</html>
