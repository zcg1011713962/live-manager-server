package com.hs.service.impl;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.hs.entity.bo.GiftInfoBo;
import com.hs.entity.bo.GiftLogBO;
import com.hs.service.GiftLogService;
import com.hs.service.WsClientService;
import com.hs.util.JwtUtil;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.net.URI;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
public class WsClientServiceImpl implements WsClientService {
    AtomicBoolean isConnected = new AtomicBoolean(false); // 连接状态
    AtomicLong lastPingTime = new AtomicLong(System.currentTimeMillis()); // 记录最近一次ping时间
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2); // 线程池定时任务
    private static final int MAX_RETRY = 5; // 最大重试次数
    private static final AtomicInteger retryCount = new AtomicInteger(0); // 记录重试次数

    @Autowired
    private GiftLogService giftLogService;
    private Disposable webSocketConnection;


    @PostConstruct
    public void init() {
       connect();
        // 启动定时任务：每 30 秒检查连接状态
        scheduler.scheduleAtFixedRate(() -> {
            long now = System.currentTimeMillis();
            if (now - lastPingTime.get() > 30000) { // 超过 30 秒未收到消息，判定断开
                if (isConnected.get()) {
                    log.warn("WebSocket 可能已断开，超过 30 秒未收到消息");
                }
                isConnected.set(false);
            }
            log.info("间隔30秒检测连接状态,当前连接状态:{}", isConnected.get());
        }, 30, 30, TimeUnit.SECONDS);
        // 重连
        scheduler.scheduleAtFixedRate(() -> {
            if(!isConnected.get()){
                connect();
                log.info("检测到连接断开，触发180秒重连机制");
            }
        }, 180, 180, TimeUnit.SECONDS);
    }

    @Override
    public void connect() {
        if (webSocketConnection != null && !webSocketConnection.isDisposed()) {
            log.info("WebSocket 已经连接，无需重复连接");
            return;
        }

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

        webSocketConnection = client.execute(URI.create(url), session -> {
            // 1. 发送订阅消息
            Mono<Void> sendSubscription = session.send(
                    Mono.just(session.textMessage(subscribeMessage))
            );

            // 2. 定时发送 ping 消息
            Mono<Void> sendPing = session.send(
                    Flux.interval(Duration.ofSeconds(15)) // 每 15 秒发送一次
                            .map(interval -> {
                                isConnected.set(true);
                                lastPingTime.set(System.currentTimeMillis()); // 更新 ping 发送时间
                                // log.info("间隔15秒发送ping消息");
                                return session.textMessage(pingMessage);
                            })
            ).then();


            // 3. 持续接收返回的消息
            Mono<Void> receiveMessages = session.receive()
                    .map(WebSocketMessage::getPayloadAsText) // 提取消息文本
                    .doOnNext(message -> {
                        isConnected.set(true); // 只要收到消息，就认为连接正常
                        lastPingTime.set(System.currentTimeMillis()); // 更新接收消息时间

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
        }).doOnTerminate(() -> {
            log.warn("WebSocket 连接已关闭，准备重连...");
        }).retryWhen(Retry.backoff(MAX_RETRY, Duration.ofSeconds(5)) // 失败后尝试重连（最多 MAX_RETRY 次）
                .doBeforeRetry(retrySignal -> {
                    int attempt = retryCount.incrementAndGet();
                    log.warn("WebSocket 连接断开，正在进行第 {} 次重连...", attempt);
                })
                .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> {
                    log.error("WebSocket 重连失败，达到最大重试次数 {} 次", MAX_RETRY);
                    return new RuntimeException("WebSocket 重连失败");
                })
        ).doOnError(error -> {
            // WebSocket 连接错误处理
            log.error("WebSocket connection error: {}", error.getMessage());
        }).subscribe();
    }


    @Override
    public void disconnect() {
        if (webSocketConnection != null && !webSocketConnection.isDisposed()) {
            log.info("正在主动断开 WebSocket 连接...");
            webSocketConnection.dispose();  // 取消订阅，断开连接
            webSocketConnection = null;     // 置空，避免重复关闭
        } else {
            log.warn("WebSocket 当前未连接，无需断开");
        }
    }

    @Override
    public void reConnect() {
        try {
            disconnect();
            Thread.sleep(1000);
            connect();
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public boolean getStatus() {
        return isConnected.get();
    }
}
