package org.xdty.callerinfo;

import android.content.ComponentName;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.test.espresso.FailureHandler;
import androidx.test.espresso.NoMatchingRootException;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SdkSuppress;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObject2;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;

import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.xdty.callerinfo.activity.SettingsActivity;
import org.xdty.callerinfo.model.db.InCall;
import org.xdty.callerinfo.utils.Utils;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.pressKey;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.action.ViewActions.swipeRight;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.hasSibling;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.xdty.callerinfo.TestUtils.atPosition;
import static org.xdty.callerinfo.TestUtils.childWithBackgroundColor;
import static org.xdty.callerinfo.TestUtils.clickChildViewWithId;
import static org.xdty.callerinfo.TestUtils.isWindowAtPosition;
import static org.xdty.callerinfo.TestUtils.itemsCountIs;
import static org.xdty.callerinfo.TestUtils.swipeDown;
import static org.xdty.callerinfo.TestUtils.swipeUp;

@RunWith(AndroidJUnit4.class)
@SdkSuppress(minSdkVersion = 18)
public class MainActivityTest extends ActivityTestBase {

    @Test
    public void testEmptyList() {
        UiObject2 list = mDevice.wait(Until.findObject(By.res(BASIC_PACKAGE, "history_list")), 500);
        UiObject2 empty = mDevice.findObject(By.res(BASIC_PACKAGE, "empty_text"));

        if (list == null) {
            assertThat(empty, notNullValue());
        }

        if (empty == null) {
            assertThat(list, notNullValue());
        }
    }

    @Test
    public void testActionSetting() {
        openActionBarOverflowOrOptionsMenu(getContext());
        onView(withText(R.string.action_settings)).perform(click());
        intended(hasComponent(new ComponentName(getContext(), SettingsActivity.class)));
        //pressBack();
    }

    @Test
    public void testRecyclerViewItemClick() {

        String text = Utils.Companion.readableTime(mInCalls.get(0).getDuration());

        onView(withId(R.id.history_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0,
                clickChildViewWithId(R.id.card_view)));
        onView(allOf(withId(R.id.time),
                hasSibling(withText(text)))).check(matches(isDisplayed()));
        onView(withId(R.id.history_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0,
                clickChildViewWithId(R.id.card_view)));
        onView(allOf(withId(R.id.time),
                hasSibling(withText(text)))).check(matches(not(isDisplayed())));
    }

    @Test
    public void testRecyclerViewItemColor() {
        onView(withId(R.id.history_list)).check(
                matches(atPosition(0,
                        childWithBackgroundColor(R.id.card_view, mSetting.getPoiColor()))));
        onView(withId(R.id.history_list)).check(
                matches(atPosition(1,
                        childWithBackgroundColor(R.id.card_view, mSetting.getReportColor()))));
        onView(withId(R.id.history_list)).check(
                matches(atPosition(2,
                        childWithBackgroundColor(R.id.card_view, mSetting.getReportColor()))));
        onView(withId(R.id.history_list)).check(
                matches(atPosition(3,
                        childWithBackgroundColor(R.id.card_view, mSetting.getNormalColor()))));
    }

