<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>欧美澳类中国售前文件审批列表</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/rdmZhgl/saleFileOMAApplyList.js?version=${static_res_version}"
            type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">负责人: </span>
                    <input class="mini-textbox" name="applyUserName"/>
                </li>
                <li style="margin-left:15px;margin-right: 15px"><span class="text" style="width:auto"><spring:message code="page.saleFileApplyList.name1" />: </span>
                    <input id="instStatus" name="instStatus" class="mini-combobox" style="width:150px;"
                           textField="value" valueField="key" emptyText="<spring:message code="page.saleFileApplyList.name2" />..." onvaluechanged="searchFrm()"
                           required="false" allowInput="false" showNullItem="true" nullItemText="<spring:message code="page.saleFileApplyList.name2" />..."
                           data="[ {'key' : 'DRAFTED','value' : '草稿'},{'key' : 'RUNNING','value' : '运行中'},{'key' : 'SUCCESS_END','value' : '成功结束'},{'key' : 'DISCARD_END','value' : '作废'},{'key' : 'ABORT_END','value' : '异常中止结束'},{'key' : 'PENDING','value' : '挂起'}]"
                    />
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">设计型号: </span>
                    <input class="mini-textbox" name="designModel"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">销售型号: </span>
                    <input class="mini-textbox" name="saleModel"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto">文件类型: </span>
                    <input id="fileType" name="fileType" class="mini-combobox rxc" plugins="mini-combobox"
                           style="width:100%;height:34px" label="<spring:message code="page.saleFileApplyEdit.name5" />："  onvaluechanged="onChangeFileType"
                           length="50"
                           only_read="false" required="true" allowinput="false" mwidth="100"
                           wunit="%" mheight="34" hunit="px" shownullitem="true" multiSelect="false"
                           textField="text" valueField="key_" emptyText="<spring:message code="page.saleFileApplyEdit.name6" />..."
                           url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=sailFileOMA_WJFL"
                           nullitemtext="<spring:message code="page.saleFileApplyEdit.name6" />..." emptytext="<spring:message code="page.saleFileApplyEdit.name6" />..."/>
                </li>

                <li class="liBtn">
					<span class="unfoldBtn" onclick="no_more(this,'moreBox')">
						<em><spring:message code="page.saleFileApplyList.name3" /></em>
						<i class="unfoldIcon"></i>
					</span>
                </li>

                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()"><spring:message code="page.saleFileApplyList.name4" /></a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true" onclick="clearForm()"><spring:message code="page.saleFileApplyList.name5" /></a>
                    <div style="display: inline-block" class="separator"></div>
<%--                    <a id="deptApply" class="mini-button " style="margin-left: 5px;" plain="true" onclick="doApply()">新增申请</a>--%>
<%--                    <f:a alias="saleFile-copyBusiness" onclick="copyBusiness()"  style="margin-left: 10px">多语言归档</f:a>--%>
                    <f:a alias="saleFile-releaseBusiness" onclick="releaseBusiness()" showNoRight="false"
                         style="margin-left: 5px">发布</f:a>
                    <a class="mini-button btn-red" style="margin-left: 10px" plain="true" onclick="removeApply()"><spring:message code="page.saleFileApplyList.name6" /></a>
                    <f:a alias="saleFileOMAApply-exportBusiness" onclick="exportBusiness()" showNoRight="false"
                         style="margin-left: 10px">导出</f:a>
                    <f:a alias="saleFileOMAApply-downloadBusiness" onclick="downloadBusiness()" showNoRight="false"
                         style="margin-left: 10px">批量下载</f:a>
                </li>
            </ul>
            <div id="moreBox">
                <ul>
                    <li style="margin-left:15px;margin-right: 15px">
                        <span class="text" style="width:auto"><spring:message code="page.saleFileApplyList.name8" />: </span>
                        <input class="mini-textbox" name="applyId"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto"><spring:message code="page.saleFileApplyList.name9" /> </span>:<input name="apply_startTime"
                                                                                    class="mini-datepicker"
                                                                                    format="yyyy-MM-dd"
                                                                                    style="width:100px"/>
                    </li>
                    <li style="margin-right: 15px">
                        <span class="text-to" style="width:auto"><spring:message code="page.saleFileApplyList.name10" />: </span><input name="apply_endTime"
                                                                                  class="mini-datepicker"
                                                                                  format="yyyy-MM-dd"
                                                                                  style="width:100px"/>
                    </li>
                </ul>
            </div>
        </form>
    </div>
