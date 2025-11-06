package jin.project.dailyq

import android.content.Context
import android.content.SharedPreferences
import java.util.Calendar

object PreferenceManager {
    private const val PREFS_NAME = "dailyq_prefs"
    private const val KEY_LAST_VISIT_DATE = "last_visit_date"
    private const val KEY_HAS_VISITED_TODAY = "has_visited_today"

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun markVisitToday(context: Context) {
        val prefs = getSharedPreferences(context)
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        prefs.edit().apply {
            putLong(KEY_LAST_VISIT_DATE, today)
            putBoolean(KEY_HAS_VISITED_TODAY, true)
            apply()
        }
    }

    fun hasVisitedToday(context: Context): Boolean {
        val prefs = getSharedPreferences(context)
        val lastVisitDate = prefs.getLong(KEY_LAST_VISIT_DATE, 0)
        val hasVisited = prefs.getBoolean(KEY_HAS_VISITED_TODAY, false)
        
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        // 날짜가 바뀌었으면 초기화
        if (lastVisitDate != today) {
            prefs.edit().putBoolean(KEY_HAS_VISITED_TODAY, false).apply()
            return false
        }

        return hasVisited
    }

    fun resetDailyFlag(context: Context) {
        val prefs = getSharedPreferences(context)
        prefs.edit().putBoolean(KEY_HAS_VISITED_TODAY, false).apply()
    }
}

