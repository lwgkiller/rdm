<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>编辑申请</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmZhgl/saleFileApplyEdit.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>

<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="processInfo" style="display: none" class="mini-button" onclick="processInfo()"><spring:message code="page.saleFileApplyEdit.name" /></a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()"><spring:message code="page.saleFileApplyEdit.name1" /></a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin:0 auto; width: 80%;">
        <form id="applyForm" method="post">
            <input id="id" name="id" class="mini-hidden"/>
            <input id="taskId_" name="taskId_" class="mini-hidden"/>
            <input id="instId" name="instId" class="mini-hidden"/>
            <input id="applyType" name="applyType" class="mini-hidden"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0">
                <caption>
                    <spring:message code="page.saleFileApplyEdit.name2" />
                </caption>
                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.saleFileApplyEdit.name3" />：</td>
                    <td style="min-width:170px">
                        <input id="editorUserId" name="editorUserId" textname="editorUserName"
                               class="mini-user rxc" plugins="mini-user" style="width:98%;height:34px;" allowinput="false"
                               label="<spring:message code="page.saleFileApplyEdit.name3" />" length="50" mainfield="no" single="true" enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 20%"><spring:message code="page.saleFileApplyEdit.name4" />：</td>
                    <td style="min-width:170px">
                        <input id="editorUserDeptId" name="editorUserDeptId" class="mini-dep rxc" plugins="mini-dep"
                               data-options="{'single':'true','config':{'type':'','grouplevel':'','groupid':'','groupidText':''}}"
                               style="width:98%;height:34px" allowinput="false" label="<spring:message code="page.saleFileApplyEdit.name4" />" textname="editorUserDeptName" length="500"
                               maxlength="500" minlen="0" single="true" required="false" initlogindep="false" showclose="false"
                               mwidth="160" wunit="px" mheight="34" hunit="px" enabled="false"/>
                    </td>
                <tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        <spring:message code="page.saleFileApplyEdit.name5" /><span style="color: #ff0000">*</span>：
                    </td>
                    <td align="center" rowspan="1">
                        <input id="fileType" name="fileType" class="mini-combobox rxc" plugins="mini-combobox"
                               style="width:100%;height:34px" label="<spring:message code="page.saleFileApplyEdit.name5" />："  onvaluechanged="onChangeFileType"
                               length="50"
                               only_read="false" required="true" allowinput="false" mwidth="100"
                               wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                               textField="text" valueField="key_" emptyText="<spring:message code="page.saleFileApplyEdit.name6" />..."
                               url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=saleFile_WJFL"
                               nullitemtext="<spring:message code="page.saleFileApplyEdit.name6" />..." emptytext="<spring:message code="page.saleFileApplyEdit.name6" />..."/>
                    </td>
                <td style="text-align: center;width: 20%"><spring:message code="page.saleFileApplyEdit.name7" /><span style="color: #ff0000">*</span>:
                    <image src="${ctxPath}/styles/images/question.png"
                           style="cursor: pointer;vertical-align: middle"
                           title="售前文件以文件的归档日期作为版本号"/></td>
                <td colspan="1">
                    <input id="version"  name="version" class="mini-textbox" style="width:98%;"/>
                </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.saleFileApplyEdit.name8" /><span style="color: #ff0000">*</span>：</td>
                    <td colspan="1">
                        <input id="designModel" required name="designModel" style="width:99%;" class="mini-buttonedit"
                               textname="designModel" allowInput="false" onbuttonclick="selectDesignModel()"/>

                    </td>
                    <td style="text-align: center;width: 20%"><spring:message code="page.saleFileApplyEdit.name9" /><span style="color: #ff0000">*</span>：</td>
                    <td colspan="1">
                        <input id="saleModel" required name="saleModel" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        <spring:message code="page.saleFileApplyEdit.name10" />：
                    </td>
                    <td align="center" rowspan="1">
                        <input id="director" name="director" textname="directorName" enabled="false"
                               class="mini-user rxc" plugins="mini-user" style="width:98%;height:34px;" allowinput="false"
                               label="<spring:message code="page.saleFileApplyEdit.name10" />" length="50" mainfield="no" single="true"
                        />
                    </td>
                    <td style="text-align: center;width: 20%">规划销售区域：</td>
                    <td style="min-width:170px">
                        <input id="region"  name="region" class="mini-textbox" enabled="false" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.saleFileApplyEdit.name11" /><span style="color: #ff0000">*</span></td>
                    <%--<td colspan="3">--%>
                    <td style="min-width:170px">
                    <input id="language" name="language" class="mini-combobox rxc" plugins="mini-combobox"
                               style="width:100%;height:34px" label="<spring:message code="page.saleFileApplyEdit.name11" />："
                           <%--onvaluechanged="onChangeFileType"--%>
                               length="50"
                               only_read="false" required="true" allowinput="false" mwidth="100"
                               wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                               textField="text" valueField="key_" emptyText="<spring:message code="page.saleFileApplyEdit.name6" />..."
                               url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=YZ"
                               nullitemtext="<spring:message code="page.saleFileApplyEdit.name6" />..." emptytext="<spring:message code="page.saleFileApplyEdit.name6" />..."/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center"><spring:message code="page.saleFileApplyEdit.name12" />：</td>
                    <td colspan="3">
						<textarea id="remark" name="remark" class="mini-textarea rxc" plugins="mini-textarea"
                                  style="width:99%;height:100px;line-height:25px;"
                                  label="<spring:message code="page.saleFileApplyEdit.name12" />" datatype="varchar" length="500" vtype="length:500" minlen="0"
                                  allowinput="true"
                                   mwidth="80" wunit="%" mheight="200" hunit="px"></textarea>
                    </td>
                </tr>
				<tr>
					<td style="text-align: center;height: 300px"><spring:message code="page.saleFileApplyEdit.name13" />：</td>
					<td colspan="3">
						<div style="margin-top: 10px;margin-bottom: 2px">
							<a id="addFile" class="mini-button" onclick="addSaleFile()"><spring:message code="page.saleFileApplyEdit.name14" />
                            </a>
							<span style="color: red"><spring:message code="page.saleFileApplyEdit.name15" /></span>
                            <image src="${ctxPath}/styles/images/question.png"
                                   style="cursor: pointer;vertical-align: middle"
                                   title="例文件名为XE490DK技术规格书20111228,其中的201211228为版本号"/>
						</div>
						<div id="fileListGrid" class="mini-datagrid" style="width: 100%; height: 85%" allowResize="false"
							 idField="id" url="${ctxPath}/rdmZhgl/core/saleFile/saleFileList.do?fileModel=sq&applyId=${applyId}&scene=apply" autoload="true"
							 multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
							<div property="columns">
								<div type="indexcolumn" align="center" width="20"><spring:message code="page.saleFileApplyEdit.name16" /></div>
								<div field="fileName" align="center" headerAlign="center" width="150"><spring:message code="page.saleFileApplyEdit.name17" /></div>
								<div field="fileSize" align="center" headerAlign="center" width="60"><spring:message code="page.saleFileApplyEdit.name18" /></div>
								<div field="fileDesc" align="center" headerAlign="center" width="100"><spring:message code="page.saleFileApplyEdit.name19" /></div>
								<div field="action" width="100" headerAlign='center' align="center" renderer="operationRendererSq"><spring:message code="page.saleFileApplyEdit.name20" /></div>
							</div>
						</div>
					</td>
				</tr>
                <tr>
                    <td style="text-align: center;height: 300px"><spring:message code="page.saleFileApplyEdit.name21" />：</td>
                    <td colspan="3">
                        <div style="margin-top: 10px;margin-bottom: 2px">
                            <a id="addExamine" class="mini-button" onclick="addExamineFile()"><spring:message code="page.saleFileApplyEdit.name14" /></a>
                            <span style="color: red"><spring:message code="page.saleFileApplyEdit.name22" /></span>
                        </div>
                        <div id="examineListGrid" class="mini-datagrid" style="width: 100%; height: 85%" allowResize="false"
                             idField="id" url="${ctxPath}/rdmZhgl/core/saleFile/saleFileList.do?fileModel=sp&applyId=${applyId}&scene=apply" autoload="true"
                             multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
                            <div property="columns">
                                <div type="indexcolumn" align="center" width="20"><spring:message code="page.saleFileApplyEdit.name16" /></div>
                                <div field="fileName" align="center" headerAlign="center" width="150"><spring:message code="page.saleFileApplyEdit.name17" /></div>
                                <div field="fileSize" align="center" headerAlign="center" width="60"><spring:message code="page.saleFileApplyEdit.name18" /></div>
                                <div field="fileDesc" align="center" headerAlign="center" width="100"><spring:message code="page.saleFileApplyEdit.name19" /></div>
                                <div field="userName" align="center" headerAlign="center" width="100"><spring:message code="page.saleFileApplyEdit.name23" /></div>
                                <div field="CREATE_TIME_" align="center" headerAlign="center" width="100"><spring:message code="page.saleFileApplyEdit.name24" /></div>
                                <div field="action" width="100" headerAlign='center' align="center" renderer="operationRendererSp"><spring:message code="page.saleFileApplyEdit.name20" /></div>
                            </div>
                        </div>
                    </td>
                </tr>
            </table>
        </form>
    </div>

