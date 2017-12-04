package com.crossover.trial.journals.rest;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.Charset;

import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.crossover.trial.journals.Application;
import com.crossover.trial.journals.service.CurrentUser;
import com.crossover.trial.journals.service.CurrentUserDetailsService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class JournalRestServiceTest {

	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext webApplicationContext;
	
	@Autowired
	private CurrentUserDetailsService currentUserDetailsService;	

	@Before
	public void setup() throws Exception {
		this.mockMvc = MockMvcBuilders
				.webAppContextSetup(webApplicationContext)
				.apply(SecurityMockMvcConfigurers.springSecurity())
				.build();
	}
	
	private void loadUser(String userName) {
		CurrentUser currentUser = currentUserDetailsService.loadUserByUsername(userName);
		
        Authentication authToken = new UsernamePasswordAuthenticationToken (currentUser, currentUser.getPassword(), currentUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);	
	}

	private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
			MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

	@Test
	public void browse() throws Exception {
		loadUser("user1");
		
		mockMvc.perform(get("/rest/journals")).andExpect(status().isOk())
				.andExpect(content().contentType(contentType))
				.andExpect(jsonPath("$[0].id", is(1)))
				.andExpect(jsonPath("$[0].publisher.id", is(1)))
				.andExpect(jsonPath("$[0].category.id", is(3)))
				.andExpect(jsonPath("$[0].name", is("Medicine")))
				.andExpect(jsonPath("$[0].uuid", is("8305d848-88d2-4cbd-a33b-5c3dcc548056")));
	}
	
	@Test
	public void published() throws Exception {
		loadUser("publisher1");

		mockMvc.perform(get("/rest/journals/published")).andExpect(status().isOk())
		.andExpect(content().contentType(contentType))
		.andExpect(jsonPath("$[0].id", is(1)))
		.andExpect(jsonPath("$[0].publisher.id", is(1)))
		.andExpect(jsonPath("$[0].category.id", is(3)))
		.andExpect(jsonPath("$[0].name", is("Medicine")))
		.andExpect(jsonPath("$[0].uuid", is("8305d848-88d2-4cbd-a33b-5c3dcc548056")))
		
		.andExpect(content().contentType(contentType))
		.andExpect(jsonPath("$[1].id", is(2)))
		.andExpect(jsonPath("$[1].publisher.id", is(1)))
		.andExpect(jsonPath("$[1].category.id", is(4)))
		.andExpect(jsonPath("$[1].name", is("Test Journal")))
		.andExpect(jsonPath("$[1].uuid", is("09628d25-ea42-490e-965d-cd4ffb6d4e9d")));
	}
	
	@Test
	public void subscriptions() throws Exception {
		loadUser("user1");

		mockMvc.perform(get("/rest/journals/subscriptions")).andExpect(status().isOk())
		.andExpect(content().contentType(contentType))
		.andExpect(jsonPath("$[0].id", is(1)))
		.andExpect(jsonPath("$[0].name", is("immunology")))
		.andExpect(jsonPath("$[0].active", is(false)))
		
		.andExpect(jsonPath("$[1].id", is(2)))
		.andExpect(jsonPath("$[1].name", is("pathology")))
		.andExpect(jsonPath("$[1].active", is(false)))

		.andExpect(jsonPath("$[2].id", is(3)))
		.andExpect(jsonPath("$[2].name", is("endocrinology")))
		.andExpect(jsonPath("$[2].active", is(true)))

		.andExpect(jsonPath("$[3].id", is(4)))
		.andExpect(jsonPath("$[3].name", is("microbiology")))
		.andExpect(jsonPath("$[3].active", is(false)))

		.andExpect(jsonPath("$[4].id", is(5)))
		.andExpect(jsonPath("$[4].name", is("neurology")))
		.andExpect(jsonPath("$[4].active", is(false)));
	}
	
	@Test
	@Transactional
	public void unpublish() throws Exception {
		loadUser("publisher1");

		mockMvc.perform(delete("/rest/journals/unPublish/1")).andExpect(status().isOk());
	}
	
	@Test
	@Transactional
	public void subscribe() throws Exception {
		loadUser("user1");

		mockMvc.perform(post("/rest/journals/subscribe/1")).andExpect(status().isOk());
	}	
}
