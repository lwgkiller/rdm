
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
<title>指标分析报表</title>
	<%@include file="/commons/list.jsp"%>
</head>
<body>
	 <div class="mini-toolbar" >
	 	<div class="searchBox">
			<form id="searchForm" class="search-form" style="margin-bottom: 25px">
				<ul style="margin-left: 15px">
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">设计型号: </span>
                        <input class="mini-textbox" id="jixing" name="jixing" />
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">部件名称: </span>
                        <input id="structName" name="structName" class="mini-textbox"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">特性名称: </span>
                        <input id="quotaName" name="quotaName" class="mini-textbox"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">指标状态: </span>
                        <input id="validStatus" name="validStatus" class="mini-combobox" style="width:120px;"
                               textField="value" valueField="key" emptyText="请选择..."
                               required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                               data="[ {'key' : '有效','value' : '有效'},{'key' : '作废','value' : '作废'}]"
                        />
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">特性分类: </span>
                        <input id="maxlv" name="maxlv" class="mini-combobox" style="width:120px;"
                               textField="value" valueField="key" emptyText="请选择..."
                               required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                               data="[ {'key' : '1','value' : '专控特性'},{'key' : '2','value' : '一般特性'},{'key' : '3','value' : '重要特性'},{'key' : '4','value' : '关键特性'}]"
                        />
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">包含设计标准: </span>
                        <input id="sjStandardId" style="width:98%;" class="mini-buttonedit" showClose="true"
                               oncloseclick="onSelectStandardCloseClick('sj')"
                               name="sjStandardId" textname="sjStandardName"
                               allowInput="false" onbuttonclick="selectStandardClick('sj')"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">包含测试标准: </span>
                        <input id="testStandardId" style="width:98%;" class="mini-buttonedit" showClose="true"
                               oncloseclick="onSelectStandardCloseClick('test')"
                               name="testStandardId" textname="testStandardName"
                               allowInput="false" onbuttonclick="selectStandardClick('test')"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">包含评价标准: </span>
                        <input id="evaluateStandardId" style="width:98%;" class="mini-buttonedit" showClose="true"
                               oncloseclick="onSelectStandardCloseClick('evaluate')"
                               name="evaluateStandardId" textname="evaluateStandardName"
                               allowInput="false" onbuttonclick="selectStandardClick('evaluate')"/>
                    </li>
					<li style="margin-left: 10px">
						<a class="mini-button"  style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
						<a class="mini-button btn-red" style="margin-right: 5px"  plain="true" onclick="clearForm()">清空查询</a>
