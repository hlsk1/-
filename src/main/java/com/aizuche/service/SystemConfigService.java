package com.aizuche.service;

import com.aizuche.entity.SystemConfig;
import com.aizuche.repository.SystemConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class SystemConfigService {

    @Autowired
    private SystemConfigRepository configRepository;

    public List<SystemConfig> findAll() {
        return configRepository.findAll();
    }

    public Optional<SystemConfig> findByKey(String key) {
        return configRepository.findByConfigKey(key);
    }

    /**
     * 获取所有配置项，以 Map 形式返回
     */
    public Map<String, String> getAllConfigMap() {
        Map<String, String> result = new HashMap<>();
        for (SystemConfig config : configRepository.findAll()) {
            result.put(config.getConfigKey(), config.getConfigValue());
        }
        return result;
    }

    /**
     * 获取单个配置值
     */
    public String getConfigValue(String key, String defaultValue) {
        return configRepository.findByConfigKey(key)
                .map(SystemConfig::getConfigValue)
                .orElse(defaultValue);
    }

    @Transactional
    public SystemConfig saveOrUpdate(String key, String value, String description) {
        SystemConfig config = configRepository.findByConfigKey(key).orElse(new SystemConfig());
        config.setConfigKey(key);
        config.setConfigValue(value);
        config.setDescription(description);
        config.setCreateTime(config.getCreateTime() != null ? config.getCreateTime() : LocalDateTime.now());
        config.setUpdateTime(LocalDateTime.now());
        return configRepository.save(config);
    }

    @Transactional
    public void saveAll(Map<String, String> configs) {
        for (Map.Entry<String, String> entry : configs.entrySet()) {
            saveOrUpdate(entry.getKey(), entry.getValue(), null);
        }
    }

    @Transactional
    public void deleteByKey(String key) {
        configRepository.findByConfigKey(key).ifPresent(configRepository::delete);
    }
}
