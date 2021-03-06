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

import static io.elastest.eus.docker.DockerContainer.dockerBuilder;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import com.github.dockerjava.api.model.Ports.Binding;

import io.elastest.eus.service.DockerService;
import io.elastest.eus.service.JsonService;
import io.elastest.eus.service.PropertiesService;

/**
 * Tests for Docker service.
 *
 * @author Boni Garcia (boni.garcia@urjc.es)
 * @since 0.0.1
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Tag("integration")
@DisplayName("Integration test for Docker Service")
public class DockerIntegrationTest {

    final Logger log = LoggerFactory.getLogger(DockerIntegrationTest.class);

    @Autowired
    private DockerService dockerService;

    @Autowired
    private PropertiesService propertiesService;

    @Autowired
    private JsonService jsonService;

    @Value("${hub.exposedport}")
    private int hubExposedPort;

    @Value("${hub.vnc.exposedport}")
    private int hubVncExposedPort;

    @Test
    @DisplayName("Ask for Chrome to Docker")
    void testDocker() throws Exception {
        // Test data (input)
        String jsonCapabilities = "{ \"desiredCapabilities\": {"
                + "\"browserName\": \"chrome\"," + " \"version\": \"\","
                + "\"platform\": \"ANY\"" + " }" + "}";

        // Exercise
        String browserName = jsonService.getBrowser(jsonCapabilities);
        String version = jsonService.getVersion(jsonCapabilities);
        String platform = jsonService.getPlatform(jsonCapabilities);

        log.debug("Starting Hub from JSON message {}", jsonCapabilities);

        String imageId = propertiesService
                .getDockerImageFromCapabilities(browserName, version, platform);

        String containerName = dockerService
                .generateContainerName("eus-hub-for-test-");

        int hubBindPort = dockerService.findRandomOpenPort();
        Binding bindPort = Ports.Binding.bindPort(hubBindPort);
        ExposedPort exposedPort = ExposedPort.tcp(hubExposedPort);

        int hubVncBindPort = dockerService.findRandomOpenPort();
        Binding bindHubVncPort = Ports.Binding.bindPort(hubVncBindPort);
        ExposedPort exposedHubVncPort = ExposedPort.tcp(hubVncExposedPort);

        List<PortBinding> portBindings = asList(
                new PortBinding(bindPort, exposedPort),
                new PortBinding(bindHubVncPort, exposedHubVncPort));

        dockerService
                .startAndWaitContainer(dockerBuilder(imageId, containerName)
                        .portBindings(portBindings).build());

        // Assertions
        assertTrue(dockerService.existsContainer(containerName));

        // Tear down
        log.debug("Stoping Hub");
        dockerService.stopAndRemoveContainer(containerName);
    }

}
