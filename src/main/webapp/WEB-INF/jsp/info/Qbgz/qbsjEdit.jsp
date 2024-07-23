
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge">
    <title>情报报告</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/qbgz/qbsjEdit.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
    <style>
        html, body {
            overflow: hidden;
            height: 100%;
            width: 100%;
            padding: 0;
            margin: 0;
        }

    </style>
</head>
<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="processInfo" class="mini-button" style="display: none" onclick="processInfo()">流程信息</a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin: 0 auto; width: 70%;">
        <form id="formQbgz" method="post">
            <input id="id" name="id" class="mini-hidden"/>
            <input id="instId" name="instId" class="mini-hidden"/>
            <input id="CREATE_BY_" name="CREATE_BY_" class="mini-hidden"/>
            <table class="table-detail" cellspacing="1" cellpadding="0">
                <tr>
                    <td style="width: 10%">情报编号：</td>
                    <td style="width: 40%">
                        <input id="qbNumber" name="qbNumber" class="mini-textbox" readonly style="width:70%"/>
                        <span style="color: red">(流程结束后自动生成)</span>
                    </td>
                    <td style="width: 10%">情报名称：<span style="color:red">*</span></td>
                    <td style="width: 40%">
                        <input id="qbName" name="qbName" style="width:98%;" class="mini-textbox"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 10%">情报大类：<span style="color:red">*</span></td>
                    <td>
                        <input id="bigTypeName" name="bigTypeName" class="mini-combobox" style="width:98%;"
                               textField="text"
                               valueField="id" emptyText="请选择..."
                               data="[{id:'产品情报',text:'产品情报'},{id:'研发体系',text:'研发体系'},{id:'技术创新',text:'技术创新'},
                         {id:'海外情报专题',text:'海外情报专题'},{id:'电动挖情报专题',text:'电动挖情报专题'},{id:'全球产业布局',text:'全球产业布局'}]"
                               required="true" allowInput="false" showNullItem="false" nullItemText="请选择..."
                               onvaluechanged="bigTypeValueChanged()"
                        />
                    </td>
                    <td style="width: 10%;">情报小类：<span style="color:red">*</span></td>
                    <td >
                        <input id="qbTypeId" name="qbTypeId" class="mini-combobox" style="width:98%;"
                               textField="smallTypeName"
                               valueField="id" emptyText="请选择..."
                               allowInput="false" showNullItem="false" nullItemText="请选择..."/>
                    </td>
                </tr>

                <tr>
                    <td style="width: 10%;">情报价值：<span style="color:red">*</span></td>
                    <td >
                        <input id="qbValue" name="qbValue"  class="mini-combobox" style="width:98%;" textField="text"
                               valueField="id" emptyText="请选择..."
                               data="[{id:'高',text:'高'},{id:'中',text:'中'},{id:'低',text:'低'}]"
                               allowInput="false" showNullItem="false" nullItemText="请选择..."/>
                    </td>
                    <td style="text-align: left">情报来源：<span style="color:red">*</span></td>
                    <td >
                        <input id="qbSource" name="qbSource"  class="mini-combobox" style="width:98%;" textField="text"
                               valueField="id" emptyText="请选择..."
                               data="[{id:'报刊杂志',text:'报刊杂志'},{id:'行业协会和贸易组织出版物',text:'行业协会和贸易组织出版物'},
                               {id:'产业/市场调查报告',text:'产业/市场调查报告'},{id:'政府机构档案，贸易部数据',text:'政府机构档案，贸易部数据'},
                               {id:'政府出版物',text:'政府出版物'},{id:'联机数据库',text:'联机数据库'},
                               {id:'国际互联网',text:'国际互联网'},{id:'公司产品介绍、样本、手册/财务公报',text:'公司产品介绍、样本、手册/财务公报'},
                               {id:'展览会',text:'展览会'},{id:'企业招聘广告',text:'企业招聘广告'},
                               {id:'企业内部职员',text:'企业内部职员'},{id:'经销商',text:'经销商'},
                               {id:'行业的提供商和制造商',text:'行业的提供商和制造商'},{id:'行业协会',text:'行业协会'},
                               {id:'行业主管部门',text:'行业主管部门'},{id:'信用调查报告',text:'信用调查报告'},
                               {id:'客户群',text:'客户群'},{id:'竞争对手',text:'竞争对手'},
                               {id:'反求工程',text:'反求工程'},{id:'专业调查咨询机构',text:'专业调查咨询机构'}
                               ]"
                               allowInput="false" showNullItem="false" nullItemText="请选择..."/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: left">情报可靠性：<span style="color:red">*</span></td>
                    <td >
                        <input id="qbKKX" name="qbKKX"  class="mini-combobox" style="width:98%;" textField="text"
                               valueField="id" emptyText="请选择..."
                               data="[{id:'A--完全可靠',text:'A--完全可靠'},{id:'B--通常可靠',text:'B--通常可靠'},
                               {id:'C--比较可靠',text:'C--比较可靠'},{id:'D--通常不可靠',text:'D--通常不可靠'},
                               {id:'E--不可靠',text:'E--不可靠'},{id:'F--无法评价可靠性',text:'F--无法评价可靠性'}
                               ]"
                               allowInput="false" showNullItem="false" nullItemText="请选择..."/>
                    </td>
                    <td style="text-align: left">情报准确度：<span style="color:red">*</span></td>
                    <td >
                        <input id="qbZQD" name="qbZQD"  class="mini-combobox" style="width:98%;" textField="text"
                               valueField="id" emptyText="请选择..."
                               data="[{id:'1--经其他渠道证实',text:'1--经其他渠道证实'},{id:'2--很可能是真实的',text:'2--很可能是真实的'},
                               {id:'3--可能是真实的',text:'3--可能是真实的'},{id:'4--真实性值得怀疑',text:'4--真实性值得怀疑'},
                               {id:'5--很不可能',text:'5--很不可能'},{id:'6--无法评价真实性',text:'6--无法评价真实性'}
                               ]"
                               allowInput="false" showNullItem="false" nullItemText="请选择..."/>
                    </td>
                </tr>
                <tr>
                <td style="width: 10%">备注说明：</td>
                <td colspan="3">
                    <input id="qbDesc" name="qbDesc" class="mini-textarea" style="width:98%;height: 150px"/>
                </td>
                </tr>
                <tr>
                    <td style="width: 10%;height: 250px">附件列表：</td>
                    <td colspan="3">
                        <div style="margin-top: 25px;margin-bottom: 2px">
                            <a id="addFile" class="mini-button" onclick="fileupload()">添加附件</a>
                            <span style="color: red">注：添加附件前，请先进行草稿的保存。</span>
                        </div>
                        <div id="fileListGrid" class="mini-datagrid" style="width: 100%; height: 100%"
                             allowResize="false"
                             idField="id" autoload="true"
                             url="${ctxPath}/Info/Qbsj/getQbsjFileList.do?qbgzId=${qbgzId}"
                             multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
                            <div property="columns">
                                <div type="indexcolumn" align="center" width="20">序号</div>
                                <div field="fileName" width="140" headerAlign="center" align="center">附件名称</div>
                                <div field="fileSize" width="80" headerAlign="center" align="center">附件大小</div>
                                <div field="note" width="80" headerAlign="center" align="center">备注</div>
                                <div field="action" width="100" headerAlign='center' align="center" renderer="operationRenderer">操作
                                </div>
                            </div>
                        </div>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var action = "${action}";
    var jsUseCtxPath = "${ctxPath}";
    var qbgzId = "${qbgzId}";
    var isQbzy =${isQbzy};
    var formQbgz = new mini.Form("#formQbgz");
    var fileListGrid = mini.get("fileListGrid");
    var nodeVarsStr = '${nodeVars}';
    var currentUserId = "${currentUserId}";
    var currentUserName = "${currentUserName}";
    var currentTime = "${currentTime}";
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";


    function valuechanged() {
        var projectName = mini.get("projectName").getValue();
        if ("成本价格" == projectName) {
            mini.get("qbgzType").setData("[{id:'物料成本',text:'物料成本'},{id:'挖机售价',text:'挖机售价'},{id:'供应商情况',text:'供应商情况'}]");
        } else if ("产品技术" == projectName) {
            mini.get("qbgzType").setData("[{id:'新品信息',text:'新品信息'},{id:'新品计划',text:'新品计划'},{id:'创新规划',text:'创新规划'}]");
        } else if ("研发体系" == projectName) {
            mini.get("qbgzType").setData("[{id:'研发体系文件',text:'研发体系文件'},{id:'机构及人员信息',text:'机构及人员信息'}" +
                ",{id:'海外研发情况',text:'海外研发情况'},{id:'奖项申报',text:'奖项申报'}]");
        } else if ("产权创新" == projectName) {
            mini.get("qbgzType").setData("[{id:'专利',text:'专利'},{id:'论文',text:'论文'},{id:'标准',text:'标准'}]");
        }
    }

    function operationRenderer(e) {
        var record = e.record;
        var cellHtml = returnQbsjPreviewSpan(record.fileName, record.id, record.qbBaseInfoId, coverContent);
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="downLoadQbsjFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.qbBaseInfoId + '\')">下载</span>';

        //增加删除按钮
        if (action == 'edit' || (action == 'task' && stageName == 'bianZhi')) {
            var deleteUrl = "/Info/Qbsj/deleteQbgzFile.do"
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.qbBaseInfoId + '\',\'' + deleteUrl + '\')">删除</span>';
        }
        return cellHtml;
    }

</script>
</body>
</html>
