package com.joyfishs.system.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * D6 临时调试用: 直接读 /tmp/qrcode/ 返回图片.
 * 用于验证文件读路径 vs 静态资源 handler.
 * <p>
 * 走 controller 路径 = Spring DispatcherServlet 路由 = 证明 controller 是工作的.
 * 走 addResourceHandlers = ResourceHttpRequestHandler = 不进 controller, 但被 WebMvc 注册.
 * </p>
 * <p>debug 用, 验证完会删掉</p>
 */
@RestController
public class DebugQrController {

    @Value("${safety.qr.local.base-path:/tmp/qrcode}")
    private String basePath;

    @GetMapping("/debug/qrcode/**")
    public ResponseEntity<?> serveQrCode() {
        // 简化: 把 /debug/qrcode/xxx 映射到 /tmp/qrcode/xxx
        // 但 /debug/qrcode/** 这种 ant 模式 Spring 会接受, 这里我们用 raw request 路径
        return ResponseEntity.badRequest().body("use /debug/file?p=...");
    }

    @GetMapping("/debug/file")
    public ResponseEntity<?> serveFile(String p) {
        if (p == null || p.contains("..")) {
            return ResponseEntity.badRequest().body("invalid path");
        }
        File f = Paths.get(basePath, p).toFile();
        if (!f.exists()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(new FileSystemResource(f));
    }
}
