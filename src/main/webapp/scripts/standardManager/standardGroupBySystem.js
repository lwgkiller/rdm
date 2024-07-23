$(function () {
    setStandardCategoryAndBelongDep();
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

function clearGroupBySystemSearch() {
    //清理条件
    mini.get("systemCategoryId").setValue(systemCategoryValue);
    mini.get("systemTreeSelect").setValue("");
    mini.get("systemTreeSelect").setText("");
    mini.get("selectedSystemIds").setValue("");
    mini.get("status").setValue("");
    mini.get("category").setValue("");
    mini.get("belongDep").setValue("");
    mini.get("publishTimeFrom").setValue("");
    mini.get("publishTimeTo").setValue("");

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
        mini.alert(standardGroupBySystem_name1);
    }

}

function hideWindow() {
    selectSystemWindow.hide();

}

function exportSystemReport() {
    var queryParam = [];
    queryParam.push({name: "systemCategoryId", value: mini.get("systemCategoryId").getValue()});
    queryParam.push({name: "selectedSystemIds", value: mini.get("selectedSystemIds").getValue()});
    var standardStatus = $.trim(mini.get("status").getValue());
    if (standardStatus) {
        queryParam.push({name: "standardStatus", value: standardStatus});
    }
    var standardCategoryId = $.trim(mini.get("category").getValue());
    if (standardCategoryId) {
        queryParam.push({name: "standardCategoryId", value: standardCategoryId});
    }

    var belongDepId = $.trim(mini.get("belongDep").getValue());
    if (belongDepId) {
        queryParam.push({name: "belongDepId", value: belongDepId});
    }
    var publishTimeFrom = $.trim(mini.get("publishTimeFrom").getText());
    if (publishTimeFrom) {
        queryParam.push({name: "publishTimeFrom", value: publishTimeFrom});
    }
    var publishTimeTo = $.trim(mini.get("publishTimeTo").getText());
    if (publishTimeTo) {
        queryParam.push({name: "publishTimeTo", value: publishTimeTo});
    }

    $("#filter").val(mini.encode(queryParam));
    var excelForm = $("#excelForm");
    excelForm.submit();
}