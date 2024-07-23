<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>试制通知单列表</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/rdmZhgl/yfjbProductionNoticeList.js?version=${static_res_version}"
            type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px"><span class="text" style="width:auto">销售型号: </span>
                    <input name="saleModel" class="mini-textbox rxc" allowInput="true" style="width:100%;"/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">设计型号: </span>
                    <input name="designModel" class="mini-textbox rxc" allowInput="true" style="width:100%;"/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">责任人: </span>
                    <input name="responseMan" class="mini-textbox rxc" allowInput="true" style="width:100%;"/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">降本方式: </span>
                    <input id="costType" name="costType" class="mini-combobox rxc" plugins="mini-combobox"
                           style="width:100%;height:34px" label="降本方式："
                           length="50" onvaluechanged="searchFrm()"
                           only_read="false" required="false" allowinput="false" mwidth="100"
                           wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                           textField="text" valueField="key_" emptyText="请选择..."
                           url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=YFJB-JBFS"
                           nullitemtext="请选择..." emptytext="请选择..."/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">年度: </span>
                    <input id="reportYear" name="reportYear" class="mini-combobox rxc" plugins="mini-combobox"
                           style="width:100%;height:34px" label="年度："
                           length="50"
                           only_read="false" required="true" allowinput="false" mwidth="100"
                           wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                           onvaluechanged="searchFrm()"
                           textField="text" valueField="value" emptyText="请选择..."
                           url="${ctxPath}/sys/core/commonInfo/getYearList.do"
                           nullitemtext="请选择..." emptytext="请选择..."/>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                </li>
            </ul>
        </form>
        <!--导出Excel相关HTML-->
        <form id="excelForm" action="${ctxPath}/rdmZhgl/core/yfjb/productionNotice/exportExcel.do" method="post"
              target="excelIFrame">
            <input type="hidden" name="pageIndex" id="pageIndex"/>
            <input type="hidden" name="pageSize" id="pageSize"/>
            <input type="hidden" name="filter" id="filter"/>
        </form>
        <iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>
        <ul class="toolBtnBox">
            <a class="mini-button btn-red" style="margin-left: 10px" plain="true" onclick="removeRow()">删除</a>
            <a class="mini-button" style="margin-left: 5px" plain="true" onclick="exportBtn()">导出</a>
        </ul>
    </div>
