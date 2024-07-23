<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>其他类列表</title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<div class="mini-fit" style="height: 100%;">
    <span>培训课程</span>
    <div id="courseListGrid" class="mini-datagrid" style="width: 100%; height:450px;" allowResize="false" showPager="false"
         url="${ctxPath}/portrait/course/coursePersonList.do?userId=${userId}&&reportYear=${reportYear}" idField="id"
         multiSelect="true" showColumnsMenu="false" sizeList="[10,20,50,100,200]" pageSize="10" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="indexcolumn" align="center" headerAlign="center" width="20">序号</div>
            <div field="courseName" width="120" headerAlign="center" align="center" allowSort="true">培训课程名称</div>
            <div field="courseDate" width="80" headerAlign="center" align="center" allowSort="true" >课程时间</div>
            <div field="studentNum" width="80" headerAlign="center" align="center" allowSort="true"  >课程人数</div>
            <div field="courseHour" width="50" headerAlign="center" align="center" allowSort="true" >课时</div>
            <div field="score" width="50" headerAlign="center" align="center" allowSort="false">得分</div>
        </div>
    </div>
    <span>培养</span>
    <div id="cultureListGrid" class="mini-datagrid" style="width: 100%; height:450px;" allowResize="false" showPager="false"
         url="${ctxPath}/portrait/culture/culturePersonList.do?userId=${userId}&&reportYear=${reportYear}" idField="id"
         multiSelect="true" showColumnsMenu="false" sizeList="[10,20,50,100,200]" pageSize="10" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="indexcolumn" align="center" headerAlign="center" width="20">序号</div>
            <div field="teacherName" width="120" headerAlign="center" align="center" allowSort="true">导师姓名</div>
            <div field="studentName" width="80" headerAlign="center" align="center" allowSort="true" >徒弟姓名</div>
            <div field="conformDate" width="80" headerAlign="center" align="center" allowSort="true"  >确定时间</div>
            <div field="score" width="50" headerAlign="center" align="center" allowSort="false">得分</div>
        </div>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var rewardListGrid = mini.get("rewardListGrid");
    var bidPlanListGrid = mini.get('bidPlanListGrid');
    var courseListGrid = mini.get('courseListGrid');
    var cultureListGrid = mini.get('cultureListGrid');
    rewardListGrid.load();
    bidPlanListGrid.load();
    courseListGrid.load();
    cultureListGrid.load();
    var rewardLevelList = getDics("rewardLevel");
    var rewardRankList = getDics("awardRank");
    var bidPlanRoleList = getDics("bidPlanRole");
    var bidPlanTypeList = getDics("bidPlanType");

    function onWSwitchRewardLevel(e) {
        var record = e.record;
        var rewardLevel = record.rewardLevel;
        var levelText = '';
        for(var i=0;i<rewardLevelList.length;i++){
            if(rewardLevelList[i].key_==rewardLevel){
                levelText = rewardLevelList[i].text;
                break
            }
        }
        return levelText;
    }
    function onWSwitchRewardRank(e) {
        var record = e.record;
        var rewardRank = record.rewardRank;
        var rankText = '';
        for(var i=0;i<rewardRankList.length;i++){
            if(rewardRankList[i].key_==rewardRank){
                rankText = rewardRankList[i].text;
                break
            }
        }
        return rankText;
    }
    function onWSwitchBidPlanRole(e) {
        var record = e.record;
        var projectRole = record.projectRole;
        var roleText = '';
        for(var i=0;i<bidPlanRoleList.length;i++){
            if(bidPlanRoleList[i].key_==projectRole){
                roleText = bidPlanRoleList[i].text;
                break
            }
        }
        return roleText;
    }
    function onWSwitchBidPlanType(e) {
        var record = e.record;
        var projectType = record.projectType;
        var typeText = '';
        for(var i=0;i<bidPlanTypeList.length;i++){
            if(bidPlanTypeList[i].key_==projectType){
                typeText = bidPlanTypeList[i].text;
                break
            }
        }
        return typeText;
    }
    function getDics(dicKey) {
        let resultDate = [];
        $.ajax({
            async:false,
            url: __rootPath + '/sys/core/commonInfo/getDicItem.do?dicType='+dicKey,
            type: 'GET',
            contentType: 'application/json',
            success: function (data) {
                if (data.code==200) {
                    resultDate = data.data;
                }
            }
        });
        return resultDate;
    }
</script>
</body>
</html>
