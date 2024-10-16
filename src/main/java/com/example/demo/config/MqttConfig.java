package com.example.demo.config;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "mqtt")
public class MqttConfig {
    @Value("${mqtt.connection.timeout}")
    private int connectionTimeout;

    @Value("${mqtt.keep.alive.interval}")
    private int keepAliveInterval;

    @Value("${mqtt.automatic.reconnect}")
    private boolean automaticReconnect;

    @Value("${mqtt.clean.session}")
    private boolean cleanSession;

    @Bean
    public MqttConnectOptions mqttConnectOptions() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(automaticReconnect);
        options.setCleanSession(cleanSession);
        options.setConnectionTimeout(connectionTimeout);
        options.setKeepAliveInterval(keepAliveInterval);

        return options;
    }

}
