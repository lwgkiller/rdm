<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>研发降本项目列表</title>
    <%@include file="/commons/list.jsp" %>
<%--    <script src="${ctxPath}/scripts/rdmZhgl/yfjbList.js?version=${static_res_version}" type="text/javascript"></script>--%>
</head>
<style type="text/css">
</style>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <ul class="toolBtnBox">
            <a class="mini-button" style="margin-left: 5px" plain="true" onclick="exportExcel()">导出</a>
        </ul>
    </div>
</div>
<div class="mini-fit" style="height:  100%;">
    <div id="listGrid" class="mini-datagrid" allowResize="true" style="height:  100%;" sortField="UPDATE_TIME_"
         sortOrder="desc"
         url="${ctxPath}/rdmZhgl/core/yfjb/delayList.do?deptId=${deptId}&delayType=${delayType}" idField="id" showPager="false" allowCellWrap="true"
         multiSelect="true" showColumnsMenu="false" sizeList="[10,20,50,100,200]" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="indexcolumn" align="center" headerAlign="center" width="40px">序号</div>
            <div field="saleModel" name="saleModel" width="200px" headerAlign="center" align="center" allowSort="false">
                销售型号
            </div>
            <div field="designModel" name="designModel" width="200px" headerAlign="center" align="left"
                 allowSort="false">设计型号
            </div>
            <div field="infoStatus" width="150px" headerAlign="center" align="center" allowSort="false"
                 renderer="onStatus">项目状态
            </div>
            <div field="isNewProcess" width="150px" headerAlign="center" align="center" allowSort="false"
                 renderer="onNewProcess">是否填写最新进度
            </div>
            <div width="150px" headerAlign="center" align="center" renderer="renderMember" allowSort="false">项目成员</div>
            <div field="orgItemCode" width="150px" headerAlign="center" align="center">原物料编码</div>
            <div field="orgItemName" width="200px" headerAlign="center" align="center" allowSort="false">原物料名称</div>
            <div field="orgItemPrice" width="150px" headerAlign="center" align="center" allowSort="false"
                 renderer="onHidePrice">原物料价格
            </div>
            <div field="orgSupplier" width="200px" headerAlign="center" align="center" allowSort="false">原供应商</div>
            <div field="basePrice" width="150px" headerAlign="center" align="center" allowSort="false"
                 renderer="onHidePrice">基准价格
            </div>
            <div field="costType" width="150px" headerAlign="center" align="center" allowSort="false" renderer="onJbfs">
                降本方式
            </div>
            <div field="costMeasure" width="200px" headerAlign="center" align="center" allowSort="false">降本措施</div>
            <div field="newItemCode" width="150px" headerAlign="center" align="center" allowSort="false">替代物料编码</div>
            <div field="newItemName" width="200px" headerAlign="center" align="center" allowSort="false">替代物料名称</div>
            <div field="newItemPrice" width="150px" headerAlign="center" align="center" allowSort="false"
                 renderer="onHidePrice">替代物料价格
            </div>
            <div field="newSupplier" width="200px" headerAlign="center" align="center" allowSort="false">新供应商</div>
            <div field="differentPrice" width="150px" headerAlign="center" align="center" allowSort="false"
                 renderer="toFixNum">差额
            </div>
            <div field="perSum" width="150px" headerAlign="center" align="center" allowSort="false">单台用量</div>
            <div field="replaceRate" width="150px" headerAlign="center" align="center" allowSort="false">代替比例(%)</div>
            <div field="perCost" width="150px" headerAlign="center" align="center" allowSort="false"
                 renderer="toFixNum">单台降本
            </div>
            <div field="achieveCost" width="150px" headerAlign="center" align="center" allowSort="false"
                 renderer="toFixNum">已实现单台降本
            </div>
            <div field="risk" width="200px" headerAlign="center" align="center" allowSort="false">风险评估</div>
            <div field="isReplace" width="150px" headerAlign="center" align="center" allowSort="false"
                 renderer="onReplace">生产是否切换
            </div>
            <div field="isSz" width="150px" headerAlign="center" align="center" allowSort="false" renderer="onIsSz">
                是否需要试制
            </div>
            <div field="jhsz_date" width="150px" headerAlign="center" align="center" allowSort="false">计划试制时间</div>
            <div field="sjsz_date" width="150px" headerAlign="center" align="center" allowSort="false">实际试制时间</div>
            <div field="jhxfqh_date" width="180px" headerAlign="center" align="center" allowSort="false">计划下发切换通知单时间
            </div>
            <div field="sjxfqh_date" width="180px" headerAlign="center" align="center" allowSort="false">实际下发切换通知单时间
            </div>
            <div field="jhqh_date" width="150px" headerAlign="center" align="center" allowSort="false">计划切换时间</div>
            <div field="sjqh_date" width="150px" headerAlign="center" align="center" allowSort="false">实际切换时间</div>
            <div field="deptName" width="150px" headerAlign="center" align="center" allowSort="false">所属部门</div>
            <div field="major" width="150px" headerAlign="center" align="center" allowSort="false" renderer="onMajor">
                所属专业
            </div>
            <div field="responseMan" width="150px" headerAlign="center" align="center" allowSort="false">责任人</div>
            <div field="yearMonth" width="150px" headerAlign="center" align="center" allowSort="false">进度年月</div>
            <div field="type" width="150px" headerAlign="center" align="center" allowSort="false"
                 renderer="onProcessType">进度类型
            </div>
            <div field="processStatus" width="150px" headerAlign="center" align="center" allowSort="false"
                 renderer="onProcessStatus">进度状态
            </div>
            <div field="processContent" width="150px" headerAlign="center" align="center" allowSort="false">进度内容</div>
            <div width="150px" headerAlign="center" align="center" renderer="process" allowSort="false">降本项目进度跟踪</div>
        </div>
    </div>
