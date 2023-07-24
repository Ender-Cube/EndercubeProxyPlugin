package me.zax71.endercubeproxyplugin;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import me.zax71.endercubeproxyplugin.listeners.RedisSub;
import org.slf4j.Logger;
import redis.clients.jedis.Jedis;

@Plugin(
        id = "endercubeproxyplugin",
        name = "EndercubeProxyPlugin",
        version = "1.0.0",
        authors = {"Zax71"}
)
public class EndercubeProxyPlugin {

    private Logger logger;
    private ProxyServer proxy;


    public static Jedis REDIS;

    @Inject
    public EndercubeProxyPlugin(Logger logger, ProxyServer proxy) {
        this.logger = logger;
        this.proxy = proxy;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        this.initRedis();
    }

    private void initRedis() {
        // Init Redis, hard coding values because config is too difficult
        REDIS = new Jedis(
                "redis",
                6379
        );

        // Create subscriber thread
        Thread newThread = new Thread(() -> {
            REDIS.subscribe(new RedisSub(logger, proxy), "endercube/proxy/map/switch");
        });
        newThread.start();
        logger.info("Started Redis subscribe thread");
    }
}
