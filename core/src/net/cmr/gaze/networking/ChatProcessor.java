package net.cmr.gaze.networking;

import net.cmr.gaze.game.ChatMessage;
import net.cmr.gaze.inventory.Item;
import net.cmr.gaze.inventory.Items;
import net.cmr.gaze.inventory.Items.ItemType;
import net.cmr.gaze.leveling.Skills.Skill;
import net.cmr.gaze.networking.GameServer.ServerType;
import net.cmr.gaze.networking.packets.ChatPacket;
import net.cmr.gaze.world.Tile;

public abstract class ChatProcessor {

    public static final ChatProcessor COMMAND_PROCESSOR = new ChatProcessor() {
        @Override
        public boolean processMessage(PlayerConnection connection, ChatMessage message) {
            if (message.getMessage().startsWith("/")) {

                if(connection.server.serverType == ServerType.MultiplayerPublic) {
                    connection.getSender().addPacket(new ChatPacket(new ChatMessage("SERVER", "You cannot use commands in a public server.")));
                    return true;
                }

                String[] args = message.getMessage().substring(1).split(" ");
                String command = args[0];
                String[] commandArgs = new String[args.length - 1];
                System.arraycopy(args, 1, commandArgs, 0, commandArgs.length);

                if(command.equalsIgnoreCase("give")) {
                    if(commandArgs.length==0) {
                        connection.getSender().addPacket(new ChatPacket(new ChatMessage("SERVER", "Usage: /give <item> [amount]")));
                        return true;
                    }
                    int amount = 1;
                    if(commandArgs.length==2) {
                        try {
                            amount = Integer.parseInt(commandArgs[1]);
                        } catch(NumberFormatException e) {
                            connection.getSender().addPacket(new ChatPacket(new ChatMessage("SERVER", "Invalid amount: " + commandArgs[1])));
                            return true;
                        }
                    }
                    if(ItemType.itemIDExists(commandArgs[0].hashCode())) {
                        ItemType type = ItemType.getItemTypeFromID(commandArgs[0].hashCode());
                        if(amount > type.getMaxStackSize()) {
                            connection.getSender().addPacket(new ChatPacket(new ChatMessage("SERVER", "Amount too large: " + amount)));
                            return true;
                        }
                        Item item = Items.getItem(type, amount);
                        connection.getPlayer().getInventory().add(item);
                        connection.inventoryChanged();
                        connection.getSender().addPacket(new ChatPacket(new ChatMessage("SERVER", "Gave " + amount + " " + type.name())));
                    } else {
                        connection.getSender().addPacket(new ChatPacket(new ChatMessage("SERVER", "Invalid item: " + commandArgs[0])));
                        return true;
                    }
                } else if(command.equalsIgnoreCase("tp")) {
                    if(commandArgs.length == 2) {
                        try {
                            double x = Double.parseDouble(commandArgs[0]);
                            double y = Double.parseDouble(commandArgs[1]);
                            connection.getPlayer().getWorld().teleportPlayerToWorld(connection.getPlayer(), x*Tile.TILE_SIZE, y*Tile.TILE_SIZE);
                            connection.getSender().addPacket(new ChatPacket(new ChatMessage("SERVER", "Teleported to " + x + ", " + y)));
                            return true;
                        } catch(NumberFormatException e) {
                            connection.getSender().addPacket(new ChatPacket(new ChatMessage("SERVER", "Invalid amount: " + commandArgs[1])));
                            return true;
                        }
                    } else {
                        connection.getSender().addPacket(new ChatPacket(new ChatMessage("SERVER", "Usage: /tp <x> <y>")));
                        return true;
                    }
                } else if(command.equalsIgnoreCase("skill")) {
                    if(commandArgs.length >= 1) {
                        Skill skill = null;
                        try {
                            skill = Skill.valueOf(commandArgs[0].toUpperCase());
                        } catch(Exception e) {
                            connection.getSender().addPacket(new ChatPacket(new ChatMessage("SERVER", "Invalid skill: " + commandArgs[0])));
                            return true;
                        }
                        if(commandArgs.length == 2) {
                            try {
                                int level = Integer.parseInt(commandArgs[1]);
                                connection.getPlayer().setLevel(skill, level);
                                connection.getSender().addPacket(new ChatPacket(new ChatMessage("SERVER", "Set skill level for "+skill.name()+" to " + level)));
                                return true;
                            } catch(NumberFormatException e) {
                                connection.getSender().addPacket(new ChatPacket(new ChatMessage("SERVER", "Invalid amount: " + commandArgs[1])));
                                return true;
                            }
                        } else {
                            connection.getSender().addPacket(new ChatPacket(new ChatMessage("SERVER", "Current skill level for "+skill.name()+": " + connection.getPlayer().getSkills().getLevel(skill))));
                            return true;
                        }
                    }
                    connection.getSender().addPacket(new ChatPacket(new ChatMessage("SERVER", "Usage: /skill <skill> [level]")));
                    return true;
                }
                return true;
            }
            return false;
        }
    };

    /** 
     * @return false if the message should still be sent to other players, true otherwise
    */
    public abstract boolean processMessage(PlayerConnection connection, ChatMessage message);
}
