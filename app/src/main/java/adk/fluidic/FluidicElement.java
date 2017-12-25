package adk.fluidic;

import android.content.Context;
import android.graphics.Point;
import android.support.animation.DynamicAnimation;
import android.support.animation.FlingAnimation;
import android.support.animation.SpringAnimation;
import android.support.animation.SpringForce;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.animation.OvershootInterpolator;
import android.widget.RelativeLayout;

/**
 * ADK started crafting this at EPOCH 1514051586.
 */

public class FluidicElement extends RelativeLayout {

    private static final int HORIZONTAL = 0;
    private static final int VERTICAL = 1;
    private static final float FLING_FRICTION = 0.3f;
    private static final float SPRING_DAMPING_RATIO = SpringForce.DAMPING_RATIO_LOW_BOUNCY;
    private static final float SPRING_STIFFNESS = SpringForce.STIFFNESS_LOW;

    private boolean fluidityX = true;
    private boolean fluidityY = true;
    private SpringForce forceX, forceY;
    private SpringAnimation springX, springY;
    private FlingAnimation flingX, flingY;

    private float left, top, right, bottom;

    private VelocityTracker mVelocityTracker;
    private float curX, curY, lastTouchX, lastTouchY;

    public FluidicElement(Context context) {
        super(context);
        initialiseFluidity(context);
    }

    public FluidicElement(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialiseFluidity(context);
    }

    private void initialiseFluidity(Context context) {

        if (fluidityX) curX = getX();
        if (fluidityY) curY = getY();

        final Point screenSize = new Point();
        ((MainActivity) context).getWindowManager().getDefaultDisplay().getSize(screenSize);

        post(new Runnable() {
            @Override
            public void run() {
                setBounds(0, 0, screenSize.x, screenSize.y);
                setupFlings();
                setupSprings();
            }
        });
    }


    private void setupFlings() {

        flingX = new FlingAnimation(this, DynamicAnimation.X)
                .setMinValue(left)
                .setMaxValue(right)
                .setFriction(FLING_FRICTION)
                .addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() {
                    @Override
                    public void onAnimationUpdate(DynamicAnimation animation, float value,
                                                  float velocity) {
                        curX = value;
                        if (!withinBounds(curX, HORIZONTAL)) {
                            animation.cancel();
                            springBackIntoBounds(HORIZONTAL);
                        }
                    }
                });

        flingY = new FlingAnimation(this, DynamicAnimation.Y)
                .setMinValue(top)
                .setMaxValue(bottom)
                .setFriction(FLING_FRICTION)
                .addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() {
                    @Override
                    public void onAnimationUpdate(DynamicAnimation animation, float value,
                                                  float velocity) {
                        curY = value;
                        if (!withinBounds(curY, VERTICAL)) {
                            animation.cancel();
                            springBackIntoBounds(VERTICAL);
                        }
                    }
                });

    }

    private void setupSprings() {

        forceX = new SpringForce().setStiffness(SPRING_STIFFNESS)
                .setDampingRatio(SPRING_DAMPING_RATIO);

        springX = new SpringAnimation(this, DynamicAnimation.X)
                .setSpring(forceX);

        springX.addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() {
            @Override
            public void onAnimationUpdate(DynamicAnimation animation, float value, float velocity) {
                curX = value;
            }
        });


        forceY = new SpringForce().setStiffness(SpringForce.STIFFNESS_MEDIUM)
                .setDampingRatio(SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY);

        springY = new SpringAnimation(this, DynamicAnimation.Y)
                .setSpring(forceY);

        springY.addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() {
            @Override
            public void onAnimationUpdate(DynamicAnimation animation, float value, float velocity) {
                curY = value;
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float dX, dY;

        switch (event.getActionMasked()) {

            case MotionEvent.ACTION_DOWN: {

                lastTouchX = event.getRawX();
                lastTouchY = event.getRawY();

                if (mVelocityTracker == null) {
                    mVelocityTracker = VelocityTracker.obtain();
                } else {
                    mVelocityTracker.clear();
                }

                mVelocityTracker.addMovement(event);
                return true;
            }

            case MotionEvent.ACTION_MOVE: {

                mVelocityTracker.addMovement(event);

                if (fluidityX) {

                    if (flingX.isRunning()){
                        flingX.cancel();
                    }

                    dX = event.getRawX() - lastTouchX;
                    curX += dX;
                    setX(curX);
                    lastTouchX = event.getRawX();
                }

                if (fluidityY) {

                    if (flingY.isRunning()) {
                         flingY.cancel();
                    }

                    dY = event.getRawY() - lastTouchY;
                    curY += dY;
                    setY(curY);
                    lastTouchY = event.getRawY();
                }

                return true;
            }

            case MotionEvent.ACTION_UP: {

                mVelocityTracker.addMovement(event);

                if (withinBounds(curX, HORIZONTAL)
                        && withinBounds(curY, VERTICAL)) {

                    // Create any flings based on the velocity.
                    mVelocityTracker.computeCurrentVelocity(1000);

                    if (fluidityX) {

                        if (flingY.isRunning()) {
                            flingX.cancel();
                        }

                        float vX = mVelocityTracker.getXVelocity();
                        flingX.setStartValue(curX)
                                .setStartVelocity(2 * vX)
                                .start();
                    }

                    if (fluidityY) {

                        if (flingY.isRunning()) {
                            flingY.cancel();
                        }

                        float vY = mVelocityTracker.getYVelocity();
                        flingY.setStartValue(curY)
                                .setStartVelocity(2 * vY)
                                .start();
                    }

                } else {

                    if (fluidityX && !withinBounds(curX, HORIZONTAL)) {
                        springBackIntoBounds(HORIZONTAL);
                    }

                    if (fluidityY && !withinBounds(curY, VERTICAL)) {
                        springBackIntoBounds(VERTICAL);
                    }
                }

                return true;
            }

            case MotionEvent.ACTION_CANCEL: {
                mVelocityTracker.recycle();
                break;
            }
        }
        return false;
    }


    void setBounds(float left, float top, float right, float bottom) {

        this.left = left;
        this.top = top;
        this.right = right - getWidth();
        this.bottom = bottom - getHeight();

        animate()
                .x(top)
                .y(top)
                .setDuration(350)
                .setInterpolator(new OvershootInterpolator())
                .start();

        curX = top;
        curY = left;

        setupSprings();
        setupFlings();
    }

    boolean withinBounds(float value, int mode) {
        switch (mode) {
            case HORIZONTAL:
                return (left <= value && value <= right);
            case VERTICAL:
                return (top <= value && value <= bottom);
            default:
                return true;
        }
    }

    private void springBackIntoBounds(int mode) {

        switch (mode) {
            case HORIZONTAL: {
                flingX.cancel();
                springX.cancel();
                forceX.setFinalPosition(curX < left ? left : right);
                springX.setSpring(forceX)
                        .start();
                break;
            }
            case VERTICAL: {
                flingY.cancel();
                springY.cancel();
                forceY.setFinalPosition(curY < top ? top : bottom);
                springY.setSpring(forceY)
                        .start();
                break;
            }
        }
    }
}
