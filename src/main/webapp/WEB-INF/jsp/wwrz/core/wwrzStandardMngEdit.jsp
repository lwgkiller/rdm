<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>标准维护</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
</head>
<body>
<div id="ToolBar" class="topToolBar" style="display: block">
    <div>
        <a id="save" class="mini-button" img="${ctxPath}/scripts/mini/miniui/res/images/add.png"
           onclick="saveData()">保存</a>
        <a id="closeWindow" class="mini-button" img="${ctxPath}/scripts/mini/miniui/res/images/system_close.gif"
           onclick="CloseWindow()">关闭</a>
    </div>
</div>
<div class="mini-fit">
    <div class="form-container" style="margin:0 auto; width: 100%;">
        <form id="dataForm" method="post">
            <input id="id" name="id" class="mini-hidden"/>
            <input id="standardType" name="standardType" class="mini-hidden"/>
            <table cellspacing="1" cellpadding="0" class="table-detail column-four grey">
                <caption>
                    标准信息维护
                </caption>
                <tbody>
                <tr class="firstRow displayTr">
                    <td align="center"></td>
                    <td align="left"></td>
                    <td align="center"></td>
                    <td align="left"></td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">标准编号<span style="color: #ff0000">*</span>：</td>
                    <td align="center" rowspan="1" colspan="3">
                        <input id="standardId" style="width:98%;" class="mini-buttonedit" showClose="true"
                               name="standardId" textname="standardNumber" allowInput="false" onbuttonclick="selectStandardName()"/>
                    </td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        标准名称：
                    </td>
                    <td align="center" rowspan="1" colspan="3">
                        <input id="standardName"  name="standardName" readonly class="mini-textbox" style="width:100%;"/>
                    </td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        排序号：
                    </td>
                    <td align="center" rowspan="1" colspan="3">
                        <input id="indexSort"  name="indexSort"  class="mini-spinner"   allowLimitValue="false" style="width:100%;"/>
                    </td>
                </tr>
                </tbody>
            </table>
        </form>
    </div>
    <div id="selectStandardWindow" title="选择标准" class="mini-window" style="width:800px;height:450px;"
         showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
        <div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px"
             borderStyle="border-left:0;border-top:0;border-right:0;">
            <span class="text" style="width:auto">标准标号: </span><input class="mini-textbox" style="width: 120px" name="searchStandardNumber" id="searchStandardNumber"/>
            <span class="text" style="width:auto">标准名称: </span><input class="mini-textbox" style="width: 120px" name="searchStandardName" id="searchStandardName"/>
            <a class="mini-button" iconCls="icon-search" plain="true" onclick="searchStandardList()">查询</a>
        </div>
        <div class="mini-fit">
            <div id="standardListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false" onrowdblclick="onRowDblClick"
                 idField="id"  showColumnsMenu="false" sizeList="[5,10,20,50,100,200]" pageSize="5"
                 allowAlternating="true" pagerButtons="#pagerButtons" url="${ctxPath}/standardManager/core/standard/queryList.do">
                <div property="columns">
                    <div type="checkcolumn" width="30"></div>
                    <div field="categoryName" sortField="categoryName" width="80" headerAlign="center" align="center"
                         allowSort="true" hideable="true">标准类别
                    </div>
                    <div field="standardNumber" sortField="standardNumber" width="100" headerAlign="center"
                         align="center" allowSort="true" hideable="true">标准编号
                    </div>
                    <div field="standardName" sortField="standardName" width="200" headerAlign="center" align="left"
                         allowSort="true" hideable="true">标准名称
                    </div>
                    <div field="belongDepName" sortField="belongDepName" width="80" headerAlign="center" align="center"
                         allowSort="true">归口部门
                    </div>
                    <div field="standardStatus" sortField="standardStatus" width="60" headerAlign="center"
                         align="center" hideable="true"
                         allowSort="true" renderer="statusRenderer">状态
                    </div>
                </div>
            </div>
        </div>
        <div property="footer" style="padding:5px;height: 35px">
            <table style="width:100%;height: 100%">
                <tr>
                    <td style="width:120px;text-align:center;">
                        <input type="button" style="height: 25px;width: 70px" value="确定" onclick="selectStandardOK()"/>
                        <input type="button" style="height: 25px;width: 70px" value="取消" onclick="selectStandardHide()"/>
                    </td>
                </tr>
            </table>
        </div>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var applyObj = ${applyObj};
    var action = '${action}';
    var dataForm = new mini.Form("#dataForm");
    var selectStandardWindow=mini.get("selectStandardWindow");
    var standardListGrid=mini.get("standardListGrid");
    $(function () {
        dataForm.setData(applyObj);
    })

    function saveData() {
        dataForm.validate();
        if (!dataForm.isValid()) {
            return;
        }
        var formData = dataForm.getData();
        var config = {
            url: jsUseCtxPath + "/wwrz/core/standardMng/save.do",
            method: 'POST',
            data: formData,
            success: function (result) {
                //如果存在自定义的函数，则回调
                var result = mini.decode(result);
                if (result.success) {
                    CloseWindow('ok');
                } else {
                }
                ;
            }
        }
        _SubmitJson(config);
    }
    /**
     * 选择
     */
    function selectStandardName(){
        selectStandardWindow.show();
        searchStandardList();
    }
    function searchStandardList() {
        var queryParam = [];
        //其他筛选条件
        var standardNumber = $.trim(mini.get("searchStandardNumber").getValue());
        if (standardNumber) {
            queryParam.push({name: "standardNumber", value: standardNumber});
        }
        var standardName = $.trim(mini.get("searchStandardName").getValue());
        if (standardName) {
            queryParam.push({name: "standardName", value: standardName});
        }
        queryParam.push({name: "systemCategoryId", value: 'JS'});
        var data = {};
        data.filter = mini.encode(queryParam);
        data.pageIndex = standardListGrid.getPageIndex();
        data.pageSize = standardListGrid.getPageSize();
        data.sortField = standardListGrid.getSortField();
        data.sortOrder = standardListGrid.getSortOrder();
        //查询
        standardListGrid.load(data);
    }
    function selectStandardOK() {
        var selectRow = standardListGrid.getSelected();
        if (selectRow) {
            mini.get("standardId").setValue(selectRow.id);
            mini.get("standardId").setText(selectRow.standardNumber);
            mini.get("standardName").setValue(selectRow.standardName);
        }
        selectStandardHide();
    }

    function selectStandardHide() {
        selectStandardWindow.hide();
        mini.get("searchStandardNumber").setValue('');
        mini.get("searchStandardName").setValue('');
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


</script>
</body>
</html>
