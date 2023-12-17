import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.myapplication10.R

class MyService : Service() {

    private val channelId = "my_channel_id"
    private val notificationId = 1
    private lateinit var notificationManager: NotificationManager
    private lateinit var handler: Handler

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        handler = Handler(Looper.getMainLooper())
        createNotificationChannel()
        startForeground(notificationId, createNotification())
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        showToast("Service started")
        handler.postDelayed(::sendNotification, 10 * 60 * 1000) // 10 minutes
        return START_STICKY
    }

    private fun showToast(message: String) {
        handler.post {
            Toast.makeText(this@MyService, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendNotification() {
        val notification = createNotification()
        notificationManager.notify(notificationId, notification)
        handler.postDelayed(::sendNotification, 10 * 60 * 1000)
    }

    private fun createNotification(): Notification {
        val intent = Intent(this, MyService::class.java)
        val pendingIntent =
            PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val disableIntent = Intent(this, MyService::class.java)
        disableIntent.action = "disable"
        val disablePendingIntent =
            PendingIntent.getService(this, 0, disableIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("My Service")
            .setContentText("Notification every 10 minutes")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true) // Dismiss the notification when the user clicks on it
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
    }


    private fun createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "My Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
