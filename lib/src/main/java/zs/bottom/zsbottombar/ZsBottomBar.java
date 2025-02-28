package zs.bottom.zsbottombar;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import androidx.core.graphics.drawable.DrawableCompat;

import java.util.List;

public class ZsBottomBar extends View {

    // Basic attributes
    private int barBackgroundColor, barIndicatorColor, itemTextColor, itemTextColorActive;
    private int activeItemColor, itemBadgeColor, itemFontFamily;
    private float barIndicatorWidth, itemIconSize, itemIconMargin, itemTextSize;
    private boolean isIndicatorEnabled = true;
    private int indicatorGravity = 1, activeItemIndex = 0;
    private float indicatorX = 0f;

    // Indicator Shape & Borders
    private float indicatorBorderWidth,indicatorHeight;
    
    // Indicator Animations
    private int indicatorAnimationType, indicatorAnimationDuration;

    // Other attributes
    private int  indicatorInterpolator;

    private List<BottomBarItem> items;
    private OnItemSelectedListener itemSelectedListener;

    private final Paint indicatorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public ZsBottomBar(Context context) {
        super(context);
        init(context, null);
    }

    public ZsBottomBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setDefaults();
        if (attrs != null) {
            TypedArray typedArray =
                    context.obtainStyledAttributes(attrs, R.styleable.NiceBottomBar);
            loadAttributes(context, typedArray);
            typedArray.recycle();
        }
        setupPaints();
        setBackgroundColor(barBackgroundColor);
    }

    private void setDefaults() {
        barBackgroundColor = Color.WHITE;
        barIndicatorColor = Color.BLUE;
        itemTextColor = Color.DKGRAY;
        itemTextColorActive = Color.BLACK;
        activeItemColor = itemTextColorActive;
        barIndicatorWidth = dpToPx(50f);
        itemIconSize = dpToPx(24f);
        itemIconMargin = dpToPx(5f);
        itemTextSize = dpToPx(12f);
        indicatorHeight = dpToPx(2f);
    }

    private void loadAttributes(Context context, TypedArray typedArray) {
        // Basic attributes
        barBackgroundColor =
                typedArray.getColor(R.styleable.NiceBottomBar_backgroundColor, barBackgroundColor);
        barIndicatorColor =
                typedArray.getColor(R.styleable.NiceBottomBar_indicatorColor, barIndicatorColor);
        barIndicatorWidth =
                typedArray.getDimension(
                        R.styleable.NiceBottomBar_indicatorWidth, barIndicatorWidth);
        isIndicatorEnabled =
                typedArray.getBoolean(
                        R.styleable.NiceBottomBar_indicatorEnabled, isIndicatorEnabled);
        itemTextColor = typedArray.getColor(R.styleable.NiceBottomBar_textColor, itemTextColor);
        itemTextColorActive =
                typedArray.getColor(R.styleable.NiceBottomBar_textColorActive, itemTextColorActive);
        itemTextSize = typedArray.getDimension(R.styleable.NiceBottomBar_textSize, itemTextSize);
        itemIconSize = typedArray.getDimension(R.styleable.NiceBottomBar_iconSize, itemIconSize);
        itemIconMargin =
                typedArray.getDimension(R.styleable.NiceBottomBar_iconMargin, itemIconMargin);
        activeItemIndex = typedArray.getInt(R.styleable.NiceBottomBar_activeItem, activeItemIndex);
        indicatorGravity =
                typedArray.getInt(R.styleable.NiceBottomBar_indicatorGravity, indicatorGravity);

        
        indicatorBorderWidth =
                typedArray.getDimension(R.styleable.NiceBottomBar_indicatorBorderWidth, 0);
        indicatorHeight =
                typedArray.getDimension(R.styleable.NiceBottomBar_indicatorHeight, indicatorHeight);
        
        

        // Animations
        indicatorAnimationType =
                typedArray.getInt(R.styleable.NiceBottomBar_indicatorAnimationType, 0);
        indicatorInterpolator =
                typedArray.getInt(R.styleable.NiceBottomBar_indicatorInterpolator, 5);

       

        // Menu Items
        int menuResId = typedArray.getResourceId(R.styleable.NiceBottomBar_menu, 0);
        if (menuResId != 0) {
            items = new BottomBarParser(context, menuResId).parse();
        }
    }

    private Interpolator getInterpolator() {
        switch (indicatorInterpolator) {
            case 0:
                return new android.view.animation.AccelerateInterpolator();
            case 1:
                return new android.view.animation.DecelerateInterpolator();
            case 2:
                return new android.view.animation.AccelerateDecelerateInterpolator();
            case 3:
                return new android.view.animation.AnticipateInterpolator();
            case 4:
                return new android.view.animation.AnticipateOvershootInterpolator();
            case 6:
                return new android.view.animation.OvershootInterpolator();
            default:
                return new LinearInterpolator();
        }
    }

    private void setupPaints() {    
    indicatorPaint.setStyle(Paint.Style.FILL);    
    indicatorPaint.setColor(barIndicatorColor);    

    textPaint.setTextSize(itemTextSize);    
    textPaint.setTextAlign(Paint.Align.CENTER);    

    }
    
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (items != null && !items.isEmpty()) {
            setActiveItem(activeItemIndex);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (items == null || items.isEmpty()) return;

        int itemCount = items.size();
        float itemWidth = getWidth() / (float) itemCount;
        float centerY = getHeight() / 2f;

        for (int i = 0; i < itemCount; i++) {
            BottomBarItem item = items.get(i);
            float itemCenterX = getItemCenterX(i);

            // Set icon bounds
            int iconSizePx = (int) itemIconSize;
            int iconTop = (int) (centerY - (iconSizePx / 2) - itemIconMargin);
            int iconLeft = (int) (itemCenterX - (iconSizePx / 2));
            int iconRight = iconLeft + iconSizePx;
            int iconBottom = iconTop + iconSizePx;

            Drawable icon = item.getIcon();
            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
            DrawableCompat.setTint(
                    icon, i == activeItemIndex ? itemTextColorActive : itemTextColor);
            icon.draw(canvas);

            // Draw text
            textPaint.setColor(i == activeItemIndex ? itemTextColorActive : itemTextColor);
            float textY = iconBottom + itemTextSize;
            canvas.drawText(item.getTitle(), itemCenterX, textY, textPaint);
        }

        // Draw indicator
        if (isIndicatorEnabled) {
    float yPosition = (indicatorGravity == 1) ? getHeight() - indicatorHeight : indicatorHeight;
    
    canvas.drawRect(
        indicatorX - barIndicatorWidth / 2,  // Left
        yPosition - indicatorHeight / 2,     // Top
        indicatorX + barIndicatorWidth / 2,  // Right
        yPosition + indicatorHeight / 2,     // Bottom
        indicatorPaint
    );
}}

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            float touchX = event.getX();
            int newIndex = (int) (touchX / (getWidth() / items.size()));

            if (newIndex != activeItemIndex) {
                setActiveItem(newIndex);
                if (itemSelectedListener != null) {
                    itemSelectedListener.onItemSelected(newIndex);
                }
            }
        }
        return true;
    }

    public void setActiveItem(int index) {
        activeItemIndex = index;
        animateIndicator(index);
    }

    private void animateIndicator(int index) {
        float targetX = getItemCenterX(index);
        ValueAnimator animator = ValueAnimator.ofFloat(indicatorX, targetX);
        animator.setInterpolator(new AnticipateOvershootInterpolator());
        animator.addUpdateListener(
                animation -> {
                    indicatorX = (float) animation.getAnimatedValue();
                    invalidate();
                });
        animator.start();
    }

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        this.itemSelectedListener = listener;
    }

    private float getItemCenterX(int index) {
        return (getWidth() / (float) items.size()) * index + (getWidth() / (2f * items.size()));
    }

    public interface OnItemSelectedListener {
        void onItemSelected(int position);
    }

    private float dpToPx(float dp) {
        return getResources().getDisplayMetrics().density * dp;
    }
}
