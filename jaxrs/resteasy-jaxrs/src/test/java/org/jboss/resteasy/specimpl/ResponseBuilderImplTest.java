package org.jboss.resteasy.specimpl;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * @author Michiel Meeuwissen
 * @since 3.0.15
 */
public class ResponseBuilderImplTest
{

   @Test
   public void testLastModified() throws Exception
   {
      ResponseBuilderImpl impl = new ResponseBuilderImpl();

      Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z").parse("2015-12-08 15:50:00 GMT");
      impl.lastModified(date);
      assertEquals(impl.build().getLastModified(), date);
      assertEquals(impl.build().getHeaderString("Last-Modified"), "Tue, 08 Dec 2015 15:50:00 GMT");

      // getHeaders is used to actually build the response, so date headers must be formatted correctly in that too
      assertEquals(impl.build().getHeaders().getFirst("Last-Modified"), "Tue, 08 Dec 2015 15:50:00 GMT"); // FAILS in 3.0.13
   }
}
