package gr.angelo.currencyexchange.utils;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;

import com.gauravk.bubblenavigation.IBubbleNavigation;
import com.takusemba.spotlight.OnSpotlightStateChangedListener;
import com.takusemba.spotlight.OnTargetStateChangedListener;
import com.takusemba.spotlight.Spotlight;
import com.takusemba.spotlight.shape.Circle;
import com.takusemba.spotlight.target.SimpleTarget;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import gr.angelo.currencyexchange.BottomNavigation;
import gr.angelo.currencyexchange.R;

public class Utils {
    public void prepareSymbols(String base) {
        List<String> result = new LinkedList<>(Arrays.asList(BottomNavigation.allSymbols.split("\\s*,\\s*")));
        result.remove(base);
        BottomNavigation.symbols = TextUtils.join(",", result);
    }

    public List<String> getAllSymbols() {
        return new LinkedList<>(Arrays.asList(BottomNavigation.allSymbols.split("\\s*,\\s*")));
    }

    public void showSpotlight(Activity activity, View view, IBubbleNavigation nav) {
        SimpleTarget simpleTarget = new SimpleTarget.Builder(activity)
                .setPoint(view)
                .setShape(new Circle(200f)) // or RoundedRectangle()
                .setTitle(activity.getString(R.string.login_title))
                .setDescription(activity.getString(R.string.login_desc))
                .setOverlayPoint(100f, 100f)
                .setOnSpotlightStartedListener(new OnTargetStateChangedListener<SimpleTarget>() {
                    @Override
                    public void onStarted(SimpleTarget target) {
                    }
                    @Override
                    public void onEnded(SimpleTarget target) {
                        nav.setCurrentActiveItem(0);
                    }
                })
                .build();

        Spotlight.with(activity)
                .setOverlayColor(R.color.background)
                .setDuration(1000L)
                .setAnimation(new DecelerateInterpolator(2f))
                .setTargets(simpleTarget)
                .setClosedOnTouchedOutside(true)
                .start();
    }
}
