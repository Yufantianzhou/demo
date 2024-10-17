package com.example.demo.service.mqtt;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

/**
 * MQTT 服务
 */
@Service
public class MqttClientService {

    private static final Logger log = LoggerFactory.getLogger(MqttClientService.class);

    private final ApplicationEventPublisher publisher;
    private final MqttClient client;
    private final MqttConnectOptions mqttConnectOptions;
    private final String topic;

    public MqttClientService(ApplicationEventPublisher publisher,
            MqttClient client,
            MqttConnectOptions mqttConnectOptions,
            @Value("${mqtt.topic}") String topic) {
        this.publisher = publisher;
        this.client = client;
        this.mqttConnectOptions = mqttConnectOptions;
        this.topic = topic;
    }

    @PostConstruct
    void init() {
        try {
            connect();
            client.subscribe(topic);
            log.info("Subscribed to topic [{}].", topic);
        } catch (MqttException e) {
            log.error("Failed to connect and subscribe: {}", e.getMessage());
        }
    }

    private void connect() throws MqttException {
        if (!client.isConnected()) {
            client.connect(mqttConnectOptions);
            client.setCallback(new MqttCallbackHandler());
            log.info("Connected to MQTT broker at {}", client.getCurrentServerURI());
        } else {
            log.info("Already connected to MQTT broker.");
        }
    }

    public void disconnect() throws MqttException {
        if (client.isConnected()) {
            client.disconnect();
            log.info("Disconnected from MQTT broker.");
        } else {
            log.warn("MQTT client is not connected, no action taken.");
        }
    }

    public void publish(String topic, String message) throws MqttException {
        if (client.isConnected()) {
            client.publish(topic, new MqttMessage(message.getBytes()));
            log.info("Published message to topic [{}]: {}", topic, message);
        } else {
            log.error("Cannot publish message, MQTT client is not connected.");
        }
    }

    public class MqttCallbackHandler implements MqttCallbackExtended {

        @Override
        public void connectionLost(Throwable throwable) {
            log.warn("MQTT Client connection lost: {}", throwable.getMessage());
        }

        @Override
        public void messageArrived(String topic, MqttMessage mqttMessage) {
            log.info("Message received from topic [{}]: {}", topic, mqttMessage.toString());
            // 这里可以进行消息的进一步处理
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
            log.info("Message delivery complete.");
        }

        @Override
        public void connectComplete(boolean reconnect, String serverURI) {
            log.info("MQTT client connected successfully (reconnect: {}): {},topic: {}", reconnect, serverURI, topic);
        }
    }
}
