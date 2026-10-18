package `in`.vinit.storyghost.core.common

import timber.log.Timber

/**
 * Common logging abstraction decoupled from Android runtime and third-party frameworks.
 *
 * Allows mocking and verifying log statements in JVM unit tests without depending on [android.util.Log].
 */
interface Logger {
    /** Logs a debug message with the specified [tag]. */
    fun d(tag: String, message: String)

    /** Logs a warning message with the specified [tag]. */
    fun w(tag: String, message: String)

    /** Logs an error message and optional [throwable] cause with the specified [tag]. */
    fun e(tag: String, message: String, throwable: Throwable? = null)
}

/**
 * Concrete [Logger] implementation delegating to [Timber].
 */
class TimberLoggerImpl : Logger {
    override fun d(tag: String, message: String) {
        Timber.tag(tag).d(message)
    }

    override fun w(tag: String, message: String) {
        Timber.tag(tag).w(message)
    }

    override fun e(tag: String, message: String, throwable: Throwable?) {
        Timber.tag(tag).e(throwable, message)
    }
}
