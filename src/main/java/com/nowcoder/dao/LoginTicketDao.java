package com.nowcoder.dao;

import com.nowcoder.model.LoginTicket;
import org.apache.ibatis.annotations.*;

@Mapper
public interface LoginTicketDao {
    String tableName = " login_ticket ";
    String insertFields = " user_id,ticket,expired,status ";
    String selectFields = " id,"+insertFields;

    @Insert({"insert into",tableName,"(",insertFields,") values" +
            "(#{userId},#{ticket},#{expired},#{status})"})
    int addTicket(LoginTicket loginTicket);

    @Select({"select",selectFields,"from",tableName,"where ticket = #{ticket}"})
    LoginTicket selectByTicket(String ticket);

    @Update({"update",tableName,"set status = #{status} where ticket = #{ticket}"})
    void updateStatus(@Param("status") int status, @Param("ticket") String ticket);
}
