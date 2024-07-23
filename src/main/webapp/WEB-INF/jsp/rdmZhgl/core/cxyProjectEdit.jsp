<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>产学研项目信息采集</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmZhgl/cxyProjectEdit.js?version=${static_res_version}" type="text/javascript"></script>
</head>

<body>
<div id="toolBar" class="topToolBar">
    <div>
        <a id="saveCxyProjectDraft" class="mini-button" style="display: none" onclick="saveCxyProjectDraft()">保存草稿</a>
        <a id="commitCxyProject" class="mini-button" style="display: none" onclick="commitCxyProject()">提交</a>
        <a id="feedbackCxyProject" class="mini-button" style="display: none" onclick="feedbackCxyProject()">保存</a>
        <a id="closeWindow" class="mini-button btn-red" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin:0 auto; width: 100%;height:98%;padding: 0">
        <form id="cxyProjectForm" method="post" style="height: 95%;width: 100%">
            <input class="mini-hidden" id="id" name="id"/>
            <input class="mini-hidden" id="isDelay" name="isDelay"/>
            <input class="mini-hidden" id="isSubmit" name="isSubmit"/>
            <input class="mini-hidden" id="responsibleName" name="responsibleName"/>
            <input class="mini-hidden" id="responsibleDepName" name="responsibleDepName"/>
            <table class="table-detail grey" cellspacing="1" cellpadding="0" style="height: 100%;width: 100%">
                <caption style="font-weight: bold;font-size: 20px !important;">产学研项目信息采集</caption>
                <tr>
                    <td style="text-align: center;width: 20%">项目名称：</td>
                    <td colspan="3">
                        <input id="projectDesc" name="projectDesc" class="mini-textbox" style="width:98%;"/>
                    </td>
                <tr>
                <tr>
                    <td style="text-align: center;width: 20%">项目负责人：</td>
                    <td style="min-width:170px">
                        <input id="responsibleUserId" name="responsibleUserId" textname="responsibleName"
                               class="mini-user rxc" plugins="mini-user" style="width:98%;height:34px;" allowinput="false"
                               label="项目负责人" length="50" mainfield="no" single="false"/>
                    </td>
                    <td style="text-align: center;width: 20%">部门：</td>
                    <td style="min-width:170px">
                        <input id="responsibleUserDepId" name="responsibleUserDepId" class="mini-dep rxc" plugins="mini-dep"
                               data-options="{'single':'true','config':{'type':'','grouplevel':'','groupid':'','groupidText':''}}"
                               style="width:98%;height:34px" allowinput="false" label="部门" textname="responsibleDepName" length="500"
                               maxlength="500" minlen="0" single="false" required="false" initlogindep="false" showclose="false"
                               mwidth="160" wunit="px" mheight="34" hunit="px" enabled="false"/>
                    </td>
                <tr>
                <tr>
                    <td style="text-align: center;width: 20%">承担单位：</td>
                    <td style="min-width:170px">
                        <input id="undertaker" name="undertaker" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 20%">合作单位：</td>
                    <td style="min-width:170px">
                        <input id="collaborator" name="collaborator" class="mini-textbox" style="width:98%;"/>
                    </td>
                <tr>
                <tr>
                    <td style="text-align: center;width: 20%">开始时间：</td>
                    <td style="min-width:170px">
                        <input id="beginTime" name="beginTime" class="mini-datepicker" format="yyyy-MM-dd"
                               showTime="false" showOkButton="true" showClearButton="false" style="width:98%;"/>
                    </td>
                    <td style="text-align: center;width: 20%">结束时间：</td>
                    <td style="min-width:170px">
                        <input id="endTime" name="endTime" class="mini-datepicker" format="yyyy-MM-dd"
                               showTime="false" showOkButton="true" showClearButton="false" style="width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">合同金额(万元)：</td>
                    <td style="min-width:170px">
                        <input id="contractAmount" name="contractAmount" class="mini-spinner"
                               value="0" minValue="-1000000" maxValue="1000000" decimalPlaces="2"/>
                    </td>
                    <td style="text-align: center;width: 20%">已支付合同金额(万元)：</td>
                    <td style="min-width:170px">
                        <input id="paidContractAmount" name="paidContractAmount" class="mini-spinner"
                               value="0" minValue="-1000000" maxValue="1000000" decimalPlaces="2"/>
                    </td>
                <tr>
                <tr>
                    <td style="text-align: center;width: 20%">项目所属类型：</td>
                    <td style="min-width:170px">
                        <input id="projectType" name="projectType"
                               class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=cxyProjectType"
                               valueField="key" textField="value"/>
                    </td>
                    <td style="text-align: center;width: 20%">技术研究方向：</td>
                    <td style="min-width:170px">
                        <input id="researchDirection" name="researchDirection"
                               class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=cxyResearchDirection"
                               valueField="key" textField="value"/>
                    </td>
                <tr>
                <tr>
                    <td style="text-align: center;width: 20%">项目执行情况：</td>
                    <td style="min-width:170px">
                        <input id="implementation" name="implementation"
                               class="mini-combobox" style="width:98%" showNullItem="true"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=cxyImplementation"
                               valueField="key" textField="value"/>
                    </td>
                    <td style="text-align: center;width: 20%">项目性质：</td>
                    <td style="min-width:170px">
                        <input id="projectProperties" name="projectProperties"
                               class="mini-combobox" style="width:98%"
                               url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=cxyProjectProperties"
                               valueField="key" textField="value"/>
                    </td>
                <tr>
                <tr>
                    <td style="text-align: center;width: 20%">项目简介:</td>
                    <td colspan="3">
						<textarea id="remark" name="remark" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:99.1%;height:150px;line-height:25px;"
                                  datatype="varchar" allowinput="true" mwidth="80" wunit="%" mheight="200" hunit="px">
                        </textarea>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">合同上要求指标:</td>
                    <td colspan="3">
						<textarea id="contractIndicators" name="contractIndicators" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:99.1%;height:150px;line-height:25px;"
                                  datatype="varchar" allowinput="true" mwidth="80" wunit="%" mheight="200" hunit="px">
                        </textarea>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%">目前已完成指标:</td>
                    <td colspan="3">
						<textarea id="completedIndicators" name="completedIndicators" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:99.1%;height:150px;line-height:25px;"
                                  datatype="varchar" allowinput="true" mwidth="80" wunit="%" mheight="200" hunit="px">
                        </textarea>
                    </td>
                </tr>
                <td style="text-align: center;width: 20%">项目完成率(%)</td>
                <td colspan="3">
                    <input id="completionRate" name="completionRate" class="mini-spinner"
                           value="0" minValue="0" maxValue="100"/>
                </td>
                <tr>
                    <td style="text-align: center;width: 20%">延期原因:</td>
                    <td colspan="3">
						<textarea id="delayReason" name="delayReason" class="mini-textarea rxc"
                                  plugins="mini-textarea" style="width:99.1%;height:150px;line-height:25px;"
                                  datatype="varchar" allowinput="true" mwidth="80" wunit="%" mheight="200" hunit="px">
                        </textarea>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;height: 300px">文件列表：</td>
                    <td colspan="3">
                        <div class="mini-toolbar" id="fileButtons" style="display: none">
                            <a id="uploadFile" class="mini-button" style="margin-bottom: 5px;margin-top: 5px" onclick="uploadCxyProjectFile">上传文件</a>
                        </div>
                        <div id="fileListGrid" class="mini-datagrid" style="width: 100%; height: 85%" allowResize="false"
                             idField="id" url="${ctxPath}/zhgl/core/cxypojfile/dataList.do?cxyProjectId=${cxyProjectId}"
                             multiSelect="false" showPager="true" showColumnsMenu="false" sizeList="[10,20,50,100]" pageSize="10"
                             allowAlternating="true" pagerButtons="#pagerButtons">
                            <div property="columns">
                                <div type="indexcolumn" headerAlign="center" align="center" width="20">序号</div>
                                <div field="fileName" align="center" headerAlign="center" width="150">文件名</div>
                                <div field="fileSize" align="center" headerAlign="center" width="60">文件大小</div>
                                <div field="fileDesc" align="center" headerAlign="center" width="100">备注说明</div>
                                <div field="action" width="80" headerAlign='center' align="center" renderer="operationRenderer">操作</div>
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
    var jsUseCtxPath = "${ctxPath}";
    var cxyProjectForm = new mini.Form("#cxyProjectForm");
    var fileListGrid = mini.get("fileListGrid");
    var currentUserName = "${currentUserName}";
    var currentTime = "${currentTime}";
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>徐州徐工挖掘机械有限公司";

    var cxyProjectId = "${cxyProjectId}";
    var action = "${action}";
    var currentUserMainGroupId = "${currentUserMainGroupId}";
    var currentUserMainGroupName = "${currentUserMainGroupName}";
    var currentUserId = "${currentUserId}";

    function operationRenderer(e) {
        var record = e.record;
        var cellHtml = '';
        cellHtml = returncxyProjectPreviewSpan(record.fileName, record.id, record.cxyProjectId);
        cellHtml += '&nbsp;&nbsp;&nbsp;<span title="下载" style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downLoadCxyProjectFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.cxyProjectId + '\')">下载</span>';
        //增加删除按钮
        if (record.CREATE_BY_ == currentUserId && action != 'detail') {
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteCxyProjectFile(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
        }
        return cellHtml;
    }

</script>
</body>
</html>
