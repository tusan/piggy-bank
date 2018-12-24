package com.piggybank.controller;

import com.piggybank.model.Expense;
import com.piggybank.model.ExpenseType;
import com.piggybank.repository.ExpenseQuery;
import com.piggybank.repository.ExpenseRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller("api/v1/expenses")
@CrossOrigin
public class PiggyBankController {

    private static final DateTimeFormatter YYYY_MM_DD = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final ExpenseRepository expenseRepository;

    public PiggyBankController(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    @GetMapping
    public ResponseEntity<List<Expense>> expenses(
            @RequestParam(value = "date-start", required = false) String dateStart,
            @RequestParam(value = "date-start", required = false) String dateEnd,
            @RequestParam(value = "category", required = false, defaultValue = "ALL") ExpenseType category) {

        LocalDate startDate = Strings.isBlank(dateStart)
                ? LocalDateTime.MIN.toLocalDate()
                : LocalDate.parse(dateStart, YYYY_MM_DD);

        LocalDate endDate = Strings.isBlank(dateEnd)
                ? LocalDate.now()
                : LocalDate.parse(dateEnd, YYYY_MM_DD);

        List<Expense> result = expenseRepository.find(ExpenseQuery.builder()
                .setDateStart(startDate)
                .setDateEnd(endDate)
                .setCategory(category)
                .build());

        return ResponseEntity.ok(result);
    }
}
