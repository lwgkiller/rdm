$(function () {
});

function editOrViewApply(id,action) {
    var title="";
    if(reviewCategory == 'common') {
        title='采购物料价格审批表';
    } else if(reviewCategory == 'disposable') {
        title='一次性采购物料价格审批表';
    } else if(reviewCategory == 'emergency') {
        title='应急采购物料价格审批表';
    }
    var url=jsUseCtxPath+"/matPriceReview/core/editPage.do?id="+id+"&action="+action+"&reviewCategory="+reviewCategory;
    var winObj=window.open(url,title);
    var loop = setInterval(function() {
        if(winObj.closed) {
            clearInterval(loop);
            if(applyListGrid){
                applyListGrid.reload();
            }
        }
    }, 1000);
}

//删除记录
function removeApply(record) {
    var rows =[];
    if(record) {
        rows.push(record);
    } else {
        rows = applyListGrid.getSelecteds();
    }

    if (rows.length <= 0) {
        mini.alert("请至少选中一条记录");
        return;
    }

    mini.confirm("确定删除选中记录？", "提示", function (action) {
        if (action != 'ok') {
            return;
        } else {
            //对于已提交OA状态的申请不能删除
            var ids = [];
            var existCannotDelete=false;
            for (var i = 0; i < rows.length; i++) {
                var r = rows[i];
                if(!r.oaFormId) {
                    ids.push(r.id);
                } else {
                    existCannotDelete=true;
                }
            }
            if(existCannotDelete) {
                mini.alert("已提交OA的数据不允许删除！");
            }
            if(ids.length>0) {
                _SubmitJson({
                    url: jsUseCtxPath+"/matPriceReview/core/applyDelete.do",
                    method: 'POST',
                    data: {ids: ids.join(',')},
                    contentType:'application/json',
                    success: function (text) {
                        if(applyListGrid) {
                            applyListGrid.reload();
                        }
                    }
                });
            }
        }
    });
}



