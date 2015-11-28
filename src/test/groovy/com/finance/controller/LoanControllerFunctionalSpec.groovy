package com.finance.controller

import com.finance.conf.Application
import com.finance.controller.dto.CustomerDto
import com.finance.controller.dto.LoanRequestDto
import com.finance.controller.dto.LoanResponseDto
import com.finance.domain.repository.CustomerRepository
import com.finance.domain.repository.LoanRepository
import com.finance.util.CommonTestObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.IntegrationTest
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.client.ClientHttpResponse
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.transaction.TransactionConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.web.client.DefaultResponseErrorHandler
import org.springframework.web.client.ResponseErrorHandler
import org.springframework.web.client.RestTemplate
import spock.lang.Specification

import javax.transaction.Transactional

@ContextConfiguration(loader = SpringApplicationContextLoader.class, classes = Application.class)
@WebAppConfiguration
@IntegrationTest("server.port:8080")
@ActiveProfiles("test")
@Transactional
@TransactionConfiguration(defaultRollback = true)
class LoanControllerFunctionalSpec extends Specification {

  @Autowired
  LoanRepository loanRepository

  @Autowired
  JdbcTemplate jdbcTemplate;


  @Autowired
  CustomerRepository customerRepository


  def setup() {
    //jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY FALSE")
   // jdbcTemplate.execute("TRUNCATE TABLE LOAN")
  //  jdbcTemplate.execute("TRUNCATE TABLE CUSTOMER")
    jdbcTemplate.execute("drop table customer")
 //   jdbcTemplate.execute("COMMIT")
    //loanRepository.deleteAll()
    //customerRepository.deleteAll()
  }


  void "should grant a loan"() {
    given:
    CustomerDto customerData = CommonTestObject.createCustomerData()
    BigDecimal loanAmount = 300
    Date repaymentDate = CommonTestObject.generateDate(15);
    LoanRequestDto loanRequest = new LoanRequestDto(customerData, loanAmount, repaymentDate)

    when:
    ResponseEntity<LoanResponseDto> entity = new RestTemplate().postForEntity("http://localhost:8080/loan/getLoan", loanRequest, LoanResponseDto.class)

    then:
    entity.statusCode == HttpStatus.OK
    entity.body.loanReference == '0000000001'
  }

  void "should extend twice a loan"() {

    given:
    CustomerDto customerData = CommonTestObject.createCustomerData()
    BigDecimal loanAmount = 300
    Date repaymentDate = CommonTestObject.generateDate(15);
    LoanRequestDto loanRequest = new LoanRequestDto(customerData, loanAmount, repaymentDate)
    String loanReference = '0000000001'

    when:
    RestTemplate restTemplate = new RestTemplate()
    restTemplate.postForEntity("http://localhost:8080/loan/getLoan", loanRequest, LoanResponseDto.class)
    restTemplate.postForEntity("http://localhost:8080/loan/getLoanExtension", loanReference, LoanResponseDto.class)
    ResponseEntity<LoanResponseDto> entity = restTemplate.postForEntity("http://localhost:8080/loan/getLoanExtension", loanReference, LoanResponseDto.class)

    then:
    entity.statusCode == HttpStatus.OK
    entity.body.loanReference == '0000000001'
  }

  void "should refuse a loan application: more than 3 application form the same IP"() {
    given:
    CustomerDto customerData = CommonTestObject.createCustomerData()
    BigDecimal loanAmount = 300
    Date repaymentDate = CommonTestObject.generateDate(15);
    LoanRequestDto loanRequest = new LoanRequestDto(customerData, loanAmount, repaymentDate)

    RestTemplate restTemplate = new RestTemplate()
    CustomResponseErrorHandler errorHandler = new CustomResponseErrorHandler()
    restTemplate.setErrorHandler(errorHandler);

    when:
    restTemplate.postForEntity("http://localhost:8080/loan/getLoan", loanRequest, LoanResponseDto.class)
    restTemplate.postForEntity("http://localhost:8080/loan/getLoan", loanRequest, LoanResponseDto.class)
    restTemplate.postForEntity("http://localhost:8080/loan/getLoan", loanRequest, LoanResponseDto.class)
    ResponseEntity<LoanResponseDto> entity = restTemplate.postForEntity("http://localhost:8080/loan/getLoan", loanRequest, LoanResponseDto.class)

    then:
    entity.statusCode == HttpStatus.OK
    entity.body.loanReference == null

  }

  void "should return loan history"() {

    given:
    CustomerDto customerData = CommonTestObject.createCustomerData()
    BigDecimal loanAmount = 300
    Date repaymentDate = CommonTestObject.generateDate(15);
    LoanRequestDto loanRequest = new LoanRequestDto(customerData, loanAmount, repaymentDate)

    RestTemplate restTemplate = new RestTemplate()

    when:
    restTemplate.postForEntity("http://localhost:8080/loan/getLoan", loanRequest, LoanResponseDto.class)
    restTemplate.postForEntity("http://localhost:8080/loan/getLoan", loanRequest, LoanResponseDto.class)
    restTemplate.postForEntity("http://localhost:8080/loan/getLoan", loanRequest, LoanResponseDto.class)

    ResponseEntity entity = restTemplate.getForEntity("http://localhost:8080/loan/getLoanHistory/1", List.class)

    then:
    entity.statusCode == HttpStatus.OK
    entity.getBody().size() == 3
    entity.getBody().get(2).loanReference == '0000000001'

  }


  class CustomResponseErrorHandler implements ResponseErrorHandler {

    private ResponseErrorHandler errorHandler = new DefaultResponseErrorHandler();

    private Map<String, Object> properties = new HashMap<String, Object>();

    public boolean hasError(ClientHttpResponse response) throws IOException {
      return errorHandler.hasError(response);
    }

    public void handleError(ClientHttpResponse response) throws IOException {
      properties.put("code", response.getStatusCode())
      properties.put("header", response.getHeaders())
      throw new Exception()
    }

    public Map<String, Object> getProperties() {
      return properties;
    }
  }


}