package com.example.demo.service.mqtt;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;

public class MqttClientServiceTest {
    @Mock
    private ApplicationEventPublisher publisher;

    @Mock
    private MqttClient client;

    @Mock
    private MqttConnectOptions mqttConnectOptions;

    private MqttClientService mqttClientService;
    private final String topic = "test/topic";

    @BeforeEach
    @SuppressWarnings("unused")
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mqttClientService = new MqttClientService(publisher, client, mqttConnectOptions, topic);
    }

    @Test
    void testConnect() throws MqttException {
        when(client.isConnected()).thenReturn(false);

        mqttClientService.init();

        verify(client).connect(mqttConnectOptions);
      //  verify(client).setCallback(any(MqttCallbackHandler.class));
    }
    

    @Test
    void testDisconnect() throws MqttException {
        when(client.isConnected()).thenReturn(true);

        mqttClientService.disconnect();

        verify(client).disconnect();
    }


    @Test
    void testPublishWhenNotConnected() throws MqttException {
        String message = "Hello, MQTT!";
        when(client.isConnected()).thenReturn(false);

        mqttClientService.publish(topic, message);

        verify(client, never()).publish(anyString(), any(MqttMessage.class));
    }
}
