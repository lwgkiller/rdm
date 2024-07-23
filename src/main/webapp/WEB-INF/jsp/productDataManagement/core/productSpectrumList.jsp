<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>产品型谱列表</title>
    <%@include file="/commons/list.jsp" %>
    <script src="${ctxPath}/scripts/rdmZhgl/rdmZhglUtil.js?version=${static_res_version}"
            type="text/javascript"></script>
</head>
<body>
<div class="mini-toolbar">
    <div class="searchBox">
        <form id="searchForm" class="search-form" style="margin-bottom: 25px">
            <input class="mini-hidden" id="isComplex" name="isComplex"/>
            <ul>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.productSpectrumList.wlh"/>：</span>
                    <input class="mini-textbox" id="materialCode" name="materialCode"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.productSpectrumList.sjxh"/>：</span>
                    <input class="mini-textbox" id="designModel" name="designModel"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.productSpectrumList.xsxh"/>：</span>
                    <input class="mini-textbox" id="saleModel" name="saleModel"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.productSpectrumList.cps"/>：</span>
                    <input class="mini-textbox" id="departName" name="departName"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.productSpectrumList.cpzg"/>：</span>
                    <input class="mini-textbox" id="productManagerName" name="productManagerName"/>
                </li>
                <li style="margin-right: 15px">
                    <span class="text" style="width:auto"><spring:message code="page.productSpectrumList.pfjd"/>：</span>
                    <input id="pfjd" name="dischargeStage" class="mini-combobox" style="width:98%;"
                           textField="text" valueField="key_" emptyText="请选择..."
                           required="false" allowInput="false" showNullItem="false"
                           multiSelect="true"
                           url="${ctxPath}/sys/core/commonInfo/getDicItem.do?dicType=PFBZ"/>
                    </td>
                </li>
                <li class="liBtn">
					<span class="unfoldBtn" onclick="no_more(this,'moreBox')">
						<em><spring:message code="page.productSpectrumList.zk"/></em>
						<i class="unfoldIcon"></i>
					</span>
                </li>
                <div id="moreBox">
                    <ul>
                        <li style="margin-right: 15px">
                            <span class="text" style="width:auto"><spring:message code="page.productSpectrumList.yfzt"/>：</span>
                            <input id="rdStatus" name="rdStatus" class="mini-combobox" style="width:98%;"
                                   textField="value" valueField="key"
                                   emptyText="<spring:message code="page.productSpectrumEdit.qxz" />..."
                                   required="false" allowInput="false" showNullItem="true"
                                   nullItemText="<spring:message code="page.productSpectrumEdit.qxz" />..."
                                   data="[{'key' : '在研','value' : '在研'}
                                       ,{'key' : '定型','value' : '定型'}
                                       ,{'key' : '作废','value' : '作废'}
                                       ]"
                            />
                        </li>
                        <li style="margin-right: 15px">
                            <span class="text" style="width:auto"><spring:message code="page.productSpectrumList.cpsm"/>：</span>
                            <input class="mini-textbox" name="productNotes" id="productNotes"/>
                        </li>
                        <li style="margin-right: 15px">
                            <span class="text" style="width:auto"><spring:message code="page.productSpectrumList.cpbq"/>：</span>
                            <input id="tagIds" name="tagIds" textname="tagNames"
                                   property="editor" class="mini-buttonedit"
                                   showClose="true"
                                   allowInput="false"
                                   oncloseclick="selectTagCloseClick()"
                                   onbuttonclick="selectTag()"
                                   style="width:98%;"/>
                            <%--<input class="mini-textbox"  name="tagNames" id="tagNames"/>--%>
                        </li>
                        <li style="margin-right: 15px">
                            <span class="text" style="width:auto"><spring:message code="page.productSpectrumList.zzzt"/>：</span>
                            <input id="manuStatus" name="manuStatus" class="mini-combobox" style="width:98%;"
                                   textField="value" valueField="key"
                                   emptyText="<spring:message code="page.productSpectrumEdit.qxz" />..."
                                   required="false" allowInput="false" showNullItem="true"
                                   nullItemText="<spring:message code="page.productSpectrumEdit.qxz" />..."
                                   data="[{'key' : '未生产','value' : '未生产'}
                                       ,{'key' : '量产','value' : '量产'}
                                       ,{'key' : '样机','value' : '样机'}
                                       ,{'key' : '停产','value' : '停产'}
                                       ]"
                            /></li>
                        <li style="margin-right: 15px">
                            <span class="text" style="width:auto"><spring:message code="page.productSpectrumList.xszt"/>：</span>
                            <input id="saleStatus" name="saleStatus" class="mini-combobox" style="width:98%;"
                                   textField="value" valueField="key"
                                   emptyText="<spring:message code="page.productSpectrumEdit.qxz" />..."
                                   required="false" allowInput="false" showNullItem="true"
                                   nullItemText="<spring:message code="page.productSpectrumEdit.qxz" />..."
                                   data="[{'key' : '在售','value' : '在售'}
                                       ,{'key' : '停售','value' : '停售'}
                                       ,{'key' : '不可售','value' : '不可售'}
                                       ]"
                            /></li>
                        <li style="margin-right: 15px">
                            <span class="text" style="width:auto"><spring:message
                                    code="page.productSpectrumList.ghnwxs"/>：</span>
                            <input id="abroad" name="abroad" class="mini-combobox" style="width:98%;"
                                   textField="value" valueField="key"
                                   emptyText="<spring:message code="page.productSpectrumEdit.qxz" />..."
                                   required="false" allowInput="false" showNullItem="true"
                                   nullItemText="<spring:message code="page.productSpectrumEdit.qxz" />..."
                                   multiSelect="true"
                                   data="[{'key' : '国内','value' : '国内'}
                                       ,{'key' : '出口','value' : '出口'}
                                       ]"
                            /></li>
                        <li style="margin-right: 15px">
                            <span class="text" style="width:auto"><spring:message
                                    code="page.productSpectrumList.ghxsqyhgj"/>：</span>
                            <input id="region" name="region" class="mini-combobox" style="width:98%"
                                   multiSelect="true"
                                   url="${ctxPath}/sys/core/sysDic/getByDicKey.do?dicKey=CPXPGHXSQYGJ"
                                   valueField="name" textField="name"/>
                        </li>
                        <li style="margin-right: 15px">
                            <span class="text" style="width:auto"><spring:message
                                    code="page.productSpectrumList.gysmc"/>：</span>
                            <input class="mini-textbox" name="supplyName" id="supplyName"/>
                        </li>
                        <li style="margin-right: 15px">
                            <span class="text" style="width:auto"><spring:message
                                    code="page.productSpectrumList.pzbwlh"/>：</span>
                            <input class="mini-textbox" name="settingMaterialCode" id="settingMaterialCode"/>
                        </li>
                        <li style="margin-right: 15px">
                            <span class="text" style="width:auto"><spring:message
                                    code="page.productSpectrumList.pzbxh"/>：</span>
                            <input class="mini-textbox" name="settingModel" id="settingModel"/>
                        </li>

                        <li style="margin-right: 15px">
                            <span class="text" style="width:auto"><spring:message
                                    code="page.productSpectrumList.qzsjc"/> </span>:<input id="startTime"
                                                                                           name="startTime"
                                                                                           class="mini-datepicker"
                                                                                           format="yyyy-MM"
                                                                                           style="width:120px"/>
                        </li>
                        <li style="margin-right: 15px">
                            <span class="text-to" style="width:auto"><spring:message code="page.productSpectrumList.z"/>: </span><input
                                id="endTime" name="endTime"
                                class="mini-datepicker"
                                format="yyyy-MM"
                                style="width:120px"/>
                        </li>
                        <li style="margin-right: 15px"><span class="text" style="width:auto">流程状态: </span>
                            <input id="instStatus" name="instStatus" class="mini-combobox" style="width:150px;"
                                   textField="value" valueField="key" emptyText="请选择..."
                                   required="false" allowInput="false" showNullItem="true" nullItemText="请选择..."
                                   data="[ {'key' : 'DRAFTED','value' : '草稿'},{'key' : 'RUNNING','value' : '审批中'},{'key' : 'SUCCESS_END','value' : '批准'},{'key' : 'DISCARD_END','value' : '作废'}]"
                            />
                        </li>
                    </ul>
                </div>
                <li style="margin-left: 10px">
                <li style="margin-left: 10px">
                    <a class="mini-button" style="margin-right: 5px" plain="true" onclick="searchFrm()"><spring:message
                            code="page.productSpectrumList.cx"/></a>
                    <a class="mini-button btn-red" style="margin-right: 5px" plain="true"
                       onclick="clearForm()"><spring:message code="page.productSpectrumList.qkcx"/></a>
                    <div style="display: inline-block" class="separator"></div>
                    <a class="mini-button" style="margin-right: 5px" plain="true"
                       onclick="addBusiness()"><spring:message code="page.productSpectrumList.xz"/></a>
                    <a class="mini-button" style="margin-right: 5px" plain="true"
                       onclick="editBusiness()">型谱管理员编辑</a>
                    <a class="mini-button" style="margin-right: 5px" plain="true"
                       onclick="changeSaleStatus('销售状态')">修改销售状态</a>
                    <a class="mini-button" style="margin-right: 5px" plain="true"
                       onclick="changeSaleStatus('制造状态')">修改制造状态</a>
                    <a class="mini-button" style="margin-right: 5px" plain="true"
                       onclick="changeSaleStatus('研发状态')">修改研发状态</a>
                    <%--                    <a class="mini-button" style="margin-right: 5px" plain="true"
                                           onclick="changeSaleStatus('其他内容')">修改其他内容</a>--%>

                </li>
            </ul>
        </form>
        <span class="searchSelectionBtn" onclick="no_search(this ,'searchForm')">
				<i class="icon-sc-lower"></i>
        </span>
    </div>
