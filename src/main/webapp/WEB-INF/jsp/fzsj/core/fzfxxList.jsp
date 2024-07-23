<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>仿真分析项</title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<div class="mini-toolbar" >
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul >
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">领域：</span>
                    <input id="field" name="field" class="mini-combobox" style="width:120px;"
                           textField="text" valueField="key_" emptyText="请选择..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."data="fzlyList"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">仿真分析项：</span>
                    <input class="mini-textbox" id="fzfxx" name="fzfxx" />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">负责人：</span>
                    <input class="mini-textbox" id="principal" name="principal" />
                </li>
                <li style="display: none">
                    <input class="mini-textbox" id="fzdxId" name="fzdxId" value="${fzdxId}"/>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px"  plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px"  plain="true" onclick="clearQuery()">清空查询</a>
                    <div id="separator" style="display: inline-block" class="separator"></div>
                    <a id="addFzfxx" class="mini-button" style="margin-right: 5px" plain="true" onclick="addFzfxx()">新增</a>
                    <a id="saveFzfxx" class="mini-button" style="margin-right: 5px" plain="true" onclick="saveFzfxx()">保存</a>
                    <a id="delFzfxx" class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="delFzfxx()">删除</a>
                </li>
            </ul>
        </form>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="fzfxxGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" allowCellSelect="true"
         url="${ctxPath}/fzsj/core/fzdx/fzdxListQuery.do?" idField="id" allowCellEdit="false" allowCellValid="true"
         multiSelect="true" showColumnsMenu="true" sizeList="[20,50,100]" pageSize="20" allowAlternating="true" pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="25"></div>
            <div type="indexcolumn" width="25" headerAlign="center" align="center">序号</div>
            <div field="sdmAnalysisType" align="center"  width="1"  headerAlign="left" visible="false">id</div>
            <div field="field" vtype="required" headerAlign="center" align="center" width="30" renderer="fieldRenderer">
                领域<input property="editor" class="mini-combobox" style="width: 100%" textField="text" valueField="key_" data="fzlyList"/>
            </div>
            <div field="fzfxx" vtype="required" headerAlign="center" align="center" allowSort="false" width="40">
                仿真分析项<input property="editor" class="mini-textbox" style="width: 100%"/>
            </div>
            <div field="sdmAnalysisId"   displayfield="sdmAnalysisName"  vtype="required" headerAlign="center" align="center" allowSort="false" width="120">
                SDM仿真项
                <input
                        property="editor"
                        id="applicationType"
                        class="mini-treeselect"
                        url="${ctxPath}/fzsj/core/sdm/listSdmAnalysis.do"
                        multiSelect="false"
                        textField="name"
                        valueField="id"
                        parentField="parentId"
                        required="true"
                        value="" onnodeclick="setTreeKey(e)"
                        showFolderCheckBox="false"
                        expandOnLoad="true"
                        popupWidth="100%"
                        style="width:100%"
                />
            </div>
            <div field="demandData" vtype="required" headerAlign="center" width="150" align="center" allowSort="false" renderer="demandDatarenderer">
                需求资料
                <input property="editor" class="mini-textarea" style="width: 100%" minHeight="80"/>
            </div>
            <div field="currentAblityLevel" vtype="required" headerAlign="center" align="center" allowSort="false" width="35">
                目前能力等级
                <input property="editor" class="mini-textbox" style="width: 100%"/>
            </div>
            <div field="completionTime" vtype="required" dateFormat="yyyy-MM-dd" headerAlign="center" align="center" allowSort="false" width="40">
                预计能力建成时间
                <input property="editor" class="mini-datepicker" allowinput="false" style="width: 100%"/>
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
    var fzfxxGrid=mini.get("fzfxxGrid");
    var jsUseCtxPath="${ctxPath}";
    var fzdxId = "${fzdxId}";
    var fzlyList = getDics("FZLY");
    mini.get("field").setData(fzlyList);
    var fzsjgly = "${fzsjgly}";
    //操作栏
    fzfxxGrid.on("update",function(e){
        handGridButtons(e.sender.el);
    });
    $(function () {
        searchFrm();
        if (fzsjgly == 'true') {
            fzfxxGrid.setAllowCellEdit(true);
        } else {
            $("#separator").hide();
            mini.get("addFzfxx").hide();
            mini.get("saveFzfxx").hide();
            mini.get("delFzfxx").hide();
        }
    });

    function fieldRenderer(e) {
        for(var i=0;i<fzlyList.length;i++) {
            if (e.value == fzlyList[i].key_) {
                return fzlyList[i].text;
            }
        }
        return '';
    }

    function demandDatarenderer(e) {
        var html = "<div style='line-height: 20px;height:100px;overflow: auto;text-align: left;vertical-align: middle'>";
        html += '<span style="white-space:pre-wrap" >' + e.value + '</span>';
        html += '</div>'
        return html;
    }

    function clearQuery() {
        mini.get("field").setValue('');
        mini.get("fzfxx").setValue('');
        mini.get("principal").setValue('');
        searchFrm();
    }
    function addFzfxx() {
        var newRow = {
            id: '',
            fzdxId: fzdxId,
            field: '',
            fzfxx: '',
            demandData: '',
            currentAblityLevel: '',
            completionTime: '',
            principalId: '',
            principal: ''
        }
        fzfxxGrid.addRow(newRow,0)
        fzfxxGrid.validateRow(newRow);
    }
    function saveFzfxx() {
        fzfxxGrid.validate();
        if (fzfxxGrid.isValid() == false) {
            var error = fzfxxGrid.getCellErrors()[0];
            fzfxxGrid.beginEditCell(error.record,error.column);
            return;
        }
        var changes = fzfxxGrid.getChanges();
        for (var i=0; i<changes.length;i++)  {
            changes[i].principal = changes[i].principalId_name;
        }
        var json = mini.encode(changes);
        $.ajax({
            url: jsUseCtxPath + "/fzsj/core/fzdx/saveFzfxx.do",
            type: 'post',
            async: false,
            data:json,
            contentType: 'application/json',
            success: function (res) {
                if (res) {
                    if (res) {
                        mini.alert("保存成功","提示",function () {
                            searchFrm();
                        })

                    }
                }
            }
        })
    }
    function delFzfxx() {
        var rows = fzfxxGrid.getSelecteds();
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
                    url: jsUseCtxPath + "/fzsj/core/fzdx/deleteFzfxx.do",
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

    function setTreeKey(e){
        var tree = mini.get("applicationType");
        var node=e.node;
        var parentnode= tree.tree.getAncestors(node)
        var text = '';
        for(var i=0;i<parentnode.length;i++){
            text += parentnode[i].name+'-';
        }
        text += node.name;
        e.sender.setValue(node.id);
        e.sender.setText(text);
        fzfxxGrid.updateRow(fzfxxGrid.getSelected(), {sdmAnalysisType: node.type});
    }
</script>
<redxun:gridScript gridId="fzfxxGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl="" />
</body>
</html>

