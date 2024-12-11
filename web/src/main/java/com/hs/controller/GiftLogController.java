package com.hs.controller;

import com.hs.entity.BaseResponse;
import com.hs.entity.PageResponse;
import com.hs.entity.bo.GiftLogBO;
import com.hs.entity.vo.GiftLogVO;
import com.hs.enums.ErrorCode;
import com.hs.service.GiftLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class GiftLogController {
    @Autowired
    private GiftLogService giftLogService;

    @GetMapping("/api/giftlog")
    public Mono<BaseResponse<PageResponse<GiftLogVO>>> search(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum, @RequestParam(value = "pageSize",defaultValue = "20") Integer pageSize,
                                                              @RequestParam(value = "roomId",required = false) String roomId, @RequestParam(value = "giftId",required = false) String giftId,
                                                              @RequestParam(value = "giftId",required = false) String senderId) {
        log.debug("查询礼物日志 roomId:{} giftId:{} senderId:{}", roomId, giftId, senderId);
        CompletableFuture<BaseResponse<PageResponse<GiftLogVO>>> future = giftLogService.searchGiftLog(pageNum, pageSize, roomId, giftId, senderId).thenApply((logicResponse) -> {
            if (logicResponse.getStatus() == ErrorCode.SUCCESS) {
                PageResponse<GiftLogBO> pageBO = logicResponse.getData();

                List<GiftLogVO> giftLogVOList = pageBO.getRecords().stream()
                        .map(d -> {
                            return GiftLogVO.builder()
                                    .roomId(d.getRoomId())
                                    .giftId(d.getGiftId())
                                    .amount(d.getAmount())
                                    .anchorName(d.getAnchorName())
                                    .senderId(d.getSenderId())
                                    .comboCount(d.getComboCount())
                                    .chargePolicy(d.getChargePolicy()).build();
                        }).collect(Collectors.toList());

                PageResponse<GiftLogVO> pageResponse = new PageResponse<>(
                        giftLogVOList,
                        pageBO.getTotal(),
                        pageBO.getSize(),
                        pageBO.getCurrent(),
                        pageBO.getPages()
                );
                return BaseResponse.<PageResponse<GiftLogVO>>builder()
                        .status(ErrorCode.SUCCESS.getCode())
                        .data(pageResponse)
                        .build();
            }
            return BaseResponse.<PageResponse<GiftLogVO>>builder().status(logicResponse.getStatus().getCode()).message(logicResponse.getMsg()).build();
        }).exceptionally((e)->{
            log.error("查询礼物日志异常", e);
            return BaseResponse.<PageResponse<GiftLogVO>>builder().status(ErrorCode.FAILED.getCode()).message(ErrorCode.FAILED.getDesc()).build();
        });
        return Mono.fromFuture(future);
    }

}
