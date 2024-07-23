<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>标准需求反馈列表管理</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/standardManager/jsStandardDemandList.js?version=${static_res_version}"
            type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-left:15px;margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.jsStandardDemandList.name1" />: </span>
                    <input class="mini-textbox" name="applyId"/>
                </li>
                <li style="margin-left:15px;margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.jsStandardDemandList.name2" />: </span>
                    <input id="feedbackType" name="feedbackType" class="mini-combobox" style="width:98%;"
                           textField="key" valueField="value"  allowInput="false" showNullItem="true"
                           required="false" allowInput="false" emptyText="<spring:message code="page.jsStandardDemandList.name3" />..." nullItemText="<spring:message code="page.jsStandardDemandList.name3" />..."
                           data="[ {'key' : '技术标准需求反馈','value' : 'need'},{'key' : '标准使用问题反馈','value' : 'problem'}]"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.jsStandardDemandList.name4" />: </span>
                    <input class="mini-textbox" name="applyUserName"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.jsStandardDemandList.name5" />: </span>
                    <input id="applyUserDept" name="applyUserDeptId" class="mini-dep rxc" plugins="mini-dep"
                           style="width:98%;height:34px"
                           allowinput="false" textname="depName" single="true" initlogindep="false"/>
                </li>

                <li class="liBtn">
					<span class="unfoldBtn" onclick="no_more(this,'moreBox')">
						<em><spring:message code="page.jsStandardDemandList.name6" /></em>
						<i class="unfoldIcon"></i>
					</span>
                </li>

                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()"><spring:message code="page.jsStandardDemandList.name7" /></a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()"><spring:message code="page.jsStandardDemandList.name8" /></a>
                    <div style="display: inline-block" class="separator"></div>
                    <a class="mini-button" style="margin-right: 5px" plain="true"
                       onclick="addApply()"><spring:message code="page.jsStandardDemandList.name9" /></a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="removeApply()"><spring:message code="page.jsStandardDemandList.name10" /></a>
                </li>
            </ul>
            <div id="moreBox">
                <ul>
                    <li style="margin-left:15px;margin-right: 15px"><span class="text"
                                                                          style="width:auto"><spring:message code="page.jsStandardDemandList.name11" />: </span>
                        <input id="instStatus" name="instStatus" class="mini-combobox" style="width:150px;"
                               textField="value" valueField="key" emptyText="<spring:message code="page.jsStandardDemandList.name3" />..."
                               required="false" allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.jsStandardDemandList.name3" />..."
                               data="[ {'key' : 'DRAFTED','value' : '草稿'},{'key' : 'RUNNING','value' : '运行中'},{'key' : 'SUCCESS_END','value' : '成功结束'},{'key' : 'DISCARD_END','value' : '作废'},{'key' : 'ABORT_END','value' : '异常中止结束'},{'key' : 'PENDING','value' : '挂起'}]"
                        />
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto"><spring:message code="page.jsStandardDemandList.name12" /> </span>:<input name="applyTimeStart"
                                                                                    class="mini-datepicker"
                                                                                    format="yyyy-MM-dd"
                                                                                    style="width:100px"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text-to" style="width:auto"><spring:message code="page.jsStandardDemandList.name13" />: </span><input name="applyTimeEnd"
                                                                                  class="mini-datepicker"
                                                                                  format="yyyy-MM-dd"
                                                                                  style="width:100px"/>
                    </li>
                    <li style="margin-left:15px;margin-right: 15px">
                        <span class="text" style="width:auto"><spring:message code="page.jsStandardDemandList.name14" />: </span>
                        <input id="acceptStatus" name="acceptStatus" class="mini-combobox" style="width:98%;"
                               textField="key" valueField="value"  allowInput="false" showNullItem="true"
                               required="false" allowInput="false" emptyText="<spring:message code="page.jsStandardDemandList.name3" />..." nullItemText="<spring:message code="page.jsStandardDemandList.name3" />..."
                               data="[ {'key' : '采纳','value' : 'all'},{'key' : '部分采纳','value' : 'part'},{'key' : '不采纳','value' : 'no'}]"/>
                    </li>
                </ul>
            </div>
        </form>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="applyListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         url="${ctxPath}/standardManager/core/standardDemand/jsDemandList.do"
         idField="applyId"
         multiSelect="true" showColumnsMenu="false" sizeList="[20,50,100,200]" pageSize="20" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div name="action" cellCls="actionIcons" width="130" headerAlign="center" align="center"
                 renderer="onActionRenderer" cellStyle="padding:0;"><spring:message code="page.jsStandardDemandList.name15" />
            </div>
            <div field="applyId" sortField="applyId" width="130" headerAlign="center" align="center" allowSort="true">
                <spring:message code="page.jsStandardDemandList.name1" />
            </div>
            <div field="feedbackType" renderer="typeRender" sortField="feedbackType" width="130" headerAlign="center" align="center" allowSort="true">
                <spring:message code="page.jsStandardDemandList.name2" />
            </div>
            <div field="applyUserName" sortField="applyUserName" width="70" headerAlign="center" align="center"
                 allowSort="true"><spring:message code="page.jsStandardDemandList.name4" />
            </div>
            <div field="applyUserDeptName" sortField="applyUserDeptName" width="110" headerAlign="center" align="center"
                 allowSort="true"><spring:message code="page.jsStandardDemandList.name5" />
            </div>
            <div field="doDeptNames" width="280" headerAlign="center" align="center" allowSort="false"><spring:message code="page.jsStandardDemandList.name19" /></div>
            <div field="acceptStatus" width="70" headerAlign="center" align="center" renderer="acceptStatusRenderer"
                 allowSort="false"><spring:message code="page.jsStandardDemandList.name14" />
            </div>
            <div field="instStatus" width="70" headerAlign="center" align="center" renderer="onStatusRenderer"
                 allowSort="false"><spring:message code="page.jsStandardDemandList.name16" />
            </div>
            <div field="currentProcessUser" sortField="currentProcessUser" width="70" align="center"
                 headerAlign="center" allowSort="false"><spring:message code="page.jsStandardDemandList.name17" />
            </div>
            <div field="applyTime" sortField="applyTime" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" width="100"
                 headerAlign="center" allowSort="true"><spring:message code="page.jsStandardDemandList.name18" />
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var applyListGrid = mini.get("applyListGrid");
    var currentUserId = "${currentUser.userId}";
    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var applyId = record.applyId;
        var instId = record.INST_ID_;
        var s = '<span  title=' + jsStandardDemandList_mx + ' onclick="detailApplyRow(\'' + applyId +'\',\''+record.instStatus+ '\')">' + jsStandardDemandList_mx + '</span>';
        if (record.instStatus == 'DRAFTED') {
            if (record.CREATE_BY_ == currentUserId) {
                s += '<span  title=' + jsStandardDemandList_bj + ' onclick="editApplyRow(\'' + applyId + '\',\'' + instId + '\')">' + jsStandardDemandList_bj + '</span>';
            } else {
                s += '<span  title=' + jsStandardDemandList_bj + ' style="color: silver">' + jsStandardDemandList_bj + '</span>';
            }

        } else {
            if (record.myTaskId) {
                s += '<span  title=' + jsStandardDemandList_bl + ' onclick="doApplyTask(\'' + record.myTaskId + '\')">' + jsStandardDemandList_bl + '</span>';
            } else {
                s += '<span  title=' + jsStandardDemandList_bl + ' style="color: silver">' + jsStandardDemandList_bl + '</span>';
            }
        }
        if(record.CREATE_BY_ == currentUserId && (record.instStatus == 'DRAFTED'||record.instStatus == 'DISCARD_END')) {
            s += '<span  title=' + jsStandardDemandList_sc + ' onclick="removeApply(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">' + jsStandardDemandList_sc + '</span>';
        } else {
            s += '<span  title=' + jsStandardDemandList_sc + ' style="color: silver">' + jsStandardDemandList_sc + '</span>';
        }
        return s;
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

    function acceptStatusRenderer(e) {
        var record = e.record;
        var acceptStatus = record.acceptStatus;
        if(!acceptStatus) {
            return;
        }
        if(acceptStatus=='all') {
            return "采纳";
        }
        if(acceptStatus=='part') {
            return "部分采纳";
        }
        if(acceptStatus=='no') {
            return "不采纳";
        }
        return acceptStatus;
    }

    function typeRender(e) {
        var record=e.record;
        var feedbackType=record.feedbackType;
        if(feedbackType=='need') {
            return "技术标准需求反馈";
        } else if(feedbackType=='problem'){
            return "标准使用问题反馈";
        }
    }
</script>
<redxun:gridScript gridId="applyListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
