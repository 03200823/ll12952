package org.xdty.callerinfo;

import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.view.View;

import androidx.test.espresso.FailureHandler;
import androidx.test.espresso.NoMatchingRootException;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.filters.SdkSuppress;
import androidx.test.uiautomator.By;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;
import androidx.test.uiautomator.Until;

import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.xdty.callerinfo.receiver.IncomingCall;
import org.xdty.callerinfo.service.FloatWindow;

import static androidx.test.InstrumentationRegistry.getTargetContext;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.xdty.callerinfo.TestUtils.atPosition;
import static org.xdty.callerinfo.TestUtils.checkRadioItem;
import static org.xdty.callerinfo.TestUtils.childWithBackgroundColor;
import static org.xdty.callerinfo.TestUtils.isWindowAtPosition;
import static org.xdty.callerinfo.TestUtils.setProgress;
import static org.xdty.callerinfo.TestUtils.swipeDown;
import static org.xdty.callerinfo.TestUtils.swipeUp;
import static org.xdty.callerinfo.TestUtils.withHeight;
import static org.xdty.callerinfo.TestUtils.withTextAlign;
import static org.xdty.callerinfo.TestUtils.withTextPadding;
import static org.xdty.callerinfo.TestUtils.withTextSize;
import static org.xdty.callerinfo.TestUtils.withTransparency;

@RunWith(AndroidJUnit4.class)
@SdkSuppress(minSdkVersion = 18)
@LargeTest
public class SettingsActivityTest extends ActivityTestBase {

    IncomingCall.PhoneStateListener mIncomingCallListener;

    @Override
    public void beforeTest() {
        navigateToSetting();
        mIncomingCallListener = IncomingCall.PhoneStateListener.Companion.getInstance();
    }

    public void navigateToSetting() {
        openActionBarOverflowOrOptionsMenu(getContext());
        onView(withText(R.string.action_settings)).perform(click());
        SystemClock.sleep(1000);
    }

    @Test
    public void testColorSettings() {

        // normal color
        onView(withText(R.string.color_normal)).perform(click());
        onView(withContentDescription("Color 6"))
                .inRoot(isDialog())
                .perform(click());
        pressBack();
        onView(withId(R.id.history_list)).check(
                matches(atPosition(3,
                        childWithBackgroundColor(R.id.card_view, mSetting.getNormalColor()))));

        // poi color
        navigateToSetting();
        onView(withText(R.string.color_poi)).perform(click());
        onView(withContentDescription("Color 8"))
                .inRoot(isDialog())
                .perform(click());
        pressBack();
        onView(withId(R.id.history_list)).check(
                matches(atPosition(0,
                        childWithBackgroundColor(R.id.card_view, mSetting.getPoiColor()))));

        // report color
        navigateToSetting();
        onView(withText(R.string.color_report)).perform(click());
        onView(withContentDescription("Color 3"))
                .inRoot(isDialog())
                .perform(click());
        pressBack();
        onView(withId(R.id.history_list)).check(
                matches(atPosition(1,
                        childWithBackgroundColor(R.id.card_view, mSetting.getReportColor()))));
        onView(withId(R.id.history_list)).check(
                matches(atPosition(2,
                        childWithBackgroundColor(R.id.card_view, mSetting.getReportColor()))));
    }

    @Test
    public void testWindowTextSizeSetting() {
        onView(withText(R.string.window_text_style)).perform(click());

        // open text size setting dialog
        onView(withText(R.string.window_text_size)).perform(click());

        onView(withId(R.id.seek_bar))
                .inRoot(isDialog())
                .perform(setProgress(10));

        onView(withId(R.id.window_layout)).inRoot(not(isDialog()))
                .check(matches(isDisplayed()));
        onView(withId(R.id.number_info)).inRoot(not(isDialog()))
                .check(matches(withText(R.string.text_size)));

        // check preview window text size
        onView(withId(R.id.number_info)).inRoot(not(isDialog()))
                .check(matches(withTextSize(10)));

        // set another value
        onView(withId(R.id.seek_bar))
                .inRoot(isDialog())
                .perform(setProgress(40));

        onView(withId(R.id.number_info)).inRoot(not(isDialog()))
                .check(matches(withTextSize(40)));

        // cancel size setting
        onView(withText(R.string.cancel))
                .inRoot(isDialog())
                .perform(click());

        // check text size setting not performed
        onView(withText(R.string.window_text_size)).perform(click());

        onView(withId(R.id.number_info)).inRoot(not(isDialog()))
                .check(matches(not(withTextSize(10))));
        onView(withId(R.id.number_info)).inRoot(not(isDialog()))
                .check(matches(not(withTextSize(40))));

        // set text size value and click ok
        onView(withId(R.id.seek_bar))
                .inRoot(isDialog())
                .perform(setProgress(30));
        onView(withId(R.id.number_info)).inRoot(not(isDialog()))
                .check(matches(withTextSize(30)));

        onView(withText(R.string.ok))
                .inRoot(isDialog())
                .perform(click());

        // check text size setting
        pressBack();
        pressBack();

        openActionBarOverflowOrOptionsMenu(getTargetContext());
        onView(withText(R.string.action_float_window))
                .perform(click());

        onView(withId(R.id.number_info)).inRoot(
                withDecorView(not(is(mActivityRule.getActivity().getWindow().getDecorView()))))
                .check(matches(withTextSize(mSetting.getTextSize())));

    }

