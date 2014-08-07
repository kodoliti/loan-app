package com.finance.controller.dto;

import com.finance.domain.model.Loan;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class LoanDto {

    private BigDecimal totalAmount;

    private Date repaymentDate;

    private String ipAddress;

    private Loan.LoanStatus status;

    private String loanReference;

    private List<LoanExtensionDto> loanExtensionDtoList;


    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public Date getRepaymentDate() {
        return repaymentDate;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public Loan.LoanStatus getStatus() {
        return status;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setRepaymentDate(Date repaymentDate) {
        this.repaymentDate = repaymentDate;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public void setStatus(Loan.LoanStatus status) {
        this.status = status;
    }

    public List<LoanExtensionDto> getLoanExtensionDtoList() {
        return loanExtensionDtoList;
    }

    public void setLoanExtensionDtoList(List<LoanExtensionDto> loanExtensionDtoList) {
        this.loanExtensionDtoList = loanExtensionDtoList;
    }

    public String getLoanReference() {
        return loanReference;
    }

    public void setLoanReference(String loanReference) {
        this.loanReference = loanReference;
    }
}
