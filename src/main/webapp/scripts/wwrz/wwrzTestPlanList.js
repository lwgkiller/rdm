$(function () {
    var monthArray = [2,3,6,9,12];
    var dayArray = [20,21,22,23,24,25,26,27,28,29,30,31];
    var currentDate = new Date();
    var month = currentDate.getMonth()+1;
    var day = currentDate.getDate();
    if($.inArray(month,monthArray)>-1&&$.inArray(day,dayArray)>-1){
        mini.get('addButton').setEnabled(true);
        mini.get('delButton').setEnabled(true);
    }else{
        mini.get('addButton').setEnabled(false);
        mini.get('delButton').setEnabled(false);
    }
    if(currentUserId=="1"){
        $('#deptApply').show();
    }
});
//行功能按钮
function onActionRenderer(e) {
    var record = e.record;
    var id = record.id;
    var s = '';
    if(currentUserId==record.CREATE_BY_||currentUserId==record.charger){
        s += '<span  title="编辑" onclick="viewForm(\'' + id + '\',\'edit\')">编辑</span>';
    }else{
        s += '<span  title="编辑" style="color: silver">编辑</span>';
    }

    return s;
}
function addItems() {
    var action = "add";
    let url = jsUseCtxPath + "/wwrz/core/testPlan/editPage.do?action="+action;
    var title = "新增";
    mini.open({
        title: title,
        url: url,
        width: 800,
        height: 500,
        showModal: true,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
            searchFrm();
        }
    });
}
//查看
function viewForm(id,action) {
    var url= jsUseCtxPath +"/wwrz/core/testPlan/editPage.do?action="+action+"&id="+id;
    var title = "修改";
    if(action=='view'){
        title = "查看";
    }
    mini.open({
        title: title,
        url: url,
        width: 1000,
        height: 800,
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
                var planStatus = r.planStatus;
                if(planStatus=='ysp'){
                    mini.alert("已审批的不允许删除！");
                    return;
                }
                if(r.CREATE_BY_!=currentUserId && currentUserId==r.charger) {
                    mini.alert("产品主管或者创建人允许删除！");
                    return;
                }
                ids.push(r.id);
            }
            if (ids.length > 0) {
                _SubmitJson({
                    url: jsUseCtxPath + "/wwrz/core/testPlan/remove.do",
                    method: 'POST',
                    data: {ids: ids.join(',')},
                    success: function (text) {
                        searchFrm();
                    }
                });
            }

        }
    });
}
function onCertType(e) {
    var record = e.record;
    var certType = record.certType;
    var resultText = '';
    for (var i = 0; i < certTypeList.length; i++) {
        if (certTypeList[i].key_ == certType) {
            resultText = certTypeList[i].text;
            break
        }
    }
    return resultText;
}
function onTestType(e) {
    var record = e.record;
    var testType = record.testType;
    var resultText = '';
    for (var i = 0; i < testTypeList.length; i++) {
        if (testTypeList[i].key_ == testType) {
            resultText = testTypeList[i].text;
            break
        }
    }
    return resultText;
}
function onPlanStatusType(e) {
    var record = e.record;
    var resultValue = record.planStatus;
    var resultText = '';
    for (var i = 0; i < planStatusList.length; i++) {
        if (planStatusList[i].key_ == resultValue) {
            resultText = planStatusList[i].text;
            break
        }
    }
    var _html = '';
    var color = '';
    if (resultValue == 'dsp') {
        color = 'red'
    } else if (resultValue == 'spz') {
        color = '#0558f0';
    } else if (resultValue == 'ysp') {
        color = 'green';
    }
    _html = '<span style="color: ' + color + '">' + resultText + '</span>'
    return _html;
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
//新增计划审批流程
function createFlow() {
    var postData = {};
    var _url = jsUseCtxPath + '/wwrz/core/plan/createFlow.do';
    var resultData = ajaxRequest(_url, 'POST', false, postData);
    if (resultData) {
        mini.alert(resultData.message);
        searchFrm();
    }
}
