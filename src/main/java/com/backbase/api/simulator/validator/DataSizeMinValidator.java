package com.backbase.api.simulator.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.springframework.util.unit.DataSize;

public class DataSizeMinValidator implements ConstraintValidator<DataSizeMin, DataSize> {

    private DataSize min;

    @Override
    public void initialize(DataSizeMin dataSizeMin) {
        this.min = DataSize.of(dataSizeMin.value(), dataSizeMin.unit());
    }

    @Override
    public boolean isValid(DataSize value, ConstraintValidatorContext context) {
        return value.compareTo(min) >= 0;
    }
}
