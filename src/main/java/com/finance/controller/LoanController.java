package com.finance.controller;

import com.finance.controller.dto.LoanDto;
import com.finance.controller.dto.LoanRequestDto;
import com.finance.controller.dto.LoanResponseDto;
import com.finance.domain.exception.CreateLoanException;
import com.finance.domain.exception.CreateLoanExtensionException;
import com.finance.domain.model.Loan;
import com.finance.service.LoanApplicationService;
import com.finance.service.mapper.LoanMapper;
import org.hibernate.validator.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Artur Wojcik
 */

@RestController
@RequestMapping(value = "/loan")
public class LoanController {

    private static final Logger logger = LoggerFactory.getLogger(LoanController.class);

    private LoanApplicationService loanApplicationService;

    private LoanMapper loanMapper;

    @Autowired
    public LoanController(LoanApplicationService loanApplicationService, LoanMapper loanMapper) {
        this.loanApplicationService = loanApplicationService;
        this.loanMapper = loanMapper;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET, consumes = "application/json", produces = "application/json")
    @ResponseBody
    public LoanResponseDto getLoan(@RequestParam LoanRequestDto loanRequestDto, HttpServletRequest httpRequest) {
        logger.info(loanRequestDto.toString());
        String ipAddress = httpRequest.getRemoteAddr();
        try {
            Loan loan = loanApplicationService.createLoan(loanRequestDto, ipAddress);
            return new LoanResponseDto(loan.getLoanReference(), "The loan application was accepted. Loan will be granted.");
        } catch (CreateLoanException e) {
            return new LoanResponseDto(null, String.format("Error has occurred during loan's creation : %s", e.getMessage()));
        }
    }


    @RequestMapping(value = "/{loanReference}/extension", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public LoanResponseDto getLoanExtension(@PathVariable("loanReference") @Valid @NotBlank String loanReference) {
        try {
            Loan loan = loanApplicationService.createLoanExtension(loanReference);
            return new LoanResponseDto(loan.getLoanReference(), "The loan was extended correctly.", true);
        } catch (CreateLoanExtensionException e) {
            return new LoanResponseDto(loanReference, String.format("Error has occurred during loan's extension : %s", e.getMessage()), false);
        }
    }

    @RequestMapping(value = "/{customerId}/history", method = RequestMethod.GET)
    @ResponseBody
    public List<LoanDto> getLoanHistory(@PathVariable("customerId") Long customerId) {
        List<Loan> loanList = loanApplicationService.getLoanHistory(customerId);
        return loanList.stream().map((loan) -> loanMapper.getLoanDto(loan)).collect(Collectors.toList());
    }

}
