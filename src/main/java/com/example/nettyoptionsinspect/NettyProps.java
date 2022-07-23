package com.example.nettyoptionsinspect;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "netty.options")
@Data
public class NettyProps {
    private String connectTimeoutMillis;
    private Integer maxMessagesPerWrite;
    private Boolean soKeepalive;
    private Integer soLinger;
    private Integer soTimeout;
    private Integer soRcvbuf;
    private Boolean soReuseaddr;
    private Integer soSndbuf;
    private Integer soBacklog;
    private Boolean soBroadcast;
    private Boolean tcpNodelay;
    private Boolean tcpFastopenConnect;
    private Integer waterlow;
    private Integer waterhigh;
}
