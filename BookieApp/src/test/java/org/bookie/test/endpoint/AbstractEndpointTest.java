package org.bookie.test.endpoint;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;

import org.bookie.app.BookieApplication;
import org.bookie.test.AbstractTest;
import org.bookie.test.conf.WebTestConfiguration;
import org.junit.Before;
import org.junit.Rule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.cli.CliDocumentation;
import org.springframework.restdocs.http.HttpDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.restdocs.snippet.Attributes;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(classes = BookieApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@Import(WebTestConfiguration.class)
public abstract class AbstractEndpointTest extends AbstractTest {

	@Autowired
	private WebApplicationContext wac;

	protected MockMvc mockMvc;

	@Autowired
	protected ObjectMapper objectMapper;

	protected RestDocumentationResultHandler restDocumentationResultHandler;

	@Autowired
	@Rule
	public JUnitRestDocumentation restDocumentation;

	@Before
	public void init() {
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

	protected final FieldDescriptor fieldWithPath(final String name, final String description,
			final String[]... attributes) {
		final FieldDescriptor result = PayloadDocumentation.fieldWithPath(name).description(description);
		for (final String[] attr : attributes) {
			result.attributes(Attributes.key(attr[0]).value(attr[1]));
		}
		return result;
	}
}
