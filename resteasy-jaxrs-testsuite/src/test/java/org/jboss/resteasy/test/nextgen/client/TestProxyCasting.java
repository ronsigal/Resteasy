package org.jboss.resteasy.test.nextgen.client;

import org.jboss.resteasy.test.BaseResourceTest;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.jboss.resteasy.client.jaxrs.internal.proxy.ResteasyClientProxy;

import static org.junit.Assert.assertEquals;

public class TestProxyCasting extends BaseResourceTest
{
	public static interface Nothing
	{
	}
	
	public static interface InterfaceA
	{
		@GET
		@Path("foo")
		@Produces("text/plain")
		String getFoo();
	}

	public static interface InterfaceB
	{
		@GET
		@Path("bar")
		@Produces("text/plain")
		String getBar();
	}

	@Path("/foobar")
	public static class FooBarImpl implements InterfaceA, InterfaceB, Nothing
	{
		@Override
		public String getFoo()
		{
			return "FOO";
		}

		@Override
		public String getBar()
		{
			return "BAR";
		}
	}

	@Before
	public void setUp() throws Exception
	{
		addPerRequestResource(FooBarImpl.class);
	}

	@Test
	public void testSubresourceProxy() throws Exception
	{
		InterfaceA a = TestPortProvider.createProxy(InterfaceA.class, "/foobar");
		assertEquals("FOO", a.getFoo());
		InterfaceB b = ((ResteasyClientProxy) a).as(InterfaceB.class);
		assertEquals("BAR", b.getBar());
	}
}
