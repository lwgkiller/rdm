// var isBianzhi = "";   //是否编制阶段
// var isAssign = "";    //是否指派服务工程责任人阶段
// var secondaryResponse = ""; //是否二级响应阶段
// var thirdResponse = ""; // 是否三级响应阶段
// var firstFwzcrygz = ""; // 是否一级服务支持人跟踪阶段
// var secondFwzcrygz = ""; // 是否二级服务支持人跟踪阶段
// var cpszrrtx = ""; // 是否产品所责任人填写阶段
// var updateMake = ""; // 是否制作状态更新阶段
// var fwgczrrqr = "" // 是否如无工程责任人确认
// $(function () {
//     if (wgjzlsjId) {
//         var url = jsUseCtxPath + "/serviceEngineering/core/wgjzlsj/getWgjzlsjDetail.do";
//         $.ajaxSettings.async = false;
//         $.post(
//             url,
//             {wgjzlsjId: wgjzlsjId},
//             function (json) {
//                 formWgjzlsj.setData(json);
//                 if (json.office == '各类通知单号') {
//                     mini.get("orderNo").setEnabled(true);
//                 } else {
//                     mini.get("orderNo").setEnabled(false);
//                 }
//             });
//         $.ajaxSettings.async = true;
//     }
//     //明细入口
//     if (action == 'detail') {
//         formWgjzlsj.setEnabled(false);
//         $("#detailToolBar").show();
//         //非草稿放开流程信息查看按钮
//         if (taskStatus != 'DRAFTED') {
//             $("#processInfo").show();
//         }
//     } else if (action == 'task') {
//         taskActionProcess();
//     } else if (action == 'editDiskPath') {
//         $("#detailToolBar").show();
//         $("#editDiskPath").show();
//         formWgjzlsj.setEnabled(false);
//         mini.get("networkDiskPath").setEnabled(true);
//     } else {
//         if (isFwgc == "no") {
//             mini.get("cpsPrincipalId").setEnabled(false);
//             mini.get("cpsPrincipalId").setValue(currentUserId);
//             mini.get("cpsPrincipalId").setText(currentUserName);
//             mini.get("materialDepartmentId").setEnabled(false);
//             mini.get("materialDepartmentId").setValue(mainGroupId);
//             mini.get("materialDepartmentId").setText(mainGroupName);
//         } else {
//             /*            mini.get("supplier").setEnabled(false);
//              mini.get("supplierContact").setEnabled(false);
//              mini.get("supplieContactWay").setEnabled(false);*/
//             mini.get("firstDeadline").setEnabled(false);
//             mini.get("firstProvide").setEnabled(false);
//         }
//         mini.get("orderNo").setEnabled(false);
//         mini.get("fwgcPrincipalId").setEnabled(false);
//         mini.get("secondDeadline").setEnabled(false);
//         mini.get("secondProvide").setEnabled(false);
//         mini.get("thirdMake").setEnabled(false);
//         mini.get("makeTime").setEnabled(false);
//         mini.get("thirdInform").setEnabled(false);
//         mini.get("filing").setEnabled(false);
//         mini.get("filingTime").setEnabled(false)
//         mini.get("networkDiskPath").setEnabled(false);
//         mini.get("submitDate").setEnabled(false);
//         mini.get("yjwcsj").setEnabled(false);
//     }
// });

// //流程引擎调用此方法进行表单数据的获取
// function getData() {
//     var formData = _GetFormJsonMini("formWgjzlsj");
//     if (formData.SUB_fileListGrid) {
//         delete formData.SUB_fileListGrid;
//     }
//     //此处用于向后台产生流程实例时替换标题中的参数时使用
//     formData.bos = [];
//     formData.vars = [{key: 'materialCode', val: formData.materialCode}];
//     return formData;
// }

// //保存草稿
// function saveWgjzlsj(e) {
//     var caoGaoValid = validCaoGao();
//     if (!caoGaoValid.result) {
//         mini.alert(caoGaoValid.message);
//         return;
//     }
//     window.parent.saveDraft(e);
// }

