package com.cuit.linzhi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 读取excel所需配置
 */
@Component
@ConfigurationProperties(prefix = "spring.excel")
public class RadiationExcelReadConfig {

    private List<String> filePath;
    private Map<String, Map<String,String>> configMap;

    public List<String> getFilePath() {
        return filePath;
    }

    public void setFilePath(List<String> filePath) {
        this.filePath = filePath;
    }

    public Map<String, Map<String, String>> getConfigMap() {
        return configMap;
    }

    public void setConfigMap(Map<String, Map<String, String>> configMap) {
        this.configMap = configMap;
    }
}
