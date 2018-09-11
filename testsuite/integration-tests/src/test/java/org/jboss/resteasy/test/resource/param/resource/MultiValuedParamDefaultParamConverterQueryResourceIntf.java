package org.jboss.resteasy.test.resource.param.resource;

import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.jboss.resteasy.annotations.Regex;
import org.jboss.resteasy.annotations.Separator;

@Path("query")
public interface MultiValuedParamDefaultParamConverterQueryResourceIntf {

   //////////////////////////////////
   // MultiValuedParamDefaultParamConverterConstructorClass
   
   @Path("constructor/separator/list")
   @GET
   public String queryConstructorSeparatorList(@QueryParam("q") @Separator("-") List<MultiValuedParamDefaultParamConverterConstructorClass> list);
 
   @Path("constructor/separator/set")
   @GET
   public String queryConstructorSeparatorSet(@QueryParam("q") @Separator("-") Set<MultiValuedParamDefaultParamConverterConstructorClass> set);

   @Path("constructor/separator/sortedset")
   @GET
   public String queryConstructorSeparatorSortedSet(@QueryParam("q") @Separator("-") SortedSet<MultiValuedParamDefaultParamConverterConstructorClass> set);
   
   @Path("constructor/regex/list")
   @GET
   public String queryConstructorRegexList(@QueryParam("q") @Regex("([^-]+)") List<MultiValuedParamDefaultParamConverterConstructorClass> list);
   
   @Path("constructor/regex/set")
   @GET
   public String queryConstructorRegexSet(@QueryParam("q") @Regex("([^-]+)") Set<MultiValuedParamDefaultParamConverterConstructorClass> set); 
   
   @Path("constructor/regex/sortedset")
   @GET
   public String queryConstructorRegexSortedSet(@QueryParam("q") @Regex("([^-]+)") SortedSet<MultiValuedParamDefaultParamConverterConstructorClass> set);
   
   @Path("constructor/default/list")
   @GET
   public String queryConstructorDefaultList(@QueryParam("q") List<MultiValuedParamDefaultParamConverterConstructorClass> list);
   
   @Path("constructor/default/set")
   @GET
   public String queryConstructorDefaultSet(@QueryParam("q") Set<MultiValuedParamDefaultParamConverterConstructorClass> set);
   
   @Path("constructor/default/sortedset")
   @GET
   public String queryConstructorDefaultSortedSet(@QueryParam("q") SortedSet<MultiValuedParamDefaultParamConverterConstructorClass> set);
   
   //////////////////////////////////
   // MultiValuedParamDefaultParamConverterValueOfClass
   
   @Path("valueOf/separator/list")
   @GET
   public String queryValueOfSeparatorList(@QueryParam("q") @Separator("-") List<MultiValuedParamDefaultParamConverterValueOfClass> list);
   
   @Path("valueOf/separator/set")
   @GET
   public String queryValueOfSeparatorSet(@QueryParam("q") @Separator("-") Set<MultiValuedParamDefaultParamConverterValueOfClass> set);
   
   @Path("valueOf/separator/sortedset")
   @GET
   public String queryValueOfSeparatorSortedSet(@QueryParam("q") @Separator("-") SortedSet<MultiValuedParamDefaultParamConverterValueOfClass> set);
   
   @Path("valueOf/regex/list")
   @GET
   public String queryValueOfRegexList(@QueryParam("q") @Regex("([^-]+)") List<MultiValuedParamDefaultParamConverterValueOfClass> list);
   
   @Path("valueOf/regex/set")
   @GET
   public String queryValueOfRegexSet(@QueryParam("q")@ Regex("([^-]+)") Set<MultiValuedParamDefaultParamConverterValueOfClass> set);
   
   @Path("valueOf/regex/sortedset")
   @GET
   public String queryValueOfRegexSortedSet(@QueryParam("q") @Regex("([^-]+)") SortedSet<MultiValuedParamDefaultParamConverterValueOfClass> set);
   
   @Path("valueOf/default/list")
   @GET
   public String queryValueOfDefaultList(@QueryParam("q") List<MultiValuedParamDefaultParamConverterValueOfClass> list);
   
   @Path("valueOf/default/set")
   @GET
   public String queryValueOfDefaultSet(@QueryParam("q") Set<MultiValuedParamDefaultParamConverterValueOfClass> set);
   
