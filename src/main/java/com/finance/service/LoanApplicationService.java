package com.finance.service;

import com.finance.controller.dto.CustomerDto;
import com.finance.controller.dto.LoanRequestDto;
import com.finance.domain.annotation.ApplicationService;
import com.finance.domain.exception.CreateLoanException;
import com.finance.domain.exception.CreateLoanExtensionException;
import com.finance.domain.model.Contact;
import com.finance.domain.model.Customer;
import com.finance.domain.model.Loan;
import com.finance.domain.model.LoanFactory;
import com.finance.domain.policy.LoanExtensionPolicy;
import com.finance.domain.repository.CustomerRepository;
import com.finance.domain.repository.LoanRepository;
import com.finance.domain.specification.CompositeSpecification;
import com.finance.domain.specification.MaxTotalAmountSpecification;
import com.finance.domain.specification.Specification;
import com.finance.domain.specification.risk.NumberOfApplicationsAllowedSpecification;
import com.finance.domain.specification.risk.TimeOfReceiptApplicationSpecification;
import com.finance.domain.specification.risk.TotalAmountDependFromTimeSpecification;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Artur Wojcik
 */
@ApplicationService
public class LoanApplicationService {

    private LoanRepository loanRepository;

    private CustomerRepository customerRepository;

    private LoanFactory loanFactory;

    @Value("${max.loan.amount.in.pln}")
    private BigDecimal maxLoanAmount;

    @Value("${high.risk.hour.to}")
    private Integer highRiskHourTo;

    @Value("${max.num.of.applications.from.ip.per.day}")
    private Integer maxNumOfApplicationsFromIpPerDay;

    private LoanExtensionPolicy weeklyLoanExtensionPolicy;


    @Autowired
    public LoanApplicationService(LoanRepository loanRepository,
                                  CustomerRepository customerRepository,
                                  LoanFactory loanFactory,
                                  LoanExtensionPolicy weeklyLoanExtensionPolicy) {
        this.loanRepository = loanRepository;
        this.customerRepository = customerRepository;
        this.loanFactory = loanFactory;
        this.weeklyLoanExtensionPolicy = weeklyLoanExtensionPolicy;
    }

    public Loan createLoan(LoanRequestDto loanRequestDto, String ipAddress) throws CreateLoanException {

        Loan loan = loanFactory.createLoan(getCustomer(loanRequestDto.getCustomerDto()),
                loanRequestDto.getTotalAmount(),
                loanRequestDto.getRepaymentDate(),
                ipAddress);

        Specification<Loan> loanSpecification = createSpecification();

        if (!loanSpecification.isSatisfiedBy(loan)) {
            throw new CreateLoanException(loanSpecification.getMessage());
        }

        loan.grant();

        loanRepository.save(loan);

        return loan;
    }


    public Loan createLoanExtension(String loanReference) throws CreateLoanExtensionException {

        Loan loan = loanRepository.findById(getIdFromLoanReference(loanReference));
        loan.setWeeklyLoanExtensionPolicy(weeklyLoanExtensionPolicy);

        if (loan == null) {
            throw new CreateLoanExtensionException("There is no loan for given reference");
        }

        loan.extend();

        loanRepository.save(loan);

        return loan;
    }

    private Long getIdFromLoanReference(String loanReference) throws CreateLoanExtensionException {
        try {
            return Long.valueOf(loanReference);
        } catch (NumberFormatException e) {
            throw new CreateLoanExtensionException("Given reference has bad format.");
        }
    }


    public List<Loan> getLoanHistory(Long customerId) {
        return loanRepository.findByCustomerIdOrderByCreatedDesc(customerId);
    }


    private Specification<Loan> createSpecification() {

        return new CompositeSpecification<Loan>(
                new MaxTotalAmountSpecification(maxLoanAmount),
                new TimeOfReceiptApplicationSpecification(new LocalTime(highRiskHourTo, 0))
                        .or(new TotalAmountDependFromTimeSpecification(maxLoanAmount)),
                new NumberOfApplicationsAllowedSpecification(loanRepository, maxNumOfApplicationsFromIpPerDay));

    }

    private Customer getCustomer(CustomerDto customerDto) {

        Customer customer = customerRepository.findByIdentificationNumber(customerDto.getIdentificationNumber());

        if (customer != null) {
            return customer;
        }

        return new Customer(
                customerDto.getFirstName(),
                customerDto.getSurname(),
                customerDto.getDateOfBirth(),
                customerDto.getIdentificationNumber(),
                new Contact(customerDto.geteMailAddress(),
                        customerDto.getMobilePhoneNumber(),
                        customerDto.getApartmentNumber(),
                        customerDto.getBuildingNumber(),
                        customerDto.getStreet(),
                        customerDto.getCity(),
                        customerDto.getPostcode())
        );
    }


}
