package adk.fluidic;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getActionBar().hide();

        View left = findViewById(R.id.left);
        View top = findViewById(R.id.top);
        View right = findViewById(R.id.right);
        View bottom = findViewById(R.id.bottom);
        CardView card = findViewById(R.id.card);

        Point p = new Point();
        getWindowManager().getDefaultDisplay().getSize(p);

        left.setX(100);
        left.setY(0);

        top.setX(0);
        top.setY(200);

        right.setX(p.x - 100);
        right.setY(0);

        bottom.setX(0);
        bottom.setY(p.y - 200);


    }
}
