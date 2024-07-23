function exportSystem() {
    // var params=[];
    // var obj={};
    // obj.name="systemCategoryId";
    // obj.value=tabName;
    // params.push(obj);
    // $("#filter").val(mini.encode(params));
    // $("#pageIndex").val(0);
    // $("#pageSize").val(100000);
    var excelForm = $("#excelForm");
    excelForm.submit();
}

//刷新
function refreshSystemTree() {
    systemTree.load();
}

//展开
function expandAll() {
    systemTree.expandAll();
}

//收缩
function collapseAll() {
    systemTree.collapseAll();
}

//查找节点
function filterSystemTree() {
    systemTree.selectNode(null);
    systemTree.clearFilter();
    collapseAll();
    var nameFilter =$.trim(mini.get("filterNameId").getValue());
    var numberFilter =$.trim(mini.get("filterNumberId").getValue());
    if(nameFilter || numberFilter){
        nameFilter = nameFilter.toLowerCase();
        numberFilter=numberFilter.toLowerCase();
        var nodes=systemTree.findNodes(function (node) {
            var systemName = node.systemName ? node.systemName.toLowerCase() : "";
            var systemNumber= node.systemNumber ? node.systemNumber.toLowerCase() : "";
            if(nameFilter && systemName.indexOf(nameFilter)==-1) {
                return false;
            }
            if(numberFilter && systemNumber.indexOf(numberFilter)==-1) {
                return false;
            }
            return true;
        });
        //展开所有找到的节点
        for (var i = 0, l = nodes.length; i < l; i++) {
            var node = nodes[i];
            systemTree.expandPath(node);
        }

        //第一个节点选中并滚动到视图
        var firstNode = nodes[0];
        if (firstNode) {
            systemTree.selectNode(firstNode);
            systemTree.scrollIntoView(firstNode);
        }
    }
}

//节点前添加
function onAddBefore() {
    var pointManager=whetherIsPointStandardManager(tabName,currentUserRoles);
    if(!pointManager) {
        mini.alert('没有操作权限');
        return;
    }
    var selectNode = systemTree.getSelectedNode();
    if(!selectNode) {
        mini.alert('请选择一个节点');
        return;
    }
    if(!selectNode.parentId) {
        mini.alert('不允许增加根节点');
        return;
    }
    var newNode = {id:Math.uuidFast(),systemName:"undefined_"+Date.now(),systemCategoryId:tabName,parentId:selectNode.parentId};
    systemTree.addNode(newNode, "before", selectNode);
    systemTree.selectNode(newNode);
    systemTree.scrollIntoView(newNode);
}

//节点后添加
function onAddAfter() {
    var pointManager=whetherIsPointStandardManager(tabName,currentUserRoles);
    if(!pointManager) {
        mini.alert('没有操作权限');
        return;
    }
    var selectNode = systemTree.getSelectedNode();
    if(!selectNode) {
        mini.alert('请选择一个节点');
        return;
    }
    if(!selectNode.parentId) {
        mini.alert('不允许增加根节点');
        return;
    }
    var newNode = {id:Math.uuidFast(),systemName:"undefined_"+Date.now(),systemCategoryId:tabName,parentId:selectNode.parentId};
    systemTree.addNode(newNode, "after", selectNode);
    systemTree.selectNode(newNode);
    systemTree.scrollIntoView(newNode);
}

//子节点添加
function onAddSubNode() {
    var pointManager=whetherIsPointStandardManager(tabName,currentUserRoles);
    if(!pointManager) {
        mini.alert('没有操作权限');
        return;
    }
    var selectNode = systemTree.getSelectedNode();
    if(!selectNode) {
        mini.alert('请选择一个节点');
        return;
    }
    var newNode = {id:Math.uuidFast(),systemName:"undefined_"+Date.now(),systemCategoryId:tabName,parentId:selectNode.id};
    systemTree.addNode(newNode, "add", selectNode);
    systemTree.selectNode(newNode);
    systemTree.scrollIntoView(newNode);
}

//编辑节点打开界面
function onEditNode(action) {
    var pointManager=whetherIsPointStandardManager(tabName,currentUserRoles);
    if(!pointManager) {
        mini.alert('没有操作权限');
        return;
    }
    var selectNode = systemTree.getSelectedNode();
    if(!selectNode) {
        mini.alert('请选择一个节点');
        return;
    }
    mini.open({
        title: "编辑节点",
        url: jsUseCtxPath + "/standardManager/core/standardSystem/treeEdit.do",
        width: 750,
        height: 450,
        showModal:true,
        allowResize: true,
        onload: function () {
            var iframe = this.getIFrameEl();
            var data = { node: selectNode,action:action };
            iframe.contentWindow.SetData(data);
        }
    });
}


