package cn.afternode.commons.bukkit.message;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class TabBuilder {
    private CommandSender sender = null;

    private final List<String> list = new ArrayList<>();

    public TabBuilder(
            CommandSender sender
    ) {
        this.sender = sender;
    }

    public CommandSender getSender() {
        return sender;
    }

    public TabBuilder sender(CommandSender sender) {
        this.sender = sender;
        return this;
    }

    public TabBuilder add(String prefix, String... items) {
        for (String i: items)
            if (i.startsWith(prefix))
                list.add(i);
        return this;
    }

    public TabBuilder players(String prefix) {
        Bukkit.getOnlinePlayers()
                .parallelStream()
                .map(Player::getName)
                .filter((s) -> s.startsWith(prefix))
                .forEach(list::add);
        return this;
    }

    public TabBuilder players(Predicate<Player> filter) {
        Bukkit.getOnlinePlayers()
                .stream()
                .filter(filter)
                .forEach((p) -> list.add(p.getName()));
        return this;
    }

    public TabBuilder worlds(String prefix) {
        for (World w: Bukkit.getWorlds()) {
            if (w.getName().startsWith(prefix))
                list.add(w.getName());
        }
        return this;
    }

    public TabBuilder worlds(Predicate<World> filter) {
        for (World w: Bukkit.getWorlds()) {
            if (filter.test(w))
                list.add(w.getName());
        }
        return this;
    }

    public TabBuilder permission(String permission, String prefix, String... items) {
        checkSender();

        if (sender.hasPermission(permission))
            for (String i: items)
                if (i.startsWith(prefix))
                    list.add(i);
        return this;
    }

    public TabBuilder permission(String permission, String... items) {
        if (sender.hasPermission(permission))
            Collections.addAll(list, items);
        return this;
    }

    public TabBuilder permission(Permission permission, String prefix, String ... items) {
        checkSender();

        if (sender.hasPermission(permission))
            for (String i: items)
                if (i.startsWith(prefix))
                    list.add(i);
        return this;
    }

    public TabBuilder permission(Permission permission, String... items) {
        checkSender();

        if (sender.hasPermission(permission))
            Collections.addAll(list, items);
        return this;
    }

    public List<String> build() {
        return new ArrayList<>(list);
    }

    private void checkSender() {
        if (sender == null)
            throw new NullPointerException("No sender passed to this builder");
    }
}
