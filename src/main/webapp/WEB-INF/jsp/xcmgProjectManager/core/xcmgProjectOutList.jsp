<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>项目成果列表</title>
    <%@include file="/commons/list.jsp" %>
    <link href="${ctxPath}/scripts/mini/miniui/themes/icons.css" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/xcmgProjectManager/projectOutList.js?version=${static_res_version}" type="text/javascript"></script>
</head>

<body>
<div class="mini-toolbar">
    <a class="mini-button" plain="true" onclick="searchOut()">查询</a>

    <a class="mini-button"  id="addOutButtons" plain="true" onclick="addOut()">添加</a>
    <a class="mini-button btn-red"  id="delOutButtons" plain="true" onclick="deleteOut()">删除</a>
    <div style="display: inline-block" class="separator"></div>
    <a class="mini-button" plain="true" id="saveOutButtons" onclick="saveOut()">保存</a>
    <p style="display: inline-block;color: #f6253e;font-size:15px;vertical-align: middle">（添加、删除、编辑后都需要保存）</p>
</div>
<div id="grid_project_out" class="mini-datagrid" allowResize="false" style="height:90%;width:99%"
     idField="id" allowCellEdit="true" allowCellSelect="true" allowSortColumn="false"
     url="${ctxPath}/xcmgProjectManager/core/xcmgProject/queryOutList?outPlanId=${outPlanId}&typeName=${typeName}"
     autoload="true" multiSelect="true" showColumnsMenu="false" allowAlternating="true" showPager="false" allowCellWrap="true">
    <div property="columns">
        <div type="checkcolumn" width="15"></div>
        <div field="outDescription" headerAlign="center" align="center" width="120">实际成果描述<span style="color:red">*</span>
            <input property="editor" class="mini-textarea"/>
        </div>
        <div field="outReferId" displayField="outName" headerAlign="center" align="center" width="100">成果链接
            <input property="editor" class="mini-buttonedit" showClose="true" allowInput="false"
                   oncloseclick="outNameCloseClick(e)" onbuttonclick="outNameClick()"/>
        </div>
        <div field="outNumber" headerAlign="center" align="center" width="80">成果编号
        </div>
        <div name="action" cellCls="actionIcons" headerAlign="center" align="center"
             renderer="outOperateRender" width="40">成果查看
        </div>
        <%--软著--%>
        <div visible="false" name="zpsm" field="zpsm" width="70" headerAlign="center" align="center" allowSort="false"
             renderer="rjzzZpsmRenderer">软件作品说明
        </div>
        <div visible="false" name="fbzt" field="fbzt" width="70" headerAlign="center" align="center" allowSort="false"
             renderer="rjzzFbztRenderer">发表状态
        </div>
        <%--标准--%>
        <div visible="false" name="categoryName" field="categoryName" width="70" headerAlign="center" align="center" allowSort="false">标准类别
        </div>
        <div visible="false" name="standardStatus" field="standardStatus" width="70" headerAlign="center" align="center" allowSort="false"
             renderer="standardStatusRenderer">状态
        </div>
        <%-- DFMEA --%>
        <div visible="false" name="femaType" field="femaType" width="70" headerAlign="center" align="center" allowSort="false"
             renderer="fmeaRenderer">FMEA类型
        </div>
        <%--国内专利--%>
        <div visible="false" name="zllxName" field="zllxName" width="70" headerAlign="center" align="center" allowSort="false">专利类型
        </div>
        <div visible="false" name="gnztName" field="gnztName" width="70" headerAlign="center" align="center" allowSort="false">案件状态
        </div>
        <div field="description" headerAlign="center" align="center" width="50">备注
            <input property="editor" class="mini-textarea"/>
        </div>
        <div field="outPlanId" visible="false">所属成果计划
        </div>
        <div field="projectId" visible="false">项目
        </div>
    </div>