</div>

<%--关联产品型谱弹窗--%>
<div id="spectrumWindow" title="选择产品型号"
     class="mini-window" style="width:850px;height:600px;"
     showModal="true" showFooter="true" allowResize="true">
    <div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px"
         borderStyle="border-left:0;border-top:0;border-right:0;">
        <span style="font-size: 14px;color: #777">设计型号: </span>
        <input class="mini-textbox" width="130" id="searchDesignModel" textField="designModel" valueField="designModel"
               style="margin-right: 15px"/>
        <a class="mini-button" iconCls="icon-search" plain="true" onclick="searchDesignModel()">查询</a>
    </div>

    <div class="mini-fit">
        <div id="spectrumListGrid" class="mini-datagrid" style="width: 100%; height: 100%;"
             allowResize="false" allowCellWrap="false" showCellTip="true" idField="id"
             allowCellEdit="true" allowCellSelect="true" multiSelect="false" showColumnsMenu="false"
             sizeList="[50,100,200,500]" pageSize="50" allowAlternating="true" pagerButtons="#pagerButtons"
             allowSortColumn="true" allowUnselect="true" autoLoad="false"
             url="${ctxPath}/world/core/productSpectrum/applyList.do">
            <div property="columns">
                <div type="checkcolumn" width="20"></div>
                <div field="materialCode" width="120" headerAlign="center" align="center" renderer="render"
                     allowSort="true">
                    <spring:message code="page.productSpectrumList.wlh"/>
                </div>
                <div field="designModel" width="150" headerAlign="center" align="center" renderer="render" allowSort="true">
                    <spring:message code="page.productSpectrumList.sjxh"/>
                </div>
                <div field="departName" width="120" headerAlign="center" align="center" renderer="render" allowSort="true">
                    <spring:message code="page.productSpectrumList.cps"/>
                </div>
                <div field="productManagerName" width="80" headerAlign="center" align="center" renderer="render"
                     allowSort="true">
                    <spring:message code="page.productSpectrumList.cpzg"/>
                </div>
                <div field="saleModel" width="120" headerAlign="center" align="center" renderer="render" allowSort="true">
                    <spring:message code="page.productSpectrumList.xsxh"/>
                </div>
                <div field="dischargeStage" width="120" headerAlign="center" align="center" renderer="render"
                     allowSort="true">
                    <spring:message code="page.productSpectrumList.pfjd"/>
                </div>
                <div field="rdStatus" width="120" headerAlign="center" align="center" renderer="render" allowSort="true">
                    <spring:message code="page.productSpectrumList.yfzt"/>
                </div>
                <div field="yfztqr" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" headerAlign="center"
                     allowSort="true">研发状态确认时间
                </div>
                <div field="productNotes" width="180" headerAlign="center" align="center" renderer="render"
                     allowSort="true">
                    <spring:message code="page.productSpectrumList.cpsm"/>
                </div>
                <div field="tagNames" width="240" headerAlign="center" align="center" renderer="render" allowSort="true">
                    <spring:message code="page.productSpectrumList.cpbq"/>
                </div>
                <div field="manuStatus" width="80" headerAlign="center" align="center" renderer="render" allowSort="true">
                    <spring:message code="page.productSpectrumList.zzzt"/>
                </div>
                <div field="zzztqr" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" headerAlign="center"
                     allowSort="true">制造状态确认时间
                </div>
                <div field="saleStatus" width="80" headerAlign="center" align="center" renderer="render" allowSort="true">
                    <spring:message code="page.productSpectrumList.xszt"/>
                </div>
                <div field="xsztqr" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" headerAlign="center"
                     allowSort="true">销售状态确认时间
                </div>
                <div field="abroad" width="120" headerAlign="center" align="center" renderer="render" allowSort="true">
                    <spring:message code="page.productSpectrumList.ghnwxs"/>
                </div>
                <div field="region" width="180" headerAlign="center" align="center" renderer="render" allowSort="true">
                    <spring:message code="page.productSpectrumList.ghxsqyhgj"/>
                </div>
                <div field="taskName" headerAlign='center' align='center' width="80">当前任务</div>
                <div field="allTaskUserNames" headerAlign='center' align='center' width="120">当前处理人</div>
                <%--<div field="status" width="60" headerAlign="center" align="center" allowSort="true"--%>
                <%--renderer="onStatusRenderer">状态--%>
            </div>
            <div field="CREATE_TIME_" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" headerAlign="center"
                 allowSort="true">创建时间
            </div>

        </div>

    </div>
    <div property="footer" style="padding:5px;height: 35px">
        <table style="width:100%;height: 100%">
            <tr>
                <td style="width:120px;text-align:center;">
                    <input type="button" style="height: 25px;width: 70px"
                           value="确定" onclick="okWindow()"/>
                    <input type="button" style="height: 25px;width: 70px"
                           value="取消"
                           onclick="hideWindow()"/>
                </td>
            </tr>
        </table>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    let nodeVarsStr = '${nodeVars}';
    var action = "${action}";
    let jsUseCtxPath = "${ctxPath}";
    let ApplyObj =${applyObj};
    let status = "${status}";
    let applyForm = new mini.Form("#applyForm");
    var fileListGrid = mini.get("fileListGrid");
    var examineListGrid = mini.get("examineListGrid");
    var currentUserName = "${currentUserName}";
    var applyId = "${applyId}";
    var currentUserId = "${currentUserId}";
    var currentTime = "${currentTime}";
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>" + xzxgwjjxyxgs;
    // @mh增加设计型号选择弹窗
    var spectrumWindow = mini.get("spectrumWindow");
    var spectrumListGrid = mini.get("spectrumListGrid");
    var userRole = "${userRole}";

    function operationRendererSq(e) {
        var record = e.record;
        var cellHtml = '';
        cellHtml = returnSalePreviewSpan(record.fileName, record.id, record.applyId, coverContent, record.fileSize);
        cellHtml += '&nbsp;&nbsp;&nbsp;<span title=' + saleFileApplyEdit_name + ' style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downFile(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">' + saleFileApplyEdit_name + '</span>';
        //增加删除按钮
        if (action == 'edit' || (action == 'task' && isFirstNode)) {
            var deleteUrl = "/rdmZhgl/core/saleFile/delSaleFile.do"
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title=' + saleFileApplyEdit_name1 + ' style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.applyId + '\',\'' + deleteUrl + '\')">' + saleFileApplyEdit_name1 + '</span>';
        }
        return cellHtml;
    }
    function operationRendererSp(e) {
        var record = e.record;
        var cellHtml = '';
        cellHtml = returnSalePreviewSpan(record.fileName, record.id, record.applyId, coverContent, record.fileSize);
        cellHtml += '&nbsp;&nbsp;&nbsp;<span title=' + saleFileApplyEdit_name + ' style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downFile(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">' + saleFileApplyEdit_name + '</span>';
        //增加删除按钮
        if (action == 'task' && record.CREATE_BY_==currentUserId) {
            var deleteUrl = "/rdmZhgl/core/saleFile/delSaleFile.do"
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title=' + saleFileApplyEdit_name1 + ' style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFileSp(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.applyId + '\',\'' + deleteUrl + '\')">' + saleFileApplyEdit_name1 + '</span>';
        }
        return cellHtml;
    }
    function deleteFileSp(fileName,fileId,formId,urlValue) {
        mini.confirm(saleFileApplyEdit_name2, saleFileApplyEdit_name3,
            function (action) {
                if (action == "ok") {
                    var url = jsUseCtxPath + urlValue;
                    var data = {
                        fileName: fileName,
                        id: fileId,
                        formId: formId
                    };
                    $.ajax({
                        url:url,
                        method:'post',
                        contentType: 'application/json',
                        data:mini.encode(data),
                        success:function (json) {
                            if(examineListGrid) {
                                examineListGrid.load();
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
