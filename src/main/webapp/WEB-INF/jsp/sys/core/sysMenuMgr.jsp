<%--
	菜单管理功能
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>系统菜单管理</title>
<%-- 	<link href="${ctxPath}/styles/form.css" rel="stylesheet" type="text/css" /> --%>
     <%--<%@include file="/commons/get.jsp"%>--%>
     <%@include file="/commons/edit.jsp"%>
     <script src="${ctxPath}/scripts/share/dialog.js" type="text/javascript"></script>

	<style type="text/css">
		.mini-layout-region-west .mini-iconfont:before{font-size:22px;line-height: 26px}
		#center .mini-tree-icon:before{margin-top:2px;font-size:22px;}
		#center .icon-button:before{font-size:16px;line-height: 28px;}
		.mini-layout-border>#center{
			background: transparent;
		}
		.mini-tree .mini-grid-viewport{
			background: #fff;
		}
		.icon-shang-add,
		.icon-xia-add{
			color: #0daaf6;
		}
		.icon-jia{
			color: #ff8b00;
		}
		.icon-save{
			color:#2cca4e;
		}
		.icon-zhuanhuan{
			color: #9e7ed0;;
		}
		.icon-trash{
			color: red;
		}
		.icon-grant{
			color: #66b1ff;
		}
	</style>