//更新节点
function onUpdateNode(options) {
    var selectNode = systemTree.getSelectedNode();
    systemTree.updateNode(selectNode,options);
}

//删除节点
function onRemoveNode() {
    var pointManager=whetherIsPointStandardManager(tabName,currentUserRoles);
    if(!pointManager) {
        mini.alert('没有操作权限');
        return;
    }
    var selectNode = systemTree.getSelectedNode();
    if(!selectNode) {
        mini.alert('请选择一个节点');
        return;
    }
    mini.confirm("确定删除选中节点？", "确定？",
        function (action) {
            if (action == "ok") {
                //根节点不允许删除
                if(!selectNode.parentId) {
                    mini.alert('根节点不允许删除');
                    return;
                }

                //判断该节点及所有子节点是否有关联的标准，如果有则不允许删除
                var allChild=systemTree.getAllChildNodes(selectNode);
                var systemIdArr=[{id:selectNode.id}];
                for(var i=0;i<allChild.length;i++) {
                    systemIdArr.push({id:allChild[i].id});
                }
                $.ajax({
                    url: jsUseCtxPath+"/standardManager/core/standardSystem/queryStandardBySystemIds.do",
                    data: mini.encode(systemIdArr),
                    type: "post",
                    contentType: 'application/json',
                    async:false,
                    success: function (data) {
                        if (data && data.num==0) {
                            systemTree.removeNode(selectNode);
                        } else {
                            mini.alert("节点或子节点存在关联的标准，不能删除！");
                        }
                    }
                });
            }
        }
    );
}

//保存节点
function onSaveNode() {
    var pointManager=whetherIsPointStandardManager(tabName,currentUserRoles);
    if(!pointManager) {
        mini.alert('没有操作权限');
        return;
    }
    if(systemTree.isChanged()) {
        var message="数据保存成功！";
        var changes=systemTree.getChanges();
        var json = mini.encode(changes);
        $.ajax({
            url: jsUseCtxPath+"/standardManager/core/standardSystem/treeSave.do",
            data: json,
            type: "post",
            contentType: 'application/json',
            async:false,
            success: function (data) {
                if (data && data.message) {
                    message = data.message;
                    mini.alert(message);
                    refreshSystemTree();
                }
            }
        });
    }else {
        mini.alert("数据保存成功！");
    }
}


//移动节点
function onMoveNode(e) {
    var pointManager=whetherIsPointStandardManager(tabName,currentUserRoles);
    if(!pointManager) {
        mini.alert('没有操作权限');
        return;
    }
    var selectNode = systemTree.getSelectedNode();
    if (selectNode) {
        if(!selectNode.parentId) {
            mini.alert('不允许移动根节点');
            return;
        }
        moveWindow.show();
        fillMoveTree(systemTree.getData());
    } else {
        mini.alert("请选择一个节点");
    }
}

function fillMoveTree(treeData) {
    treeData = mini.clone(treeData);
    moveTree.loadData(treeData);
    $("#moveAction").val("add");
}
function okWindow() {
    var moveNode = systemTree.getSelectedNode();
    var targetNode = moveTree.getSelectedNode();
    var moveAction = $("#moveAction").val();

    if (moveNode && targetNode && moveAction) {
        targetNode = systemTree.getNode(targetNode.id);
        if (systemTree.isAncestor(moveNode, targetNode)) {
            mini.alert("移动节点不能是目标节点的父节点或本身");
            return;
        }
        if(moveAction=="before"||moveAction=="after") {
            moveNode.parentId=targetNode.parentId;
        } else if(moveAction=="add") {
            moveNode.parentId=targetNode.id;
        }
        systemTree.moveNode(moveNode, targetNode, moveAction);
        moveWindow.hide();
    } else {
        mini.alert("请选择一个目标节点");
    }

}
function hideWindow() {
    moveWindow.hide();
}

function leafNodeDbClick() {
    var selectNode = systemTree.getSelectedNode();
    if(systemTree.isLeaf(selectNode)) {
        onEditNode('detail');
    }
}
