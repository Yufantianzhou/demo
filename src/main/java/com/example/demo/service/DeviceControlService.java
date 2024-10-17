package com.example.demo.service;

import java.io.IOException;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.demo.service.mqtt.MqttClientService;
import com.example.demo.util.TcpStream;

@Service
public class DeviceControlService {
    private static final Logger log = LoggerFactory.getLogger(DeviceControlService.class);
    private static final int DEFAULT_TIMEOUT = 5000;
    private static final int PORT = 45678;

    @Value("${using-tcp}")
    private  boolean usingTcp;

    MqttClientService mqttClientService;
    DeviceDataService deviceDataService;

    public DeviceControlService(DeviceDataService deviceDataService, MqttClientService mqttClientService) {
        this.deviceDataService = deviceDataService;
        this.mqttClientService = mqttClientService;
    }

    public boolean deviceOps(String device, String ops) {
        log.info("device {} {}", device, ops);

        if (usingTcp) {
            try {
                if (!deviceDataService.hasDevice(device)) {
                    return false;
                }
                String status = TcpStream.sendAndReceive(deviceDataService.getData(device), PORT, DEFAULT_TIMEOUT, ops.getBytes());
                return true;
            } catch (IOException e) {
                e.fillInStackTrace();
            }
        } else {
            try {
                mqttClientService.publish(device + "/ops", ops);
                return true;
            } catch (MqttException e) {
                e.fillInStackTrace();
            }
        }
        return false;
    }

    /*JUST FOR TEST*/
    public void sendMqttMessage() throws MqttException {
        mqttClientService.publish("test/lol", "switch");
    }

    public boolean connect(String ip) {
        log.info("device connect {}", ip);
        try {
            String device_id = TcpStream.sendAndReceive(ip, PORT, DEFAULT_TIMEOUT, "?deviceId".getBytes());
            deviceDataService.addDevice(device_id, ip);
            return true;
        } catch (IOException e) {
            e.fillInStackTrace();
            return false;
        }
    }
}
