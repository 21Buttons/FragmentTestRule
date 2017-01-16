package com.android21buttons.fragmenttestrule;

import android.support.test.rule.ActivityTestRule;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

/**
 * This rule provides functional testing of a single fragment.
 * <p>
 * Idea extracted from: http://stackoverflow.com/a/38393087/842697
 *
 * @param <A> The activity where the fragment will be added
 * @param <F> The fragment to test
 */
public class FragmentTestRule<A extends FragmentActivity, F extends Fragment> extends ActivityTestRule<A> {
    private static final String TAG = "FragmentTestRule";

    private final Class<F> fragmentClass;
    private F fragment;

    public FragmentTestRule(Class<A> activityClass, Class<F> fragmentClass) {
        this(activityClass, fragmentClass, false);
    }

    public FragmentTestRule(Class<A> activityClass, Class<F> fragmentClass, boolean initialTouchMode) {
        this(activityClass, fragmentClass, initialTouchMode, true);
    }

    public FragmentTestRule(Class<A> activityClass, Class<F> fragmentClass, boolean initialTouchMode, boolean launchActivity) {
        super(activityClass, initialTouchMode, launchActivity);
        this.fragmentClass = fragmentClass;
    }

    @Override
    protected void afterActivityLaunched() {
        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    FragmentTestRule.this.fragment = createFragment();
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(android.R.id.content, fragment)
                            .commitNow();
                }
            });
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }
    }

    protected F createFragment() {
        try {
            return fragmentClass.newInstance();
        } catch (InstantiationException e) {
            throw new AssertionError(String.format("%s: Could not insert %s into %s: %s",
                    getClass().getSimpleName(),
                    fragmentClass.getSimpleName(),
                    getActivity().getClass().getSimpleName(),
                    e.getMessage()));
        } catch (IllegalAccessException e) {
            throw new AssertionError(String.format("%s: Could not insert %s into %s: %s",
                    getClass().getSimpleName(),
                    fragmentClass.getSimpleName(),
                    getActivity().getClass().getSimpleName(),
                    e.getMessage()));
        }
    }

    public F getFragment() {
        if (fragment == null) {
            Log.w(TAG, "Fragment wasn't created yet");
        }
        return fragment;
    }
}
