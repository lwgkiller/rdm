//发送消息
function sendMsg() {
    //检查必填项
    var standardId = mini.get("standardSelectId").getValue();
    if(!$.trim(standardId)) {
        mini.alert(standardMessageEdit_name1);
        return;
    }

    var title = mini.get("standardMsgTitle").getValue();
    if(!$.trim(title)) {
        mini.alert(standardMessageEdit_name2);
        return;
    }

    var messageData={};
    if($("#messageId").val()) {
        messageData.id= $("#messageId").val();
    }
    messageData.standardMsgTitle=title;
    messageData.standardMsgContent=mini.get("standardMsgContent").getValue();
    messageData.relatedStandardId=mini.get("standardSelectId").getValue();
    messageData.receiveDeptIds = mini.get('depSelectId').getValue();
    messageData.receiveDeptNames = mini.get('depSelectId').getText();
    if(scene=='public') {
        messageData.isPublicScene ='yes';
    } else {
        messageData.isPublicScene ='no';
    }
    messageData = mini.encode(messageData);
    $.ajax({
        url: jsUseCtxPath + '/standardManager/core/standardMessage/send.do',
        type: 'POST',
        data: messageData,
        async: false,
        contentType: 'application/json',
        success: function (data) {
            if(data) {
                mini.alert(data.message,standardMessageEdit_name3,function (action) {
                    if(action=='ok') {
                        CloseWindow();
                    }
                });
            }
        }
    });
}

function selectStandard(){
    selectStandardWindow.show();
    $.ajax({
        url:jsUseCtxPath+'/standardManager/core/standardSystem/queryCategory.do',
        success:function (data) {
            if(data) {
                mini.get("standardSystemCategory").load(data);
                mini.get("standardSystemCategory").setValue(data[0].systemCategoryId);
                searchStandard();
            }
        }
    });

}

//查询标准
function searchStandard() {
    var queryParam = [];
    //其他筛选条件
    var standardNumber = $.trim(mini.get("filterNumberId").getValue());
    if (standardNumber) {
        queryParam.push({name: "standardNumber", value: standardNumber});
    }
    queryParam.push({name: "systemCategoryId", value: $.trim(mini.get("standardSystemCategory").getValue())});
    var data = {};
    data.filter = mini.encode(queryParam);
    data.pageIndex = standardListGrid.getPageIndex();
    data.pageSize = standardListGrid.getPageSize();
    data.sortField = standardListGrid.getSortField();
    data.sortOrder = standardListGrid.getSortOrder();
    //查询
    standardListGrid.load(data);
}

function onRowDblClick() {
    okWindow();
}

function okWindow() {
    var selectRow = standardListGrid.getSelected();
    if (selectRow) {
        mini.get("standardSelectId").setText(selectRow.standardName);
        mini.get("standardSelectId").setValue(selectRow.id);
    }
    hideWindow();
}

function hideWindow() {
    selectStandardWindow.hide();
    mini.get("standardSystemCategory").setValue('');
    mini.get("filterNumberId").setValue('');
}
