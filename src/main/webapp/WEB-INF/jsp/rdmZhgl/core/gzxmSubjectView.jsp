<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>总览</title>
    <%@include file="/commons/edit.jsp" %>
    <link href="${ctxPath}/styles/list.css?static_res_version=${static_res_version}" rel="stylesheet" type="text/css"/>
    <script src="${ctxPath}/scripts/rdmZhgl/gzxmProjectView.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
<div>
<%--    <input type="button" value="导出" onclick="printpage()" />--%>
</div>
<div class="mini-fit" style="margin-top: 10px">
    <div id="itemGrid" class="mini-datagrid" style=" height: 100%;" allowResize="true"
         url="${ctxPath}/rdmZhgl/core/gzxm/subject/items.do"
         idField="id" allowAlternating="true" showPager="false" multiSelect="true" allowCellEdit="true"
         allowCellSelect="true" allowCellWrap="true"
         ondrawcell="onDrawCell" allowRowSelect="false"
         editNextOnEnterKey="true" editNextRowCell="true">
        <div property="columns">
            <div field="finishProcessName" name="finishProcessName" displayfield="finishProcessName"
                 width="100px"
                 headerAlign="center" align="left">
                项目自评
            </div>
            <div field="indexSort" displayfield="indexSort" width="80px" headerAlign="center" align="left">
                编号
            </div>
            <div field="important" width="60px" headerAlign="center" renderer="onImportant" align="center">
                重要度
            </div>
            <div field="taskName" displayfield="taskName" width="200px" headerAlign="center"
                 align="left">
                任务名称
            </div>
            <div field="taskTarget" name="taskTarget" displayfield="taskTarget" width="300px" headerAlign="center"
                 align="left">
                任务目标
            </div>
            <div field="outputFile" displayfield="outputFile" width="300px" headerAlign="center" align="left">
                输出物
            </div>
            <div  renderer="detailAttach"  align="center" width="80" headerAlign="center">
                输出物文件
            </div>
            <div field="resDeptIds" displayField="resDeptIds" align="center" width="110px" headerAlign="center">
                责任部门
            </div>
            <div field="resUserIds" displayfield="resUserIds" width="110px" headerAlign="center" align="center">
                责任人
            </div>
            <div field="planStartDate" width="90px" headeralign="center" allowsort="true" align="center"
                 dateFormat="yyyy-MM-dd">
                计划开始时间

            </div>
            <div field="planEndDate" width="90px" headeralign="center" allowsort="true" align="center"
                 dateFormat="yyyy-MM-dd">
                计划结束时间

            </div>
            <div field="actStartDate" width="90px" headeralign="center" allowsort="true" align="center"
                 dateFormat="yyyy-MM-dd">
                实际开始时间
            </div>
            <div field="actEndDate" width="90px" headeralign="center" allowsort="true" align="center"
                 dateFormat="yyyy-MM-dd">
                实际结束时间
            </div>
            <div field="reason" displayfield="reason" width="200px" headerAlign="center" align="left">
                延期原因与补救措施
            </div>
            <div field="remark" displayfield="remark" width="100px" headerAlign="center" align="center">
                备注
            </div>
            <div field="finished" displayfield="finished" width="100px" headerAlign="center" align="center">
                完成情况
            </div>
            <div field="" width="100px" headerAlign="center" align="center">1月
                <div property="columns" align="center">
                    <div field="11" name="colorCell" width="20px" headeralign="center" align="center">1</div>
                    <div field="12" name="colorCell" width="20px" headeralign="center" align="center">2</div>
                    <div field="13" name="colorCell" width="20px" headeralign="center" align="center">3</div>
                    <div field="14" name="colorCell" width="20px" headeralign="center" align="center">4</div>
                    <div field="15" name="colorCell" width="20px" headeralign="center" align="center">5</div>
                    <div field="16" name="colorCell" width="20px" headeralign="center" align="center">6</div>
                </div>
            </div>
            <div field="" width="100px" headerAlign="center" align="center">2月
                <div property="columns" align="center">
                    <div field="21" name="colorCell" width="20px" headeralign="center" align="center">1</div>
                    <div field="22" name="colorCell" width="20px" headeralign="center" align="center">2</div>
                    <div field="23" name="colorCell" width="20px" headeralign="center" align="center">3</div>
                    <div field="24" name="colorCell" width="20px" headeralign="center" align="center">4</div>
                    <div field="25" name="colorCell" width="20px" headeralign="center" align="center">5</div>
                    <div field="26" name="colorCell" width="20px" headeralign="center" align="center">6</div>
                </div>
            </div>
            <div field="" width="100px" headerAlign="center" align="center">3月
                <div property="columns" align="center">
                    <div field="31" name="colorCell" width="20px" headeralign="center" align="center">1</div>
                    <div field="32" name="colorCell" width="20px" headeralign="center" align="center">2</div>
                    <div field="33" name="colorCell" width="20px" headeralign="center" align="center">3</div>
                    <div field="34" name="colorCell" width="20px" headeralign="center" align="center">4</div>
                    <div field="35" name="colorCell" width="20px" headeralign="center" align="center">5</div>
                    <div field="36" name="colorCell" width="20px" headeralign="center" align="center">6</div>
                </div>
            </div>
            <div field="" width="100px" headerAlign="center" align="center">4月
                <div property="columns" align="center">
                    <div field="41" name="colorCell" width="20px" headeralign="center" align="center">1</div>
                    <div field="42" name="colorCell" width="20px" headeralign="center" align="center">2</div>
                    <div field="43" name="colorCell" width="20px" headeralign="center" align="center">3</div>
                    <div field="44" name="colorCell" width="20px" headeralign="center" align="center">4</div>
                    <div field="45" name="colorCell" width="20px" headeralign="center" align="center">5</div>
                    <div field="46" name="colorCell" width="20px" headeralign="center" align="center">6</div>
                </div>
            </div>
            <div field="" width="100px" headerAlign="center" align="center">5月
                <div property="columns" align="center">
                    <div field="51" name="colorCell" width="20px" headeralign="center" align="center">1</div>
                    <div field="52" name="colorCell" width="20px" headeralign="center" align="center">2</div>
                    <div field="53" name="colorCell" width="20px" headeralign="center" align="center">3</div>
                    <div field="54" name="colorCell" width="20px" headeralign="center" align="center">4</div>
                    <div field="55" name="colorCell" width="20px" headeralign="center" align="center">5</div>
                    <div field="56" name="colorCell" width="20px" headeralign="center" align="center">6</div>
                </div>
            </div>
            <div field="" width="100px" headerAlign="center" align="center">6月
                <div property="columns" align="center">
                    <div field="61" name="colorCell" width="20px" headeralign="center" align="center">1</div>
                    <div field="62" name="colorCell" width="20px" headeralign="center" align="center">2</div>
                    <div field="63" name="colorCell" width="20px" headeralign="center" align="center">3</div>
                    <div field="64" name="colorCell" width="20px" headeralign="center" align="center">4</div>
                    <div field="65" name="colorCell" width="20px" headeralign="center" align="center">5</div>
                    <div field="66" name="colorCell" width="20px" headeralign="center" align="center">6</div>
                </div>
            </div>
            <div field="" width="100px" headerAlign="center" align="center">7月
                <div property="columns" align="center">
                    <div field="71" name="colorCell" width="20px" headeralign="center" align="center">1</div>
                    <div field="72" name="colorCell" width="20px" headeralign="center" align="center">2</div>
                    <div field="73" name="colorCell" width="20px" headeralign="center" align="center">3</div>
                    <div field="74" name="colorCell" width="20px" headeralign="center" align="center">4</div>
                    <div field="75" name="colorCell" width="20px" headeralign="center" align="center">5</div>
                    <div field="76" name="colorCell" width="20px" headeralign="center" align="center">6</div>
                </div>
            </div>
            <div field="" width="100px" headerAlign="center" align="center">8月
                <div property="columns" align="center">
                    <div field="81" name="colorCell" width="20px" headeralign="center" align="center">1</div>
                    <div field="82" name="colorCell" width="20px" headeralign="center" align="center">2</div>
                    <div field="83" name="colorCell" width="20px" headeralign="center" align="center">3</div>
                    <div field="84" name="colorCell" width="20px" headeralign="center" align="center">4</div>
                    <div field="85" name="colorCell" width="20px" headeralign="center" align="center">5</div>
                    <div field="86" name="colorCell" width="20px" headeralign="center" align="center">6</div>
                </div>
            </div>
            <div field="" width="100px" headerAlign="center" align="center">9月
                <div property="columns" align="center">
                    <div field="91" name="colorCell" width="20px" headeralign="center" align="center">1</div>
                    <div field="92" name="colorCell" width="20px" headeralign="center" align="center">2</div>
                    <div field="93" name="colorCell" width="20px" headeralign="center" align="center">3</div>
                    <div field="94" name="colorCell" width="20px" headeralign="center" align="center">4</div>
                    <div field="95" name="colorCell" width="20px" headeralign="center" align="center">5</div>
                    <div field="96" name="colorCell" width="20px" headeralign="center" align="center">6</div>
                </div>
            </div>
            <div field="" width="100px" headerAlign="center" align="center">10月
                <div property="columns" align="center">
                    <div field="101" name="colorCell" width="20px" headeralign="center" align="center">1</div>
                    <div field="102" name="colorCell" width="20px" headeralign="center" align="center">2</div>
                    <div field="103" name="colorCell" width="20px" headeralign="center" align="center">3</div>
                    <div field="104" name="colorCell" width="20px" headeralign="center" align="center">4</div>
                    <div field="105" name="colorCell" width="20px" headeralign="center" align="center">5</div>
                    <div field="106" name="colorCell" width="20px" headeralign="center" align="center">6</div>
                </div>
            </div>
            <div field="" width="100px" headerAlign="center" align="center">11月
                <div property="columns" align="center">
                    <div field="111" name="colorCell" width="20px" headeralign="center" align="center">1</div>
                    <div field="112" name="colorCell" width="20px" headeralign="center" align="center">2</div>
                    <div field="113" name="colorCell" width="20px" headeralign="center" align="center">3</div>
                    <div field="114" name="colorCell" width="20px" headeralign="center" align="center">4</div>
                    <div field="115" name="colorCell" width="20px" headeralign="center" align="center">5</div>
                    <div field="116" name="colorCell" width="20px" headeralign="center" align="center">6</div>
                </div>
            </div>
            <div field="" width="100px" headerAlign="center" align="center">12月
                <div property="columns" align="center">
                    <div field="121" name="colorCell" width="20px" headeralign="center" align="center">1</div>
                    <div field="122" name="colorCell" width="20px" headeralign="center" align="center">2</div>
                    <div field="123" name="colorCell" width="20px" headeralign="center" align="center">3</div>
                    <div field="124" name="colorCell" width="20px" headeralign="center" align="center">4</div>
                    <div field="125" name="colorCell" width="20px" headeralign="center" align="center">5</div>
                    <div field="126" name="colorCell" width="20px" headeralign="center" align="center">6</div>
                </div>
            </div>
            <div field="nextYear" name="nextYear" name="colorCell" width="60px" headerAlign="center" align="center">
            </div>
        </div>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var mainId = "${mainId}";
    var itemGrid = mini.get("itemGrid");
    var importantList = getDics("ZDXMZYJD");
    var finishProcessList = getDics("WCJD");
    var currentUserId = "${currentUser.userId}";
    itemGrid.on("load", function () {
        itemGrid.mergeColumns(["finishProcessName","taskTarget"]);
    });
    var valueStr = '';

    function onDrawCell(e) {
        var record = e.record;
        var field = e.field;
        var planStartDate = record.planStartDate;
        var actStartDate = record.actStartDate;
        var planEndDate = record.planEndDate;
        var actEndDate = record.actEndDate;
        if (field == 'finished') {
            if (actEndDate) {
                e.cellHtml = '<span style="text-align: center">已完成</span>';
                e.cellStyle = "background-color:green";
            }
        }
        if (e.column.name != 'colorCell' && e.column.name != 'nextYear') {
            if("finishProcessName"==field){
                valueStr += ' ,';
            }else{
                valueStr += e.cellHtml+',';
            }
            return;
        }
        /**
         * 时间设置规则
         * 1.实际开始时间为空， 计划开始时间前面 颜色白色，计划开始时间后面至当前时间为红色
         * 2.实际开始时间不为空，实际开始时间等于计划开始时间颜色正常，大于计划开始时间，中间时间为红色
         * */
        var planStartFlag = timeFlag(planStartDate);
        var planEndFlag = timeFlag(planEndDate);
        var iconText = '';
        //设置计划节点
        if (planEndFlag == 'nextYear') {
            if (field == 'nextYear') {
                if (record.important == '0') {
                    e.cellHtml = '<span style="color: white">★</span>';
                    iconText = '★';
                } else {
                    e.cellHtml = '<span style="color: white">●</span>';
                    iconText = '●';
                }
            }
        } else if (planEndFlag != 'preYear') {
            if (field == planEndFlag) {
                if (record.important == '0') {
                    e.cellHtml = '<span style="color: white">★</span>';
                    iconText = '★';
                } else {
                    e.cellHtml = '<span style="color: white">●</span>';
                    iconText = '●';
                }
            }
        }
        debugger
        //设置完成节点
        if (actEndDate) {
            var actEndFlag = timeFlag(actEndDate);
            if (actEndFlag == 'nextYear') {
                if (field == 'nextYear') {
                    if (record.important == '0') {
                        e.cellHtml = '<span style="color: black">★</span>';
                        iconText = '★';
                    } else {
                        e.cellHtml = '<span style="color: black">●</span>';
                        iconText = '●';
                    }
                }
            } else if (actEndFlag != 'preYear') {
                if (field == actEndFlag) {
                    if (record.important == '0') {
                        e.cellHtml = '<span style="color: black">★</span>';
                        iconText = '★';
                    } else {
                        e.cellHtml = '<span style="color: black">●</span>';
                        iconText = '●';
                    }
                }
            }
        }
        //设置条目
        if (planStartFlag != 'nextYear' && planStartFlag != 'preYear') {
            if (parseInt(planStartFlag) >= parseInt(asign)) {
                if (planEndFlag == 'nextYear') {
                    if (parseInt(field) >= parseInt(planStartFlag)) {
                        e.cellStyle = "background-color:blue;";
                    }
                    if (field == 'nextYear') {
                        e.cellStyle = "background-color:blue;";
                    }
                } else if (planEndFlag != 'preYear') {
                    if (parseInt(field) >= parseInt(planStartFlag) && parseInt(field) <= parseInt(planEndFlag)) {
                        e.cellStyle = "background-color:blue;";
                    }
                }
            } else {
                if (actStartDate) {
                    var actStartFlag = timeFlag(actStartDate);
                    if (parseInt(actStartFlag) <= parseInt(asign)) {
                        if (parseInt(planStartFlag) == parseInt(actStartFlag)) {
                            if (parseInt(field) >= parseInt(planStartFlag) && parseInt(field) <= parseInt(asign)) {
                                e.cellStyle = "background-color:green;";
                            }
                            if (planEndFlag == 'nextYear') {
                                if (parseInt(field) > parseInt(asign)) {
                                    e.cellStyle = "background-color:blue;";
                                }
                                if (field == 'nextYear') {
                                    e.cellStyle = "background-color:blue;";
                                }
                            } else if (planEndFlag != 'preYear') {
                                if (parseInt(field) >= parseInt(asign) && parseInt(field) <= parseInt(planEndFlag)) {
                                    e.cellStyle = "background-color:blue;";
                                }
                            }
                        }
                        if (parseInt(actStartFlag) > parseInt(planStartFlag)) {
                            if (parseInt(field) >= parseInt(planStartFlag) && parseInt(field) < parseInt(actStartFlag)) {
                                e.cellStyle = "background-color:red;";
                            }
                            if (parseInt(field) >= parseInt(actStartFlag) && parseInt(field) <= parseInt(asign)) {
                                e.cellStyle = "background-color:green;";
                            }
                            if (planEndFlag == 'nextYear') {
                                if (parseInt(field) > parseInt(asign)) {
                                    e.cellStyle = "background-color:blue;";
                                }
                                if (field == 'nextYear') {
                                    e.cellStyle = "background-color:blue;";
                                }
                            } else if (planEndFlag != 'preYear') {
                                if (parseInt(field) >= parseInt(asign) && parseInt(field) <= parseInt(planEndFlag)) {
                                    e.cellStyle = "background-color:blue;";
                                }
                            }
                        }
                    } else {
                        if (parseInt(field) >= parseInt(planStartFlag) && parseInt(field) <= parseInt(asign)) {
                            e.cellStyle = "background-color:red;";
                        }
                        if (planEndFlag == 'nextYear') {
                            if (parseInt(field) >= parseInt(asign)) {
                                e.cellStyle = "background-color:blue;";
                            }
                            if (field == 'nextYear') {
                                e.cellStyle = "background-color:blue;";
                            }
                        } else if (planEndFlag != 'preYear') {
                            if (parseInt(field) >= parseInt(asign) && parseInt(field) <= parseInt(planEndFlag)) {
                                e.cellStyle = "background-color:blue;";
                            }
                        }
                    }
                } else {
                    if (parseInt(field) >= parseInt(planStartFlag) && parseInt(field) <= parseInt(asign)) {
                        e.cellStyle = "background-color:red;";
                    }
                    if (planEndFlag == 'nextYear') {
                        if (parseInt(field) > parseInt(asign)) {
                            e.cellStyle = "background-color:blue;";
                        }
                        if (field == 'nextYear') {
                            e.cellStyle = "background-color:blue;";
                        }
                    } else if (planEndFlag != 'preYear') {
                        if (parseInt(field) > parseInt(asign) && parseInt(field) <= parseInt(planEndFlag)) {
                            e.cellStyle = "background-color:blue;";
                        }
                    }
                }
            }
        } else {
            if (planStartFlag == 'nextYear') {
                if (field == 'nextYear') {
                    e.cellStyle = "background-color:blue;";
                }
            } else if (planStartFlag == 'preYear') {
                if (actStartDate) {
                    var actStartFlag = timeFlag(actStartDate);
                    if (actStartFlag == 'preYear') {
                        if (planEndFlag == 'nextYear') {
                            if (parseInt(field) <= parseInt(asign)) {
                                e.cellStyle = "background-color:green;";
                            }
                            if (parseInt(field) > parseInt(asign)) {
                                e.cellStyle = "background-color:blue;";
                            }
                            if (field == 'nextYear') {
                                e.cellStyle = "background-color:blue;";
                            }
                        } else if (planEndFlag == 'preYear') {
                            if (parseInt(field) <= parseInt(asign)) {
                                e.cellStyle = "background-color:red;";
                            }
                        } else {

                            if (parseInt(planEndFlag) <= parseInt(asign)) {
                                if (parseInt(field) <= parseInt(planEndFlag)) {
                                    e.cellStyle = "background-color:green;";
                                }
                            } else {
                                if (parseInt(field) <= parseInt(asign)) {
                                    e.cellStyle = "background-color:green;";
                                }
                                if (parseInt(field) > parseInt(asign) && parseInt(field) <= parseInt(planEndFlag)) {
                                    e.cellStyle = "background-color:blue;";
                                }
                            }
                        }
                    } else if (actStartFlag != 'nextYear') {
                        if (parseInt(actStartFlag) < parseInt(asign)) {
                            if (parseInt(field) < parseInt(actStartFlag)) {
                                e.cellStyle = "background-color:red;";
                            }
                            if (parseInt(field) >= parseInt(actStartFlag) && parseInt(field) <= parseInt(asign)) {
                                e.cellStyle = "background-color:green;";
                            }
                            if (planEndFlag == 'nextYear') {
                                if (parseInt(field) > parseInt(asign)) {
                                    e.cellStyle = "background-color:blue;";
                                }
                                if (field == 'nextYear') {
                                    e.cellStyle = "background-color:blue;";
                                }
                            } else if (planEndFlag == 'preYear') {
                                if (parseInt(field) > parseInt(asign) && parseInt(field) <= parseInt(planEndFlag)) {
                                    e.cellStyle = "background-color:blue;";
                                }
                            }
                        }
                    }
                } else {
                    if (parseInt(field) <= parseInt(asign)) {
                        e.cellStyle = "background-color:red;";
                    }
                    if (planEndFlag == 'nextYear') {
                        if (parseInt(field) > parseInt(asign)) {
                            e.cellStyle = "background-color:blue;";
                        }
                        if (field == 'nextYear') {
                            e.cellStyle = "background-color:blue;";
                        }
                    } else if (planEndFlag == 'preYear') {
                    } else {
                        if (parseInt(field) > parseInt(asign) && parseInt(field) <= parseInt(planEndFlag)) {
                            e.cellStyle = "background-color:blue;";
                        }
                    }
                }
            }
        }
        if (actEndDate) {
            var actEndFlag = timeFlag(actEndDate);
            if (actEndFlag != 'nextYear' && actEndFlag != 'preYear') {
                if(planEndFlag=='preYear'){
                    if (parseInt(field) <= parseInt(actEndFlag)) {
                        e.cellStyle = "background-color:red;";
                    }
                }else if(planEndFlag=='nextYear'){

                }else{
                    if (parseInt(actEndFlag) > parseInt(planEndFlag)) {
                        if (parseInt(field) >= parseInt(planEndFlag) && parseInt(field) <= parseInt(actEndFlag)) {
                            e.cellStyle = "background-color:red;";
                        }
                    }
                    if(parseInt(field)>parseInt(actEndFlag)){
                        e.cellStyle = "background-color:white;";
                    }
                }
            }else{
                e.cellStyle = "background-color:white;";
            }
        } else {
            if (planEndFlag == 'preYear') {
                if (parseInt(field) <= parseInt(asign)) {
                    e.cellStyle = "background-color:red;";
                }
            } else if (planEndFlag == 'nextYear') {

            } else {
                if (parseInt(planEndFlag) < parseInt(asign)) {
                    if (parseInt(field) <= parseInt(asign) && parseInt(field) >= parseInt(planEndFlag)) {
                        e.cellStyle = "background-color:red;";
                    }
                } else {
                    if (parseInt(field) > parseInt(asign) && parseInt(field) <= parseInt(planEndFlag)) {
                        e.cellStyle = "background-color:blue;";
                    }
                }
            }
        }
        if(field=='nextYear'){
            if(e.cellHtml==''){
                if(e.cellStyle.indexOf('red')>-1){
                    valueStr += "红色,";
                }else if(e.cellStyle.indexOf('blue')>-1){
                    valueStr += "蓝色,";
                }else{
                    valueStr += " ,";
                }
            }else{
                valueStr += iconText;
            }
            valueStr +='\n';
        }else{
            if(e.cellHtml==''){
                if(e.cellStyle.indexOf('red')>-1){
                    valueStr += "红色,";
                }else if(e.cellStyle.indexOf('blue')>-1){
                    valueStr += "蓝色,";
                }else{
                    valueStr += " ,";
                }
            }else{
                valueStr += iconText;
            }

        }
    }

    function onImportant(e) {
        var record = e.record;
        var value = record.important;
        var resultText = '';
        for (var i = 0; i < importantList.length; i++) {
            if (importantList[i].key_ == value) {
                resultText = importantList[i].text;
                break
            }
        }
        return resultText;
    }

    function onProcess(e) {
        var record = e.record;
        var value = record.finishProcess;
        var resultText = '';
        for (var i = 0; i < finishProcessList.length; i++) {
            if (finishProcessList[i].key_ == value) {
                resultText = finishProcessList[i].text;
                break
            }
        }
        var processDetail = record.processDetail;
        var currentStage = record.currentStage;
        currentStage = currentStage==undefined?'':currentStage;
        processDetail = processDetail==undefined?'':processDetail
        var _html = '<span style="font-weight: bold">完成进度：</span><br>' + resultText + '<br>' +
            '<span style="font-weight: bold">当前进度：</span><br>' + currentStage +'<br>'+
            '<span style="font-weight: bold">详述：</span><br>' +processDetail
        return _html;
    }
    function detailAttach(e) {
        var record = e.record;
        var taskId = record.id;
        var editable = "false";
        if(record.CREATE_BY_ == currentUserId){
            editable = "true"
        }
        var s = '';
        if(taskId==''||taskId=='undefined'||taskId==undefined){
            s += '<span  title="文件列表" style="color: grey"">文件列表</span>';
        }else{
            s += '<span  title="文件列表" style="cursor: pointer;color: #0a7ac6" onclick="showFilePage(\'' + taskId + '\',\'' + editable + '\')">文件列表</span>';
        }
        return s;
    }
    function showFilePage(taskId,editable) {
        mini.open({
            title: "文件列表",
            url: jsUseCtxPath + "/rdmZhgl/core/gzxm/project/fileWindow.do?taskId=" + taskId + "&fileType=2&editable=false&gzxmAdmin=false"+"&isReporter=false",
            width: 1000,
            height: 500,
            showModal: false,
            allowResize: true,
            onload: function () {
            },
            ondestroy: function (action) {
            }
        });
    }
</script>
</body>
</html>
