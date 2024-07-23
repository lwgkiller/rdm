<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>欧美澳类中国售前文件编辑界面</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmZhgl/saleFileOMAApplyEdit.js?version=${static_res_version}" type="text/javascript"></script>
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
<div id="toolBar" class="topToolBar" style="display: none">
    <div>
        <a id="saveStandard" class="mini-button" onclick="saveBusiness()">保存</a>
        <a id="closeWindow2" class="mini-button btn-red" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin:0 auto; width: 80%;">
        <form id="applyForm" method="post">
            <input id="id" name="id" class="mini-hidden"/>
            <input id="taskId_" name="taskId_" class="mini-hidden"/>
            <input id="instId" name="instId" class="mini-hidden"/>
            <input id="applyType" name="applyType" class="mini-hidden"/>
            <input id="fileStatus" name="fileStatus" class="mini-hidden"/>
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
                        文件类别<span style="color: #ff0000">*</span>：
                    </td>
                    <td align="center" rowspan="1">
                        <input id="fileType" name="fileType" class="mini-combobox rxc" plugins="mini-combobox"
                               style="width:100%;height:34px" label="<spring:message code="page.saleFileApplyEdit.name5" />："
                               length="50"
                               only_read="false" required="true" allowinput="false" mwidth="100"
                               wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                               textField="text" valueField="key_" emptyText="<spring:message code="page.saleFileApplyEdit.name6" />..."
                               url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=sailFileOMA_WJFL"
                               nullitemtext="<spring:message code="page.saleFileApplyEdit.name6" />..." emptytext="<spring:message code="page.saleFileApplyEdit.name6" />..."/>
                    </td>
                <td style="text-align: center;width: 20%"><spring:message code="page.saleFileApplyEdit.name7" /><span style="color: #ff0000">*</span>:
                    <image src="${ctxPath}/styles/images/question.png"
                           style="cursor: pointer;vertical-align: middle"
                           title="售前文件以文件的申请日期作为版本号"/></td>
                <td colspan="1">
                    <input id="version"  name="version" class="mini-textbox" style="width:98%;" allowinput="false"/>
                </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.saleFileApplyEdit.name8" />：</td>
                    <td colspan="1">
                        <input id="designModel"  name="designModel" style="width:99%;" class="mini-buttonedit"
                               textname="designModel" allowInput="true" onbuttonclick="selectDesignModel()"/>

                    </td>
                    <td style="text-align: center;width: 20%"><spring:message code="page.saleFileApplyEdit.name9" />：</td>
                    <td colspan="1">
                        <input id="saleModel"  name="saleModel" class="mini-textbox" style="width:98%;"/>
                    </td>
                </tr>


                <tr>
                    <td style="text-align: center;width: 20%">物料编码：</td>
                    <td colspan="1">
                        <input id="materialCode"  name="materialCode" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td align="center" style="white-space: nowrap;">
                        产品主管：
                    </td>
                    <td align="center" rowspan="1">
                        <input id="director" name="director" textname="directorName" enabled="false"
                               class="mini-user rxc" plugins="mini-user" style="width:98%;height:34px;" allowinput="true"
                               label="<spring:message code="page.saleFileApplyEdit.name10" />" length="50" mainfield="no" single="true"
                        />
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
                               url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=AMOYZFL"
                               nullitemtext="<spring:message code="page.saleFileApplyEdit.name6" />..." emptytext="<spring:message code="page.saleFileApplyEdit.name6" />..."/>
                    </td>
                    <td style="text-align: center;width: 20%">销售区域<span style="color: #ff0000">*</span>：</td>
                    <td style="min-width:170px">

                        <input id="region" name="region" class="mini-combobox rxc" plugins="mini-combobox"
                               style="width:100%;height:34px" label="销售区域："
                               length="50"
                               only_read="false" required="true" allowinput="false" mwidth="100"
                               wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                               textField="text" valueField="key_" emptyText="请选择..."
                               url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=AMOXSQY"
                               nullitemtext="请选择..." />
                    </td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        系统分类</span>：
                    </td>
                    <td align="center" rowspan="1">
                        <input id="systemType" name="systemType" class="mini-combobox rxc" plugins="mini-combobox"
                               style="width:100%;height:34px" label="系统分类："
                               length="50"
                               only_read="false" required="true" allowinput="false" mwidth="100"
                               wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                               textField="text" valueField="key_" emptyText="请选择..."
                               url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=sailFileOMA_XTFL"
                               nullitemtext="请选择..." />
                    </td>

                    <td style="text-align: center;width: 20%">会签意见人员<span style="color:red">*</span>：</td>
                    <td style="min-width:170px" colspan="3">
                        <input id="yjUserIds" name="yjUserIds" textname="yjUserNames"
                               property="editor" class="mini-user rxc" plugins="mini-user"
                               style="width:98%;height:34px;" allowinput="false" mainfield="no" single="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center">适用性说明：</td>
                    <td colspan="3">
						<textarea id="applicabilityDoc" name="applicabilityDoc" class="mini-textarea rxc" plugins="mini-textarea"
                                  style="width:99%;height:100px;line-height:25px;"
                                  label="适用性说明" datatype="varchar" length="500" vtype="length:500" minlen="0"
                                  allowinput="true"
                                  mwidth="80" wunit="%" mheight="200" hunit="px"></textarea>
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
                    <td style="text-align: center;height: 400px">关联明细：</td>
                    <td colspan="3">
                        <div class="mini-toolbar" id="itemButtons">
                            <a id="addItem" class="mini-button" style="margin-bottom: 5px;margin-top: 5px" onclick="addItem">添加</a>
                            <a id="deleteItem" class="mini-button btn-red" style="margin-bottom: 5px;margin-top: 5px" onclick="deleteItem">删除</a>
                        </div>
                        <div id="itemListGrid" class="mini-datagrid" style="width: 100%; height: 85%" allowResize="true" allowCellWrap="false"
                             showCellTip="true" autoload="true" idField="id" multiSelect="true" showPager="false"
                             showColumnsMenu="false" allowcelledit="true" allowcellselect="true" allowCellValid="true"
                             allowAlternating="true" oncellvalidation="onCellValidation">
                            <div property="columns">
                                <div type="checkcolumn" width="20"></div>
                                <div field="salesModel_item" width="100" headerAlign="center" align="center" renderer="render">
                                    销售型号<input property="editor" class="mini-textbox" style="width: 100%"/>
                                </div>
                                <div field="designModel_item" width="100" headerAlign="center" align="center" renderer="render">
                                    设计型号<input property="editor" class="mini-textbox" style="width: 100%"/>
                                </div>
                                <div field="materialCode_item" width="100" headerAlign="center" align="center" renderer="render">
                                    物料编码<input property="editor" class="mini-textbox" style="width: 100%"/>
                                </div>
                                <div field="cpzgId_item" width="100" headerAlign="center" displayField="cpzgId_item_name" align="center" width="30">
                                    产品主管
                                    <%--lwgkiller：此处在行内用mini-user组件，textname无效，默认会以name+"_name"作为textname--%>
                                    <input property="editor" class="mini-user rxc" plugins="mini-user" allowinput="false"
                                           mainfield="no" name="cpzgId_item"/>
                                </div>
                            </div>
                        </div>
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
							 idField="id" url="${ctxPath}/rdmZhgl/core/saleFileOMA/saleFileOMAList.do?fileModel=sq&applyId=${applyId}&scene=apply" autoload="true"
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

            </table>
        </form>
    </div>

