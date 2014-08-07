package com.finance.domain.model;


import javax.persistence.MappedSuperclass;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@MappedSuperclass
public abstract class AuditableDate {

    @Temporal(TemporalType.TIMESTAMP)
    private Date created = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    private Date updated = new Date();

    public Date getUpdated() {
        return updated;
    }

    public Date getCreated() {
        return created;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updated = new Date();
    }

}
