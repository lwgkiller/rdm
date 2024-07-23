<%@ taglib prefix="redxun" uri="http://www.redxun.cn/gridFun" %>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <%@include file="/commons/dynamic.jspf" %>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>我的桌面</title>
    <link rel="stylesheet" type="text/css" href="${ctxPath}/styles/icons.css">
    <link rel="stylesheet" type="text/css" href="${ctxPath}/scripts/layoutit/css/layoutitIndex.css">
    <link rel="stylesheet" type="text/css" href="${ctxPath}/scripts/layoutit/css/layout.css">
    <!--[if lte IE 8]>
    <script src="js/html5shiv.js"></script>

    <link rel="stylesheet" type="text/css" href="${ctxPath}/scripts/layoutit/css/layoutitIndexIE8.css">
    <![endif]-->
    <script type="text/javascript" src="${ctxPath}/scripts/jquery-1.11.3.js"></script>
    <script type="text/javascript" src="${ctxPath}/scripts/mini/miniui/miniui.js"></script>
    <script type="text/javascript" src="${ctxPath}/scripts/share.js"></script>
    <script type="text/javascript" src="${ctxPath}/scripts/layoutit/js/layoutitIndex.js"></script>

    <link rel="stylesheet" type="text/css" href="${ctxPath }/scripts/layoutit/css/jquery.gridster.min.css">
    <script type="text/javascript" src="${ctxPath }/scripts/layoutit/js/jquery.gridster.min.js"></script>

    <link href="${ctxPath}/scripts/mini/miniui/themes/default/miniui.css" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/form/customFormUtil.js?version=${static_res_version}" type="text/javascript"></script>

    <link href="${ctxPath}/styles/commons.css" rel="stylesheet" type="text/css"/>

    <script src="${ctxPath}/scripts/standardManager/standardDeskHome.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/common/list.js?static_res_version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/common/commonTools.js?static_res_version=${static_res_version}" type="text/javascript"></script>

    <style>
        .lui_dataview_classic_new {
            background:url(${ctxPath}/styles/images/list_new.png) no-repeat;
            display: inline-block;
            width: 23px;
            height: 11px;
            margin-top: 3px;
            margin-left: 10px;
        }

        .span-header{
            line-height:38px;
            color: #006699 !important;;
            font-size: 14px !important;
            float:left;
            clear:right;
        }

        .gridster ul li div.containerBox {
            height: calc(100% - 40px);
            box-sizing: border-box;
            background: #fff;
        }

        #quickSearchId .mini-textbox {
            height: 27px;
        }
        #quickSearchId .mini-textbox-border {
            height: 25px;
        }
        #quickSearchId .mini-textbox-input {
            height: 25px;
            line-height: 23px;
        }
    </style>
