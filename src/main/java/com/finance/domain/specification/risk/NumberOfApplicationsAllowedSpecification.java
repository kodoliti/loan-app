package com.finance.domain.specification.risk;

import com.finance.domain.model.Loan;
import com.finance.domain.repository.LoanRepository;
import com.finance.domain.specification.CompositeSpecification;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import java.util.List;


public class NumberOfApplicationsAllowedSpecification extends CompositeSpecification<Loan> {

    private int maxNumberOfApplications = 0;

    private LoanRepository loanRepository;

    public NumberOfApplicationsAllowedSpecification(LoanRepository loanRepository, int maxNumberOfApplications) {
        this.loanRepository = loanRepository;
        this.maxNumberOfApplications = maxNumberOfApplications;
    }

    @Override
    public boolean isSatisfiedBy(Loan candidate) {
        LocalDateTime now = LocalDateTime.now();
        LocalDate localDate = new LocalDate();

        List<Loan> loans = loanRepository.findByIpAddressAndCreatedBetween(candidate.getIpAddress(), localDate.toDate(), now.toDate());
        return loans.size() < maxNumberOfApplications;
    }

    @Override
    public String getMessage() {
        return "Number of applications is not allowed";
    }
}
