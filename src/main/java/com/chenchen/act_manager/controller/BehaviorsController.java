package com.chenchen.act_manager.controller;

import com.chenchen.act_manager.entity.QueryReq;
import com.chenchen.act_manager.entity.WordInfo;
import com.chenchen.act_manager.service.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author zouchanglin
 * @since 2026/1/20
 */
@RestController
@RequestMapping("/api")
public class BehaviorsController {

    @Autowired
    WordService wordService;

    @GetMapping("/behaviors")
    public List<WordInfo> behaviors(@ModelAttribute QueryReq req) {
        return wordService.getWordInfos(req);
    }

    @PostMapping("/behaviors")
    public Boolean save(@RequestBody WordInfo wordInfo) {
        return wordService.save(wordInfo);
    }

    @GetMapping("/behaviors/{id}")
    public WordInfo getById(@PathVariable Integer id) {
        return wordService.getWordInfoById(id);
    }

    @DeleteMapping("/behaviors/{id}")
    public Boolean deleteWordInfoById(@PathVariable Integer id) {
        wordService.deleteWordInfoById(id);
        return true;
    }

    @PutMapping("/behaviors/{id}")
    public Boolean updateWordInfoById(@PathVariable Integer id, @RequestBody WordInfo wordInfo) {
        wordService.updateWordInfoById(id, wordInfo);
        return true;
    }

    @GetMapping("/behaviors/export")
    public ResponseEntity<ByteArrayResource> export() {
        // 生成Word文档字节数组
        byte[] wordBytes = wordService.export(wordService.getAllWordInfos());

        // 创建资源对象
        ByteArrayResource resource = new ByteArrayResource(wordBytes);

        // 设置响应头
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=行为规范表.docx");
        headers.add(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");

        // 返回文件下载
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(wordBytes.length)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
                .body(resource);

    }
}