   @Path("valueOf/default/sortedset")
   @GET
   public String queryValueOfDefaultSortedSet(@QueryParam("q") SortedSet<MultiValuedParamDefaultParamConverterValueOfClass> set);
   
   @Path("fromString/separator/list")
   @GET
   public String queryFromStringSeparatorList(@QueryParam("q") @Separator("-") List<MultiValuedParamDefaultParamConverterFromStringClass> list);
   
   @Path("fromString/separator/set")
   @GET
   public String queryFromStringSeparatorSet(@QueryParam("q") @Separator("-") Set<MultiValuedParamDefaultParamConverterFromStringClass> set);
   
   @Path("fromString/separator/sortedset")
   @GET
   public String queryFromStringSeparatorSortedSet(@QueryParam("q") @Separator("-") SortedSet<MultiValuedParamDefaultParamConverterFromStringClass> set);
   
   @Path("fromString/regex/list")
   @GET
   public String queryFromStringRegexList(@QueryParam("q") @Regex("([^-]+)") List<MultiValuedParamDefaultParamConverterFromStringClass> list);
   
   @Path("fromString/regex/set")
   @GET
   public String queryFromStringRegexSet(@QueryParam("q") @Regex("([^-]+)") Set<MultiValuedParamDefaultParamConverterFromStringClass> set);
   
   @Path("fromString/regex/sortedset")
   @GET
   public String queryFromStringRegexSortedSet(@QueryParam("q") @Regex("([^-]+)") SortedSet<MultiValuedParamDefaultParamConverterFromStringClass> set);
   
   @Path("fromString/default/list")
   @GET
   public String queryFromStringDefaultList(@QueryParam("q") List<MultiValuedParamDefaultParamConverterFromStringClass> list);
   
   @Path("fromString/default/set")
   @GET
   public String queryFromStringDefaultSet(@QueryParam("q") Set<MultiValuedParamDefaultParamConverterFromStringClass> set);
   
   @Path("fromString/default/sortedset")
   @GET
   public String queryFromStringDefaultSortedSet(@QueryParam("q") SortedSet<MultiValuedParamDefaultParamConverterFromStringClass> set);
   
   @Path("paramConverter/separator/list")
   @GET
   public String queryParamConverterSeparatorList(@QueryParam("q") @Separator("-") List<MultiValuedParamDefaultParamConverterParamConverterClass> list);
   
   @Path("paramConverter/separator/set")
   @GET
   public String queryParamConverterSeparatorSet(@QueryParam("q") @Separator("-") Set<MultiValuedParamDefaultParamConverterParamConverterClass> set);
   
   @Path("paramConverter/separator/sortedset")
   @GET
   public String queryParamConverterSeparatorSortedSet(@QueryParam("q") @Separator("-") SortedSet<MultiValuedParamDefaultParamConverterParamConverterClass> set);
   
   @Path("paramConverter/regex/list")
   @GET
   public String queryParamConverterRegexList(@QueryParam("q") @Regex("([^-]+)") List<MultiValuedParamDefaultParamConverterParamConverterClass> list);
   
   @Path("paramConverter/regex/set")
   @GET
   public String queryParamConverterRegexSet(@QueryParam("q") @Regex("([^-]+)") Set<MultiValuedParamDefaultParamConverterParamConverterClass> set);
   
   @Path("paramConverter/regex/sortedset")
   @GET
   public String queryParamConverterRegexSortedSet(@QueryParam("q") @Regex("([^-]+)") SortedSet<MultiValuedParamDefaultParamConverterParamConverterClass> set);
   
   @Path("paramConverter/default/list")
   @GET
   public String queryParamConverterDefaultList(@QueryParam("q") List<MultiValuedParamDefaultParamConverterParamConverterClass> list);
   
   @Path("paramConverter/default/set")
   @GET
   public String queryParamConverterDefaultSet(@QueryParam("q") Set<MultiValuedParamDefaultParamConverterParamConverterClass> set);
   
   @Path("paramConverter/default/sortedset")
   @GET
   public String queryParamConverterDefaultSortedSet(@QueryParam("q") SortedSet<MultiValuedParamDefaultParamConverterParamConverterClass> set);
}
