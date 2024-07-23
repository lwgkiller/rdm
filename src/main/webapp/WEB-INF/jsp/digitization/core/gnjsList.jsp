
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>功能建设</title>
<%-- 	<link href="${ctxPath}/styles/form.css" rel="stylesheet" type="text/css" /> --%>
     <%@include file="/commons/get.jsp"%>
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
	<div id="layout1" class="mini-layout" style="width:100%;height:100%;">
	    <div
	    	region="west" 
	    	width="210"
			title="功能模块"
	    	showSplitIcon="true"
	    	showCollapseButton="false"
	    	showProxy="false"
    	>
<%--			<div class="treeToolBar">
				<a class="mini-button"   onclick="refreshSystem()">刷新</a>
			</div>--%>
			<div class="mini-fit">
	        	<ul id="leftTree" class="mini-tree" url="${ctxPath}/sys/core/sysMenuMgr/listAllSubSys.do?type=gnjs" style="width:100%;height:100%;"
					showTreeIcon="true" textField="name" idField="sysId" resultAsTree="false"  parentField="parentId"
	                onnodeclick="searchRdmMenu" expandOnLoad="0"
				></ul>
			</div>
	    </div>
	   
	    <div region="center" showHeader="false" >
			<div class="mini-toolbar" >
				<div class="searchBox">
					<form id="searchForm" class="search-form" style="margin-bottom: 25px">
						<ul>
							<li><a class="mini-button" onclick="expand()">全部展开</a></li>
							<li><a class="mini-button" onclick="collapse()">全部收起</a></li>
							<div style="display: inline-block" class="separator"></div>
							<li style="margin-right: 15px">
								<span class="text" style="width:auto">完成时间 从：</span>
								<input id="startTime"  name="startTime"  allowInput="false" valueType="string" class="mini-datepicker" format="yyyy-MM-dd" style="width:120px"/>
							</li>
							<li style="margin-right: 15px">
								<span class="text-to" style="width:auto">至: </span>
								<input id="endTime" name="endTime" allowInput="false" valueType="string" class="mini-datepicker" format="yyyy-MM-dd" style="width:120px" />
							</li>
							<li style="margin-right: 15px">
								<span class="text" style="width:auto">开发人员：</span>
								<input class="mini-textbox" id="developUser" name="developUser" />
							</li>
							<li style="margin-right: 15px">
								<span class="text" style="width:auto">当前状态：</span>
								<input id="gnStatus" name="gnStatus" class="mini-combobox" style="width:80%"
									   textField="text" valueField="id"  showNullItem="false" oncloseclick="onCloseClick" multiSelect="true"  showClose="true"
									   data="[{id:'方案梳理',text:'方案梳理'},{id:'待开发',text:'待开发'},
                                           {id:'新功能开发中',text:'新功能开发中'},{id:'优化开发中',text:'优化开发中'}
                                           ,{id:'测试中',text:'测试中'},{id:'已上线',text:'已上线'}]"/>
							</li>
							<a class="mini-button" style="margin-right: 5px" plain="true"
							   onclick="searchRdmMenu()">查询</a>
							<a class="mini-button btn-red" style="margin-right: 5px"  plain="true" onclick="clearSearchRdmMenu()">清空查询</a>
						</ul>
					</form>
				</div>
			</div>

			<div class="mini-fit">
				<div id="menuGrid" class="mini-treegrid" style="width:100%;height:100%;"
					resultAsTree="false" autoload="false" allowCellWrap="true"
					treeColumn="NAME_" idField="id" parentField="PARENT_ID_"
					expandOnLoad="true" allowAlternating="true"
					allowResize="true" ondrawcell="onDrawCell"
				 	allowCellSelect="true" url="${ctxPath}/digitization/core/Szh/getTreeBySysId.do">
					<div property="columns">
						<div field="NAME_" name="NAME_" align="center" headerAlign="center" width="160">名称</div>
						<div field="principal" name="principal" align="center" headerAlign="center" width="120">负责人</div>
						<div field="gnStatus" name="gnStatus" align="center" headerAlign="center" width="70">当前状态</div>
						<div field="onlineTime" name="onlineTime" align="center" headerAlign="center" width="70">（计划）完成时间</div>
						<div field="DESCP_" name="DESCP_" align="center" headerAlign="center" width="250">（优化）功能描述</div>
						<div field="clickCount" name="clickCount" align="center" headerAlign="center" width="40" renderer="clickCountRender">累计点击量</div>
						<div field="developUser" name="developUser" align="center" headerAlign="center" width="50">开发人员</div>
					</div>
				</div>
			</div>
	    </div>
	</div>


    <script type="text/javascript">
        mini.parse();
        var tree = mini.get("#leftTree");
        var grid=mini.get("#menuGrid");
        var showDevelopUser = "${showDevelopUser}";
        var jsUseCtxPath="${ctxPath}";

        $(function(){
            if (showDevelopUser == 'true') {
                grid.showColumn("developUser");
                mini.get("developUser").show();
            } else {
                grid.hideColumn("developUser");
                mini.get("developUser").hide();
            }
			//选中第一个节点
			var data=tree.getData();
            if(data&&data[0]) {
				tree.selectNode(data[0]);
			}
			//起止日期默认本月
			var nowDate=new Date();
			mini.get("startTime").setValue(new Date(nowDate.getFullYear(),nowDate.getMonth()));
            mini.get("endTime").setValue(new Date(nowDate.getFullYear(),nowDate.getMonth()+1,0));
            searchRdmMenu();
        });

        function expand(){
        	grid.expandAll();
        }
        
        function collapse(){
        	grid.collapseAll();
        }

       function refreshSystem(){
       		tree.reload();
       }
        function onDrawCell(e) {
            var record = e.record;
            var field = e.field;
            if (field=='gnStatus') {
                if(record.gnStatus=='待开发'){
                    e.cellStyle = "background-color:orange";
                }else if(record.gnStatus=='方案梳理'){
                    e.cellStyle = "background-color:red";
                }else if(record.gnStatus=='新功能开发中'){
                    e.cellStyle = "background-color:yellow";
                }else if(record.gnStatus=='已上线'){
                    e.cellStyle = "background-color:green";
                }else if(record.gnStatus=='优化开发中'){
                    e.cellStyle = "background-color:yellow";
                }else if(record.gnStatus=='测试中'){
                    e.cellStyle = "background-color:#409eff";
                }
            }
        }

        function searchRdmMenu() {
            var queryParam = [];
            var node = tree.getSelectedNode();
            if(!node) {
                return;
			}
            var sysId='';
            if(node.parentId) {
                sysId=node.sysId;
			}
            queryParam.push({name: "sysId", value: sysId});
            var startTime = $.trim(mini.get("startTime").getText());
            if (startTime) {
                queryParam.push({name: "startTime", value: startTime});
            }
            var endTime = $.trim(mini.get("endTime").getValue());
            if (endTime) {
                queryParam.push({name: "endTime", value: endTime});
            }
            var developUser = $.trim(mini.get("developUser").getValue());
            if (developUser) {
                queryParam.push({name: "developUser", value: developUser});
            }
            var gnStatus = $.trim(mini.get("gnStatus").getValue());
            if (gnStatus) {
                queryParam.push({name: "gnStatus", value: gnStatus});
            }
            var data = {};
            data.filter = mini.encode(queryParam);
            grid.load(data);
        }

        function clearSearchRdmMenu() {
			mini.get("startTime").setValue('');
            mini.get("endTime").setValue('');
            mini.get("developUser").setValue('');
            mini.get("gnStatus").setValue('');
            searchRdmMenu();
        }

        function onCloseClick(e) {
            var obj = e.sender;
            obj.setText("");
            obj.setValue("");
        }

        function clickCountRender(e) {
            var record = e.record;
            var clickCount = record.clickCount;
            var s='';
			if(record.URL_) {
			    if(record.showClickCount=='true') {
                    s = '<a href="#" style="color:#0f89d0;text-decoration:underline;" onclick="clickChart(\'' + record.id + '\')">'+clickCount+'</a>';
                } else {
			        s='***';
				}
            }
            return s;
        }

        function clickChart(menuId) {
            mini.open({
                title: "近1年点击量统计",
                url: jsUseCtxPath + '/digitization/core/Szh/clickChartPage.do?menuId='+menuId,
                width: 1200,
                height: 600,
                showModal: true,
                allowResize: true,
                onload: function () {

                }
            });
        }
    </script>
</body>
</html>