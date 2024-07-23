<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>帖子列表</title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<div class="mini-fit" style="height:  100%;">
    <div id="listGrid" class="mini-datagrid" allowResize="true" style="height:  100%;" sortField="CREATE_TIME_"
         sortOrder="desc"
         url="${ctxPath}/rdmZhgl/core/bbs/report/bbsList.do" idField="id" showPager="false"
         multiSelect="true" showColumnsMenu="false" sizeList="[15,20,50,100,200]" allowAlternating="true" pageSize="15"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="indexcolumn" align="center" headerAlign="center" width="50px">序号</div>
            <div field="plateName" name="plateName" width="160px" headerAlign="center" align="center" allowSort="false">
                板块
            </div>
            <div field="title" name="title" width="320px" headerAlign="center" align="left" allowSort="false"
                 renderer="onMessageTitleRenderer">标题
            </div>
            <div field="bbsType" name="bbsType" width="100px" headerAlign="center" align="center"
                 allowSort="false" renderer="onBbsType">帖子分类
            </div>
            <div field="direction" name="direction" width="180px" headerAlign="center" align="center" allowSort="false">
                主题
            </div>
            <div field="urgency" name="urgency" width="100px" headerAlign="center" align="center"
                 allowSort="false" renderer="onBbsUrgency">紧急程度
            </div>
            <div field="closed" name="closed" width="100px" headerAlign="center" align="center" allowSort="false"
                 renderer="onClosed">是否关帖
            </div>
            <div field="isAdopt" name="isAdopt" width="100px" headerAlign="center" align="center" allowSort="false"
                 renderer="onAdopt">是否采纳
            </div>
            <div field="planFinishDate" width="160px" headerAlign="center" align="center" allowSort="true">计划完成时间</div>
            <div field="planContent" width="220px" headerAlign="center" align="center" allowSort="true">计划内容</div>
            <div field="unAdoptReason" width="220px" headerAlign="center" align="center" allowSort="true">不采纳原因</div>
            <div field="conformDate" width="160px" headerAlign="center" align="center" allowSort="true">结果确认时间</div>
            <div field="techOpinion" width="220px" headerAlign="center" align="center" allowSort="true">技术管理部结束时的意见</div>
            <div field="satisfaction" name="satisfaction" width="100px" headerAlign="center" align="center"
                 allowSort="false" renderer="onSatisfaction">满意度
            </div>
            <div field="opinion" name="opinion" width="200px" headerAlign="center" align="center" allowSort="false">关帖意见
            </div>
            <div field="totalDiscussNum" name="totalDiscussNum" width="100px" headerAlign="center" align="center"
                 allowSort="false">回帖数量
            </div>
            <div field="publisher" width="120px" headerAlign="center" align="center" allowSort="false">发帖人</div>
            <div field="CREATE_TIME_" width="220px" headerAlign="center" align="center" allowSort="true">发帖时间</div>
            <div field="closeDate" width="220px" headerAlign="center" align="center" allowSort="true" dateFormat="yyyy-MM-dd HH:mm:ss" >关帖时间</div>
        </div>
    </div>
</div>
<form id="excelForm" action="${ctxPath}/rdmZhgl/core/bbs/exportExcel.do" method="post" target="excelIFrame">
    <input type="hidden" name="pageIndex" id="pageIndex"/>
    <input type="hidden" name="pageSize" id="pageSize"/>
    <input type="hidden" name="filter" id="filter"/>
</form>
<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var currentUserId = "${currentUser.userId}";
    var listGrid = mini.get("listGrid");
    var SFList = getDics("YESORNO");
    var satisfactionList = getDics("satisfaction");
    var bbsTypeList = getDics("BBSType");
    var bbsUrgencyList = getDics("BBSUrgency");
    var paramJson = ${paramJson};
    listGrid.frozenColumns(0, 3);
    $(function () {
        searchList();
    })
    function searchList() {
        var paramArray = [{name: "barName", value: paramJson.barName},
            {name: "startTime", value: paramJson.startTime},
            {name: "endTime", value: paramJson.endTime},
            {name: "reportType", value: paramJson.reportType},
            {name: "seriesName", value: paramJson.seriesName}
        ];
        var data = {};
        data.filter = mini.encode(paramArray);
        listGrid.load(data);
    }

    function onMessageTitleRenderer(e) {
        var record = e.record;
        var title = record.title;
        var bbsType = record.bbsType;
        if(bbsType=='gjta'){
            var applyId = record.id;
            var instId = record.INST_ID_;
            var s = '';
            if (record.instStatus == 'DRAFTED') {
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
    function exportExcel(){
        var params=[];
        var parent=$(".search-form");
        var inputAry=$("input",parent);
        inputAry.each(function(i){
            var el=$(this);
            var obj={};
            obj.name=el.attr("name");
            if(!obj.name) return true;
            obj.value=el.val();
            params.push(obj);
        });
        $("#filter").val(mini.encode(params));
        var excelForm = $("#excelForm");
        excelForm.submit();
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
    function onAdopt(e) {
        var record = e.record;
        var resultValue = record.isAdopt;
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
    function onBbsUrgency(e) {
        var record = e.record;
        var resultValue = record.urgency;
        var resultText = '';
        for (var i = 0; i < bbsUrgencyList.length; i++) {
            if (bbsUrgencyList[i].key_ == resultValue) {
                resultText = bbsUrgencyList[i].text;
                break
            }
        }
        return resultText;
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

    function seeMessage(id) {
        window.open(jsUseCtxPath + "/rdmZhgl/core/bbs/viewPage.do?id=" + id);
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
</script>
<redxun:gridScript gridId="listGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
