package com.firstprojects.catchtheobject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import com.firstprojects.catchtheobject.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

   private Handler handler;
   private Runnable runnable;
   private ActivityMainBinding binding;

    private boolean isGameStarted = false;

    private final ArrayList<Integer> backgrounds = new ArrayList<>();

    private int selectedBackgroundNumber = 0;

    private void changeBackground(int index){
        binding.backgroundOfActivity.setBackgroundResource(backgrounds.get(index));
    }

    private int score = 0;

    private int targetWidth;
    private int targetHeight;

    private float targetX = 0.0f;
    private float targetY = 0.0f;

    private final float gravity = 1f;
    private float velocity = 0.0f;
    private float horizontalVelocity = 1f;

    private Directions currentDirection;

    private enum Directions{
        UP_LEFT,
        UP_RIGHT,
        DOWN_LEFT,
        DOWN_RIGHT,
        LEFT,
        RIGHT,
    }

   private final DisplayMetrics metrics = new DisplayMetrics();

   private int getScreenHeight = 0;
   private int getScreenWidth = 0;

   private final Random random = new Random();

    //GET RANDOM NUMBER FROM ONE YOU WANT TO ONE YOU WANT
    private int getRandom(int from , int to){
        int num = random.nextInt(to);
        while(num < from){
            num = random.nextInt(to);
        }
        return num;
    }

    //GET THE CURRENT DIRECTION AT THE MOMENT
    private Directions getCurrentDirection() {

        if(velocity > 0 && horizontalVelocity >= 0){
                binding.movingObject.setImageDrawable( ContextCompat.getDrawable(this,R.drawable.left_up)); //***
            return Directions.UP_RIGHT;
        }else if(velocity < 0 && horizontalVelocity >= 0){
                binding.movingObject.setImageDrawable( ContextCompat.getDrawable(this,R.drawable.right_down)); //***
            return Directions.DOWN_RIGHT;
        }else if(velocity < 0 && horizontalVelocity <= 0){
                binding.movingObject.setImageDrawable( ContextCompat.getDrawable(this,R.drawable.left_down)); //***
            return Directions.DOWN_LEFT;
        }else if(velocity > 0 && horizontalVelocity <= 0){
                //THIS IS JUST ON THE LEFT DIRECTION.!!
                binding.movingObject.setImageDrawable( ContextCompat.getDrawable(this,R.drawable.right_up)); //***
            return Directions.UP_LEFT;
        }else{
            //velocity == 0
            if(horizontalVelocity <= 0){
                binding.movingObject.setImageDrawable( ContextCompat.getDrawable(this,R.drawable.left));
                return Directions.LEFT;
            }else{
                binding.movingObject.setImageDrawable( ContextCompat.getDrawable(this,R.drawable.right));
                return Directions.RIGHT;
            }
        }

    }

    private void uponTouchingCeilingOrDownGetNewDirection() {
        if(random.nextBoolean()){
            horizontalVelocity = getRandom(0,getScreenHeight/100);
        }else{
            horizontalVelocity = - getRandom(0,getScreenHeight/100);
        }
    }

    private void gameEngine(){
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {

                    currentDirection = getCurrentDirection();

                if(targetY < getScreenHeight && targetY > 0){

                    if(targetX >= getScreenWidth - targetWidth || targetX <= 0){

                        if(targetX >= getScreenWidth - targetWidth){

                            //this is where I adjust the location of the object.
                            binding.movingObject.setX(getScreenWidth - targetWidth - 1);
                            horizontalVelocity = getRandom(getScreenHeight/300,getScreenHeight/100);
                        }else if(targetX < 0) {

                            //this is where I adjust the location of the object.
                            horizontalVelocity = - getRandom(getScreenHeight/300,getScreenHeight/100);
                            binding.movingObject.setX(1);
                        }
                    }
                }else{
                    if(targetY > getScreenHeight){


                        binding.movingObject.setImageDrawable(ContextCompat.getDrawable(MainActivity.this,R.drawable.right_up));

                        binding.movingObject.setY(getScreenHeight - 1f);
                        velocity = getRandom(getScreenHeight/50,getScreenHeight/35);

                    }else if(targetY < 0){

                        binding.movingObject.setImageDrawable(ContextCompat.getDrawable(MainActivity.this,R.drawable.left_up));

                        binding.movingObject.setY(0.1f);
                        velocity =  - getRandom(getScreenHeight/50,getScreenHeight/35);

                    }
                    uponTouchingCeilingOrDownGetNewDirection();
                }
                theWayOfMovement();
                handler.postDelayed(this,1);
            }
        };
        handler.post(runnable);
    }

    //MOVEMENT ENGINE
    private void theWayOfMovement() {
        velocity = velocity - gravity;

        targetX  = targetX - horizontalVelocity;
        targetY  = targetY - velocity;

        binding.movingObject.setX(targetX);
        binding.movingObject.setY(targetY);
    }

    //INITILIZATION PROCESS
    private void definition(){


        backgrounds.add(R.drawable.background1);
        backgrounds.add(R.drawable.background2);
        backgrounds.add(R.drawable.background3);
        backgrounds.add(R.drawable.background4);
        backgrounds.add(R.drawable.background5);

        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        binding.scoreTableTextView.setText(getResources().getString(R.string.default_score_text));
        binding.countDownTimerTextView.setText(getResources().getString(R.string.default_countdown_timer));

        getScreenWidth = metrics.widthPixels;
        getScreenHeight = metrics.heightPixels;

        targetWidth = getScreenWidth / 5;
        targetHeight = getScreenHeight / 7;

        binding.movingObject.getLayoutParams().height = targetHeight;
        binding.movingObject.getLayoutParams().width  = targetWidth;

        targetX = (int) (getScreenWidth / 2);
        targetY = (int) (getScreenHeight / 2);

        binding.startGameButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               startGame(60);
           }
       });

       binding.movingObject.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               catchTheObject();
           }
       });

       binding.arrowLeftButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               if(selectedBackgroundNumber > 0){
                   selectedBackgroundNumber--;
               }else{
                   selectedBackgroundNumber = backgrounds.size() - 1;
               }
               changeBackground(selectedBackgroundNumber);
           }
       });

       binding.arrowRightButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               if(selectedBackgroundNumber < backgrounds.size() - 1){
                   selectedBackgroundNumber++;
               }else{
                   selectedBackgroundNumber = 0;
               }
               changeBackground(selectedBackgroundNumber);
           }
       });
    }

    //LET'S GET STARTED!!
    private void startGame(long time) {
        isGameStarted = true;
        setViewsToGameSituation();

        score = 0;
        binding.scoreTableTextView.setText(getResources().getString(R.string.score_text,String.valueOf(score)));

        startCountDownTimer(time);

        gameEngine();

        targetX = (int) (getScreenWidth / 2);
        targetY = (int) (getScreenHeight / 2);
    }

    private void setViewsToGameSituation(){
        if(isGameStarted){

            binding.movingObject.setVisibility(View.VISIBLE);
            binding.startGameButton.setVisibility(View.GONE);
            binding.mainEncounterPicture.setVisibility(View.GONE);
            binding.arrowRightButton.setVisibility(View.GONE);
            binding.arrowLeftButton.setVisibility(View.GONE);
            binding.mainTitleOfGame.setVisibility(View.GONE);

        }else{

            binding.movingObject.setVisibility(View.GONE);
            binding.startGameButton.setVisibility(View.VISIBLE);
            binding.mainEncounterPicture.setVisibility(View.VISIBLE);
            binding.arrowRightButton.setVisibility(View.VISIBLE);
            binding.arrowLeftButton.setVisibility(View.VISIBLE);
            binding.mainTitleOfGame.setVisibility(View.VISIBLE);

        }
    }
    //UPON CLICKING THE OBJECT WHAT IS GOING TO HAPPEN?
    private void catchTheObject() {

        score++;
        binding.scoreTableTextView.setText(this.getResources().getString(R.string.score_text,String.valueOf(score)));

        //DIFFUCULTY FOR THE GAME
        if(currentDirection == Directions.UP_RIGHT){
          velocity = velocity + (int)(getScreenHeight / 80);
          horizontalVelocity = horizontalVelocity + (int)(getScreenHeight / 80);

        }else if(currentDirection == Directions.UP_LEFT){
          velocity = velocity + (int)(getScreenHeight / 80);
          horizontalVelocity = horizontalVelocity - (int)(getScreenHeight / 80);

        }else if(currentDirection == Directions.DOWN_RIGHT){
          velocity = velocity - (int)(getScreenHeight / 80);
          horizontalVelocity = horizontalVelocity + (int)(getScreenHeight / 80);

        }else if(currentDirection == Directions.DOWN_LEFT){
          velocity = velocity - (int)(getScreenHeight / 80);
          horizontalVelocity = horizontalVelocity - (int)(getScreenHeight / 80);
      }

    }

    //COUNTDOWNTIMER
    private long timeLeft;
    private void startCountDownTimer(long during){
        new CountDownTimer(during * 1000,1000) {
            @Override
            public void onTick(long l) {
                timeLeft = l / 1000;
                binding.countDownTimerTextView.setText(getResources().getString(R.string.countdown_timer,String.valueOf(timeLeft)));
            }

            @Override
            public void onFinish() {
                isGameStarted = false;

                handler.removeCallbacks(runnable);

                setViewsToGameSituation();

            }
        }.start();
    }

    //STATE SAVER
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(SCORE_TAG,score);
        outState.putBoolean(GAME_SITUATION_TAG,isGameStarted);
        if(isGameStarted){
            outState.putLong(COUNTDOWN_TIMER_TAG,timeLeft);
        }

        super.onSaveInstanceState(outState);
    }

    private final String SCORE_TAG = "score_tag"; //TAGS
    private final String COUNTDOWN_TIMER_TAG = "countdown_timer_tag";
    private final String GAME_SITUATION_TAG = "game_situation_tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if(savedInstanceState != null){

            boolean game = savedInstanceState.getBoolean(GAME_SITUATION_TAG);

            if(game){
                long timeLeft = savedInstanceState.getLong(COUNTDOWN_TIMER_TAG);
                startGame(timeLeft);
            }
            score = savedInstanceState.getInt(SCORE_TAG);
            binding.scoreTableTextView.setText(String.valueOf(score));
        }
        definition();
    }
}