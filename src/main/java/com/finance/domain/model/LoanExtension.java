package com.finance.domain.model;

import com.finance.domain.annotation.DomainAggregateRoot;
import com.finance.domain.policy.LoanExtensionPolicy;
import org.joda.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.Date;

@Entity
public class LoanExtension {

    @Id
    @GeneratedValue
    private Long id;

    private Date oldRepaymentDate;

    private BigDecimal oldLoanAmount;

    private BigDecimal newLoanAmount;

    private Date extendedRepaymentDate;

    @Transient
    private LoanExtensionPolicy loanExtensionPolicy;

    public LoanExtension() {
    }

    public LoanExtension(Date oldRepaymentDate, BigDecimal oldLoanAmount, LoanExtensionPolicy loanExtensionPolicy) {
        this.oldRepaymentDate = oldRepaymentDate;
        this.oldLoanAmount = oldLoanAmount;
        this.loanExtensionPolicy = loanExtensionPolicy;
    }

    public void determineRepaymentDate() {
        this.extendedRepaymentDate = loanExtensionPolicy.getPaymentDate(new LocalDate(oldRepaymentDate)).toDate();
    }

    public void recalculateInterest() {
        this.newLoanAmount = loanExtensionPolicy.recalculateInterest(oldLoanAmount);
    }

    public Long getId() {
        return id;
    }

    public Date getOldRepaymentDate() {
        return oldRepaymentDate;
    }

    public Date getExtendedRepaymentDate() {
        return extendedRepaymentDate;
    }

    public BigDecimal getOldLoanAmount() {
        return oldLoanAmount;
    }

    public BigDecimal getNewLoanAmount() {
        return newLoanAmount;
    }
}
