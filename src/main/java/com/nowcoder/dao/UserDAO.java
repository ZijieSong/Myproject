package com.nowcoder.dao;

import com.nowcoder.model.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserDAO {
    String tableName = " user ";
    String insertFields = " name, password, salt, head_url ";
    String selectFields = " id, name, password, salt, head_url ";

    @Insert({"insert into ",tableName,"(",insertFields,") values " +
            "(#{name},#{password},#{salt},#{headUrl})"})
    int addUser(User user);

    @Select({"select",selectFields,"from",tableName,"where id =#{id}"})
    User selectById(int id);

    @Update({"update",tableName,"set password = #{password} " +
            "where id = #{id}"})
    void updatePassword(User user);

    @Delete({"delete from",tableName,"where id = #{id}"})
    void deleteById(int id);

    @Select({"select", selectFields,"from",tableName,"where name = #{name}"})
    User selectByName(String name);
}
