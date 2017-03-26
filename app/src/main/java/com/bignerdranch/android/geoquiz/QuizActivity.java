package com.bignerdranch.android.geoquiz;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class QuizActivity extends AppCompatActivity {

    private Button mTrueButton;
    private Button mFalseButton;
    private Button mCheatButton;
    private Button mResetButton;
    private ImageButton mNextButton;
    private ImageButton mPrevButton;
    private TextView mQuestionTextView;
    private Question[] mQuestionBank = new Question [] {
            new Question(R.string.question_australia, true, false),
            new Question(R.string.question_oceans, true, false),
            new Question(R.string.question_mideast, false, false),
            new Question(R.string.question_africa, false, false),
            new Question(R.string.question_americas, true, false),
            new Question(R.string.question_asia, true, false),
    };
    private int mCurrentIndex = 0;
    private boolean mIsCheater;
    private double mTotalScore = 0;
    private int mCheatCount = 0;
    private static final String TAG = "QuizActivity";
    private static final String KEY_INDEX = "index";
    private static final String KEY_CHEATERCOUNT = "cheaterCount";
    private static final String KEY_ISCHEATER = "isCheater";
    private static final int REQUEST_CODE_CHEAT = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bundle) called");

        setContentView(R.layout.activity_quiz);

        // attach question text view
        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);
        // set click listener
        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNextButton.performClick();
            }
        });
        // attach true button
        mTrueButton = (Button) findViewById(R.id.true_button);
        // set click listener
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(true);
            }
        });
        // attach false button
        mFalseButton = (Button) findViewById(R.id.false_button);
        // set click listener
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(false);
            }
        });
        // attach next button
        mNextButton = (ImageButton) findViewById(R.id.next_button);
        // set click event
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // increment current index
                mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
                // set cheater
                mIsCheater = false;
                // set next question
                updateQuestion();
            }
        });
        // attach last question
        mPrevButton = (ImageButton) findViewById(R.id.prev_button);
        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // decrement current index
                mCurrentIndex = (mCurrentIndex + mQuestionBank.length - 1) % mQuestionBank.length;
                // set cheater
                mIsCheater = false;
                // set last question
                updateQuestion();
            }
        });
        // attach cheat button
        mCheatButton = (Button) findViewById(R.id.cheat_button);
        // set click event
        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = CheatActivity.newIntent(QuizActivity.this, mQuestionBank[mCurrentIndex].isAnswerTrue());
                startActivityForResult(intent, REQUEST_CODE_CHEAT);
                if(mCheatCount < 2)
                    mCheatButton.setEnabled(true);
                else
                    mCheatButton.setEnabled(false);
            }
        });
        // attach reset button
        mResetButton = (Button) findViewById(R.id.reset_button);
        // set click event
        mResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetGame();
            }
        });
        // get saved values
        if(savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX, 0);
            mIsCheater = savedInstanceState.getBoolean(KEY_ISCHEATER, false);
            mCheatCount = savedInstanceState.getInt(KEY_CHEATERCOUNT, 0);
            if(mCheatCount < 2)
                mCheatButton.setEnabled(true);
        }
        // set first question
        updateQuestion();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != Activity.RESULT_OK)
            return;
        if(requestCode == REQUEST_CODE_CHEAT)
            if(data == null)
                return;
        mIsCheater = CheatActivity.wasAnswerShown(data);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
    }
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }
    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");}
    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }

    /**
     * Save instance state.
     *
     * @param savedInstanceState
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "onSaveInstanceState");
        // save index
        savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);
        // save cheat user
        savedInstanceState.putBoolean(KEY_ISCHEATER, mIsCheater);
        // save cheat count
        savedInstanceState.putInt(KEY_CHEATERCOUNT, mCheatCount);
    }

    /**
     * Update question.
     */
    private void updateQuestion() {
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        mQuestionTextView.setText(question);
        setAnswerBtnState(!mQuestionBank[mCurrentIndex].isAnswered());
    }

    /**
     * Check answer.
     */
    private void checkAnswer(boolean userPressedTrue) {
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
        int messageResId = 0;
        // is cheater?
        if(mIsCheater) {
            mCheatCount++;
            messageResId = R.string.judgment_toast;
        } else {
            // check answer
            if (userPressedTrue == answerIsTrue) {
                messageResId = R.string.correct_toast;
                // get total score
                mTotalScore += (100 / mQuestionBank.length);
            } else
                messageResId = R.string.incorrect_toast;
        }

        // show answer
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();
        // disable buttons
        setAnswerBtnState(false);
        // set question as answered
        mQuestionBank[mCurrentIndex].setAnswered(true);
        // get total score
        getScore();
    }

    /**
     * If all questions are answered get total score.
     */
    private void getScore() {
        int questNumAnswered = 0;
        for(int i = 0; i < mQuestionBank.length; i++) {
            if(mQuestionBank[i].isAnswered()) {
                questNumAnswered++;
                if(mQuestionBank.length == questNumAnswered) {
                    String score = "Score: " + mTotalScore + " %";
                    Toast.makeText(this, score, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * Set answer button state.
     *
     * @param state
     */
    private void setAnswerBtnState(boolean state) {
        mTrueButton.setEnabled(state);
        mFalseButton.setEnabled(state);
    }

    /**
     * Reset question game.
     */
    private void resetGame() {
        // set current index to beginning
        mCurrentIndex = 0;
        // set total score to zero
        mTotalScore = 0;
        // set all questions answered
        for(int i = 0; i < mQuestionBank.length; i++)
            mQuestionBank[i].setAnswered(false);
        // update question
        updateQuestion();
    }
}
