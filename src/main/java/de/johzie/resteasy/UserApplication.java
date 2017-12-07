package de.johzie.resteasy;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;

@ApplicationPath("/")
public class UserApplication extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        HashSet<Class<?>> classes = new HashSet<>();
        classes.add(UserResource.class);
        return classes;
    }

    public static void main(String[] args) {
        // start HTTP server and deploy this REST application
        new UndertowJaxrsServer().start().deploy(UserApplication.class);
    }
}
