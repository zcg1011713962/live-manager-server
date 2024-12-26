package com.hs.enums;

public enum ChargePolicyType {
    DIAMOND(1002, "钻石"),
    COIN(1003, "金币"),
    FREEGIFT(1018, "免费背包礼"),
    GOLDBean(3008, "金豆"),
    OTHER(-1, "其他");

    private final int type;

    private final String desc;

    ChargePolicyType(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public int getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }

    // 通过 key 获取对应的枚举值
    public static ChargePolicyType fromKey(int key) {
        for (ChargePolicyType gift : ChargePolicyType.values()) {
            if (gift.getType() == key) {
                return gift;
            }
        }
        return OTHER;
    }

    // 通过 value 获取对应的枚举值
    public static ChargePolicyType fromValue(String value) {
        for (ChargePolicyType gift : ChargePolicyType.values()) {
            if (gift.getDesc().equals(value)) {
                return gift;
            }
        }
        return OTHER;
    }
}
