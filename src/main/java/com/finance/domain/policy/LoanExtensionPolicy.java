package com.finance.domain.policy;

import org.joda.time.LocalDate;

import java.math.BigDecimal;

public interface LoanExtensionPolicy {

    LocalDate getPaymentDate(LocalDate localDate);

    BigDecimal recalculateInterest(BigDecimal amount);
}
