/*
 * Copyright (c) 2019 Cisco and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package icn.forwarder.com.utility;

public class Constants {
    public static final String DEFAULT_NEXT_HOP_IP = "10.228.40.61";
    public static final String DEFAULT_NEXT_HOP_PORT = "11111";
    public static final String DEFAULT_NETMASK = "16";
    public static final String DEFAULT_PREFIX = "b001::";
    public static final String ENABLED = "Enabled";
    public static final String DISABLED = "Disabled";
    public static final String FORWARDER_PREFERENCES = "forwarderPreferences";
    public static final String DEFAULT_SOURCE_INTERFACE = "eth0";
    public static final String DEFAULT_SOURCE_PORT = "1111";
    public static final String DEFAULT_CAPACITY = "0";
    public static final String DEFAULT_CONFIGURATION =
            "add listener udp listener0 %%source_ip%% %%source_port%%\n" +
            "add connection udp conn0 %%next_hop_ip%% %%next_hop_port%% %%source_ip%% %%source_port%%\n" +
            "add route conn0 %%prefix%%/%%netmask%% 1\n";
    public static final String SOURCE_IP = "%%source_ip%%";
    public static final String SOURCE_PORT = "%%source_port%%";
    public static final String NEXT_HOP_IP = "%%next_hop_ip%%";
    public static final String NEXT_HOP_PORT = "%%next_hop_port%%";
    public static final String PREFIX = "%%prefix%%";
    public static final String NETMASK = "%%netmask%%";
    public static final String CONFIGURATION_PATH = "Configuration";
    public static final String CONFIGURATION_FILE_NAME = "forwarder.conf";
    public static final int FOREGROUND_SERVICE = 101;
}
