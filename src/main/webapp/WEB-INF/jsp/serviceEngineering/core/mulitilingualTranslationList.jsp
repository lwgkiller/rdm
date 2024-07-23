<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>零部件词汇查询</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/serviceEngineering/mulitilingualTranslationList.js?version=${static_res_version}"
            type="text/javascript"></script>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css" />
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">中文名称: </span>
                    <input class="mini-textbox" id="materialName" name="materialName">
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">多语言选择: </span>
                </li>
                <li style="margin-right: 15px">
                    <input id="languageId" name="languageId" class="mini-checkboxlist" style="width:auto;"
                           repeatItems="13" repeatLayout="table" repeatDirection="horizontal"
                           textfield="languageName" valuefield="languageId" value="english"
                           data="[ {'languageName' : '英文','languageId' : 'english'},{'languageName' : '俄文','languageId' : 'russian'},
                           {'languageName' : '葡文','languageId' : 'portuguese'},{'languageName' : '德文','languageId' : 'germany'},
                           {'languageName' : '西文','languageId' : 'spanish'},{'languageName' : '法文','languageId' : 'french'},
                           {'languageName' : '意文','languageId' : 'italian'},{'languageName' : '波兰语','languageId' : 'polish'},
                           {'languageName' : '土耳其语','languageId' : 'turkish'},{'languageName' : '瑞典语','languageId' : 'swedish'},
                           {'languageName' : '丹麦文','languageId' : 'danish'},{'languageName' : '荷兰语','languageId' : 'dutch'},
                           {'languageName' : '斯洛文尼亚语','languageId' : 'slovenia'},{'languageName' : '罗马尼亚语','languageId' : 'romania'},
                           {'languageName' : '繁体字','languageId' : 'chineseT'}]"/>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearFormUpgrade()">清空查询</a>
                    <div style="display: inline-block" class="separator"></div>
                    <a id="addId" class="mini-button" style="margin-right: 5px" plain="true"  onclick="openLbjEditWindow('add','')">新增</a>
                    <a id = "deletedId" class="mini-button btn-red" style="margin-right: 5px" plain="true"
                       onclick="removeLbj()">删除</a>
                    <a id = "importMaterialName" class="mini-button" style="margin-right: 5px" plain="true" onclick="importMaterialName()">导入</a>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="exportMaterialName()">导出当前页</a>
                </li>
            </ul>

        </form>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="lbjfyListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         onlyCheckSelection="true"
         url="${ctxPath}/yfgj/core/mulitilingualTranslation/getLbjfyList.do" idField="id" allowCellWrap="true"
         multiSelect="true" showColumnsMenu="true" sizeList="[2000,3000,4000,5000]" pageSize="2000" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div name="action" cellCls="actionIcons" width="100" headerAlign="center" align="center"
                 renderer="onActionRenderer" cellStyle="padding:0;">操作
            </div>
            <div field="materialCode" sortField="materialCode" width="80" headerAlign="center" align="center"
                 allowSort="true">物料编码
            </div>
            <div field="originChinese" sortField="originChinese" width="80" headerAlign="center" align="center"
                 allowSort="true">原始中文
            </div>
            <div field="chineseName" sortField="chineseName" width="80" headerAlign="center" align="center"
                 allowSort="true">中文名称
            </div>
            <div name="englishName" field="englishName" width="120" headerAlign="center" align="center" allowSort="true">英文名称</div>
            <div name="russianName" field="russianName" width="120" headerAlign="center" align="center" allowSort="true">俄文名称</div>
            <div name="portugueseName" field="portugueseName" width="120" headerAlign="center" align="center" allowSort="true">葡文名称</div>
            <div name="germanyName" field="germanyName" width="120" headerAlign="center" align="center" allowSort="true">德文名称</div>
            <div name="spanishName" field="spanishName" width="120" headerAlign="center" align="center" allowSort="true">西文名称</div>
            <div name="frenchName" field="frenchName" width="120" headerAlign="center" align="center" allowSort="true">法文名称</div>
            <div name="italianName" field="italianName" width="120" headerAlign="center" align="center" allowSort="true">意文名称</div>
            <div name="polishName" field="polishName" width="120" headerAlign="center" align="center" allowSort="true">波兰语</div>
            <div name="turkishName" field="turkishName" width="120" headerAlign="center" align="center" allowSort="true">土耳其语</div>
            <div name="swedishName" field="swedishName" width="120" headerAlign="center" align="center" allowSort="true">瑞典语</div>
            <div name="danishName" field="danishName" width="120" headerAlign="center" align="center" allowSort="true">丹麦文</div>
            <div name="dutchName" field="dutchName" width="120" headerAlign="center" align="center" allowSort="true">荷兰语</div>
            <div name="sloveniaName" field="sloveniaName" width="120" headerAlign="center" align="center" allowSort="true">斯洛文尼亚语 </div>
            <div name="romaniaName" field="romaniaName" width="120" headerAlign="center" align="center" allowSort="true">罗马尼亚语 </div>
            <div name="chineseTName" field="chineseTName" width="120" headerAlign="center" align="center" allowSort="true">繁体字</div>
        </div>
    </div>
