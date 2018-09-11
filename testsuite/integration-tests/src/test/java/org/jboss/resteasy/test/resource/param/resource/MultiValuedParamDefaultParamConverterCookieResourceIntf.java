package org.jboss.resteasy.test.resource.param.resource;

import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.jboss.resteasy.annotations.Regex;
import org.jboss.resteasy.annotations.Separator;

@Path("cookie")
public interface MultiValuedParamDefaultParamConverterCookieResourceIntf {

   //////////////////////////////////
   // MultiValuedParamDefaultParamConverterConstructorClass
   
   @Path("constructor/separator/list")
   @GET
   public String cookieConstructorSeparatorList(@CookieParam("c") @Separator("#") List<MultiValuedParamDefaultParamConverterConstructorClass> list);;
 
   @Path("constructor/separator/set")
   @GET
   public String cookieConstructorSeparatorSet(@CookieParam("c") @Separator("#") Set<MultiValuedParamDefaultParamConverterConstructorClass> set);;

   @Path("constructor/separator/sortedset")
   @GET
   public String cookieConstructorSeparatorSortedSet(@CookieParam("c") @Separator("#") SortedSet<MultiValuedParamDefaultParamConverterConstructorClass> set);
   
   @Path("constructor/regex/list")
   @GET
   public String cookieConstructorRegexList(@CookieParam("c") @Regex("([^#]+)") List<MultiValuedParamDefaultParamConverterConstructorClass> list);
   
   @Path("constructor/regex/set")
   @GET
   public String cookieConstructorRegexSet(@CookieParam("c") @Regex("([^#]+)") Set<MultiValuedParamDefaultParamConverterConstructorClass> set);
   
   @Path("constructor/regex/sortedset")
   @GET
   public String cookieConstructorRegexSortedSet(@CookieParam("c") @Regex("([^#]+)") SortedSet<MultiValuedParamDefaultParamConverterConstructorClass> set);
   
   @Path("constructor/default/list")
   @GET
   public String cookieConstructorDefaultList(@CookieParam("c") List<MultiValuedParamDefaultParamConverterConstructorClass> list);
   
   @Path("constructor/default/set")
   @GET
   public String cookieConstructorDefaultSet(@CookieParam("c") Set<MultiValuedParamDefaultParamConverterConstructorClass> set);
   
   @Path("constructor/default/sortedset")
   @GET
   public String cookieConstructorDefaultSortedSet(@CookieParam("c") SortedSet<MultiValuedParamDefaultParamConverterConstructorClass> set);
   
   //////////////////////////////////
   // MultiValuedParamDefaultParamConverterValueOfClass
   
   @Path("valueOf/separator/list")
   @GET
   public String cookieValueOfSeparatorList(@CookieParam("c") @Separator("#") List<MultiValuedParamDefaultParamConverterValueOfClass> list);
   
   @Path("valueOf/separator/set")
   @GET
   public String cookieValueOfSeparatorSet(@CookieParam("c") @Separator("#") Set<MultiValuedParamDefaultParamConverterValueOfClass> set);
   
   @Path("valueOf/separator/sortedset")
   @GET
   public String cookieValueOfSeparatorSortedSet(@CookieParam("c") @Separator("#") SortedSet<MultiValuedParamDefaultParamConverterValueOfClass> set);
   
   @Path("valueOf/regex/list")
   @GET
   public String cookieValueOfRegexList(@CookieParam("c") @Regex("([^#]+)") List<MultiValuedParamDefaultParamConverterValueOfClass> list);
   
   @Path("valueOf/regex/set")
   @GET
   public String cookieValueOfRegexSet(@CookieParam("c")@ Regex("([^#]+)") Set<MultiValuedParamDefaultParamConverterValueOfClass> set);
   
   @Path("valueOf/regex/sortedset")
   @GET
   public String cookieValueOfRegexSortedSet(@CookieParam("c") @Regex("([^#]+)") SortedSet<MultiValuedParamDefaultParamConverterValueOfClass> set);
   
   @Path("valueOf/default/list")
   @GET
   public String cookieValueOfDefaultList(@CookieParam("c") List<MultiValuedParamDefaultParamConverterValueOfClass> list); 
   
   @Path("valueOf/default/set")
   @GET
   public String cookieValueOfDefaultSet(@CookieParam("c") Set<MultiValuedParamDefaultParamConverterValueOfClass> set);
   
