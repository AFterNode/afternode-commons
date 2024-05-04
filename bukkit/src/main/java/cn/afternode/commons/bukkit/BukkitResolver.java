package cn.afternode.commons.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.UUID;

public class BukkitResolver {
    /**
     * Resolve online player
     * @param resolve Player name or UUID with format uuid:[uuid]
     * @return Result player
     */
    public static Player resolvePlayerOnline(String resolve) {
        if (resolve.startsWith("uuid:")) {
            String uuidStr = resolve.substring(5);
            UUID id;
            try {
                id = UUID.fromString(uuidStr);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid uuid %s".formatted(uuidStr), e);
            }
            return Bukkit.getPlayer(id);
        } else {
            return Bukkit.getPlayer(resolve);
        }
    }

    /**
     * Resolve player
     * @param resolve Player name or UUID with format uuid:[uuid]
     * @return Result player, not null when parameter is UUID
     */
    public static OfflinePlayer resolvePlayer(String resolve) {
        if (resolve.startsWith("uuid:")) {
            String uuidStr = resolve.substring(5);
            UUID id;
            try {
                id = UUID.fromString(uuidStr);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid uuid %s".formatted(uuidStr), e);
            }
            return Objects.requireNonNullElse(Bukkit.getPlayer(id), Bukkit.getOfflinePlayer(id));
        } else {
            return Bukkit.getPlayer(resolve);
        }
    }
}
