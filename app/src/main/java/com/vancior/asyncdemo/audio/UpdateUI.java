package com.vancior.asyncdemo.audio;

import android.graphics.Color;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;

import static com.vancior.asyncdemo.MainActivity.lineChartView;

/**
 * Created by H on 2016/12/6.
 */

public class UpdateUI extends Thread {

    private static List<PointValue> pointValues = new ArrayList<>();
    private static List<AxisValue> axisValues = new ArrayList<>();
    private static List<AxisValue> axisValuesY = new ArrayList<>();
    private static Line line = new Line().setColor(Color.parseColor("#FFCD41"));
    private static List<Line> lines = new ArrayList<>();
    private static LineChartData chartData = new LineChartData();
    public static double[] sound;

    private static final int start = 0;
    private static final int end = 2000;
    private static double step;

    public void run() {
        setLineChart();
    }

    public static void setStep(double _step) {
        step = _step;
    }

    private static void setAxis() {
        int k = start;
        for (double i = 0; i < end; i += step, ++k) {
            axisValues.add(new AxisValue((float)i));
            pointValues.add(new PointValue((float)i, (float)sound[k]));
        }
        for (int i = 0; i <= 400; i += 100) {
            AxisValue value = new AxisValue(i);
            String label = "";
            value.setLabel(label);
            axisValuesY.add(value);
        }
    }

    public static void setLineChart() {
        pointValues.clear();
        axisValues.clear();
        axisValuesY.clear();
        lines.clear();

        setAxis();

        line.setValues(pointValues);
        lines.add(line);
        chartData.setLines(lines);

        Axis axisX = new Axis();
        axisX.setValues(axisValues);
        axisX.setTextColor(Color.BLACK);
        axisX.setHasSeparationLine(false);
        chartData.setAxisXBottom(axisX);

        Axis axisY = new Axis().setHasLines(true);
        axisY.setMaxLabelChars(3);
        axisY.setValues(axisValuesY);
        chartData.setAxisYLeft(axisY);

//        Viewport v = lineChartView.getCurrentViewport();
//        System.out.println(v.left + " " + v.top + " " + v.right + " " + v.bottom);

//        Viewport viewport = new Viewport(0, 64, 64, 0);
//

        lineChartView.setLineChartData(chartData);


    }

    public static void initLineChart() {
        line.setShape(ValueShape.CIRCLE);
        line.setPointRadius(5);
        line.setCubic(false);
        line.setFilled(false);
        line.setHasLabels(false);
        line.setHasLabelsOnlyForSelected(false);
        line.setHasLines(true);
        line.setHasPoints(false);

        lineChartView.setViewportCalculationEnabled(false);
        lineChartView.setInteractive(false);
        lineChartView.setZoomEnabled(false);
        lineChartView.setMaximumViewport(new Viewport(0, 800, 2000, 0));
        lineChartView.setCurrentViewport(new Viewport(0, 800, 2000, 0));
        lineChartView.setVisibility(View.VISIBLE);
    }
}
