

function getRatingRuleByScore(knotScore) {
    if (knotScore == '') {
        return '';
    } else {
        for (var i = 0; i < knotScore2RatingRule.length; i++) {
            var oneRule = knotScore2RatingRule[i];
            if (knotScore >= oneRule.minScore && knotScore <= oneRule.maxScore) {
                return oneRule;
            }
        }
    }
    return {};
}

//如果项目成员有数据，则检查是否填写分级评分，以及成员id、角色、承担内容和角色系数(项目变更界面和立项评审之前所有的都必填，)
//在由部门负责人审核成员配置之前必须要填好角色、工作内容、角色系数和部门，同时必传交付物要全部分配完成
function checkMemberRequired(rowData,checkAll,checkDelivery) {
    if (!rowData || rowData.length <= 0) {
        return {result: true};
    }
    //zwt
    var beginLevelId = $.trim(mini.get("beginLevel").getValue())
    if (!beginLevelId) {
        return {result: false, message: projectProductEdit_name13};
    }
    var assignDeliveryIds=[];
    for (var i = 0; i < rowData.length; i++) {
        var row = rowData[i];
        var userValid=row.userValid;
        //冻结的不检查,否则可能存在无法编辑的情况
        if(userValid=='02') {
            continue;
        }
        if (!row.memberDeptId) {
            return {result: false, message: '请填写项目成员信息中的成员部门'};
        }
        if (!row.roleId) {
            return {result: false, message: '请填写项目成员信息中的角色'};
        }

        if(checkAll) {
            if (!$.trim(row.projectTask)) {
                return {result: false, message: '请填写项目成员“'+(row.userName?row.userName:"")+'”的承担工作内容'};
            }
            if (!$.trim(row.workHour)) {
                return {result: false, message: '请填写项目成员“'+(row.userName?row.userName:"")+'”的工时'};
            }
        }
        if(checkDelivery && row.respDeliveryIds) {
            assignDeliveryIds=assignDeliveryIds.concat(row.respDeliveryIds.split(","));
        }
    }
    //必传交付物要全部分配完成
    if(checkDelivery) {
        return checkDeliveryAssign(assignDeliveryIds);
    } else {
        return {result: true};
    }
}

//产品研发整机类的项目，在立项节点需要将非技术中心部门的成员姓名选择好
function checkProductGlUserAssign(membersData,projectId,categoryId) {
    var postData={projectId:projectId,membersData:membersData,categoryId:categoryId};
    var result={result: true};
    $.ajax({
        url: jsUseCtxPath + '/xcmgProjectManager/core/xcmgProject/checkProductGlUserAssign.do',
        type: 'post',
        async:false,
        data:mini.encode(postData),
        contentType: 'application/json',
        success: function (data) {
            if (!data.success) {
                result={result: false, message: data.message};
            }
        }
    });
    return result;
}

function checkDeliveryAssign(assignDeliveryIds) {
    var projectSource=mini.get("projectSource").getValue();
    var projectCategory=mini.get("projectCategory").getValue();
    var postData={projectSourceId:projectSource,projectCategoryId:projectCategory,projectLevelId:mini.get("beginLevel").getValue(),projectId:mini.get("projectId").getValue(),assignDeliveryIds:assignDeliveryIds};
    var result={result: true};
    $.ajax({
        url: jsUseCtxPath + '/xcmgProjectManager/core/xcmgProject/checkDeliveryAssign.do',
        type: 'post',
        async:false,
        data:mini.encode(postData),
        contentType: 'application/json',
        success: function (data) {
            if (!data.success) {
                result={result: false, message: data.message};
            }
        }
    });
    return result;
}

