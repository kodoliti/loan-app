package com.finance.controller

import com.finance.conf.Application
import com.finance.controller.dto.CustomerDto
import com.finance.controller.dto.LoanRequestDto
import com.finance.controller.dto.LoanResponseDto
import com.finance.util.CommonTestObject
import org.springframework.boot.SpringApplication
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.client.ClientHttpResponse
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.web.WebAppConfiguration
import org.springframework.web.client.DefaultResponseErrorHandler
import org.springframework.web.client.ResponseErrorHandler
import org.springframework.web.client.RestTemplate
import spock.lang.Shared
import spock.lang.Specification

import javax.transaction.Transactional
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit


@WebAppConfiguration
@ContextConfiguration(classes = [Application.class])
@ActiveProfiles("test")
@Transactional
class LoanControllerFunctionalSpec extends Specification {

  @Shared
  ConfigurableApplicationContext context

  void setupSpec() {
    Future future = Executors
            .newSingleThreadExecutor().submit(
            new Callable() {
              @Override
              public ConfigurableApplicationContext call() throws Exception {
                return (ConfigurableApplicationContext) SpringApplication
                        .run(Application.class)
              }
            })
    context = future.get(60, TimeUnit.SECONDS)
  }

  void cleanupSpec() {
    if (context != null) {
      context.close()
    }
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
    String loanReference = '0000000001'

    when:
    ResponseEntity<LoanResponseDto> entity = new RestTemplate().postForEntity("http://localhost:8080/loan/getLoanExtension", loanReference, LoanResponseDto.class)
    entity = new RestTemplate().postForEntity("http://localhost:8080/loan/getLoanExtension", loanReference, LoanResponseDto.class)

    then:
    entity.statusCode == HttpStatus.OK
    entity.body.loanReference == '0000000001'
  }


  void "should grant second and third a loan"() {
    given:
    CustomerDto customerData = CommonTestObject.createCustomerData()
    BigDecimal loanAmount = 200
    Date repaymentDate = CommonTestObject.generateDate(20);
    LoanRequestDto loanRequest = new LoanRequestDto(customerData, loanAmount, repaymentDate)

    when:
    ResponseEntity<LoanResponseDto> entity2 = new RestTemplate().postForEntity("http://localhost:8080/loan/getLoan", loanRequest, LoanResponseDto.class)
    ResponseEntity<LoanResponseDto> entity3 = new RestTemplate().postForEntity("http://localhost:8080/loan/getLoan", loanRequest, LoanResponseDto.class)
    then:
    entity2.statusCode == HttpStatus.OK
    entity2.body.loanReference == '0000000002'
    entity3.body.loanReference == '0000000003'
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
    ResponseEntity entity = restTemplate.postForEntity("http://localhost:8080/loan/getLoan", loanRequest, LoanResponseDto.class)

    then:
    entity.statusCode == HttpStatus.OK
    entity.body.loanReference == null

  }

  void "should return loan history"() {
    when:
    ResponseEntity entity = new RestTemplate().getForEntity("http://localhost:8080/loan/getLoanHistory/1", List.class)

    then:
    entity.statusCode == HttpStatus.OK
    entity.getBody().size() == 3

    entity.getBody().get(2).loanReference == '0000000001'
    entity.getBody().get(2).loanExtensionDtoList.size() == 2
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