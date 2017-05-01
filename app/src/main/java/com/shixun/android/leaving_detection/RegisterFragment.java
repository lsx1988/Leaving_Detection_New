package com.shixun.android.leaving_detection;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SignUpCallback;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by shixunliu on 10/4/17.
 */

public class RegisterFragment extends GeneralFragment {

    @BindView(R.id.username) AutoCompleteTextView mUsernameView;
    @BindView(R.id.password) EditText mPasswordView;
    @BindView(R.id.register_form) ScrollView mRegisterFormView;
    @BindView(R.id.register_progress) ProgressBar mProgressView;
    @BindView(R.id.username_register_button) Button mUsernameSignInButton;

    @Override
    public void onResume() {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getString(R.string.register));
        setHasOptionsMenu(true);
        super.onResume();
    }

    @Override
    protected int getLayoutID() {
        return R.layout.fragment_register;
    }

    @OnClick(R.id.username_register_button)
    public void login() {
        attemptRegister();
    }

    private void attemptRegister() {
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);

            AVUser user = new AVUser();// 新建 AVUser 对象实例
            user.setUsername(username);// 设置用户名
            user.setPassword(password);// 设置密码
            user.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(AVException e) {
                    if (e == null) {
                        // 注册成功，把用户对象赋值给当前用户 AVUser.getCurrentUser()
                        if(getActivity() instanceof ActionListener) {
                            ((ActionListener) getActivity()).onRegisterSuccessful();
                        }
                    } else {
                        // 失败的原因可能有多种，常见的是用户名已经存在。
                        showProgress(false);
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private boolean isusernameValid(String username) {
        //TODO: Replace this with your own logic
        return username.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mRegisterFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if(getActivity() instanceof ActionListener) {
                ((ActionListener) getActivity()).onUpNevigationClick();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
