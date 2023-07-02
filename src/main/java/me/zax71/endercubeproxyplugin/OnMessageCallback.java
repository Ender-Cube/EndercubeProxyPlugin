package me.zax71.endercubeproxyplugin;

import com.google.inject.Inject;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;

public class OnMessageCallback implements MqttCallback {

    @Inject
    private Logger logger;

    public void connectionLost(Throwable cause) {
        // After the connection is lost, it usually reconnects here
        logger.info("disconnect，you can reconnect");
    }

    public void messageArrived(String topic, MqttMessage message) throws Exception {
        // The messages obtained after subscribe will be executed here
        logger.info("Received message topic:" + topic);
        logger.info("Received message Qos:" + message.getQos());
        logger.info("Received message content:" + new String(message.getPayload()));
    }

    public void deliveryComplete(IMqttDeliveryToken token) {
        logger.info("deliveryComplete---------" + token.isComplete());
    }
}
