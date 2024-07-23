var solutionList;
$(function () {
    getSolutions();
    //查询标准体系类别
    querySystemCategoryInfos();
    //查询标准类别下拉框
    querySelectInfos();
    //加载代办信息
    queryTaskTodoList('');
    //查询待办数量
    getTaskTODONum();
    //查询运行中的我的申请
    queryRunningMyApply();
    //查询宣贯通知
    queryMsgList();
    //查询模板
    queryTemplate();
    //设置链接地址
    setLink();
});

function getSolutions() {
    $.ajax({
        url: __rootPath+"/standardManager/core/standard/getSolutions.do",
        type: 'get',
        contentType: 'application/json',
        success: function (data) {
            if(data){
                solutionList = mini.decode(data);
            }
        },
        failture:function(){
        }
    });
}

function filterDbType(_type) {
    for(var i=0;i<solutionList.length;i++){
        if(solutionList[i].id==_type){
            return solutionList[i].text;
        }
    }
}

function taskTypeChange() {
    let taskType = mini.get("Q_solutionKey_S_EQ").getValue();
    queryTaskTodoList(taskType);
}
function moreDate(tableName) {
    if(tableName=='message'){
        window.open(__rootPath+"/standardManager/core/standardMessage/management.do")
    }
}

function refreshDeskHomeTab(tabName) {
    if(tabName=='task') {
        taskTypeChange();
    } else if(tabName=='apply') {
        queryRunningMyApply();
    } else if(tabName=='message') {
        queryMsgList();
    } else if(tabName=='template') {
        queryTemplate();
    }
}

function setLink() {
    var newRow={linkName:'徐工集团标准信息系统',url:'http://10.80.1.2:8060/'};
    linkListGrid.addRow(newRow, 0);
    var newRow2={linkName:'徐工集团六西格玛管理系统',url:'http://10.1.1.25:88/'};
    linkListGrid.addRow(newRow2, 1);
}

function queryTemplate() {
    templateListGrid.load();
}

function queryMsgList() {
    standardMsgListGrid.load();
}

function queryRunningMyApply() {
    var params=[];
    params.push({name:'CREATE_BY_',value:currentUserId});
    params.push({name:'instStatus',value:'RUNNING'});
    var data={};
    data.pageIndex=0;
    data.pageSize=10000;
    data.sortField='applyCategoryId';
    data.sortOrder='asc';
    data.filter=mini.encode(params);
    applyListGrid.load(data);
}

function queryTaskTodoList(taskType) {
    var data={};
    data.pageIndex=0;
    data.pageSize=10000;
    data.sortField=grid.getSortField();
    data.sortOrder=grid.getSortOrder();
    if(taskType){
        data.Q_solutionKey_S_EQ =taskType;
    }
    grid.load(data,function (data) {
        if (typeof(eval("filterTaskTodoResult")) == "function") {
            filterTaskTodoResult(data);
        }
    });
}

function filterTaskTodoResult(data) {
    let solKeys = getProperties("standardTaskKey").split(",");
    var rows= grid.findRows(function(row){
        if(!filterTask(row.solId,row.solKey,solKeys)) return true;
    })
    grid.removeRows(rows)
}

//过滤代办
function filterTask(solId,solKey,arry) {
    try {
        if(solKey==null||solKey==""){
            solKey = getBpmSolution(solId);
        }
        for(let i=0;i<arry.length;i++){
            if(solKey == arry[i]){
                return true;
            }
        }
    }catch (e) {
        return false
    }
    return false
}
function getBpmSolution(solId) {
    let solKey = "";
    let postData = {"solId":solId};
    $.ajax({
        async:false,
        url: __rootPath + '/xcmgProjectManager/core/xcmgProjectAbolish/getBpmSolution.do',
        type: 'POST',
        data:mini.encode(postData),
        contentType: 'application/json',
        success: function (returnData) {
            if (returnData) {
                solKey = returnData.KEY_;
            }
        }
    });
    return solKey;
}
//获取消息个数
function getTaskTODONum() {
    $.ajax({
        url: jsUseCtxPath+"/standardManager/report/standard/queryTaskTODONum.do",
        type: 'get',
        contentType: 'application/json',
        success: function (data) {
            if(data){
                $("#taskNum").html(data.task);
            }
        }
    });
}

/**
 * 代办事项
 * */
function onActionRenderer(e) {
    var record = e.record;
    var pkId = record.pkId;
    var locked = record.locked;
    var uid=record._uid;
    var s= '<span style="color: #409EFF;cursor: pointer " title="办理" onclick="checkAndHandTask(\'' + uid + '\')">办理</span>';
    if(locked){
        s+= '<span style="color: green;cursor: pointer " title="释放任务" onclick="releaseTask(\'' + uid + '\')">释放任务</span>';
    }
    return s;
}
/**
 * 判断任务锁定状态
 * @param uid
 * @param fromMgr 从管理页面进入
 * @param fromPortal 从门户进入
 */
