package com.reliaquest.api.models;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateEmployeeInput {
    @NotBlank
    private String name;

    @Positive
    @NotNull
    private Integer salary;

    @Min(16)
    @Max(75)
    @NotNull private Integer age;

    @NotBlank
    private String title;
}
