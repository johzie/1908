package de.johzie.resteasy;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("users")
public interface IUserResource {

    @GET
    List<User> getUsers();

    @GET
    @Path("{id}")
    User getUser(@PathParam("id") int id);

    @POST
    Response create(User user);

    @PUT
    @Path("{id}")
    Response update(@PathParam("id") int id, User user);

    @DELETE
    Response remove(User user);

}
