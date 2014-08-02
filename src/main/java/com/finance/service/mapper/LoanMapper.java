package com.finance.service.mapper;

import com.finance.controller.dto.LoanDto;
import com.finance.controller.dto.LoanExtensionDto;
import com.finance.domain.model.Loan;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class LoanMapper {

    public LoanDto getLoanDto(Loan loan) {
        LoanDto loanDto = new LoanDto();
        BeanUtils.copyProperties(loan, loanDto);
        loanDto.setLoanReference(loan.getLoanReference());
        loanDto.setLoanExtensionDtoList(loan.getLoanExtensionList()
                .stream()
                .map((loanExtension) -> {
                    LoanExtensionDto loanExtensionDto = new LoanExtensionDto();
                    BeanUtils.copyProperties(loanExtension, loanExtensionDto);
                    return loanExtensionDto;
                })
                .collect(Collectors.toList()));

        return loanDto;
    }

}
