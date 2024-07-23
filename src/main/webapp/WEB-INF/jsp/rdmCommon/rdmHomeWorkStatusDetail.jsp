<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title></title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
    <div class="mini-toolbar" style="margin-top: 0px;margin-bottom: 5px">
        <li id="operateBtn" style="float: left;width: 100%">
            <input id="dayStr" format="yyyy-MM-dd" style="width: 115px;font-size: 10px;float: left;margin-left: 5px;margin-right: 20px"
                   showOkButton="true" showClearButton="false" onvaluechanged="refreshData()" onfocus="this.blur()" class="mini-datepicker" value=""/>
            <a class="mini-button"  onclick="refreshData()">刷新</a>
            <span class="separator"></span>
            <a class="mini-button"  onclick="addData()">新增</a>
            <a class="mini-button btn-red" plain="true" onclick="removeData()">删除</a>
            <a class="mini-button"  onclick="saveData()">保存</a>
            <p style="display: inline-block;font-size:15px;vertical-align: middle;">（操作前<span style="color: #f6253e;font-weight: bold">选择正确日期</span>，新增、修改、删除后都需<span style="color: #f6253e;font-weight: bold">点击保存</span>）</p>
            <a class="mini-button" style="float: right;margin-right: 20px" onclick="ddNotice()">钉钉通知</a>
        </li>
    </div>
    <div class="mini-fit" style="height: 98%">
        <div id="statusListGrid" class="mini-datagrid" style="width: 100%; height: 100%;" allowResize="false"
             idField="id"  showColumnsMenu="false"  showPager="false" multiSelect="true"
             allowCellEdit="true" allowCellSelect="true" allowCellWrap="true"
             allowAlternating="true">
            <div property="columns">
                <div type="checkcolumn" width="30"></div>
                <div type="indexcolumn" width="40">序号</div>
                <div field="leaderUserId" visible="false">领导userId</div>
                <div field="leaderUserName" visible="false">领导userName</div>
                <div field="dayStr" visible="false">日期</div>
                <div field="ifNotice" visible="false">是否已弹框提醒</div>
                <div field="CREATE_BY_" visible="false">创建人</div>
                <div field="startTime" width="100" headerAlign="center" align="center" allowSort="false" dateFormat="HH:mm">开始时间<span style="color: #ff0000">*</span><br>(请选择)
                    <input property="editor" class='mini-timespinner' allowInput='true'  format='HH:mm' style="width:90%;"/>
                </div>
                <div field="endTime" width="100" headerAlign="center" align="center" allowSort="false" dateFormat="HH:mm">结束时间<span style="color: #ff0000">*</span><br>(请选择)
                    <input property="editor" class='mini-timespinner' allowInput='true'  format='HH:mm' style="width:90%;"/>
                </div>
                <div field="place"  width="140" headerAlign="center" align="center" allowSort="false">地点<span style="color: #ff0000">*</span>
                    <input property="editor" class="mini-textbox" />
                </div>
                <div field="scheduleDesc"  width="330" headerAlign="center" align="left" allowSort="false">事项说明<span style="color: #ff0000">*</span>
                    <input property="editor" class="mini-textarea" />
                </div>
                <div field="creatorName" align="center" width="60" headerAlign="center" >创建人
                </div>
                <div field="CREATE_TIME_" dateFormat="yyyy-MM-dd HH:mm" align="left" width="130" headerAlign="center" >创建时间
                </div>

            </div>
        </div>
    </div>

