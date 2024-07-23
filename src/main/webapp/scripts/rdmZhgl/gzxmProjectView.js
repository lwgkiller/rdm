var currentDate = new Date();
var currentMonth = currentDate.getMonth()+1;
var week = getMonthWeek(currentDate);
var asign = currentMonth+''+week;
$(function () {
    var column=itemGrid.getColumn("nextYear")
    var nextYear = new Date().getFullYear()+1;
    itemGrid.updateColumn(column,{header:nextYear+'年'});
    loadGrid();
})
function getMonthWeek(_currentDate) {
    var currentDay = new Date(_currentDate);
    var theSaturday = currentDay.getDate() + (6 - currentDay.getDay());
    return Math.ceil(theSaturday / 7);
}
function printpage() {
    // 列标题，逗号隔开，每一个逗号就是隔开一个单元格
    let str = `项目自评,编号,重要度,任务名称,任务目标,输出物,责任部门,责任人,计划开始时间,计划结束时间,实际开始时间,实际结束时间,延期原因与补救措施,备注,完成情况`;
    str += valueStr;
    // encodeURIComponent解决中文乱码
    const uri = 'data:text/csv;charset=utf-8,\ufeff' + encodeURIComponent(str);
    // 通过创建a标签实现
    const link = document.createElement("a");
    link.href = uri;
    // 对下载的文件命名
    link.download =  "国重项目任务表.csv";
    link.click();

};
// 输出base64编码
function loadGrid() {
    var paramArray = [{name: "mainId", value: mainId}];
    var data = {};
    data.filter = mini.encode(paramArray);
    data.mainId = mainId;
    itemGrid.load(data);
}
function timeFlag(_planEndDate) {
    var planEndDate = new Date(_planEndDate);
    var currentDate = new Date();
    var planYear = planEndDate.getFullYear();
    var currentYear = currentDate.getFullYear();
    var result = '';
    if(planYear>currentYear){
        result = 'nextYear';
    }else if(planYear<currentYear){
        result = 'preYear';
    }else{
        var week = getMonthWeek(_planEndDate);
        var month = planEndDate.getMonth()+1;
        result = month+''+week;
    }
    return result;
}
