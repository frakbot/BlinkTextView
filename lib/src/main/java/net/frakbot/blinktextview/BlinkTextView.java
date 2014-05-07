/*
 * Copyright 2014 Frakbot (Sebastiano Poggi and Francesco Pontillo)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.frakbot.blinktextview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;
import com.google.gag.annotation.disclaimer.SafeForSpeedsNotExceeding;
import com.google.gag.annotation.enforceable.CantTouchThis;
import com.google.gag.annotation.remark.PleaseDontShowUpOn;
import com.google.gag.annotation.remark.WTF;
import com.google.gag.annotation.team.Channeling;
import com.google.gag.enumeration.*;

/**
 * Blink like it's the 90s all over again.
 */
public class BlinkTextView extends TextView {

    private static final String TAG = BlinkTextView.class.getSimpleName();

    /**
     * The blink period. This has been scientifically calculated as the
     * most effective (read, annoying) blinking period during years of
     * testing. Since it's super effective, you won't need to be able to
     * change it. And you can't. It's 987 ms. Period. (pun intended)
     * <p/><p/>
     * <h2>The "science, bitch!" corner</h2> <br/>
     * Why is 987 ms so scientifically and disturbingly annoying? Why 987
     * and not, say, 986? Why not 988? Why do you keep asking questions?
     * There's an answer to <i>at least</i> one of those questions!
     * <p/>
     * Which unfortunately isn't why you keep asking annoying questions.
     * We don't know. You probably are stupid. Or a verbal sadist. If that
     * is even a thing. Which it may or might not be. But I digress.
     * <p/>
     * So, why 987? Well, ~500 ms would be better, but it annoys me to the
     * point where I don't even want to check if the app builds. So, 1 s
     * is more acceptable. But 1 s is too simple. Too perfect.
     * <p/>
     * Several batches of tests have shown that people with mild forms of
     * OCD would feel comfortable with a 1 s blink period (severe OCD
     * would kick in as soon as the damn thing begins to blink).
     * <p/>
     * So we trimmed a few millis at a time and checked how many people
     * would still OCD. Turns out, after you go below the 990 ms threshold
     * people begins noticing there's something wrong going on. And that
     * annoys them. Which is fundamental in simulating &lt;blink/&gt;.
     * <p/>
     * Plus, 987 is a convenient streak of adjacent keys on the keyboard.
     * <p/>
     * Ok, that is actually the whole reason.
     *
     * @see #BLINK_VARIANCE
     */
    public static final int BLINK_PERIOD = 987;  // ms

    /**
     * The blink variance multiplier. This is only used when the View
     * is in OCD mode. It will make your blink ever blinkier.
     * <p/>
     * Yes, by blinkier we mean more annoying.
     *
     * @see #setOcdModeEnabled(boolean)
     */
    public static final float BLINK_VARIANCE = .8f;  // multiplier

    protected Handler mBlinkMachine;
    private boolean mShouldActuallyDoAnything;
    private boolean mErmShouldIDraw;
    private Blinker mBlinkyThing;

    private boolean mOcdMode;

    public BlinkTextView(Context context) {
        super(context);
        init(null);
    }

    public BlinkTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public BlinkTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    @SafeForSpeedsNotExceeding(value = 42, units = SpeedUnits.TWIPS_PER_JIFFY)
    private void init(AttributeSet attrs) {
        mBlinkMachine = new Handler();
        mBlinkyThing = new Blinker();

        final Context context = getContext();
        if (attrs != null && context != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BlinkTextView);

            final int N;
            if (a != null) {
                N = a.getIndexCount();

                for (int i = 0; i < N; ++i) {
                    int attr = a.getIndex(i);
                    switch (attr) {
                        case R.styleable.BlinkTextView_ocdMode:
                            setOcdModeEnabled(a.getBoolean(attr, false));
                            break;
                    }
                }

                a.recycle();
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (BuildConfig.DEBUG) Log.d(TAG, "Hello this is dog.");

        if (!mShouldActuallyDoAnything) {
            Log.i(TAG, "LOL I have no idea what I'm doing");

            // Lazily begin doing things. No hurry, right?
            mShouldActuallyDoAnything = true;
            youBetterBlinkNAO();
            return;
        }

        if (mErmShouldIDraw || isInEditMode()) {
            super.onDraw(canvas);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        youBetterBlinkNAO();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        stahpBinkingPlease();
    }

    @PleaseDontShowUpOn(Website.WIKIPEDIA)
    public void youBetterBlinkNAO() {
        if (!mShouldActuallyDoAnything) {
            return;
        }

        final int period = pissOffUsersIfNeeded(BLINK_PERIOD);
        mBlinkMachine.postDelayed(mBlinkyThing, period);
    }

    @CantTouchThis(Stop.HAMMERTIME)
    public void stahpBinkingPlease() {
        if (!mShouldActuallyDoAnything) {
            return;
        }

        noSeriouslyStahpIt();
    }

    /**
     * Gets a value indicating if OCD mode is enabled.
     *
     * @return Returns ture if O.C.D mode  is active or
     * false if othewrise. (LOL
     */
    public boolean isOcdModeEnabled() {
        return mOcdMode;
    }

    /**
     * Enables or disables OCD mode.
     * <p/>
     * OCD mode randomly varies the blink interval to
     * make users appreciate even more your taste for
     * all things blinking.
     * <p/>
     * The blinking interval is varied at most by the
     * {@link #BLINK_VARIANCE} factor.
     *
     * @param ocdMode If true, the OCD mode is enabled.
     *                If false, it's disabled. Duh!
     *
     * @see #BLINK_VARIANCE
     */
    @Channeling(person = "Chtulu",
                entity = ChannelingEntity.OLD_ONE,
                disposition = OpinionOfHumanity.COMMITTED_TO_THE_EVENTUAL_DESTRUCTION_OF)
    public void setOcdModeEnabled(boolean ocdMode) {
        mOcdMode = ocdMode;
    }

    @WTF
    private void noSeriouslyStahpIt() {
        goHomeBlinkYoureDrunk();
    }

    private void goHomeBlinkYoureDrunk() {
        mBlinkMachine.removeCallbacks(mBlinkyThing);
    }

    private int pissOffUsersIfNeeded(int baseBlinkPeriod) {
        if (!mOcdMode) {
            return baseBlinkPeriod;
        }

        int variance = (int) (baseBlinkPeriod * BLINK_VARIANCE * (Math.random() - .5f));
        if (BuildConfig.DEBUG) Log.v(TAG, "Variance for this pass: " + variance);
        return baseBlinkPeriod + variance;
    }

    /**
     * Blink on you crazy diamond.
     */
    private class Blinker implements Runnable {

        @Override
        public void run() {
            mErmShouldIDraw = !mErmShouldIDraw;
            invalidate();

            // Reschedule this awesome runnable.
            youBetterBlinkNAO();
        }
    }
}
