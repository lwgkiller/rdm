package com.redxun.saweb.listener;

import com.baidu.fsg.uid.impl.CachedUidGenerator;
import com.redxun.saweb.util.WebAppUtil;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;

public class SysInitListener implements ApplicationListener<ContextRefreshedEvent>,Ordered {
    @Override
    public void onApplicationEvent(ContextRefreshedEvent ev) {
        if (ev.getApplicationContext().getParent() != null) return;
        CachedUidGenerator cachedUidGenerator= WebAppUtil.getBean(CachedUidGenerator.class);
        try {
                cachedUidGenerator.init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
