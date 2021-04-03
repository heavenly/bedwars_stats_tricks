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
public class SetScaryCommand implements ICommand {
    public static double scary_index = 2.0;

    @Override
    public String getCommandName() {
        return "set_scary";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/set_scary threat_index";
    }

    @Override
    public List<String> getCommandAliases() {
        return new ArrayList<>();
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length != 1) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "expected 1 arguments: (star * fkdr^2) / 10."));
            return;
        }

        scary_index = Double.parseDouble(args[0]); //80 is good
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + "set scary index: " + scary_index));
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
