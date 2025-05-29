package com.supplylink.dtos.res;

import com.supplylink.models.enums.PaymentStatus;

import java.math.BigDecimal;

public class PaymentResponse {
    private String method;
    private String transactionId;
    private PaymentStatus status;
    private BigDecimal amount;

    public PaymentResponse() {}

    public PaymentResponse(String method, String transactionId, PaymentStatus status, BigDecimal amount) {
        this.method = method;
        this.transactionId = transactionId;
        this.status = status;
        this.amount = amount;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
