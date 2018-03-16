package com.nowcoder.dao;

import com.nowcoder.model.Message;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MessageDao {

    String tableName = " message ";
    String insertFields = " from_id, to_id, content, created_date, has_read, conversation_id ";
    String selectFields = " id," + insertFields;

    @Insert({"insert into", tableName, "(", insertFields, ") values" +
            "(#{fromId},#{toId},#{content},#{createdDate},#{hasRead},#{conversationId})"})
    int addMessage(Message message);

    @Select({"select", selectFields, "from", tableName, "where conversation_id = #{conversationId}" +
            "order by created_date desc limit #{offset},#{limit}"})
    List<Message> getConversationDetail(@Param("conversationId") String conversationId,
                                        @Param("offset") int offset,
                                        @Param("limit") int limit);

    @Select({"select", insertFields, ",count(id) as id from (select", selectFields, "from", tableName,
            "where from_id = #{userId} or to_id = #{userId} order by created_date desc limit 9999999999) " +
                    "tt group by conversation_id order by created_date desc limit #{offset},#{limit}"})
    List<Message> getMessageList(@Param("userId") int userId,
                                 @Param("offset") int offset,
                                 @Param("limit") int limit);

    @Select({"select count(id) from",tableName,"where has_read = 0 and to_id = #{toId} and conversation_id = #{conversationId}"})
    //0代表未读信息,默认就是未读消息0
    int unreadCount(@Param("toId") int toId,
                    @Param("conversationId") String conversationId);

    @Update({"update",tableName,"set has_read = #{hasRead} where to_id =#{toId} and conversation_id = #{conversationId}"})
    int updateHasRead(@Param("hasRead") int hasRead,
                      @Param("toId") int toId,
                      @Param("conversationId") String conversationId);
}
