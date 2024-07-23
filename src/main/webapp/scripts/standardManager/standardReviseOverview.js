$(function () {
    // refreshAchievementType();
    timeInit();
});

function timeInit() {
    //今年1月1号到当前日期
    let nowDate = new Date();
    let nowYear = nowDate.getFullYear();
    let month = nowDate.getMonth() + 1;
    let day = nowDate.getDate();
    let startTime =nowYear + "-01-01";
    let endTime = nowYear + "-" + month + "-" + day;
    mini.get("yyTimeFrom").setValue(startTime);
    mini.get("yyTimeTo").setValue(endTime);
    queryStandardReviseChartData();
    queryStandardYearChartData();
    queryStandardJdChartData();
}

function refreshAchievementType() {
    mini.get("deptId").setValue('161416982793248776');
    mini.get("deptId").setText('小挖研究所');
}

// 指定图表的配置项和数据
var standardReviseOption = {
    title: {
        show: false,
        text: '各部门季度累计完成情况',
        textStyle: {
            lineHeight: 80,
            height: 80,
        },
        textAlign: 'auto',
        textVerticalAlign: 'top'
    },
    tooltip: {
        trigger: 'axis',
        axisPointer: {            // 坐标轴指示器，坐标轴触发有效
            type: 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
        },
        formatter: function (params) {
            let res = '<p>部门：' + params[0].name + '</p>';
            for (let i = 0; i< params.length; i++) {
                res += '<p><span style="display:inline-block;margin-right:5px;border-radius:10px;width:9px;height:9px;background-color:' + params[i].color + '"></span>';
                if (params[i].seriesType === 'line') {
                    res += params[i].seriesName + '：' + params[i].data + '%';
                } else {
                    res += params[i].seriesName + '：' + params[i].data;
                }
            }
            return res;
        }
    },
    color: ['#e20200','#00B215', '#6495ED'],
    legend: {
        data: []
    },
    grid: {
        left: '3.5%',
        right: '3.5%',
        top: '15%',
        bottom: '2%',
        containLabel: true
    },
    xAxis: [
        {
            type: 'category',
            data: [],
            axisLabel: {
                show: true,
                interval: 0,
                rotate: 0,
                color: '#333',
                fontSize: 15,
                formatter: function (params) {
                    var newParamsName = "";
                    var paramsNameNumber = params.length;
                    var provideNumber = 2;
                    var rowNumber = Math.ceil(paramsNameNumber / provideNumber);
                    if (paramsNameNumber > provideNumber) {
                        for (var p = 0; p < rowNumber; p++) {
                            var tempStr = "";
                            var start = p * provideNumber;
                            var end = start + provideNumber;
                            if (p == rowNumber - 1) {
                                tempStr = params.substring(start, paramsNameNumber);
                            } else {
                                tempStr = params.substring(start, end) + "\n";
                            }
                            newParamsName += tempStr;
                        }

                    } else {
                        newParamsName = params;
                    }
                    return newParamsName
                }
            }
        }
    ],
    yAxis: [
        {
            type: 'value',
            name: '︵\n计\n划\n︶\n完\n成\n数\n量\n︵\n个\n︶',
            nameLocation: 'center',
            nameGap: 35,
            nameRotate: 0,
            position: 'left',
            minInterval:1
        },
        {
            type: 'value',
            name: '总\n量\n完\n成\n率\n%',
            nameLocation: 'center',
            nameGap: 35,
            nameRotate: 0,
            position: 'right',
            axisLabel: {
                formatter: '{value}%'
            }

        }
    ],
    series: []
};


function queryStandardReviseChartData() {
    var startTime=mini.get("yyTimeFrom").getText();
    if (!startTime) {
        mini.alert(standardReviseOverview_name1);
        return;
    }
    var endTime=mini.get("yyTimeTo").getText();
    if (!endTime) {
        mini.alert(standardReviseOverview_name2);
        return;
    }
    $.ajax({
        url: jsUseCtxPath + '/standardManager/core/standardManagement/queryReviseOverviewByDept.do?startTime='+startTime+"&endTime="+endTime,
        contentType: 'application/json',
        success: function (resp) {
            if(!resp.success) {
                return;
            }
            let returnData = resp.data;
            if (returnData) {
                //对option进行赋值
                standardReviseOption.legend.data = returnData.legendData;
                standardReviseOption.xAxis[0].data = returnData.xAxisData;
                standardReviseOption.series = returnData.series;
                standardReviseBar.setOption(standardReviseOption);
            }
        }
    });
}

