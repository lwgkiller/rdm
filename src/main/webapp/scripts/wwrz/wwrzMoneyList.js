$(function () {
    if (!permission) {
        mini.get('showAll').setValue(0);
    }
    searchFrm();
});
//行功能按钮
//1.本人允许编辑和标记完成
//2.标记完成后，除了总管理员外，自己不允许编辑
function onActionRenderer(e) {
    var record = e.record;
    var id = record.id;
    var finishFlag = record.finishFlag;
    var s = '';
    if ((currentUserId == record.CREATE_BY_ && finishFlag == 'N') || permission) {
        s += '<span  title="编辑" onclick="viewForm(\'' + id + '\',\'edit\')">编辑</span>';
    } else {
        s += '<span  title="编辑" style="color: silver">编辑</span>';
    }
    if (currentUserId == record.CREATE_BY_ && finishFlag == 'N') {
        s += '<span  title="标记完成" onclick="setFinish(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">标记完成</span>';
    } else if (finishFlag == 'Y') {
        s += '<span  title="已完成" style="color: silver">已完成</span>';
    } else {
        s += '<span  title="标记完成" style="color: silver">标记完成</span>';
    }

    return s;
}

function setFinish(record) {
    //判断是否主流程申请终止
    let postData = {"applyId": record.applyId};
    let _url = jsUseCtxPath + '/wwrz/core/money/isEnd.do';
    let resultData = ajaxRequest(_url, 'POST', false, postData);
    if (resultData && resultData.success) {
        if (resultData.data.STATUS_ != 'DISCARD_END') {
            //1.验证 除了证书和备注，其余必填
            if (!record.items || !record.contractCode || !record.money || !record.invoiceCode || !record.paymentDate || !record.documentCode || !record.reportCode || !record.companyCode) {
                mini.alert("除了证书和备注，其余必填");
                return
            }
        }
    }
    //2.提示提交后无法修改，请确认
    mini.confirm("提交后无法修改，请确认？", "提示", function (action) {
        if (action != 'ok') {
            return;
        } else {
            _SubmitJson({
                url: jsUseCtxPath + "/wwrz/core/money/finish.do",
                method: 'POST',
                data: {id: record.id},
                success: function (text) {
                    searchFrm();
                }
            });

        }
    });
}

function addItems() {
    var action = "add";
    let url = jsUseCtxPath + "/wwrz/core/money/editPage.do?action=" + action;
    var title = "新增";
    mini.open({
        title: title,
        url: url,
        width: 1200,
        height: 600,
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
function viewForm(id, action) {
    var url = jsUseCtxPath + "/wwrz/core/money/editPage.do?action=" + action + "&id=" + id;
    var title = "修改";
    if (action == 'view') {
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
                ids.push(r.id);
                var createBy = r.CREATE_BY_;
                if (createBy != currentUserId) {
                    mini.alert("只有创建人才可以删除！");
                    return;
                }
            }
            if (ids.length > 0) {
                _SubmitJson({
                    url: jsUseCtxPath + "/wwrz/core/money/remove.do",
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

function onProductType(e) {
    var record = e.record;
    var certType = record.productType;
    var resultText = '';
    for (var i = 0; i < productTypeList.length; i++) {
        if (productTypeList[i].key_ == certType) {
            resultText = productTypeList[i].text;
            break
        }
    }
    return resultText;
}

function onCabForm(e) {
    var record = e.record;
    var testType = record.cabForm;
    var resultText = '';
    for (var i = 0; i < cabFormList.length; i++) {
        if (cabFormList[i].key_ == testType) {
            resultText = cabFormList[i].text;
            break
        }
    }
    return resultText;
}

function exportExcel() {
    var params = [];
    var parent = $(".search-form");
    var inputAry = $("input", parent);
    inputAry.each(function (i) {
        var el = $(this);
        var obj = {};
        obj.name = el.attr("name");
        if (!obj.name) return true;
        obj.value = el.val();
        params.push(obj);
    });
    $("#filter").val(mini.encode(params));
    var excelForm = $("#excelForm");
    excelForm.submit();
}
