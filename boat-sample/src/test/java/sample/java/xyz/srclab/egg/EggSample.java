package sample.java.xyz.srclab.egg;

import org.testng.annotations.Test;
import xyz.srclab.common.egg.BoatEggManager;
import xyz.srclab.common.egg.Egg;
import xyz.srclab.common.egg.EggManager;
import xyz.srclab.common.test.TestLogger;

import java.awt.*;

/**
 * @Author: TannerHu
 * @Date: 2021/6/10
 * @Version:
 **/
public class EggSample {

    private static final TestLogger logger = TestLogger.DEFAULT;

    @Test
    public void testEgg() {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        EggManager eggManager = BoatEggManager.INSTANCE;
        Egg egg = eggManager.pick("O Battle");
        egg.hatchOut("Thank you, Taro.");
        //Or
        //egg.hatchOut("谢谢你，泰罗。");
    }
}
