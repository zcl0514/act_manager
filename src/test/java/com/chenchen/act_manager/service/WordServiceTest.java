package com.chenchen.act_manager.service;

import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author zouchanglin
 * @since 2026/1/20
 */
@SpringBootTest
//@MapperScan("com.chenchen.act_manager.service")
class WordServiceTest {

    @Autowired
    private WordService wordService;

    @Test
    void generate() {
        wordService.generate(wordService.getWordInfos());
    }
}