<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>科技计划项目列表</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/rdmZhgl/awardSciencePlanProjectEdit.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
</head>
<body>

<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">

            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">参与人: </span>
                    <input class="mini-textbox" id="prizewinner" name="prizewinner">
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">承担单位: </span>
                    <input class="mini-textbox" id="commendUnit" name="commendUnit">
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">获奖类别: </span>
                    <input id="awardType" name="awardType" class="mini-combobox" style="width:150px;"
                           textField="key" valueField="value" emptyText="请选择..." onvaluechanged="searchFrm"
                           required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                           data="[ {'key' : '国家级','value' : 'gjj'},{'key' : '省部级、市级','value' : 'sbj'},{'key' : '其他(不计入画像)','value' : 'other'}]"
                    />
                </li>

                <li class="liBtn">
					<span class="unfoldBtn" onclick="no_more(this,'moreBox')">
						<em>展开</em>
						<i class="unfoldIcon"></i>
					</span>
                </li>
                <div id="moreBox">
                    <ul>
                        <li style="margin-right: 15px">
                            <span class="text" style="width:auto">项目名称: </span>
                            <input class="mini-textbox" id="projectName" name="projectName">
                        </li>
                       <%-- <li style="margin-right: 15px">
                            <span class="text" style="width:auto">获奖时间 从 </span>:<input id="rdTimeStart"
                                                                                        name="rdTimeStart"
                                                                                        class="mini-datepicker"
                                                                                        format="yyyy-MM-dd"
                                                                                        style="width:120px"/>
                        </li>
                        <li style="margin-right: 15px">
                            <span class="text-to" style="width:auto">至: </span><input id="rdTimeEnd" name="rdTimeEnd"
                                                                                      class="mini-datepicker"
                                                                                      format="yyyy-MM-dd"
                                                                                      style="width:120px"/>
                        </li>--%>

                    </ul>
                </div>


                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearFormUpgrade()">清空查询</a>
                    <div style="display: inline-block" class="separator"></div>
                    <a id="addId" class="mini-button" style="margin-right: 5px" plain="true"
                       onclick="openAsppeEditWindow('add','')">新增</a>
                    <a id="deletedId" class="mini-button btn-red" style="margin-right: 5px" plain="true"
                       onclick="removeAsppe()">删除</a>
                    <a id="importMaterialName" class="mini-button" style="margin-right: 5px" plain="true"
                       onclick="importAgpMaterialName()">批量导入</a>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="exportAsppe()">导出</a>
                </li>
            </ul>

        </form>
    </div>
</div>

<div class="mini-fit" style="height: 100%;">

    <div id="asppeListGrid" class="mini-datagrid" style="width: 200%; height: 100%;" allowResize="false"
         onlyCheckSelection="true"
         url="${ctxPath}/zhgl/core/awardSciencePlanProject/getAsppList.do" idField="id"
         multiSelect="true" showColumnsMenu="true" sizeList="[50,100,200]" pageSize="50" allowAlternating="true"
         pagerButtons="#pagerButtons">

        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div name="action" cellCls="actionIcons" width="100" headerAlign="center" align="center"
                 renderer="onActionRenderer" cellStyle="padding:0;">操作
            </div>
            <div name="awardType" field="awardType" width="80" headerAlign="center" align="center"
                 renderer="onAwardTypeRenderer" allowSort="true">获奖类别
            </div>
            <div name="competentOrganization" field="competentOrganization" width="100" headerAlign="center" align="center" allowSort="true">
                主管单位
            </div>
            <div name="bonusType" field="bonusType" width="100" headerAlign="center" align="center" allowSort="true">
                奖项类别
            </div>
            <div name="projectName" field="projectName" width="100" headerAlign="center" align="center" allowSort="true">
                项目名称
            </div>
            <div name="commendUnit" field="commendUnit" width="100" headerAlign="center" align="center" allowSort="true">
                承担单位
            </div>
            <div name="prizewinner" field="prizewinner" width="100" headerAlign="center" align="center" allowSort="true">
                参与人
            </div>
            <div name="contractBeginTime" field="contractBeginTime" width="60" headerAlign="center" align="center" allowSort="true">
                开始时间
            </div>
            <div name="contractEndTime" field="contractEndTime" width="60" headerAlign="center" align="center" allowSort="true">
                结束时间
            </div>
            <div name="certificateNumber" field="certificateNumber" width="100" headerAlign="center" align="center" allowSort="true">
                合同编号
            </div>
            <div name="projectAggregateAmount" field="projectAggregateAmount" width="80" headerAlign="center" align="center" allowSort="true">
                总投资
            </div>
            <div name="governmentFundingMoney" field="governmentFundingMoney" width="100" headerAlign="center" align="center" allowSort="true">
                资金补助总计
            </div>
            <div name="fundingMoneyTime" field="fundingMoneyTime" width="110" headerAlign="center" align="center" allowSort="true">
                已拨付金额及拨付时间
            </div>
            <div name="fundingMoneyPlanTime" field="fundingMoneyPlanTime" width="120" headerAlign="center" align="center" allowSort="true">
               补助剩余金额及计划拨付时间
            </div>
            <div name="stageExplain" field="stageExplain" width="110" headerAlign="center" align="center" allowSort="true">
                所处阶段及进展说明
            </div>
            <div name="checkAcceptTime" field="checkAcceptTime" width="80" headerAlign="center" align="center" allowSort="true">
                计划验收时间
            </div>
            <div name="checkAcceptPracticalTime" field="checkAcceptPracticalTime" width="80" headerAlign="center" align="center" allowSort="true">
                实际验收时间
            </div>
            <%--<div name="prizeTime" field="prizeTime" width="80" headerAlign="center" align="center" allowSort="true">
                获奖时间
            </div>--%>

            <div field="fileId" width="0" headerAlign="center" align="center" allowSort="false">
                <input id="fileId" name="fileId" class="mini-hidden"/>
            </div>
            <div name="remark" field="remark" width="80" headerAlign="center" align="center" allowSort="true">备注</div>
            <div name="CREATE_TIME_" field="CREATE_TIME_" width="80" headerAlign="center" align="center"
                 allowSort="true">创建时间
            </div>
            <div field="fileName" width="70" headerAlign="center" align="center" allowSort="false" renderer="onActionRendererCpa"
            >
                附件
            </div>
        </div>
    </div>
