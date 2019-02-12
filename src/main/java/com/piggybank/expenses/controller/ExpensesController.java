package com.piggybank.expenses.controller;

import com.piggybank.expenses.dto.Expense;
import com.piggybank.expenses.repository.ExpenseQuery;
import com.piggybank.expenses.repository.ExpenseRepository;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RequestMapping("api/v1/expenses")
@CrossOrigin
class ExpensesController {

    private static final DateTimeFormatter YYYY_MM_DD = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final ExpenseRepository expenseRepository;

    public ExpensesController(final ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    @GetMapping
    public ResponseEntity<List<Expense>> expenses(
            @RequestParam(value = "date-start", required = false) final String dateStart,
            @RequestParam(value = "date-end", required = false) final String dateEnd) {

        final LocalDate startDate = Strings.isBlank(dateStart)
                ? null
                : LocalDate.parse(dateStart, YYYY_MM_DD);

        final LocalDate endDate = Strings.isBlank(dateEnd)
                ? null
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
