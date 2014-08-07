package com.finance.service

import com.finance.controller.dto.CustomerDto
import com.finance.controller.dto.LoanRequestDto
import com.finance.domain.exception.CreateLoanException
import com.finance.domain.model.Contact
import com.finance.domain.model.Customer
import com.finance.domain.model.Loan
import com.finance.domain.model.LoanFactory
import com.finance.domain.policy.WeeklyLoanExtensionPolicy
import com.finance.domain.repository.CustomerRepository
import com.finance.domain.repository.LoanDomainRepository
import com.finance.util.CommonTestObject
import org.joda.time.DateTime
import org.joda.time.DateTimeUtils
import org.joda.time.LocalDate
import org.joda.time.LocalDateTime
import spock.lang.Specification

class LoanApplicationServiceSpec extends Specification {

  CustomerRepository customerRepository = Mock()

  LoanDomainRepository loanRepository = Mock()

  LoanFactory loanFactory = new LoanFactory()

  WeeklyLoanExtensionPolicy loanExtensionPolicy = new WeeklyLoanExtensionPolicy()

  LoanApplicationService loanService;

  def setup() {
    loanExtensionPolicy.interestRate = 1.5
    loanFactory.weeklyLoanExtensionPolicy = loanExtensionPolicy
    loanService = new LoanApplicationService(loanRepository, customerRepository, loanFactory)
    loanService.highRiskHourTo = 6
    loanService.maxLoanAmount = new BigDecimal(300.00)
    loanService.maxNumOfApplicationsFromIpPerDay = 3
  }

  def "should grant a loan"() {
    setup:

    loanRepository.findByIpAddressAndCreatedBetween("127.0.0.1", _, _) >> []
    setTime(15, 30, 0)

    CustomerDto customerData = CommonTestObject.createCustomerData()
    BigDecimal loanAmount = 300.00;
    Date repaymentDate = CommonTestObject.generateDate(15);
    LoanRequestDto loanRequest = new LoanRequestDto(customerData, loanAmount, repaymentDate)

    when:
    def result = loanService.createLoan(loanRequest, "127.0.0.1");

    then:
    result.status == Loan.LoanStatus.ACTIVE
    result.totalAmount == 300
  }

  def "should refuse a loan application because it applies with exceed amount"() {
    setup:
    loanRepository.findByIpAddressAndCreatedBetween("127.0.0.1", _, _) >> []
    setTime(16, 45, 0)

    CustomerDto customerData = CommonTestObject.createCustomerData()
    BigDecimal loanAmount = 5000.00;
    Date repaymentDate = CommonTestObject.generateDate(15);
    LoanRequestDto loanRequest = new LoanRequestDto(customerData, loanAmount, repaymentDate)

    when:
    loanService.createLoan(loanRequest, "127.0.0.1");
    then:
    thrown(CreateLoanException)
  }

  def "should refuse a loan application because it applies at 0.45 AM with max amount"() {
    setup:
    loanRepository.findByIpAddressAndCreatedBetween("127.0.0.1", _, _) >> []
    setTime(0, 45, 0)

    CustomerDto customerData = CommonTestObject.createCustomerData()
    BigDecimal loanAmount = 300.00;
    Date repaymentDate = CommonTestObject.generateDate(15);
    LoanRequestDto loanRequest = new LoanRequestDto(customerData, loanAmount, repaymentDate)

    when:
    loanService.createLoan(loanRequest, "127.0.0.1");
    then:
    thrown(CreateLoanException)
  }

  def "should receive a loan, it applies at 2.45 AM with medium amount"() {
    setup:
    loanRepository.findByIpAddressAndCreatedBetween("127.0.0.1", _, _) >> []
    setTime(2, 45, 0)

    CustomerDto customerData = CommonTestObject.createCustomerData()
    BigDecimal loanAmount = 150.00;
    Date repaymentDate = CommonTestObject.generateDate(15);
    LoanRequestDto loanRequest = new LoanRequestDto(customerData, loanAmount, repaymentDate)

    when:
    def result = loanService.createLoan(loanRequest, "127.0.0.1");
    then:
    result.status == Loan.LoanStatus.ACTIVE
  }

  def "should receive a loan, it applies at 15.45 AM with max amount"() {
    setup:
    loanRepository.findByIpAddressAndCreatedBetween("127.0.0.1", _, _) >> []
    setTime(15, 45, 0)

    CustomerDto customerData = CommonTestObject.createCustomerData()
    BigDecimal loanAmount = 300.00;
    Date repaymentDate = CommonTestObject.generateDate(15);
    LoanRequestDto loanRequest = new LoanRequestDto(customerData, loanAmount, repaymentDate)

    when:
    def result = loanService.createLoan(loanRequest, "127.0.0.1");
    then:
    result.status == Loan.LoanStatus.ACTIVE
  }


  def "should refuse a loan application because it applies 10 times form single IP"() {
    setup:
    loanRepository.findByIpAddressAndCreatedBetween("127.0.0.1", _, _) >> getLoanList()
    setTime(15, 45, 0)

    CustomerDto customerData = CommonTestObject.createCustomerData()
    BigDecimal loanAmount = 100.00;
    Date repaymentDate = CommonTestObject.generateDate(15);
    LoanRequestDto loanRequest = new LoanRequestDto(customerData, loanAmount, repaymentDate)

    when:
    loanService.createLoan(loanRequest, "127.0.0.1");
    then:
    thrown(CreateLoanException)
  }

  def "should extend a loan"() {
    setup:
    CustomerDto customerData = CommonTestObject.createCustomerData()
    BigDecimal loanAmount = 200.00;
    Date repaymentDate = CommonTestObject.generateDate(15);
    LoanRequestDto loanRequest = new LoanRequestDto(customerData, loanAmount, repaymentDate)
    def loan = loanFactory.createLoan(
            getCustomer(loanRequest.getCustomerDto()),
            loanRequest.getTotalAmount(),
            loanRequest.getRepaymentDate(),
            "127.0.0.1")
    loan.status = Loan.LoanStatus.ACTIVE
    loanRepository.load(1l) >> loan

    when:
    def result = loanService.createLoanExtension("0000000001");

    then:
    result.status == Loan.LoanStatus.EXTENDED
    result.loanExtensionList.size() == 1
    result.repaymentDate == new LocalDate(repaymentDate).plusWeeks(1).toDate();
    result.totalAmount == 205.75
  }


  private void setTime(int hour, int minute, int second) {
    LocalDateTime localDateTime = new LocalDateTime();
    DateTime dateTime = new DateTime(localDateTime.getYear(), localDateTime.getMonthOfYear(),
            localDateTime.getDayOfMonth(), hour, minute, second, 0)
    DateTimeUtils.setCurrentMillisFixed(dateTime.getMillis());
  }

  List<Loan> getLoanList() {
    def list = []
    for (int i = 0; i < 10; i++) {
      list.add(new Loan(null, null, null, null, null))
    }
    return list
  }


  Customer getCustomer(CustomerDto customerData) {

    return new Customer(
            customerData.getFirstName(),
            customerData.getSurname(),
            customerData.getDateOfBirth(),
            customerData.getIdentificationNumber(),
            new Contact(customerData.geteMailAddress(),
                    customerData.getMobilePhoneNumber(),
                    customerData.getApartmentNumber(),
                    customerData.getBuildingNumber(),
                    customerData.getStreet(),
                    customerData.getCity(),
                    customerData.getPostcode())
    );
  }


  def cleanup() {
    DateTimeUtils.setCurrentMillisSystem();
  }
}
