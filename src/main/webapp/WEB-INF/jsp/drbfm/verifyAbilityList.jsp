
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
	<title>验证能力清单</title>
	<%@include file="/commons/list.jsp"%>
</head>
<body>
<div class="mini-toolbar" >
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul >
                <li style="float: left">
                    <span class="text" style="width:auto">验证能力: </span>
                    <input id="abilityNameFilter" name="abilityName" class="mini-textbox" style="width:200px;"/>
                </li>
                <li style="float: left">
                    <span class="text" style="width:auto">归口部门: </span>
                    <input id="respDeptIdFilter" name="respDeptId" textname="respDeptName" class="mini-dep rxc" plugins="mini-dep"
                           style="width:98%;height:34px" allowinput="false" length="500" maxlength="500" minlen="0"
                           single="true" initlogindep="false"/>
                </li>
                <li style="float: left">
                    <span class="text" style="width:auto">当前是否具备: </span>
                    <input id="currentOkFilter" name="currentOk" class="mini-combobox" style="width:150px;"
                           textField="value" valueField="key" emptyText="请选择..."
                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                           data="[ {'key' : '是','value' : '是'},{'key' : '否','value' : '否'}]"
                    />
                </li>
                <li>
                    <a class="mini-button" iconCls="icon-reload" onclick="searchFrm()" plain="true">查询</a>
                    <a class="mini-button btn-red" style="margin-left:5px; margin-right: 5px"  plain="true" onclick="clearForm()">清空查询</a>
                    <a class="mini-button" iconCls="icon-add" style="margin-right: 5px" onclick="openAbilityEditWindow('add')">新增</a>
                    <a class="mini-button btn-red" plain="true" onclick="removeAbility()">删除</a>
                </li>

            </ul>
        </form>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
	<div id="abilityListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" allowCellWrap="false"
         url="${ctxPath}/drbfm/ability/getAbilityList.do"  sizeList="[20,50,100,200]" pageSize="20" pagerButtons="#pagerButtons"
         idField="id" allowAlternating="true" showPager="true" multiSelect="true">
		<div property="columns">
			<div type="checkcolumn" width="30px"></div>
			<div name="action" cellCls="actionIcons" width="70px" headerAlign="center" align="center" renderer="actionRender" cellStyle="padding:0;">操作</div>
            <div field="abilityName" align="center" width="150px" headerAlign="center" >验证能力名称</div>
			<div field="respDeptName" width="100px" headerAlign="center" align="center" >归口部门</div>
            <div field="currentOk" width="80px" headerAlign="center" align="center" >当前能力是否具备</div>
            <div field="remark" width="100px" headerAlign="center" align="center" >备注说明</div>
            <div field="creator" width="50px" headerAlign="center" align="center" >创建人</div>
			<div field="CREATE_TIME_" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" width="70px" headerAlign="center" >创建时间</div>
			<div field="updator" width="50px" headerAlign="center" align="center" >更新人</div>
			<div field="UPDATE_TIME_" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" width="70px" headerAlign="center" >更新时间</div>
		</div>
	</div>
</div>

