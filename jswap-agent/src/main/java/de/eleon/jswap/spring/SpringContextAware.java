package de.eleon.jswap.spring;

import com.google.common.collect.Lists;
import de.eleon.jswap.Log;
import org.springframework.context.support.AbstractApplicationContext;

import java.util.List;

public class SpringContextAware {

    private static List<AbstractApplicationContext> abstractApplicationContexts = Lists.newArrayList();

    public static void register(AbstractApplicationContext abstractApplicationContext) {
        Log.LOG("SpringContextAware: register org.springframework.context.support.AbstractApplicationContext");
        abstractApplicationContexts.add(abstractApplicationContext);
    }

    public static List<AbstractApplicationContext> getAbstractApplicationContexts() {
        return abstractApplicationContexts;
    }
}
