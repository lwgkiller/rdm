$(function () {
    setStandardCategoryAndBelongDep();
    setQueryYear();
    searchFrm();
    //查询标准体系类别
    $.ajax({
        url: jsUseCtxPath +'/standardManager/core/standardSystem/queryCategory.do',
        success:function (data) {
            if(data) {
                mini.get("systemCategoryId").load(data);
                mini.get("systemCategoryId").setValue(systemCategoryValue);
                searchFrm()
            }
        }
    });
});


function setQueryYear() {
    var publishTimeFromData=generateYearSelect("from");
    var publishTimeToData=generateYearSelect("to");
    mini.get("publishTimeFrom").load(publishTimeFromData);
    mini.get("publishTimeTo").load(publishTimeToData);
    var nowY=new Date().getFullYear();
    var initFromY=nowY-4;
    mini.get("publishTimeFrom").setValue(initFromY+'-01-01 00:00:00');
    mini.get("publishTimeTo").setValue(nowY+'-12-31 24:00:00');
}

function generateYearSelect(fromOrTo) {
    var data=[];
    var nowDate=new Date();
    var startY=nowDate.getFullYear()-10;
    var endY=nowDate.getFullYear()+30;
    for(var i=startY;i<=endY;i++) {
        var oneData={};
        oneData.key=i+'年';
        if(fromOrTo=='from') {
            oneData.value=i+'-01-01 00:00:00';
        } else if(fromOrTo=='to') {
            oneData.value=i+'-12-31 24:00:00';
        }
        data.push(oneData);
    }
    return data;
}

function setStandardCategoryAndBelongDep() {
    $.ajax({
        url: jsUseCtxPath + '/standardManager/core/standard/getStandardSelectInfos.do',
        type: 'get',
        contentType: 'application/json',
        success: function (data) {
            if (data) {
                mini.get("category").load(data.category);
                mini.get("belongDep").load(data.belongDep);
            }
        }
    });
}

function clearGroupByPublishSearch() {
    //清理条件
    mini.get("systemCategoryId").setValue(systemCategoryValue);
    mini.get("publishTimeFrom").setValue("");
    mini.get("publishTimeTo").setValue("");
    mini.get("status").setValue("");
    mini.get("selectedSystemIds").setValue("");
    mini.get("systemTreeSelect").setValue("");
    mini.get("systemTreeSelect").setText("");
    mini.get("category").setValue("");
    mini.get("belongDep").setValue("");

    searchFrm();
}

function selectSystem() {
    var url=jsUseCtxPath+"/standardManager/core/standardSystem/treeQuery.do?systemCategoryId="+mini.get("systemCategoryId").getValue()
    selectSystemTree.setUrl(url);
    selectSystemWindow.show();
    mini.get("filterNameId").setValue('');
}

//查找体系节点
function filterSystemTree() {
    var key = mini.get("filterNameId").getValue();
    if (key == "") {
        selectSystemTree.clearFilter();
    } else {
        key = key.toLowerCase();
        var nodes = selectSystemTree.findNodes(function (node) {
            var systemName = node.systemName ? node.systemName.toLowerCase() : "";
            if (systemName.indexOf(key) != -1) {
                return true;
            }
        });
        //展开所有找到的节点
        for (var i = 0, l = nodes.length; i < l; i++) {
            var node = nodes[i];
            selectSystemTree.expandPath(node);
        }

        //第一个节点选中并滚动到视图
        var firstNode = nodes[0];
        if (firstNode) {
            selectSystemTree.scrollIntoView(firstNode);
        }
    }
}

function okWindow() {
    var targetNode = selectSystemTree.getSelectedNode();

    if (targetNode) {
        mini.get("systemTreeSelect").setText(targetNode.systemName);
        mini.get("systemTreeSelect").setValue(targetNode.id);
        //查找所有子节点
        //体系Id
        var systemIdArr = [];
        systemIdArr.push(targetNode.id);
        var allChild = selectSystemTree.getAllChildNodes(targetNode);
        for (var i = 0; i < allChild.length; i++) {
            systemIdArr.push(allChild[i].id);
        }
        var systemIdStr=systemIdArr.join(",");
        mini.get("selectedSystemIds").setValue(systemIdStr);

        selectSystemWindow.hide();
    } else {
        mini.alert(standardGroupByPublish_name1);
    }

}

function hideWindow() {
    selectSystemWindow.hide();

}

function exportPublishReport() {
    var queryParam = [];
    queryParam.push({name: "selectedSystemIds", value: mini.get("selectedSystemIds").getValue()});
    var standardCategoryId = $.trim(mini.get("category").getValue());
    if (standardCategoryId) {
        queryParam.push({name: "standardCategoryId", value: standardCategoryId});
    }
    var standardStatus = $.trim(mini.get("status").getValue());
    if (standardStatus) {
        queryParam.push({name: "standardStatus", value: standardStatus});
    }
    var belongDepId = $.trim(mini.get("belongDep").getValue());
    if (belongDepId) {
        queryParam.push({name: "belongDepId", value: belongDepId});
    }
    var publishTimeFrom = $.trim(mini.get("publishTimeFrom").getValue());
    if (publishTimeFrom) {
        queryParam.push({name: "publishTimeFrom", value: publishTimeFrom});
    }
    var publishTimeTo = $.trim(mini.get("publishTimeTo").getValue());
    if (publishTimeTo) {
        queryParam.push({name: "publishTimeTo", value: publishTimeTo});
    }
    queryParam.push({name: "systemCategoryId", value: mini.get("systemCategoryId").getValue()});
    $("#filter").val(mini.encode(queryParam));
    var excelForm = $("#excelForm");
    excelForm.submit();
}