</div>
<form id="excelForm" action="${ctxPath}/rdmZhgl/core/yfjb/exportDelayExcel.do" method="post" target="excelIFrame">
    <input type="hidden" name="deptId" id="deptId" value="${deptId}"/>
    <input type="hidden" name="delayType" id="delayType" value="${delayType}"/>
</form>
<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var currentUserId = "${currentUser.userId}";
    var listGrid = mini.get("listGrid");
    var jbfsList = getDics("YFJB-JBFS");
    var replaceList = getDics("YESORNO");
    var majorList = getDics("YFJB-SSZY");
    var statusList = getDics("YFJB-XMZT");
    var processTypeList = getDics("YFJB-JDLB");
    var processStatusList = getDics("YFJB-JDZT");
    listGrid.frozenColumns(0, 5);


    function exportExcel(){
        <%--mini.get('deptId').setValue(${deptId});--%>
        <%--mini.get('delayType').setValue(${delayType});--%>
        var excelForm = $("#excelForm");
        excelForm.submit();
    }

    function toFixNum(e) {
        if (e.value) {
            return parseFloat(e.value).toFixed(2);
        }
    }
    function onProcessStatus(e) {
        var processStatus = e.record.processStatus;
        if (processStatus == 'tq') {
            e.rowStyle = 'background-color:green';
        } else if (processStatus == 'yh') {
            e.rowStyle = 'background-color:red';
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

    function onIsSz(e) {
        var record = e.record;
        var resultValue = record.isSz;
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

    function onProcessType(e) {
        var record = e.record;
        var resultValue = record.type;
        var resultText = '';
        for (var i = 0; i < processTypeList.length; i++) {
            if (processTypeList[i].key_ == resultValue) {
                resultText = processTypeList[i].text;
                break
            }
        }
        return resultText;
    }

    function onProcessStatus(e) {
        var record = e.record;
        var resultValue = record.processStatus;
        var resultText = '';
        for (var i = 0; i < processStatusList.length; i++) {
            if (processStatusList[i].key_ == resultValue) {
                resultText = processStatusList[i].text;
                break
            }
        }
        return resultText;
    }

    function onNewProcess(e) {
        var record = e.record;
        var resultValue = record.isNewProcess;
        var resultText = '';
        for (var i = 0; i < replaceList.length; i++) {
            if (replaceList[i].key_ == resultValue) {
                resultText = replaceList[i].text;
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
        } else if (resultValue == '4') {
            color = '#3b0df0';
        }
        _html = '<span style="color: ' + color + '">' + resultText + '</span>'
        return _html;
    }

    function onHidePrice() {
        return '***';
    }
</script>
<redxun:gridScript gridId="listGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
