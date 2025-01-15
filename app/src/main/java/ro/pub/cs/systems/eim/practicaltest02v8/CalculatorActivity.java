package ro.pub.cs.systems.eim.practicaltest02v8;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CalculatorActivity extends AppCompatActivity {

    private EditText operationEditText;
    private EditText t1EditText;
    private EditText t2EditText;
    private TextView resultTextView;
    private Button calculateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);

        operationEditText = findViewById(R.id.operationEditText);
        t1EditText = findViewById(R.id.t1EditText);
        t2EditText = findViewById(R.id.t2EditText);
        resultTextView = findViewById(R.id.resultTextView);
        calculateButton = findViewById(R.id.calculateButton);

        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String operation = operationEditText.getText().toString();
                String t1 = t1EditText.getText().toString();
                String t2 = t2EditText.getText().toString();
                new CalculatorTask().execute(operation, t1, t2);
            }
        });
    }

    private class CalculatorTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String operation = params[0];
            String t1 = params[1];
            String t2 = params[2];
            try {
                URL url = new URL("http://192.168.1.100:8080?operation=" + operation + "&t1=" + t1 + "&t2=" + t2);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                return response.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return "Error";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            resultTextView.setText(result);
        }
    }
}