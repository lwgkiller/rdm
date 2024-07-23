<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
	<title>标准体系管理</title>
	<%@include file="/commons/list.jsp"%>
    <style>
        .mini-tab-text{
            font-size:18px
        }
        .mini-tab{
            min-width:120px
        }
    </style>
</head>
<body>
<div class="mini-fit" style="height: 100%;">
	<div id="systemTab" class="mini-tabs" activeIndex="${activeTabIndex}" style="width:100%;height:100%;">
	</div>
</div>
<script>
	var ctxPath="${ctxPath}";
	var standardNumber="${standardNumber}";
	var standardName="${standardName}";
	var standardCategory="${standardCategory}";
	var systemCategorysJsonArray=${systemCategorysJsonArray};
	var appendHtml="";
	for(var i=0;i<systemCategorysJsonArray.length;i++) {
		var systemCategory=systemCategorysJsonArray[i];
		appendHtml+='<div title="'+systemCategory.systemCategoryName+'" name="'+systemCategory.systemCategoryId+'" url="'+ctxPath+'/standardManager/core/standardSystem/tabPage.do?tabName=' +systemCategory.systemCategoryId+
            '&standardNumber='+standardNumber+'&standardName='+standardName+'&standardCategory='+standardCategory+'"></div>';
	}
	$("#systemTab").append(appendHtml);
</script>
</body>
</html>