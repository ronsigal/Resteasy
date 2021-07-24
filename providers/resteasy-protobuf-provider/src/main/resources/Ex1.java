package x.y;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PathParam;

@Path("")
public class Ex1 {

    protected int i;

    @GET
    public void f() {
        i = 7;
    }
}

class Ex2 extends Ex1 {

    protected String j;

    @POST
    public void g(@PathParam("p") String p) {
        j = p;
    }
}
