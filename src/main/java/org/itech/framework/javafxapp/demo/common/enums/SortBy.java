package org.itech.framework.javafxapp.demo.common.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum SortBy {
    DUE_DATE(1, "Due Date"), PRIORITY(2, "Priority");
    private final Integer code;
    private final String desc;

    SortBy(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static List<EnumObject> getAll(){
        return Arrays.stream(SortBy.values()).map(v -> new EnumObject(v.code, v.desc)).toList();
    }

    public static String getDescByCode(Integer code) {
        if (code == null) return "Unknown";
        return Arrays.stream(SortBy.values())
                .filter(status -> code.equals(status.getCode()))
                .findFirst()
                .map(SortBy::getDesc)
                .orElse("Unknown");
    }

    public static SortBy getByCodeOrThrow(Integer code) {
        return Arrays.stream(SortBy.values())
                .filter(status -> code.equals(status.getCode()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid task code: " + code));
    }
}
