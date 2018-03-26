package com.nowcoder.dao;

import com.nowcoder.model.Feed;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FeedDao {
    String tableName = " feed ";
    String insertFields = " type, user_id, created_date, data ";
    String selectFields = " id,"+insertFields;

    @Insert({"insert into",tableName,"(",insertFields,") values " +
            "(#{type},#{userId},#{createdDate},#{data})"})
    int addFeed(Feed feed);

    @Select({"select",selectFields,"from",tableName,"where id = #{id}"})
    Feed selectFeedById(int id);

    List<Feed> selectUserFeeds(@Param("maxId") int maxId,
                               @Param("userIds") List<Integer> userIds,
                               @Param("count") int count);
}