<div id="abilityEditWindow" title="验证能力编辑" class="mini-window" style="width:750px;height:250px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
    <div class="mini-fit">
        <div class="topToolBar" style="float: right;">
            <div style="position: relative!important;">
                <a id="saveAbilityBtn"class="mini-button" onclick="saveAbility()">保存</a>
                <a id="closeAbilityBtn" class="mini-button btn-red" onclick="closeAbility()">关闭</a>
            </div>
        </div>
        <input id="abilityId" name="id" class="mini-hidden"/>
        <table class="table-detail"  cellspacing="1" cellpadding="0" style="width: 99%;height: 70%">
            <tr>
                <td style="width: 12%">验证能力名称：</td>
                <td style="width: 38%;">
                    <input class="mini-textbox" id="abilityName" name="abilityName" style="width: 98%"/>
                </td>
                <td style="width: 12%">归口部门：</td>
                <td style="width: 38%;">
                    <input id="respDeptId" name="respDeptId" textname="respDeptName" class="mini-dep rxc" plugins="mini-dep"
                           style="width:98%;height:34px" allowinput="false" length="500" maxlength="500" minlen="0"
                           single="true" initlogindep="false"/>
                </td>
            </tr>
            <tr>
                <td style="width: 12%">当前能力是否具备：</td>
                <td style="width: 38%;">
                    <input id="currentOk" name="currentOk" class="mini-combobox" style="width: 98%"
                           textField="value" valueField="key" emptyText="请选择..."
                           required="false" allowInput="false" showNullItem="false" nullItemText="请选择..."
                           data="[ {'key' : '是','value' : '是'},{'key' : '否','value' : '否'}]"
                    />
                </td>
                <td style="width: 12%">备注说明：</td>
                <td style="width: 38%;">
                    <input class="mini-textbox" id="remark" name="remark" style="width: 98%"/>
                </td>
            </tr>
            <tr>
                <td style="text-align: center;">关联标准：</td>
                <td colspan="3" style="width: 80%;">
                    <input id="standardIds" name="standardIds" textname="standardNames"
                           property="editor"
                           class="mini-buttonedit" showClose="true" allowInput="false"
                           oncloseclick="standardCloseClick()" onbuttonclick="selectStandardClick()"
                           style="width:98%;"/>
                </td>
            </tr>

        </table>
    </div>
</div>

