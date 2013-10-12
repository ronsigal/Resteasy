package org.jboss.resteasy.test.nextgen.validation;

import java.util.Locale;

import org.jboss.byteman.contrib.bmunit.BMUnitRunner;
import org.junit.runner.RunWith;

/**
 * Unit test for RESTEASY-937
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Oct 3, 2013
 */
@RunWith(BMUnitRunner.class)
public class TestValidationI18NLocal_es_ES extends TestValidationI18NLocal
{
   @Override
   Locale getLocale()
   {
      return new Locale("es", "ES");
   }
}
