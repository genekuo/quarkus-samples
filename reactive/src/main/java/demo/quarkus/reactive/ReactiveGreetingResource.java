package demo.quarkus.reactive;

import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.jboss.resteasy.reactive.RestSseElementType;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/hello")
public class ReactiveGreetingResource {

    @Inject
    ReactiveGreetingService service;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/greeting/{name}")
    public Uni<String> greeting(String name) {
        return service.greeting(name);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/greetings/{count}/{name}")
    public Multi<String> greetings(int count, String name) {
        return service.greetings(count, name);
    }

    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS)
    @RestSseElementType((MediaType.TEXT_PLAIN))
    @Path("/stream/{count}/{name}")
    public Multi<String> greetingsAsStream(int count, String name) {
        return service.greetings(count, name);
    }


    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello RESTEasy Reactive";
    }
}