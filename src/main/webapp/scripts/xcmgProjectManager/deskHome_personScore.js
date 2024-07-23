$(function () {
    mini.get("#ydjfMonth").setValue(lastMonth);
    loadChartOutydjf();
});

function loadChartOutydjf() {
    $.ajax({
        url: jsUseCtxPath + "/xcmgProjectManager/report/xcmgProject/deskHomePersonScore.do",
        data: {
            time: mini.get("#ydjfMonth").getText()
        },
        dataType: "json",
        success: function (text) {
            var returnJson = mini.decode(text);
            var timesData = returnJson.data.times;
            var scoreData = returnJson.data.scores;
            if (returnJson.success) {
                option = {
                    tooltip : {
                        trigger: 'axis',
                        formatter:function(value){
                            return '积分：'+value[0].data
                        }
                       },
                    xAxis: {
                        type: 'category',
                        data: timesData.reverse()
                    },
                    yAxis: {
                        type: 'value'
                    },
                    series: [{
                        data: scoreData.reverse(),
                        type: 'line'
                    }]
                };
                ydjfChart.setOption(option);
            } else {
                mini.alert(returnJson.msg, "系统提示");
            }
        }
    });
}

function changeTime() {
    loadChartOutydjf();
}