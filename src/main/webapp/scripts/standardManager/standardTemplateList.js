function searchDoc() {
    templateListGrid.setUrl(url+mini.get("#docName").getValue());
    templateListGrid.load();
}

//打开文件上传窗口
function openUploadWindow() {
    mini.open({
        title: "文件上传",
        url: jsUseCtxPath + "/standardManager/core/standardConfig/templateUploadWindow.do",
        width: 850,
        height: 400,
        showModal:false,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
            searchDoc();
        }
    });
}



//下载文档
function seeTemplate(id) {
    window.open(jsUseCtxPath + "/standardManager/core/standardConfig/templateSee.do?id="+id);
}

//删除文档
function deleteDoc(id,fileName,relativeFilePath) {
    mini.confirm("确定删除？", "确定？",
        function (action) {
            if (action == "ok") {
                $.ajaxSettings.async = false;
                var url = jsUseCtxPath + "/standardManager/core/standardConfig/templateDelete.do";
                var data = {
                    id: id,
                    relativeFilePath: relativeFilePath,
                    fileName: fileName
                };
                $.post(
                    url,
                    data,
                    function (json) {
                        searchDoc();
                    });
                $.ajaxSettings.async = true;
            }
        }
    );
}