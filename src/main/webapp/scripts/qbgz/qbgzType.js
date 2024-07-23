$(function () {
    var timeStr=initTime();
    mini.get("previewTimeFrom2").setValue(timeStr.startStr);
    mini.get("previewTimeTo2").setValue(timeStr.endStr);
    queryTypeChart();
});
var typeoption =
    {
        tooltip: {
            trigger: 'item',
            formatter: function (params) {
                var res=params.name+':'+params.value+'次</p>';
                return res;
            },
            position: [],
            backgroundColor: '#eee',
            borderColor: '#777',
            borderWidth: 1
        },
        legend: {
            orient: 'vertical',
            x: 'left',
            data: ['三一','卡特','小松','柳工','临工','其他']
        },
        series: [
            {
                type: 'pie',
                radius:'65%',
                center: ['50%', '50%'],
                selectedMode: 'single',
                data: [],
                itemStyle: {
                    emphasis: {
                        shadowBlur: 10,
                        shadowOffsetX: 0,
                        shadowColor: 'rgba(0, 0, 0, 0.5)'
                    }
                },
                label:{
                    normal: {
                        show: true,
                        formatter: function (params) {
                            return params.name + " : " + +params.value + "次"
                        }
                    }
                },
                labelLine: {
                    normal: {
                        show: true
                    }
                }
            }
        ]
    };
function queryTypeChart() {
    var timeFrom=mini.get("previewTimeFrom2").getText();
    var timeTo=mini.get("previewTimeTo2").getText();
    $.ajax({
        url: jsUseCtxPath + '/Info/Qbgz/queryType.do?timeFrom='+timeFrom+"&timeTo="+timeTo,
        type: 'GET',
        contentType: 'application/json',
        success: function (returnData) {
            var returnJson = mini.decode(returnData);
            var seriesdata = [];
                queryData=returnJson.data;
                for(var i=0;i<queryData.length;i++) {
                    var onePie = {};
                    onePie.name = queryData[i].companyName;
                    onePie.value = queryData[i].countNumber;
                    seriesdata.push(onePie);
                }
                typeoption.series[0].data=seriesdata;
                typeChart.setOption(typeoption);
        }
    });
}

