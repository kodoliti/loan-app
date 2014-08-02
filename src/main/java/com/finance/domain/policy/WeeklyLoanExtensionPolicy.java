package com.finance.domain.policy;

import com.finance.domain.annotation.DomainPolicy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.GregorianCalendar;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Value;

@DomainPolicy
public class WeeklyLoanExtensionPolicy implements LoanExtensionPolicy {

    @Value("${loan.extension.interest.rate}")
    private Double interestRate;

    @Override
    public LocalDate getPaymentDate(LocalDate localDate) {
        return localDate.plusWeeks(1);
    }

    @Override
    public BigDecimal recalculateInterest(BigDecimal amount) {
        LocalDate localDate = new LocalDate();
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        boolean leapYear = gregorianCalendar.isLeapYear(localDate.getYear());

        int daysInYear = leapYear ? 366 : 365;

        BigDecimal ratePerWeek = new BigDecimal((interestRate / daysInYear) * 7);

        BigDecimal amountWithInterest = amount.multiply(ratePerWeek).add(amount);

        return amountWithInterest.setScale(2, BigDecimal.ROUND_HALF_UP).stripTrailingZeros();
    }
}
