package net.qsoft.databasewitharraylist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DatabaseAdapter {
    MyDatabaseHelper helper;
    private Context context;

    public DatabaseAdapter(Context context) {
        helper = new MyDatabaseHelper(context);
        this.context = context;
        helper.getWritableDatabase();
    }

    public long insertSingleContact(Contact contact)
    {
        SQLiteDatabase database = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MyDatabaseHelper.CONTACT_COLUMN_ID,contact.getId());
        contentValues.put(MyDatabaseHelper.CONTACT_COLUMN_NAME,contact.getName());
        contentValues.put(MyDatabaseHelper.CONTACT_COLUMN_PHONE,contact.getPhoneNumber());

        long rowId = database.insert(MyDatabaseHelper.TABLE_NAME_CONTACT,null,contentValues);
        return rowId;
    }

    public long insertData(String jsonEmployee)
    {
        SQLiteDatabase database = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MyDatabaseHelper.EMPLOYEE_COLUMN_EMPLOYEES,jsonEmployee);
        long rowId = database.insert(MyDatabaseHelper.TABLE_NAME,null,contentValues);
        return rowId;

    }
    public String getAllEmployees()
    {
        SQLiteDatabase database = helper.getWritableDatabase();
        Cursor cursor = database.rawQuery(MyDatabaseHelper.SELECT_ALL_EMPLOYEES,null);
        int indexOne = cursor.getColumnIndex(MyDatabaseHelper.EMPLOYEE_COLUMN_EMPLOYEES);
        String data="";
        Gson gson = new Gson();
        while (cursor.moveToNext())
        {
             String text = cursor.getString(indexOne);
             if(text!=null)
             {
                 /*
                 String[] strings = text.split(",");
                 for (int i=0;i<strings.length;i++)
                 {
                     data = data + strings[i]+"\n";
                 }
                 data = data+"\n";
                 */
                 Type employeeType = new TypeToken<ArrayList<Employee>>(){}.getType();
                 ArrayList<Employee> employees = gson.fromJson(text,employeeType);
                 for(int i=0;i<employees.size();i++)
                 {
                     data = data + employees.get(i).getFirstName()+" "+
                             employees.get(i).getLastName()+"\n"+
                             employees.get(i).getSalary()+"\n\n";
                 }
             }
        }
        return  data;
    }
    private List<ContentValues> buildContentValuesList(String jsonStr) throws JSONException {
        if(TextUtils.isEmpty(jsonStr))
        {
            return null;
        }

        List<ContentValues> list = new ArrayList<ContentValues>();
        JSONArray jsonArray = new JSONArray(jsonStr);
        for(int i=0;i<jsonArray.length();i++)
        {
            JSONObject jb = jsonArray.optJSONObject(i);
            if(jb==null)
            {
                continue;
            }
            ContentValues contentValues = new ContentValues();
            contentValues.put(MyDatabaseHelper.CONTACT_COLUMN_ID,jb.optInt("id"));
            contentValues.put(MyDatabaseHelper.CONTACT_COLUMN_NAME,jb.optString("name"));
            contentValues.put(MyDatabaseHelper.CONTACT_COLUMN_PHONE,jb.optInt("phoneNumber"));

            list.add(contentValues);


        }
        return list;
    }

    public void insertContactFromMain(String jsonStr)
    {
        try {
            insertContact(buildContentValuesList(jsonStr));
            Toast.makeText(context, "Contact List Successfully Inserted", Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void insertContact(List<ContentValues> valueList) {
        if (valueList.size() > 0) {
            ContentValues[] valueArray = new ContentValues[valueList.size()];
            valueArray = valueList.toArray(valueArray);
           // context.getContentResolver().bulkInsert(android.net.Uri,valueArray);
            Uri uri = Uri.parse("contents://"+context.getPackageName()+"/databases/"+MyDatabaseHelper.TABLE_NAME_CONTACT);
            context.getContentResolver().bulkInsert(uri,valueArray);

        }


    }




    static class MyDatabaseHelper extends SQLiteOpenHelper
    {

        private static final String DATABASE_NAME = "Employee.db";
        private static final String TABLE_NAME = "Employee_Details";
        private static final int VERSION_NUMBER = 6;
        private static final String EMPLOYEE_COLUMN_ID = "id";
        private static final String EMPLOYEE_COLUMN_EMPLOYEES= "employees";
        private static final String TABLE_NAME_CONTACT ="Contact";
        private static final String CONTACT_COLUMN_ID = "id";
        private static final String CONTACT_COLUMN_NAME = "name";
        private static final String CONTACT_COLUMN_PHONE= "phoneNumber";
        private static final String CONTACT_COLUMN_SERIAL_ID= "serialId";



        private static final String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+"("+
                EMPLOYEE_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"+
                EMPLOYEE_COLUMN_EMPLOYEES+" TEXT"+");";

        private static final String CREATE_TABlE_CONTACT = "CREATE TABLE "+ TABLE_NAME_CONTACT+"("+
                CONTACT_COLUMN_SERIAL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"+
                CONTACT_COLUMN_ID + " INTEGER,"+
                CONTACT_COLUMN_NAME+" TEXT,"+
                CONTACT_COLUMN_PHONE+" TEXT"+
                ");";



        private static final String SELECT_ALL_EMPLOYEES = "SELECT * FROM "+TABLE_NAME;
        private static final String SELECT_ALL_CONTACTS = "SELECT * FROM "+TABLE_NAME_CONTACT;



        private Context context;

        public MyDatabaseHelper(@Nullable Context context) {
            super(context,DATABASE_NAME, null,VERSION_NUMBER);
            this.context = context;
            Toast.makeText(context, "Database Created", Toast.LENGTH_SHORT).show();
        }



        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(CREATE_TABLE);
            sqLiteDatabase.execSQL(CREATE_TABlE_CONTACT);
            Toast.makeText(context, "Table is created", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_CONTACT);

            onCreate(sqLiteDatabase);

        }

    }
}
