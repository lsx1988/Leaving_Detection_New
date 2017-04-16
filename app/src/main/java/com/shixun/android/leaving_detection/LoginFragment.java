package com.shixun.android.leaving_detection;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by shixunliu on 10/4/17.
 */

public class LoginFragment extends GeneralFragment {

    /**
     * 获取控件引用
     */
    @BindView(R.id.username) AutoCompleteTextView mUsernameView;
    @BindView(R.id.password) EditText mPasswordView;
    @BindView(R.id.login_form) ScrollView mLoginFormView;
    @BindView(R.id.login_progress) ProgressBar mProgressView;
    @BindView(R.id.username_login_button) Button mUsernameLoginButton;
    @BindView(R.id.username_register_button)
    TextView mUsernameRegisterButton;

    @Override
    public void onResume() {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.login));
        super.onResume();
    }

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_login;
    }

    /**
     * 点击login 按钮,尝试登录
     */
    @OnClick(R.id.username_login_button)
    public void login() {
        attemptLogin();
    }

    /**
     * 点击register 按钮
     */
    @OnClick(R.id.username_register_button)
    public void register() {
       if(getActivity() instanceof btnClickListener) {
           ((btnClickListener) getActivity()).onRegisterClick();
       }
    }

    /**
     * 尝试登录
     */
    private void attemptLogin() {
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        /**
         * 获取用户输入的用户名与密码
         */
        final String username = mUsernameView.getText().toString();
        final String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        /**
         *  检测用户名与密码格式
         */

        //如过密码格式错误,提示错误并聚焦
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        //如果用户名为空,提示错误并聚焦
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        }

        // 用户名或密码有错则聚焦,无错则尝试登录, 登录程控则跳转到 detection 界面
        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);

            AVUser.logInInBackground(username, password, new LogInCallback<AVUser>() {
                @Override
                public void done(AVUser avUser, AVException e) {
                    if (e == null) {
                        // 注册成功，把用户对象赋值给当前用户 AVUser.getCurrentUser()
                        if(getActivity() instanceof btnClickListener) {
                            ((btnClickListener) getActivity()).onLoginSuccessful();
                        }
                    } else {
                        showProgress(false);
                       Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
