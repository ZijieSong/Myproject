package com.nowcoder.dao;

import com.nowcoder.model.Message;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MessageDao {

    String tableName = " message ";
    String insertFields = " from_id, to_id, content, created_date, has_read, conversation_id ";
    String selectFields = " id,"+insertFields;

    @Insert({"insert into", tableName, "(", insertFields,") values" +
            "(#{fromId},#{toId},#{content},#{createdDate},#{hasRead},#{conversationId})"})
    int addMessage(Message message);

    @Select({"select",selectFields,"from",tableName,"where conversation_id = #{conversationId}" +
            "order by created_date desc limit #{offset},#{limit}"})
    List<Message> getConversationDetail(@Param("conversationId") String conversationId,
                                        @Param("offset") int offset,
                                        @Param("limit") int limit);
}
