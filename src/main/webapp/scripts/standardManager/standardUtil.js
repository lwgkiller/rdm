//是否是标准管理人员
function whetherIsStandardManager(userRoles) {
    for (var i = 0; i < userRoles.length; i++) {
        if (userRoles[i].REL_TYPE_KEY_ == 'GROUP-USER-LEADER'||userRoles[i].REL_TYPE_KEY_ == 'GROUP-USER-BELONG') {
            if(userRoles[i].NAME_.indexOf('标准管理人员')!=-1) {
                return true;
            }
        }
    }
    return false;
}

//是否是指定体系类别的标准管理人员
function whetherIsPointStandardManager(systemCategoryId,userRoles) {
    for (var i = 0; i < userRoles.length; i++) {
        if (userRoles[i].REL_TYPE_KEY_ == 'GROUP-USER-LEADER'||userRoles[i].REL_TYPE_KEY_ == 'GROUP-USER-BELONG') {
            if(systemCategoryId=='GL'&&userRoles[i].NAME_.indexOf('管理标准管理人员')!=-1) {
                return true;
            }
            if(systemCategoryId=='JS'&&userRoles[i].NAME_.indexOf('技术标准管理人员')!=-1) {
                return true;
            }
            if(systemCategoryId=='NK'&&userRoles[i].NAME_.indexOf('内控标准管理人员')!=-1) {
                return true;
            }
            if(systemCategoryId=='GLBZFBSPRY'&&userRoles[i].NAME_.indexOf('管理标准附表审批人员')!=-1) {
                return true;
            }
        }
    }
    return false;
}

/**
 * 是否是指定体系类别的子管理员
 */
function whetherIsSubManager(systemCategoryId,currentUserSubManager) {
    if(currentUserSubManager) {
        var oneCategorySystemIds=currentUserSubManager[systemCategoryId];
        if(oneCategorySystemIds&&oneCategorySystemIds.length!=0) {
            return true;
        }
    }
    return false;
}

/**
 * 是否是指定体系的子管理员
 */
function whetherIsPointSubManager(systemCategoryId,systemId,currentUserSubManager) {
    if(currentUserSubManager) {
        var oneCategorySystemIds=currentUserSubManager[systemCategoryId];
        if(oneCategorySystemIds&&oneCategorySystemIds.indexOf(systemId)!=-1) {
            return true;
        }
    }
    return false;
}

//是否是管理职级人员
function whetherIsGLMan(userZJ) {
    for (var i = 0; i < userZJ.length; i++) {
        if (userZJ[i].REL_TYPE_KEY_ == 'GROUP-USER-LEADER'||userZJ[i].REL_TYPE_KEY_ == 'GROUP-USER-BELONG') {
            if(userZJ[i].NAME_.indexOf('管理')!=-1) {
                return true;
            }
        }
    }
    return false;
}

//是否是标准管理领导（预览和下载所有标准都不需要申请）
function whetherIsLeader(userRoles) {
    for (var i = 0; i < userRoles.length; i++) {
        if (userRoles[i].REL_TYPE_KEY_ == 'GROUP-USER-LEADER'||userRoles[i].REL_TYPE_KEY_ == 'GROUP-USER-BELONG') {
            if(userRoles[i].NAME_=='标准管理领导') {
                return true;
            }
        }
    }
    return false;
}
function bytesToSize(bytes) {
    if (bytes === 0) return '0 B';
    var k = 1024,
        sizes = ['B', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'],
        i = Math.floor(Math.log(bytes) / Math.log(k));

    return (bytes / Math.pow(k, i)).toPrecision(3) + ' ' + sizes[i];
}

function getYMDHmsString(dateObj) {
    if(!dateObj) {
        return "";
    }
    var year=dateObj.getFullYear();
    var month=dateObj.getMonth()+1;
    if(month<10) {
        month='0'+month;
    }
    var date=dateObj.getDate();
    if(date<10) {
        date='0'+date;
    }
    var hour=dateObj.getHours();
    if(hour<10) {
        hour='0'+hour;
    }
    var minute=dateObj.getMinutes();
    if(minute<10) {
        minute='0'+minute;
    }
    var second=dateObj.getSeconds();
    if(second<10) {
        second='0'+second;
    }
    return year+'-'+month+'-'+date+' '+hour+':'+minute+':'+second;
}