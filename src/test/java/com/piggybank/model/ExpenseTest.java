package com.piggybank.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ExpenseTest {
    private ObjectMapper mapper;

    @Before
    public void setUp() {
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
    }

    @Test
    public void shouldBuildAnExpense() throws Exception {
      final String initialJson = " {\n" +
                "        \"type\" : \"MOTO\",\n" +
                "        \"date\" : \"20181127\",\n" +
                "        \"amount\" : \"24.5\",\n" +
                "        \"description\": \"Benzina\"\n" +
                "    }";


        Assert.assertEquals(Expense.newBuilder()
                .setDate(LocalDate.of(2018, Month.NOVEMBER, 27))
                .setDescription("Benzina")
                .setType(ExpenseType.MOTO)
                .setAmount(24.5)
                .build(), mapper.readValue(initialJson, Expense.class));
    }

    @Test
    public void shouldBuildAnExpenseWithoutDescription() throws Exception {
      final String initialJson = " {\n" +
            "        \"type\" : \"MOTO\",\n" +
            "        \"date\" : \"20181127\",\n" +
            "        \"amount\" : \"24.5\"" +
            "    }";

        Assert.assertEquals(Expense.newBuilder()
            .setDate(LocalDate.of(2018, Month.NOVEMBER, 27))
            .setType(ExpenseType.MOTO)
            .setAmount(24.5)
            .build(), mapper.readValue(initialJson, Expense.class));
    }

    @Test
    public void shouldBuildAListOfExpenses() throws Exception {
      final String initialJson = "[\n" +
                "    {\n" +
                "        \"type\" : \"MOTO\",\n" +
                "        \"date\" : \"20181127\",\n" +
                "        \"amount\": 24.5,\n" +
                "        \"description\": \"Benzina\"\n" +
                "    },\n" +
                "    {\n" +
                "        \"type\" : \"CASA\",\n" +
                "        \"date\" : \"20181129\",\n" +
                "        \"amount\": 400,\n" +
                "        \"description\": \"Affitto\"\n" +
                "    }\n" +
                "]";

      final List<Expense> expected = Arrays.asList(
                Expense.newBuilder()
                        .setDate(LocalDate.of(2018, Month.NOVEMBER, 27))
                        .setDescription("Benzina")
                        .setType(ExpenseType.MOTO)
                        .setAmount(24.5)
                        .build(),
                Expense.newBuilder()
                        .setDate(LocalDate.of(2018, Month.NOVEMBER, 29))
                        .setDescription("Affitto")
                        .setType(ExpenseType.CASA)
                        .setAmount(400)
                        .build());

        Assert.assertEquals(expected, mapper.readValue(initialJson, new TypeReference<List<Expense>>() {
        }));
    }

}
