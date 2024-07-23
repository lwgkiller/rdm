
<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>经验教训列表管理</title>
    <%@include file="/commons/list.jsp" %>

    <script src="${ctxPath}/scripts/zlgjNPI/gjllList.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li>
                    <a id="addRel" class="mini-button " style="margin-right: 5px" plain="true"
                       onclick="addRelExp()">新增关联</a>
                </li>
                <li>
                    <a id="delRel" class="mini-button  btn-red" style="margin-right: 5px" plain="true"
                       onclick="delRelExp()">删除关联</a>
                </li>
            </ul>
        </form>
        <span class="searchSelectionBtn" onclick="no_search(this ,'searchForm')">
				<i class="icon-sc-lower"></i>
			</span>
    </div>
</div>
<div class="mini-fit" style="height: 70%;">
    <div id="relGjllListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         sizeList="[50,100,200]" pageSize="50" allowCellWrap="true"
         url="${ctxPath}/drbfm/single/getSingleExpList.do?partId=${singleId}"
         autoload="true"
         idField="id" allowAlternating="true" showPager="true" multiSelect="true">
        <div property="columns">
            <div type="checkcolumn" width="10"></div>
            <div type="indexcolumn" align="center" width="10">序号</div>
            <div name="action" cellCls="actionIcons" width="25" headerAlign="center" align="center"
                 renderer="onMessageActionRenderer" cellStyle="padding:0;">操作
            </div>
            <div field="wtlx" headerAlign='center' align='center'  width="20" renderer="wtlxRenderer">问题类型</div>
            <div field="zlgjNumber" headerAlign='center' align='center'  width="40" renderer="linkZlgj">问题编号</div>
            <div field="jiXing" headerAlign='center' align='center'  width="15">机型类别</div>
            <div field="smallJiXing" headerAlign='center' align='center' width="20">机型</div>
            <div field="gzlj" width="20" headerAlign="center" align="center" allowSort="true">零部件名称</div>
            <div field="lbjgys" width="30" headerAlign="center" align="center" allowSort="true">零部件供应商</div>
            <div field="wtms" width="40" dateFormat="yyyy-MM-dd" headerAlign='center' align="center">问题描述</div>
            <div field="fujian" width="20" headerAlign="center" align="center" allowSort="true" renderer="fujian">问题图片
            </div>
            <div field="zrrName" width="15" headerAlign="center" align="center" allowSort="true">责任人</div>
            <div field="ssbmName" width="20" headerAlign="center" align="center" allowSort="true">责任部门</div>
            <div field="cqcs" width="40" headerAlign="center" align="center" allowSort="true">改进方案</div>
            <div field="tzdh" width="20" headerAlign="center" align="center" allowSort="true">标准化文件</div>
            <div field="yjqhch" width="20" headerAlign="center" align="center" allowSort="true">预计切换车号</div>
            <div field="qhTime" sortField="qhTime"  width="20" align="center" headerAlign="center" allowSort="true" dateFormat="yyyy-MM-dd HH:mm:ss">实际完成时间</div>
            <div field="creator" width="15" headerAlign="center" align="center" allowSort="true">创建人</div>
        </div>
    </div>
</div>

<div style="margin-bottom: 25px">
    <a id="addFile" class="mini-button" onclick="fileupload()">添加附件</a>
</div>
<div class="mini-fit" style="height: 30%">
    <div id="fileListGrid" class="mini-datagrid" style="width: 100%; height: 100%"
         allowResize="false"
         idField="id" autoload="true"
         url="${ctxPath}/drbfm/single/queryExpFileList.do?belongSingleId=${singleId}"
         multiSelect="true" showPager="false" showColumnsMenu="false" allowAlternating="true">
        <div property="columns">
            <div type="indexcolumn" align="center" width="20">序号</div>
            <div field="fileName" width="140" headerAlign="center" align="center">文件名称</div>
            <div field="fileDesc" width="140" headerAlign="center" align="center">文件描述</div>
            <div field="fileSize" width="80" headerAlign="center" align="center">文件大小</div>
            <div field="action" width="100" headerAlign='center' align="center"
                 renderer="operationRenderer">操作
            </div>
        </div>
    </div>
