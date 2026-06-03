package org.ChickenDJR.xSex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class SexTabCompleter implements TabCompleter {

    private static final List<String> SUB_COMMANDS = List.of("accept", "deny", "gender");
    private static final List<String> GENDERS = List.of("male", "female");

    private final Main plugin;

    public SexTabCompleter(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> suggestions = new ArrayList<>(SUB_COMMANDS);
            for (Player player : Bukkit.getOnlinePlayers()) {
                suggestions.add(player.getName());
            }
            return filter(suggestions, args[0]);
        }

        if (args.length == 2) {
            String sub = args[0].toLowerCase();
            if (sub.equals("accept") || sub.equals("deny")) {
                List<String> names = new ArrayList<>();
                for (String requester : plugin.getManager().requests.keySet()) {
                    String target = plugin.getManager().requests.get(requester);
                    if (target != null && target.equals(sender.getName())) {
                        names.add(requester);
                    }
                }
                return filter(names, args[1]);
            }
            if (sub.equals("gender")) {
                return filter(GENDERS, args[1]);
            }
            return Collections.emptyList();
        }

        return Collections.emptyList();
    }

    private List<String> filter(List<String> source, String prefix) {
        String lower = prefix.toLowerCase();
        return source.stream()
                .filter(s -> s.toLowerCase().startsWith(lower))
                .collect(Collectors.toList());
    }
}
