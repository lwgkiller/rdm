$(function () {
    //立项时间从去年1月1号到今年12月31号
    var nowDate=new Date();
    var nowYear=nowDate.getFullYear();
    var startTime=nowYear+"-01-01";
    var endTime=nowYear+"-12-31";
    mini.get("jfzbBuildFrom").setValue(startTime);
    mini.get("jfzbBuildTo").setValue(endTime);
    loadChartOutjfzb();
});

var queryData;
var jfzbOption = {
    tooltip : {
        trigger: 'item',
        formatter:  function (params) {
            var result= '<div style="width: 140px;height: 140px;">' +
                '<p style="width: 100%; text-align:center;background-color: #333">'+params.name+'<br>'+params.value+'分('+params.percent+'%)</p>' +
                '<span class="jfzb-jb">级别</span>' +
                '<span class="jfzb-fs">分数</span>' +
                '<span class="jfzb-zb">占比</span>' +
                '<br><hr style="height: 0px">';
            var detailInfo=queryData[params.dataIndex].jfzbStaticsList;
            for(var index=0;index<detailInfo.length;index++) {
                var levelName=detailInfo[index].levelname;
                var score=detailInfo[index].levelsum;
                var percent=score*100/params.value;
                percent=percent.toFixed(2);

                result+='<p class="jfzb-jb">'+levelName+'</p>';
                result+='<p class="jfzb-fs">'+score+'</p>';
                result+='<p class="jfzb-zb">'+percent+'%</p>';
            }
            result+='</div>';
            return result;
        },
        position: [],
        backgroundColor: '#eee',
        borderColor: '#777',
        borderWidth: 1
    },
    legend: {
        orient: 'vertical',
        x: 'left'
    },
    series: [
        {
            type: 'pie',
            radius: ['65%', '85%'],
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
                    show: false
                }
            },
            labelLine: {
                normal: {
                    show: false
                }
            }
        }
    ]
};


function loadChartOutjfzb() {
    var postData={};
    if(mini.get("jfzbBuildFrom").getValue()) {
        postData.buildTimeFrom=mini.get("jfzbBuildFrom").getValue();
    }
    if(mini.get("jfzbBuildTo").getValue()) {
        postData.buildTimeTo=mini.get("jfzbBuildTo").getValue();
    }
    $.ajax({
        url: jsUseCtxPath + "/xcmgProjectManager/report/xcmgProject/queryJfzbDepsumscore.do",
        type: 'POST',
        data:mini.encode(postData),
        contentType: 'application/json',
        success: function (text) {
            var returnJson = mini.decode(text);
            var seriesdata = [];
            if (returnJson.success) {
                queryData=returnJson.data;
                for(var i=0;i<queryData.length;i++) {
                    var onePie={};
                    onePie.name=queryData[i].depname;
                    onePie.value=queryData[i].depsumscore;
                    /*var detailInfo=[
                        '{title|{b}}{abg|}',
                        '  {weatherHead|级别}{valueHead|分数}{rateHead|占比}',
                        '{hr|}'
                    ];
                    for(var level in queryData[i].levelName2Statics) {
                        var levelObj=queryData[i].levelName2Statics[level];
                        var percent=levelObj.levelsum*100/queryData[i].depsumscore;
                        percent=percent.toFixed(2);
                        var oneDetail='  {rowStyle|'+levelObj.levelname+'}{value|'+levelObj.levelsum+'}{rate|'+percent+'%}';
                        detailInfo.push(oneDetail);
                    }
                    var label={
                        normal: {
                                position:'center',
                                formatter: '',
                                backgroundColor: '#eee',
                                borderColor: '#777',
                                borderWidth: 1,
                                borderRadius: 4,
                                rich: {
                                title: {
                                    color: '#eee',
                                    align: 'center'
                                },
                                abg: {
                                    groundColor: '#333',
                                    width: '100%',
                                    align: 'right',
                                    height: 25,
                                    borderRadius: [4, 4, 0, 0]
                                },
                                rowStyle: {
                                    height: 30,
                                    align: 'left'
                                },
                                weatherHead: {
                                    color: '#333',
                                    height: 24,
                                    align: 'left'
                                },
                                hr: {
                                    borderColor: '#777',
                                    width: '100%',
                                    borderWidth: 0.5,
                                    height: 0
                                },
                                value: {
                                    width: 20,
                                    padding: [0, 20, 0, 30],
                                    align: 'center'
                                },
                                valueHead: {
                                    color: '#333',
                                    width: 20,
                                    padding: [0, 20, 0, 30],
                                    align: 'center'
                                },
                                rate: {
                                    width: 40,
                                    align: 'right',
                                    padding: [0, 10, 0, 0]
                                },
                                rateHead: {
                                    color: '#333',
                                    width: 40,
                                    align: 'center',
                                    padding: [0, 10, 0, 0]
                                }
                            }
                        }
                    };
                    label.normal.formatter=detailInfo.join('\n');
                    label.normal.show=false;
                    onePie.labelLine={show:false};
                    onePie.label=label;*/

                    seriesdata.push(onePie);
                }
                jfzbOption.series[0].data=seriesdata;
                var x=$("#jfzb").width()/2-72;
                var y=$("#jfzb").height()/2-75;
                jfzbOption.tooltip.position=[x,y];
                jfzbChart.setOption(jfzbOption);
            } else {
                mini.alert(returnJson.msg, "系统提示");
            }
        }
    });
}

function jfzbBuildFromChanged() {
    loadChartOutjfzb();
}
function jfzbBuildToChanged() {
    loadChartOutjfzb();
}