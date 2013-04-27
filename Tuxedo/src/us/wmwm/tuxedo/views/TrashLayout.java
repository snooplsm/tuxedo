package us.wmwm.tuxedo.views;

import android.content.Context;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import us.wmwm.tuxedo.R;

public class TrashLayout extends RelativeLayout{

    private final View trashImageView;

    public TrashLayout(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.trash, this);
        trashImageView = findViewById(R.id.trash_imageview);
    }

    public boolean checkCollision(MotionEvent event) {
        int[] location = new int[2];
        trashImageView.getLocationOnScreen(location);
        int trashImageViewX = location[0];
        int trashImageViewY = location[1];
        // the *2 is to make the hit area larger
        return event.getRawX() > trashImageViewX - trashImageView.getWidth() && event.getRawY() > trashImageViewY - trashImageView.getHeight() &&
                event.getRawX() < trashImageViewX + trashImageView.getWidth()*2 && event.getRawY() < trashImageViewY + trashImageView.getHeight()*2;
    }
}
