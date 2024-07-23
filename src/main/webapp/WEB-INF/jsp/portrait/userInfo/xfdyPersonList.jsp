<%@ taglib prefix="redxun" uri="http://www.redxun.cn/gridFun" %>
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/common/list.js?static_res_version=${static_res_version}"
            type="text/javascript"></script>
    <style>

        .item
        {
            float: left;
            width: 270px;
            height: 330px;
            border: solid 1px #ccc;
            border-radius: 4px;
            margin-left: 8px;
            margin-top: 8px;
        }
        .item-inner
        {
            width:100%;
        }
        .item-image
        {
            height: 150px;
            width: 125px;
            margin: auto;
            margin-top: 5px;
            display: block;
        }
        .item-name
        {
            text-align:center;
            font-size:18px;
            font-family:"微软雅黑";
            letter-spacing:5px;
            font-weight:bold;
            padding-top:5px;
        }

        .item-action
        {
            text-align:right;
            padding-right:6px;
        }

        .button.add
        {
            display: inline-block;
            padding: 3px 8px;
            font-size: 13px;
            font-weight: normal;
            line-height: 1.4;
            text-align: center;
            white-space: nowrap;
            vertical-align: middle;
            cursor: pointer;
            border: 1px solid transparent;
            border-radius: 4px;
            color: #fff;
            background-color: #337ab7;
            border-color: #2e6da4;
        }
        .button.remove
        {
            display: inline-block;
            padding: 3px 8px;
            font-size: 13px;
            font-weight: normal;
            line-height: 1.4;
            text-align: center;
            color: #f56c6c;
            white-space: nowrap;
            vertical-align: middle;
            cursor: pointer;
            border: 1px solid transparent;
            border-radius: 4px;
            background-color: #fbc4c4;
            border-color: #fbc4c4;
        }
    </style>
</head>
<body>
<div style='height:100px;width:100%;background-image: url("${ctxPath}/styles/images/xfdy.jpg");background-size: cover'>
</div>
<div class="mini-toolbar" id="editToolBar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-top: 5px;margin-bottom: 10px">
            <ul>
                <li style="margin-right: 15px"><span class="text" style="width:auto">姓名: </span><input
                        class="mini-textbox" style="width: 150px" name="userName"/></li>
                <li style="margin-right: 15px"><span class="text" style="width:auto">所属部门: </span><input
                        class="mini-textbox" style="width: 150px" name="deptName"/></li>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    <div style="display: inline-block" class="separator"></div>
                    <a id="addPerson" class="mini-button" onclick="addInfo()" style="display: none">添加</a>
                </li>
            </ul>
        </form>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div class="mini-datagrid" id="xfdyListGrid" style="width: 100%; height: 100%;" allowResize="false"
         showHGridLines="false"
         url="${ctxPath}/person/core/getPerosn.do" autoLoad="true"
         idField="id" showPager="false" multiSelect="false" showColumns="false"
         viewType="cardview" itemRenderer="itemRenderer">
        <div property="columns">
        </div>
    </div>
</div>



<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var xfdyListGrid = mini.get("xfdyListGrid");
    var isXfdy =${isXfdy};
    if (isXfdy) {
        mini.get("addPerson").show();
    }

    $(function () {
        searchFrm();
    });

    function itemRenderer(record, rowIndex, meta) {
        meta.rowCls = "item";
        var html = '<div class="item-inner">'
            + '<img class="item-image" src="' + jsUseCtxPath + '/person/core/downloadShowFile.do?xfdyId=' + record.xfdyId + '"/>'
            + '<div class="item-name">' + record.applyName + '</div>'
            + '<div class="mini-textarea" style="width:98%;overflow: auto;height: 115px;text-align:left;font-size:15px;font-family:\'微软雅黑\'">' +'<span style="font-weight:bold">部门：</span>'+record.deptName+'<br>'
            +'<span style="font-weight:bold">简介：</span>'+record.info + '</div></div>'
            + '<div  style="text-align: right" class="item-action">'
        if (isXfdy) {
            html += '<button id="add" class="button add">编辑</button><button id="remove" class="button remove">删除</button>';
        } /*else {
            html += '<button id="detail" style="margin-top: 0px;" class="button detail">详情</button></div></div>';
        }*/
        html+='</div></div>';
        return html;
    }


    $(xfdyListGrid.el).on("click", ".add", function (event) {
        var record = xfdyListGrid.getRowByEvent(event);
        editInfo(record.xfdyId);
    });
    $(xfdyListGrid.el).on("click", ".detail", function (event) {
        var record = xfdyListGrid.getRowByEvent(event);
        detailInfo(record.xfdyId);
    });
    $(xfdyListGrid.el).on("click", ".remove", function (event) {
        var record = xfdyListGrid.getRowByEvent(event);
        removeInfo(record);
    });

    function addInfo() {
        mini.open({
            title: "新增",
            url: jsUseCtxPath + "/person/core/edit.do?action=add",
            width: 750,
            height: 500,
            allowResize: true,
            onload: function () {
                searchFrm();
            },
            ondestroy: function () {
                searchFrm();
            }
        });
    }

    function editInfo(xfdyId) {
        mini.open({
            title: "编辑",
            url: jsUseCtxPath + "/person/core/edit.do?action=edit&xfdyId=" + xfdyId,
            width: 750,
            height: 500,
            allowResize: true,
            onload: function () {
                searchFrm();
            },
            ondestroy: function () {
                searchFrm();
            }
        });
    }

    function detailInfo(xfdyId) {
        mini.open({
            title: "详情",
            url: jsUseCtxPath + "/person/core/edit.do?action=detail&xfdyId=" + xfdyId,
            width: 750,
            height: 500,
            allowResize: true,
            onload: function () {
                searchFrm();
            },
            ondestroy: function () {
                searchFrm();
            }
        });
    }

    function removeInfo(record) {
        var rows = [];
        if (record) {
            rows.push(record);
        } else {
            rows = xfdyListGrid.getSelecteds();
        }
        if (rows.length <= 0) {
            mini.alert("请至少选中一条记录");
            return;
        }
        mini.confirm("确定删除选中记录？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                var rowIds = [];
                for (var i = 0, l = rows.length; i < l; i++) {
                    var r = rows[i];
                    rowIds.push(r.xfdyId);
                }
                if (rowIds.length > 0) {
                    _SubmitJson({
                        url: jsUseCtxPath + "/person/core/deleteXfdyPerson.do",
                        method: 'POST',
                        showMsg: false,
                        data: {ids: rowIds.join(',')},
                        success: function (data) {
                            if (data) {
                                mini.alert(data.message);
                                searchFrm();
                            }
                        }
                    });
                }
            }
        });
    }
</script>
<redxun:gridScript gridId="xfdyListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
