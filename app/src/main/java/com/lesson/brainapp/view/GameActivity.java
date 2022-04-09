package com.lesson.brainapp.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lesson.brainapp.R;
import com.lesson.brainapp.model.GameProcess;
import com.lesson.brainapp.model.Test.Test;
import com.lesson.brainapp.model.Test.TestException.TestException;

import java.util.Locale;

public class GameActivity extends AppCompatActivity {

    private SharedPreferences preferences;
    private TextView textViewRecord;
    private TextView textViewRight;
    private TextView textViewWrong;
    private TextView textViewTime;
    private TextView textViewExpression;


    private Button buttonStart;
    private Button[] vButtons;

    private Timer timer;
    private GameProcess game;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        textViewRecord = findViewById(R.id.textViewStar);
        textViewRight = findViewById(R.id.textViewRight);
        textViewWrong = findViewById(R.id.textViewWrong);
        textViewTime = findViewById(R.id.textView_time);
        textViewExpression = findViewById(R.id.textView_expression);


        Button button0 = findViewById(R.id.btn0);
        Button button1 = findViewById(R.id.btn1);
        Button button2 = findViewById(R.id.btn2);
        Button button3 = findViewById(R.id.btn3);
        buttonStart = findViewById(R.id.btn_start);
        vButtons = new Button[]{button0, button1, button2, button3};


        buttonStart.setOnClickListener(this::buttonStartListener);
        button0.setOnClickListener(this::buttonsVariantListener);
        button1.setOnClickListener(this::buttonsVariantListener);
        button2.setOnClickListener(this::buttonsVariantListener);
        button3.setOnClickListener(this::buttonsVariantListener);

        checkState(savedInstanceState);

        setVisibilityToStars();
        textViewRecord.setText(String.valueOf(game.getRecord()));
        textViewRight.setText(String.valueOf(game.getRight()));
        textViewWrong.setText(String.valueOf(game.getWrong()));
    }

    private void checkState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            try {
                game = savedInstanceState.getParcelable("game");
                long time = savedInstanceState.getLong("time");
                if (game != null && time != 0L) {
                    timer = new Timer(time, 1000);
                    for (int i = 0; i < vButtons.length; i++) {
                        vButtons[i].setText(String.valueOf(game.getVariant(i)));
                    }
                    textViewExpression.setText(game.getExpression());
                    buttonStart.setVisibility(View.GONE);
                    timer.start();
                } else {
                    setUpLevel();
                }
            } catch (TestException e) {
                setUpLevel();
            }
        } else {
            setUpLevel();
        }
    }


    private void setUpLevel() {
        game = new GameProcess();
        Intent intent = getIntent();
        String lvlRecordNameForPreference = intent.getStringExtra("levelRecordName");
        lvlRecordNameForPreference = (lvlRecordNameForPreference == null) ? "recordMiddle" : lvlRecordNameForPreference;
        game.setLvlRecordNameForPreference(lvlRecordNameForPreference);
        int level = intent.getIntExtra("level", Test.MIDDLE);
        int record = preferences.getInt(lvlRecordNameForPreference, 0);
        game.setLevel(level);
        game.setRecordLevel(record);
    }

    private void setVisibilityToStars() {
        ImageView imageViewStarMiddle = findViewById(R.id.imageViewStar2);
        ImageView imageViewStarHigh = findViewById(R.id.imageViewStar3);
        if (game.getLevel() == Test.LOW) {
            imageViewStarMiddle.setVisibility(View.GONE);
            imageViewStarHigh.setVisibility(View.GONE);
        } else if (game.getLevel() == Test.MIDDLE) {
            imageViewStarHigh.setVisibility(View.GONE);
        }
    }

    private void buttonStartListener(View view) {
        game.run();
        generateStartNewTest();
        buttonStart.setVisibility(View.GONE);
    }

    private void buttonsVariantListener(View view) {
        if (game.isRun()) {
            timer.cancel();
            int answer = Integer.parseInt(String.valueOf(view.getTag()));
            boolean isNotRight = !game.answerProcessing(answer);
            if (isNotRight) {
                MediaPlayer mp = MediaPlayer.create(this, R.raw.incorrect);
                mp.start();
            }
            textViewRight.setText(String.valueOf(game.getRight()));
            textViewWrong.setText(String.valueOf(game.getWrong()));
            textViewRecord.setText(String.valueOf(game.getRecord()));
            generateStartNewTest();
        }
    }

    private void generateStartNewTest() {
        try {
            game.generateTest();
            for (int i = 0; i < vButtons.length; i++) {
                vButtons[i].setText(String.valueOf(game.getVariant(i)));
            }
            textViewExpression.setText(game.getExpression());
            timer = new GameActivity.Timer(game.getAvailableTime(), 1000);
            timer.start();
        } catch (TestException e) {
            e.printStackTrace();
            Toast.makeText(this, "Problem with variant generation", Toast.LENGTH_SHORT).show();
            buttonStart.setVisibility(View.VISIBLE);
            game.finishTheTest();
        }
    }


    public class Timer extends CountDownTimer {

        private long timeUntill;

        public Timer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            timeUntill = millisUntilFinished;
            long seconds = millisUntilFinished / 1000;
            game.decreaseTime(1000);
            textViewTime.setText(String.format(Locale.getDefault(), ":%02d", seconds));
            Log.d("it0088", "onTick: "+millisUntilFinished);
        }

        @Override
        public void onFinish() {
            finishTheTest();
        }

        public long getTimeUntill() {
            return timeUntill;
        }

    }

    private void finishTheTest() {
        timer = null;
        buttonStart.setVisibility(View.VISIBLE);
        game.finishTheTest();
        checkRecordInPreferences();
    }

    private void checkRecordInPreferences() {
        int oldRecord = preferences.getInt(game.getLvlRecordNameForPreference(), 0);
        if (game.isNewRecordGreater(oldRecord)) {
            int record = game.getRecord();
            preferences.edit().putInt(game.getLvlRecordNameForPreference(), record).apply();
            congrate(record);
        }
    }

    private void congrate(int record) {
        MediaPlayer mp = MediaPlayer.create(this, R.raw.record);
        mp.start();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.record, null);
        setViewData(view, record);
        builder.setView(view);
        builder.create().show();
    }

    private void setViewData(View view, int record) {
        ImageView star1 = view.findViewById(R.id.star1);
        ImageView star2 = view.findViewById(R.id.star2);
        ImageView star3 = view.findViewById(R.id.star3);

        TextView newRecord = view.findViewById(R.id.textView_newRecord);
        TextView victoryScore = view.findViewById(R.id.textView_victory_score);

        int level = game.getLevel();
        int score = 0;
        if (level == Test.LOW) {
            star2.setVisibility(View.GONE);
            star3.setVisibility(View.GONE);
            score = 100;
        } else if (level == Test.MIDDLE) {
            star2.setVisibility(View.GONE);
            score = 50;
        } else if (level == Test.HIGH) {
            score = 25;
        }

        newRecord.setText(getText(R.string.congratenewrecord).toString().concat(" " + String.valueOf(record)));
        if (record >= score) {
            victoryScore.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            victoryScore.setText(getText(R.string.victory));
        } else {
            victoryScore.setText(getText(R.string.victory_score).toString().concat(" " + String.valueOf(score)));
        }

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (game.isRun()) {
            outState.putParcelable("game", game);
            outState.putLong("time", timer.getTimeUntill());
            timer.cancel();
        }

    }

}