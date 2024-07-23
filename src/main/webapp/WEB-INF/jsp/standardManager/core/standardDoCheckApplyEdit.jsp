<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html >
<html>
<head>
    <title>标准执行性自查表单</title>
    <%@include file="/commons/edit.jsp"%>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css" />
    <script src="${ctxPath}/scripts/standardManager/standardDoCheckApplyEdit.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div id="detailToolBar" class="topToolBar" style="display: none">
    <div>
        <a id="processInfo" class="mini-button" style="display: none" onclick="processInfo()"><spring:message code="page.standardDoCheckApplyEdit.name" /></a>
        <a id="closeWindow1" class="mini-button" onclick="CloseWindow()"><spring:message code="page.standardDoCheckApplyEdit.name1" /></a>
    </div>
</div>

<div class="mini-fit">
    <div class="form-container" style="margin:0 auto; width: 100%;height:98%;padding: 0">
        <form id="doCheckForm" method="post">
            <input class="mini-hidden" id="id" name="id" />
            <input class="mini-hidden" id="zcStatus" name="zcStatus" />
            <input id="instId" name="instId" class="mini-hidden"/>
            <p style="font-size: 16px;font-weight: bold;"><spring:message code="page.standardDoCheckApplyEdit.name2" /></p>
            <hr>
            <table class="table-detail grey"  cellspacing="1" cellpadding="0">
                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.standardDoCheckApplyEdit.name3" />：</td>
                    <td style="min-width:170px">
                        <input id="standardId"  name="standardId" textname="standardNumber" class="mini-buttonedit" style="width:98%;" showClose="true" allowInput="false"
                               oncloseclick="standardNumberCloseClick()"  onbuttonclick="standardNumberClick()"/>
                    </td>
                    <td style="text-align: center;width: 20%"><spring:message code="page.standardDoCheckApplyEdit.name4" />：</td>
                    <td style="min-width:170px">
                        <input id="standardName"  name="standardName"  class="mini-textbox" style="width:98%;" enabled="false"/>
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.standardDoCheckApplyEdit.name5" />：</td>
                    <td >
                        <input id="firstWriterId" name="firstWriterId" textname="firstWriterName" class="mini-user rxc"
                               plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="" length="50" maxlength="50"
                               mainfield="no"  single="true" />
                    </td>
                    <td style="text-align: center;width: 20%"><spring:message code="page.standardDoCheckApplyEdit.name6" />：</td>
                    <td >
                        <input id="djrUserId" name="djrUserId" textname="djrUserName" class="mini-user rxc"
                               plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="" length="50" maxlength="50"
                               mainfield="no"  single="true" />
                    </td>
                </tr>
                <tr>
                    <td style="text-align: center;width: 20%"><spring:message code="page.standardDoCheckApplyEdit.name7" />：</td>
                    <td >
                        <input id="szrUserId" name="szrUserId" textname="szrUserName" class="mini-user rxc"
                               plugins="mini-user" style="width:98%;height:34px;" allowinput="false" label="" length="50" maxlength="50"
                               mainfield="no"  single="true" />
                    </td>
                    <td style="text-align: center;width: 20%"><spring:message code="page.standardDoCheckApplyEdit.name8" />：<span style="color: red">(<spring:message code="page.standardDoCheckApplyEdit.name9" />)</span></td>
                    <td style="background: #f0f0f0">
                        <input id="checkResult" name="checkResult" class="mini-checkboxlist" multiSelect="single" style="width:98%"
                               textField="value" valueField="key" emptyText="<spring:message code="page.standardDoCheckApplyEdit.name10" />..." enabled="false"
                               data="[{key:'完全按标准执行',value:'完全按标准执行'},{key:'未完全按标准执行',value:'未完全按标准执行'}]"/>
                    </td>
                </tr>
            </table>

            <p style="font-size: 16px;font-weight: bold;margin-top: 30px"><spring:message code="page.standardDoCheckApplyEdit.name11" /></p>
            <hr>
            <div class="mini-toolbar" style="margin-bottom: 5px" id="checkListToolBar">
                <a class="mini-button" id="addCheckBtn"  plain="true" onclick="addCheck"><spring:message code="page.standardDoCheckApplyEdit.name12" /></a>
                <a class="mini-button btn-red" id="delCheckBtn" plain="true" onclick="delCheck"><spring:message code="page.standardDoCheckApplyEdit.name13" /></a>
            </div>
            <div id="checkGrid" class="mini-datagrid"  allowResize="false" style="height:630px" autoload="false"
                 idField="id" allowCellEdit="true" allowCellSelect="true" allowSortColumn="false" allowCellWrap="true"
                 multiSelect="true" showColumnsMenu="false" allowAlternating="true" showPager="false"
            >
                <div property="columns">
                    <div type="checkcolumn" width="40"></div>
                    <div type="indexcolumn" headerAlign="center" width="40"><spring:message code="page.standardDoCheckApplyEdit.name14" /></div>
                    <div field="fileType" width="100" headerAlign="center" align="center" allowSort="true"><spring:message code="page.standardDoCheckApplyEdit.name15" />
                        <input property="editor" class="mini-combobox" style="width:90%;"
                               textField="text" valueField="value" emptyText="<spring:message code="page.standardDoCheckApplyEdit.name10" />..."
                               allowInput="false" showNullItem="false" nullItemText="<spring:message code="page.standardDoCheckApplyEdit.name10" />..."
                               data="[{'text':'设计过程文件','value':'设计过程文件'},{'text':'技术协议','value':'技术协议'},
                               {'text':'三维模型','value':'三维模型'},{'text':'工程图','value':'工程图'},
                               {'text':'测试','value':'测试'},{'text':'工艺文件','value':'工艺文件'},
                               {'text':'生产现场','value':'生产现场'},{'text':'物资采购','value':'物资采购'},
                               {'text':'质量检验','value':'质量检验'},{'text':'其它','value':'其它'}
                               ]"
                        />
                    </div>
                    <div field="fileName" width="200" headerAlign="center" align="center" allowSort="true"><spring:message code="page.standardDoCheckApplyEdit.name16" />
                        <input property="editor" class="mini-textarea"/>
                    </div>
                    <div field="filePath" width="190" headerAlign="center" align="center"  allowSort="true"><spring:message code="page.standardDoCheckApplyEdit.name17" />
                        <input property="editor" class="mini-textarea"/>
                    </div>
                    <div field="useDesc" width="290" headerAlign="center" align="left"  allowSort="true" renderer="renderTextAreaWrapAutoHeight"><spring:message code="page.standardDoCheckApplyEdit.name18" />
                        <input property="editor" class="mini-textarea"/>
                    </div>
                    <div field="judge" width="80" headerAlign="center" align="center"  allowSort="true"><spring:message code="page.standardDoCheckApplyEdit.name19" />
                        <input property="editor" class="mini-combobox" style="width:90%;" textField="text" valueField="value" emptyText="<spring:message code="page.standardDoCheckApplyEdit.name10" />..."
                               allowInput="false" showNullItem="false" nullItemText="<spring:message code="page.standardDoCheckApplyEdit.name10" />..."
                            data="[{'text':'是','value':'是'},{'text':'否','value':'否'}]"
                        />
                    </div>
                    <div field="respUserId" displayField="respUserName" width="110" headerAlign="center" align="center"  allowSort="true"><spring:message code="page.standardDoCheckApplyEdit.name20" /><br><spring:message code="page.standardDoCheckApplyEdit.name21" />
                        <input property="editor" class="mini-user rxc" plugins="mini-user"  style="width:90%;height:34px;" allowinput="false"
                               length="50" maxlength="50"  mainfield="no"  single="true" />
                    </div>
                    <div field="deptName" width="110" headerAlign="center" align="center"  allowSort="true"><spring:message code="page.standardDoCheckApplyEdit.name22" />
                        <input property="editor" class="mini-textbox" readonly/>
                    </div>
                    <div field="detailTypes" width="100" headerAlign="center" align="center" allowSort="true"><spring:message code="page.standardDoCheckApplyEdit.name23" />
                        <input property="editor" class="mini-combobox" style="width:90%;" multiSelect="true"
                               textField="text" valueField="value" emptyText="<spring:message code="page.standardDoCheckApplyEdit.name10" />..."
                               allowInput="false" showNullItem="false" nullItemText="<spring:message code="page.standardDoCheckApplyEdit.name10" />..."
                               data="[{'text':'标准问题','value':'标准问题'},{'text':'执行问题','value':'执行问题'}
                               ]"
                        />
                    </div>
                    <div field="modifyMethod" width="240" headerAlign="center" align="left"  allowSort="true" renderer="renderTextAreaWrapAutoHeight"><spring:message code="page.standardDoCheckApplyEdit.name24" />
                        <input property="editor" class="mini-textarea"/>
                    </div>
                    <div field="planFinishTime" width="95" headerAlign="center" align="center" dateFormat="yyyy-MM-dd"
                         allowSort="true" allowInput="false"><spring:message code="page.standardDoCheckApplyEdit.name25" />
                        <input property="editor" class="mini-datepicker" style="width:100%"/>
                    </div>
                    <div field="closeRespUserId" displayField="closeRespUserName" width="110" headerAlign="center" align="center"
                         allowSort="true" allowInput="false"><spring:message code="page.standardDoCheckApplyEdit.name26" />
                        <input property="editor" class="mini-user rxc" plugins="mini-user"  style="width:90%;height:34px;" allowinput="false"
                               length="50" maxlength="50"  mainfield="no"  single="true" />
                    </div>
                    <div field="confirmPlan" name="confirmPlan" width="100" headerAlign="center" align="center" allowSort="true"><spring:message code="page.standardDoCheckApplyEdit.name52" />
                        <input property="editor" class="mini-combobox" style="width:90%;" multiSelect="false"
                               textField="text" valueField="value" emptyText="<spring:message code="page.standardDoCheckApplyEdit.name10" />..."
                               allowInput="false" showNullItem="false" nullItemText="<spring:message code="page.standardDoCheckApplyEdit.name10" />..."
                               data="[{'text':'不符合','value':'不符合'},{'text':'符合','value':'符合'}]"
                        />
                    </div>
                    <div field="closeDesc" width="240" headerAlign="center" align="left"  allowSort="true" renderer="renderTextAreaWrapAutoHeight"><spring:message code="page.standardDoCheckApplyEdit.name28" />
                        <input property="editor" class="mini-textarea"/>
                    </div>
                    <div field="confirmResult" name="confirmResult" width="100" headerAlign="center" align="center" allowSort="true"><spring:message code="page.standardDoCheckApplyEdit.name29" />
                        <input property="editor" class="mini-combobox" style="width:90%;" multiSelect="false"
                               textField="text" valueField="value" emptyText="请选择..."
                               allowInput="false" showNullItem="false" nullItemText="请选择..."
                               data="[{'text':'不符合','value':'不符合'},{'text':'待整改','value':'待整改'},{'text':'符合','value':'符合'}]"
                        />
                    </div>
                    <div width="95" headerAlign="center" align="center" renderer="closeProblemFileRenderer" ><spring:message code="page.standardDoCheckApplyEdit.name30" /></div>
                </div>
            </div>

        </form>
    </div>
