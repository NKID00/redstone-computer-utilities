package me.nk0.rcu.helper

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import com.mojang.brigadier.suggestion.Suggestion
import com.mojang.brigadier.suggestion.SuggestionProvider
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import me.nk0.rcu.manager.InterfaceManager
import me.nk0.rcu.util.TypedArgument
import me.nk0.rcu.util.TypedArgumentType
import net.minecraft.util.Identifier
import java.util.*
import java.util.concurrent.ExecutionException
import java.util.function.Consumer
import java.util.function.Function

object ArgumentHelper {
    private fun anyUnquotedStringReader(s: String): StringReader {
        return object : StringReader(s) {
            override fun readUnquotedString(): String {
                val begin = cursor
                while (canRead() && CommandHelper.isAllowedInUnquotedString(peek())) {
                    skip()
                }
                return string.substring(begin, cursor)
            }

            @Throws(CommandSyntaxException::class)
            override fun readString(): String {
                if (canRead()) {
                    val c = peek()
                    if (isQuotedStringStart(c)) {
                        skip()
                        return readStringUntil(c)
                    }
                    val result = readUnquotedString()
                    if ((!canRead()) || peek() == CommandDispatcher.ARGUMENT_SEPARATOR_CHAR) {
                        return result
                    } else {
                        throw CommandSyntaxException.BUILT_IN_EXCEPTIONS
                            .dispatcherExpectedArgumentSeparator().createWithContext(this)
                    }
                }
                return ""
            }
        }
    }

    @Throws(CommandSyntaxException::class)
    private fun parseMulti(greedyString: String): LinkedList<String> {
        val reader = anyUnquotedStringReader(greedyString)
        val result = LinkedList<String>()
        var watchdogCursor = -1
        while (reader.canRead()) {
            if (reader.cursor == watchdogCursor) {
                Log.error("Dead loop encountered with {} at {}", greedyString, watchdogCursor)
                break
            } else {
                watchdogCursor = reader.cursor
            }
            reader.skipWhitespace()
            if (!reader.canRead()) {
                break
            }
            result.add(reader.readString())
        }
        return result
    }

    @JvmStatic
    @Throws(CommandSyntaxException::class)
    fun <S> getMulti(context: CommandContext<S>?, name: String): LinkedList<String> {
        return try {
            parseMulti(StringArgumentType.getString(context, name))
        } catch (e: IllegalArgumentException) {
            LinkedList<String>()
        }
    }

    private fun anyUnquotedKeepQuotationMarkStringReader(s: String): StringReader {
        return object : StringReader(s) {
            override fun readUnquotedString(): String {
                val begin = cursor
                while (canRead() && CommandHelper.isAllowedInUnquotedString(peek())) {
                    skip()
                }
                return string.substring(begin, cursor)
            }

            @Throws(CommandSyntaxException::class)
            override fun readString(): String {
                if (canRead()) {
                    val c = peek()
                    if (isQuotedStringStart(c)) {
                        skip()
                        return CommandHelper.quoted(readStringUntil(c))
                    }
                    val result = readUnquotedString()
                    if ((!canRead()) || peek() == CommandDispatcher.ARGUMENT_SEPARATOR_CHAR) {
                        return result
                    } else {
                        throw CommandSyntaxException.BUILT_IN_EXCEPTIONS
                            .dispatcherExpectedArgumentSeparator().createWithContext(this)
                    }
                }
                return ""
            }
        }
    }

    @Throws(CommandSyntaxException::class)
    private fun parseTypedMulti(greedyString: String): LinkedList<TypedArgument> {
        val reader = anyUnquotedKeepQuotationMarkStringReader(greedyString)
        val result = LinkedList<TypedArgument>()
        var watchdogCursor = -1
        while (reader.canRead()) {
            if (reader.cursor == watchdogCursor) {
                Log.error("Dead loop encountered with {} at {}", greedyString, watchdogCursor)
                break
            } else {
                watchdogCursor = reader.cursor
            }
            reader.skipWhitespace()
            if (!reader.canRead()) {
                break
            }
            val s = reader.readString()
            if (CommandHelper.isQuoted(s)) {
                result.add(TypedArgument(TypedArgumentType.Literal, CommandHelper.unquoted(s)))
            } else if (s.contains(":")) {
                val identifier = Identifier.tryParse(s)
                if (identifier == null) {
                    result.add(TypedArgument(TypedArgumentType.Literal, s))
                } else {
                    val type = TypedArgumentType.fromString(identifier.namespace)
                    if (type == TypedArgumentType.Literal) {
                        result.add(TypedArgument(TypedArgumentType.Literal, s))
                    } else {
                        result.add(TypedArgument(type, identifier.path))
                    }
                }
            } else if (CommandHelper.isLetterDigitUnderline(s)) {
                if (InterfaceManager.nameExists(s)) {
                    result.add(TypedArgument(TypedArgumentType.Interface, s))
                } else {
                    result.add(TypedArgument(TypedArgumentType.Literal, s))
                }
            } else {
                result.add(TypedArgument(TypedArgumentType.Literal, s))
            }
        }
        return result
    }

    @JvmStatic
    @Throws(CommandSyntaxException::class)
    fun <S> getTypedMulti(context: CommandContext<S>?, name: String): LinkedList<TypedArgument> {
        return try {
            parseTypedMulti(StringArgumentType.getString(context, name))
        } catch (e: IllegalArgumentException) {
            LinkedList<TypedArgument>()
        }
    }