    @Test
    public void testActionClearHistory() {

        onView(withId(R.id.history_list)).check(matches(isDisplayed()));
        onView(withId(R.id.empty_text)).check(matches(not(isDisplayed())));

        openActionBarOverflowOrOptionsMenu(getContext());
        onView(withText(R.string.action_clear_history))
                .perform(click());

        // check confirm dialog and click cancel
        onView(withText(R.string.clear_history_message))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));
        onView(withText(R.string.cancel))
                .inRoot(isDialog())
                .perform(click());

        // check list exist
        onView(withId(R.id.history_list)).check(matches(isDisplayed()));
        onView(withId(R.id.empty_text)).check(matches(not(isDisplayed())));

        openActionBarOverflowOrOptionsMenu(getContext());
        onView(withText(R.string.action_clear_history))
                .perform(click());

        // check confirm dialog and click ok
        onView(withText(R.string.clear_history_message))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));
        onView(withText(R.string.ok))
                .inRoot(isDialog())
                .perform(click());

        // check list not exist
        onView(withId(R.id.history_list)).check(matches(isDisplayed()));
        onView(withId(R.id.empty_text)).check(matches(isDisplayed()));

        // reinsert data for other tests
        mDatabase.addInCallers(mInCalls);
    }

    @Test
    public void testActionClearCache() {

        onView(withId(R.id.history_list)).check(matches(isDisplayed()));
        onView(withId(R.id.empty_text)).check(matches(not(isDisplayed())));

        openActionBarOverflowOrOptionsMenu(getContext());
        onView(withText(R.string.action_clear_cache))
                .perform(click());

        // check confirm dialog and click ok
        onView(withText(R.string.clear_cache_confirm_message))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));
        onView(withText(R.string.ok))
                .inRoot(isDialog())
                .perform(click());

        // check snack bar
        onView(allOf(withId(R.id.snackbar_text),
                withText(R.string.clear_cache_message)))
                .check(matches(isDisplayed()));

        // check list exist
        onView(withId(R.id.history_list)).check(matches(isDisplayed()));
        onView(withId(R.id.empty_text)).check(matches(not(isDisplayed())));
    }

    @Test
    public void testActionSearch() {
        onView(withId(R.id.action_search)).perform(click());
        onView(withId(R.id.search_src_text)).check(matches(isDisplayed()));
        onView(withId(R.id.search_src_text)).check(matches(withHint(R.string.search_hint)));
        onView(isAssignableFrom(ImageButton.class)).perform(click());
        onView(withId(R.id.search_src_text)).check(doesNotExist());
        onView(withId(R.id.action_search)).perform(click());
        onView(isAssignableFrom(EditText.class)).perform(typeText("10086"),
                pressKey(KeyEvent.KEYCODE_ENTER));

        onView(withId(R.id.window_layout)).inRoot(
                withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
        onView(withId(R.id.number_info)).inRoot(
                withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView()))))
                .check(matches(withText("??????????????????")));

        onView(withId(R.id.history_list)).check(matches(not(isDisplayed())));

        onView(isAssignableFrom(ImageButton.class)).perform(click());
        onView(withId(R.id.window_layout)).inRoot(
                withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView()))))
                .withFailureHandler(
                        new FailureHandler() {
                            @Override
                            public void handle(Throwable error, Matcher<View> viewMatcher) {
                                assertTrue(error instanceof NoMatchingRootException);
                            }
                        })
                .check(doesNotExist());
        onView(withId(R.id.history_list)).check(matches(isDisplayed()));
    }

    @Test
    public void testActionMoveWindowPosition() {

        // click move window menu and check window visibility
        openActionBarOverflowOrOptionsMenu(getContext());
        onView(withText(R.string.action_float_window))
                .perform(click());
        onView(withId(R.id.window_layout)).inRoot(
                withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView()))))
                .check(matches(isDisplayed()));
        onView(withId(R.id.number_info)).inRoot(
                withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView()))))
                .check(matches(withText(R.string.float_window_hint)));

        // swipe up window and check position
        onView(withId(R.id.number_info)).inRoot(
                withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView()))))
                .perform(swipeUp());
        onView(withId(R.id.window_layout)).inRoot(
                withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView()))))
                .check(matches(isWindowAtPosition(mSetting.getWindowX(), mSetting.getWindowY())));

        // swipe down window and check position
        onView(withId(R.id.number_info)).inRoot(
                withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView()))))
                .perform(swipeDown());
        onView(withId(R.id.window_layout)).inRoot(
                withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView()))))
                .check(matches(isWindowAtPosition(mSetting.getWindowX(), mSetting.getWindowY())));

        // click close window menu and check window visibility
        openActionBarOverflowOrOptionsMenu(getContext());
        onView(withText(R.string.close_window))
                .perform(click());
        onView(withId(R.id.window_layout)).inRoot(
                withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView()))))
                .withFailureHandler(
                        new FailureHandler() {
                            @Override
                            public void handle(Throwable error, Matcher<View> viewMatcher) {
                                assertTrue(error instanceof NoMatchingRootException);
                            }
                        })
                .check(doesNotExist());
    }

    @Test
    public void testNotificationClick() throws UiObjectNotFoundException {
        // click move window menu and check notification
        openActionBarOverflowOrOptionsMenu(getContext());
        onView(withText(R.string.action_float_window))
                .perform(click());
        onView(withId(R.id.window_layout)).inRoot(
                withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView()))))
                .check(matches(isDisplayed()));

        mDevice.openNotification();
        mDevice.wait(Until.hasObject(By.pkg("com.android.systemui")), 10000);
        UiSelector notificationStackScroller = new UiSelector().packageName("com.android.systemui")
                .resourceId("com.android.systemui:id/notification_stack_scroller");
        UiObject notificationStackScrollerUiObject = mDevice.findObject(notificationStackScroller);
        assertTrue(notificationStackScrollerUiObject.exists());

        String text = getContext().getString(R.string.app_name);
        UiObject notify = notificationStackScrollerUiObject.getChild(new UiSelector().text(text));
        assertTrue(notify.exists());
        notify.click();
        SystemClock.sleep(2000);
        onView(withId(R.id.window_layout)).inRoot(
                withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView()))))
                .withFailureHandler(
                        new FailureHandler() {
                            @Override
                            public void handle(Throwable error, Matcher<View> viewMatcher) {
                                assertTrue(error instanceof NoMatchingRootException);
                            }
                        })
                .check(doesNotExist());
    }

    @Test
    public void testRecyclerViewItemSwipe() throws UiObjectNotFoundException {

        // swipe and undo
        onView(withId(R.id.history_list))
                .perform(RecyclerViewActions.actionOnItemAtPosition(1, swipeLeft()));
        onView(withId(R.id.history_list))
                .check(itemsCountIs(3));
        onView(allOf(withText(R.string.undo), hasSibling(withText(R.string.deleted))))
                .perform(click());
        onView(withId(R.id.history_list))
                .check(itemsCountIs(4));

        // swipe and delete
        onView(withId(R.id.history_list))
                .perform(RecyclerViewActions.actionOnItemAtPosition(1, swipeLeft()));
        onView(withId(R.id.history_list))
                .check(itemsCountIs(3));
        onView(allOf(withId(R.id.snackbar_text), withText(R.string.deleted)))
                .perform(swipeRight());
        onView(withId(R.id.history_list))
                .check(itemsCountIs(3));

        onView(withId(R.id.history_list)).check(
                matches(atPosition(0, hasDescendant(withText(mInCalls.get(0).getNumber())))));
        onView(withId(R.id.history_list)).check(
                matches(atPosition(1, hasDescendant(withText(mInCalls.get(2).getNumber())))));

        // swipe and wait snack bar gone
        onView(withId(R.id.history_list))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, swipeLeft()));
        onView(withId(R.id.history_list))
                .check(itemsCountIs(2));
        SystemClock.sleep(5000);

        onView(withId(R.id.history_list)).check(
                matches(atPosition(0, hasDescendant(withText(
                        mDatabase.fetchInCallsSync().get(0).getNumber())))));
    }

    @Test
    public void testSwipeRefresh() {
        long time = System.currentTimeMillis();
        InCall inCall = new InCall("10000", time, 3553, 35052);
        mDatabase.saveInCall(inCall);

        onView(withId(R.id.swipe_refresh_layout)).perform(swipeDown());

        SystemClock.sleep(500);

        onView(withId(R.id.history_list)).check(
                matches(atPosition(0, hasDescendant(withText("??????????????????")))));

        mDatabase.removeInCall(inCall);

        onView(withId(R.id.swipe_refresh_layout)).perform(swipeDown());
        onView(withId(R.id.history_list)).check(
                matches(atPosition(0, hasDescendant(withText("??????????????????")))));
    }
}
