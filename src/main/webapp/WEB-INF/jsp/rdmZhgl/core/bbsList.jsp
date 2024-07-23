<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>帖子列表</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/rdmZhgl/bbsList.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px"><span class="text" style="width:auto"><spring:message code="page.bbsList.bt" />: </span>
                    <input name="title" class="mini-textbox rxc" allowInput="true" style="width:100%;"/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto"><spring:message code="page.bbsList.zt" />: </span>
                    <input name="direction" class="mini-textbox rxc" allowInput="true" style="width:100%;"/>
                </li>
                <li style="margin-right: 15px"><span class="text" style="width:auto"><spring:message code="page.bbsList.tzfl" />: </span>
                    <input id="bbsType" name="bbsType" class="mini-combobox rxc" plugins="mini-combobox"
                           style="width:100%;height:34px"  label="<spring:message code="page.bbsList.qxz" />：" onvaluechanged="searchFrm"
                           length="50"   allowinput="false" mwidth="100"
                           wunit="%" mheight="34" hunit="px"   shownullitem="true" multiSelect="false"
                           textField="text" valueField="key_" emptyText="<spring:message code="page.bbsList.qxz" />..."
                           url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=BBSType"
                           nullitemtext="<spring:message code="page.bbsList.qxz" />..." emptytext="<spring:message code="page.bbsList.qxz" />..."/>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()"><spring:message code="page.bbsList.cx" /></a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()"><spring:message code="page.bbsList.qkcx" /></a>
                    <div style="display: inline-block" class="separator"></div>
                    <a class="mini-button" plain="true" onclick="addForm()"><spring:message code="page.bbsList.ft" /></a>
                    <div style="display: inline-block" class="separator"></div>
                    <a class="mini-button" plain="true" style="" onclick="startGjtaApply()"><spring:message code="page.bbsList.gjtalc" /></a>
                </li>
            </ul>
        </form>
    </div>
</div>
<div class="mini-fit" style="height:  100%;">
    <div id="listGrid" class="mini-datagrid" allowResize="true" style="height:  100%;" sortField="UPDATE_TIME_"
         sortOrder="desc"
         url="${ctxPath}/rdmZhgl/core/bbs/list.do?plate=${plate}&model=${model}" idField="id" showPager="true"
         allowCellWrap="true" sortField="CREATE_TIME_" sortOrder="desc"
         multiSelect="false" showColumnsMenu="false" sizeList="[10,20,50,100,200]" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div type="indexcolumn" align="center" headerAlign="center" width="30"><spring:message code="page.bbsList.xh" /></div>
            <div field="title" name="title" width="220" headerAlign="center" align="center" allowSort="false"
                 renderer="onMessageTitleRenderer"><spring:message code="page.bbsList.bt" />
            </div>
            <div field="direction" name="direction" width="100" headerAlign="center" align="center" allowSort="false">
                <spring:message code="page.bbsList.zt" />
            </div>
            <div field="bbsType" name="bbsType" width="50" headerAlign="center" align="center"
                 allowSort="false" renderer="onBbsType"><spring:message code="page.bbsList.tzfl" />
            </div>
            <div field="publisher" width="60" headerAlign="center" align="center" allowSort="false"><spring:message code="page.bbsList.ftr" /></div>
            <div field="CREATE_TIME_" width="80" headerAlign="center" align="center" allowSort="true"><spring:message code="page.bbsList.ftsj" /></div>
            <div field="closed" name="closed" width="50" headerAlign="center" align="center" allowSort="false"
                 renderer="onClosed"><spring:message code="page.bbsList.sfyft" />
            </div>
            <div field="closeDate" width="120" headerAlign="center" align="center" allowSort="true"
                 dateFormat="yyyy-MM-dd HH:mm:ss" ><spring:message code="page.bbsList.gtsj" /></div>
            <div field="satisfaction" name="satisfaction" width="50" headerAlign="center" align="center"
                 allowSort="false" renderer="onSatisfaction"><spring:message code="page.bbsList.myd" />
            </div>
            <div name="action" cellCls="actionIcons" width="80" headerAlign="center" align="center"
                 renderer="onActionRenderer" cellStyle="padding:0;">
                <spring:message code="page.bbsList.cz" />
            </div>
        </div>
    </div>