function currentDeliveryAssgin(rowData) {
    var projectSource=mini.get("projectSource").getValue();
    var projectCategory=mini.get("projectCategory").getValue();
    var beginLevelId=mini.get("beginLevel").getValue();
    //必传交付物统计表
    var deliverysStatisticMap = new Map();
    var deliveryId2Name = new Map();
    //获取项目必要的交付物信息
    $.ajax({
        url: jsUseCtxPath + '/xcmgProjectManager/core/xcmgProject/getProjectDeliverys.do?projectSourceId=' + projectSource + "&projectCategoryId=" + projectCategory + "&projectLevelId=" + beginLevelId,
        type: 'get',
        async:false,
        contentType: 'application/json',
        success: function (data) {
            if (data) {
                for (var i = 0; i < data.length; i++) {
                    var oneTempData = data[i];
                    deliveryId2Name.set(oneTempData.deliveryId, oneTempData.deliveryName);
                    deliverysStatisticMap.set(oneTempData.deliveryId, 0);
                }
            }
        }
    });
    //已分配交付物情况获取(非整机类，页面获取)
    if (projectCategory != "02") {
        for (var i = 0; i < rowData.length; i++) {
            var row = rowData[i];
            if (row.userValid != '02' && row.respDeliveryIds) {//冻结人员不统计
                var tempRespDeliveryArray = row.respDeliveryIds.split(",");
                for (var j = 0; j < tempRespDeliveryArray.length; j++) {
                    if (deliverysStatisticMap.has(tempRespDeliveryArray[j])) {
                        deliverysStatisticMap.set(tempRespDeliveryArray[j], deliverysStatisticMap.get(tempRespDeliveryArray[j]) + 1);
                    }
                }
            }
        }
    } else {
        //后台获取 查询每个人的分配情况,DAO已过滤掉冻结成员 获取项目必要的交付物信息
        var userIdStr = '';
        for (var i = 0; i < rowData.length; i++) {
            userIdStr += rowData[i].userId+",";
        }
        $.ajax({
            url: jsUseCtxPath + '/xcmgProjectManager/core/xcmgProject/queryMemberDeliveryAssign.do?projectId=' + mini.get("projectId").getValue(),
            type: 'get',
            async:false,
            contentType: 'application/json',
            success: function (data) {
                if (data) {
                    for (var i = 0; i < data.length; i++) {
                        var row = data[i];
                        if (row.respDeliveryIds) {
                            var tempRespDeliveryArray = row.respDeliveryIds.split(",");
                            for (var j = 0; j < tempRespDeliveryArray.length; j++) {
                                if (deliverysStatisticMap.has(tempRespDeliveryArray[j])) {
                                    deliverysStatisticMap.set(tempRespDeliveryArray[j], deliverysStatisticMap.get(tempRespDeliveryArray[j]) + 1);
                                }
                            }
                        }
                    }
                }
            }
        });
    }
    return [deliverysStatisticMap,deliveryId2Name];
}

//检查某个部门的是否所有成员均已指定
function checkMemberPoint(rowData,deptId) {
    if (!rowData || rowData.length <= 0) {
        return {result: true};
    }
    for (var i = 0; i < rowData.length; i++) {
        var row = rowData[i];
        var userValid=row.userValid;
        if(userValid=='02') {
            continue;
        }
        if (!row.userId && row.memberDeptId == deptId) {
            return {result: false, message: '请选择项目成员信息中本部门的成员姓名'};
        }
    }
    return {result: true};
}


//检查产学研外部成员如果有数据，必填项是否填写
function checkMemOutRequired(rowData) {
    if (!rowData || rowData.length <= 0) {
        return {result: true};
    }
    for (var i = 0; i < rowData.length; i++) {
        var row = rowData[i];
        if (!row.userName || !row.userDeptName || !row.userRole|| !row.userTask) {
            return {result: false, message: '请填写外部成员必填信息！'};
        }
    }
    return {result: true};
}

//如果项目成员有数据，则检查是否填写分级评分，则检查项目级别与成员职级是否匹配
function checkMemberRankOk(rowData) {
    if (!rowData || rowData.length <= 0) {
        return {result: true};
    }
    var levelId = $.trim(mini.get("beginLevel").getValue())
    if (!levelId) {
        return {result: false, message: projectProductEdit_name13};
    }
    for (var i = 0; i < rowData.length; i++) {
        var row = rowData[i];
        var roleId = row.roleId;
        var roleName=row.roleName;
        var zjno = row.zjno;
        var gwno=row.gwno;
        var userName=row.userName;
        var minZjNo = findMinZjNo(mini.get("beginLevel").getValue(), roleId);
        if(roleName!='项目指导人') {
            if (!zjno || zjno < minZjNo.zjNo) {
                return {result: false, message: '项目成员\''+userName+'\' 信息错误！当前项目级别的“' + row.roleName + '”最低职资要求为：' + minZjNo.zjName};
            }
        } else {
            if (!zjno ||!gwno || gwno < minZjNo.zjNo) {
                return {result: false, message: '项目成员\''+userName+'\' 信息错误！当前项目级别的“' + row.roleName + '”最低职资要求为：' + minZjNo.zjName};
            }
        }
    }
    return {result: true};
}

