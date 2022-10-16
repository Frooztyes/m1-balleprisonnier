package fr.icom.info.m1.balleauprisonnier_mvn;

import java.awt.*;

public final class Const {
    public static final Dimension FIELD_DIM = new Dimension(600, 600);
    public static final int OFFSET_FIELD = 20;
    public static final int NB_EQ1 = 3;
    public static final int NB_EQ2 = 3;

    public static final int NB_IA_EQ1 = 2;
    public static final int NB_IA_EQ2 = 2;

    public static final int MOVESPEED = 2;

    public static final int SIDE_TOP = 1;
    public static final int SIDE_BOT = 2;

    public static final int HITBOX_PLAYER = 64;

    public static final int HEIGHT_EQ1 = FIELD_DIM.height - HITBOX_PLAYER/2 - OFFSET_FIELD;
    public static final int HEIGHT_EQ2 = 20 + OFFSET_FIELD;
}
