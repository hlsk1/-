package com.aizuche.controller;

import com.aizuche.util.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    // 上传目录：项目 static/uploads/vehicles/
    private static final String UPLOAD_DIR = System.getProperty("user.dir")
            + File.separator + "src" + File.separator + "main"
            + File.separator + "resources" + File.separator + "static"
            + File.separator + "uploads" + File.separator + "vehicles";

    /**
     * 单张图片上传
     */
    @PostMapping("/image")
    public Result uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("请选择文件");
        }

        // 校验文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return Result.error("只支持图片文件上传");
        }

        try {
            String url = saveFile(file);
            Map<String, String> result = new HashMap<>();
            result.put("url", url);
            result.put("name", file.getOriginalFilename());
            return Result.success(result);
        } catch (IOException e) {
            return Result.error("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 多张图片上传（返回URL列表，逗号分隔的字符串）
     */
    @PostMapping("/images")
    public Result uploadImages(@RequestParam("files") List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            return Result.error("请选择文件");
        }

        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) continue;
            try {
                urls.add(saveFile(file));
            } catch (IOException e) {
                return Result.error("文件上传失败: " + e.getMessage());
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("urls", urls);
        result.put("urlString", String.join(",", urls));
        result.put("count", urls.size());
        return Result.success(result);
    }

    private String saveFile(MultipartFile file) throws IOException {
        // 确保目录存在
        File dir = new File(UPLOAD_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 生成唯一文件名：日期_随机UUID_原始扩展名
        String datePrefix = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        String originalName = file.getOriginalFilename();
        String ext = "";
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf("."));
        }
        String newFileName = datePrefix + "_" + uuid + ext;

        // 保存文件
        Path filePath = Paths.get(UPLOAD_DIR, newFileName);
        Files.write(filePath, file.getBytes());

        // 返回可访问的URL路径
        return "/uploads/vehicles/" + newFileName;
    }
}
