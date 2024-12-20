package com.hs.enums;

public enum ActivityType {
    ALL(0L, "无"),
    BOX(100438L, "拆盒子");

    private final Long type;

    private final String desc;

    ActivityType(Long type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public Long getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }


    // 通过 key 获取对应的枚举值
    public static ActivityType fromKey(Long key) {
        for (ActivityType gift : ActivityType.values()) {
            if (gift.getType().longValue() == key) {
                return gift;
            }
        }
        throw new IllegalArgumentException("Unexpected key: " + key);
    }

    // 通过 value 获取对应的枚举值
    public static ActivityType fromValue(String value) {
        for (ActivityType gift : ActivityType.values()) {
            if (gift.getDesc().equals(value)) {
                return gift;
            }
        }
        throw new IllegalArgumentException("Unexpected value: " + value);
    }
}
