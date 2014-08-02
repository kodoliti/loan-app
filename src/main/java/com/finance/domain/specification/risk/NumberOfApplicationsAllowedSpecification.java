package com.finance.domain.specification.risk;

import com.finance.domain.model.Loan;
import com.finance.domain.repository.LoanDomainRepository;
import com.finance.domain.specification.CompositeSpecification;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import java.util.List;



public class NumberOfApplicationsAllowedSpecification extends CompositeSpecification<Loan> {

    private int maxNumberOfApplications = 0;

    private LoanDomainRepository loanDomainRepository;

    public NumberOfApplicationsAllowedSpecification(LoanDomainRepository loanDomainRepository, int maxNumberOfApplications) {
        this.loanDomainRepository = loanDomainRepository;
        this.maxNumberOfApplications = maxNumberOfApplications;
    }

    @Override
    public boolean isSatisfiedBy(Loan candidate) {
        LocalDateTime now = LocalDateTime.now();
        LocalDate localDate = new LocalDate();

        List<Loan> loans = loanDomainRepository.findByIpAddressAndCreatedBetween(candidate.getIpAddress(), localDate.toDate(), now.toDate());
        return loans.size() < maxNumberOfApplications;
    }

    @Override
    public String getMessage() {
        return "Number of applications is not allowed";
    }
}
