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
public class SetDodgeCommand implements ICommand {
    public static String apply_dodge = "bedwars_four_four"; //default dodge command

    @Override
    public String getCommandName() {
        return "set_dodge";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/set_dodge game_mode";
    }

    @Override
    public List<String> getCommandAliases() {
        return new ArrayList<>();
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length != 1) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "expected 1 argument."));
            return;
        }

        apply_dodge = args[0];
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + "set dodge command to: /play " + apply_dodge));
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
