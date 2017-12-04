package com.crossover.trial.journals.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.crossover.trial.journals.Application;
import com.crossover.trial.journals.model.Subscription;
import com.crossover.trial.journals.model.User;
import com.crossover.trial.journals.service.ServiceException;
import com.crossover.trial.journals.service.UserService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserServiceTest {

	@Autowired
	private UserService userService;
	
	@Test
	public void findById() {
		User user1 = userService.getUserByLoginName("user1").get();
		User user2 = userService.findById(user1.getId());
		
		assertEquals(user1.getId(), user2.getId());
	}	
	
	@Test
	public void subscribeCategorySuccess() {
		User user = userService.getUserByLoginName("user2").get();
		userService.subscribe(user, 1l);

		List<Subscription> subscriptions = user.getSubscriptions();
		assertNotNull(subscriptions);
		assertEquals(1, subscriptions.size());
	}

	@Test
	public void subscribeCategoryFail1() {
		User user = userService.getUserByLoginName("user1").get();
		userService.subscribe(user, 3l);
		
		List<Subscription> subscriptions = user.getSubscriptions();
		assertNotNull(subscriptions);
		assertEquals(1, subscriptions.size());		
	}
	
	@Test(expected = ServiceException.class)
	public void subscribeCategoryFail2() {
		User user = userService.getUserByLoginName("user1").get();
		userService.subscribe(user, 10l);
	}
}
