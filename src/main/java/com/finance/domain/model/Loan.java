package com.finance.domain.model;

import com.finance.domain.annotation.DomainAggregateRoot;
import com.finance.domain.policy.LoanExtensionPolicy;

import javax.persistence.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@DomainAggregateRoot
public class Loan extends AuditableDate {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    private Customer customer;

    private BigDecimal totalAmount;

    private Date repaymentDate;

    private String ipAddress;

    @Enumerated(EnumType.STRING)
    private LoanStatus status;

    @OneToMany(cascade = CascadeType.ALL)
    private List<LoanExtension> loanExtensionList = new ArrayList();

    @Transient
    private String loanReference;

    @Transient
    LoanExtensionPolicy weeklyLoanExtensionPolicy;

    public Loan() {
    }

    public Loan(Customer customer, BigDecimal totalAmount, Date repaymentDate, String ipAddress, LoanStatus status) {
        this.customer = customer;
        this.totalAmount = totalAmount;
        this.repaymentDate = repaymentDate;
        this.ipAddress = ipAddress;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public Date getRepaymentDate() {
        return repaymentDate;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public LoanStatus getStatus() {
        return status;
    }

    public List<LoanExtension> getLoanExtensionList() {
        return loanExtensionList;
    }

    public String getLoanReference() {
        DecimalFormat df = new DecimalFormat("#0000000000");
        if (id != null) {
            return df.format(id);
        }
        return "";
    }

    public void grant() {
        if (status != LoanStatus.DRAFT) {
            throw new RuntimeException("Granting loan is not allowed, loan in incorrect state!");
        }
        status = LoanStatus.ACTIVE;
    }

    public void extend() {
        if (status != LoanStatus.ACTIVE && status != LoanStatus.EXTENDED) {
            throw new RuntimeException("Extension loan is not allowed, loan in incorrect state!");
        }
        status = LoanStatus.EXTENDED;
        LoanExtension loanExtension = new LoanExtension(repaymentDate, totalAmount, weeklyLoanExtensionPolicy);

        loanExtension.recalculateInterest();

        loanExtension.determineRepaymentDate();

        loanExtensionList.add(loanExtension);

        repaymentDate = loanExtension.getExtendedRepaymentDate();
        totalAmount = loanExtension.getNewLoanAmount();
    }

    public void setWeeklyLoanExtensionPolicy(LoanExtensionPolicy weeklyLoanExtensionPolicy) {
        this.weeklyLoanExtensionPolicy = weeklyLoanExtensionPolicy;
    }

    public enum LoanStatus {
        DRAFT, ACTIVE, EXTENDED, COMPLETED, OVERDUE
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Loan loan = (Loan) o;

        if (!id.equals(loan.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