// //点击提交时触发
// function submitWgjzlsj(e) {
//     var postData = {};
//     postData.id = mini.get("id").getValue();
//     postData.responseLevel = mini.get("responseLevel").getValue();
//     postData.yyscbm = $.trim(mini.get("yyscbm").getValue());
//     postData.dataType = mini.get("dataType").getValue();
//     postData.materialCode = $.trim(mini.get("materialCode").getValue());
//     postData.materialName = $.trim(mini.get("materialName").getValue());
//     postData.materialDescription = $.trim(mini.get("materialDescription").getValue());
//     postData.materialDepartmentId = mini.get("materialDepartmentId").getValue();
//     postData.materialDepartment = mini.get("materialDepartmentId").getText();
//     postData.office = mini.get("office").getValue();
//     postData.orderNo = $.trim(mini.get("orderNo").getValue());
//     postData.designModel = mini.get("designModel").getValue();
//     postData.supplier = mini.get("supplier").getValue();
//     postData.supplierContact = mini.get("supplierContact").getValue();
//     postData.supplieContactWay = $.trim(mini.get("supplieContactWay").getValue());
//     postData.cpsPrincipalId = mini.get("cpsPrincipalId").getValue();
//     postData.cpsPrincipal = mini.get("cpsPrincipalId").getText();
//     postData.submitDate = mini.get("submitDate").getValue();
//     postData.yjwcsj = mini.get("yjwcsj").getValue();
//     postData.fwgcPrincipalId = mini.get("fwgcPrincipalId").getValue();
//     postData.fwgcPrincipal = mini.get("fwgcPrincipalId").getText();
//     postData.firstDeadline = mini.get("firstDeadline").getValue();
//     postData.firstProvide = mini.get("firstProvide").getValue();
//     postData.secondDeadline = mini.get("secondDeadline").getValue();
//     postData.secondProvide = mini.get("secondProvide").getValue();
//     postData.thirdMake = mini.get("thirdMake").getValue();
//     postData.makeTime = mini.get("makeTime").getValue();
//     postData.thirdInform = mini.get("thirdInform").getValue();
//     postData.filing = mini.get("filing").getValue();
//     postData.filingTime = mini.get("filingTime").getValue();
//     postData.networkDiskPath = $.trim(mini.get("networkDiskPath").getValue());
//     postData.remark = mini.get("remark").getValue();
//     if (!postData.networkDiskPath) {
//         mini.alert('请输入外购件网盘路径');
//         return;
//     }
//     $.ajax({
//         url: jsUseCtxPath + "/serviceEngineering/core/wgjzlsj/saveWgjzlsj.do",
//         type: 'POST',
//         contentType: 'application/json',
//         data: mini.encode(postData),
//         success: function (returnData) {
//             if (returnData && returnData.message) {
//                 mini.alert(returnData.message, '提示', function () {
//                     if (returnData.success) {
//                         CloseWindow()
//                     }
//                 });
//             }
//         }
//     });
// }

// //保存草稿必选类型，因为生成编号过程只写在create方法中出现一次
// function validCaoGao() {
//     var dataType = mini.get("dataType").getValue();
//     if (!dataType) {
//         return {"result": false, "message": "请选择资料类型"};
//     }
//     return {"result": true};
// }

