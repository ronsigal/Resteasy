package org.jboss.resteasy.test.providers.multipart;

import org.junit.Assert;

import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartInput;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.test.BaseResourceTest;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

/**
 * @author Attila Kiraly
 */
public class InputPartDefaultContentTypeEncodingOverwriteTest extends BaseResourceTest {
	protected static final String TEXT_PLAIN_WITH_CHARSET_UTF_8 = "text/plain; charset=utf-8";

	@Path("/mime")
	public static class MyService {

		@POST
		@Consumes(MediaType.MULTIPART_FORM_DATA)
		@Produces(MediaType.TEXT_PLAIN)
		public String sendDefaultContentType(MultipartInput input) {
			return input.getParts().get(0).getMediaType().toString();
		}
	}

	@Provider
	public static class ContentTypeSetterPreProcessorInterceptor implements ContainerRequestFilter {

      @Override
      public void filter(ContainerRequestContext requestContext) throws IOException
      {
         HttpRequest request = ResteasyProviderFactory.getContextData(HttpRequest.class);
         request.setAttribute(InputPart.DEFAULT_CONTENT_TYPE_PROPERTY, TEXT_PLAIN_WITH_CHARSET_UTF_8);
      }
	}

	@Before
	public void setUp() throws Exception {
		dispatcher.getRegistry().addPerRequestResource(MyService.class);
		dispatcher.getProviderFactory().registerProvider(
				ContentTypeSetterPreProcessorInterceptor.class, false);
	}

	private static final String TEST_URI = TestPortProvider.generateURL("");

	@Test
	public void testContentType() throws Exception {
		String message = "--boo\r\n"
				+ "Content-Disposition: form-data; name=\"foo\"\r\n"
				+ "Content-Transfer-Encoding: 8bit\r\n\r\n" + "bar\r\n"
				+ "--boo--\r\n";
		
		Builder request = ClientBuilder.newClient().target(TEST_URI + "/mime").request();
		Response response = request.post(Entity.entity(message.getBytes(), "multipart/form-data; boundary=boo"));
		Assert.assertEquals("Status code is wrong.", 20, response.getStatus() / 10);
		Assert.assertEquals("Response text is wrong", 
		                    MediaType.valueOf(TEXT_PLAIN_WITH_CHARSET_UTF_8),
		                    MediaType.valueOf(response.readEntity(String.class)));
	}
}
