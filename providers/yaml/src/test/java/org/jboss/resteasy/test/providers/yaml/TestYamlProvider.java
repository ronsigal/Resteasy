package org.jboss.resteasy.test.providers.yaml;

import org.junit.AfterClass;
import org.junit.Assert;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.Response;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

public class TestYamlProvider extends BaseResourceTest {

    private static final String TEST_URI = generateURL("/yaml");
    private static Client client;

    @BeforeClass
    public static void beforeClass()
    {
       client = ClientBuilder.newClient();
    }
    
    @AfterClass
    public static void afterClass()
    {
       client.close();
    }
    
    @Before
    public void setUp() {

        addPerRequestResource(YamlResource.class);

    }

    @Test
    public void testGet() throws Exception {

       Builder request = client.target(TEST_URI).request();

       Response response = request.get();
       
       Assert.assertEquals(200, response.getStatus());
       
       Assert.assertEquals("text/x-yaml", response.getHeaderString("Content-Type"));
       
       String s = response.readEntity(String.class);
       
       MyObject o1 = YamlResource.createMyObject();

       String s1 = new Yaml().dump(o1);
       
       Assert.assertEquals(s1, s);

    }

    @Test
    public void testPost() throws Exception {

       Builder request = client.target(TEST_URI).request();
       
       MyObject o1 = YamlResource.createMyObject();

       String s1 = new Yaml().dump(o1);

       Response response = request.post(Entity.entity(s1, "text/x-yaml"));
       
       Assert.assertEquals(200, response.getStatus());
       
       Assert.assertEquals("text/x-yaml", response.getHeaderString("Content-Type"));
       
       Assert.assertEquals(s1, response.readEntity(String.class));

    }

    @Test
    public void testBadPost() throws Exception {

       Builder request = client.target(TEST_URI).request();
       
       Response response = request.post(Entity.entity("---! bad", "text/x-yaml"));
       
       Assert.assertEquals(400, response.getStatus());
       
       response.close();
       
    }

    @Test
    public void testPostList() throws Exception {

        Builder request = client.target(TEST_URI + "/list").request();
        
        List<String> data = Arrays.asList("a", "b", "c");

        String s1 = new Yaml().dump(data).trim();

        Response response = request.post(Entity.entity(s1, "text/x-yaml"));
        
        Assert.assertEquals(200, response.getStatus());

        Assert.assertEquals("text/plain", response.getHeaderString("Content-Type"));

        Assert.assertEquals(s1, response.readEntity(String.class).trim());

    }

}
