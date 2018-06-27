package game.programming.whileloop.canvas_game;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pushpika.canvas_game.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class AnimationActivity extends AppCompatActivity {

    int width, height;
    int current_pos, target_pos;
    int x = 10;
    int y = 10;

    board_layout bd_layout;
    ConstraintLayout layout;
    ImageView token;
    TextView vcpText;

    //vcp stuff
    DatabaseReference vcpDatabase;
    List<VCP> vcpList;
    long result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Log.i("TAG", "current pos-- "+ MainActivity.current_pos);
        Log.i("TAG", "target pos" + MainActivity.target_pos);
        Log.i("TAG", "Target class " + MainActivity.target_class);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {

            setContentView(R.layout.new_animation);

            layout = (ConstraintLayout) findViewById(R.id.consLayout);
            token = (ImageView) findViewById(R.id.ball);
            vcpText = (TextView) findViewById(R.id.vcpText);
            current_pos = MainActivity.current_pos;
            target_pos = MainActivity.target_pos;

            //retrieve vcp from firebase
            vcpDatabase = FirebaseDatabase.getInstance().getReference("vcp").child("values");
            vcpList = new ArrayList<VCP>();

            vcpDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot vcpSnapshot: dataSnapshot.getChildren()) {
                        VCP vcp = vcpSnapshot.getValue(VCP.class);
                        vcpList.add(vcp);
                    }
                    findCurrentVcpValue(target_pos);
                    ViewTreeObserver observer = layout.getViewTreeObserver();
                    observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            init();
                            layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            startBoardAnimation(current_pos, target_pos);
                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        } else {
            bd_layout = new board_layout(this);
            bd_layout.setBackgroundResource(R.drawable.board_new2);
            //setContentView(activity_animation_layout);4
            setContentView(bd_layout);
            Log.i("TAG", "current pos-- "+ MainActivity.current_pos);
            Log.i("TAG", "target pos" + MainActivity.target_pos);
            Log.i("TAG", "Target class " + MainActivity.target_class);
            int timeforanim = Math.abs(MainActivity.target_pos - MainActivity.current_pos);
            Handler handler = new Handler();
            //final Intent intent = new Intent(this, MainActivity.class);
            final Intent intent = new Intent(this, NewQuestionView.class);
            handler.postDelayed(new Runnable() {
                public void run() {
                    startActivity(intent);
                    //bd_layout.setBackgroundColor(Color.WHITE);
                    finish();
                }
            }, timeforanim*500 + 2000);
        }
    }

    public void init() {
        height = layout.getHeight();
        width = layout.getWidth();
    }

    public void startBoardAnimation(int curPos, int finalPos) {
        int dif = Math.abs(finalPos - curPos);
        float travelX = width / x;
        float travelY = height / y;
        float curX = 0;
        float curY = 0;
        int up, right;
        int a = curPos / 10;
        int b = curPos % 10;
        if (b == 0) {
            up = a - 1;
        } else {
            up = a;
        }
        if (a % 2 == 0) {
            if (b == 0) {
                right = b;
            } else {
                right = b - 1;
            }
        } else {
            if (b == 0) {
                right = x - b - 1;
            } else {
                right = x - b;
            }
        }
        curX = travelX * right;
        curY = -(travelY * up);

        //moving token to current position
        ObjectAnimator animate1 = ObjectAnimator.ofFloat(token, View.TRANSLATION_X, curX);
        animate1.setDuration(50);
        ObjectAnimator animate2 = ObjectAnimator.ofFloat(token, View.TRANSLATION_Y, curY);
        animate2.setDuration(50);
        ObjectAnimator animate3 = ObjectAnimator.ofFloat(vcpText, View.ALPHA, 1, 0);
        animate3.setDuration(50);

        //disappearing vcpText
        ObjectAnimator disappear = ObjectAnimator.ofFloat(vcpText, View.ALPHA, 1);
        disappear.setDuration(50);

        //play starting animations
        AnimatorSet startSequence = new AnimatorSet();
        startSequence.playTogether(animate1, animate2, disappear);
        startSequence.play(animate3).after(disappear);
        startSequence.start();

        zoomAnimation(finalPos);

        ObjectAnimator[] animations = new ObjectAnimator[dif];
        if (finalPos > curPos) {
            animations = forwardAnimation(dif, curPos, curX, curY, travelX, travelY);
        } else if (finalPos < curPos)  {
            animations = backAnimation(dif, curPos, curX, curY, travelX, travelY);
        } else {
            vcpAnimate();
        }

        AnimatorSet animationSequence = new AnimatorSet();
        animationSequence.playSequentially(animations);
        animationSequence.setInterpolator(new AccelerateDecelerateInterpolator());
        animationSequence.setStartDelay(2000);
        animationSequence.start();
        animationSequence.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                vcpAnimate();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });



    }

    public void zoomAnimation(float zoomPoint) {
        int firstDigit = (int) (zoomPoint % 10);
        int secondDigit = (int) (zoomPoint / 10);
        int pivotx;
        int pivoty;

        // setting up pivotx
        if (((secondDigit)%2) == 0) {
            // odd rows
            if (firstDigit < 4) {
                pivotx = 0;
            } else if (firstDigit < 6) {
                pivotx = (width / 4);
            } else if (firstDigit < 8) {
                pivotx = (width / 2);
            }
            else {
                pivotx = width;
            }
        } else {
            // even rows
            if (firstDigit < 4) {
                pivotx = width;
            } else if (firstDigit < 6) {
                pivotx = (width / 4 * 3);
            } else if (firstDigit < 8) {
                pivotx = (width / 4);
            } else {
                pivotx = 0;
            }
        }

        //setting up pivoty
        if (secondDigit < 3) {
            pivoty = height;
        } else if (secondDigit < 5) {
            pivoty = (height / 4 * 3);
        } else if (secondDigit < 7) {
            pivoty = (height / 2);
        } else {
            pivoty = 0;
        }
        layout.setPivotX(pivotx);
        layout.setPivotY(pivoty);
        ObjectAnimator animate1 = ObjectAnimator.ofFloat(layout, View.SCALE_X, 1, 1.5f);
        ObjectAnimator animate2 = ObjectAnimator.ofFloat(layout, View.SCALE_Y, 1, 1.5f);
        AnimatorSet animations = new AnimatorSet();
        animations.playTogether(animate1, animate2);
        animations.setDuration(1000);
        animations.start();
    }

    public ObjectAnimator[] forwardAnimation(int dif, int curPos, float curX, float curY, float travelX, float travelY) {
        ObjectAnimator[] animations = new ObjectAnimator[dif];
        for (int i = 0; i < dif; i++) {
            Log.v("curPos+i", String.valueOf((curPos + i)));
            Log.v("answers", String.valueOf((curPos + i / 10) % 2));
            if ((curPos + i) % 10 == 0) {
                ObjectAnimator animate = ObjectAnimator.ofFloat(token, View.TRANSLATION_Y, curY - travelY);
                animate.setDuration(500);
                animations[i] = animate;
                curY -= travelY;
            } else if (((curPos + i) / 10) % 2 == 1) {
                ObjectAnimator animate = ObjectAnimator.ofFloat(token, View.TRANSLATION_X, curX - travelX);
                animate.setDuration(500);
                animations[i] = animate;
                curX -= travelX;
            } else {
                ObjectAnimator animate = ObjectAnimator.ofFloat(token, View.TRANSLATION_X, curX + travelX);
                animate.setDuration(500);
                animations[i] = animate;
                curX += travelX;
            }
        }
        return animations;
    }

    public ObjectAnimator[] backAnimation(int dif, int curPos, float curX, float curY, float travelX, float travelY) {
        ObjectAnimator[] animations = new ObjectAnimator[dif];
        for (int i = 0; i < dif; i++) {
            Log.v("curPos+i", String.valueOf((curPos + i)));
            Log.v("answers", String.valueOf((curPos + i / 10) % 2));
            if ((curPos - i) % 10 == 1) {
                ObjectAnimator animate = ObjectAnimator.ofFloat(token, View.TRANSLATION_Y, curY + travelY);
                animate.setDuration(500);
                animations[i] = animate;
                curY += travelY;
            } else if (((curPos - i) / 10) % 2 == 1) {
                ObjectAnimator animate;
                if ((curPos-i)%10 == 0) {
                    animate = ObjectAnimator.ofFloat(token, View.TRANSLATION_X, curX - travelX);
                    curX -= travelX;
                } else {
                    animate = ObjectAnimator.ofFloat(token, View.TRANSLATION_X, curX + travelX);
                    curX += travelX;
                }
                animate.setDuration(500);
                animations[i] = animate;
            } else {
                ObjectAnimator animate;
                if ((curPos-i)%10 == 0) {
                    animate = ObjectAnimator.ofFloat(token, View.TRANSLATION_X, curX + travelX);
                    curX += travelX;
                } else {
                    animate = ObjectAnimator.ofFloat(token, View.TRANSLATION_X, curX - travelX);
                    curX -= travelX;
                }
                animate.setDuration(500);
                animations[i] = animate;
            }
        }
        return animations;
    }

    public void vcpAnimate() {
        float travelX = width/x;
        float travelY = height/y;
        int up, right;
        int a = target_pos / 10;
        int b = target_pos % 10;
        if (b == 0) {
            up = a - 1;
        } else {
            up = a;
        }
        if (a % 2 == 0) {
            if (b == 0) {
                right = b;
            } else {
                right = b - 1;
            }
        } else {
            if (b == 0) {
                right = x - b - 1;
            } else {
                right = x - b;
            }
        }
        float abc;
        if ((travelX * right) < (width / 10 * 8)) {
            abc = travelX * right + (token.getWidth() + 10f);
        }
        else {
            abc = travelX * right - (vcpText.getWidth() + 10f);
        }
        float def = -(travelY * up);
        ObjectAnimator translateXAnim = ObjectAnimator.ofFloat(vcpText, View.TRANSLATION_X, abc);
        translateXAnim.setDuration(1000);
        ObjectAnimator translateYAnim = ObjectAnimator.ofFloat(vcpText, View.TRANSLATION_Y, def);
        translateYAnim.setDuration(1000);
        ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(vcpText, View.ALPHA, 0, 1);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(translateXAnim, translateYAnim);
        animatorSet.play(alphaAnim).after(translateYAnim);
        animatorSet.start();
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                startNewQuestion();
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    public void startNewQuestion() {
        final Intent intent = new Intent(this, NewQuestionView.class);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
            }
        }, 2000);
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Please wait .....", Toast.LENGTH_SHORT).show();
    }

    private long findCurrentVcpValue(long question) {
        for (int i=0; i<vcpList.size(); i++) {
            VCP current = vcpList.get(i);
            if (current.question == question) {
                result = current.vcp;
                vcpText.append(String.valueOf(result));
            }
        }
        return -1;
    }

}