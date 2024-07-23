$(function () {
    if(paramJson.majorName){
        mini.get('major').setText(paramJson.majorName);
        mini.get('major').setValue(paramJson.major);
    }
    if(paramJson.deptName){
        mini.get('deptName').setValue(paramJson.deptName);
    }
    if(paramJson.reportType){
        mini.get('isReplace').setValue(paramJson.reportType);
    }
    searchFrm()
})

function addForm() {
    let url = jsUseCtxPath + "/rdmZhgl/core/yfjb/getEditPage.do?action=add";
    mini.open({
        title: "新增",
        url: url,
        width: 1200,
        height: 1000,
        showModal: true,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
            listGrid.reload();
        }
    });
}

//修改
function editForm(id) {
    var url = jsUseCtxPath + "/rdmZhgl/core/yfjb/getEditPage.do?action=edit&&id=" + id;
    var title = "编辑";
    mini.open({
        title: title,
        url: url,
        width: 1200,
        height: 1000,
        showModal: true,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
            searchFrm();
        }
    });
}

//删除记录
function removeRow() {
    var rows = [];
    rows = listGrid.getSelecteds();
    if (rows.length <= 0) {
        mini.alert("请至少选中一条记录");
        return;
    }
    mini.confirm("确定取消选中记录？", "提示", function (action) {
        if (action != 'ok') {
            return;
        } else {
            var ids = [];
            for (var i = 0, l = rows.length; i < l; i++) {
                var r = rows[i];
                ids.push(r.id);
                var createBy = r.CREATE_BY_;
                if(!YFJBAdmin){
                    if (createBy != currentUserId ) {
                        mini.alert("只有创建人才可以删除！");
                        return;
                    }
                    var infoStatus = r.infoStatus;
                    if(infoStatus=='2'){
                        mini.alert("只有草稿和废止状态才能删除！");
                        return;
                    }
                }
            }
            if (ids.length > 0) {
                _SubmitJson({
                    url: jsUseCtxPath + "/rdmZhgl/core/yfjb/remove.do",
                    method: 'POST',
                    data: {ids: ids.join(',')},
                    success: function (text) {
                        if (listGrid) {
                            listGrid.reload();
                        }
                    }
                });
            }
        }
    });
}

function process(e) {
    var record = e.record;
    var id = record.id;
    var mainId = record.id;
    var response = record.response;
    var s = '';
    s = '<a href="#" style="color:#44cef6;text-decoration:underline;" ' +
        'onclick="showFilePage(\'' + mainId + '\',\'' + response + '\')">项目进度</a>';
    return s;
}
function showFilePage(mainId,response) {
    var url = jsUseCtxPath + "/rdmZhgl/core/yfjb/getProcessPage.do?mainId=" + mainId+"&response="+response;
    var title = "降本项目进度跟踪";
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
            searchFrm()
        }
    });
}
function renderMember(e) {
    var record = e.record;
    var id = record.id;
    var mainId = record.id;
    var response = record.response;
    var s = '';
    s = '<a href="#" style="color:#44cef6;text-decoration:underline;" ' +
        'onclick="showMemberPage(\'' + mainId + '\',\'' + response + '\')">项目成员</a>';
    return s;
}
function showMemberPage(mainId,response) {
    var url = jsUseCtxPath + "/rdmZhgl/core/yfjb/memberPage.do?mainId=" + mainId+"&response="+response;
    var title = "项目成员维护";
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
        }
    });
}
function copyInfo() {
    var rows = [];
    rows = listGrid.getSelecteds();
    if (rows.length <= 0) {
        mini.alert("请至少选中一条记录进行复制");
        return;
    }
    mini.confirm("确定复制选中记录？", "提示", function (action) {
        if (action != 'ok') {
            return;
        } else {
            var ids = [];
            for (var i = 0, l = rows.length; i < l; i++) {
                var r = rows[i];
                var createBy = r.CREATE_BY_;
                if (createBy != currentUserId ) {
                    mini.alert("只有创建人才可以复制！");
                    return;
                }
                ids.push(r.id);
            }
            if (ids.length > 0) {
                _SubmitJson({
                    url: jsUseCtxPath + "/rdmZhgl/core/yfjb/copy.do",
                    method: 'POST',
                    data: {ids: ids.join(',')},
                    success: function (text) {
                        if (listGrid) {
                            listGrid.reload();
                        }
                    }
                });
            }
        }
    });
}
function changeNotice() {
    var rows = [];
    rows = listGrid.getSelecteds();
    if (rows.length != 1) {
        mini.alert("请选中一条记录");
        return;
    }
    //判断是否为项目负责人
    var response = rows[0].response;
    if(currentUserId!=response&&currentUserId != '1'){
        mini.alert("只能项目负责人填写");
        return;
    }
    var url = jsUseCtxPath + "/rdmZhgl/core/yfjb/changeNotice/getEditPage.do?mainId=" + rows[0].id;
    var title = "切换通知单";
    mini.open({
        title: title,
        url: url,
        width: 1000,
        height: 600,
        showModal: true,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
        }
    });
}
function produceNotice() {
    var rows = [];
    rows = listGrid.getSelecteds();
    if (rows.length != 1) {
        mini.alert("请选中一条记录");
        return;
    }
    //判断是否为项目负责人
    var response = rows[0].response;
    if(currentUserId!=response&&currentUserId != '1'){
        mini.alert("只能项目负责人填写");
        return;
    }
    var url = jsUseCtxPath + "/rdmZhgl/core/yfjb/productionNotice/getEditPage.do?mainId=" + rows[0].id;
    var title = "试制通知单";
    mini.open({
        title: title,
        url: url,
        width: 1000,
        height: 600,
        showModal: true,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
        }
    });
}