function findMinZjNo(levelId, roleId) {
    for (var i = 0; i < roleRankRequire.length; i++) {
        if (roleRankRequire[i].roleId == roleId && roleRankRequire[i].levelId == levelId) {
            return {zjNo: roleRankRequire[i].minRankNo, zjName: roleRankRequire[i].ZJName};
        }
    }
    return {zjNo: 1, zjName: "助理级"};
}


//是否是部门负责人
function whetherIsDepRespman(userDeps) {
    for (var i = 0; i < userDeps.length; i++) {
        if (userDeps[i].REL_TYPE_KEY_ == 'GROUP-USER-LEADER') {
            return true;
        }
    }
    return false;
}

//是否是技术委员会成员
function whetherIsJSWYHman(userRoles) {
    for (var i = 0; i < userRoles.length; i++) {
        if (userRoles[i].REL_TYPE_KEY_ == 'GROUP-USER-LEADER'||userRoles[i].REL_TYPE_KEY_ == 'GROUP-USER-BELONG') {
            if(userRoles[i].NAME_=='技术委员会') {
                return true;
            }
        }
    }
    return false;
}

//是否是分管领导
function whetherIsLeader(userRoles) {
    for (var i = 0; i < userRoles.length; i++) {
        if (userRoles[i].REL_TYPE_KEY_ == 'GROUP-USER-LEADER'||userRoles[i].REL_TYPE_KEY_ == 'GROUP-USER-BELONG') {
            if(userRoles[i].NAME_=='分管领导') {
                return true;
            }
        }
    }
    return false;
}

//是否是挖掘机械研究院项目管理人员
function whetherIsProjectManager(userRoles) {
    for (var i = 0; i < userRoles.length; i++) {
        if (userRoles[i].REL_TYPE_KEY_ == 'GROUP-USER-LEADER'||userRoles[i].REL_TYPE_KEY_ == 'GROUP-USER-BELONG') {
            if(userRoles[i].NAME_=='技术中心项目管理人员') {
                return true;
            }
        }
    }
    return false;
}

//是否是非挖掘机械研究院的项目管理人员
function whetherIsProjectManagerNotZSZX(userRoles) {
    for (var i = 0; i < userRoles.length; i++) {
        if (userRoles[i].REL_TYPE_KEY_ == 'GROUP-USER-LEADER'||userRoles[i].REL_TYPE_KEY_ == 'GROUP-USER-BELONG') {
            if(userRoles[i].NAME_!='技术中心项目管理人员'&&userRoles[i].NAME_.indexOf('项目管理人员')!=-1) {
                return true;
            }
        }
    }
    return false;
}

//是否是项目管理人员
function whetherIsProjectManagerAll(userRoles) {
    for (var i = 0; i < userRoles.length; i++) {
        if (userRoles[i].REL_TYPE_KEY_ == 'GROUP-USER-LEADER'||userRoles[i].REL_TYPE_KEY_ == 'GROUP-USER-BELONG') {
            if(userRoles[i].NAME_.indexOf('项目管理人员')!=-1) {
                return true;
            }
        }
    }
    return false;
}

//如果定量目标有数据，则序号、衡量指标和项目目标水平值必填
function checkMeasureTargetRequired(rowData) {
    if (!rowData || rowData.length <= 0) {
        return {result: true};
    }

    for (var i = 0; i < rowData.length; i++) {
        var row = rowData[i];
        if (!$.trim(row.number) || !$.trim(row.target) || !$.trim(row.targetProjectLevel)) {
            return {result: false, message: '请填写项目目标中定量目标的必填项'};
        }
    }
    return {result: true};
}

//立项评审之前 填写项目成果信息
function checkAchievementRequired(rowData,required) {
    if (!rowData || rowData.length <= 0) {
        if(required){
            return {result: false, message: '请填写项目成果计划信息'};
        }else{
            return {result: true};
        }

    }

    for (var i = 0; i < rowData.length; i++) {
        var row = rowData[i];
        if (!$.trim(row.deptId) || !$.trim(row.typeId) || !$.trim(row.num) || !$.trim(row.output_time)|| !$.trim(row.description)) {
            return {result: false, message: '请填写项目成果计划信息的必填项'};
        }
    }
    return {result: true};
}

function getUserInfoById(userId) {
    var userInfo = "";
    $.ajax({
        url: jsUseCtxPath + '/xcmgProjectManager/core/xcmgProject/' + userId + '/getUserInfoById.do',
        type: 'get',
        async: false,
        contentType: 'application/json',
        success: function (data) {
            userInfo = data;
        }
    });
    return userInfo;
}