</div>

<div id="selectStandardWindow" title="<spring:message code="page.standardDoCheckApplyEdit.name31" />" class="mini-window" style="width:1200px;height:500px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
    <input id="parentInputScene" style="display: none" />
    <div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px"
         borderStyle="border-left:0;border-top:0;border-right:0;">
        <span class="text" style="width:auto"><spring:message code="page.standardDoCheckApplyEdit.name32" />: </span>
        <input id="filterCategoryId" class="mini-combobox" style="width:150px;"
                   textField="categoryName" valueField="id" emptyText="<spring:message code="page.standardDoCheckApplyEdit.name10" />..."
                   required="false" allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.standardDoCheckApplyEdit.name10" />..."/>
        <span style="font-size: 14px;color: #777"><spring:message code="page.standardDoCheckApplyEdit.name3" />: </span>
        <input class="mini-textbox" width="130" id="filterStandardNumberId" style="margin-right: 15px"/>
        <span style="font-size: 14px;color: #777"><spring:message code="page.standardDoCheckApplyEdit.name4" />: </span>
        <input class="mini-textbox" width="130" id="filterStandardNameId" style="margin-right: 15px"/>
        <a class="mini-button" iconCls="icon-search" plain="true" onclick="searchStandard()"><spring:message code="page.standardDoCheckApplyEdit.name33" /></a>
    </div>
    <div class="mini-fit">
        <div id="standardListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onrowdblclick="onRowDblClick"
             idField="id"  showColumnsMenu="false" sizeList="[20,50,100,200]" pageSize="20"
             allowAlternating="true" pagerButtons="#pagerButtons" url="${ctxPath}/standardManager/core/standard/queryList.do"
        >
            <div property="columns">
                <div type="checkcolumn" width="30"></div>
                <div field="systemName" sortField="systemName" width="90" headerAlign="center" align="center"
                     allowSort="true"><spring:message code="page.standardDoCheckApplyEdit.name34" />
                </div>
                <div field="categoryName" sortField="categoryName" width="90" headerAlign="center" align="center"
                     allowSort="true"><spring:message code="page.standardDoCheckApplyEdit.name35" />
                </div>
                <div field="standardNumber" sortField="standardNumber" width="140" headerAlign="center" align="center"
                     align="center" allowSort="true"><spring:message code="page.standardDoCheckApplyEdit.name36" />
                </div>
                <div field="standardName" sortField="standardName" width="180" headerAlign="center" align="center"
                     allowSort="true"><spring:message code="page.standardDoCheckApplyEdit.name37" />
                </div>
                <div field="belongDepName" sortField="belongDepName" width="80" headerAlign="center" align="center"
                     allowSort="true"><spring:message code="page.standardDoCheckApplyEdit.name38" />
                </div>
                <div field="standardStatus" sortField="standardStatus" width="60" headerAlign="center" align="center"
                     allowSort="true" renderer="statusRenderer"><spring:message code="page.standardDoCheckApplyEdit.name39" />
                </div>
                <div field="publisherName" sortField="publisherName" align="center"
                     width="60" headerAlign="center" allowSort="true"><spring:message code="page.standardDoCheckApplyEdit.name40" />
                </div>
                <div field="publishTime" sortField="publishTime" dateFormat="yyyy-MM-dd" align="center"
                     width="100" headerAlign="center" allowSort="true"><spring:message code="page.standardDoCheckApplyEdit.name41" />
                </div>
            </div>
        </div>
    </div>
    <div property="footer" style="padding:5px;height: 35px">
        <table style="width:100%;height: 100%">
            <tr>
                <td style="width:120px;text-align:center;">
                    <input type="button" style="height: 25px;width: 70px" value="<spring:message code="page.standardDoCheckApplyEdit.name42" />" onclick="selectStandardOK()"/>
                    <input type="button" style="height: 25px;width: 70px" value="<spring:message code="page.standardDoCheckApplyEdit.name43" />" onclick="selectStandardHide()"/>
                </td>
            </tr>
        </table>
    </div>