<%--                        <a class="mini-button" style="margin-right: 5px" plain="true" onclick="exportProjectProgress()">导出</a>--%>
					</li>
				</ul>
			</form>
			<span class="searchSelectionBtn" onclick="no_search(this ,'searchForm')">
				<i class="icon-sc-lower"></i>
			</span>
		</div>
     </div>
	<div class="mini-fit" style="height: 100%;">
		<div id="quotaListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" autoload="true"
			url="${ctxPath}/drbfm/report/quotaReportList.do" idField="id" allowCellWrap="true"
			multiSelect="true" showColumnsMenu="true" sizeList="[50,100,200]" pageSize="50" allowAlternating="true" pagerButtons="#pagerButtons">
			<div property="columns">
                <div type="indexcolumn" headerAlign="center" width="50px">序号</div>
				<div field="jixing" name="jixing" width="100" headerAlign="center" align="center" >机型</div>
				<div field="structNumber" name="structNumber" width="120" headerAlign="center" align="center" >部件代号</div>
				<div field="structName" name="structName" width="100" headerAlign="center" align="center" >部件名称</div>
				<div field="quotaName" width="220" headerAlign="center" align="center" >特性名称</div>
				<div field="sjStandardValue"  width="120" headerAlign="center" align="left" >特性值</div>
                <div field="maxlv" headerAlign="center" width="70" align="left" renderer="txRenderer">特性分类</div>
				<div field="validStatus" width="70" headerAlign="center" align="center" >指标状态</div>
				<div field="sjStandardNames" width="140" headerAlign="center" align="center" >设计标准名称</div>
                <div field="testStandardNames" width="140" headerAlign="center" align="center" >测试标准名称</div>
                <div field="evaluateStandardNames" width="140" headerAlign="center" align="center" >评价标准名称</div>
				<div field="creator" width="65" headerAlign="center" align="center">创建人</div>
				<div field="CREATE_TIME_"  align="center" width="85" headerAlign="center">创建时间</div>
			</div>
		</div>
	</div>

     <!--导出Excel相关HTML-->
     <form id="excelForm" action="${ctxPath}/xcmgProjectManager/report/xcmgProject/exportProjectProgressExcel.do" method="post" target="excelIFrame">
         <input type="hidden" name="filter" id="filter"/>
     </form>
     <iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>


     <div id="selectStandardWindow" title="选择标准" class="mini-window" style="width:950px;height:550px;"
          showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
         <input id="standardType" name="standardType" class="mini-hidden"/>
         <div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px"
              borderStyle="border-left:0;border-top:0;border-right:0;">
             <span style="font-size: 14px;color: #777">体系类别: </span>
             <input class="mini-combobox" width="130" id="filterSystemCategory" style="margin-right: 15px" textField="text"
                    valueField="id" emptyText="请选择..." value="JS"
                    data="[{id:'JS',text:'技术标准'},{id:'GL',text:'管理标准'}]"/>
             <span style="font-size: 14px;color: #777">编号: </span>
             <input class="mini-textbox" width="130" id="filterStandardNumberId" style="margin-right: 15px"/>
             <span style="font-size: 14px;color: #777">名称: </span>
             <input class="mini-textbox" width="130" id="filterStandardNameId" style="margin-right: 15px"/>
             <a class="mini-button" plain="true" onclick="searchStandardList()">查询</a>
         </div>
         <div class="mini-fit">
             <div id="standardListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
                  onrowdblclick="onRowDblClick"
                  idField="id" showColumnsMenu="false" sizeList="[20,50,100,200]" pageSize="20" multiSelect="false"
                  allowAlternating="true" pagerButtons="#pagerButtons"
                  url="${ctxPath}/standardManager/core/standard/queryList.do"
             >
                 <div property="columns">
                     <div type="checkcolumn" width="30"></div>
                     <div field="categoryName" sortField="categoryName" width="60" headerAlign="center" align="center"
                          allowSort="true">标准类别
                     </div>
                     <div field="standardNumber" sortField="standardNumber" width="140" headerAlign="center" align="center"
                          align="center" allowSort="true">编号
                     </div>
                     <div field="standardName" sortField="standardName" width="180" headerAlign="center" align="center"
                          allowSort="true">名称
                     </div>
                     <div field="standardStatus" sortField="standardStatus" width="60" headerAlign="center" align="center"
                          allowSort="true" renderer="standardStatusRenderer">状态
                     </div>
                     <div field="publisherName" sortField="publisherName" align="center"
                          width="60" headerAlign="center" allowSort="true">起草人
                     </div>
                 </div>
             </div>
         </div>
         <div property="footer" style="padding:5px;height: 35px">
             <table style="width:100%;height: 100%">
                 <tr>
                     <td style="width:120px;text-align:center;">
                         <input type="button" style="height: 25px;width: 70px" value="确定" onclick="selectOutOK()"/>
                         <input type="button" style="height: 25px;width: 70px" value="取消" onclick="selectOutHide()"/>
                     </td>
                 </tr>
             </table>
         </div>
     </div>

	<script type="text/javascript">
		mini.parse();
		var jsUseCtxPath="${ctxPath}";
        var quotaListGrid=mini.get("quotaListGrid");
        var selectStandardWindow = mini.get("selectStandardWindow");
        var standardListGrid = mini.get("standardListGrid");

        quotaListGrid.on("load", function () {
            quotaListGrid.mergeColumns(["jixing","structNumber","structName"]);
        });


        function exportProjectProgress(){
            var parent=$(".search-form");
            var inputAry=$("input",parent);
            var params=[];
            inputAry.each(function(i){
                var el=$(this);
                var obj={};
                obj.name=el.attr("name");
                if(!obj.name) return true;
                obj.value=el.val();
                params.push(obj);
            });

            $("#filter").val(mini.encode(params));
            var excelForm = $("#excelForm");
            excelForm.submit();
        }

        function onSelectStandardCloseClick(inputScene) {
            if(inputScene=='sj') {
                mini.get("sjStandardId").setValue('');
                mini.get("sjStandardId").setText('');
            } else if(inputScene=='test') {
                mini.get("testStandardId").setValue('');
                mini.get("testStandardId").setText('');
            } else if(inputScene=='evaluate') {
                mini.get("evaluateStandardId").setValue('');
                mini.get("evaluateStandardId").setText('');
            }
        }
        function standardStatusRenderer(e) {
            var record = e.record;
            var status = record.standardStatus;

            var arr = [{'key': 'draft', 'value': '草稿', 'css': 'orange'},
                {'key': 'enable', 'value': '有效', 'css': 'green'},
                {'key': 'disable', 'value': '已废止', 'css': 'red'}
            ];

            return $.formatItemValue(arr, status);
        }

        //标准引用
        function selectStandardClick(standardType) {
            selectStandardWindow.show();
            mini.get("standardType").setValue(standardType);
            searchStandardList();
        }

        //查询标准
        function searchStandardList() {
            var queryParam = [];
            var systemCategoryId = $.trim(mini.get("filterSystemCategory").getValue());
            if (systemCategoryId) {
                queryParam.push({name: "systemCategoryId", value: systemCategoryId});
            }
            var standardNumber = $.trim(mini.get("filterStandardNumberId").getValue());
            if (standardNumber) {
                queryParam.push({name: "standardNumber", value: standardNumber});
            }
            var standardName = $.trim(mini.get("filterStandardNameId").getValue());
            if (standardName) {
                queryParam.push({name: "standardName", value: standardName});
            }
            //默认搜索有效的
            queryParam.push({name: "standardStatus", value: "enable"});
            var inputList = '';
            inputList = standardListGrid;
            var data = {};
            data.filter = mini.encode(queryParam);
            data.pageIndex = inputList.getPageIndex();
            data.pageSize = inputList.getPageSize();
            data.sortField = inputList.getSortField();
            data.sortOrder = inputList.getSortOrder();
            //查询
            inputList.load(data);
        }

        function onRowDblClick() {
            selectOutOK();
        }

        function selectOutOK() {
            var standardType = mini.get("standardType").getValue();
            var inputList = '';
            inputList = standardListGrid;
            // 这里要改成多选，只返回id，用逗号拼接
            var selectRows = inputList.getSelecteds();
            if (selectRows) {
                var ids = [];
                var names = [];
                for (var i = 0; i < selectRows.length; i++) {
                    var row = selectRows[i];
                    ids.push(row.id);
                    names.push(row.standardName);
                }
                var idsStr = ids.join(',');
                var namesStr = names.join(',');
                if (standardType == "sj") {
                    mini.get("sjStandardId").setValue(idsStr);
                    mini.get("sjStandardId").setText(namesStr);
                } else if (standardType == "test") {
                    mini.get("testStandardId").setValue(idsStr);
                    mini.get("testStandardId").setText(namesStr);
                } else if (standardType == "evaluate") {
                    mini.get("evaluateStandardId").setValue(idsStr);
                    mini.get("evaluateStandardId").setText(namesStr);
                } else {
                    mini.alert("标准选择错误！");
                    return;
                }
            } else {
                mini.alert("请选择一条数据！");
                return;
            }
            selectOutHide();
        }

        function selectOutHide() {
            selectStandardWindow.hide();
            mini.get("standardType").setValue('');
            mini.get("filterSystemCategory").setValue('JS');
            mini.get("filterStandardNumberId").setValue('');
            mini.get("filterStandardNameId").setValue('');
            standardListGrid.deselectAll(true);
        }

        function txRenderer(e) {
            var record = e.record;
            var txLevel = record.maxlv;
            var res = "";
            if (txLevel == "1") {
                res = "专控特性";
            } else if (txLevel == "2") {
                res = "一般特性";
            } else if (txLevel == "3") {
                res = "重要特性";
            }else if (txLevel == "4") {
                res = "关键特性";
            }
            return res;
        }
	</script>
	<redxun:gridScript gridId="quotaListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl="" />
</body>
</html>