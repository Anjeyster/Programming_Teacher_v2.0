package game.programming.whileloop.canvas_game;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pushpika.canvas_game.R;

/**
 * Created by tuanamith on 3/30/18.
 */

public class SliderAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;
    int slideNumber = 4;

    public SliderAdapter(Context context) {
        this.context = context;
    }

    public int[] slideImages = {
            R.drawable.page_1,
            R.drawable.page_2,
            R.drawable.page_3,
            R.drawable.page_4
    };

    public String[] slideDesc = {
            "Description for first guide",
            "Description for second guide",
            "Description for third guide",
            ""
    };


    @Override
    public int getCount() {
        return slideNumber;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == (ConstraintLayout) object;
    }

    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_layout, container, false);

        ImageView slideImageView = (ImageView) view.findViewById(R.id.slide_image);
//        TextView slideDescription = (TextView) view.findViewById(R.id.slide_desc);
        Button getStarted = (Button) view.findViewById(R.id.startBtn);

        if(position == 3) {
            getStarted.setVisibility(View.VISIBLE);
        }

        slideImageView.setImageResource(slideImages[position]);
//        slideDescription.setText(slideDesc[position]);


        container.addView(view);
        return view;
    }

    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((ConstraintLayout) object);
    }
}