</div>
<div id="selectZlWindow" title="选择国内专利" class="mini-window" style="width:900px;height:500px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
    <div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px"
         borderStyle="border-left:0;border-top:0;border-right:0;">
        <span style="font-size: 14px;color: #777">专利名称: </span>
        <input class="mini-textbox" width="130" id="zl_patentName" style="margin-right: 15px"/>
        <span style="font-size: 14px;color: #777">专利（申请）号: </span>
        <input class="mini-textbox" width="130" id="zl_applicationNumber" style="margin-right: 15px"/>
        <a class="mini-button" iconCls="icon-search" plain="true" onclick="searchOutList()">查询</a>
    </div>
    <div class="mini-fit">
        <div id="zlListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onrowdblclick="onRowDblClick"
             idField="zgzlId" showColumnsMenu="false" sizeList="[20,50,100,200]" pageSize="20"
             allowAlternating="true" pagerButtons="#pagerButtons" url="${ctxPath}/zhgl/core/zlgl/queryListData.do"
        >
            <div property="columns">
                <div type="checkcolumn" width="30"></div>
                <div field="patentName" sortField="patentName" width="130px" headerAlign="center" align="center" allowSort="false">专利名称</div>
                <div field="zllxName" displayField="zllxName" width="80px" headerAlign="center" align="center" allowSort="false">专利类型</div>
                <div field="applicationNumber" width="130px" headerAlign="center" align="center" allowSort="false">专利（申请）号</div>
                <div field="theInventors" width="150px" headerAlign="center" align="center" allowSort="false">发明人</div>
                <div field="gnztName" sortField="gnztId" displayField="gnztName" width="70px" headerAlign="center" align="center" allowSort="true">
                    状态
                </div>
                <div field="reportName" align="center" width="130px" headerAlign="center" allowSort="false">提案名称</div>
            </div>
        </div>
    </div>
    <div property="footer" style="padding:5px;height: 35px">
        <table style="width:100%;height: 100%">
            <tr>
                <td style="width:120px;text-align:center;">
                    <input type="button" style="height: 25px;width: 70px" value="确定" onclick="selectOutOK()"/>
                    <input type="button" style="height: 25px;width: 70px" value="取消" onclick="selectOutHide()"/>
                </td>
            </tr>
        </table>
    </div>
</div>

<div id="selectGjZlWindow" title="选择国际专利" class="mini-window" style="width:900px;height:500px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
    <div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px"
         borderStyle="border-left:0;border-top:0;border-right:0;">
        <span style="font-size: 14px;color: #777">国际申请号: </span>
        <input class="mini-textbox" width="130" id="gjzl_applictonNumber" style="margin-right: 15px"/>
        <span style="font-size: 14px;color: #777">PCT名称: </span>
        <input class="mini-textbox" width="130" id="gjzl_pctName" style="margin-right: 15px"/>
        <a class="mini-button" iconCls="icon-search" plain="true" onclick="searchOutList()">查询</a>
    </div>
    <div class="mini-fit">
        <div id="gjZlListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onrowdblclick="onRowDblClick"
             idField="gjzlId" showColumnsMenu="false" sizeList="[20,50,100,200]" pageSize="20"
             allowAlternating="true" pagerButtons="#pagerButtons" url="${ctxPath}/zhgl/core/zlgl/queryGjListData.do"
        >
            <div property="columns">
                <div type="checkcolumn" width="30"></div>
                <div field="pctName" width="120" headerAlign="center" align="center" allowSort="true">pct名称</div>
                <div field="applictonNumber" align="center" width="120" headerAlign="center" allowSort="false">国际申请号</div>
                <div field="gjztName" displayField="gjztName" width="80" headerAlign="center" align="center" allowSort="false">当前状态</div>
                <div field="applictonDay" width="70" headerAlign="center" align="center" allowSort="false">国际申请日</div>
                <div field="openDay" width="70" headerAlign="center" align="center" allowSort="false">国际公开日</div>
                <div field="theInventor" width="100" headerAlign="center" align="center" allowSort="false">发明人</div>
            </div>
        </div>
    </div>
    <div property="footer" style="padding:5px;height: 35px">
        <table style="width:100%;height: 100%">
            <tr>
                <td style="width:120px;text-align:center;">
                    <input type="button" style="height: 25px;width: 70px" value="确定" onclick="selectOutOK()"/>
                    <input type="button" style="height: 25px;width: 70px" value="取消" onclick="selectOutHide()"/>
                </td>
            </tr>
        </table>
    </div>