</div>

<div class="mini-fit" style="height: 100%;">
    <div id="businessListGrid" class="mini-datagrid" style="width: 100%; height: 100%;"
         allowResize="false" allowCellWrap="false" showCellTip="true" idField="id"
         allowCellEdit="true" allowCellSelect="true" multiSelect="false" showColumnsMenu="false"
         sizeList="[50,100,200,500]" pageSize="50" allowAlternating="true" pagerButtons="#pagerButtons"
         allowSortColumn="true" allowUnselect="true" autoLoad="false"
         url="${ctxPath}/world/core/productSpectrum/applyList.do">
        <div property="columns">
            <div type="checkcolumn" width="20"></div>
            <div cellCls="actionIcons" width="80" headerAlign="center" align="center" renderer="onActionRenderer"
                 cellStyle="padding:0;"><spring:message code="page.productSpectrumList.zc"/>
            </div>
            <div field="materialCode" width="120" headerAlign="center" align="center" renderer="render"
                 allowSort="true">
                <spring:message code="page.productSpectrumList.wlh"/>
            </div>
            <div field="designModel" width="150" headerAlign="center" align="center" renderer="render" allowSort="true">
                <spring:message code="page.productSpectrumList.sjxh"/>
            </div>
            <div field="departName" width="120" headerAlign="center" align="center" renderer="render" allowSort="true">
                <spring:message code="page.productSpectrumList.cps"/>
            </div>
            <div field="productManagerName" width="80" headerAlign="center" align="center" renderer="render"
                 allowSort="true">
                <spring:message code="page.productSpectrumList.cpzg"/>
            </div>
            <div field="saleModel" width="120" headerAlign="center" align="center" renderer="render" allowSort="true">
                <spring:message code="page.productSpectrumList.xsxh"/>
            </div>
            <div field="dischargeStage" width="120" headerAlign="center" align="center" renderer="render"
                 allowSort="true">
                <spring:message code="page.productSpectrumList.pfjd"/>
            </div>
            <div field="lxsj" dateFormat="yyyy-MM-dd" align="center" headerAlign="center"
                 allowSort="true">立项时间
            </div>
            <div field="rdStatus" width="120" headerAlign="center" align="center" renderer="render" allowSort="true">
                <spring:message code="page.productSpectrumList.yfzt"/>
            </div>
            <div field="yfztqr" dateFormat="yyyy-MM-dd" align="center" headerAlign="center"
                 allowSort="true">研发状态确认时间
            </div>
            <div field="productNotes" width="180" headerAlign="center" align="center" renderer="render"
                 allowSort="true">
                <spring:message code="page.productSpectrumList.cpsm"/>
            </div>
            <div field="tagNames" width="240" headerAlign="center" align="center" renderer="render" allowSort="true">
                <spring:message code="page.productSpectrumList.cpbq"/>
            </div>
            <div field="manuStatus" width="80" headerAlign="center" align="center" renderer="render" allowSort="true">
                <spring:message code="page.productSpectrumList.zzzt"/>
            </div>
            <div field="zzztqr" dateFormat="yyyy-MM-dd" align="center" headerAlign="center"
                 allowSort="true">制造状态确认时间
            </div>
            <div field="saleStatus" width="80" headerAlign="center" align="center" renderer="render" allowSort="true">
                <spring:message code="page.productSpectrumList.xszt"/>
            </div>
            <div field="xsztqr" dateFormat="yyyy-MM-dd" align="center" headerAlign="center"
                 allowSort="true">销售状态确认时间
            </div>
            <div field="abroad" width="120" headerAlign="center" align="center" renderer="render" allowSort="true">
                <spring:message code="page.productSpectrumList.ghnwxs"/>
            </div>
            <div field="region" width="180" headerAlign="center" align="center" renderer="render" allowSort="true">
                <spring:message code="page.productSpectrumList.ghxsqyhgj"/>
            </div>
            <div field="taskName" headerAlign='center' align='center' width="80">当前任务</div>
            <div field="allTaskUserNames" headerAlign='center' align='center' width="120">当前处理人</div>
            <div field="status" width="60" headerAlign="center" align="center" allowSort="true"
                 renderer="onStatusRenderer">状态
            </div>
            <div field="CREATE_TIME_" dateFormat="yyyy-MM-dd HH:mm:ss" align="center" headerAlign="center"
                 allowSort="true">创建时间
            </div>


        </div>
    </div>
