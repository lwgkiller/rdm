<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>欧美澳售前文件列表</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/rdmZhgl/saleFileOMAList.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>

                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">文件类型: </span>
                    <input id="fileType" name="fileType" class="mini-combobox rxc" plugins="mini-combobox"
                           style="width:100%;height:34px" label="<spring:message code="page.saleFileApplyEdit.name5" />："  onvaluechanged="searchFrm()"
                           length="50"
                           only_read="false" required="true" allowinput="false" mwidth="100"
                           wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                           textField="text" valueField="key_" emptyText="<spring:message code="page.saleFileApplyEdit.name6" />..."
                           url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=sailFileOMA_WJFL"
                           nullitemtext="<spring:message code="page.saleFileApplyEdit.name6" />..." emptytext="<spring:message code="page.saleFileApplyEdit.name6" />..."/>
                </li>

                <li style="margin-right: 15px"><span class="text" style="width:auto"><spring:message code="page.saleFilesList.name3" />: </span><input
                        class="mini-textbox" style="width: 150px" name="designModel"/></li>
                <li style="margin-right: 15px"><span class="text" style="width:auto"><spring:message code="page.saleFilesList.name4" />: </span><input
                        class="mini-textbox" style="width: 150px" name="saleModel"/></li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()"><spring:message code="page.saleFilesList.name5" /></a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()"><spring:message code="page.saleFilesList.name6" /></a>
                </li>
            </ul>
        </form>
        <ul class="toolBtnBox">
        </ul>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="dataListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         url="${ctxPath}/rdmZhgl/core/saleFileOMA/saleFileOMA.do?applyType=${applyType}&fileModel=sq" idField="id"
         sortField="UPDATE_TIME_" sortOrder="desc"
         multiSelect="true" showColumnsMenu="false" sizeList="[10,20,50,100,200]" pageSize="15" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div name="action" cellCls="actionIcons" width="60" headerAlign="center" align="center"
                 renderer="onActionRenderer" cellStyle="padding:0;"><spring:message code="page.saleFilesList.name7" />
            </div>
            <div type="indexcolumn" align="center" headerAlign="center" width="30"><spring:message code="page.saleFilesList.name8" /></div>
            <div field="designModel" width="80" headerAlign="center" align="center" allowSort="true"><spring:message code="page.saleFilesList.name3" /></div>
            <div field="saleModel" width="80" headerAlign="center" align="center" allowSort="true"><spring:message code="page.saleFilesList.name4" /></div>
            <div field="fileType" width="80" headerAlign="center" align="center" allowSort="true"
                 renderer="onWSwitchType"><spring:message code="page.saleFilesList.name" />
            </div>
            <div field="language" width="80" headerAlign="center" align="center" allowSort="true"><spring:message code="page.saleFilesList.name9" /></div>
        </div>
    </div>
</div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var dataListGrid = mini.get("dataListGrid");
    var applyType = "${applyType}";
    var typeList = getDics("sailFileOMA_WJFL");
    var currentUserName = "${currentUserName}";
    var currentTime = "${currentTime}";
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>" + xzxgwjjxyxgs;

    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var designModel = record.designModel;
        var saleModel = record.saleModel;
        var fileType = record.fileType;
        var language = record.language;
        var cellHtml = '';
        cellHtml += '&nbsp;&nbsp;&nbsp;<span title=' + saleFilesList_name + ' style="color:#409EFF;cursor: pointer;" ' +
            'onclick="getDetail(\'' + designModel + '\',\'' + saleModel + '\',\'' + fileType + '\',\''+language+'\')">' + saleFilesList_name + '</span>';
        return cellHtml;
    }
    function getDetail(designModel,saleModel,fileType,language) {
        var action = "detail";
        var url = jsUseCtxPath + "/rdmZhgl/core/saleFileOMA/filesDetail.do?action=" + action + "&designModel=" + designModel
            + "&saleModel=" + saleModel+ "&fileType=" + fileType+ "&language=" + language;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (dataListGrid) {
                    dataListGrid.reload()
                }
            }
        }, 1000);
    }
    function onStatusRenderer(e) {
        var record = e.record;
        var instStatus = record.instStatus;
        var arr = [{'key': 'DRAFTED', 'value': '草稿', 'css': 'orange'},
            {'key': 'RUNNING', 'value': '运行中', 'css': 'green'},
            {'key': 'SUCCESS_END', 'value': '成功结束', 'css': 'blue'},
            {'key': 'DISCARD_END', 'value': '作废', 'css': 'red'},
            {'key': 'ABORT_END', 'value': '异常中止结束', 'css': 'red'},
            {'key': 'PENDING', 'value': '挂起', 'css': 'gray'}
        ];

        return $.formatItemValue(arr, instStatus);
    }

    function onWSwitchType(e) {
        var record = e.record;
        var fileType = record.fileType;
        var typeText = '';
        for (var i = 0; i < typeList.length; i++) {
            if (typeList[i].key_ == fileType) {
                typeText = typeList[i].text;
                break
            }
        }
        return typeText;
    }


    function getDics(dicKey) {
        let resultDate = [];
        $.ajax({
            async: false,
            url: __rootPath + '/sys/core/commonInfo/getDicItem.do?dicType=' + dicKey,
            type: 'GET',
            contentType: 'application/json',
            success: function (data) {
                if (data.code == 200) {
                    resultDate = data.data;
                }
            }
        });
        return resultDate;
    }
</script>
<redxun:gridScript gridId="dataListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
