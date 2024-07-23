<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>技术秘密列表管理</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/rdmZhgl/ccbgList.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.ccbgList.name" />: </span>
                    <input class="mini-textbox" id="year" name="year">
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.ccbgList.name1" />: </span>
                    <input class="mini-textbox" id="month" name="month">
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.ccbgList.name2" />: </span>
                    <input id="level1" name="level1" class="mini-combobox" style="width:98%"
                           url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=CCBGYJFL"
                           valueField="key" textField="value" showNullItem="true" onvaluechanged="onLevel1Changed"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.ccbgList.name3" />: </span>
                    <input id="level2" name="level2" class="mini-combobox" style="width:98%"
                           valueField="key" textField="value"/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto"><spring:message code="page.ccbgList.name4" />:</span>
                    <input id="editorUserDepId"
                           name="editorUserDepId"
                           class="mini-buttonedit icon-dep-button"
                           required="true" allowInput="false"
                           onbuttonclick="selectMainDep" style="width:98%"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.ccbgList.name5" />: </span>
                    <input class="mini-textbox" id="editorUserName" name="editorUserName">
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.ccbgList.name6" />: </span>
                    <input class="mini-textbox" id="memberUserNames" name="memberUserNames">
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto"><spring:message code="page.ccbgList.name7" />: </span>
                    <input id="businessStatus" name="businessStatus" class="mini-combobox" style="width:120px;"
                           textField="value" valueField="key" emptyText="<spring:message code="page.ccbgList.name8" />..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.ccbgList.name8" />..."
                           data="[{'key' : 'editing','value' : '编制中'},
							   {'key' : 'reviewing','value' : '审核中'},
							   {'key' : 'approving','value' : '批准中'},
							   {'key' : 'close','value' : '审批通过'}]"
                    />
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()"><spring:message code="page.ccbgList.name9" /></a>
                    <%--<a class="mini-button" style="margin-right: 5px" plain="true" onclick="updateUser()">更新</a>--%>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()"><spring:message code="page.ccbgList.name10" /></a>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="addCcbg()"><spring:message code="page.ccbgList.name11" /></a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="removeCcbg()"><spring:message code="page.ccbgList.name12" /></a>
                </li>
            </ul>
        </form>
        <span class="searchSelectionBtn" onclick="no_search(this ,'searchForm')">
				<i class="icon-sc-lower"></i>
        </span>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="ccbgListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onlyCheckSelection="true"
         url="${ctxPath}/zhgl/core/ccbg/getCcbgList.do" idField="id" allowCellWrap="true"
         multiSelect="true" showColumnsMenu="true" sizeList="[10,50,100,200]" pageSize="10" allowAlternating="true" pagerButtons="#pagerButtons"
         sortField="endTime" sortOrder="desc">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div name="action" cellCls="actionIcons" width="100" headerAlign="center" align="center" renderer="onActionRenderer"
                 cellStyle="padding:0;"><spring:message code="page.ccbgList.name13" />
            </div>
            <div field="remark" width="150" headerAlign="center" align="center" allowSort="false" renderer="render"><spring:message code="page.ccbgList.name14" /></div>
            <div field="level1" width="100" headerAlign="center" align="center" allowSort="false" renderer="render"><spring:message code="page.ccbgList.name2" /></div>
            <div field="level2" width="100" headerAlign="center" align="center" allowSort="false" renderer="render"><spring:message code="page.ccbgList.name3" /></div>
            <div field="editorUserName" width="80" headerAlign="center" align="center" allowSort="false" renderer="render"><spring:message code="page.ccbgList.name5" /></div>
            <div field="editorUserDeptName" width="150" headerAlign="center" align="center" allowSort="false" renderer="render"><spring:message code="page.ccbgList.name4" /></div>
            <div field="memberUserNames" align="center" width="200" headerAlign="center" allowSort="false" renderer="render"><spring:message code="page.ccbgList.name6" /></div>
            <div field="beginTime" width="90" headerAlign="center" align="center" allowSort="false" format="yyyy-MM-dd"><spring:message code="page.ccbgList.name15" /></div>
            <div field="endTime" width="90" headerAlign="center" align="center" allowSort="false" format="yyyy-MM-dd"><spring:message code="page.ccbgList.name16" /></div>
            <div field="year" width="60" headerAlign="center" align="center" allowSort="false" renderer="render"><spring:message code="page.ccbgList.name" /></div>
            <div field="month" width="60" headerAlign="center" align="center" allowSort="false" renderer="render"><spring:message code="page.ccbgList.name1" /></div>
            <div field="trip" width="300" headerAlign="center" align="center" allowSort="false" renderer="render"><spring:message code="page.ccbgList.name17" /></div>
            <div field="primaryCoverage" width="300" headerAlign="center" align="center" allowSort="false" renderer="render"><spring:message code="page.ccbgList.name18" /></div>
            <div field="summaryAndProposal" width="300" headerAlign="center" align="center" allowSort="false" renderer="render"><spring:message code="page.ccbgList.name19" /></div>
            <div field="firstFileId" width="200" headerAlign="center" align="center" visible="false"><spring:message code="page.ccbgList.name20" /></div>
            <div field="fileName" width="200" headerAlign="center" align="center" renderer="renderFile"><spring:message code="page.ccbgList.name21" /></div>
            <div field="businessStatus" width="80" headerAlign="center" align="center" allowSort="false" renderer="onStatusRenderer"><spring:message code="page.ccbgList.name22" /></div>
            <div field="currentProcessUser" sortField="currentProcessUser" width="80" align="center" headerAlign="center"
                 allowSort="false" renderer="render"><spring:message code="page.ccbgList.name23" />
            </div>
            <div field="currentProcessTask" width="80" align="center" headerAlign="center" renderer="render"><spring:message code="page.ccbgList.name24" /></div>
            <div field="reviewerUserName" width="80" headerAlign="center" align="center" allowSort="false" renderer="render"><spring:message code="page.ccbgList.name25" /></div>
            <div field="approverUserName" width="80" headerAlign="center" align="center" allowSort="false" renderer="render"><spring:message code="page.ccbgList.name26" /></div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var ccbgListGrid = mini.get("ccbgListGrid");
    var currentUserId = "${currentUserId}";
    var currentUserNo = "${currentUserNo}";
    var currentUserName = "${currentUserName}";
    var currentTime = new Date();
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>" + xzxgwjjxyxgs;

    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var ccbgId = record.id;
        var instId = record.instId;
        var s = '';
        s += '<span  title=' + ccbgList_name + ' onclick="ccbgDetail(\'' + ccbgId + '\',\'' + record.status + '\')">' + ccbgList_name + '</span>';
        //任何人都能编辑但只有创建者能删除
        if (record.status == 'DRAFTED') {
            s += '<span  title=' + ccbgList_name1 + ' onclick="ccbgEdit(\'' + ccbgId + '\',\'' + instId + '\')">' + ccbgList_name1 + '</span>';
        } else {
            var currentProcessUserId = record.currentProcessUserId;
            if ((currentProcessUserId && currentProcessUserId.indexOf(currentUserId) != -1) || currentUserNo == 'admin') {
                s += '<span  title=' + ccbgList_name2 + ' onclick="ccbgTask(\'' + record.taskId + '\')">' + ccbgList_name2 + '</span>';
            }
        }
        var status = record.status;
        if (currentUserNo != 'admin') {
            //任何人都能编辑但只有创建者能删除
            if (status == 'DRAFTED' && currentUserId == record.CREATE_BY_) {
                s += '<span  title=' + ccbgList_name3 + ' onclick="removeCcbg(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">' + ccbgList_name3 + '</span>';
            }
        } else {
            s += '<span  title=' + ccbgList_name3 + ' onclick="removeCcbg(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">' + ccbgList_name3 + '</span>';
        }
        return s;
    }

    function onStatusRenderer(e) {
        var record = e.record;
        var businessStatus = record.businessStatus;

        var arr = [{'key': 'editing', 'value': '编制中', 'css': 'blue'},
            {'key': 'reviewing', 'value': '审核中', 'css': 'orange'},
            {'key': 'approving', 'value': '批准中', 'css': 'red'},
            {'key': 'close', 'value': '审批通过', 'css': 'green'}
        ];

        return $.formatItemValue(arr, businessStatus);
    }

    //所有非特殊文本都变成这个格式
    function render(e) {
        if (e.value != null && e.value != "") {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            return html;
        }
    }

    //文件预览渲染
    function renderFile(e) {
        var record = e.record;
        var cellHtml = '';
        var fileName = record.fileName;
        var fileId = record.firstFileId;
        var formId = record.id;
        if (fileName != "" && fileName != undefined) {
            var fileType = getFileType(fileName);
            if (fileType == 'other') {
                cellHtml = '<span  title=' + ccbgList_name4 + ' style="color: silver" >' + fileName + '</span>';
            } else if (fileType == 'pdf') {
                var url = '/zhgl/core/ccbg/ccbgPdfPreview.do';
                cellHtml = '<span  title=' + ccbgList_name4 + ' style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">' + fileName + '</span>';
            } else if (fileType == 'office') {
                var url = '/zhgl/core/ccbg/ccbgOfficePreview.do';
                cellHtml = '<span  title=' + ccbgList_name4 + ' style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">' + fileName + '</span>';
            } else if (fileType == 'pic') {
                var url = '/zhgl/core/ccbg/ccbgImagePreview.do';
                cellHtml = '<span  title=' + ccbgList_name4 + ' style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\')">' + fileName + '</span>';
            }
        } else {
            cellHtml = '';
        }

        return cellHtml;
    }

    function getFileType(fileName) {
        var suffix = "";
        var suffixIndex = fileName.lastIndexOf('.');
        if (suffixIndex != -1) {
            suffix = fileName.substring(suffixIndex + 1).toLowerCase();
        }
        var pdfArray = ['pdf'];
        if (pdfArray.indexOf(suffix) != -1) {
            return 'pdf';
        }
        var officeArray = ['doc', 'docx', 'ppt', 'txt', 'xlsx', 'xls', 'pptx'];
        if (officeArray.indexOf(suffix) != -1) {
            return 'office';
        }
        var picArray = ['jpg', 'jpeg', 'jif', 'bmp', 'png', 'tif', 'gif'];
        if (picArray.indexOf(suffix) != -1) {
            return 'pic';
        }
        return 'other';
    }
    //选择主部门
    function selectMainDep(e) {
        var b = e.sender;

        _TenantGroupDlg('1', true, '', '1', function (g) {
            b.setValue(g.groupId);
            b.setText(g.name);
        }, false);

    }

    function onLevel1Changed() {
        var level1 = mini.get("level1").getValue();
        var url = jsUseCtxPath + "/sys/core/sysDic/getByDicKey.do?dicKey=CCBGEJFL";
        var level2s = [];
        $.ajax({
            url: url,
            method: 'get',
            success: function (array) {
                level2s.clear();
                debugger;
                for (var i = 0, l = array.length; i < l; i++) {
                    if (array[i].key.split('-')[0] == level1) {
                        level2s.push({
                            key: array[i].key.split('-')[1],
                            value: array[i].key.split('-')[1]
                        })
                    }
                }
                mini.get("level2").setData(level2s);
            }
        });


        //positionCombo.select(0);
    }
/*    function updateUser() {
        $.ajax({
            url: jsUseCtxPath + '/zhgl/core/ccbg/userUpdate.do',
            success: function (result) {
                mini.alert(result.message);
            }
        })
    }*/

</script>
<redxun:gridScript gridId="ccbgListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>