</div>

<div id="closeProblemFileWindow" title="<spring:message code="page.standardDoCheckApplyEdit.name44" />" class="mini-window" style="width:1000px;height:400px;"
     showModal="true" showFooter="false" allowResize="true" showCloseButton="true">
    <div class="mini-toolbar">
        <a id="cxFileUploadBtn" class="mini-button"  style="margin-bottom: 5px;display: none" onclick="uploadFile()"><spring:message code="page.standardDoCheckApplyEdit.name45" /></a>
    </div>
    <input class="mini-hidden" id="detailId" />
    <div class="mini-fit">
        <div id="fileListGrid" class="mini-datagrid" style="width: 100%; height:100%"
             allowResize="false" allowCellWrap="true" idField="id"
             autoload="false" showPager="false" showColumnsMenu="false" allowAlternating="true">
            <div property="columns">
                <div field="fileName"name="name"  width="160" headerAlign='center' align='center'><spring:message code="page.standardDoCheckApplyEdit.name46" /></div>
                <div field="fileSize" headerAlign='center' align='center' width="60"><spring:message code="page.standardDoCheckApplyEdit.name47" /></div>
                <div field="fileDesc" width="80" headerAlign="center" align="center"><spring:message code="page.standardDoCheckApplyEdit.name48" /></div>
                <div field="creator" width="80" headerAlign='center' align="center"><spring:message code="page.standardDoCheckApplyEdit.name49" /></div>
                <div field="CREATE_TIME_" width="100" dateFormat="yyyy-MM-dd HH:mm:ss" headerAlign='center' align="center"><spring:message code="page.standardDoCheckApplyEdit.name50" /></div>
                <div field="action" width="100" headerAlign='center' align="center" renderer="operationRenderer"><spring:message code="page.standardDoCheckApplyEdit.name51" /></div>
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var nodeVarsStr = '${nodeVars}';
    var jsUseCtxPath="${ctxPath}";
    var doCheckForm = new mini.Form("#doCheckForm");
    var checkGrid = mini.get("checkGrid");
    var fileListGrid = mini.get("fileListGrid");
    var closeProblemFileWindow=mini.get("closeProblemFileWindow");

    var action="${action}";
    var status="${status}";
    var applyId="${applyId}";
    var currentUserId="${currentUserId}";
    var currentUserName="${currentUserName}";
    var currentTime="${currentTime}";
    var coverContent=currentUserName+"<br/>"+currentTime+"<br/>" + xzxgwjjxyxgs;

    var selectStandardWindow=mini.get("selectStandardWindow");
    var standardListGrid=mini.get("standardListGrid");
    var categoryList=${categoryList};

    function operationRenderer(e) {
        var record = e.record;
        //预览、下载和删除
        var cellHtml=returnPreviewSpan(record.fileName,record.id,record.baseInfoId,coverContent);
        var downloadUrl = '/standard/core/doCheck/fileDownload.do';
        cellHtml+='&nbsp;&nbsp;&nbsp;<span title=' + standardDoCheckApplyEdit_name + ' style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downLoadFile(\''+record.fileName+'\',\''+record.id+'\',\''+record.baseInfoId+'\',\''+downloadUrl+'\')">' + standardDoCheckApplyEdit_name + '</span>';
        if(action =='task' && (stageName == 'zgResultWrite'||stageName == 'zgResultWriteAgain')) {
            var deleteUrl="/standard/core/doCheck/deleteFiles.do";
            cellHtml+='&nbsp;&nbsp;&nbsp;<span title=' + standardDoCheckApplyEdit_name1 + ' style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile(\''+record.fileName+'\',\''+record.id+'\',\''+record.baseInfoId+'\',\''+deleteUrl+'\')">' + standardDoCheckApplyEdit_name1 + '</span>';
        }
        return cellHtml;
    }

    function closeProblemFileRenderer(e) {
        var record = e.record;
        var cellHtml='';
        var detailId=record.id;
        if(detailId) {
            cellHtml='<span  title=' + standardDoCheckApplyEdit_name2 + ' style="color:#409EFF;cursor: pointer;" onclick="closeProblemFileClick(\'' +detailId+'\',\''+record.judge+ '\')">' + standardDoCheckApplyEdit_name2 + '</span>';
        }
        return cellHtml;
    }


    function returnPreviewSpan(fileName,fileId,formId,coverContent) {
        var fileType=getFileType(fileName);
        var s='';
        if(fileType=='other'){
            s = '<span  title=' + standardDoCheckApplyEdit_name3 + ' style="color: silver" >' + standardDoCheckApplyEdit_name3 + '</span>';
        }else {
            var url='/standard/core/doCheck/preview.do?fileType='+fileType;
            s = '<span  title=' + standardDoCheckApplyEdit_name3 + ' style="color:#409EFF;cursor: pointer;" onclick="previewPdf6(\'' +fileName+'\',\''+fileId+'\',\''+formId+'\',\''+coverContent+'\',\''+url+'\',\''+fileType+ '\')">' + standardDoCheckApplyEdit_name3 + '</span>';
        }
        return s;
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

    checkGrid.on("cellbeginedit", function (e) {
        var record = e.record;
        if(!e.editor) {
            return;
        }
        if(action=='task') {
            if (stageName=='shqkWrite') {
                if(e.field == "fileType"
                    || e.field == "fileName"
                    || e.field == "filePath"
                    || e.field == 'useDesc'
                    || e.field == 'judge'
                    || (e.field == 'respUserId' && record.judge=='否')) {
                    e.editor.setEnabled(true);
                } else {
                    e.editor.setEnabled(false);
                }
            } else if (stageName=='zgPlanWrite' || stageName=='zgPlanWriteAgain') {
                if((e.field == "detailTypes"
                    || e.field == "modifyMethod"
                    || e.field == "planFinishTime"
                    || e.field == "closeRespUserId") && record.judge=='否') {
                    e.editor.setEnabled(true);
                } else {
                    e.editor.setEnabled(false);
                }
            }else if (stageName=='confirmPlan') {
                if(e.field == "confirmPlan" && record.judge=='否') {
                    e.editor.setEnabled(true);
                } else {
                    e.editor.setEnabled(false);
                }
            }else if (stageName=='zgResultWrite' || stageName=='zgResultWriteAgain') {
                if(e.field == "closeDesc" && record.judge=='否') {
                    e.editor.setEnabled(true);
                } else {
                    e.editor.setEnabled(false);
                }
            }else if (stageName=='confirmResult') {
                if(e.field == "confirmResult" && record.judge=='否') {
                    e.editor.setEnabled(true);
                } else {
                    e.editor.setEnabled(false);
                }
            } else {
                e.editor.setEnabled(false);
            }
        } else {
            e.editor.setEnabled(false);
        }

    });

    checkGrid.on("cellcommitedit", function (e) {
        var grid = e.sender,
            record = e.record;
        if (e.field == "respUserId") {
            var userId=e.value;
            var userInfo=getUserInfoById(userId);
            grid.updateRow(record, {deptName:userInfo.mainDepName});
        }
    });

    checkGrid.on("drawcell", function (e) {
        var grid = e.sender,
            record = e.record;
        if (e.field == "judge") {
            var judge=e.value;
            if(judge=='否') {
                e.cellHtml = '<span style="color:red">'+judge+'</span>';
            }
        }
    });

</script>
</body>
</html>
