package com.finance.domain.repository;

import com.finance.domain.model.Loan;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Transactional
public interface LoanRepository extends CrudRepository<Loan, Long> {

    Loan findById(Long id);

    List<Loan> findByIpAddress(String ipAddress);

    List<Loan> findByIpAddressAndCreatedBetween(String ipAddress, Date dateFrom, Date dateTo);

    List<Loan> findByCustomerIdOrderByCreatedDesc(Long customerId);
}
