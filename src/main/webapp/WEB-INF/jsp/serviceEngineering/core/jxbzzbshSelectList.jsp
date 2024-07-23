<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>检修标准值表审核选择列表</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/serviceEngineering/jxbzzbshList.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar" >
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul >
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">物料编码：</span>
                    <input class="mini-textbox" id="materialCode" name="materialCode" />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">产品所：</span>
                    <input class="mini-textbox" id="productDepartment" name="productDepartment" />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">产品类型：</span>
                    <input id="productType" name="productType" class="mini-combobox" style="width:120px;"
                           textField="value" valueField="key" emptyText="请选择..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                           data="[{key:'lunWa',value:'轮挖'},{key:'lvWa',value:'履挖'},{key:'teWa',value:'特挖'},{key:'dianWa',value:'电挖'}]"
                    />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">销售型号：</span>
                    <input class="mini-textbox" id="salesModel" name="salesModel" />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">版本类型：</span>
                    <input id="versionType" name="versionType" class="mini-combobox" style="width:120px;"
                           textField="value" valueField="key" emptyText="请选择..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                           data="[{key:'cgb',value:'常规版'},{key:'csb',value:'测试版'},{key:'wzb',value:'完整版'}]"
                    />
                </li>
                <li style="margin-right: 15px;display: none;">
                    <span class="text" style="width:auto;">任务状态：</span>
                    <input id="taskStatus" name="taskStatus" class="mini-combobox" style="width:120px;"
                           textField="value" valueField="key" emptyText="请选择..." value="SUCCESS_END"
                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                           data="[{'key': 'DRAFTED', 'value': '草稿', 'css': 'orange'},{'key': 'RUNNING', 'value': '运行中', 'css': 'green'},
                                    {'key': 'SUCCESS_END', 'value': '成功结束', 'css': 'blue'},{'key': 'DISCARD_END', 'value': '作废', 'css': 'red'},
                                    {'key': 'ABORT_END', 'value': '异常中止结束', 'css': 'red'},{'key': 'PENDING', 'value': '挂起', 'css': 'gray'}]"
                    />
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px"  plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px"  plain="true" onclick="clearQuery()">清空查询</a>
                </li>
            </ul>
        </form>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="jxbzzbshSelectGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onlyCheckSelection="true"
         url="${ctxPath}/serviceEngineering/core/jxbzzbsh/jxbzzbshListQuery.do" idField="id" onload="onGridLoad"
         multiSelect="true" showColumnsMenu="true" sizeList="[20,50,100]" pageSize="20" allowAlternating="true" pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="25"></div>
            <div type="indexcolumn" width="28" headerAlign="center" align="center">序号</div>
            <div field="materialCode" headerAlign="center" align="center" allowSort="false">物料编码</div>
            <div field="productDepartment" headerAlign="center" align="center" allowSort="false">产品所</div>
            <div field="productType" headerAlign="center" align="center" allowSort="false" renderer="productTypeRenderer" width="70">产品类型</div>
            <div field="salesModel" headerAlign="center" align="center" allowSort="false">销售型号</div>
            <div field="versionType" headerAlign="center" align="center" allowSort="false" renderer="versionTypeRenderer" width="70">版本类型</div>
            <div field="versionNum" headerAlign="center" align="center" allowSort="false" width="30">版本号</div>
            <div field="note" headerAlign="center" align="center" allowSort="false" renderer="noteStatusRenderer">备注</div>
            <div field="applicationNumber" headerAlign="center" align="center" allowSort="false" width="70">申请编号</div>
            <div field="taskStatus"  headerAlign="center" align="center" renderer="taskStatusRenderer" width="70">任务状态</div>
            <div field="creator" headerAlign="center" align="center" allowSort="false" width="60">创建者</div>
            <div field="CREATE_TIME_" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" headerAlign="center" allowSort="true" width="120">创建时间</div>
        </div>
    </div>
