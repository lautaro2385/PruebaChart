package co.com.bluesoft.pruebachart;

import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class MainActivity extends AppCompatActivity {

    TextView tv;
    LineChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = findViewById(R.id.textView);
        chart = findViewById(R.id.chart);

        //Datos 1 de pruebas
        double[] y = {150, 135, 120, 100, 80};
        double[] x = {0, 818, 1561, 2236, 2995};
        //DAtos 2 de pruebas
        double[] x2 = {
                0,
                860,
                1530,
                2300,
                3100};
        double[] y2 = {
                150,
                137,
                123,
                105,
                85,
        };

        PolynomialSplineFunction psf = Iterpolacion(x, y);
        PolynomialSplineFunction psf2 = Iterpolacion(x2, y2);
        PolynomialFunction pf = GeneraEcuacion(x, y, 2);

        LineDataSet dataSet = GeneretePoints((d) -> (float) psf.value(d), "Seria 1", android.R.color.holo_red_dark, android.R.color.black);
        LineDataSet dataSet2 = GeneretePoints((d) -> (float) psf.value(d), "Seria 2", android.R.color.holo_green_dark, android.R.color.holo_blue_dark);
        LineDataSet dataSet3 = GeneretePoints((d) -> (float) pf.value(d), "Seria 3", android.R.color.holo_orange_dark, android.R.color.darker_gray);

        drawOnChar(dataSet, dataSet3);
    }

    /**
     * Con este método se implementa una interpolacion, la cual no genera una función, sino que genera
     * varias funciones para aproximar a los puntos que dieron
     */
    private PolynomialSplineFunction Iterpolacion(double[] x, double[] y) {
        SplineInterpolator inter = new SplineInterpolator();
        return inter.interpolate(x, y);
    }

    /**
     * Con este método se genera la ecuación de orden n que más se aproxime a los puntos dados
     *
     * @param x     los valores en x
     * @param y     los valores en y
     * @param grade grado de la ecuación a aproximas
     * @return polinomio calculado
     */
    private PolynomialFunction GeneraEcuacion(double[] x, double[] y, int grade) {
        final WeightedObservedPoints obs = new WeightedObservedPoints();
        //vuelve los putnos x y y en WeightedObservedPoints
        for (int i = 0; i < x.length; i++) {
            obs.add(x[i], y[i]);
        }
        //se obtiene la ecucacion que se aproxima de orden grade
        final PolynomialCurveFitter fitter = PolynomialCurveFitter.create(grade);
        //se obtienen los coeficioentes de la ecuación
        final double[] coeff = fitter.fit(obs.toList());
        tv.setText(String.format("%fx^2+%fx+%f", coeff[2], coeff[1], coeff[0]));
        //se obtiene la función polinomial
        return new PolynomialFunction(coeff);
    }

    /**
     * Se evalua sobre la función interpolada con los puntos y se genera un data set para
     * pintarlo en la grafica
     *
     * @param func        valor a evaluar
     * @param label       nombre de la seria
     * @param colorLine   color de la linea de la grafica
     * @param colorCircle color del circulo en la gráfica
     * @return data de la gráfica
     */
    private LineDataSet GeneretePoints(Function<Integer, Float> func,
                                       String label,
                                       @ColorRes int colorLine,
                                       @ColorRes int colorCircle) {
        List<Entry> entries = new ArrayList<>();
        //se generan todos los puentos de la gráfica
        for (int i = 0; i < 3000; i += 100) {
            entries.add(new Entry(i, func.apply(i)));
        }
        //se crea el data set
        LineDataSet dataSet = new LineDataSet(entries, label);
        dataSet.setColor(getResources().getColor(colorLine, null));
        dataSet.setCircleColor(getResources().getColor(colorCircle, null));
        return dataSet;
    }

    private void drawOnChar(ILineDataSet... dataSets) {
        LineData lineData = new LineData(dataSets);
        chart.setData(lineData);
        YAxis yAxis = chart.getAxisLeft();
        yAxis.setAxisMinimum(0f); // start at zero
        yAxis.setAxisMaximum(150f); // the axis maximum is 100
        chart.invalidate(); // refresh

    }
}
