package org.nott.executor;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.nott.SimpleHearts;
import org.nott.utils.Permissions;

import java.util.List;

/**
 * @author Nott
 * @date 2025-2-7
 */
public class HeartExecutor implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        String commandType = args[0];

        switch (commandType){
            default -> helpCommand(commandSender);
            case "help" -> helpCommand(commandSender);
            case "buy" -> buyCommand(commandSender);
            case "set" -> adminSetCommand(commandSender, args);
            case "reload" -> reloadCommand(commandSender);
        }

        return true;
    }

    private void adminSetCommand(CommandSender commandSender, String[] args) {
        Permissions.hasPermission(commandSender, "simpleheart.admin");
        if (args.length != 3) {
            commandSender.sendMessage(ChatColor.RED + SimpleHearts.MESSAGE_YML_FILE.getString("common.admin_set_error"));
            return;
        }
        Player player = SimpleHearts.INSTANCE.getServer().getPlayerExact(args[0]);
        if (player == null) {
            commandSender.sendMessage(ChatColor.RED + SimpleHearts.MESSAGE_YML_FILE.getString("common.player_not_found"));
            return;
        }
        double health = 0;
        try {
            health = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            SimpleHearts.INSTANCE.logger.warning("Health value must be a number");
            commandSender.sendMessage(ChatColor.RED + SimpleHearts.MESSAGE_YML_FILE.getString("common.admin_set_error"));
            return;
        }
        if(health > SimpleHearts.CONFIG_YML_FILE.getDouble("heart.max-heart",40)){
            commandSender.sendMessage(ChatColor.RED + SimpleHearts.MESSAGE_YML_FILE.getString("common.admin_set_max").replaceAll("\\{max}", String.valueOf(SimpleHearts.CONFIG_YML_FILE.getDouble("heart.max-heart",40))));
            return;
        }
        player.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
        commandSender.sendMessage(ChatColor.GREEN + SimpleHearts.MESSAGE_YML_FILE.getString("common.admin_set_success").replaceAll("\\{player}", player.getName()).replaceAll("\\{health}", String.valueOf(health)));
    }

    private void reloadCommand(CommandSender commandSender) {
        Permissions.hasPermission(commandSender, "simpleheart.admin");
        SimpleHearts.INSTANCE.pluginInit();
        commandSender.sendMessage(ChatColor.GREEN + SimpleHearts.MESSAGE_YML_FILE.getString("common.reload"));
    }

    private void buyCommand(CommandSender commandSender) {
        Permissions.hasPermission(commandSender, "simpleheart.player");
        Player buyer = (Player) commandSender;
        double health = buyer.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).getBaseValue();
        if (health == SimpleHearts.CONFIG_YML_FILE.getDouble("heart.max-heart",40)) {
            commandSender.sendMessage(ChatColor.RED + SimpleHearts.MESSAGE_YML_FILE.getString("common.buy_max"));
            return;
        }
        double price = SimpleHearts.CONFIG_YML_FILE.getDouble("heart.price");
        if (SimpleHearts.ECONOMY.has(buyer, price)) {
            SimpleHearts.ECONOMY.withdrawPlayer(buyer, price);
            buyer.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).setBaseValue(health + 2);
            health = buyer.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).getBaseValue();
            buyer.sendMessage(ChatColor.GREEN + SimpleHearts.MESSAGE_YML_FILE.getString("common.buy_success") + (health));
        } else {
            buyer.sendMessage(ChatColor.RED + SimpleHearts.MESSAGE_YML_FILE.getString("common.buy_failed").replaceAll("\\{price}", String.valueOf(price)));
        }
    }

    private void helpCommand(CommandSender commandSender) {
        List<String> list = SimpleHearts.MESSAGE_YML_FILE.getStringList("help");
        StringBuffer bf = new StringBuffer();
        for (String str : list) {
            if (str.contains("{price}")){
                str = str.replaceAll("\\{price}", String.valueOf(SimpleHearts.CONFIG_YML_FILE.getDouble("heart.price")));
            }
            bf.append(ChatColor.GOLD + str).append("\n");
        }
        commandSender.sendMessage(bf.toString());
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(args.length == 1 && Permissions.hasPermission(commandSender,"simpleheart.admin")){
            return List.of("help", "buy", "set", "reload");
        }else {
            return List.of("help", "buy");
        }
    }
}