</div>
<div class="mini-toolbar" style="text-align: center;padding-top: 8px;padding-bottom: 8px;">
    <a class="mini-button" onclick="onOk()">确定</a>
    <a class="mini-button btn-red" onclick="onCancel()">取消</a>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var jxbzzbshSelectGrid=mini.get("jxbzzbshSelectGrid");

    var selectName = [];
    var selectId = [];

    function onGridLoad(e) {
        if (selectId.length>0) {
            for (var i = 0; i < jxbzzbshSelectGrid.data.length; i++) {
                if (selectId.indexOf(jxbzzbshSelectGrid.data[i].id) != -1) {
                    jxbzzbshSelectGrid.setSelected(jxbzzbshSelectGrid.getRow(i));
                }
            }
        }
    }

    jxbzzbshSelectGrid.on("select", function (e) {
        var rec = e.record;
        var materialCode = rec.materialCode;
        var id = rec.id;
        if (selectId.indexOf(id) == -1) {
            selectId.push(id);
            selectName.push(materialCode);
        }
    });

    jxbzzbshSelectGrid.on("deselect", function (e) {
        var rec = e.record;
        var materialCode = rec.materialCode;
        var id = rec.id;
        delItem(materialCode, selectName);
        delItem(id, selectId);
    });

    function delItem(item, list) {
        // 表示先获取这个元素的下标，然后从这个下标开始计算，删除长度为1的元素
        list.splice(list.indexOf(item), 1)
    }

    //操作栏
    jxbzzbshSelectGrid.on("update",function(e){
        handGridButtons(e.sender.el);
    });
    $(function () {
        searchFrm();
    });
    function versionTypeRenderer(e) {
        var record = e.record;
        var arr = [{key:'cgb',value:'常规版'},{key:'csb',value:'测试版'},{key:'wzb',value:'完整版'}];
        return $.formatItemValue(arr,record.versionType);
    }

    function productTypeRenderer(e) {
        var record = e.record;
        var arr = [
            {key:'lunWa',value:'轮挖'},
            {key:'teWa',value:'特挖'},
            {key:'dianWa',value:'电挖'},
            {key:'lvWa',value:'履挖'}
        ];
        return $.formatItemValue(arr,record.productType);
    }

    function taskStatusRenderer(e) {
        var record = e.record;
        var taskStatus = record.taskStatus;
        var arr = [{'key': 'DRAFTED', 'value': '草稿', 'css': 'orange'},
            {'key': 'RUNNING', 'value': '运行中', 'css': 'green'},
            {'key': 'SUCCESS_END', 'value': '成功结束', 'css': 'blue'},
            {'key': 'DISCARD_END', 'value': '作废', 'css': 'red'},
            {'key': 'ABORT_END', 'value': '异常中止结束', 'css': 'red'},
            {'key': 'PENDING', 'value': '挂起', 'css': 'gray'}
        ];
        if (!taskStatus) {
            return "<span class='blue'>成功结束</span>";
        }
        return $.formatItemValue(arr,taskStatus);
    }

    function onOk(){
        var rows = getJxbzzbxfsqMaterialCode();
        if(!rows){
            alert("最少选择一条记录!")
            return;
        }
        CloseWindow('ok');
    }

    function onCancel(){
        CloseWindow('cancel');
    }

    function getJxbzzbxfsqMaterialCode() {
        var rows = jxbzzbshSelectGrid.getSelecteds();
        return rows;
    }
    function clearQuery() {
        mini.get("materialCode").setValue("");
        mini.get("productDepartment").setValue("");
        mini.get("versionType").setValue("");
        mini.get("productType").setValue("");
        mini.get("salesModel").setValue("");
        searchFrm();
    }

    function noteStatusRenderer(e) {
        var record = e.record;
        var note = record.note;
        var arr = [{'key': '最新版本', 'value': '最新版本', 'css': 'green'},
            {'key': '历史版本', 'value': '历史版本', 'css': 'red'}];
        return $.formatItemValue(arr, note);
    }
</script>
<redxun:gridScript gridId="jxbzzbshSelectGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl="" />
</body>
</html>

