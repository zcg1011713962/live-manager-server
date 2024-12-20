package com.hs.controller;

import com.hs.entity.BaseResponse;
import com.hs.entity.PageResponse;
import com.hs.entity.bo.GiftLogBO;
import com.hs.entity.vo.GiftLogVO;
import com.hs.enums.ActivityType;
import com.hs.enums.ChargePolicyType;
import com.hs.enums.ErrorCode;
import com.hs.service.GiftLogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class GiftLogController {
    @Autowired
    private GiftLogService giftLogService;

    @GetMapping("/api/giftlog")
    public Mono<PageResponse<GiftLogVO>> search(@RequestParam(value = "draw") int draw, @RequestParam(value = "pageNum") Integer pageNum,
                                                              @RequestParam(value = "pageSize") Integer pageSize, @RequestParam(value = "roomId",required = false) String roomId,
                                                              @RequestParam(value = "giftId",required = false) String giftId, @RequestParam(value = "senderId",required = false) String senderId,
                                                              @RequestParam(value = "activityDesc",required = false) String activityDesc,@RequestParam(value = "startTime",required = false) Long startTime,
                                                              @RequestParam(value = "endTime",required = false) Long endTime) {
        log.debug("查询礼物日志 pageSize:{} pageNum :{} roomId:{} giftId:{} senderId:{} startTime:{} endTime:{}", pageSize, pageNum, roomId, giftId, senderId, startTime, endTime);
        Long activityId = StringUtils.isNotBlank(activityDesc) ? ActivityType.fromValue(activityDesc).getType() : null;

        CompletableFuture<PageResponse<GiftLogVO>> future = giftLogService.searchGiftLog(pageNum, pageSize, roomId, giftId, senderId, activityId, startTime, endTime).thenApply((logicResponse) -> {
            if (logicResponse.getStatus() == ErrorCode.SUCCESS) {
                PageResponse<GiftLogBO> pageBO = logicResponse.getData();

                List<GiftLogVO> giftLogVOList = pageBO.getData().stream()
                        .map(d -> {
                            return GiftLogVO.builder()
                                    .activityDesc(ActivityType.fromKey(d.getActivityId()).getDesc())
                                    .roomId(d.getRoomId())
                                    .anchorName(d.getAnchorName())
                                    .senderId(d.getSenderId())
                                    .senderName(d.getSenderName())
                                    .senderAvatarUrl(d.getSenderAvatarUrl())
                                    .giftId(d.getGiftId())
                                    .amount(d.getAmount())
                                    .comboCount(d.getComboCount())
                                    .chargePolicy(ChargePolicyType.fromKey(d.getChargePolicy()).getDesc())
                                    .totalPayment(d.getTotalPayment())
                                    .totalGems(d.getTotalGems())
                                    .sentTimestamp(d.getSentTimestamp())
                                    .build();
                        }).collect(Collectors.toList());

                return new PageResponse<GiftLogVO>(
                        giftLogVOList,
                        pageBO.getRecordsTotal(),
                        pageBO.getRecordsFiltered(),
                        draw
                );
            }
            return new PageResponse<GiftLogVO>(
                    null,
                    0,
                   0,
                    draw);
        }).exceptionally((e)->{
            log.error("查询礼物日志异常", e);
            return new PageResponse<GiftLogVO>(
                    null,
                    0,
                    0,
                    draw);
        });
        return Mono.fromFuture(future);
    }


    @GetMapping(value = "/api/giftlog/export", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public Mono<Void> exportExcel(ServerWebExchange exchange, @RequestParam(value = "draw") int draw,  @RequestParam(value = "roomId",required = false) String roomId,
                                  @RequestParam(value = "giftId",required = false) String giftId, @RequestParam(value = "senderId",required = false) String senderId,
                                  @RequestParam(value = "activityDesc",required = false) String activityDesc,@RequestParam(value = "startTime",required = false) Long startTime,
                                  @RequestParam(value = "endTime",required = false) Long endTime) {
        // 创建 Excel 文件并写入响应流
        return Mono.fromCallable(() -> {
            try {
                // 创建 Excel 工作簿
                Workbook workbook = new XSSFWorkbook();
                Sheet sheet = workbook.createSheet("礼物数据");

                // 创建表头行
                Row headerRow = sheet.createRow(0);
                headerRow.createCell(0).setCellValue("活动类型");
                headerRow.createCell(1).setCellValue("房间号");
                headerRow.createCell(2).setCellValue("主播名");
                headerRow.createCell(3).setCellValue("送礼人ID");
                headerRow.createCell(4).setCellValue("送礼人");
                headerRow.createCell(5).setCellValue("送礼人头像");
                headerRow.createCell(6).setCellValue("礼物ID");
                headerRow.createCell(7).setCellValue("数量");
                headerRow.createCell(8).setCellValue("连击数");
                headerRow.createCell(9).setCellValue("付费类型");
                headerRow.createCell(10).setCellValue("总支付");
                headerRow.createCell(11).setCellValue("主播总获得宝石");
                headerRow.createCell(12).setCellValue("送礼时间");


                Long activityId = StringUtils.isNotBlank(activityDesc) ? ActivityType.fromValue(activityDesc).getType() : null;
                List<GiftLogVO> giftLogVO = giftLogService.searchGiftLog(null, null, roomId, giftId, senderId, activityId, startTime, endTime).thenApply((logicResponse) -> {
                    if (logicResponse.getStatus() == ErrorCode.SUCCESS) {
                        PageResponse<GiftLogBO> pageBO = logicResponse.getData();
                        List<GiftLogVO> giftLogVOList = pageBO.getData().stream()
                                .map(d -> {
                                    return GiftLogVO.builder()
                                            .activityDesc(ActivityType.fromKey(d.getActivityId()).getDesc())
                                            .roomId(d.getRoomId())
                                            .anchorName(d.getAnchorName())
                                            .senderId(d.getSenderId())
                                            .senderName(d.getSenderName())
                                            .senderAvatarUrl(d.getSenderAvatarUrl())
                                            .giftId(d.getGiftId())
                                            .amount(d.getAmount())
                                            .comboCount(d.getComboCount())
                                            .chargePolicy(ChargePolicyType.fromKey(d.getChargePolicy()).getDesc())
                                            .totalPayment(d.getTotalPayment())
                                            .totalGems(d.getTotalGems())
                                            .sentTimestamp(d.getSentTimestamp())
                                            .build();
                                }).collect(Collectors.toList());
                        return giftLogVOList;
                    }
                    return Collections.<GiftLogVO>emptyList();
                }).join();

                // 写入数据
                int rowNum = 1;
                for (GiftLogVO log : giftLogVO) {
                    // 转换为 LocalDateTime
                    LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(log.getSentTimestamp()), ZoneId.systemDefault());
                    // 定义格式化器
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    // 格式化为字符串
                    String time = dateTime.format(formatter);
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(log.getActivityDesc());
                    row.createCell(1).setCellValue(String.valueOf(log.getRoomId()));
                    row.createCell(2).setCellValue(log.getAnchorName());
                    row.createCell(3).setCellValue(String.valueOf(log.getSenderId()));
                    row.createCell(4).setCellValue(log.getSenderName());
                    row.createCell(5).setCellValue(log.getSenderAvatarUrl());
                    row.createCell(6).setCellValue(log.getGiftId());
                    row.createCell(7).setCellValue(log.getAmount());
                    row.createCell(8).setCellValue(log.getComboCount());
                    row.createCell(9).setCellValue(log.getChargePolicy());
                    row.createCell(10).setCellValue(log.getTotalPayment());
                    row.createCell(11).setCellValue(log.getTotalGems());
                    row.createCell(12).setCellValue(time);
                }

                // 将 Excel 文件写入 ByteArrayOutputStream
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                workbook.write(outputStream);
                workbook.close();

                // 获取响应
                byte[] excelBytes = outputStream.toByteArray();

                // 将 Excel 数据转换为 DataBuffer
                DataBuffer dataBuffer = exchange.getResponse().bufferFactory().wrap(excelBytes);

                // 设置响应头
                exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_OCTET_STREAM);
                exchange.getResponse().getHeaders().set("Content-Disposition", "attachment; filename=example.xlsx");

                // 写入 DataBuffer 到响应流
                return exchange.getResponse().writeWith(Mono.just(dataBuffer));
            } catch (IOException e) {
                log.error("导出excel error", e);
                return Mono.error(e);
            }
        }).flatMap(response -> response).then();  // 确保返回的是 Mono<Void> 类型
    }

}
