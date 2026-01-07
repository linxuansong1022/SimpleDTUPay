package org.acme.simpledtupay.api;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.acme.simpledtupay.domain.Customer;
import org.acme.simpledtupay.domain.PaymentService;

@Path("/customers")
public class CustomerResource {
    @Inject
    PaymentService paymentService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)

    public String registerCustomer(Customer customer){
        String id =  paymentService.registerCustomer(customer);
        return id;
    }

}
