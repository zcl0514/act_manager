package com.chenchen.act_manager.mapper;

import com.chenchen.act_manager.dtos.WordInfoDto;
import com.chenchen.act_manager.entity.WordInfo;
import com.chenchen.act_manager.enums.ActManagerType;
import com.chenchen.act_manager.enums.OwnerType;
import com.chenchen.act_manager.utils.CollectionUtil;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @author zouchanglin
 * @since 2026/1/19
 */
@SpringBootTest
@MapperScan("com.chenchen.act_manager.mapper")
class WordInfoMapperTest {

    @Autowired
    private WordInfoMapper wordInfoMapper;

    @Test
    public void testInsert() {

        String s = "制定并策划日常行为管理手册==学习新技能（如学车考驾照）==跳出舒适圈，学习一种健康的兴趣爱好";
        String[] arr = s.split("==");
        for (String str : arr) {
            WordInfo wordInfo = new WordInfo();
            wordInfo.setRole(str);
            wordInfo.setRedFlowerCount(3);
            wordInfo.setType(ActManagerType.GREAT_AWARD.getCode());
            wordInfo.setOwner(OwnerType.DAD_MUM.getCode());
            wordInfo.setExtra("特殊奖励");
            wordInfoMapper.insert(wordInfo);
        }

    }

    @Test
    public void testSelectAll() {
        List<WordInfo> wordInfos = wordInfoMapper.selectAll();
        System.out.println(CollectionUtil.toList(wordInfos,this::toDto));
    }

    private WordInfoDto toDto(WordInfo wordInfo) {
        WordInfoDto wordInfoDto = new WordInfoDto();
        wordInfoDto.setRole(wordInfo.getRole());
        wordInfoDto.setRedFlowerCount(wordInfo.getRedFlowerCount());
        wordInfoDto.setExtra(wordInfo.getExtra());
        wordInfoDto.setType(ActManagerType.getByCode(wordInfo.getType()));
        wordInfoDto.setOwner(OwnerType.getByCode(wordInfo.getOwner()));
        return wordInfoDto;
    }


}