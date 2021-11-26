package com.backbase.api.simulator.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;
import org.springframework.util.unit.DataUnit;

@Documented
@Constraint(validatedBy = DataSizeMinValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DataSizeMin {

    String message() default "must be more than or equal to {value} {unit}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    long value();

    DataUnit unit();
}