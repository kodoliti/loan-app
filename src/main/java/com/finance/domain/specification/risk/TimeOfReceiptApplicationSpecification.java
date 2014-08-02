package com.finance.domain.specification.risk;

import com.finance.domain.model.Loan;
import com.finance.domain.specification.CompositeSpecification;
import org.joda.time.LocalTime;


public class TimeOfReceiptApplicationSpecification extends CompositeSpecification<Loan> {

    private LocalTime highRiskHourTo;

    public TimeOfReceiptApplicationSpecification(LocalTime highRiskHourTo) {
        this.highRiskHourTo = highRiskHourTo;
    }

    @Override
    public boolean isSatisfiedBy(Loan candidate) {
        LocalTime localTime = LocalTime.now();
        return localTime.isAfter(highRiskHourTo);
    }

    @Override
    public String getMessage() {
        return "Loan amount and time are not allowed";
    }

}
