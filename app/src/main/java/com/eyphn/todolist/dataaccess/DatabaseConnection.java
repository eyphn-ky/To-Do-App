package com.eyphn.todolist.dataaccess;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;
import com.eyphn.todolist.entities.Category;
import com.eyphn.todolist.entities.ToDo;
import java.util.ArrayList;
import java.util.List;

public class DatabaseConnection extends SQLiteOpenHelper {
    private static final String database_name="todoapp_db";
    private static final String task_category_table="task_categories";
    private static final String to_do_table="to_dos";
    private static final int database_version=1;

    public DatabaseConnection(@Nullable Context context) {
        super(context,database_name,null,database_version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTaskCategoryTable="CREATE TABLE "+task_category_table+"(Id integer PRIMARY KEY AUTOINCREMENT, Name TEXT)";
        String createToDosTable="CREATE TABLE "+to_do_table+"(Id INTEGER PRIMARY KEY AUTOINCREMENT,Detail TEXT,DueDate TEXT,DueTime TEXT,CategoryId INTEGER,IsNotified INTEGER)";
        sqLiteDatabase.execSQL(createTaskCategoryTable);
        sqLiteDatabase.execSQL(createToDosTable);
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
     sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+task_category_table);
     sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+to_do_table);
    }
    /*ToDo sınıfı için metodlar*/
    public long AddToDo(ToDo toDo) {
        try {
            SQLiteDatabase db=this.getWritableDatabase();
            ContentValues cv= new ContentValues();
            cv.put("Detail",toDo.getDetail());
            cv.put("DueDate", toDo.getDueDate());
            cv.put("DueTime",toDo.getDueTime());
            cv.put("CategoryId",toDo.getCategoryId());
            cv.put("IsNotified", toDo.getIsNotified());
            long id = db.insert(to_do_table,null,cv);
            return id;
        }
        catch (Exception ex){
            throw ex;
        }
    }
    public int DeleteToDo(String Id){
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            return db.delete(to_do_table,"Id = ?", new String[]{Id});
        }
        catch(Exception ex){
            throw ex;
        }
    }
    public List<ToDo> GetAllToDo() {
        List<ToDo> toDos = new ArrayList<ToDo>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query="Select * from "+to_do_table;
        Cursor cursor = db.rawQuery(query,null);
        int Id = cursor.getColumnIndex("Id");
        int Detail = cursor.getColumnIndex("Detail");
        int DueDate = cursor.getColumnIndex("DueDate");
        int DueTime = cursor.getColumnIndex("DueTime");
        int CategoryId = cursor.getColumnIndex("CategoryId");
        int IsNotified = cursor.getColumnIndex("IsNotified");
        try
        {
            while (cursor.moveToNext()){
                ToDo _todo = new ToDo();
                _todo.setId(cursor.getInt(Id));
                _todo.setDetail(cursor.getString(Detail));
                _todo.setDueDate(cursor.getString(DueDate));
                _todo.setDueTime(cursor.getString(DueTime));
                _todo.setCategoryId(cursor.getInt(CategoryId));
                _todo.setIsNotified(cursor.getInt(IsNotified));
                toDos.add(_todo);
            }
        }
        finally {
            cursor.close();
            db.close();
        }
        return toDos;
    }
    public List<ToDo> GetToDosByCategoryId(int id) {
        List<ToDo> toDos = new ArrayList<ToDo>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query="Select * from "+to_do_table+" Where CategoryId ="+id;
        Cursor cursor = db.rawQuery(query,null);
        int Id = cursor.getColumnIndex("Id");
        int Detail = cursor.getColumnIndex("Detail");
        int DueDate = cursor.getColumnIndex("DueDate");
        int DueTime = cursor.getColumnIndex("DueTime");
        int CategoryId = cursor.getColumnIndex("CategoryId");
        int IsNotified = cursor.getColumnIndex("IsNotified");
        try
        {
            while (cursor.moveToNext()){
                ToDo _todo = new ToDo();
                _todo.setId(cursor.getInt(Id));
                _todo.setDetail(cursor.getString(Detail));
                _todo.setDueDate(cursor.getString(DueDate));
                _todo.setDueTime(cursor.getString(DueTime));
                _todo.setCategoryId(cursor.getInt(CategoryId));
                _todo.setIsNotified(cursor.getInt(IsNotified));
                toDos.add(_todo);
            }
        }
        finally {
            cursor.close();
            db.close();
        }
        return toDos;
    }
    public void UpdateToDo(ToDo toDo){
        SQLiteDatabase db = this.getWritableDatabase();
        String Id = String.valueOf(toDo.getId());
        ContentValues cv = new ContentValues();
        cv.put("Id", toDo.getId());
        cv.put("Detail",toDo.getDetail());
        cv.put("DueDate",toDo.getDueDate());
        cv.put("DueTime",toDo.getDueTime());
        cv.put("CategoryId",toDo.getCategoryId());
        cv.put("IsNotified",toDo.getIsNotified());
        db.update(to_do_table, cv, " Id=?", new String[]{Id});
    }
    /*Category sınıfı için metodlar*/
    public long AddCategory(Category category){
        try {
            SQLiteDatabase db=this.getWritableDatabase();
            ContentValues cv= new ContentValues();
            cv.put("Name",category.getName());
            long id = db.insert(task_category_table,null,cv);
            return id;
        }
        catch (Exception ex)
        {
            throw ex;
        }
    }
    public void UpdateCategory(Category category){

        SQLiteDatabase db = this.getWritableDatabase();
        String Id = String.valueOf(category.getId());
        ContentValues cv = new ContentValues();
        cv.put("Name", category.getName());
        cv.put("Id",category.getId());
        db.update(task_category_table, cv, " Id=?", new String[]{Id});
    }
    public int DeleteCategoryById(String Id){
        try{
            SQLiteDatabase db = this.getWritableDatabase();
            return db.delete(task_category_table,"Id = ?",new String[]{Id});
        }
        catch (Exception ex){
            throw ex;
        }
    }
    public List<Category> GetAllCategory(){
        List<Category> categories = new ArrayList<Category>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query="Select * from "+task_category_table;
        Cursor cursor = db.rawQuery(query,null);
        int Id = cursor.getColumnIndex("Id");
        int Name = cursor.getColumnIndex("Name");
        try
        {
            while (cursor.moveToNext()){
                Category _category = new Category();
                _category.setId(cursor.getInt(Id));
                _category.setName(cursor.getString(Name));
                categories.add(_category);
            }
        }
        finally {
            cursor.close();
            db.close();
        }
        return categories;
    }

}
