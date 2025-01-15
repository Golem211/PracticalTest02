package ro.pub.cs.systems.eim.practicaltest02v8;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private EditText currencyEditText;
    private TextView resultTextView;
    private Button fetchButton;
    private Button navigateButton;
    private Handler handler;
    private SharedPreferences sharedPreferences;
    private long lastFetchTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currencyEditText = findViewById(R.id.currencyEditText);
        resultTextView = findViewById(R.id.resultTextView);
        fetchButton = findViewById(R.id.fetchButton);
        navigateButton = findViewById(R.id.navigateButton);
        handler = new Handler(Looper.getMainLooper());
        sharedPreferences = getSharedPreferences("BitcoinRateCache", MODE_PRIVATE);

        fetchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currency = currencyEditText.getText().toString().toUpperCase();
                long currentTime = System.currentTimeMillis();
                System.out.println("currentTime: " + currentTime);
                lastFetchTime = sharedPreferences.getLong("lastFetchTime", 0);
                System.out.println("lastFetchTime: " + lastFetchTime);
                //currency changed
                String cacheCurrency = sharedPreferences.getString("currency", "N/A");
                if (!currency.equals(cacheCurrency) || (currentTime - lastFetchTime > 60000)) { // 1 minute in milliseconds
                    Log.d("MainActivity", "Fetching new data for currency: " + currency);
                    fetchBitcoinRate(currency);
                } else {
                    Log.d("MainActivity", "Reading from cache for currency: " + currency);
                    readFromCache(currency);
                }
            }
        });

        navigateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CalculatorActivity.class);
                startActivity(intent);
            }
        });
    }

    private void fetchBitcoinRate(final String currency) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("https://api.coindesk.com/v1/bpi/currentprice/" + currency + ".json");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");

                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    Log.d("MainActivity", "Received data: " + response.toString());

                    JSONObject jsonObject = new JSONObject(response.toString());
                    String rate = jsonObject.getJSONObject("bpi").getJSONObject(currency).getString("rate");

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("rate", rate);
                    editor.putLong("lastFetchTime", System.currentTimeMillis());
                    editor.apply();
                    Log.d("MainActivity", "Parsed rate: " + rate);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            resultTextView.setText("1 BTC = " + rate + " " + currency);
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void readFromCache(String currency) {
        String rate = sharedPreferences.getString("rate", "N/A");
        Log.d("MainActivity", "Read from cache: " + rate);
        resultTextView.setText("1 BTC = " + rate + " " + currency);
    }
}