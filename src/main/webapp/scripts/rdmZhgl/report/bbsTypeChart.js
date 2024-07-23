$(function () {
    var currentDate = new Date();
    var year = currentDate.getFullYear();
    var month = currentDate.getMonth();
    var startTime = year+'-'+month+'-01';
    mini.get("bbsType_startTime").setValue(startTime);
    mini.get("bbsType_endTime").setValue(currentDate);
    initBbsTypeChart();
    // bbsTypeChart.setOption(carConsumeTimeChartOption);
});

// 指定图表的配置项和数据
bbsTypeChartOption = {
    title: {
        text: '帖子分类统计',
        left: 'center'
    },
    tooltip: {
        trigger: 'item'
    },
    legend: {
        orient: 'vertical',
        left: 'left',
    },
    series: [
        {
            name: '帖子个数',
            type: 'pie',
            radius: '50%',
            data: [
                {value: 1048, name: '改进提案'},
                {value: 735, name: '知识分享'},
                {value: 580, name: '公开讨论'},
            ],
            emphasis: {
                itemStyle: {
                    shadowBlur: 10,
                    shadowOffsetX: 0,
                    shadowColor: 'rgba(0, 0, 0, 0.5)'
                }
            }
        }
    ]
};

function initBbsTypeChart() {
    var bbs_startTime = mini.get('bbsType_startTime').getText();
    var bbs_endTime = mini.get('bbsType_endTime').getText();
    $.ajax({
        url: jsUseCtxPath + '/rdmZhgl/core/bbs/report/bbsTypeReport.do?bbs_startTime='+bbs_startTime+'&&bbs_endTime='+bbs_endTime,
        type: 'POST',
        contentType: 'application/json',
        success: function (returnData) {
            if (returnData.success) {
                //对option进行赋值
                var dataDetail = returnData.data;
                var seriesData = [];
                var gjta = {"name":"改进提案","value":dataDetail.gjta};
                var zsfx = {"name":"知识分享","value":dataDetail.zsfx};
                var gktl = {"name":"公开讨论","value":dataDetail.gktl};
                seriesData.push(gjta);
                seriesData.push(zsfx);
                seriesData.push(gktl);
                bbsTypeChartOption.series[0].data = seriesData;
                bbsTypeChart.setOption(bbsTypeChartOption);
            }else{
                mini.alert(returnData.message);
            }
        }
    });
}
