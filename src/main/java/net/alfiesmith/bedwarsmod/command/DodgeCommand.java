package net.alfiesmith.bedwarsmod.command;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import net.alfiesmith.bedwarsmod.api.HypixelApi;
import net.alfiesmith.bedwarsmod.api.model.BedwarsStats;
import net.alfiesmith.bedwarsmod.api.model.HypixelPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.event.HoverEvent;
import net.minecraft.event.HoverEvent.Action;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

@RequiredArgsConstructor
public class DodgeCommand implements ICommand {

    private static final long TIMEOUT = 7000;
    private static final char STAR = '\u272B';
    private static final DecimalFormat FORMAT = new DecimalFormat("##.00");
    private static final EnumChatFormatting YELLOW = EnumChatFormatting.YELLOW;
    private static final EnumChatFormatting GREEN = EnumChatFormatting.GREEN;

    private final HypixelApi api;
    private final ExecutorService service =
            Executors.newSingleThreadExecutor(runnable -> new Thread(runnable, "dodge-service-thread"));

    @Override
    public String getCommandName() {
        return "dodge";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "dodge";
    }

    @Override
    public List<String> getCommandAliases() {
        return new ArrayList<>();
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "attempting dodge..."));

        service.execute(() -> {
            Set<NetworkPlayerInfo> playerInfo =
                    Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap().stream()
                            .filter(info -> !isBot(info.getGameProfile().getName()))
                            .collect(Collectors.toSet());


            AtomicBoolean should_stop = new AtomicBoolean(false); //prevent spamming the dodge command
            for (NetworkPlayerInfo player : playerInfo) {
                this.api.getPlayer(player.getGameProfile().getId()).thenAccept(hypixelPlayer -> {
                    should_stop.set(!sendMessage(sender, hypixelPlayer, player, should_stop));
                });
                if (should_stop.get())
                    break;
            }


        });
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        return new ArrayList<>();
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    @Override
    public int compareTo(ICommand o) {
        return 0;
    }

    private boolean sendMessage(ICommandSender sender, HypixelPlayer player, NetworkPlayerInfo raw_player, AtomicBoolean should_stop) {
        if (should_stop.get())
            return false;

        if (player == null || player.getDisplayName() == null) {
            ChatComponentText text = new ChatComponentText(
                    EnumChatFormatting.BOLD.toString() + EnumChatFormatting.RED + raw_player.getGameProfile().getName() + " is nicked!"
            );
            sender.addChatMessage(text);
            return true;
        }

        BedwarsStats stats = player.getBedwarsStats();
        if (stats == null) {
            ChatComponentText text = new ChatComponentText(
                    EnumChatFormatting.BOLD.toString() + EnumChatFormatting.RED + raw_player.getGameProfile().getName() + " is nicked!"
            );
            sender.addChatMessage(text);
            return true;
        }

        //chat data
        ChatComponentText text = new ChatComponentText(
                player.getRank().getColor().toString()
                        + ((isDecentPlayer(player)) ? EnumChatFormatting.BOLD.toString() : "") +
                        player.getDisplayName()
                        + EnumChatFormatting.WHITE + " - "
                        + player.getBedwarsStats().getLevelColor() + player.getBedwarsStats().getLevel()
                        + STAR
                        + EnumChatFormatting.WHITE
                        + " - FKDR: " + FORMAT
                        .format(stats.getFinalKillDeathRatio())
                        + " - SI: " + String.format("%.2f", stats.scary_index)
        );
        if (stats.getWinstreak() > 0)
            text.appendText(" - WS: " + stats.getWinstreak());

        //hover data
        text.getChatStyle().setChatHoverEvent(new HoverEvent(
                Action.SHOW_TEXT,
                new ChatComponentText(
                        YELLOW + "Name: " + GREEN + player.getDisplayName() + "\n"
                                + YELLOW + "Bedwars Level: " + GREEN + stats.getLevel() + STAR + "\n"
                                + YELLOW + "Bedwars Games: " + GREEN + stats.getGamesPlayed() + "\n"
                                + YELLOW + "Bedwars Wins: " + GREEN + stats.getWins() + "\n"
                                + YELLOW + "Bedwars Losses: " + GREEN + stats.getLosses() + "\n"
                                + YELLOW + "Bedwars W/L: " + GREEN + FORMAT.format(stats.getWinLossRatio()) + "\n"
                                + YELLOW + "Bedwars Final Kills: " + GREEN + stats.getFinalKills() + "\n"
                                + YELLOW + "Bedwars Final Deaths: " + GREEN + stats.getFinalDeaths() + "\n"
                                + YELLOW + "Bedwars Final K/D: " + GREEN + FORMAT
                                .format(stats.getFinalKillDeathRatio()) + "\n"
                                + YELLOW + "Bedwars Winstreak: " + GREEN + stats.getWinstreak()
                )
        ));

        sender.addChatMessage(text);

        if (raw_player.getGameProfile().getName().equalsIgnoreCase(Minecraft.getMinecraft().thePlayer.getGameProfile().getName()))
            return true; //prevent auto dodge


        //do dodging stuff here
        double fkdr = stats.getFinalKillDeathRatio();
        int winstreak = stats.getWinstreak();
        int level = stats.getLevel();
        ChatComponentText dodge_text = new ChatComponentText(
                EnumChatFormatting.BOLD.toString() + EnumChatFormatting.RED + "scary user dodged!"
        );

        double threat_level = (level * fkdr * fkdr) / 10;
        if (threat_level > SetScaryCommand.scary_index) {
            sender.addChatMessage(dodge_text);
            Minecraft.getMinecraft().thePlayer.sendChatMessage("/play " + SetDodgeCommand.apply_dodge);
            return false;
        }
        return true;
    }

    private boolean isBot(String name) {
        if (name == null || name.isEmpty()) {
            return true;
        }
        // all bot names are 10 characters in length
        if (name.length() != 10) {
            return false;
        }
        int num = 0, let = 0;
        for (char c : name.toCharArray()) {
            if (Character.isLetter(c)) {
                if (Character.isUpperCase(c)) {
                    // npc does not have upper case letter
                    return false;
                }
                let++;
            } else if (Character.isDigit(c)) {
                num++;
            } else {
                // npc name is alphanumerical
                return false;
            }
        }

        return num >= 2 && let >= 2;
    }

    private boolean isDecentPlayer(HypixelPlayer player) {
        BedwarsStats stats = player.getBedwarsStats();
        return stats.getFinalKillDeathRatio() >= 2.0 || stats.getWinstreak() >= 10
                || stats.getLevel() >= 100 || stats.getWinLossRatio() >= 1.2
                || stats.getGamesPlayed() >= 1500;
    }
}
