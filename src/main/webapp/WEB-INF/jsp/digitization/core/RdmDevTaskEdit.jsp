<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>RDM功能开发申请</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/digitization/rdmDevTaskEdit.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
</head>

<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="processInfo" class="mini-button" style="display: none" onclick="processInfo()">流程信息</a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin: 0 auto; width: 100%;">
        <form id="formDevTask" method="post">
            <input id="applyId" name="applyId" class="mini-hidden"/>
            <input id="instId" name="instId" class="mini-hidden"/>
            <input id="CREATE_BY_" name="CREATE_BY_" class="mini-hidden"/>
            <table class="table-detail grey"  cellspacing="1" cellpadding="0">
                <caption style="font-weight: bold;font-size: 20px !important;">
                    RDM功能开发申请单：<input id="applyNum" name="applyNum" class="mini-textbox" readonly/>
                </caption>
                <tr>
                    <td style="width: 14%">申请人：</td>
                    <td style="width: 36%;">
                        <input id="applyUserName" name="applyUserName" class="mini-textbox" style="width:98%;" readonly/>
                    </td>
                    <td style="width: 14%">申请人部门：</td>
                    <td style="width: 36%;">
                        <input id="applyUserDeptName" name="applyUserDeptName" class="mini-textbox" style="width:98%;" readonly/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%">功能开发类型：<span style="color:red">*</span></td>
                    <td style="width: 36%;">
                        <input id="devType" name="devType" class="mini-combobox" style="width:98%;"
                               textField="key" valueField="value" emptyText="请选择..."
                               required="false" allowInput="false" showNullItem="false"
                               data="[ {'key' : '新功能开发','value' : 'add'},{'key' : '优化功能开发','value' : 'modify'}]"
                        />
                    </td>
                    <td style="width: 14%">申请说明（不超过200字）：<span style="color:red">*</span></td>
                    <td style="width: 36%;">
                        <input id="applyName" name="applyName" class="mini-textarea" emptytext="如“新增xxx版块”、“xx版块下新增xx功能”、“xx版块下xx功能优化”"
                               style="width:98%;overflow: auto;height: 60px" />
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%">需求详细描述：<span style="color:red">*</span><br>（不超过2000字）</td>
                    <td colspan="3">
                        <input id="applyReason" name="applyReason" class="mini-textarea"
                                  style="width:99%;height:150px;" emptytext="请输入需求的详细描述..." />
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%">功能价值描述：<span style="color:red">*</span><br>（不超过2000字）</td>
                    <td colspan="3">
                        <textarea id="applyValue" name="applyValue" class="mini-textarea rxc" plugins="mini-textarea"
                                  style="width:99%;height:120px;line-height:25px;" label="立项理由" datatype="varchar" allowinput="true"  emptytext="请输入通过功能开发达到的目的或者价值点..."
                                  mwidth="80" wunit="%" mheight="200" hunit="px"></textarea>
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%">业务部门负责人意见：<span style="color:red">*</span></td>
                    <td colspan="3">
                        <textarea id="creatorRespOpinion" name="creatorRespOpinion" class="mini-textarea rxc" plugins="mini-textarea"
                                  style="width:99%;height:80px;line-height:25px;" label="立项理由" datatype="varchar" allowinput="true"
                                  emptytext="请输入业务部门负责人意见..." mwidth="80" wunit="%" mheight="200" hunit="px"></textarea>
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%">信息部技术分析意见：<span style="color:red">*</span></td>
                    <td style="width: 36%;" >
                        <div id="xxbTechOpinionDiv" style="width: 100%;height:100%;">
                        <input id="xxbTechOpinion" name="xxbTechOpinion" class="mini-radiobuttonlist" style="width:98%;"
                               repeatItems="3" repeatLayout="table" repeatDirection="horizontal"
                               textfield="opinionText" valuefield="opinionValue"
                               data="[ {'opinionText' : '采纳','opinionValue' : 'yes'},{'opinionText' : '部分采纳','opinionValue' : 'partYes'},{'opinionText' : '不采纳','opinionValue' : 'no'}]"/>
                        </div>
                    </td>
                    <td style="width: 14%">信息部技术分析理由：<span style="color:red">*</span></td>
                    <td style="width: 36%;">
						<textarea id="xxbTechOpinionReason" name="xxbTechOpinionReason" class="mini-textarea rxc" plugins="mini-textarea"
                                  style="width:99%;height:80px;line-height:25px;" allowinput="true"
                                  emptytext="请输入技术可行性分析理由..." ></textarea>
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%">信息部负责人意见：</td>
                    <td colspan="3" >
						<textarea id="xxbRespOpinion" name="xxbRespOpinion" class="mini-textarea rxc" plugins="mini-textarea" style="width:98%;height:80px;line-height:25px;" label="" datatype="varchar" length="1000" vtype="length:1000" minlen="0" allowinput="true"
                                  emptytext="请输入数字化部负责人意见..." mwidth="80" wunit="%" mheight="200" hunit="px"></textarea>
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%">计划开始时间：<span style="color:red">*</span></td>
                    <td style="width: 36%;">
                        <input id="planStartTime" name="planStartTime" class="mini-datepicker" style="width:98%;" allowInput="false"/>
                    </td>
                    <td style="width: 14%">计划结束时间：<span style="color:red">*</span></td>
                    <td style="width: 36%;">
                        <input id="planEndTime" name="planEndTime" class="mini-datepicker" style="width:98%;" allowInput="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%;">预估工作量（人天）：<span style="color:red">*</span></td>
                    <td >
                        <input id="gzlEvaluate" name="gzlEvaluate" class="mini-textbox" style="width:98%;"/>
                    </td>
                    <td style="width: 14%">是否涉及流程变更：<span style="color:red">*</span></td>
                    <td>
                        <input id="flowChange" name="flowChange" class="mini-combobox" style="width:98%;"
                               textField="value" valueField="key" emptyText="请选择..."
                               required="false" allowInput="false" showNullItem="false"
                               enabled="false"
                               data="[{'key' : 'yes','value' : '是'},{'key' : 'no','value' : '否'}]"/>
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%">开发责任人：<span style="color:red">*</span></td>
                    <td>
                        <input id="devRespUserId" name="devRespUserId" showclose="false" textname="devRespUserName" class="mini-user rxc" plugins="mini-user"
                               style="width:98%;height:34px;" allowinput="false" length="50" maxlength="50"  mainfield="no"
                               single="true"/>
                    </td>
                    <td style="width: 14%">完成情况描述：<span style="color:red">*</span><br>（不超过1000字，说明菜单路径及<br>相关权限配置）</td>
                    <td style="width: 36%;" >
						<textarea id="finishDesc" name="finishDesc" class="mini-textarea rxc" plugins="mini-textarea"
                                  style="width:99%;height:80px;line-height:25px;" label="" datatype="varchar"
                                  emptytext="请输入完成情况描述..." allowinput="true"
                                  mwidth="80" wunit="%" mheight="90" hunit="px"></textarea>
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%;height: 200px">附件列表：<span style="color:red">*</span><br>（需求方案相关的文档）</td>
                    <td colspan="3">
                        <div style="margin-top: 10px;margin-bottom: 2px">
                            <a id="addFile" class="mini-button"  onclick="addDevTaskFile()">添加附件</a>
                            <span style="color: red">注：添加附件前，请先保存草稿</span>
                        </div>
                        <div id="fileListGrid" class="mini-datagrid" style="width: 100%; height: 85%" allowResize="false"
                             idField="id" url="${ctxPath}/devTask/core/getDevFileList.do?applyId=${applyId}" autoload="true"
                             showPager="false" showColumnsMenu="false" allowAlternating="true" >
                            <div property="columns">
                                <div field="fileName"  width="140" headerAlign="center" align="center" >附件名称</div>
                                <div field="fileSize"  width="80" headerAlign="center" align="center" >附件大小</div>
                                <div field="creator"  width="70" headerAlign="center" align="center" >创建人</div>
                                <div field="CREATE_TIME_" width="90" headerAlign="center" align="center" dateFormat="yyyy-MM-dd HH:mm:ss">创建时间</div>
                                <div field="action" width="100" headerAlign='center' align="center" renderer="operationRenderer">操作</div>
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
    var action="${action}";
    var status="${status}";
    var jsUseCtxPath="${ctxPath}";
    var fileListGrid=mini.get("fileListGrid");
    var formDevTask = new mini.Form("#formDevTask");
    var applyId="${applyId}";
    var nodeVarsStr='${nodeVars}';
    var currentUserName="${currentUserName}";
    var currentTime="${currentTime}";
    var coverContent=currentUserName+"<br/>"+currentTime+"<br/>徐州徐工挖掘机械有限公司";

    function operationRenderer(e) {
        var downloadUrl='/devTask/core/devPdfPreview.do';
        var record = e.record;
        var cellHtml = '';
        cellHtml=returnDevTaskPreviewSpan(record.fileName,record.id,record.applyId,coverContent);
        cellHtml+='&nbsp;&nbsp;&nbsp;<span  title="下载" style="color:#409EFF;cursor: pointer;" onclick="downLoadFile(\'' +record.fileName+'\',\''+record.id+'\',\''+record.applyId+'\',\''+downloadUrl+ '\')">下载</span>';
        //增加删除按钮
        if(action=='edit' || (action=='task' && (nodeName=='bianzhi' || nodeName=='creatorRespSH'))) {
            var deleteUrl="/devTask/core/delDevTaskFile.do"
            cellHtml+='&nbsp;&nbsp;&nbsp;<span title="删除" style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile(\''+record.fileName+'\',\''+record.id+'\',\''+record.applyId+'\',\''+deleteUrl+'\')">删除</span>';
        }
        return cellHtml;
    }

</script>
</body>
</html>
