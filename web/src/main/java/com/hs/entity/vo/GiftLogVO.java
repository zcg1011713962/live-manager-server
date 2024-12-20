package com.hs.entity.vo;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

@Data
@ToString
@Getter
@Builder
public class GiftLogVO {
    // 房间号
    private Long roomId;
    // 主播昵称
    private String anchorName;
    // 送礼人唯一ID
    private Long senderId;
    // 送礼人昵称
    private String senderName;
    // 发言人头像
    private String senderAvatarUrl;
    // 道具（礼物）ID
    private Long giftId;
    // 消费数量
    private int amount;
    // 送礼连击数
    private int comboCount;
    // 扣费策略 (1002-钻石, 1003-金币, 1018-免费背包礼物, 3008-金豆)
    private String chargePolicy;
    // 总支付
    private Double totalPayment;
    // 主播总获得宝石
    private Double totalGems;
    // 发送时间戳 (秒)
    private Long sentTimestamp;
    // 活动类型
    private String activityDesc;
}
