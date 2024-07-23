
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=Edge">
    <title>新增</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/standardManager/nationStandardEdit.js?version=${static_res_version}"type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
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
<div id="detailToolBar" class="topToolBar" >
    <div>
        <a id="release" name="release" class="mini-button"  onclick="release()"><spring:message code="page.nationStandardEdit.name" /></a>
        <a id="closeWindow" class="mini-button" onclick="CloseWindow()"><spring:message code="page.nationStandardEdit.name1" /></a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin: 0 auto; width: 80%;">
        <form id="formNationStandard" method="post">
            <input id="standardId" name="standardId" class="mini-hidden"/>
            <table class="table-detail" cellspacing="1" cellpadding="0">
                <tr style="display: none">
                    <td style="width: 10%;font-size:14pt"><spring:message code="page.nationStandardEdit.name2" />：</td>
                    <td style="width: 33%;min-width:170px;font-size:14pt">
                        <input id="companyName" name="companyName"  class="mini-textbox"  style="width:98%;" />
                    </td>
                </tr>
                <tr>
                    <td style="font-size:14pt;width: 20%;text-align: left"><spring:message code="page.nationStandardEdit.name3" />：</td>
                    <td>
                        <input id="standardName" name="standardName" class="mini-textbox" style="font-size:14pt;width:98%;"/>
                    </td>
                    <td style="font-size:14pt;width: 20%;text-align: left"><spring:message code="page.nationStandardEdit.name4" />：</td>
                    <td>
                        <input id="spNumber" name="spNumber" class="mini-textbox" style="font-size:14pt;width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="font-size:14pt;width: 20%;text-align: left"><spring:message code="page.nationStandardEdit.name5" />：</td>
                    <td>
                        <input id="standardLv" name="standardLv" class="mini-combobox" style="font-size:14pt;width:98%;" textField="text" valueField="id" emptyText="<spring:message code="page.nationStandardEdit.name6" />..."
                         data="[{id:'国际',text:'国际'},{id:'国家',text:'国家'},{id:'集团',text:'集团'},
                         {id:'地方',text:'地方'},{id:'行业',text:'行业'},{id:'团体',text:'团体'}]"
                               required="true" allowInput="true" showNullItem="true" nullItemText="<spring:message code="page.nationStandardEdit.name6" />..."/>
                    </td>
                    <td style="font-size:14pt;width: 20%;text-align: left"><spring:message code="page.nationStandardEdit.name7" />：</td>
                    <td>
                        <input id="joinDegree" name="joinDegree" class="mini-combobox" style="font-size:14pt;width:98%;" textField="text" valueField="id" emptyText="<spring:message code="page.nationStandardEdit.name6" />..."
                               data="[{id:'参与',text:'参与'},{id:'主持',text:'主持'}]"
                               required="true" allowInput="true" showNullItem="true" nullItemText="<spring:message code="page.nationStandardEdit.name6" />..."/>
                    </td>
                </tr>
                <tr>
                    <td style="font-size:14pt;width: 20%;text-align: left"><spring:message code="page.nationStandardEdit.name8" />：</td>
                    <td>
                        <input id="enactoralter" name="enactoralter" class="mini-combobox" style="font-size:14pt;width:98%;" textField="text" valueField="id" emptyText="<spring:message code="page.nationStandardEdit.name6" />..."
                               data="[{id:'制定',text:'制定'},{id:'修订',text:'修订'}]"
                               required="true" allowInput="true" showNullItem="true" nullItemText="<spring:message code="page.nationStandardEdit.name6" />..."/>
                    </td>
                    <td style="font-size:14pt;width: 20%;text-align: left"><spring:message code="page.nationStandardEdit.name9" />：</td>
                    <td>
                        <input id="standaStauts" name="standaStauts" class="mini-combobox" style="font-size:14pt;width:98%;" textField="text" valueField="id" emptyText="<spring:message code="page.nationStandardEdit.name6" />..."
                               data="[{id:'申请立项',text:'申请立项'},{id:'已立项 ',text:'已立项'},
                               {id:'工作组讨论稿',text:'工作组讨论稿'},{id:'征求意见稿',text:'征求意见稿'},
                               {id:'送审稿',text:'送审稿'},{id:'报批稿',text:'报批稿'},
                               {id:'已报批',text:'已报批'},{id:'已发布',text:'已发布'}]"
                               required="true" allowInput="true" showNullItem="true" nullItemText="<spring:message code="page.nationStandardEdit.name6" />..."/>
                    </td>
                </tr>
                <tr>
                    <td style="font-size:14pt;width: 20%;text-align: left"><spring:message code="page.nationStandardEdit.name10" />：</td>
                    <td>
                        <input class="mini-monthpicker" id="releaseTime" name="releaseTime"   showTime="true" style="font-size:14pt;width: 98%"/>
                    </td>
                    <td style="font-size:14pt;width: 20%;text-align: left"><spring:message code="page.nationStandardEdit.name19" />：</td>
                    <td>
                        <input id="belongGroup" name="belongGroup" class="mini-textbox" style="font-size:14pt;width:98%;"/>
                    </td>
                </tr>
                <tr>
                    <td style="font-size:14pt;text-align: left"><spring:message code="page.nationStandardEdit.name11" /></td>
                    <td colspan="4">
                    <textarea id="note" name="note" class="mini-textarea rxc" plugins="mini-textarea"
                              style="font-size:14pt;width:100%;height:100px;line-height:25px;" label="<spring:message code="page.nationStandardEdit.name20" />" datatype="varchar"
                              length="10" vtype="length:10" minlen="0" allowinput="true" emptytext="<spring:message code="page.nationStandardEdit.name21" />..."
                              mwidth="8" wunit="%" mheight="20" hunit="px"></textarea>
                    </td>
                </tr>
                <tr>
                    <td style="width: 14%;height: 300px"><spring:message code="page.nationStandardEdit.name12" />：</td>
                    <td colspan="3">
                        <div style="margin-top: 25px;margin-bottom: 2px">
                            <a id="addFile" class="mini-button" onclick="fileupload()"><spring:message code="page.nationStandardEdit.name13" /></a>
                        </div>
                        <div id="fileListGrid" class="mini-datagrid" style="width: 100%; height: 100%"
                             allowResize="false"
                             idField="id"  autoload="true"
                             url="${ctxPath}/standardManager/core/NationStandardChangeController/getFileList.do?standardId=${standardId}"
                             multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
                            <div property="columns">
                                <div type="indexcolumn" align="center" width="20"><spring:message code="page.nationStandardEdit.name14" /></div>
                                <div field="fileName" width="140" headerAlign="center" align="center"><spring:message code="page.nationStandardEdit.name15" /></div>
                                <div field="fileDeliveryId" width="140" headerAlign="center" align="center" renderer="type"><spring:message code="page.nationStandardEdit.name16" /></div>
                                <div field="fileSize" width="80" headerAlign="center" align="center"><spring:message code="page.nationStandardEdit.name17" /></div>
                                <div field="action" width="100" headerAlign='center' align="center" renderer="operationRenderer"><spring:message code="page.nationStandardEdit.name18" />
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
    var action="${action}";
    var jsUseCtxPath = "${ctxPath}";
    var standardId = "${standardId}";
    var formNationStandard = new mini.Form("#formNationStandard");
    var fileListGrid=mini.get("fileListGrid");
    var currentUserName="${currentUserName}";
    var currentUserId = "${currentUserId}";
    var currentTime="${currentTime}";
    var coverContent=currentUserName+"<br/>"+currentTime+"<br/>" + xzxgwjjxyxgs;
    function operationRenderer(e) {
        var record = e.record;
        var cellHtml = '';
        cellHtml=returnstandardChangePreviewSpan(record.fileName,record.fileId,record.standardId,coverContent);
        cellHtml+='&nbsp;&nbsp;&nbsp;<span title=' + nationStandardEdit_name + ' style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downLoadstandardChangeFile(\''+record.fileName+'\',\''+record.fileId+'\',\''+record.standardId+'\')">' + nationStandardEdit_name + '</span>';
        //增加删除按钮
        if(action=='edit') {
            var deleteUrl="/standardManager/core/NationStandardChangeController/delstandardChangeFile.do"
            cellHtml+='&nbsp;&nbsp;&nbsp;<span title=' + nationStandardEdit_name1 + ' style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile(\''+record.fileName+'\',\''+record.fileId+'\',\''+record.standardId+'\',\''+deleteUrl+'\')">' + nationStandardEdit_name1 + '</span>';
        }
        return cellHtml;
    }
    function type(e) {
        var record = e.record;
        var type = record.fileDeliveryId;
        var result = '';
        if(type=="GBJH"){
            result = '<span>国标计划下达文件</span>';
        }else if(type=="JSFW"){
            result = '<span>技术服务合同</span>';
        }else if(type=="BZCA"){
            result = '<span>标准草案</span>';
        }else if(type=="BZZQ"){
            result = '<span>标准征求意见稿</span>';
        }else if(type=="YJHZ"){
            result = '<span>意见汇总及处理</span>';
        }else if(type=="BZHS"){
            result = '<span>标准会审稿</span>';
        }else if(type=="BZBP"){
            result = '<span>标准报批稿</span>';
        }else if(type=="FBG"){
            result = '<span>发布稿</span>';
        }
        return result;
    }

</script>
</body>
</html>