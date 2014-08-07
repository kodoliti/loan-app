package com.finance.controller.dto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class LoanRequestDto implements Serializable {

    private static final long serialVersionUID = -1L;

    @NotNull
    @Valid
    private CustomerDto customerDto;

    @NotNull
    private BigDecimal totalAmount;

    @NotNull
    private Date repaymentDate;

    public LoanRequestDto() {
    }

    public LoanRequestDto(CustomerDto customerDto, BigDecimal totalAmount, Date repaymentDate) {
        this.customerDto = customerDto;
        this.totalAmount = totalAmount;
        this.repaymentDate = repaymentDate;
    }

    public CustomerDto getCustomerDto() {
        return customerDto;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public Date getRepaymentDate() {
        return repaymentDate;
    }

    public void setCustomerDto(CustomerDto customerDto) {
        this.customerDto = customerDto;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setRepaymentDate(Date repaymentDate) {
        this.repaymentDate = repaymentDate;
    }

    @Override
    public String toString() {
        return "LoanRequestDto{" +
                "customerDto=" + customerDto +
                ", totalAmount=" + totalAmount +
                ", repaymentDate=" + repaymentDate +
                '}';
    }
}
