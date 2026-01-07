package org.acme.simpledtupay.api;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.acme.simpledtupay.domain.Customer;
import org.acme.simpledtupay.domain.Merchant;
import org.acme.simpledtupay.domain.PaymentService;

@Path("/merchants")
public class MerchantResource {
    @Inject
    PaymentService paymentService;

    //POST请求 注册返回ID字符串 但是也是简单的JSON
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String registerMerchant(Merchant merchant){
        String id =  paymentService.registerMerchant(merchant);
        return id;
    }
}
