package me.zax71.endercubeproxyplugin.listeners;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;
import redis.clients.jedis.JedisPubSub;

import java.util.HashMap;
import java.util.UUID;

public class RedisSub extends JedisPubSub {

    private Logger logger;
    private ProxyServer proxy;

    @Inject
    public RedisSub(Logger logger, ProxyServer proxy) {
        this.logger = logger;
        this.proxy = proxy;
    }

    @Override
    public void onMessage(String channel, String message) {
        logger.info("Received Redis message on " + channel + " containing " + message);

        // Turn JSON into HashMap
        HashMap<String, String> map = new Gson().fromJson(message, new TypeToken<HashMap<String, String>>() {
        }.getType());

        // Retrieve a Player object from the UUID
        if (proxy.getPlayer(UUID.fromString(map.get("player"))).isEmpty()) {
            logger.warn("The UUID " + map.get("player") + " does not match an online player. Not forwarding to Parkour");
            return;
        }
        Player player = proxy.getPlayer(UUID.fromString(map.get("player"))).get();

        // Get the parkour server and send player there
        if (proxy.getServer("parkour").isEmpty()) {
            logger.warn("The parkour server does not exist on the proxy, this is a hard coded value as i cba to make a config so look in RedisSub and change it in the onMessage method");
            return;
        }
        player.createConnectionRequest(proxy.getServer("parkour").get()).connect();


    }
}
