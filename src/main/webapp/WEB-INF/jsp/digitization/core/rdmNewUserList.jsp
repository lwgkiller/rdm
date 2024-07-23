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
    <%@include file="/commons/list.jsp"%>
</head>
<body>
    <div class="mini-toolbar" >
        <div class="searchBox">
            <form id="searchForm" class="search-form" style="margin-bottom: 25px">
                <ul >
                    <li style="margin-left: 10px">
                        <a class="mini-button" style="margin-right: 5px"  plain="true" onclick="searchFrm()">查询</a>
                    </li>
                    <li style="margin-left: 10px">
                        <a class="mini-button" style="margin-right: 5px"  plain="true" onclick="confirmUser()">确认</a>
                    </li>
                    <li style="margin-left: 10px">
                        <a class="mini-button btn-red" style="margin-right: 5px"  plain="true" onclick="deleteUser()">删除</a>
                    </li>
                </ul>
            </form>
        </div>
    </div>
    <div class="mini-fit" style="height: 100%;">
        <div id="userListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" showPager="false"
             url="${ctxPath}/rdm/core/newUserList.do" idField="id" multiSelect="true" allowAlternating="true" >
            <div property="columns">
                <div type="checkcolumn" headerAlign="center" align="center" width="20"></div>
                <div field="confirmStatus" headerAlign="center" align="center" renderer="statusRender">状态</div>
                <div field="fullname" headerAlign="center" align="center">姓名</div>
                <div field="userNo" headerAlign="center" align="center">账号</div>
                <div field="mainDepName" headerAlign="center" align="center">部门</div>
                <div field="gwName" headerAlign="center" align="center">岗位</div>
                <div field="zjName" headerAlign="center" align="center">职级</div>
                <div field="certNo" headerAlign="center" align="center">身份证号</div>
                <div field="mobile" headerAlign="center" align="center">手机号</div>
                <div field="windchillPDM" headerAlign="center" align="center">PDM账号</div>
                <div field="szrUserName" headerAlign="center" align="center">室主任姓名</div>
            </div>
        </div>
    </div>
    <script type="text/javascript">
        mini.parse();
        var jsUseCtxPath="${ctxPath}";
        var userListGrid=mini.get("userListGrid");

        $(function () {
            searchFrm();
        });

        function statusRender(e) {
            var record=e.record;
            if(record.confirmStatus && record.confirmStatus=='yes') {
                return "<span class='green'>已确认</span>";
            } else {
                return "<span class='red'>未确认</span>";
            }
        }

        function confirmUser() {
            var selectRows=userListGrid.getSelecteds();
            if(!selectRows || selectRows.length<=0) {
                mini.alert("请选择数据！");
                return;
            }
            var postData={ids:[]};
            for(var index=0;index<selectRows.length;index++) {
                var confirmStatus=selectRows[index].confirmStatus;
                if(confirmStatus=='no') {
                    postData.ids.push(selectRows[index].id);
                }
            }
            if(postData.ids.length==0) {
                mini.alert("请选择未确认的数据！");
                return;
            }
            $.ajax({
                url: '${ctxPath}/rdm/core/newUserConfirm.do',
                type: 'POST',
                data:mini.encode(postData),
                contentType: 'application/json',
                success: function (returnData) {
                    if(returnData && !returnData.success) {
                        mini.alert(returnData.message);
                    } else {
                        mini.alert("创建成功！");
                        searchFrm();
                    }
                }
            });
        }
        function deleteUser(){
            var selectRows=userListGrid.getSelecteds();
            if(!selectRows || selectRows.length<=0) {
                mini.alert("请至少选中一条记录！");
                return;
            }
            mini.confirm("确定删除选中记录？", "提示", function (action){
                if (action != 'ok') {
                    return;
                } else {
                    var rowIds = [];
                    for (var i = 0, l = selectRows.length; i < l; i++) {
                        var r = selectRows[i];
                        rowIds.push(r.id);
                    }
                    _SubmitJson({
                        url: '${ctxPath}/rdm/core/deleteUser.do',
                        method: 'POST',
                        data: {ids: rowIds.join(',')},
                        success: function (data) {
                            if (data) {
                                searchFrm();
                            }
                        }
                    });
                }
            });
        }

    </script>
    <redxun:gridScript gridId="userListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl="" />
</body>
</html>
