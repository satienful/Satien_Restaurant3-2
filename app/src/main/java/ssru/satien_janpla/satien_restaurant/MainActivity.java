package ssru.satien_janpla.satien_restaurant;

import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
    //Explicit
    private UserTable objUserTABLE;
    private FoodTABLE objFoodTABLE;
    private OrderTABLE objOrderTABLE;
    private MySQLite mySQLite;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Connected SQLite
        connectedSQLite();

        //Test Add Value
         testAddValue();

        //Synchronize MySQL to SQLite
        //synAndDelete();

        //Request SQLite
        mySQLite = new MySQLite(this);

    }   //OnCreate
    private void synAndDelete(){
        SQLiteDatabase sqLiteDatabase = openOrCreateDatabase(MySQLiteOpenHelper.DATABASE_NAME,
                MODE_PRIVATE,null);
        sqLiteDatabase.delete(MySQLite.user_table,null,null);
        MySynJSON mySynJSON = new MySynJSON();
        mySynJSON.execute();
    }   //SynAndDelete

    //Create Inner Class for Connected JSON
    public class MySynJSON extends AsyncTask<Void, Void, String>{

        @Override
        protected String doInBackground(Void... voids) {
            try {
                String strURL = "http://www.csclub.ssru.ac.th/satien/php_get_userTABLE.php";
                OkHttpClient okHttpClient = new OkHttpClient();
                Request.Builder builder = new Request.Builder();
                Request request = builder.url(strURL).build();
                Response response = okHttpClient.newCall(request).execute();
                return response.body().string();
            } catch (Exception e){
                Log.d("Satien","doInBack ==> " + e.toString());
                return null;
            }
        }   //doInBackground

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("satien", "strJSON ==> " + s );
            try {
                JSONArray jsonArray = new JSONArray(s);
                for (int i=0; i<jsonArray.length(); i++){

                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String strUser = jsonObject.getString(MySQLite.column_user);
                    String strPassword = jsonObject.getString(mySQLite.column_password);
                    String strName = jsonObject.getString(mySQLite.column_name);
                    mySQLite.addNewUser(strUser, strPassword, strName);
                }   //for
                Toast.makeText(MainActivity.this,"Synchronize mySQL to SQLite Finsh",
                        Toast.LENGTH_SHORT).show();
            } catch (Exception e){
                Log.d("satien", "onPost ==> " + e.toString());
            }
        }   // OnPostExcute
    }   //MySynJSON Class

     private void testAddValue() {
        objUserTABLE.addNewUser("testUser", "testPass", "testName");
        objFoodTABLE.addNewFood("testFood", "testSource", "testPrice");
        objOrderTABLE.addOrder("testOfficer", "testDesk", "testFood", "testItem");
    }   //testAddValue

    private void connectedSQLite() {
        objUserTABLE = new UserTable(this);
        objFoodTABLE = new FoodTABLE(this);
        objOrderTABLE = new OrderTABLE(this);
    }   //connectedSQLite

  }   //Main Class
