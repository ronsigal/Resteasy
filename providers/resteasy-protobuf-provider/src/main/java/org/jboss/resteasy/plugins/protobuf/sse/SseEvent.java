package org.jboss.resteasy.plugins.protobuf.sse;

public class SseEvent {

   private String  comment;
   private String  name;
   private int     id;
   private byte[]  data;
   private long    retry;
   
   public String getComment()
   {
      return comment;
   }
   public void setComment(String comment)
   {
      this.comment = comment;
   }
   public String getName()
   {
      return name;
   }
   public void setName(String name)
   {
      this.name = name;
   }
   public int getId()
   {
      return id;
   }
   public void setId(int id)
   {
      this.id = id;
   }
   public byte[] getData() {
      return data;
   }
   public void setData(byte[] data)
   {
      this.data = data;
   }
   public long isRetry()
   {
      return retry;
   }
   public void setRetry(long retry)
   {
      this.retry = retry;
   }
}
