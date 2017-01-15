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

import org.bookie.model.Organization;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.headers.RequestHeadersSnippet;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.annotation.DirtiesContext;

import com.fasterxml.jackson.core.JsonProcessingException;

public class OrganizationEndpointTest extends AbstractEndpointTest {

	private RequestHeadersSnippet requestHeaders;

	@Override
	@Before
	public void init() {
		super.init();
		this.requestHeaders = requestHeaders(
				headerWithName("Content-Type").description("Payload content type")
						.attributes(key("default").value(MediaType.APPLICATION_JSON_VALUE))
						.attributes(key("required").value("Yes")));
	}

	@Test
	public void createOrganizationTest() throws JsonProcessingException, Exception {
		final Organization request = new Organization();
		request.setName("org 1");
		request.setEmail("test@test.com");
		request.setPhone("+112346");

		final List<FieldDescriptor> requestFields = Arrays.asList(
				this.fieldWithPath("name", "Organization name"),
				this.fieldWithPath("email", "Email address to contact organization"),
				this.fieldWithPath("phone", "Phone number to contact organization"));

		final List<FieldDescriptor> responseFields = new ArrayList<>();
		responseFields.addAll(Arrays.asList(
				this.fieldWithPath("id", "Organization id")));
		responseFields.addAll(requestFields);

		this.mockMvc
				.perform(post("/api/organization/")
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
	public void createDuplicateOrganizationTest() throws JsonProcessingException, Exception {
		final Organization org = new Organization();
		org.setName("org 1");
		org.setEmail("test@test.com");
		org.setPhone("+112346");
		org.setCode("org1");

		this.mockMvc
				.perform(post("/api/organization/")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON_VALUE)
						.content(this.objectMapper.writeValueAsString(org)));

		final Organization request = new Organization();
		request.setName(org.getName());
		request.setCode(org.getCode());

		this.mockMvc
				.perform(post("/api/organization/")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON_VALUE)
						.content(this.objectMapper.writeValueAsString(request)))
				.andExpect(status().is5xxServerError())
				.andDo(this.restDocumentationResultHandler.document(
						this.requestHeaders));
	}

}
