<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>审批列表</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/rdmZhgl/saleFileApplyList.js?version=${static_res_version}"
            type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.saleFileApplyList.name" />: </span>
                    <input class="mini-textbox" name="applyUserName"/>
                </li>
                <li style="margin-left:15px;margin-right: 15px"><span class="text" style="width:auto"><spring:message code="page.saleFileApplyList.name1" />: </span>
                    <input id="instStatus" name="instStatus" class="mini-combobox" style="width:150px;"
                           textField="value" valueField="key" emptyText="<spring:message code="page.saleFileApplyList.name2" />..." onvaluechanged="searchFrm()"
                           required="false" allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.saleFileApplyList.name2" />..."
                           data="[ {'key' : 'DRAFTED','value' : '草稿'},{'key' : 'RUNNING','value' : '运行中'},{'key' : 'SUCCESS_END','value' : '成功结束'},{'key' : 'DISCARD_END','value' : '作废'},{'key' : 'ABORT_END','value' : '异常中止结束'},{'key' : 'PENDING','value' : '挂起'}]"
                    />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">设计型号: </span>
                    <input class="mini-textbox" name="designModel"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">销售型号: </span>
                    <input class="mini-textbox" name="saleModel"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">文件类型: </span>
                    <input id="fileType" name="fileType" class="mini-combobox rxc" plugins="mini-combobox"
                           style="width:100%;height:34px" label="<spring:message code="page.saleFileApplyEdit.name5" />："  onvaluechanged="onChangeFileType"
                           length="50"
                           only_read="false" required="true" allowinput="false" mwidth="100"
                           wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                           textField="text" valueField="key_" emptyText="<spring:message code="page.saleFileApplyEdit.name6" />..."
                           url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=saleFile_WJFL"
                           nullitemtext="<spring:message code="page.saleFileApplyEdit.name6" />..." emptytext="<spring:message code="page.saleFileApplyEdit.name6" />..."/>
                </li>

                <li class="liBtn">
					<span class="unfoldBtn" onclick="no_more(this,'moreBox')">
						<em><spring:message code="page.saleFileApplyList.name3" /></em>
						<i class="unfoldIcon"></i>
					</span>
                </li>

                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()"><spring:message code="page.saleFileApplyList.name4" /></a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()"><spring:message code="page.saleFileApplyList.name5" /></a>
                    <div style="display: inline-block" class="separator"></div>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="removeApply()"><spring:message code="page.saleFileApplyList.name6" /></a>
                    <a id="deptApply" class="mini-button " style="margin-left: 10px;" plain="true"
                       onclick="doApply()"><spring:message code="page.saleFileApplyList.name7" /></a>
                </li>
            </ul>
            <div id="moreBox">
                <ul>
                    <li style="margin-left:15px;margin-right: 15px">
                        <span class="text" style="width:auto"><spring:message code="page.saleFileApplyList.name8" />: </span>
                        <input class="mini-textbox" name="applyId"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto"><spring:message code="page.saleFileApplyList.name9" /> </span>:<input name="apply_startTime"
                                                                                    class="mini-datepicker"
                                                                                    format="yyyy-MM-dd"
                                                                                    style="width:100px"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text-to" style="width:auto"><spring:message code="page.saleFileApplyList.name10" />: </span><input name="apply_endTime"
                                                                                  class="mini-datepicker"
                                                                                  format="yyyy-MM-dd"
                                                                                  style="width:100px"/>
                    </li>
                </ul>
            </div>
        </form>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="applyListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         url="${ctxPath}/rdmZhgl/core/saleFile/queryList.do?applyType=${applyType}"
         idField="applyId" sortOrder="desc" sortField="applyTime"
         multiSelect="true" showColumnsMenu="false" sizeList="[20,50,100,200]" pageSize="20" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div name="action" cellCls="actionIcons" width="90" headerAlign="center" align="center"
                 renderer="onActionRenderer" cellStyle="padding:0;"><spring:message code="page.saleFileApplyList.name11" />
            </div>
            <div field="id" sortField="id" width="100" headerAlign="center" align="center" allowSort="true"><spring:message code="page.saleFileApplyList.name8" /></div>
            <div field="userName" sortField="userName" width="40" headerAlign="center" align="center" allowSort="true">
                <spring:message code="page.saleFileApplyList.name" />
            </div>
            <div field="designModel" width="70" headerAlign="center" align="center" allowSort="false"><spring:message code="page.saleFileApplyList.name12" /></div>
            <div field="saleModel" width="70" headerAlign="center" align="center" allowSort="false"><spring:message code="page.saleFileApplyList.name13" /></div>
            <div field="fileType" width="70" headerAlign="center" align="center" allowSort="false" renderer="onWSwitchType"><spring:message code="page.saleFileApplyList.name14" /></div>
            <div field="language" width="35" headerAlign="center" align="center" allowSort="false" >语种</div>
            <div field="region" width="70" headerAlign="center" align="center" allowSort="false" >规划销售区域</div>
            <div field="instStatus" width="70" headerAlign="center" align="center" renderer="onStatusRenderer"
                 allowSort="false"><spring:message code="page.saleFileApplyList.name15" />
            </div>
            <div field="currentProcessUser" sortField="currentProcessUser" width="50" align="center"
                 headerAlign="center" allowSort="false"><spring:message code="page.saleFileApplyList.name16" />
            </div>
            <div field="currentProcessTask" sortField="currentProcessTask" width="80" align="center"
                 headerAlign="center" allowSort="false"><spring:message code="page.saleFileApplyList.name17" />
            </div>
            <div field="applyTime" sortField="applyTime" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" width="100"
                 headerAlign="center" allowSort="true"><spring:message code="page.saleFileApplyList.name18" />
            </div>
            <div field="reason" width="60" headerAlign="center" align="center" allowSort="false"><spring:message code="page.saleFileApplyList.name19" /></div>
            <div field="INST_ID_" visible="false"></div>
            <div field="taskId" visible="false"></div>
            <div field="processTask" visible="false"></div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var applyListGrid = mini.get("applyListGrid");
    var currentUserId = "${currentUser.userId}";
    var applyTypeList = getDics("YDGZSPLX");
    var applyType = "${applyType}";
    var typeList = getDics("saleFile_WJFL");
    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var applyId = record.id;
        var instId = record.INST_ID_;
        var applyType = record.applyType;
        var s = '<span  title=' + saleFileApplyList_name + ' onclick="detailApply(\'' + applyId + '\',\'' + record.instStatus + '\',\'' + applyType + '\')">' + saleFileApplyList_name + '</span>';
        if (record.instStatus == 'DRAFTED') {
            if (record.CREATE_BY_ == currentUserId) {
                s += '<span  title=' + saleFileApplyList_name1 + ' onclick="editApplyRow(\'' + applyId + '\',\'' + instId + '\',\'' + applyType + '\')">' + saleFileApplyList_name1 + '</span>';
            } else {
                s += '<span  title=' + saleFileApplyList_name1 + ' style="color: silver">' + saleFileApplyList_name1 + '</span>';
            }

        } else {
            if (record.processTask) {
                s += '<span  title=' + saleFileApplyList_name2 + ' onclick="doApplyTask(\'' + record.taskId + '\',\'' + applyType + '\')">' + saleFileApplyList_name2 + '</span>';
            } else {
                s += '<span  title=' + saleFileApplyList_name2 + ' style="color: silver">' + saleFileApplyList_name2 + '</span>';
            }
        }
        if (record.CREATE_BY_ == currentUserId && (record.instStatus == 'DRAFTED' || record.instStatus == 'DISCARD_END')) {
            s += '<span  title=' + saleFileApplyList_name3 + ' onclick="removeApply(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">' + saleFileApplyList_name3 + '</span>';
        } else {
            s += '<span  title=' + saleFileApplyList_name3 + ' style="color: silver">' + saleFileApplyList_name3 + '</span>';
        }
        return s;
    }

    function onUseStatusRenderer(e) {
        var record = e.record;
        var useStatus = record.useStatus;
        if (useStatus == 'yes') {
            return '<span>已使用</span>';
        } else {
            return '<span>未使用</span>';
        }
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

    function onApplyType(e) {
        var record = e.record;
        var applyType = record.applyType;
        var resultText = '';
        for (var i = 0; i < applyTypeList.length; i++) {
            if (applyTypeList[i].key_ == applyType) {
                resultText = applyTypeList[i].text;
                break
            }
        }
        return resultText;
    }
</script>
<redxun:gridScript gridId="applyListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
