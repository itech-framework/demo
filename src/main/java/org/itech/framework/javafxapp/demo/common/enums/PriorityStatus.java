package org.itech.framework.javafxapp.demo.common.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum PriorityStatus {
    LOW(1, "Low Priority"),
    MEDIUM(2, "Medium Priority"),
    HIGH(3, "High Priority"),
    CRITICAL(4, "Critical Priority");

    private final Integer code;
    private final String desc;

    PriorityStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static List<EnumObject> getAll() {
        return Arrays.stream(PriorityStatus.values())
                .map(v -> new EnumObject(v.code, v.desc))
                .toList();
    }

    public static String getDescByCode(Integer code) {
        if (code == null) return "Unknown";
        return Arrays.stream(PriorityStatus.values())
                .filter(priority -> code.equals(priority.getCode()))
                .findFirst()
                .map(PriorityStatus::getDesc)
                .orElse("Unknown");
    }
    public static PriorityStatus getByCodeOrThrow(Integer code) {
        return Arrays.stream(PriorityStatus.values())
                .filter(priority -> code.equals(priority.getCode()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid priority code: " + code));
    }
}