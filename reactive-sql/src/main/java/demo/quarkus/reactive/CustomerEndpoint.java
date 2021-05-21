package demo.quarkus.reactive;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

@Path("customers")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomerEndpoint {


    @Inject
    io.vertx.mutiny.pgclient.PgPool client;


    @PostConstruct
    private void initdb() {

        client.query("DROP TABLE IF EXISTS CUSTOMER").execute()
                .flatMap(r -> client.query("CREATE SEQUENCE IF NOT EXISTS  customerId_seq").execute())
                .flatMap(r -> client.query("CREATE TABLE CUSTOMER (id SERIAL PRIMARY KEY, name TEXT NOT NULL,surname TEXT NOT NULL)").execute())
                .flatMap(r -> client.query("INSERT INTO CUSTOMER (id, name, surname) VALUES ( nextval('customerId_seq'), 'John','Doe')").execute())
                .flatMap(r -> client.query("INSERT INTO CUSTOMER (id, name, surname) VALUES ( nextval('customerId_seq'), 'Fred','Smith')").execute())
                .await().indefinitely();
    }

    @GET
    public Multi<Customer> getAll() {
        return Customer.findAll(client);
    }

    @GET
    @Path("{id}")
    public Uni<Response> getSingle(@PathParam("id") Long id) {
        return Customer.findById(client, id)
                .onItem().transform(customer -> customer != null ? Response.ok(customer) : Response.status(Response.Status.NOT_FOUND))
                .onItem().transform(javax.ws.rs.core.Response.ResponseBuilder::build);
    }


    @POST
    public Uni<Response> create(Customer customer) {

        return customer.create(client)
                .onItem().transform(id -> URI.create("/customers/" + id))
                .onItem().transform(uri -> Response.created(uri).build());
     }


    @PUT
    public Uni<Response> update(Customer customer) {
        return customer.update(client)
                .onItem().transform(updated -> updated ? Response.ok(customer) : Response.status(Response.Status.NOT_FOUND))
                .onItem().transform(javax.ws.rs.core.Response.ResponseBuilder::build);
    }


    @DELETE
    public Uni<Response> delete(@QueryParam("id") Long customerId) {
        return Customer.delete(client, customerId)
                .onItem().transform(deleted -> deleted ? Response.ok(Response.Status.NO_CONTENT): Response.status(Response.Status.NOT_FOUND))
                .onItem().transform(javax.ws.rs.core.Response.ResponseBuilder::build);
    }

}