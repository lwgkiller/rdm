<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>知识产权列表</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/portrait/knowledge/portraitKnowledgeList.js?version=${static_res_version}"
            type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px"><span class="text" style="width:auto">所属部门: </span><input
                        class="mini-textbox" style="width: 150px" name="deptName"/></li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">姓名: </span><input
                        class="mini-textbox" style="width: 150px" name="userName"/></li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">产权号: </span><input
                        class="mini-textbox" style="width: 150px" name="knowledgeCode"/></li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">产权名称: </span><input
                        class="mini-textbox" style="width: 150px" name="knowledgeName"/></li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">产权类型: </span>
                    <input id="knowledgeType" name="knowledgeType" class="mini-combobox rxc" plugins="mini-combobox"
                           style="width:100%;height:34px"  label="产权类型："
                           length="50"
                           only_read="false"  allowinput="false" mwidth="100" onvaluechanged="searchFrm"
                           wunit="%" mheight="34" hunit="px"   shownullitem="true" multiSelect="false"
                           textField="text" valueField="key_" emptyText="请选择..."
                           url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=knowledgeType"
                           nullitemtext="请选择..." emptytext="请选择..."/>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                </li>
            </ul>
        </form>
        <ul class="toolBtnBox">
            <a id="asyncButton" class="mini-button" style="margin-left: 10px" img="${ctxPath}/scripts/mini/miniui/res/images/xhtml.png"
               onclick="asyncData()">数据同步</a>
            <span style="color: red">注：每晚数据自动同步</span>
        </ul>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="knowledgeListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         url="${ctxPath}/portrait/knowledge/knowledgeList.do" idField="id" sortField="UPDATE_TIME_" sortOrder="desc"
         multiSelect="true" showColumnsMenu="false" sizeList="[10,20,50,100,200]" pageSize="15" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div type="indexcolumn" align="center" headerAlign="center" width="30">序号</div>
            <div name="action" cellCls="actionIcons" width="60" headerAlign="center" align="center"
                 renderer="onActionRenderer" cellStyle="padding:0;">操作
            </div>
            <div field="deptName" width="80" headerAlign="center" align="center" allowSort="true">所属部门</div>
            <div field="userName" width="50" headerAlign="center" align="center" allowSort="true">姓名</div>
            <div field="knowledgeCode" width="80" headerAlign="center" align="center" allowSort="true">产权号</div>
            <div field="knowledgeName" width="120" headerAlign="center" align="left" allowSort="true">产权名称</div>
            <div field="knowledgeType" width="50" headerAlign="center" align="center" allowSort="true"
                 renderer="onWSwitchType">产权类型
            </div>
            <div field="applyDate" width="80" headerAlign="center" align="center" allowSort="true">申请日期</div>
            <div field="knowledgeStatus" width="50" headerAlign="center" align="center" allowSort="true"
                 renderer="onWSwitchStatus">产权状态
            </div>
            <div field="authorizeDate" width="80" headerAlign="center" align="center" allowSort="true">受理或授权日期</div>
            <div field="ranking" width="50" headerAlign="center" align="center" allowSort="true">排名
            </div>
            <div field="score" width="50" headerAlign="center" align="center" allowSort="false">得分</div>
            <div field="CREATE_TIME_" width="80" headerAlign="center" align="center" allowSort="false">添加日期</div>
        </div>
    </div>
</div>
<div id="importWindow" title="知识产权导入窗口" class="mini-window" style="width:750px;height:280px;"
     showModal="true" showFooter="false" allowResize="true">
    <p style="display: inline-block;color: red;font-size:15px;vertical-align: middle">
        <image src="${ctxPath}/styles/images/warn.png" style="margin-right:5px;vertical-align: middle;height: 15px"/>
        注意：公司领导不计入排名，无需将领导加入作者中排名！
    </p>
    <div style="display: inline-block;float: right;">
        <a id="importBtn" class="mini-button" onclick="importKnowledge()">导入</a>
        <a id="closeWindow" class="mini-button btn-red" onclick="closeImportWindow()">关闭</a>
    </div>
    <div class="mini-fit" style="font-size: 14px;margin-top: 30px">
        <form id="formImport" method="post">
            <table class="table-detail" cellspacing="1" cellpadding="0">
                <tr>
                    <td style="width: 30%">上传模板：</td>
                    <td style="width: 70%;">
                        <a href="#" style="color:blue;text-decoration:underline;" onclick="downImportTemplate()">知识产权导入模板下载.xls</a>
                    </td>
                </tr>
                <tr>
                    <td style="width: 30%">操作：</td>
                    <td>
                        <input class="mini-textbox" style="width:60%;float: left" id="fileName" name="fileName"
                               readonly/>
                        <input id="inputFile" style="display:none;" type="file" onchange="getSelectFile()"
                               accept="application/xls"/>
                        <a id="uploadFileBtn" class="mini-button" style="float: left;margin-left: 10px"
                           onclick="uploadFile">选择文件</a>
                        <a id="clearFileBtn" class="mini-button" style="float: left;margin-left: 10px"
                           onclick="clearUploadFile">清除</a>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var knowledgeListGrid = mini.get("knowledgeListGrid");
    var typeList = getDics("knowledgeType");
    var statusList = getDics("knowledgeStatus");
    var rankList = getDics("knowledgeRank");
    var importWindow = mini.get("importWindow");
    var permission = ${permission};
    if(!permission){
        mini.get("asyncButton").setEnabled(false);
    }
    //数据同步
    function asyncData() {
        mini.confirm("确定同步数据？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                _SubmitJson({
                    url: jsUseCtxPath + "/portrait/knowledge/asyncKnowledge.do",
                    method: 'POST',
                    success: function (data) {
                        searchFrm();
                    }
                });
            }
        });
    }
    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var id = record.id;
        var s = '<span  title="查看" onclick="viewForm(\'' + id + '\',\'view\')">查看</span>';
        return s;
    }

    function onWSwitchType(e) {
        var record = e.record;
        var knowledgeType = record.knowledgeType;
        var typeText = '';
        for (var i = 0; i < typeList.length; i++) {
            if (typeList[i].key_ == knowledgeType) {
                typeText = typeList[i].text;
                break
            }
        }
        return typeText;
    }

    function onWSwitchStatus(e) {
        var record = e.record;
        var knowledgeStatus = record.knowledgeStatus;
        var statusText = '';
        for (var i = 0; i < statusList.length; i++) {
            if (statusList[i].key_ == knowledgeStatus) {
                statusText = statusList[i].text;
                break
            }
        }
        return statusText;
    }

    function onWSwitchRank(e) {
        var record = e.record;
        var ranking = record.ranking;
        var rankText = '';
        for (var i = 0; i < rankList.length; i++) {
            if (rankList[i].key_ == ranking) {
                rankText = rankList[i].text;
                break
            }
        }
        return rankText;
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
<redxun:gridScript gridId="knowledgeListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
