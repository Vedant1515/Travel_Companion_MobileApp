package com.example.travelcompanion;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button btnCurrency, btnFuel, btnTemperature, btnConvert;
    private Spinner spinnerFrom, spinnerTo;
    private TextInputEditText etInputValue;
    private TextInputLayout inputLayout;
    private TextView tvResult, tvCategoryLabel, tvReference;

    private String currentCategory = "CURRENCY";

    private final List<String> currencyUnits = Arrays.asList(
            "USD", "AUD", "EUR", "JPY", "GBP"
    );

    private final List<String> fuelUnits = Arrays.asList(
            "mpg", "km/L", "L/100km",
            "Gallon(US)", "Litre",
            "Mile", "Kilometre", "Nautical Mile"
    );

    private final List<String> tempUnits = Arrays.asList(
            "Celsius (C)", "Fahrenheit (F)", "Kelvin (K)"
    );

    private double getToUSD(String currency) {
        switch (currency) {
            case "USD": return 1.0;
            case "AUD": return 1.0 / 1.55;
            case "EUR": return 1.0 / 0.92;
            case "JPY": return 1.0 / 148.50;
            case "GBP": return 1.0 / 0.78;
            default:    return 1.0;
        }
    }

    private double getFromUSD(String currency) {
        switch (currency) {
            case "USD": return 1.0;
            case "AUD": return 1.55;
            case "EUR": return 0.92;
            case "JPY": return 148.50;
            case "GBP": return 0.78;
            default:    return 1.0;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupCategoryButtons();
        setupConvertButton();
        selectCategory("CURRENCY");
    }

    private void initViews() {
        btnCurrency     = findViewById(R.id.btnCurrency);
        btnFuel         = findViewById(R.id.btnFuel);
        btnTemperature  = findViewById(R.id.btnTemperature);
        btnConvert      = findViewById(R.id.btnConvert);
        spinnerFrom     = findViewById(R.id.spinnerFrom);
        spinnerTo       = findViewById(R.id.spinnerTo);
        etInputValue    = findViewById(R.id.etInputValue);
        inputLayout     = findViewById(R.id.inputLayout);
        tvResult        = findViewById(R.id.tvResult);
        tvCategoryLabel = findViewById(R.id.tvCategoryLabel);
        tvReference     = findViewById(R.id.tvReference);
    }

    private void setupCategoryButtons() {
        btnCurrency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectCategory("CURRENCY");
            }
        });

        btnFuel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectCategory("FUEL");
            }
        });

        btnTemperature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectCategory("TEMPERATURE");
            }
        });
    }

    private void selectCategory(String category) {
        currentCategory = category;

        btnCurrency.setBackgroundTintList(
                getColorStateList(R.color.colorInactive));
        btnFuel.setBackgroundTintList(
                getColorStateList(R.color.colorInactive));
        btnTemperature.setBackgroundTintList(
                getColorStateList(R.color.colorInactive));

        tvResult.setText("-");
        etInputValue.setText("");
        inputLayout.setError(null);

        switch (category) {
            case "CURRENCY":
                btnCurrency.setBackgroundTintList(
                        getColorStateList(R.color.colorCurrency));
                tvCategoryLabel.setText("Currency Conversion");
                tvCategoryLabel.setTextColor(Color.parseColor("#4CAF50"));
                setupSpinner(spinnerFrom, currencyUnits, 0);
                setupSpinner(spinnerTo, currencyUnits, 1);
                tvReference.setText(
                        "1 USD = 1.55 AUD\n" +
                                "1 USD = 0.92 EUR\n" +
                                "1 USD = 148.50 JPY\n" +
                                "1 USD = 0.78 GBP"
                );
                break;

            case "FUEL":
                btnFuel.setBackgroundTintList(
                        getColorStateList(R.color.colorFuel));
                tvCategoryLabel.setText("Fuel and Distance Conversion");
                tvCategoryLabel.setTextColor(Color.parseColor("#FF9800"));
                setupSpinner(spinnerFrom, fuelUnits, 0);
                setupSpinner(spinnerTo, fuelUnits, 1);
                tvReference.setText(
                        "1 mpg = 0.425 km/L\n" +
                                "1 Gallon (US) = 3.785 Litres\n" +
                                "1 Nautical Mile = 1.852 km\n" +
                                "1 Mile = 1.60934 km"
                );
                break;

            case "TEMPERATURE":
                btnTemperature.setBackgroundTintList(
                        getColorStateList(R.color.colorTemperature));
                tvCategoryLabel.setText("Temperature Conversion");
                tvCategoryLabel.setTextColor(Color.parseColor("#F44336"));
                setupSpinner(spinnerFrom, tempUnits, 0);
                setupSpinner(spinnerTo, tempUnits, 1);
                tvReference.setText(
                        "F = (C x 1.8) + 32\n" +
                                "C = (F - 32) / 1.8\n" +
                                "K = C + 273.15\n" +
                                "Negative temps are valid"
                );
                break;
        }
    }

    private void setupSpinner(Spinner spinner, List<String> items, int defaultIndex) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                items
        );
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(defaultIndex);
    }

    private void setupConvertButton() {
        btnConvert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performConversion();
            }
        });
    }

    private void performConversion() {
        String inputText = "";
        if (etInputValue.getText() != null) {
            inputText = etInputValue.getText().toString().trim();
        }

        if (inputText.isEmpty()) {
            inputLayout.setError("Please enter a value to convert");
            tvResult.setText("-");
            showToast("Please enter a value!");
            return;
        }

        double inputValue;
        try {
            inputValue = Double.parseDouble(inputText);
        } catch (NumberFormatException e) {
            inputLayout.setError("Please enter a valid number");
            tvResult.setText("-");
            showToast("Invalid number entered!");
            return;
        }

        inputLayout.setError(null);

        String fromUnit = spinnerFrom.getSelectedItem().toString();
        String toUnit   = spinnerTo.getSelectedItem().toString();

        if (fromUnit.equals(toUnit)) {
            tvResult.setText(inputValue + " " + toUnit +
                    "\nSame unit - no conversion needed");
            showToast("Source and destination are the same unit!");
            return;
        }

        if (currentCategory.equals("FUEL") && inputValue < 0) {
            inputLayout.setError("Fuel and distance values cannot be negative");
            tvResult.setText("-");
            showToast("Fuel and distance values cannot be negative!");
            return;
        }

        if (currentCategory.equals("TEMPERATURE")) {
            if (fromUnit.equals("Kelvin (K)") && inputValue < 0) {
                inputLayout.setError("Kelvin cannot be below 0");
                tvResult.setText("-");
                showToast("Kelvin cannot be negative!");
                return;
            }
        }

        Double result = null;

        switch (currentCategory) {
            case "CURRENCY":
                result = convertCurrency(fromUnit, toUnit, inputValue);
                break;
            case "FUEL":
                result = convertFuel(fromUnit, toUnit, inputValue);
                break;
            case "TEMPERATURE":
                result = convertTemperature(fromUnit, toUnit, inputValue);
                break;
        }

        if (result == null) {
            tvResult.setText("Conversion not supported between these units");
            showToast("This conversion is not supported");
        } else {
            String formattedResult = String.format("%.4f", result);
            tvResult.setText(inputValue + " " + fromUnit +
                    "\n= " + formattedResult + " " + toUnit);
        }
    }

    private Double convertCurrency(String from, String to, double value) {
        double toUSD   = getToUSD(from);
        double fromUSD = getFromUSD(to);
        return value * toUSD * fromUSD;
    }

    private Double convertFuel(String from, String to, double value) {
        List<String> efficiencyUnits = Arrays.asList("mpg", "km/L", "L/100km");
        List<String> volumeUnits     = Arrays.asList("Gallon(US)", "Litre");
        List<String> distanceUnits   = Arrays.asList("Mile", "Kilometre", "Nautical Mile");

        if (efficiencyUnits.contains(from) && efficiencyUnits.contains(to)) {
            return convertEfficiency(from, to, value);
        } else if (volumeUnits.contains(from) && volumeUnits.contains(to)) {
            return convertVolume(from, to, value);
        } else if (distanceUnits.contains(from) && distanceUnits.contains(to)) {
            return convertDistance(from, to, value);
        } else {
            return null;
        }
    }

    private double convertEfficiency(String from, String to, double value) {
        double kmL;
        switch (from) {
            case "mpg":     kmL = value * 0.425; break;
            case "km/L":    kmL = value; break;
            case "L/100km": kmL = (value != 0) ? 100.0 / value : 0; break;
            default:        kmL = value; break;
        }
        switch (to) {
            case "mpg":     return kmL / 0.425;
            case "km/L":    return kmL;
            case "L/100km": return (kmL != 0) ? 100.0 / kmL : 0;
            default:        return kmL;
        }
    }

    private double convertVolume(String from, String to, double value) {
        double litres;
        switch (from) {
            case "Gallon(US)": litres = value * 3.785; break;
            case "Litre":      litres = value; break;
            default:           litres = value; break;
        }
        switch (to) {
            case "Gallon(US)": return litres / 3.785;
            case "Litre":      return litres;
            default:           return litres;
        }
    }

    private double convertDistance(String from, String to, double value) {
        double km;
        switch (from) {
            case "Mile":          km = value * 1.60934; break;
            case "Kilometre":     km = value; break;
            case "Nautical Mile": km = value * 1.852; break;
            default:              km = value; break;
        }
        switch (to) {
            case "Mile":          return km / 1.60934;
            case "Kilometre":     return km;
            case "Nautical Mile": return km / 1.852;
            default:              return km;
        }
    }

    private Double convertTemperature(String from, String to, double value) {
        double celsius;
        switch (from) {
            case "Celsius (C)":    celsius = value; break;
            case "Fahrenheit (F)": celsius = (value - 32) / 1.8; break;
            case "Kelvin (K)":     celsius = value - 273.15; break;
            default: return null;
        }
        switch (to) {
            case "Celsius (C)":    return celsius;
            case "Fahrenheit (F)": return (celsius * 1.8) + 32;
            case "Kelvin (K)":     return celsius + 273.15;
            default: return null;
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}