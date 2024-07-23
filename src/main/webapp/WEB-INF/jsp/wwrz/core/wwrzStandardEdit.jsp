<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>标准结构化</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/wwrz/wwrzStandardEdit.js?version=${static_res_version}"
            type="text/javascript"></script>
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
    <div class="form-container">
        <form id="infoForm" method="post">
            <input id="id" name="id" class="mini-hidden"/>
            <table cellspacing="1" cellpadding="0" class="table-detail column-four grey">
                <caption>
                    标准结构化
                </caption>
                <tbody>
                <tr class="firstRow displayTr">
                    <td align="center"></td>
                    <td align="left"></td>
                    <td align="center"></td>
                    <td align="left"></td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        标准标号<span style="color: #ff0000">*</span>：
                    </td>
                    <td align="center" colspan="3" rowspan="1">
                        <input name="standardNumber" required class="mini-textbox rxc" style="width:100%;height:34px">
                    </td>
                </tr>
                <tr>
                    <td align="center" style="white-space: nowrap;">
                        标准名称<span style="color: #ff0000">*</span>：
                    </td>
                    <td align="center" colspan="3" rowspan="1">
                        <input name="standardName" required class="mini-textbox rxc" style="width:100%;height:34px">
                    </td>
                </tr>
                </tbody>
            </table>
            <fieldset id="fdAchievementInfo" class="hideFieldset" >
                <legend>
                    <label style="font-size:17px">
                        标准章节
                    </label>
                </legend>
                <div class="fieldset-body" style="margin: 10px 10px 10px 10px">
                    <div class="mini-toolbar" id="projectAchievementButtons">
                        <a class="mini-button" id="addSectorButton"   plain="true" onclick="addSector()">添加</a>
                        <a class="mini-button btn-red" id="delProductButton"  plain="true" onclick="delSector()">删除</a>
                        <span style="color: red">注：章节信息添加删除后需要保存，保存后添加章节附件</span>
                    </div>
                    <div id="grid_sector" class="mini-datagrid"  allowResize="false" style="margin-top: 5px"
                         idField="id" allowCellEdit="true" allowCellSelect="true" allowSortColumn="false"
                         multiSelect="true" showColumnsMenu="false"  showPager="false" allowCellWrap="true" showVGridLines="true">
                        <div property="columns">
                            <div type="checkcolumn" width="10"></div>
                            <div type="indexcolumn" headerAlign="center" align="center"  width="15">序号</div>
                            <div field="id" align="center"  width="1"  headerAlign="left" visible="false">id</div>
                            <div field="standardCode"  headerAlign="center" align="center" width="100">章节
                                <input property="editor" class="mini-textbox" />
                            </div>
                            <div field="remark"  headerAlign="center" align="center" width="100">备注说明
                                <input property="editor" class="mini-textbox" />
                            </div>
                            <div  field="sectorAttach"  renderer="sectorAttach"  align="center" width="100" headerAlign="center">
                                内容附件
                            </div>
                        </div>
                    </div>
                </div>
            </fieldset>
        </form>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var applyObj = ${applyObj};
    var action = '${action}';
    var infoForm = new mini.Form("#infoForm");
    var grid_sector = mini.get("#grid_sector");
</script>
</body>
</html>
