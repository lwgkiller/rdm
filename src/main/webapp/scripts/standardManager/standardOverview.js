$(function () {
    //查询标准体系类别
    $.ajax({
        url: jsUseCtxPath +'/standardManager/core/standardSystem/queryCategory.do',
        success:function (data) {
            if(data) {
                mini.get("systemCategoryId").load(data);
                mini.get("systemCategoryId").setValue(systemCategoryValue);
                refreshAllChart();
            }
        }
    });
});