    @Throws(CommandSyntaxException::class)
    private fun parseMultiInternal(greedyString: String): LinkedList<String> {
        val reader = anyUnquotedStringReader(greedyString)
        val result = LinkedList<String>()
        var watchdogCursor = -1
        while (reader.canRead()) {
            if (reader.cursor == watchdogCursor) {
                Log.error("Dead loop encountered with {} at {}", greedyString, watchdogCursor)
                break
            } else {
                watchdogCursor = reader.cursor
            }
            reader.skipWhitespace()
            if (!reader.canRead()) {
                result.add("")
                break
            }
            result.add(reader.readString())
        }
        return result
    }

    private fun parseMultiInternalSuppress(greedyString: String): LinkedList<String> {
        val reader = anyUnquotedStringReader(greedyString)
        val result = LinkedList<String>()
        var watchdogCursor = -1
        while (reader.canRead()) {
            if (reader.cursor == watchdogCursor) {
                Log.error("Dead loop encountered with {} at {}", greedyString, watchdogCursor)
                break
            } else {
                watchdogCursor = reader.cursor
            }
            reader.skipWhitespace()
            if (!reader.canRead()) {
                result.add("")
                break
            }
            val remaining = reader.remaining
            try {
                result.add(reader.readString())
            } catch (e: CommandSyntaxException) {
                result.add(remaining)
                break
            }
        }
        return result
    }

    private fun splitLast(greedyString: String): Int {
        val reader = anyUnquotedStringReader(greedyString)
        var cursor = 0
        var watchdogCursor = -1
        while (reader.canRead()) {
            if (reader.cursor == watchdogCursor) {
                Log.error("Dead loop encountered with {} at {}", greedyString, watchdogCursor)
                break
            } else {
                watchdogCursor = reader.cursor
            }
            cursor = reader.cursor + 1
            reader.skipWhitespace()
            if (!reader.canRead()) {
                break
            }
            cursor = reader.cursor
            try {
                reader.readString()
            } catch (e: CommandSyntaxException) {
                break
            }
        }
        return cursor
    }

    @Throws(CommandSyntaxException::class)
    fun <S> fetch(
        context: CommandContext<S>?, builder: SuggestionsBuilder,
        provider: SuggestionProvider<S>,
    ): List<Suggestion> {
        val emptyBuilder = SuggestionsBuilder(builder.input, builder.start)
        return try {
            provider.getSuggestions(context, emptyBuilder).get().list
        } catch (e: InterruptedException) {
            emptyList()
        } catch (e: ExecutionException) {
            emptyList()
        }
    }

    private fun uniqueMulti(greedyString: String, suggestions: List<Suggestion>): List<String> {
        var arguments = try {
            parseMultiInternal(greedyString)
        } catch (e: CommandSyntaxException) {
            parseMultiInternalSuppress(greedyString)
        }
        if (arguments.size == 0) {
            return suggestions.stream()
                .map { s: Suggestion -> s.text }
                .toList()
        }
        arguments.removeLast()
        val makeCompilerHappy = arguments
        val previousArguments = greedyString.substring(0, splitLast(greedyString))
        return suggestions.stream()
            .map { s: Suggestion -> s.text }
            .filter { s: String -> !makeCompilerHappy.contains(s) }
            .map { s: String -> previousArguments + s }
            .toList()
    }

    @JvmStatic
    fun <S> uniqueMulti(provider: SuggestionProvider<S>): SuggestionProvider<S> {
        return SuggestionProvider { context: CommandContext<S>?, builder: SuggestionsBuilder ->
            uniqueMulti(builder.remaining, fetch(context, builder, provider)).forEach(
                Consumer { s: String? ->
                    builder.suggest(s)
                },
            )
            builder.buildFuture()
        }
    }

    @JvmStatic
    fun <S> repeatableMulti(provider: SuggestionProvider<S>): SuggestionProvider<S> {
        return SuggestionProvider { context: CommandContext<S>?, builder: SuggestionsBuilder ->
            val remaining = builder.remaining
            val previousArguments = remaining.substring(0, splitLast(remaining))
            fetch(context, builder, provider).forEach(
                Consumer { s: Suggestion ->
                    builder.suggest(previousArguments + s.text)
                },
            )
            builder.buildFuture()
        }
    }

    @JvmStatic
    @SafeVarargs
    fun <S> merge(provider: SuggestionProvider<S>, vararg providers: SuggestionProvider<S>): SuggestionProvider<S> {
        return SuggestionProvider { context: CommandContext<S>?, builder: SuggestionsBuilder ->
            fetch(context, builder, provider).forEach(
                Consumer { s: Suggestion ->
                    builder.suggest(s.text)
                },
            )
            for (p in providers) {
                fetch(context, builder, p).forEach(
                    Consumer { s: Suggestion ->
                        builder.suggest(s.text)
                    },
                )
            }
            builder.buildFuture()
        }
    }

    @JvmStatic
    fun <S> map(provider: SuggestionProvider<S>, callable: Function<String?, String?>): SuggestionProvider<S> {
        return SuggestionProvider { context: CommandContext<S>?, builder: SuggestionsBuilder ->
            fetch(context, builder, provider).forEach(
                Consumer { s: Suggestion ->
                    builder.suggest(callable.apply(s.text))
                },
            )
            builder.buildFuture()
        }
    }
}