    @Test
    public void testWindowTextAlignmentSetting() {
        onView(withText(R.string.window_text_style)).perform(click());

        testWindowTextAlignmentSetting(0, FloatWindow.TEXT_ALIGN_LEFT);
        testWindowTextAlignmentSetting(1, FloatWindow.TEXT_ALIGN_CENTER);
        testWindowTextAlignmentSetting(2, FloatWindow.TEXT_ALIGN_RIGHT);

    }

    private void testWindowTextAlignmentSetting(int itemId, int alignment) {
        // open text alignment setting dialog
        onView(withText(R.string.window_text_alignment)).perform(click());

        onView(withId(R.id.radio))
                .inRoot(isDialog())
                .perform(checkRadioItem(itemId));
        // check text style alignment
        onView(withText(R.string.window_text_size)).perform(click());
        onView(withId(R.id.number_info)).inRoot(not(isDialog()))
                .check(matches(withTextAlign(alignment)));
        pressBack();
    }

    @Test
    public void testWindowTextPaddingSetting() {
        onView(withText(R.string.window_text_style)).perform(click());

        int align = mSetting.getTextAlignment();

        // open text padding setting dialog
        onView(withText(R.string.window_text_padding)).perform(click());

        onView(withId(R.id.seek_bar))
                .inRoot(isDialog())
                .perform(setProgress(10));

        onView(withId(R.id.window_layout)).inRoot(not(isDialog()))
                .check(matches(isDisplayed()));
        onView(withId(R.id.number_info)).inRoot(not(isDialog()))
                .check(matches(withText(R.string.text_padding)));

        // check preview window text padding
        onView(withId(R.id.number_info)).inRoot(not(isDialog()))
                .check(matches(withTextPadding(align, 10)));

        onView(withId(R.id.seek_bar))
                .inRoot(isDialog())
                .perform(setProgress(20));
        onView(withId(R.id.number_info)).inRoot(not(isDialog()))
                .check(matches(withTextPadding(align, 20)));

        onView(withId(R.id.seek_bar))
                .inRoot(isDialog())
                .perform(setProgress(40));
        onView(withId(R.id.number_info)).inRoot(not(isDialog()))
                .check(matches(withTextPadding(align, 40)));

        // cancel padding setting
        onView(withText(R.string.cancel))
                .inRoot(isDialog())
                .perform(click());

        // check text padding setting not performed
        onView(withText(R.string.window_text_padding)).perform(click());

        onView(withId(R.id.number_info)).inRoot(not(isDialog()))
                .check(matches(not(withTextPadding(align, 10))));
        onView(withId(R.id.number_info)).inRoot(not(isDialog()))
                .check(matches(not(withTextPadding(align, 20))));
        onView(withId(R.id.number_info)).inRoot(not(isDialog()))
                .check(matches(not(withTextPadding(align, 40))));

        // set text padding value and click ok
        onView(withId(R.id.seek_bar))
                .inRoot(isDialog())
                .perform(setProgress(30));
        onView(withId(R.id.number_info)).inRoot(not(isDialog()))
                .check(matches(withTextPadding(align, 30)));

        onView(withText(R.string.ok))
                .inRoot(isDialog())
                .perform(click());

        // check text padding in text size setting
        onView(withText(R.string.window_text_size)).perform(click());
        onView(withId(R.id.number_info)).inRoot(not(isDialog()))
                .check(matches(withTextPadding(align, 30)));
    }

