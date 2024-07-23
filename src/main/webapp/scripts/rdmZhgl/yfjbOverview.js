function toFixNum(e) {
    if(e.value){
        return parseFloat(e.value).toFixed(2);
    }
}
function onProcessStatus(e) {
    var processStatus = e.record.processStatus;
    if(processStatus=='tq'){
        e.rowStyle = 'background-color:green';
    }else if(processStatus=='yh'){
        e.rowStyle = 'background-color:red';
    }
}
function onJbfs(e) {
    var record = e.record;
    var resultValue = record.costType;
    var resultText = '';
    for (var i = 0; i < jbfsList.length; i++) {
        if (jbfsList[i].key_ == resultValue) {
            resultText = jbfsList[i].text;
            break
        }
    }
    return resultText;
}
function onReplace(e) {
    var record = e.record;
    var resultValue = record.isReplace;
    var resultText = '';
    for (var i = 0; i < replaceList.length; i++) {
        if (replaceList[i].key_ == resultValue) {
            resultText = replaceList[i].text;
            break
        }
    }
    return resultText;
}
function onMajor(e) {
    var record = e.record;
    var resultValue = record.major;
    var resultText = '';
    for (var i = 0; i < majorList.length; i++) {
        if (majorList[i].key_ == resultValue) {
            resultText = majorList[i].text;
            break
        }
    }
    return resultText;
}
function onProcessType(e) {
    var record = e.record;
    var resultValue = record.type;
    var resultText = '';
    for (var i = 0; i < processTypeList.length; i++) {
        if (processTypeList[i].key_ == resultValue) {
            resultText = processTypeList[i].text;
            break
        }
    }
    return resultText;
}
function onProcessStatus(e) {
    var record = e.record;
    var resultValue = record.processStatus;
    var resultText = '';
    for (var i = 0; i < processStatusList.length; i++) {
        if (processStatusList[i].key_ == resultValue) {
            resultText = processStatusList[i].text;
            break
        }
    }
    return resultText;
}
function onNewProcess(e) {
    var record = e.record;
    var resultValue = record.isNewProcess;
    var resultText = '';
    for (var i = 0; i < replaceList.length; i++) {
        if (replaceList[i].key_ == resultValue) {
            resultText = replaceList[i].text;
            break
        }
    }
    return resultText;
}
function onStatus(e) {
    var record = e.record;
    var resultValue = record.infoStatus;
    var resultText = '';
    for (var i = 0; i < statusList.length; i++) {
        if (statusList[i].key_ == resultValue) {
            resultText = statusList[i].text;
            break
        }
    }
    var _html = '';
    var color = '';
    if(resultValue=='1'){
        color = '#cdcd62'
    }else if(resultValue=='2'){
        color = 'green';
    }else if(resultValue=='3'){
        color = 'red';
    }else if(resultValue=='4'){
        color = '#3b0df0';
    }
    _html = '<span style="color: '+color+'">'+resultText+'</span>'
    return _html;
}
function onHidePrice() {
    return '***';
}
function getDics(dicKey) {
    let resultDate = [];
    $.ajax({
        async: false,
        url: __rootPath + '/sys/core/commonInfo/getDicItem.do?dicType=' + dicKey,
        type: 'GET',
        contentType: 'application/json',
        success: function (data) {
            if (data.code == 200) {
                resultDate = data.data;
            }
        }
    });
    return resultDate;
}
