package com.hs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hs.db.entity.GiftLog;
import com.hs.db.mapper.GiftLogMapper;
import com.hs.entity.LogicResponse;
import com.hs.entity.PageResponse;
import com.hs.entity.bo.GiftLogBO;
import com.hs.enums.ErrorCode;
import com.hs.service.GiftLogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
@Slf4j
@Service
public class GiftLogServiceImpl implements GiftLogService {
    @Autowired
    private GiftLogMapper giftLogMapper;

    @Override
    public CompletableFuture<LogicResponse<PageResponse<GiftLogBO>>> searchGiftLog(Integer pageNum, Integer pageSize, String roomId, String giftId, String senderId, Long activityId) {

        return CompletableFuture.supplyAsync(() -> {
            QueryWrapper<GiftLog> queryWrapper = new QueryWrapper<>();

            // 构建查询条件
            queryWrapper.eq(StringUtils.isNotBlank(roomId), "room_id", roomId);
            queryWrapper.eq(StringUtils.isNotBlank(giftId), "gift_id", giftId);
            queryWrapper.eq(StringUtils.isNotBlank(senderId), "sender_id", senderId);
            queryWrapper.eq(activityId != null, "activity_id", activityId);
            queryWrapper.orderByAsc("sent_timestamp");

            List<GiftLogBO> giftLogBOList;

            if (pageSize == null || pageNum == null) {
                // 如果分页参数为空，查询所有记录
                List<GiftLog> giftLogs = giftLogMapper.selectList(queryWrapper);
                giftLogBOList = giftLogs.stream()
                        .map(d -> GiftLogBO.builder()
                                .activityId(d.getActivityId())
                                .roomId(d.getRoomId())
                                .anchorName(d.getAnchorName())
                                .senderId(d.getSenderId())
                                .senderName(d.getSenderName())
                                .senderAvatarUrl(d.getSenderAvatarUrl())
                                .giftId(d.getGiftId())
                                .amount(d.getAmount())
                                .comboCount(d.getComboCount())
                                .chargePolicy(d.getChargePolicy())
                                .totalPayment(d.getTotalPayment())
                                .totalGems(d.getTotalGems())
                                .sentTimestamp(d.getSentTimestamp())
                                .build())
                        .collect(Collectors.toList());

                // 返回所有记录的 PageResponse
                PageResponse<GiftLogBO> pageResponse = new PageResponse<>(giftLogBOList, giftLogs.size(), giftLogs.size());
                return LogicResponse.<PageResponse<GiftLogBO>>builder().status(ErrorCode.SUCCESS).data(pageResponse).build();
            } else {
                // 如果分页参数不为空，进行分页查询
                Page<GiftLog> page = new Page<>(pageNum, pageSize);
                IPage<GiftLog> giftLogPage = giftLogMapper.selectPage(page, queryWrapper);

                giftLogBOList = giftLogPage.getRecords().stream()
                        .map(d -> GiftLogBO.builder()
                                .activityId(d.getActivityId())
                                .roomId(d.getRoomId())
                                .anchorName(d.getAnchorName())
                                .senderId(d.getSenderId())
                                .senderName(d.getSenderName())
                                .senderAvatarUrl(d.getSenderAvatarUrl())
                                .giftId(d.getGiftId())
                                .amount(d.getAmount())
                                .comboCount(d.getComboCount())
                                .chargePolicy(d.getChargePolicy())
                                .totalPayment(d.getTotalPayment())
                                .totalGems(d.getTotalGems())
                                .sentTimestamp(d.getSentTimestamp())
                                .build())
                        .collect(Collectors.toList());

                // 返回分页记录的 PageResponse
                PageResponse<GiftLogBO> pageResponse = new PageResponse<>(
                        giftLogBOList,
                        giftLogPage.getTotal(),
                        giftLogPage.getTotal()
                );
                return LogicResponse.<PageResponse<GiftLogBO>>builder().status(ErrorCode.SUCCESS).data(pageResponse).build();
            }
        });
    }


    @Override
    public void insertGiftLog(GiftLogBO giftLogBO) {
        GiftLog giftLog = new GiftLog();
        BeanUtils.copyProperties(giftLogBO, giftLog);
        int ret = giftLogMapper.insert(giftLog);
        if (ret != 1) {
            log.error("insertGiftLog error:{}", giftLogBO);
        }
    }

}