<div id="selectStandardWindow" title="选择标准" class="mini-window" style="width:950px;height:550px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
    <input id="standardType" name="standardType" class="mini-hidden" />
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
             idField="id" showColumnsMenu="false" sizeList="[20,50,100,200]" pageSize="20" multiSelect="true"
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
                    <input type="button" style="height: 25px;width: 70px" value="确定" onclick="selectStandardOK()"/>
                    <input type="button" style="height: 25px;width: 70px" value="取消" onclick="selectStandardHide()"/>
                </td>
            </tr>
        </table>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
	var currentUserNo="${currentUserNo}";
	var currentUserId = "${currentUserId}";
	var currentUserDeptId = "${currentUserDeptId}";
    var currentUserDeptName = "${currentUserDeptName}";
    var abilityEditWindow = mini.get("abilityEditWindow");
    var abilityListGrid = mini.get("abilityListGrid");
    var selectStandardWindow = mini.get("selectStandardWindow");
    var standardListGrid = mini.get("standardListGrid");


    mini.get("respDeptId").setEnabled(false);

    function actionRender(e) {
        var record = e.record;
        var creatorId = record.CREATE_BY_;
        var s = '';
        if (creatorId !=currentUserId) {
            s = '<span  title="编辑" style="color: silver" >编辑</span>';
        } else {
            s = '<span  title="编辑" onclick="openAbilityEditWindow(\'edit\',' +JSON.stringify(record).replace(/"/g, '&quot;')+ ')">编辑</span>';
        }
        return s;
    }

    function openAbilityEditWindow(action,record) {
        abilityEditWindow.show();
        if(action=='edit' && record) {
            mini.get("abilityId").setValue(record.id);
            mini.get("abilityName").setValue(record.abilityName);
            mini.get("respDeptId").setValue(record.respDeptId);
            mini.get("respDeptId").setText(record.respDeptName);
            mini.get("currentOk").setValue(record.currentOk);
            mini.get("remark").setValue(record.remark);
            mini.get("standardIds").setValue(record.standardIds);
            mini.get("standardIds").setText(record.standardNames);
        }
        if(action=='add') {
            mini.get("respDeptId").setValue(currentUserDeptId);
            mini.get("respDeptId").setText(currentUserDeptName);
            mini.get("currentOk").setValue("是");
        }
    }

    function saveAbility() {
        var formData = {};
        formData.id= mini.get("abilityId").getValue();
        formData.abilityName=mini.get("abilityName").getValue();
        formData.respDeptId=mini.get("respDeptId").getValue();
        formData.respDeptName=mini.get("respDeptId").getText();
        formData.currentOk=mini.get("currentOk").getValue();
        formData.remark=mini.get("remark").getText();
        formData.standardIds=mini.get("standardIds").getValue();
        formData.standardNames=mini.get("standardIds").getText();
        var json = mini.encode(formData);
        $.ajax({
            url: jsUseCtxPath + "/drbfm/ability/saveAbility.do",
            type: 'post',
            async: false,
            data: json,
            contentType: 'application/json',
            success: function (data) {
                if (data) {
                    if (data.success) {
                        closeAbility();
                    } else {
                        mini.alert("数据保存失败，" + data.message);
                    }
                }
            }
        });
    }

    abilityListGrid.on("drawcell",function(e){
        var record=e.record;

        if (record.currentOk && record.currentOk == '否'){
            e.cellStyle+="background-color: #a59d9da6;";
        }
    });

    function closeAbility() {
        abilityEditWindow.hide();
        mini.get("abilityId").setValue("");
        mini.get("abilityName").setValue("");
        mini.get("respDeptId").setValue("");
        mini.get("respDeptId").setText("");
        mini.get("currentOk").setValue("");
        mini.get("remark").setValue("");
        mini.get("standardIds").setValue("");
        mini.get("standardIds").setText("");
        abilityListGrid.reload();
    }

    function removeAbility() {
        var rows = abilityListGrid.getSelecteds();
        if (rows.length <= 0) {
            mini.alert("请至少选中一条记录");
            return;
        }

        mini.confirm("确定删除选中记录【仅能删除本人创建的数据，已被验证任务关联的不能删除】？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                var rowIds = [];
                for (var i = 0, l = rows.length; i < l; i++) {
                    var r = rows[i];
                    if(r.CREATE_BY_ == currentUserId) {
                        rowIds.push(r.id);
                    }
                }
                if(rowIds.length==0) {
                    mini.alert("仅能删除本人创建的数据！");
                    return;
                }

                _SubmitJson({
                    url: jsUseCtxPath + "/drbfm/ability/deleteAbility.do",
                    method: 'POST',
                    showMsg:false,
                    data: {ids: rowIds.join(',')},
                    success: function (data) {
                        if (data) {
                            mini.alert(data.message);
                            abilityListGrid.reload();
                        }
                    }
                });
            }
        });
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
        data.pageIndex = 0;
        data.pageSize = inputList.getPageSize();
        data.sortField = inputList.getSortField();
        data.sortOrder = inputList.getSortOrder();
        //查询
        inputList.load(data);
    }

    function onRowDblClick() {
        selectStandardOK();
    }

    function selectStandardOK() {
        var standardType=mini.get("standardType").getValue();
        var inputList = standardListGrid;
        // 这里要改成多选，只返回id，用逗号拼接
        var selectRows = inputList.getSelecteds();
        if (selectRows && selectRows.length>0) {
            var ids = [];
            var names = [];
            for (var i = 0; i < selectRows.length; i++) {
                var row = selectRows[i];
                ids.push(row.id);
                names.push('【'+row.standardNumber+'】'+row.standardName);
            }
            var idsStr = ids.join(',');
            var namesStr = names.join('，');


            var existValue=mini.get("standardIds").getValue();
            if(existValue) {
                idsStr=existValue+","+idsStr;
                namesStr=mini.get("standardIds").getText()+"，"+namesStr;
            }
            mini.get("standardIds").setValue(idsStr);
            mini.get("standardIds").setText(namesStr);

        } else {
            mini.alert("请选择一条数据！");
            return;
        }
        selectStandardHide();
    }

    function selectStandardHide() {
        selectStandardWindow.hide();
        mini.get("standardType").setValue('');
        mini.get("filterSystemCategory").setValue('JS');
        mini.get("filterStandardNumberId").setValue('');
        mini.get("filterStandardNameId").setValue('');
        standardListGrid.deselectAll(true);
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

    function standardCloseClick() {
        mini.get("standardIds").setValue("");
        mini.get("standardIds").setText("");
    }



</script>
<redxun:gridScript gridId="abilityListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl="" />
</body>
</html>