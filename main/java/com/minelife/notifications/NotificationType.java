package com.minelife.notifications;

public enum NotificationType {

    BLACK(0, 0, 28, 4, 4, 0xFFFFFF),
    WHITE(0, 32, 60, 4, 4, 4210752),
    EDGED(0, 96, 121, 5, 7, 4210752);

    public int x, y, bottomY, width = 160, topHeight, bottomHeight, middleHeight = 9, textColor;

    NotificationType(int x, int y, int bottomY, int topHeight, int bottomHeight, int textColor) {
        this.x = x;
        this.y = y;
        this.bottomY = bottomY;
        this.topHeight = topHeight;
        this.bottomHeight = bottomHeight;
        this.textColor = textColor;
    }

}
