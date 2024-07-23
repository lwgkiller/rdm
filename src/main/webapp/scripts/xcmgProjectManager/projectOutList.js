$(function () {
    if (canOperateFile != 'yes'&&typeName.indexOf("技术封装") != -1) {
        mini.get("addOutButtons").setEnabled(false);
        mini.get("delOutButtons").setEnabled(false);
        mini.get("saveOutButtons").setEnabled(false);
        grid_project_out.setAllowCellEdit(false);
    }
    if (typeName.indexOf("国内发明专利") != -1 || typeName.indexOf("国内实用新型专利") != -1 || typeName.indexOf("国内外观专利") != -1) {
        mini.get("addOutButtons").setEnabled(false);
        mini.get("delOutButtons").setEnabled(false);
        mini.get("saveOutButtons").setEnabled(false);
        grid_project_out.showColumn("zllxName");
        grid_project_out.showColumn("gnztName");
    } else if (typeName.indexOf("专利") != -1) {

    } else if (typeName.indexOf("标准") != -1) {
        grid_project_out.showColumn("categoryName");
        grid_project_out.showColumn("standardStatus");
    } else if (typeName.indexOf("软件著作权") != -1) {
        grid_project_out.showColumn("zpsm");
        grid_project_out.showColumn("fbzt");
    } else if (typeName.indexOf("论文") != -1 || typeName.indexOf("期刊") != -1) {

    } else if (typeName.indexOf("FMEA") != -1){
        grid_project_out.showColumn("jixing");
        grid_project_out.showColumn("femaType");
        grid_project_out.showColumn("analyseName");
    }
});

function addOut() {
    var addRow = {projectId: projectId, outPlanId: outPlanId};
    grid_project_out.addRow(addRow);
}

function deleteOut() {
    var selecteds = grid_project_out.getSelecteds();
    if (selecteds && selecteds.length > 0) {
        grid_project_out.removeRows(selecteds);
    }
}

function saveOut() {
    var changeDataArr = grid_project_out.getChanges();
    if (changeDataArr.length == 0) {
        mini.alert("暂无需要保存的数据！");
        return;
    }
    for (var index = 0; index < changeDataArr.length; index++) {
        if (!changeDataArr[index].outDescription && changeDataArr[index]._state != 'removed') {
            mini.alert("存在“实际成果描述”为空的数据！");
            return;
        }
    }
    //保存
    var formData = {changeDataArr: changeDataArr, typeName: typeName};
    var json = mini.encode(formData);
    $.ajax({
        url: jsUseCtxPath + '/xcmgProjectManager/core/xcmgProject/saveOutList.do',
        type: 'post',
        async: false,
        data: json,
        contentType: 'application/json',
        success: function (data) {
            if (data) {
                if (data.success) {
                    mini.alert("数据保存成功", "提示", function () {
                        grid_project_out.reload();
                    });
                } else {
                    mini.alert("数据保存失败，" + data.message, "提示", function () {
                        grid_project_out.reload();
                    });
                }
            }
        }
    });
}

function searchOut() {
    grid_project_out.reload();
}

function detailOut(outUrl) {
    if (!outUrl) {
        return;
    }
    if (!belongSubSysKey) {
        outUrl=jsUseCtxPath +outUrl;
    } else {
        outUrl=jsUseCtxPath +"/pageJumpRedirect.do?targetSubSysKey="+belongSubSysKey+"&targetUrl="+encodeURIComponent(outUrl);
    }
    window.open(outUrl);
}

function outNameCloseClick(e) {
    var obj = e.sender;
    obj.setValue("");
    obj.setText("");
    var updateOutRow = grid_project_out.getSelected();
    if (updateOutRow) {
        grid_project_out.updateRow(updateOutRow, {outReferId: '', outName: '', outNumber: ''});
    }
}

