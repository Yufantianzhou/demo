package com.example.demo.config;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "mqtt")
//@ConfigurationonProperty(value="mqtt.enabled",havingValue="true")
public class MqttConfig {
   // private static final Logger log = LoggerFactory.getLogger(MqttConfig.class);

    @Value("${mqtt.connection.timeout}")
    private int connectionTimeout;

    @Value("${mqtt.keep.alive.interval}")
    private int keepAliveInterval;

    @Value("${mqtt.automatic.reconnect}")
    private boolean automaticReconnect;

    @Value("${mqtt.clean.session}")
    private boolean cleanSession;

    @Value("${mqtt.brokerUrl:tcp://localhost:1883}")
    private String brokerUrl;

    @Value("${mqtt.clientId}")
    private String clientId;

    // @Value("${mqtt.topic}")
    // private String topic;

    @Bean
    public MqttConnectOptions mqttConnectOptions() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(automaticReconnect);
        options.setCleanSession(cleanSession);
        options.setConnectionTimeout(connectionTimeout);
        options.setKeepAliveInterval(keepAliveInterval);
        options.setServerURIs(brokerUrl.split(","));
        return options;
    }

    @Bean
    public MqttClient mqttClient() throws MqttException {
        try {
            MqttClient mqttClient = new MqttClient(brokerUrl, clientId);
            return mqttClient;
        } catch (MqttException e) {
            throw e;
        }
    }

}
