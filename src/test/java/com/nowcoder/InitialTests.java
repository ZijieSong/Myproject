package com.nowcoder;

import com.nowcoder.dao.QuestionDAO;
import com.nowcoder.dao.UserDAO;
import com.nowcoder.model.Question;
import com.nowcoder.model.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Date;
import java.util.List;
import java.util.Random;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WendaApplication.class)
@Sql("/init-schema.sql")
public class InitialTests {

    @Autowired
    UserDAO userDAO;
    @Autowired
    QuestionDAO questionDAO;

	@Test
	public void contextLoads() {
        Random random = new Random();
        Date date = new Date();
		for(int i =0; i<11; i++){
		    User user = new User();
		    user.setPassword("");
		    user.setSalt("");
		    user.setName(String.format("user%d",i));
		    user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png",random.nextInt(1000)));
		    userDAO.addUser(user);

		    user.setPassword("zz");
		    userDAO.updatePassword(user);

		    date.setTime(date.getTime()+1000*3600*i);
            Question question = new Question();
            question.setContent(String.format("blablalalalalalal %d",i));
            question.setTitle(String.format("Title%d",i));
            question.setUserId(i+1);
            question.setCommentCount(i);
            question.setCreatedDate(date);
            questionDAO.addQuestion(question);
        }

//        Assert.assertEquals("zz",userDAO.selectById(1).getPassword());
//        userDAO.deleteById(1);
//        Assert.assertNull(userDAO.selectById(1));

        List<Question> questions = questionDAO.selectLatestQuestions(0,0,5);
        for(Question question:questions)
            System.out.println(question.getTitle());
    }

}