function outNameClick() {
    var updateOutRow = grid_project_out.getSelected();
    if (typeName.indexOf("国内发明专利") != -1 || typeName.indexOf("国内实用新型专利") != -1 || typeName.indexOf("国内外观专利") != -1) {
        selectZlWindow.show();
    } else if (typeName.indexOf("专利") != -1) {
        selectGjZlWindow.show();
    } else if (typeName.indexOf("标准") != -1) {
        selectStandardWindow.show();
    } else if (typeName.indexOf("软件著作权") != -1) {
        selectRjzzWindow.show();
    } else if (typeName.indexOf("论文") != -1 || typeName.indexOf("期刊") != -1) {
        selectLwWindow.show();
    } else if (typeName.indexOf("技术封装") != -1) {
        selectJsWindow.show();
    } else if (typeName.indexOf("FMEA") != -1) {
        selectFMEAWindow.show();
    }
    searchOutList();
}


//查询
function searchOutList() {
    var queryParam = [];
    if (typeName.indexOf("国内发明专利") != -1 || typeName.indexOf("国内实用新型专利") != -1 || typeName.indexOf("国内外观专利") != -1) {
        var applicationNumber = $.trim(mini.get("zl_applicationNumber").getValue());
        if (applicationNumber) {
            queryParam.push({name: "applicationNumber", value: applicationNumber});
        }
        var patentName = $.trim(mini.get("zl_patentName").getValue());
        if (patentName) {
            queryParam.push({name: "patentName", value: patentName});
        }
    } else if (typeName.indexOf("专利") != -1) {
        var applictonNumber = $.trim(mini.get("gjzl_applictonNumber").getValue());
        if (applictonNumber) {
            queryParam.push({name: "applictonNumber", value: applictonNumber});
        }
        var pctName = $.trim(mini.get("gjzl_pctName").getValue());
        if (pctName) {
            queryParam.push({name: "pctName", value: pctName});
        }
    } else if (typeName.indexOf("标准") != -1) {
        var systemCategoryId = $.trim(mini.get("filterSystemCategory").getValue());
        if (systemCategoryId) {
            queryParam.push({name: "systemCategoryId", value: systemCategoryId});
        }
        var standardNumber = $.trim(mini.get("filterStandardNumberId").getValue());
        if (standardNumber) {
            queryParam.push({name: "standardNumber", value: standardNumber});
        }
        var standardName = $.trim(mini.get("filterStandardNameId").getValue());
        if (standardName) {
            queryParam.push({name: "standardName", value: standardName});
        }
    } else if (typeName.indexOf("软件著作权") != -1) {

        var rjzzNum = $.trim(mini.get("rjzzNum").getValue());
        if (rjzzNum) {
            queryParam.push({name: "rjzzNum", value: rjzzNum});
        }
        var rjmqc = $.trim(mini.get("rjmqc").getValue());
        if (rjmqc) {
            queryParam.push({name: "rjmqc", value: rjmqc});
        }
    } else if (typeName.indexOf("论文") != -1 || typeName.indexOf("期刊") != -1) {
        var kjlwNum = $.trim(mini.get("kjlwNum").getValue());
        if (kjlwNum) {
            queryParam.push({name: "kjlwNum", value: kjlwNum});
        }
        var kjlwName = $.trim(mini.get("kjlwName").getValue());
        if (kjlwName) {
            queryParam.push({name: "kjlwName", value: kjlwName});
        }
    } else if (typeName.indexOf("技术封装") != -1) {
        var jsNum = $.trim(mini.get("jsNum").getValue());
        if (jsNum) {
            queryParam.push({name: "jsNum", value: jsNum});
        }
        var jsName = $.trim(mini.get("jsName").getValue());
        if (jsName) {
            queryParam.push({name: "jsName", value: jsName});
        }
        var ytxm = $.trim(mini.get("ytxm").getValue());
        if (ytxm) {
            queryParam.push({name: "ytxm", value: ytxm});
        }
    } else if (typeName.indexOf("FMEA") != -1) {
        var jixing = $.trim(mini.get("jixing").getValue());
        if (jixing) {
            queryParam.push({name: "jixing", value: jixing});
        }
        var analyseName = $.trim(mini.get("analyseName").getValue());
        if (analyseName) {
            queryParam.push({name: "analyseName", value: analyseName});
        }
        var creator = $.trim(mini.get("creator").getValue());
        if (creator) {
            queryParam.push({name: "creator", value: creator});
        }
    }

    var inputList = '';
    if (typeName.indexOf("国内发明专利") != -1 || typeName.indexOf("国内实用新型专利") != -1 || typeName.indexOf("国内外观专利") != -1) {
        inputList = zlListGrid;
    } else if (typeName.indexOf("专利") != -1) {
        inputList = gjZlListGrid;
    } else if (typeName.indexOf("标准") != -1) {
        inputList = standardListGrid;
    } else if (typeName.indexOf("软件著作权") != -1) {
        inputList = rjzzListGrid;
    } else if (typeName.indexOf("论文") != -1 || typeName.indexOf("期刊") != -1) {
        inputList = lwListGrid;
    } else if (typeName.indexOf("技术封装") != -1 ) {
        inputList = jsListGrid;
    } else if (typeName.indexOf("FMEA") != -1 ) {
        inputList = fmeaListGrid;
    }
    var data = {};
    data.filter = mini.encode(queryParam);
    data.pageIndex = inputList.getPageIndex();
    data.pageSize = inputList.getPageSize();
    data.sortField = inputList.getSortField();
    data.sortOrder = inputList.getSortOrder();
    //查询
    inputList.load(data);
}

