package org.itech.framework.javafxapp.demo.common.enums;

import lombok.Getter;

public record EnumObject(Integer code, String desc) {

    @Override
    public String toString() {
        return this.desc;
    }
}
