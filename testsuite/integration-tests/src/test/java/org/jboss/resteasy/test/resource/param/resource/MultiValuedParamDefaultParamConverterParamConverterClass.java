package org.jboss.resteasy.test.resource.param.resource;

import java.util.Comparator;

public class MultiValuedParamDefaultParamConverterParamConverterClass implements Comparable<MultiValuedParamDefaultParamConverterParamConverterClass>  {
   private String s;

   public MultiValuedParamDefaultParamConverterParamConverterClass (String s) {
      this.s = s;
   }

   public String getS() {
      return s;
   }

   @Override
   public int compareTo(MultiValuedParamDefaultParamConverterParamConverterClass o) {
      return s.compareTo(o.getS());
   }
   
   public String saveAsString() {
      return s;
   }
   
   public static Comp COMP = new Comp();
   static class Comp implements Comparator<MultiValuedParamDefaultParamConverterParamConverterClass> {

      @Override
      public int compare(MultiValuedParamDefaultParamConverterParamConverterClass o1, MultiValuedParamDefaultParamConverterParamConverterClass o2) {
         return o1.getS().compareTo(o2.getS());
      }
   }
}
