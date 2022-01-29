package name.nkid00.rcutil.command;

import java.util.regex.Pattern;

import com.mojang.brigadier.context.CommandContext;

import name.nkid00.rcutil.RCUtil;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;

public class RcuNew {
    private static final Pattern componentSelectorPattern = Pattern.compile("(\\w+):(\\w+)");
    private static final Pattern connectionSelectorPattern = Pattern.compile("(\\w+):(\\w+)->(\\w+):(\\w+)");

    public static int execute(CommandContext<ServerCommandSource> c) {
        var s = c.getSource();
        var player = Command.getPlayerOrNull(s);
        if (player == null) {
            s.sendError(new TranslatableText("rcutil.commands.rcu.new.failed.notplayerentity"));
            return 0;
        }
        var uuid = player.getUuid();
        if (!RCUtil.getCommandStatus(uuid).equals(CommandStatus.Idle)) {
            s.sendError(new TranslatableText("rcutil.commands.rcu.new.failed.running"));
            return 0;
        }
        String option = null;
        try {
            option = c.getArgument("option", String.class);
        } catch (IllegalArgumentException e) {
        }
        var selector = c.getArgument("component or connection selector", String.class);
        // component selector
        var matcher = componentSelectorPattern.matcher(selector);
        if (matcher.matches()) {
            var type = matcher.group(1);
            var name = matcher.group(2);
            switch (type) {
                case "wires":
                    if (RCUtil.getWires(uuid).containsKey(name)) {
                        s.sendError(new TranslatableText("rcutil.commands.rcu.new.component.failed.exists", selector));
                        return 0;
                    }
                    RCUtil.getWiresBuilder(uuid).name = name;
                    RCUtil.setCommandStatus(uuid, CommandStatus.RcuNewWiresLsb);
                    s.sendFeedback(new TranslatableText("rcutil.commands.rcu.new.wires.step.lsb", RCUtil.wandItemHoverableText), false);
                    return 1;
                case "bus":
                    if (RCUtil.getWires(uuid).containsKey(name)) {
                        s.sendError(new TranslatableText("rcutil.commands.rcu.new.component.failed.exists", selector));
                        return 0;
                    }
                    return 1;
                case "addrbus":
                    if (RCUtil.getWires(uuid).containsKey(name)) {
                        s.sendError(new TranslatableText("rcutil.commands.rcu.new.component.failed.exists", selector));
                        return 0;
                    }
                    return 1;
                case "ram":
                    if (RCUtil.getWires(uuid).containsKey(name)) {
                        s.sendError(new TranslatableText("rcutil.commands.rcu.new.component.failed.exists", selector));
                        return 0;
                    }
                    return 1;
                case "fileram":
                    if (RCUtil.getWires(uuid).containsKey(name)) {
                        s.sendError(new TranslatableText("rcutil.commands.rcu.new.component.failed.exists", selector));
                        return 0;
                    }
                    return 1;
                default:
                    s.sendError(new TranslatableText("rcutil.commands.rcu.new.component.failed.type"));
                    return 0;
            }
        }
        // connection selector
        matcher = connectionSelectorPattern.matcher(selector);
        if (matcher.matches()) {
            var typeSource = matcher.group(1);
            var nameSource = matcher.group(2);
            var typeTarget = matcher.group(1);
            var nameTarget = matcher.group(2);
            return 0;
        }
        // not matched
        s.sendError(new TranslatableText("rcutil.commands.rcu.new.failed.selector"));
        return 0;
    }
}
