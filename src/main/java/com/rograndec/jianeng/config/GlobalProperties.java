package com.rograndec.jianeng.config;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.util.Map;
import java.util.Properties;

@Configuration
public class GlobalProperties {
    private static Properties properties;

    static {
        try {
            properties = yaml2Properties("application.yml");
            if (properties != null && properties.contains("spring.profiles.active")) {
                Properties activeProfileProperties = yaml2Properties("application-" + properties.getProperty("spring.profiles.active") + ".yml");
                if (activeProfileProperties != null && activeProfileProperties.size() > 0) {
                    for (Map.Entry<Object, Object> entry : activeProfileProperties.entrySet()) {
                        if (entry.getKey() == null) {
                            continue;
                        }
                        properties.put(entry.getKey(), entry.getValue());
                    }
                }
            }
        } catch (Throwable e) {
            ;
        }
    }

    public static Properties yaml2Properties(String yamlSource) {
        YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
        yaml.setResources(new ClassPathResource(yamlSource));
        return yaml.getObject();
    }

    public static Properties getParentProperties () {
        return properties;
    }

    public static Object getPropertyValue(String key) {
        if (properties == null || properties.isEmpty()) {
            return null;
        }
        return properties.get(key);
    }

}
