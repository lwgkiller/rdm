<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>零部件词汇查询</title>
    <%@include file="/commons/list.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css" />
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">物料编码: </span>
                    <input class="mini-textbox" id="materialCode" name="materialCode">
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">建议中文: </span>
                        <input class="mini-textbox" id="materialName" name="materialName">
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">原始中文: </span>
                    <input class="mini-textbox" id="originChinese" name="originChinese">
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">多语言选择: </span>
                </li>
                <li style="margin-right: 15px">
                    <input id="languageId" name="languageId" class="mini-combobox" style="width:auto;"
                           repeatItems="13" repeatLayout="table" repeatDirection="horizontal" onvaluechanged="searchFrm()"
                           textfield="languageName" valuefield="languageId" value="english" multiSelect="true"
                           data="[ {'languageName' : '英文','languageId' : 'english'},{'languageName' : '俄文','languageId' : 'russian'},
                           {'languageName' : '葡文','languageId' : 'portuguese'},{'languageName' : '德文','languageId' : 'germany'},
                           {'languageName' : '西文','languageId' : 'spanish'},{'languageName' : '法文','languageId' : 'french'},
                           {'languageName' : '意文','languageId' : 'italian'},{'languageName' : '波兰语','languageId' : 'polish'},
                           {'languageName' : '土耳其语','languageId' : 'turkish'},{'languageName' : '瑞典语','languageId' : 'swedish'},
                           {'languageName' : '丹麦文','languageId' : 'danish'},{'languageName' : '荷兰语','languageId' : 'dutch'},
                           {'languageName' : '斯洛文尼亚语','languageId' : 'slovenia'},{'languageName' : '罗马尼亚语','languageId' : 'romania'},
                           {'languageName' : '繁体字','languageId' : 'chineseT'},{'languageName' : '泰语','languageId' : 'thai'},
                           {'languageName' : '匈牙利语','languageId' : 'hungarian'},{'languageName' : '挪威语','languageId' : 'norwegian'},
                           {'languageName' : '韩语','languageId' : 'korean'},{'languageName' : '印尼语','languageId' : 'indone'},
                           {'languageName' : '阿拉伯语','languageId' : 'arabic'},{'languageName' : '日语','languageId' : 'japanese'}
                           ]"/>
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearFormUpgrade()">清空查询</a>
                    <div style="display: inline-block" class="separator"></div>
                    <a id="addId" class="mini-button" style="margin-right: 5px" plain="true"  onclick="openLjtcEditWindow('add','')">新增</a>
                    <a id = "deletedId" class="mini-button btn-red" style="margin-right: 5px" plain="true"
                       onclick="removeLjtc()">删除</a>
                    <a id = "importLjtc" class="mini-button" style="margin-right: 5px;" plain="true" onclick="importLjtc()">导入</a>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="exportLjtc()">导出当前页</a>
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="exportLjtcAll()">导出全部</a>
                </li>
            </ul>

        </form>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="ljtcListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         onlyCheckSelection="true"
         url="${ctxPath}/yfgj/core/mulitilingualTranslation/getLjtcList.do" idField="id" allowCellWrap="true"
         multiSelect="true" showColumnsMenu="true" sizeList="[50,100,150,200]" pageSize="50" allowAlternating="true"
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
            <div name="thaiName" field="thaiName" width="120" headerAlign="center" align="center" allowSort="true">泰语</div>
            <div name="hungarianName" field="hungarianName" width="120" headerAlign="center" align="center" allowSort="true">匈牙利语</div>
            <div name="norwegianName" field="norwegianName" width="120" headerAlign="center" align="center" allowSort="true">挪威语</div>
            <div name="koreanName" field="koreanName" width="120" headerAlign="center" align="center" allowSort="true">韩语</div>
            <div name="indoneName" field="indoneName" width="120" headerAlign="center" align="center" allowSort="true">印尼语</div>
            <div name="arabicName" field="arabicName" width="120" headerAlign="center" align="center" allowSort="true">阿拉伯语</div>
            <div name="japaneseName" field="japaneseName" width="120" headerAlign="center" align="center" allowSort="true">日语</div>
          </div>
    </div>
</div>

<!--导出Excel相关HTML-->
<form id="excelForm" action="${ctxPath}/yfgj/core/mulitilingualTranslation/exportLjtcList.do" method="post" target="excelIFrame">
    <input type="hidden" name="filter" id="filter"/>
    <input type="hidden" name="pageIndex" id="pageIndex"/>
    <input type="hidden" name="pageSize" id="pageSize"/>
    <input type="hidden" name="languageList" id="languageList"/>
