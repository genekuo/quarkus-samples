package demo.quarkus.elythron;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;


@Path("customers")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
//@RolesAllowed("user")
public class CustomerEndpoint {

    @Inject CustomerRepository customerRepository;

    @GET
    @RolesAllowed("user")
    public List<Customer> getAll() {
        return customerRepository.findAll();
    }

    @POST
    @RolesAllowed("admin")
    public Response create(Customer customer) {
        customerRepository.createCustomer(customer);
        return Response.status(201).build();
    }

    @PUT
    @RolesAllowed("admin")
    public Response update(Customer customer) {
        customerRepository.updateCustomer(customer);
        return Response.status(204).build();
    }
    @DELETE
    @RolesAllowed("admin")
    public Response delete(@QueryParam("id") Long customerId) {
        customerRepository.deleteCustomer(customerId);
        return Response.status(204).build();
    }

}
