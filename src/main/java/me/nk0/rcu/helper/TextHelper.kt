package me.nk0.rcu.helper

import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Formatting

object TextHelper {
    fun copy(text: Text): MutableText {
        return text.copy()
    }

    fun empty(): MutableText {
        return Text.empty()
    }

    @JvmStatic
    fun literal(string: String?): MutableText {
        return Text.literal(string)
    }

    @JvmStatic
    fun translatable(key: String?, vararg args: Any?): MutableText {
        return Text.translatable(key, *args)
    }

    @JvmStatic
    fun formatted(text: Text?, formatting: Formatting?): MutableText {
        return empty().append(text).formatted(formatting)
    }

    fun info(text: Text): MutableText {
        return text.copy()
    }

    @JvmStatic
    fun warn(text: Text?): MutableText {
        return formatted(text, Formatting.YELLOW)
    }

    @JvmStatic
    fun error(text: Text?): MutableText {
        return formatted(text, Formatting.RED)
    }
}
