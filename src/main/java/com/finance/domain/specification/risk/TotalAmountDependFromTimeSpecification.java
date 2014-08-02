package com.finance.domain.specification.risk;

import com.finance.domain.model.Loan;
import com.finance.domain.specification.CompositeSpecification;

import java.math.BigDecimal;

public class TotalAmountDependFromTimeSpecification extends CompositeSpecification<Loan> {

    private BigDecimal maxTotalAmount;

    public TotalAmountDependFromTimeSpecification(BigDecimal maxTotalAmount) {
        this.maxTotalAmount = maxTotalAmount;
    }

    @Override
    public boolean isSatisfiedBy(Loan candidate) {
        return maxTotalAmount.compareTo(candidate.getTotalAmount()) > 0;
    }

    @Override
    public String getMessage() {
        return "Loan amount and time are not allowed";
    }
}
