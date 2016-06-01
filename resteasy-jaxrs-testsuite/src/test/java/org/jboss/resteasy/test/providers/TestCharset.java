package org.jboss.resteasy.test.providers;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.Encoded;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.junit.Assert;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for RESTEASY-1066.
 *
 * @author <a href="mailto:ron.sigal@jboss.com">Ron Sigal</a>
 * @date Aug 13, 2014
 */
public class TestCharset
{
	protected static ResteasyDeployment deployment;
	protected static Dispatcher dispatcher;
	protected static Client client;
	protected static final MediaType TEXT_PLAIN_UTF16_TYPE;
	protected static final MediaType WILDCARD_UTF16_TYPE;
	protected static final String TEXT_PLAIN_UTF16 = "text/plain;charset=UTF-16";
	protected static final String WILDCARD_UTF16 = "*/*;charset=UTF-16";
	static
	{
		Map<String, String> params = new HashMap<String, String>();
		params.put("charset", "UTF-16");
		TEXT_PLAIN_UTF16_TYPE = new MediaType("text", "plain", params);
		WILDCARD_UTF16_TYPE = new MediaType("*", "*", params);
	}
	
	public static class Foo
	{
		private String s;
		public Foo(String s)
		{
			this.s = s;
		}
		public String valueOf()
		{
			return s;
		}
		public String toString()
		{
			return s;
		}
	}
	
	@Path("/")
	public static class TestResource
	{
		@POST
		@Path("produces/string/utf16")
		@Consumes("text/plain")
		@Produces(TEXT_PLAIN_UTF16)
		public String stringProducesUtf16(String s)
		{
		   System.out.println("server default charset: " + Charset.defaultCharset());
			System.out.println("s: " + s);
			return s;
		}
		
		@POST
		@Path("accepts/string/default")
		@Consumes("text/plain")
		public String stringAcceptsDefault(String s)
		{
			System.out.println("s: " + s);
			return s;
		}
		
		@POST
		@Path("produces/foo/utf16")
		@Consumes("text/plain")
		@Produces(TEXT_PLAIN_UTF16)
		public Foo fooProducesUtf16(Foo foo)
		{
			System.out.println("foo: " + foo.valueOf());
			return foo;
		}
		
		@POST
		@Path("accepts/foo/default")
		@Consumes("text/plain")
		@Produces("text/plain")
		public Foo fooAcceptsDefault(Foo foo)
		{
			System.out.println("foo: " + foo.valueOf());
			return foo;
		}
		
		@POST
		@Path("accepts/form/default")
		@Produces("application/x-www-form-urlencoded")
		@Consumes("application/x-www-form-urlencoded")
		@Encoded
		public MultivaluedMap<String, String> formAcceptsDefault(MultivaluedMap<String, String> form)
		{
			String s = form.getFirst("title");
			System.out.println("s: " + s);
			return form;
		}
	}
	
	@Before
	public void before() throws Exception
	{
		Hashtable<String,String> initParams = new Hashtable<String,String>();
		Hashtable<String,String> contextParams = new Hashtable<String,String>();
		deployment = EmbeddedContainer.start(initParams, contextParams);
		dispatcher = deployment.getDispatcher();
		deployment.getRegistry().addPerRequestResource(TestResource.class);
		client = ClientBuilder.newClient();
	}
	
	@After
	public void after() throws Exception
	{
	   client.close();
		EmbeddedContainer.stop();
		dispatcher = null;
		deployment = null;
	}
	
	/**
	 * Tests StringTextStar provider, where the charset is unspecified.
	 *
	 * @throws Exception
	 */
	@Test
	public void testStringDefault() throws Exception
	{
      System.out.println("client default charset: " + Charset.defaultCharset());
      Builder builder = client.target(generateURL("/accepts/string/default")).request();
		String str = "La Règle du Jeu";
		System.out.println(str);
		Response response = builder.post(Entity.entity(str, MediaType.WILDCARD_TYPE));
		Assert.assertEquals(200, response.getStatus());
		String entity = response.readEntity(String.class);
		System.out.println("Result: " + entity);
		Assert.assertEquals(str, entity);
	}
	
