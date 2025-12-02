package com.austain.srevice.impl;


import com.austain.domain.po.RecordPO;
import com.austain.handler.SimpleEbbinghausReview;
import com.austain.mapper.StudyRecordMapper;
import com.austain.srevice.StudyRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;


@Service
public class StudyRecordServiceImpl implements StudyRecordService {

    @Autowired
    private StudyRecordMapper studyRecordMapper;

    @Override
    public int addStudyRecord(String studyRecord,String table) {
        // 如果表不存在，直接返回 0，避免 SQL 异常
        if (studyRecordMapper.existsTable(table) == 0) {
            return 0;
        }
        return studyRecordMapper.addStudyRecord(studyRecord,table);
    }

    @Override
    @Transactional
    public List<RecordPO> getTodayList(String table) {
        // 句子记录表、单词记录表等可能尚未创建，这里先做表存在性校验
        if (studyRecordMapper.existsTable(table) == 0) {
            // 返回空列表，前端会显示“今日暂无学习内容”的提示，而不是抛出错误
            return new ArrayList<>();
        }
        if (table.equals("daily_record")){          // 如果是日常记录表，则直接返回所有内容，不经过筛选
            System.out.println("我是直接返回日常所有");
            return studyRecordMapper.getTodayList(table);
        }

        List<RecordPO> studyRecordList = studyRecordMapper.getSelectedList(table);  // 查询数据库是否已经存在今天要复习的内容，有的话直接从数据库查
        if (!studyRecordList.isEmpty()){   // 数据库查到的内容不为空，就直接返回数据库的，里面做了标记，为业务所需
            System.out.println("我来自数据库查询");
            for (RecordPO recordPO : studyRecordList){  // 遍历查到的内容做处理——添加时间（前端需要）
                LocalDate createTime = recordPO.getCreateTime().toLocalDate(); // 如果是 LocalDateTime
                recordPO.setToDate(ChronoUnit.DAYS.between(createTime, LocalDate.now()));
            }
            return studyRecordList;
        }

        studyRecordList = studyRecordMapper.getTodayList(table);  // 数据库里面没有的，需要获取所有内容，按照艾宾浩斯算法生成
        SimpleEbbinghausReview scheduler = new SimpleEbbinghausReview();  // 创建艾宾浩斯曲线对象

        // 获取当前日期
        LocalDate today = LocalDate.now();

        List<RecordPO> reviewItems = scheduler.getReviewItems(studyRecordList, today);  // 将所有内容按照艾宾浩斯算法进行筛选
        List<Integer> selectedIds = new ArrayList<>();  // 存储已选择的记录的 ID

        for (RecordPO recordPO : reviewItems){  // 遍历已选择的记录,将对应的id取出
            selectedIds.add(Integer.parseInt(recordPO.getId()));
        }
        studyRecordMapper.markSelected(selectedIds,table);  // 将已选择的记录的 selected 字段设置为 1

        System.out.println("我来自后端生成查询");
        return reviewItems;
    }

    @Override
    @Transactional
    public int markReviewed(List<String> ids,String  table) {
        // 目标表不存在时也直接返回 0，避免 SQL 异常
        if (studyRecordMapper.existsTable(table) == 0) {
            return 0;
        }
        if (ids.isEmpty()){  // 如果没有要标记的 ID，则将所有记录的 already_reviewed 字段设置为 0
            studyRecordMapper.clearReviewed(table);
            return 1;
        }
        studyRecordMapper.clearReviewed(table);  // 先清空所有记录的 already_reviewed 字段
        return studyRecordMapper.markReviewed(ids,table);  // 再将要标记的 ID 的 already_reviewed 字段设置为 1
    }


}
