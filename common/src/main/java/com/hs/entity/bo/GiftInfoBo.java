package com.hs.entity.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GiftInfoBo {
    private int statusCode;
    private String statusMsg;
    private String notice;
    private Data data;

    @lombok.Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Data {
        private Long activityId;
        private int itemCount;
        private Long itemId;
        private int payType;
        private Long pid;
        private String presenterNick;
        private Long roomId;
        private int sendItemComboHits;
        private Long sendTimeStamp;
        private String senderAvatarUrl;
        private String senderNick;
        private Long senderUid;
        private Double totalGet;
        private Double totalPay;
        private String unionId;
    }


}
