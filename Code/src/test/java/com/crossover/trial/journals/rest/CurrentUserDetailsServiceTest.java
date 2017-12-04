package com.crossover.trial.journals.rest;

import static org.junit.Assert.assertNotNull;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.crossover.trial.journals.Application;
import com.crossover.trial.journals.service.CurrentUser;
import com.crossover.trial.journals.service.CurrentUserDetailsService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CurrentUserDetailsServiceTest {

	@Autowired
	private CurrentUserDetailsService currentUserDetailsService;
	
	@Test
	public void loadUserByUsernameSuccess() {
		CurrentUser currentUser = currentUserDetailsService.loadUserByUsername("user1");
		assertNotNull(currentUser);
		assertNotNull(currentUser.getUser());
		assertNotNull(currentUser.getId());
		assertNotNull(currentUser.getRole());
	}	

	@Test(expected = UsernameNotFoundException.class)
	public void loadUserByUsernameFail() {
		currentUserDetailsService.loadUserByUsername("user10");
	}	
}