// //检验表单是否必填(编制)
// function validBianZhi() {
//     var dataType = mini.get("dataType").getValue();
//     if (!dataType) {
//         return {"result": false, "message": "请选择资料类型"};
//     }
//     var materialCode = $.trim(mini.get("materialCode").getValue())
//     if (!materialCode) {
//         return {"result": false, "message": "请输入物料编码"};
//     }
//     var materialName = $.trim(mini.get("materialName").getValue())
//     if (!materialName) {
//         return {"result": false, "message": "请输入物料名称"};
//     }
//     var materialDescription = $.trim(mini.get("materialDescription").getValue());
//     if (!materialDescription) {
//         return {"result": false, "message": "请输入物料描述"};
//     }
//     var materialDepartmentId = mini.get("materialDepartmentId").getValue();
//     if (!materialDepartmentId) {
//         return {"result": false, "message": '请选择物料所属部门'};
//     }
//     var office = $.trim(mini.get("office").getValue())
//     if (!office) {
//         return {"result": false, "message": "请选择反馈来源"};
//     }
//     var orderNo = $.trim(mini.get("orderNo").getValue())
//     if (office == '各类通知单号' && !orderNo) {
//         return {"result": false, "message": "请输入通知单号"};
//     }
//     var designModel = $.trim(mini.get("designModel").getValue())
//     if (!designModel) {
//         return {"result": false, "message": "请输入设计机型"};
//     }
//     if (isFwgc == 'no') {
//         var supplier = $.trim(mini.get("supplier").getValue());
//         if (!supplier) {
//             return {"result": false, "message": "请选输入供应商"};
//         }
//         var supplierContact = $.trim(mini.get("supplierContact").getValue());
//         if (!supplierContact) {
//             return {"result": false, "message": "请输入供应商联系人"};
//         }
//         var supplieContactWay = $.trim(mini.get("supplieContactWay").getValue());
//         if (!supplieContactWay) {
//             return {"result": false, "message": "请输入供应商联系方式"};
//         }
//         var firstProvide = mini.get("firstProvide").getValue();
//         if (!firstProvide) {
//             return {"result": false, "message": "请选择(一级响应)是否提供"};
//         }
//         if (firstProvide == 'yes') {
//             var firstDeadline = mini.get("firstDeadline").getValue();
//             if (!firstDeadline) {
//                 return {"result": false, "message": "请选择(一级响应)限期提供(时间)"};
//             }
//         }
//         var isReptition = validReptition();
//         if (!isReptition) {
//             return {"result": false, "message": "物料已存在"};
//         }
//     }
//     var cpsPrincipalId = mini.get("cpsPrincipalId").getValue();
//     if (!cpsPrincipalId) {
//         return {"result": false, "message": "请选择产品所责任人"};
//     }
//     var businessNo = mini.get("businessNo").getValue();
//     if (!businessNo) {
//         return {"result": false, "message": "请先保存草稿生成编号"};
//     }
//     return {"result": true};
// }

// //检验表单是否必填(二级响应)
// function validSecondaryResponse() {
//     var secondProvide = mini.get("secondProvide").getValue();
//     if (!secondProvide) {
//         return {"result": false, "message": "请选择(二级响应)是否提供"};
//     }
//     if (secondProvide == 'yes') {
//         var secondDeadline = mini.get("secondDeadline").getValue();
//         if (!secondDeadline) {
//             return {"result": false, "message": "请选择(二级响应)限期提供(时间)"};
//         }
//     }
//     return {"result": true};
// }

// //检验表单是否必填(三级响应)
// function validThirdResponse() {
//     var thirdMake = mini.get("thirdMake").getValue();
//     if (!thirdMake) {
//         return {"result": false, "message": "请选择是否已制作"};
//     } else {
//         if (thirdMake == 'yes') {
//             var makeTime = mini.get("makeTime").getValue();
//             if (!makeTime) {
//                 return {"result": false, "message": "请选择制作完成时间"};
//             }
//         }
//     }
//     var thirdInform = mini.get("thirdInform").getValue();
//     if (!thirdInform) {
//         return {"result": false, "message": "请选择是否需要下管理通报"};
//     }
//     var filing = mini.get("filing").getValue();
//     if (!filing) {
//         return {"result": false, "message": "请选择是否已归档"};
//     } else {
//         if (filing == 'yes') {
//             var filingTime = mini.get("filingTime").getValue();
//             if (!filingTime) {
//                 return {"result": false, "message": "请选择归档完成时间"}
//             }
//             var networkDiskPath = $.trim(mini.get("networkDiskPath").getValue());
//             if (!networkDiskPath) {
//                 return {"result": false, "message": "请输入外购件网盘路径"}
//             }
//         }
//     }
//     return {"result": true};
// }

