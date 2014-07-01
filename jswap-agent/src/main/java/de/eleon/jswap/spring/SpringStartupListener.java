package de.eleon.jswap.spring;

import de.eleon.jswap.Log;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class SpringStartupListener implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        for (int i = 0; i < 100; i++)
            Log.LOG("SpringStartupListener");
    }

}