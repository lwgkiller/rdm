
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<title>项目信息</title>
	<%@include file="/commons/edit.jsp"%>
	<link href="${ctxPath}/scripts/mini/miniui/themes/icons.css" rel="stylesheet" type="text/css" />
    <script src="${ctxPath}/scripts/xcmgProjectManager/projectInputEdit.js?version=${static_res_version}" type="text/javascript"></script>
</head>


<body>
<div class="topToolBar" >
    <div>
        <span style="color: red;float: left">(提示：所有字段均为必填项)</span>
        <a class="mini-button" onclick="saveInput()">保存</a>
        <a class="mini-button btn-red" onclick="closeInput()">关闭</a>
    </div>
</div>
<div class="mini-fit" >
    <div class="form-container" style="margin: 0 auto">
    <form id="inputForm" method="post">
        <input id="id_input" name="id" class="mini-hidden"/>
        <input id="projectId_input" name="projectId" class="mini-hidden"/>
        <table cellspacing="1" cellpadding="0" class="table-detail column-six grey">
            <tr>
                <td align="center" style="width: 10%">类型：</td>
                <td align="center" colspan="3">
                    <input id="inputType" name="inputType" class="mini-combobox" style="width:99%;"
                           textField="value" valueField="key" emptyText="请选择..."
                           required="false" allowInput="false" showNullItem="false" onvaluechanged="inputTypeChange"
                           data="[ {'key' : '标准','value' : '标准'},{'key' : '专利','value' : '专利'},{'key' : '情报报告','value' : '情报报告'}]"
                    />
                </td>
            </tr>
            <tr>
                <td align="center" style="width: 10%">输入名称：</td>
                <td align="center" style="width: 40%">
                    <input id="referId" name="referId" textname="inputName" style="width:98%;height:34px;" class="mini-buttonedit" showClose="true" allowInput="false"
                           oncloseclick="inputNameCloseClick()"  onbuttonclick="inputNameClick()"/>
                </td>
                <td align="center" style="width: 10%">编号：</td>
                <td align="center" style="width: 40%">
                    <input id="inputNumber" name="inputNumber" class="mini-textbox" style="width:98%;height:34px;" readonly/>
                </td>
            </tr>
            <tr>
                <td align="center" style="width: 10%">引用条款（内容）：</td>
                <td align="center" style="width: 40%">
                    <input id="referContent" name="referContent" class="mini-textarea" style="width:98%;height:95px;overflow: auto;"/>
                </td>
                <td align="center" style="width: 10%">与本项目的关系：</td>
                <td align="center" style="width: 40%">
                    <input id="relation" name="relation" class="mini-textarea" style="width:98%;height:95px;overflow: auto;"/>
                </td>
            </tr>

        </table>
    </form>
    </div>
</div>

<div id="selectStandardWindow" title="选择标准" class="mini-window" style="width:950px;height:550px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
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
        <a class="mini-button" plain="true" onclick="searchInputList()">查询</a>
    </div>
    <div class="mini-fit">
        <div id="standardListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onrowdblclick="onRowDblClick"
             idField="id"  showColumnsMenu="false" sizeList="[20,50,100,200]" pageSize="20"
             allowAlternating="true" pagerButtons="#pagerButtons" url="${ctxPath}/standardManager/core/standard/queryList.do"
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
                     allowSort="true" renderer="statusRenderer">状态
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
                    <input type="button" style="height: 25px;width: 70px" value="确定" onclick="selectInputOK()"/>
                    <input type="button" style="height: 25px;width: 70px" value="取消" onclick="selectInputHide()"/>
                </td>
            </tr>
        </table>
    </div>
</div>

