$(function () {
       searchList();
})
function searchList() {
    var parent=$(".search-form",window.parent.document);
    var inputAry=$("input",parent);
    var params=[];
    inputAry.each(function(i){
        var el=$(this);
        var obj={};
        obj.name=el.attr("name");
        if(!obj.name) return true;
        obj.value=el.val();
        params.push(obj);
    });
    var data={};
    data.filter=mini.encode(params);
    unPlanListGrid.load(data);
}

function loadGrid() {
    var mainId = mini.get('id').getValue();
    var paramArray = [{name: "mainId", value: mainId}];
    var data = {};
    data.filter = mini.encode(paramArray);
    data.mainId = mainId;
    itemGrid.load(data);
}

//新增计划审批流程
function doPlanApply() {
    var url = jsUseCtxPath + "/bpm/core/bpmInst/YDGZJHSP/start.do?applyType=0";
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            searchList();
        }
    }, 1000);
}
//新增计划审批流程
function doFinishApply() {
    var url = jsUseCtxPath + "/bpm/core/bpmInst/YDGZJHSP/start.do?applyType=1";
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            searchList();
        }
    }, 1000);
}
//新增延期审批流程
function doDelayApply() {
    var unPlanRows = [];
    unPlanRows = unPlanListGrid.getSelecteds();
    var sum = unPlanRows.length;
    if (sum <= 0) {
        mini.alert(monthUnWorkList_name2);
        return;
    }
    var unPlanIds = [];
    if(unPlanRows.length>0){
        for(var i=0;i<unPlanRows.length;i++){
            unPlanIds.push(unPlanRows[i].mainId);
        }
    }
    var url = jsUseCtxPath + "/bpm/core/bpmInst/YDJHYQSP/start.do?unPlanIds="+unPlanIds.join(",");
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
            clearInterval(loop);
            searchList();
        }
    }, 1000);
}
//新增非项目计划
function addUnProjectPlan() {
    let url = jsUseCtxPath + "/rdmZhgl/core/monthUnProjectPlan/getEditPage.do?action=add";
    mini.open({
        title: monthUnWorkList_name3,
        url: url,
        width: 1200,
        height: 800,
        showModal: true,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
            unPlanListGrid.reload();
        }
    });
}
//修改
function editProjectPlan(_type) {
    //先判断 是项目计划还是非项目计划
    var unPlanRows = [];
    unPlanRows = unPlanListGrid.getSelecteds();
    var sum = unPlanRows.length;
    if (sum != 1) {
        mini.alert(monthUnWorkList_name4);
        return;
    }
    var mainId;
    var url;
    if(unPlanRows.length==1){
        var responseMan = unPlanRows[0].responseMan;
        if(responseMan!=currentUserName&& !permission){
            mini.alert(monthUnWorkList_name5);
            return;
        }
        mainId = unPlanRows[0].mainId;
        //判断是否已经在审批中
        let postData = {"projectId":mainId,"type":"1"};
        let _url = jsUseCtxPath + '/rdmZhgl/core/monthWorkApply/isRunning.do';
        let resultData = ajaxRequest(_url,'POST',false,postData);
        if(resultData&&!resultData.success&&!isAdmin){
            if(_type=='finish'){
                if(resultData.data.processStatus!='SUCCESS_END'){
                    mini.alert(monthUnWorkList_name6);
                    return;
                }
            }else{
                mini.alert(monthUnWorkList_name7);
                return;
            }
        }
        if(_type=='finish'){
            url = jsUseCtxPath + "/rdmZhgl/core/monthUnProjectPlan/getEditPage.do?action=finish&&mainId="+mainId;
        }else{
            url = jsUseCtxPath + "/rdmZhgl/core/monthUnProjectPlan/getEditPage.do?action=edit&&mainId="+mainId;
        }
    }
    var title = "编辑";
    mini.open({
        title: title,
        url: url,
        width: 1200,
        height: 800,
        showModal: true,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
            searchList();
            unPlanListGrid.deselectAll(true);
        }
    });
}
//删除记录
function removeRow() {
    //先判断 是项目计划还是非项目计划
    var unPlanRows = [];
    unPlanRows = unPlanListGrid.getSelecteds();
    var sum = unPlanRows.length;
    if (sum <= 0) {
        mini.alert(monthUnWorkList_name8);
        return;
    }
    if(unPlanRows.length>0) {
        for (var i = 0, l = unPlanRows.length; i < l; i++) {
            var r = unPlanRows[i];
            debugger
            var responseMan = r.responseMan;
            var asyncStatus = r.asyncStatus;
            if (responseMan != currentUserName&& !permission) {
                mini.alert(monthUnWorkList_name9);
                return;
            }
            if (asyncStatus == "1" && !isAdmin) {
                mini.alert(monthUnWorkList_name10);
                return;
            }
            let postData = {"projectId":r.mainId,"type":"1"};
            let _url = jsUseCtxPath + '/rdmZhgl/core/monthWorkApply/isRunning.do';
            let resultData = ajaxRequest(_url,'POST',false,postData);
            if(resultData&&!resultData.success&&!isAdmin){
                mini.alert(monthUnWorkList_name11);
                return;
            }
        }
    }
    mini.confirm(monthUnWorkList_name12, monthUnWorkList_name13, function (action) {
        if (action != 'ok') {
            return;
        } else {
            if(unPlanRows.length>0){
                var ids = [];
                for (var i = 0, l = unPlanRows.length; i < l; i++) {
                    var r = unPlanRows[i];
                    ids.push(r.mainId);
                }
                if (ids.length > 0) {
                    _SubmitJson({
                        url: jsUseCtxPath + "/rdmZhgl/core/monthUnProjectPlan/remove.do",
                        method: 'POST',
                        data: {ids: ids.join(',')},
                        success: function (text) {
                            if (unPlanListGrid) {
                                unPlanListGrid.reload();
                            }
                        }
                    });
                }
            }
        }
    });
}
function exportBtn(){
    var params=[];
    var parent=$(".search-form",window.parent.document);
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
    var excelForm = $("#excelUnForm");
    excelForm.submit();
}
//删除记录
function copyPlan() {
    yearForm.validate();
    if (!yearForm.isValid()) {
        mini.alert(monthUnWorkList_name14);
        return;
    }
    var yearMonth = mini.get('yearSelect').getText();
    //添加验证，只能复制到本月以后
   var currentYearMonth = getCurrentYearMonth();
   if(yearMonth<=currentYearMonth){
       mini.alert(monthUnWorkList_name15);
       return;
   }
    var unPlanRows = [];
    unPlanRows = unPlanListGrid.getSelecteds();
    var sum = unPlanRows.length;
    if (sum != 1) {
        mini.alert(monthUnWorkList_name16);
        return;
    }
    if(unPlanRows.length>0) {
        for (var i = 0, l = unPlanRows.length; i < l; i++) {
            var r = unPlanRows[i];
            var responseMan = r.responseMan;
            if (responseMan != currentUserName&& !permission) {
                mini.alert(monthUnWorkList_name17);
                return;
            }
        }
    }
    if(unPlanRows.length>0){
        var ids = [];
        for (var i = 0, l = unPlanRows.length; i < l; i++) {
            var r = unPlanRows[i];
            ids.push(r.mainId);
        }
        if (ids.length > 0) {
            _SubmitJson({
                url: jsUseCtxPath + "/rdmZhgl/core/monthUnProjectPlan/copy.do",
                method: 'POST',
                data: {ids: ids.join(','),yearMonth:yearMonth},
                success: function (text) {
                    if (unPlanListGrid) {
                        unPlanListGrid.reload();
                    }
                }
            });
        }
    }
}
function openEditWindow() {
    yearMonthSelectedWindow.show();
}
function closeEditWindow() {
    yearMonthSelectedWindow.hide();
}