	/**
	 * Tests StringTextStar provider, where the charset is specified
	 * by the resource method.
	 *
	 * @throws Exception
	 */
	@Test
	public void testStringProducesUtf16() throws Exception
	{
	   Builder builder = client.target(generateURL("/produces/string/utf16")).request();
		String str = "La Règle du Jeu";
		System.out.println(str);
		Response response = builder.post(Entity.entity(str, TEXT_PLAIN_UTF16_TYPE));
		Assert.assertEquals(200, response.getStatus());
		String entity = response.readEntity(String.class);
		System.out.println("Result: " + entity);
		Assert.assertEquals(str, entity);
	}
	
	/**
	 * Tests StringTextStar provider, where the charset is specified
	 * by the Accept header.
	 *
	 * @throws Exception
	 */
	@Test
	public void testStringAcceptsUtf16() throws Exception
	{
	   Builder builder = client.target(generateURL("/accepts/string/default")).request();
	   builder.accept(WILDCARD_UTF16_TYPE);
		String str = "La Règle du Jeu";
		System.out.println(str);
		Response response = builder.post(Entity.entity(str, TEXT_PLAIN_UTF16_TYPE));
		Assert.assertEquals(200, response.getStatus());
		String entity = response.readEntity(String.class);
		System.out.println("Result: " + entity);
		Assert.assertEquals(str, entity);
	}
	
	/**
	 * Tests DefaultTextPlain provider, where the charset is unspecified.
	 *
	 * @throws Exception
	 */
	@Test
	public void testFooDefault() throws Exception
	{
	   Builder builder = client.target(generateURL("/accepts/foo/default")).request();
		Foo foo = new Foo("La Règle du Jeu");
		System.out.println(foo);
		Response response = builder.post(Entity.entity(foo, MediaType.TEXT_PLAIN_TYPE));
		Assert.assertEquals(200, response.getStatus());
		String entity = response.readEntity(String.class);
		System.out.println("Result: " + entity);
		Assert.assertEquals(foo.valueOf(), entity);
	}
	
	/**
	 * Tests DefaultTextPlain provider, where the charset is specified
	 * by the resource method.
	 *
	 * @throws Exception
	 */
	@Test
	public void testFooProducesUtf16() throws Exception
	{
      Builder builder = client.target(generateURL("/produces/foo/utf16")).request();
		Foo foo = new Foo("La Règle du Jeu");
		System.out.println(foo);
		Response response = builder.post(Entity.entity(foo, TEXT_PLAIN_UTF16_TYPE));
		Assert.assertEquals(200, response.getStatus());
		String entity = response.readEntity(String.class);
		System.out.println("Result: " + entity);
		Assert.assertEquals(foo.valueOf(), entity);
	}
	
	/**
	 * Tests DefaultTextPlain provider, where the charset is specified
	 * by the Accept header.
	 *
	 * @throws Exception
	 */
	@Test
	public void testFooAcceptsUtf16() throws Exception
	{
      Builder builder = client.target(generateURL("/accepts/foo/default")).request();
      builder.accept(TEXT_PLAIN_UTF16_TYPE);
		Foo foo = new Foo("La Règle du Jeu");
		System.out.println(foo);
		Response response = builder.post(Entity.entity(foo, TEXT_PLAIN_UTF16_TYPE));
		Assert.assertEquals(200, response.getStatus());
		String entity = response.readEntity(String.class);
		System.out.println("Result: " + entity);
		Assert.assertEquals(foo.valueOf(), entity);
	}
	
	@Test
	public void testFormDefault() throws Exception
	{
      Builder builder = client.target(generateURL("/accepts/form/default")).request();
      Response response = builder.post(Entity.form(new Form("title", "La Règle du Jeu")));
      Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
		Assert.assertEquals("title=La Règle du Jeu", response.readEntity(String.class));
	}
}