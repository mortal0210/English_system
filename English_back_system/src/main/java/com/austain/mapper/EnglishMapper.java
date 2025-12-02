package com.austain.mapper;

import com.austain.domain.po.AddRequest;
import com.austain.domain.po.Englishs;
import com.austain.domain.po.Sentence;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface EnglishMapper {

    @Insert("insert into englishword4420(word,chinese,pronounce,times) values(#{word},#{chinese},#{pronounce},#{times})")
    void insert(Englishs englishs);

    @Select("select * from ${bookName} where id between #{startIndex} and  #{endIndex}")
    List<Englishs> getEnglishList(String startIndex,String endIndex,@Param("bookName") String bookName);

    @Update("update englishword575 set pronounce = #{pronounce} where word = #{word}")
//    @Update("update ${bookName} set pronounce = #{pronounce} where word = #{word}")
    void updatePronounce(String word, String pronounce);

    @Insert("insert into againenglishword(word,chinese,pronounce,times,bookname) values(#{word},#{chinese},#{pronounce},#{times},#{bookname})")
    int addAgainWord(AddRequest request);

    @Delete("delete from againenglishword where word = #{word} and id = #{id}")
    int removeAgainWord(AddRequest request);

    @Select("select * from sentence200 where id between #{start} and #{end}")
    List<Sentence> getSentenceList(String start, String end);

    @Insert("insert into finally_again_word(word,chinese,pronounce,times,bookname) values(#{word},#{chinese},#{pronounce},#{times},#{bookname})")
    int finalAddAgainWord(AddRequest request);

    /**
     * 统计指定词书的总单词数，用于 /stats/books/{bookName}。
     */
    @Select("SELECT COUNT(*) FROM ${bookName}")
    int countWordsByBook(@Param("bookName") String bookName);
}
