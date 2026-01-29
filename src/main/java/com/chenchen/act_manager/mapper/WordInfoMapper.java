package com.chenchen.act_manager.mapper;

import com.chenchen.act_manager.entity.QueryReq;
import com.chenchen.act_manager.entity.WordInfo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author zouchanglin
 * @since 2026/1/19
 */
@Mapper
public interface WordInfoMapper {

    @Insert("insert into word_info (id,role,red_flower_count,type,extra,owner) " +
            "values(#{id},#{role},#{redFlowerCount},#{type},#{extra},#{owner})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(WordInfo record);

    @Select("select * from word_info where status=1")
    List<WordInfo> selectAll();

    @Select("select * from word_info where id=#{id}")
    WordInfo selectById(@RequestParam("id") Integer id);

    @Update("update word_info set status=0 where id=#{id}")
    void deleteById(@RequestParam("id") Integer id);

    @Update("update word_info " +
            "set role=#{wordInfo.role},red_flower_count=#{wordInfo.redFlowerCount},type=#{wordInfo.type},extra=#{wordInfo.extra},owner=#{wordInfo.owner} " +
            "where id=#{id}")
    void updateById(@Param("id") Integer id,@Param("wordInfo") WordInfo wordInfo);

    List<WordInfo> search(QueryReq req);
}
