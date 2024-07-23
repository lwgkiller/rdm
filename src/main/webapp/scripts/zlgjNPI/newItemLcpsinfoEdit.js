$(function () {
    formXplcpsxx.setData(xplcxxObj);
    if (action == 'detail') {
        formXplcpsxx.setEnabled(false);
        mini.get("saveStandard").hide();
    }

    if (action == "add") {
        mini.get("bmId").setEnabled(false);
        mini.get("zrrId").setEnabled(false);
        mini.get("yhcs").setEnabled(false);
        mini.get("wcTime").setEnabled(false);
    }
    if(action == "update" && !isFqBianZhi && !isShBianZhi && !isHqBianZhi){
        mini.get("bmId").setEnabled(false);
        mini.get("zrrId").setEnabled(false);
        mini.get("yhcs").setEnabled(false);
        mini.get("wcTime").setEnabled(false);
    }
    if (isFqBianZhi == 'yes'){
        mini.get("bmId").setEnabled(false);
        mini.get("zrrId").setEnabled(false);
        mini.get("yhcs").setEnabled(false);
        mini.get("wcTime").setEnabled(false);
    }
    if (isShBianZhi == "yes") {
        formXplcpsxx.setEnabled(false);
        mini.get("bmId").setEnabled(true);
        mini.get("zrrId").setEnabled(true);
    }
    if (isHqBianZhi == "yes") {
        formXplcpsxx.setEnabled(false);
        mini.get("yhcs").setEnabled(true);
        mini.get("wcTime").setEnabled(true);
    }

});

//检验表单是否必填
function validXplcxx() {
    var product = $.trim(mini.get("product").getValue())
    if (!product) {
        return {"result": false, "message": "请填写产品"};
    }
    var jixin = $.trim(mini.get("jixin").getValue())
    if (!jixin) {
        return {"result": false, "message": "请填写机型"};
    }
    var lbj = $.trim(mini.get("lbj").getValue())
    if (!lbj) {
        return {"result": false, "message": "请填写零部件"};
    }
    var wtqd = $.trim(mini.get("wtqd").getValue())
    if (!wtqd) {
        return {"result": false, "message": '请填写问题清单'};
    }
    return {"result": true};
}

//新增或者更新的保存
function saveXplcpsxx(xplcId) {
    var formData = formXplcpsxx.getData();
    var formValid = validXplcxx();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
    }
    if (isShBianZhi == "yes"){
        var bmId=$.trim(mini.get("bmId").getValue())
        if (!bmId) {
            mini.alert("请选择责任部门");
            return;
        }
        var zrrId=$.trim(mini.get("zrrId").getValue())
        if (!zrrId) {
            mini.alert("请选择责任人");
            return;
        }
    }
    if (isHqBianZhi == "yes"){
        var yhcs=$.trim(mini.get("yhcs").getValue())
        if (!yhcs) {
            mini.alert("请填写优化措施");
            return;
        }
        var wcTime=$.trim(mini.get("wcTime").getValue())
        if (!wcTime) {
            mini.alert("请选择完成日期");
            return;
        }
    }
    var file = null;
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
                    if(xhr.responseText) {
                        var returnObj=JSON.parse(xhr.responseText);
                        var message='';
                        if(returnObj.message) {
                            message=returnObj.message;
                        }
                        mini.alert(message,'提示信息',function (action) {
                            if(action=='ok') {
                                CloseWindow();
                            }
                            if(returnObj.success) {
                                mini.get("id").setValue(returnObj.id);
                            }
                            CloseWindow();

                        });
                    }
                }
            }
        };

        // 开始上传
        xhr.open('POST', jsUseCtxPath + '/zhgl/core/lcps/saveXplcxx.do?xplcId='+xplcId, false);
        xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
        var fd = new FormData();
        if (formData) {
            for (key in formData) {
                if(key=='publishTime' || key=='stopTime') {
                    fd.append(key, getYMDHmsString(formData[key]));
                } else {
                    fd.append(key, formData[key]);
                }
            }
        }
        fd.append('standardFile', file);
        xhr.send(fd);
    }
}






