package com.infernodevelopers.sendsms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private static final int STORAGE_PERMISSION_CODE = 100;

    public static final int NAME_COL=0,SURNAME_COL=1,PHONE_COL=2,MESSAGE_COL=5;
    public static final String FILE_NAME = "FILE_NAME",DEFAULT_FILE_NAME = "SMS with Google Sheets.xlsx";

    ListView messages;
    TextView status;
    sheet_data_adapter adapter;
    EditText file_name;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // hide action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);
        checkPermission(Manifest.permission.SEND_SMS, STORAGE_PERMISSION_CODE);

        sharedPreferences = getSharedPreferences(TAG,MODE_PRIVATE);

        messages = findViewById(R.id.messages);
        status = findViewById(R.id.status);
        file_name = findViewById(R.id.excel_file_name);

        file_name.setText(sharedPreferences.getString(FILE_NAME,DEFAULT_FILE_NAME));
    }

    /* Permission Logic for App */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /* Check for given permission & ask for permission if not granted */
    public boolean checkPermission(String permission, int requestCode) {
        boolean permission_granted = ContextCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_DENIED;
        if (permission_granted) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[] { permission },
                    requestCode);
        }
        return !(permission_granted);
    }

    public void sendSMS(View view){
        String number="+919146230624";
        String msg="This is a test message from app!:)";

        int count=0;

        for(sheet_items_data data:sheet_data) {
            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(data.number, null, data.message, null, null);
                count++;
                status.setText("Messages Sent:"+count+"/"+sheet_data.size());
            } catch (Exception e) {
                Log.d(TAG, "sendSMS: " + e.getMessage());
                status.setText("Messages Sending Failed");
            }
        }

        if(count==sheet_data.size()){
            adapter.clear();
            view.setEnabled(false);
        }
    }

    public void fetchSMS(View view){
        File excel_file = new File(Environment.getExternalStorageDirectory(), file_name.getText().toString());
        if(excel_file.exists()) {
            readExcelFile(excel_file );
        } else {
            Toast.makeText(getApplicationContext(),"Excel file doesn't exist:"+excel_file.getAbsolutePath(),Toast.LENGTH_SHORT).show();
        }
    }

    List<sheet_items_data> sheet_data = new ArrayList<>();
    public void readExcelFile(File file) {
        try{
            // Create a workbook using the File System
            XSSFWorkbook wb = new XSSFWorkbook(file);

            // Get the first sheet from workbook
            XSSFSheet mySheet = wb.getSheetAt(0);

            /* We now need something to iterate through the cells.*/
            Iterator<Row> rowIter = mySheet.rowIterator();

            List<String> data = new ArrayList<>();

            while(rowIter.hasNext()){
                XSSFRow myRow = (XSSFRow) rowIter.next();
                Iterator<Cell> cellIter = myRow.cellIterator();
                while(cellIter.hasNext()){
                    XSSFCell myCell = (XSSFCell) cellIter.next();
                    data.add(myCell.toString());
                }
                sheet_data.add( new sheet_items_data( data.get(PHONE_COL),data.get(MESSAGE_COL),data.get(NAME_COL)+" "+data.get(SURNAME_COL) ) );
                data.clear();
            }
        }catch (Exception e){e.printStackTrace(); }

        if(sheet_data!=null){
            sheet_data.remove(0);
            adapter = new sheet_data_adapter(this, sheet_data);
            messages.setAdapter(adapter);
            (findViewById(R.id.send_sms)).setEnabled(true);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sharedPreferences.edit().putString(FILE_NAME,file_name.getText().toString()).apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}