</div>
<div id="selectStandardWindow" title="选择标准" class="mini-window" style="width:950px;height:550px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
    <div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px"
         borderStyle="border-left:0;border-top:0;border-right:0;">
        <span style="font-size: 14px;color: #777">体系类别: </span>
        <input class="mini-combobox" width="130" id="filterSystemCategory" style="margin-right: 15px" textField="text"
               valueField="id" emptyText="请选择..." value="JS"
               data="[{id:'JS',text:'技术标准'},{id:'GL',text:'管理标准'}]"/>
        <span style="font-size: 14px;color: #777">编号: </span>
        <input class="mini-textbox" width="130" id="filterStandardNumberId" style="margin-right: 15px"/>
        <span style="font-size: 14px;color: #777">名称: </span>
        <input class="mini-textbox" width="130" id="filterStandardNameId" style="margin-right: 15px"/>
        <a class="mini-button" plain="true" onclick="searchOutList()">查询</a>
    </div>
    <div class="mini-fit">
        <div id="standardListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onrowdblclick="onRowDblClick"
             idField="id" showColumnsMenu="false" sizeList="[20,50,100,200]" pageSize="20"
             allowAlternating="true" pagerButtons="#pagerButtons" url="${ctxPath}/standardManager/core/standard/queryList.do"
        >
            <div property="columns">
                <div type="checkcolumn" width="30"></div>
                <div field="categoryName" sortField="categoryName" width="60" headerAlign="center" align="center"
                     allowSort="true">标准类别
                </div>
                <div field="standardNumber" sortField="standardNumber" width="140" headerAlign="center" align="center"
                     align="center" allowSort="true">编号
                </div>
                <div field="standardName" sortField="standardName" width="180" headerAlign="center" align="center"
                     allowSort="true">名称
                </div>
                <div field="standardStatus" sortField="standardStatus" width="60" headerAlign="center" align="center"
                     allowSort="true" renderer="standardStatusRenderer">状态
                </div>
                <div field="publisherName" sortField="publisherName" align="center"
                     width="60" headerAlign="center" allowSort="true">起草人
                </div>
            </div>
        </div>
    </div>
    <div property="footer" style="padding:5px;height: 35px">
        <table style="width:100%;height: 100%">
            <tr>
                <td style="width:120px;text-align:center;">
                    <input type="button" style="height: 25px;width: 70px" value="确定" onclick="selectOutOK()"/>
                    <input type="button" style="height: 25px;width: 70px" value="取消" onclick="selectOutHide()"/>
                </td>
            </tr>
        </table>
    </div>
