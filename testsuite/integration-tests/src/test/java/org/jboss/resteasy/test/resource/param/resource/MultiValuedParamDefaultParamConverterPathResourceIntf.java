package org.jboss.resteasy.test.resource.param.resource;

import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.jboss.resteasy.annotations.Regex;
import org.jboss.resteasy.annotations.Separator;

@Path("path")
public interface MultiValuedParamDefaultParamConverterPathResourceIntf {

   //////////////////////////////////
   // MultiValuedParamDefaultProviderConstructorClass
   
   @Path("constructor/separator/list/{p}")
   @GET
   public String pathConstructorSeparatorList(@PathParam("p") @Separator("-") List<MultiValuedParamDefaultParamConverterConstructorClass> list);
 
   @Path("constructor/separator/set/{p}")
   @GET
   public String pathConstructorSeparatorSet(@PathParam("p") @Separator("-") Set<MultiValuedParamDefaultParamConverterConstructorClass> set);
   
   @Path("constructor/separator/sortedset/{p}")
   @GET
   public String pathConstructorSeparatorSortedSet(@PathParam("p") @Separator("-") SortedSet<MultiValuedParamDefaultParamConverterConstructorClass> set);
   
   @Path("constructor/regex/list/{p}")
   @GET
   public String pathConstructorRegexList(@PathParam("p") @Regex("([^-]+)") List<MultiValuedParamDefaultParamConverterConstructorClass> list);
   
   @Path("constructor/regex/set/{p}")
   @GET
   public String pathConstructorRegexSet(@PathParam("p") @Regex("([^-]+)") Set<MultiValuedParamDefaultParamConverterConstructorClass> set);
   
   @Path("constructor/regex/sortedset/{p}")
   @GET
   public String pathConstructorRegexSortedSet(@PathParam("p") @Regex("([^-]+)") SortedSet<MultiValuedParamDefaultParamConverterConstructorClass> set);
   
   @Path("constructor/default/list/{p}")
   @GET
   public String pathConstructorDefaultList(@PathParam("p") List<MultiValuedParamDefaultParamConverterConstructorClass> list);
   
   @Path("constructor/default/set/{p}")
   @GET
   public String pathConstructorDefaultSet(@PathParam("p") Set<MultiValuedParamDefaultParamConverterConstructorClass> set);
   
   @Path("constructor/default/sortedset/{p}")
   @GET
   public String pathConstructorDefaultSortedSet(@PathParam("p") SortedSet<MultiValuedParamDefaultParamConverterConstructorClass> set);
   
   //////////////////////////////////
   // MultiValuedParamDefaultProviderValueOfClass
   
   @Path("valueOf/separator/list/{p}")
   @GET
   public String pathValueOfSeparatorList(@PathParam("p") @Separator("-") List<MultiValuedParamDefaultParamConverterValueOfClass> list);
   
   @Path("valueOf/separator/set/{p}")
   @GET
   public String pathValueOfSeparatorSet(@PathParam("p") @Separator("-") Set<MultiValuedParamDefaultParamConverterValueOfClass> set);
   
   @Path("valueOf/separator/sortedset/{p}")
   @GET
   public String pathValueOfSeparatorSortedSet(@PathParam("p") @Separator("-") SortedSet<MultiValuedParamDefaultParamConverterValueOfClass> set);
   
   @Path("valueOf/regex/list/{p}")
   @GET
   public String pathValueOfRegexList(@PathParam("p") @Regex("([^-]+)") List<MultiValuedParamDefaultParamConverterValueOfClass> list);
   
   @Path("valueOf/regex/set/{p}")
   @GET
   public String pathValueOfRegexSet(@PathParam("p") @Regex("([^-]+)") Set<MultiValuedParamDefaultParamConverterValueOfClass> set);
   
   @Path("valueOf/regex/sortedset/{p}")
   @GET
   public String pathValueOfRegexSortedSet(@PathParam("p") @Regex("([^-]+)") SortedSet<MultiValuedParamDefaultParamConverterValueOfClass> set);
   
   @Path("valueOf/default/list/{p}")
   @GET
   public String pathValueOfDefaultList(@PathParam("p") List<MultiValuedParamDefaultParamConverterValueOfClass> list);
   
   @Path("valueOf/default/set/{p}")
   @GET
   public String pathValueOfDefaultSet(@PathParam("p") Set<MultiValuedParamDefaultParamConverterValueOfClass> set);
   
