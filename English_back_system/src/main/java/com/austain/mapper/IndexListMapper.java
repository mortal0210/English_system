package com.austain.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface IndexListMapper {

    /**
     * 获取可用的单词课本列表。
     * 使用专门的元数据表而不是直接从 information_schema 读取所有表，避免把非词汇表暴露给前端。
     *
     * 建议配套数据表：word_book（见接口文档与建表 SQL）。
     */
    @Select("SELECT book_name FROM word_book WHERE enable = 1 ORDER BY sort_order, id")
    List<String> getBookNameList();
}