function checkAndHandTask(uid,fromMgr,fromPortal,colId){
    if(fromPortal){
        $.ajax({
            url:jsUseCtxPath+'/bpm/core/bpmTask/checkTaskLockStatus.do?taskId='+uid,
            async:false,
            success:function (result) {
                if(!result.success){
                    top._ShowTips({
                        msg:result.message
                    });
                    var _inp = $('.colId_'+colId).find("input[id='Refresh']");
                    _inp.click();
                }else{
                    handStandardHomeTask(uid,false,fromPortal);
                }
            }

        })
    }else{
        var row=grid.getRowByUID(uid);
        $.ajax({
            url:jsUseCtxPath+'/bpm/core/bpmTask/checkTaskLockStatus.do?taskId='+row.id + '&fromMgr=' + fromMgr,
            async:false,
            success:function (result) {
                if(!result.success){
                    top._ShowTips({
                        msg:result.message
                    });
                    //加载代办信息
                    queryTaskTodoList();
                    //查询待办数量
                    getTaskTODONum();
                }else{
                    handStandardHomeTask(uid,fromMgr,false);
                }
            }

        })
    }
}
/**
 * 释放任务
 */
function releaseTask(uid){
    var row=grid.getRowByUID(uid);

    var url=jsUseCtxPath+'/bpm/core/bpmTask/releaseTask.do?taskId='+row.id;

    var config = {};
    config.url = url;
    config.data = {'taskId':row.id};
    config.success = function(result){
        if(result.success){
            //加载代办信息
            queryTaskTodoList();
            //查询待办数量
            getTaskTODONum();
        }
    }
    _SubmitJson(config);
}

function handStandardHomeTask(uid,fromMgr,fromPortal){
    var row = {id:uid};
    if(!fromPortal){
        row = grid.getRowByUID(uid);
    }
    var url=__rootPath+'/bpm/core/bpmTask/toStart.do?taskId='+row.id + '&fromMgr=' + fromMgr;
    console.info(url);
    var winObj = openNewWindow(url,"handTask");
    var loop = setInterval(function() {
        if(!winObj) {
            clearInterval(loop);
        } else if(winObj.closed) {
            clearInterval(loop);
            if(grid){
                //加载代办信息
                queryTaskTodoList();
                //查询待办数量
                getTaskTODONum();
            };
        }
    }, 1000);

}

function jumpLink(url) {
    window.open(url);
}

function jumpToStandardSearch(scene) {
    var url=jsUseCtxPath + "/standardManager/core/standard/management.do?1=1";
    if(scene=='base') {
        var number=$.trim(mini.get("standardNumber").getValue());
        var name=$.trim(mini.get("standardName").getValue());
        var category=mini.get("standardCategory").getValue();
        var systemCategory=mini.get("systemCategory").getValue();
        var standardField=mini.get("standardField").getValue();
        if(number) {
            url+="&standardNumber="+number;
        }
        if(name) {
            url+="&standardName="+name;
        }
        if(category) {
            url+="&standardCategory="+category;
        }
        if(systemCategory) {
            url+="&systemCategory="+systemCategory;
        }
        if(standardField) {
            url+="&fieldId="+standardField;
        }
    }
    window.open(encodeURI(encodeURI(url)));
}

function clearQuickSearch() {
    mini.get("standardNumber").setValue('');
    mini.get("standardName").setValue('');
    mini.get("standardCategory").setValue('');
    mini.get("systemCategory").setValue(systemCategoryValue);
    mini.get("standardField").setValue('');
}

//查询下拉框的内容
function querySelectInfos() {
    $.ajax({
        url: jsUseCtxPath + '/standardManager/core/standard/getStandardSelectInfos.do?systemCategoryId='+systemCategoryValue,
        type: 'get',
        contentType: 'application/json',
        success: function (data) {
            if (data) {
                mini.get("standardCategory").load(data.category);
                mini.get("standardField").load(data.fields);
            }
        }
    });
}

//体系类别变化，重新查专业领域
function systemCategoryChange(){
    $.ajax({
        url: jsUseCtxPath + '/standardManager/core/standard/getStandardSelectInfos.do?systemCategoryId='+mini.get("systemCategory").getValue(),
        type: 'get',
        contentType: 'application/json',
        success: function (data) {
            if (data) {
                mini.get("standardField").load(data.fields);
            }
        }
    });
}

function querySystemCategoryInfos() {
    $.ajax({
        url: jsUseCtxPath+'/standardManager/core/standardSystem/queryCategory.do',
        success:function (data) {
            if(data) {
                mini.get("systemCategory").load(data);
                mini.get("systemCategory").setValue(systemCategoryValue);
            }
        }
    });
}
