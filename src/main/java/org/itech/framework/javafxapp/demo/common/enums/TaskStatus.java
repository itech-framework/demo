package org.itech.framework.javafxapp.demo.common.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum TaskStatus {
    PROGRESS(1, "In Progress"), COMPLETE(2, "Completed"), OVER_DUE(3, "Over Due"), CANCEL(4, "Cancel");
    private final Integer code;
    private final String desc;

    TaskStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static List<EnumObject> getAll(){
        return Arrays.stream(TaskStatus.values()).map(v -> new EnumObject(v.code, v.desc)).toList();
    }

    public static String getDescByCode(Integer code) {
        if (code == null) return "Unknown";
        return Arrays.stream(TaskStatus.values())
                .filter(status -> code.equals(status.getCode()))
                .findFirst()
                .map(TaskStatus::getDesc)
                .orElse("Unknown");
    }

    public static TaskStatus getByCodeOrThrow(Integer code) {
        return Arrays.stream(TaskStatus.values())
                .filter(status -> code.equals(status.getCode()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid task code: " + code));
    }

}
