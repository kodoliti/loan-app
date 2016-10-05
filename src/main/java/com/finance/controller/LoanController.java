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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Artur Wojcik
 */

@RestController
@RequestMapping(value = "/loan", produces = "application/json")
public class LoanController {

    private static final Logger logger = LoggerFactory.getLogger(LoanController.class);

    private LoanApplicationService loanApplicationService;

    private LoanMapper loanMapper;

    @Autowired
    public LoanController(LoanApplicationService loanApplicationService, LoanMapper loanMapper) {
        this.loanApplicationService = loanApplicationService;
        this.loanMapper = loanMapper;
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<LoanResponseDto> createLoan(@RequestBody LoanRequestDto loanRequestDto, HttpServletRequest httpRequest) {
        String ipAddress = httpRequest.getRemoteAddr();
        try {
            Loan loan = loanApplicationService.createLoan(loanRequestDto, ipAddress);
            LoanResponseDto loanResponseDto = new LoanResponseDto(loan.getLoanReference(), "The loan application was accepted. Loan will be granted.");
            return new ResponseEntity(loanResponseDto, HttpStatus.CREATED);
        } catch (CreateLoanException e) {
            String message = String.format("Error has occurred during loan's creation : %s", e.getMessage());
            return new ResponseEntity(new LoanResponseDto(null, message), HttpStatus.BAD_REQUEST);
        }
    }


    @RequestMapping(value = "/{loanReference}/extension", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<LoanResponseDto> createLoanExtension(@PathVariable("loanReference") @Valid @NotBlank String loanReference) {
        LoanResponseDto loanResponseDto;
        try {
            Loan loan = loanApplicationService.createLoanExtension(loanReference);
            loanResponseDto = new LoanResponseDto(loan.getLoanReference(), "The loan was extended correctly.", true);
            return new ResponseEntity(loanResponseDto, HttpStatus.CREATED);
        } catch (CreateLoanExtensionException e) {
            loanResponseDto = new LoanResponseDto(loanReference, String.format("Error has occurred during loan's extension : %s", e.getMessage()), false);
            return new ResponseEntity(loanResponseDto, HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/customer/{customerId}/history", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<LoanDto>> getLoanHistory(@PathVariable("customerId") Long customerId) {
        List<Loan> loanList = loanApplicationService.getLoanHistory(customerId);
        return new ResponseEntity(loanList.stream().map((loan) -> loanMapper.getLoanDto(loan)).collect(Collectors.toList()), HttpStatus.OK);
    }

}
