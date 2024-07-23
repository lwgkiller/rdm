<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>仿真类别列表</title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<div class="mini-toolbar" >
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul >
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">仿真对象：</span>
                    <input class="mini-textbox" id="fzdx" name="fzdx" />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">类型：</span>
                    <input id="type" name="type" class="mini-combobox" style="width:120px;"
                           textField="value" valueField="key" emptyText="请选择..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                           data="[{'key': 'zj', 'value': '整机' },{'key': 'xt', 'value': '系统'},{'key': 'bj', 'value': '部件'},{'key': 'lj', 'value': '零件'}]"
                    />
                </li>
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
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px"  plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px"  plain="true" onclick="clearForm()">清空查询</a>
                </li>
                <span class="text" style="color:red">（注：能力等级大于等于2级的才允许选择）</span>
            </ul>
        </form>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="fzlbGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onlyCheckSelection="false"
         url="${ctxPath}/fzsj/core/fzdx/fzdxListQuery.do" idField="id"
         multiSelect="false" showColumnsMenu="true" sizeList="[20,50,100]" pageSize="20" allowAlternating="true" pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="25"></div>
            <div type="indexcolumn" width="30" headerAlign="center" align="center">序号</div>
            <div field="fzdx" headerAlign="center" align="center" allowSort="false" width="70">仿真对象</div>
            <div field="type" headerAlign="center" align="center" allowSort="false" width="60" renderer="typeRenderer">类型</div>
            <div field="field" headerAlign="center" align="center" allowSort="false" width="70" renderer="fieldRenderer">领域</div>
            <div field="fzfxx" headerAlign="center" align="center" allowSort="false" width="80">仿真分析项</div>
            <div field="demandData" headerAlign="center" align="center" allowSort="false">需求资料</div>
            <div field="currentAblityLevel" headerAlign="center" align="center" allowSort="false" width="80">目前能力等级</div>
            <div field="completionTime" dateFormat="yyyy-MM-dd" headerAlign="center" align="center" allowSort="false" width="80">预计能力建成时间</div>
            <div field="principal"  headerAlign="center" align="center" width="60">负责人</div>
            <div field="creator" headerAlign="center" align="center" allowSort="false" width="60">创建者</div>
            <div field="CREATE_TIME_" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" headerAlign="center" allowSort="true" width="120">创建时间</div>
        </div>
    </div>
</div>
<div class="mini-toolbar" style="text-align: center;padding-top: 8px;padding-bottom: 8px;">
    <a class="mini-button" onclick="onFzlbOk()">确定</a>
    <a class="mini-button btn-red" onclick="onFzlbCancel()">取消</a>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var fzlbGrid=mini.get("fzlbGrid");
    //操作栏
    fzlbGrid.on("update",function(e){
        handGridButtons(e.sender.el);
    });
    var fzlyList = getDics("FZLY");
    mini.get("field").setData(fzlyList);
    $(function () {
        searchFrm();
    });

    function typeRenderer(e) {
        var record = e.record;
        var arr = [
            {'key': 'zj', 'value': '整机' },
            {'key': 'xt', 'value': '系统'},
            {'key': 'bj', 'value': '部件'},
            {'key': 'lj', 'value': '零件'}
        ];
        return $.formatItemValue(arr,record.type);
    }

    function fieldRenderer(e) {
        for(var i=0;i<fzlyList.length;i++) {
            if (e.value == fzlyList[i].key_) {
                return fzlyList[i].text;
            }
        }
        return '';
    }

    function onFzlbOk(){
        var row = getFzlbList();
        if(!row){
            alert("最少选择一条记录!")
            return;
        }
        if(!row.currentAblityLevel || row.currentAblityLevel<2) {
            mini.alert("能力等级大于等于2级的才允许选择!")
            return;
        }
        CloseWindow('ok');
    }

    function onFzlbCancel(){
        CloseWindow('cancel');
    }

    function getFzlbList() {
        var row = fzlbGrid.getSelected();
        return row;
    }
</script>
<redxun:gridScript gridId="fzlbGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl="" />
</body>
</html>