</div>

<%--关联产品型谱弹窗--%>
<div id="spectrumWindow" title="选择产品型号"
     class="mini-window" style="width:850px;height:500px;"
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
    // var examineListGrid = mini.get("examineListGrid");
    var currentUserName = "${currentUserName}";
    var applyId = "${applyId}";
    var currentUserId = "${currentUserId}";
    var currentTime = "${currentTime}";
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>" + xzxgwjjxyxgs;
    // @mh增加设计型号选择弹窗
    var spectrumWindow = mini.get("spectrumWindow");
    var spectrumListGrid = mini.get("spectrumListGrid");
    var userRole = "${userRole}";
    var isPointRole = "${isPointRole}";
    //增加关联明细
    var itemListGrid = mini.get("itemListGrid");
    // var messageListGrid = mini.get("messageListGrid");

    function operationRendererSq(e) {
        var record = e.record;
        var cellHtml = '';
        cellHtml = returnSalePreviewSpan(record.fileName, record.id, record.applyId, coverContent, record.fileSize);
        //给下载权限
        if(userRole =='canDownload'){
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title=' + saleFileApplyEdit_name + ' style="color:#409EFF;cursor: pointer;" ' +
                'onclick="downFile(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">' + saleFileApplyEdit_name + '</span>';
        }

        //增加删除按钮
        if (action == 'edit' || (action == 'task' && isFirstNode)) {
            var deleteUrl = "/rdmZhgl/core/saleFileOMA/delSaleFileOMA.do"
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title=' + saleFileApplyEdit_name1 + ' style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFileSp(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.applyId + '\',\'' + deleteUrl + '\')">' + saleFileApplyEdit_name1 + '</span>';
        }
        //增加删除按钮
        if (action == 'task' && (isJiaodui || isShenhe)) {
            var deleteUrl = "/rdmZhgl/core/saleFileOMA/delSaleFileOMA.do"
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title=' + saleFileApplyEdit_name1 + ' style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFileSp(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.applyId + '\',\'' + deleteUrl + '\')">' + saleFileApplyEdit_name1 + '</span>';
        }

        // 服务所售前文件负责人增加删除按钮
        if (status == "SUCCESS_END" && isPointRole == "true") {
            var deleteUrl = "/rdmZhgl/core/saleFileOMA/delSaleFileOMA.do"
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
                            if(fileListGrid) {
                                fileListGrid.load();
                            }
                        }
                    });
                }
            }
        );
    }

    //..
    function addItem() {

        var newRow = {}
        itemListGrid.addRow(newRow, 0);
    }
    //..
    function deleteItem() {
        var row = itemListGrid.getSelected();
        if (!row) {
            mini.alert("请选中一条记录");
            return;
        }
        delRowGrid("itemListGrid");
    }
    //..
    function onCellValidation(e) {

        if (e.field == 'salesModel_item' && (!e.value || e.value == '')) {
            e.isValid = false;
            e.errorText = '不能为空！';
        }
        if (e.field == 'designModel_item' && (!e.value || e.value == '')) {
            e.isValid = false;
            e.errorText = '不能为空！';
        }
        if (e.field == 'materialCode_item' && (!e.value || e.value == '')) {
            e.isValid = false;
            e.errorText = '不能为空！';
        }
        if (e.field == 'cpzgId_item' && (!e.value || e.value == '')) {
            e.isValid = false;
            e.errorText = '不能为空！';
        }
    }

    function saveBusiness() {

        var formData = applyForm.getData();
        var checkResult = draftOrStartValid(formData);
        if (!checkResult.result) {
            mini.alert(checkResult.message);
            return;
        }


        var data = fileListGrid.getData();
        if (data.length < 1) {
            mini.alert("没有上传翻译文件！");
            return;
        }
        //XMLHttpRequest方式上传表单
        var xhr = false;
        try {
            //尝试创建 XMLHttpRequest 对象，除 IE 外的浏览器都支持这个方法。
            xhr = new XMLHttpRequest();
        } catch (e) {
            //使用较新版本的 IE 创建 IE 兼容的对象（Msxml2.XMLHTTP）。
            xhr = ActiveXobject("Msxml12.XMLHTTP");
        }

        if (xhr.upload) {
            xhr.onreadystatechange = function (e) {
                if (xhr.readyState == 4) {
                    if (xhr.status == 200) {
                        if (xhr.responseText) {
                            var returnObj = JSON.parse(xhr.responseText);
                            var message = '';
                            if (returnObj.message) {
                                message = returnObj.message;
                            }
                            mini.alert(message, "提示信息", function (action) {
                                if (returnObj.success) {
                                    mini.get("id").setValue(returnObj.id);
                                }
                                CloseWindow();
                            });
                        }
                    }
                }
            };

            //开始上传
            xhr.open('POST', jsUseCtxPath + '/rdmZhgl/core/saleFileOMA/saveBusiness.do', false);
            xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
            var fd = new FormData();
            if (formData) {
                for (key in formData) {
                    fd.append(key, formData[key]);
                }
            }
            // fd.append('businessFile', file);
            xhr.send(fd);
        }
    }
</script>
</body>
</html>