// //检验表单是否必填(服务支持人员跟踪)
// function validFwzcrygz() {
//     var filing = mini.get("filing").getValue();
//     if (!filing) {
//         return {"result": false, "message": "请选择是否已归档"};
//     } else {
//         if (filing == 'yes') {
//             var filingTime = mini.get("filingTime").getValue();
//             if (!filingTime) {
//                 return {"result": false, "message": "请选择归档完成时间"}
//             }
//             var networkDiskPath = $.trim(mini.get("networkDiskPath").getValue());
//             if (!networkDiskPath) {
//                 return {"result": false, "message": "请输入外购件网盘路径"}
//             }
//         }
//     }
//     return {"result": true};
// }

// //检验表单是否必填(指派服务工程责任人)
// function validAssign() {
//     var fwgcPrincipalId = mini.get("fwgcPrincipalId").getValue();
//     if (!fwgcPrincipalId) {
//         return {"result": false, "message": "请选择服务工程责任人"};
//     }
//     var firstProvide = mini.get("firstProvide").getValue();
//     if (!firstProvide) {
//         return {"result": false, "message": "请选择(一级响应)是否提供"};
//     }
//     if (firstProvide == 'yes') {
//         var firstDeadline = mini.get("firstDeadline").getValue();
//         if (!firstDeadline) {
//             return {"result": false, "message": "请选择(一级响应)限期提供(时间)"};
//         }
//     }
//     var isReptition = validReptition();
//     var filing = mini.get("filing").getValue();
//     if (!filing) {
//         return {"result": false, "message": "请选择是否已归档"};
//     } else {
//         if (filing == 'yes') {
//             var filingTime = mini.get("filingTime").getValue();
//             if (!filingTime) {
//                 return {"result": false, "message": "请选择归档完成时间"}
//             }
//             var networkDiskPath = $.trim(mini.get("networkDiskPath").getValue());
//             if (!networkDiskPath) {
//                 return {"result": false, "message": "请输入外购件网盘路径"}
//             }
//         }
//     }
//     return {"result": true};
// }

// //检验表单是否必填(产品责任人填写)
// function validCpszrrtx() {
//     var supplier = $.trim(mini.get("supplier").getValue());
//     if (!supplier) {
//         return {"result": false, "message": "请输入供应商"};
//     }
//     var supplierContact = $.trim(mini.get("supplierContact").getValue());
//     if (!supplierContact) {
//         return {"result": false, "message": "请输入供应商联系人"};
//     }
//     var supplieContactWay = $.trim(mini.get("supplieContactWay").getValue());
//     if (!supplieContactWay) {
//         return {"result": false, "message": "请输入供应商联系方式"};
//     }
//     var firstProvide = mini.get("firstProvide").getValue();
//     if (!firstProvide) {
//         return {"result": false, "message": "请选择(一级响应)是否提供"};
//     }
//     if (firstProvide == 'yes') {
//         var firstDeadline = mini.get("firstDeadline").getValue();
//         if (!firstDeadline) {
//             return {"result": false, "message": "请选择(一级响应)限期提供(时间)"};
//         }
//     }
//     var isReptition = validReptition();
//     if (!isReptition) {
//         return {"result": false, "message": "物料已存在"};
//     }
//     return {"result": true};
// }

// //检验表单是否必填(更新制作状态)
// function validUpdateMake() {
//     var thirdMake = mini.get("thirdMake").getValue();
//     if (!thirdMake) {
//         return {"result": false, "message": "请选择是否已制作"};
//     } else {
//         if (thirdMake == 'yes') {
//             var makeTime = mini.get("makeTime").getValue();
//             if (!makeTime) {
//                 return {"result": false, "message": "请选择制作完成时间"};
//             }
//         }
//     }
//     return {"result": true};
// }

