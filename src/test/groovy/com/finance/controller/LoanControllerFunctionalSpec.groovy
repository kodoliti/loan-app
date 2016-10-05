package com.finance.controller

import com.finance.conf.Application
import com.finance.controller.dto.CustomerDto
import com.finance.controller.dto.LoanRequestDto
import com.finance.controller.dto.LoanResponseDto
import com.finance.domain.repository.CustomerRepository
import com.finance.domain.repository.LoanRepository
import com.finance.util.CommonTestObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.IntegrationTest
import org.springframework.boot.test.SpringApplicationContextLoader
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.client.ClientHttpResponse
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.web.client.DefaultResponseErrorHandler
import org.springframework.web.client.ResponseErrorHandler
import org.springframework.web.client.RestTemplate
import spock.lang.Specification

@ContextConfiguration(loader = SpringApplicationContextLoader.class, classes = Application.class)
@WebAppConfiguration
@EnableAutoConfiguration
@IntegrationTest("server.port:8080")
class LoanControllerFunctionalSpec extends Specification {

  @Autowired
  LoanRepository loanRepository

  @Autowired
  CustomerRepository customerRepository


  def setup() {
    loanRepository.deleteAll()
  }


  void "should grant a loan"() {
    given:
    CustomerDto customerData = CommonTestObject.createCustomerData()
    BigDecimal loanAmount = 300
    Date repaymentDate = CommonTestObject.generateDate(15);
    LoanRequestDto loanRequest = new LoanRequestDto(customerData, loanAmount, repaymentDate)

    when:
    ResponseEntity<LoanResponseDto> entity = new RestTemplate().postForEntity("http://localhost:8080/loan", loanRequest, LoanResponseDto.class)

    then:
    entity.statusCode == HttpStatus.CREATED
  }

  void "should extend twice a loan"() {
    given:
    CustomerDto customerData = CommonTestObject.createCustomerData()
    BigDecimal loanAmount = 300
    Date repaymentDate = CommonTestObject.generateDate(15);
    LoanRequestDto loanRequest = new LoanRequestDto(customerData, loanAmount, repaymentDate)


    when:
    RestTemplate restTemplate = new RestTemplate()
    ResponseEntity<LoanResponseDto> entity1 = restTemplate.postForEntity("http://localhost:8080/loan", loanRequest, LoanResponseDto.class)
    ResponseEntity<LoanResponseDto> entity2 = restTemplate.postForEntity("http://localhost:8080/loan/"+entity1.getBody().loanReference+"/extension", null, LoanResponseDto.class)
    ResponseEntity<LoanResponseDto> entity3 = restTemplate.postForEntity("http://localhost:8080/loan/"+entity2.getBody().loanReference+"/extension", null, LoanResponseDto.class)

    then:
    entity3.statusCode == HttpStatus.CREATED
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
    restTemplate.postForEntity("http://localhost:8080/loan", loanRequest, LoanResponseDto.class)
    restTemplate.postForEntity("http://localhost:8080/loan", loanRequest, LoanResponseDto.class)
    restTemplate.postForEntity("http://localhost:8080/loan", loanRequest, LoanResponseDto.class)
    ResponseEntity<LoanResponseDto> entity = restTemplate.postForEntity("http://localhost:8080/loan", loanRequest, LoanResponseDto.class)

    then:
    entity.statusCode == HttpStatus.BAD_REQUEST
    entity.body.loanReference == null

  }

  void "should return loan history"() {

    given:
    String personalId = "88082103716"
    CustomerDto customerData = CommonTestObject.createCustomerData()
    customerData.setIdentificationNumber(personalId)
    BigDecimal loanAmount = 300
    Date repaymentDate = CommonTestObject.generateDate(15)
    LoanRequestDto loanRequest = new LoanRequestDto(customerData, loanAmount, repaymentDate)

    RestTemplate restTemplate = new RestTemplate()

    when:
    restTemplate.postForEntity("http://localhost:8080/loan", loanRequest, LoanResponseDto.class)
    restTemplate.postForEntity("http://localhost:8080/loan", loanRequest, LoanResponseDto.class)
    restTemplate.postForEntity("http://localhost:8080/loan", loanRequest, LoanResponseDto.class)

    def customer = customerRepository.findByIdentificationNumber(personalId)

    ResponseEntity entity = restTemplate.getForEntity("http://localhost:8080/loan/customer/"+customer.id+"/history", List.class)

    then:
    entity.statusCode == HttpStatus.OK
    entity.getBody().size() == 3
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
    }

    public Map<String, Object> getProperties() {
      return properties;
    }
  }


}