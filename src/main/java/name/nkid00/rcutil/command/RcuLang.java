package name.nkid00.rcutil.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import name.nkid00.rcutil.exception.LanguageNotFoundException;
import name.nkid00.rcutil.helper.CommandHelper;
import name.nkid00.rcutil.helper.I18n;
import name.nkid00.rcutil.manager.LanguageManager;
import net.minecraft.server.command.ServerCommandSource;

public class RcuLang {
    public static int executeGet(CommandContext<ServerCommandSource> c) throws CommandSyntaxException {
        var s = c.getSource();
        var player = CommandHelper.requirePlayer(s);
        var uuid = player.getUuid();
        s.sendFeedback(I18n.t(uuid, "rcutil.command.rcu_lang.success.display",
                LanguageManager.langCode(player.getUuid())), false);
        return 1;
    }

    public static int executeSet(CommandContext<ServerCommandSource> c) throws CommandSyntaxException {
        var s = c.getSource();
        var player = CommandHelper.requirePlayer(s);
        var uuid = player.getUuid();
        var currentLangCode = LanguageManager.langCode(uuid);
        var langCode = StringArgumentType.getString(c, "language");
        if (langCode.equals(currentLangCode)) {
            s.sendError(I18n.t(uuid, "rcutil.command.rcu_lang.fail.already_set", currentLangCode));
            return 0;
        }
        try {
            LanguageManager.setLangCode(uuid, langCode);
        } catch (LanguageNotFoundException e) {
            s.sendError(I18n.t(uuid, "rcutil.command.rcu_lang.fail.invalid_language", langCode,
                    LanguageManager.languages()));
            return 0;
        }
        s.sendFeedback(I18n.t(uuid, "rcutil.command.rcu_lang.success.set", langCode), false);
        return 1;
    }
}
