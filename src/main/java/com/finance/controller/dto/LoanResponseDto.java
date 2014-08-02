package com.finance.controller.dto;

public class LoanResponseDto {

    private String loanReference;

    private String message;

    private boolean isExtended;

    public LoanResponseDto() {
    }

    public LoanResponseDto(String loanReference, String message) {
        this.loanReference = loanReference;
        this.message = message;
    }

    public LoanResponseDto(String loanReference, String message, boolean isExtended) {
        this.loanReference = loanReference;
        this.message = message;
        this.isExtended = isExtended;
    }


    public String getMessage() {
        return message;
    }

    public String getLoanReference() {
        return loanReference;
    }

    public boolean isExtended() {
        return isExtended;
    }

    public void setLoanReference(String loanReference) {
        this.loanReference = loanReference;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setExtended(boolean extended) {
        isExtended = extended;
    }
}
