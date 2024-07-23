<%@page contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>技术创新列表</title>
    <%@include file="/commons/list.jsp" %>
</head>
<body>
<div class="mini-fit" style="height: 100%;">
    <span>科技项目</span>
    <div id="projectListGrid" class="mini-datagrid" style="width: 100%; height:250px;" allowResize="false" showPager="false" autoload="true"
         url="${ctxPath}/portrait/project/projectPersonList.do?userId=${userId}&&reportYear=${reportYear}" idField="id"
         multiSelect="true" showColumnsMenu="false" sizeList="[10,20,50,100,200]" pageSize="10" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="indexcolumn" align="center" headerAlign="center" width="20">序号</div>
            <div field="projectCode" width="80" headerAlign="center" align="center" allowSort="true">项目编号</div>
            <div field="projectName" width="120" headerAlign="center" align="left" allowSort="true" >项目名称</div>
            <div field="categoryName" width="80" headerAlign="center" align="center" allowSort="true" >项目类别</div>
            <div field="levelName" width="50" headerAlign="center" align="center" allowSort="true" >项目级别</div>
            <div field="projectTask" width="100" headerAlign="center" align="left" allowSort="true" >承担工作</div>
            <div field="roleName" width="50" headerAlign="center" align="center" allowSort="true" >项目角色</div>
            <div field="totalScore" width="50" headerAlign="center" align="center" allowSort="false">积分</div>
        </div>
    </div>
    <span>参与标准</span>
    <div id="standardListGrid" class="mini-datagrid" style="width: 100%; height:250px;" allowResize="false" showPager="false" autoload="true"
         url="${ctxPath}/portrait/standard/standardPersonList.do?userId=${userId}&&reportYear=${reportYear}" idField="id"
         multiSelect="true" showColumnsMenu="false" sizeList="[10,20,50,100,200]" pageSize="10" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="indexcolumn" align="center" headerAlign="center" width="20">序号</div>
            <div field="standardCode" width="80" headerAlign="center" align="center" allowSort="true">标准编号</div>
            <div field="standardName" width="120" headerAlign="center" align="left" allowSort="true" >标准名称</div>
            <div field="standardType" width="50" headerAlign="center" align="center" allowSort="true" >标准分类</div>
            <div field="standardSort" width="50" headerAlign="center" align="center" allowSort="true" renderer="onWSwitchStandardRank">排序</div>
            <div field="score" width="50" headerAlign="center" align="center" allowSort="false">得分</div>
            <div field="publishDate" width="50" headerAlign="center" align="center" allowSort="true" >发布日期</div>
            <div field="author" width="100" headerAlign="center" align="left" allowSort="true" >起草人</div>
        </div>
    </div>
    <span>知识产权</span>
    <div id="knowledgeListGrid" class="mini-datagrid" style="width: 100%; height:250px;" allowResize="false" showPager="false" autoload="true"
         url="${ctxPath}/portrait/knowledge/knowledgePersonList.do?userId=${userId}&&reportYear=${reportYear}" idField="id"
         multiSelect="true" showColumnsMenu="false" sizeList="[10,20,50,100,200]" pageSize="10" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="indexcolumn" align="center" headerAlign="center" width="20">序号</div>
            <div field="knowledgeCode" width="80" headerAlign="center" align="center" allowSort="true">产权号</div>
            <div field="knowledgeName" width="120" headerAlign="center" align="left" allowSort="true" >产权名称</div>
            <div field="knowledgeType" width="80" headerAlign="center" align="center" allowSort="true" renderer="onWSwitchType" >产权类型</div>
            <div field="applyDate" width="50" headerAlign="center" align="center" allowSort="true" >申请日期</div>
            <div field="knowledgeStatus" width="80" headerAlign="center" align="center" allowSort="true" renderer="onWSwitchStatus">产权状态</div>
            <div field="authorizeDate" width="50" headerAlign="center" align="center" allowSort="true" >受理或授权日期</div>
            <div field="ranking" width="80" headerAlign="center" align="center" allowSort="true" >排名</div>
            <div field="score" width="50" headerAlign="center" align="center" allowSort="false">得分</div>
        </div>
    </div>
    <span>荣誉奖项</span>
    <div id="rewardListGrid" class="mini-datagrid" style="width: 100%; height:200px;" allowResize="false" showPager="false" autoload="true"
         url="${ctxPath}/portrait/reward/rewardPersonList.do?userId=${userId}&&reportYear=${reportYear}" idField="id"
         multiSelect="true" showColumnsMenu="false" sizeList="[10,20,50,100,200]" pageSize="10" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="indexcolumn" align="center" headerAlign="center" width="20">序号</div>
            <div field="rewardName" width="120" headerAlign="center" align="left" allowSort="true">奖项名称</div>
            <div field="rewardLevel" width="80" headerAlign="center" align="center" allowSort="true" renderer="onWSwitchRewardLevel">奖项级别</div>
            <div field="rewardRank" width="80" headerAlign="center" align="center" allowSort="true" renderer="onWSwitchRewardRank">排名</div>
            <div field="score" width="50" headerAlign="center" align="center" allowSort="false">得分</div>
            <div field="rewardYear" width="50" headerAlign="center" align="center" allowSort="true" >获奖年度</div>
            <div field="CREATE_TIME_" width="80" headerAlign="center" align="center" allowSort="true" >同步日期</div>
        </div>
    </div>
    <span>专有技术</span>
    <div id="secretListGrid" class="mini-datagrid" style="width: 100%; height:200px;" allowResize="false" showPager="false" autoload="true"
         url="${ctxPath}/portrait/secret/secretPersonList.do?userId=${userId}&&reportYear=${reportYear}" idField="id"
         multiSelect="true" showColumnsMenu="false" sizeList="[10,20,50,100,200]" pageSize="10" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="indexcolumn" align="center" headerAlign="center" width="20">序号</div>
            <div field="jsmmNumber" width="80" headerAlign="center" align="center" allowSort="true">秘密编号</div>
            <div field="jsmmName" width="120" headerAlign="center" align="left" allowSort="true">秘密名称</div>
            <div field="score" width="50" headerAlign="center" align="center" allowSort="false">得分</div>
            <div field="applyYear" width="50" headerAlign="center" align="center" allowSort="true" >年度</div>
            <div field="CREATE_TIME_" width="80" headerAlign="center" align="center" allowSort="true" >同步日期</div>
        </div>
    </div>
    <span>技术数据库</span>
    <div id="techListGrid" class="mini-datagrid" style="width: 100%; height:200px;" allowResize="false" showPager="false" autoload="true"
         url="${ctxPath}/portrait/technology/technologyPersonList.do?userId=${userId}&&reportYear=${reportYear}" idField="id"
         multiSelect="true" showColumnsMenu="false" sizeList="[10,20,50,100,200]" pageSize="10" allowAlternating="true"
         pagerButtons="#pagerButtons">
        <div property="columns">
            <div type="indexcolumn" align="center" headerAlign="center" width="20">序号</div>
            <div field="jsNum" width="80" headerAlign="center" align="center" allowSort="false">技术编号</div>
            <div field="jsName" width="120" headerAlign="center" align="left" allowSort="false">技术名称</div>
            <div field="score" width="50" headerAlign="center" align="center" allowSort="false">得分</div>
            <div field="applyYear" width="50" headerAlign="center" align="center" allowSort="true" >年度</div>
            <div field="CREATE_TIME_" width="80" headerAlign="center" align="center" allowSort="true" >同步日期</div>
        </div>
    </div>
