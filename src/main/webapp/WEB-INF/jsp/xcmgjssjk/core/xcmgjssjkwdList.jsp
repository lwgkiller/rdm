<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2021/4/6
  Time: 9:59
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>我的技术库</title>
    <%@include file="/commons/list.jsp"%>
    <script src="${ctxPath}/scripts/xcmgjssjk/xcmgjssjkList.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
    <div class="mini-toolbar" >
        <div class="searchBox">
            <form id="searchForm" class="search-form" style="margin-bottom: 25px">
                <ul >
                    <li style="margin-right: 14px">
                        <span class="text" style="width:auto">技术编号: </span>
                        <input class="mini-textbox" id="jsNum" name="jsNum">
                    </li>
                    <li style="margin-right: 14px">
                        <span class="text" style="width:auto">技术名称: </span>
                        <input class="mini-textbox" id="jsName" name="jsName" />
                    </li>
                    <li style="margin-right: 14px">
                        <span class="text" style="width:auto">项目编号: </span>
                        <input class="mini-textbox" id="xmNum" name="xmNum" />
                    </li>
                    <li style="margin-right: 14px"><span class="text" style="width:auto">技术类别: </span>
                        <input id="jslb" name="jslb" class="mini-combobox" style="width:120px;"
                               textField="value" valueField="key" emptyText="请选择..." onvaluechanged="searchFrm"
                               required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                               data="[{'key' : 'XTJS','value' : '系统技术'},{'key' : 'LBJJS','value' : '零部件技术'},
                                   {'key' : 'ZCJS','value' : '支撑技术'}]"
                        />
                    </li>
                    <li style="margin-right: 15px"><span class="text" style="width:auto">申请部门:</span>
                        <input id="deptId" name="deptId" class="mini-dep rxc" plugins="mini-dep"
                               data-options="{'single':'true','config':{'type':'','grouplevel':'','groupid':'','groupidText':''}}"
                               style="width:160px;height:34px"
                               allowinput="false" textname="deptName" length="500" maxlength="500" minlen="0" single="true" required="false" initlogindep="false"
                               mwidth="160" wunit="px" mheight="34" hunit="px"/>
                    </li>
                    <li style="margin-left: 10px">
                        <a class="mini-button" style="margin-right: 5px"  plain="true" onclick="searchFrm()">查询</a>
                        <a class="mini-button btn-red" style="margin-right: 5px"  plain="true" onclick="clearForm()">清空查询</a>
                        <div style="display: inline-block" class="separator"></div>
                        <a id="addJssjk" class="mini-button" style="margin-right: 5px" plain="true" onclick="updateJssjk()">修改技术</a>
                        <a id="removeJssjk" class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="removeJssjk()">删除技术</a>
                    </li>
                </ul>
            </form>
        </div>
    </div>
    <div class="mini-fit" style="height: 100%;">
        <div id="jssjkListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onlyCheckSelection="true"
             url="${ctxPath}/jssj/core/config/getJssjkList.do?type=${type}" idField="jssjkId"
             multiSelect="false" showColumnsMenu="true" sizeList="[50,100,200]" pageSize="50" allowAlternating="true" pagerButtons="#pagerButtons">
            <div property="columns">
                <div type="checkcolumn" width="20"></div>
                <div name="action" cellCls="actionIcons" width="60" headerAlign="center" align="center" renderer="onActionRenderer" cellStyle="padding:0;">操作</div>
                <div field="jsNum"  width="100" headerAlign="center" align="center" allowSort="false" >技术编号</div>
                <div field="jsName"  sortField="jsName"  width="150" headerAlign="center" align="center" allowSort="true">技术名称</div>
                <div field="ytxm" sortField="ytxm"  width="100" headerAlign="center" align="center" allowSort="false">依托项目</div>
                <div field="jsfzrName" align="center" width="60" headerAlign="center" allowSort="false">技术负责人</div>
                <div field="deptName" align="center" width="80" headerAlign="center" allowSort="false">申请部门</div>
                <div field="jdTime" align="center" width="60" headerAlign="center" allowSort="false">鉴定日期</div>
                <div field="CREATE_TIME_" sortField="CREATE_TIME_" align="center" width="60" headerAlign="center" allowSort="false">申请时间</div>
                <div field="jslb"  width="80" headerAlign="center" align="center" allowSort="false" renderer="onStatusRenderer1">技术类别</div>
                <div field="jsfx"  width="60" headerAlign="center" align="center" allowSort="false" renderer="onStatusRenderer2">专业方向</div>
                <div field="yffx"  width="70" headerAlign="center" align="center" allowSort="false"renderer="onStatusRenderer3" >研发方向</div>
            </div>
        </div>
    </div>
    <script type="text/javascript">
        mini.parse();
        var jsUseCtxPath="${ctxPath}";
        var jssjkListGrid=mini.get("jssjkListGrid");
        var type="${type}";
        var currentUserId="${currentUserId}";
        var currentUserNo="${currentUserNo}";
        <%--var isJsglbRespUser=${isJsglbRespUser};--%>
        //行功能按钮
        function onActionRenderer(e) {
            var record = e.record;
            var jssjkId = record.jssjkId;
            var s = '';
            if(type =='my'){
                 s+= '<span  title="明细" onclick="jssjkDetail(\'' + jssjkId +'\''+ ')">明细</span>';
            }
            if (type =='all'){
                s+= '<span  title="明细" onclick="jssjkDetail(\'' + jssjkId +'\''+ ')">明细</span>';
            }
            return s;
        }


        function onStatusRenderer1(e) {
            var record = e.record;
            var jslb = record.jslb;

            var arr = [ {'key' : 'XTJS','value' : '系统技术'},{'key' : 'LBJJS','value' : '零部件技术'},
                {'key' : 'ZCJS','value' : '支撑技术'}
            ];
            return $.formatItemValue(arr,jslb);
        }
        function onStatusRenderer2(e) {
            var record = e.record;
            var jsfx = record.jsfx;

            var arr = [ {'key' : 'YY','value' : '液压'},{'key' : 'JG','value' : '结构'},{'key' : 'DL','value' : '动力'}
                ,{'key' : 'KZ','value' : '控制'},{'key' : 'DQ','value' : '电气'},{'key' : 'FZ','value' : '仿真'}
                ,{'key' : 'CS','value' : '测试'}
            ];
            return $.formatItemValue(arr,jsfx);
        }
        function onStatusRenderer3(e) {
            var record = e.record;
            var yffx = record.yffx;

            var arr = [ {'key' : 'JNJS','value' : '节能技术'},{'key' : 'KKXJS','value' : '可靠性技术'},{'key' : 'LBJYF','value' : '零部件研发'}
                ,{'key' : 'QLH','value' : '轻量化'},{'key' : 'YYJS','value' : '预研技术'},{'key' : 'RXSS','value' : '人性化舒适化'}
                ,{'key' : 'ZNHJS','value' : '智能化技术'},{'key' : 'QT','value' : '其他'}
            ];
            return $.formatItemValue(arr,yffx);
        }
    </script>
    <redxun:gridScript gridId="jssjkListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl="" />
</body>
</html>