<div id="selectZlWindow" title="选择专利" class="mini-window" style="width:950px;height:550px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
    <div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px"
         borderStyle="border-left:0;border-top:0;border-right:0;">
        <span style="font-size: 14px;color: #777">专利名称: </span>
        <input class="mini-textbox" width="130" id="zl_patentName" style="margin-right: 15px"/>
        <span style="font-size: 14px;color: #777">专利（申请）号: </span>
        <input class="mini-textbox" width="130" id="zl_applicationNumber" style="margin-right: 15px"/>
        <a class="mini-button" iconCls="icon-search" plain="true" onclick="searchInputList()">查询</a>
    </div>
    <div class="mini-fit">
        <div id="zlListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onrowdblclick="onRowDblClick"
             idField="zgzlId"  showColumnsMenu="false" sizeList="[20,50,100,200]" pageSize="20"
             allowAlternating="true" pagerButtons="#pagerButtons" url="${ctxPath}/zhgl/core/zlgl/queryListData.do"
        >
            <div property="columns">
                <div type="checkcolumn" width="30"></div>
                <div field="patentName" sortField="patentName" width="130px" headerAlign="center" align="center" allowSort="false">专利名称</div>
                <div field="zllxName" displayField="zllxName" width="80px" headerAlign="center" align="center" allowSort="false">专利类型</div>
                <div field="applicationNumber"  width="130px" headerAlign="center" align="center" allowSort="false">专利（申请）号</div>
                <div field="theInventors"  width="150px" headerAlign="center" align="center" allowSort="false">发明人</div>
                <div field="gnztName" sortField="gnztId" displayField="gnztName" width="70px" headerAlign="center" align="center" allowSort="true">状态</div>
                <div field="reportName" align="center" width="130px" headerAlign="center" allowSort="false">提案名称</div>
            </div>
        </div>
    </div>
    <div property="footer" style="padding:5px;height: 35px">
        <table style="width:100%;height: 100%">
            <tr>
                <td style="width:120px;text-align:center;">
                    <input type="button" style="height: 25px;width: 70px" value="确定" onclick="selectInputOK()"/>
                    <input type="button" style="height: 25px;width: 70px" value="取消" onclick="selectInputHide()"/>
                </td>
            </tr>
        </table>
    </div>
</div>

<div id="selectQbWindow" title="选择情报报告" class="mini-window" style="width:950px;height:550px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
    <div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px"
         borderStyle="border-left:0;border-top:0;border-right:0;">
        <span class="text" style="width:auto">公司: </span>
        <input id="qb_companyName" name="companyName" class="mini-combobox" style="width:150px;" textField="text"
               valueField="id" emptyText="请选择..." data="[{id:'三一',text:'三一'},{id:'卡特',text:'卡特'},{id:'小松',text:'小松'},{id:'柳工',text:'柳工'},{id:'临工',text:'临工'},{id:'其他',text:'其他'}]"
               allowInput="true" showNullItem="true" nullItemText="请选择..."/>
        <span class="text" style="width:auto">项目: </span>
        <input id="qb_projectName" name="projectName" class="mini-combobox" style="width:150px;" textField="text"
               valueField="id" emptyText="请选择..." data="[{id:'成本价格',text:'成本价格'},{id:'产品技术',text:'产品技术'},{id:'研发体系',text:'研发体系'},{id:'产权创新',text:'产权创新'}]"
               allowInput="true" showNullItem="true" nullItemText="请选择..."
               onvaluechanged="valuechanged()"/>
        <span class="text" style="width:auto">类别: </span>
        <input id="qb_qbgzType" name="qbgzType" class="mini-combobox" style="width:150px;" textField="text"
               valueField="id" emptyText="请选择..." allowInput="true" showNullItem="true" nullItemText="请选择..."/>
        <span style="font-size: 14px;color: #777">情报名称: </span>
        <input class="mini-textbox" width="130" id="qb_qbName" style="margin-right: 15px"/>
        <a class="mini-button" iconCls="icon-search" plain="true" onclick="searchInputList()">查询</a>
    </div>
    <div class="mini-fit">
        <div id="qbListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onrowdblclick="onRowDblClick"
             idField="qbgzId"  showColumnsMenu="false" sizeList="[20,50,100,200]" pageSize="20"
             allowAlternating="true" pagerButtons="#pagerButtons" url="${ctxPath}/Info/Qbgz/queryQbgz.do"
        >
            <div property="columns">
                <div type="checkcolumn" width="30"></div>
                <div field="companyName" headerAlign='center' width="40" align='center'>公司</div>
                <div field="projectName" sortField="projectName" width="40" headerAlign="center" align="center" allowSort="true">项目</div>
                <div field="qbgzType" sortField="qbgzType" width="40" headerAlign="center" align="center" allowSort="true">类别</div>
                <div field="qbNum" sortField="qbNum" width="50" headerAlign="center" align="center" allowSort="false">编号</div>
                <div field="qbName" sortField="qbName" width="110" headerAlign="center" align="center" allowSort="false">情报名称</div>
                <div field="userName" headerAlign='center' align='center' width="40">发布人</div>
                <div field="status" width="30" headerAlign="center" align="center" allowSort="true" renderer="onStatusRenderer">状态</div>
            </div>
        </div>
    </div>
    <div property="footer" style="padding:5px;height: 35px">
        <table style="width:100%;height: 100%">
            <tr>
                <td style="width:120px;text-align:center;">
                    <input type="button" style="height: 25px;width: 70px" value="确定" onclick="selectInputOK()"/>
                    <input type="button" style="height: 25px;width: 70px" value="取消" onclick="selectInputHide()"/>
                </td>
            </tr>
        </table>
    </div>
