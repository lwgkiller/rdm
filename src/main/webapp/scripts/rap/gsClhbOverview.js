$(function () {
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

    mini.get("deptNumTimeFrom").setValue(startTime);
    mini.get("deptNumTimeTo").setValue(endTime);
    let previousYear = nowDate.getFullYear()-1;
    let monthStartTime = previousYear + "-" + month+"-01";
    mini.get("monthNumTimeFrom").setValue(monthStartTime);
    mini.get("monthNumTimeTo").setValue(endTime);
    queryDeptNumData();
    queryDeptDelayData();
    queryYearNumData();
    queryMonthNumData();
    queryModelNumData();
}



// 指定图表的配置项和数据
var deptNumDataOption = {
    title: {
        show: false,
        text: '各部门累计完成情况',
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
                res += params[i].seriesName + '：' + params[i].data;
            }
            return res;
        }
    },
    color: ['#1d8404',"orange",'#e20200', '#6495ED'],
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
            name: '数\n量\n︵\n个\n︶',
            nameLocation: 'center',
            nameGap: 35,
            nameRotate: 0,
            position: 'left',
            minInterval:1
        }
    ],
    series: []
};

function queryDeptNumData() {
    var startTime=mini.get("deptNumTimeFrom").getText();
    if (!startTime) {
        mini.alert("请选择开始时间");
        return;
    }
    var endTime=mini.get("deptNumTimeTo").getText();
    if (!endTime) {
        mini.alert("请选择结束时间");
        return;
    }
    $.ajax({
        url: jsUseCtxPath + '/environment/core/ClhbOverview/queryNumByDept.do?startTime='+startTime+"&endTime="+endTime,
        contentType: 'application/json',
        success: function (resp) {
            if(!resp.success) {
                return;
            }
            let returnData = resp.data;
            if (returnData) {
                //对option进行赋值
                deptNumDataOption.legend.data = returnData.legendData;
                deptNumDataOption.xAxis[0].data = returnData.xAxisData;
                deptNumDataOption.series = returnData.series;
                deptNumDataBar.setOption(deptNumDataOption);
            }
        }
    });
}

var deptDelayDataOption = {
    title: {
        show: false,
        text: '各部门环保信息公开流程延期情况',
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
                res += params[i].seriesName + '：' + params[i].data;
            }
            return res;
        }
    },
    color: ['#e20200'],
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
            name: '数\n量\n︵\n个\n︶',
            nameLocation: 'center',
            nameGap: 25,
            nameRotate: 0,
            position: 'left',
            minInterval:1
        }
    ],
    series: []
};

function queryDeptDelayData() {
    $.ajax({
        url: jsUseCtxPath + '/environment/core/ClhbOverview/queryDelayByDept.do?',
        contentType: 'application/json',
        success: function (resp) {
            if(!resp.success) {
                return;
            }
            let returnData = resp.data;
            if (returnData) {
                //对option进行赋值
                deptDelayDataOption.legend.data = returnData.legendData;
                deptDelayDataOption.xAxis[0].data = returnData.xAxisData;
                deptDelayDataOption.series = returnData.series;
                deptDelayDataBar.setOption(deptDelayDataOption);
            }
        }
    });
}

// 指定图表的配置项和数据
var yearNumOption = {
    tooltip: {
        trigger: 'item',
        formatter: '{b}:{c}',
    },
    legend: {
        orient: 'vertical',
        left: 'left',
        data: []
    },
    series: [
        {
            type: 'pie',
            radius: '55%',
            center: ['50%','60%'],
            data: [],
            emphasis: {
                itemStyle: {
                    shadowBlur: 10,
                    shadowOffsetX: 0,
                    shadowColor: 'rgb(0,0,0,0.5)'
                }
            },
            label: {
                show: true,
                position: 'outside',
                formatter: '{b}: {c}'
            }
        }
    ]
};


function queryYearNumData() {
    $.ajax({
        url: jsUseCtxPath + '/environment/core/ClhbOverview/queryYearNum.do?',
        contentType: 'application/json',
        success: function (resp) {
            if(!resp.success) {
                return;
            }
            let returnData = resp.data;
            if (returnData) {
                //对option进行赋值
                yearNumOption.legend.data = returnData.legendData;
                yearNumOption.series[0].data = returnData.series;
                yearNumDataBar.setOption(yearNumOption);
            }
        }
    });
}

var monthNumDataOption = {
    title: {
        show: false,
        text: '国四环保信息公开月度完成量',
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
            let res = '<p>月份：' + params[0].name + '</p>';
            for (let i = 0; i< params.length; i++) {
                res += '<p><span style="display:inline-block;margin-right:5px;border-radius:10px;width:9px;height:9px;background-color:' + params[i].color + '"></span>';
                res += params[i].seriesName + '：' + params[i].data;
            }
            return res;
        }
    },
    color: ['#1d8404'],
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
                    var provideNumber = 7;
                    var rowNumber = Math.ceil(paramsNameNumber);
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
            name: '数\n量\n︵\n个\n︶',
            nameLocation: 'center',
            nameGap: 35,
            nameRotate: 0,
            position: 'left',
            minInterval:1
        }
    ],
    series: []
};

function queryMonthNumData() {
    var startTime=mini.get("monthNumTimeFrom").getText();
    if (!startTime) {
        mini.alert("请选择开始时间");
        return;
    }
    var endTime=mini.get("monthNumTimeTo").getText();
    if (!endTime) {
        mini.alert("请选择结束时间");
        return;
    }
    $.ajax({
        url: jsUseCtxPath + '/environment/core/ClhbOverview/queryMonthNum.do?startTime='+startTime+"&endTime="+endTime,
        contentType: 'application/json',
        success: function (resp) {
            if(!resp.success) {
                return;
            }
            let returnData = resp.data;
            if (returnData) {
                //对option进行赋值
                monthNumDataOption.legend.data = returnData.legendData;
                monthNumDataOption.xAxis[0].data = returnData.xAxisData;
                monthNumDataOption.series = returnData.series;
                monthNumDataBar.setOption(monthNumDataOption);
            }
        }
    });
}


var modelNumDataOption = {
    title: {
        show: false,
        text: '各发动机品牌产品数',
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
            let res = '<p>品牌：' + params[0].name + '</p>';
            for (let i = 0; i< params.length; i++) {
                res += '<p><span style="display:inline-block;margin-right:5px;border-radius:10px;width:9px;height:9px;background-color:' + params[i].color + '"></span>';
                res += params[i].seriesName + '：' + params[i].data;
            }
            return res;
        }
    },
    color: ['#0ac2db'],
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
                    var provideNumber = 7;
                    var rowNumber = Math.ceil(paramsNameNumber);
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
            name: '数\n量\n︵\n个\n︶',
            nameLocation: 'center',
            nameGap: 25,
            nameRotate: 0,
            position: 'left',
            minInterval:1
        }
    ],
    series: []
};

function queryModelNumData() {
    $.ajax({
        url: jsUseCtxPath + '/environment/core/ClhbOverview/queryNumByBrand.do?',
        contentType: 'application/json',
        success: function (resp) {
            if(!resp.success) {
                return;
            }
            let returnData = resp.data;
            if (returnData) {
                //对option进行赋值
                modelNumDataOption.legend.data = returnData.legendData;
                modelNumDataOption.xAxis[0].data = returnData.xAxisData;
                modelNumDataOption.series = returnData.series;
                modelNumDataBar.setOption(modelNumDataOption);
            }
        }
    });
}