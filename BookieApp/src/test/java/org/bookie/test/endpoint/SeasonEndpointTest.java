package org.bookie.test.endpoint;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
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

import org.bookie.auth.OrganizationWebAuthenticationDetailsSource.OrganizationWebAuthenticationDetails;
import org.bookie.model.Organization;
import org.bookie.model.Place;
import org.bookie.model.Season;
import org.bookie.service.SeasonService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.restdocs.headers.RequestHeadersSnippet;
import org.springframework.restdocs.payload.FieldDescriptor;

public class SeasonEndpointTest extends AbstractEndpointTest {

	@Autowired
	private SeasonService seasonService;

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
	public void createSeasonTest() throws Exception {
		final LocalDate now = LocalDate.now();
		final Date start = Date.from(now.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
		final Date end = Date.from(
				now.withDayOfMonth(1).plusMonths(1).atStartOfDay(ZoneId.systemDefault()).minusMinutes(1).toInstant());

		final Organization org = this.createOrganization("org");

		final Season request = new Season();
		request.setDateStart(start);
		request.setDateEnd(end);
		request.setTimeStart(7 * 60);
		request.setTimeEnd(22 * 60);
		request.setName("season 1");

		final List<FieldDescriptor> requestFields = Arrays.asList(
				this.fieldWithPath("dateStart", "Start of the season"),
				this.fieldWithPath("dateEnd", "End of the season"),
				this.fieldWithPath("timeStart", "Minimal time, when booking can start (\"in minutes\")"),
				this.fieldWithPath("timeEnd", "Maximal time, when booking can end (\"in minutes\")"),
				this.fieldWithPath("name", "User readable name of the season", new String[] { "unique", "Yes" }));

		final List<FieldDescriptor> responseFields = new ArrayList<>();
		responseFields.addAll(Arrays.asList(
				this.fieldWithPath("id", "Season id")));
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
		final Organization org = this.createOrganization("org");

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
		season.setOrganization(org);
		this.seasonService.createOrUpdateSeason(season);

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
						responseFields(this.fieldWithPath("season", "Season entity for current date"),
								this.fieldWithPath("season.dateStart", "Start of the season"),
								this.fieldWithPath("season.dateEnd", "End of the season"),
								this.fieldWithPath("season.timeStart",
										"Minimal time, when booking can start (\"in minutes\")"),
								this.fieldWithPath("season.timeEnd",
										"Maximal time, when booking can end (\"in minutes\")"),
								this.fieldWithPath("season.name", "User readable name of the season",
										new String[] { "unique", "Yes" }),
								this.fieldWithPath("season.id", "Season id"),
								this.fieldWithPath("places", "List of places available during the season"),
								this.fieldWithPath("places[].placeCount", "Number of places with placeType"),
								this.fieldWithPath("places[].placeType", "Place type"))));
	}

	@Test
	public void getByDateTest() throws Exception {
		final Organization org = this.createOrganization("org");

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
		season.setOrganization(org);
		this.seasonService.createOrUpdateSeason(season);

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
						responseFields(this.fieldWithPath("season", "Season entity for current date"),
								this.fieldWithPath("season.dateStart", "Start of the season"),
								this.fieldWithPath("season.dateEnd", "End of the season"),
								this.fieldWithPath("season.timeStart",
										"Minimal time, when booking can start (\"in minutes\")"),
								this.fieldWithPath("season.timeEnd",
										"Maximal time, when booking can end (\"in minutes\")"),
								this.fieldWithPath("season.name", "User readable name of the season",
										new String[] { "unique", "Yes" }),
								this.fieldWithPath("season.id", "Season id"),
								this.fieldWithPath("places", "List of places available during the season"),
								this.fieldWithPath("places[].placeCount", "Number of places with placeType"),
								this.fieldWithPath("places[].placeType", "Place type"))));
	}
}
