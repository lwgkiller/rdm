$(function () {
    centerBottomChartOneInit();
});

function centerBottomChartOneInit() {
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
    // mini.get("centerBottomChartOneBuildFrom").setValue(startTime);
    mini.get("centerBottomChartOneBuildTo").setValue(endTime);
    queryCenterBottomChartOneChartData()
}

// 指定图表的配置项和数据
var centerBottomChartOneOption = {
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

function queryCenterBottomChartOneChartData() {
    var postData = {};
    if (mini.get("centerBottomChartOneBuildFrom").getValue()) {
        postData.startTime = mini.get("centerBottomChartOneBuildFrom").getValue();
    }
    if (mini.get("centerBottomChartOneBuildTo").getValue()) {
        postData.endTime = mini.get("centerBottomChartOneBuildTo").getValue();
    }
    postData.list = ["CNWT", "SCWT", "HWWT"];
    // postData.list = ["XPSZ","XPZDSY","XPLS"];
    postData.czxpj = mini.get("czxpj").getValue();
    $.ajax({
        url: jsUseCtxPath + '/zlgj/core/report/percentage.do',
        type: 'POST',
        data: mini.encode(postData),
        contentType: 'application/json',
        success: function (resp) {
            if (resp.success) {
                //对option进行赋值
                centerBottomChartOneOption.series[0].data[0].value = resp.data;
                centerBottomChartOneChart.setOption(centerBottomChartOneOption);
            }
        }
    });
}

function centerBottomChartOneBuildFromChanged() {
    queryCenterBottomChartOneChartData();
}

function centerBottomChartOneBuildToChanged() {
    queryCenterBottomChartOneChartData();
}

