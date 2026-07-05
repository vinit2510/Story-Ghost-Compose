package `in`.vinit.storyghost.core.common

import java.text.SimpleDateFormat
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

object TimeUtils {

    fun getStoryTimestamp(time: Long): String {
        try {
            val eventTime = Instant.ofEpochSecond(time)
            val currentTime = Instant.now()
            val duration = Duration.between(eventTime, currentTime)
            val minutesAgo = duration.toMinutes()
            val hoursAgo = duration.toHours()

            return when {
                minutesAgo < 60 -> "$minutesAgo minute ago"
                hoursAgo < 24 -> "$hoursAgo hour ago"
                else -> {
                    val formatter = DateTimeFormatter.ofPattern("dd MMM yy")
                        .withZone(ZoneId.systemDefault())
                    formatter.format(eventTime)
                }
            }
        } catch (e: Exception) {
            return ""
        }
    }

    fun getFileTimestamp(time: Long): String {
        try {
            val eventTime = Instant.ofEpochMilli(time)
            val currentTime = Instant.now()
            val duration = Duration.between(eventTime, currentTime)
            val minutesAgo = duration.toMinutes()
            val hoursAgo = duration.toHours()

            return when {
                minutesAgo < 60 -> "$minutesAgo minute ago"
                hoursAgo < 24 -> "$hoursAgo hour ago"
                else -> {
                    val dateFormat = SimpleDateFormat("dd MMM yy", Locale.getDefault())
                    dateFormat.format(Date(time))
                }
            }
        } catch (_: Exception) {
            return ""
        }
    }
}