function submitInfo() {
    var rows = [];
    rows = listGrid.getSelecteds();
    if (rows.length <= 0) {
        mini.alert("请至少选中一条记录进行提交");
        return;
    }
    mini.confirm("确定提交？提交以后修改时间信息需要审批", "提示", function (action) {
        if (action != 'ok') {
            return;
        } else {
            var ids = [];
            for (var i = 0, l = rows.length; i < l; i++) {
                var r = rows[i];
                var createBy = r.CREATE_BY_;
                if (createBy != currentUserId ) {
                    mini.alert("只有创建人才可以提交！");
                    return;
                }
                var infoStatus = r.infoStatus;
                if(infoStatus!='1'){
                    mini.alert("请提交草稿状态的信息！");
                    return;
                }
                ids.push(r.id);
            }
            if (ids.length > 0) {
                _SubmitJson({
                    url: jsUseCtxPath + "/rdmZhgl/core/yfjb/submit.do",
                    method: 'POST',
                    data: {ids: ids.join(',')},
                    success: function (text) {
                        if (listGrid) {
                            listGrid.reload();
                        }
                    }
                });
            }
        }
    });
}

function doAbolishApply() {
    var productRows = listGrid.getSelecteds();
    var mainId = "";
    if (productRows.length != 1) {
        mini.alert("请先选一条项目！");
        return;
    } else {
        var creator = productRows[0].CREATE_BY_;
        var response = productRows[0].response;
        if(creator!=currentUserId&&response!=currentUserId){
            mini.alert("只有创建人或者责任人可以申请作废！");
            return;
        }
        var infoStatus = productRows[0].infoStatus;
        if(infoStatus!='2'){
            mini.alert("只有已提交状态可以申请作废！");
            return;
        }
        mainId = productRows[0].id;
    }
    var url = jsUseCtxPath + "/bpm/core/bpmInst/YFJB-XMZF/start.do?mainId=" + mainId;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
        }
    }, 1000);
}