</form>
<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>
<!--导出Excel相关HTML-->
<form id="excelFormAll" action="${ctxPath}/yfgj/core/mulitilingualTranslation/exportLjtcListAll.do" method="post" target="excelIFrameAll">
    <input type="hidden" name="languageListAll" id="languageListAll"/>
</form>
<iframe id="excelIFrameAll" name="excelIFrameAll" style="display: none;"></iframe>

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
    var ljtcListGrid = mini.get("ljtcListGrid");
    var currentUserId = "${currentUserId}";
    var isDYYFY = "${isDYYFY}";

    var currentUserNo = "${currentUserNo}";
    var updateJlWindow = mini.get("updateJlWindow");

    var importWindow = mini.get("importWindow");


    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var chineseId = record.chineseId;
        var s = '';
        if(isDYYFY == "true"){
            s += '<span  title="修改" onclick="openLjtcEditWindow(\'update\',\'' + chineseId + '\')">编辑</span>';
            s += '<span  title="删除" onclick="removeLjtc(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
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
        ljtcListGrid.load();
    }

    $(function () {
        if(isDYYFY == "false"){
            mini.get("importLjtc").setEnabled(false);
            mini.get("deletedId").setEnabled(false);
            mini.get("addId").setEnabled(false);
        }
        showColumn();
        searchFrm();
    });

    function hideColumn() {
        var languageId = mini.get("languageId").getValue();
        if (languageId) {
            if (!languageId.match("english")) {
                var englishName = ljtcListGrid.getColumn('englishName');
                ljtcListGrid.hideColumn(englishName);
            }
            if (!languageId.match("russian")) {
                var russianName = ljtcListGrid.getColumn('russianName');
                ljtcListGrid.hideColumn(russianName);
            }
            if (!languageId.match("portuguese")) {
                var portugueseName = ljtcListGrid.getColumn('portugueseName');
                ljtcListGrid.hideColumn(portugueseName);
            }
            if (!languageId.match("germany")) {
                var germanyName = ljtcListGrid.getColumn('germanyName');
                ljtcListGrid.hideColumn(germanyName);
            }
            if (!languageId.match("spanish")) {
                var spanishName = ljtcListGrid.getColumn('spanishName');
                ljtcListGrid.hideColumn(spanishName);
            }
            if (!languageId.match("french")) {
                var frenchName = ljtcListGrid.getColumn('frenchName');
                ljtcListGrid.hideColumn(frenchName);
            }
            if (!languageId.match("italian")) {
                var italianName = ljtcListGrid.getColumn('italianName');
                ljtcListGrid.hideColumn(italianName);
            }
            if (!languageId.match("polish")) {
                var polishName = ljtcListGrid.getColumn('polishName');
                ljtcListGrid.hideColumn(polishName);
            }
            if (!languageId.match("turkish")) {
                var turkishName = ljtcListGrid.getColumn('turkishName');
                ljtcListGrid.hideColumn(turkishName);
            }
            if (!languageId.match("swedish")) {
                var swedishName = ljtcListGrid.getColumn('swedishName');
                ljtcListGrid.hideColumn(swedishName);
            }
            if (!languageId.match("danish")) {
                var danishName = ljtcListGrid.getColumn('danishName');
                ljtcListGrid.hideColumn(danishName);
            }
            if (!languageId.match("dutch")) {
                var dutchName = ljtcListGrid.getColumn('dutchName');
                ljtcListGrid.hideColumn(dutchName);
            }
            if (!languageId.match("slovenia")) {
                var sloveniaName = ljtcListGrid.getColumn('sloveniaName');
                ljtcListGrid.hideColumn(sloveniaName);
            }
            if (!languageId.match("romania")) {
                var romaniaName = ljtcListGrid.getColumn('romaniaName');
                ljtcListGrid.hideColumn(romaniaName);
            }
            if (!languageId.match("chineseT")) {
                var chineseTName = ljtcListGrid.getColumn('chineseTName');
                ljtcListGrid.hideColumn(chineseTName);
            }
            //@mh 2023年8月10日 增加三种语言
            if (!languageId.match("thai")) {
                var thaiName = ljtcListGrid.getColumn('thaiName');
                ljtcListGrid.hideColumn(thaiName);
            }
            if (!languageId.match("hungarian")) {
                var hungarianName = ljtcListGrid.getColumn('hungarianName');
                ljtcListGrid.hideColumn(hungarianName);
            }
            if (!languageId.match("norwegian")) {
                var norwegianName = ljtcListGrid.getColumn('norwegianName');
                ljtcListGrid.hideColumn(norwegianName);
            }
            if (!languageId.match("korean")) {
                var koreanName = ljtcListGrid.getColumn('koreanName');
                ljtcListGrid.hideColumn(koreanName);
            }
            if (!languageId.match("indone")) {
                var indoneName = ljtcListGrid.getColumn('indoneName');
                ljtcListGrid.hideColumn(indoneName);
            }

            if (!languageId.match("arabic")) {
                var arabicName = ljtcListGrid.getColumn('arabicName');
                ljtcListGrid.hideColumn(arabicName);
            }

            if (!languageId.match("japanese")) {
                var japaneseName = ljtcListGrid.getColumn('japaneseName');
                ljtcListGrid.hideColumn(japaneseName);
            }
        }
    }

    function showColumn() {
        var englishName = ljtcListGrid.getColumn('englishName');
        ljtcListGrid.showColumn(englishName);
        var russianName = ljtcListGrid.getColumn('russianName');
        ljtcListGrid.showColumn(russianName);
        var portugueseName = ljtcListGrid.getColumn('portugueseName');
        ljtcListGrid.showColumn(portugueseName);
        var germanyName = ljtcListGrid.getColumn('germanyName');
        ljtcListGrid.showColumn(germanyName);
        var spanishName = ljtcListGrid.getColumn('spanishName');
        ljtcListGrid.showColumn(spanishName);
        var frenchName = ljtcListGrid.getColumn('frenchName');
        ljtcListGrid.showColumn(frenchName);
        var italianName = ljtcListGrid.getColumn('italianName');
        ljtcListGrid.showColumn(italianName);
        var polishName = ljtcListGrid.getColumn('polishName');
        ljtcListGrid.showColumn(polishName);
        var turkishName = ljtcListGrid.getColumn('turkishName');
        ljtcListGrid.showColumn(turkishName);
        var swedishName = ljtcListGrid.getColumn('swedishName');
        ljtcListGrid.showColumn(swedishName);
        var danishName = ljtcListGrid.getColumn('danishName');
        ljtcListGrid.showColumn(danishName);
        var dutchName = ljtcListGrid.getColumn('dutchName');
        ljtcListGrid.showColumn(dutchName);
        var sloveniaName = ljtcListGrid.getColumn('sloveniaName');
        ljtcListGrid.showColumn(sloveniaName);
        var romaniaName = ljtcListGrid.getColumn('romaniaName');
        ljtcListGrid.showColumn(romaniaName);
        var chineseTName = ljtcListGrid.getColumn('chineseTName');
        ljtcListGrid.showColumn(chineseTName);
        //@mh 2023年8月10日 增加三种语言
        var thaiName = ljtcListGrid.getColumn('thaiName');
        ljtcListGrid.showColumn(thaiName);
        var hungarianName = ljtcListGrid.getColumn('hungarianName');
        ljtcListGrid.showColumn(hungarianName);
        var norwegianName = ljtcListGrid.getColumn('norwegianName');
        ljtcListGrid.showColumn(norwegianName);
        var koreanName = ljtcListGrid.getColumn('koreanName');
        ljtcListGrid.showColumn(koreanName);
        var indoneName = ljtcListGrid.getColumn('indoneName');
        ljtcListGrid.showColumn(indoneName);
        var arabicName = ljtcListGrid.getColumn('arabicName');
        ljtcListGrid.showColumn(arabicName);
        var japaneseName = ljtcListGrid.getColumn('japaneseName');
        ljtcListGrid.showColumn(japaneseName);

    }


    /**
     * 添加弹窗
     */
    function openLjtcEditWindow(action, chineseId) {
        var title = "";
        if (action == "add") {
            title = "零部件词汇添加"
        }else {
            title = "零部件词汇修改"
        }
        mini.open({
            title: title,
            url: jsUseCtxPath + "/yfgj/core/mulitilingualTranslation/editLjtc.do?chineseId=" + chineseId + '&action'+ action ,
            width: 800,
            height: 600,
            showModal: true,
            allowResize: true,
            onload: function () {
            },
            ondestroy: function (action) {
                searchLjtcList();
            }
        });

    }

    function searchLjtcList() {
        var queryParam = [];
        //其他筛选条件
        var materialName = $.trim(mini.get("materialName").getValue());
        if (materialName) {
            queryParam.push({name: "materialName", value: materialName});
        }
        var languageId = $.trim(mini.get("languageId").getValue());
        if (languageId) {
            queryParam.push({name: "languageId", value: languageId});
        }
        var data = {};
        data.filter = mini.encode(queryParam);
        data.pageIndex = ljtcListGrid.getPageIndex();
        data.pageSize = ljtcListGrid.getPageSize();
        data.sortField = ljtcListGrid.getSortField();
        data.sortOrder = ljtcListGrid.getSortOrder();
        //查询
        ljtcListGrid.load(data);
    }


    /**
     * 零部件删除
     * @param record
     */
    function removeLjtc(record) {

        var rows = [];
        if (record) {
            rows.push(record);
        } else {
            rows = ljtcListGrid.getSelecteds();
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
                    rowIds.push(r.chineseId);
                }

                _SubmitJson({
                    url: jsUseCtxPath + "/yfgj/core/mulitilingualTranslation/deleteLjtc.do?",
                    method: 'POST',
                    showMsg: false,
                    data: {ids: rowIds.join(',')},
                    success: function (data) {
                        if (data) {
                            mini.alert(data.message);
                            ljtcListGrid.load();
                        }
                    }
                });
            }
        });
    }

    function exportLjtc() {
        var parent = $(".search-form");
        var inputAry = $("input", parent);
        var params = [];
        inputAry.each(function (i) {
            var el = $(this);
            var obj = {};
            obj.name = el.attr("name");
            if (!obj.name) return true;
            obj.value = el.val();
            params.push(obj);
        });
        var languageList = $.trim(mini.get("languageId").getValue());
        // var pageIndex = ljtcListGrid.getPageIndex();
        var pageIndex = 0;
        var pageSize = ljtcListGrid.getPageSize();
        var totalCount =ljtcListGrid.getTotalCount();
        $("#filter").val(mini.encode(params));
        $("#pageIndex").val(mini.encode(pageIndex));
        $("#pageSize").val(mini.encode(totalCount));
        $("#languageList").val(mini.encode(languageList));
        var excelForm = $("#excelForm");
        excelForm.submit();
    }
    function exportLjtcAll() {
        var parent = $(".search-form");
        var inputAry = $("input", parent);
        var params = [];
        inputAry.each(function (i) {
            var el = $(this);
            var obj = {};
            obj.name = el.attr("name");
            if (!obj.name) return true;
            obj.value = el.val();
            params.push(obj);
        });
        var languageList = $.trim(mini.get("languageId").getValue());
        $("#languageListAll").val(mini.encode(languageList));
        var excelForm = $("#excelFormAll");
        excelForm.submit();
    }
    //导入
    function importLjtc() {
        importWindow.show();
    }

    //上传批量导入
    function importMaterial() {
        var file = null;
        var fileList = $("#inputFile")[0].files;
        if (fileList && fileList.length > 0) {
            file = fileList[0];
        }
        if (!file) {
            mini.alert('请选择文件！');
            return;
        }
        //XMLHttpRequest方式上传表单
        var xhr = false;
        try {
            //尝试创建 XMLHttpRequest 对象，除 IE 外的浏览器都支持这个方法。
            xhr = new XMLHttpRequest();
        } catch (e) {
            //使用较新版本的 IE 创建 IE 兼容的对象（Msxml2.XMLHTTP）。
            xhr = ActiveXobject("Msxml12.XMLHTTP");
        }

        if (xhr.upload) {
            xhr.onreadystatechange = function (e) {
                if (xhr.readyState == 4) {
                    if (xhr.status == 200) {
                        if (xhr.responseText) {
                            var returnObj = JSON.parse(xhr.responseText);
                            var message = '';
                            if (returnObj.message) {
                                message = returnObj.message;
                            }
                            mini.alert(message);
                        }
                    }
                }
            };

            // 开始上传
            xhr.open('POST', jsUseCtxPath + '/yfgj/core/mulitilingualTranslation/importExcelljtc.do', false);
            xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
            var fd = new FormData();
            fd.append('importFile', file);
            xhr.send(fd);
        }
    }

    function closeImportWindow() {
        importWindow.hide();
        clearUploadFile();
        searchFrm();
    }


    //文件类型判断及文件名显示
    function getSelectFile() {
        var fileList = $("#inputFile")[0].files;
        if (fileList && fileList.length > 0) {
            var fileNameSuffix = fileList[0].name.split('.').pop();
            if (fileNameSuffix == 'xls'||fileNameSuffix == 'xlsx') {
                mini.get("fileName").setValue(fileList[0].name);
            }
            else {
                clearUploadFile();
                mini.alert('请上传excel文件！');
            }
        }
    }

    //触发文件选择
    function uploadFile() {
        $("#inputFile").click();
    }

    //清空文件
    function clearUploadFile() {
        $("#inputFile").val('');
        mini.get("fileName").setValue('');
    }

    //导入模板下载
    function downImportTemplate() {
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", jsUseCtxPath + "/yfgj/core/mulitilingualTranslation/importTemplateDownloadLjtc.do");
        $("body").append(form);
        form.submit();
        form.remove();
    }



</script>
<redxun:gridScript gridId="ljtcListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>