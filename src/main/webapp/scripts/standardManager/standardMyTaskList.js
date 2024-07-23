var solutionList;
$(function () {
    getSolutions();
})
function getSolutions() {
    $.ajax({
        url: __rootPath+"/standardManager/core/standard/getSolutions.do",
        type: 'get',
        contentType: 'application/json',
        success: function (data) {
            if(data){
                solutionList = mini.decode(data);
            }
        },
        failture:function(){
        }
    });
}
function filterDbType(_type) {
    for(var i=0;i<solutionList.length;i++){
        if(solutionList[i].id==_type){
            return solutionList[i].text;
        }
    }
}
