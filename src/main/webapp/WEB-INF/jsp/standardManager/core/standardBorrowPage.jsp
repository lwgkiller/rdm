<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>标准借用管理</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/standardManager/standardBorrow.js?version=${static_res_version}"
            type="text/javascript"></script>
    <script src="${ctxPath}/scripts/standardManager/standardUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
</head>
<body>
    <div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 15px"
         borderStyle="border-left:0;border-top:0;border-right:0;">
        <span style="font-size: 14px;color: #777;margin-left:15px;">借用到标准体系类别: </span>
        <input id="standardBorrowSystemCategory" class="mini-combobox" style="width:130px;margin-right: 15px"
               textField="systemCategoryName" valueField="systemCategoryId"
               required="false" allowInput="false" onvaluechanged="changeBorrowSystemCategory()"/>
        <span style="font-size: 14px;color: #777;margin-left:15px;">借用到标准体系: </span>
        <input id="systemTreeSelectId" style="width:170px;margin-right: 15px" class="mini-buttonedit" name="systemId" textname="systemName" allowInput="false" onbuttonclick="selectSystem()"/>
        <a class="mini-button"  plain="true" onclick="addBorrow()">新增借用</a>
        <a class="mini-button"  plain="true" onclick="searchBorrowList()">刷新</a>
    </div>
    <div class="mini-fit">
        <div id="standardBorrowListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
             idField="id" multiSelect="false" showColumnsMenu="false" url="${ctxPath}/standardManager/core/standard/borrowList.do?standardFromId=${standardFromId}"
             allowAlternating="true" showPager="false">
            <div property="columns">
                <div cellCls="actionIcons" width="70" headerAlign="center" align="center" renderer="borrowOperateRenderer"
                     cellStyle="padding:0;">操作
                </div>
                <div field="standardName" width="170" headerAlign="center" align="center"
                     allowSort="true">标准名称
                </div>
                <div field="standardNumber" width="130" headerAlign="center" align="center"
                     align="center" allowSort="true">标准编号
                </div>
                <div field="systemCategoryName" width="90" headerAlign="center" align="center"
                     allowSort="true">借用到标准体系类别
                </div>
                <div field="systemName" width="150" headerAlign="center" align="center"
                     allowSort="true">借用到标准体系
                </div>
            </div>
        </div>
    </div>

    <div id="selectSystemWindow" title="选择所属体系" class="mini-window" style="width:750px;height:450px;"
         showModal="true" showFooter="true" allowResize="true">
        <div class="mini-toolbar" style="text-align:center;line-height:30px;"
             borderStyle="border-left:0;border-top:0;border-right:0;">
            <span style="font-size: 14px;color: #777">名称: </span>
            <input class="mini-textbox" width="130" id="filterSystemNameId" onenter="filterSystemTree()"/>
            <a class="mini-button" iconCls="icon-search" plain="true" onclick="filterSystemTree()"></a>
        </div>
        <div class="mini-fit">
            <ul id="selectSystemTree" class="mini-tree" style="width:100%;height:100%;"
                showTreeIcon="true" textField="systemName" idField="id" expandOnLoad="0" parentField="parentId" resultAsTree="false">
            </ul>
        </div>
        <div property="footer" style="padding:5px;height: 40px">
            <table style="width:100%;height: 100%">
                <tr>
                    <td style="width:120px;text-align:center;">
                        <input type="button" style="height: 25px;width: 70px" value="确定" onclick="selectSystemOK()"/>
                        <input type="button" style="height: 25px;width: 70px" value="取消" onclick="selectSystemHide()"/>
                    </td>
                </tr>
            </table>
        </div>
    </div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var standardFromId="${standardFromId}";
    var systemCategoryArray=${systemCategoryArray};
    var borrowStandardArray=${borrowStandardArray};

    var selectSystemWindow=mini.get("selectSystemWindow");
    var selectSystemTree=mini.get("selectSystemTree");
    var standardBorrowListGrid = mini.get("standardBorrowListGrid");
    //操作栏
    standardBorrowListGrid.on("update",function(e){
        handGridButtons(e.sender.el);
    });

    function borrowOperateRenderer(e) {
        var record = e.record;
        var standardToId = record.id;
        var s='<span  title="收回" onclick="callBack(\'' + standardToId +'\')">收回</span>';
        return s;
    }

</script>
</body>
</html>