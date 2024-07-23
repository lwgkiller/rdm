function saveRecord() {
    var jclx = mini.get("jclx").getValue();
    var postData = recordForm.getData();
    //必填项检查
    if (jclx == 'fjc') {
        var checkResult = fjcRecordInfoValid(postData);
        if (!checkResult.success) {
            mini.alert(checkResult.message);
            return;
        }
    } else {
        var checkResult = jcRecordInfoValid(postData);
        if (!checkResult.success) {
            mini.alert(checkResult.message);
            return;
        }
    }

    $.ajax({
        url: jsUseCtxPath + "/matPriceReview/core/recordSave.do",
        type: 'POST',
        contentType: 'application/json',
        data: mini.encode(postData),
        success: function (returnData) {
            if (returnData && returnData.message) {
                mini.alert(returnData.message, '提示', function () {
                    if (returnData.success) {
                        CloseWindow();
                    }
                });
            }
        }
    });
}

function fjcRecordInfoValid(postData) {
    var result = {success: true, message: ""};
    if(!postData.matCode) {
        result.success=false;
        result.message="请填写物料号";
        return result;
    }
    if(!postData.matName) {
        result.success=false;
        result.message="请填写物料描述";
        return result;
    }
    if(!postData.cgzz) {
        result.success=false;
        result.message="请填写采购组织";
        return result;
    }
    if(!postData.gc) {
        result.success=false;
        result.message="请填写工厂代码";
        return result;
    }
    if(!postData.applierCode) {
        result.success=false;
        result.message="请填写供应商代码(SAP)";
        return result;
    }
    if(!postData.applierName) {
        result.success=false;
        result.message="请填写供应商名称";
        return result;
    }
    if(!postData.mrpkzz) {
        result.success=false;
        result.message="请填写MRP控制者";
        return result;
    }
    if(!postData.planDeliveryTime) {
        result.success=false;
        result.message="请填写计划交货时间";
        return result;
    }
    if(!postData.cgz) {
        result.success=false;
        result.message="请填写采购组";
        return result;
    }
    if(!postData.jingjia) {
        result.success=false;
        result.message="请填写净价";
        return result;
    }
    if(!postData.bizhong) {
        result.success=false;
        result.message="请填写币种";
        return result;
    }
    if(!postData.jgdw) {
        result.success=false;
        result.message="请填写价格单位";
        return result;
    }
    if(!postData.jldw) {
        result.success=false;
        result.message="请填写单位";
        return result;
    }
    if(!postData.shuima) {
        result.success=false;
        result.message="请填写税码";
        return result;
    }
    if(!postData.sfGjPrice) {
        result.success=false;
        result.message="请填写是否估计价格";
        return result;
    }
    return result;
}

function jcRecordInfoValid(postData) {
    var result = {success: true, message: ""};
    if(!postData.matCode) {
        result.success=false;
        result.message="请填写物料编码";
        return result;
    }
    if(!postData.matName) {
        result.success=false;
        result.message="请填写物料描述";
        return result;
    }
    if(!postData.cgzz) {
        result.success=false;
        result.message="请填写采购组织";
        return result;
    }
    if(!postData.cggs) {
        result.success=false;
        result.message="请填写采购公司";
        return result;
    }
    if(!postData.gc) {
        result.success=false;
        result.message="请填写工厂";
        return result;
    }
    if(!postData.applierName) {
        result.success=false;
        result.message="请填写供应商名称";
        return result;
    }
    if(!postData.applierCode) {
        result.success=false;
        result.message="请填写供应商编码";
        return result;
    }
    if(!postData.jldw) {
        result.success=false;
        result.message="请填写采购单位";
        return result;
    }
    if(!postData.jgdw) {
        result.success=false;
        result.message="请填写价格单位";
        return result;
    }
    if(!postData.jgNumber) {
        result.success=false;
        result.message="请填写价格数量";
        return result;
    }
    if(!postData.jbdw) {
        result.success=false;
        result.message="请填写基本单位";
        return result;
    }
    if(!postData.bizhong) {
        result.success=false;
        result.message="请填写币种";
        return result;
    }
    if(!postData.wsdj) {
        result.success=false;
        result.message="请填写未税单价";
        return result;
    }
    if(!postData.shuima) {
        result.success=false;
        result.message="请填写税码";
        return result;
    }
    if(!postData.shuilv) {
        result.success=false;
        result.message="请填写税率";
        return result;
    }
    if(!postData.priceValidStart) {
        result.success=false;
        result.message="请填写价格有效期从";
        return result;
    }
    if(!postData.priceValidEnd) {
        result.success=false;
        result.message="请填写价格有效期至";
        return result;
    }
    if(!postData.zgPrice) {
        result.success=false;
        result.message="请填写暂估价";
        return result;
    }
    if(!postData.jgld) {
        result.success=false;
        result.message="请填写价格联动";
        return result;
    }if(!postData.cpf) {
        result.success=false;
        result.message="请填写出票方";
        return result;
    }
    return result;
}

function syncRecordInfo(propName) {
    var matCode = mini.get("matCode").getValue();
    if(!matCode) {
        mini.alert("请先填写物料编号！");
        return;
    }
    mini.confirm("信息会被覆盖，确定继续？", "确定？",
        function (action) {
            if (action == "ok") {
                var reviewId = mini.get("reviewId").getValue();
                var jclx = mini.get("jclx").getValue();
                $.ajax({
                    url:jsUseCtxPath + "/matPriceReview/core/syncRecordInfo.do?matCode="+matCode+"&reviewId="+reviewId+"&jclx="+jclx,
                    method:'get',
                    contentType: 'application/json',
                    success:function (returnData) {
                        if(returnData&&returnData.message) {
                            mini.alert(returnData.message,'提示',function () {
                                if(returnData.success) {
                                    if(propName=='matName' && returnData.data && returnData.data.wlms) {
                                        mini.get("matName").setValue(returnData.data.wlms);
                                    }
                                    if(propName=='mrpkzz' && returnData.data && returnData.data.mrpkzz) {
                                        mini.get("mrpkzz").setValue(returnData.data.mrpkzz);
                                        mini.get("recordType").setValue(returnData.data.recordType);
                                    }
                                    if(propName=='cgz' && returnData.data && returnData.data.cgz) {
                                        mini.get("cgz").setValue(returnData.data.cgz);
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

function mrpkzzBlur() {
    var mrpkzz = mini.get("mrpkzz").getValue();
    if(mrpkzz=='502' || mrpkzz=='503') {
        mini.get("recordType").setValue("1");
    } else {
        mini.get("recordType").setValue("0");
    }
}
