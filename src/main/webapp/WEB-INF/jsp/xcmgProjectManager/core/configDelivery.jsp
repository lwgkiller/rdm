
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
	<title>项目交付物明细配置</title>
	<%@include file="/commons/list.jsp"%>
	<script src="${ctxPath}/scripts/xcmgProjectManager/projectConfig.js?version=${static_res_version}" type="text/javascript"></script>
	<script src="${ctxPath}/scripts/xcmgProjectManager/projectUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar" >
	<ul class="toolBtnBox">
		<li style="float: left">
			<span class="text" style="width:auto"><spring:message code="page.configDelivery.name" />: </span>
			<input id="projectCategory" name="categoryId" class="mini-combobox" style="width:200px;"
				   textField="categoryName" valueField="categoryId"
				   required="false" allowInput="false" showNullItem="false" onvaluechanged="queryDeliveryByCategory()"/>
		</li>
		<span class="separator"></span>
		<li>
			<a class="mini-button" iconCls="icon-reload" onclick="refreshDelivery()" plain="true"><spring:message code="page.configDelivery.name1" /></a>
			<a class="mini-button" iconCls="icon-add" onclick="openDeliveryWindow(mini.get('projectCategory').getValue(),mini.get('projectCategory').getText(),'')"><spring:message code="page.configDelivery.name2" /></a>
			<a class="mini-button btn-red" plain="true" onclick="removeDelivery()"><spring:message code="page.configDelivery.name3" /></a>
		</li>

	</ul>
</div>
<div class="mini-fit" style="height: 100%;">
	<div id="deliveryListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" allowCellWrap="true"
		  idField="deliveryId" allowAlternating="true" showPager="false" multiSelect="true">
		<div property="columns">
			<div type="checkcolumn" width="30px"></div>
			<div name="action" cellCls="actionIcons" width="70px" headerAlign="center" align="center" renderer="deliveryAction" cellStyle="padding:0;"><spring:message code="page.configDelivery.name4" /></div>
			<div field="stageNo" name="stageNo" width="50px" headerAlign="center" align="center" ><spring:message code="page.configDelivery.name5" /></div>
			<div field="stageId" visible="false"></div>
			<div field="stageNameAndPercent" name="stageNameAndPercent" width="120px" headerAlign="center" align="center" ><spring:message code="page.configDelivery.name6" /></div>
			<div field="deliveryName" headerAlign="center" align="center" width="180px"><spring:message code="page.configDelivery.name7" /></div>
			<div field="levelIds" width="180px" headerAlign="center" align="center" renderer="deliveryLevel"><spring:message code="page.configDelivery.name8" /></div>
			<div field="excludeSourceIds" align="center" width="120px" headerAlign="center" renderer="deliverySource"><spring:message code="page.configDelivery.name9" /></div>
			<div field="solutionName" align="center" width="190px" headerAlign="center" renderer="filterFileType"><spring:message code="page.configDelivery.name10" /></div>
            <div field="fromPdm" align="center" width="150px" headerAlign="center" renderer="fromPdmRender"><spring:message code="page.configDelivery.name11" /></div>
			<div field="creator" width="60px" headerAlign="center" align="center" ><spring:message code="page.configDelivery.name12" /></div>
			<div field="CREATE_TIME_" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" width="80px" headerAlign="center" ><spring:message code="page.configDelivery.name13" /></div>
			<div field="updator" width="60px" headerAlign="center" align="center" ><spring:message code="page.configDelivery.name14" /></div>
			<div field="UPDATE_TIME_" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" width="80px" headerAlign="center" ><spring:message code="page.configDelivery.name15" /></div>
		</div>
	</div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var url="${ctxPath}/xcmgProjectManager/core/config/deliveryList.do?categoryId=";
    var isManager=whetherIsProjectManager(${currentUserRoles});
	var currentUserNo="${currentUser.userNo}";

    //设置项目类别下拉及初始值
    var smallCategory=${smallCategoryInfos};
    mini.get("projectCategory").load(smallCategory);
    var categoryValue="";
    for(var i=0;i<smallCategory.length;i++) {
        if(smallCategory[i].categoryName=='产品研发（整机）类') {
            categoryValue=smallCategory[i].categoryId;
		}
	}
    mini.get("projectCategory").setValue(categoryValue);

    var deliveryListGrid = mini.get("deliveryListGrid");
    deliveryListGrid.setUrl(url+mini.get("projectCategory").getValue());
    deliveryListGrid.on("load", function () {
        deliveryListGrid.mergeColumns(["stageNo", "stageNameAndPercent"]);
    });
    deliveryListGrid.on("update",function(e){
        handGridButtons(e.sender.el);
    });
    deliveryListGrid.load();

    function deliveryAction(e) {
        var record = e.record;
        var deliveryId = record.deliveryId;
        var s = '<span  title=' + configDelivery_name + ' onclick="openDeliveryWindow(\'' +mini.get("projectCategory").getValue()+'\',\''+mini.get("projectCategory").getText()+'\',\''+deliveryId+ '\')">' + configDelivery_name + '</span>';
        s+='<span title=' + configDelivery_name1 + ' onclick="removeDelivery()">' + configDelivery_name1 + '</span>';
        return s;
    }

    var levelInfos=${levelInfos};
    function deliveryLevel(e) {
        var record = e.record;
        var levelIds = record.levelIds;
        var result='';
        if(levelIds) {
			var levelIdArr=levelIds.split(",");
			for(var i=0;i<levelIdArr.length;i++) {
			    for(var j=0;j<levelInfos.length;j++) {
			        if(levelInfos[j].levelId==levelIdArr[i]) {
			            result+=levelInfos[j].levelName+'，';
			            break;
					}
				}
			}
			result=result.substr(0,result.length-1);
        }
        return result;
    }

    var sourceInfos=${sourceInfos};
    function deliverySource(e) {
        var record = e.record;
        var excludeSourceIds = record.excludeSourceIds;
        var result='';
        if(excludeSourceIds) {
            var excludeSourceIdArr=excludeSourceIds.split(",");
            for(var i=0;i<excludeSourceIdArr.length;i++) {
                for(var j=0;j<sourceInfos.length;j++) {
                    if(sourceInfos[j].sourceId==excludeSourceIdArr[i]) {
                        result+=sourceInfos[j].sourceName+'，';
                        break;
                    }
                }
            }
            result=result.substr(0,result.length-1);
        }
        return result;
    }

	function filterFileType(e) {
		var record = e.record;
		var solutionName = record.solutionName;
		if(!solutionName) {
			var key = record.fileType;
			if(key=='pdmApprove'){
				return 'PDM文件目录说明';
			}else{
				return ''
			}
		}
		return solutionName;
	}

	function fromPdmRender(e) {
        var record = e.record;
        var fromPdm = record.fromPdm;
        if(fromPdm && fromPdm=='yes') {
            return "<span style='color:red'>是</span>";
        } else {
            return "否";
        }
    }
</script>
<%--便于修改操作这一列的样式--%>
<%--<redxun:gridScript gridId="deliveryListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl="" />--%>
</body>
</html>