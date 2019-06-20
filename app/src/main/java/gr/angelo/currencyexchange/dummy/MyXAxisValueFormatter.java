package gr.angelo.currencyexchange.dummy;

import android.icu.text.SimpleDateFormat;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.Date;
import java.util.Locale;

public class MyXAxisValueFormatter extends ValueFormatter {
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    @Override
    public String getFormattedValue(float value) {
        return formatter.format(new Date((long) value));

    }
}