// //检验表单是否必填(服务工程责任人确认)
// function validFwgczrrqr() {
//     var firstProvide = mini.get("firstProvide").getValue();
//     if (!firstProvide) {
//         return {"result": false, "message": "请选择(一级响应)是否提供"};
//     }
//     if (firstProvide == 'yes') {
//         var firstDeadline = mini.get("firstDeadline").getValue();
//         if (!firstDeadline) {
//             return {"result": false, "message": "请选择(一级响应)限期提供(时间)"};
//         }
//     }
//     var filing = mini.get("filing").getValue();
//     if (!filing) {
//         return {"result": false, "message": "请选择是否已归档"};
//     } else {
//         if (filing == 'yes') {
//             var filingTime = mini.get("filingTime").getValue();
//             if (!filingTime) {
//                 return {"result": false, "message": "请选择归档完成时间"}
//             }
//             var networkDiskPath = $.trim(mini.get("networkDiskPath").getValue());
//             if (!networkDiskPath) {
//                 return {"result": false, "message": "请输入外购件网盘路径"}
//             }
//         }
//     }
//     return {"result": true};
// }

// //启动流程
// function startWgjzlsjProcess(e) {
//     var bianZhiValid = validBianZhi();
//     if (!bianZhiValid.result) {
//         mini.alert(bianZhiValid.message);
//         return;
//     }
//     window.parent.startProcess(e);
// }

// //流程中暂存信息（如编制阶段）
// function saveWgjzlsjInProcess() {
//     var bianZhiValid = validBianZhi();
//     if (!bianZhiValid.result) {
//         mini.alert(bianZhiValid.message);
//         return;
//     }
//     var formData = _GetFormJsonMini("formWgjzlsj");
//     if (formData.SUB_fileListGrid) {
//         delete formData.SUB_fileListGrid;
//     }
//     $.ajax({
//         url: jsUseCtxPath + '/serviceEngineering/core/wgjzlsj/saveWgjzlsj.do',
//         type: 'post',
//         async: false,
//         data: mini.encode(formData),
//         contentType: 'application/json',
//         success: function (data) {
//             if (data) {
//                 var message = "";
//                 if (data.success) {
//                     message = "数据保存成功";
//                 } else {
//                     message = "数据保存失败" + data.message;
//                 }
//
//                 mini.alert(message, "提示信息", function () {
//                     window.location.reload();
//                 });
//             }
//         }
//     });
// }

// //校验是否重复
// function validReptition() {
//     var valid = true;
//     var postData = {};
//     postData.id = mini.get("id").getValue();
//     postData.materialCode = $.trim(mini.get("materialCode").getValue());
//     postData.supplier = $.trim(mini.get("supplier").getValue());
//     postData.dataType = mini.get("dataType").getValue();
//     $.ajax({
//         url: jsUseCtxPath + '/serviceEngineering/core/wgjzlsj/validReptition.do',
//         type: 'post',
//         async: false,
//         data: mini.encode(postData),
//         contentType: 'application/json',
//         success: function (data) {
//             if (!data.success) {
//                 valid = false;
//             }
//         }
//     });
//     return valid;
// }