</head>
<body>
	<ul id="treeMenu" class="mini-contextmenu" >
	    <li  class=" btn-red" onclick="delSystem">删除子系统</li>
	    <li  onclick="editSystem">编辑子系统</li>
	    <li  onclick="addSystem">新增子系统</li>
	    <li  onclick="allotToSysInst">授权机构</li>
	    <li  onclick="transferSysMenus">迁移子系统菜单</li>
	</ul>
	<div id="layout1" class="mini-layout" style="width:100%;height:100%;">
	    <div
	    	region="west"
	    	width="210"
			title="菜单管理"
	    	showSplitIcon="true"
	    	showCollapseButton="false"
	    	showProxy="false"
    	>
			<div class="treeToolBar">
				<a class="mini-button"    onclick="addSystem()">新增</a>
				<a class="mini-button btn-red"  onclick="delSystem()">删除</a>
				<a class="mini-button"   onclick="refreshSystem()">刷新</a>
			</div>
			<div class="mini-fit">
	        	<ul id="leftTree" class="mini-tree" url="${ctxPath}/sys/core/sysMenuMgr/listAllSubSys.do" style="width:100%;height:100%;"
					showTreeIcon="true" textField="name" idField="sysId" resultAsTree="false"
	                onnodeclick="onClick"  contextMenu="#treeMenu"
				></ul>
			</div>
	    </div>

	    <div region="center" showHeader="false" >
			<div class="form-toolBox">
				<ul>
					<li><a class="mini-button"   onclick="onSave()">保存菜单</a></li>
					<li><a class="mini-button"   onclick="newRow();">新增菜单</a></li>
					<li><a class="mini-button"   onclick="newSubRow();">新增子菜单</a></li>
					<li><a class="mini-button btn-red"   onclick="delRow();">删除菜单</a></li>
					<li><a class="mini-button"   onclick="expand();">展开菜单</a></li>
					<li><a class="mini-button"   onclick="collapse();">收起菜单</a></li>
				</ul>
			</div>
			<div class="mini-fit">
				<div id="menuGrid" class="mini-treegrid" style="width:100%;height:100%;"
					resultAsTree="false"
					treeColumn="name" idField="menuId" parentField="parentId"
					expandOnLoad="true" allowAlternating="true"
					allowResize="true" onrowclick="onRowClick" oncellendedit="onMenuRowEndEdit"
					allowCellValid="true" oncellvalidation="onCellValidation"
					allowCellEdit="true" allowCellSelect="true" url="${ctxPath}/sys/core/sysMenuMgr/treeBySysId.do?sysId=${param.sysId}">
					<div property="columns">
						<div name="action" width="110"  renderer="onActionRenderer" align="center" headerAlign="center">操作</div>
						<div name="name" field="name" align="left" width="160">菜单名称
							<input property="editor" class="mini-textbox" style="width:100%;" required="true"/>
						</div>
						<div field="key" name="key" align="left" width="110">菜单Key
							<input property="editor" class="mini-textbox" style="width:100%;" required="true"/>
						</div>
					</div>
				</div>
			</div>
	    </div>
	    <div
	    	title="菜单明细"
	    	region="east"
	    	showHeader=""
	    	width="350"
	    	showSplitIcon="true"
	    	showCollapseButton="false"
	    	showProxy="false"
    	>
	    	<div class="treeToolBar">
	    		<a class="mini-button"  onclick="saveRowForm">保存</a>
	    	</div>
	    	<div class="mini-fit">
		    	<div id="menuForm" >
		    		<table class="table-detail column-two table-align" style="width:100%; " >
		    			<tr>
		    				<td>所属子系统</td>
		    				<td>
		    					<input id="sysId"
								   name="sysId"
								   class="mini-combobox"
								   url="${ctxPath}/sys/core/sysMenuMgr/listAllSubSys.do"
								   textField="name"
								   valueField="sysId"
								   style="width:90%"
								   required="true"
								/>
		    				</td>
		    			</tr>
		    			<tr>
		    				<td>菜单名称</td>
		    				<td>
		    					<input class="mini-textbox" name="name" maxlength="120" required="true" style="width:90%" />
		    					<input class="mini-hidden" id="_uid" name="_uid" />
		    					<input class="mini-hidden" id="menuId" name="menuId"/>
		    					<input class="mini-hidden" name="parentId"/>
		    				</td>
		    			</tr>
		    			<tr>
		    				<td>菜单Key</td>
		    				<td>
		    					<input class="mini-textbox" name="key" maxlength="120" required="true" style="width:90%"/>
		    				</td>
		    			</tr>

		    			<tr>
		    				<td>访问地址</td>
		    				<td>
		    					<input class="mini-textbox" name="url" style="width:90%"/>
		    				</td>
		    			</tr>
		    			<tr>
		    				<td>菜单图标</td>
		    				<td>
		    					<input name="iconCls" id="iconCls"  class="mini-buttonedit" onbuttonclick="selectIcon" style="width:80%"/>
		    					<a class="mini-button MyBlock" id="icnClsBtn"></a>
		    				</td>
		    			</tr>
		    			<tr >
		    				<td>序号</td>
		    				<td>
		    					<input name="sn" changeOnMousewheel="false" class="mini-spinner"  minValue="0" maxValue="100000"  onvaluechanged="changeSn"/>
		    				</td>
		    			</tr>
		    			<tr>
		    				<td>访问方式</td>
		    				<td>
		    					<div class="mini-radiobuttonlist"  required="true" repeatDirection="vertical" name="showType" value="URL" data="[{id:'URL',text:'URL访问'},{id:'POP_WIN',text:'弹窗'},{id:'NEW_WIN',text:'新窗口'},{id:'TAB_NAV',text:'Tab导航'},{id:'FUNS',text:'功能面板集（标签）'},{id:'FUNS_BLOCK',text:'功能面板集（单页）'},{id:'FUN',text:'功能面板'}]"></div>
		    				</td>
		    			</tr>
		    			<tr>
		    				<td>菜单类型</td>
		    				<td>
		    					<div class="mini-radiobuttonlist"  required="true" name="isBtnMenu" value="NO" data="[{id:'NO',text:'导航菜单'},{id:'YES',text:'按钮菜单'}]"></div>
		    				</td>
		    			</tr>
						<tr>
							<td>责任人</td>
							<td >
								<input id="principalId" name="principalId" textname="principal" class="mini-user rxc"
									   plugins="mini-user" style="width:80%" allowinput="false" label="责任人" length="50"
									   mainfield="no"  single="false"
								/>
							</td>
						</tr>
						<tr>
							<td>关键用户</td>
							<td >
								<input id="gjyhId" name="gjyhId" textname="gjyh" class="mini-user rxc"
									   plugins="mini-user" style="width:80%" allowinput="false" label="关键用户" length="50"
									   mainfield="no"  single="false"
								/>
							</td>
						</tr>
						<tr>
							<td>状态</td>
							<td>
								<input id="gnStatus" name="gnStatus" class="mini-combobox" style="width:80%"
									   textField="text" valueField="id" emptyText="请选择..." showNullItem="true" nullItemText="请选择..."
									   data="[{id:'方案梳理',text:'方案梳理'},{id:'待开发',text:'待开发'},
                                           {id:'新功能开发中',text:'新功能开发中'},{id:'优化开发中',text:'优化开发中'}
                                           ,{id:'测试中',text:'测试中'},{id:'已上线',text:'已上线'}]"							/>
							</td>
						</tr>
						<tr>
							<td>（计划）完成时间</td>
							<td>
								<input id="onlineTime" name="onlineTime" valueType="string" class="mini-datepicker" allowInput="false" style="width:80%;"/>
							</td>
						</tr>
						<tr>
							<td>（优化）功能描述</td>
							<td>
								<input class="mini-textarea" id="descp" name="descp" maxlength="1000" style="width:90%;height: 120px" />
							</td>
						</tr>
						<tr>
							<td>开发人员</td>
							<td>
								<input class="mini-textbox" id="developUser" name="developUser" maxlength="120" style="width:90%" />
							</td>
						</tr>
		    		</table>
		    	</div>
	    	</div>
	    </div>
	</div>


    <script type="text/javascript">
        mini.parse();

        var tree = mini.get("#leftTree");
        var grid=mini.get("#menuGrid");
		var menuForm=new mini.Form('menuForm');
		collapse();
        function onClick(e) {
            var node = tree.getSelectedNode();
            //var grid=mini.get('#menuGrid');
            grid.setUrl("${ctxPath}/sys/core/sysMenuMgr/treeBySysId.do?sysId="+node.sysId);
            collapse();
        }

        function onChangeInstType(e){
        	var s=e.sender;
        	var val=s.getValue();
        	tree.setUrl('${ctxPath}/sys/core/sysMenuMgr/listSys.do?instType='+val);
        	tree.load();
        	var subcombox=mini.get('sysId');
        	subcombox.load('${ctxPath}/sys/core/sysMenuMgr/listSys.do?instType='+val);
        	grid.setData();
        }

        function onRowClick(e){
        	var row=grid.getSelected();
        	var formRowUID=mini.get('_uid').getValue();
        	if(row!=null && row._uid!=formRowUID){
        		menuForm.setData(row);
        		mini.get('icnClsBtn').setIconCls(row.iconCls);
        		mini.get('iconCls').setText(row.iconCls);

        		var node = tree.getSelectedNode();

    			if(node){
    				var sysId=node.sysId;
    				mini.get('sysId').setValue(sysId);
    			}
        	}
        }
        //菜单行编辑，把数据写回form
        function onMenuRowEndEdit(e){
        	var data=menuForm.getData();
        	data.name=e.record.name;
        	data.key=e.record.key;
        	menuForm.setData(data);
        }

        function expand(){
        	grid.expandAll();
        }

        function collapse(){
        	grid.collapseAll();
        }
        function onCellValidation(e){
        	if(e.field=='key' && (!e.value||e.value=='')){
        		 e.isValid = false;
        		 e.errorText='菜单Key不能为空！';
        	}
        	if(e.field=='name' && (!e.value||e.value=='')){
        		e.isValid = false;
       		 	e.errorText='菜单名称不能为空！';
        	}
        }

        function changeSn(e){
        	var row=grid.getSelected();
        	row.sn=e.value;
        }

        function onActionRenderer(e) {
            var record = e.record;
            var uid = record._uid;
            var pkId=record.pkId;
            var s = '<span class="icon-button icon-shang-add" title="在前新增菜单" onclick="newBeforeRow(\''+uid+'\')"></span>';
            s+=' <span class="icon-button icon-xia-add" title="在后新增菜单" onclick="newAfterRow(\''+uid+'\')"></span>';
            s+= '<span class="icon-button icon-jia" title="新增子菜单" onclick="newSubRow()"></span>';
            s+=' <span class="icon-button icon-zhuanhuan" title="转移" onclick="transportTo(\'' + pkId + '\')"></span>';
            s+=' <span class="icon-button icon-trash" title="删除" onclick="delRow(\'' + uid + '\')"></span>';
            return s;
        }
		//在当前选择行的下新增子记录
        function newSubRow(){
        	var node = grid.getSelectedNode();
            var newNode = {isBtnMenu:'NO',showType:'URL'};
            grid.addNode(newNode, "add", node);
			/*grid.scrollIntoView (node);*/
        }
        function newRow() {
            var node = grid.getSelectedNode();
            var arrays = grid.getList ();
            grid.addNode({isBtnMenu:'NO',showType:'URL'}, "before", node);
            grid.cancelEdit();
            grid.beginEditRow(node);
            var row =grid.getRow(arrays.length - 1);
			grid.scrollIntoView (row);
			console.log(row);
			console.log(arrays);
        }

        function newAfterRow(row_uid){
        	var node = grid.getRowByUID(row_uid);
        	grid.addNode({isBtnMenu:'NO',showType:'URL'}, "after", node);
            grid.cancelEdit();
            grid.beginEditRow(node);
        }

        function newBeforeRow(row_uid){
        	var node = grid.getRowByUID(row_uid);
        	grid.addNode({isBtnMenu:'NO',showType:'URL'}, "before", node);
            grid.cancelEdit();
            grid.beginEditRow(node);
        }




