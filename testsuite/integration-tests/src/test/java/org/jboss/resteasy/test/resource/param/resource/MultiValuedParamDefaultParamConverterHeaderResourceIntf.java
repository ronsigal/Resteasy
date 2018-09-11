package org.jboss.resteasy.test.resource.param.resource;

import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;

import org.jboss.resteasy.annotations.Regex;
import org.jboss.resteasy.annotations.Separator;

@Path("header")
public interface MultiValuedParamDefaultParamConverterHeaderResourceIntf {

   //////////////////////////////////
   // MultiValuedParamDefaultParamConverterConstructorClass
   
   @Path("constructor/separator/list")
   @GET
   public String headerConstructorSeparatorList(@HeaderParam("h") @Separator("-") List<MultiValuedParamDefaultParamConverterConstructorClass> list);
 
   @Path("constructor/separator/set")
   @GET
   public String headerConstructorSeparatorSet(@HeaderParam("h") @Separator("-") Set<MultiValuedParamDefaultParamConverterConstructorClass> set);

   @Path("constructor/separator/sortedset")
   @GET
   public String headerConstructorSeparatorSortedSet(@HeaderParam("h") @Separator("-") SortedSet<MultiValuedParamDefaultParamConverterConstructorClass> set);
   
   @Path("constructor/regex/list")
   @GET
   public String headerConstructorRegexList(@HeaderParam("h") @Regex("([^-]+)") List<MultiValuedParamDefaultParamConverterConstructorClass> list);
   
   @Path("constructor/regex/set")
   @GET
   public String headerConstructorRegexSet(@HeaderParam("h") @Regex("([^-]+)") Set<MultiValuedParamDefaultParamConverterConstructorClass> set);
   
   @Path("constructor/regex/sortedset")
   @GET
   public String headerConstructorRegexSortedSet(@HeaderParam("h") @Regex("([^-]+)") SortedSet<MultiValuedParamDefaultParamConverterConstructorClass> set);
   
   @Path("constructor/default/list")
   @GET
   public String headerConstructorDefaultList(@HeaderParam("h") List<MultiValuedParamDefaultParamConverterConstructorClass> list);
   
   @Path("constructor/default/set")
   @GET
   public String headerConstructorDefaultSet(@HeaderParam("h") Set<MultiValuedParamDefaultParamConverterConstructorClass> set);
   
   @Path("constructor/default/sortedset")
   @GET
   public String headerConstructorDefaultSortedSet(@HeaderParam("h") SortedSet<MultiValuedParamDefaultParamConverterConstructorClass> set);
   
   //////////////////////////////////
   // MultiValuedParamDefaultParamConverterValueOfClass
   
   @Path("valueOf/separator/list")
   @GET
   public String headerValueOfSeparatorList(@HeaderParam("h") @Separator("-") List<MultiValuedParamDefaultParamConverterValueOfClass> list);
   
   @Path("valueOf/separator/set")
   @GET
   public String headerValueOfSeparatorSet(@HeaderParam("h") @Separator("-") Set<MultiValuedParamDefaultParamConverterValueOfClass> set);
   
   @Path("valueOf/separator/sortedset")
   @GET
   public String headerValueOfSeparatorSortedSet(@HeaderParam("h") @Separator("-") SortedSet<MultiValuedParamDefaultParamConverterValueOfClass> set);
   
   @Path("valueOf/regex/list")
   @GET
   public String headerValueOfRegexList(@HeaderParam("h") @Regex("([^-]+)") List<MultiValuedParamDefaultParamConverterValueOfClass> list);
   
   @Path("valueOf/regex/set")
   @GET
   public String headerValueOfRegexSet(@HeaderParam("h") @Regex("([^-]+)") Set<MultiValuedParamDefaultParamConverterValueOfClass> set);
   
   @Path("valueOf/regex/sortedset")
   @GET
   public String headerValueOfRegexSortedSet(@HeaderParam("h") @Regex("([^-]+)") SortedSet<MultiValuedParamDefaultParamConverterValueOfClass> set);
   
   @Path("valueOf/default/list")
   @GET
   public String headerValueOfDefaultList(@HeaderParam("h") List<MultiValuedParamDefaultParamConverterValueOfClass> list);
   
   @Path("valueOf/default/set")
   @GET
   public String headerValueOfDefaultSet(@HeaderParam("h") Set<MultiValuedParamDefaultParamConverterValueOfClass> set);
   
