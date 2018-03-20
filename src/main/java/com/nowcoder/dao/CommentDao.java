package com.nowcoder.dao;

import com.nowcoder.model.Comment;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CommentDao {
    String tableName = " comment ";
    String insertFields = " content, entity_id, entity_type, created_date, user_id, status ";
    String selectFields =" id,"+insertFields;

    @Insert({"insert into", tableName, "(",insertFields,") " +
            "values(#{content},#{entityId},#{entityType},#{createdDate},#{userId},#{status})"})
    int addComment(Comment comment);

    @Select({"select",selectFields,"from",tableName,
            "where entity_id = #{entityId} and entity_type =#{entityType} order by created_date desc"})
    List<Comment> selectByEntity(@Param("entityId") int entityId, @Param("entityType") int entityType);

    @Select({"select count(id) from",tableName,"where entity_id = #{entityId} and entity_type =#{entityType}"})
    int getCommentCount(@Param("entityId") int entityId, @Param("entityType") int entityType);

    @Update({"update",tableName,"set status = #{status} where id = #{id}"})
    int updateStatusById(@Param("status") int status, @Param("id") int id);

    @Update({"update",tableName,"set status = #{status} where entity_id = #{entityId} and entity_type =#{entityType"})
    int updateStatusByEntity(@Param("status") int status,@Param("entityId") int entityId, @Param("entityType") int entityType);

    @Select({"select",selectFields,"from",tableName,"where id = #{commentId}"})
    Comment getCommentById(@Param("commentId") int commentId);
}
