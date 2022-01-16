package com.eyphn.todolist;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.eyphn.todolist.dataaccess.DatabaseConnection;
import com.eyphn.todolist.entities.Category;
import com.eyphn.todolist.entities.ToDo;
import com.eyphn.todolist.spinner.SpinnerCategory;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    Button addButton;
    EditText addToDoText;
    LinearLayout toDoContainerLinearLayout;
    Spinner categorySelectOption;
    int CategoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);//When the keyboard is open layout is move up
        setContentView(R.layout.activity_main);
        setTitle("To Do");
        addButton = (Button) findViewById(R.id.addToDoButton);
        addToDoText = (EditText) findViewById(R.id.addToDoEditText);
        toDoContainerLinearLayout = (LinearLayout) findViewById(R.id.toDoContainerLinearLayout);
        categorySelectOption = (Spinner) findViewById(R.id.categorySelectOption);
        DatabaseConnection db = new DatabaseConnection(getApplicationContext());
        /*Program ilk yüklendiğinde bir kez çalışır*/
        if (db.GetAllCategory().size() == 0) {
            Category category = new Category();
            category.setName("Varsayılan (Tümü)");
            db.AddCategory(category);
        }
        GetCategorySpinner();
        GetToDos(CategoryId);

        addToDoText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN && i == KeyEvent.KEYCODE_ENTER) {
                    if (addToDoText.getText().toString().trim().matches("")) {
                        Toast.makeText(getApplicationContext(), R.string.nothing_to_save, Toast.LENGTH_LONG).show();
                        return false;
                    } else {
                        ToDo toDo = new ToDo(addToDoText.getText().toString().trim(), "", CategoryId, "",0);
                        long id = db.AddToDo(toDo);
                        if (id > 0) {
                            GetToDos(CategoryId);
                            Toast.makeText(getApplicationContext(), R.string.success_save, Toast.LENGTH_LONG).show();
                            addToDoText.setText("");
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.error_save, Toast.LENGTH_LONG).show();
                        }
                        return true;
                    }
                }
                return false;
            }
        });
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addToDoDetailActivity = new Intent(MainActivity.this, AddToDoDetailActivity.class);
                startActivity(addToDoDetailActivity);
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.task_category) {
            Intent getTaskListActivity = new Intent(MainActivity.this, TaskListActivity.class);
            startActivity(getTaskListActivity);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void GetToDos(int CategoryId) {
        toDoContainerLinearLayout.removeAllViews();
        DatabaseConnection db = new DatabaseConnection(getApplicationContext());
        List<ToDo> toDoList;
        if (CategoryId == 1) {
            toDoList = db.GetAllToDo();
        } else {
            toDoList = db.GetToDosByCategoryId(CategoryId);
        }
        for (ToDo _toDo : toDoList) {
            final LinearLayout layout = new LinearLayout(this);
            final TextView todo = new TextView(this);
            final CheckBox todoCheck = new CheckBox(this);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f);
            params.setMargins(10, 20, 10, 0);
            layout.setId(_toDo.getId());
            layout.setLayoutParams(params);
            layout.setOrientation(LinearLayout.HORIZONTAL);
            layout.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.todocard));
            layout.setPadding(20, 10, 0, 10);
            LinearLayout.LayoutParams paramsForCheckBox = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            todoCheck.setClickable(true);
            todoCheck.setId(_toDo.getId());
            todoCheck.setLayoutParams(paramsForCheckBox);
            if (!TextUtils.isEmpty(_toDo.getDueDate()) || !TextUtils.isEmpty(_toDo.getDueTime())) {
                todo.setText(_toDo.getDetail() + " | " + _toDo.getDueDate() + " | " + _toDo.getDueTime());
            } else {
                todo.setText(_toDo.getDetail());
            }
            todo.setId(_toDo.getId());
            todo.setTextSize(18);
            todoCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    todoCheck.setClickable(false);
                    DatabaseConnection db = new DatabaseConnection(getApplicationContext());
                    db.DeleteToDo(String.valueOf(_toDo.getId()));
                    Animation animFadeOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_out);
                    layout.startAnimation(animFadeOut);
                    db.close();
                    final Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            toDoContainerLinearLayout.removeView(layout);
                        }
                    }, 2000);
                }
            });
            layout.setGravity(Gravity.CENTER_VERTICAL);
            layout.addView(todoCheck);
            layout.addView(todo);
            toDoContainerLinearLayout.addView(layout);
        }
    }

    public void GetCategorySpinner() {
        if (!(categorySelectOption.getChildCount() > 0)) {
            DatabaseConnection db = new DatabaseConnection(getApplicationContext());
            List<Category> categories;
            categories = db.GetAllCategory();
            ArrayList<SpinnerCategory> categoryList = new ArrayList<>();
            int i = 0;
            for (Category _category : categories) {
                categoryList.add(new SpinnerCategory(String.valueOf(_category.getId()), _category.getName()));
                i = i + 1;
            }
            db.close();
            ArrayAdapter<SpinnerCategory> categoriesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categoryList);
            categoriesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            categorySelectOption.setAdapter(categoriesAdapter);
            categorySelectOption.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
                    SpinnerCategory spn = (SpinnerCategory) parent.getItemAtPosition(i);
                    CategoryId = Integer.parseInt(spn.Id);
                    GetToDos(CategoryId);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });
        }
    }
}
