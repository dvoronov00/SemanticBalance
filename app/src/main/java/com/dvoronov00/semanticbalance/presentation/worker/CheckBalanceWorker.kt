package com.dvoronov00.semanticbalance.presentation.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.WorkerParameters
import androidx.work.rxjava3.RxWorker
import com.dvoronov00.semanticbalance.R
import com.dvoronov00.semanticbalance.data.extension.calculateExistingDays
import com.dvoronov00.semanticbalance.domain.usecase.GetAccountDataUseCase
import com.dvoronov00.semanticbalance.presentation.App
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.net.UnknownHostException
import javax.inject.Inject

class CheckBalanceWorker(context: Context, parameterName: WorkerParameters) :
    RxWorker(context, parameterName) {

    companion object {
        private const val CHANNEL_ID = "22"
    }

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    @Inject
    lateinit var getAccountDataUseCase: GetAccountDataUseCase

    private fun createNotification(existingDays: Int) {
        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationCompat.Builder(applicationContext, CHANNEL_ID)
        } else {
            NotificationCompat.Builder(applicationContext)
        }

        var title = ""
        var description = ""
        if (existingDays == 0) {
            title = "Баланс на нуле"
            description =
                "Баланса не хватит, чтобы оплатить завтрашний день"
        } else if (existingDays == 1) {
            title = "Баланс на исходе"
            description = "Баланса хватит еще на один день"
        }

        builder.setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle(title)
            .setContentText(description)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        notificationManager.notify(22, builder.build())
    }

    private fun areNotificationEnabled(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if(!notificationManager.areNotificationsEnabled()){
                return false
            }
            createNotificationChannel()

            val channels = notificationManager.notificationChannels
            channels.firstOrNull { it.id == CHANNEL_ID }?.let {
                return true
            }
            return false
        }else{
            return NotificationManagerCompat.from(applicationContext).areNotificationsEnabled()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = applicationContext.getString(R.string.balance)
            val descriptionText = applicationContext.getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                shouldVibrate()
            }
            notificationManager.createNotificationChannel(channel)
        }

    }

    override fun createWork(): Single<Result> {
        App.getComponent().inject(this)

        return Single.create {single ->
            if (areNotificationEnabled()) {
                getAccountDataUseCase.getAccountData()
                    .subscribeOn(Schedulers.io())
                    .subscribe ({ account ->
                        val existingDays = account.calculateExistingDays()
                        if (existingDays == 0 || existingDays == 1) {
                            createNotification(existingDays)
                            single.onSuccess(Result.success())
                        }
                    }, {
                        val bundle = Bundle()
                        bundle.putString("error", it.message)
                        when(it){
                            is UnknownHostException -> {
                                single.onSuccess(Result.retry())
                            }
                            else -> {
                                single.onError(it)
                            }
                        }
                    })
            }else{
                val errorMessage = "Notification services is off"
                single.onError(Exception(errorMessage))
            }
        }

    }
}
