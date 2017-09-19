package de.johzie.resteasy;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.mock.MockDispatcherFactory;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.mock.MockHttpResponse;
import org.jboss.resteasy.plugins.server.resourcefactory.POJOResourceFactory;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.johzie.resteasy.IUserResource;
import de.johzie.resteasy.User;
import de.johzie.resteasy.UserResource;

public class UserResourceTest {

    private static UndertowJaxrsServer server;

    private void initUndertowServerAndDeployApp() throws Exception {
        server = new UndertowJaxrsServer().start();
        server.deploy(MyApp.class);
    }

    private void stopUndertowServer() throws Exception {
        server.stop();
    }

    @ApplicationPath("/base")
    public static class MyApp extends Application {
        @Override
        public Set<Class<?>> getClasses() {
            HashSet<Class<?>> classes = new HashSet<>();
            classes.add(UserResource.class);
            return classes;
        }
    }

    @Test
    public void testUserResourceWithHttpClient() throws Exception {
        // Given
        initUndertowServerAndDeployApp();

        Client client = ClientBuilder.newClient();

        User expectedUser = new User(1, "Sharp Roentgen", "sharo", "sharp@roentgen.com");

        // When
        Response response = client.target(TestPortProvider.generateURL("/base/users/1")).request().get();

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_OK);

        User currentUser = response.readEntity(User.class);
        assertThat(currentUser).isEqualToComparingFieldByField(expectedUser);

        stopUndertowServer();
    }

    @Test
    public void testUserResourceWithProxy() throws Exception {
        // Given
        initUndertowServerAndDeployApp();

        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget webTarget = client.target(TestPortProvider.generateURL("/base"));
        IUserResource userResourceProxy = webTarget.proxy(IUserResource.class);

        User userToAdd = new User(5, "Nifty Benz", "nibe", "nifty@benz.com");

        // When
        Response response = userResourceProxy.create(userToAdd);

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_CREATED);

        User currentUser = userResourceProxy.getUser(userToAdd.getId());
        assertThat(currentUser).isEqualToComparingFieldByField(userToAdd);

        stopUndertowServer();
    }

    @Test
    public void testUserResourceWithMockedRequests()
            throws URISyntaxException, JsonParseException, JsonMappingException, IOException {
        // Given
        MockHttpRequest request = MockHttpRequest.get("/users/1");
        MockHttpResponse response = new MockHttpResponse();

        Dispatcher dispatcher = MockDispatcherFactory.createDispatcher();
        POJOResourceFactory userResourceFactory = new POJOResourceFactory(UserResource.class);
        dispatcher.getRegistry().addResourceFactory(userResourceFactory);

        User expectedUser = new User(1, "Sharp Roentgen", "sharo", "sharp@roentgen.com");

        // When
        dispatcher.invoke(request, response);

        // Then
        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_OK);

        ObjectMapper mapper = new ObjectMapper();
        User currentUser = mapper.readValue(response.getContentAsString(), User.class);
        assertThat(currentUser).isEqualToComparingFieldByField(expectedUser);
    }
}
