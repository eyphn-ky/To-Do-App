package com.eyphn.todolist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.eyphn.todolist.dataaccess.DatabaseConnection;
import com.eyphn.todolist.entities.Category;
import com.eyphn.todolist.services.NotificationService;
import java.util.ArrayList;
import java.util.List;
public class TaskListActivity extends AppCompatActivity {

    LinearLayout taskCategoryList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Görev Kategorilerini Düzenle");
        taskCategoryList = (LinearLayout) findViewById(R.id.taskCategoryList);
        GetAllTaskCategoryWithTheirToDo();
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent mainActivity = new Intent(TaskListActivity.this, MainActivity.class);
        startActivity(mainActivity);
        finish();
        return true;
    }
    public void onBackPressed(){
        Intent mainActivity = new Intent(TaskListActivity.this, MainActivity.class);
        startActivity(mainActivity);
        finish();
    }
    public void GetAllTaskCategoryWithTheirToDo() {
        taskCategoryList.removeAllViews();
        DatabaseConnection db = new DatabaseConnection(getApplicationContext());
        List<Category> categoryList = new ArrayList<Category>();
        categoryList = db.GetAllCategory();
        if (categoryList.size() > 0) {
            for (Category _category : categoryList) {

                final LinearLayout mainContainerLinearLayout = new LinearLayout(this);
                final LinearLayout textContainerLinearLayout = new LinearLayout(this);
                final LinearLayout iconContainerLinearLayout = new LinearLayout(this);
                final TextView taskNameTextView = new TextView(this);
                final TextView todoCountTextView = new TextView(this);
                final TextView deleteTextView = new TextView(this);
                final TextView editTextView = new TextView(this);

                LinearLayout.LayoutParams paramsForMainContainerLL = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                paramsForMainContainerLL.setMargins(0, 0, 0, 15);
                mainContainerLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
                mainContainerLinearLayout.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.category));
                mainContainerLinearLayout.setId(_category.getId());
                mainContainerLinearLayout.setLayoutParams(paramsForMainContainerLL);

                LinearLayout.LayoutParams paramsForTextContainerLL = new LinearLayout.LayoutParams(0, 150, 0.75f);
                textContainerLinearLayout.setLayoutParams(paramsForTextContainerLL);
                textContainerLinearLayout.setOrientation(LinearLayout.VERTICAL);

                LinearLayout.LayoutParams paramsForTaskName = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 90);
                taskNameTextView.setPadding(30, 9, 30, 0);
                taskNameTextView.setTextSize(18);
                taskNameTextView.setTextColor(Color.WHITE);
                taskNameTextView.setText(_category.getName());
                taskNameTextView.setLayoutParams(paramsForTaskName);


                LinearLayout.LayoutParams paramsForToDoCount = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 60);
                todoCountTextView.setPadding(30, 0, 30, 9);
                todoCountTextView.setTextSize(18);
                todoCountTextView.setTextColor(Color.WHITE);
                if(_category.getId()==1)
                {
                    todoCountTextView.setText(String.valueOf(db.GetAllToDo().size()));
                }
                else{
                    todoCountTextView.setText(String.valueOf(db.GetToDosByCategoryId(_category.getId()).size()));
                }
                todoCountTextView.setLayoutParams(paramsForToDoCount);


                LinearLayout.LayoutParams paramsForIconContainerLL = new LinearLayout.LayoutParams(0, 150, 0.25f);
                iconContainerLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
                iconContainerLinearLayout.setLayoutParams(paramsForIconContainerLL);

                //İlgili kategoriyi kullanan veriler görevler varsa kategori silinemez

                if (db.GetToDosByCategoryId(_category.getId()).size()==0 && _category.getId()!=1) {
                    LinearLayout.LayoutParams paramsForEdit = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 0.5f);
                    Drawable editImage = getApplicationContext().getResources().getDrawable(R.drawable.ic_baseline_edit_24);
                    editTextView.setCompoundDrawablesWithIntrinsicBounds(editImage, null, null, null);
                    editTextView.setId(_category.getId());
                    editTextView.setLayoutParams(paramsForEdit);
                    LinearLayout.LayoutParams paramsForDelete = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 0.5f);
                    Drawable deleteImage = getApplicationContext().getResources().getDrawable(R.drawable.ic_baseline_delete_24);
                    deleteTextView.setCompoundDrawablesWithIntrinsicBounds(deleteImage, null, null, null);
                    deleteTextView.setLayoutParams(paramsForDelete);
                    deleteTextView.setId(_category.getId());
                    iconContainerLinearLayout.addView(editTextView);
                    iconContainerLinearLayout.addView(deleteTextView);

                    deleteTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (db.GetToDosByCategoryId(deleteTextView.getId()).size() == 0 && db.GetAllCategory().size()>1) {
                                db.DeleteCategoryById(String.valueOf(deleteTextView.getId()));
                                taskCategoryList.removeView(mainContainerLinearLayout);
                            }
                        }
                    });
                    editTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ShowUpdatePopup(taskNameTextView.getText().toString(),editTextView.getId());
                        }
                    });
                }

                textContainerLinearLayout.addView(taskNameTextView);
                textContainerLinearLayout.addView(todoCountTextView);

                mainContainerLinearLayout.addView(textContainerLinearLayout);
                mainContainerLinearLayout.addView(iconContainerLinearLayout);

                taskCategoryList.addView(mainContainerLinearLayout);
            }
        }
        else{
            EmptyCategoryList();
        }
        CategoryAddButton();
    }
    @Override
    protected void onStop() {
        super.onStop();
        startService(new Intent(this, NotificationService.class));
    }
    public void ShowAddPopup(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Kategori ekle");
        alert.setMessage("Kategori Adı :");
        final EditText input = new EditText(this);
        alert.setView(input);
        alert.setPositiveButton("Ekle", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (input.getText().toString().trim().matches("")) {
                    Toast.makeText(getApplicationContext(), R.string.nothing_to_save, Toast.LENGTH_LONG).show();
                } else {
                    DatabaseConnection db = new DatabaseConnection(getApplicationContext());
                    Category category = new Category();
                    category.setName(input.getText().toString());
                    db.AddCategory(category);
                    db.close();
                    GetAllTaskCategoryWithTheirToDo();
                }
            }
        });

        alert.setNegativeButton("İptal Et", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        alert.show();
    }
    public void ShowUpdatePopup(String Name,int Id){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Kategori güncelle");
        alert.setMessage("Yeni Kategori Adı :");
        final EditText input = new EditText(this);
        input.setText(Name);
        input.setSelection(input.getText().length());
        alert.setView(input);
        alert.setPositiveButton("Güncelle", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (input.getText().toString().trim().matches("")) {
                    Toast.makeText(getApplicationContext(), R.string.nothing_to_save, Toast.LENGTH_LONG).show();
                } else {
                    DatabaseConnection db = new DatabaseConnection(getApplicationContext());
                    Category category = new Category();
                    category.setName(input.getText().toString());
                    category.setId(Id);
                    db.UpdateCategory(category);
                    db.close();
                    GetAllTaskCategoryWithTheirToDo();
                }
            }
        });

        alert.setNegativeButton("İptal Et", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        alert.show();
    }
    public void CategoryAddButton(){
        Button addCategoryButton = new Button(this);
        LinearLayout.LayoutParams addButtonParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,150);
        addCategoryButton.setLayoutParams(addButtonParams);
        addCategoryButton.setText("Yeni bir kategori ekle");
        addCategoryButton.setGravity(Gravity.CENTER_VERTICAL);
        addCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowAddPopup();
            }
        });
        taskCategoryList.addView(addCategoryButton);
    }
    public void EmptyCategoryList(){
        TextView empty = new TextView(this);
        empty.setText("Henüz bir kategori eklemediniz!");
        empty.setTextSize(16);
        empty.setGravity(Gravity.CENTER_HORIZONTAL);
        taskCategoryList.addView(empty);
    }
}