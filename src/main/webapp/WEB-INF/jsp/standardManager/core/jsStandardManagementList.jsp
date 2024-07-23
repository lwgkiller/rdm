<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>技术标准制修订管理列表</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/standardManager/jsStandardManagementList.js?version=${static_res_version}"
            type="text/javascript"></script>

</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-left:15px;margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.jsStandardManagementList.name1" />: </span>
                    <input class="mini-textbox" name="standardName"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.jsStandardManagementList.name2" />: </span>
                    <input class="mini-textbox" name="standardLeaderName"/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto"><spring:message code="page.jsStandardManagementList.name3" />:</span>
                    <input class="mini-textbox" name="DeptName"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.jsStandardManagementList.name4" />: </span>
                    <input class="mini-textbox" name="standardPeopleName"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.jsStandardManagementList.name5" />: </span>
                    <input class="mini-textbox" name="taskName"/>
                </li>
                <li class="liBtn">
					<span class="unfoldBtn" onclick="no_more(this,'moreBox')">
						<em><spring:message code="page.jsStandardManagementList.name6" /></em>
						<i class="unfoldIcon"></i>
					</span>
                </li>

                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()"><spring:message code="page.jsStandardManagementList.name7" /></a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()"><spring:message code="page.jsStandardManagementList.name8" /></a>
                    <div style="display: inline-block" class="separator"></div>
                    <a class="mini-button" id="bzxdAddApply" style="margin-right: 5px" plain="true"
                       onclick="addApply()"><spring:message code="page.jsStandardManagementList.name9" /></a>
                    <a class="mini-button btn-red" id="bzxdRemoveApply" style="margin-right: 5px" plain="true" onclick="removeApply()"><spring:message code="page.jsStandardManagementList.name10" /></a>
                </li>
            </ul>
            <div id="moreBox">
                <ul>
                    <li style="margin-left:15px;margin-right: 15px"><span class="text" style="width:auto"><spring:message code="page.jsStandardManagementList.name11" />: </span>
                        <input id="instStatus" name="instStatus" class="mini-combobox" style="width:150px;"
                               textField="value" valueField="key" emptyText="<spring:message code="page.jsStandardManagementList.name12" />..."
                               required="false" allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.jsStandardManagementList.name12" />..."
                               data="[ {'key' : 'DRAFTED','value' : '草稿'},{'key' : 'RUNNING','value' : '运行中'},{'key' : 'SUCCESS_END','value' : '成功结束'},{'key' : 'DISCARD_END','value' : '作废'},{'key' : 'ABORT_END','value' : '异常中止结束'},{'key' : 'PENDING','value' : '挂起'}]"
                        />
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto"><spring:message code="page.jsStandardManagementList.name13" />: </span>
                        <input class="mini-textbox" name="feedbackId"/>
                    </li>
                </ul>
            </div>
        </form>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="standardListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         url="${ctxPath}/standardManager/core/standardManagement/jsStandardList.do"
         idField="applyId" allowCellWrap="true"
         multiSelect="true" showColumnsMenu="false" sizeList="[20,50,100,200]" pageSize="20" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="25"></div>
            <div name="action" cellCls="actionIcons" width="110" headerAlign="center" align="center"
                 renderer="onActionRenderer" cellStyle="padding:0;"><spring:message code="page.jsStandardManagementList.name14" />
            </div>
            <div field="rwly" width="100" headerAlign="center" align="center" allowSort="false">任务来源
            </div>
            <div field="feedbackId" width="150" headerAlign="center" align="center" allowSort="false" renderer="jumpToStandardDetail"><spring:message code="page.jsStandardManagementList.name15" /></div>
            <div field="jsxmId" width="150" headerAlign="center" align="center" allowSort="false" visible="false">jsxmId </div>
            <div field="jsbzzcId" width="150" headerAlign="center" align="center" allowSort="false" visible="false">jsbzzcId </div>
            <div field="otherId" width="150" headerAlign="center" align="center" allowSort="false" visible="false">otherId </div>
            <div field="standardName" sortField="standardName" width="200" headerAlign="center" align="center"
                 allowSort="true"><spring:message code="page.jsStandardManagementList.name1" />
            </div>

            <div field="standardLeaderName" width="65" headerAlign="center" align="center" allowSort="true"
                 renderer="onSaleStatus"><spring:message code="page.jsStandardManagementList.name16" />
            </div>
            <div field="DeptName" width="100" headerAlign="center" align="center" allowSort="true"renderer="onSaleStatus"><spring:message code="page.jsStandardManagementList.name17" />
            </div>
            <div field="standardPeopleName"  width="75" headerAlign="center" align="center"
                 allowSort="true"><spring:message code="page.jsStandardManagementList.name18" />
            </div>
            <div field="instStatus" width="70" headerAlign="center" align="center" allowSort="true"
                 renderer="onStatusRenderer"><spring:message code="page.jsStandardManagementList.name19" />
            </div>
            <div field="allTaskUserNames"  width="130" align="center"
                 headerAlign="center" allowSort="false"><spring:message code="page.jsStandardManagementList.name20" />
            </div>
            <div field="taskName" width="70" align="center" headerAlign="center"><spring:message code="page.jsStandardManagementList.name21" /></div>
            <div width="40" align="center" headerAlign="center" renderer="onRiskRenderer"><spring:message code="page.jsStandardManagementList.name22" /></div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var standardListGrid = mini.get("standardListGrid");
    var currentUserId = "${currentUser.userId}";
    var isZJConform = ${isZJConform};
    var isBzrzs = ${isBzrzs};
    var isFGLD = ${isFGLD};
    var isJszxsz = ${isJszxsz};
    var canAddApply=${canAddApply};
    var currentUserNo="${currentUserNo}";
    var isZfAdmin = "${isZfAdmin}";


    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var applyId = record.id;
        var instId = record.instId;
        var suser =record.yjUserIds;
        var s = '';
        if(record.CREATE_BY_ == currentUserId || record.standardLeaderId ==currentUserId || record.standardPeopleId == currentUserId || (record.mainDraftIds&&record.mainDraftIds.includes(currentUserId))
            ||(suser && suser.indexOf(currentUserId)!=-1) || isBzrzs || isFGLD || isJszxsz || currentUserNo=='admin'){
            s += '<span  title=' + jsStandardManagementList_mx + ' onclick="detailStandardRow(\'' + applyId +'\',\''+record.instStatus+ '\')">' + jsStandardManagementList_mx + '</span>';
        }else {
            s += '<span  title=' + jsStandardManagementList_mx + ' style="color: silver">' + jsStandardManagementList_mx + '</span>';
        }

        if (record.instStatus == 'DRAFTED') {
            if (record.CREATE_BY_ == currentUserId) {
                s += '<span  title=' + jsStandardManagementList_bj + ' onclick="editApplyRow(\'' + applyId + '\',\'' + instId + '\')">' + jsStandardManagementList_bj + '</span>';
            } else {
                s += '<span  title=' + jsStandardManagementList_bj + ' style="color: silver">' + jsStandardManagementList_bj + '</span>';
            }

        } else {
            if (record.myTaskId) {
                s += '<span  title=' + jsStandardManagementList_bl + ' onclick="doApplyTask(\'' + record.myTaskId + '\')">' + jsStandardManagementList_bl + '</span>';
            }
        }
        if(record.CREATE_BY_ == currentUserId && record.instStatus == 'DRAFTED') {
            s += '<span  title=' + jsStandardManagementList_sc + ' onclick="removeApply(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">' + jsStandardManagementList_sc + '</span>';
        } else {
            s += '<span  title=' + jsStandardManagementList_sc + ' style="color: silver">' + jsStandardManagementList_sc + '</span>';
        }
        if(record.standardLeaderId == currentUserId || currentUserNo=='admin') {
            var instId=record.instId;
            if(!instId) {
                instId='';
            }
            s += '<span  title=' + jsStandardManagementList_lccb + ' onclick="taskRemind(\'' + instId + '\')">' + jsStandardManagementList_lccb + '</span>';
        }
        if (status != "SUCCESS_END" && (isZfAdmin == "true"||currentUserId=="1")) {
            s += '<span  title="作废" onclick="discardInst(\'' + record.instId + '\')">作废</span>';
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

    function typeRender(e) {
        var record=e.record;
        var feedbackType=record.feedbackType;
        if(feedbackType=='need') {
            return "技术标准需求反馈";
        } else if(feedbackType=='problem'){
            return "标准使用问题反馈";
        }
    }

    function jumpToStandardDetail(e) {
        var record = e.record;
        var feedbackId = record.feedbackId;
        if(!feedbackId) {
            feedbackId='';
        }
        var s = '<a href="#" style="color:#44cef6;text-decoration:underline;" onclick="detailApplyRow(\'' + feedbackId +'\',\''+'SUCCESS_END'+ '\')">'+feedbackId+'</a>';
        return s;
    }

    // function jumpToStandardDetailRamke(e) {
    //     var record = e.record;
    //     var feedbackId = record.feedbackId;
    //     var jsxmId = record.jsxmId;
    //     var jsbzzcId = record.jsbzzcId;
    //     var otherId = record.otherId;
    //     if(!feedbackId) {
    //         feedbackId='';
    //     }
    //     var rwly = record.rwly;
    //     if (rwly == "技术项目") {
    //         var s = '<a href="#" style="color:#44cef6;text-decoration:underline;" onclick="jsxmdetailApplyRow(\'' + jsxmId +'\',\''+'SUCCESS_END'+ '\')">'+jsxmId+'</a>';
    //
    //     }else if (rwly == "标准信息反馈") {
    //         var s = '<a href="#" style="color:#44cef6;text-decoration:underline;" onclick="detailApplyRow(\'' + feedbackId +'\',\''+'SUCCESS_END'+ '\')">'+feedbackId+'</a>';
    //
    //     }else if (rwly == "技术标准自查") {
    //         var s = '<a href="#" style="color:#44cef6;text-decoration:underline;" onclick="jsbzdetailApplyRow(\'' + jsbzzcId +'\',\''+'SUCCESS_END'+ '\')">'+jsbzzcId+'</a>';
    //     }else if (rwly == "其他") {
    //         var s = '<div >'+otherId+'</div>'
    //     }
    //     return s;
    // }
    function onRiskRenderer(e) {
        var record = e.record;
        var hasRisk = record.hasRisk;

        var color='';
        var title='';
        if(hasRisk==1) {
            color='#fb0808';
            title='项目延误';
        } else if(hasRisk==2) {
            color='#32CD32';
            title='项目未延误';
        }else if(hasRisk==3) {
            color='#cccccc';
            title='项目未启动或无状态';
        }else if (hasRisk==0){
            color='#32CD32';
            title='没有风险';
        }
        var s = '<span title= "'+title+'" style="display:inline-block;margin-right:5px;border-radius:10px;width:9px;height:9px;background-color:' + color + '"></span>';
        return s;
    }

    function discardInst(instId) {

        if (!confirm('流程终止后不可恢复，确定要作废该流程吗?')) {
            return;
        }
        _SubmitJson({
            url: __rootPath + '/bpm/core/bpmInst/discardInst.do',
            data: {
                instId: instId
            },
            method: 'POST',
            success: function () {
                searchFrm();
            }
        })
    }
</script>
<redxun:gridScript gridId="standardListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
