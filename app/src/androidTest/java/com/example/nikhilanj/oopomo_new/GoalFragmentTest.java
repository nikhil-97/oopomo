package com.example.nikhilanj.oopomo_new;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class GoalFragmentTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void goalFragmentTest() {
        ViewInteraction bottomNavigationItemView = onView(
                allOf(withId(R.id.navigation_goals),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.bottomnavigation),
                                        0),
                                1),
                        isDisplayed()));
        bottomNavigationItemView.perform(click());

        ViewInteraction floatingActionButton = onView(
                allOf(withId(R.id.fab_add_goal),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content), 1), 3), isDisplayed()));
        floatingActionButton.perform(click());

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.et_goal_title),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.goal_editable),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText.perform(click());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.et_goal_title),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.goal_editable),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText2.perform(replaceText("Goal 1"), closeSoftKeyboard());

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.et_goal_title), withText("Goal 1"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.goal_editable),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText3.perform(pressImeActionButton());

        ViewInteraction floatingActionButton2 = onView(
                allOf(withId(R.id.btn_save_goal),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.goal_editable),
                                        2),
                                0),
                        isDisplayed()));
        floatingActionButton2.perform(click());

        ViewInteraction floatingActionButton3 = onView(
                allOf(withId(R.id.btn_show_goal_edit_menu),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.noteditable_goal),
                                        0),
                                1),
                        isDisplayed()));
        floatingActionButton3.perform(click());

        ViewInteraction appCompatTextView = onView(
                allOf(withId(R.id.title), withText("Edit Goal"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.support.v7.view.menu.ListMenuItemView")),
                                        0),
                                0),
                        isDisplayed()));
        appCompatTextView.perform(click());

        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.et_goal_description),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.goal_editable),
                                        1),
                                0),
                        isDisplayed()));
        appCompatEditText4.perform(replaceText("This is a description of goal 1"), closeSoftKeyboard());

        ViewInteraction floatingActionButton4 = onView(
                allOf(withId(R.id.btn_save_goal),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.goal_editable),
                                        2),
                                0),
                        isDisplayed()));
        floatingActionButton4.perform(click());

        ViewInteraction floatingActionButton5 = onView(
                allOf(withId(R.id.fab_add_goal),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        1),
                                3),
                        isDisplayed()));
        floatingActionButton5.perform(click());

        ViewInteraction appCompatEditText5 = onView(
                allOf(withId(R.id.et_goal_title),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.goal_editable),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText5.perform(click());

        ViewInteraction appCompatEditText6 = onView(
                allOf(withId(R.id.et_goal_title),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.goal_editable),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText6.perform(replaceText("Goal 2"), closeSoftKeyboard());

        ViewInteraction appCompatEditText7 = onView(
                allOf(withId(R.id.et_goal_description),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.goal_editable),
                                        1),
                                0),
                        isDisplayed()));
        appCompatEditText7.perform(replaceText("This is a description of goal 2"), closeSoftKeyboard());

        ViewInteraction floatingActionButton6 = onView(
                allOf(withId(R.id.btn_save_goal),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.goal_editable),
                                        2),
                                0),
                        isDisplayed()));
        floatingActionButton6.perform(click());

        ViewInteraction floatingActionButton7 = onView(
                allOf(withId(R.id.fab_add_goal),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content), 1), 3), isDisplayed()));
        floatingActionButton7.perform(click());

        ViewInteraction floatingActionButton8 = onView(
                allOf(withId(R.id.btn_show_goal_edit_menu),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.noteditable_goal),
                                        0),
                                1),
                        isDisplayed()));
        floatingActionButton8.perform(click());

        ViewInteraction floatingActionButton9 = onView(
                allOf(withId(R.id.btn_show_goal_edit_menu),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.noteditable_goal),
                                        0),
                                1),
                        isDisplayed()));
        floatingActionButton9.perform(click());

        ViewInteraction appCompatTextView2 = onView(
                allOf(withId(R.id.title), withText("Delete Goal"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.support.v7.view.menu.ListMenuItemView")),
                                        0),
                                0),
                        isDisplayed()));
        appCompatTextView2.perform(click());

        ViewInteraction appCompatButton = onView(
                allOf(withId(android.R.id.button1), withText("YES"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.buttonPanel),
                                        0),
                                3)));
        appCompatButton.perform(scrollTo(), click());

        ViewInteraction floatingActionButton10 = onView(
                allOf(withId(R.id.fab_add_goal),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        1),
                                3),
                        isDisplayed()));
        floatingActionButton10.perform(click());

    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