</div>

<div id="selectTagWindow" title="<spring:message code="page.productSpectrumList.xzbq" />" class="mini-window"
     style="width:1340px;height:550px;"
     showModal="true" showFooter="true" allowResize="true" showCloseButton="false">
    <div class="mini-toolbar">
        <div class="searchBox">
            <form id="searchWindowForm" class="search-form" style="margin-bottom: 25px">
                <ul>
                    <li style="margin-right: 15px"><span class="text" style="width:auto"><spring:message
                            code="page.productSpectrumList.bqmc"/>: </span><input
                            class="mini-textbox" id="tagName" onenter="searchTag"></li>
                    <li style="margin-right: 15px">
                        <span class="text" style="width:auto"><spring:message
                                code="page.productSpectrumList.sfbx"/>: </span>
                        <input id="tagStatus" name="tagStatus" class="mini-combobox" style="width:98%;"
                               textField="value" valueField="key"
                               emptyText="<spring:message code="page.productSpectrumList.qxz" />..."
                               required="false" allowInput="false" showNullItem="true"
                               nullItemText="<spring:message code="page.productSpectrumList.qxz" />..."
                               data="[{'key' : '是','value' : '是'},{'key' : '否','value' : '否'}]"/>
                    </li>
                    <li style="margin-left: 10px">
                        <a class="mini-button" style="margin-right: 5px" plain="true"
                           onclick="searchTag()"><spring:message code="page.productSpectrumList.cx"/></a>
                        <a class="mini-button btn-red" style="margin-right: 5px" plain="true"
                           onclick="clearSearchTag()"><spring:message code="page.productSpectrumList.qkcx"/></a>
                        <div style="display: inline-block" class="separator"></div>
                    </li>
                </ul>
            </form>
        </div>
    </div>
    <div class="mini-fit">
        <div id="tagListGrid" class="mini-datagrid" allowResize="false" style="height: 90%"
             idField="id" allowCellEdit="false" allowCellSelect="true" allowSortColumn="false"
             multiSelect="true" showColumnsMenu="false" allowAlternating="true" showPager="false"
             url="${ctxPath}/world/core/productSpectrum/tagListQuery.do"
             allowCellWrap="true" showVGridLines="true">
            <div property="columns">
                <div type="checkcolumn" width="60px"></div>
                <div field="key_" align="center" headerAlign="center" width="150px" align="left"><spring:message
                        code="page.productSpectrumList.bqlx"/>
                </div>
                <div field="text" align="center" headerAlign="center" width="90px" align="left"><spring:message
                        code="page.productSpectrumList.bqmc"/>
                </div>
                <div field="value" align="center" headerAlign="center" width="90px" align="left"><spring:message
                        code="page.productSpectrumList.sfbt"/>
                </div>
            </div>
        </div>
        <div property="footer" style="padding:10px;height: 35px">
            <table style="width:100%;height: 100%">
                <tr>
                    <td style="width:120px;text-align:center;">
                        <input type="button" style="height: 25px;width: 70px"
                               value="<spring:message code="page.productSpectrumList.qd" />" onclick="selectTagOK()"/>
                        <input type="button" style="height: 25px;width: 70px"
                               value="<spring:message code="page.productSpectrumList.qx" />" onclick="selectTagHide()"/>
                    </td>
                </tr>
            </table>
        </div>
    </div>
