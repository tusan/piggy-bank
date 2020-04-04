package com.piggybank.service.expenses.dto;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static com.piggybank.service.expenses.dto.ExpenseType.HOUSE;
import static com.piggybank.service.expenses.dto.ExpenseType.MOTORBIKE;
import static java.time.Month.NOVEMBER;

public class ExpenseDtoTest {
  private ObjectMapper mapper;

  @Before
  public void setUp() {
    mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
  }

  @Test
  public void shouldBuildAnExpense() throws Exception {
    final String initialJson =
        " {\n"
            + "        \"type\" : \"MOTORBIKE\",\n"
            + "        \"date\" : \"20181127\",\n"
            + "        \"amount\" : \"24.5\",\n"
            + "        \"description\": \"Fuel\"\n"
            + "    }";

    Assert.assertEquals(
        ExpenseDto.newBuilder()
            .setDate(LocalDate.of(2018, NOVEMBER, 27))
            .setDescription("Fuel")
            .setType(MOTORBIKE)
            .setAmount(24.5)
            .build(),
        mapper.readValue(initialJson, ExpenseDto.class));
  }

  @Test
  public void shouldBuildAnExpenseWithoutDescription() throws Exception {
    final String initialJson =
        " {\n"
            + "        \"type\" : \"MOTORBIKE\",\n"
            + "        \"date\" : \"20181127\",\n"
            + "        \"amount\" : \"24.5\""
            + "    }";

    Assert.assertEquals(
        ExpenseDto.newBuilder()
            .setDate(LocalDate.of(2018, NOVEMBER, 27))
            .setType(MOTORBIKE)
            .setAmount(24.5)
            .build(),
        mapper.readValue(initialJson, ExpenseDto.class));
  }

  @Test
  public void shouldBuildAListOfExpenses() throws Exception {
    final String initialJson =
        "[\n"
            + "    {\n"
            + "        \"type\" : \"MOTORBIKE\",\n"
            + "        \"date\" : \"20181127\",\n"
            + "        \"amount\": 24.5,\n"
            + "        \"description\": \"Fuel\"\n"
            + "    },\n"
            + "    {\n"
            + "        \"type\" : \"HOUSE\",\n"
            + "        \"date\" : \"20181129\",\n"
            + "        \"amount\": 400,\n"
            + "        \"description\": \"Rent\"\n"
            + "    }\n"
            + "]";

    final List<ExpenseDto> expected =
        Arrays.asList(
            ExpenseDto.newBuilder()
                .setDate(LocalDate.of(2018, NOVEMBER, 27))
                .setDescription("Fuel")
                .setType(MOTORBIKE)
                .setAmount(24.5)
                .build(),
            ExpenseDto.newBuilder()
                .setDate(LocalDate.of(2018, NOVEMBER, 29))
                .setDescription("Rent")
                .setType(HOUSE)
                .setAmount(400)
                .build());

    Assert.assertEquals(
        expected, mapper.readValue(initialJson, new TypeReference<List<ExpenseDto>>() {}));
  }
}
