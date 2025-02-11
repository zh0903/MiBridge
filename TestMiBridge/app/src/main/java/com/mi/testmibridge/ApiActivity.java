package com.mi.testmibridge;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bun.miitmdid.core.JLibrary;
import com.mi.mibridge.MiBridge;

import java.util.ArrayList;
import java.util.List;

public class ApiActivity extends AppCompatActivity {

    // 数据源
    private List<MiBridgeModel> miBridgeModels = new ArrayList<>();

    // 输出
    private TextView mBridge_output;

    // DebugPermission输入的key
    private EditText mAuthKeyEditText;

    // cpu-level
    private EditText mCpuLevelEditText;

    // cpu-timeout
    private EditText mCpuTimeoutEditText;

    // gpu-level
    private EditText mGpuLevelEditText;

    // gpu-timeout
    private EditText mGpuTimeoutEditText;

    // ddr-level
    private EditText mDdrLevelEditText;

    // ddr-timeout
    private EditText mDdrTimeoutEditText;

    // ThreadPriority
    private EditText mThreadPriorityEditText;

    // io_prefetch_path
    private EditText mIOPrefetchPathEditText;

    //VAID
    private static String my_vaid;

    // 包名
    private static String mPackageName;

    // uid
    private int mBridgeUid;

    // tid
    private int mBridgeTid;