</div>
<script type="text/javascript">
    mini.parse();
    var jsUseCtxPath = "${ctxPath}";
    var projectListGrid = mini.get("projectListGrid");
    var standardListGrid = mini.get('standardListGrid');
    var knowledgeListGrid = mini.get('knowledgeListGrid');
    var typeList = getDics("knowledgeType");
    var statusList = getDics("knowledgeStatus");
    var rankList = getDics("knowledgeRank");
    var standardRankList = getDics("standardRank");
    var rewardLevelList = getDics("rewardLevel");
    var rewardRankList = getDics("awardRank");
    function onWSwitchStandardRank(e) {
        var record = e.record;
        var standardRank = record.standardSort;
        var rankText = '';
        for(var i=0;i<standardRankList.length;i++){
            if(standardRankList[i].key_==standardRank){
                rankText = standardRankList[i].text;
                break
            }
        }
        return rankText;
    }
    function onWSwitchType(e) {
        var record = e.record;
        var knowledgeType = record.knowledgeType;
        var typeText = '';
        for(var i=0;i<typeList.length;i++){
            if(typeList[i].key_==knowledgeType){
                typeText = typeList[i].text;
                break
            }
        }
        return typeText;
    }
    function onWSwitchStatus(e) {
        var record = e.record;
        var knowledgeStatus = record.knowledgeStatus;
        var statusText = '';
        for(var i=0;i<statusList.length;i++){
            if(statusList[i].key_==knowledgeStatus){
                statusText = statusList[i].text;
                break
            }
        }
        return statusText;
    }
    function onWSwitchRank(e) {
        var record = e.record;
        var ranking = record.ranking;
        var rankText = '';
        for(var i=0;i<rankList.length;i++){
            if(rankList[i].key_==ranking){
                rankText = rankList[i].text;
                break
            }
        }
        return rankText;
    }
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
