import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    private val utcFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    private val desiredFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    fun formatUtcToDesired(time: String): String {
        val date = utcFormat.parse(time)
        return desiredFormat.format(date)
    }
}
