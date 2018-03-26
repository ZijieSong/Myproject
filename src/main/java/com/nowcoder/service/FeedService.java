package com.nowcoder.service;

import com.nowcoder.dao.FeedDao;
import com.nowcoder.model.Feed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedService {
    @Autowired
    FeedDao feedDao;

    public boolean addFeed(Feed feed){
        feedDao.addFeed(feed);
        return feed.getId()>0;
    }

    public Feed getFeedById(int id){
        return feedDao.selectFeedById(id);
    }

    public List<Feed> getUserFeeds(int maxId, List<Integer> userIds, int count){
        return feedDao.selectUserFeeds(maxId,userIds,count);
    }
}
