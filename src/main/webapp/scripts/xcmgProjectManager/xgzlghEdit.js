$(function () {
    // console.log("测试数据",anpObj)
    formZlgh.setData(zlghObj);
    if (action == 'detail') {
        formZlgh.setEnabled(false);
        mini.get("saveStandard").hide();
        mini.get("notice").hide();
    }
});

/**
 * 检验表单是否必填
 *
 * @returns {*}
 */
function validZlghMessage() {
    var zlghName = $.trim(mini.get("zlghName").getValue())
    if (!zlghName) {
        return {"result": false, "message": "请填写战略规划名称"};
    }
    var ghnr = $.trim(mini.get("ghnr").getValue())
    if (!ghnr) {
        return {"result": false, "message": "请填写主要规划内容"};
    }
    var ghzgbmId = $.trim(mini.get("ghzgbmId").getValue())
    if (!ghzgbmId) {
        return {"result": false, "message": "请选择规划主管部门"};
    }
    var ghfzrId = $.trim(mini.get("ghfzrId").getValue())
    if (!ghfzrId) {
        return {"result": false, "message": "请选择规划负责人"};
    }
    var ghnf = $.trim(mini.get("ghnf").getValue())
    if (!ghnf) {
        return {"result": false, "message": "请选择规划年份"};
    }
    var ghwcTime = $.trim(mini.get("ghwcTime").getValue())
    if (!ghwcTime) {
        return {"result": false, "message": "请选择规划完成时间"};
    }
    var ghbb = $.trim(mini.get("ghbb").getValue())
    if (!ghbb) {
        return {"result": false, "message": "请填写规划版本"};
    }
    var ghyxqs = $.trim(mini.get("ghyxqs").getValue())
    if (!ghyxqs) {
        return {"result": false, "message": "请选择规划有效周期开始"};
    }
    var ghyxqe = $.trim(mini.get("ghyxqe").getValue())
    if (!ghyxqe) {
        return {"result": false, "message": "请选择规划有效周期结束"};
    }

    return {"result": true};
}

/**
 * 新增或者更新的保存
 */
function saveZlgh(zlghId,type) {

    var formData = formZlgh.getData();

    var formValid = validZlghMessage();
    if (!formValid.result) {
        mini.alert(formValid.message);
        return;
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
                                mini.get("zlghId").setValue(returnObj.zlghId);
                            }
                            CloseWindow();

                        });
                    }
                }
            }
        };

        // 开始上传
        xhr.open('POST', jsUseCtxPath + '/strategicPlanning/core/xgzlgh/saveZlgh.do?zlghId='+zlghId + '&type=' + type, false);
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


function notice() {
    var ghfzrId = $.trim(mini.get("ghfzrId").getValue());
    var xbbmfzrId = $.trim(mini.get("xbbmfzrId").getValue());
    if (!ghfzrId) {
        mini.alert('规划负责人为空！');
        return;
    }
    mini.confirm("确定通知到规划负责人和协办负责人？", "提示", function (action) {
        if (action != 'ok') {
            return;
        } else {
            _SubmitJson({
                url: jsUseCtxPath + "/strategicPlanning/core/xgzlgh/notice.do",
                method: 'POST',
                data: {ghfzrId: ghfzrId,xbbmfzrId:xbbmfzrId},
                success: function (text) {
                }
            });
        }
    });
}
