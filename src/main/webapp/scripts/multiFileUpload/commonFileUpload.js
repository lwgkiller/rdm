$(function(){
    $('#fileUploadDiv').Huploadify({
        sendFileAttr:["fileName","fileSize"],
        uploadFileList:multiuploadGrid,
        url:jsUseCtxPath+'/xcmgProjectManager/core/fileUpload/xcmgDocMgrUpload.do',
        addFileToGrid: function (files) {
            for(var i=0;i<files.length;i++) {
                var file=files[i];
                var row = {id:file.id,fileName:file.name,status:file.status,fileSize:bytesToSize(file.size),complete:0};
                this.uploadFileList.addRow(row);
            }
        }
    });
});