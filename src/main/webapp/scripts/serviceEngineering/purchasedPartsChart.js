$(function () {
    queryWgjzlsjChart();
});

//收集率
sjlChartOption = {
    title : {
        text: '收集率',
        x:'center'
    },
    tooltip : {
        trigger: 'item',
        formatter: function (param) {
            var res = '已收集：' + param.data.ysjNum + '<br/>未收集：' +
                      param.data.wsjNum + '<br/>已收集率：' + param.data.ysjl + '%'
            return res
        }
    },
    legend: {
        orient: 'vertical',
        left: 'left',
        data: ['已收集','未收集']
    },
    series : [
        {
            name: '收集率',
            type: 'pie',
            radius : '55%',
            center: ['50%', '60%'],
            data:[],
            itemStyle: {
                emphasis: {
                    shadowBlur: 10,
                    shadowOffsetX: 0,
                    shadowColor: 'rgba(0, 0, 0, 0.5)'
                }
            }
        }
    ],
    color: ['rgb(29, 132, 4)','rgb(202, 134, 34)']
};
// 制作率
zzlChartOption = {
    title : {
        text: '制作率',
        x:'center'
    },
    tooltip : {
        trigger: 'item',
        formatter: function (param) {
            var res = '已制作：' + param.data.yzzNum + '<br/>未制作：' +
                param.data.wzzNum + '<br/>已制作率：' + param.data.yzzl + '%'
            return res
        }
    },
    legend: {
        orient: 'vertical',
        left: 'left',
        data: ['已制作','未制作']
    },
    series : [
        {
            name: '制作率',
            type: 'pie',
            radius : '55%',
            center: ['50%', '60%'],
            data:[],
            itemStyle: {
                emphasis: {
                    shadowBlur: 10,
                    shadowOffsetX: 0,
                    shadowColor: 'rgba(0, 0, 0, 0.5)'
                }
            }
        }
    ],
    color: ['rgb(29, 132, 4)','rgb(202, 134, 34)']
};

function queryWgjzlsjChart () {
    $.ajax({
        url: jsUseCtxPath + '/serviceEngineering/core/wgjzlsj/queryWgjzlsjChart.do',
        type: 'POST',
        contentType: 'application/json',
        success: function (returnData) {
            var sjlData = [
                {name: '已收集',value: 0, ysjNum:0, wsjNum:0, ysjl: 0},
                {name: '未收集',value: 0, ysjNum:0, wsjNum:0, ysjl: 0}
            ];
            var zzlData = [
                {name: '已制作',value: 0, yzzNum:0, wzzNum:0, yzzl: 0},
                {name: '未制作',value: 0, yzzNum:0, wzzNum:0, yzzl: 0}
            ];
            if (returnData) {
                sjlData=[];
                zzlData=[];
                sjlData.push({name: '已收集',value: returnData.ysjNum, ysjNum: returnData.ysjNum, wsjNum: returnData.wsjNum, ysjl: returnData.ysjl});
                sjlData.push({name: '未收集',value: returnData.wsjNum,ysjNum: returnData.ysjNum, wsjNum: returnData.wsjNum, ysjl: returnData.ysjl});
                zzlData.push({name: '已制作', value: returnData.yzzNum, yzzNum: returnData.yzzNum, wzzNum: returnData.wzzNum, yzzl: returnData.yzzl});
                zzlData.push({name: '未制作',value: returnData.wzzNum, yzzNum: returnData.yzzNum, wzzNum: returnData.wzzNum, yzzl: returnData.yzzl});
            }
            //对option进行赋值
            sjlChartOption.series[0].data=sjlData;
            zzlChartOption.series[0].data=zzlData;
            sjlChart.setOption(sjlChartOption);
            zzlChart.setOption(zzlChartOption);

        }
    });
}