// //流程中的审批或者下一步
// function wgjzlsjApprove() {
//     //编制阶段的下一步需要校验表单必填字段
//     var valid = true;
//     if (isBianzhi == 'yes') {
//         valid = validBianZhi();
//         if (!valid.result) {
//             mini.alert(valid.message);
//             return;
//         }
//     }
//     if (isAssign == 'yes') {
//         valid = validAssign()
//         if (!valid.result) {
//             mini.alert(valid.message);
//             return;
//         }
//         mini.get("step").setValue("isAssign");
//     }
//     if (secondaryResponse == 'yes') {
//         valid = validSecondaryResponse()
//         if (!valid.result) {
//             mini.alert(valid.message);
//             return;
//         }
//         mini.get("step").setValue("secondaryResponse");
//     }
//     if (thirdResponse == 'yes') {
//         valid = validThirdResponse()
//         if (!valid.result) {
//             mini.alert(valid.message);
//             return;
//         }
//     }
//     if (firstFwzcrygz == 'yes') {
//         valid = validFwzcrygz()
//         if (!valid.result) {
//             mini.alert(valid.message);
//             return;
//         }
//         mini.get("step").setValue("firstFwzcrygz");
//     }
//     if (secondFwzcrygz == 'yes') {
//         valid = validFwzcrygz()
//         if (!valid.result) {
//             mini.alert(valid.message);
//             return;
//         }
//         mini.get("step").setValue("secondFwzcrygz");
//     }
//     if (cpszrrtx == 'yes') {
//         valid = validCpszrrtx()
//         if (!valid.result) {
//             mini.alert(valid.message);
//             return;
//         }
//         mini.get("step").setValue("cpszrrtx");
//     }
//     if (updateMake == 'yes') {
//         valid = validUpdateMake();
//         if (!valid.result) {
//             mini.alert(valid.message);
//             return;
//         }
//
//     }
//     if (fwgczrrqr == 'yes') {
//         valid = validFwgczrrqr();
//         if (!valid.result) {
//             mini.alert(valid.message);
//             return;
//         }
//
//     }
//     //检查通过
//     window.parent.approve();
// }

// function wgjzlsjProcessInfo() {
//     var instId = $("#instId").val();
//     var url = jsUseCtxPath + "/bpm/core/bpmInst/info.do?instId=" + instId;
//     _OpenWindow({
//         url: url,
//         max: true,
//         title: "流程图实例",
//         width: 800,
//         height: 600
//     });
// }


