package com.websockettest2.service.socket;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.websockettest2.dto.MessageDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class PlusMessageHandler extends TextWebSocketHandler {

	private static final ConcurrentHashMap<String, WebSocketSession> clients = new ConcurrentHashMap<>();
	private final ObjectMapper objectMapper;

	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		log.info("새로운 세션: " + session.getId() + "\n");
		clients.put(session.getId(), session);
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		log.info(session.getId() + " [Connection is Closed.]");
		clients.remove(session.getId());
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message)
		throws Exception {
		MessageDto dto = objectMapper.readValue(message.getPayload(), MessageDto.class);

		clients.forEach((key, value) -> {
			if (key.equals(dto.getTo())) {
				try {
					TextMessage message1 = new TextMessage(dto.getMessage());
					value.sendMessage(message1);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		});
	}
}
