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
    public CompletableFuture<LogicResponse<PageResponse<GiftLogBO>>> searchGiftLog(Integer pageNum, Integer pageSize, String roomId, String giftId, String senderId) {

        return CompletableFuture.supplyAsync(()->{
            QueryWrapper<GiftLog> queryWrapper = new QueryWrapper<>();

            queryWrapper.eq(StringUtils.isNotBlank(roomId), "roomId", roomId);
            queryWrapper.eq(StringUtils.isNotBlank(giftId), "giftId", giftId);
            queryWrapper.eq(StringUtils.isNotBlank(senderId), "senderId", senderId);

            Page<GiftLog> page = new Page<>(pageNum, pageSize);
            IPage<GiftLog> giftLogPage = giftLogMapper.selectPage(page, queryWrapper);

            // 将 User 实体转换为 UserBO
            List<GiftLogBO> doctorBOList = giftLogPage.getRecords().stream()
                    .map(d -> {
                        return GiftLogBO.builder()
                                .roomId(d.getRoomId())
                                .giftId(d.getGiftId())
                                .amount(d.getAmount())
                                .anchorName(d.getAnchorName())
                                .senderId(d.getSenderId())
                                .comboCount(d.getComboCount())
                                .chargePolicy(d.getChargePolicy()).build();
                    }).collect(Collectors.toList());

            PageResponse<GiftLogBO> pageResponse = new PageResponse<>(
                    doctorBOList,
                    giftLogPage.getTotal(),
                    giftLogPage.getSize(),
                    giftLogPage.getCurrent(),
                    giftLogPage.getPages()
            );
            return LogicResponse.<PageResponse<GiftLogBO>>builder().status(ErrorCode.SUCCESS).data(pageResponse).build();
        });
    }

}
