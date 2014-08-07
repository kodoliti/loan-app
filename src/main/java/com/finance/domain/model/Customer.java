package com.finance.domain.model;

import com.finance.domain.annotation.DomainAggregateRoot;

import javax.persistence.*;
import java.util.Date;

@Entity
@DomainAggregateRoot
public class Customer extends AuditableDate {

    @Id
    @GeneratedValue
    private Long id;

    private String firstName;

    private String surname;

    @Temporal(TemporalType.DATE)
    private Date dateOfBirth;

    private String identificationNumber;

    @Embedded
    private Contact contactDetail;

    public Customer() {
    }

    public Customer(String firstName, String surname, Date dateOfBirth, String identificationNumber, Contact contactDetail) {
        this.firstName = firstName;
        this.surname = surname;
        this.dateOfBirth = dateOfBirth;
        this.identificationNumber = identificationNumber;
        this.contactDetail = contactDetail;
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getSurname() {
        return surname;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public String getIdentificationNumber() {
        return identificationNumber;
    }

    public Contact getContactDetail() {
        return contactDetail;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Customer customer = (Customer) o;

        if (id != null ? !id.equals(customer.id) : customer.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
