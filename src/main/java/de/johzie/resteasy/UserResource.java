package de.johzie.resteasy;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

public class UserResource implements IUserResource {

    private static Map<Integer, User> users = new HashMap<>();

    static {
        users.put(1, new User(1, "Sharp Roentgen", "sharo", "sharp@roentgen.com"));
        users.put(2, new User(2, "Clever Wozniak", "clewo", "clever@wozniak.com"));
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUser(@PathParam("id") int id) {
        User user = users.get(id);
        return user;
    }

    @Override
    public Response create(User user) {
        users.put(user.getId(), user);
        return Response.created(URI.create("/users/" + user.getId())).build();
    }

    @Override
    public Response update(int id, User user) {
        users.put(id, user);
        return Response.ok().build();
    }

    @Override
    public Response remove(User user) {
        users.remove(user.getId());
        return Response.noContent().build();
    }
}
