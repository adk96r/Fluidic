package adk.fluidic;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Make the app fullscreen.
        getActionBar().hide();

        // Get the screen size.
        final Point screenSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(screenSize);

        // Draw the margins for demo.
        drawMargins(screenSize);

        // Set the same margins to the fluidic layout.
        final FluidicElement fluidicElement = findViewById(R.id.fluidic_layout);

        // Make sure to set them only after the layout's been drawn.
        fluidicElement.post(new Runnable() {
            @Override
            public void run() {
                fluidicElement.setBounds(100, 100,
                        screenSize.x - 100, screenSize.y - 100);
            }
        });

    }

    private void drawMargins(Point screenSize) {
        // Set the margins
        View left = findViewById(R.id.left);
        View top = findViewById(R.id.top);
        View right = findViewById(R.id.right);
        View bottom = findViewById(R.id.bottom);

        left.setX(100);
        left.setY(0);

        top.setX(0);
        top.setY(100);

        right.setX(screenSize.x - 100);
        right.setY(0);

        bottom.setX(0);
        bottom.setY(screenSize.y - 100);
    }
}
