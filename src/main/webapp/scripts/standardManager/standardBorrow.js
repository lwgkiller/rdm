$(function () {
    mini.get("standardBorrowSystemCategory").load(systemCategoryArray);
    standardBorrowListGrid.loadData(borrowStandardArray);
});

function selectSystem() {
    var systemCategoryId = mini.get("standardBorrowSystemCategory").getValue();
    if (!systemCategoryId) {
        mini.alert('请选择标准体系类别！');
        return;
    }
    var url=jsUseCtxPath+"/standardManager/core/standardSystem/treeQuery.do?systemCategoryId="+systemCategoryId;
    selectSystemTree.load(url);
    selectSystemWindow.show();
    mini.get("filterSystemNameId").setValue('');
}

function selectSystemOK() {
    var targetNode = selectSystemTree.getSelectedNode();

    if (targetNode && selectSystemTree.isLeaf(targetNode)) {
        mini.get("systemTreeSelectId").setText(targetNode.systemName);
        mini.get("systemTreeSelectId").setValue(targetNode.id);
        selectSystemHide();
    } else {
        mini.alert("请选择一个非父节点");
    }

}

function selectSystemHide() {
    selectSystemWindow.hide();

}

//查找体系节点
function filterSystemTree() {
    var key = mini.get("filterSystemNameId").getValue();
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
            // systemTree.selectNode(firstNode);
            selectSystemTree.scrollIntoView(firstNode);
        }
    }
}

function callBack(standardToId) {
    $.ajax({
        url:jsUseCtxPath+"/standardManager/core/standard/callBackBorrow.do?standardToId="+standardToId,
        success:function (result) {
            mini.alert(result.message);
            searchBorrowList();
        }
    });
}

function searchBorrowList() {
    standardBorrowListGrid.load();
}

function addBorrow() {
    var data={"borrowFromId":standardFromId,"borrowToSystemId":mini.get("systemTreeSelectId").getValue()};
    data=mini.encode(data);
    $.ajax({
        url:jsUseCtxPath+"/standardManager/core/standard/addBorrow.do",
        type:'POST',
        contentType: 'application/json',
        data:data,
        success:function (result) {
            mini.alert(result.message);
            searchBorrowList();
        }
    });
}

function changeBorrowSystemCategory() {
    mini.get("systemTreeSelectId").setValue('');
    mini.get("systemTreeSelectId").setText('');
}