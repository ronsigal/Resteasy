package org.jboss.resteasy.test.resource.param.resource;

import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import javax.ws.rs.GET;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.Path;

import org.jboss.resteasy.annotations.Regex;
import org.jboss.resteasy.annotations.Separator;

@Path("matrix")
public interface MultiValuedParamDefaultParamConverterMatrixResourceIntf {

   //////////////////////////////////
   // MultiValuedParamDefaultParamConverterConstructorClass
   
   @Path("constructor/separator/list")
   @GET
   public String matrixConstructorSeparatorList(@MatrixParam("m") @Separator("-") List<MultiValuedParamDefaultParamConverterConstructorClass> list);
 
   @Path("constructor/separator/set")
   @GET
   public String matrixConstructorSeparatorSet(@MatrixParam("m") @Separator("-") Set<MultiValuedParamDefaultParamConverterConstructorClass> set);

   @Path("constructor/separator/sortedset")
   @GET
   public String matrixConstructorSeparatorSortedSet(@MatrixParam("m") @Separator("-") SortedSet<MultiValuedParamDefaultParamConverterConstructorClass> set);
   
   @Path("constructor/regex/list")
   @GET
   public String matrixConstructorRegexList(@MatrixParam("m") @Regex("([^-]+)") List<MultiValuedParamDefaultParamConverterConstructorClass> list);
   
   @Path("constructor/regex/set")
   @GET
   public String matrixConstructorRegexSet(@MatrixParam("m") @Regex("([^-]+)") Set<MultiValuedParamDefaultParamConverterConstructorClass> set);
   
   @Path("constructor/regex/sortedset")
   @GET
   public String matrixConstructorRegexSortedSet(@MatrixParam("m") @Regex("([^-]+)") SortedSet<MultiValuedParamDefaultParamConverterConstructorClass> set);
   
   @Path("constructor/default/list")
   @GET
   public String matrixConstructorDefaultList(@MatrixParam("m") List<MultiValuedParamDefaultParamConverterConstructorClass> list);
   
   @Path("constructor/default/set")
   @GET
   public String matrixConstructorDefaultSet(@MatrixParam("m") Set<MultiValuedParamDefaultParamConverterConstructorClass> set);
   
   @Path("constructor/default/sortedset")
   @GET
   public String matrixConstructorDefaultSortedSet(@MatrixParam("m") SortedSet<MultiValuedParamDefaultParamConverterConstructorClass> set);
   
   //////////////////////////////////
   // MultiValuedParamDefaultParamConverterValueOfClass
   
   @Path("valueOf/separator/list")
   @GET
   public String matrixValueOfSeparatorList(@MatrixParam("m") @Separator("-") List<MultiValuedParamDefaultParamConverterValueOfClass> list);
   
   @Path("valueOf/separator/set")
   @GET
   public String matrixValueOfSeparatorSet(@MatrixParam("m") @Separator("-") Set<MultiValuedParamDefaultParamConverterValueOfClass> set);
   
   @Path("valueOf/separator/sortedset")
   @GET
   public String matrixValueOfSeparatorSortedSet(@MatrixParam("m") @Separator("-") SortedSet<MultiValuedParamDefaultParamConverterValueOfClass> set);
   
   @Path("valueOf/regex/list")
   @GET
   public String matrixValueOfRegexList(@MatrixParam("m") @Regex("([^-]+)") List<MultiValuedParamDefaultParamConverterValueOfClass> list);
   
   @Path("valueOf/regex/set")
   @GET
   public String matrixValueOfRegexSet(@MatrixParam("m") @Regex("([^-]+)") Set<MultiValuedParamDefaultParamConverterValueOfClass> set);
   
   @Path("valueOf/regex/sortedset")
   @GET
   public String matrixValueOfRegexSortedSet(@MatrixParam("m") @Regex("([^-]+)") SortedSet<MultiValuedParamDefaultParamConverterValueOfClass> set);
   
   @Path("valueOf/default/list")
   @GET
   public String matrixValueOfDefaultList(@MatrixParam("m") List<MultiValuedParamDefaultParamConverterValueOfClass> list);
   
   @Path("valueOf/default/set")
   @GET
   public String matrixValueOfDefaultSet(@MatrixParam("m") Set<MultiValuedParamDefaultParamConverterValueOfClass> set);
   
