package me.zax71.endercubeproxyplugin.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Dependency;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;

import java.util.Objects;

@CommandAlias("hub|lobby")
public class HubCommand extends BaseCommand {

    @Dependency
    private ProxyServer proxy;

    @Default
    public void onHub(Player player, String[] args) {
        if (player.getCurrentServer().isPresent() && Objects.equals(player.getCurrentServer().get().getServerInfo().getName(), "hub")) {
            player.sendMessage(Component.text("You're already in the hub!"));
            return;
        }

        player.sendMessage(Component.text("Sending you back to hub"));
        player.createConnectionRequest(proxy.getServer("hub").get()).connect();
    }
}
