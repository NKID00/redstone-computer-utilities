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
        I18n.sendFeedback(s, false, "rcutil.command.rcu_lang.success.display",
                LanguageManager.langCode(player.getUuid()));
        return 1;
    }

    public static int executeSet(CommandContext<ServerCommandSource> c) throws CommandSyntaxException {
        var s = c.getSource();
        var player = CommandHelper.requirePlayer(s);
        var uuid = player.getUuid();
        var currentLangCode = LanguageManager.langCode(uuid);
        var langCode = StringArgumentType.getString(c, "language");
        if (langCode.equals(currentLangCode)) {
            I18n.sendError(s, "rcutil.command.rcu_lang.fail.already_set", currentLangCode);
            return 0;
        }
        try {
            LanguageManager.setLangCode(uuid, langCode);
        } catch (LanguageNotFoundException e) {
            I18n.sendError(s, "rcutil.command.rcu_lang.fail.invalid_language", langCode,
                    LanguageManager.languages());
            return 0;
        }
        I18n.sendFeedback(s, false, "rcutil.command.rcu_lang.success.set", langCode);
        return 1;
    }
}
