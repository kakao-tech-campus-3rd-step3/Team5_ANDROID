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
                    9 -> "오늘의 질문을 확인해보세요! 🌟"
                    12 -> "아직 확인하지 않으셨네요. 오늘의 질문을 놓치지 마세요!"
                    15 -> "오후 시간, DailyQ를 확인해보세요 📝"
                    18 -> "저녁 시간입니다. 오늘의 질문에 답해보세요!"
                    21 -> "하루를 마무리하며 DailyQ를 확인해보세요 ✨"
                    else -> "DailyQ를 확인해보세요!"
                }
                
                NotificationHelper.showNotification(applicationContext, title, message)
            }
        }
        
        return Result.success()
    }
}

