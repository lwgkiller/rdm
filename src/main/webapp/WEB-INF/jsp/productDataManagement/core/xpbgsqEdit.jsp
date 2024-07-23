<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html >
<html>
<head>
    <title>型谱变更申请</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}"
            type="text/javascript"></script>

</head>
<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="processInfo" class="mini-button" style="display: none" onclick="processInfo()">流程信息</a>
        <a id="closeWindow1" class="mini-button" onclick="CloseWindow()">关闭</a>
    </div>
</div>

<div class="mini-fit">
    <div class="form-container" style="margin:0 auto; width: 100%;height:98%;padding: 0">
        <form id="xpbgsqForm" method="post" style="height: 95%;width: 98%">
            <input class="mini-hidden" id="id" name="id"/>
            <input class="mini-hidden" id="CREATE_BY_" name="CREATE_BY_"/>
            <input id="instId" name="instId" class="mini-hidden"/>
            <p style="text-align: center;font-size: 20px;font-weight: bold;margin-top: 20px">型谱变更申请</p>
            <table class="table-detail grey" cellspacing="1" cellpadding="0">
                <tr>
                    <td style="text-align: center;width: 20%">流程编号：</td>
                    <td style="min-width:170px">
                        <input id="applyNumber" name="applyNumber" class="mini-textbox" style="width:98%;"
                               enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 20%">申请人：</td>
                    <td style="min-width:170px">
                        <input id="creatorName" name="creatorName" class="mini-textbox" style="width:98%;"
                               enabled="false"/>
                    </td>

                    <%--<td style="text-align: center;width: 20%">申请时间：</td>--%>
                    <%--<td style="min-width:170px">--%>
                    <%--<input id="applyTime" name="applyTime" class="mini-textbox" style="width:98%;"--%>
                    <%--enabled="false"/>--%>
                    <%--</td>--%>
                </tr>
                <tr>

                    <td style="text-align: center;width: 20%">部门名称：</td>
                    <td style="min-width:170px">
                        <input id="departName" name="departName" class="mini-textbox" style="width:98%;"
                               enabled="false"/>
                    </td>
                    <td style="text-align: center;width: 20%">设计型号：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <%--<input id="designModel" name="designModel" class="mini-textbox" style="width:85%;"--%>
                               <%--enabled="true"/>--%>
                        <input id="designModel" style="width:85%;" class="mini-buttonedit" name="designModel"
                               textname="designModel"
                               allowInput="false" onbuttonclick="selectDesignModel()"/>


                        <span herf="#" style="color:#44cef6;cursor: pointer"
                              onclick="jumpSpectrumDetail()">点击查看</span>
                    </td>


                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">申请权限类别：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <input property="editor" class="mini-combobox"
                               style="width:98%;"
                               enabled="true"
                               id="aimType" name="aimType"
                               textField="key" valueField="value" emptyText="请选择..."
                               required="false" allowInput="false" showNullItem="true"
                               nullItemText="请选择..."
                               onvaluechanged="onAimTypeChange"
                               data="[{'key' : '研发状态','value' : '研发状态'}
                                       ,{'key' : '其他内容','value' : '其他内容'}
                                       ,{'key' : '销售状态','value' : '销售状态'}
                                       ,{'key' : '制造状态','value' : '制造状态'}
                                  ]"
                        />
                    </td>
                    <td style="text-align: center">产品主管：<span style="color:red">*</span></td>
                    <td style="min-width:170px">
                        <%--<td style="min-width:170px" colspan="3">--%>
                        <input id="productMangerIds" name="productMangerIds" textname="productManagerNames"
                               property="editor" class="mini-user rxc" plugins="mini-user"
                               enabled="false"
                               style="width:98%;height:34px;" allowinput="false" mainfield="no" single="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center">变更内容：<span style="color:red">*</span></td>
                    <td colspan="3">
						<textarea id="changeDesc" name="changeDesc" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:98%;height:250px;line-height:25px;"
                                  label="变更内容" datatype="varchar" length="200" vtype="length:200" minlen="0"
                                  allowinput="true"
                                  emptytext="请输入变更内容..." mwidth="80" wunit="%" mheight="150" hunit="px"></textarea>
                    </td>
                </tr>

                <tr>
                    <td style="text-align: center;width: 14%;height:10px">附件列表：</td>
                    <td colspan="3" height="60px">
                        <div id="fileToolBar" style="margin-top: 10px;margin-bottom: 2px">
                            <a id="addFile" class="mini-button" onclick="addXpbgsqFile('${applyId}')">添加附件</a>
                            <span style="color: red">注：添加附件前，请先进行草稿的保存</span>
                        </div>
                        <div id="fileListGrid" class="mini-datagrid"
                             allowResize="false"
                             idField="id"
                             url="${ctxPath}/pdm/core/xpbgsq/demandList.do?applyId=${applyId}"
                             autoload="true"
                             multiSelect="true" showPager="false" showColumnsMenu="false"
                             allowAlternating="true"
                             style="height:150px;"
                        >
                            <div property="columns">
                                <div type="indexcolumn" align="center" width="20">序号</div>
                                <div field="fileName" width="140" headerAlign="center" align="center">附件名称</div>
                                <div field="fileDesc" width="80" headerAlign="center" align="center">附件描述
                                </div>
                                <div field="fileSize" width="80" headerAlign="center" align="center">附件大小</div>
                                <div field="action" width="100" headerAlign='center' align="center"
                                     renderer="operationRenderer">操作
                                </div>
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
    var jsUseCtxPath = "${ctxPath}";
    var nodeVarsStr = '${nodeVars}';

    var xpbgsqForm = new mini.Form("#xpbgsqForm");
    var fileListGrid = mini.get("fileListGrid");
    var spectrumListGrid = mini.get("spectrumListGrid");
    var spectrumWindow = mini.get("spectrumWindow");
    var belongYjy = "${belongYjy}";
    var action = "${action}";
    var status = "${status}";
    var applyId = "${applyId}";
    var instId = "${instId}";
    var currentUserId = "${currentUserId}";
    var currentUserName = "${currentUserName}";
    var currentTime = "${currentTime}";
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>" + xzxgwjjxyxgs;


    function render(e) {
        if (e.value != null && e.value != "") {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            return html;
        }
    }

    function setApply() {
        var partsType = mini.get("partsType").getValue();
        if (!partsType) {
            mini.get("checker").setValue('');
            mini.get("checker").setText('');
            return;
        }
        $.ajax({
            url: jsUseCtxPath + '/pdm/core/xpbgsq/getUserInfoByPartsType.do?partsType=' + partsType,
            type: 'get',
            async: false,
            contentType: 'application/json',
            success: function (data) {
                mini.get("checker").setValue(data.resId);
                mini.get("checker").setText(data.resName);
            }
        });
    }

    var stageName = "";
    $(function () {
        var url = jsUseCtxPath + "/pdm/core/xpbgsq/getJson.do";
        $.post(
            url,
            {id: applyId},
            function (json) {
                xpbgsqForm.setData(json);
            });
        if (action == 'detail') {
            xpbgsqForm.setEnabled(false);
            $("#detailToolBar").show();
            $("#fileToolBar").hide();
            if (status != 'DRAFTED') {
                $("#processInfo").show();
            }
        } else if (action == 'task') {
            taskActionProcess();
        }
    });

    function getData() {
        var formData = _GetFormJsonMini("xpbgsqForm");

        if (formData.SUB_fileListGrid) {
            delete formData.SUB_fileListGrid;
        }
        if (formData.SUB_demandGrid) {
            delete formData.SUB_demandGrid;
        }

        return formData;

    }

    //保存草稿
    function saveDraft(e) {
        window.parent.saveDraft(e);
    }

    //发起流程
    function startProcess(e) {
        var formValid = validXpbgsq();
        if (!formValid.result) {
            mini.alert(formValid.message);
            return;
        }
        window.parent.startProcess(e);
    }

    //下一步审批
    function projectApprove() {
        //编制阶段的下一步需要校验表单必填字段
        if (stageName == 'start') {
            var formValid = validXpbgsq();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        } else {
            var formValid = validNext();
            if (!formValid.result) {
                mini.alert(formValid.message);
                return;
            }
        }

        //检查通过
        window.parent.approve();
    }

    function validNext() {

        return {"result": true};
    }


    function validXpbgsq() {

        var designModel = $.trim(mini.get("designModel").getValue());
        if (!designModel) {
            return {"result": false, "message": "请填写设计型号"};
        }
        var changeDesc = $.trim(mini.get("changeDesc").getValue());
        if (!changeDesc) {
            return {"result": false, "message": "请填写变更内容"};
        }
        var aimType = $.trim(mini.get("aimType").getValue());
        if (!aimType) {
            return {"result": false, "message": "请选择申请权限类别"};
        }
        if (aimType == "销售状态" || aimType == "制造状态") {
            var productMangerIds = $.trim(mini.get("productMangerIds").getValue());
            if (!productMangerIds) {
                return {"result": false, "message": "请选择产品主管"};
            }
        }

        // 在这要加部门判断
        // if (belongYjy == "false"&&currentUserId!="1") {
        if (belongYjy == "false") {
            if (aimType == "研发状态" || aimType == "其他内容") {
                return {"result": false, "message": "只有技术中心可以申请研发状态和其他内容变更！"};
            }
        }


        return {"result": true};
    }

    function processInfo() {
        var instId = $("#instId").val();
        var url = jsUseCtxPath + "/bpm/core/bpmInst/info.do?instId=" + instId;
        _OpenWindow({
            url: url,
            max: true,
            title: "流程图实例",
            width: 800,
            height: 600
        });
    }


    function taskActionProcess() {
        //获取上一环节的结果和处理人
        bpmPreTaskTipInForm();
        //获取环境变量
        if (!nodeVarsStr) {
            nodeVarsStr = "[]";
        }
        var nodeVars = $.parseJSON(nodeVarsStr);
        for (var i = 0; i < nodeVars.length; i++) {
            if (nodeVars[i].KEY_ == 'stageName') {
                stageName = nodeVars[i].DEF_VAL_;
            }
        }
        if (stageName != 'start') {
            xpbgsqForm.setEnabled(false);
            $("#fileToolBar").hide();
        }


    }


    function addXpbgsqFile(applyId) {

        var stageKey = "";
        if (!applyId) {
            mini.alert("请先保存草稿！");
            return;
        }


        mini.open({
            title: "文件上传",
            url: jsUseCtxPath + "/pdm/core/xpbgsq/openUploadWindow.do?applyId=" + applyId,
            width: 750,
            height: 450,
            showModal: false,
            allowResize: true,
            onload: function () {
                var iframe = this.getIFrameEl();
                var projectParams = {};
                projectParams.applyId = applyId;
                var data = {projectParams: projectParams};  //传递上传参数
                iframe.contentWindow.SetData(data);
            },
            ondestroy: function (action) {
                fileListGrid.load();
            }
        });
    }

    function operationRenderer(e) {
        var record = e.record;
        //预览、下载和删除
        if (!record.id) {
            return "";
        }
        var cellHtml = returnPreviewSpan(record.fileName, record.id, record.applyId, coverContent);
        var downloadUrl = '/pdm/core/xpbgsq/fileDownload.do';
        cellHtml += '&nbsp;&nbsp;&nbsp;<span title=' + "下载" + ' style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downLoadFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.applyId + '\',\'' + downloadUrl + '\')">' + "下载" + '</span>';

        if (record && (action == "edit" || stageName == "start")) {
            // if (record ) {
            var deleteUrl = "/pdm/core/xpbgsq/deleteFiles.do";
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title=' + "删除" + ' style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.applyId + '\',\'' + deleteUrl + '\')">' + "删除" + '</span>';
        }
        return cellHtml;
    }

    function returnPreviewSpan(fileName, fileId, formId, coverContent) {
        var s = '';
        if (fileName == "") {
            return s;
        }
        var fileType = getFileType(fileName);

        if (fileType == 'other') {
            s = '<span  title=' + "预览" + ' style="color: silver" >' + "预览" + '</span>';
        } else {
            var url = '/pdm/core/xpbgsq/preview.do?fileType=' + fileType;
            s = '<span  title=' + "预览" + ' style="color:#409EFF;cursor: pointer;" onclick="previewPdf6(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\',\'' + fileType + '\')">' + "预览" + '</span>';
        }
        return s;
    }

    function onAimTypeChange() {
        var aimType = mini.get("aimType").getValue();
        if (aimType == '销售状态' || aimType == "制造状态") {
            mini.get("productMangerIds").setEnabled(true);
        } else {
            mini.get("productMangerIds").setEnabled(false);
            mini.get("productMangerIds").setValue("");
            mini.get("productMangerIds").setText("");
        }
    }

    // 产品型谱跳转到详情
    function jumpSpectrumDetail() {
        // 这个要关联好之后才能开放
        var businessId = mini.get("designModel").getValue();
        if (businessId) {
            var url = jsUseCtxPath + "/world/core/productSpectrum/EditPage.do?id=" + businessId + '&action=detail';

            var winObj = window.open(url);
        }

    }

    /**
     * 关联设计型号弹窗
     */
    function selectDesignModel() {
        spectrumWindow.show();
        searchDesignModel();
    }

    /**
     * 关联设计型号查询
     */
    function searchDesignModel() {
        var queryParam = [];
        //其他筛选条件
        var designModel = $.trim(mini.get("searchDesignModel").getValue());
        if (designModel) {
            queryParam.push({name: "designModel", value: designModel});
        }
        // queryParam.push({name: "instStatus", value: "SUCCESS_END"});

        var data = {};
        data.filter = mini.encode(queryParam);
        data.pageIndex = spectrumListGrid.getPageIndex();
        data.pageSize = spectrumListGrid.getPageSize();
        data.sortField = spectrumListGrid.getSortField();
        data.sortOrder = spectrumListGrid.getSortOrder();
        //查询
        spectrumListGrid.load(data);
    }


    /**
     * 产品型号确定按钮
     */
    function okWindow() {
        var selectRow = spectrumListGrid.getSelected();
        if (selectRow) {
            mini.get("designModel").setValue(selectRow.id);

            mini.get("designModel").setText(selectRow.designModel);
        }
        hideWindow()
    }

    /**
     * 产品型号关闭按钮
     */
    function hideWindow() {
        spectrumWindow.hide();
        mini.get("searchDesignModel").setValue('');
        mini.get("searchDesignModel").setText('');
    }

</script>
</body>
</html>
