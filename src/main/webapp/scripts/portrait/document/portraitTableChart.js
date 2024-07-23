$(function () {
    initTableChart();
});
// 指定图表的配置项和数据
var tableOption = {
    tooltip: {
        trigger: 'item',
        triggerOn: 'mousemove'
    },
    series: [
        {
            type: 'tree',
            id: 0,
            name: 'tree1',
            edgeShape: 'polyline',
            edgeForkPosition: '83%',
            initialTreeDepth: 3,
            data: [],

            top: '10%',
            left: '8%',
            bottom: '22%',
            right: '20%',

            symbolSize: 7,



            lineStyle: {
                width: 2
            },

            label: {
                backgroundColor: '#fff',
                position: 'left',
                verticalAlign: 'middle',
                align: 'right'
            },

            leaves: {
                label: {
                    position: 'right',
                    verticalAlign: 'middle',
                    align: 'left'
                }
            },

            expandAndCollapse: true,
            animationDuration: 550,
            animationDurationUpdate: 750
        }
    ]
};

function initTableChart() {
    //近5年数据分类统计
    var currentYear = new Date().getFullYear();
    var returnData = getScoreData(currentYear);
    var data = [
        {
            "name": "总积分",
            "children": [
                {
                    "name": "技术创新",
                    "children": [
                        {
                            "name": "标准",
                            "children": [{"name": "得分："+returnData.standardScore}]
                        },
                        {
                            "name": "知识产权",
                            "children": [{"name": "得分："+returnData.knowledgeScore}]
                        },
                        {
                            "name": "科技项目",
                            "children": [{"name": "得分："+returnData.projectScore}]
                        },
                        {
                            "name": "荣誉奖项",
                            "children": [{"name": "得分："+returnData.rewardScore}]
                        },
                        {
                            "name": "专有技术",
                            "children": [{"name": "得分："+returnData.secretScore}]
                        },
                        {
                            "name": "技术数据库",
                            "children": [{"name": "得分："+returnData.technologyScore}]
                        },
                    ]
                },
                {
                    "name": "技术协同",
                    "children": [
                        {
                            "name": "RDM论坛",
                            "children": [{"name": "得分："+returnData.bbsScore}]
                        },
                        {
                            "name": "专利解读",
                            "children": [{"name": "得分："+returnData.patentReadScore}]
                        },
                        {
                            "name": "情报报告",
                            "children": [{"name": "得分："+returnData.informationScore}]
                        },
                        {
                            "name": "分析改进",
                            "children": [{"name": "得分："+returnData.analysisImproveScore}]
                        },
                        {
                            "name": "合同管理",
                            "children": [{"name": "得分："+returnData.contractScore}]
                        }
                    ]
                },
                {
                    "name": "敬业表现",
                    "children": [
                        {
                            "name": "考勤",
                            "children": [{"name": "得分："+returnData.attendanceScore}]
                        },
                        {
                            "name": "通报",
                            "children": [{"name": "得分："+returnData.notificationScore}]
                        },
                        {
                            "name": "通报",
                            "children": [{"name": "得分："+returnData.performanceScore}]
                        }

                    ]
                },
                {
                    "name": "职业发展",
                    "children": [
                        {
                            "name": "培训",
                            "children": [{"name": "得分："+returnData.courseScore}]
                        },
                        {
                            "name": "培养",
                            "children": [{"name": "得分："+returnData.cultureScore}]
                        }
                    ]
                }
            ]
        }
    ]
    tableOption.series[0].data = data;
    tableOption.series[0].edgeShape = 'polyline';
    tableChart.setOption(tableOption);
}

function getScoreData(_reportYear) {
    var resultDate = {};
    $.ajax({
        url: jsUseCtxPath + '/portrait/document/personScore.do?userId=' + userId + '&&reportYear=' + _reportYear,
        type: 'POST',
        async: false,
        contentType: 'application/json',
        success: function (returnData) {
            if (returnData.success) {
                //对option进行赋值
                resultDate = returnData.data;
            } else {
                mini.alert(returnData.message);
            }
        }
    });
    return resultDate;
}

