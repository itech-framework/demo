package org.itech.framework.javafxapp.demo.common.enums;

import java.util.Arrays;
import java.util.List;

import lombok.Getter;

@Getter
public enum DueStatus {
	
	OVERDUE(1, "Overdue"), DUE_TODAY(2, "Due Today"), DUE_THIS_WEEK(3, "Due This Week"), DUE_THIS_MONTH(4, "Due This Month");
    private final Integer code;
    private final String desc;

    DueStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static List<EnumObject> getAll(){
        return Arrays.stream(DueStatus.values()).map(v -> new EnumObject(v.code, v.desc)).toList();
    }

    public static String getDescByCode(Integer code) {
        if (code == null) return "Unknown";
        return Arrays.stream(DueStatus.values())
                .filter(status -> code.equals(status.getCode()))
                .findFirst()
                .map(DueStatus::getDesc)
                .orElse("Unknown");
    }

    public static DueStatus getByCodeOrThrow(Integer code) {
        return Arrays.stream(DueStatus.values())
                .filter(status -> code.equals(status.getCode()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid task code: " + code));
    }

}
