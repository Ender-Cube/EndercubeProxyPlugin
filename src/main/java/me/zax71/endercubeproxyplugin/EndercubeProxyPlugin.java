package me.zax71.endercubeproxyplugin;

import co.aikar.commands.VelocityCommandManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import com.zaxxer.hikari.HikariDataSource;
import me.zax71.endercubeproxyplugin.commands.HubCommand;
import me.zax71.endercubeproxyplugin.commands.ParkourLeaderboardCommand;
import me.zax71.endercubeproxyplugin.listeners.RedisSub;
import net.endercube.EndercubeCommon.SQLWrapper;
import org.slf4j.Logger;
import redis.clients.jedis.Jedis;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Plugin(
        id = "endercubeproxyplugin",
        name = "EndercubeProxyPlugin",
        version = "1.1.0",
        authors = {"Zax71"}
)
public class EndercubeProxyPlugin {

    private Logger logger;
    private ProxyServer proxy;
    public static SQLWrapper SQL;

    @Inject
    public EndercubeProxyPlugin(Logger logger, ProxyServer proxy) {
        this.logger = logger;
        this.proxy = proxy;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        this.initRedis();

        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            logger.error("Could not find MariaDB JDBC driver");
            e.printStackTrace();
        }

        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:mariadb://mariadb:3306/endercube?createDatabaseIfNotExist=true");
        dataSource.setUsername(System.getenv("MARIADB_USER"));
        dataSource.setPassword(System.getenv("MARIADB_PASSWORD"));

        SQL = new SQLWrapper(dataSource);

        VelocityCommandManager manager = new VelocityCommandManager(proxy, this);
        manager.registerCommand(new ParkourLeaderboardCommand());
        manager.registerCommand(new HubCommand());

        // Add parkourMaps completion
        manager.getCommandCompletions().registerCompletion("parkourMaps", context ->
                getParkourMapsFromRedis()
                        .stream()
                        .map(map -> map.get("name"))
                        .toList()
        );

    }

    private List<HashMap<String, String>> getParkourMapsFromRedis() {
        // Init Redis, hard coding values because config is too difficult
        Jedis redis = new Jedis(
                "redis",
                6379
        );

        Type typeToken = new TypeToken<ArrayList<HashMap<String, String>>>() {
        }.getType();

        return new Gson().fromJson(redis.get("parkourMaps"), typeToken);
    }

    private void initRedis() {
        // Init Redis, hard coding values because config is too difficult
        Jedis redis = new Jedis(
                "redis",
                6379
        );

        // Create subscriber thread
        Thread newThread = new Thread(() -> {
            redis.subscribe(new RedisSub(logger, proxy), "endercube/proxy/map/switch");
        });
        newThread.start();
        logger.info("Started Redis subscribe thread");
    }
}
