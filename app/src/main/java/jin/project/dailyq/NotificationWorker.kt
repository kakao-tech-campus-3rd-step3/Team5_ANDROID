package jin.project.dailyq

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.util.Calendar

class NotificationWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        
        // 정확한 시간에만 알림 발송 (9시, 12시, 15시, 18시, 21시)
        val targetHours = listOf(9, 12, 15, 18, 21)
        
        if (hour in targetHours && minute < 15) { // 15분 이내에만 실행
            val hasVisited = PreferenceManager.hasVisitedToday(applicationContext)
            
            // 9시는 항상 알림, 나머지는 접속하지 않았을 때만
            if (hour == 9 || !hasVisited) {
                val title = "DailyQ"
                val message = when (hour) {
                    9 -> "일어나셨어요?"
                    12 -> "오늘 접속하셨나요?"
                    15 -> "언제 와요?"
                    18 -> "아직도 안 와요?"
                    21 -> "이건 좀..........................................................................."
                    else -> "DailyQ를 확인해보세요!"
                }
                
                NotificationHelper.showNotification(applicationContext, title, message)
            }
        }
        
        return Result.success()
    }
}

