package me.zax71.endercubeproxyplugin;

import com.google.inject.Inject;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;

@Plugin(
        id = "endercubeproxyplugin",
        name = "EndercubeProxyPlugin",
        version = "0.1.0"
)
public class EndercubeProxyPlugin {

    @Inject
    private Logger logger;

    public static MqttClient MQTTClient;

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        this.initMQTT();
    }

    private void initMQTT() {
        String broker = "tcp://localhost:1883";
        String clientID = "EndercubeProxyPlugin";
        MemoryPersistence persistence = new MemoryPersistence();


        try {
            logger.info("Initialising MQTT");
            MQTTClient = new MqttClient(broker, clientID, persistence);

            MQTTClient.setCallback(new OnMessageCallback());
            MQTTClient.connect();
            MQTTClient.subscribe("endercube/gotoMap/#", 2);
        } catch (MqttException exception) {
            logger.warn("reason " + exception.getReasonCode());
            logger.warn("msg " + exception.getMessage());
            logger.warn("loc " + exception.getLocalizedMessage());
            logger.warn("cause " + exception.getCause());
            logger.warn("excep " + exception);
            exception.printStackTrace();
        }
    }
}
