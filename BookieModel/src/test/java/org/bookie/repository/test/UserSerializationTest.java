package org.bookie.repository.test;

import java.io.IOException;

import org.bookie.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;

@RunWith(BlockJUnit4ClassRunner.class)

public class UserSerializationTest {

	private ObjectMapper objectMapper;

	@Before
	public void init() {
		this.objectMapper = new ObjectMapper();
	}

	@Test
	public void testSerialization() throws JsonProcessingException {
		final User user = new User();
		user.setEmail("em1");
		user.setEnabled(true);
		user.setFailedLogins(10);
		user.setId("id1");
		user.setName("name1");
		user.setPassword("pwd");
		user.setPhone("ph1");
		user.setSurname("sur1");
		user.setUsername("un1");
		user.setVerified(false);

		final String string = this.objectMapper.writeValueAsString(user);
		Assert.assertTrue(string.contains("\"email\""));
		Assert.assertTrue(string.contains("\"enabled\""));
		Assert.assertTrue(string.contains("\"failedLogins\""));
		Assert.assertTrue(string.contains("\"id\""));
		Assert.assertTrue(string.contains("\"name\""));
		// password must not be serialized
		Assert.assertFalse(string.contains("\"password\""));
		Assert.assertTrue(string.contains("\"phone\""));
		Assert.assertTrue(string.contains("\"surname\""));
		Assert.assertTrue(string.contains("\"username\""));
		Assert.assertTrue(string.contains("\"verified\""));
	}

	@Test
	public void testDeserialization() throws IOException {
		String string = "{\"id\":\"id1\",\"username\":\"un1\",\"name\":\"name1\",\"surname\":\"sur1\",\"phone\":\"ph1\",\"email\":\"em1\",\"enabled\":true,\"verified\":true,\"failedLogins\":10,\"password\":\"pwd\"}";
		try {
			this.objectMapper.readValue(string, User.class);
		} catch (final UnrecognizedPropertyException ex) {
			Assert.assertTrue(ex.getPropertyName().equals("failedLogins"));
		}

		string = "{\"id\":\"id1\",\"username\":\"un1\",\"name\":\"name1\",\"surname\":\"sur1\",\"phone\":\"ph1\",\"email\":\"em1\",\"enabled\":true,\"verified\":true,\"password\":\"pwd\"}";
		final User user = this.objectMapper.readValue(string, User.class);
		Assert.assertNotNull(user.getEmail());
		Assert.assertTrue(user.isEnabled());
		Assert.assertNotNull(user.getId());
		Assert.assertNotNull(user.getName());
		Assert.assertNotNull(user.getPassword());
		Assert.assertNotNull(user.getPhone());
		Assert.assertNotNull(user.getSurname());
		Assert.assertNotNull(user.getUsername());
		Assert.assertTrue(user.isVerified());
	}

}
