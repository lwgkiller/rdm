$(function () {
    materielApplyForm.setData(matPriceReviewObj);
    if(action=='view') {
        materielApplyForm.setEnabled(false);
        mini.get("matDetailButtons").hide();
        mini.get("jcRecordButtons").hide();
        mini.get("fjcRecordButtons").hide();
        mini.get("saveMatPriceReview").hide();
        mini.get("commitMatPriceReview").hide();
    }else {
        mini.get("applyNo").setEnabled(false);
        mini.get("applyDeptId").setEnabled(false);
        mini.get("applyUserId").setEnabled(false);
    }
});

function saveOrCommitMatPriceReview(scene) {
    var postData=materielApplyForm.getData();
    postData.jgExcuteStart=mini.get("jgExcuteStart").getText();
    postData.jgExcuteEnd=mini.get("jgExcuteEnd").getText();
    //必填项检查
    var checkResult=applyBaseInfoValid(postData);
    if(!checkResult.success) {
        mini.alert(checkResult.message);
        return;
    }
    var id=postData.id;

    $.ajax({
        url:jsUseCtxPath+"/matPriceReview/core/applySaveOrCommit.do?scene="+scene,
        type:'POST',
        contentType:'application/json',
        data:mini.encode(postData),
        success:function (returnData) {
            if(returnData&&returnData.message) {
                mini.alert(returnData.message,'提示',function () {
                    if(returnData.success) {
                        if(scene=='save') {
                            var url=jsUseCtxPath+"/matPriceReview/core/editPage.do?id="+returnData.data+"&action=edit&reviewCategory="+mini.get("reviewCategory").getValue();
                            window.location.href=url;
                        } else {
                            CloseWindow();
                        }
                    } else {
                        if(scene=='commit' && !id && returnData.data) {
                            var url=jsUseCtxPath+"/matPriceReview/core/editPage.do?id="+returnData.data+"&action=edit&reviewCategory="+mini.get("reviewCategory").getValue();
                            window.location.href=url;
                        }
                    }
                });
            }
        }
    });
}

function applyBaseInfoValid(postData) {
    var result={success:true,message:""};
    if(!postData.applyUserMobile) {
        result.success=false;
        result.message="请填写申请人电话";
        return result;
    }
    if(!postData.applyCategory) {
        result.success=false;
        result.message="请选择申报类别";
        return result;
    }
    if(!postData.matCategory) {
        result.success=false;
        result.message="请选择物料类别";
        return result;
    }
    if(!postData.applierCode) {
        result.success=false;
        result.message="请填写供应商编码";
        return result;
    }
    if(!postData.applierName) {
        result.success=false;
        result.message="请填写供应商名称（全称）";
        return result;
    }
    if(!postData.ptProduct) {
        result.success=false;
        result.message="请填写配套产品";
        return result;
    }
    if(!postData.zxRate) {
        result.success=false;
        result.message="请填写执行税率";
        return result;
    }


    return result;
}


//新增物料
function editOrViewMateriel(matObj,action) {
    var applyId=mini.get("id").getValue();
    if(!applyId) {
        mini.alert('请先点击“暂存”按钮创建申请单！');
        return;
    }
    matDetailWindow.show();
    if(action=='view') {
        mini.get("saveMatBtn").hide();
        matDetailForm.setEnabled(false);
        $("#syncMatName").hide();
        $("#syncJclx").hide();
    } else {
        mini.get("saveMatBtn").show();
        matDetailForm.setEnabled(true);
        $("#syncMatName").show();
        $("#syncJclx").show();
    }
    if(matObj) {
        matDetailForm.setData(matObj);
    }
}

function saveMatDetail() {
    var postData=matDetailForm.getData();
    //必填项检查
    var checkResult=matDetailInfoValid(postData);
    if(!checkResult.success) {
        mini.alert(checkResult.message);
        return;
    }
    if(!postData.id) {
        postData.reviewId = mini.get("id").getValue();
    }

    $.ajax({
        url:jsUseCtxPath+"/matPriceReview/core/matDetailSave.do",
        type:'POST',
        contentType:'application/json',
        data:mini.encode(postData),
        success:function (returnData) {
            if(returnData&&returnData.message) {
                mini.alert(returnData.message,'提示',function () {
                    if(returnData.success) {
                        closeMatDetail();
                    }
                });
            }
        }
    });
}

function matDetailInfoValid(postData) {
    var result={success:true,message:""};
    if(!postData.matCode) {
        result.success=false;
        result.message="请填写物料号";
        return result;
    }
    if(!postData.matName) {
        result.success=false;
        result.message="请填写物料名称";
        return result;
    }
    if(!postData.jclx) {
        result.success=false;
        result.message="请选择集采类型";
        return result;
    }
    if(!postData.tuhao) {
        result.success=false;
        result.message="请填写图号(规格)";
        return result;
    }
    if(!postData.zjxh) {
        result.success=false;
        result.message="请填写整机型号";
        return result;
    }
    if(!postData.jldw) {
        result.success=false;
        result.message="请填写计量单位";
        return result;
    }
    if(!postData.cgsl) {
        result.success=false;
        result.message="请填写采购数量";
        return result;
    }
    if(!postData.sbjg) {
        result.success=false;
        result.message="请填写申报价/原执行价格(含税)";
        return result;
    }
    if(!postData.pzjghs) {
        result.success=false;
        result.message="请填写批准价(含税)";
        return result;
    }
    if(!postData.pzjgbhs) {
        result.success=false;
        result.message="请填写批准价(不含税)";
        return result;
    }
    if(!postData.yfcdf) {
        result.success=false;
        result.message="请填写运费承担方";
        return result;
    }

    return result;
}

function closeMatDetail() {
    matDetailForm.setData({});
    matDetailWindow.hide();
    materielGrid.reload();
}

//删除物料
function deleteMateriel(recordId) {
    var ids = [];
    if (recordId) {
        ids.push(recordId);
    } else {
        var rows=materielGrid.getSelecteds();
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
                    url: jsUseCtxPath + "/matPriceReview/core/matDetailDelete.do",
                    method: 'POST',
                    data: {ids: ids.join(',')},
                    success: function (text) {
                        materielGrid.reload();
                    }
                });
            }
        }
    });
}



