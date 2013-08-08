package org.jboss.resteasy.test;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ListExperiment extends BaseResourceTest
{
   @XmlAccessorType(XmlAccessType.FIELD)
   @XmlRootElement(name = "book")
   public static class Book {
       private String name;
       private String author;
       private String publisher;
       private String isbn;
    
       public String getName() {
           return name;
       }
    
       public void setName(String name) {
           this.name = name;
       }
    
       public String getAuthor() {
           return author;
       }
    
       public void setAuthor(String author) {
           this.author = author;
       }
    
       public String getPublisher() {
           return publisher;
       }
    
       public void setPublisher(String publisher) {
           this.publisher = publisher;
       }
    
       public String getIsbn() {
           return isbn;
       }
    
       public void setIsbn(String isbn) {
           this.isbn = isbn;
       }
    
       @Override
       public String toString() {
           return "Book [name=" + name + ", author=" + author + ", publisher="
                   + publisher + ", isbn=" + isbn  + "]";
       }
   }
   
   @XmlAccessorType(XmlAccessType.FIELD)
   @XmlRootElement(name = "books")
   public static class Books {
    
       @XmlElement(name = "book", type = Book.class)
       private List<Book> books = new ArrayList<Book>();
    
       public Books() {}
    
       public Books(List<Book> books) {
           this.books = books;
       }
    
       public List<Book> getBooks() {
           return books;
       }
    
       public void setBooks(List<Book> books) {
           this.books = books;
       }
   }
   
   @XmlRootElement(name="customer")
   @XmlAccessorType(XmlAccessType.PROPERTY)
   public static class Customer
   {
      private String name;

      @XmlElement
      public String getName()
      {
         return name;
      }

      public void setName(String name)
      {
         this.name = name;
      }
   }

   @Path("/test")
   public static class TestService
   {
      @GET
      @Produces("application/xml")
      @Path("books")
      public Books get()
      {
         Book b1 = new Book();
         b1.setAuthor("ron");
         b1.setName("junk");
         Book b2 = new Book();
         b2.setAuthor("tanicka");
         b2.setName("great");
         ArrayList<Book> ab = new ArrayList<Book>();
         ab.add(b1);
         ab.add(b2);
         Books bs = new Books(ab);
         return bs;
      }
      
      @POST
      @Consumes("application/xml")
      public void post(Customer cust)
      {
         System.out.println(cust.getName());
      }

      @POST
      @Path("string")
      public void postString(String cust)
      {
         System.out.println("*******");
         System.out.println(cust);
      }
   }

   @BeforeClass
   public static void setup()
   {
      addPerRequestResource(TestService.class);
   }

   @Test
   public void testCase() throws Exception
   {

      ClientRequest request = new ClientRequest(TestPortProvider.generateURL("/test/books"));
      ClientResponse<?> response = request.get();
      System.out.println("status: " + response.getStatus());
      Object entity = response.getEntity(String.class);
      System.out.println("entity: " + entity);
   }
   
   @Test
   public void testCaseXML() throws Exception
   {

      ClientRequest request = new ClientRequest(TestPortProvider.generateURL("/test/books"));
      request.accept(MediaType.APPLICATION_XML);
      ClientResponse<?> response = request.get();
      System.out.println("status: " + response.getStatus());
      Object entity = response.getEntity(Books.class);
      System.out.println("entity: " + entity);
   }
}
