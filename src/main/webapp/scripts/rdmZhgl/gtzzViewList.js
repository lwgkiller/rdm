$(function () {
})



function addForm() {
    let url = jsUseCtxPath + "/rdmZhgl/core/gtzz/getEditPage.do?action=add";
    mini.open({
        title: "新增",
        url: url,
        width: 1600,
        height: 800,
        showModal: true,
        allowResize: true,
        onload: function () {
        },
        ondestroy: function (action) {
            planListGrid.reload();
        }
    });
}

//修改
function viewForm(id) {
    var url = jsUseCtxPath + "/rdmZhgl/core/gtzz/getViewPage.do?action=edit&&mainId=" + id;
    window.open(url);
}