</div>

<%--关联经验教训弹窗--%>
<div id="selectExpWindow" title="选择关联经验教训" class="mini-window" style="width:1050px;height:750px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
    <div class="mini-toolbar" style="text-align:left;line-height:30px;margin-bottom: 10px"
         borderStyle="border-left:0;border-top:0;border-right:0;">
        <div class="searchBox">
            <form id="searchGrid" class="search-form" style="margin-bottom: 25px">
                <ul>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">问题类型: </span>
                        <input id="wtlx" name="wtlx" class="mini-combobox" style="width:98%;"
                               textField="value" valueField="key" emptyText="请选择..."
                               required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                               data="[{'key' : 'XPSZ','value' : '新品试制'},{'key' : 'XPZDSY','value' : '新品整机试验'}
                           ,{'key' : 'XPLS','value' : '新品路试'},{'key' : 'CNWT','value' : '厂内问题'}
                           ,{'key' : 'SCWT','value' : '市场问题'},{'key' : 'HWWT','value' : '海外问题'}
                           ,{'key' : 'WXBLX','value' : '维修便利性'},{'key' : 'LBJSY','value' : '新品零部件试验'}]"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">机型: </span>
                        <input class="mini-textbox" id="smallJiXing" name="smallJiXing" />
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">机型类别: </span>
                        <input id="jiXing" name="jiXing" class="mini-combobox" style="width:100px;"
                               textField="value" valueField="key" emptyText="请选择..."
                               required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                               data="[{'key' : '微挖','value' : '微挖'},{'key' : '轮挖','value' : '轮挖'},{'key' : '小挖','value' : '小挖'},
                                   {'key' : '中挖','value' : '中挖'},{'key' : '大挖','value' : '大挖'},{'key' : '特挖','value' : '特挖'},
                                   {'key' : '属具','value' : '属具'},{'key' : '新能源','value' : '新能源'}
                                   ,{'key' : '海外','value' : '海外'}]"
                        />
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto">零部件名称: </span>
                        <input class="mini-textbox" id="gzlj" name="gzlj" />
                    </li>
                    <li style="margin-left: 10px">
                        <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()">查询</a>
                        <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()">清空查询</a>
                    </li>
                </ul>
            </form>
        </div>


    </div>



    <div class="mini-fit" style="height: 100%;">
        <div id="gjllListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
             sizeList="[50,100,200]" pageSize="50" allowCellWrap="true"
             url="${ctxPath}/zlgjNPI/core/Gjll/queryList.do"
             idField="id" allowAlternating="true" showPager="true" multiSelect="true">
            <div property="columns">
                <div type="checkcolumn" width="10"></div>
                <div type="indexcolumn" align="center" width="10">序号</div>
                <div name="action" cellCls="actionIcons" width="25" headerAlign="center" align="center"
                     renderer="onMessageActionRenderer" cellStyle="padding:0;">操作
                </div>
                <div field="wtlx" headerAlign='center' align='center'  width="20" renderer="wtlxRenderer">问题类型</div>
                <div field="zlgjNumber" headerAlign='center' align='center'  width="40" renderer="linkZlgj">问题编号</div>
                <div field="jiXing" headerAlign='center' align='center'  width="15">机型类别</div>
                <div field="smallJiXing" headerAlign='center' align='center' width="20">机型</div>
                <div field="gzlj" width="20" headerAlign="center" align="center" allowSort="true">零部件名称</div>
                <div field="lbjgys" width="30" headerAlign="center" align="center" allowSort="true">零部件供应商</div>
                <div field="wtms" width="40" dateFormat="yyyy-MM-dd" headerAlign='center' align="center">问题描述</div>
                <div field="fujian" width="20" headerAlign="center" align="center" allowSort="true" renderer="fujian">问题图片
                </div>
                <div field="zrrName" width="15" headerAlign="center" align="center" allowSort="true">责任人</div>
                <div field="ssbmName" width="20" headerAlign="center" align="center" allowSort="true">责任部门</div>
                <div field="cqcs" width="40" headerAlign="center" align="center" allowSort="true">改进方案</div>
                <div field="tzdh" width="20" headerAlign="center" align="center" allowSort="true">标准化文件</div>
                <div field="yjqhch" width="20" headerAlign="center" align="center" allowSort="true">预计切换车号</div>
                <div field="qhTime" sortField="qhTime"  width="20" align="center" headerAlign="center" allowSort="true" dateFormat="yyyy-MM-dd HH:mm:ss">实际完成时间</div>
                <div field="creator" width="15" headerAlign="center" align="center" allowSort="true">创建人</div>
            </div>
        </div>
    </div>

    <div property="footer" style="padding:5px;height: 40px">
        <table style="width:100%;height: 100%">
            <tr>
                <td style="width:120px;text-align:center;">
                    <input type="button" style="height: 25px;width: 70px" value="确定" onclick="selectExpOK()"/>
                    <input type="button" style="height: 25px;width: 70px" value="取消"
                           onclick="selectExpHide()"/>
                </td>
            </tr>
        </table>
    </div>
