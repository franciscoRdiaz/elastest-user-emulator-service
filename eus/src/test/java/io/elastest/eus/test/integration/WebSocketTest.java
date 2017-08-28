/*
 * (C) Copyright 2017-2019 ElasTest (http://elastest.io/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package io.elastest.eus.test.integration;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import java.io.File;
import java.nio.charset.Charset;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import io.elastest.eus.app.EusSpringBootApp;
import io.elastest.eus.service.JsonService;
import io.elastest.eus.service.SessionService;
import io.elastest.eus.session.SessionInfo;
import io.elastest.eus.test.util.WebSocketClient;
import io.elastest.eus.test.util.WebSocketClient.MessageHandler;

/**
 * Tests for properties WebSocket.
 *
 * @author Boni Garcia (boni.garcia@urjc.es)
 * @since 0.0.1
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = EusSpringBootApp.class, webEnvironment = RANDOM_PORT)
public class WebSocketTest {

    final Logger log = LoggerFactory.getLogger(WebSocketTest.class);

    @LocalServerPort
    int serverPort;

    @Value("${ws.path}")
    private String wsPath;

    @Value("${server.contextPath}")
    private String contextPath;

    @Value("${ws.protocol.getSessions}")
    private String wsProtocolGetSessions;

    @Value("${ws.protocol.getRecordings}")
    private String wsProtocolGetRecordings;

    @Value("${ws.protocol.newSession}")
    private String wsProtocolNewSession;

    @Value("${ws.protocol.recordedSession}")
    private String wsProtocolRecordedSesssion;

    @Value("${registry.metadata.extension}")
    private String registryMetadataExtension;

    @Value("${registry.folder}")
    private String registryFolder;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private JsonService jsonService;

    @BeforeEach
    void setup() {
        log.debug("App started on port {}", serverPort);
    }

    @Test
    void testSessions() throws Exception {
        SessionInfo sessionInfo = new SessionInfo();
        sessionInfo.setBrowser("chrome");
        sessionInfo.setVersion("59");
        sessionService.putSession("my-session-id", sessionInfo);
        String jsonMessage = jsonService.newSessionJson(sessionInfo).toString();
        assertNotNull(jsonMessage);

        String wsUrl = "ws://localhost:" + serverPort + "/" + contextPath
                + wsPath;

        final String sentMessage = wsProtocolGetSessions;
        final String[] receivedMessage = { "" };

        CountDownLatch latch = new CountDownLatch(1);
        WebSocketClient webSocketClient = new WebSocketClient(wsUrl);
        webSocketClient.addMessageHandler(new MessageHandler() {
            @Override
            public void handleMessage(String message) {
                log.debug("Sent message: {} -- received message: {}",
                        sentMessage, message);
                receivedMessage[0] = message;
                latch.countDown();
            }
        });

        webSocketClient.sendMessage(sentMessage);

        latch.await(5, SECONDS);

        assertTrue(receivedMessage[0].contains(wsProtocolNewSession));
    }

    @Test
    void testRecordings() throws Exception {
        SessionInfo sessionInfo = new SessionInfo();
        sessionInfo.setBrowser("chrome");
        sessionInfo.setVersion("59");
        String sessionId = "my-session-id";
        sessionService.putSession(sessionId, sessionInfo);

        String jsonFileName = sessionId + registryMetadataExtension;

        JSONObject sessionInfoToJson = jsonService
                .recordedSessionJson(sessionInfo);
        File file = new File(registryFolder + jsonFileName);
        FileUtils.writeStringToFile(file, sessionInfoToJson.toString(),
                Charset.defaultCharset());

        String jsonMessage = jsonService.newSessionJson(sessionInfo).toString();
        assertNotNull(jsonMessage);

        String wsUrl = "ws://localhost:" + serverPort + "/" + contextPath
                + wsPath;

        final String sentMessage = wsProtocolGetRecordings;
        final String[] receivedMessage = { "" };

        CountDownLatch latch = new CountDownLatch(1);
        WebSocketClient webSocketClient = new WebSocketClient(wsUrl);
        webSocketClient.addMessageHandler(new MessageHandler() {
            @Override
            public void handleMessage(String message) {
                log.debug("Sent message: {} -- received message: {}",
                        sentMessage, message);
                receivedMessage[0] = message;
                latch.countDown();
            }
        });

        webSocketClient.sendMessage(sentMessage);

        latch.await(5, SECONDS);

        assertTrue(receivedMessage[0].contains(wsProtocolRecordedSesssion));

        file.delete();
    }

}