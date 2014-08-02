package com.finance.domain.specification;

public interface Specification<T> {

    boolean isSatisfiedBy(T candidate);

    Specification<T> and(Specification<T> other);

    Specification<T> or(Specification<T> other);

    Specification<T> not();

    String getMessage();

}
