package com.eyphn.todolist;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityManager;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;
import com.eyphn.todolist.dataaccess.DatabaseConnection;
import com.eyphn.todolist.entities.Category;
import com.eyphn.todolist.entities.ToDo;
import com.eyphn.todolist.services.NotificationService;
import com.eyphn.todolist.spinner.SpinnerCategory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddToDoDetailActivity extends AppCompatActivity {
    EditText jobToDoEditText;
    EditText dueDateEditText;
    EditText dueTimeEditText;
    Button addToDoWithDetailButton;
    Spinner categorySelectOption;
    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;
    int CategoryId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_do_detail);
        setTitle("Detaylı Görev Planlama");
        jobToDoEditText = (EditText) findViewById(R.id.jobToDoEditText);
        dueDateEditText = (EditText) findViewById(R.id.dueDateTextView);
        dueTimeEditText = (EditText) findViewById(R.id.dueTimeTextView);
        addToDoWithDetailButton = (Button) findViewById(R.id.addToDoWithDetailButton);
        categorySelectOption = (Spinner)findViewById(R.id.categorySelectOption);
        CreateCategorySelectOption();
        Calendar calendar = Calendar.getInstance();
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int hour = calendar.get(Calendar.HOUR);
        final int minute = calendar.get(Calendar.MINUTE);
        CreateDueTimePicker(hour,minute);
        CreateDueDatePicker(day,year,month);
        AddToDoWithDetail();
        Locale locale = new Locale("tr");
        Locale.setDefault(locale);
    }
    @Override
    protected void onStop() {
        super.onStop();
        startService(new Intent(this,NotificationService.class));
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent mainActivity = new Intent(AddToDoDetailActivity.this, MainActivity.class);
        startActivity(mainActivity);
        finish();
        return true;
    }
    public void onBackPressed(){
        Intent mainActivity = new Intent(AddToDoDetailActivity.this, MainActivity.class);
        startActivity(mainActivity);
        finish();
    }
    /*Category Seçme Select Option'unu Doldurur*/
    public void CreateCategorySelectOption(){
        if(!(categorySelectOption.getChildCount()>0)) {
            DatabaseConnection db = new DatabaseConnection(getApplicationContext());
            List<Category> categories = new ArrayList();
            categories = db.GetAllCategory();
            ArrayList<SpinnerCategory> categoryList = new ArrayList<SpinnerCategory>();
            int i = 0;
            for (Category _category : categories) {
                categoryList.add(new SpinnerCategory(String.valueOf(_category.getId()),_category.getName()));
                i=i+1;
            }
            db.close();
            ArrayAdapter<SpinnerCategory> categoriesAdapter = new ArrayAdapter<SpinnerCategory>(this, android.R.layout.simple_spinner_item, categoryList);
            categoriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            categorySelectOption.setAdapter(categoriesAdapter);
            categorySelectOption.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
                    SpinnerCategory spn = (SpinnerCategory) parent.getItemAtPosition(i);
                    CategoryId = Integer.parseInt(spn.Id);
                }
                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }
    }
    /*View'da edittexte tıklanıldığında date picker oluşturur*/
    public void CreateDueTimePicker(int hour,int minute){
        dueTimeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePickerDialog = new TimePickerDialog(AddToDoDetailActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int i, int i1) {
                        String hourString = String.valueOf(i);
                        String minuteString = String.valueOf(i1);
                        if(i<10)
                        {
                            hourString ="0"+i;
                        }
                        if(i1<10)
                        {
                            minuteString="0"+i1;
                        }
                        dueTimeEditText.setText(hourString+":"+minuteString);
                    }
                },hour,minute,true);
                timePickerDialog.show();
            }
        });
    }
    /*View'da edittexte tıklanıldığında time picker oluşturur*/
    public void CreateDueDatePicker(int day,int year,int month){
        dueDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog = new DatePickerDialog(AddToDoDetailActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        String dayOfMonthString=String.valueOf(dayOfMonth);
                        String monthString = String.valueOf(month+1);
                        if(dayOfMonth<10)
                        {
                            dayOfMonthString ="0"+dayOfMonth;
                        }
                        if(month<10)
                        {
                            monthString ="0"+(month+1);
                        }
                        String date=dayOfMonthString+"/"+monthString+"/"+year;
                        dueDateEditText.setText(date);
                    }
                },year,month,day);
                datePickerDialog.show();
            }
        });
    }
    /*Butona tıklandığında veritabanına kayıt*/
    public void AddToDoWithDetail(){
        addToDoWithDetailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(TextUtils.isEmpty(jobToDoEditText.getText())) {
                    Toast.makeText(getApplicationContext(),"Yapılacak işi girmediniz!",Toast.LENGTH_LONG).show();
                }
                else {
                    DatabaseConnection db = new DatabaseConnection(getApplicationContext());
                    ToDo toDo = new ToDo();
                    toDo.setDetail(jobToDoEditText.getText().toString());
                    toDo.setCategoryId(CategoryId);
                    toDo.setDueDate(dueDateEditText.getText().toString());
                    toDo.setDueTime(dueTimeEditText.getText().toString());
                    toDo.setIsNotified(0);
                    db.AddToDo(toDo);
                    if (!IsNotificationServiceWork())
                    {
                        startService(new Intent(getApplicationContext(), NotificationService.class));
                    }
                    Intent mainActivity = new Intent(AddToDoDetailActivity.this, MainActivity.class);
                    startActivity(mainActivity);
                    finish();
                }
            }
        });
    }
    public boolean IsNotificationServiceWork(){
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if(getApplicationContext().getPackageName().equals(service.service.getPackageName()))
            {
                return  true;
            }
        }
        return false;
    }
}
