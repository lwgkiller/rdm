$(function () {
    setPublishChartTime();
    queryPublishChart();
});

function queryPublishChart() {
    var timeFrom=mini.get("publishTimeFrom").getValue();
    var timeTo=mini.get("publishTimeTo").getValue();
    var systemCategoryId=mini.get("systemCategoryId").getValue();
    $.ajax({
        url: jsUseCtxPath + '/standardManager/report/standard/queryPublishChart.do?publishTimeFrom='+timeFrom+'&publishTimeTo='+timeTo+"&systemCategoryId="+systemCategoryId,
        type: 'GET',
        contentType: 'application/json',
        success: function (returnData) {
            if (returnData) {
                dataValue.data=returnData.data;
                sumData.data=returnData.data;
                publishOption.series=[];
                publishOption.series.push(dataValue);
                publishOption.series.push(sumData);
                publishOption.xAxis[0].data=returnData.years;
                publishTimeChart.setOption(publishOption);
            }
        }
    });
}

function setPublishChartTime() {
    var publishTimeFromData=generateYearSelect("from");
    var publishTimeToData=generateYearSelect("to");
    mini.get("publishTimeFrom").load(publishTimeFromData);
    mini.get("publishTimeTo").load(publishTimeToData);
    var nowY=new Date().getFullYear();
    var initFromY=nowY-4;
    mini.get("publishTimeFrom").setValue(initFromY+'-01-01 00:00:00');
    mini.get("publishTimeTo").setValue(nowY+'-12-31 24:00:00');
}

function generateYearSelect(fromOrTo) {
    var data=[];
    var nowDate=new Date();
    var startY=nowDate.getFullYear()-10;
    var endY=nowDate.getFullYear()+30;
    for(var i=startY;i<=endY;i++) {
        var oneData={};
        oneData.key=i+'年';
        if(fromOrTo=='from') {
            oneData.value=i+'-01-01 00:00:00';
        } else if(fromOrTo=='to') {
            oneData.value=i+'-12-31 24:00:00';
        }
        data.push(oneData);
    }
    return data;
}


// 指定图表的配置项和数据
var publishOption = {
    tooltip : {
        trigger: 'axis',
        axisPointer : {            // 坐标轴指示器，坐标轴触发有效
            type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
        },
        formatter:function (params) {
            var res='<p>'+params[0].name+'年</p>';
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
