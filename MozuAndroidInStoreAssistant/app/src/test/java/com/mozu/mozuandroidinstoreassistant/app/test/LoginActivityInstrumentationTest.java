package com.mozu.mozuandroidinstoreassistant.app.test;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;

import com.mozu.mozuandroidinstoreassistant.app.LoginActivity;
import com.mozu.mozuandroidinstoreassistant.app.R;

import java.lang.Exception;

import static android.test.ViewAsserts.assertOnScreen;
import static com.google.android.apps.common.testing.ui.espresso.Espresso.onView;
import static com.google.android.apps.common.testing.ui.espresso.assertion.ViewAssertions.matches;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withId;
import static com.google.android.apps.common.testing.ui.espresso.matcher.ViewMatchers.withText;

public class LoginActivityInstrumentationTest extends ActivityInstrumentationTestCase2<LoginActivity> {
    private LoginActivity mLoginActivity;
    private TextView mEmailView;

    public LoginActivityInstrumentationTest() {
        super("com.mozu.mozuandroidinstoreassistant.app", LoginActivity.class);
    }

    protected void setUp() throws Exception {
        super.setUp();

        mLoginActivity = getActivity();
        mEmailView = (TextView) mLoginActivity.findViewById(R.id.email);
    }

    public void testTextView() {
        assertOnScreen(mLoginActivity.getWindow().getDecorView(), mEmailView);
    }

    public void testLabel() {
        onView(withId(R.id.email_login_form)).check(matches(withText("Email")));
    }

}