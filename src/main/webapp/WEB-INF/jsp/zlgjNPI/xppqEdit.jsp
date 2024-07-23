<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>新品剖切验证</title>
    <%@include file="/commons/edit.jsp"%>
    <script src="${ctxPath}/scripts/zlgjNPI/xppqEdit.js?version=${static_res_version}" type="text/javascript"></script>
    <script src="${ctxPath}/scripts/rdmCommon/formUtil.js?version=${static_res_version}" type="text/javascript"></script>
</head>
<body>
    <div id="detailToolBar" class="topToolBar" style="display: none">
        <div>
            <a id="processInfo" class="mini-button" style="display: none" onclick="processInfo()">流程信息</a>
            <a id="closeWindow" class="mini-button" onclick="CloseWindow()">关闭</a>
        </div>
    </div>
    <div class="mini-fit" id="content">
        <div class="form-container" style="margin: 0 auto">
            <form id="formZlgj" method="post">
                <input id="wtId" name="wtId" class="mini-hidden"/>
                <input id="instId" name="instId" class="mini-hidden"/>
                <input id="creator" name="creator" class="mini-hidden"/>
                <p style="font-size: 16px;font-weight: bold">问题识别信息</p><hr />
                <div class="fieldset-body" style="margin: 10px 10px 10px 10px">
                    <table class="table-detail" cellspacing="1" cellpadding="0">
                        <tr>
                            <td style="width: 14%">设计图号：</td>
                            <td style="width: 18%;min-width:140px">
                                <input id="sjth" name="sjth" class="mini-textbox" style="width:98%;" />
                            </td>
                            <td style="width: 14%">剖切方式：</td>
                            <td style="width: 19%;min-width:170px">
                                <input id="pqfs" name="pqfs" class="mini-combobox" style="width:98%;"
                                       textField="name" valueField="value" emptyText="请选择..."
                                       required="false" allowInput="false" showNullItem="false"
                                       data="[{'name':'全尺寸剖切','value':'全尺寸剖切'},{'name':'样块剖切','value':'样块剖切'}]"
                                />
                            </td>
                            <td style="width: 14%">剖切计划完成日期 ：</td>
                            <td style="width: 21%">
                                <input id="pqjhwcTime" name="pqjhwcTime"  class="mini-datepicker" format="yyyy-MM-dd" allowInput="false"
                                       showTime="false" showOkButton="false" showClearButton="true" style="width:98%;" />
                            </td>
                        </tr>
                        <tr>
                            <td style="width: 14%">工艺工程师：</td>
                            <td style="width: 18%;min-width:140px">
                                <input id="gygcsId" name="gygcsId" textname="gygcsName" class="mini-user rxc"
                                       plugins="mini-user" style="width:100%" allowinput="false" label="" length="50"
                                       mainfield="no"  single="true"
                                />
                            </td>
                        </tr>

                    </table>
                </div>

                <p style="font-size: 16px;font-weight: bold;margin-top: 20px">剖切方案及执行过程</p>
                <a style="color:red" >（每一行数据代表一次剖切验证的执行过程，灰色代表已执行完的历史数据，不允许编辑删除）</a><hr />
                <div class="fieldset-body" style="margin: 10px 10px 10px 10px">
                    <div class="mini-toolbar" id="pqzxButtons">
                        <a class="mini-button"  plain="true" onclick="addPqzx">添加</a>
                        <a class="mini-button btn-red"  plain="true" onclick="removePqzx">删除</a>

                    </div>
                    <div id="grid_xppqProcess" class="mini-datagrid"  allowResize="false"
                         idField="zxId" allowCellEdit="true" allowCellSelect="true" allowSortColumn="false"
                         multiSelect="true" showColumnsMenu="false"  showPager="false" allowCellWrap="true" showVGridLines="true">
                        <div property="columns">
                            <div type="checkcolumn" width="10"></div>
                            <div field="pqfcId" displayField="pqfcName" align="center" headerAlign="center" width="40">剖切分厂
                                <input property="editor" class="mini-combobox" style="width:98%;"
                                       textField="pqfcName" valueField="pqfcId" emptyText="请选择..."
                                       required="false" allowInput="false" showNullItem="false"
                                data="[{'pqfcName':'结构一厂','pqfcId':'378253371714615869'},{'pqfcName':'结构二厂','pqfcId':'378253371714615866'}]"
                                />
                            </div>
                            <div field="pqfa" align="center" headerAlign="center" width="40" renderer="renderAttachFile">剖切方案
                            </div>
                            <div field="yjscTime"  headerAlign="center" align="center" width="40" dateFormat="yyyy-MM-dd">样件生产日期
                                <input property="editor" class="mini-datepicker" style="width:100%;" allowInput="false"/>
                            </div>
                            <div field="pqjhTime"  headerAlign="center" align="center" width="40" dateFormat="yyyy-MM-dd">剖切计划日期
                                <input property="editor" class="mini-datepicker" style="width:100%;" allowInput="false"/>
                            </div>
                            <div field="hjwcTime"  headerAlign="center" align="center" width="40" dateFormat="yyyy-MM-dd">样件焊接完成日期
                                <input property="editor" class="mini-datepicker" style="width:100%;" allowInput="false"/>
                            </div>
                            <div field="jyjg"  headerAlign="center" align="left" width="100">样件检验结果
                                <input property="editor" class="mini-textarea" style="width:100%;overflow: auto;"/>
                            </div>
                            <div field="pqwcTime"  headerAlign="center" align="center" width="40" dateFormat="yyyy-MM-dd">剖切实际完成日期
                                <input property="editor" class="mini-datepicker" style="width:100%;" allowInput="false"/>
                            </div>
                            <div field="pqfx" align="center" headerAlign="center" width="40" renderer="renderAttachFile">剖切分析结果
                            </div>
                            <div field="psjgDesc"  headerAlign="center" align="left" width="90">评审结果
                                <input property="editor" class="mini-textbox" style="width:100%;overflow: auto;"/>
                            </div>
                            <div field="psjg" align="center" headerAlign="center" width="40" renderer="renderAttachFile">评审结果附件
                            </div>
                        </div>
                    </div>
                </div>

            </form>
        </div>
    </div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath="${ctxPath}";
    var nodeVarsStr='${nodeVars}';
    var status="${status}";
    var wtId="${wtId}";
    var action="${action}";
    var formZlgj = new mini.Form("#formZlgj");
    var currentUserId="${currentUserId}";
    var currentUserName="${currentUserName}";
    var currentTime="${currentTime}";
    var coverContent=currentUserName+"<br/>"+currentTime+"<br/>徐州徐工挖掘机械有限公司";
    var grid_xppqProcess = mini.get("#grid_xppqProcess");

    grid_xppqProcess.on("drawcell", function (e) {
        var record=e.record;
        debugger
        if(record.zxId&&record.ifGd=='yes') {
            e.cellStyle="background-color: #ccc;";
        }
    });

    grid_xppqProcess.on("cellbeginedit", function (e) {
        if(!e.editor) {
            return;
        }
        var record=e.record;
        if(record.ifGd=='yes') {
            e.editor.setEnabled(false);
            return;
        }
        if(stageName=='fazd') {
            if(e.field == "yjscTime" || e.field == "pqjhTime" || e.field=="pqfcId") {
                e.editor.setEnabled(true);
            } else {
                e.editor.setEnabled(false);
            }
        } else if(stageName=='yjhj') {
            if(e.field == "hjwcTime") {
                e.editor.setEnabled(true);
            } else {
                e.editor.setEnabled(false);
            }
        }else if(stageName=='yjjy') {
            if(e.field == "jyjg") {
                e.editor.setEnabled(true);
            } else {
                e.editor.setEnabled(false);
            }
        }else if(stageName=='yjpq') {
            if(e.field == "pqwcTime") {
                e.editor.setEnabled(true);
            } else {
                e.editor.setEnabled(false);
            }
        }else if(stageName=='zzps') {
            if(e.field == "psjgDesc") {
                e.editor.setEnabled(true);
            } else {
                e.editor.setEnabled(false);
            }
        } else {
            e.editor.setEnabled(false);
        }
    });

    function renderAttachFile(e) {
        var record = e.record;
        var zxId = record.zxId;
        var wtId=record.wtId;
        if(!zxId) {
            zxId='';
        }
        if(!wtId) {
            wtId='';
        }
        var type=e.field;
        var ifGd='';
        if(record.ifGd) {
            ifGd=record.ifGd;
        }
        var s = '<a href="#" style="color:#44cef6;text-decoration:underline;" ' +
            'onclick="addXppqFile(\'' + zxId +'\',\''+wtId+'\',\''+type+'\',\''+ifGd+ '\')">附件</a>';
        return s;
    }


</script>
</body>
</html>