   @Path("valueOf/default/sortedset")
   @GET
   public String headerValueOfDefaultSortedSet(@HeaderParam("h") SortedSet<MultiValuedParamDefaultParamConverterValueOfClass> set);
   
   @Path("fromString/separator/list")
   @GET
   public String headerFromStringSeparatorList(@HeaderParam("h") @Separator("-") List<MultiValuedParamDefaultParamConverterFromStringClass> list);
   
   @Path("fromString/separator/set")
   @GET
   public String headerFromStringSeparatorSet(@HeaderParam("h") @Separator("-") Set<MultiValuedParamDefaultParamConverterFromStringClass> set);
   
   @Path("fromString/separator/sortedset")
   @GET
   public String headerFromStringSeparatorSortedSet(@HeaderParam("h") @Separator("-") SortedSet<MultiValuedParamDefaultParamConverterFromStringClass> set);
   
   @Path("fromString/regex/list")
   @GET
   public String headerFromStringRegexList(@HeaderParam("h") @Regex("([^-]+)") List<MultiValuedParamDefaultParamConverterFromStringClass> list);
   
   @Path("fromString/regex/set")
   @GET
   public String headerFromStringRegexSet(@HeaderParam("h") @Regex("([^-]+)") Set<MultiValuedParamDefaultParamConverterFromStringClass> set);
   
   @Path("fromString/regex/sortedset")
   @GET
   public String headerFromStringRegexSortedSet(@HeaderParam("h") @Regex("([^-]+)") SortedSet<MultiValuedParamDefaultParamConverterFromStringClass> set);
   
   @Path("fromString/default/list")
   @GET
   public String headerFromStringDefaultList(@HeaderParam("h") List<MultiValuedParamDefaultParamConverterFromStringClass> list);
   
   @Path("fromString/default/set")
   @GET
   public String headerFromStringDefaultSet(@HeaderParam("h") Set<MultiValuedParamDefaultParamConverterFromStringClass> set);
   
   @Path("fromString/default/sortedset")
   @GET
   public String headerFromStringDefaultSortedSet(@HeaderParam("h") SortedSet<MultiValuedParamDefaultParamConverterFromStringClass> set);
   
   @Path("paramConverter/separator/list")
   @GET
   public String headerParamConverterSeparatorList(@HeaderParam("h") @Separator("-") List<MultiValuedParamDefaultParamConverterParamConverterClass> list);
   
   @Path("paramConverter/separator/set")
   @GET
   public String headerParamConverterSeparatorSet(@HeaderParam("h") @Separator("-") Set<MultiValuedParamDefaultParamConverterParamConverterClass> set);
   
   @Path("paramConverter/separator/sortedset")
   @GET
   public String headerParamConverterSeparatorSortedSet(@HeaderParam("h") @Separator("-") SortedSet<MultiValuedParamDefaultParamConverterParamConverterClass> set);
   
   @Path("paramConverter/regex/list")
   @GET
   public String headerParamConverterRegexList(@HeaderParam("h") @Regex("([^-]+)") List<MultiValuedParamDefaultParamConverterParamConverterClass> list);
   
   @Path("paramConverter/regex/set")
   @GET
   public String headerParamConverterRegexSet(@HeaderParam("h") @Regex("([^-]+)") Set<MultiValuedParamDefaultParamConverterParamConverterClass> set);
   
   @Path("paramConverter/regex/sortedset")
   @GET
   public String headerParamConverterRegexSortedSet(@HeaderParam("h") @Regex("([^-]+)") SortedSet<MultiValuedParamDefaultParamConverterParamConverterClass> set);
   
   @Path("paramConverter/default/list")
   @GET
   public String headerParamConverterDefaultList(@HeaderParam("h") List<MultiValuedParamDefaultParamConverterParamConverterClass> list);
   
   @Path("paramConverter/default/set")
   @GET
   public String headerParamConverterDefaultSet(@HeaderParam("h") Set<MultiValuedParamDefaultParamConverterParamConverterClass> set);
   
   @Path("paramConverter/default/sortedset")
   @GET
   public String headerParamConverterDefaultSortedSet(@HeaderParam("h") SortedSet<MultiValuedParamDefaultParamConverterParamConverterClass> set);
}

