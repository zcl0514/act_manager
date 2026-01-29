package com.chenchen.act_manager.service;

import com.chenchen.act_manager.entity.QueryReq;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
        wordService.generate(wordService.getWordInfos(new QueryReq()));
    }

    @Test
    public void search(){
        QueryReq queryReq = new QueryReq();
        queryReq.setType(3);
        System.out.println(wordService.getWordInfos(queryReq));
    }
}