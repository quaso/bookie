package org.bookie.test.endpoint;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.util.Date;

import org.bookie.auth.OrganizationWebAuthenticationDetailsSource.OrganizationWebAuthenticationDetails;
import org.bookie.model.Organization;
import org.bookie.model.Place;
import org.bookie.model.Season;
import org.bookie.model.User;
import org.json.JSONObject;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

//@ActiveProfiles("hsql")
public class TestAll extends AbstractEndpointTest {

	@Test
	public void create() throws Exception {

		final Organization org = this.createOrganizationRequest();

		// add organization
		this.mockMvc
				.perform(post("/api/organization/").contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON_VALUE).content(this.objectMapper.writeValueAsString(org)))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.id").isNotEmpty());

		final Date start = this.date(LocalDate.of(2016, 1, 1));
		final Date end = this.date(LocalDate.of(2016, 3, 31));

		// create season 1
		final String seasonId = this
				.extractId(
						this.mockMvc
								.perform(post("/api/season/")
										.header(OrganizationWebAuthenticationDetails.HEADER_ORGANIZATION_NAME,
												org.getCode())
										.contentType(MediaType.APPLICATION_JSON)
										.accept(MediaType.APPLICATION_JSON_VALUE)
										.content(this.objectMapper
												.writeValueAsString(this.createSeason(start, end, "season 1"))))
								.andExpect(status().isCreated()).andExpect(jsonPath("$.id").isNotEmpty()).andReturn());

		// create place 1
		final String place1Id = this
				.extractId(
						this.mockMvc
								.perform(post("/api/place/")
										.header(OrganizationWebAuthenticationDetails.HEADER_ORGANIZATION_NAME,
												org.getCode())
										.contentType(MediaType.APPLICATION_JSON)
										.accept(MediaType.APPLICATION_JSON_VALUE)
										.content(this.objectMapper.writeValueAsString(this.createPlace("1", "type1"))))
								.andExpect(status().isCreated()).andExpect(jsonPath("$.id").isNotEmpty()).andReturn());

		// create place 2
		final String place2Id = this
				.extractId(
						this.mockMvc
								.perform(post("/api/place/")
										.header(OrganizationWebAuthenticationDetails.HEADER_ORGANIZATION_NAME,
												org.getCode())
										.contentType(MediaType.APPLICATION_JSON)
										.accept(MediaType.APPLICATION_JSON_VALUE)
										.content(this.objectMapper.writeValueAsString(this.createPlace("2", "type1"))))
								.andExpect(status().isCreated()).andExpect(jsonPath("$.id").isNotEmpty()).andReturn());

		// add place 1 to season 1
		this.mockMvc
				.perform(post("/api/season/place/").param("seasonId", seasonId).param("placeId", place1Id)
						.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isCreated());

		// create user 1
		final String str = "{\"password\":\"pwd\","
				+ this.objectMapper.writeValueAsString(this.createUser("tester")).substring(1);
		final String user1Id = this.extractId(this.mockMvc
				.perform(post("/api/user/").contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON_VALUE).content(str))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.id").isNotEmpty()).andReturn());

		// add user 1 to role admin
		this.mockMvc
				.perform(post("/api/user/role/")
						.header(OrganizationWebAuthenticationDetails.HEADER_ORGANIZATION_NAME, org.getCode())
						.param("userId", user1Id).param("roleName", "ROLE_ADMIN")
						.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isCreated());
	}

	private String extractId(final MvcResult result) throws UnsupportedEncodingException {
		final String contentAsString = result.getResponse().getContentAsString();
		final JSONObject root = new JSONObject(contentAsString);
		return root.getString("id");
	}

	private Organization createOrganizationRequest() {
		final Organization org = new Organization();
		org.setName("org 1");
		org.setEmail("test@test.com");
		org.setPhone("+112346");
		org.setCode("org1");
		return org;
	}

	private Season createSeason(final Date start, final Date end, final String name) {
		final Season season = new Season();
		season.setDateStart(start);
		season.setDateEnd(end);
		season.setTimeStart(7 * 60);
		season.setTimeEnd(22 * 60);
		season.setName(name);
		return season;
	}

	private Place createPlace(final String name, final String type) {
		final Place place = new Place();
		place.setName(name);
		place.setType(type);
		return place;
	}

	private User createUser(final String name) {
		final User user = new User();
		user.setUsername(name);
		user.setName(name);
		user.setSurname("surname");
		user.setPhone("123");
		user.setPassword("pwd");
		return user;
	}
}