function doEditApply() {
    var productRows = listGrid.getSelecteds();
    var mainId = "";
    if (productRows.length != 1) {
        mini.alert("请先选一条项目！");
        return;
    } else {
        var creator = productRows[0].CREATE_BY_;
        var response = productRows[0].response;
        if(creator!=currentUserId&&response!=currentUserId){
            mini.alert("只有创建人或者责任人可以申请！");
            return;
        }
        var infoStatus = productRows[0].infoStatus;
        if(infoStatus!='2'){
            mini.alert("只有已提交状态需要申请！");
            return;
        }
        mainId = productRows[0].id;
    }
    var url = jsUseCtxPath + "/bpm/core/bpmInst/YFJB-XGXXSP/start.do?mainId=" + mainId;
    var winObj = window.open(url);
    var loop = setInterval(function () {
        if (winObj.closed) {
        }
    }, 1000);
}
//导入
function openExportWindow() {
    exportWindow.show();
}
function closeExportWindow() {
    exportWindow.hide();
}
function verifyInfo(e) {
    var key_ = e.selected.key_;
    if(key_=='jxwdbb'){
        setHide();
        $('#reportModelType').show();
    }else if(key_=='jhxfsztzdxm'||key_=='sjxfsztzdxm'||key_=='jhxfqhtzdxm'||key_=='sjxfqhtzdxm'||key_=='jhqhxm'||key_=='sjqhxm'){
        setHide();
        $('#yearMonthTr').show();
        mini.alert("请选择报表年月！");
    }else if(key_=='jbxmxfqhtzdsjwqhtj'){
        setHide();
        $('#reportYearMonthTr').show();
        mini.alert("请选择统计月份！");
    }else if(key_=='wlwdbb'){
        setHide();
        $('#reportYearTr').show();
        $('#costTypeTr').show();
        $('#reportDeptTr').show();
        $('#ruleTr').show();
        mini.alert("请选择项目维度和报表年度！");
    }else if(key_=='jbzxlbjszjhb'){
        setHide();
        $('#reportYearMonthTr').show();
        mini.alert("请选择统计月份！");
    }else if(key_=='qnjbycysjjbdb'){
        setHide();
        $('#reportYearTr').show();
        $('#ruleTr').show();
        mini.alert("请选择报表年度！");
    }else{
        setHide()
    }
}
function setHide() {
    $('#reportModelType').hide();
    $('#modelInfo').hide();
    $('#yearMonthTr').hide();
    $('#reportYearTr').hide();
    $('#costTypeTr').hide();
    $('#reportYearMonthTr').hide();
    $('#reportDeptTr').hide();
    $('#ruleTr').hide();
}
//导出
function exportBtn(){
    var reportType = mini.get('reportType').getValue();
    if(reportType=='jxwdbb'){
        var modelType = mini.get('modelType').getValue();
        if(!modelType){
            mini.alert("请先选择机型报表分类！");
            return
        }else{
            if(modelType=='1'){
                var saleModel = mini.get('saleModel').getValue();
                if(!saleModel){
                    mini.alert("请填写销售机型！");
                    return
                }
            }
        }

    }
    if(reportType=='jhxfsztzdxm'||reportType=='sjxfsztzdxm'||reportType=='jhxfqhtzdxm'||reportType=='sjxfqhtzdxm'||reportType=='jhqhxm'||reportType=='sjqhxm'){
        var yearMonth = mini.get('yearMonth').getValue();
        if(!yearMonth){
            mini.alert("请选择报表年月！");
            return
        }
    }
    if(reportType=='jbxmxfqhtzdsjwqhtj'){
        var reportYearMonth = mini.get('reportYearMonth').getValue();
        if(!reportYearMonth){
            mini.alert("请选择统计月份！");
            return
        }
    }
    if(reportType=='jbzxlbjszjhb'){
        var reportYearMonth = mini.get('reportYearMonth').getValue();
        if(!reportYearMonth){
            mini.alert("请选择统计月份！");
            return
        }
    }
    if(reportType=='wlwdbb'){
        var reportYear = mini.get('reportYear').getValue();
        if(!reportYear){
            mini.alert("请选择统计年份！");
            return
        }
        var costCategory = mini.get('costCategory').getValue();
        if(!costCategory){
            mini.alert("请选择项目维度！");
            return
        }
    }
    if(reportType=='qnjbycysjjbdb'){
        var reportYear = mini.get('reportYear').getValue();
        var reportRule = mini.get('reportRule').getValue();
        if(!reportRule){
            mini.alert("请选择统计规则！");
            return
        }
        if(!reportYear){
            mini.alert("请选择统计年份！");
            return
        }
    }
    var params=[];
    var parent=$("#infoForm");
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
function showModelType(e) {
    var key_ = e.selected.key_;
    if(key_=='1'){
        $('#modelInfo').show();
        $('#reportDeptTr').hide();
    }else if(key_=='2'){
        $('#modelInfo').hide();
        $('#reportDeptTr').show();
    }
}

//导入
function openImportWindow() {
    importWindow.show();
}
function closeImportWindow() {
    importWindow.hide();
    clearUploadFile();
    searchFrm();
}

//导入模板下载
function downImportTemplate() {
    var form = $("<form>");
    form.attr("style", "display:none");
    form.attr("target", "");
    form.attr("method", "post");
    form.attr("action", jsUseCtxPath + "/rdmZhgl/core/yfjb/importTemplateDownload.do");
    $("body").append(form);
    form.submit();
    form.remove();
}

//触发文件选择
function uploadFile() {
    $("#inputFile").click();
}

//文件类型判断及文件名显示
function getSelectFile() {
    var fileList = $("#inputFile")[0].files;
    if (fileList && fileList.length > 0) {
        var fileNameSuffix = fileList[0].name.split('.').pop();
        if (fileNameSuffix == 'xls'||fileNameSuffix == 'xlsx') {
            mini.get("fileName").setValue(fileList[0].name);
        }
        else {
            clearUploadFile();
            mini.alert('请上传excel文件！');
        }
    }
}

//清空文件
function clearUploadFile() {
    $("#inputFile").val('');
    mini.get("fileName").setValue('');
}

//上传批量导入
function importProduct() {
    var file = null;
    var fileList = $("#inputFile")[0].files;
    if (fileList && fileList.length > 0) {
        file = fileList[0];
    }
    if (!file) {
        mini.alert('请选择文件！');
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
        xhr.open('POST', jsUseCtxPath + '/rdmZhgl/core/yfjb/importExcel.do', false);
        xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
        var fd = new FormData();
        fd.append('importFile', file);
        xhr.send(fd);
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
    $("#filterSearch").val(mini.encode(params));
    var excelForm = $("#excelSearchForm");
    excelForm.submit();
}
