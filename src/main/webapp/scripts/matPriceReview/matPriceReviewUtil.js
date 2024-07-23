function addMatPriceReviewFile() {
    var applyId=mini.get("id").getValue();
    if(!applyId) {
        mini.alert('请先点击“暂存”按钮创建申请单！');
        return;
    }

    var canOperateFile = false;
    if (action=='edit') {
        canOperateFile = true;
    }
    mini.open({
        title: "附件列表",
        url: jsUseCtxPath + "/matPriceReview/core/fileListWindow.do?formId=" + applyId + "&canOperateFile=" + canOperateFile ,
        width: 800,
        height: 600,
        showModal: false,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
        }
    });
}

function syncMatInfo(propName) {
    var matCode = mini.get("matCode").getValue();
    if(!matCode) {
        mini.alert("请先填写物料号！");
        return;
    }

    mini.confirm("信息会被覆盖，确定继续？", "确定？",
        function (action) {
            if (action == "ok") {
                $.ajax({
                    url:jsUseCtxPath+"/matPriceReview/core/syncMatInfo.do?matCode="+matCode,
                    contentType:'application/json',
                    success:function (returnData) {
                        if(returnData&&returnData.message) {
                            mini.alert(returnData.message,'提示',function () {
                                if(returnData.success) {
                                    if(propName=='jclx' && returnData.data && returnData.data.jclx) {
                                        mini.get("jclx").setValue(returnData.data.jclx);
                                    }
                                    if(propName=='matName' && returnData.data && returnData.data.wlms) {
                                        mini.get("matName").setValue(returnData.data.wlms);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        }
    );
}

function editOrViewRecord(id,action,jclx) {
    var applyId=mini.get("id").getValue();
    if(!applyId) {
        mini.alert('请先点击“暂存”按钮创建申请单！');
        return;
    }
    var title="集采信息记录";
    if(jclx == 'fjc') {
        title='非集采信息记录';
    }
    var url=jsUseCtxPath+"/matPriceReview/core/recordEditPage.do?id="+id+"&action="+action+"&jclx="+jclx+"&reviewId="+applyId;
    mini.open({
        title: title,
        url: url,
        width: 1070,
        height: 470,
        showModal:true,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
            if(jclx == 'fjc') {
                fjcRecordGrid.reload();
            } else {
                jcRecordGrid.reload();
            }
        }
    });
}

function deleteRecord(recordId,jclx) {
    var ids = [];
    if (recordId) {
        ids.push(recordId);
    } else {
        var rows=[];
        if(jclx == 'fjc') {
            rows=fjcRecordGrid.getSelecteds();
        } else {
            rows=jcRecordGrid.getSelecteds();
        }
        for (var i = 0, l = rows.length; i < l; i++) {
            var r = rows[i];
            ids.push(r.id);
        }
    }

    if (ids.length <= 0) {
        mini.alert("请至少选中一条记录");
        return;
    }

    mini.confirm("确定删除选中记录？", "提示", function (action) {
        if (action != 'ok') {
            return;
        } else {
            if (ids.length > 0) {
                _SubmitJson({
                    url: jsUseCtxPath + "/matPriceReview/core/recordDelete.do",
                    method: 'POST',
                    data: {ids: ids.join(',')},
                    success: function (text) {
                        if(jclx == 'fjc') {
                            fjcRecordGrid.reload();
                        } else {
                            jcRecordGrid.reload();
                        }
                    }
                });
            }
        }
    });
}

function generateRecord(jclx) {
    var applyId=mini.get("id").getValue();
    if(!applyId) {
        mini.alert('请先点击“暂存”按钮创建申请单！');
        return;
    }
    var matData=materielGrid.getData();
    if(!matData || matData.length==0) {
        mini.alert("请添加物料后重试！");
        return;
    }
    $.ajax({
        url:jsUseCtxPath+"/matPriceReview/core/generateRecord.do?jclx="+jclx+"&reviewId="+applyId,
        contentType:'application/json',
        success:function (returnData) {
            if(returnData&&returnData.message) {
                mini.alert(returnData.message,'提示',function () {
                    if(jclx == 'fjc') {
                        fjcRecordGrid.reload();
                    } else {
                        jcRecordGrid.reload();
                    }
                });
            }
        }
    });
}