</div>




<script type="text/javascript">
    mini.parse();
    var importWindow = mini.get("importWindow");
    var singleId = "${singleId}";
    var selectExpWindow = mini.get("selectExpWindow");
    var jsUseCtxPath = "${ctxPath}";
    var gjllListGrid = mini.get("gjllListGrid");
    var relGjllListGrid = mini.get("relGjllListGrid");
    var fileListGrid = mini.get("fileListGrid");
    var currentUserId = "${currentUserId}";
    var currentUserDep = "${currentUserDep}";
    var currentUserNo = "${currentUserNo}";
    var currentUserName= "${currentUserName}";
    var currentTime= "${currentTime}";
    var belongbj = "${belongbj}";
    var stageName = "${stageName}";
    var type = "${type}";
    var action = "${action}";
    var coverContent = currentUserName + "<br/>" + currentTime + "<br/>" + xzxgwjjxyxgs;


    if (type != "" && belongbj != "") {
        $("#link").show();
        $("#exportGjll").hide();
        $("#editMsg").hide();
    }

    $(function () {

        if (action == 'task'&&stageName=='bjfzrfxfx') {

        }else if (action == 'edit') {

        } else {
            mini.get("addRel").setEnabled(false);
            mini.get("delRel").setEnabled(false);
        }
    });


    function linkZlgj(e) {
        var record = e.record;
        var wtId = record.wtId;
        var zlgjNumber = record.zlgjNumber;
        var status = "SUCCESS_END"
        if(zlgjNumber){
            var linkStr='<span  style="color: #409EFF;cursor: pointer;" title="明细" onclick="linkDetail(\'' + wtId +'\',\''+status+ '\')">'+zlgjNumber+'</span>';
        }else {
            linkStr='';
        }
        return linkStr;
    }

    function onMessageActionRenderer(e) {
        var record = e.record;
        var gjId = record.gjId;
        var s = '';
        s += '<span   style="color:dodgerblue" title="查看" onclick="gjllDetail(\'' + gjId + '\')">查看</span>';
        return s;
    }

    function linkDetail(wtId, status) {
        var action = "detail";
        var url = jsUseCtxPath + "/xjsdr/core/zlgj/editPage.do?action=" + action + "&wtId=" + wtId + "&status=" + status;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (gjllListGrid) {
                    gjllListGrid.reload()
                }
            }
        }, 1000);
    }

    function wtlxRenderer(e) {
        var record = e.record;
        var wtlx = record.wtlx;
        var wtlxName='';
        switch (wtlx) {
            case 'XPSZ':
                wtlxName='新品试制';
                break;
            case 'XPZDSY':
                wtlxName='新品整机试验';
                break;
            case 'XPLS':
                wtlxName='新品路试';
                break;
            case 'CNWT':
                wtlxName='厂内问题';
                break;
            case 'SCWT':
                wtlxName='市场问题';
                break;
            case 'HWWT':
                wtlxName='海外问题';
                break;
            case 'WXBLX':
                wtlxName='维修便利性';
                break;
            case 'LBJSY':
                wtlxName='新品零部件试验';
                break;
        }
        return wtlxName;
    }

    function fujian(e) {
        var record = e.record;
        var gjId = record.gjId;
        var s = '';
        s += '<span style="color:dodgerblue" title="图片列表" onclick="gjllFile(\'' + gjId + '\')">附件列表</span>';
        return s;
    }
    function exportGjll() {
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
        $("#filter").val(mini.encode(params));
        var excelForm = $("#excelForm");
        excelForm.submit();
    }


    function linkGjll() {
        var rows = gjllListGrid.getSelecteds();
        if (rows.length <= 0) {
            mini.alert("请至少选中一条记录");
            return;
        }
        var ids = [];
        for (var i = 0, l = rows.length; i < l; i++) {
            var r = rows[i];
            ids.push(r.gjId);
        }
        var url = jsUseCtxPath + "/Gjll/linkGjll.do?belongbj=" + belongbj;
        _SubmitJson({
            url: url,
            method: 'POST',
            showMsg: false,
            data: {ids: ids.join(',')},
            success: function (data) {
                if (data) {
                    mini.alert(data.message);
                    searchStandard();
                }
            }
        });
    }


    /**
     * 关联特性要求查询
     */
    function searchRequest() {
        var queryParam = [];
        //其他筛选条件
        // var requestType = $.trim(mini.get("requestType").getValue());
        // if (requestType) {
        //     queryParam.push({name: "requestType", value: requestType});
        // }
        var requestDesc = $.trim(mini.get("requestDesc").getValue());
        if (requestDesc) {
            queryParam.push({name: "requestDesc", value: requestDesc});
        }
        // queryParam.push({name: "instStatus", value: "SUCCESS_END"});

        var data = {};
        data.filter = mini.encode(queryParam);
        data.pageIndex = requestListGrid.getPageIndex();
        data.pageSize = requestListGrid.getPageSize();
        data.sortField = requestListGrid.getSortField();
        data.sortOrder = requestListGrid.getSortOrder();
        //查询
        requestListGrid.load(data);
    }
    
    function addRelExp() {
        selectExpWindow.show();
    }


    /**
     * 关联经验教训确定按钮
     */
    function selectExpOK() {
        var rows = gjllListGrid.getSelecteds();
        if (rows.length > 0) {
            var relExpIds = [];
            for (var i = 0, l = rows.length; i < l; i++) {
                var r = rows[i];
                relExpIds.push(r.gjId);
            }
            _SubmitJson({
                url: jsUseCtxPath + "/drbfm/single/createExpRel.do",
                method: 'POST',
                showMsg: false,
                data: { relExpIds: relExpIds.join(','), partId: singleId},
                success: function (data) {
                    if (data) {
                        mini.alert(data.message);
                        relGjllListGrid.load();
                    }
                }
            });
        }else {
            mini.alert("请选择关联经验教训！");
            return;
        }
        selectExpHide();
    }

    /**
     * 关联特性要求关闭按钮
     */
    function selectExpHide() {
        selectExpWindow.hide();
        // mini.get("requestType").setValue('');
        // mini.get("requestType").setText('');
        // mini.get("request").setValue('');
        // mini.get("request").setText('');
    }
    


    function delRelExp(record) {
        var rows = [];
        if (record) {
            rows.push(record);
        } else {
            rows = relGjllListGrid.getSelecteds();
        }
        if (rows.length <= 0) {
            mini.alert("请至少选中一条记录");
            return;
        }
        mini.confirm("确定删除选中记录？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                debugger;
                var rowIds = [];
                for (var i = 0, l = rows.length; i < l; i++) {
                    var r = rows[i];
                    rowIds.push(r.recordId);
                }

                if (rowIds.length > 0) {
                    _SubmitJson({
                        url: jsUseCtxPath + "/drbfm/single/deleteExpRel.do",
                        method: 'POST',
                        showMsg: false,
                        data: {ids: rowIds.join(',')},
                        success: function (data) {
                            if (data) {
                                mini.alert(data.message);
                                relGjllListGrid.load();
                            }
                        }
                    });
                }
            }
        });
    }



    function fileupload() {
        if(!singleId){
            mini.alert("添加附件信息异常!");
            return;
        }
        mini.open({
            title: "添加附件",
            url: jsUseCtxPath + "/drbfm/single/openExpUploadWindow.do?belongSingleId="+singleId,
            width: 850,
            height: 550,
            showModal:true,
            allowResize: true,
            ondestroy: function () {
                if(fileListGrid) {
                    fileListGrid.load();
                }
            }
        });
    }
    function downLoadFile(fileName, fileId, formId, urlValue) {
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", jsUseCtxPath + urlValue);
        var inputFileName = $("<input>");
        inputFileName.attr("type", "hidden");
        inputFileName.attr("name", "fileName");
        inputFileName.attr("value", fileName);
        var inputFileId = $("<input>");
        inputFileId.attr("type", "hidden");
        inputFileId.attr("name", "fileId");
        inputFileId.attr("value", fileId);
        var inputFormId = $("<input>");
        inputFormId.attr("type", "hidden");
        inputFormId.attr("name", "formId");
        inputFormId.attr("value", formId);
        $("body").append(form);
        form.append(inputFileName);
        form.append(inputFileId);
        form.append(inputFormId);
        form.submit();
        form.remove();
        debugger;
    }

    function operationRenderer(e) {
        var record = e.record;
        if (!record.id) {
            return "";
        }
        var cellHtml = returnPreviewSpan(record.fileName, record.id, record.belongSingleId, coverContent);
        var downloadUrl = '/drbfm/single/fileDownload.do';
        if (record) {
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title=' + "下载" + ' style="color:#409EFF;cursor: pointer;" ' +
                'onclick="downLoadFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.belongSingleId + '\',\'' + downloadUrl + '\')">' + "下载" + '</span>';
        } else {
            cellHtml += '&nbsp;&nbsp;&nbsp;<span  title=' + "下载" + ' style="color: silver" >' + "下载" + '</span>';
        }
        if (record && (currentUserId == "1" || record.CREATE_BY_ == currentUserId) && action != "detail" ) {
            var deleteUrl = "/drbfm/single/deleteFiles.do";
            cellHtml += '&nbsp;&nbsp;&nbsp;<span title=' + "删除" + ' style="color:#409EFF;cursor: pointer;" ' +
                'onclick="deleteFile(\'' + record.fileName + '\',\'' + record.id + '\',\'' + record.belongSingleId + '\',\'' + deleteUrl + '\')">' + "删除" + '</span>';
        } else {
            cellHtml += '&nbsp;&nbsp;&nbsp;<span  title=' + "删除" + ' style="color: silver" > ' + "删除" + ' </span>';
        }
        return cellHtml;
    }

    function returnPreviewSpan(fileName, fileId, formId, coverContent) {
        debugger;
        var s = '';
        if (fileName == "") {
            return s;
        }
        debugger;
        var fileType = getFileType(fileName);
        if (fileType == 'other') {
            s = '&nbsp;&nbsp;&nbsp;<span  title=' + "预览" + ' style="color: silver" >' + "预览" + '</span>';
        } else {
            var url = '/drbfm/single/preview.do?fileType=' + fileType;
            s = '<span  title=' + "预览" + ' style="color:#409EFF;cursor: pointer;" onclick="previewPdf6(\'' + fileName + '\',\'' + fileId + '\',\'' + formId + '\',\'' + coverContent + '\',\'' + url + '\',\'' + fileType + '\')">' + "预览" + '</span>';
        }
        return s;
    }

    function deleteFile(fileName, fileId, formId, urlValue) {
        mini.confirm("确定删除？", "提示",
            function (action) {
                if (action == "ok") {
                    var url = jsUseCtxPath + urlValue;
                    var data = {
                        fileName: fileName,
                        id: fileId,
                        formId: formId
                    };
                    $.ajax({
                        url: url,
                        method: 'post',
                        contentType: 'application/json',
                        data: mini.encode(data),
                        success: function (json) {
                            fileListGrid.load();
                        }
                    });
                }
            }
        );
    }

</script>
<redxun:gridScript gridId="gjllListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
