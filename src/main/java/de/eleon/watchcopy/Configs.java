package de.eleon.watchcopy;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

import java.util.List;
import java.util.Properties;

import static java.lang.String.format;

public class Configs {

    private final static String KEY_FORMAT = "%s.%s";

    public static List<Config> getConfigsFromSystemProperties(final String prefix) {
        return getConfigsFromPoperties(prefix, System.getProperties());
    }

    protected static List<Config> getConfigsFromPoperties(final String prefix, final Properties properties) {
        final String from = format(KEY_FORMAT, prefix, "from");
        List<Config> ret = FluentIterable.from(properties.keySet()).filter(new Predicate<Object>() {
            @Override
            public boolean apply(Object input) {
                return ((String)input).startsWith(from);
            }
        }).transform(new Function<Object, Config>() {
            @Override
            public Config apply(Object input) {

                // from
                String fromKey = (String)input;
                String fromValue = properties.getProperty(fromKey);
                Preconditions.checkArgument(fromValue != null && !fromValue.isEmpty(), "need param -D%s=value", fromKey);

                // to
                String toKey = fromKey.replace(from, format(KEY_FORMAT, prefix, "to"));
                String toValue = properties.getProperty(toKey);
                Preconditions.checkArgument(toValue != null && !toValue.isEmpty(), "need param -D%s=value", toKey);

                // config
                String configKey = fromKey.replace(from, format(KEY_FORMAT, prefix, "config"));
                String configValue = properties.getProperty(configKey);

                return new Config(fromValue, toValue, configValue != null ? configValue : "");
            }
        }).toList();
        if (ret.size() == 0) {
            throw new IllegalArgumentException(format("need params -D%s[0]=value -D%s[0]=value", from, format(KEY_FORMAT, prefix, "to")));
        }
        return ret;
    }
}
