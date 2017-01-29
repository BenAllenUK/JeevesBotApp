package info.benallen.jeeves.jeevescontroller.face;

import com.crowdrobo.robohead.PixelInterface;

/**
 * Created by rory on 30/01/16.
 */
public abstract class FacialFeature implements PixelInterface {


    private PixelInfo mPixelInfo;

    public void setPixelInfo(PixelInfo pixelInfo) {
        this.mPixelInfo = pixelInfo;
    }

    @Override
    public boolean shouldDrawPixel(int x, int y) {
        if (y >= mPixelInfo.y && y < mPixelInfo.y + mPixelInfo.map.length) {
            //within y
            if (x >= mPixelInfo.x && x < mPixelInfo.x + mPixelInfo.map[0].length) {
                //within x
                return mPixelInfo.map[y - mPixelInfo.y][x - mPixelInfo.x] == 1;
            }
        }
        return false;
    }
}