    @Test
    public void testWindowTextColorSetting() {
        onView(withText(R.string.window_text_style)).perform(click());

        onView(withText(R.string.window_text_color)).perform(click());

        // TODO: test incoming window text color
    }

    @Test
    public void testWindowHeightSetting() {
        onView(withText(R.string.window_height)).perform(click());

        SystemClock.sleep(1000);

        onView(withId(R.id.window_layout)).inRoot(not(isDialog()))
                .check(matches(isDisplayed()));
        onView(withId(R.id.number_info)).inRoot(not(isDialog()))
                .check(matches(withText(R.string.window_height_message)));

        onView(withId(R.id.seek_bar))
                .inRoot(isDialog())
                .perform(setProgress(60));
        onView(withId(R.id.content)).inRoot(not(isDialog()))
                .check(matches(withHeight(60)));

        onView(withId(R.id.seek_bar))
                .inRoot(isDialog())
                .perform(setProgress(90));
        onView(withId(R.id.content)).inRoot(not(isDialog()))
                .check(matches(withHeight(90)));

        onView(withId(R.id.seek_bar))
                .inRoot(isDialog())
                .perform(setProgress(120));
        onView(withId(R.id.content)).inRoot(not(isDialog()))
                .check(matches(withHeight(120)));

        // cancel height setting
        onView(withText(R.string.cancel))
                .inRoot(isDialog())
                .perform(click());

        // check text height setting not performed
        onView(withText(R.string.window_transparent)).perform(click());
        onView(withId(R.id.content)).inRoot(not(isDialog()))
                .check(matches(not(withHeight(60))));
        onView(withId(R.id.content)).inRoot(not(isDialog()))
                .check(matches(not(withHeight(90))));
        onView(withId(R.id.content)).inRoot(not(isDialog()))
                .check(matches(not(withHeight(120))));

        pressBack();

        onView(withText(R.string.window_height)).perform(click());

        // set height setting and click ok
        onView(withId(R.id.seek_bar))
                .inRoot(isDialog())
                .perform(setProgress(110));
        onView(withId(R.id.content)).inRoot(not(isDialog()))
                .check(matches(withHeight(110)));
        onView(withText(R.string.ok))
                .inRoot(isDialog())
                .perform(click());

        onView(withText(R.string.window_transparent)).perform(click());
        onView(withId(R.id.content)).inRoot(not(isDialog()))
                .check(matches(withHeight(110)));
    }

    @Test
    public void testWindowTransparentSetting() {
        onView(withText(R.string.window_transparent)).perform(click());

        onView(withId(R.id.window_layout)).inRoot(not(isDialog()))
                .check(matches(isDisplayed()));
        onView(withId(R.id.number_info)).inRoot(not(isDialog()))
                .check(matches(withText(R.string.text_transparent)));

        onView(withId(R.id.seek_bar))
                .inRoot(isDialog())
                .perform(setProgress(60));
        onView(withId(R.id.content)).inRoot(not(isDialog()))
                .check(matches(withTransparency(60)));

        onView(withId(R.id.seek_bar))
                .inRoot(isDialog())
                .perform(setProgress(20));
        onView(withId(R.id.content)).inRoot(not(isDialog()))
                .check(matches(withTransparency(20)));

        onView(withId(R.id.seek_bar))
                .inRoot(isDialog())
                .perform(setProgress(90));
        onView(withId(R.id.content)).inRoot(not(isDialog()))
                .check(matches(withTransparency(90)));

        // cancel the change
        onView(withText(R.string.cancel))
                .inRoot(isDialog())
                .perform(click());

        // check window transparency
        onView(withText(R.string.window_height))
                .perform(click());
        onView(withId(R.id.content)).inRoot(not(isDialog()))
                .check(matches(not(withTransparency(60))));
        onView(withId(R.id.content)).inRoot(not(isDialog()))
                .check(matches(not(withTransparency(20))));
        onView(withId(R.id.content)).inRoot(not(isDialog()))
                .check(matches(not(withTransparency(90))));

        pressBack();

        // save the change
        onView(withText(R.string.window_transparent)).perform(click());
        onView(withId(R.id.seek_bar))
                .inRoot(isDialog())
                .perform(setProgress(50));
        onView(withId(R.id.content)).inRoot(not(isDialog()))
                .check(matches(withTransparency(50)));

        onView(withText(R.string.ok))
                .inRoot(isDialog())
                .perform(click());

        // check the transparency is performed
        onView(withText(R.string.window_height))
                .perform(click());
        onView(withId(R.id.content)).inRoot(not(isDialog()))
                .check(matches(withTransparency(50)));
    }

