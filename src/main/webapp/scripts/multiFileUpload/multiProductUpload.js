function initUpload(){
    $('#fileUploadDiv').Huploadify({
        formData:projectParams,
        sendFileAttr:["fileName","fileSize","fileDeliveryId","productIds","productNames"],
        uploadFileList:grid,
        url:jsUseCtxPath+'/xcmgProjectManager/core/fileUpload/upload.do',
        onUploadStart:function (file) {
            var row = this.uploadFileList.findRow(function(row){
                if(row.id == file.id) return true;
            });
            if(!row.fileDeliveryId) {
                return {success:false,message:"“"+row.fileName+"”没有选择交付物类型，请点击单元格处选择！"};
            }
            if(includeProductIds){
                if(!row.productIds) {
                    return {success:false,message:"“"+row.fileName+"”没有选择适用产品，请点击单元格处选择！"};
                }
            }
            //判断这个类型的交付物是不是只能在PDM中上传
            if(judgeFromPdm(row.fileDeliveryId)) {
                return {success:false,message:"交付物类型“"+row.deliveryTypeName+"”不允许在RDM文件归档中直接上传，请在对应系统（PDM/TDM/SDM）或相关流程中归档！"};
            }
            //判断这个交付物能不能由这个用户上传
            // if(!projectParams.action || projectParams.action!='change') {
            //     var judgeResult=judgeUserCanUpload(row.fileDeliveryId,currentUserId);
            //     if(!judgeResult) {
            //         return {success:false,message:"交付物类型“"+row.deliveryTypeName+"”不允许当前用户上传！请查看项目成员中的“负责交付物”信息"};
            //     }
            // }
            return {success:true,message:""};
        },
        addFileToGrid: function (files) {
            for(var i=0;i<files.length;i++) {
                var file=files[i];
                var row = {id:file.id,fileName:file.name,status:file.status,fileSize:bytesToSize(file.size),complete:0};
                this.uploadFileList.addRow(row);
            }
        }
    });
}

function judgeUserCanUpload(fileDeliveryId,userId) {
    if(!deliveryInfos) {
        return true;
    }
    for(var index=0;index<deliveryInfos.length;index++) {
        if(deliveryInfos[index].deliveryId==fileDeliveryId) {
            var userIds=deliveryInfos[index].pointUserIds;
            if(!userIds || userIds.length==0 || userIds.indexOf(userId) != -1) {
                return true;
            } else {
                return false;
            }
        }
    }
    return true;
}

function judgeFromPdm(fileDeliveryId) {
    if(!deliveryInfos) {
        return false;
    }
    for(var index=0;index<deliveryInfos.length;index++) {
        if(deliveryInfos[index].deliveryId==fileDeliveryId) {
            var fromPdm=deliveryInfos[index].fromPdm;
            if(fromPdm && fromPdm=='yes') {
                return true;
            }
        }
    }
    return false;
}



