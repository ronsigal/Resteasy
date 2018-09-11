package org.jboss.resteasy.test.resource.param.resource;

import javax.ws.rs.ext.ParamConverter;

public class MultiValuedParamDefaultParamConverterParamConverter implements ParamConverter<MultiValuedParamDefaultParamConverterParamConverterClass> {

   @Override
   public MultiValuedParamDefaultParamConverterParamConverterClass fromString(String value) {
      return new MultiValuedParamDefaultParamConverterParamConverterClass(value);
   }

   @Override
   public String toString(MultiValuedParamDefaultParamConverterParamConverterClass value) {
      return value.getS();
   }
}
