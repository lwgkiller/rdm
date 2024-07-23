$(function () {
    typeDeptInit();
});

function typeDeptInit() {
    //今年1月1号到今年12月31号
    let nowDate = new Date();
    let nowYear = nowDate.getFullYear();
    let month = nowDate.getMonth() + 1;
    let day = nowDate.getDate();
    let startMonth = month - 4;
    if (startMonth < 0) {
        startMonth = 1;
    }
    let startTime = (nowYear) + "-" + startMonth + "-01";
    let endTime = nowYear + "-" + month + "-" + day;
    mini.get("typeDeptFrom1").setValue(startTime);
    mini.get("typeDeptTo1").setValue(endTime);
    mini.get("yzcd").setValue("A");
    querytypeDeptChartData()
}

// 指定图表的配置项和数据
var typeDeptChartOption = {
    tooltip: {
        trigger: 'axis',
        axisPointer: {            // 坐标轴指示器，坐标轴触发有效
            type: 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
        },
        formatter: function (params) {
            var res = '<p>部门：' + params[0].name + '</p>';
            if (params.length == 3) {
                var sum = params[2].data;
                for (var i = 0; i < params.length - 1; i++) {
                    res += '<p><span style="display:inline-block;margin-right:5px;border-radius:10px;width:9px;height:9px;background-color:' + params[i].color + '"></span>';
                    res += params[i].seriesName + '：' + params[i].data;
                    var percent = 0;
                    if (params[i].data && params[i].data != 0&&sum) {
                        percent = params[i].data * 100 / sum;
                    }
                    percent = percent.toFixed(2);
                    res += '（' + percent + '%）</p>';
                }
            } else if (params.length == 6) {
                var suma = 0;
                var sumb = 0;
                var sumc = 0;
                if(params[4].seriesName.indexOf("A")>-1){
                    suma = params[4].data;
                }else if(params[4].seriesName.indexOf("B")>-1){
                    sumb = params[4].data;
                }else if(params[4].seriesName.indexOf("C")>-1){
                    sumc = params[4].data;
                }

                if(params[5].seriesName.indexOf("A")>-1){
                    suma = params[5].data;
                }else if(params[5].seriesName.indexOf("B")>-1){
                    sumb = params[5].data;
                }else if(params[5].seriesName.indexOf("C")>-1){
                    sumc = params[5].data;
                }
                for (var i = 0; i < params.length - 2; i++) {
                    res += '<p><span style="display:inline-block;margin-right:5px;border-radius:10px;width:9px;height:9px;background-color:' + params[i].color + '"></span>';
                    res += params[i].seriesName + '：' + params[i].data;
                    var percent = 0;
                    if (params[i].data && params[i].data != 0&&suma&&params[i].seriesName.indexOf("A")>-1) {
                        percent = params[i].data * 100 / suma;
                    }
                    if (params[i].data && params[i].data != 0&&sumc&&params[i].seriesName.indexOf("C")>-1) {
                        percent = params[i].data * 100 / sumc;
                    }
                    if (params[i].data && params[i].data != 0&&sumb&&params[i].seriesName.indexOf("B")>-1) {
                        percent = params[i].data * 100 / sumb;
                    }
                    percent = percent.toFixed(2);
                    res += '（' + percent + '%）</p>';
                }
            } else if (params.length == 9) {
                var suma = 0;
                var sumb = 0;
                var sumc = 0;
                if(params[6].seriesName.indexOf("A")>-1){
                    suma = params[6].data;
                }else if(params[6].seriesName.indexOf("B")>-1){
                    sumb = params[6].data;
                }else if(params[6].seriesName.indexOf("C")>-1){
                    sumc = params[6].data;
                }
                if(params[7].seriesName.indexOf("A")>-1){
                    suma = params[7].data;
                }else if(params[7].seriesName.indexOf("B">-1)){
                    sumb = params[7].data;
                }else if(params[7].seriesName.indexOf("C")>-1){
                    sumc = params[7].data;
                }
                if(params[8].seriesName.indexOf("A")>-1){
                    suma = params[8].data;
                }else if(params[8].seriesName.indexOf("B")>-1){
                    sumb = params[8].data;
                }else if(params[8].seriesName.indexOf("C")>-1){
                    sumc = params[8].data;
                }
                for (var i = 0; i < params.length - 3; i++) {
                    res += '<p><span style="display:inline-block;margin-right:5px;border-radius:10px;width:9px;height:9px;background-color:' + params[i].color + '"></span>';
                    res += params[i].seriesName + '：' + params[i].data;
                    var percent = 0;
                    if (params[i].data && params[i].data != 0&&suma&&params[i].seriesName.indexOf("A")>-1) {
                        percent = params[i].data * 100 / suma;
                    }
                    if (params[i].data && params[i].data != 0&&sumc&&params[i].seriesName.indexOf("C")>-1) {
                        percent = params[i].data * 100 / sumc;
                    }
                    if (params[i].data && params[i].data != 0&&sumb&&params[i].seriesName.indexOf("B")>-1) {
                        percent = params[i].data * 100 / sumb;
                    }
                    percent = percent.toFixed(2);
                    res += '（' + percent + '%）</p>';
                }
            }
            return res;
        }
    },
    color: ['#1d8404', 'darkorange','#1d8404', 'darkorange','#1d8404', 'darkorange'],
    legend: {
        data: []
    },
    grid: {
        left: '3.5%',
        right: '2%',
        top: '8%',
        bottom: '10%',
        containLabel: true
    },
    xAxis: [
        {
            type: 'category',
            data: [],
            axisLabel: {
                show: true,
                margin: 20,
                interval: 0,
                rotate: 0,
                color: '#333',
                fontSize: 10,
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
            name: '数\n量\n︵\n个\n︶',
            nameLocation: 'center',
            nameGap: 25,
            nameRotate: 0,
            //负数取绝对值显示为正数
            axisLabel: {
                formatter(value) {
                    return Math.abs(value);
                }
            }
        }
    ],
    series: []
};

var typeDeptSumDataC = {
    name: 'C总计',
    type: 'bar',
    stack: 'C数量',
    label: {
        normal: {
            offset: ['50', '80'],
            show: true,
            position: 'insideBottom',
            formatter: '{c}',
            textStyle: {color: '#000'}
        }
    },
    itemStyle: {
        normal: {
            color: 'rgba(128, 128, 128, 0)'
        }
    },
    data: []
};
var typeDeptSumDataB = {
    name: 'B总计',
    type: 'bar',
    stack: 'B数量',
    label: {
        normal: {
            offset: ['50', '80'],
            show: true,
            position: 'insideBottom',
            formatter: '{c}',
            textStyle: {color: '#000'}
        }
    },
    itemStyle: {
        normal: {
            color: 'rgba(128, 128, 128, 0)'
        }
    },
    data: []
};
var typeDeptSumDataA = {
    name: 'A总计',
    type: 'bar',
    stack: 'A数量',
    label: {
        normal: {
            offset: ['50', '80'],
            show: true,
            position: 'insideBottom',
            formatter: '{c}',
            textStyle: {color: '#000'}
        }
    },
    itemStyle: {
        normal: {
            color: 'rgba(128, 128, 128, 0)'
        }
    },
    data: []
};


function querytypeDeptChartData() {
    var postData = {};
    if (mini.get("typeDeptFrom1").getText()) {
        postData.startTime = mini.get("typeDeptFrom1").getText();
    }
    if (mini.get("typeDeptTo1").getText()) {
        postData.endTime = mini.get("typeDeptTo1").getText();
    }
    if (mini.get("wtlx1").getValue()) {
        postData.wtlx = mini.get("wtlx1").getValue();
    }
    if (mini.get("yzcd").getValue()) {
        postData.yzcd = mini.get("yzcd").getValue();
    }
    postData.czxpj = mini.get("czxpj").getValue();
    $.ajax({
        url: jsUseCtxPath + '/zlgj/core/report/typeDataDept.do',
        type: 'POST',
        data: mini.encode(postData),
        contentType: 'application/json',
        success: function (resp) {
            let returnData = resp.data;
            if (returnData) {
                //对option进行赋值
                typeDeptChartOption.xAxis[0].data = returnData.xAxisData;
                typeDeptChartOption.series = returnData.series;
                typeDeptChartOption.legend.data = returnData.legendData;
                if (returnData.sumDataA) {
                    typeDeptSumDataA.data = returnData.sumDataA;
                    typeDeptChartOption.series.push(typeDeptSumDataA);
                }
                if (returnData.sumDataB) {
                    typeDeptSumDataB.data = returnData.sumDataB;
                    typeDeptChartOption.series.push(typeDeptSumDataB);
                }
                if (returnData.sumDataC) {
                    typeDeptSumDataC.data = returnData.sumDataC;
                    typeDeptChartOption.series.push(typeDeptSumDataC);
                }
                typeDataDept.clear();
                typeDataDept.setOption(typeDeptChartOption);
            }
        }
    });
}

function typeDeptBuildFromChanged() {
    querytypeDeptChartData();
}

function typeDeptBuildToChanged() {
    querytypeDeptChartData();
}

function typelxChanged() {
    querytypeDeptChartData();
}

