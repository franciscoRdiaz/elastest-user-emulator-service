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
package io.elastest.eus.external;

import java.util.List;
import java.util.Map;

import com.google.gson.annotations.SerializedName;

/**
 * Utility class for deserialize container info from docker-compose-ui.
 *
 * @author Boni Garcia (boni.garcia@urjc.es)
 * @since 0.1.1
 */
public class DockerContainerInfo {

    List<DockerContainer> containers;

    public List<DockerContainer> getContainers() {
        return containers;
    }

    @Override
    public String toString() {
        return "DockerContainerInfo [getContainers()=" + getContainers() + "]";
    }

    public class DockerContainer {
        String command;

        @SerializedName("is_running")
        boolean isRunning;

        Labels labels;

        String name;

        @SerializedName("name_without_project")
        String nameWithoutProject;

        Ports ports;

        String state;

        Map<String, Object> volumes;

        public String getCommand() {
            return command;
        }

        public boolean isRunning() {
            return isRunning;
        }

        public Labels getLabels() {
            return labels;
        }

        public String getName() {
            return name;
        }

        public String getNameWithoutProject() {
            return nameWithoutProject;
        }

        public Ports getPorts() {
            return ports;
        }

        public String getState() {
            return state;
        }

        public Map<String, Object> getVolumes() {
            return volumes;
        }

        @Override
        public String toString() {
            return "DockerContainer [getCommand()=" + getCommand()
                    + ", isRunning()=" + isRunning() + ", getLabels()="
                    + getLabels() + ", getName()=" + getName()
                    + ", getNameWithoutProject()=" + getNameWithoutProject()
                    + ", getPorts()=" + getPorts() + ", getState()="
                    + getState() + ", getVolumes()=" + getVolumes() + "]";
        }

    }

    public class Labels {
        @SerializedName("com.docker.compose.config-hash")
        String configHash;

        @SerializedName("com.docker.compose.container-number")
        String containerNumber;

        @SerializedName("com.docker.compose.oneoff")
        String oneoff;

        @SerializedName("com.docker.compose.project")
        String project;

        @SerializedName("com.docker.compose.service")
        String service;

        @SerializedName("com.docker.compose.version")
        String version;

        public String getConfigHash() {
            return configHash;
        }

        public String getContainerNumber() {
            return containerNumber;
        }

        public String getOneoff() {
            return oneoff;
        }

        public String getProject() {
            return project;
        }

        public String getService() {
            return service;
        }

        public String getVersion() {
            return version;
        }

        @Override
        public String toString() {
            return "Labels [getConfigHash()=" + getConfigHash()
                    + ", getContainerNumber()=" + getContainerNumber()
                    + ", getOneoff()=" + getOneoff() + ", getProject()="
                    + getProject() + ", getService()=" + getService()
                    + ", getVersion()=" + getVersion() + "]";
        }

    }

    public class Ports {
        Map<String, PortInfo> portsMap;

        public Map<String, PortInfo> getPortsMap() {
            return portsMap;
        }

        @Override
        public String toString() {
            return "Ports [getPortsMap()=" + getPortsMap() + "]";
        }

    }

    public class PortInfo {
        @SerializedName("HostIp")
        String hostIp;

        @SerializedName("HostPort")
        String hostPort;

        public String getHostIp() {
            return hostIp;
        }

        public String getHostPort() {
            return hostPort;
        }

        @Override
        public String toString() {
            return "PortInfo [getHostIp()=" + getHostIp() + ", getHostPort()="
                    + getHostPort() + "]";
        }
    }

}
