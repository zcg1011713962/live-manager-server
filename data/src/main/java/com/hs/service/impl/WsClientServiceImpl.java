package com.hs.service.impl;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.hs.entity.bo.GiftInfoBo;
import com.hs.entity.bo.GiftLogBO;
import com.hs.service.GiftLogService;
import com.hs.service.WsClientService;
import com.hs.util.JwtUtil;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakeException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.HandshakeInfo;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Duration;
import java.util.UUID;

@Slf4j
@Service
public class WsClientServiceImpl implements WsClientService {

    @Autowired
    private GiftLogService giftLogService;

    @PostConstruct
    public void init() {
        connect();
    }

    @Override
    public void connect() {
        String appId = "tmp_nS4HrIbi2F";
        String roomId = "32429581";
        long currentTimeInSeconds = System.currentTimeMillis() / 1000;

        String token = JwtUtil.createJWT(roomId, appId, currentTimeInSeconds);

        String url = new StringBuffer()
                .append("ws://ws-apiext.nimo.tv/websocket?do=comm&roomId=")
                .append(roomId)
                .append("&appId=")
                .append(appId)
                .append("&iat=")
                .append(currentTimeInSeconds)
                .append("&sToken=")
                .append(token)
                .toString();

        log.info("连接websocket地址:{}", url);


        WebSocketClient client = new ReactorNettyWebSocketClient();

        String reqId = UUID.randomUUID().toString().replace("-", "");

        String subscribeMessage = "{\"command\":\"subscribeNotice\",\"data\":[\"getSendItemNotice\"],\"= \":\""+ reqId +"\"}";
        String pingMessage = "ping";

        client.execute(URI.create(url), session -> {
            // 1. 发送订阅消息
            Mono<Void> sendSubscription = session.send(
                    Mono.just(session.textMessage(subscribeMessage))
            );

            // 2. 定时发送 ping 消息
            Mono<Void> sendPing = session.send(
                    Flux.interval(Duration.ofSeconds(15)) // 每 15 秒发送一次
                            .map(interval -> {
                                log.info("间隔15秒发送ping消息");
                                return session.textMessage(pingMessage);
                            })
            ).then();


            // 3. 持续接收返回的消息
            Mono<Void> receiveMessages = session.receive()
                    .map(WebSocketMessage::getPayloadAsText) // 提取消息文本
                    .doOnNext(message -> {
                        if(JSONUtil.isTypeJSON(message)){
                            JSONObject json = JSONUtil.parseObj(message);
                            String statusCode = json.getStr("statusCode");
                            if(String.valueOf(200).equals(statusCode)){
                                JSONObject data = JSONUtil.parseObj(json.get("data"));
                                String command = data.getStr("command");
                                if("subscribeNotice".equals(command)){
                                    JSONArray p = JSONUtil.parseArray(data.get("data"));
                                    log.info("订阅成功: {}", p);
                                    return;
                                }
                            }


                        }
                        log.info("Received message: {}", message);
                        try {
                            GiftInfoBo giftInfoBo = JSONUtil.toBean(message, GiftInfoBo.class);
                            GiftLogBO giftbo = GiftLogBO.builder()
                                    .roomId(giftInfoBo.getData().getRoomId())
                                    .anchorName(giftInfoBo.getData().getPresenterNick())
                                    .senderId(giftInfoBo.getData().getSenderUid())
                                    .senderName(giftInfoBo.getData().getSenderNick())
                                    .senderAvatarUrl(giftInfoBo.getData().getSenderAvatarUrl())
                                    .giftId(giftInfoBo.getData().getItemId())
                                    .amount(giftInfoBo.getData().getItemCount())
                                    .comboCount(giftInfoBo.getData().getSendItemComboHits())
                                    .chargePolicy(giftInfoBo.getData().getPayType())
                                    .totalPayment(giftInfoBo.getData().getTotalPay())
                                    .totalGems(giftInfoBo.getData().getTotalGet())
                                    .sentTimestamp(giftInfoBo.getData().getSendTimeStamp())
                                    .activityId(giftInfoBo.getData().getActivityId())
                                    .build();
                            giftLogService.insertGiftLog(giftbo);
                        }catch (Exception e){
                            log.error("GiftLog: {}", e.getMessage());
                        }
                    }) // 处理消息
                    .doOnError(error -> log.error("Error receiving message: {}", error.getMessage())) // 错误处理
                    .then();
            return Mono.when(
                    sendSubscription, // 发送订阅消息
                    sendPing,         // 定时发送 ping 消息
                    receiveMessages   // 持续接收消息
            );
        }).doOnError(error -> {
            // WebSocket 连接错误处理
            log.error("WebSocket connection error: {}", error.getMessage());
        }).subscribe();
    }
}
