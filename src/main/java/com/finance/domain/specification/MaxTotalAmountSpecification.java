package com.finance.domain.specification;

import com.finance.domain.model.Loan;

import java.math.BigDecimal;

public class MaxTotalAmountSpecification extends CompositeSpecification<Loan> {

    private BigDecimal maxTotalAmount;

    public MaxTotalAmountSpecification(BigDecimal maxTotalAmount) {
        this.maxTotalAmount = maxTotalAmount;
    }

    @Override
    public boolean isSatisfiedBy(Loan candidate) {
        return maxTotalAmount.compareTo(candidate.getTotalAmount()) >= 0;
    }

    @Override
    public String getMessage() {
        return "Loan amount is not allowed";
    }
}