</div>

<!--导出Excel相关HTML-->
<form id="excelForm" action="${ctxPath}/yfgj/core/mulitilingualTranslation/exportMaterialList.do" method="post" target="excelIFrame">
    <input type="hidden" name="filter" id="filter"/>
    <input type="hidden" name="pageIndex" id="pageIndex"/>
    <input type="hidden" name="pageSize" id="pageSize"/>
    <input type="hidden" name="languageList" id="languageList"/>
</form>
<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>

<div id="importWindow" title="零部件词汇导入窗口" class="mini-window" style="width:750px;height:280px;"
     showModal="true" showFooter="false" allowResize="true">
    <p style="display: inline-block;color: red;font-size:15px;vertical-align: middle">
    </p>
    <div style="display: inline-block;float: right;">
        <a id="importBtn" class="mini-button" onclick="importMaterial()">导入</a>
        <a id="closeWindow" class="mini-button btn-red" onclick="closeImportWindow()">关闭</a>
    </div>
    <div class="mini-fit" style="font-size: 14px;margin-top: 30px">
        <form id="formImport" method="post">
            <table class="table-detail" cellspacing="1" cellpadding="0">
                <tr>
                    <td style="width: 30%">上传模板：</td>
                    <td style="width: 70%;">
                        <a href="#" style="color:blue;text-decoration:underline;" onclick="downImportTemplate()">零部件词汇导入模板.xls</a>
                    </td>
                </tr>
                <tr>
                    <td style="width: 30%">操作：</td>
                    <td>
                        <input class="mini-textbox" style="width:60%;float: left" id="fileName" name="fileName"
                               readonly/>
                        <input id="inputFile" style="display:none;" type="file" onchange="getSelectFile()"
                               accept="application/xls"/>
                        <a id="uploadFileBtn" class="mini-button" style="float: left;margin-left: 10px"
                           onclick="uploadFile">选择文件</a>
                        <a id="clearFileBtn" class="mini-button" style="float: left;margin-left: 10px"
                           onclick="clearUploadFile">清除</a>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var lbjfyListGrid = mini.get("lbjfyListGrid");
    var currentUserId = "${currentUserId}";
    var isDYYFY = "${isDYYFY}";

    var currentUserNo = "${currentUserNo}";
    var updateJlWindow = mini.get("updateJlWindow");

    //添加列表
    var quoteLbjWindow = mini.get("quoteLbjWindow");

    var importWindow = mini.get("importWindow");


    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var chineseId = record.chineseId;
        var s = '';
        if(isDYYFY == "true"){
            s += '<span  title="修改" onclick="openLbjEditWindow(\'update\',\'' + chineseId + '\')">编辑</span>';
            s += '<span  title="删除" onclick="removeLbj(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
        }else {
            s += '<span  title="修改" style="color: silver">修改</span>';
            s += '<span  title="删除" style="color: silver">删除</span>';
        }


        return s;
    }

    function searchFrm() {
        var parent = $(".search-form");
        var inputAry = $("input", parent);
        showColumn()
        hideColumn();
        searchByInput(inputAry);
    }

    /**
     *清空查询
     */
    function clearFormUpgrade(){
        // mini.get("materialName").setText("");
        mini.get("languageId").setValue("");
        mini.get("materialName").setValue("");
        showColumn();
        lbjfyListGrid.load();
    }


</script>
<redxun:gridScript gridId="lbjfyListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>