package com.itheima.mobilesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.itheima.mobilesafe.R;
import com.itheima.mobilesafe.utils.ConstantValue;
import com.itheima.mobilesafe.utils.SPUtil;
import com.itheima.mobilesafe.utils.StreamUtil;
import com.itheima.mobilesafe.utils.ToastUtil;

import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

public class SplashActivity extends Activity {
    private TextView versionTV;
    private int mLocalVersionCode;
    private static final String tag = SplashActivity.class.getName();
    private final MyHandler mHandler = new MyHandler(this);

    private static final String CHECK_VERSION_URL = "http://10.0.2.2:8080/newVersion";
    private static final String APK_NAME = "mobilesafe.apk";
    private static final int UPDATE_VERSION = 100; //更新版本状态码
    private static final int ENTER_HOME = 101; //进入应用程序主界面状态码
    private static final int EXCEPTION_CODE = 102; //异常状态码

    private static final long SPLASH_DURATION = 500; //毫秒
    private String mVersionDes;
    private String mDownloadUrl;
    private ProgressDialog progressDialog;

    private static final int INSTALL_REQUEST_CODE = 1;
    private RelativeLayout rlRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去除掉当前activity的头title
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);

        //初始化UI
        initUI();
        //初始化数据
        initData();
        //初始化动画
        initAnimation();
    }

    /**
     * 添加淡入的动画效果
     */
    private void initAnimation() {
        AlphaAnimation animation = new AlphaAnimation(0, 1);
        animation.setDuration(3000);
        rlRoot.startAnimation(animation);
    }

    /**
     * 获取数据方法
     */
    private void initData() {
        //应用版本名称
        String versionName = getVersionName();
        versionTV.setText(versionName);
        //检测是否有更新, 如果有,提示用户下载(本地版本号 和服务器版本号对比)
        //成员变量(m member)
        mLocalVersionCode = getVersionCode();

        //是否自动更新
        boolean update = SPUtil.getBoolean(this, ConstantValue.UPDATE_CONFIG, false);
        if (update) {
            //获取服务器版本号(客户端发请求, 服务端给响应)
            checkVersion();
        } else {
            mHandler.sendEmptyMessageDelayed(ENTER_HOME, SPLASH_DURATION);
        }
    }

    /**
     * 检测版本号
     */
    private void checkVersion() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                long start = System.currentTimeMillis();
                Message message = Message.obtain();
                try {
                    //10.0.2.2 仅限模拟器访问电脑tomcat
                    URL url = new URL(CHECK_VERSION_URL);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    //设置常见请求参数
                    connection.setConnectTimeout(2000);  //请求2秒
                    connection.setReadTimeout(2000);  //读取超时
                    connection.setRequestMethod("GET"); //请求方式

                    //获取响应吗
                    if (connection.getResponseCode() == 200) {
                        //以流的形式获取数据
                        InputStream is = connection.getInputStream();
                        String result = StreamUtil.stream2String(is);
                        //json的解析
                        JSONObject jsonObject = new JSONObject(result);
                        String versionName = jsonObject.getString("versionName");
                        String versionCode = jsonObject.getString("versionCode");
                        mVersionDes = jsonObject.getString("versionDes");
                        mDownloadUrl = jsonObject.getString("downloadUrl");
                        //比对版本号
                        if (Integer.parseInt(versionCode) > mLocalVersionCode) {
                            //提示用户更新, 使用消息机制
                            message.what = UPDATE_VERSION;
                        } else {
                            //进入应用主界面
                            message.what = ENTER_HOME;
                        }
                    }
                } catch (Exception e) {
                    message.what = EXCEPTION_CODE;
                    e.printStackTrace();
                } finally {
                    //指定睡眠时间, 请求网络的时长超过4秒则不做处理
                    //请求网络的时长小于4秒,强制让其睡眠满4秒
                    long end = System.currentTimeMillis();
                    long time = end - start;
                    if (time < SPLASH_DURATION) {
                        try {
                            Thread.sleep(SPLASH_DURATION - time);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    mHandler.sendMessage(message);
                }
            }
        }) {
        }.start();
    }

    /**
     * 获取版本号
     *
     * @return
     */
    private int getVersionCode() {
        PackageManager pm = getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取版本名称
     *
     * @return 应用版本名称, 返回null代表异常
     */
    private String getVersionName() {
        //获取包管理对象
        PackageManager pm = getPackageManager();
        //从包管理对象中,获取指定包名的基本信息,传0代表获取基本信息
        try {
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
            //获取对应版本名称
            return packageInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 初始化UI
     */
    private void initUI() {
        versionTV = findViewById(R.id.tv_version_name);
        rlRoot = findViewById(R.id.rl_root);
    }

    /**
     * (1) 将非静态内部类Handler和Runnable转为静态内部类，因为非静态内部类(匿名内部类)都会默认持有对外部类的强引用。
     * (2) 改成静态内部类后，对外部类的引用设为弱引用，因为在垃圾回收时，会自动将弱引用的对象回收。
     */
    private static class MyHandler extends Handler {
        WeakReference<SplashActivity> mWeakReference;
        SplashActivity splashActivity;

        MyHandler(SplashActivity activity) {
            mWeakReference = new WeakReference<>(activity);
            splashActivity = mWeakReference.get();
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (splashActivity != null) {
                switch (msg.what) {
                    case UPDATE_VERSION:
                        //弹出对话框,提示用户更新
                        splashActivity.showUpdateDialog();
                        break;
                    case ENTER_HOME:
                        //进入应用程序主界面
                        splashActivity.enterHome();
                        break;
                    case EXCEPTION_CODE:
                        //发生异常
                        ToastUtil.show(splashActivity, "发生了异常");
                        splashActivity.enterHome();
                        break;
                }
            }
        }
    }

    /**
     * 弹出对话框,提示用户更新
     */
    private void showUpdateDialog() {
        //对话框是依赖于activity存在的
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.home_apps);
        builder.setTitle("发现新版本");
        builder.setMessage(mVersionDes);
        builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //下载apk, 根据mDownloadUrl
                downloadApk();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //取消对话框
                enterHome();
            }
        });

        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                //后退
                enterHome();
                dialog.dismiss();
            }
        });

        builder.show();
    }

    /**
     * 下载Apk的方法
     */
    private void downloadApk() {
        progressDialog = new ProgressDialog(this);
        //下载apk,反之apk的所在路径
        //获取路径
        final String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + APK_NAME; ///storage/sdcard/mobilesafe.apk
        Log.i(tag, "path1:" + path);
        //发送请求, 获取apk,防止到指定路径
        RequestParams params = new RequestParams(mDownloadUrl);
        params.setSaveFilePath(path); //设置下载路径
        params.setAutoRename(false); //设置不自动命名
        //params.setSslSocketFactory(...); // 设置ssl
        x.http().get(params, new Callback.ProgressCallback<File>() {
            @Override
            public void onSuccess(File result) {
                Log.i(tag, "success");
                progressDialog.dismiss();
                installApk(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ex.printStackTrace();
                Log.i(tag, "error");
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                Log.i(tag, "cancelled");
            }

            @Override
            public void onFinished() {
                Log.i(tag, "finished");
            }

            @Override
            public void onWaiting() {
                Log.i(tag, "waiting");
            }

            @Override
            public void onStarted() {
                Log.i(tag, "started");
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                Log.i(tag, "loading");
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setMessage("下载中...");
                progressDialog.show();
                progressDialog.setMax((int) total);
                progressDialog.setProgress((int) current);
            }
        });
    }

    /**
     * 隐式意图安装对应apk
     *
     * @param file
     */
    private void installApk(File file) {
        //系统界面
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");

        //startActivity(intent);
        //安装界面用户点击取消后能跳转到应用的主界面
        startActivityForResult(intent, INSTALL_REQUEST_CODE);
    }

    /**
     * 开启一个activity后,返回结果调用的方法
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == INSTALL_REQUEST_CODE) {
            enterHome();
        }
    }

    /**
     * 进入应用程序主界面
     */
    public void enterHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        //在开启一个新的界面后,将导航界面关闭(导航界面只可见一次)
        finish();
    }
}
