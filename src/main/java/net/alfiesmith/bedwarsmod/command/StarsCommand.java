package net.alfiesmith.bedwarsmod.command;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
public class StarsCommand implements ICommand {

  private static final long TIMEOUT = 15000;
  private static final char STAR = '\u272B';
  private static final DecimalFormat FORMAT = new DecimalFormat("##.00");
  private static final EnumChatFormatting YELLOW = EnumChatFormatting.YELLOW;
  private static final EnumChatFormatting GREEN = EnumChatFormatting.GREEN;

  private final HypixelApi api;
  private final ExecutorService service =
      Executors.newSingleThreadExecutor(runnable -> new Thread(runnable, "stars-service-thread"));

  @Override
  public String getCommandName() {
    return "stars";
  }

  @Override
  public String getCommandUsage(ICommandSender sender) {
    return "stars";
  }

  @Override
  public List<String> getCommandAliases() {
    return new ArrayList<>();
  }

  @Override
  public void processCommand(ICommandSender sender, String[] args) {
    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Loading stars..."));

    service.execute(() -> {
      List<HypixelPlayer> players = new ArrayList<>();

      List<NetworkPlayerInfo> playerInfo =
          new ArrayList<>(Minecraft.getMinecraft().getNetHandler().getPlayerInfoMap()).stream()
              .filter(info -> !isBot(info.getGameProfile().getName()))
              .collect(Collectors.toList());

      for (NetworkPlayerInfo player : playerInfo) {
        this.api.getPlayer(player.getGameProfile().getId()).thenAccept(players::add);
      }

      long start = System.currentTimeMillis();
      while (players.size() != entityPlayers.size()
          && start + TIMEOUT > System.currentTimeMillis()) {
      }

      players = players.stream()
          .filter(player -> !HypixelPlayer.isEmpty(player))
          .sorted(Comparator.comparing(HypixelPlayer::getBedwarsStats))
          .collect(Collectors.toList());

      sendMessages(sender, players);
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

  private void sendMessages(ICommandSender sender, List<HypixelPlayer> players) {
    for (int i = 0; i < players.size(); i++) {
      HypixelPlayer player = players.get(i);
      BedwarsStats stats = player.getBedwarsStats();
      ChatComponentText text = new ChatComponentText(
          player.getRank().getColor() + player.getDisplayName()
              + EnumChatFormatting.WHITE + " - "
              + player.getBedwarsStats().getLevelColor() + player.getBedwarsStats().getLevel()
              + STAR
      );

      text.getChatStyle().setChatHoverEvent(new HoverEvent(
          Action.SHOW_TEXT,
          new ChatComponentText(
              YELLOW + "Name: " + GREEN + player.getDisplayName() + "\n"
                  + YELLOW + "Network Level: " + GREEN + player.getNetworkLevel() + "\n"
                  + YELLOW + "Bedwars Level: " + GREEN + stats.getLevel() + STAR + "\n"
                  + YELLOW + "Bedwars Games: " + GREEN + stats.getGamesPlayed() + "\n"
                  + YELLOW + "Bedwars Wins: " + GREEN + stats.getWins() + "\n"
                  + YELLOW + "Bedwars Losses: " + GREEN + stats.getLosses() + "\n"
                  + YELLOW + "Bedwars W/L: " + GREEN + FORMAT.format(stats.getWinLossRatio()) + "\n"
                  + YELLOW + "Bedwars Kills: " + GREEN + stats.getKills() + "\n"
                  + YELLOW + "Bedwars Deaths: " + GREEN + stats.getDeaths() + "\n"
                  + YELLOW + "Bedwars K/D: " + GREEN + FORMAT.format(stats.getKillDeathRatio())
                  + "\n"
                  + YELLOW + "Bedwars Final Kills: " + GREEN + stats.getFinalKills() + "\n"
                  + YELLOW + "Bedwars Final Deaths: " + GREEN + stats.getFinalDeaths() + "\n"
                  + YELLOW + "Bedwars Final K/D: " + GREEN + FORMAT
                  .format(stats.getFinalKillDeathRatio()) + "\n"
                  + YELLOW + "Bedwars Winstreak: " + GREEN + stats.getWinstreak()
          )
      ));

      sender.addChatMessage(text);
    }
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
}