    @Test
    public void testWindowOutgoingVisibilitySetting() {
        onView(withText(R.string.window_visibility))
                .perform(click());

        // test outgoing window
        mIncomingCallListener.onCallStateChanged(TelephonyManager.CALL_STATE_OFFHOOK, "10086");
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

        onView(withText(R.string.display_on_outgoing)).perform(click());
        mIncomingCallListener.onCallStateChanged(TelephonyManager.CALL_STATE_OFFHOOK, "10086");
        onView(withId(R.id.window_layout)).inRoot(not(isDialog()))
                .check(matches(isDisplayed()));
        onView(withId(R.id.number_info)).inRoot(not(isDialog()))
                .check(matches(withText("??????????????????")));
        mIncomingCallListener.onCallStateChanged(TelephonyManager.CALL_STATE_IDLE, "10086");
    }

    @Test
    public void testWindowHideAfterAcceptSetting() {
        onView(withText(R.string.window_visibility))
                .perform(click());

        mIncomingCallListener.onCallStateChanged(TelephonyManager.CALL_STATE_RINGING, "10086");
        onView(withId(R.id.window_layout)).inRoot(not(isDialog()))
                .check(matches(isDisplayed()));
        onView(withId(R.id.number_info)).inRoot(not(isDialog()))
                .check(matches(withText("??????????????????")));
        mIncomingCallListener.onCallStateChanged(TelephonyManager.CALL_STATE_OFFHOOK, "10086");
        onView(withId(R.id.window_layout)).inRoot(not(isDialog()))
                .check(matches(isDisplayed()));
        onView(withId(R.id.number_info)).inRoot(not(isDialog()))
                .check(matches(withText("??????????????????")));
        mIncomingCallListener.onCallStateChanged(TelephonyManager.CALL_STATE_IDLE, "10086");

        onView(withText(R.string.hide_when_off_hook)).perform(click());

        mIncomingCallListener.onCallStateChanged(TelephonyManager.CALL_STATE_RINGING, "10086");
        onView(withId(R.id.window_layout)).inRoot(not(isDialog()))
                .check(matches(isDisplayed()));
        onView(withId(R.id.number_info)).inRoot(not(isDialog()))
                .check(matches(withText("??????????????????")));
        mIncomingCallListener.onCallStateChanged(TelephonyManager.CALL_STATE_OFFHOOK, "10086");
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
        mIncomingCallListener.onCallStateChanged(TelephonyManager.CALL_STATE_IDLE, "10086");
    }

    @Test
    public void testWindowHideAfterTapSetting() {
        onView(withText(R.string.window_visibility))
                .perform(click());

        mIncomingCallListener.onCallStateChanged(TelephonyManager.CALL_STATE_RINGING, "10086");
        onView(withId(R.id.window_layout)).inRoot(not(isDialog()))
                .check(matches(isDisplayed()));
        onView(withId(R.id.number_info)).inRoot(not(isDialog()))
                .check(matches(withText("??????????????????")));
        mIncomingCallListener.onCallStateChanged(TelephonyManager.CALL_STATE_OFFHOOK, "10086");
        onView(withId(R.id.window_layout)).inRoot(not(isDialog()))
                .check(matches(isDisplayed()));
        onView(withId(R.id.number_info)).inRoot(not(isDialog()))
                .check(matches(withText("??????????????????")));
        mIncomingCallListener.onCallStateChanged(TelephonyManager.CALL_STATE_IDLE, "10086");

        onView(withText(R.string.hide_when_touch)).perform(click());

        mIncomingCallListener.onCallStateChanged(TelephonyManager.CALL_STATE_RINGING, "10086");
        onView(withId(R.id.window_layout)).inRoot(not(isDialog()))
                .check(matches(isDisplayed()));
        onView(withId(R.id.number_info)).inRoot(not(isDialog()))
                .check(matches(withText("??????????????????")));
        mIncomingCallListener.onCallStateChanged(TelephonyManager.CALL_STATE_OFFHOOK, "10086");
        onView(withId(R.id.window_layout)).inRoot(not(isDialog()))
                .check(matches(isDisplayed()));
        onView(withId(R.id.number_info)).inRoot(not(isDialog()))
                .check(matches(withText("??????????????????")));

        // click outside the window
        mDevice.click(500, 500);
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

        mIncomingCallListener.onCallStateChanged(TelephonyManager.CALL_STATE_IDLE, "10086");
    }

