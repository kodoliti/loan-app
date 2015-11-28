package com.finance.domain.repository;

import com.finance.domain.model.Customer;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;


@Transactional
public interface CustomerRepository extends CrudRepository<Customer, Long> {

    Customer findByIdentificationNumber(String identificationNumber);

    Customer findByIdentificationNumber(Pageable pageable);

}
