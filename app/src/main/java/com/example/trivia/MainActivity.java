package com.example.trivia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Delayed;

import Data.AnswerListAsynResponse;
import Data.QuestionBank;
import model.Question;
import model.Score;
import util.Prefs;

import static java.lang.Thread.interrupted;
import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView questionTextView;
    private TextView questionCounterTextView;
    private Button trueButton;
    private Button falseButton;
    private ImageButton nextButton;
    private ImageButton prevButton;
    private int currentQuestionIndex;
    private List<Question> questionList;
    private TextView highScore;
    private TextView yourScore;
    private Button shareButton;

    private int scoreCounter = 0;
    private Score score;
    private Prefs prefs;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        score = new Score();
        prefs = new Prefs(MainActivity.this);

        nextButton = findViewById(R.id.next_button);
        prevButton = findViewById(R.id.prev_button);
        trueButton = findViewById(R.id.true_button);
        falseButton = findViewById(R.id.false_button);
        questionTextView = findViewById(R.id.question_textview);
        questionCounterTextView = findViewById(R.id.counter_text);
         highScore = findViewById(R.id.HIGH_SCORE);
         yourScore = findViewById(R.id.YOUR_SCORE);
         shareButton = findViewById(R.id.shareButton);

       nextButton.setOnClickListener(this);
       prevButton.setOnClickListener(this);
       trueButton.setOnClickListener(this);
        falseButton.setOnClickListener(this);

        highScore.setText("Highest Score : " + prefs.getHighestScore());
        currentQuestionIndex = prefs.getState();

        questionList= new QuestionBank().getQuestion(new AnswerListAsynResponse() {
            @SuppressLint("SetTextI18n")
            @Override
            public void processFinished(ArrayList<Question> questionArrayList) {
                questionTextView.setText(questionArrayList.get(currentQuestionIndex).getAnswer());
                questionCounterTextView.setText(currentQuestionIndex + "/" +questionArrayList.size());
            }
        });

       shareButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(Intent.ACTION_SEND);
               intent.setType("text/plain");
               intent.putExtra(Intent.EXTRA_SUBJECT, "Hey I am Playing TRIVIA");
               if(score.getScore() > prefs.getHighestScore()) {
                   intent.putExtra(Intent.EXTRA_TEXT, "My Score is : " + score.getScore() + "\n" + " My Highest Score is : " + score.getScore());
               }else {
                   intent.putExtra(Intent.EXTRA_TEXT, "My Score is : " + score.getScore() + "\n" + " My Highest Score is : " + prefs.getHighestScore());
               }
               startActivity(intent);
           }
       });
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.prev_button:
                if(currentQuestionIndex == 0){}
                else {
                    currentQuestionIndex = (currentQuestionIndex - 1) % questionList.size();
                    questionCounterTextView.setText(currentQuestionIndex + "/" +questionList.size());
                    questionTextView.setText(questionList.get(currentQuestionIndex).getAnswer());
                } break;

            case R.id.next_button:
                currentQuestionIndex = (currentQuestionIndex + 1) % questionList.size();
                questionCounterTextView.setText(currentQuestionIndex + "/" +questionList.size());
                questionTextView.setText(questionList.get(currentQuestionIndex).getAnswer());
                break;

            case R.id.true_button:
                if(questionList.get(currentQuestionIndex).getAnswerTrue()){
                    Toast.makeText(MainActivity.this,"Correct",Toast.LENGTH_SHORT).show();
                    addPoints();
                    fadeView();
                }else{
                    Toast.makeText(MainActivity.this,"Wrong!!",Toast.LENGTH_SHORT).show();
                    shakeAnimation();
                    deductPoints();
                    questionTextView.setText(questionList.get(currentQuestionIndex).getAnswer());
                }
                break;

            case R.id.false_button:
                if(!questionList.get(currentQuestionIndex).getAnswerTrue()){
                    Toast.makeText(MainActivity.this,"Correct",Toast.LENGTH_SHORT).show();
                    addPoints();
                    fadeView();
                }else{
                    Toast.makeText(MainActivity.this,"Wrong!!",Toast.LENGTH_SHORT).show();
                    shakeAnimation();
                    deductPoints();
                    questionTextView.setText(questionList.get(currentQuestionIndex).getAnswer());
                }
                break;
            default:
        }
    }

     private void fadeView(){
        final CardView cardView = findViewById(R.id.cardView);

        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f , 0.0f);

        alphaAnimation.setDuration(350);
        alphaAnimation.setRepeatCount(1);
        alphaAnimation.setRepeatMode(Animation.REVERSE);
        cardView.setAnimation(alphaAnimation);

        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                cardView.setCardBackgroundColor(Color.GREEN);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.setCardBackgroundColor(Color.WHITE);
                updateQues();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

     private void shakeAnimation() {
          Animation shake = AnimationUtils.loadAnimation(MainActivity.this,R.anim.shake_animation);
          final CardView cardView = findViewById(R.id.cardView);
          cardView.setAnimation(shake);

          shake.setAnimationListener(new Animation.AnimationListener() {
              @Override
              public void onAnimationStart(Animation animation) {
                  cardView.setCardBackgroundColor(Color.RED);
              }

              @Override
              public void onAnimationEnd(Animation animation) {
                 cardView.setCardBackgroundColor(Color.WHITE);
                 updateQues();
              }

              @Override
              public void onAnimationRepeat(Animation animation) {
              }
          });
      }

     @SuppressLint("SetTextI18n")
     private void addPoints() {
        scoreCounter += 100;
        score.setScore(scoreCounter);
        yourScore.setText("Score : " + score.getScore());
      }

    @SuppressLint("SetTextI18n")
    private void deductPoints() {
        if(scoreCounter > 0) {
            scoreCounter -= 100;
            score.setScore(scoreCounter);
            yourScore.setText("Score : " + score.getScore());
        }
    }

      private void updateQues() {
          currentQuestionIndex = (currentQuestionIndex + 1) % questionList.size();
          questionCounterTextView.setText(currentQuestionIndex + "/" + questionList.size());
          questionTextView.setText(questionList.get(currentQuestionIndex).getAnswer());
      }

    @Override
    protected void onPause() {
        super.onPause();
    prefs.saveHighestScore(score.getScore());
    prefs.setState(currentQuestionIndex);
    }

}
