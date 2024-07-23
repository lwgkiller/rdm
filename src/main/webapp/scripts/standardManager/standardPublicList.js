$(function () {
    if(isPointManager) {
        $("#operateStandard").show();
    }
    if(standardNumber) {
        mini.get("standardNumber").setValue(standardNumber);
    }
    if(standardName) {
        mini.get("standardName").setValue(standardName);
    }
    if(companyName) {
        mini.get("companyName").setValue(companyName);
    }
    searchStandard();
});


//查询标准
function searchStandard() {
    var queryParam = [];
    //其他筛选条件
    var standardNumber = $.trim(mini.get("standardNumber").getValue());
    if (standardNumber) {
        queryParam.push({name: "standardNumber", value: standardNumber});
    }
    var standardName = $.trim(mini.get("standardName").getValue());
    if (standardName) {
        queryParam.push({name: "standardName", value: standardName});
    }
    var companyName = $.trim(mini.get("companyName").getValue());
    if (companyName) {
        queryParam.push({name: "companyName", value: companyName});
    }
    var standardStatus = $.trim(mini.get("standardStatus").getValue());
    if (standardStatus) {
        queryParam.push({name: "standardStatus", value: standardStatus});
    }

    var data = {};
    data.filter = mini.encode(queryParam);
    data.pageIndex = standardListGrid.getPageIndex();
    data.pageSize = standardListGrid.getPageSize();
    data.sortField = standardListGrid.getSortField();
    data.sortOrder = standardListGrid.getSortOrder();
    //查询
    standardListGrid.load(data);
}

//清空筛选条件后查询标准
function clearSearchStandard() {
    mini.get("standardNumber").setValue("");
    mini.get("standardName").setValue("");
    mini.get("standardStatus").setValue("");
    mini.get("companyName").setValue("");
    searchStandard();
}

//打开标准的编辑页面（新增、编辑、明细）
function openStandardEditWindow(standardId, action) {
    mini.open({
        title: "标准公开",
        url: jsUseCtxPath + "/standardManager/core/standard/publicEditPage.do?standardId=" + standardId + '&action=' + action ,
        width: 800,
        height: 450,
        showModal: true,
        allowResize: true,
        onload: function () {
            var iframe = this.getIFrameEl();
            var data = {isPointManager:isPointManager};  //传递上传参数
            iframe.contentWindow.setData(data);
        },
        ondestroy: function (action) {
            searchStandard();
        }
    });
}

//删除标准
function removeStandard(record) {
    var rows = [];
    if (record) {
        rows.push(record);
    } else {
        rows = standardListGrid.getSelecteds();
    }
    if (rows.length <= 0) {
        mini.alert("请至少选中一条记录");
        return;
    }

    mini.confirm("确定删除选中记录？", "提示", function (action) {
        if (action != 'ok') {
            return;
        } else {
            var ids = [];
            var existCannotDelete=false;
            for (var i = 0, l = rows.length; i < l; i++) {
                var r = rows[i];
                if(isPointManager) {
                    ids.push(r.id);
                } else {
                    existCannotDelete=true;
                }
            }
            if(existCannotDelete) {
                mini.alert('仅技术标准管理员有权限操作！');
            }
            if(ids.length<=0) {
                return;
            }
            $.ajax({
                url: jsUseCtxPath + "/standardManager/core/standard/deletePublic.do",
                type: 'POST',
                data: mini.encode({ids: ids.join(',')}),
                contentType: 'application/json',
                success: function (data) {
                    if (data) {
                        mini.alert(data.message);
                        searchStandard();
                    }
                }
            });
        }
    });
}

//预览标准全文
function previewStandard(standardId,coverConent) {
    if(status=='disable') {
        mini.confirm("该标准已废止，确定预览吗？","提示",function (action) {
            if(action=="ok") {
                previewStandardDo(standardId, coverConent);
            }
        });
    } else {
        previewStandardDo(standardId, coverConent);
    }

}

function previewStandardDo(standardId, coverConent) {
    var previewUrl = jsUseCtxPath + "/standardManager/core/standard/publicPreview.do?standardId=" + standardId;
    window.open(jsUseCtxPath + "/pdfjs/web/viewer.html?coverContent="+encodeURIComponent(coverConent)+"&plate=bz&file=" + encodeURIComponent(previewUrl));
}

//下载标准全文
function downloadStandard(standardId, standardName, standardNumber) {
    if(status=='disable') {
        mini.confirm("该标准已废止，确定下载吗？","提示",function (action) {
            if(action=="ok") {
                downloadStandardDo(standardId, standardName, standardNumber);
            }
        });
    }
    else {
        downloadStandardDo(standardId, standardName, standardNumber);
    }
}

function downloadStandardDo(standardId, standardName, standardNumber) {
    var form = $("<form>");
    form.attr("style", "display:none");
    form.attr("target", "");
    form.attr("method", "post");
    form.attr("action", jsUseCtxPath + "/standardManager/core/standard/publicDownload.do");
    var standardIdAttr = $("<input>");
    standardIdAttr.attr("type", "hidden");
    standardIdAttr.attr("name", "standardId");
    standardIdAttr.attr("value", standardId);
    var standardNameAttr = $("<input>");
    standardNameAttr.attr("type", "hidden");
    standardNameAttr.attr("name", "standardName");
    standardNameAttr.attr("value", standardName);
    var standardNumberAttr = $("<input>");
    standardNumberAttr.attr("type", "hidden");
    standardNumberAttr.attr("name", "standardNumber");
    standardNumberAttr.attr("value", standardNumber);
    $("body").append(form);
    form.append(standardIdAttr);
    form.append(standardNameAttr);
    form.append(standardNumberAttr);
    form.submit();
    form.remove();
}

//导出标准
function exportStandard() {
    var queryParam = [];
    //其他筛选条件
    var standardNumber = $.trim(mini.get("standardNumber").getValue());
    if (standardNumber) {
        queryParam.push({name: "standardNumber", value: standardNumber});
    }
    var standardName = $.trim(mini.get("standardName").getValue());
    if (standardName) {
        queryParam.push({name: "standardName", value: standardName});
    }
    var companyName = $.trim(mini.get("companyName").getValue());
    if (companyName) {
        queryParam.push({name: "companyName", value: companyName});
    }
    var standardStatus = $.trim(mini.get("standardStatus").getValue());
    if (standardStatus) {
        queryParam.push({name: "standardStatus", value: standardStatus});
    }


    $("#filter").val(mini.encode(queryParam));
    var excelForm = $("#excelForm");
    excelForm.submit();
}

