package org.acme.simpledtupay.api;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.simpledtupay.domain.Merchant;
import org.acme.simpledtupay.domain.Payment;
import org.acme.simpledtupay.domain.PaymentService;

import java.math.BigDecimal;
import java.util.List;

@Path("/payments")
public class PaymentResource {
    @Inject
    PaymentService paymentService;
    //DTO
   public static class PaymentRequest {
        public BigDecimal amount;
        public String customerId;
        public String merchantId;
        public String description;
   }
    //POST请求 发起支付
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response pay(PaymentRequest request){ //use DTO to get arguments
       try{
           boolean success = paymentService.pay(
                   request.amount,
                   request.customerId,
                   request.merchantId,
                   request.description
           );
           return Response.ok().build();
       } catch(IllegalArgumentException e){
           return Response.status(404).entity(e.getMessage()).build();
       }
    }

    //GET 获取列表
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Payment> getPayments(){
       return paymentService.getPayments();
    }
}