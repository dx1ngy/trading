package com.dx1ngy.trading.api.manager;

import com.dx1ngy.trading.api.utils.StpUtil;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.SmartLifecycle;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@ServerEndpoint("/ws/{token}")
public class WebSocketManager implements SmartLifecycle {

    private volatile boolean running = false;
    private static final Map<String, Session> sessionMap = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) throws IOException {
        Object loginId = StpUtil.USER.getLoginIdByToken(token);
        if (loginId == null) {
            session.close();
            sessionMap.remove(token);
        } else {
            sessionMap.put(token, session);
        }
    }

    @OnMessage
    public void onMessage(Session session, String message, @PathParam("token") String token) {
        log.info("token: {} message: {}", token, message);
    }

    @OnClose
    public void onClose(Session session, @PathParam("token") String token) throws IOException {
        Object loginId = StpUtil.USER.getLoginIdByToken(token);
        if (loginId != null) {
            session.close();
            sessionMap.remove(token);
        }
    }

    @OnError
    public void onError(Session session, @PathParam("token") String token, Throwable throwable) throws IOException {
        log.error("websocket error", throwable);
        session.close();
        sessionMap.remove(token);
    }

    public static void sendMessage(Long userId, String message) {
        String token = StpUtil.USER.getTokenValueByLoginId(userId);
        if (StringUtils.isNotBlank(token) && sessionMap.containsKey(token)) {
            var session = sessionMap.get(token);
            if (session.isOpen()) {
                session.getAsyncRemote().sendText(message);
            }
        }
    }

    public static void sendMessage(String message) {
        sessionMap.values().forEach(session -> {
            if (session.isOpen()) {
                session.getAsyncRemote().sendText(message);
            }
        });
    }

    private void closeAll() {
        sessionMap.values().forEach(session -> {
            if (session.isOpen()) {
                try {
                    session.close();
                } catch (IOException e) {
                    log.error("websocket close error", e);
                }
            }
        });
    }

    @Override
    public void start() {
        running = true;
    }

    @Override
    public void stop() {
        running = false;
        log.info("关闭所有websocket");
        closeAll();
    }

    @Override
    public boolean isRunning() {
        return running;
    }
}