    private MiitHelper.AppIdsUpdater appIdsUpdater = new MiitHelper.AppIdsUpdater() {
        @Override
        public void OnIdsAvalid(@NonNull String ids) {
            my_vaid = ids;
        }
    };

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        JLibrary.InitEntry(base);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_api);
        //获取VAID等设备标识符
        MiitHelper miitHelper = new MiitHelper(appIdsUpdater);
        miitHelper.getDeviceIds(getApplicationContext());

        initData();
        initView();
    }

    private void initView() {
        mAuthKeyEditText = findViewById(R.id.edit_auth_key);
        mCpuLevelEditText = findViewById(R.id.cpu_level);
        mCpuTimeoutEditText = findViewById(R.id.cpu_timeout);
        mGpuLevelEditText = findViewById(R.id.gpu_level);
        mGpuTimeoutEditText = findViewById(R.id.gpu_timeout);
        mDdrLevelEditText = findViewById(R.id.ddr_level);
        mDdrTimeoutEditText = findViewById(R.id.ddr_timeout);
        mBridge_output = findViewById(R.id.bridge_output);
        mThreadPriorityEditText = findViewById(R.id.thread_priority_edit);
        mIOPrefetchPathEditText = findViewById(R.id.io_prefetch_path);
        RecyclerView recyclerView = findViewById(R.id.bridge_recycle_view);
        recyclerView.setAdapter(new MiBridgeAdapter(miBridgeModels));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void showResult(boolean flag, String toastStr) {
        String result = flag ? "Success" : "Fail";
        mBridge_output.setText(result);
        Toast.makeText(ApiActivity.this, toastStr + result, Toast.LENGTH_SHORT).show();
    }

    private void showResult(int ret, String toastStr) {
        String result = ret == 0 ? "Success" : (ret == -2 ? "No Permission" : "Fail");
        mBridge_output.setText(result);
        Toast.makeText(ApiActivity.this, toastStr + result, Toast.LENGTH_SHORT).show();
    }

    private void initData() {
        mPackageName = getApplicationContext().getPackageName();
        mBridgeUid = android.os.Process.myUid();
        mBridgeTid = android.os.Process.myTid();
        miBridgeModels.add(new MiBridgeModel("checkPermission", new Runnable() {
            @Override
            public void run() {
                boolean ret = MiBridge.checkPermission(mPackageName, mBridgeUid);
                showResult(ret, "checkPermission ");
            }
        }));

        miBridgeModels.add(new MiBridgeModel("checkDebugPermission", new Runnable() {
            @Override
            public void run() {
                String authKey = mAuthKeyEditText.getText().toString();
                if (!"".equals(authKey)) {
                    boolean ret = MiBridge.checkDebugPermission(ApiActivity.this, mPackageName, mBridgeUid, authKey);
                    showResult(ret, "checkDebugPermission ");
                } else {
                    Toast.makeText((ApiActivity.this), "Please input authentication key!", Toast.LENGTH_SHORT).show();
                }
            }
        }));

        miBridgeModels.add(new MiBridgeModel("Testing:GetVAID", new Runnable() {
            @Override
            public void run() {
                mBridge_output.setText(my_vaid);
            }
        }));

        miBridgeModels.add(new MiBridgeModel("requestCpuHighFreq(level,timeout)", new Runnable() {
            @Override
            public void run() {
                int cpuLevel = MiBridgeUtil.parseString(mCpuLevelEditText.getText().toString());
                int cpuTimeout = MiBridgeUtil.parseString(mCpuTimeoutEditText.getText().toString());
                int ret = MiBridge.requestCpuHighFreq(mBridgeUid, cpuLevel, cpuTimeout);
                showResult(ret, "requestCpuHighFreq ");
            }
        }));

        miBridgeModels.add(new MiBridgeModel("cancelCpuHighFreq", new Runnable() {
            @Override
            public void run() {
                int ret = MiBridge.cancelCpuHighFreq(mBridgeUid);
                showResult(ret, "cancelCpuHighFreq ");
            }
        }));

        miBridgeModels.add(new MiBridgeModel("requestGpuHighFreq(level,timeout)", new Runnable() {
            @Override
            public void run() {
                int gpuLevel = MiBridgeUtil.parseString(mGpuLevelEditText.getText().toString());
                int gpuTimeout = MiBridgeUtil.parseString(mGpuTimeoutEditText.getText().toString());
                int ret = MiBridge.requestGpuHighFreq(mBridgeUid, gpuLevel, gpuTimeout);
                showResult(ret, "requestGpuHighFreq ");
            }
        }));

        miBridgeModels.add(new MiBridgeModel("cancelGpuHighFreq", new Runnable() {
            @Override
            public void run() {
                int ret = MiBridge.cancelGpuHighFreq(mBridgeUid);
                showResult(ret, "cancelGpuHighFreq ");
            }
        }));

        miBridgeModels.add(new MiBridgeModel("requestIOPrefetch(filePath)", new Runnable() {
            @Override
            public void run() {
                String textString = mIOPrefetchPathEditText.getText().toString();
                String path = TextUtils.isEmpty(textString) ? "" : textString;
                int ret = MiBridge.requestIOPrefetch(mBridgeUid, path);
                showResult(ret, "requestIOPrefetch ");
            }
        }));

        miBridgeModels.add(new MiBridgeModel("requestThreadPriority", new Runnable() {
            @Override
            public void run() {
                int timeout = MiBridgeUtil.parseString(mThreadPriorityEditText.getText().toString());
                int ret = MiBridge.requestThreadPriority(mBridgeUid, mBridgeTid, timeout);
                showResult(ret, "requestThreadPriority ");
            }
        }));

        miBridgeModels.add(new MiBridgeModel("cancelThreadPriority", new Runnable() {
            @Override
            public void run() {
                int ret = MiBridge.cancelThreadPriority(mBridgeUid, mBridgeTid);
                showResult(ret, "cancelThreadPriority ");
            }
        }));

        miBridgeModels.add(new MiBridgeModel("requestDdrHighFreq(level,timeout)", new Runnable() {
            @Override
            public void run() {
                int ddrLevel = MiBridgeUtil.parseString(mDdrLevelEditText.getText().toString());
                int ddrTimeout = MiBridgeUtil.parseString(mDdrTimeoutEditText.getText().toString());
                int ret = MiBridge.requestDdrHighFreq(mBridgeUid, ddrLevel, ddrTimeout);
                showResult(ret, "requestGpuHighFreq ");
            }
        }));

        miBridgeModels.add(new MiBridgeModel("cancelDdrHighFreq", new Runnable() {
            @Override
            public void run() {
                int ret = MiBridge.cancelDdrHighFreq(mBridgeUid);
                showResult(ret, "cancelDdrHighFreq ");
            }
        }));

        miBridgeModels.add(new MiBridgeModel("requestBindCore", new Runnable() {
            @Override
            public void run() {
                int ret = MiBridge.requestBindCore(mBridgeUid, mBridgeTid,1000);
                showResult(ret, "requestThreadPriority ");
            }
        }));
    }


}