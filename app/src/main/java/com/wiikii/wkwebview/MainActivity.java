package com.wiikii.wkwebview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.wiikii.library.activity.WebViewActivity;

/**
 * Created by wiikii on 15/4/28.
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        final EditText editText = (EditText) findViewById(R.id.editText);
        Button button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = editText.getText().toString();
                if(url != null) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setClass(MainActivity.this, WebViewActivity.class);
                    intent.putExtra(WebViewActivity.WEB_VIEW_DATA_URL, url);
//                    intent.putExtra(WebViewActivity.WEB_VIEW_DATA_TITLE, "WebView");
                    intent.putExtra(WebViewActivity.WEB_VIEW_DATA_DATA, "");
                    intent.putExtra(WebViewActivity.WEB_VIEW_DATA_SHARE, true);
                    startActivity(intent);
                }
            }
        });
    }
}
