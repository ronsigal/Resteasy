package org.jboss.resteasy.test;

import org.jboss.resteasy.util.TypeConverter;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * A TypeConverterTest.
 *
 * @author <a href="ryan@damnhandy.com>Ryan J. McDonough</a>
 * @version $Revision: $
 */
public class TypeConverterTest
{

   /**
    * Test method for {@link org.jboss.resteasy.util.TypeConverter#getType(java.lang.Class, java.lang.String)}.
    */
   @Test
   public void testBooleanTypes()
   {
      assertTrue(TypeConverter.getType(Boolean.class, "T"));
      assertTrue(TypeConverter.getType(Boolean.class, "t"));
      assertTrue(TypeConverter.getType(Boolean.class, "Y"));
      assertTrue(TypeConverter.getType(Boolean.class, "y"));
      assertTrue(TypeConverter.getType(Boolean.class, "Yes"));
      assertTrue(TypeConverter.getType(Boolean.class, "YES"));
      assertTrue(TypeConverter.getType(Boolean.class, "TRUE"));
      assertTrue(TypeConverter.getType(Boolean.class, "true"));

      assertFalse(TypeConverter.getType(Boolean.class, "F"));
      assertFalse(TypeConverter.getType(Boolean.class, "f"));
      assertFalse(TypeConverter.getType(Boolean.class, "N"));
      assertFalse(TypeConverter.getType(Boolean.class, "n"));
      assertFalse(TypeConverter.getType(Boolean.class, "No"));
      assertFalse(TypeConverter.getType(Boolean.class, "No"));
      assertFalse(TypeConverter.getType(Boolean.class, "FALSE"));
      assertFalse(TypeConverter.getType(Boolean.class, "False"));
   }

   /**
    * FIXME Comment this
    */
   @Test
   public void testIntegerTypes()
   {
      assertEquals(11, (int)TypeConverter.getType(int.class, "11"));
      assertEquals(11, (int)TypeConverter.getType(Integer.class, "11"));
   }

   /**
    * FIXME Comment this
    */
   @Test
   public void testDoubleTypes()
   {
      assertEquals(20.15d, (double)TypeConverter.getType(double.class, "20.15"), 0.00001d);
      assertEquals(20.15d, (double)TypeConverter.getType(Double.class, "20.15"), 0.00001d);
   }

   /**
    * FIXME Comment this
    */
   @Test
   public void testFloatTypes()
   {
      assertEquals(23.44f, (float)TypeConverter.getType(float.class, "23.44"), 0.00001f);
      assertEquals(23.44f, (float)TypeConverter.getType(Float.class, "23.44"), 0.00001f);
   }

   /**
    * FIXME Comment this
    */
   @Test
   public void testLongTypes()
   {
      assertEquals(23L, (long)TypeConverter.getType(long.class, "23"));
      assertEquals(23L, (long)TypeConverter.getType(Long.class, "23"));
   }

   /**
    * FIXME Comment this
    *
    * @throws Exception
    */
   @Test(expected = IllegalArgumentException.class)
   public void testDate()
   {
      TypeConverter.getType(Date.class, "07/04/2008");
   }

}