</div>


<form id="excelForm" action="${ctxPath}/world/core/productSpectrum/exportData.do?menuType=${menuType}" method="post"
      target="excelIFrame">
    <input type="hidden" name="filter" id="filter"/>
</form>
<iframe id="excelIFrame" name="excelIFrame" style="display: none;"></iframe>


<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var businessListGrid = mini.get("businessListGrid");
    var tagListGrid = mini.get("tagListGrid");
    var currentUserId = "${currentUserId}";
    var currentUserName = "${currentUserName}";
    var currentUserNo = "${currentUserNo}";
    var currentTime = "${currentTime}";
    var selectTagWindow = mini.get("selectTagWindow");
    var menuType = "${menuType}";
    var isCpxp = "${isCpxp}";
    var coverContent = "${currentUserName}" + "<br/>" + currentTime + "<br/>" + xzxgwjjxyxgs;


    $(function () {
        searchFrm();
    });

    //..
    function render(e) {
        if (e.value != null && e.value != "") {
            var html = '<span style="white-space:pre-wrap;text-align:center;display:block;line-height: 20px;" >' + e.value + '</span>';
            return html;
        }
    }

    //..
    function opRenderer(e) {
        var record = e.record;
        var businessId = record.id;

        var s = '<span  title=' + roductSpectrumList_mx + ' onclick="detailBusiness(\'' + businessId + '\')">' + roductSpectrumList_mx + '</span>';
        return s;
    }

    function onActionRenderer(e) {
        var record = e.record;
        var applyId = record.id;
        var instId = record.instId;
        var status = record.status;
        var pmName = record.productManagerName;
        var s = '';
        if (status != 'DRAFTED') {
            s += '<span  title="明细" onclick="detailBusiness(\'' + applyId + '\')">明细</span>';
        }
        if (record.status == 'DRAFTED') {
            s += '<span  title="编辑" onclick="spectrumEdit(\'' + applyId + '\',\'' + instId + '\',\'' + pmName + '\')">编辑</span>';
        }

        if (status == 'RUNNING' && record.myTaskId) {
            s += '<span  title="办理" onclick="spectrumTask(\'' + record.myTaskId + '\')">办理</span>';
        }

        if (status == 'DRAFTED' || currentUserNo == 'admin') {
            s += '<span  title="删除" onclick="removeSpectrum(' + JSON.stringify(record).replace(/"/g, '&quot;') + ')">删除</span>';
        }
        return s;
    }

    //..


    //..
    function addBusiness() {
        // todo 这个暂时放开
        if (isCpxp == "false" && currentUserId != "1") {
            mini.alert("只有产品型谱管理员才可以新建");
            return;
        }
        var url = jsUseCtxPath + "/bpm/core/bpmInst/CPXPFB/start.do";
        <%--var url = jsUseCtxPath + "/world/core/productSpectrum/EditPage.do?action=add&menuType=${menuType}";--%>
        var winObj = window.open(url, '');
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (businessListGrid) {
                    businessListGrid.reload();
                }
            }
        }, 1000);
    }

    function startDraft() {
        if (currentUserId != "1") {
            mini.alert("只有管理员才可以新建");
            return;
        }
        var num = 2;
        var url = jsUseCtxPath + "/world/core/productSpectrum/startDraft.do?num=" + num
        var winObj = window.open(url, '');
    }

    //..
    function changeSaleStatus(changeType) {
        var row = businessListGrid.getSelected();
        if (!row) {
            mini.alert("请在列表中选择需要修改的数据行！")
            return;
        }
        // 这个undefined是针对历史数据，可能没有流程
        if (row.status == undefined || status == 'DRAFTED') {
            mini.alert("只有运行中或流程结束状态可以修改！")
            return;
        }

        var designModel = row.designModel;
        var modifyId = currentUserId;
        var aimType = changeType;

        $.ajax({
            url: jsUseCtxPath + '/world/core/productSpectrum/checkEditPermition.do?designModel='
            + designModel + '&modifyId=' + modifyId + '&aimType=' + aimType,
            async: false,
            success: function (result) {
                if (!result.success) {
                    mini.alert("当前用户无此型号编辑权限，请先进行权限申请！");
                    return;
                }

                var id = row.id;
                var url = jsUseCtxPath + "/world/core/productSpectrum/EditPage.do?id=" + id + '&action=spEdit' + "&changeType=" + changeType;
                var winObj = window.open(url, '');
                var loop = setInterval(function () {
                    if (winObj.closed) {
                        clearInterval(loop);
                        if (businessListGrid) {
                            businessListGrid.reload();
                        }
                    }
                }, 1000);
            },
        });
    }


    function editBusiness() {
        //todo 数据录入阶段先不做限制
        if (isCpxp == "false" && currentUserId != "1") {
            mini.alert("只有产品型谱管理员才可以编辑");
            return;
        }
        var row = businessListGrid.getSelected();
        if (!row) {
            mini.alert("请选择至少1条数据！");
            return;
        }
        var id = row.id;
        var url = jsUseCtxPath + "/world/core/productSpectrum/EditPage.do?id=" + id + '&action=spEdit' + '&changeType=其他内容';
        var winObj = window.open(url, '');
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (businessListGrid) {
                    businessListGrid.reload();
                }
            }
        }, 1000);
    }


    function spectrumTask(taskId) {
        $.ajax({
            url: jsUseCtxPath + '/bpm/core/bpmTask/checkTaskLockStatus.do?taskId=' + taskId,
            async: false,
            success: function (result) {
                if (!result.success) {
                    top._ShowTips({
                        msg: result.message
                    });
                } else {
                    var url = jsUseCtxPath + '/bpm/core/bpmTask/toStart.do?taskId=' + taskId;
                    var winObj = openNewWindow(url, "handTask");
                    var loop = setInterval(function () {
                        if (!winObj) {
                            clearInterval(loop);
                        } else if (winObj.closed) {
                            clearInterval(loop);
                            if (businessListGrid) {
                                businessListGrid.reload();
                            }
                        }
                    }, 1000);
                }
            }
        })
    }

    function removeSpectrum(record) {
        var rows = [];
        if (record) {
            rows.push(record);
        } else {
            rows = businessListGrid.getSelecteds();
        }
        if (rows.length <= 0) {
            mini.alert("请至少选中一条记录");
            return;
        }
        if (isCpxp == "false" && currentUserId != "1") {
            mini.alert("只有型谱管理员才可以删除！")
            return;
        }
        mini.confirm("确定删除选中记录？", "提示", function (action) {
            if (action != 'ok') {
                return;
            } else {
                var rowIds = [];
                var instIds = [];
                var existStartInst = false;
                for (var i = 0, l = rows.length; i < l; i++) {
                    var r = rows[i];
                    if (r.status == 'DRAFTED') {
                        rowIds.push(r.id);
                        instIds.push(r.instId);
                    } else if (currentUserNo == 'admin') {
                        rowIds.push(r.id);
                        instIds.push(r.instId);
                    } else {
                        existStartInst = true;
                        continue;
                    }
                }
                if (existStartInst) {
                    alert("只可以删除草稿状态数据！");
                }
                if (rowIds.length > 0) {
                    _SubmitJson({
                        url: jsUseCtxPath + "/world/core/productSpectrum/deleteApply.do",
                        method: 'POST',
                        showMsg: false,
                        data: {ids: rowIds.join(','), instIds: instIds.join(',')},
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


    //..
    function deleteBusiness() {
        if (isCpxp == "false" && currentUserId != "1") {
            mini.alert(roductSpectrumList_zycpsc);
            return;
        }
        var rows = businessListGrid.getSelecteds();
        if (rows.length != 1) {
            mini.alert(roductSpectrumList_qxz);
            return;
        }
        var row = rows[0];
        // if (row.CREATE_BY_ != currentUserId && currentUserNo != 'admin') {
        //     mini.alert("只有归档人才能删除");
        //     return;
        // }

        mini.confirm(roductSpectrumList_qssc, roductSpectrumList_ts, function (action) {
            if (action != 'ok') {
                return;
            } else {
                var id = row.id;
                var fileName = row.fileName;
                $.ajax({
                    url: jsUseCtxPath + "/world/core/productSpectrum/deleteBusiness.do",
                    type: 'POST',
                    data: mini.encode({id: id, fileName: fileName}),
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

    //..

    function detailBusiness(businessId) {
        var url = jsUseCtxPath + "/world/core/productSpectrum/EditPage.do?id=" + businessId + '&action=detail';
        var winObj = window.open(url, '');
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (businessListGrid) {
                    businessListGrid.reload();
                }
            }
        }, 1000);
    }

    function spectrumEdit(applyId, instId, pmName) {
        if (currentUserName != pmName && currentUserId != "1") {
            mini.alert("只有产品主管或管理员可以编辑！")
            return;
        }
        var url = jsUseCtxPath + "/bpm/core/bpmInst/start.do?instId=" + instId + "&applyId=" + applyId;
        var winObj = window.open(url);
        var loop = setInterval(function () {
            if (winObj.closed) {
                clearInterval(loop);
                if (businessListGrid) {
                    businessListGrid.reload()
                }
            }
        }, 1000);
    }


    //..
    //..


    function exportList() {
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

    function no_more(This, ID) {
        var texts = $(This).find("em").text();
        if (texts == "展开") {
            $(This).find("em").text(roductSpectrumList_sq);
        } else {
            $(This).find("em").text(roductSpectrumList_zk);
        }
        $(This).find(".unfoldIcon").toggleClass("upIcon");
        $("#" + ID + "").slideToggle(300);
        setTimeout("mini.layout()", 300);
    }


    function selectTagOK() {
        var inputList = '';
        inputList = tagListGrid;
        // 这里要改成多选，只返回id，用逗号拼接
        var selectRows = inputList.getSelecteds();
        debugger;
        if (selectRows) {
            var ids = [];
            var names = [];
            for (var i = 0; i < selectRows.length; i++) {
                var row = selectRows[i];
                ids.push(row.id);
                names.push(row.text);

            }
            var idsStr = ids.join(',');
            var namesStr = names.join(',');
            mini.get("tagIds").setValue(idsStr);
            mini.get("tagIds").setText(namesStr);
        } else {
            mini.alert(roductSpectrumList_qxzytsj);
            return;
        }
        selectTagHide();
    }

    function selectTagHide() {
        selectTagWindow.hide();
        // mini.get("standardType").setValue('');
        tagListGrid.deselectAll(true);
    }

    function selectTagClick() {
        selectTagWindow.show();
        searchTag();
    }

    //查询标签
    function searchTag() {
        var queryParam = [];
        var tagName = $.trim(mini.get("tagName").getValue());
        if (tagName) {
            queryParam.push({name: "tagName", value: tagName});
        }
        var tagStatus = $.trim(mini.get("tagStatus").getValue());
        if (tagStatus) {
            queryParam.push({name: "tagStatus", value: tagStatus});
        }

        var inputList = '';
        inputList = tagListGrid;
        var data = {};
        data.filter = mini.encode(queryParam);
        data.pageIndex = inputList.getPageIndex();
        data.pageSize = inputList.getPageSize();
        data.sortField = inputList.getSortField();
        data.sortOrder = inputList.getSortOrder();
        //查询
        inputList.load(data);
    }

    function clearSearchTag() {
        mini.get("tagName").setValue("");
        mini.get("tagStatus").setValue("");
        searchTag();
    }

    function selectTag() {
        selectTagWindow.show();
        searchTag();
    }

    function selectTagCloseClick() {
        mini.get("tagIds").setValue("");
        mini.get("tagIds").setText("");
    }

    function onStatusRenderer(e) {
        var record = e.record;
        var status = record.status;

        var arr = [{'key': 'DRAFTED', 'value': '草稿', 'css': 'orange'},
            {'key': 'RUNNING', 'value': '审批中', 'css': 'green'},
            {'key': 'SUCCESS_END', 'value': '批准', 'css': 'blue'},
            {'key': 'DISCARD_END', 'value': '作废', 'css': 'red'}
        ];

        return $.formatItemValue(arr, status);
    }

</script>
<redxun:gridScript gridId="businessListGrid" entityName="" winHeight="" winWidth="" entityTitle="" baseUrl=""/>
</body>
</html>
