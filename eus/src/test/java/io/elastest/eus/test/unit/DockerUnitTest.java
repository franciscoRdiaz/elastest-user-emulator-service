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
package io.elastest.eus.test.unit;

import static io.elastest.eus.docker.DockerContainer.dockerBuilder;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import com.github.dockerjava.api.model.Ports.Binding;

import io.elastest.eus.service.DockerService;
import io.elastest.eus.service.ShellService;
import io.elastest.eus.test.util.MockitoExtension;

/**
 * Tests for shell service.
 *
 * @author Boni Garcia (boni.garcia@urjc.es)
 * @since 0.1.1
 */
@ExtendWith(MockitoExtension.class)
@Tag("unit")
@DisplayName("Unit tests for Docker")
public class DockerUnitTest {

    final Logger log = LoggerFactory.getLogger(DockerUnitTest.class);

    @InjectMocks
    DockerService dockerService;

    @Mock
    ShellService shellService;

    @Test
    @DisplayName("Try to start a container with invalid input")
    void testEmptyContainer() {
        assertThrows(Exception.class, () -> {
            Binding bindPort = Ports.Binding.bindPort(0);
            ExposedPort exposedPort = ExposedPort.tcp(0);
            List<PortBinding> portBindings = asList(
                    new PortBinding(bindPort, exposedPort));

            dockerService.startAndWaitContainer(
                    dockerBuilder("", "").portBindings(portBindings).build());
        });

    }

    @Test
    @DisplayName("Docker server URL when running inside a container")
    void testDockerUrlInContainer() throws IOException {
        String dockerIpAddress = "172.17.0.1";
        when(shellService.isRunningInContainer()).thenReturn(true);
        when(shellService.runAndWait(any(String.class)))
                .thenReturn("default via " + dockerIpAddress + " dev eth0");

        String dockerServerUrl = dockerService.getDockerServerUrl();
        log.debug("Docker server URL {}", dockerServerUrl);
        assertThat(dockerServerUrl, containsString(dockerIpAddress));
    }

}