<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>人员异动</title>
    <%@include file="/commons/list.jsp" %>
</head>

<body>
<div class="mini-toolbar" >
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul >
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">标题 </span>
                    <input class="mini-textbox" id="title" name="title" />
                </li>
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px"  plain="true" onclick="searchFrm()">查询</a>
                    <a class="mini-button btn-red" style="margin-right: 5px"  plain="true" onclick="clearForm()">清空查询</a>
                    <div style="display: inline-block" class="separator"></div>
                    <a id="addDetail" style="margin-right: 5px;display: none" class="mini-button" onclick="addRyyd()">添加</a>
                </li>
            </ul>
        </form>
    </div>
</div>

<div class="mini-fit">
    <div id="detailListGrid" class="mini-datagrid" style="width: 100%; height: 100%"
         allowResize="false" allowCellWrap="true"
         idField="id" url="${ctxPath}/zhgl/core/ryyd/getRyydList.do"
         autoload="true" allowCellEdit="true" allowCellSelect="true"
         multiSelect="true" showPager="true" showColumnsMenu="false" allowAlternating="true">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div field="action" name="action" cellCls="actionIcons" width="40" headerAlign="center" align="center" renderer="onActionRenderer" cellStyle="padding:0;">操作</div>
            <div field="title" width="80" headerAlign='center' align="center">标题</div>
            <div field="CREATE_TIME_" width="30" dateFormat="yyyy-MM-dd" headerAlign='center' align="center">创建时间</div>
        </div>
    </div>
</div>


<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var detailListGrid = mini.get("detailListGrid");
    var action = "${action}";
    var currentUserMainDepName = "${currentUserMainDepName}";
    var isDepRespMan = ${isDepRespMan};
    var isJxzy = ${isJxzy};
    var isZbsj = ${isZbsj};
    var isFgld = ${isFgld};
    var currentUserId = "${currentUserId}";


    $(function () {
        if (isJxzy||currentUserId=='1') {
            $("#addDetail").show();
        }
    });

    function onActionRenderer(e) {
        var record = e.record;
        var zId = record.zId;
        var s ='<span  title="查看" onclick="ryydDetail(\'' + zId + '\')">查看</span>';
        if(isJxzy||currentUserId=='1') {
            s+='<span  title="编辑" onclick="ryydEdit(\'' + zId + '\')">编辑</span>';
        }
        if(isJxzy||currentUserId=='1') {
            s+='<span  title="删除" onclick="removeRyyd(\'' + zId + '\')">删除</span>';
        }

        return s;
    }

    function addRyyd() {
        var url = jsUseCtxPath + "/zhgl/core/ryyd/ryydEditPage.do?action=add";
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (detailListGrid) {
                    detailListGrid.reload()
                }
            }
        }, 1000);
    }
    function ryydDetail(zId) {
        var action = "detail";
        var url = jsUseCtxPath + "/zhgl/core/ryyd/ryydEditPage.do?zId=" + zId+ "&action=" + action ;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (detailListGrid) {
                    detailListGrid.reload()
                }
            }
        }, 1000);
    }
    function ryydEdit(zId) {
        var action = "edit";
        var url = jsUseCtxPath + "/zhgl/core/ryyd/ryydEditPage.do?zId=" + zId+ "&action=" + action ;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (detailListGrid) {
                    detailListGrid.reload()
                }
            }
        }, 1000);
    }
    function removeRyyd(zId) {
        mini.confirm("确定删除选中记录？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                _SubmitJson({
                    url: jsUseCtxPath + "/zhgl/core/ryyd/deleteRyyd.do",
                    method: 'POST',
                    showMsg:false,
                    data: {id: zId},
                    success: function (data) {
                        if (data) {
                            mini.alert(data.message);
                            searchFrm();
                        }
                    }
                });
            }
        });
    }
    function openImportWindow() {
        importWindow.show();
    }
    function closeImportWindow() {
        importWindow.hide();
        clearUploadFile();
        searchFrm();
    }

    //导入模板下载
    function downImportYdjx() {
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", jsUseCtxPath + "/zhgl/core/ydjx/importTemplateDownload.do");
        $("body").append(form);
        form.submit();
        form.remove();
    }

    //触发文件选择
    function uploadFile() {
        $("#inputFile").click();
    }

    //文件类型判断及文件名显示
    function    getSelectFile() {
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

    //清空文件
    function clearUploadFile() {
        $("#inputFile").val('');
        mini.get("fileName").setValue('');
    }

    //上传批量导入
    function importProduct() {
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
            xhr.open('POST', jsUseCtxPath + '/zhgl/core/ydjx/importExcel.do', false);
            xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
            var fd = new FormData();
            fd.append('importFile', file);
            xhr.send(fd);
        }
    }
</script>
<redxun:gridScript gridId="detailListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl="" />
</body>
</html>
