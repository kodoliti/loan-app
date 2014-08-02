package com.finance.domain.specification;

public class AndSpecification<T> extends CompositeSpecification<T> {

    private String message;

    private Specification<T> a;
    private Specification<T> b;

    public AndSpecification(Specification<T> a, Specification<T> b) {
        this.a = a;
        this.b = b;
    }

    public boolean isSatisfiedBy(T candidate) {
     /*   boolean isSatisfiedByA = a.isSatisfiedBy(candidate);

        if (!isSatisfiedByA) {
            this.message = a.getMessage();
            return false;
        }
        boolean isSatisfiedByB = b.isSatisfiedBy(candidate);
        if (!isSatisfiedByB) {
            this.message = b.getMessage();
            return false;
        } */
        return a.isSatisfiedBy(candidate) && b.isSatisfiedBy(candidate);
    }


    public String getMessage() {
        return this.message;
    }

}
