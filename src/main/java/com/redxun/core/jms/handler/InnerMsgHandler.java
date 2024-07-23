package com.redxun.core.jms.handler;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.redxun.core.jms.IJmsHandler;
import com.redxun.core.jms.MessageModel;
import com.redxun.core.jms.MessageUtil;
import com.redxun.core.util.BeanUtil;
import com.redxun.org.api.model.IUser;
import com.redxun.sys.org.entity.OsUser;

/**
 * 内部消息。
 * @author ray
 *
 */
public class InnerMsgHandler implements IJmsHandler {


//    @Resource
//    InfInnerMsgManager infInnerMsgManager;

    @Override
    public String getType() {
        return "inner";
    }

    @Override
    public String getName() {
        return "内部消息";
    }

    @Override
    public void handleMessage(MessageModel model) {

        String userIds=getSendUsers( model);
        String content="";
        try {
            content = MessageUtil.getContent(model, this.getType());
            Map<String,Object> vars=model.getVars();
            String linkMsg="";
            if(BeanUtil.isNotEmpty(vars)){
                linkMsg= (String) vars.get("linkmsg");
            }
//            infInnerMsgManager.sendMessage("0", userIds, "", content, linkMsg,true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param model
     * @return
     */
    private String getSendUsers(MessageModel model){
        List<IUser> userList=model.getRecieverList();
        if(BeanUtil.isEmpty(userList)) return "";
        String userIds="";
        for(int i=0;i<userList.size();i++){
            IUser user=userList.get(i);
            if(i==0){
                userIds=user.getUserId();
            }
            else{
                userIds+="," + user.getUserId();
            }
        }
        return userIds;
    }
}
