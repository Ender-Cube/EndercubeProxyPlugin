package me.zax71.endercubeproxyplugin.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import com.velocitypowered.api.proxy.Player;
import me.zax71.endercubeproxyplugin.utils.ComponentUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;

import static me.zax71.endercubeproxyplugin.EndercubeProxyPlugin.SQLite;
import static me.zax71.endercubeproxyplugin.utils.ComponentUtils.toHumanReadableTime;

@CommandAlias("parkourleaderboard")
public class ParkourLeaderboardCommand extends BaseCommand {
    @Default
    @CommandCompletion("@parkourMaps")
    public static void onLeaderboard(Player player, String[] args) {

        // Check if it is out of bounds
        if (!(0 < args.length)) {
            player.sendMessage(Component.text("Please specify a parkour map"));
            return;
        }

        if (args[0] != null) {
            player.sendMessage(createLeaderboard(args[0]));
        }
    }

    private static TextComponent createLeaderboard(String mapName) {
        Component placementComponent = Component.text()
                .append(leaderboardEntry("#FFD700", mapName, 1))
                .append(leaderboardEntry("#808080", mapName, 2))
                .append(leaderboardEntry("#CD7F32", mapName, 3))
                .append(Component.newline())
                .build();

        for (int i = 0; i < 10 - 3; i++) {
            placementComponent = placementComponent.append(leaderboardEntry("#AAAAAA", mapName, i + 4));
        }

        return Component.text()
                .append(ComponentUtils.centerComponent(MiniMessage.miniMessage().deserialize("<bold><gradient:#FF416C:#FF4B2B>All Time Leaderboard For " + mapName)))
                .append(Component.newline())
                .append(Component.newline())
                .append(placementComponent)
                .build();
    }

    private static Component leaderboardEntry(String color, String mapName, int placement) {
        String placementToNameGap;
        if (SQLite.getPlayerOverall(mapName, placement) == null) {
            return Component.empty();
        }
        if (placement >= 10) {
            placementToNameGap = " ";
        } else {
            placementToNameGap = "  ";
        }
        return MiniMessage.miniMessage().deserialize("<" + color + ">#<bold>" + placement + placementToNameGap + SQLite.getPlayerOverall(mapName, placement) + "</bold> " + toHumanReadableTime(SQLite.getTimeOverall(mapName, placement)))
                .append(Component.newline());
    }
}
