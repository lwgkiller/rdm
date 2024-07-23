$(function () {
});

function onProductType(e) {
    var record = e.record;
    var certType = record.productType;
    var resultText = '';
    for (var i = 0; i < productTypeList.length; i++) {
        if (productTypeList[i].key_ == certType) {
            resultText = productTypeList[i].text;
            break
        }
    }
    return resultText;
}

function onCabForm(e) {
    var record = e.record;
    var testType = record.cabForm;
    var resultText = '';
    for (var i = 0; i < cabFormList.length; i++) {
        if (cabFormList[i].key_ == testType) {
            resultText = cabFormList[i].text;
            break
        }
    }
    return resultText;
}

function exportExcel() {
    var params = [];
    var parent = $(".search-form");
    var inputAry = $("input", parent);
    inputAry.each(function (i) {
        var el = $(this);
        var obj = {};
        obj.name = el.attr("name");
        if (!obj.name) return true;
        obj.value = el.val();
        params.push(obj);
    });
    $("#filter").val(mini.encode(params));
    var excelForm = $("#excelForm");
    excelForm.submit();
}