function judgeUserRespProjectDelay5(userId) {
    var delay5Number = 0;
    $.ajax({
        url: jsUseCtxPath + '/xcmgProjectManager/core/xcmgProject/respProjectDelay5.do?userId='+userId,
        type: 'get',
        async: false,
        contentType: 'application/json',
        success: function (data) {
            delay5Number = data;
        }
    });
    if(delay5Number>2) {
        return true;
    } else {
        return false;
    }
}

function bytesToSize(bytes) {
    if (bytes === 0) return '0 B';
    var k = 1024,
        sizes = ['B', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'],
        i = Math.floor(Math.log(bytes) / Math.log(k));

    return (bytes / Math.pow(k, i)).toPrecision(3) + ' ' + sizes[i];
}

function previewPdf(fileName,relativeFilePath,actionType, coverConent) {
    if(!fileName) {
        fileName='';
    }
    if(!relativeFilePath) {
        relativeFilePath='';
    }
    if(!actionType) {
        actionType='';
    }
    var previewUrl = jsUseCtxPath + "/xcmgProjectManager/core/fileUpload/fileDownload.do?action=preview&fileName="+encodeURIComponent(fileName)+"&actionType="+actionType+"&relativeFilePath="+relativeFilePath;
    window.open(jsUseCtxPath + "/pdfjs/web/viewer.html?coverContent="+encodeURIComponent(coverConent)+"&file=" + encodeURIComponent(previewUrl));
}

function previewPic(fileName,relativeFilePath,actionType) {
    if(!fileName) {
        fileName='';
    }
    if(!relativeFilePath) {
        relativeFilePath='';
    }
    if(!actionType) {
        actionType='';
    }
    var previewUrl = jsUseCtxPath + "/xcmgProjectManager/core/fileUpload/imagePreview.do?fileName=" + encodeURIComponent(fileName)+"&actionType="+actionType+"&relativeFilePath="+relativeFilePath;
    window.open(previewUrl);
}

function previewDoc(fileName,relativeFilePath,actionType,coverContent,fileSize) {
    if(!fileName) {
        fileName='';
    }
    if(!relativeFilePath) {
        relativeFilePath='';
    }
    if(!actionType) {
        actionType='';
    }
    if(!fileSize) {
        fileSize='';
    } else {
        var fileSizeArr=fileSize.split(' ');
        if(fileSizeArr.length==2) {
            if((fileSizeArr[1]=='GB' || fileSizeArr[1]=='TB'
                || fileSizeArr[1]=='PB'|| fileSizeArr[1]=='EB'
                || fileSizeArr[1]=='ZB'|| fileSizeArr[1]=='YB')||(fileSizeArr[1]=='MB' && fileSizeArr[0] > 50)) {
                mini.alert("文件超过50MB不允许预览，请下载后查看！");
                return;
            }
        }
    }
    var previewUrl = jsUseCtxPath + "/xcmgProjectManager/core/fileUpload/officePreview.do?fileName=" + encodeURIComponent(fileName)+"&actionType="+actionType+"&relativeFilePath="+relativeFilePath;
    window.open(jsUseCtxPath + "/pdfjs/web/viewer.html?coverContent="+encodeURIComponent(coverContent)+"&file=" + encodeURIComponent(previewUrl));
}

/**
 * 获取附件类型
 * */
function getFileType(fileName) {
    var suffix="";
    var suffixIndex=fileName.lastIndexOf('.');
    if(suffixIndex!=-1) {
        suffix=fileName.substring(suffixIndex+1).toLowerCase();
    }
    var pdfArray = ['pdf'];
    if(pdfArray.indexOf(suffix)!=-1){
        return 'pdf';
    }
    var officeArray = ['doc','docx','ppt','txt','xlsx','xls','pptx'];
    if(officeArray.indexOf(suffix)!=-1){
        return 'office';
    }
    var picArray = ['jpg','jpeg','jif','bmp','png','tif','gif'];
    if(picArray.indexOf(suffix)!=-1){
        return 'pic';
    }
    return 'other';
}

function returnPreviewSpan(fileName,relativeFilePath,actionType,coverContent,fileSize) {
    if (!fileSize) {
        fileSize='';
    }
    var fileType=getFileType(fileName);
    var s='';
    if(fileType=='other'){
        s = '<span  title="预览" style="color: silver" >预览</span>';
    }else if(fileType=='pdf'){
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPdf(\'' +fileName+'\',\''+relativeFilePath+'\',\''+actionType+'\',\''+coverContent+ '\')">预览</span>';
    }else if(fileType=='office'){
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewDoc(\'' +fileName+'\',\''+relativeFilePath+'\',\''+actionType+'\',\''+coverContent +'\',\''+fileSize+'\')">预览</span>';
    }else if(fileType=='pic'){
        s = '<span  title="预览" style="color:#409EFF;cursor: pointer;" onclick="previewPic(\'' +fileName+'\',\''+relativeFilePath+'\',\''+actionType+'\',\''+coverContent+ '\')">预览</span>';
    }
    return s;
}

//技术研发类需要有应用规划
function checkProjectApplyPlan() {
    var projectCategory=mini.get("projectCategory").getText();
    if(!projectCategory) {
        return {result:false,message:"请选择项目类别！"};
    }
    //非技术研发的不需要
    if(projectCategory.indexOf("技术研发")==-1) {
        return {result:true};
    }
    var applyPlan = $.trim(mini.get("applyPlan").getValue());
    if (!applyPlan) {
        return {result:false,message:"请填写项目目标中的应用规划！"};
    }
    return {result:true};
}


//检查项目计划中的计划开始、结束时间
function checkProjectPlanRequired(rowData) {
    if (!rowData || rowData.length <= 0) {
        return {result: true};
    }
    var prePlanEndTime="";
    var rowPlanStartTime="";
    var rowPlanEndTime="";
    for (var i = 0; i < rowData.length; i++) {
        var row = rowData[i];
        if (!row.planStartTime) {
            return {result: false, message: '请填写项目计划中“' + row.stageName + '”阶段的计划开始时间'};
        }
        if (!row.planEndTime) {
            return {result: false, message: '请填写项目计划中“' + row.stageName + '”阶段的计划结束时间'};
        }
        rowPlanStartTime=row.planStartTime;
        if(rowPlanStartTime.constructor==Date) {
            rowPlanStartTime=convertDate2Str(rowPlanStartTime);
        }
        rowPlanEndTime=row.planEndTime;
        if(rowPlanEndTime.constructor==Date) {
            rowPlanEndTime=convertDate2Str(rowPlanEndTime);
        }
        if(rowPlanStartTime>rowPlanEndTime) {
            return {result: false, message: '项目计划中“' + row.stageName + '”阶段的计划开始时间应小于等于计划结束时间'};
        }
        if(prePlanEndTime) {
            if(rowPlanStartTime<prePlanEndTime) {
                return {result: false, message: '项目计划中“' + row.stageName + '”阶段的计划开始时间应大于等于前一阶段的计划结束时间'};
            }
        }
        prePlanEndTime=rowPlanEndTime;
    }
    return {result: true};
}

function convertDate2Str(dateObj) {
    var result="";
    result+=dateObj.getFullYear();
    var month=dateObj.getMonth()+1;
    if(month<10) {
        month='0'+month;
    }
    var date=dateObj.getDate();
    if(date<10) {
        date='0'+date;
    }
    result+='-'+month+'-'+date;
    return result;
}

//检查项目成员中是否有项目指导人
function checkMemberHasGuid(rowData) {
    if (!rowData || rowData.length <= 0) {
        return {result: false,message:'请添加项目指导人'};
    }
    for (var i = 0; i < rowData.length; i++) {
        var row = rowData[i];
        var roleName=row.roleName;
        if(roleName=='项目指导人') {
            return {result:true};
        }
    }
    return {result:false,message:'请添加项目指导人'};
}

//产学研表单特殊处理
function cxyProjectProcess() {
    if(isCxy) {
        $("#hzdwTr").show();
        $("#cxyMemOut").show();
    } else {
        $("#hzdwTr").hide();
        $("#cxyMemOut").hide();
        mini.get("hzdw").setValue("");
        grid_cxy_memberInfo.setData([]);
    }
}

function filterMapByKey(map, keys) {
    var newMap = new Map();
    for (var key of keys) {
        if (map.has(key)) {
            newMap.set(key, map.get(key));
        }
    }
    return newMap;
}
function judgeIsNumber(numStr) {
    if(!numStr) {
        return false;
    }
    var r = new RegExp("^(-?\\d+)(\\.\\d+)?$");
    if(!r.test(numStr)) {
        return false;
    }
    return true;
}

function getUserDutyStatus(userId) {
    var userInfo;
    if (!userId) {
        return null;
    }
    $.ajax({
        url: jsUseCtxPath + '/xcmgProjectManager/core/xcmgProject/getDutyStatus.do?userId='+userId,
        type: 'get',
        async: false,
        contentType: 'application/json',
        success: function (data) {
            userInfo = data;
        }
    });
    return userInfo;
}
