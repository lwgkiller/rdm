<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>改进计划列表管理</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/standardManager/standardUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">标准编号: </span>
                    <input class="mini-textbox" id="standardNumber" name="standardNumber"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">标准名称: </span>
                    <input class="mini-textbox" id="standardName" name="standardName"/>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <div style="display: inline-block" class="separator"></div>
                    <a id="addJsbz" class="mini-button" style="margin-right: 5px" plain="true" onclick="addJsbz2()">关联标准</a>
                    <a id="addMsg" class="mini-button" style="margin-right: 5px" plain="true" onclick="addMsg()">发送关联信息</a>
                </li>
            </ul>
        </form>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="jsbzListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onlyCheckSelection="true"
         url="${ctxPath}/standardManager/core/standard/queryList.do?belongbj=${type}" idField="id"
         multiSelect="false" showColumnsMenu="true" sizeList="[50,100,200]" pageSize="50"
         allowAlternating="true" pagerButtons="#pagerButtons" showPager="true">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div name="action" cellCls="actionIcons" width="30" headerAlign="center" align="center" renderer="onMessageActionRenderer"
                 cellStyle="padding:0;">操作
            </div>
            <div field="categoryName" headerAlign='center' align='center' width="40">标准类别</div>
            <div field="standardNumber" width="50" headerAlign="center" align="center" allowSort="true">标准编号</div>
            <div field="standardName" width="100" headerAlign="center" align="center" allowSort="true">标准名称</div>
            <div field="standardStatus" sortField="standardStatus" width="30" headerAlign="center"
                 align="center" hideable="true"
                 allowSort="true" renderer="statusRenderer">状态
            </div>
            <div field="userName" headerAlign='center' align='center' width="40">关联人</div>
            <div cellCls="actionIcons" width="50" headerAlign="center" align="center" renderer="fileRenderer"
                 cellStyle="padding:0;" hideable="true">标准全文
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var jsbzListGrid = mini.get("jsbzListGrid");
    var currentUserZJ =${currentUserZJ};
    var currentUserId = "${currentUserId}";
    var currentUserRoles =${currentUserRoles};
    var type = "${type}";
    var currentUserNo = "${currentUserNo}";
    var currentTime = "${currentTime}";
    var coverContent = "${currentUserName}" + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";
    var isGlNetwork = "${isGlNetwork}";
    var isGlr =${isGlr};
    // $(function () {
    //     if (isGlr) {
    //         $("#addMsg").show();
    //         $("#addJsbz").show();
    //     }
    // });
    //行功能按钮
    function onMessageActionRenderer(e) {
        var record = e.record;
        var standardId = record.standardId;
        var userId = record.userId;
        var s = '';
        if (isGlr||userId==currentUserId) {
            s += '<span  title="移除" onclick="removeJsbz(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">移除</span>';
        } else {
            s += '<span  title="移除" style="color: silver">移除</span>';
        }
        return s;
    }


    function judgePreviewNeedApply(standardId, categoryName) {
        if (categoryName != '企业标准') {
            return {result: '0'};
        }
        var applyCategoryId = 'preview';
        //标准管理领导不需要申请
        var isLeader = whetherIsLeader(currentUserRoles);
        if (isLeader) {
            return {result: '0'};
        }
        //技术标准管理人员不需要申请
        var JSSystemStandardManager = whetherIsPointStandardManager('JS', currentUserRoles);
        if (JSSystemStandardManager) {
            return {result: '0'};
        }
        //非管理职级的人员预览也不需要
        var isGLMan = whetherIsGLMan(currentUserZJ);
        if (!isGLMan) {
            return {result: '0'};
        }
        //其他场景都需要判断是否已经有申请单
        var resultCodeId = {result: '3'};
        $.ajax({
            url: jsUseCtxPath + "/standardManager/core/standardApply/checkOperateApply.do",
            type: 'POST',
            data: mini.encode({standardId: standardId, applyCategoryId: applyCategoryId, applyUserId: currentUserId}),
            contentType: 'application/json',
            async: false,
            success: function (data) {
                if (data) {
                    resultCodeId.result = data.result;
                    resultCodeId.applyId = data.applyId;
                }
            }
        });
        return resultCodeId;
    }

    function judgeDownloadNeedApply(standardId, categoryName) {
        var applyCategoryId = 'download';
        //如果技术标准的类型不是企业标准，则允许下载
        if (categoryName != '企业标准') {
            return {result: '0'};
        } else {
            //其他场景都需要判断是否已经有申请单
            var resultCodeId = {result: '3'};
            $.ajax({
                url: jsUseCtxPath + "/standardManager/core/standardApply/checkOperateApply.do",
                type: 'POST',
                data: mini.encode({standardId: standardId, applyCategoryId: applyCategoryId, applyUserId: currentUserId}),
                contentType: 'application/json',
                async: false,
                success: function (data) {
                    if (data) {
                        resultCodeId.result = data.result;
                        resultCodeId.applyId = data.applyId;
                    }
                }
            });
            return resultCodeId;
        }
    }


    function previewStandard(standardId, standardName, standardNumber, categoryName, status, coverConent, systemCategoryId) {
        if (status == 'disable') {
            mini.confirm("该标准已废止，确定预览吗？", "提示", function (action) {
                if (action == "ok") {
                    previewStandardDo(standardId, standardName, standardNumber, categoryName, status, coverConent, systemCategoryId);
                }
            });
        } else {
            previewStandardDo(standardId, standardName, standardNumber, categoryName, status, coverConent, systemCategoryId);
        }

    }

    function previewStandardDo(standardId, standardName, standardNumber, categoryName, status, coverConent, systemCategoryId) {
        var resultCodeId = judgePreviewNeedApply(standardId, categoryName);
        if (resultCodeId.result == '0' || resultCodeId.result == '1') {
            if (resultCodeId.result == '1') {
                changeApplyUseStatus(resultCodeId.applyId, 'yes');
            }
            //记录预览情况
            recordStandardOperate('preview', standardId);
            var previewUrl = jsUseCtxPath + "/standardManager/core/standard/preview.do?standardId=" + standardId;
            if (systemCategoryId == 'JS') {
                window.open(jsUseCtxPath + "/pdfjs/web/viewer.html?coverContent=" + encodeURIComponent(coverConent) + "&plate=bz&path=" + jsUseCtxPath + "&recordId=" + standardId + "&file=" + encodeURIComponent(previewUrl));
            } else {
                window.open(jsUseCtxPath + "/pdfjs/web/viewer.html?coverContent=" + encodeURIComponent(coverConent) + "&path=" + jsUseCtxPath + "&recordId=" + standardId + "&file=" + encodeURIComponent(previewUrl));
            }

        } else if (resultCodeId.result == '2') {
            mini.alert('请在“标准预览申请”页面跟进申请单“' + resultCodeId.applyId + '”的审批');
        } else if (resultCodeId.result == '3') {
            //跳转到新增预览申请界面
            mini.confirm("当前操作需要提交预览申请，确定继续？", "权限不足", function (action) {
                if (action == "ok") {
                    addApply('preview', standardId, standardName);
                }
            });
        }
    }
    function addApply(applyCategoryId, standardId, standardName) {
        var title = "新增预览申请";
        if (applyCategoryId == 'download') {
            title = "新增下载申请";
        }
        var width = getWindowSize().width;
        var height = getWindowSize().height;
        _OpenWindow({
            url: jsUseCtxPath + "/bpm/core/bpmInst/BZGLSQ/start.do?standardApplyCategoryId=" + applyCategoryId + "&standardId=" + standardId,
            title: title,
            width: width,
            height: height,
            showMaxButton: true,
            showModal: true,
            allowResize: true,
            ondestroy: function () {
                if (standardListGrid) {
                    standardListGrid.reload();
                }
            }
        });
    }
    //下载标准全文
    function downloadStandard(standardId, standardName, standardNumber, categoryName, status) {
        if (status == 'disable') {
            mini.confirm("该标准已废止，确定下载吗？", "提示", function (action) {
                if (action == "ok") {
                    downloadStandardDo(standardId, standardName, standardNumber, categoryName, status);
                }
            });
        }
        else {
            downloadStandardDo(standardId, standardName, standardNumber, categoryName, status);
        }
    }

    function downloadStandardDo(standardId, standardName, standardNumber, categoryName, status) {
        var resultCodeId = judgeDownloadNeedApply(standardId, categoryName);
        if (resultCodeId.result == '0' || resultCodeId.result == '1') {
            if (resultCodeId.result == '1') {
                changeApplyUseStatus(resultCodeId.applyId, 'yes');
            }
            //记录下载情况
            recordStandardOperate('download', standardId);
            var form = $("<form>");
            form.attr("style", "display:none");
            form.attr("target", "");
            form.attr("method", "post");
            form.attr("action", jsUseCtxPath + "/standardManager/core/standard/download.do");
            var standardIdAttr = $("<input>");
            standardIdAttr.attr("type", "hidden");
            standardIdAttr.attr("name", "standardId");
            standardIdAttr.attr("value", standardId);
            var standardNameAttr = $("<input>");
            standardNameAttr.attr("type", "hidden");
            standardNameAttr.attr("name", "standardName");
            standardNameAttr.attr("value", standardName);
            var standardNumberAttr = $("<input>");
            standardNumberAttr.attr("type", "hidden");
            standardNumberAttr.attr("name", "standardNumber");
            standardNumberAttr.attr("value", standardNumber);
            $("body").append(form);
            form.append(standardIdAttr);
            form.append(standardNameAttr);
            form.append(standardNumberAttr);
            form.submit();
            form.remove();
        } else if (resultCodeId.result == '2') {
            mini.alert('已存在审批中的申请，请在“标准流程管理”--“标准下载申请”页面跟进申请单“' + resultCodeId.applyId + '”的审批');
        } else if (resultCodeId.result == '3') {
            //跳转到新增下载申请界面
            mini.confirm("当前操作会创建一个下载申请单，审批完成后可在此处或“标准流程管理”--“标准下载申请”页面点击“下载”按钮下载标准，确定继续？", "权限不足", function (action) {
                if (action == "ok") {
                    addApply('download', standardId, standardName);
                }
            });
        }
    }
    function recordStandardOperate(action, standardId) {
        $.ajax({
            url: jsUseCtxPath + "/standardManager/core/standard/record.do?standardId=" + standardId + "&action=" + action,
            method: 'GET',
            success: function (data) {

            }
        });
    }

    function changeApplyUseStatus(applyId, useStatus) {
        $.ajax({
            url: jsUseCtxPath + "/standardManager/core/standardApply/changeUseStatus.do?applyId=" + applyId + "&useStatus=" + useStatus,
            method: 'GET',
            success: function () {

            }
        });
    }

    function fileRenderer(e) {
        var record = e.record;
        var standardId = record.id;
        var existFile = record.existFile;
        var standardName = record.standardName;
        var standardNumber = record.standardNumber;
        var status = record.standardStatus;
        var categoryName = record.categoryName;
        var systemCategoryId = record.systemCategoryId;
        if (existFile) {
            var s = '<span title="预览"  onclick="previewStandard(\'' + standardId + '\',\'' + standardName + '\',\'' + standardNumber + '\',\'' + categoryName + '\',\'' + status + '\',\'' + coverContent + '\',\'' + systemCategoryId + '\')">预览</span>';
            if (isGlNetwork == "true") {
                s = '<span title="预览"  style="color: silver">预览</span>';
            }
            s += '<span title="下载" onclick="downloadStandard(\'' + standardId + '\',\'' + standardName + '\',\'' + standardNumber + '\',\'' + categoryName + '\',\'' + status + '\')">下载</span>';
            return s;
        } else {
            var s = '<span title="预览"  style="color: silver">预览</span>';
            s += '<span title="下载" style="color: silver">下载</span>';
            return s;
        }
    }

    function statusRenderer(e) {
        var record = e.record;
        var status = record.standardStatus;

        var arr = [{'key': 'draft', 'value': '草稿', 'css': 'orange'},
            {'key': 'enable', 'value': '有效', 'css': 'green'},
            {'key': 'disable', 'value': '已废止', 'css': 'red'}
        ];

        return $.formatItemValue(arr, status);
    }
    //马天宇:type是某个业务的id
    function addJsbz() {
        var url = jsUseCtxPath + "/standardManager/core/standard/tabPage.do?tabName=JS&action=add&type=" + type;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (jsbzListGrid) {
                    jsbzListGrid.reload()
                }
            }
        }, 1000);
    }

    //@lwgkiller改:将type改为业务标志，然后再传一个businessId代表业务id。如果后期绑定业务暴增，还要进行改造，通过post传json过去
    function addJsbz2() {
        var url = jsUseCtxPath + "/standardManager/core/standard/tabPage.do?tabName=JS&action=add&type=keyDesign&businessId=" + type;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (jsbzListGrid) {
                    jsbzListGrid.reload()
                }
            }
        }, 1000);
    }

    function removeJsbz(record) {
        var rows = [];

        if (record) {
            rows.push(record);
        } else {
            rows = jsbzListGrid.getSelecteds();
        }
        if (rows.length <= 0) {
            mini.alert("请至少选中一条记录");
            return;
        }
        mini.confirm("确定删除选中记录？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                var rowIds = [];
                var belongbjs = [];
                for (var i = 0, l = rows.length; i < l; i++) {
                    var r = rows[i];
                    rowIds.push(r.id);
                    belongbjs.push(r.belongbj);
                }
                _SubmitJson({
                    url: jsUseCtxPath + "/jsbz/deleteJsbz.do",
                    method: 'POST',
                    showMsg: false,
                    data: {ids: rowIds.join(','), belongbjs: belongbjs.join(',')},
                    success: function (data) {
                        if (data) {
                            mini.alert(data.message);
                            searchFrm();
                        }
                    }
                });
            }
        });
    }


    function addMsg() {
        mini.open({
            title: "新增",
            url: jsUseCtxPath + "/jsbz/sendMsgPage.do?action=add&type=" + type,
            width: 1050,
            height: 800,
            allowResize: true,
            onload: function () {
                searchFrm();
            },
            ondestroy: function () {
                searchFrm();
            }
        });
    }

</script>
<redxun:gridScript gridId="jsbzListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>