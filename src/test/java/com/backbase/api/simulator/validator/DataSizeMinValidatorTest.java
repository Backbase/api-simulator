package com.backbase.api.simulator.validator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.backbase.api.simulator.config.WireMockServerConfiguration;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.util.unit.DataSize;

class DataSizeMinValidatorTest {

    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void testValidDataSize() {
        WireMockServerConfiguration configuration = new WireMockServerConfiguration();
        Set<ConstraintViolation<WireMockServerConfiguration>> violations = validator.validate(configuration);
        assertEquals(Set.of(), violations);
    }

    @Test
    void testInvalidDataSize() {
        WireMockServerConfiguration configuration = new WireMockServerConfiguration();
        configuration.setHeaderBufferSize(DataSize.ofKilobytes(4));
        Set<ConstraintViolation<WireMockServerConfiguration>> violations = validator.validate(configuration);
        assertEquals(1, violations.size());
        assertEquals("must be more than or equal to 8 KILOBYTES", violations.iterator().next().getMessage());
    }
}
