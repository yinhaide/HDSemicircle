package com.yhd.semicircle.app.frag;

import android.view.View;
import android.widget.TextView;

import com.de.rocket.ue.frag.RoFragment;
import com.de.rocket.ue.injector.BindView;
import com.de.rocket.ue.injector.Event;
import com.yhd.semicircle.SemiCircleView;
import com.yhd.semicircle.app.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 双波浪曲线
 * Created by haide.yin(haide.yin@tcl.com) on 2019/6/6 16:12.
 */
public class Frag_semicircle extends RoFragment {


    @BindView(R.id.smc)
    private SemiCircleView barChartView;
    @BindView(R.id.tv_progress)
    private TextView tvProgress;

    @Override
    public int onInflateLayout() {
        return R.layout.frag_semicircle;
    }

    @Override
    public void initViewFinish(View inflateView) {

    }

    @Override
    public void onNexts(Object object) {

    }

    @Event(R.id.bt_thirty)
    private void thirty(View view) {
        /**
         * 需要加深分布点,是一个String[]类表,规则如下
         * String[0]:开始百分比(0-1f)
         * String[1]:扫过的百分比(0-1f)
         * String[0] + String[1] <= 1f
         */
        List<float[]> deepArray = new ArrayList<>();
        deepArray.add(new float[]{0.2f, 0.1f});
        deepArray.add(new float[]{0.4f, 0.1f});
        deepArray.add(new float[]{0.6f, 0.1f});
        barChartView.setDeepArray(deepArray);

        tvProgress.setText("30%");
    }

    @Event(R.id.bt_sixty)
    private void sixty(View view) {
        /**
         * 需要加深分布点,是一个String[]类表,规则如下
         * String[0]:开始百分比(0-1f)
         * String[1]:扫过的百分比(0-1f)
         * String[0] + String[1] <= 1f
         */
        List<float[]> deepArray = new ArrayList<>();
        deepArray.add(new float[]{0.05f, 0.05f});
        deepArray.add(new float[]{0.15f, 0.2f});
        deepArray.add(new float[]{0.4f, 0.15f});
        deepArray.add(new float[]{0.6f, 0.1f});
        deepArray.add(new float[]{0.8f, 0.1f});
        barChartView.setDeepArray(deepArray);

        tvProgress.setText("60%");
    }
}
