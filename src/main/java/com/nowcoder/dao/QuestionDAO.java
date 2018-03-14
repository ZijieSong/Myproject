package com.nowcoder.dao;

import com.nowcoder.model.Question;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface QuestionDAO {
    String tableName = " question ";
    String insertFields = " title, content, created_date, user_id, comment_count ";
    String selectFields = "id,"+insertFields;

    @Insert({"insert into",tableName,"(",insertFields,") values" +
            "(#{title},#{content},#{createdDate},#{userId},#{commentCount})"})
    int addQuestion(Question question);

    @Select({"select",selectFields,"from",tableName,"where id =#{id}"})
    Question selectById(int id);

    List<Question> selectLatestQuestions(@Param("userId") int userId,
                                         @Param("offset") int offset,
                                         @Param("limit") int limit);
}
