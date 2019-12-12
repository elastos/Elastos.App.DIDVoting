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
@ConfigurationProperties("packet")
public class PacketConfiguration {
    private String createPacketUrl;
    private String grabPacketUrl;
    private String peekPacketUrl;

    public String getCreatePacketUrl() {
        return createPacketUrl;
    }

    public void setCreatePacketUrl(String createPacketUrl){
        this.createPacketUrl = createPacketUrl;
    }

    public String getGrabPacketUrl() {
        return grabPacketUrl;
    }

    public void setGrabPacketUrl(String grabPacketUrl) {
        this.grabPacketUrl = grabPacketUrl;
    }

    public String getPeekPacketUrl() {
        return peekPacketUrl;
    }

    public void setPeekPacketUrl(String peekPacketUrl) {
        this.peekPacketUrl = peekPacketUrl;
    }
}
