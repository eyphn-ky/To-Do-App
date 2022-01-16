package com.eyphn.todolist.services;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.eyphn.todolist.R;
import com.eyphn.todolist.dataaccess.DatabaseConnection;
import com.eyphn.todolist.entities.ToDo;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class NotificationService extends Service {


    Timer timer;
    TimerTask timerTask;
    String tag = "Zamanlayıcı";
    Random generator = new Random();
    int delayTime = 10000;
    Handler handler = new Handler(Looper.getMainLooper());
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {}
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        startTimer();
        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        stopTimerTask();
        super.onDestroy();
        timer.cancel();

        super.onCreate();
        timer = new Timer();
        generator = new Random();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                GetNearestTimeTodo();
            }
        },0,delayTime);

    }

    public void startTimer() {
        timer = new Timer();
        initializeTimerTask();
        timer.schedule(timerTask,0,delayTime); //
    }

    public void stopTimerTask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        GetNearestTimeTodo();
                    }
                });
            }
        };
    }

    public void GetNearestTimeTodo(){
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
            {
                NotificationChannel channel = new NotificationChannel("Notification","Zamanı Gelen Görev Uyarısı",NotificationManager.IMPORTANCE_DEFAULT);
                NotificationManager manager = getSystemService(NotificationManager.class);
                manager.createNotificationChannel(channel);
            }
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            SimpleDateFormat dayFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date date = new Date();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    DatabaseConnection db = new DatabaseConnection(getApplicationContext());
                    List<ToDo> list = db.GetAllToDo();
                    if(!list.isEmpty())
                    {
                        for (ToDo _todo : list) {
                            if (_todo.getDueDate().equals(dayFormat.format(date)) && _todo.getIsNotified()==0) {
                                if (_todo.getDueTime().equals(timeFormat.format(date))) {
                                    ToDo toDo = new ToDo();
                                    toDo.setDueDate(_todo.getDueDate());
                                    toDo.setDueTime(_todo.getDueTime());
                                    toDo.setIsNotified(1);
                                    toDo.setCategoryId(_todo.getCategoryId());
                                    toDo.setDetail(_todo.getDetail());
                                    toDo.setId(_todo.getId());
                                    NotificationCompat.Builder builder= new NotificationCompat.Builder(NotificationService.this,"Notification");
                                    builder.setContentTitle("Zamanı Gelen Görev Uyarısı");
                                    builder.setContentText(_todo.getDetail());
                                    builder.setSmallIcon(R.drawable.ic_baseline_notifications_24);
                                    builder.setAutoCancel(true);
                                    NotificationManagerCompat managerCompat = NotificationManagerCompat.from(NotificationService.this);
                                    managerCompat.notify(generator.nextInt(),builder.build());
                                    db.UpdateToDo(toDo);
                                    db.close();
                                }
                            }
                        }
                    }
                }
            });
        }
    }