</div>



<script type="text/javascript">
    var jsUseCtxPath="${ctxPath}";
    mini.parse();
    var inputForm = new mini.Form("#inputForm");
    var selectStandardWindow=mini.get("selectStandardWindow");
    var standardListGrid = mini.get("standardListGrid");
    var selectZlWindow=mini.get("selectZlWindow");
    var zlListGrid = mini.get("zlListGrid");
    var selectQbWindow=mini.get("selectQbWindow");
    var qbListGrid = mini.get("qbListGrid");

    function SetData(params) {
        if(params) {
            inputForm.setData(params);
        }
    }

    function statusRenderer(e) {
        var record = e.record;
        var status = record.standardStatus;
        var arr = [{'key': 'draft', 'value': '草稿', 'css': 'orange'},
            {'key': 'enable', 'value': '有效', 'css': 'green'},
            {'key': 'disable', 'value': '已废止', 'css': 'red'}
        ];

        return $.formatItemValue(arr, status);
    }

    function onStatusRenderer(e) {
        var record = e.record;
        var status = record.status;

        var arr = [ {'key' : 'DRAFTED','value' : '草稿','css' : 'orange'},
            {'key' : 'RUNNING','value' : '审批中','css' : 'green'},
            {'key' : 'SUCCESS_END','value' : '批准','css' : 'blue'}
        ];

        return $.formatItemValue(arr,status);
    }
    function valuechanged() {
        var projectName = mini.get("qb_projectName").getValue();
        if ("成本价格" == projectName) {
            mini.get("qb_qbgzType").setData("[{id:'物料成本',text:'物料成本'},{id:'挖机售价',text:'挖机售价'},{id:'供应商情况',text:'供应商情况'}]");
        } else if ("产品技术" == projectName) {
            mini.get("qb_qbgzType").setData("[{id:'新品信息',text:'新品信息'},{id:'新品计划',text:'新品计划'},{id:'创新规划',text:'创新规划'}]");
        } else if ("研发体系" == projectName) {
            mini.get("qb_qbgzType").setData("[{id:'研发体系文件',text:'研发体系文件'},{id:'机构及人员信息',text:'机构及人员信息'}" +
                ",{id:'海外研发情况',text:'海外研发情况'},{id:'奖项申报',text:'奖项申报'}]");
        } else if ("产权创新" == projectName) {
            mini.get("qb_qbgzType").setData("[{id:'专利',text:'专利'},{id:'论文',text:'论文'},{id:'标准',text:'标准'}]");
        }
    }

</script>
</body>
</html>