</div>
<div id="selectRjzzWindow" title="选择软件著作权" class="mini-window" style="width:950px;height:550px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
    <div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px"
         borderStyle="border-left:0;border-top:0;border-right:0;">
        <span class="text" style="width:auto">软件著作编号: </span>
        <input class="mini-textbox" id="rjzzNum" name="rjzzNum">
        <span class="text" style="width:auto">名称: </span>
        <input class="mini-textbox" id="rjmqc" name="rjmqc"/>
        <a class="mini-button" plain="true" onclick="searchOutList()">查询</a>
    </div>
    <div class="mini-fit">
        <div id="rjzzListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onrowdblclick="onRowDblClick"
             idField="rjzzId" showColumnsMenu="false" sizeList="[20,50,100,200]" pageSize="20"
             allowAlternating="true" pagerButtons="#pagerButtons" url="${ctxPath}/zhgl/core/rjzz/getRjzzList.do"
        >
            <div property="columns">
                <div type="checkcolumn" width="30"></div>
                <div field="rjzzNum" sortField="rjzzNum" width="80" headerAlign="center" align="center" allowSort="true">软件著作编号</div>
                <div field="rjmqc" width="100" headerAlign="center" align="center" allowSort="false">名称</div>
                <div field="djNum" align="center" width="60" headerAlign="center" allowSort="false">登记号</div>
                <div field="zsNum" width="60" headerAlign="center" align="center" allowSort="false">证书号</div>
                <div field="applyUserName" width="70" headerAlign="center" align="center" allowSort="false">申请人</div>
                <div field="zpsm" width="70" headerAlign="center" align="center" allowSort="false" renderer="rjzzZpsmRenderer">软件作品说明</div>
                <div field="fbzt" width="70" headerAlign="center" align="center" allowSort="false" renderer="rjzzFbztRenderer">发表状态</div>
                <div field="status" width="60" headerAlign="center" align="center" allowSort="true" renderer="rjzzStatusRenderer">认定状态</div>
            </div>
        </div>
    </div>
    <div property="footer" style="padding:5px;height: 35px">
        <table style="width:100%;height: 100%">
            <tr>
                <td style="width:120px;text-align:center;">
                    <input type="button" style="height: 25px;width: 70px" value="确定" onclick="selectOutOK()"/>
                    <input type="button" style="height: 25px;width: 70px" value="取消" onclick="selectOutHide()"/>
                </td>
            </tr>
        </table>
    </div>
</div>

<div id="selectLwWindow" title="选择论文期刊" class="mini-window" style="width:950px;height:550px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
    <div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px"
         borderStyle="border-left:0;border-top:0;border-right:0;">
        <span class="text" style="width:auto">软件著作编号: </span>
        <input class="mini-textbox" id="kjlwNum" name="kjlwNum">
        <span class="text" style="width:auto">论文名称: </span>
        <input class="mini-textbox" id="kjlwName" name="kjlwName"/>
        <a class="mini-button" plain="true" onclick="searchOutList()">查询</a>
    </div>
    <div class="mini-fit">
        <div id="lwListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onrowdblclick="onRowDblClick"
             idField="kjlwId" showColumnsMenu="false" sizeList="[20,50,100,200]" pageSize="20"
             allowAlternating="true" pagerButtons="#pagerButtons" url="${ctxPath}/zhgl/core/kjlw/getKjlwList.do"
        >
            <div property="columns">
                <div type="checkcolumn" width="30"></div>
                <div field="kjlwNum" sortField="kjlwNum" width="80" headerAlign="center" align="center" allowSort="true">编号</div>
                <div field="kjlwName" width="100" headerAlign="center" align="center" allowSort="false">论文名称</div>
                <div field="writerName" align="center" width="60" headerAlign="center" allowSort="false">作者</div>
                <div field="qkmc" width="60" headerAlign="center" align="center" allowSort="false">期刊名称</div>
                <div field="qNum" width="60" allowSort="true" headerAlign="center" align="center">期号</div>
                <div field="yema" width="60" allowSort="true" headerAlign="center" align="center">页码</div>
                <div field="fbTime" width="60" allowSort="true" headerAlign="center" align="center">发表时间</div>
                <div field="status" width="60" headerAlign="center" align="center" allowSort="true" renderer="lwStatusRenderer">认定状态</div>
            </div>
        </div>
    </div>
    <div property="footer" style="padding:5px;height: 35px">
        <table style="width:100%;height: 100%">
            <tr>
                <td style="width:120px;text-align:center;">
                    <input type="button" style="height: 25px;width: 70px" value="确定" onclick="selectOutOK()"/>
                    <input type="button" style="height: 25px;width: 70px" value="取消" onclick="selectOutHide()"/>
                </td>
            </tr>
        </table>
    </div>
</div>

