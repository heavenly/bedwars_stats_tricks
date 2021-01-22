package net.alfiesmith.bedwarsmod.command;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.RequiredArgsConstructor;
import net.alfiesmith.bedwarsmod.api.HypixelApi;
import net.alfiesmith.bedwarsmod.api.model.HypixelPlayer;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

@RequiredArgsConstructor
public class StarsCommand implements ICommand {

  private static final char STAR = '\u272B';

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
      for (EntityPlayer player : sender.getEntityWorld().playerEntities) {
        HypixelPlayer hypixelPlayer = this.api.getPlayer(player.getUniqueID());
        if (!HypixelPlayer.isEmpty(hypixelPlayer)) {
          players.add(this.api.getPlayer(player.getUniqueID()));
        }
      }

      players.sort(Comparator.comparing(HypixelPlayer::getBedwarsStats));

      ChatComponentText text = new ChatComponentText("");
      for (int i = 0; i < players.size(); i++) {
        HypixelPlayer player = players.get(i);
        text.appendText(
            player.getRank().getColor() + player.getDisplayName()
                + EnumChatFormatting.WHITE + " - "
                + player.getBedwarsStats().getLevelColor() + player.getBedwarsStats().getLevel()
                + STAR
        );

        if (i + 1 < players.size()) {
          text.appendText("\n");
        }
      }

      sender.addChatMessage(text);
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
}
