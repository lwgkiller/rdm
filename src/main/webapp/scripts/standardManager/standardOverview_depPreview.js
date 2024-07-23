$(function () {
    var timeStr=initTime();
    mini.get("depPreviewTimeFrom").setValue(timeStr.startStr);
    mini.get("depPreviewTimeTo").setValue(timeStr.endStr);
    queryDepPreviewChart();
});

function queryDepPreviewChart() {
    var systemCategoryId=mini.get("systemCategoryId").getValue();
    var timeFrom=mini.get("depPreviewTimeFrom").getText();
    var timeTo=mini.get("depPreviewTimeTo").getText();
    $.ajax({
        url: jsUseCtxPath + '/standardManager/report/standard/queryDepCheckStandardChart.do?systemCategoryId='+systemCategoryId+"&timeFrom="+timeFrom+"&timeTo="+timeTo+"&checkCategoryId=preview",
        type: 'GET',
        contentType: 'application/json',
        success: function (returnData) {
            if (returnData) {
                var axisData = [];
                var seriesData = [];
                var data = returnData.data;
                for (i = data.length-1; i >=0; i--) {
                    axisData.add(data[i].depName);
                    seriesData.add(data[i].countNumber);
                }
                option =
                    {
                        tooltip: {
                            trigger: 'axis',
                            axisPointer: {
                                type: 'shadow'
                            },
                            formatter: function (params) {
                                var res='<p>'+params[0].name+'</p>';
                                res+='<p><span style="display:inline-block;margin-right:5px;border-radius:10px;width:9px;height:9px;background-color:' + params[0].color + '"></span>';
                                res+=mini.get("systemCategoryId").getText()+'累计预览：'+params[0].data+'次</p>';
                                return res;
                            }
                        },
                        color:['#66b1ff'],
                        grid: {
                            left: '3%',
                            right: '4%',
                            bottom: '3%',
                            containLabel: true,
                            top:'top',
                            left:'left'
                        },
                        xAxis: {
                            type: 'value',
                            name: '次数',
                            nameLocation: 'center',
                            nameGap: 20,
                            nameRotate: 0,
                            minInterval:1
                        },
                        yAxis: {
                            type: 'category',
                            axisLabel: {
                                show: true,
                                interval: 0
                            },
                            axisTick: {
                                alignWithLabel: true
                            },
                            data: axisData
                        },
                        series: [
                            {
                                type: 'bar',
                                barWidth: '60%',
                                data: seriesData
                            }
                        ]
                    };
                depPreviewChart.setOption(option);
            }
        }
    });
}