function onRowDblClick() {
    selectOutOK();
}
//todo:点击确认，成果计划返回的字段有所增加(老李mark暂留)
function selectOutOK() {
    var inputList = '';
    if (typeName.indexOf("国内发明专利") != -1 || typeName.indexOf("国内实用新型专利") != -1 || typeName.indexOf("国内外观专利") != -1) {
        inputList = zlListGrid;
    } else if (typeName.indexOf("专利") != -1) {
        inputList = gjZlListGrid;
    } else if (typeName.indexOf("标准") != -1) {
        inputList = standardListGrid;
    } else if (typeName.indexOf("软件著作权") != -1) {
        inputList = rjzzListGrid;
    } else if (typeName.indexOf("论文") != -1 || typeName.indexOf("期刊") != -1) {
        inputList = lwListGrid;
    } else if (typeName.indexOf("技术封装") != -1 ) {
        inputList = jsListGrid;
    } else if (typeName.indexOf("FMEA") != -1 ) {
        inputList = fmeaListGrid;
    }
    var selectRow = inputList.getSelected();
    if (selectRow) {
        var outReferId = '';
        var outName = '';
        var outNumber = '';
        var updateOutRow = grid_project_out.getSelected();
        if (typeName.indexOf("国内发明专利") != -1 || typeName.indexOf("国内实用新型专利") != -1 || typeName.indexOf("国内外观专利") != -1) {
            outReferId = selectRow.zgzlId;
            outName = selectRow.patentName;
            outNumber = selectRow.applicationNumber;
            var zllxName = selectRow.zllxName;
            var gnztName = selectRow.gnztName;
            if (updateOutRow) {
                grid_project_out.updateRow(updateOutRow, {
                    outReferId: outReferId, outName: outName, outNumber: outNumber,
                    zllxName: zllxName, gnztName: gnztName
                });
            }
        } else if (typeName.indexOf("专利") != -1) {
            outReferId = selectRow.gjzlId;
            outName = selectRow.pctName;
            outNumber = selectRow.applictonNumber;
            if (updateOutRow) {
                grid_project_out.updateRow(updateOutRow, {outReferId: outReferId, outName: outName, outNumber: outNumber});
            }
        } else if (typeName.indexOf("标准") != -1) {
            outReferId = selectRow.id;
            outName = selectRow.standardName;
            outNumber = selectRow.standardNumber;
            var categoryName = selectRow.categoryName;
            var standardStatus = selectRow.standardStatus;
            if (updateOutRow) {
                grid_project_out.updateRow(updateOutRow, {
                    outReferId: outReferId, outName: outName, outNumber: outNumber,
                    categoryName: categoryName, standardStatus: standardStatus
                });
            }
        } else if (typeName.indexOf("软件著作权") != -1) {
            outReferId = selectRow.rjzzId;
            outName = selectRow.rjmqc;
            outNumber = selectRow.rjzzNum;
            var zpsm = selectRow.zpsm;
            var fbzt = selectRow.fbzt;
            if (updateOutRow) {
                grid_project_out.updateRow(updateOutRow, {outReferId: outReferId, outName: outName, outNumber: outNumber, zpsm: zpsm, fbzt: fbzt});
            }
        } else if (typeName.indexOf("论文") != -1 || typeName.indexOf("期刊") != -1) {
            outReferId = selectRow.kjlwId;
            outName = selectRow.kjlwName;
            outNumber = selectRow.kjlwNum;
            if (updateOutRow) {
                grid_project_out.updateRow(updateOutRow, {outReferId: outReferId, outName: outName, outNumber: outNumber});
            }
        } else if (typeName.indexOf("技术封装") != -1 ) {
            if(selectRow.jsfzrId!=currentUserId){
                mini.alert("只能选择技术负责人为自己的技术！");
                return;
            }
            outReferId = selectRow.jssjkId;
            outName = selectRow.jsName;
            outNumber = selectRow.jsNum;
            if (updateOutRow) {
                grid_project_out.updateRow(updateOutRow, {outReferId: outReferId, outName: outName, outNumber: outNumber});
            }
        } else if (typeName.indexOf("FMEA") != -1 ) {
            outReferId = selectRow.id;
            outName = selectRow.analyseName;
            outNumber = selectRow.jixing;
            var femaType = selectRow.femaType;
            if (updateOutRow) {
                grid_project_out.updateRow(updateOutRow, {outReferId: outReferId, outName: outName, outNumber: outNumber,femaType:femaType});
            }
        }

    } else {
        mini.alert("请选择一条数据！");
        return;
    }
    selectOutHide();
}

