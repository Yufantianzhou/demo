package com.example.demo.service.mqtt;

import com.example.demo.event.DeviceEvent;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/**
 * Mqtt服务
 */
@Slf4j
@Service
public class MqttClientService {
    @Value("${mqtt-message-event-suffix}")
    String eventSuffix;

    @Value("${mqtt-message-service-suffix}")
    String serviceSuffix;


    ApplicationEventPublisher publisher;
    MqttAsyncClient client;

    MqttClientService(ApplicationEventPublisher publisher, MqttAsyncClient client) {
        this.publisher = publisher;
        this.client = client;
    }

    @PostConstruct
    void init() {
        try {
            connect();
        } catch (MqttException e) {
            log.error(e.getMessage());
        }
    }

    public void connect() throws MqttException {
        client.setCallback(new Callback());
        client.subscribe("host", 2);
        log.info("Connected to broker {}", client.getServerURI());
    }

    public void disconnect() throws MqttException {
        if (client.isConnected()) {
            client.disconnect();
        }
    }

    public void publish(String topic, String message) throws MqttException {
        publish(topic, message, 2);
    }


    public void publish(String topic, String message, int qos) throws MqttException {
        MqttMessage mqttMessage = new MqttMessage(message.getBytes());
        mqttMessage.setQos(qos);
        client.publish(topic, mqttMessage);
    }

    public void subscribe(String topic) throws MqttException {
        client.subscribe(topic, 2);
    }

    class Callback implements MqttCallback, MqttCallbackExtended {
        @Override
        public void connectionLost(Throwable throwable) {
            log.info("MqttClient Connection lost");
        }

        @Override
        public void messageArrived(String topic, MqttMessage mqttMessage) {
            log.info("MqttMessage received: {}", mqttMessage.toString());
            if (topic.endsWith(eventSuffix)) {
                publisher.publishEvent(new DeviceEvent(this, topic, mqttMessage.toString()));
            }
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
            log.info("Delivery complete");
        }

        @Override
        public void connectComplete(boolean b, String s) {
            try {
                client.setCallback(new Callback());
                client.subscribe("host", 2);
                log.info("Reconnect and subscribe success");
            } catch (MqttException e) {
                log.error(e.getMessage());
            }
        }
    }
}