var standardYearOption = {
    title: {
        show: false,
        text: '各部门年度累计完成情况',
        textStyle: {
            lineHeight: 80,
            height: 80,
        },
        textAlign: 'auto',
        textVerticalAlign: 'top'
    },
    tooltip: {
        trigger: 'axis',
        axisPointer: {            // 坐标轴指示器，坐标轴触发有效
            type: 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
        },
        formatter: function (params) {
            let res = '<p>部门：' + params[0].name + '</p>';
            for (let i = 0; i< params.length; i++) {
                res += '<p><span style="display:inline-block;margin-right:5px;border-radius:10px;width:9px;height:9px;background-color:' + params[i].color + '"></span>';
                if (params[i].seriesType === 'line') {
                    res += params[i].seriesName + '：' + params[i].data + '%';
                } else {
                    res += params[i].seriesName + '：' + params[i].data;
                }
            }
            return res;
        }
    },
    color: ['#e20200','#00b215', '#6495ED'],
    legend: {
        data: []
    },
    grid: {
        left: '3.5%',
        right: '3.5%',
        top: '15%',
        bottom: '2%',
        containLabel: true
    },
    xAxis: [
        {
            type: 'category',
            data: [],
            axisLabel: {
                show: true,
                interval: 0,
                rotate: 0,
                color: '#333',
                fontSize: 15,
                formatter: function (params) {
                    var newParamsName = "";
                    var paramsNameNumber = params.length;
                    var provideNumber = 2;
                    var rowNumber = Math.ceil(paramsNameNumber / provideNumber);
                    if (paramsNameNumber > provideNumber) {
                        for (var p = 0; p < rowNumber; p++) {
                            var tempStr = "";
                            var start = p * provideNumber;
                            var end = start + provideNumber;
                            if (p == rowNumber - 1) {
                                tempStr = params.substring(start, paramsNameNumber);
                            } else {
                                tempStr = params.substring(start, end) + "\n";
                            }
                            newParamsName += tempStr;
                        }

                    } else {
                        newParamsName = params;
                    }
                    return newParamsName
                }
            }
        }
    ],
    yAxis: [
        {
            type: 'value',
            name: '︵\n计\n划\n︶\n完\n成\n数\n量\n︵\n个\n︶',
            nameLocation: 'center',
            nameGap: 35,
            nameRotate: 0,
            position: 'left',
            minInterval:1
        },
        {
            type: 'value',
            name: '总\n量\n完\n成\n率\n%',
            nameLocation: 'center',
            nameGap: 35,
            nameRotate: 0,
            position: 'right',
            axisLabel: {
                formatter: '{value}%'
            }

        }
    ],
    series: []
};

function queryStandardYearChartData() {
    $.ajax({
        url: jsUseCtxPath + '/standardManager/core/standardManagement/queryYearOverviewByDept.do?',
        contentType: 'application/json',
        success: function (resp) {
            if(!resp.success) {
                return;
            }
            let returnData = resp.data;
            if (returnData) {
                //对option进行赋值
                standardYearOption.legend.data = returnData.legendData;
                standardYearOption.xAxis[0].data = returnData.xAxisData;
                standardYearOption.series = returnData.series;
                standardYearBar.setOption(standardYearOption);
            }
        }
    });
}
var standardJdOption = {
    title: {
        show: false,
        text: '各部门全年及各季度计划完成情况',
        textStyle: {
            lineHeight: 80,
            height: 80,
        },
        textAlign: 'auto',
        textVerticalAlign: 'top'
    },
    tooltip: {
        trigger: 'axis',
        axisPointer: {            // 坐标轴指示器，坐标轴触发有效
            type: 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
        },
        formatter: function (params) {
            let res = '<p>季度：' + params[0].name + '</p>';
            for (let i = 0; i< params.length; i++) {
                res += '<p><span style="display:inline-block;margin-right:5px;border-radius:10px;width:9px;' +
                    'height:9px;background-color:' + params[i].color + '"></span>';
                if (params[i].seriesType === 'line') {
                    res += params[i].seriesName + '：' + params[i].data + '%';
                } else {
                    res += params[i].seriesName + '：' + params[i].data;
                }
            }
            return res;
        }
    },
    color: ['#e20200','#00B215', '#6495ED'],
    legend: {
        data: []
    },
    grid: {
        left: '3.5%',
        right: '3.5%',
        top: '15%',
        bottom: '2%',
        containLabel: true
    },
    xAxis: [
        {
            type: 'category',
            data: [],
            axisLabel: {
                show: true,
                interval: 0,
                rotate: 0,
                color: '#333',
                fontSize: 15,
                formatter: function (params) {
                    var newParamsName = "";
                    var paramsNameNumber = params.length;
                    var provideNumber = 4;
                    var rowNumber = Math.ceil(paramsNameNumber / provideNumber);
                    if (paramsNameNumber > provideNumber) {
                        for (var p = 0; p < rowNumber; p++) {
                            var tempStr = "";
                            var start = p * provideNumber;
                            var end = start + provideNumber;
                            if (p == rowNumber - 1) {
                                tempStr = params.substring(start, paramsNameNumber);
                            } else {
                                tempStr = params.substring(start, end) + "\n";
                            }
                            newParamsName += tempStr;
                        }

                    } else {
                        newParamsName = params;
                    }
                    return newParamsName
                }
            }
        }
    ],
    yAxis: [
        {
            type: 'value',
            name: '︵\n计\n划\n︶\n完\n成\n数\n量\n︵\n个\n︶',
            nameLocation: 'center',
            nameGap: 35,
            nameRotate: 0,
            position: 'left',
            minInterval:1
        },
        {
            type: 'value',
            name: '总\n量\n完\n成\n率\n%',
            nameLocation: 'center',
            nameGap: 35,
            nameRotate: 0,
            position: 'right',
            axisLabel: {
                formatter: '{value}%'
            }

        }
    ],
    series: []
};


function queryStandardJdChartData() {
    var deptId=mini.get("deptId").getValue();
    $.ajax({
        url: jsUseCtxPath + '/standardManager/core/standardManagement/queryJdOverviewByDept.do?deptId='+deptId,
        contentType: 'application/json',
        success: function (resp) {
            if(!resp.success) {
                return;
            }
            let returnData = resp.data;
            if (returnData) {
                //对option进行赋值
                standardJdOption.legend.data = returnData.legendData;
                standardJdOption.xAxis[0].data = returnData.xAxisData;
                standardJdOption.series = returnData.series;
                standardJdBar.setOption(standardJdOption);
            }
        }
    });
}