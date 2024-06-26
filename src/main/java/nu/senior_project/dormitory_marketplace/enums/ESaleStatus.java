package nu.senior_project.dormitory_marketplace.enums;

import lombok.Getter;

@Getter
public enum ESaleStatus {
    NEEDS_SELLER_VALIDATION((short) 1),
    NEEDS_BUYER_VALIDATION((short) 2),
    PAID((short) 3),
    APPROVED_BY_BUYER((short) 4),
    CANCELLED_BY_BUYER((short) 5),
    CANCELLED_BY_SELLER((short) 6),
    FINISHED((short) 7);

    private final short value;

    ESaleStatus(short value) {
        this.value = value;
    }

    public static String getString(short number) {
        if (number == 1) {
            return "NEEDS_SELLER_VALIDATION";
        } else if (number == 2) {
            return "NEEDS_BUYER_VALIDATION";
        } else if (number == 3) {
            return "PAID";
        } else if (number == 4) {
            return "APPROVED_BY_BUYER";
        } else if (number == 5) {
            return "CANCELLED_BY_BUYER";
        } else if (number == 6) {
            return "CANCELLED_BY_SELLER";
        } else {
            return "FINISHED";
        }
    }
}
