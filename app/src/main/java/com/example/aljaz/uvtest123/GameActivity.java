package com.example.aljaz.uvtest123;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.app.Application;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;


public class GameActivity extends AppCompatActivity {


    private static int NBR_ITEMS = 6;
    private android.support.v7.widget.GridLayout mGrid;
    private ScrollView mScrollView;
    private ValueAnimator mAnimator;
    private AtomicBoolean mIsScrolling = new AtomicBoolean(false);
    public ArrayList<String> words = new ArrayList<>();
    String correctWord;
    MediaPlayer okSound;





    public String getWord(ArrayList words){
        int arraySize = words.size();
        Random rand = new Random();
        int number = rand.nextInt(arraySize);
        String word = (String) words.get(number);
        return word;
    }

    public static void shuffleArray(String[] a) {
        int n = a.length;
        Random random = new Random();
        random.nextInt();
        for (int i = 0; i < n; i++) {
            int change = i + random.nextInt(n - i);
            swap(a, i, change);
        }
    }

    private static void swap(String[] a, int i, int change) {
        String helper = a[i];
        a[i] = a[change];
        a[change] = helper;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_game);
        okSound = MediaPlayer.create(this, R.raw.applause10);


        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("dog");
        arrayList.add("cat");
        arrayList.add("mouse");
        arrayList.add("kolesa");
        Intent intent = getIntent();
        HashMap<String, String> map = (HashMap<String, String>) intent.getSerializableExtra("hashMap");
        for (Map.Entry<String, String> entry : map.entrySet())
        {
            if(entry.getKey() != null){
                arrayList.add(entry.getKey());
            }

        }
        words = arrayList;
        String word = getWord(words);
        correctWord = word;
        ImageView image = findViewById(R.id.imageView);
        if(correctWord.equals("dog") || correctWord.equals("cat") || correctWord.equals("mouse") || correctWord.equals("kolesa")){
            int imageId = getResources().getIdentifier(correctWord, "drawable", getPackageName());
            image.setImageResource(imageId);
        }else{
            String path = map.get(correctWord);
            image.setImageBitmap(BitmapFactory.decodeFile(path));
        }



        NBR_ITEMS = word.length();
        String[] letters1 = word.split("");
        int n  = letters1.length - 1;
        String[] letters = new String[n];
        for(int i = 1; i <= n; i++){
            letters[i - 1] = letters1[i];
        }
        shuffleArray(letters);















        mScrollView = findViewById(R.id.scroll_view);
        mScrollView.setSmoothScrollingEnabled(true);

        mGrid =  findViewById(R.id.grid_layout);
        mGrid.setOnDragListener(new DragListener());

        final LayoutInflater inflater = LayoutInflater.from(this);
        for (int i = 0; i < NBR_ITEMS; i++) {
            final View itemView = inflater.inflate(R.layout.grid_item, mGrid, false);
            final TextView text = itemView.findViewById(R.id.text);
            String letter = letters[i];
            text.setText(letter);

            itemView.setOnLongClickListener(new LongPressListener());
            mGrid.addView(itemView);
        }
    }

    static class LongPressListener implements View.OnLongClickListener {
        @Override
        public boolean onLongClick(View view) {
            final ClipData data = ClipData.newPlainText("", "");
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
            view.startDrag(data, shadowBuilder, view, 0);
            view.setVisibility(View.INVISIBLE);
            return true;
        }
    }

    class DragListener implements View.OnDragListener {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            final View view = (View) event.getLocalState();
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_LOCATION:
                    // do nothing if hovering above own position
                    if (view == v) return true;
                    // get the new list index
                    final int index = calculateNewIndex(event.getX(), event.getY());


                    final Rect rect = new Rect();
                    mScrollView.getHitRect(rect);
                    final int scrollY = mScrollView.getScrollY();

                    if (event.getY() -  scrollY > mScrollView.getBottom() - 250) {
                        startScrolling(scrollY, mGrid.getHeight());
                    } else if (event.getY() - scrollY < mScrollView.getTop() + 250) {
                        startScrolling(scrollY, 0);
                    } else {
                        stopScrolling();
                    }

                    // remove the view from the old position
                    mGrid.removeView(view);
                    // and push to the new
                    mGrid.addView(view, index);
                    break;
                case DragEvent.ACTION_DROP:
                    view.setVisibility(View.VISIBLE);
                    int number = mGrid.getChildCount();
                    String wordTest = "";
                    for(int i = 0; i < number; i++){
                       View item = mGrid.getChildAt(i);
                       TextView textView = item.findViewById(R.id.text);
                       wordTest += textView.getText();

                    }
                    if(wordTest.equals(correctWord)){
                        okSound.start();
                        AlertDialog.Builder dialog = new AlertDialog.Builder(GameActivity.this);
                        dialog.setMessage("Play again?").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                                startActivity(getIntent());
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        });

                        AlertDialog dia = dialog.create();
                        dia.setTitle("Hello old friend!");
                        dia.show();

                    }
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    if (!event.getResult()) {
                        view.setVisibility(View.VISIBLE);
                    }
                    break;
            }
            return true;
        }
    }

    private void startScrolling(int from, int to) {
        if (from != to && mAnimator == null) {
            mIsScrolling.set(true);
            mAnimator = new ValueAnimator();
            mAnimator.setInterpolator(new OvershootInterpolator());
            mAnimator.setDuration(Math.abs(to - from));
            mAnimator.setIntValues(from, to);
            mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    mScrollView.smoothScrollTo(0, (int) valueAnimator.getAnimatedValue());
                }
            });
            mAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mIsScrolling.set(false);
                    mAnimator = null;
                }
            });
            mAnimator.start();
        }
    }

    private void stopScrolling() {
        if (mAnimator != null) {
            mAnimator.cancel();
        }
    }

    private int calculateNewIndex(float x, float y) {
        // calculate which column to move to
        final float cellWidth = mGrid.getWidth() / mGrid.getColumnCount();
        final int column = (int)(x / cellWidth);

        // calculate which row to move to
        final float cellHeight = mGrid.getHeight() / mGrid.getRowCount();
        final int row = (int)Math.floor(y / cellHeight);

        // the items in the GridLayout is organized as a wrapping list
        // and not as an actual grid, so this is how to get the new index
        int index = row * mGrid.getColumnCount() + column;
        if (index >= mGrid.getChildCount()) {
            index = mGrid.getChildCount() - 1;
        }

        return index;
    }



}



