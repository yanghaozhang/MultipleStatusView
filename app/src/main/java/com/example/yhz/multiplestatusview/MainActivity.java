package com.example.yhz.multiplestatusview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private MultipleStatusView statusView;

    private boolean haveInit;

    FrameLayout.LayoutParams params =
            new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private void ensureInit() {
        if (!haveInit) {
            load();
            haveInit = true;
        }
    }

    public void load() {
        statusView = (MultipleStatusView) ((ViewStub) findViewById(R.id.viewstub)).inflate();
        statusView.setOnRetryClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = null;
                switch (v.getId()) {
                    case R.id.empty_retry_view:
                        str = "重试-数据为空";
                        break;
                    case R.id.error_retry_view:
                        str = "重试错误";
                        break;
                    case R.id.no_network_retry_view:
                        str = "重试-网络错误";
                        break;
                    default:
                        break;
                }

                switch (statusView.getViewStatus()) {
                    case MultipleStatusView.STATUS_EMPTY:
                        str += "--empty";
                        break;
                    case MultipleStatusView.STATUS_ERROR:
                        str += "--error";
                        break;
                    case MultipleStatusView.STATUS_NO_NETWORK:
                        str += "--no network";
                        break;
                    default:
                        break;
                }
                Toast.makeText(MainActivity.this, "on click :" + str, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void gone(View view) {
        ensureInit();
        statusView.hide();
    }

    public void visible(View view) {
        ensureInit();
        statusView.setVisibility(View.VISIBLE);
    }

    public void empty(View view) {
        ensureInit();
        statusView.showEmpty();
    }

    public void error(View view) {
        ensureInit();
        statusView.showError();
    }

    public void loadding(View view) {
        ensureInit();
        statusView.showLoading();
    }

    public void nonetwork(View view) {
        ensureInit();
        statusView.showNoNetwork();
    }

    public void emptyWithParams(View view) {
        ensureInit();
        statusView.showEmpty(R.layout.custom_empty_view, params);
    }

    public void errorWithParams(View view) {
        ensureInit();
        statusView.showError(R.layout.custom_error_view, params);
    }

    public void loaddingWithParams(View view) {
        ensureInit();
        statusView.showLoading(R.layout.loading_view, params);
    }

    public void noNetWorkWithParams(View view) {
        ensureInit();
        statusView.showNoNetwork(R.layout.custom_no_network_view, params);
    }
}
