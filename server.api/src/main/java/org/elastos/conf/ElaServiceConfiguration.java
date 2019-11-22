/**
 * Copyright (c) 2017-2018 The Elastos Developers
 * <p>
 * Distributed under the MIT software license, see the accompanying file
 * LICENSE or https://opensource.org/licenses/mit-license.php
 */
package org.elastos.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("serviceurl")
public class ElaServiceConfiguration {
    private String didServicePrefix;
    private String didNodePrefix;
    private String elaNodePrefix;
    private String packetTopicId;
    private String packetUrl;

    public String getDidServicePrefix() {
        return didServicePrefix;
    }

    public void setDidServicePrefix(String didServicePrefix) {
        this.didServicePrefix = didServicePrefix;
    }

    public String getDidNodePrefix() {
        return didNodePrefix;
    }

    public void setDidNodePrefix(String didNodePrefix) {
        this.didNodePrefix = didNodePrefix;
    }

    public String getElaNodePrefix() {
        return elaNodePrefix;
    }

    public void setElaNodePrefix(String elaNodePrefix) {
        this.elaNodePrefix = elaNodePrefix;
    }

    public String getPacketTopicId() {
        return packetTopicId;
    }

    public void setPacketTopicId(String packetTopicId) {
        this.packetTopicId = packetTopicId;
    }

    public String getPacketUrl() {
        return packetUrl;
    }

    public void setPacketUrl(String packetUrl) {
        this.packetUrl = packetUrl;
    }
}
