package com.example.inehemias.miniproject4v2;


import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.net.URI;
import java.text.NumberFormat;

public class MainActivity extends AppCompatActivity implements TextWatcher, SeekBar.OnSeekBarChangeListener, AdapterView.OnItemSelectedListener {

    private static final String TAG = "debug";
    private EditText amountInput;

    private TextView percentageView, tipAmountView, totalAmountView, pptAmountView, textViewAmount;
    private ArrayAdapter<CharSequence> adapter;

    private SeekBar seekBar;

    private Spinner spinner;

    private int split_between;

    private double percent = 0.00;
    private double billAmount = 0, tip = 0, total = 0, ppt = 0;

    private boolean roundTip, roundTotal = false;

    private static final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
    private static final NumberFormat percentFormat = NumberFormat.getPercentInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setters();



    }
    public void setters(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        amountInput     = (EditText) findViewById(R.id.amount);
        percentageView  = (TextView) findViewById(R.id.percentage);
        tipAmountView   = (TextView) findViewById(R.id.tipAmount);
        totalAmountView = (TextView) findViewById(R.id.totalAmount);
        seekBar         = (SeekBar) findViewById(R.id.seekBar);
        spinner         = (Spinner) findViewById(R.id.spinner);
        pptAmountView   = (TextView) findViewById(R.id.pptAmount);
        textViewAmount= (TextView) findViewById(R.id.textView_BillAmount);


        adapter = ArrayAdapter.createFromResource(this, R.array.spinner_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        amountInput.addTextChangedListener(this);
        seekBar.setOnSeekBarChangeListener(this);
        spinner.setOnItemSelectedListener(this);

    }

    //    OptionsMenu Methods
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_share) {
            sendSMS(String.format("Bill Amount: %.2f\nTip: %.2f\nTotal: %.2f\nPer Person Amount: %.2f\n", billAmount, tip, total, ppt));
            return true;
        } else if (id == R.id.action_info) {
            dialog().show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //    My Methods
    private void sendSMS(String message) {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"));
        intent.putExtra("sms_body", message);

        if (intent.resolveActivity(getPackageManager()) != null)
            startActivity(intent);
    }
    private AlertDialog dialog() {
        return new AlertDialog.Builder(this).setMessage(R.string.info_message).setTitle(R.string.info_title)
                .setPositiveButton(R.string.positive_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).create();
    }

    private void calc() {


        percentageView.setText(percentFormat.format(percent));

        if (amountInput.getText().toString().isEmpty()) {
            setTexts(0,0, 0);
            billAmount = 0; tip = 0; total = 0; ppt = 0;
            textViewAmount.setText(currencyFormat.format(billAmount));



            return;
        }
        else{

            billAmount = Double.parseDouble(amountInput.getText().toString());
            billAmount=billAmount/100;
            textViewAmount.setText(currencyFormat.format(billAmount));
            tip   =  billAmount * percent;
            if (roundTip) tip = Math.ceil(tip);

            total =  billAmount + tip;
            if (roundTotal) total = Math.ceil(total);

            ppt  = total / split_between;
            textViewAmount.setText(currencyFormat.format(billAmount));

            setTexts(tip, total, ppt);
        }
    }

    private void setTexts(double tip, double total, double ppt) {
        tipAmountView.setText(currencyFormat.format(tip));
        totalAmountView.setText(currencyFormat.format(total));
        pptAmountView.setText(currencyFormat.format(ppt));
    }

    public void onRadioButtonClicked(View view) {
        switch (view.getId()) {
            case R.id.tipBtton:
                roundTip = true;
                roundTotal = false;
                break;
            case R.id.totalButton:
                roundTip = false;
                roundTotal = true;
                break;
            default:
                roundTotal = false;
                roundTip = false;
                break;
        }
        calc();
    }

    //    TextWatcher Methods
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {


        calc();
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    //    OnSeekBarChangeListener Methods
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        percent = (double) progress/100;
        calc();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    //    OnItemSelectedListener Methods
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        split_between = (int) Double.parseDouble(String.valueOf(spinner.getItemAtPosition(position)));
        ppt = total / split_between;
        pptAmountView.setText(currencyFormat.format(ppt));

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}