</div>
<div id="closeBbsWindow" title="<spring:message code="page.bbsList.gbtz" />" class="mini-window" style="width:500px;height:200px;"
     showModal="true" showFooter="false" allowResize="true">
    <div class="topToolBar" style="text-align:right;">
        <a class="mini-button" onclick="closePost()"><spring:message code="page.bbsList.gt" /></a>
    </div>
    <div class="mini-fit" style="font-size: 14px">
        <form id="opinionForm" method="post">
            <input id="id" name="id" class="mini-hidden"/>
            <table cellspacing="1" cellpadding="0" class="table-detail column-six grey">
                <tr>
                    <td align="center" style="width: 10%"><spring:message code="page.bbsList.myd" />：<span style="color:red">*</span></td>
                    <td align="center" colspan="3" style="width: 99%">
                        <input id="satisfaction" name="satisfaction" class="mini-combobox rxc" plugins="mini-combobox"
                               style="width:100%;height:34px" label="<spring:message code="page.bbsList.myd" />:"
                               length="50" required
                               only_read="false" required="false" allowinput="false" mwidth="100"
                               wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                               textField="text" valueField="key_" emptyText="<spring:message code="page.bbsList.qxz" />..."
                               url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=satisfaction"
                               nullitemtext="<spring:message code="page.bbsList.qxz" />..." emptytext="<spring:message code="page.bbsList.qxz" />..." />
                    </td>
                </tr>
                <tr>
                    <td align="center" style="width: 10%"><spring:message code="page.bbsList.yj" />：<span style="color:red">*</span></td>
                    <td align="center" colspan="3"  style="width: 20%">
                        <input name="opinion" class="mini-textbox" required
                               allowinput="true" initcurtime="false" mwidth="100" wunit="%"
                               min="0" mheight="34" hunit="px" style="width:100%;height:34px"/>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>
