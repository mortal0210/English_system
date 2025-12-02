package com.austain.mapper;

import com.austain.domain.po.RecordPO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface StudyRecordMapper {

    /**
     * 添加学习记录，添加时把selected字段设置为1，表示今天需要复习
     * @param studyRecord 学习记录
     * @return 添加结果
     */
    @Insert("insert into ${table}(record,selected) values(#{studyRecord},1)")
    int addStudyRecord(String studyRecord,String table);

    /**
     * 获取今天需要复习的记录
     * @return 需要复习的记录
     */
    @Select("select * from ${table}")
    List<RecordPO> getTodayList(String table);


    /**
     * 更新所有记录的selected字段为0，用于每晚定时器触发
     */
    @Update("UPDATE ${table} SET selected = 0,already_reviewed = 0")
    void updateAllScoreToZero(String table);


    /**
     * 获取所有已选择的记录，表示今天要复习的内容
     * @return 已选择的记录
     */
    @Select("select * from ${table} where selected = 1")
    List<RecordPO> getSelectedList(String table);


    /**
     * 将已选择的记录的selected字段设置为1，用于第一次查询的时候，将符合当天复习的内容标记为已选择
     * @param selectedIds 已选择的记录的id
     */
    void markSelected(@Param("selectedIds") List<Integer> selectedIds,@Param("table") String table);

    /**
     * 将已选择的记录的selected字段设置为1，用于标记已复习的内容，ids为前端传递
     * @param ids 已选择的记录的id
     * @return 添加结果
     */
    int markReviewed(List<String> ids,String table);

    /**
     * 清空已复习的记录，在前端每次提交已复习的内容时触发
     */
    @Update("UPDATE ${table} SET already_reviewed = 0")
    void clearReviewed(@Param("table") String table);

    /**
     * 判断记录表是否存在，防止前端选择了还未创建的记录表时直接抛出 SQL 异常。
     */
    @Select("SELECT COUNT(*) FROM information_schema.TABLES WHERE TABLE_SCHEMA = 'english' AND TABLE_NAME = #{table}")
    int existsTable(@Param("table") String table);
}
