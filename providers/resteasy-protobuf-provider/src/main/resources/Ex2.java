package x.y;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PathParam;

@Path("")
public class Ex2 {

	protected HashMap<String, String> m1;
    protected int i2;
    private String s2;
    private boolean[] bs;

    @GET
    public void f() {
        i2 = 7;
        s2 = "abc";
    }
    
    public void g() {
        i2 = 7;
        s2 = "abc";
    }
}

class Ex2b extends Ex2 {

    protected int i2b;
    private int j2b;
    private Ex2c ex2c;

    @POST
    public void g(@PathParam("p") String p, Ex2c ex2c) {
        i2b = 9;
        j2b = 11;
    }
    
    @PUT
    public int h(@PathParam("p") String p, int i) {
        i2b = 9;
        j2b = 11;
    }
    
    @PUT
    public Ex2c k(@PathParam("p") String p, int i) {
        i2b = 9;
        j2b = 11;
    }
}

class Ex2c extends Ex2 {

    protected int i2c;
    private int j2c;
}

class Ex2ca extends Ex2c {

    protected int i2ca;
    private int j2ca;

    public String h() {
        i2ca = 9;
        j2ca = 11;
        return "s";
    }
}

class ExMap extends HashMap {
}

