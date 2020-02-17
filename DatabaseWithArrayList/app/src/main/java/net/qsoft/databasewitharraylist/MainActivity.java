package net.qsoft.databasewitharraylist;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private DatabaseAdapter databaseAdapter;
    private ArrayList<Employee>employeeArrayList;
    private AlertDialog dialog;
    private List<Contact>contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseAdapter = new DatabaseAdapter(this);
        employeeArrayList = new ArrayList<>();
        contactList = new ArrayList<>();

        createContractArrayList();

        employeeArrayList.add(new Employee("Pabel", "Rana", 10000));
        employeeArrayList.add(new Employee("Monir", "Hossain", 20000));
        employeeArrayList.add(new Employee("Rakib", "Hasan", 30000));

        Gson gson = new Gson();
        String jsonEmployeeList = gson.toJson(employeeArrayList);

      //  databaseAdapter.insertData(jsonEmployeeList);

       // String allData = databaseAdapter.getAllEmployees();

        //Toast.makeText(this, allData, Toast.LENGTH_LONG).show();

        //createDialog(allData);


        for (int i = 0; i < 5; i++)
        {
            long id = databaseAdapter.insertSingleContact(new Contact(101,"Akash","01768924976"));
            if(id>0)
            {
                Toast.makeText(this, "Contact Successfully inserted", Toast.LENGTH_SHORT).show();
            }
        }

        String jsonContractList = gson.toJson(contactList);
        databaseAdapter.insertContactFromMain(jsonContractList);


    }

    private void createContractArrayList() {
        contactList.add(new Contact(101,"Pabel","01768924976"));
        contactList.add(new Contact(101,"Pabel","01768924976"));
        contactList.add(new Contact(101,"Pabel","01768924976"));
        contactList.add(new Contact(101,"Pabel","01768924976"));
        contactList.add(new Contact(101,"Pabel","01768924976"));

    }

    public void createDialog(String data)
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(data);
        builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
               dialog.dismiss();
            }
        });
        builder.create();
        dialog = builder.show();
    }
}
