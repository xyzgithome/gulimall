package com.atguigu.common.valid;

import com.atguigu.common.valid.annotation.Enums;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class EnumsConstraintValidator implements ConstraintValidator<Enums, Integer> {
    private Set<Integer> set = new HashSet<>();

    @Override
    public void initialize(Enums constraintAnnotation) {
        for (int value : constraintAnnotation.values()) {
            set.add(value);
        }
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (Objects.isNull(value)) {
            return true;
        }
        return set.contains(value);
    }
}
