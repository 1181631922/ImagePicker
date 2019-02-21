package com.meili.moon.imagepicker.property;

import com.meili.moon.imagepicker.util.Preconditions;

import java.util.Arrays;

/**
 * Author： fanyafeng
 * Date： 2018/11/30 10:15 AM
 * Email: fanyafeng@live.cn
 */
public class RoundingParams {
    private boolean mRoundAsCircle = false;
    private float[] mCornersRadii = null;

    public boolean ismRoundAsCircle() {
        return mRoundAsCircle;
    }

    public RoundingParams setmRoundAsCircle(boolean roundAsCircle) {
        this.mRoundAsCircle = roundAsCircle;
        return this;
    }

    public RoundingParams setCornersRadii(float topLeft, float topRight, float bottomRight, float bottomLeft) {
        float[] radii = getOrCreateRoundedCornersRadii();
        radii[0] = topLeft;
        radii[1] = topRight;
        radii[2] = bottomRight;
        radii[3] = bottomLeft;
        return this;
    }

    public RoundingParams setCornersRadii(float[] radii) {
        Preconditions.checkNotNull(radii);
        Preconditions.checkArgument(radii.length == 4, "radii should have exactly 8 values");
        System.arraycopy(radii, 0, getOrCreateRoundedCornersRadii(), 0, 4);
        return this;
    }

    public RoundingParams setCornersRadius(float radius) {
        Arrays.fill(getOrCreateRoundedCornersRadii(), radius);
        return this;
    }

    private float[] getOrCreateRoundedCornersRadii() {
        if (mCornersRadii == null) {
            mCornersRadii = new float[4];
        }
        return mCornersRadii;
    }

    public float[] getCornersRadii() {
        return mCornersRadii;
    }
}
