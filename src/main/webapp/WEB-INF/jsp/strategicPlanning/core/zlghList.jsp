<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>关键绩效指标</title>
    <%@include file="/commons/list.jsp" %>
    <%--<script src="${ctxPath}/scripts/portrait/index/portraitIndexList.js?version=${static_res_version}" type="text/javascript"></script>--%>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">指标名称: </span>
                    <input id="key" name="KPI_name" class="mini-textbox" style="width: 150px" />
                    <input id="test" class="mini-textbox" style="width: 150px;display: none"/>
                    <%--<input id="loadTableHeader" class="mini-textbox" style="width: 150px;display: none" name="loadTableHeader" />--%>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" id="KPI_name"
                       onclick="searchKPI()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <a id="saveKpi" class="mini-button" iconCls="icon-save" onclick="saveData()" plain="true">保存</a>
                    <p id="tishi" style="display: inline-block;color: #888;font-size:15px;vertical-align: middle">（
                        <image src="${ctxPath}/styles/images/warn.png"
                               style="margin-right:5px;vertical-align: middle;height: 25px"/>
                        编辑后都需要进行保存操作）
                    </p>
                </li>
            </ul>
        </form>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="indexListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         idField="id" <%--sortField="UPDATE_TIME_" sortOrder="desc"--%>
         url="${ctxPath}/strategicplanning/core/kpilist/indexList.do"
         allowCellEdit="true" allowCellSelect="true" multiSelect="true"
         editNextOnEnterKey="true" editNextRowCell="true"
         multiSelect="true" showColumnsMenu="false" sizeList="[10,20,50,100,200]" pageSize="15" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div name="kpiName" field="kpiName" width="200" headerAlign="center">指标名称</div>
            <div header="前1年" name="pre1" headerAlign="center">
                <div property="columns">
                    <div name="preId" field="preId" visible="false" name="id"></div>
                    <div name="preFolk" field="preFolk" visible="false" name="id"></div>
                    <div name="preTarget" field="preTarget" width="100" headerAlign="center" allowSort="true">目标值
                        <input property="editor" class="mini-textbox"/>
                    </div>
                    <div name="preReality" field="preReality" width="100" headerAlign="center">实际值
                        <input property="editor" class="mini-textbox"/></div>
                </div>
            </div>
            <div header="当前年" name="now" headerAlign="center">
                <div property="columns">
                    <div name="currentId" field="currentId" visible="false" name="id"></div>
                    <div name="currentFolk" field="currentFolk" visible="false" name="id"></div>
                    <div name="currentTarget" field="currentTarget" width="100" headerAlign="center">目标值
                        <input property="editor" class="mini-textbox"/></div>
                    <div name="currentReality" field="currentReality" width="100" headerAlign="center">实际值
                        <input property="editor" class="mini-textbox"/></div>
                </div>
            </div>
            <div header="后1年" name="after1" headerAlign="center">
                <div property="columns">
                    <div name="afterOneId" field="afterOneId" visible="false" name="id"></div>
                    <div name="afterOneFolk" field="afterOneFolk" visible="false" name="id"></div>
                    <div name="afterOneTarget" field="afterOneTarget" width="100" headerAlign="center">目标值
                        <input property="editor" class="mini-textbox"/></div>
                    <div name="afterOneReality" field="afterOneReality" width="100" headerAlign="center">实际值
                        <input property="editor" class="mini-textbox"/></div>
                </div>
            </div>
            <div header="后2年" name="after2" headerAlign="center">
                <div property="columns">
                    <div name="afterTwoId" field="afterTwoId" visible="false" name="id"></div>
                    <div name="afterTwoFolk" field="afterTwoFolk" visible="false" name="id"></div>
                    <div name="afterTwoTarget" field="afterTwoTarget" width="100" headerAlign="center">目标值
                        <input property="editor" class="mini-textbox"/></div>
                    <div name="afterTwoReality" field="afterTwoReality" width="100" headerAlign="center">实际值
                        <input property="editor" class="mini-textbox"/></div>
                </div>
            </div>
            <div header="后3年" name="after3" headerAlign="center">
                <div property="columns">
                    <div name="afterThreeId" field="afterThreeId" visible="false" name="id"></div>
                    <div name="afterThreeFolk" field="afterThreeFolk" visible="false" name="id"></div>
                    <div name="afterThreeTarget" field="afterThreeTarget" width="100" headerAlign="center">目标值
                        <input property="editor" class="mini-textbox"/></div>
                    <div name="afterThreeReality" field="afterThreeReality" width="100" headerAlign="center">实际值
                        <input property="editor" class="mini-textbox"/></div>
                </div>
            </div>
            <div header="后4年" name="after4" headerAlign="center">
                <div property="columns">
                    <div name="afterFourId" field="afterFourId" visible="false" name="id"></div>
                    <div name="afterFourFolk" field="afterFourFolk" visible="false" name="id"></div>
                    <div name="afterFourTarget" field="afterFourTarget" width="100" headerAlign="center">目标值
                        <input property="editor" class="mini-textbox"/></div>
                    <div name="afterFourReality" field="afterFourReality" width="100" headerAlign="center">实际值
                        <input property="editor" class="mini-textbox"/></div>
                </div>
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var url = "${ctxPath}/strategicplanning/core/kpilist/indexList.do";
    var currentUserName = "${currentUserName}";
    var currentTime = "${currentTime}";
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";
    var currentUserNo = "${currentUserNo}";
    var currentUserId = "${currentUserId}";
    var isZLGHZY =${isZLGHZY};
    var fileListGrid = mini.get("indexListGrid");
    var key = mini.get("#key");
    loadTableHeader()
    searchKPI();
    qxkz();

    function qxkz() {
        if (!isZLGHZY) {
            $("p").hide();
            mini.get("saveKpi").hide();
            return;
        }

    }

    fileListGrid.on("cellbeginedit", function (e) {
        if (!isZLGHZY) {
            if(e.editor) {
                e.editor.setEnabled(false);
                return;
            }
        }
    });

    function loadTableHeader() {
        var nowYear = new Date().getFullYear();
        var pre1Col = fileListGrid.getColumn("pre1")
        var colum = pre1Col.columns;
        fileListGrid.updateColumn(pre1Col, {header: (nowYear - 1) + '年'});
        var nowCol = fileListGrid.getColumn("now")
        fileListGrid.updateColumn(nowCol, {header: (nowYear) + '年'});
        var af1Col = fileListGrid.getColumn("after1")
        fileListGrid.updateColumn(af1Col, {header: (nowYear + 1) + '年'});
        var af2Col = fileListGrid.getColumn("after2")
        fileListGrid.updateColumn(af2Col, {header: (nowYear + 2) + '年'});
        var af3Col = fileListGrid.getColumn("after3")
        fileListGrid.updateColumn(af3Col, {header: (nowYear + 3) + '年'});
        var af4Col = fileListGrid.getColumn("after4")
        fileListGrid.updateColumn(af4Col, {header: (nowYear + 4) + '年'});
        fileListGrid.load();
    }

    //保存
    function saveData() {
        let allData = fileListGrid.getChanges();
        var message = "数据保存成功";
        var needReload = true;
        if (allData.length > 0) {
            if (needReload) {
                var json = mini.encode(allData);
                $.ajax({
                    url: jsUseCtxPath + "/strategicplanning/core/kpilist/saveKpi.do",
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
                    fileListGrid.reload();
                }
            }
        });
    }

    function clearForm() {
        key.setValue("");
        /* fileListGrid.setUrl(jsUseCtxPath + "/strategicplanning/core/kpilist/saveKpi.do");*/
        fileListGrid.load();
    }

    function searchKPI() {
        var queryParam = [];
        //其他筛选条件
        var cxName = $.trim(key.getValue());
        if (cxName) {
            queryParam.push({name: "KPI_name", value: cxName});
        }
        var data = {};
        data.filter = JSON.stringify(queryParam);
        data.pageIndex = fileListGrid.getPageIndex();
        data.pageSize = fileListGrid.getPageSize();
        data.sortField = fileListGrid.getSortField();
        data.sortOrder = fileListGrid.getSortOrder();
        //查询
        fileListGrid.load(data);
    }

    // 锁定第一列
    fileListGrid.frozenColumns(0, 0);


</script>
<redxun:gridScript gridId="indexListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
