$(function () {
    querySystemCategoryInfos();
    if(action!='add'){
        selectSystemTree.loadData(systemArray);
        groupForm.setData(applyObj);
    }
});

function saveData() {
    //判断表单内容是否完整
    var formData = groupForm.getData();
    var checkResult=groupFormValid(formData);
    if(!checkResult) {
        return;
    }
    var config = {
        url: jsUseCtxPath+"/standardManager/core/subManagerGroup/save.do",
        method: 'POST',
        data: formData,
        success: function (result) {
            //如果存在自定义的函数，则回调
            var result=mini.decode(result);
            if(result.success){
                CloseWindow('ok');
            }
        }
    };
    _SubmitJson(config);
}

function groupFormValid(formData) {
    if(!formData) {
        mini.alert('请填写表单！');
        return false;
    }
    if(!$.trim(formData.groupName)) {
        mini.alert('请填写用户组名称！');
        return false;
    }
    if(!$.trim(formData.systemCategoryId)) {
        mini.alert('请选择标准体系类别！');
        return false;
    }
    if(!$.trim(formData.systemId)) {
        mini.alert('请选择标准体系！');
        return false;
    }
    // if(!$.trim(formData.recUser)) {
    //     mini.alert('请添加组内人员！');
    //     return false;
    // }
    return true;
}

function querySystemCategoryInfos() {
    $.ajax({
        url: jsUseCtxPath + '/standardManager/core/standardSystem/queryCategory.do',
        sync:false,
        success: function (data) {
            if (data) {
                mini.get("systemCategory").load(data);
            }
        }
    });
}

function systemCategoryChanged() {
    mini.get("systemTreeSelectId").setValue('');
    mini.get("systemTreeSelectId").setText('');
    var systemCategoryId = mini.get("systemCategory").getValue();
    //判断是否有选择该体系类别的权限
    if(!systemCategoryIdSet||systemCategoryIdSet.length==0||systemCategoryIdSet.indexOf(systemCategoryId)==-1) {
        mini.alert('当前用户没有选择该类别的权限！');
        mini.get("systemCategory").setValue('');
        mini.get("systemCategory").setText('');
        return;
    }
}

function selectSystem() {
    var systemCategoryId = mini.get("systemCategory").getValue();
    if (!systemCategoryId) {
        mini.alert('请选择标准体系类别！');
        return;
    }
    var url=jsUseCtxPath+"/standardManager/core/standardSystem/treeQuery.do?systemCategoryId="+systemCategoryId;
    selectSystemTree.load(url);
    selectSystemWindow.show();
    mini.get("filterNameId").setValue('');
}

function selectSystemOK() {
    var targetNode = selectSystemTree.getSelectedNode();
    if (targetNode) {
        mini.get("systemTreeSelectId").setText(targetNode.systemName);
        mini.get("systemTreeSelectId").setValue(targetNode.id);
        selectSystemHide();
    } else {
        mini.alert("请选择一个节点");
    }

}

function selectSystemHide() {
    selectSystemWindow.hide();
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
            selectSystemTree.selectNode(firstNode);
            selectSystemTree.scrollIntoView(firstNode);
        }
    }
}
