$(function () {
    //立项时间从去年1月1号到今年12月31号
    var nowDate=new Date();
    var nowYear=nowDate.getFullYear();
    var startTime=(nowYear-1)+"-01-01";
    var endTime=nowYear+"-12-31";
    mini.get("cgjhFrom").setValue(startTime);
    mini.get("cgjhTo").setValue(endTime);
    queryCgjh();
});

function compare(propName) {
    return function (a,b) {
        var aValue=a[propName];
        var bValue=b[propName];
        return bValue-aValue;
    }
}

// 指定图表的配置项和数据
var cgjhOption = {
    tooltip : {
        trigger: 'axis',
        axisPointer : {            // 坐标轴指示器，坐标轴触发有效
            type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
        },
        formatter:function (params) {
            var res='<p>类别：'+params[0].name+'</p>';
            var dataArr=[];
            for(var i=0;i<params.length;i++){
                if(params[i].data &&params[i].data!=0) {
                    dataArr.push({seriesName:params[i].seriesName,data:params[i].data,color:params[i].color});
                }
            }
            dataArr.sort(compare('data'));
            for(var i=0;i<dataArr.length;i++){
                res+='<p><span style="display:inline-block;margin-right:5px;border-radius:10px;width:9px;height:9px;background-color:' + dataArr[i].color + '"></span>';
                res+=dataArr[i].seriesName+'：'+dataArr[i].data;
            }
            return res;
        }
    },
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
                        var provideNumber = 10;
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
                        return newParamsName;
                    }
            }
        }
    ],
    yAxis : [
        {
            type : 'value',
            name:'数\n量\n︵\n个\n︶',
            nameLocation :'middle',
            nameGap:40,
            nameRotate:0
        }
    ],
    series : []
};

function queryCgjh() {
    var postData={};
    if(mini.get("cgjhFrom").getValue()) {
        postData.cgjhFrom=mini.get("cgjhFrom").getValue();
    }
    if(mini.get("cgjhTo").getValue()) {
        postData.cgjhTo=mini.get("cgjhTo").getValue();
    }
    $.ajax({
        url: jsUseCtxPath + '/xcmgProjectManager/report/xcmgProject/queryCgjh.do',
        type: 'POST',
        data:mini.encode(postData),
        contentType: 'application/json',
        success: function (returnData) {
            if (returnData) {
                //对option进行赋值
                cgjhOption.legend.data=returnData.deptNames;
                cgjhOption.xAxis[0].data=returnData.achievementTypes;
                cgjhOption.series=returnData.series;
                cgjhChart.setOption(cgjhOption);
            }
        }
    });
}

function cgjhTimeChange() {
    queryCgjh();
}

