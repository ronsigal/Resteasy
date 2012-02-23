package org.jboss.resteasy.test.xxe;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.xml.bind.JAXBElement;

import org.jboss.resteasy.test.xxe.AbstractTestXXE.FavoriteMovieXmlRootElement;

/**
 * Unit tests for RESTEASY-647.
 * 
 * Idea for test comes from Tim McCune: 
 * http://jersey.576304.n2.nabble.com/Jersey-vulnerable-to-XXE-attack-td3214584.html
 *
 * @author <a href="mailto:ron.sigal@jboss.com">Ron Sigal</a>
 * @date Jan 6, 2012
 */
@Path("/")
public class MovieResource
{
   @POST
   @Path("xmlRootElement")
   @Consumes({"application/xml"})
   public String addFavoriteMovie(FavoriteMovieXmlRootElement movie)
   {
      System.out.println("MovieResource(xmlRootElment): title = " + movie.getTitle());
      return movie.getTitle();
   }

   @POST
   @Path("xmlType")
   @Consumes({"application/xml"})
   public String addFavoriteMovie(FavoriteMovieXmlType movie)
   {
      System.out.println("MovieResource(xmlType): title = " + movie.getTitle());
      return movie.getTitle();
   }

   @POST
   @Path("JAXBElement")
   @Consumes("application/xml")
   public String addFavoriteMovie(JAXBElement<FavoriteMovie> value)
   {
      System.out.println("MovieResource(JAXBElement): title = " + value.getValue().getTitle());
      return value.getValue().getTitle();
   }

   @POST
   @Path("list")
   @Consumes("application/xml")
   public String addFavoriteMovie(List<FavoriteMovieXmlRootElement> list)
   {
      String titles = "";
      Iterator<FavoriteMovieXmlRootElement> it = list.iterator();
      while (it.hasNext())
      {
         String title = it.next().getTitle();
         System.out.println("MovieResource(list): title = " + title);
         titles += title;
      }
      return titles;
   }

   @POST
   @Path("set")
   @Consumes("application/xml")
   public String addFavoriteMovie(Set<FavoriteMovieXmlRootElement> set)
   {
      String titles = "";
      Iterator<FavoriteMovieXmlRootElement> it = set.iterator();
      while (it.hasNext())
      {
         String title = it.next().getTitle();
         System.out.println("MovieResource(list): title = " + title);
         titles += title;
      }
      return titles;
   }

   @POST
   @Path("array")
   @Consumes("application/xml")
   public String addFavoriteMovie(FavoriteMovieXmlRootElement[] array)
   {
      String titles = "";
      for (int i = 0; i < array.length; i++)
      {
         String title = array[i].getTitle();
         System.out.println("MovieResource(list): title = " + title);
         titles += title;
      }
      return titles;
   }

   @POST
   @Path("map")
   @Consumes("application/xml")
   public String addFavoriteMovie(Map<String,FavoriteMovieXmlRootElement> map)
   {
      String titles = "";
      Iterator<String> it = map.keySet().iterator();
      while (it.hasNext())
      {
         String title = map.get(it.next()).getTitle();
         System.out.println("MovieResource(map): title = " + title);
         titles += title;
      }
      return titles;
   }
}
