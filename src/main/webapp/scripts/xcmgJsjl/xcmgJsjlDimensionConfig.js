function renderMeetingUserNames(e) {
    var record = e.record;
    var html = "<div style='line-height: 20px;height:150px;overflow: auto' >";
    var meetingUserNames = record.meetingUserNames;
    if (meetingUserNames == null) {
        meetingUserNames = "";
    }
    html += '<span style="white-space:pre-wrap" >' + meetingUserNames + '</span>';
    html += '</div>'
    return html;
}

function renderContent() {
    var record = e.record;
    var html = "<div style='line-height: 20px;height:150px;overflow: auto' >";
    var contentAndConclusion = record.contentAndConclusion;
    if (contentAndConclusion == null) {
        contentAndConclusion = "";
    }
    html += '<span style="white-space:pre-wrap" >' + contentAndConclusion + '</span>';
    html += '</div>'
    return html;
}

function renderPlan() {
    var record = e.record;
    var html = "<div style='line-height: 20px;height:150px;overflow: auto' >";
    var planAndResult = record.planAndResult;
    if (planAndResult == null) {
        planAndResult = "";
    }
    html += '<span style="white-space:pre-wrap" >' + planAndResult + '</span>';
    html += '</div>'
    return html;
}