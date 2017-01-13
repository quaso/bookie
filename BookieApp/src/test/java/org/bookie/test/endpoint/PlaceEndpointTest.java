package org.bookie.test.endpoint;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.bookie.auth.OrganizationWebAuthenticationDetailsSource.OrganizationWebAuthenticationDetails;
import org.bookie.model.Organization;
import org.bookie.model.Place;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.headers.RequestHeadersSnippet;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.annotation.DirtiesContext;

import com.fasterxml.jackson.core.JsonProcessingException;

public class PlaceEndpointTest extends AbstractEndpointTest {

	private RequestHeadersSnippet requestHeaders;

	@Override
	@Before
	public void init() {
		super.init();
		this.requestHeaders = requestHeaders(
				headerWithName(OrganizationWebAuthenticationDetails.HEADER_ORGANIZATION_NAME)
						.description("Organization name")
						.attributes(key("default").value(""))
						.attributes(key("required").value("Yes")),
				headerWithName("Content-Type").description("Payload content type")
						.attributes(key("default").value(MediaType.APPLICATION_JSON_VALUE))
						.attributes(key("required").value("Yes")));
	}

	@Test
	public void createPlaceTest() throws JsonProcessingException, Exception {
		final Organization org = this.createOrganization("org");

		final Place request = new Place();
		request.setName("place 1");
		request.setType("type1");

		final List<FieldDescriptor> requestFields = Arrays.asList(
				this.fieldWithPath("name", "Place name"),
				this.fieldWithPath("type", "Place type"),
				this.fieldWithPath("enabled",
						"Indicates if the place is enabled. Disabled places cannot be added to a season.",
						new String[] { "default", "true" }));

		final List<FieldDescriptor> responseFields = new ArrayList<>();
		responseFields.addAll(Arrays.asList(
				this.fieldWithPath("id", "Season id"),
				this.fieldWithPath("organization", "Organization entity")));
		responseFields.addAll(requestFields);

		this.mockMvc
				.perform(post("/api/place/")
						.header(OrganizationWebAuthenticationDetails.HEADER_ORGANIZATION_NAME, org.getName())
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON_VALUE)
						.content(this.objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").isNotEmpty())
				.andDo(this.restDocumentationResultHandler.document(
						this.requestHeaders,
						requestFields(requestFields),
						responseFields(responseFields)));
	}

	@Test
	@Transactional(value = TxType.NOT_SUPPORTED)
	@DirtiesContext
	public void createDuplicatePlaceTest() throws JsonProcessingException, Exception {
		final Organization org = this.createOrganization("org");

		final Place place = new Place();
		place.setName("place 1");
		place.setType("type1");

		this.mockMvc
				.perform(post("/api/place/")
						.header(OrganizationWebAuthenticationDetails.HEADER_ORGANIZATION_NAME, org.getName())
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON_VALUE)
						.content(this.objectMapper.writeValueAsString(place)));

		final Place request = new Place();
		request.setName(place.getName());
		request.setType(place.getType());

		this.mockMvc
				.perform(post("/api/place/")
						.header(OrganizationWebAuthenticationDetails.HEADER_ORGANIZATION_NAME, org.getName())
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON_VALUE)
						.content(this.objectMapper.writeValueAsString(request)))
				.andExpect(status().isConflict())
				.andDo(this.restDocumentationResultHandler.document(
						this.requestHeaders));
	}

}
