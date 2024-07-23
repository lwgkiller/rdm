<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
	<title>标准体系管理</title>
	<%@include file="/commons/list.jsp"%>
	<script src="${ctxPath}/scripts/common/UUID.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-splitter" style="width:100%;height:100%;">
	<div size="15%" showCollapseButton="true">
		<div id="systemSearch" class="mini-toolbar" style="margin-bottom: 15px;padding:2px;border-top:0;border-left:0;border-right:0;">
			<a id="add" style="margin-bottom: 15px;margin-top: 10px" class="mini-button" onclick="onAddSubNode()">插入子节点</a><br>
			<a id="edit" style="margin-bottom: 15px" class="mini-button" onclick="onEditNode('edit')">编辑</a>
			<a id="remove" style="margin-bottom: 15px" class="mini-button btn-red" onclick="onRemoveNode()">删除</a>
			<a id="save" style="margin-bottom: 15px" class="mini-button"  onclick="onSaveNode()">保存</a>
			<p style="display: inline-block;color: red;font-size:14px;vertical-align: middle">（<image src="${ctxPath}/styles/images/warn.png" style="margin-right:5px;vertical-align: middle;height: 15px"/>所有操作后均需“保存”后生效）</p>
		</div>
		<hr color="#ddd"/>
		<div class="mini-fit">
			<ul id="systemTree" class="mini-tree"
				url="${ctxPath}/key/core/keyDesign/treeQuery.do?codeId=${codeId}"
				style="width:100%;height:98%;padding:5px;" onnodedblclick="leafNodeDbClick()"
				showTreeIcon="true" textField="codeName" expandOnLoad="0" idField="id" parentField="parentId"
				resultAsTree="false"
			></ul>
		</div>
	</div>
	<div class="mini-fit" style="height: 100%;">
		<div id="systemTab" class="mini-tabs" activeIndex="0" style="width:100%;height:100%;">
			<div title="技术标准" name="BZ" refreshOnClick="true"></div>
			<div title="技术参数" name="CS" refreshOnClick="true"></div>
			<div title="改进履历" name="GJ" refreshOnClick="true"></div>
			<div title="验证报告" name="YZ" refreshOnClick="true"></div>
		</div>
	</div>
</div>
<script type="text/javascript">
    mini.parse();
	var ctxPath="${ctxPath}";
    var codeId="${codeId}";
    var isGlr=${isGlr};
    var systemTree=mini.get("systemTree");

    $(function () {
        if(!isGlr){
            mini.get("add").setEnabled(false);
            mini.get("edit").setEnabled(false);
            mini.get("save").setEnabled(false);
            mini.get("remove").setEnabled(false);
		}
    });

    systemTree.on("nodeselect", function (e) {
        var selectNode = systemTree.getSelectedNode();
        var tabs=mini.get("systemTab");

        var tab1=tabs.getTab("BZ");
        var newUrl=ctxPath+"/jsbz/jsbzListPage.do?type="+selectNode.id;
        tabs.updateTab(tab1,{url:newUrl,loadedUrl:newUrl});

        var tab2=tabs.getTab("CS");
        var newUrl2=ctxPath+"/jscs/jscsListPage.do?type="+selectNode.id;
        tabs.updateTab(tab2,{url:newUrl2,loadedUrl:newUrl2});

        var tab3=tabs.getTab("GJ");
        var newUrl3=ctxPath+"/Gjll/keyGjllListPage.do?type="+selectNode.id;
        tabs.updateTab(tab3,{url:newUrl3,loadedUrl:newUrl3});

        var tab4=tabs.getTab("YZ");
        var newUrl4=ctxPath+"/yzbg/yzbgListPage.do?type="+selectNode.id;
        tabs.updateTab(tab4,{url:newUrl4,loadedUrl:newUrl4});

        var activeTab=tabs.getActiveTab();
        tabs.reloadTab(activeTab);
    });

    var nodes=systemTree.getList();
    for(var i=0;i<nodes.length;i++){
        if(systemTree.isLeaf(nodes[i])){
            systemTree.select(nodes[i]);
            break;
        }
    }

    function onAddSubNode() {
        var selectNode = systemTree.getSelectedNode();
        if(!selectNode) {
            mini.alert('请选择一个节点');
            return;
        }
        var newNode = {id:Math.uuidFast(),codeName:"undefined_"+Date.now(),codeId:codeId,parentId:selectNode.id};
        systemTree.addNode(newNode, "add", selectNode);
        systemTree.selectNode(newNode);
        systemTree.scrollIntoView(newNode);
    }

    function onSaveNode() {
        if(systemTree.isChanged()) {
            var message="数据保存成功！";
            var changes=systemTree.getChanges();
            var json = mini.encode(changes);
            $.ajax({
                url: ctxPath+"/key/core/keyDesign/treeSave.do",
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

    function refreshSystemTree() {
        systemTree.load();
    }

    function onEditNode(action) {
        var selectNode = systemTree.getSelectedNode();
        if(!selectNode) {
            mini.alert('请选择一个节点');
            return;
        }
        mini.open({
            title: "编辑节点",
            url: ctxPath + "/key/core/keyDesign/treeEdit.do",
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

    function onUpdateNode(options) {
        var selectNode = systemTree.getSelectedNode();
        systemTree.updateNode(selectNode,options);
    }

    function leafNodeDbClick() {
        var selectNode = systemTree.getSelectedNode();
        if(systemTree.isLeaf(selectNode)) {
            onEditNode('detail');
        }
    }

    function onRemoveNode() {
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
                    var belongbj={id:selectNode.id};
                    $.ajax({
                        url: ctxPath+"/key/core/keyDesign/queryBySystemIds.do",
                        data:mini.encode(belongbj),
                        type: "post",
                        contentType: 'application/json',
                        async:false,
                        success: function (data) {
                            if (data && data.num==0) {
                                systemTree.removeNode(selectNode);
                            } else {
                                mini.alert("节点存在关联标准或技术参数，暂时无法删除！");
                            }
                        }
                    });
                }
            }
        );
    }


</script>
</body>
</html>