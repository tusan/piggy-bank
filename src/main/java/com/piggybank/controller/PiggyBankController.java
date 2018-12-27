package com.piggybank.controller;

import com.piggybank.model.Expense;
import com.piggybank.repository.ExpenseQuery;
import com.piggybank.repository.ExpenseRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Controller("api/v1/expenses")
@CrossOrigin
public class PiggyBankController {

    private static final DateTimeFormatter YYYY_MM_DD = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final ExpenseRepository expenseRepository;

    public PiggyBankController(final ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    @GetMapping
    public ResponseEntity<List<Expense>> expenses(
      @RequestParam(value = "date-start", required = false) final String dateStart,
      @RequestParam(value = "date-end", required = false) final String dateEnd) {

        final LocalDate startDate = Strings.isBlank(dateStart)
                ? LocalDateTime.MIN.toLocalDate()
                : LocalDate.parse(dateStart, YYYY_MM_DD);

        final LocalDate endDate = Strings.isBlank(dateEnd)
                ? LocalDate.now()
                : LocalDate.parse(dateEnd, YYYY_MM_DD);

        final List<Expense> result = expenseRepository.find(ExpenseQuery.builder()
                .setDateStart(startDate)
                .setDateEnd(endDate)
                .build());

        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody final Expense expense) {
        try {
            expenseRepository.save(expense);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (final Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
