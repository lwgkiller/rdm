$(function () {
    centerBottomChartTwoInit();
});

function centerBottomChartTwoInit() {
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
    // mini.get("centerBottomChartTwoBuildFrom").setValue(startTime);
    mini.get("centerBottomChartTwoBuildTo").setValue(endTime);
    queryCenterBottomChartTwoChartData()
}

// 指定图表的配置项和数据
var centerBottomChartChartOption = {
    tooltip: {
        formatter: ''
    },
    series: [{
        name: "",
        type: "gauge",
        detail: {
            formatter: '{value}%',
        },
        data: [{
            value: 0,
            name: '完成率'
        }],
        axisLine: {
            show: true,
            lineStyle: {
                color: [
                    [0.2, '#c23531'],
                    [0.75, 'darkorange'],
                    [1, 'green'],
                ],
                width: 20
            }
        },
        splitLine: {
            show: true,
            length: 20
        }
    }],
};

function queryCenterBottomChartTwoChartData() {
    var postData = {};
    if (mini.get("centerBottomChartTwoBuildFrom").getValue()) {
        postData.startTime = mini.get("centerBottomChartTwoBuildFrom").getValue();
    }
    if (mini.get("centerBottomChartTwoBuildTo").getValue()) {
        postData.endTime = mini.get("centerBottomChartTwoBuildTo").getValue();
    }
    postData.list = ["XPSZ", "XPZDSY", "XPLS","WXBLX","LBJSY"];
    postData.czxpj = mini.get("czxpj").getValue();
    $.ajax({
        url: jsUseCtxPath + '/zlgj/core/report/percentage.do',
        type: 'POST',
        data: mini.encode(postData),
        contentType: 'application/json',
        success: function (resp) {
            if (resp.success) {
                //对option进行赋值
                centerBottomChartChartOption.series[0].data[0].value = resp.data;
                centerBottomChartTwoChart.setOption(centerBottomChartChartOption);
            }
        }
    });
}

function centerBottomChartTwoBuildFromChanged() {
    queryCenterBottomChartTwoChartData();
}

function centerBottomChartTwoBuildToChanged() {
    queryCenterBottomChartTwoChartData();
}