//导入相关代码

//导入
function openBudget() {
    importBudgetWindow.show();
}
function closeBudgetWindow() {
    importBudgetWindow.hide();
    clearBudgetFile();
    searchList();
}

//导入模板下载
function downBudgetTemplate() {
    var form = $("<form>");
    form.attr("style", "display:none");
    form.attr("target", "");
    form.attr("method", "post");
    form.attr("action", jsUseCtxPath + "/rdmZhgl/core/monthUnProjectPlan/budgetTemplateDownload.do");
    $("body").append(form);
    form.submit();
    form.remove();
}

//触发文件选择
function uploadBudgetFile() {
    $("#inputBudgetFile").click();
}

//文件类型判断及文件名显示
function getBudgetFile() {
    var fileList = $("#inputBudgetFile")[0].files;
    if (fileList && fileList.length > 0) {
        var fileNameSuffix = fileList[0].name.split('.').pop();
        if (fileNameSuffix == 'xls'||fileNameSuffix == 'xlsx') {
            mini.get("budgetFileName").setValue(fileList[0].name);
        }
        else {
            clearBudgetFile();
            mini.alert(monthUnWorkList_name18);
        }
    }
}

//清空文件
function clearBudgetFile() {
    $("#inputBudgetFile").val('');
    mini.get("budgetFileName").setValue('');
}

