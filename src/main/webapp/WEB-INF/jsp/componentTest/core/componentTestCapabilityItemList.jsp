<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>测试项次</title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">测试项次：</span>
                    <input class="mini-textbox" id="capabilityItem" name="capabilityItem"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">资源：</span>
                    <input class="mini-textbox" id="resources" name="resources"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">负责人：</span>
                    <input class="mini-textbox" id="principal" name="principal"/>
                </li>
                <li style="display: none">
                    <input class="mini-textbox" id="capabilityId" name="capabilityId" value="${componentTestCapabilityId}"/>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearQuery()">清空查询</a>
                    <div id="separator" style="display: inline-block" class="separator"></div>
                    <a id="addComponentTestCapabilityItem" class="mini-button" style="margin-right: 5px" plain="true"
                       onclick="addComponentTestCapabilityItem()">新增</a>
                    <a id="saveComponentTestCapabilityItem" class="mini-button" style="margin-right: 5px" plain="true"
                       onclick="saveComponentTestCapabilityItem()">保存</a>
                    <a id="delComponentTestCapabilityItem" class="mini-button btn-red" style="margin-right: 5px" plain="true"
                       onclick="delComponentTestCapabilityItem()">删除</a>
                </li>
            </ul>
        </form>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="componentTestCapabilityItemGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" allowCellSelect="true"
         url="${ctxPath}/componentTest/core/capability/componentTestCapabilityItemListQuery.do?" idField="id" allowCellEdit="false"
         allowCellValid="true" multiSelect="true" showColumnsMenu="true" sizeList="[20,50,100]" pageSize="20" allowAlternating="true"
         pagerButtons="#pagerButtons" allowCellWrap="true">
        <div property="columns">
            <div type="checkcolumn" width="25"></div>
            <div field="capability2" name="capability2" headerAlign="center" align="center" allowSort="false" width="40" renderer="render">专业</div>
            <div field="capability" name="capability" headerAlign="center" align="center" allowSort="false" width="40" renderer="render">测试项目</div>
            <div field="orderNum" vtype="required" headerAlign="center" align="center" allowSort="false" width="40">测试项次编号
                <input property="editor" class="mini-spinner" increment="1" maxValue="1000"/>
            </div>
            <div field="capabilityItem" vtype="required" headerAlign="center" align="center" allowSort="false" width="40" renderer="render">
                测试项次<input property="editor" class="mini-textbox" style="width: 100%"/>
            </div>
            <div field="keyPoint" name="keyPoint" vtype="required" headerAlign="center" width="200" align="center" allowSort="false"
                 renderer="render">
                关注要点<input property="editor" class="mini-textarea" style="width: 100%" minHeight="80"/>
            </div>
            <div field="resources" vtype="required" headerAlign="center" align="center" allowSort="false" width="40">
                资源<input property="editor" class="mini-textbox" style="width: 100%"/>
            </div>
            <div field="principalId" vtype="required" headerAlign="center" displayField="principalId_name" align="center" width="30">
                负责人
                <%--lwgkiller：此处在行内用mini-user组件，textname无效，默认会以name+"_name"作为textname--%>
                <input property="editor" class="mini-user rxc" plugins="mini-user" allowinput="false"
                       mainfield="no" name="principalId"/>
            </div>
            <%--<div field="creator" headerAlign="center" align="center" allowSort="false" width="60">创建者</div>--%>
            <%--<div field="CREATE_TIME_" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" headerAlign="center" allowSort="true" width="120">创建时间</div>--%>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var componentTestCapabilityItemGrid = mini.get("componentTestCapabilityItemGrid");
    var jsUseCtxPath = "${ctxPath}";
    var componentTestCapabilityId = "${componentTestCapabilityId}";
    var isComponentTestAdmin = "${isComponentTestAdmin}";
    componentTestCapabilityItemGrid.on("load", function () {
        componentTestCapabilityItemGrid.mergeColumns(["capability", "capability2"]);
    });
    //..
    $(function () {
        searchFrm();
        if (isComponentTestAdmin == 'true') {
            componentTestCapabilityItemGrid.setAllowCellEdit(true);
        } else {
            $("#separator").hide();
            mini.get("addComponentTestCapabilityItem").hide();
            mini.get("saveComponentTestCapabilityItem").hide();
            mini.get("delComponentTestCapabilityItem").hide();
        }
    });
    //..
    function keyPointrenderer(e) {
        var html = "<div style='line-height: 20px;height:100px;overflow: auto;text-align: left;vertical-align: middle'>";
        html += '<span style="white-space:pre-wrap" >' + e.value + '</span>';
        html += '</div>'
        return html;
    }
    function render(e) {
        if (e.value != null && e.value != "") {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            return html;
        }
    }
    //..
    function clearQuery() {
        mini.get("resources").setValue('');
        mini.get("capabilityItem").setValue('');
        mini.get("principal").setValue('');
        searchFrm();
    }
    //..
    function addComponentTestCapabilityItem() {
        componentTestCapabilityItemGrid.mergeColumns([]);
        var newRow = {
            id: '',
            capabilityId: componentTestCapabilityId,
            resources: '',
            capabilityItem: '',
            keyPoint: '',
            principalId: '',
            principal: ''
        }
        componentTestCapabilityItemGrid.addRow(newRow, 0)
        componentTestCapabilityItemGrid.validateRow(newRow);
    }
    //..
    function saveComponentTestCapabilityItem() {
        componentTestCapabilityItemGrid.validate();
        if (componentTestCapabilityItemGrid.isValid() == false) {
            var error = componentTestCapabilityItemGrid.getCellErrors()[0];
            componentTestCapabilityItemGrid.beginEditCell(error.record, error.column);
            return;
        }
        var changes = componentTestCapabilityItemGrid.getChanges();
        for (var i = 0; i < changes.length; i++) {
            changes[i].principal = changes[i].principalId_name;
        }
        var json = mini.encode(changes);
        $.ajax({
            url: jsUseCtxPath + "/componentTest/core/capability/saveComponentTestCapabilityItem.do",
            type: 'post',
            async: false,
            data: json,
            contentType: 'application/json',
            success: function (res) {
                if (res) {
                    mini.alert(res.message, "提示", function () {
                        searchFrm();
                    })
                }
            }
        })
    }
    //..
    function delComponentTestCapabilityItem() {
        var rows = componentTestCapabilityItemGrid.getSelecteds();
        if (rows.length <= 0) {
            mini.alert("请至少选中一条记录");
            return;
        }
        mini.confirm("确定删除选中记录？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                var ids = [];
                for (var i = 0; i < rows.length; i++) {
                    ids.push(rows[i].id);
                }
                _SubmitJson({
                    url: jsUseCtxPath + "/componentTest/core/capability/deleteComponentTestCapabilityItem.do",
                    method: 'POST',
                    data: {ids: ids.join(',')},
                    success: function (data) {
                        if (data) {
                            searchFrm();
                        }
                    }
                });
            }
        });
    }
</script>
<redxun:gridScript gridId="componentTestCapabilityItemGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>

