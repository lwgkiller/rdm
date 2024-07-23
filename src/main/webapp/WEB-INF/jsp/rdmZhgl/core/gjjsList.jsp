<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2021/4/12
  Time: 16:55
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>关键技术</title>
    <%@include file="/commons/list.jsp"%>
    <script src="${ctxPath}/scripts/rdmZhgl/gjjsList.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
    <div class="mini-toolbar" >
        <div class="searchBox">
            <form id="searchForm" class="search-form" style="margin-bottom: 25px">
                <ul >
                    <li style="margin-right: 14px">
                        <span class="text" style="width:auto">项目名称: </span>
                        <input class="mini-textbox" id="proName" name="proName">
                        <input class="mini-textbox" style="display: none">
                    </li>
                    <li style="margin-right: 14px">
                        <span class="text" style="width:auto">项目经理: </span>
                        <input class="mini-textbox" id="proRespUserName" name="proRespUserName">
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">技术水平: </span>
                        <input id="jssp" name="jssp" class="mini-combobox" style="width:150px;"
                               textField="value" valueField="key" emptyText="请选择..." onvaluechanged="searchFrm"
                               required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                               data="[{'key' : '国际先进','value' : '国际先进'},{'key' : '国际领先','value' : '国际领先'},{'key' : '国内领先','value' : '国内领先'}]"
                        />
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">鉴定时间 从 </span>:<input id="jdTimeStart"
                                                                                    name="jdTimeStart"
                                                                                    class="mini-datepicker"
                                                                                    format="yyyy-MM-dd"
                                                                                    style="width:120px"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text-to" style="width:auto">至: </span><input id="jdTimeEnd" name="jdTimeEnd"
                                                                                  class="mini-datepicker"
                                                                                  format="yyyy-MM-dd"
                                                                                  style="width:120px"/>
                    </li>
                    <li style="margin-left: 10px">
                        <a class="mini-button" style="margin-right: 5px"  plain="true" onclick="searchFrm()">查询</a>
                        <a class="mini-button btn-red" style="margin-right: 5px"  plain="true" onclick="clearForm()">清空查询</a>
                        <div style="display: inline-block" class="separator"></div>
                        <a id="addNewgjjs" class="mini-button" style="margin-right: 5px" plain="true" onclick="addNewgjjs()">新增</a>
                        <a id="removeGjjs" class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="removeGjjs()">删除</a>
                    </li>
                </ul>
            </form>
        </div>
    </div>
    <div class="mini-fit" style="height: 100%;">
        <div id="gjjsListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="true" onlyCheckSelection="true"
             url="${ctxPath}/zhgl/core/gjjs/queryListData.do" idField="id"
             multiSelect="true" showColumnsMenu="true" sizeList="[50,100,200]" pageSize="50" allowAlternating="true" pagerButtons="#pagerButtons">
            <div property="columns">
                <div type="checkcolumn" field="pId" name="pId" width="20"></div>
                <div  field="czId" name="czId" cellCls="actionIcons" width="60" headerAlign="center" align="center" renderer="onActionRenderer" cellStyle="padding:0;">操作</div>
                <div field="jdTime" name="jdTime" sortField="jdTime" width="80" headerAlign="center" align="center" allowSort="false">鉴定时间</div>
                <div field="proName" name="proName" sortField="proName"  width="120" headerAlign="center" align="center" allowSort="false">项目名称</div>
                <div field="proRespUserName" name="proRespUserName"  width="70" headerAlign="center" align="center" allowSort="false">项目经理</div>
                <div field="jsName" align="center" width="150" headerAlign="center" allowSort="false">技术名称</div>
                <div field="jssp" sortField="patentName" width="150" headerAlign="center" align="center" allowSort="false">技术水平</div>
                <div field="beizhu"  width="150" headerAlign="center" align="center" allowSort="false">备注</div>
            </div>
        </div>
    </div>
    <script type="text/javascript">
        mini.parse();
        var gjjsPath="${ctxPath}";
        var gjjsListGrid=mini.get("gjjsListGrid");
        var currentUserId="${currentUserId}";
        var currentUserNo="${currentUserNo}";
        //必须为要合并的列增加name属性
        gjjsListGrid.on("load", function () {
            gjjsListGrid.mergeColumns(["pId","czId","jdTime","proName","proRespUserName"]);
        });

        function onActionRenderer(e) {
            var record = e.record;
            var pId = record.pId;
            var s = '';
            s+= '<span  title="明细" onclick="gjjsDetail(\'' + pId +'\''+ ')">明细</span>'
                +'<span  title="编辑" onclick="gjjsEdit(\'' + pId +'\''+ ')">编辑</span>';

            return s;
        }
    </script>
    <redxun:gridScript gridId="gjjsListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl="" />
</body>
</html>
