package com.bignerdranch.android.geoquiz;

import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewAnimationUtils;
import android.animation.Animator;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CheatActivity extends AppCompatActivity {

    private TextView mAnswerTextView;
    private TextView mApiLevelTextView;
    private Button mShowAnswerBtn;
    private boolean mAnswerIsTrue;
    private static final String EXTRA_ANSWER_IS_TRUE = "com.bignerdranch.android.geoquiz.answer_is_true";
    private static final String EXTRA_ANSWER_SHOWN = "com.bignerdranch.android.geoquiz.answer_shown";

    /**
     * Create a new intent
     *
     * @param packageContext
     * @param answerIsTrue
     * @return
     */
    public static Intent newIntent(Context packageContext, boolean answerIsTrue) {
        Intent intent = new Intent(packageContext, CheatActivity.class);
        intent.putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue);
        return intent;
    }

    /**
     * Set was answer shown.
     *
     * @param result
     * @return
     */
    public static boolean wasAnswerShown(Intent result) {
        return result.getBooleanExtra(EXTRA_ANSWER_SHOWN, false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat);
        // set api level text view
        mApiLevelTextView = (TextView) findViewById(R.id.api_level);
        // set api level
        setApiLevel();
        // get intent
        mAnswerIsTrue = getIntent().getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false);
        // set answer text view
        mAnswerTextView = (TextView) findViewById(R.id.answer_text_view);
        // show answer button
        mShowAnswerBtn = (Button) findViewById(R.id.show_answer_button);
        // set click event
        mShowAnswerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAnswerIsTrue)
                    mAnswerTextView.setText(R.string.true_button);
                else
                    mAnswerTextView.setText(R.string.false_button);
                setAnswerShownResult(true);
                // animate show answer button
                animateShowAnswerBtn();
            }
        });

    }

    /**
     * Set answer show result.
     *
     * @param isAnswerShown
     */
    private void setAnswerShownResult(boolean isAnswerShown) {
        Intent data = new Intent();
        data.putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown);
        setResult(RESULT_OK, data);
    }

    /**
     * Animate show answer button.
     */
    private void animateShowAnswerBtn() {
        if(Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.LOLLIPOP) {
            int cx = mShowAnswerBtn.getWidth() / 2;
            int cy = mShowAnswerBtn.getHeight() / 2;
            float radius = mShowAnswerBtn.getWidth();
            Animator anim = ViewAnimationUtils.createCircularReveal(mShowAnswerBtn, cx, cy, radius, 0);
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    mShowAnswerBtn.setVisibility(View.INVISIBLE);
                }
            });
            anim.start();
        } else
            mShowAnswerBtn.setVisibility(View.INVISIBLE);
    }

    /**
     * Show API Level.
     */
    private void setApiLevel() {
        int apiLevel = Integer.valueOf(Build.VERSION.SDK_INT);
        mApiLevelTextView.setText("API Level " + apiLevel);
    }
}
