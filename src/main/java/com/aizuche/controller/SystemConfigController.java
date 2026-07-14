package com.aizuche.controller;

import com.aizuche.entity.SystemConfig;
import com.aizuche.service.SystemConfigService;
import com.aizuche.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/config")
public class SystemConfigController {

    @Autowired
    private SystemConfigService configService;

    @GetMapping("/list")
    public Result list() {
        List<SystemConfig> list = configService.findAll();
        return Result.success(list);
    }

    @GetMapping("/map")
    public Result map() {
        Map<String, String> configMap = configService.getAllConfigMap();
        return Result.success(configMap);
    }

    @GetMapping("/get/{key}")
    public Result getByKey(@PathVariable String key) {
        return configService.findByKey(key)
                .map(Result::success)
                .orElse(Result.error("配置项不存在"));
    }

    @PostMapping("/save")
    public Result save(@RequestParam String key,
                       @RequestParam String value,
                       @RequestParam(required = false) String description) {
        SystemConfig config = configService.saveOrUpdate(key, value, description);
        return Result.success(config);
    }

    @PostMapping("/saveBatch")
    public Result saveBatch(@RequestBody Map<String, String> configs) {
        configService.saveAll(configs);
        return Result.success("配置保存成功");
    }

    @DeleteMapping("/delete/{key}")
    public Result delete(@PathVariable String key) {
        configService.deleteByKey(key);
        return Result.success("配置项已删除");
    }
}
