$(function () {
    if(action!='add'){
        infoForm.setData(applyObj);
        mini.get('id').setValue(applyObj.id);
    }else{
        mini.get('sjsz_date').setEnabled(false);
        mini.get('sjxfqh_date').setEnabled(false);
    }
    if (action == 'view') {
        infoForm.setEnabled(false);
        $('#save').hide();
    }
    if (action == 'edit') {
        //如果已提交，则不允许修改时间
        if(applyObj.infoStatus=='2'){
            setDateEnabled(false);
        }
        if(type=='apply'){
            setDateEnabled(true);
        }
    }

})
function saveData() {
    infoForm.validate();
    if (!infoForm.isValid()) {
        return;
    }
    var formData = infoForm.getData();
    var config = {
        url: jsUseCtxPath+"/rdmZhgl/core/yfjb/save.do",
        method: 'POST',
        data: formData,
        success: function (result) {
            //如果存在自定义的函数，则回调
            var result=mini.decode(result);
            if(result.success){
                CloseWindow("ok");
            }else{
            };
        }
    }
    _SubmitJson(config);
}
function calculateDifference() {
    var basePrice = mini.get('basePrice').getValue();
    var newItemPrice = mini.get('newItemPrice').getValue();
    if(basePrice&&newItemPrice){
        mini.get('differentPrice').setValue((basePrice-newItemPrice).toFixed(2));
        calculateCost()
    }
}
function calculateCost() {
    var perSum = mini.get('perSum').getValue();
    var replaceRate = mini.get('replaceRate').getValue();
    var differentPrice = mini.get('differentPrice').getValue();
    //根据降本方式进行计算
    var costType = mini.get('costType').getValue();
    if(costType=='4'){
        var basePrice = mini.get('basePrice').getValue();
        if(perSum&&replaceRate&&basePrice){
            mini.get('perCost').setValue((perSum*replaceRate*basePrice/100).toFixed(2));
        }
    }else{
        mini.get('perCost').setValue((perSum*replaceRate*differentPrice/100).toFixed(2));
    }
}
function autoFillValue() {
    var basePrice = mini.get('basePrice').getValue();
    var newItemPrice = mini.get('newItemPrice').getValue();
    var costType = mini.get('costType').getValue();
    var perSum = mini.get('perSum').getValue();
    var replaceRate = mini.get('replaceRate').getValue();
    var isReplace = mini.get("isReplace").getValue();
    //1.计算差额
    //如果是物料取消，差额=基准价格，如果不是 差额=基准价格-替代物料价格
    if(costType&&basePrice){
        if(costType=='4'){
            mini.get('differentPrice').setValue(basePrice);
        }else{
            if(newItemPrice){
                mini.get('differentPrice').setValue((basePrice-newItemPrice).toFixed(2));
            }
        }
    }
    //2.计算单台降本
    var differentPrice = mini.get('differentPrice').getValue();
    if(costType){
        if(costType=='4'){
            if(perSum&&replaceRate&&basePrice){
                mini.get('perCost').setValue((perSum*replaceRate*basePrice/100).toFixed(2));
            }
        }else{
            mini.get('perCost').setValue((perSum*replaceRate*differentPrice/100).toFixed(2));
        }
    }
    //3.计算已实现单台降本
    if(isReplace){
        if(isReplace=='0'){
            mini.get('achieveCost').setValue('0');
        }else if(isReplace=='1'){
            mini.get('achieveCost').setValue(mini.get('perCost').getValue());
        }
    }
}

function calculatePerCost(e) {
    if(e.selected.key_=='0'){
        mini.get('achieveCost').setValue('0');
    }else if(e.selected.key_=='1'){
        mini.get('achieveCost').setValue(mini.get('perCost').getValue());
    }
}
function onCostType(e) {
    if(e.selected.key_=='4'){
        var basePrice = mini.get('basePrice').getValue();
        if(basePrice){
            mini.get('differentPrice').setValue(basePrice);
        }
    }
}
function isNeedSz(e) {
    if(e.selected.key_=='0'){
        mini.get('jhsz_date').setEnabled(false);
    }else if(e.selected.key_=='1'){
        mini.get('jhsz_date').setEnabled(true);
    }
}
function setDateEnabled(flag) {
    if(mini.get('jhsz_date').getValue()){
        mini.get('jhsz_date').setEnabled(flag);
    }
    if(mini.get('sjsz_date').getValue()){
        mini.get('sjsz_date').setEnabled(flag);
    }
    if(mini.get('jhxfqh_date').getValue()){
        mini.get('jhxfqh_date').setEnabled(flag);
    }
    if(mini.get('sjxfqh_date').getValue()){
        mini.get('sjxfqh_date').setEnabled(flag);
    }
    if(mini.get('jhqh_date').getValue()){
        mini.get('jhqh_date').setEnabled(flag);
    }
    if(mini.get('sjqh_date').getValue()){
        mini.get('sjqh_date').setEnabled(flag);
    }
    // if(mini.get('jsfachwc_date').getValue()){
    //     mini.get('jsfachwc_date').setEnabled(flag);
    // }
    // if(mini.get('sztzxfwc_date').getValue()){
    //     mini.get('sztzxfwc_date').setEnabled(flag);
    // }
    // if(mini.get('xnkkxyzwc_date').getValue()){
    //     mini.get('xnkkxyzwc_date').setEnabled(flag);
    // }
    // if(mini.get('jbfazsss_date').getValue()){
    //     mini.get('jbfazsss_date').setEnabled(flag);
    // }
    // if(mini.get('jhxfsz_date').getValue()){
    //     mini.get('jhxfsz_date').setEnabled(flag);
    // }
}
//判断切换通知单是否填写
function conformChangeForm() {
    var id = mini.get('id').getValue();
    let postData = {"id": id};
    let url = jsUseCtxPath + '/rdmZhgl/core/yfjb/conformChangeForm.do';
    let resultData = ajaxRequest(url, 'POST', false, postData);
    if (!resultData.success) {
        mini.alert(resultData.message);
        mini.get('sjxfqh_date').setValue('');
        return
    }
}
//判断试制通知单是否填写
function conformProduceForm() {
    var id = mini.get('id').getValue();
    let postData = {"id": id};
    let url = jsUseCtxPath + '/rdmZhgl/core/yfjb/conformProduceForm.do';
    let resultData = ajaxRequest(url, 'POST', false, postData);
    if (!resultData.success) {
        mini.alert(resultData.message);
        mini.get('sjsz_date').setValue('');
        return
    }
}
/**
 *1.计划试制时间<计划下发切换通知单时间
 * 2.计划下发切换通知单时间<计划切换时间
 * */
function verifyDate(type) {
    var jhsz_date = mini.get('jhsz_date').getText();
    var jhxfqh_date = mini.get('jhxfqh_date').getText();
    var jhqh_date = mini.get('jhqh_date').getText();
    if(jhsz_date&&jhxfqh_date){
        if(jhsz_date>jhxfqh_date){
            mini.alert("计划试制时间不能大于计划下发切换通知单时间！");
            mini.get(type).setValue('');
            return;
        }
    }
    if(jhqh_date&&jhxfqh_date){
        if(jhqh_date<jhxfqh_date){
            mini.alert("计划切换时间不能小于计划下发切换通知单时间！");
            mini.get(type).setValue('');
            return;
        }
    }
    if(jhsz_date&&jhqh_date){
        if(jhqh_date<jhsz_date){
            mini.alert("计划切换时间不能小于计划试制时间！");
            mini.get(type).setValue('');
            return;
        }
    }
}
