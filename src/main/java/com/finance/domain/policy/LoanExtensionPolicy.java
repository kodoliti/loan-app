package com.finance.domain.policy;

import java.math.BigDecimal;
import org.joda.time.LocalDate;

public interface LoanExtensionPolicy {

    LocalDate getPaymentDate(LocalDate localDate);

    BigDecimal recalculateInterest(BigDecimal amount);
}