</div>
<div class="mini-fit" style="height: 100%;">
    <div id="applyListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
         url="${ctxPath}/rdmZhgl/core/saleFileOMA/queryList.do?applyType=${applyType}"
         idField="applyId" sortOrder="desc" sortField="applyTime"
         multiSelect="true" showColumnsMenu="false" sizeList="[20,50,100,200]" pageSize="20" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div name="action" cellCls="actionIcons" width="100" headerAlign="center" align="center"
                 renderer="onActionRenderer" cellStyle="padding:0;"><spring:message code="page.saleFileApplyList.name11" />
            </div>
            <div field="id" sortField="id" width="100" headerAlign="center" align="center" allowSort="true"><spring:message code="page.saleFileApplyList.name8" /></div>
            <div field="userName" sortField="userName" width="40" headerAlign="center" align="center" allowSort="true">
                负责人
            </div>
            <div field="fileType" width="70" headerAlign="center" align="center" allowSort="false" renderer="onWSwitchType"><spring:message code="page.saleFileApplyList.name14" /></div>
            <div field="designModel" width="80" headerAlign="center" align="center" allowSort="false"><spring:message code="page.saleFileApplyList.name12" /></div>
            <div field="materialCode"  width="70" headerAlign="center" align="center" allowSort="false">物料编码</div>
            <div field="saleModel" width="60" headerAlign="center" align="center" allowSort="false"><spring:message code="page.saleFileApplyList.name13" /></div>
            <div field="language" width="35" headerAlign="center" align="center" allowSort="false" >语种</div>
            <div field="version" width="70" headerAlign="center" align="center" allowSort="true" >版本</div>
            <div field="systemType" width="70" headerAlign="center" align="center" allowSort="true" renderer="onSwitchSystemType">系统分类</div>
            <div field="applicabilityDoc" width="70" headerAlign="center" align="center" allowSort="false" >适用性说明</div>
            <div field="fileStatus" width="50" headerAlign="center" align="center" allowSort="true" >文件状态</div>
            <div field="region" width="70" headerAlign="center" align="center" allowSort="true" >销售区域</div>
            <div field="instStatus" width="70" headerAlign="center" align="center" renderer="onStatusRenderer"
                 allowSort="false"><spring:message code="page.saleFileApplyList.name15" />
            </div>
            <div field="currentProcessUser" sortField="currentProcessUser" width="50" align="center"
                 headerAlign="center" allowSort="false"><spring:message code="page.saleFileApplyList.name16" />
            </div>
            <div field="currentProcessTask" sortField="currentProcessTask" width="80" align="center"
                 headerAlign="center" allowSort="false"><spring:message code="page.saleFileApplyList.name17" />
            </div>
            <div field="applyTime" sortField="applyTime" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" width="100"
                 headerAlign="center" allowSort="true"><spring:message code="page.saleFileApplyList.name18" />
            </div>
            <div field="INST_ID_" visible="false"></div>
            <div field="taskId" visible="false"></div>
            <div field="processTask" visible="false"></div>
        </div>
    </div>
</div>

<!--导出Excel相关HTML-->
<form id="excelForm" action="${ctxPath}/rdmZhgl/core/saleFileOMA/exportList.do" method="post" target="excelIFrame">
    <input type="hidden" name="filter" id="filter"/>
</form>
<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>