/*         function saveRowForm(e){
        	var selected=grid.getSelected();
        	if(selected){
        		grid.updateRow(selected,menuForm.getData());
        	}
        } */
		//保存单个菜单
        function saveRowForm(e){
        	var button = e.sender;
        	handClick(button,function(){
	        	menuForm.validate();
	        	if(!menuForm.isValid()){
	        		return;
	        	}
	        	var row=grid.getSelected();
	        	_SubmitJson({
	        		url: "${ctxPath}/sys/core/sysMenuMgr/saveMenu.do",
	        		data: { data: mini.encode(menuForm.getData())},
	        		success:function(result){
	        			if(row && result.data && result.data.menuId){
	                		grid.updateRow(row,result.data);
	                		grid.accept();
	                		mini.get('menuId').setValue(result.data.menuId);
	                	}
	        		}
	        	});
        	})
        }

        function cancelRow(row_uid) {
            grid.reload();
        }

        function allotToSysInst(e){
        	var tree = mini.get("#leftTree");
            var node = tree.getSelectedNode();
            _OpenWindow({
                url: "${ctxPath}/sys/core/sysInstType/selectDialog.do?subSysId="+node.pkId,
                title: "选择机构类型", width: 300, height: 400,
                ondestroy: function(action) {
                	if(action=='ok'){
                		mini.showTips({
                            content: "<b>成功</b> <br/>授权成功",
                            state: 'success',
                            x: 'center',
                            y: 'center',
                            timeout: 3000
                        });
                	}
                }
            });
        }

        //批量保存
        function onSave(){
			var node = tree.getSelectedNode();
			var sysId=null;
			if(node){
				sysId=node.sysId;
			}else{
				mini.alert("请选择子系统");
				return;
			}

        	//表格检验
        	grid.validate();
        	if(!grid.isValid()){
            	return;
            }

        	//获得表格的每行值

        	var data = grid.getData();
            var json = mini.encode(data);

            var postData={sysId:sysId,gridData:json};
            $.ajax({
                url: "${ctxPath}/sys/core/sysMenuMgr/batchSaveMenu.do",
                type:"POST",
                data: postData,
                success: function (text) {
                	//进行提示
                    _ShowTips({
                    	msg:'成功批量保存菜单!'
                    });
                	grid.load();
                },
                error: function (jqXHR, textStatus, errorThrown) {
					mini.alert(jqXHR.responseText);
                }
            });
        }

        function delRow(row_uid) {
        	var row=null;
        	if(row_uid){
        		row = grid.getRowByUID(row_uid);
        	}else{
        		row = grid.getSelected();
        	}
        	menuForm.setData({});
        	if(!row){
				mini.alert("请选择删除的行！");
        		return;
        	}

        	if (!confirm("确定删除此记录？")) {return;}

            if(row && !row.menuId){
            	 grid.removeNode(row);
            	 return;
            }else if(row.menuId){
	            $.ajax({
	                url: "${ctxPath}/sys/core/sysMenuMgr/del.do?menuId="+row.menuId,
	                success: function (text) {
	                	 grid.removeNode(row);
	                },
	                error: function () {
	                }
	            });
            }
        }

        function addSystem(){
       	 	_OpenWindow({
                url: "${ctxPath}/sys/core/subsystem/edit.do",
                title: "新增子系统", width: 1050, height: 750,
                ondestroy: function(action) {
                    if (action == 'ok') {
                        tree.reload();
                        mini.get('sysId').load(tree.getUrl());
                    }
                }
            });
       }
       //选择图标
       function selectIcon(e){
    	   var btn=e.sender;
    	   _IconSelectDlg(function(icon){
			//grid.updateRow(row,{iconCls:icon});
			btn.setText(icon);
			btn.setValue(icon);
			mini.get('icnClsBtn').setIconCls(icon);
		});
       }

       //迁移子系统菜单
       function transferSysMenus(e){
    	   var tree = mini.get("#leftTree");
           var node = tree.getSelectedNode();
           var sysId=node.sysId;
           _OpenWindow({
               url: "${ctxPath}/sys/core/subsystem/allMenu.do?isTransfer=false",
               title: "转移",
               width: 550,
               height: 450,
               ondestroy: function(action) {
                    if (action != 'ok') { return;}
					var win=this.getIFrameEl().contentWindow;
					var selNode=win.getSelectMenu();
					var targetId = selNode.id;
					var menuType=selNode.menuType;
					_SubmitJson({
						url : '${ctxPath}/sys/core/sysMenu/toMoveMenu.do?',
						data : {
							sysId:sysId,
							targetId:targetId,
							menuType:menuType,
							ckSubMenus:true
						},
						success:function() {
							var node = tree.getSelectedNode();
					        grid.setUrl("${ctxPath}/sys/core/sysMenuMgr/treeBySysId.do?sysId="+node.sysId);
							mini.alert('成功转移菜单！');
						}
					});
                }
           });
       }

       function editSystem(e){
       	  var tree = mini.get("#leftTree");
          var node = tree.getSelectedNode();
          var sysId=node.sysId;

          _OpenWindow({
	               url: "${ctxPath}/sys/core/subsystem/edit.do?pkId="+sysId,
	               title: "编辑子系统", width: 1050, height: 750,
	               ondestroy: function(action) {
	                   if (action == 'ok') {
	                       tree.reload();
	                       mini.get('sysId').load(tree.getUrl());
	                   }
	               }
	       });
       }

       function refreshSystem(){
       		tree.reload();
       }

       function delSystem(e) {
       	var tree = mini.get("#leftTree");
           var node = tree.getSelectedNode();

           if (confirm("确定删除"+ node.name+"?")) {

               $.ajax({
                   url: "${ctxPath}/sys/core/subsystem/del.do",
                   data: {
                       ids: node.sysId
                   },
                   success: function(text) {
                       tree.reload();
                       mini.get('sysId').load(tree.getUrl());
                   },
                   error: function() {
                   }
               });
           }
       }

       function transportTo(menuId){

    	   _OpenWindow({
               url: "${ctxPath}/sys/core/subsystem/allMenu.do?isTransfer=true&menuId="+menuId,
               title: "转移", width: 450, height: 550,
               ondestroy: function(action) {
                   if (action != 'ok') { return;}
					var win=this.getIFrameEl().contentWindow;
					var isMoveChilds=win.getIsMoveChilds();
					var selNode=win.getSelectMenu();
					var targetId = selNode.id;
					var menuType=selNode.menuType;

					_SubmitJson({
						url : '${ctxPath}/sys/core/sysMenu/toMoveMenu.do?',
						data : {
							menuId:menuId,
							targetId:targetId,
							menuType:menuType,
							ckSubMenus:isMoveChilds
						},
						success : function() {
							grid.load();
						}
					});
                  }
           });
       }
    </script>
</body>
</html>
