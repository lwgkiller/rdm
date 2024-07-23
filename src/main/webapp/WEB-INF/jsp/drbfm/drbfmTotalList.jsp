
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html >
<head>
    <title>总项目列表管理</title>
    <%@include file="/commons/list.jsp"%>
    <script src="${ctxPath}/scripts/drbfm/totalList.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar" >
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul >
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">设计型号: </span>
                    <input class="mini-textbox" id="jixing" name="jixing" />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">风险分析总项目名称: </span>
                    <input class="mini-textbox" id="analyseName" name="analyseName" />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">创建人: </span>
                    <input id="creator" name="creator" class="mini-textbox"/>
                </li>

                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px"  plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px"  plain="true" onclick="clearForm()">清空查询</a>
                    <div style="display: inline-block" class="separator"></div>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="addTotal()">创建总项目</a>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="copyTotal()">复制总项目</a>
                </li>
            </ul>
        </form>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="totalListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onlyCheckSelection="true"
         url="${ctxPath}/drbfm/total/getTotalList.do" idField="id"
         multiSelect="false" showColumnsMenu="true" sizeList="[50,100,200]" pageSize="50" allowAlternating="true" pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div name="action" cellCls="actionIcons" width="100" headerAlign="center" align="center" renderer="onActionRenderer" cellStyle="padding:0;">操作</div>
            <div field="jixing"   width="80" headerAlign="center" align="center">设计型号名称</div>
            <div field="femaType"   width="80" headerAlign="center" align="center" renderer="fmeaRenderer">FMEA类型</div>
            <div field="analyseName"  width="180" headerAlign="center" align="center" allowSort="false">风险分析总项目名称</div>
            <div field="creator" width="70" headerAlign="center" align="center" >创建人</div>
            <div field="department" width="70" headerAlign="center" align="center" >所属部门</div>
            <div field="CREATE_TIME_" width="70" headerAlign="center" dateFormat="yyyy-MM-dd" align="center">创建时间</div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var totalListGrid=mini.get("totalListGrid");
    var currentUserId="${currentUserId}";
    var currentUserNo="${currentUserNo}";
    const fema = new Map([['product','产品FMEA'],['base','基础FMEA']]);


    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var applyId = record.id;
        var createBy=record.CREATE_BY_;
        var submitFlag = "";
        if (currentUserNo != 'admin' && createBy != currentUserId) {
            submitFlag = ",'false'";
        }
        var s = '<span  title="查看" onclick="totalDecompose(\'' + applyId +'\',\'detail\')">查看</span>';
        s+='<span  title="编辑" onclick="totalDecompose(\'' + applyId +'\',\'edit\''+submitFlag+')">编辑</span>';
        if(createBy==currentUserId || currentUserNo=='admin') {
            s+='<span  title="删除" onclick="removeTotal(\''+applyId+'\')">删除</span>';
        }
        return s;
    }

    function fmeaRenderer(e) {
        var record = e.record
        return fema.get(record.femaType);
    }

</script>
<redxun:gridScript gridId="totalListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl="" />
</body>
</html>