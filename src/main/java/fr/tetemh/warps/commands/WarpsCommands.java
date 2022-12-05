package fr.tetemh.warps.commands;

import fr.tetemh.warps.Warps;
import fr.tetemh.warps.customclass.CWarp;
import fr.tetemh.warps.managers.CWarpsManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class WarpsCommands implements CommandExecutor {

    private CWarpsManager cWarpsManager = CWarpsManager.getInstance();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof Player){
            Player player = (Player) sender;
            List<String> banWords = new ArrayList<>();
            banWords.add("add");
            banWords.add("remove");
            banWords.add("list");
            if(args.length >= 1){
                switch(args[0].toLowerCase()){
                    case "add":
                        if(player.isOp() && args.length >= 2 && !banWords.contains(args[1])){
                            CWarp cWarp = new CWarp(args[1], player.getLocation());
                            if(args.length >= 3){
                                cWarp.setPrice(Double.parseDouble(args[2]));
                            }
                            cWarpsManager.addWarp(cWarp);
                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§aLe warp " + args[1] + " à bien été définit"));
                        }
                        break;
                    case "remove":
                        if(player.isOp() && args.length >= 2){
                            cWarpsManager.removeWarp(args[1]);
                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§aLe warp " + args[1] + " à bien été supprimé"));
                        }
                        break;
                    case "list":
                        player.sendMessage("§e------------------");
                        if(player.isOp()){
                            player.sendMessage("§ename | active");
                            cWarpsManager.getAllWarps().values().forEach(cWarp -> {
                                player.sendMessage("§e"+cWarp.getName()+" | "+cWarp.isActive());
                            });
                        }else{
                            cWarpsManager.getWarps().values().forEach(cWarp -> {
                                player.sendMessage("§e"+cWarp.getName());
                            });
                        }
                        player.sendMessage("§e------------------");

                    default:
                        if(cWarpsManager.getWarps().containsKey(args[0].toLowerCase().replace(" ", "-"))){
                            CWarp cWarp = cWarpsManager.getWarps().get(args[0]);
                            if(cWarp.isActive()){
                                List<PotionEffect> beforePotion = new ArrayList<>(player.getActivePotionEffects());
                                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 99999, 255));
                                player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 99999, 200));
                                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 99999, 255));
                                new BukkitRunnable() {
                                    int counter = 3;
                                    @Override
                                    public void run() {
                                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§aTéléportation dans " + counter));
                                        counter--;
                                        player.sendMessage();
                                        if(counter == 0){
                                            player.teleport(cWarp.getSpawn());
                                            player.getActivePotionEffects().forEach(potionEffect -> {
                                                player.removePotionEffect(potionEffect.getType());
                                            });
                                            beforePotion.forEach(player::addPotionEffect);
                                            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§aVoues avez bien été téléporter"));
                                            cancel();
                                        }
                                    }
                                }.runTaskTimer(Warps.getInstance(), 1, 20);

                            }else{
                                player.sendMessage("§cCe warp est actuellement désactiver");
                            }
                        }

                        break;
                }
            }else{

            }
        }
        return false;
    }
}