    @Test
    public void testWindowCloseAnimationSetting() {
        // do nothing
    }

    @Test
    public void testWindowNotificationSetting() throws UiObjectNotFoundException {
        onView(withText(R.string.window_visibility))
                .perform(click());

        mIncomingCallListener.onCallStateChanged(TelephonyManager.CALL_STATE_RINGING, "10086");
        onView(withId(R.id.window_layout)).inRoot(not(isDialog()))
                .check(matches(isDisplayed()));
        onView(withId(R.id.number_info)).inRoot(not(isDialog()))
                .check(matches(withText("??????????????????")));

        mDevice.openNotification();
        mDevice.wait(Until.hasObject(By.pkg("com.android.systemui")), 10000);
        UiSelector notificationStackScroller = new UiSelector().packageName("com.android.systemui")
                .className("android.view.ViewGroup")
                .resourceId("com.android.systemui:id/notification_stack_scroller");

        UiObject notificationStackScrollerUiObject = mDevice.findObject(notificationStackScroller);
        assertTrue(notificationStackScrollerUiObject.exists());

        String text = getTargetContext().getString(R.string.app_name);
        UiObject notify = notificationStackScrollerUiObject.getChild(new UiSelector().text(text));
        assertTrue(notify.exists());
        mIncomingCallListener.onCallStateChanged(TelephonyManager.CALL_STATE_IDLE, "10086");

        mDevice.pressBack();

        // disable notification
        onView(withText(R.string.disable_notify)).perform(click());
        mIncomingCallListener.onCallStateChanged(TelephonyManager.CALL_STATE_RINGING, "10086");
        onView(withId(R.id.window_layout)).inRoot(not(isDialog()))
                .check(matches(isDisplayed()));
        onView(withId(R.id.number_info)).inRoot(not(isDialog()))
                .check(matches(withText("??????????????????")));

        // check notification not exist
        mDevice.openNotification();
        mDevice.wait(Until.hasObject(By.pkg("com.android.systemui")), 10000);
        notificationStackScrollerUiObject = mDevice.findObject(notificationStackScroller);
        assertTrue(notificationStackScrollerUiObject.exists());
        notify = notificationStackScrollerUiObject.getChild(new UiSelector().text(text));
        assertFalse(notify.exists());
        mIncomingCallListener.onCallStateChanged(TelephonyManager.CALL_STATE_IDLE, "10086");
    }

    @Test
    public void testWindowMoveSetting() {
        onView(withText(R.string.window_visibility))
                .perform(click());

        mIncomingCallListener.onCallStateChanged(TelephonyManager.CALL_STATE_RINGING, "10086");
        onView(withId(R.id.window_layout)).inRoot(not(isDialog()))
                .check(matches(isDisplayed()));
        onView(withId(R.id.number_info)).inRoot(not(isDialog()))
                .check(matches(withText("??????????????????")));

        // test movement
        int oldY = mSetting.getWindowY();

        onView(withId(R.id.number_info))
                .inRoot(not(isDialog()))
                .perform(swipeUp());
        onView(withId(R.id.window_layout))
                .inRoot(not(isDialog()))
                .check(matches(isWindowAtPosition(mSetting.getWindowX(), mSetting.getWindowY())));

        // window position is changed.
        assertTrue(oldY != mSetting.getWindowY());

        mIncomingCallListener.onCallStateChanged(TelephonyManager.CALL_STATE_IDLE, "10086");

        oldY = mSetting.getWindowY();

        // disable move
        onView(withText(R.string.disable_move)).perform(click());

        mIncomingCallListener.onCallStateChanged(TelephonyManager.CALL_STATE_RINGING, "10086");

        onView(withId(R.id.number_info))
                .inRoot(not(isDialog()))
                .perform(swipeDown());
        onView(withId(R.id.window_layout))
                .inRoot(not(isDialog()))
                .check(matches(isWindowAtPosition(mSetting.getWindowX(), mSetting.getWindowY())));

        // window position is not changed.
        assertTrue(oldY == mSetting.getWindowY());

        mIncomingCallListener.onCallStateChanged(TelephonyManager.CALL_STATE_IDLE, "10086");
    }

}
