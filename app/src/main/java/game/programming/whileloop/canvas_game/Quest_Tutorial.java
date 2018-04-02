package game.programming.whileloop.canvas_game;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.pushpika.canvas_game.R;
import com.google.android.gms.games.Games;

import game.google.example.games.basegameutils.BaseGameActivity;

public class Quest_Tutorial extends BaseGameActivity {

    private ViewPager mSlideViewPager;
    private LinearLayout mDotLayout;
    private Button startButton;

    private SliderAdapter sliderAdapter;

    private TextView[] mDots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quest_tutorial);

        mSlideViewPager = (ViewPager) findViewById(R.id.slideViewPager);
        mDotLayout = (LinearLayout) findViewById(R.id.dotsLayout);
//        startButton = (Button) findViewById(R.id.startBtn);

        sliderAdapter = new SliderAdapter(this);
        mSlideViewPager.setAdapter(sliderAdapter);

        addDotsIndicator(0);

        mSlideViewPager.addOnPageChangeListener(viewListener);

    }

    private void addDotsIndicator(int position) {
        mDots = new TextView[4];
        mDotLayout.removeAllViews();
//        startButton.setVisibility(View.GONE);

        for (int i=0; i<mDots.length ; i++) {
            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(Color.parseColor("#FFB74D"));
            mDotLayout.addView(mDots[i]);
        }

        if (mDots.length > 0) {
            mDots[position].setTextColor(Color.parseColor("#E65100"));
        }

//        Log.d("START_BTN", "Came here");
//        if (position == 3) {
//            Log.d("BTN_ACTIVATE", "Button set to visible");
//            startButton.setVisibility(View.VISIBLE);
//        }
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDotsIndicator(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    //Added by me
    public void Go_Questions(View view) {
        Intent transition_page = new Intent(this,NewQuestionView.class);
        startActivity(transition_page);
        finish();
    }

    @Override
    public void onSignInFailed() {

    }

    @Override
    public void onSignInSucceeded() {

    }
}
