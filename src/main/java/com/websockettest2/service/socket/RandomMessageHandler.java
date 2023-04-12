package com.websockettest2.service.socket;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RandomMessageHandler extends TextWebSocketHandler {

	private static final ConcurrentHashMap<String, WebSocketSession> CLIENTS = new ConcurrentHashMap<>();
	private final ObjectMapper objectMapper;

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		CLIENTS.put(session.getId(), session);
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message)
		throws Exception {

		String[] param = message.getPayload().split(" ");

		String id = session.getId();

		int answer = Integer.parseInt(param[0]) + Integer.parseInt(param[1]);

		TextMessage message1 = new TextMessage(answer + "");

		CLIENTS.forEach((key, value) -> {
			if (key.equals(id)) {
				try {
					value.sendMessage(message1);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		});
	}
}
