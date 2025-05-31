package cn.afternode.commons.bukkit.kotlin.command

const val FLAG_RS_NONE = 0
const val FLAG_RS_KEY = 1
const val FLAG_RS_KEY_PREFIX = 2
const val FLAG_RS_CONTENT = 3

class ParsedArguments(vararg args: String) {
    val args: List<String>
    val flags: Map<String, String>

    init {
        val resolvedArgs = arrayListOf<String>()
        val resolvedFlags = hashMapOf<String, String>()

        var resolveLong = false
        var longChar = '\"'
        var resolveFlag = 0
        var ignoreNextFormat = false

        val buffer = StringBuilder()
        var flagKey: String? = null

        for (c in args.joinToString(" ")) {
            var endLong = false

            if (!ignoreNextFormat && c == '\\') {
                ignoreNextFormat = true
                continue
            }
            if (ignoreNextFormat) {
                buffer.append(c)
                ignoreNextFormat = false
                continue
            }

            if (resolveLong) {
                if (c == longChar) {    // end long text resolution
                    if (resolveFlag == FLAG_RS_CONTENT) {   // end flag content resolution
                        resolvedFlags[flagKey!!] = buffer.toString()
                        resolveFlag = FLAG_RS_NONE
                    } else {
                        resolvedArgs += buffer.toString()
                    }
                    buffer.clear()
                    resolveLong = false
                    endLong = true
                } else {
                    buffer.append(c)
                }
                continue
            } else if (c == '\"' || c == '\'') {    // start long text resolution
                resolveLong = true
                longChar = c
                continue
            }

            if (c == '-' && resolveFlag == FLAG_RS_NONE) {
                resolveFlag = FLAG_RS_KEY_PREFIX
            } else if (resolveFlag == FLAG_RS_KEY_PREFIX) {
                if (c != '-') {
                    resolveFlag = FLAG_RS_KEY
                    buffer.append(c)
                }
            } else if (resolveFlag == FLAG_RS_KEY) {
                if (c == '=') {
                    resolveFlag = FLAG_RS_CONTENT
                    flagKey = buffer.toString()
                    buffer.clear()
                } else
                    buffer.append(c)
            } else if (resolveFlag == FLAG_RS_CONTENT) {
                if (c == ' ') {
                    resolvedFlags[flagKey!!] = buffer.toString()
                    flagKey = null
                    buffer.clear()
                    resolveFlag = FLAG_RS_NONE
                } else {
                    buffer.append(c)
                }
            } else {    // resolve plain
                if (c == ' ') {
                    if (buffer.isNotEmpty()) {
                        resolvedArgs += buffer.toString()
                        buffer.clear()
                    }
                } else {
                    buffer.append(c)
                }
            }
        }

        when (resolveFlag) {
            FLAG_RS_CONTENT -> {
                resolvedFlags[flagKey!!] = buffer.toString()
            }
            FLAG_RS_KEY -> {    // incomplete flag
                resolvedFlags[buffer.toString()] = ""
            }
            else -> {
                resolvedArgs += buffer.toString()
            }
        }

        this.args = resolvedArgs.toList()
        this.flags = resolvedFlags.toMap()
    }

    operator fun contains(key: String): Boolean = key in flags

    fun size() = this.args.size + this.flags.size

    fun argsSize() = this.args.size

    fun flagsSize() = this.flags.size

    fun flagsKeys() = this.flags.keys

    private var idx = 0

    fun next(): String? {
        val get = this.args.getOrNull(idx)
        idx ++
        return get
    }
}