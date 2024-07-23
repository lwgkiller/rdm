$(function () {
    queryCategoryChart();
});

function queryCategoryChart() {
    var systemCategoryId=mini.get("systemCategoryId").getValue();
    $.ajax({
        url: jsUseCtxPath + '/standardManager/report/standard/queryCategoryChart.do?systemCategoryId='+systemCategoryId,
        type: 'GET',
        contentType: 'application/json',
        success: function (returnData) {
            if (returnData) {
                dataValue.data=returnData.data;
                sumData.data=returnData.data;
                categoryOption.series=[];
                categoryOption.series.push(dataValue);
                categoryOption.series.push(sumData);
                categoryOption.xAxis[0].data=returnData.categoryNames;
                categoryChart.setOption(categoryOption);
            }
        }
    });
}

// 指定图表的配置项和数据
var categoryOption = {
    tooltip : {
        trigger: 'axis',
        axisPointer : {            // 坐标轴指示器，坐标轴触发有效
            type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
        },
        formatter:function (params) {
            var res='<p>'+params[0].name+'类别</p>';
            for(var i=0;i<params.length-1;i++){
                res+='<p><span style="display:inline-block;margin-right:5px;border-radius:10px;width:9px;height:9px;background-color:' + params[i].color + '"></span>';
                res+=mini.get("systemCategoryId").getText()+params[i].seriesName+'：'+params[i].data+'</p>';
            }
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
                interval:0
            }
        }
    ],
    yAxis : [
        {
            type : 'value',
            name:'数\n量\n︵\n个\n︶',
            nameLocation :'center',
            nameRotate:0
        }
    ],
    series : []
};

var dataValue={
    name:'数量',
    type:'bar',
    stack: '数量',
    barWidth: '60',
    data:[]
}

var sumData={
    name: '总计',
    type: 'bar',
    stack: '数量',
    barWidth: '60',
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