//上传批量导入
function importBudget() {
    var file = null;
    var fileList = $("#inputBudgetFile")[0].files;
    if (fileList && fileList.length > 0) {
        file = fileList[0];
    }
    var yearMonth = mini.get('yearMonthSelect').getText();
    if(!yearMonth){
        mini.alert(monthUnWorkList_name19);
        return;
    }
    if (!file) {
        mini.alert(monthUnWorkList_name20);
        return;
    }
    //XMLHttpRequest方式上传表单
    var xhr = false;
    try {
        //尝试创建 XMLHttpRequest 对象，除 IE 外的浏览器都支持这个方法。
        xhr = new XMLHttpRequest();
    } catch (e) {
        //使用较新版本的 IE 创建 IE 兼容的对象（Msxml2.XMLHTTP）。
        xhr = ActiveXobject("Msxml12.XMLHTTP");
    }

    if (xhr.upload) {
        xhr.onreadystatechange = function (e) {
            if (xhr.readyState == 4) {
                if (xhr.status == 200) {
                    if (xhr.responseText) {
                        var returnObj = JSON.parse(xhr.responseText);
                        var message = '';
                        if (returnObj.message) {
                            message = returnObj.message;
                        }
                        mini.alert(message);
                    }
                }
            }
        };

        // 开始上传
        xhr.open('POST', jsUseCtxPath + '/rdmZhgl/core/monthUnProjectPlan/importBudgetExcel.do?yearMonth='+yearMonth, false);
        xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
        var fd = new FormData();
        fd.append('importFile', file);
        xhr.send(fd);
    }
}

//战略项目导入模板

function openStrategy() {
    importStrategyWindow.show();
}
function closeStrategyWindow() {
    importStrategyWindow.hide();
    clearStrategyFile();
    searchList();
}

//导入模板下载
function downStrategyTemplate() {
    var form = $("<form>");
    form.attr("style", "display:none");
    form.attr("target", "");
    form.attr("method", "post");
    form.attr("action", jsUseCtxPath + "/rdmZhgl/core/monthUnProjectPlan/strategyTemplateDownload.do");
    $("body").append(form);
    form.submit();
    form.remove();
}

//触发文件选择
function uploadStrategyFile() {
    $("#inputStrategyFile").click();
}

//文件类型判断及文件名显示
function getStrategyFile() {
    var fileList = $("#inputStrategyFile")[0].files;
    if (fileList && fileList.length > 0) {
        var fileNameSuffix = fileList[0].name.split('.').pop();
        if (fileNameSuffix == 'xls'||fileNameSuffix == 'xlsx') {
            mini.get("strategyFileName").setValue(fileList[0].name);
        }
        else {
            clearStrategyFile();
            mini.alert(monthUnWorkList_name18);
        }
    }
}

//清空文件
function clearStrategyFile() {
    $("#inputStrategyFile").val('');
    mini.get("strategyFileName").setValue('');
}

//上传批量导入
function importStrategy() {
    var file = null;
    var fileList = $("#inputStrategyFile")[0].files;
    if (fileList && fileList.length > 0) {
        file = fileList[0];
    }
    var yearMonth = mini.get('yearMonthSelectStrategy').getText();
    if(!yearMonth){
        mini.alert(monthUnWorkList_name19);
        return;
    }
    if (!file) {
        mini.alert(monthUnWorkList_name20);
        return;
    }
    //XMLHttpRequest方式上传表单
    var xhr = false;
    try {
        //尝试创建 XMLHttpRequest 对象，除 IE 外的浏览器都支持这个方法。
        xhr = new XMLHttpRequest();
    } catch (e) {
        //使用较新版本的 IE 创建 IE 兼容的对象（Msxml2.XMLHTTP）。
        xhr = ActiveXobject("Msxml12.XMLHTTP");
    }

    if (xhr.upload) {
        xhr.onreadystatechange = function (e) {
            if (xhr.readyState == 4) {
                if (xhr.status == 200) {
                    if (xhr.responseText) {
                        var returnObj = JSON.parse(xhr.responseText);
                        var message = '';
                        if (returnObj.message) {
                            message = returnObj.message;
                        }
                        mini.alert(message);
                    }
                }
            }
        };

        // 开始上传
        xhr.open('POST', jsUseCtxPath + '/rdmZhgl/core/monthUnProjectPlan/importStrategyExcel.do?yearMonth='+yearMonth, false);
        xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
        var fd = new FormData();
        fd.append('importStrategyFile', file);
        xhr.send(fd);
    }
}
