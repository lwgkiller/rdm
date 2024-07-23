$(function () {
    //立项时间从去年1月1号到今年12月31号
    var nowDate=new Date();
    var nowYear=nowDate.getFullYear();
    var startTime=nowYear+"-01-01";
    var endTime=nowYear+"-12-31";
    mini.get("lbtjBuildFrom").setValue(startTime);
    mini.get("lbtjBuildTo").setValue(endTime);
    queryLbtj();
});

// 指定图表的配置项和数据
var lbtjOption = {
    tooltip : {
        trigger: 'axis',
        axisPointer : {            // 坐标轴指示器，坐标轴触发有效
            type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
        },
        formatter:function (params) {
            var res='<p>类别：'+params[0].name+'</p>';
            for(var i=params.length-2;i>=0;i--){
                res+='<p><span style="display:inline-block;margin-right:5px;border-radius:10px;width:9px;height:9px;background-color:' + params[i].color + '"></span>';
                res+=params[i].seriesName+'：'+params[i].data;
                var percent=0;
                if(params[i].data &&params[i].data!=0) {
                    percent=params[i].data*100/sumData.data[params[i].dataIndex];
                }
                percent=percent.toFixed(2);
                res+='（'+percent+'%）</p>';
            }
            res+='<p style="margin-left: 12px">'+params[params.length-1].seriesName+'：'+params[params.length-1].data+'</p>';
            return res;
        }
    },
    color:['#828282','#828282','#828282','#A4D3EE','#A4D3EE','#A4D3EE','#CD4F39','#CD4F39','#CD4F39'],
    legend: {
        data:[]
    },
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
            axisLabel: {
                show: true,
                interval:0,
                rotate:0,
                color: '#333',
                fontSize:10,
                formatter: function(params){
                        var newParamsName = "";
                        var paramsNameNumber = params.length;
                        var provideNumber = 4;
                        var rowNumber = Math.ceil(paramsNameNumber / provideNumber);
                        if (paramsNameNumber > provideNumber) {
                            for (var p = 0; p < rowNumber; p++) {
                                var tempStr = "";
                                var start = p * provideNumber;
                                var end = start + provideNumber;
                                if (p == rowNumber - 1) {
                                    tempStr = params.substring(start, paramsNameNumber);
                                } else {
                                    tempStr = params.substring(start, end) + "\n";
                                }
                                newParamsName += tempStr;
                            }

                        } else {
                            newParamsName = params;
                        }
                        return newParamsName
                    }
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

var sumData={
    name: '总计',
    type: 'bar',
    stack: '数量',
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

function queryLbtj() {
    var postData={};
    if(mini.get("lbtjBuildFrom").getValue()) {
        postData.buildTimeFrom=mini.get("lbtjBuildFrom").getValue();
    }
    if(mini.get("lbtjBuildTo").getValue()) {
        postData.buildTimeTo=mini.get("lbtjBuildTo").getValue();
    }
    // if(mini.get("lbtjKnotFrom").getValue()) {
    //     postData.knotTimeFrom=mini.get("lbtjKnotFrom").getValue();
    // }
    // if(mini.get("lbtjKnotTo").getValue()) {
    //     postData.knotTimeTo=mini.get("lbtjKnotTo").getValue();
    // }
    $.ajax({
        url: jsUseCtxPath + '/xcmgProjectManager/report/xcmgProject/queryLbtj.do',
        type: 'POST',
        data:mini.encode(postData),
        contentType: 'application/json',
        success: function (returnData) {
            if (returnData) {
                //对option进行赋值
                sumData.data=returnData.sumData;
                lbtjOption.legend.data=returnData.levelList;
                lbtjOption.xAxis[0].data=returnData.categoryList;
                lbtjOption.series=returnData.series;
                lbtjOption.series.push(sumData);
                lbtjChart.setOption(lbtjOption);
            }
        }
    });
}

function lbtjBuildFromChanged() {
    queryLbtj();
}
function lbtjBuildToChanged() {
    queryLbtj();
}
function lbtjKnotFromChanged() {
    queryLbtj();
}
function lbtjKnotToChanged() {
    queryLbtj();
}

