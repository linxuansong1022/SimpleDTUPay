package org.acme.simpledtupay.infrastructure;

import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceException_Exception;
import dtu.ws.fastmoney.BankService_Service;
import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;

@ApplicationScoped
public class BankServiceAdapter {
    BankService bank;
    public BankServiceAdapter() {
        this.bank = new BankService_Service().getBankServicePort();
    }
    //bank transfer，args: 付款人账号，收款人账号，金额，备注
    public void transferMoney(String fromAccount, String toAccount, BigDecimal amount, String description){
        try{
            bank.transferMoneyFromTo(fromAccount, toAccount, amount, description);
        } catch (BankServiceException_Exception e) {
// 如果银行那边报错了（比如余额不足），它会抛出 BankServiceException_Exception
// 我们捕获它，然后抛出一个更通用的 RuntimeException，并带上错误信息。
// 这样我们的 PaymentService 只要知道“出错了”，而不需要处理复杂的 SOAP 异常。
            throw new RuntimeException(e.getMessage());
        }
    }
}
