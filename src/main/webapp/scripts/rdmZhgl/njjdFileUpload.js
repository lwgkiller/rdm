$(function () {
    initUpload();
});
function initUpload(){
    $('#fileUploadDiv').Huploadify({
        formData:{njjdId:njjdId,njfjDl:njfjDl},
        sendFileAttr:["fileName","fileSize","typeId"],
        uploadFileList:grid,
        url:jsUseCtxPath+'/zhgl/core/njjd/fileUpload.do',
        onUploadStart:function (file) {
            var row = this.uploadFileList.findRow(function(row){
                if(row.id == file.id) return true;
            });
            if(!row.typeId) {
                mini.alert("请选择附件类型")
                return;
            }
            return {success:true,message:""};
        },
        addFileToGrid: function (files) {
            console.log("addFileToGrid的typeInfos",typeInfos)
            for(var i=0;i<files.length;i++) {
                var file=files[i];
                var row = {id:file.id,fileName:file.name,status:file.status,fileSize:bytesToSize(file.size),complete:0,typeId:"",typeName:""};
                this.uploadFileList.addRow(row);
            }
        }
    });
}