// function taskActionProcess() {
//     //获取上一环节的结果和处理人
//     bpmPreTaskTipInForm();
//     //获取环境变量
//     if (!nodeVarsStr) {
//         nodeVarsStr = "[]";
//     }
//     var nodeVars = $.parseJSON(nodeVarsStr);
//     for (var i = 0; i < nodeVars.length; i++) {
//         if (nodeVars[i].KEY_ == 'isBianZhi') {
//             isBianzhi = nodeVars[i].DEF_VAL_;
//         }
//         if (nodeVars[i].KEY_ == 'isAssign') {
//             isAssign = nodeVars[i].DEF_VAL_;
//         }
//         if (nodeVars[i].KEY_ == 'secondaryResponse') {
//             secondaryResponse = nodeVars[i].DEF_VAL_;
//         }
//         if (nodeVars[i].KEY_ == 'thirdResponse') {
//             thirdResponse = nodeVars[i].DEF_VAL_;
//         }
//         if (nodeVars[i].KEY_ == 'firstFwzcrygz') {
//             firstFwzcrygz = nodeVars[i].DEF_VAL_;
//         }
//         if (nodeVars[i].KEY_ == 'secondFwzcrygz') {
//             secondFwzcrygz = nodeVars[i].DEF_VAL_;
//         }
//         if (nodeVars[i].KEY_ == 'cpszrrtx') {
//             cpszrrtx = nodeVars[i].DEF_VAL_;
//         }
//         if (nodeVars[i].KEY_ == 'updateMake') {
//             updateMake = nodeVars[i].DEF_VAL_;
//         }
//         if (nodeVars[i].KEY_ == 'fwgczrrqr') {
//             fwgczrrqr = nodeVars[i].DEF_VAL_;
//         }
//     }
//
//     if (isBianzhi == 'yes') {
//         if (isFwgc == "no") {
//             mini.get("cpsPrincipalId").setEnabled(false);
//             mini.get("materialDepartmentId").setEnabled(false);
//         } else {
//             /*            mini.get("supplier").setEnabled(false);
//              mini.get("supplierContact").setEnabled(false);
//              mini.get("supplieContactWay").setEnabled(false);*/
//             mini.get("firstDeadline").setEnabled(false);
//             mini.get("firstProvide").setEnabled(false);
//         }
//         mini.get("fwgcPrincipalId").setEnabled(false);
//         mini.get("secondDeadline").setEnabled(false);
//         mini.get("secondProvide").setEnabled(false);
//         mini.get("thirdMake").setEnabled(false);
//         mini.get("makeTime").setEnabled(false);
//         mini.get("thirdInform").setEnabled(false);
//         mini.get("filing").setEnabled(false);
//         mini.get("filingTime").setEnabled(false);
//         mini.get("thirdMake").setEnabled(false);
//         mini.get("networkDiskPath").setEnabled(false);
//         mini.get("submitDate").setEnabled(false);
//         mini.get("yjwcsj").setEnabled(false);
//     } else if (isAssign == 'yes') {
//         formWgjzlsj.setEnabled(false);
//         mini.get("supplier").setEnabled(true);
//         mini.get("supplierContact").setEnabled(true);
//         mini.get("supplieContactWay").setEnabled(true);
//         mini.get("firstDeadline").setEnabled(true);
//         mini.get("firstProvide").setEnabled(true);
//         mini.get("yyscbm").setEnabled(true);
//         mini.get("fwgcPrincipalId").setEnabled(true);
//         mini.get("filing").setEnabled(true);
//         mini.get("filingTime").setEnabled(true);
//         mini.get("networkDiskPath").setEnabled(true);
//         mini.get("remark").setEnabled(true);
//     } else if (secondaryResponse == 'yes') {
//         formWgjzlsj.setEnabled(false);
//         mini.get("secondDeadline").setEnabled(true);
//         mini.get("secondProvide").setEnabled(true);
//         mini.get("remark").setEnabled(true);
//     } else if (thirdResponse == 'yes') {
//         formWgjzlsj.setEnabled(false);
//         mini.get("thirdMake").setEnabled(true);
//         mini.get("makeTime").setEnabled(true);
//         mini.get("thirdInform").setEnabled(true);
//         mini.get("filing").setEnabled(true);
//         mini.get("filingTime").setEnabled(true);
//         mini.get("networkDiskPath").setEnabled(true);
//         mini.get("remark").setEnabled(true);
//     } else if (firstFwzcrygz == 'yes' || secondFwzcrygz) {
//         formWgjzlsj.setEnabled(false);
//         mini.get("filing").setEnabled(true);
//         mini.get("filingTime").setEnabled(true);
//         mini.get("networkDiskPath").setEnabled(true);
//         mini.get("remark").setEnabled(true);
//     } else if (cpszrrtx == 'yes') {
//         formWgjzlsj.setEnabled(false);
//         mini.get("supplier").setEnabled(true);
//         mini.get("supplierContact").setEnabled(true);
//         mini.get("supplieContactWay").setEnabled(true);
//         mini.get("firstDeadline").setEnabled(true);
//         mini.get("firstProvide").setEnabled(true);
//     } else if (updateMake == 'yes') {
//         formWgjzlsj.setEnabled(false);
//         mini.get("thirdMake").setEnabled(true);
//         mini.get("makeTime").setEnabled(true);
//         mini.get("remark").setEnabled(true);
//     } else if (fwgczrrqr == 'yes') {
//         formWgjzlsj.setEnabled(false);
//         mini.get("supplier").setEnabled(true);
//         mini.get("supplierContact").setEnabled(true);
//         mini.get("supplieContactWay").setEnabled(true);
//         mini.get("firstDeadline").setEnabled(true);
//         mini.get("firstProvide").setEnabled(true);
//         mini.get("filing").setEnabled(true);
//         mini.get("filingTime").setEnabled(true);
//         mini.get("networkDiskPath").setEnabled(true);
//         mini.get("remark").setEnabled(true);
//     }
//
// }

// function disableOrderNo() {
//     var office = mini.get("office").getValue();
//     if (office == '各类通知单号') {
//         mini.get("orderNo").setEnabled(true);
//     } else {
//         mini.get("orderNo").setValue("");
//         mini.get("orderNo").setEnabled(false);
//     }
// }