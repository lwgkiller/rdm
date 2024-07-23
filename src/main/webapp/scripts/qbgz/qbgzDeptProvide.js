$(function () {
    var timeStr=initTime();
    mini.get("previewTimeFrom").setValue(timeStr.startStr);
    mini.get("previewTimeTo").setValue(timeStr.endStr);
    queryPreviewChart();
});

function queryPreviewChart() {
    var timeFrom=mini.get("previewTimeFrom").getText();
    var timeTo=mini.get("previewTimeTo").getText();
    $.ajax({
        url: jsUseCtxPath + '/Info/Qbgz/queryProvideChart.do?timeFrom='+timeFrom+"&timeTo="+timeTo,
        type: 'GET',
        contentType: 'application/json',
        success: function (returnData) {
            if (returnData) {
                dataValue.data=returnData.data;
                sumData.data=returnData.data;
                deptProvideOptin.series=[];
                deptProvideOptin.series.push(dataValue);
                deptProvideOptin.series.push(sumData);
                deptProvideOptin.xAxis[0].data=returnData.depts;
                deptProvideChart.setOption(deptProvideOptin);
            }
        }
    });
}
var deptProvideOptin = {
    tooltip : {
        trigger: 'axis',
        axisPointer : {            // 坐标轴指示器，坐标轴触发有效
            type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
        },
        formatter: function (params)
        {
            var
            res=params[0].name+'累计提供：'+params[0].data+'份</p>';
            return res;
        }
    },
    color:['#66b1ff'],
    grid: {
        left: '3.5%',
        right: '2%',
        top:'8%',
        bottom: '2%',
        containLabel: true
    },
    xAxis : [
        {
            type : 'category',
            data : [],
            axisTick: {
                alignWithLabel: true
            },
            axisLabel:{
                interval:0,
                rotate: 40
            }
        }
    ],
    yAxis : [
        {
            type : 'value',
            name:'数\n量\n︵\n个\n︶',
            nameLocation :'center',
            nameGap:25,
            nameRotate:0
        }
    ],
    series : []
};

var dataValue={
    name:'数量',
    type:'bar',
    stack: '数量',
    barWidth: 30,
    data:[]
}

var sumData={
    name: '总计',
    type: 'bar',
    stack: '数量',
    barWidth: 30,
    label: {
        normal: {
            offset:['50', '80'],
            show: true,
            position: 'insideBottom',
            formatter:'{c}',
            textStyle:{ color:'#000' }
        }
    },
    itemStyle:{
        normal:{
            color:'rgba(128, 128, 128, 0)'
        }
    },
    data: []
};