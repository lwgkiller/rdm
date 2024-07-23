// //..
// $(function () {
//     searchFrm();
// });
// //..
// function taskStatusRenderer(e) {
//     var record = e.record;
//     var taskStatus = record.taskStatus;
//     var arr = [{'key': 'DRAFTED', 'value': '草稿', 'css': 'orange'},
//         {'key': 'RUNNING', 'value': '运行中', 'css': 'green'},
//         {'key': 'SUCCESS_END', 'value': '成功结束', 'css': 'blue'},
//         {'key': 'DISCARD_END', 'value': '作废', 'css': 'red'},
//         {'key': 'ABORT_END', 'value': '异常中止结束', 'css': 'red'},
//         {'key': 'PENDING', 'value': '挂起', 'css': 'gray'}
//     ];
//     return $.formatItemValue(arr, taskStatus);
// }
// //..新增流程（后台根据配置的表单进行跳转）
// function addWgjzlsj() {
//     var url = jsUseCtxPath + "/bpm/core/bpmInst/WGJZLSJ/start.do";
//     var winObj = window.open(url);
//     var loop = setInterval(function () {
//         if (winObj.closed) {
//             clearInterval(loop);
//             if (wgjzlsjGrid) {
//                 wgjzlsjGrid.reload()
//             }
//         }
//     }, 1000);
// }
// //..
// function removeWgjzlsj(record) {
//     var rows = [];
//     if (record) {
//         rows.push(record);
//     } else {
//         rows = wgjzlsjGrid.getSelecteds();
//     }
//     if (rows.length <= 0) {
//         mini.alert("请至少选中一条记录");
//         return;
//     }
//     mini.confirm("确定删除选中记录？", "提示", function (action) {
//         if (action != 'ok') {
//             return;
//         } else {
//             var ids = [];
//             var instIds = [];
//             // var existStartInst = false;
//             for (var i = 0, l = rows.length; i < l; i++) {
//                 var r = rows[i];
//                 // if (r.taskStatus == 'DRAFTED') {
//                 ids.push(r.id);
//                 instIds.push(r.instId);
//                 // } else {
//                 //     existStartInst = true;
//                 //     continue;
//                 // }
//             }
//             // if (existStartInst) {
//             //     alert("仅草稿状态数据可由本人删除");
//             // }
//             if (ids.length > 0) {
//                 _SubmitJson({
//                     url: jsUseCtxPath + "/serviceEngineering/core/wgjzlsj/deleteWgjzlsj.do",
//                     method: 'POST',
//                     data: {ids: ids.join(','), instIds: instIds.join(',')},
//                     success: function (data) {
//                         if (data) {
//                             searchFrm();
//                         }
//                     }
//                 });
//             }
//         }
//     });
// }
// //..导出
// function exportWgj() {
//     var parent = $(".search-form");
//     var inputAry = $("input", parent);
//     var params = [];
//     inputAry.each(function (i) {
//         var el = $(this);
//         var obj = {};
//         obj.name = el.attr("name");
//         if (!obj.name) return true;
//         obj.value = el.val();
//         params.push(obj);
//     });
//     $("#filter").val(mini.encode(params));
//     var excelForm = $("#excelForm");
//     excelForm.submit();
// }
// //..明细（直接跳转到详情的业务controller）
// function wgjzlsjDetail(wgjzlsjId, taskStatus) {
//     var action = "detail";
//     var url = jsUseCtxPath + "/serviceEngineering/core/wgjzlsj/wgjzlsjEditPage.do?action=" +
//         action + "&wgjzlsjId=" + wgjzlsjId + "&taskStatus=" + taskStatus;
//     var winObj = window.open(url);
//     var loop = setInterval(function () {
//         if (winObj.closed) {
//             clearInterval(loop);
//             if (wgjzlsjGrid) {
//                 wgjzlsjGrid.reload()
//             }
//         }
//     }, 1000);
// }
// //..编辑行数据流程（后台根据配置的表单进行跳转）
// function wgjzlsjEdit(wgjzlsjId, instId) {
//     var url = jsUseCtxPath + "/bpm/core/bpmInst/start.do?instId=" + instId;
//     var winObj = window.open(url);
//     var loop = setInterval(function () {
//         if (winObj.closed) {
//             clearInterval(loop);
//             if (wgjzlsjGrid) {
//                 wgjzlsjGrid.reload()
//             }
//         }
//     }, 1000);
// }
// //..点击办理是跳转
// function wgjzlsjTask(taskId) {
//     $.ajax({
//         url: jsUseCtxPath + '/bpm/core/bpmTask/checkTaskLockStatus.do?taskId=' + taskId,
//         async: false,
//         success: function (result) {
//             if (!result.success) {
//                 top._ShowTips({
//                     msg: result.message
//                 });
//             } else {
//                 var url = jsUseCtxPath + '/bpm/core/bpmTask/toStart.do?taskId=' + taskId;
//                 var winObj = openNewWindow(url, "handTask");
//                 var loop = setInterval(function () {
//                     if (!winObj) {
//                         clearInterval(loop);
//                     } else if (winObj.closed) {
//                         clearInterval(loop);
//                         if (wgjzlsjGrid) {
//                             wgjzlsjGrid.reload();
//                         }
//                     }
//                 }, 1000);
//             }
//         }
//     })
// }
// //..修改网盘路径
// function toEditDiskPath(wgjzlsjId) {
//     var action = "editDiskPath";
//     var url = jsUseCtxPath + "/serviceEngineering/core/wgjzlsj/wgjzlsjEditPage.do?action=" +
//         action + "&wgjzlsjId=" + wgjzlsjId;
//     var winObj = window.open(url);
//     var loop = setInterval(function () {
//         if (winObj.closed) {
//             clearInterval(loop);
//             if (wgjzlsjGrid) {
//                 wgjzlsjGrid.reload()
//             }
//         }
//     }, 1000);
// }


// function dataTypeRenderer(e) {
//     var record = e.record;
//     var arr = [{key: 'wxscl', value: '维修手册类'}, {key: 'ljtcl', value: '零件图册类'}];
//     return $.formatItemValue(arr, record.dataType);
// }

// function filingRenderer(e) {
//     var record = e.record;
//     var arr = [{key: 'yes', value: '是'}, {key: 'no', value: '否'}];
//     return $.formatItemValue(arr, record.filing);
// }

// function thirdMakeRenderer(e) {
//     var record = e.record;
//     var str = "<span>是</span>";
//     if (!record.thirdMake || record.thirdMake == 'no') {
//         str = "<span>否</span>";
//     }
//     return str;
// }

// function responseLevelRenderer(e) {
//     var record = e.record;
//     var arr = [{key: 'first', value: '一级'}, {key: 'second', value: '二级'}, {key: 'third', value: '三级'}];
//     return $.formatItemValue(arr, record.responseLevel);
// }