   @Path("valueOf/default/sortedset")
   @GET
   public String matrixValueOfDefaultSortedSet(@MatrixParam("m") SortedSet<MultiValuedParamDefaultParamConverterValueOfClass> set);
   
   @Path("fromString/separator/list")
   @GET
   public String matrixFromStringSeparatorList(@MatrixParam("m") @Separator("-") List<MultiValuedParamDefaultParamConverterFromStringClass> list);
   
   @Path("fromString/separator/set")
   @GET
   public String matrixFromStringSeparatorSet(@MatrixParam("m") @Separator("-") Set<MultiValuedParamDefaultParamConverterFromStringClass> set);
   
   @Path("fromString/separator/sortedset")
   @GET
   public String matrixFromStringSeparatorSortedSet(@MatrixParam("m") @Separator("-") SortedSet<MultiValuedParamDefaultParamConverterFromStringClass> set);
   
   @Path("fromString/regex/list")
   @GET
   public String matrixFromStringRegexList(@MatrixParam("m") @Regex("([^-]+)") List<MultiValuedParamDefaultParamConverterFromStringClass> list);
   
   @Path("fromString/regex/set")
   @GET
   public String matrixFromStringRegexSet(@MatrixParam("m") @Regex("([^-]+)") Set<MultiValuedParamDefaultParamConverterFromStringClass> set);
   
   @Path("fromString/regex/sortedset")
   @GET
   public String matrixFromStringRegexSortedSet(@MatrixParam("m") @Regex("([^-]+)") SortedSet<MultiValuedParamDefaultParamConverterFromStringClass> set);
   
   @Path("fromString/default/list")
   @GET
   public String matrixFromStringDefaultList(@MatrixParam("m") List<MultiValuedParamDefaultParamConverterFromStringClass> list);
   
   @Path("fromString/default/set")
   @GET
   public String matrixFromStringDefaultSet(@MatrixParam("m") Set<MultiValuedParamDefaultParamConverterFromStringClass> set);
   
   @Path("fromString/default/sortedset")
   @GET
   public String matrixFromStringDefaultSortedSet(@MatrixParam("m") SortedSet<MultiValuedParamDefaultParamConverterFromStringClass> set);
   
   @Path("paramConverter/separator/list")
   @GET
   public String matrixParamConverterSeparatorList(@MatrixParam("m") @Separator("-") List<MultiValuedParamDefaultParamConverterParamConverterClass> list);
   
   @Path("paramConverter/separator/set")
   @GET
   public String matrixParamConverterSeparatorSet(@MatrixParam("m") @Separator("-") Set<MultiValuedParamDefaultParamConverterParamConverterClass> set);
   
   @Path("paramConverter/separator/sortedset")
   @GET
   public String matrixParamConverterSeparatorSortedSet(@MatrixParam("m") @Separator("-") SortedSet<MultiValuedParamDefaultParamConverterParamConverterClass> set);
   
   @Path("paramConverter/regex/list")
   @GET
   public String matrixParamConverterRegexList(@MatrixParam("m") @Regex("([^-]+)") List<MultiValuedParamDefaultParamConverterParamConverterClass> list);
   
   @Path("paramConverter/regex/set")
   @GET
   public String matrixParamConverterRegexSet(@MatrixParam("m") @Regex("([^-]+)") Set<MultiValuedParamDefaultParamConverterParamConverterClass> set);
   
   @Path("paramConverter/regex/sortedset")
   @GET
   public String matrixParamConverterRegexSortedSet(@MatrixParam("m") @Regex("([^-]+)") SortedSet<MultiValuedParamDefaultParamConverterParamConverterClass> set);
   
   @Path("paramConverter/default/list")
   @GET
   public String matrixParamConverterDefaultList(@MatrixParam("m") List<MultiValuedParamDefaultParamConverterParamConverterClass> list);
   
   @Path("paramConverter/default/set")
   @GET
   public String matrixParamConverterDefaultSet(@MatrixParam("m") Set<MultiValuedParamDefaultParamConverterParamConverterClass> set);
   
   @Path("paramConverter/default/sortedset")
   @GET
   public String matrixParamConverterDefaultSortedSet(@MatrixParam("m") SortedSet<MultiValuedParamDefaultParamConverterParamConverterClass> set);
}

