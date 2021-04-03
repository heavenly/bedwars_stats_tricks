package net.alfiesmith.bedwarsmod.command;

import lombok.RequiredArgsConstructor;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class BWHelpCommand implements ICommand {

    @Override
    public String getCommandName() {
        return "help_bw_utils";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/help_bw_utils";
    }

    @Override
    public List<String> getCommandAliases() {
        return new ArrayList<>();
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        sender.addChatMessage(
                new ChatComponentText(EnumChatFormatting.AQUA + "commands are: \n"
                    + "/set_scary (threat index, which is (stars * fkdr^2) / 10) \n"
                    + "/set_dodge bedwars_four_four (or whatever your preferred mode command) \n"
                    + "/dodge - attempts to dodge \n"
                    + "/stars - view stars")
        );
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