</div>

<!--导出Excel相关HTML-->
<form id="excelForm" action="${ctxPath}/zhgl/core/awardSciencePlanProject/exportAsppeList.do" method="post"
      target="excelIFrame">
    <input type="hidden" name="filter" id="filter"/>
</form>
<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>

<div id="importWindow" title="科技计划项目奖导入窗口" class="mini-window" style="width:750px;height:280px;"
     showModal="true" showFooter="false" allowResize="true">
    <p style="display: inline-block;color: red;font-size:15px;vertical-align: middle">
    </p>
    <div style="display: inline-block;float: right;">
        <a id="importBtn" class="mini-button" onclick="importAsppeMaterial()">导入</a>
        <a id="closeWindow" class="mini-button btn-red" onclick="closeImportWindow()">关闭</a>
    </div>
    <div class="mini-fit" style="font-size: 14px;margin-top: 30px">
        <form id="formImport" method="post">
            <table class="table-detail" cellspacing="1" cellpadding="0">
                <tr>
                    <td style="width: 30%">上传模板：</td>
                    <td style="width: 70%;">
                        <a href="#" style="color:blue;text-decoration:underline;" onclick="downAsppeImportTemplate()">科技计划项目奖导入模板.xls</a>
                    </td>
                </tr>
                <tr>
                    <td style="width: 30%">操作：</td>
                    <td>
                        <input class="mini-textbox" style="width:60%;float: left" id="fileName" name="fileName"
                               readonly/>
                        <input id="inputFile" style="display:none;" type="file" onchange="getSelectFile()"
                               accept="application/xls"/>
                        <a id="uploadFileBtn" class="mini-button" style="float: left;margin-left: 10px"
                           onclick="uploadFile">选择文件</a>
                        <a id="clearFileBtn" class="mini-button" style="float: left;margin-left: 10px"
                           onclick="clearUploadFile">清除</a>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var asppeListGrid = mini.get("asppeListGrid");
    var currentUserId = "${currentUserId}";

    var currentUserNo = "${currentUserNo}";
    var updateJlWindow = mini.get("updateJlWindow");
    var currentUserName = "${currentUserName}";
    var currentTime = "${currentTime}";
    //添加列表
    var quoteLbjWindow = mini.get("quoteLbjWindow");
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";
    var importWindow = mini.get("importWindow");
    var currentUserRoles=${currentUserRoles};

    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var id = record.id;
        var s = '';
        s += '<span  title="查看" onclick="openAsppeEditWindow(\'detail\',\'' + id + '\')">查看</span>';
        if(whetherJxry(currentUserRoles)){
            s += '<span  title="修改" onclick="openAsppeEditWindow(\'update\',\'' + id + '\')">编辑</span>';

            s += '<span  title="删除" onclick="removeAsppe(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
        }else {
            s+='<span  title="修改" style="color: silver">编辑</span>';
            s+='<span  title="删除" style="color: silver">删除</span>';
        }


        return s;
    }

    function searchFrm() {
        var parent = $(".search-form");
        var inputAry = $("input", parent);
        searchByInput(inputAry);
    }

    /**
     * 附件_行功能按钮
     */
    function onActionRendererCpa(e) {
        var record = e.record;
        var cellHtml = '';
        if(record.fileName){
            //预览按钮添加
            cellHtml = returnAsppePreviewSpan(record.fileName, record.fileId,  record.id, coverContent);
            //增加下载按钮 某些节点/用户提供下载功能 （未做）

            cellHtml += '&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="downLoadAsppeFile(\'' + record.fileName + '\',\'' + record.fileId + '\',\'' + record.id + '\')">下载</span>';
        }else {
            cellHtml+='<span  title="预览" style="color: silver">预览</span>';
            cellHtml+='&nbsp;&nbsp;&nbsp;<span  title="下载" style="color: silver">下载</span>';
        }

        return cellHtml;
    }

    /**
     *清空查询
     */
    function clearFormUpgrade() {

        mini.get("prizewinner").setValue("");
        mini.get("commendUnit").setValue("");
        mini.get("awardType").setValue("");
        mini.get("projectName").setValue("");


        asppeListGrid.load();
    }

    function onAwardTypeRenderer(e) {
        var record = e.record;
        var awardType = record.awardType;

        var arr = [

            {'key': 'gjj', 'value': '国家级'}, {'key': 'sbj', 'value': '省部级、市级'}, {'key': 'other', 'value': '其他(不计入画像)'}
        ];

        return $.formatItemValue(arr, awardType);
    }
</script>
<redxun:gridScript gridId="asppeListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>