<div id="selectJsWindow" title="选择技术封装" class="mini-window" style="width:950px;height:550px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
    <div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px"
         borderStyle="border-left:0;border-top:0;border-right:0;">
        <span class="text" style="width:auto">技术编号: </span>
        <input class="mini-textbox" id="jsNum" name="jsNum">
        <span class="text" style="width:auto">技术名称: </span>
        <input class="mini-textbox" id="jsName" name="jsName"/>
        <span class="text" style="width:auto">依托项目: </span>
        <input class="mini-textbox" id="ytxm" name="ytxm"/>
        <a class="mini-button" plain="true" onclick="searchOutList()">查询</a>
    </div>
    <div class="mini-fit">
        <div id="jsListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onrowdblclick="onRowDblClick"
             idField="kjlwId" showColumnsMenu="false" sizeList="[20,50,100,200]" pageSize="20"
             allowAlternating="true" pagerButtons="#pagerButtons" url="${ctxPath}/jssj/core/config/getJssjkList.do?"
        >
            <div property="columns">
                <div type="checkcolumn" width="20"></div>
                <div field="jsNum"  width="100" headerAlign="center" align="center" allowSort="false" >技术编号</div>
                <div field="jsName"  sortField="jsName"  width="150" headerAlign="center" align="center" allowSort="true">技术名称</div>
                <div field="ytxm" sortField="ytxm"  width="100" headerAlign="center" align="center" allowSort="false">依托项目</div>
                <div field="jsfzrName" align="center" width="60" headerAlign="center" allowSort="false">技术负责人</div>
                <div field="deptName" align="center" width="80" headerAlign="center" allowSort="false">申请部门</div>
                <div field="jdTime" align="center" width="60" headerAlign="center" allowSort="false">鉴定日期</div>
            </div>
        </div>
    </div>
    <div property="footer" style="padding:5px;height: 35px">
        <table style="width:100%;height: 100%">
            <tr>
                <td style="width:120px;text-align:center;">
                    <input type="button" style="height: 25px;width: 70px" value="确定" onclick="selectOutOK()"/>
                    <input type="button" style="height: 25px;width: 70px" value="取消" onclick="selectOutHide()"/>
                </td>
            </tr>
        </table>
    </div>
</div>
<div id="selectFMEAWindow" title="选择FMEA" class="mini-window" style="width:950px;height:550px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
    <div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px"
         borderStyle="border-left:0;border-top:0;border-right:0;">
        <span class="text" style="width:auto">设计型号: </span>
        <input class="mini-textbox" id="jixing" name="jixing" />
        <span class="text" style="width:auto">风险分析总项目名称: </span>
        <input class="mini-textbox" id="analyseName" name="analyseName" />
        <span class="text" style="width:auto">创建人: </span>
        <input id="creator" name="creator" class="mini-textbox"/>
        <a class="mini-button" plain="true" onclick="searchOutList()">查询</a>
    </div>
    <div class="mini-fit">
        <div id="fmeaListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onlyCheckSelection="true"
             url="${ctxPath}/drbfm/total/getTotalList.do" idField="id"
             multiSelect="false" showColumnsMenu="true" sizeList="[50,100,200]" pageSize="50" allowAlternating="true" pagerButtons="#pagerButtons">
            <div property="columns">
                <div type="checkcolumn" width="20"></div>
                <div field="jixing"   width="80" headerAlign="center" align="center">设计型号名称</div>
                <div field="femaType"   width="80" headerAlign="center" align="center" renderer="fmeaRenderer">FMEA类型</div>
                <div field="analyseName"  width="180" headerAlign="center" align="center" allowSort="false">风险分析总项目名称</div>
                <div field="creator" width="70" headerAlign="center" align="center" >创建人</div>
                <div field="department" width="70" headerAlign="center" align="center" >所属部门</div>
                <div field="CREATE_TIME_" width="70" headerAlign="center" dateFormat="yyyy-MM-dd" align="center">创建时间</div>
            </div>
        </div>
    </div>
    <div property="footer" style="padding:5px;height: 35px">
        <table style="width:100%;height: 100%">
            <tr>
                <td style="width:120px;text-align:center;">
                    <input type="button" style="height: 25px;width: 70px" value="确定" onclick="selectOutOK()"/>
                    <input type="button" style="height: 25px;width: 70px" value="取消" onclick="selectOutHide()"/>
                </td>
            </tr>
        </table>
    </div>
