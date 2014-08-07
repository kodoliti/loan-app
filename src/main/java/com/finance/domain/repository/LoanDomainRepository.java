package com.finance.domain.repository;

import com.finance.domain.annotation.DomainRepository;
import com.finance.domain.model.Loan;
import com.finance.domain.policy.LoanExtensionPolicy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

@DomainRepository
public class LoanDomainRepository {

    private LoanRepository loanRepository;

    private LoanExtensionPolicy weeklyLoanExtensionPolicy;

    @Autowired
    public LoanDomainRepository(LoanRepository loanRepository, LoanExtensionPolicy weeklyLoanExtensionPolicy) {
        this.loanRepository = loanRepository;
        this.weeklyLoanExtensionPolicy = weeklyLoanExtensionPolicy;
    }

    public Loan load(Long loanId) {
        Loan loan = loanRepository.findById(loanId);
        loan.setWeeklyLoanExtensionPolicy(weeklyLoanExtensionPolicy);
        return loan;
    }

    public Loan findById(Long id) {
        return loanRepository.findById(id);
    }

    public List<Loan> findByIpAddress(String ipAddress) {
        return loanRepository.findByIpAddress(ipAddress);
    }

    public List<Loan> findByIpAddressAndCreatedBetween(String ipAddress, Date dateFrom, Date dateTo) {
        return loanRepository.findByIpAddressAndCreatedBetween(ipAddress, dateFrom, dateTo);
    }

    public List<Loan> findByCustomerIdOrderByCreatedDesc(Long customerId) {
        return loanRepository.findByCustomerIdOrderByCreatedDesc(customerId);
    }

    public void save(Loan loan) {
        loanRepository.save(loan);
    }

}