<div id="movePostWindow" title=bbsList_yd class="mini-window" style="width:500px;height:200px;"
     showModal="true" showFooter="false" allowResize="true">
    <div class="topToolBar" style="text-align:right;">
        <a class="mini-button" onclick="removePost()"><spring:message code="page.bbsList.yt" /></a>
    </div>
    <div class="mini-fit" style="font-size: 14px">
        <form id="postForm" method="post">
            <input id="postId" name="postId" class="mini-hidden"/>
            <table cellspacing="1" cellpadding="0" class="table-detail column-six grey">
                <tr>
                    <td align="center" style="width: 10%"><spring:message code="page.bbsList.yddbk" />：</td>
                    <td align="center" colspan="3" style="width: 85%">
                        <input id="plate" name="plate" class="mini-combobox rxc" plugins="mini-combobox"
                               style="width:100%;height:34px" label="<spring:message code="page.bbsList.bk" />:"
                               length="50" required
                               only_read="false" required="false" allowinput="false" mwidth="100"
                               wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                               textField="name" valueField="key" emptyText="<spring:message code="page.bbsList.qxz" />..."
                               url="${ctxPath}/sys/core/sysMenuMgr/listAllSubSys.do?type=bbs"
                               nullitemtext="<spring:message code="page.bbsList.qxz" />..." emptytext="<spring:message code="page.bbsList.qxz" />..." />
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var currentUserId = "${currentUser.userId}";
    var listGrid = mini.get("listGrid");
    var plate = "${plate}";
    var model = "${model}";
    var closeBbsWindow = mini.get("closeBbsWindow");
    var opinionForm = new mini.Form("#opinionForm");
    var movePostWindow = mini.get("movePostWindow");
    var postForm = new mini.Form("#postForm");
    var SFList = getDics("YESORNO");
    var satisfactionList = getDics("satisfaction");
    var bbsTypeList = getDics("BBSType");

    function onMessageTitleRenderer(e) {
        var record = e.record;
        var title = record.title;
        var bbsType = record.bbsType;
        if(bbsType=='gjta'){
            var applyId = record.id;
            var instId = record.INST_ID_;
            var s = '';
            if (record.instStatus == 'DRAFTED'||record.instStatus == 'DISCARD_END') {
                if (record.CREATE_BY_ == currentUserId) {
                    return  '<a href="#" style="color:#0754f6;" onclick="editApplyRow(\'' + applyId + '\',\'' + instId + '\')">' + title + '</a>';
                }
            } else if(record.instStatus == 'RUNNING'){
                if (record.processTask) {
                    return  '<a href="#" style="color:#0754f6;" onclick="doApplyTask(\'' + record.taskId + '\')">' + title + '</a>';
                } else {
                    return  '<a href="#" style="color:#0754f6;" onclick="detailApply(\'' + applyId + '\',\'' + record.instStatus + '\')">' + title + '</a>';
                }
            }else{
                return  '<a href="#" style="color:#0754f6;" onclick="detailApply(\'' + applyId + '\',\'' + record.instStatus + '\')">' + title + '</a>';
            }
        }else{
            return '<a href="#" style="color:#0754f6;" onclick="seeMessage(\'' + record.id + '\')">' + title + '</a>';
        }
    }
    //编辑行数据流程（后台根据配置的表单进行跳转）
    function editApplyRow(applyId,instId) {
        var url=jsUseCtxPath+"/bpm/core/bpmInst/start.do?instId="+instId;
        var winObj = window.open(url);
        var loop = setInterval(function() {
            if(winObj.closed) {
                clearInterval(loop);
                searchFrm()
            }
        }, 1000);
    }
    //跳转到任务的处理界面
    function doApplyTask(taskId) {
        $.ajax({
            url:jsUseCtxPath+'/bpm/core/bpmTask/checkTaskLockStatus.do?taskId='+taskId,
            success:function (result) {
                if(!result.success){
                    top._ShowTips({
                        msg:result.message
                    });
                }else{
                    var url=jsUseCtxPath+'/bpm/core/bpmTask/toStart.do?taskId='+taskId;
                    var winObj = window.open(url);
                    var loop = setInterval(function() {
                        if(winObj.closed) {
                            clearInterval(loop);
                            searchFrm()
                        }
                    }, 1000);
                }
            }
        })
    }
    //明细 的点击查看方法
    function detailApply(id,status) {
        var action = "detail";
        var url=jsUseCtxPath+"/rdmZhgl/core/bbs/applyDetail.do?action="+action+"&id=" + id;
        var winObj = window.open(url);
    }
    function onClosed(e) {
        var record = e.record;
        var resultValue = record.closed;
        var resultText = '';
        for (var i = 0; i < SFList.length; i++) {
            if (SFList[i].key_ == resultValue) {
                resultText = SFList[i].text;
                break
            }
        }
        return resultText;
    }
    function onBbsType(e) {
        var record = e.record;
        var resultValue = record.bbsType;
        var resultText = '';
        for (var i = 0; i < bbsTypeList.length; i++) {
            if (bbsTypeList[i].key_ == resultValue) {
                resultText = bbsTypeList[i].text;
                break
            }
        }
        return resultText;
    }
    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var id = record.id;
        var creator = record.CREATE_BY_;
        var closed = record.closed;
        var bbsType = record.bbsType;
        var s = '';
        if (currentUserId == creator || currentUserId == "1") {
            s += '<span  title='+ bbsList_bj +' onclick="editForm(\'' + id + '\')"><spring:message code="page.bbsList.bj" /></span>';
            if(bbsType=='gjta'){
                var instId = record.INST_ID_;
                if (record.instStatus == 'DRAFTED') {
                      s += '<span  title='+ bbsList_sc +' onclick="delRowByID(\'' + id + '\')"><spring:message code="page.bbsList.sc" /></span>';
                } else {
                    if(!instId){
                        s += '<span  title='+ bbsList_sc +' onclick="delRowByID(\'' + id + '\')"><spring:message code="page.bbsList.sc" /></span>';
                    }else{
                        s += '<span  title='+ bbsList_sc +' style="color: silver""><spring:message code="page.bbsList.sc" /></span>';
                    }
                }
            }else{
                s += '<span  title='+ bbsList_sc +' onclick="delRowByID(\'' + id + '\')"><spring:message code="page.bbsList.sc" /></span>';
            }
            if (closed == '0') {
                s += '<span  title='+ bbsList_gt +' onclick="closed(\'' + id + '\')"><spring:message code="page.bbsList.gt" /></span>';
            } else if (closed == '1') {
                s += '<span  title=bbsList_jh onclick="openPost(\'' + id + '\')"><spring:message code="page.bbsList.jh" /></span>';
            }
            s += '<span  title='+ bbsList_yd +' onclick="movePost(\'' + id + '\')"><spring:message code="page.bbsList.yd" /></span>';
        } else {
            s += '<span  title='+ bbsList_bj +' style="color: silver"><spring:message code="page.bbsList.bj" /></span>';
            s += '<span  title='+ bbsList_sc +' style="color: silver""><spring:message code="page.bbsList.sc" /></span>';
        }
        return s;
    }

    function seeMessage(id) {
        window.open(jsUseCtxPath + "/rdmZhgl/core/bbs/viewPage.do?id=" + id);
    }

    function onSatisfaction(e) {
        var record = e.record;
        var resultValue = record.satisfaction;
        var resultText = '';
        for (var i = 0; i < satisfactionList.length; i++) {
            if (satisfactionList[i].key_ == resultValue) {
                resultText = satisfactionList[i].text;
                if(resultValue=='fcmy') {
                    resultText='<span style="color:#1fb1e8">'+resultText+'</span>';
                }
                if(resultValue=='my') {
                    resultText='<span style="color:#1fe838">'+resultText+'</span>';
                }
                if(resultValue=='yb') {
                    resultText='<span class="orange">'+resultText+'</span>';
                }
                if(resultValue=='bmy') {
                    resultText='<span class="red">'+resultText+'</span>';
                }
                break
            }
        }
        return resultText;
    }
</script>
<redxun:gridScript gridId="listGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
