$(function () {
    initUpload();
});
function initUpload(){
    $('#fileUploadDiv').Huploadify({
        formData:{applyId:applyId,stageKey:stageKey},
        sendFileAttr:["fileName","fileSize","typeId"],
        uploadFileList:fileGrid,
        url:jsUseCtxPath+'/standardManager/core/standardManagement/fileUpload.do',
        onUploadStart:function (file) {
            var row = this.uploadFileList.findRow(function(row){
                if(row.id == file.id) return true;
            });
            if(!row.typeId) {
                return {success:false,message:"“"+row.fileName+"”没有选择附件类别，请点击单元格处选择！"};
            }
            return {success:true,message:""};
        },
        addFileToGrid: function (files) {
            console.log("addFileToGrid的typeInfos",typeInfos)
            for(var i=0;i<files.length;i++) {
                var file=files[i];
                var row = {id:file.id,fileName:file.name,fileSize:bytesToSize(file.size),complete:0};
                this.uploadFileList.addRow(row);
            }
        }
    });
}




