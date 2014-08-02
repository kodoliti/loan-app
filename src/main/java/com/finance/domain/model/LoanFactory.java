package com.finance.domain.model;

import com.finance.domain.annotation.DomainFactory;
import com.finance.domain.policy.LoanExtensionPolicy;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Date;

@DomainFactory
public class LoanFactory {

    @Autowired
    LoanExtensionPolicy weeklyLoanExtensionPolicy;

    public Loan createLoan(Customer customer, BigDecimal totalAmount, Date repaymentDate, String ipAddress) {
        Loan loan = new Loan(customer, totalAmount, repaymentDate, ipAddress, Loan.LoanStatus.DRAFT);
        loan.weeklyLoanExtensionPolicy = weeklyLoanExtensionPolicy;
        return loan;
    }

}
