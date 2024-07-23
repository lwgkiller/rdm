$(function () {
    if(!isManager) {
        mini.get("editMsg").setEnabled(false);
        mini.get("removeMsg").setEnabled(false);
        $("#removeMsg").removeClass('btn-red');
    }
    querySystemCategoryInfos();
});

function querySystemCategoryInfos() {
    $.ajax({
        url: jsUseCtxPath+'/standardManager/core/standardSystem/queryCategory.do',
        success:function (data) {
            if(data) {
                mini.get("systemCategory").load(data);
                mini.get("systemCategory").setValue(systemCategoryValue);
                refreshData();
            }
        }
    });
}


function refreshData() {
    var url=jsUseCtxPath+"/standardManager/core/standardMessage/queryMsgList.do?1=1";
    var systemCategoryValue=mini.get("systemCategory").getValue();
    if(systemCategoryValue) {
        url+="&systemCategoryValue="+systemCategoryValue;
    }
    var standardName=mini.get("standardName").getValue();
    if(standardName) {
        url+="&standardName="+standardName;
    }
    standardMsgListGrid.setUrl(url);
    standardMsgListGrid.load();
}

function editMsg(id,relatedStandardId) {
    if(!isManager) {
        mini.alert('没有操作权限！');
        return;
    }
    if(!relatedStandardId) {
        relatedStandardId='';
    }
    if(!id) {
        id='';
    }
    mini.open({
        title: "发布通知",
        url: jsUseCtxPath + "/standardManager/core/standardMessage/edit.do?id="+id+"&standardId="+relatedStandardId,
        width: 850,
        height: 550,
        showModal:true,
        allowResize: true,
        ondestroy: function (action) {
            refreshData();
        }
    });
}

function seeMessage(id,relatedStandardId) {
    window.open(jsUseCtxPath + "/standardManager/core/standardMessage/see.do?id="+id+"&standardId="+relatedStandardId);
}


function deleteMessage(id) {
    if(!isManager) {
        mini.alert('没有操作权限！');
        return;
    }
    var ids=[];
    if (!id) {
        var rows = standardMsgListGrid.getSelecteds();
        if (rows.length <= 0) {
            mini.alert("请至少选中一条记录");
            return;
        }
        for (var i = 0, l = rows.length; i < l; i++) {
            var r = rows[i];
            ids.push(r.id);
        }
    } else {
        ids.push(id);
    }

    mini.confirm("确定删除选中记录？", "提示", function (action) {
        if (action != 'ok') {
            return;
        } else {
            $.ajax({
                url: jsUseCtxPath + "/standardManager/core/standardMessage/delete.do",
                type: 'POST',
                data: mini.encode({ids: ids.join(',')}),
                contentType: 'application/json',
                success: function (data) {
                    if (data) {
                        mini.alert(data.message);
                        refreshData();
                    }
                }
            });
        }
    });
}