</div>
<script type="text/javascript">
    var jsUseCtxPath = "${ctxPath}";
    mini.parse();
    var grid_project_out = mini.get("#grid_project_out");

    var selectStandardWindow = mini.get("selectStandardWindow");
    var standardListGrid = mini.get("standardListGrid");
    var selectZlWindow = mini.get("selectZlWindow");
    var zlListGrid = mini.get("zlListGrid");
    var selectGjZlWindow = mini.get("selectGjZlWindow");
    var gjZlListGrid = mini.get("gjZlListGrid");
    var selectRjzzWindow = mini.get("selectRjzzWindow");
    var rjzzListGrid = mini.get("rjzzListGrid");
    var selectLwWindow = mini.get("selectLwWindow");
    var lwListGrid = mini.get("lwListGrid");
    var selectJsWindow = mini.get("selectJsWindow");
    var jsListGrid = mini.get("jsListGrid");
    var selectFMEAWindow = mini.get("selectFMEAWindow");
    var fmeaListGrid = mini.get("fmeaListGrid");

    var currentUserId = "${currentUserId}";
    var outPlanId = "${outPlanId}";
    var projectId = "${projectId}";
    var canOperateFile = "${canOperateFile}";
    var typeName = "${typeName}";
    var belongSubSysKey = "${belongSubSysKey}";
    const fema = new Map([['product','产品FMEA'],['base','基础FMEA']]);

    function outOperateRender(e) {
        var record = e.record;
        var outUrl = record.outUrl;
        if (!outUrl) {
            outUrl = '';
        }
        var s = '<a href="#" style="color:#44cef6;text-decoration:underline;" onclick="detailOut(\'' + outUrl + '\')">查看</a>';
        return s;
    }

    function standardStatusRenderer(e) {
        var record = e.record;
        var status = record.standardStatus;

        var arr = [{'key': 'draft', 'value': '草稿', 'css': 'orange'},
            {'key': 'enable', 'value': '有效', 'css': 'green'},
            {'key': 'disable', 'value': '已废止', 'css': 'red'}
        ];

        return $.formatItemValue(arr, status);
    }

    function rjzzStatusRenderer(e) {
        var record = e.record;
        var status = record.status;

        var arr = [{'key': 'DRAFTED', 'value': '草稿', 'css': 'orange'},
            {'key': 'RUNNING', 'value': '认定中', 'css': 'green'},
            {'key': 'SUCCESS_END', 'value': '通过认定', 'css': 'blue'},
            {'key': 'DISCARD_END', 'value': '未通过认定', 'css': 'red'}
        ];

        return $.formatItemValue(arr, status);
    }
    //todo:增加软件著作的新渲染(老李mark暂留)
    function rjzzZpsmRenderer(e) {
        var record = e.record;
        var status = record.zpsm;

        var arr = [{'key': 'YC', 'value': '原创'}, {'key': 'XG', 'value': '修改'}];

        return $.formatItemValue(arr, status);
    }
    function rjzzFbztRenderer(e) {
        var record = e.record;
        var status = record.fbzt;

        var arr = [{'key': 'YFB', 'value': '已发表'}, {'key': 'WFB', 'value': '未发表'}];

        return $.formatItemValue(arr, status);
    }

    function lwStatusRenderer(e) {
        var record = e.record;
        var status = record.status;

        var arr = [{'key': 'DRAFTED', 'value': '草稿', 'css': 'orange'},
            {'key': 'RUNNING', 'value': '认定中', 'css': 'green'},
            {'key': 'SUCCESS_END', 'value': '通过认定', 'css': 'blue'},
            {'key': 'DISCARD_END', 'value': '未通过认定', 'css': 'red'}
        ];

        return $.formatItemValue(arr, status);
    }

    function fmeaRenderer(e) {
        var record = e.record
        return fema.get(record.femaType);
    }

</script>
</body>
</html>