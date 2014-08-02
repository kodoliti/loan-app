package com.finance.controller.dto;

import java.math.BigDecimal;
import java.util.Date;

public class LoanExtensionDto {

    private Date oldRepaymentDate;

    private BigDecimal oldLoanAmount;

    private BigDecimal newLoanAmount;

    private Date extendedRepaymentDate;

    public Date getOldRepaymentDate() {
        return oldRepaymentDate;
    }

    public void setOldRepaymentDate(Date oldRepaymentDate) {
        this.oldRepaymentDate = oldRepaymentDate;
    }

    public BigDecimal getOldLoanAmount() {
        return oldLoanAmount;
    }

    public void setOldLoanAmount(BigDecimal oldLoanAmount) {
        this.oldLoanAmount = oldLoanAmount;
    }

    public BigDecimal getNewLoanAmount() {
        return newLoanAmount;
    }

    public void setNewLoanAmount(BigDecimal newLoanAmount) {
        this.newLoanAmount = newLoanAmount;
    }

    public Date getExtendedRepaymentDate() {
        return extendedRepaymentDate;
    }

    public void setExtendedRepaymentDate(Date extendedRepaymentDate) {
        this.extendedRepaymentDate = extendedRepaymentDate;
    }
}
