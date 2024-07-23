function initUpload(){
    $('#fileUploadDiv').Huploadify({
        formData:projectParams,
        sendFileAttr:["fileName","fileSize","picType","fileDesc"],
        uploadFileList:multiuploadGrid,
        url:jsUseCtxPath+'/rdmZhgl/core/product/upload.do',
        onUploadStart:function (file) {
            var row = this.uploadFileList.findRow(function(row){
                if(row.id == file.id) return true;
            });
            if(!row.picType) {
                return {success:false,message:"“"+row.fileName+"”请选择照片类型！"};
            }
            return {success:true,message:""};
        },
        addFileToGrid: function (files) {
            for(var i=0;i<files.length;i++) {
                var file=files[i];
                var fileType = file.type;
                if(fileType.indexOf('image')==-1){
                    mini.alert("请上传图片类型");
                   return
                }
                var row = {id:file.id,fileName:file.name,status:file.status,fileSize:bytesToSize(file.size),complete:0};
                this.uploadFileList.addRow(row);
            }
        }
    });
}
