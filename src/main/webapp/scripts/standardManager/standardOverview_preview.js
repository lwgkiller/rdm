$(function () {
    var timeStr=initTime();
    mini.get("previewTimeFrom").setValue(timeStr.startStr);
    mini.get("previewTimeTo").setValue(timeStr.endStr);
    queryPreviewChart();
});

function queryPreviewChart() {
    var systemCategoryId=mini.get("systemCategoryId").getValue();
    var timeFrom=mini.get("previewTimeFrom").getText();
    var timeTo=mini.get("previewTimeTo").getText();
    $.ajax({
        url: jsUseCtxPath + '/standardManager/report/standard/queryStandardCheckChart.do?systemCategoryId='+systemCategoryId+"&timeFrom="+timeFrom+"&timeTo="+timeTo+"&checkCategoryId=preview",
        type: 'GET',
        contentType: 'application/json',
        success: function (returnData) {
            if (returnData) {
                var axisData = [];
                var seriesData = [];
                var standardNumber=[];
                var data = returnData.data;
                for (i = data.length-1; i >=0; i--) {
                    axisData.add(data[i].standardName);
                    seriesData.add(data[i].countNumber);
                    standardNumber.add(data[i].standardNumber);
                }
                option =
                    {
                        tooltip: {
                            trigger: 'axis',
                            axisPointer: {
                                type: 'shadow'
                            },
                            formatter: function (params) {
                                var res='<p>'+mini.get("systemCategoryId").getText()+':'+params[0].name+'('+standardNumber[params[0].dataIndex]+')</p>';
                                res+='<p><span style="display:inline-block;margin-right:5px;border-radius:10px;width:9px;height:9px;background-color:' + params[0].color + '"></span>';
                                res+='累计预览：'+params[0].data+'次</p>';
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
                standardPreviewChart.setOption(option);
            }
        }
    });
}

