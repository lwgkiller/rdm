<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>列表管理</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/world/worldResearchList.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.worldResearchList.wjmc" />: </span>
                    <input class="mini-textbox"  name="fileName"/>
                </li>
                <li style="margin-right: 15px;" id="fileCategory">
                    <span class="text" style="width:auto"><spring:message code="page.worldResearchList.wjlb" />: </span>
                <input  name="fileCategory" class="mini-combobox rxc" plugins="mini-combobox"
                       style="width:100%;height:34px" label="<spring:message code="page.worldResearchList.wjlb" />："
                       length="50" onvaluechanged="searchFrm()"
                       only_read="false" required="false" allowinput="false" mwidth="100"
                       wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                       textField="text" valueField="key_" emptyText="<spring:message code="page.worldResearchList.qxz" />..."
                       url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=BDLB"
                       nullitemtext="<spring:message code="page.worldResearchList.qxz" />..." emptytext="<spring:message code="page.worldResearchList.qxz" />..."/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.worldResearchList.spzt" />: </span>
                    <input id="applyStatus" name="applyStatus" class="mini-combobox rxc" plugins="mini-combobox"
                           style="width:100%;height:34px" label="<spring:message code="page.worldResearchList.spzt" />："
                           length="50" onvaluechanged="searchFrm()"
                           only_read="false" required="false" allowinput="false" mwidth="100"
                           wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                           textField="text" valueField="key_" emptyText="<spring:message code="page.worldResearchList.qxz" />..."
                           url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=SPLB"
                           nullitemtext="<spring:message code="page.worldResearchList.qxz" />..." emptytext="<spring:message code="page.worldResearchList.qxz" />..."/>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()"><spring:message code="page.worldResearchList.cx" /></a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()"><spring:message code="page.worldResearchList.qkcx" /></a>
                    <a class="mini-button " style="margin-left: 10px" plain="true" onclick="downTemplate()"><spring:message code="page.worldResearchList.mbxz" /></a>
                </li>
            </ul>
        </form>
        <ul class="toolBtnBox">
            <a id="uploadBtn" class="mini-button" iconCls="icon-upload" onclick="openUploadWindow()" plain="true"><spring:message code="page.worldResearchList.scwj" /></a>
            <div style="display: inline-block" class="separator"></div>
            <a class="mini-button " style="margin-left: 10px" plain="true" onclick="submit()"><spring:message code="page.worldResearchList.tj" /></a>
            <div style="display: inline-block" class="separator"></div>
            <a id="batchAgree" class="mini-button " style="margin-left: 10px" plain="true" onclick="batchAudit(3)"><spring:message code="page.worldResearchList.pltg" /></a>
            <div style="display: inline-block" class="separator"></div>
            <a id="batchRefuse" class="mini-button btn-red" style="margin-left: 10px" plain="true" onclick="batchAudit(2)"><spring:message code="page.worldResearchList.plbh" /></a>
        </ul>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="listGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         sizeList="[50,100,200]" pageSize="50"
         url="${ctxPath}/world/core/research/getListData.do?fileType=1&menuType=${menuType}"
         idField="id" allowAlternating="true" showPager="true" multiSelect="true">
        <div property="columns">
            <div type="checkcolumn" width="10"></div>
            <div type="indexcolumn" headerAlign="center" align="center" width="15"><spring:message code="page.worldResearchList.xh" /></div>
            <div name="action" cellCls="actionIcons" width="25" headerAlign="center" align="center"
                 renderer="onMessageActionRenderer" cellStyle="padding:0;"><spring:message code="page.worldResearchList.cz" />
            </div>
            <div field="applyStatus" width="50"  headerAlign='center' align="center" renderer="onAudit"><spring:message code="page.worldResearchList.spzt" /></div>
            <div field="fileCategory" name="fileCategory" width="50" headerAlign="center" align="center" allowSort="true" renderer="onType"><spring:message code="page.worldResearchList.wjlb" /></div>
            <div field="fileName" headerAlign='center' align='left'  width="100"><spring:message code="page.worldResearchList.wjmc" /></div>
            <div field="fileDesc" headerAlign='center' align='left' width="100"><spring:message code="page.worldResearchList.wjms" /></div>
            <div field="fileSize" headerAlign='center' align='center'  width="40"><spring:message code="page.worldResearchList.wjdx" /></div>
            <div field="deptName" width="50" headerAlign="center" align="center" allowSort="true"><spring:message code="page.worldResearchList.ssbm" /></div>
            <div field="userName" width="50" headerAlign="center" align="center" allowSort="true"><spring:message code="page.worldResearchList.scr" /></div>
            <div cellCls="actionIcons" width="40" headerAlign="center" align="center" renderer="fileRenderer"
                 cellStyle="padding:0;" hideable="true"><spring:message code="page.worldResearchList.fjqw" />
            </div>
        </div>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var listGrid = mini.get("listGrid");
    var currentUserId = "${currentUser.userId}";
    var menuType = "${menuType}";
    var fileType = '1';
    var typeList = getDics("BDLB");
    var auditList = getDics("SPLB");
    var currentTime="${currentTime}";
    var currentUserName="${currentUserName}";
    var coverContent=currentUserName+"<br/>"+currentTime+"<br/>" + xzxgwjjxyxgs;
    var isFGSH = ${isFGSH};
    var menuFlag = "${menuFlag}";
    function openUploadWindow() {
        mini.open({
            title: worldResearchList_wjsc,
            url: jsUseCtxPath + "/world/core/research/openUploadWindow.do?fileType=1&menuType=${menuType}&menuFlag="+menuFlag,
            width: 800,
            height: 400,
            showModal:false,
            allowResize: true,
            onload: function () {
                var iframe = this.getIFrameEl();
                var uploadParams={};
                uploadParams.standardId = '';
                uploadParams.scene='decodeGj';
                uploadParams.menuType = menuType;
                uploadParams.fileType = fileType;
                var data = { projectParams: uploadParams };  //传递上传参数
                iframe.contentWindow.SetData(data);
            },
            ondestroy: function (action) {
                searchFrm()
            }
        });
    }

    function onMessageActionRenderer(e) {
        var record = e.record;
        var status = record.applyStatus;
        var fileId = record.id;
        var s = '';
        if ((status == '0' || status == '2'|| status == '3')&&currentUserId == record.CREATE_BY_) {
            s += '<span  title=' + worldResearchList_sc + ' onclick="deleteFile(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')"><spring:message code="page.worldResearchList.sc" /></span>';
        }else  if (status == '1'&&isFGSH) {
            s += '<span  title=' + worldResearchList_tg + ' onclick="task(\'' + fileId + '\',\'3\')"><spring:message code="page.worldResearchList.tg" /></span>';
            s += '<span  title=' + worldResearchList_bh + ' onclick="task(\'' + fileId + '\',\'2\')"><spring:message code="page.worldResearchList.bh" /></span>';
        } else{
            s+='<span  title=' + worldResearchList_sc + ' style="color: silver"><spring:message code="page.worldResearchList.sc" /></span>';
        }

        return s;
    }
    function fileRenderer(e) {
        var record = e.record;
        var fileId = record.id;
        var fileName = record.fileName;
        var cellHtml = '';
        cellHtml = returnPreviewSpan(fileId,fileName,menuType,coverContent);
        cellHtml += '&nbsp;&nbsp;&nbsp;<span title=' + worldResearchList_xz + ' style="color:#409EFF;cursor: pointer;" ' +
            'onclick="downFile(\''+fileName+'\',\''+fileId+'\',\''+menuType+'\')"><spring:message code="page.worldResearchList.xz" /></span>';
        return cellHtml;
    }
    function onType(e) {
        var record = e.record;
        var resultValue = record.fileCategory;
        var resultText = '';
        for (var i = 0; i < typeList.length; i++) {
            if (typeList[i].key_ == resultValue) {
                resultText = typeList[i].text;
                break
            }
        }
        return resultText;
    }
    function onAudit(e) {
        var record = e.record;
        var resultValue = record.applyStatus;
        var resultText = '';
        var html = '';
        for (var i = 0; i < auditList.length; i++) {
            if (auditList[i].key_ == resultValue) {
                if(resultValue=='0'){
                    html = '<span style="color: orange">'+ auditList[i].text+'</span>'
                }else if(resultValue=='1'){
                    html = '<span style="color: blue">'+ auditList[i].text+'</span>'
                }else if(resultValue=='2'){
                    html = '<span style="color: red">'+ auditList[i].text+'</span>'
                }else if(resultValue=='3'){
                    html = '<span style="color: green">'+ auditList[i].text+'</span>'
                }
                break
            }
        }
        return html;
    }
</script>
<redxun:gridScript gridId="listGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
