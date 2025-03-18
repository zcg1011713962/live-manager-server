package com.hs.service;

public interface WsClientService {

    void connect();

    void disconnect();

    void reConnect();

    boolean getStatus();
}
