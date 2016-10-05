package com.finance.domain.repository;

import com.finance.domain.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Customer findByIdentificationNumber(String identificationNumber);

}
