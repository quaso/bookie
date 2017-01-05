package org.bookie.test.endpoint;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.bookie.model.Organization;
import org.bookie.model.Season;
import org.bookie.repository.OrganizationRepository;
import org.bookie.test.conf.WebTestConfiguration;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.cli.CliDocumentation;
import org.springframework.restdocs.http.HttpDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Import(WebTestConfiguration.class)
public class SeasonEndpointTest {

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	private RestDocumentationResultHandler restDocumentationResultHandler;

	@Autowired
	private OrganizationRepository organizationRepository;

	@Autowired
	@Rule
	public JUnitRestDocumentation restDocumentation;

	@Before
	public void setUp() {
		this.restDocumentationResultHandler = document("{method-name}", preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()));

		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac)
				.apply(documentationConfiguration(this.restDocumentation).snippets().withDefaults(
						CliDocumentation.curlRequest(), HttpDocumentation.httpRequest(),
						HttpDocumentation.httpResponse()))
				.alwaysDo(this.restDocumentationResultHandler)
				.build();

		this.objectMapper.setSerializationInclusion(Include.NON_NULL);
	}

	@Test
	public void createSeasonTest() throws Exception {
		final LocalDate now = LocalDate.now();
		final Date start = Date.from(now.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
		final Date end = Date.from(
				now.withDayOfMonth(1).plusMonths(1).atStartOfDay(ZoneId.systemDefault()).minusMinutes(1).toInstant());

		final Organization org = new Organization();
		org.setName("org");
		this.organizationRepository.save(org);

		final Season request = new Season();
		request.setDateStart(start);
		request.setDateEnd(end);
		request.setTimeStart(7 * 60);
		request.setTimeEnd(22 * 60);
		request.setName("season 1");
		request.setTypes("aaa");

		final List<FieldDescriptor> requestFields = Arrays.asList(
				fieldWithPath("dateStart").description("Start of the season"),
				fieldWithPath("dateEnd").description("End of the season"),
				fieldWithPath("timeStart")
						.description("Minimal time, when booking can start (\"in minutes\")"),
				fieldWithPath("timeEnd")
						.description("Maximal time, when booking can end (\"in minutes\")"),
				fieldWithPath("name").description("User readable name of the season")
						.attributes(key("unique").value("Yes")),
				fieldWithPath("types").description("comma separated list of place types supported in the season"));

		final List<FieldDescriptor> responseFields = new ArrayList<>();
		responseFields.addAll(Arrays.asList(
				fieldWithPath("id").description("Season id"),
				fieldWithPath("organization").description("Organization entity")));
		responseFields.addAll(requestFields);

		this.mockMvc
				.perform(post("/api/season/{organizationName}", org.getName())
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON_VALUE)
						.content(this.objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").isNotEmpty())
				.andDo(this.restDocumentationResultHandler.document(
						requestHeaders(headerWithName("Content-Type")
								.description("Payload content type")
								.attributes(key("default").value("application/json"))
								.attributes(key("required").value("Yes"))),
						requestFields(requestFields),
						responseFields(responseFields)));
	}

	@Test
	public void getCurrentTest() {
	}

	@Test
	public void getByDateTest() {

	}
}