<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var statusListGrid=mini.get("statusListGrid");
    var todayStr="${todayStr}";
    var leaderUserId="${leaderUserId}";
    var leaderUserName="${leaderUserName}";
    var currentUserName = "${currentUserName}";
    var currentUserId = "${currentUserId}";

    $(function () {
        mini.get("dayStr").setValue(todayStr);
        refreshData();
    });

    function saveData() {
        var changes = statusListGrid.getChanges();
        if (changes.length > 0) {
            for (var i = 0; i < changes.length; i++) {
                var index=statusListGrid.indexOf(changes[i])+1;
                if(changes[i]._state=='removed') {
                    continue;
                }
                if (!changes[i].startTime) {
                    mini.alert("序号"+index+"请选择开始时间！");
                    return;
                }
                if (!changes[i].endTime) {
                    mini.alert("序号"+index+"请选择结束时间！");
                    return;
                }
                if (!changes[i].place) {
                    mini.alert("序号"+index+"请填写地点！");
                    return;
                }
                if (!changes[i].scheduleDesc) {
                    mini.alert("序号"+index+"请填写事项说明！");
                    return;
                }
            }

            //检查时间段是否有重叠
/*            var gridData = statusListGrid.getData();
            for(var index=0;index<gridData.length;gridData++) {

            }*/

            var changesData = mini.encode(changes);
            $.ajax({
                url: jsUseCtxPath + '/rdmHome/core/saveWorkStatus.do',
                type: 'POST',
                data: changesData,
                async: false,
                contentType: 'application/json',
                success: function (data) {
                    if (data && data.message) {
                        mini.alert(data.message,'提示',function () {
                           if(data.success) {
                               statusListGrid.load();
                           }
                        });
                    }
                }
            });
        } else {
            mini.alert("保存成功！");
        }
    }

    statusListGrid.on("cellbeginedit", function (e) {
        var record = e.record;
        if(!e.editor) {
            return;
        }
        if(record.CREATE_BY_ != currentUserId) {
            e.cancel = true;
        } else {
            e.editor.setEnabled(true);

        }
    });


    function addData() {
        var hour = new Date().getHours(); // 时
        var minutes = new Date().getMinutes(); // 分
        if (hour >= 0 && hour <= 9) {
            hour = "0" + hour;
        }
        if (minutes >= 0 && minutes <= 9) {
            minutes = "0" + minutes;
        }
        var startTime= hour+":"+minutes;
        var dayStrSelect=mini.get("dayStr").getText();
        var newRow = {startTime: startTime,endTime:startTime,creatorName:currentUserName,CREATE_TIME_:todayStr+' '+startTime,
            leaderUserId:leaderUserId,leaderUserName:leaderUserName,dayStr:dayStrSelect,ifNotice:'no',CREATE_BY_:currentUserId};
        statusListGrid.addRow(newRow);
    }

    function removeData() {
        var rows = statusListGrid.getSelecteds();
        if (rows.length > 0) {
            var finalRows=[];
            for(var index=0;index<rows.length;index++) {
                if(rows[index].CREATE_BY_ == currentUserId) {
                    finalRows.push(rows[index]);
                }
            }
            if(finalRows.length==0) {
                mini.alert("请至少选中一条本人创建的记录");
                return;
            }
            statusListGrid.removeRows(finalRows, false);
        } else {
            mini.alert("请至少选中一条本人创建的记录");
            return;
        }
    }

    function refreshData() {
        var url=jsUseCtxPath+"/rdmHome/core/queryWorkStatusOneUser.do?userId="+leaderUserId;
        var dayStr= mini.get("dayStr").getText();
        url+="&dayStr="+dayStr;
        statusListGrid.setUrl(url);
        statusListGrid.load();
    }

    function ddNotice() {
        var changes = statusListGrid.getChanges();
        if (changes.length > 0) {
            mini.alert("存在数据变更，请先进行“保存”操作！");
            return;
        }
        var rows = statusListGrid.getSelecteds();
        if (rows.length > 0) {
            var finalRows=[];
            for(var index=0;index<rows.length;index++) {
                if(rows[index].CREATE_BY_ == currentUserId) {
                    finalRows.push(rows[index]);
                }
            }
            if(finalRows.length==0) {
                mini.alert("请至少选中一条本人创建的记录");
                return;
            }
            //    发送钉钉提醒
            $.ajax({
                url: jsUseCtxPath + '/rdmHome/core/sendWorkStatusDDNotice.do?leaderUserId='+leaderUserId,
                type: 'POST',
                data: mini.encode(finalRows),
                async: false,
                contentType: 'application/json',
                success: function (data) {
                    if (data && data.message) {
                        mini.alert(data.message);
                    }
                }
            });
        } else {
            mini.alert("请至少选中一条本人创建的记录");
            return;
        }
    }
</script>
</body>
</html>
