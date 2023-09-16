package me.zax71.endercubeproxyplugin.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;

@CommandAlias("discord")
public class DiscordCommand extends BaseCommand {

    @Default
    public void onDiscord(Player player, String[] args) {

        player.sendMessage(
                Component.text("Join our ", TextColor.fromHexString("#7289da"))
                        .append(
                                Component.text("Discord", TextColor.fromHexString("#7289da"))
                                        .clickEvent(ClickEvent.openUrl("https://discord.gg/x3aynQK"))
                                        .decoration(TextDecoration.UNDERLINED, true)
                                        .decoration(TextDecoration.BOLD, true)
                        )
                        .append(Component.text("!", TextColor.fromHexString("#7289da")))
        );
    }
}