function selectOutHide() {
    if (typeName.indexOf("国内发明专利") != -1 || typeName.indexOf("国内实用新型专利") != -1 || typeName.indexOf("国内外观专利") != -1) {
        selectZlWindow.hide();
        mini.get("zl_applicationNumber").setValue('');
        mini.get("zl_patentName").setValue('');
    } else if (typeName.indexOf("专利") != -1) {
        selectGjZlWindow.hide();
        mini.get("gjzl_applictonNumber").setValue('');
        mini.get("gjzl_pctName").setValue('');
    } else if (typeName.indexOf("标准") != -1) {
        selectStandardWindow.hide();
        mini.get("filterSystemCategory").setValue('JS');
        mini.get("filterStandardNumberId").setValue('');
        mini.get("filterStandardNameId").setValue('');
    } else if (typeName.indexOf("软件著作权") != -1) {
        selectRjzzWindow.hide();
        mini.get("rjzzNum").setValue('');
        mini.get("rjmqc").setValue('');
    } else if (typeName.indexOf("论文") != -1 || typeName.indexOf("期刊") != -1) {
        selectLwWindow.hide();
        mini.get("kjlwNum").setValue('');
        mini.get("kjlwName").setValue('');
    } else if (typeName.indexOf("技术封装") != -1 ) {
        selectJsWindow.hide();
        mini.get("jsNum").setValue('');
        mini.get("xmNum").setValue('');
        mini.get("jsName").setValue('');
    } else if (typeName.indexOf("FMEA") != -1 ) {
        selectFMEAWindow.hide();
        mini.get("jixing").setValue('');
        mini.get("analyseName").setValue('');
        mini.get("creator").setValue('');
    }
}