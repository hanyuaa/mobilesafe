package com.itheima.mobilesafe;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.itheima.mobilesafe.dao.BlackNumberDao;
import com.itheima.mobilesafe.domain.BlackNumberInfo;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.itheima.mobilesafe", appContext.getPackageName());
    }

    @Test
    public void insert() {
        Context appContext = InstrumentationRegistry.getTargetContext();

        BlackNumberDao dao = BlackNumberDao.getInstance(appContext);

        for (int i = 0; i < 100; i++) {
            if (i < 10) {
                dao.insert("1860000000" + i, new Random().nextInt(3) + 1 + "");
            } else {
                dao.insert("186000000" + i, new Random().nextInt(3) + 1 + "");
            }
        }
    }

    @Test
    public void delete() {
        Context appContext = InstrumentationRegistry.getTargetContext();

        BlackNumberDao dao = BlackNumberDao.getInstance(appContext);

        dao.delete("110");
    }

    @Test
    public void update() {
        Context appContext = InstrumentationRegistry.getTargetContext();

        BlackNumberDao dao = BlackNumberDao.getInstance(appContext);

        dao.update("110", "2");
    }


    @Test
    public void find() {
        Context appContext = InstrumentationRegistry.getTargetContext();

        BlackNumberDao dao = BlackNumberDao.getInstance(appContext);

        List<BlackNumberInfo> all = dao.findAll();

        System.out.println(all.get(0).getPhone());
        System.out.println(all.get(0).getMode());
    }
}
