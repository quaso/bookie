package org.bookie.test.endpoint;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.bookie.auth.OrganizationWebAuthenticationDetailsSource.OrganizationWebAuthenticationDetails;
import org.bookie.model.Organization;
import org.bookie.model.Place;
import org.bookie.model.Season;
import org.bookie.model.SeasonPlace;
import org.bookie.repository.PlaceRepository;
import org.bookie.repository.SeasonPlaceRepository;
import org.bookie.service.OrganizationService;
import org.bookie.service.SeasonService;
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
import org.springframework.restdocs.headers.RequestHeadersSnippet;
import org.springframework.restdocs.http.HttpDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Import(WebTestConfiguration.class)
@ActiveProfiles("test")
@Transactional
public class SeasonEndpointTest {

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	private RestDocumentationResultHandler restDocumentationResultHandler;

	@Autowired
	private OrganizationService organizationService;

	@Autowired
	private SeasonService seasonService;

	@Autowired
	private PlaceRepository placeRepository;

	@Autowired
	private SeasonPlaceRepository seasonPlaceRepository;

	@Autowired
	@Rule
	public JUnitRestDocumentation restDocumentation;

	private RequestHeadersSnippet requestHeaders;

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
	public void createSeasonTest() throws Exception {
		final LocalDate now = LocalDate.now();
		final Date start = Date.from(now.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
		final Date end = Date.from(
				now.withDayOfMonth(1).plusMonths(1).atStartOfDay(ZoneId.systemDefault()).minusMinutes(1).toInstant());

		final Organization org = new Organization();
		org.setName("org");
		this.organizationService.createOrganization(org);

		final Season request = new Season();
		request.setDateStart(start);
		request.setDateEnd(end);
		request.setTimeStart(7 * 60);
		request.setTimeEnd(22 * 60);
		request.setName("season 1");
		request.setTypes("aaa,bbb");

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
				.perform(post("/api/season/")
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
	public void getCurrentTest() throws Exception {
		final Organization org = new Organization();
		org.setName("org");
		this.organizationService.createOrganization(org);

		final LocalDate now = LocalDate.now();
		final Date start = Date.from(now.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
		final Date end = Date.from(
				now.withDayOfMonth(1).plusMonths(1).atStartOfDay(ZoneId.systemDefault()).minusMinutes(1).toInstant());

		final Season season = new Season();
		season.setDateStart(start);
		season.setDateEnd(end);
		season.setTimeStart(7 * 60);
		season.setTimeEnd(22 * 60);
		season.setName("season 1");
		season.setTypes("aaa,bbb");
		season.setOrganization(org);
		this.seasonService.createSeason(season);

		final Place t1 = this.createPlace("1", "aaa", org);
		final Place t2 = this.createPlace("2", "aaa", org);
		final Place t3 = this.createPlace("3", "aaa", org);
		final Place s1 = this.createPlace("1", "bbb", org);
		final Place s2 = this.createPlace("2", "bbb", org);

		this.createSeasonPlace(season, t1);
		this.createSeasonPlace(season, t2);
		this.createSeasonPlace(season, t3);
		this.createSeasonPlace(season, s1);
		this.createSeasonPlace(season, s2);

		this.mockMvc
				.perform(get("/api/season/")
						.header(OrganizationWebAuthenticationDetails.HEADER_ORGANIZATION_NAME, org.getName())
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk())
				.andDo(this.restDocumentationResultHandler.document(
						this.requestHeaders,
						responseFields(fieldWithPath("season").description("Season entity for current date"),
								fieldWithPath("season.dateStart").description("Start of the season"),
								fieldWithPath("season.dateEnd").description("End of the season"),
								fieldWithPath("season.timeStart")
										.description("Minimal time, when booking can start (\"in minutes\")"),
								fieldWithPath("season.timeEnd")
										.description("Maximal time, when booking can end (\"in minutes\")"),
								fieldWithPath("season.name").description("User readable name of the season")
										.attributes(key("unique").value("Yes")),
								fieldWithPath("season.types")
										.description("comma separated list of place types supported in the season"),
								fieldWithPath("season.id").description("Season id"),
								fieldWithPath("places").description("List of places available during the season"),
								fieldWithPath("places[].placeCount").description("Number of places with placeType"),
								fieldWithPath("places[].placeType").description("Place type"))));
	}

	@Test
	public void getByDateTest() throws Exception {
		final Organization org = new Organization();
		org.setName("org");
		this.organizationService.createOrganization(org);

		final LocalDate now = LocalDate.now();
		final Date start = Date
				.from(now.minusMonths(3).withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
		final Date end = Date.from(
				now.minusMonths(1).withDayOfMonth(1).plusMonths(1).atStartOfDay(ZoneId.systemDefault()).minusMinutes(1)
						.toInstant());

		final Season season = new Season();
		season.setDateStart(start);
		season.setDateEnd(end);
		season.setTimeStart(7 * 60);
		season.setTimeEnd(22 * 60);
		season.setName("season 1");
		season.setTypes("aaa,bbb");
		season.setOrganization(org);
		this.seasonService.createSeason(season);

		final Place t1 = this.createPlace("1", "aaa", org);
		final Place t2 = this.createPlace("2", "aaa", org);
		final Place t3 = this.createPlace("3", "aaa", org);
		final Place s1 = this.createPlace("1", "bbb", org);
		final Place s2 = this.createPlace("2", "bbb", org);

		this.createSeasonPlace(season, t1);
		this.createSeasonPlace(season, t2);
		this.createSeasonPlace(season, t3);
		this.createSeasonPlace(season, s1);
		this.createSeasonPlace(season, s2);

		this.mockMvc
				.perform(get("/api/season/{date}",
						DateTimeFormatter.BASIC_ISO_DATE.format(LocalDate.now().minusMonths(2)))
								.header(OrganizationWebAuthenticationDetails.HEADER_ORGANIZATION_NAME,
										org.getName())
								.contentType(MediaType.APPLICATION_JSON)
								.accept(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(status().isOk())
				.andDo(this.restDocumentationResultHandler.document(
						this.requestHeaders,
						pathParameters(
								parameterWithName("date")
										.description("Date in ISO-8601 basic local date format (yyyyMMdd)")),
						responseFields(fieldWithPath("season").description("Season entity for current date"),
								fieldWithPath("season.dateStart").description("Start of the season"),
								fieldWithPath("season.dateEnd").description("End of the season"),
								fieldWithPath("season.timeStart")
										.description("Minimal time, when booking can start (\"in minutes\")"),
								fieldWithPath("season.timeEnd")
										.description("Maximal time, when booking can end (\"in minutes\")"),
								fieldWithPath("season.name").description("User readable name of the season")
										.attributes(key("unique").value("Yes")),
								fieldWithPath("season.types")
										.description("comma separated list of place types supported in the season"),
								fieldWithPath("season.id").description("Season id"),
								fieldWithPath("places").description("List of places available during the season"),
								fieldWithPath("places[].placeCount").description("Number of places with placeType"),
								fieldWithPath("places[].placeType").description("Place type"))));
	}

	private Place createPlace(final String name, final String type, final Organization organization) {
		final Place place = new Place();
		place.setName(name);
		place.setType(type);
		place.setOrganization(organization);
		this.placeRepository.save(place);
		return place;
	}

	private void createSeasonPlace(final Season season, final Place place) {
		final SeasonPlace sp = new SeasonPlace();
		sp.setSeason(season);
		sp.setPlace(place);
		this.seasonPlaceRepository.save(sp);
	}
}