</div>
<div class="mini-fit" style="height:  100%;">
    <div id="listGrid" class="mini-datagrid" allowResize="true" style="height:  100%;" sortField="UPDATE_TIME_"
         sortOrder="desc"
         url="${ctxPath}/rdmZhgl/core/yfjb/productionNotice/list.do" idField="id" showPager="true" allowCellWrap="true"
         multiSelect="true" showColumnsMenu="false" sizeList="[10,20,50,100,200]" allowAlternating="true"
         ondrawcell="onDrawCell"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="30px"></div>
            <div type="indexcolumn" align="center" headerAlign="center" width="40px">序号</div>
            <div name="action" cellCls="actionIcons" width="80" headerAlign="center" align="center"
                 renderer="onActionRenderer" cellStyle="padding:0;">
                操作
            </div>
            <div field="deptName" width="150px" headerAlign="center" align="center">所属部门</div>
            <div field="orgItemCode" width="150px" headerAlign="center" align="center">原物料编码</div>
            <div field="orgItemName" width="200px" headerAlign="center" align="center" allowSort="false">原物料名称</div>
            <div field="orgSupplier" width="200px" headerAlign="center" align="center" allowSort="false">原供应商</div>
            <div field="orgItemPrice" width="150px" headerAlign="center" align="center" allowSort="false">原物料价格</div>
            <div field="costType" width="150px" headerAlign="center" align="center" allowSort="false" renderer="onJbfs">
                降本方式
            </div>
            <div field="costMeasure" width="200px" headerAlign="center" align="center" allowSort="false">降本措施</div>
            <div field="newItemCode" width="150px" headerAlign="center" align="center" allowSort="false">替代物料编码</div>
            <div field="newItemName" width="200px" headerAlign="center" align="center" allowSort="false">替代物料名称</div>
            <div field="newSupplier" width="200px" headerAlign="center" align="center" allowSort="false">替代供应商</div>
            <div field="newItemPrice" width="150px" headerAlign="center" align="center" allowSort="false">替代物料价格</div>
            <div field="differentPrice" width="150px" headerAlign="center" align="center" allowSort="false">差额</div>
            <div field="perSum" width="150px" headerAlign="center" align="center" allowSort="false">单台用量</div>
            <div field="replaceRate" width="150px" headerAlign="center" align="center" allowSort="false">代替比例(%)</div>
            <div field="perCost" width="150px" headerAlign="center" align="center" allowSort="false">单台降本</div>
            <div field="changeable" width="150px" headerAlign="center" align="center" allowSort="false">互换性</div>
            <div field="assessment" width="250px" headerAlign="center" align="center" allowSort="false">
                主要差异性、试制要求、竞品使用情况
            </div>
            <div field="achieveCost" width="150px" headerAlign="center" align="center" allowSort="false">已实现单台降本</div>
            <div field="risk" width="200px" headerAlign="center" align="center" allowSort="false">风险评估</div>
            <div field="isReplace" width="150px" headerAlign="center" align="center" allowSort="false"
                 renderer="onReplace">生产是否切换
            </div>
            <div field="sjqh_date" width="150px" headerAlign="center" align="center" allowSort="false">实际切换时间</div>
            <div field="major" width="150px" headerAlign="center" align="center" allowSort="false" renderer="onMajor">
                所属专业
            </div>
            <div field="responseMan" width="150px" headerAlign="center" align="center" allowSort="false">责任人</div>
            <div field="saleModel" width="150px" headerAlign="center" align="center" allowSort="false">涉及机型</div>
            <div field="productionNo" width="150px" headerAlign="center" align="center" allowSort="false">试制通知号</div>
            <div field="noticeDate" width="150px" headerAlign="center" align="center" allowSort="false">试制通知单下发时间</div>
            <div field="remark" width="150px" headerAlign="center" align="center" allowSort="false">备注</div>
        </div>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var currentUserId = "${currentUser.userId}";
    var listGrid = mini.get("listGrid");
    var jbfsList = getDics("YFJB-JBFS");
    var replaceList = getDics("YESORNO");
    var majorList = getDics("YFJB-SSZY");
    var statusList = getDics("YFJB-XMZT");
    listGrid.frozenColumns(0, 5);
    var permission = ${permission};
    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var id = record.id;
        var CREATE_BY_ = record.CREATE_BY_;
        var s = '';
        if(CREATE_BY_==currentUserId||permission){
            s = '<span  title="编辑" onclick="editForm(\'' + id + '\')">编辑</span>';
        }else{
            s = '<span  title="编辑" style="color: silver">编辑</span>';
        }
        return s;
    }

    function onDrawCell(e) {
        var record = e.record;
        var field = e.field;
        if (field == 'noticeDate') {
            var noticeDate = record.noticeDate;
            var jhsz_date = record.jhsz_date;
            if(noticeDate&&jhsz_date){
                noticeDate = noticeDate.substring(0,7);
            }
            if(noticeDate>jhsz_date){
                e.cellStyle = "background-color: red";
            }
        }
    }

    function onJbfs(e) {
        var record = e.record;
        var resultValue = record.costType;
        var resultText = '';
        for (var i = 0; i < jbfsList.length; i++) {
            if (jbfsList[i].key_ == resultValue) {
                resultText = jbfsList[i].text;
                break
            }
        }
        return resultText;
    }

    function onReplace(e) {
        var record = e.record;
        var resultValue = record.isReplace;
        var resultText = '';
        for (var i = 0; i < replaceList.length; i++) {
            if (replaceList[i].key_ == resultValue) {
                resultText = replaceList[i].text;
                break
            }
        }
        return resultText;
    }

    function onMajor(e) {
        var record = e.record;
        var resultValue = record.major;
        var resultText = '';
        for (var i = 0; i < majorList.length; i++) {
            if (majorList[i].key_ == resultValue) {
                resultText = majorList[i].text;
                break
            }
        }
        return resultText;
    }

    function onStatus(e) {
        var record = e.record;
        var resultValue = record.infoStatus;
        var resultText = '';
        for (var i = 0; i < statusList.length; i++) {
            if (statusList[i].key_ == resultValue) {
                resultText = statusList[i].text;
                break
            }
        }
        var _html = '';
        var color = '';
        if (resultValue == '1') {
            color = '#cdcd62'
        } else if (resultValue == '2') {
            color = 'green';
        } else if (resultValue == '3') {
            color = 'red';
        }
        _html = '<span style="color: ' + color + '">' + resultText + '</span>'
        return _html;
    }
</script>
<redxun:gridScript gridId="listGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