   @Path("valueOf/default/sortedset")
   @GET
   public String cookieValueOfDefaultSortedSet(@CookieParam("c") SortedSet<MultiValuedParamDefaultParamConverterValueOfClass> set);
   
   @Path("fromString/separator/list")
   @GET
   public String cookieFromStringSeparatorList(@CookieParam("c") @Separator("#") List<MultiValuedParamDefaultParamConverterFromStringClass> list);
   
   @Path("fromString/separator/set")
   @GET
   public String cookieFromStringSeparatorSet(@CookieParam("c") @Separator("#") Set<MultiValuedParamDefaultParamConverterFromStringClass> set);
   
   @Path("fromString/separator/sortedset")
   @GET
   public String cookieFromStringSeparatorSortedSet(@CookieParam("c") @Separator("#") SortedSet<MultiValuedParamDefaultParamConverterFromStringClass> set);
   
   @Path("fromString/regex/list")
   @GET
   public String cookieFromStringRegexList(@CookieParam("c") @Regex("([^#]+)") List<MultiValuedParamDefaultParamConverterFromStringClass> list);
   
   @Path("fromString/regex/set")
   @GET
   public String cookieFromStringRegexSet(@CookieParam("c") @Regex("([^#]+)") Set<MultiValuedParamDefaultParamConverterFromStringClass> set);
   
   @Path("fromString/regex/sortedset")
   @GET
   public String cookieFromStringRegexSortedSet(@CookieParam("c") @Regex("([^#]+)") SortedSet<MultiValuedParamDefaultParamConverterFromStringClass> set); 
   
   @Path("fromString/default/list")
   @GET
   public String cookieFromStringDefaultList(@CookieParam("c") List<MultiValuedParamDefaultParamConverterFromStringClass> list);
   
   @Path("fromString/default/set")
   @GET
   public String cookieFromStringDefaultSet(@CookieParam("c") Set<MultiValuedParamDefaultParamConverterFromStringClass> set);
   
   @Path("fromString/default/sortedset")
   @GET
   public String cookieFromStringDefaultSortedSet(@CookieParam("c") SortedSet<MultiValuedParamDefaultParamConverterFromStringClass> set);
   
   @Path("paramConverter/separator/list")
   @GET
   public String cookieParamConverterSeparatorList(@CookieParam("c") @Separator("#") List<MultiValuedParamDefaultParamConverterParamConverterClass> list);
   
   @Path("paramConverter/separator/set")
   @GET
   public String cookieParamConverterSeparatorSet(@CookieParam("c") @Separator("#") Set<MultiValuedParamDefaultParamConverterParamConverterClass> set); 
   
   @Path("paramConverter/separator/sortedset")
   @GET
   public String cookieParamConverterSeparatorSortedSet(@CookieParam("c") @Separator("#") SortedSet<MultiValuedParamDefaultParamConverterParamConverterClass> set);
   
   @Path("paramConverter/regex/list")
   @GET
   public String cookieParamConverterRegexList(@CookieParam("c") @Regex("([^#]+)") List<MultiValuedParamDefaultParamConverterParamConverterClass> list);
   
   @Path("paramConverter/regex/set")
   @GET
   public String cookieParamConverterRegexSet(@CookieParam("c") @Regex("([^#]+)") Set<MultiValuedParamDefaultParamConverterParamConverterClass> set);
   
   @Path("paramConverter/regex/sortedset")
   @GET
   public String cookieParamConverterRegexSortedSet(@CookieParam("c") @Regex("([^#]+)") SortedSet<MultiValuedParamDefaultParamConverterParamConverterClass> set);
   
   @Path("paramConverter/default/list")
   @GET
   public String cookieParamConverterDefaultList(@CookieParam("c") List<MultiValuedParamDefaultParamConverterParamConverterClass> list);
   
   @Path("paramConverter/default/set")
   @GET
   public String cookieParamConverterDefaultSet(@CookieParam("c") Set<MultiValuedParamDefaultParamConverterParamConverterClass> set);
   
   @Path("paramConverter/default/sortedset")
   @GET
   public String cookieParamConverterDefaultSortedSet(@CookieParam("c") SortedSet<MultiValuedParamDefaultParamConverterParamConverterClass> set);
}

