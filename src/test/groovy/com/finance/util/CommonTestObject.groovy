package com.finance.util

import com.finance.controller.dto.CustomerDto
import org.joda.time.LocalDate


class CommonTestObject {


  public static Date generateDate(int daysToAdd) {
    return LocalDate.now().plusDays(daysToAdd).toDate();
  }

  public static Date generateDate(int year, int month, int dayOfMonth) {
    return new LocalDate(year, month, dayOfMonth).toDate();
  }

  public static CustomerDto createCustomerData() {
    CustomerDto customerData = new CustomerDto()
    customerData.setFirstName("Robert")
    customerData.setSurname("Lewandowski")
    customerData.setDateOfBirth(new LocalDate(1988, 8, 21).toDate())
    customerData.setIdentificationNumber("88082103716")
    customerData.seteMailAddress("rlewandowski@gmail.com")
    customerData.setMobilePhoneNumber("511-444-123")
    customerData.setApartmentNumber("103")
    customerData.setBuildingNumber("44")
    customerData.setStreet("Zlota")
    customerData.setCity("Warszawa")
    customerData.setPostcode("02-020")
    return customerData;
  }

}

