package zs.bottom.zsbottombar;

import android.graphics.RectF;
import android.graphics.drawable.Drawable;

public class BottomBarItem {
    private String title;
    private Drawable icon;
    private RectF rect;
    private float badgeSize;

    public BottomBarItem(String title, Drawable icon) {
        this.title = title;
        this.icon = icon;
        this.rect = new RectF();
        this.badgeSize = 0f;
    }

    public BottomBarItem(String title, Drawable icon, RectF rect, float badgeSize) {
        this.title = title;
        this.icon = icon;
        this.rect = rect;
        this.badgeSize = badgeSize;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public RectF getRect() {
        return rect;
    }

    public void setRect(RectF rect) {
        this.rect = rect;
    }

    public float getBadgeSize() {
        return badgeSize;
    }

    public void setBadgeSize(float badgeSize) {
        this.badgeSize = badgeSize;
    }
}