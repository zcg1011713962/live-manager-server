package com.hs.service;

import com.hs.entity.LogicResponse;
import com.hs.entity.PageResponse;
import com.hs.entity.bo.GiftLogBO;

import java.util.concurrent.CompletableFuture;

public interface GiftLogService {

    CompletableFuture<LogicResponse<PageResponse<GiftLogBO>>> searchGiftLog(Integer pageNum, Integer pageSize,String roomId, String giftId, String senderId, Long activityId);

    void insertGiftLog(GiftLogBO giftLogBO);


}