   @Path("valueOf/default/sortedset/{p}")
   @GET
   public String pathValueOfDefaultSortedSet(@PathParam("p") SortedSet<MultiValuedParamDefaultParamConverterValueOfClass> set);
   
   @Path("fromString/separator/list/{p}")
   @GET
   public String pathFromStringSeparatorList(@PathParam("p") @Separator("-") List<MultiValuedParamDefaultParamConverterFromStringClass> list);
   
   @Path("fromString/separator/set/{p}")
   @GET
   public String pathFromStringSeparatorSet(@PathParam("p") @Separator("-") Set<MultiValuedParamDefaultParamConverterFromStringClass> set);
   
   @Path("fromString/separator/sortedset/{p}")
   @GET
   public String pathFromStringSeparatorSortedSet(@PathParam("p") @Separator("-") SortedSet<MultiValuedParamDefaultParamConverterFromStringClass> set);
   
   @Path("fromString/regex/list/{p}")
   @GET
   public String pathFromStringRegexList(@PathParam("p") @Regex("([^-]+)") List<MultiValuedParamDefaultParamConverterFromStringClass> list);
   
   @Path("fromString/regex/set/{p}")
   @GET
   public String pathFromStringRegexSet(@PathParam("p") @Regex("([^-]+)") Set<MultiValuedParamDefaultParamConverterFromStringClass> set);
   
   @Path("fromString/regex/sortedset/{p}")
   @GET
   public String pathFromStringRegexSortedSet(@PathParam("p") @Regex("([^-]+)") SortedSet<MultiValuedParamDefaultParamConverterFromStringClass> set);
   
   @Path("fromString/default/list/{p}")
   @GET
   public String pathFromStringDefaultList(@PathParam("p") List<MultiValuedParamDefaultParamConverterFromStringClass> list);
   
   @Path("fromString/default/set/{p}")
   @GET
   public String pathFromStringDefaultSet(@PathParam("p") Set<MultiValuedParamDefaultParamConverterFromStringClass> set);
   
   @Path("fromString/default/sortedset/{p}")
   @GET
   public String pathFromStringDefaultSortedSet(@PathParam("p") SortedSet<MultiValuedParamDefaultParamConverterFromStringClass> set);
   
   @Path("paramConverter/separator/list/{p}")
   @GET
   public String pathParamConverterSeparatorList(@PathParam("p") @Separator("-") List<MultiValuedParamDefaultParamConverterParamConverterClass> list);
   
   @Path("paramConverter/separator/set/{p}")
   @GET
   public String pathParamConverterSeparatorSet(@PathParam("p") @Separator("-") Set<MultiValuedParamDefaultParamConverterParamConverterClass> set);
   
   @Path("paramConverter/separator/sortedset/{p}")
   @GET
   public String pathParamConverterSeparatorSortedSet(@PathParam("p") @Separator("-") SortedSet<MultiValuedParamDefaultParamConverterParamConverterClass> set);
   
   @Path("paramConverter/regex/list/{p}")
   @GET
   public String pathParamConverterRegexList(@PathParam("p") @Regex("([^-]+)") List<MultiValuedParamDefaultParamConverterParamConverterClass> list);
   
   @Path("paramConverter/regex/set/{p}")
   @GET
   public String pathParamConverterRegexSet(@PathParam("p") @Regex("([^-]+)") Set<MultiValuedParamDefaultParamConverterParamConverterClass> set);
   
   @Path("paramConverter/regex/sortedset/{p}")
   @GET
   public String pathParamConverterRegexSortedSet(@PathParam("p") @Regex("([^-]+)") SortedSet<MultiValuedParamDefaultParamConverterParamConverterClass> set);
   
   @Path("paramConverter/default/list/{p}")
   @GET
   public String pathParamConverterDefaultList(@PathParam("p") List<MultiValuedParamDefaultParamConverterParamConverterClass> list);
   
   @Path("paramConverter/default/set/{p}")
   @GET
   public String pathParamConverterDefaultSet(@PathParam("p") Set<MultiValuedParamDefaultParamConverterParamConverterClass> set);
   
   @Path("paramConverter/default/sortedset/{p}")
   @GET
   public String pathParamConverterDefaultSortedSet(@PathParam("p") SortedSet<MultiValuedParamDefaultParamConverterParamConverterClass> set);
}

