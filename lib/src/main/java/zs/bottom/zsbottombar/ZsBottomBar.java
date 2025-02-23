package zs.bottom.zsbottombar;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnticipateOvershootInterpolator;

import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import java.util.List;

public class ZsBottomBar extends View {

    private int barBackgroundColor;
    private int barIndicatorColor;
    private float barIndicatorWidth;
    private boolean barIndicatorEnabled = true;
    private int barIndicatorGravity = 1;
    private float itemIconSize;
    private float itemIconMargin;
    private int itemTextColor;
    private int itemTextColorActive;
    private float itemTextSize;
    private int itemBadgeColor;
    private int itemFontFamily;
    private int activeItem = 0;

    private int currentActiveItemColor;
    private float indicatorLocation = 0f;
    private List<BottomBarItem> items;
    private OnItemSelectedListener onItemSelectedListener;

    private final Paint paintIndicator = new Paint();
    private final Paint paintText = new Paint();
    private final Paint paintBadge = new Paint();

    public ZsBottomBar(Context context) {
        super(context);
        init(context, null);
    }

    public ZsBottomBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        barBackgroundColor = Color.parseColor(Constants.WHITE_COLOR_HEX);
        barIndicatorColor = Color.parseColor(Constants.DEFAULT_INDICATOR_COLOR);
        itemTextColor = Color.parseColor(Constants.DEFAULT_TEXT_COLOR);
        itemTextColorActive = Color.parseColor(Constants.DEFAULT_TEXT_COLOR_ACTIVE);
        itemBadgeColor = itemTextColorActive;
        barIndicatorWidth = d2p(50f);
        itemIconSize = d2p(18f);
        itemIconMargin = d2p(3f);
        itemTextSize = d2p(11f);
        currentActiveItemColor = itemTextColor;

        if (attrs != null) {
            TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.NiceBottomBar, 0, 0);
            barBackgroundColor = typedArray.getColor(R.styleable.NiceBottomBar_backgroundColor, barBackgroundColor);
            barIndicatorColor = typedArray.getColor(R.styleable.NiceBottomBar_indicatorColor, barIndicatorColor);
            barIndicatorWidth = typedArray.getDimension(R.styleable.NiceBottomBar_indicatorWidth, barIndicatorWidth);
            barIndicatorEnabled = typedArray.getBoolean(R.styleable.NiceBottomBar_indicatorEnabled, barIndicatorEnabled);
            itemTextColor = typedArray.getColor(R.styleable.NiceBottomBar_textColor, itemTextColor);
            itemTextColorActive = typedArray.getColor(R.styleable.NiceBottomBar_textColorActive, itemTextColorActive);
            itemTextSize = typedArray.getDimension(R.styleable.NiceBottomBar_textSize, itemTextSize);
            itemIconSize = typedArray.getDimension(R.styleable.NiceBottomBar_iconSize, itemIconSize);
            itemIconMargin = typedArray.getDimension(R.styleable.NiceBottomBar_iconMargin, itemIconMargin);
            activeItem = typedArray.getInt(R.styleable.NiceBottomBar_activeItem, activeItem);
            barIndicatorGravity = typedArray.getInt(R.styleable.NiceBottomBar_indicatorGravity, barIndicatorGravity);
            itemBadgeColor = typedArray.getColor(R.styleable.NiceBottomBar_badgeColor, itemBadgeColor);
            itemFontFamily = typedArray.getResourceId(R.styleable.NiceBottomBar_itemFontFamily, itemFontFamily);
            items = new BottomBarParser(context, typedArray.getResourceId(R.styleable.NiceBottomBar_menu, 0)).parse();
            typedArray.recycle();
        }

        setBackgroundColor(barBackgroundColor);

        paintIndicator.setAntiAlias(true);
        paintIndicator.setStyle(Paint.Style.FILL);
        paintIndicator.setStrokeWidth(6f);
        paintIndicator.setColor(barIndicatorColor);

        paintText.setAntiAlias(true);
        paintText.setStyle(Paint.Style.FILL);
        paintText.setColor(itemTextColor);
        paintText.setTextSize(itemTextSize);
        paintText.setTextAlign(Paint.Align.CENTER);
        paintText.setFakeBoldText(true);

        paintBadge.setAntiAlias(true);
        paintBadge.setStyle(Paint.Style.FILL);
        paintBadge.setColor(itemBadgeColor);
        paintBadge.setStrokeWidth(4f);

        if (itemFontFamily != 0) {
            Typeface typeface = ResourcesCompat.getFont(context, itemFontFamily);
            paintText.setTypeface(typeface);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (items != null && !items.isEmpty()) {
            setActiveItem(activeItem); // Initialize correctly on first view
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
            item.getIcon().setBounds(iconLeft, iconTop, iconRight, iconBottom);

            // Tint and draw the icon
            item.getIcon().mutate();
            DrawableCompat.setTint(item.getIcon(), i == activeItem ? currentActiveItemColor : itemTextColor);
            item.getIcon().draw(canvas);

            // Adjust text position
            paintText.setColor(i == activeItem ? currentActiveItemColor : itemTextColor);
            float textY = iconBottom + itemTextSize;
            canvas.drawText(item.getTitle(), itemCenterX, textY, paintText);
        }

        // Draw indicator if enabled
        if (barIndicatorEnabled) {
            float yPosition = (barIndicatorGravity == 1) ? getHeight() - 10f : 10f;
            canvas.drawLine(indicatorLocation - barIndicatorWidth / 2, yPosition,
                    indicatorLocation + barIndicatorWidth / 2, yPosition, paintIndicator);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            float touchX = event.getX();
            int newActiveItem = (int) (touchX / (getWidth() / items.size()));

            if (newActiveItem != activeItem) {
                setActiveItem(newActiveItem);
                if (onItemSelectedListener != null) {
                    onItemSelectedListener.onItemSelected(newActiveItem); // FIXED LISTENER CALL
                }
            }
        }
        return true;
    }

    public void setActiveItem(int pos) {
        activeItem = pos;
        animateIndicator(pos);
    }
    private void animateIndicator(int pos) {
    // Get the target position of the indicator
    float targetX = getItemCenterX(pos);

    // Create an animator to move the indicator smoothly
    ValueAnimator animator = ValueAnimator.ofFloat(indicatorLocation, targetX);
    animator.setInterpolator(new AnticipateOvershootInterpolator()); // Animation effect
    animator.addUpdateListener(animation -> {
        indicatorLocation = (float) animation.getAnimatedValue(); // Update the indicator's position
        invalidate(); // Redraw the view with the updated indicator position
    });

    // Start the animation
    animator.start();
}

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        this.onItemSelectedListener = listener;
    }

    private float getItemCenterX(int pos) {
        return (getWidth() / (float) items.size()) * pos + (getWidth() / (2f * items.size()));
    }

    public interface OnItemSelectedListener {
        void onItemSelected(int position);
    }

    private float d2p(float dp) {
        return getResources().getDisplayMetrics().density * dp;
    }
}