<div id="remarkWindow" title="关联产品记录" class="mini-window" style="width:950px;height:400px;"
     showModal="true" showFooter="false" allowResize="true">
    <div class="mini-fit" style="font-size: 14px;margin-top: 4px">
        <div id="remarkListGrid" class="mini-datagrid" style="width: 100%; height: 98%" allowResize="false" allowCellWrap="true"
             showCellTip="true" idField="id" showPager="false" showColumnsMenu="false" allowcelledit="false"
             allowAlternating="true">
            <div property="columns">
                <div field="salesModel_item" width="100" headerAlign="center" align="center">销售型号</div>
                <div field="designModel_item" width="100" headerAlign="center" align="center">设计型号</div>
                <div field="materialCode_item" width="100" headerAlign="center" align="center">物料编码</div>
                <div field="cpzgId_item_name" width="100" headerAlign="center" align="center">产品主管</div>
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var applyListGrid = mini.get("applyListGrid");
    var currentUserId = "${currentUser.userId}";
    // var applyTypeList = getDics("YDGZSPLX");
    var applyType = "${applyType}";
    var typeList = getDics("sailFileOMA_WJFL");
    var systemTypelist =getDics("sailFileOMA_XTFL");
    var remarkWindow = mini.get("remarkWindow");
    var remarkListGrid = mini.get("remarkListGrid");
    //行功能按钮
    function onActionRenderer(e) {
        var record = e.record;
        var applyId = record.id;
        var instId = record.INST_ID_;
        var fileStatus =record.fileStatus;
        var applyType = record.applyType;
        var s = '<span  title=' + saleFileApplyList_name + ' onclick="detailApply(\'' + applyId + '\',\'' + record.instStatus + '\',\'' + applyType + '\')">' + saleFileApplyList_name + '</span>';
        s += '<span  title="查看"  onclick="remarkCheck()">关联产品</span>';
        if (record.instStatus == 'DRAFTED' ) {
            if (record.CREATE_BY_ == currentUserId) {
                s += '<span  title=' + saleFileApplyList_name1 + ' onclick="editApplyRow(\'' + applyId + '\',\'' + instId + '\',\'' + applyType + '\')">' + saleFileApplyList_name1 + '</span>';
            } else {
                s += '<span  title=' + saleFileApplyList_name1 + ' style="color: silver">' + saleFileApplyList_name1 + '</span>';
            }

        } else {
            if (record.processTask) {
                s += '<span  title=' + saleFileApplyList_name2 + ' onclick="doApplyTask(\'' + record.taskId + '\',\'' + applyType + '\')">' + saleFileApplyList_name2 + '</span>';
            } else {
                s += '<span  title=' + saleFileApplyList_name2 + ' style="color: silver">' + saleFileApplyList_name2 + '</span>';
            }
        }
        if(fileStatus == '编辑中'){
            s += '<span  title=' + saleFileApplyList_name1 + ' onclick="editCopyRow(\'' + applyId + '\')">' + saleFileApplyList_name1 + '</span>';

        }

        if ((record.CREATE_BY_ == currentUserId && (record.instStatus == 'DRAFTED' || record.instStatus == 'DISCARD_END')) || currentUserId=='1') {
            s += '<span  title=' + saleFileApplyList_name3 + ' onclick="removeApply(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">' + saleFileApplyList_name3 + '</span>';
        } else {
            s += '<span  title=' + saleFileApplyList_name3 + ' style="color: silver">' + saleFileApplyList_name3 + '</span>';
        }
        return s;
    }


    function onWSwitchType(e) {
        var record = e.record;
        var fileType = record.fileType;
        var typeText = '';
        for (var i = 0; i < typeList.length; i++) {
            if (typeList[i].key_ == fileType) {
                typeText = typeList[i].text;
                break
            }
        }
        return typeText;
    }
    function onSwitchSystemType(e) {
        var record = e.record;
        var systemType = record.systemType;
        var typeText = '';
        for (var i = 0; i < systemTypelist.length; i++) {
            if (systemTypelist[i].key_ == systemType) {
                typeText = systemTypelist[i].text;
                break
            }
        }
        return typeText;
    }
    function onStatusRenderer(e) {
        var record = e.record;
        var instStatus = record.instStatus;
        var arr = [{'key': 'DRAFTED', 'value': '草稿', 'css': 'orange'},
            {'key': 'RUNNING', 'value': '运行中', 'css': 'green'},
            {'key': 'SUCCESS_END', 'value': '成功结束', 'css': 'blue'},
            {'key': 'DISCARD_END', 'value': '作废', 'css': 'red'},
            {'key': 'ABORT_END', 'value': '异常中止结束', 'css': 'red'},
            {'key': 'PENDING', 'value': '挂起', 'css': 'gray'}
        ];

        return $.formatItemValue(arr, instStatus);
    }

    function onApplyType(e) {
        var record = e.record;
        var applyType = record.applyType;
        var resultText = '';
        for (var i = 0; i < applyTypeList.length; i++) {
            if (applyTypeList[i].key_ == applyType) {
                resultText = applyTypeList[i].text;
                break
            }
        }
        return resultText;
    }
    //关联明细展示
    function remarkCheck() {
        var record = applyListGrid.getSelected();
        if (record != null) {
            var remarkArray = JSON.parse(record.note);
            remarkListGrid.setData(remarkArray);
            remarkWindow.show();
        }
    }

    function copyBusiness() {
        var row = applyListGrid.getSelected();
        if (!row) {
            mini.alert("请至少选中一条记录");
            return;
        } else if (row.fileStatus != "已发布") {
            mini.alert("只有 已发布 状态的数据能上传多语言版本");
            return;
        }
        var id = row.id;
        var url = jsUseCtxPath+"/rdmZhgl/core/saleFileOMA/edit.do?id=" + id + '&action=copy';
        var winObj = window.open(url, '');
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (applyListGrid) {
                    applyListGrid.reload();
                }
            }
        }, 1000);
    }
    //..
    function exportBusiness() {
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

    function releaseBusiness() {
        var row = applyListGrid.getSelected();
        if (row == null) {
            mini.alert("请至少选中一条记录");
            return;
        } else if (row.fileStatus != "编辑中") {
            mini.alert("只有 编辑中 状态的数据能发布");
            return;
        }
        mini.confirm("确定发布选中记录？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                var id = row.id;
                $.ajax({
                    url: jsUseCtxPath + "/rdmZhgl/core/saleFileOMA/releaseBusiness.do",
                    type: 'POST',
                    data: mini.encode({id: id}),
                    contentType: 'application/json',
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
    function downloadBusiness(){
        var rows = applyListGrid.getSelecteds();
        if (rows.length <= 0) {
            mini.alert("请至少选中一条记录");
            return;
        }

        var ids = "";
        var fileNames = "";
        var salefileIds = "";
        var cnt = 0;
        for (var i = 0, l = rows.length; i < l; i++) {
            var r = rows[i];
            if (r.salefileId) {
                //用form的方式去提交
                if (cnt > 0) {
                    ids += ',';
                    salefileIds += ",";
                    fileNames += ",";
                }
                ids += r.id;
                salefileIds += r.salefileId;
                fileNames += r.fileName;
                cnt = cnt + 1;
            }
        }

        zipDownloadFile(ids,fileNames,salefileIds);


    }

    function zipDownloadFile(id, fileName,fileId) {
        var form = $("<form>");
        form.attr("style", "display:none");
        form.attr("target", "");
        form.attr("method", "post");
        form.attr("action", jsUseCtxPath + "/rdmZhgl/core/saleFileOMA/zipdownload.do");
        var idAttr = $("<input>");
        idAttr.attr("type", "hidden");
        idAttr.attr("name", "id");
        idAttr.attr("value", id);
        var descriptionAttr = $("<input>");
        descriptionAttr.attr("type", "hidden");
        descriptionAttr.attr("name", "fileName");
        descriptionAttr.attr("value", fileName);
        var fileIdAttr = $("<input>");
        fileIdAttr.attr("type", "hidden");
        fileIdAttr.attr("name", "fileId");
        fileIdAttr.attr("value", fileId);
        $("body").append(form);
        form.append(idAttr);
        form.append(descriptionAttr);
        form.append(fileIdAttr);
        form.submit();
        form.remove();
    }
</script>
<redxun:gridScript gridId="applyListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
