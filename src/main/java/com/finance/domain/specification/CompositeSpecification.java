package com.finance.domain.specification;

import java.util.Arrays;
import java.util.List;

public class CompositeSpecification<T> implements Specification<T> {

    private String message;

    private List<Specification<T>> specifications;

    public CompositeSpecification (Specification<T>... specifications){
        this.specifications = Arrays.asList(specifications);
    }

    public Specification<T> and(Specification<T> other) {
        return new AndSpecification<T>(this, other);
    }

    public Specification<T> or(Specification<T> other) {
        return new OrSpecification<T>(this, other);
    }

    public Specification<T> not() {
        return new NotSpecification<T>(this);
    }

    @Override
    public boolean isSatisfiedBy(T candidate) {
        for (Specification<T> spec : specifications) {
            if (!spec.isSatisfiedBy(candidate)){
                this.message = spec.getMessage();
                return false;
            }
        }
        return true;
    }

    public String getMessage(){
        return this.message;
    }

}