</head>
<body>
<div class="personalPort">
    <div class="gridster">
        <ul>
            <li class="gs-w" data-row="1" data-col="1" data-sizex="3" data-sizey="2">
                <header class="deskHome-header">
                    <p style="width: 150px;text-align: left" class="span-header"><spring:message code="page.standardDeskHome.standName1" /></p>
					<a onClick="jumpToStandardSearch('more')" style="font-size: 14px;float: right;color:#006699;vertical-align: middle;margin-right: 10px" href="#"><spring:message code="page.standardDeskHome.standName2" /></a>
                </header>
                <div id="quickSearchId" class="containerBox">
                    <div style="position: absolute;left: 5%;">
                        <div style="margin-top: 40px">
                            <span class="text" style="width:auto;margin-right: 15px"><spring:message code="page.standardDeskHome.standName3" />: </span>
                            <input class="mini-textbox" id="standardNumber" style="height: 27px;margin-right: 20px;width: 160px" onenter="standardEnterSearch"/>
                            <span class="text" style="width:auto;margin-right: 15px"><spring:message code="page.standardDeskHome.standName4" />: </span>
                            <input class="mini-textbox" id="standardName" onenter="standardEnterSearch" style=";width: 160px"/>
                        </div>
                        <div style="margin-top: 11px">
                            <span class="text" style="width:auto;margin-right: 15px"><spring:message code="page.standardDeskHome.standName5" />: </span>
                            <input id="standardCategory" style="margin-right: 20px;width: 160px" class="mini-combobox" textField="categoryName" valueField="id" emptyText="<spring:message code="page.standardDeskHome.standName8" />..."
                                   required="false" allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.standardDeskHome.standName8" />..."/>
                            <span class="text" style="width:auto;margin-right: 15px"><spring:message code="page.standardDeskHome.standName6" />: </span>
                            <div id="systemCategory" class="mini-radiobuttonlist" style="display: inline-block;width: 170px"  repeatItems="3" repeatLayout="table" repeatDirection="horizontal"
                                 textfield="systemCategoryName" valuefield="systemCategoryId"  onvaluechanged="systemCategoryChange">
                            </div>
                        </div>
                        <div style="margin-top: 11px">
                            <span class="text" style="width:auto;margin-right: 15px"><spring:message code="page.standardDeskHome.standName7" />: </span>
                            <div id="standardField" class="mini-combobox" textField="fieldName" valueField="fieldId" emptyText="<spring:message code="page.standardDeskHome.standName8" />..."
                                 required="false" allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.standardDeskHome.standName8" />..." style=";width: 160px">
                            </div>
                        </div>
                        <div style="margin-top: 30px">
                            <a class="mini-button" style="margin-left:180px;margin-right: 100px" plain="true" onclick="jumpToStandardSearch('base')"><spring:message code="page.standardDeskHome.standName9" /></a>
                            <a class="mini-button btn-red" plain="true" onclick="clearQuickSearch()"><spring:message code="page.standardDeskHome.standName10" /></a>
                        </div>
                    </div>
                </div>
            </li>
            <li class="gs-w" data-row="1" data-col="4" data-sizex="3" data-sizey="2">
                <header class="deskHome-header">
                    <p style="width: 150px;text-align: left" class="span-header"><spring:message code="page.standardDeskHome.standName11" />(<span id="taskNum" style="color: red;width: auto">0</span>)</p>
                    <span style="float: right;	font-size: 13px;color: #333;vertical-align: middle">
						<spring:message code="page.standardDeskHome.standName12" />
						<input id="Q_solutionKey_S_EQ" name="Q_solutionKey_S_EQ" class="mini-combobox"
                               style="width:150px;height:40px;margin-right: 5px"
                               textField="text" valueField="id" emptyText="<spring:message code="page.standardDeskHome.standName8" />..." onvaluechanged="taskTypeChange()"
                               required="false" allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.standardDeskHome.standName8" />..."
                               url="${ctxPath}/standardManager/core/standard/getSolutions.do"/>
                    <a onClick="refreshDeskHomeTab('task')" style="margin-right:5px" href="#"><spring:message code="page.standardDeskHome.standName13" /></a>
                    </span>
                </header>
                <div id="bpmTask" class="containerBox">
                    <div id="taskDatagrid" class="mini-datagrid" style="width: 100%; height: 100%;"
                         allowResize="false" url="${ctxPath}/bpm/core/bpmTask/myTasks.do"
                         idField="id" showPager="false" multiSelect="false" showColumnsMenu="true" allowAlternating="true">
                        <div property="columns">
                            <div type="indexcolumn" width="20" headerAlign="center"><spring:message code="page.standardDeskHome.standName14" /></div>
                            <div name="action" cellCls="actionIcons" width="60" renderer="onActionRenderer" cellStyle="padding:0;"><spring:message code="page.standardDeskHome.standName15" /></div>
                            <div field="solKey" sortField="solKey" width="80" headerAlign="" allowSort="true" renderer="taskCategoryRenderer"><spring:message code="page.standardDeskHome.standName12" /></div>
                            <div field="description" sortField="description_" width="120" headerAlign="" allowSort="false"><spring:message code="page.standardDeskHome.standName16" /></div>
                            <div field="name" width="70" headerAlign="center"><spring:message code="page.standardDeskHome.standName17" /></div>
                            <%--<div field="assigneeNames" width="40" headerAlign="center">处理人</div>--%>
                            <div field="createTime" sortField="CREATE_TIME_" width="70" dateFormat="yyyy-MM-dd HH:mm:ss" headerAlign="center" allowSort="false"><spring:message code="page.standardDeskHome.standName18" /></div>
                        </div>
                    </div>
                </div>
            </li>
            <li class="gs-w" data-row="4" data-col="1" data-sizex="3" data-sizey="2">
                <header class="deskHome-header">
                    <p class="span-header"><spring:message code="page.standardDeskHome.standName19" /></p>
                    <a onClick="refreshDeskHomeTab('apply')" style="font-size: 14px;float: right;color:#006699;vertical-align: middle;margin-right: 10px" href="#"><spring:message code="page.standardDeskHome.standName13" /></a>
                </header>
                <div class="containerBox">
                    <div id="applyListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
                         url="${ctxPath}/standardManager/core/standardApply/applyQuery.do?"
                         idField="applyId" showPager="false"
                         multiSelect="false" showColumnsMenu="false" allowAlternating="true">
                        <div property="columns">
                            <div field="applyCategoryId" visible="false"></div>
                            <div field="applyCategoryName" sortField="applyCategoryName" width="80" headerAlign="center" align="center" allowSort="true">
                                <spring:message code="page.standardDeskHome.standName20" />
                            </div>
                            <div field="applyId" sortField="applyId" width="160" headerAlign="center" align="center" allowSort="true">
                                <spring:message code="page.standardDeskHome.standName21" />
                            </div>
                            <div field="standardNumber" sortField="standardNumber" width="130" headerAlign="center" align="center"
                                 allowSort="true"><spring:message code="page.standardDeskHome.standName3" />
                            </div>
                            <div field="standardName" width="160" headerAlign="center" align="center" allowSort="false"><spring:message code="page.standardDeskHome.standName4" /></div>
                            <div field="currentProcessUser" sortField="currentProcessUser" width="70" align="center"
                                 headerAlign="center" allowSort="false"><spring:message code="page.standardDeskHome.standName22" />
                            </div>
                            <div field="applyTime" sortField="applyTime" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" width="100"
                                 headerAlign="center" allowSort="true"><spring:message code="page.standardDeskHome.standName23" />
                            </div>
                        </div>
                    </div>
                </div>
            </li>
            <li class="gs-w" data-row="4" data-col="4" data-sizex="3" data-sizey="2">
                <header class="deskHome-header">
                    <p class="span-header"><spring:message code="page.standardDeskHome.standName24" /></p>
                    <a onClick="moreDate('message')" style="font-size: 14px;float: right;color:#006699;vertical-align: middle;margin-right: 10px" href="#"><spring:message code="page.standardDeskHome.standName25" /></a>
                </header>
                <div class="containerBox">
                    <div id="standardMsgListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" showHGridLines="false"
                         idField="id" allowAlternating="true" showPager="false" multiSelect="false" showColumns="false"
                         url="${ctxPath}/standardManager/core/standardMessage/queryMsgList.do?systemCategoryValue=${systemCategoryValue}">
                        <div property="columns">
                            <div field="standardMsgTitle" headerAlign='center' align='left' renderer="onMessageTitleRenderer"><spring:message code="page.standardDeskHome.standName26" /></div>
                            <div field="CREATE_TIME_" width="40" dateFormat="yyyy-MM-dd HH:mm:ss" headerAlign='center' align="right"><spring:message code="page.standardDeskHome.standName27" /></div>
                        </div>
                    </div>
                </div>
            </li>

            <li class="gs-w" data-col="1" data-row="7" data-sizex="3" data-sizey="2">
                <header class="deskHome-header">
                    <p class="span-header"><spring:message code="page.standardDeskHome.standName28" /></p>
                    <a onClick="refreshDeskHomeTab('template')" style="font-size: 14px;float: right;color:#006699;vertical-align: middle;margin-right: 10px" href="#"><spring:message code="page.standardDeskHome.standName13" /></a>
                </header>
                <div class="containerBox">
                    <div id="templateListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" showHGridLines="false"
                         idField="id" allowAlternating="true" showPager="false" multiSelect="false" showColumns="false"
                         url="${ctxPath}/standardManager/core/standardConfig/templateQuery.do">
                        <div property="columns">
                            <div field="templateName" headerAlign='center' align='left' width="100" renderer="onTemplateNameRenderer"><spring:message code="page.standardDeskHome.standName29" /></div>
                            <div field="CREATE_TIME_" width="100" dateFormat="yyyy-MM-dd HH:mm:ss" headerAlign='center' align="right"><spring:message code="page.standardDeskHome.standName18" /></div>
                        </div>
                    </div>
                </div>
            </li>

            <li class="gs-w" data-col="4" data-row="7" data-sizex="3" data-sizey="2">
                <header class="deskHome-header">
                    <p class="span-header"><spring:message code="page.standardDeskHome.standName30" /></p>
                </header>
                <div class="containerBox">
                    <div id="linkListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" showHGridLines="false"
                         idField="id" allowAlternating="true" showPager="false" multiSelect="false" showColumns="false">
                        <div property="columns">
                            <div field="linkName" headerAlign='center' align='left' width="100" renderer="onLinkNameRenderer"><spring:message code="page.standardDeskHome.standName29" /></div>
                        </div>
                    </div>
                </div>
            </li>

        </ul>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    overview();
    var jsUseCtxPath = "${ctxPath}";
    var currentUserId="${currentUserId}";
    var systemCategoryValue="${systemCategoryValue}";
    var grid = mini.get("taskDatagrid");
    var applyListGrid=mini.get("applyListGrid");
    var standardMsgListGrid=mini.get("standardMsgListGrid");
    var templateListGrid=mini.get("templateListGrid");
    var linkListGrid=mini.get("linkListGrid");

    //操作栏
    grid.on("update",function(e){
        handGridButtons(e.sender.el);
    });


    function onTemplateNameRenderer(e) {
        var record = e.record;
        var templateName=record.templateName;
        var result='<a href="#" style="color:#0044BB;" onclick="seeTemplate(\'' + record.id+ '\')">'+templateName+'</a>';
        var CREATE_TIME_=record.CREATE_TIME_;
        var oneWeekBeforeNow=new Date();
        oneWeekBeforeNow.setDate(new Date().getDate()-7);
        var createDate=new Date(CREATE_TIME_);
        if(createDate>oneWeekBeforeNow) {
            result+='<span class="lui_dataview_classic_new"></span>';
        }
        return result;
    }

    function onMessageTitleRenderer(e) {
        var record = e.record;
        var title=record.standardMsgTitle;
        var result='<a href="#" style="color:#0044BB;" onclick="seeMessage(\'' + record.id+'\',\''+record.relatedStandardId+ '\')">'+title+'</a>';
        var CREATE_TIME_=record.CREATE_TIME_;
        var oneWeekBeforeNow=new Date();
        oneWeekBeforeNow.setDate(new Date().getDate()-7);
        var createDate=new Date(CREATE_TIME_);
        if(createDate>oneWeekBeforeNow) {
            result+='<span class="lui_dataview_classic_new"></span>';
        }
        if(record.isRead){
            if(record.isRead=='1'){
                result = '<img style="margin-bottom:-4px" src="${ctxPath}/styles/images/standard/noticeRead.png" />'+result
            }else if(record.isRead=='0'){
                result = '<img style="margin-bottom:-4px" src="${ctxPath}/styles/images/standard/noticeUnRead.png" />'+result
            }

        }
        return result;
    }

    function seeMessage(id,relatedStandardId) {
        var url = jsUseCtxPath + "/standardManager/core/standardMessage/see.do?id="+id+"&standardId="+relatedStandardId;
        var winObj = openNewWindow(url,"");
        var loop = setInterval(function() {
            if(winObj.closed) {
                clearInterval(loop);
                standardMsgListGrid.load();
            }
        }, 1000);
    }

    function seeTemplate(id) {
        window.open(jsUseCtxPath + "/standardManager/core/standardConfig/templateSee.do?id="+id);
    }

    function onLinkNameRenderer(e) {
        var record = e.record;
        var linkName=record.linkName;
        return '<a href="#" style="color:#0044BB;" onclick="jumpLink(\'' + record.url+ '\')">'+linkName+'</a>';
    }

    function overview() {
        var gridster = $(".personalPort .gridster > ul").gridster({
            widget_base_dimensions: ['auto', 130],
            autogenerate_stylesheet: true,
            min_cols: 1,
            max_cols: 6,
            widget_margins: [5, 5],
            draggable: {
                handle: 'header'
            },
            avoid_overlapped_widgets: true
        }).data('gridster');
        $('.personalPort  .gridster  ul').css({'padding': '0'});
    }

    function standardEnterSearch() {
        jumpToStandardSearch('base');
    }

    function taskCategoryRenderer(e) {
        var record = e.record;
        var solKey=record.solKey;
        var solName="";
        solName = filterDbType(solKey)
        return '<span>'+solName+'</span>';
    }
</script>
</body>
</html>
