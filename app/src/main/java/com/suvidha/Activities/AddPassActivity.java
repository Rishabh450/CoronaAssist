package com.suvidha.Activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.suvidha.Models.Pass;
import com.suvidha.Models.PassGenerationResult;
import com.suvidha.R;
import com.suvidha.Utilities.APIClient;
import com.suvidha.Utilities.ApiInterface;
import com.suvidha.Utilities.SharedPrefManager;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.suvidha.Utilities.Utils.getAccessToken;

public class AddPassActivity extends AppCompatActivity {
    public static int total_time = 0;
    public static double total_cost = 0.0;
    //Fields
    private final String TAG = "addpass";
    SharedPrefManager sharedPrefManager;
    boolean seniorCitizen = false, urgency = false;
    String urgentMessage = "Not Urgent";
    // Views
    TextInputEditText etName, etDate, etProof, etDestination, etVehicleNumber, etPurpose, etTime, etDuration, etPassType, etPassengerCount, etUrgency;
    Button btnAdd;
    ProgressDialog progressDialog;

    // Retrofit
    ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pass);

        initialiseAllViews();
        intialiseRetrofit();

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPass();
            }
        });
        etTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker();
            }
        });

        etDuration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDurationDialog();
            }
        });

        etPassType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTypes();
            }
        });

        etPassengerCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPassengerCount();
            }
        });

        etUrgency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUrgencyStatus();
            }
        });
        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker();
            }
        });
    }

    private void showDatePicker() {
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        etDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void setUrgencyStatus() {
        LayoutInflater inflater = getLayoutInflater();

        View alertLayout = inflater.inflate(R.layout.dialog_urgency_status, null);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(alertLayout);

        final RadioButton rbUrgent = alertLayout.findViewById(R.id.pass_type_urgent);
        final RadioButton rbNotUrgent = alertLayout.findViewById(R.id.pass_type_not_urgent);
        TextInputEditText etUrgencyMessage = alertLayout.findViewById(R.id.pass_type_urgency_message);

        rbUrgent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rbUrgent.isChecked())
                    etUrgencyMessage.setVisibility(View.VISIBLE);
            }
        });
        rbNotUrgent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rbNotUrgent.isChecked())
                    etUrgencyMessage.setVisibility(View.GONE);
            }
        });
//
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, int id) {
                if (rbUrgent.isChecked()) {
                    urgency = true;
                    urgentMessage = "Urgent. " + etUrgencyMessage.getText().toString().trim();
                    etUrgency.setText(urgentMessage);
                } else if (rbNotUrgent.isChecked()) {
                    urgency = false;
                    urgentMessage = "Not Urgent.";
                    etUrgencyMessage.setVisibility(View.GONE);
                    etUrgency.setText(urgentMessage);
                }

            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        Dialog d = alert.create();
        d.show();
    }

    private void setPassengerCount() {
        LayoutInflater inflater = getLayoutInflater();

        View alertLayout = inflater.inflate(R.layout.dialog_passenger_count, null);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(alertLayout);

        ImageButton inc = alertLayout.findViewById(R.id.increase_passenger);
        ImageButton dec = alertLayout.findViewById(R.id.decrease_passenger);
        CheckBox checkBox = alertLayout.findViewById(R.id.elder_citizen);
        final TextView passengers;

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seniorCitizen = checkBox.isChecked();
            }
        });

        passengers = alertLayout.findViewById(R.id.tv_passengers);

        inc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int p = Integer.parseInt(passengers.getText().toString());
                if (p < 4) {
                    p++;
                    passengers.setText("" + p);
                } else {
                    passengers.setText("" + p);
                    Toast.makeText(AddPassActivity.this, "Maximum passengers reached", Toast.LENGTH_SHORT).show();
                }

                etPassengerCount.setText(passengers.getText().toString());
            }
        });

        dec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int p = Integer.parseInt(passengers.getText().toString());
                if (p > 1) {
                    p--;
                    passengers.setText("" + p);
                } else {
                    passengers.setText("" + p);
                    Toast.makeText(AddPassActivity.this, "Minimum passengers reached", Toast.LENGTH_SHORT).show();
                }
                etPassengerCount.setText(passengers.getText().toString());
            }
        });


        alert.setCancelable(false);

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, int id) {
                etPassengerCount.setText(passengers.getText().toString());
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        Dialog d = alert.create();
        d.show();

    }

    private void showDurationDialog() {
        LayoutInflater inflater = getLayoutInflater();

        View alertLayout = inflater.inflate(R.layout.dialog_duration, null);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(alertLayout);

        ImageButton inc_time = alertLayout.findViewById(R.id.increase_time);
        ImageButton dec_time = alertLayout.findViewById(R.id.decrease_time);
        final TextView minutes, hours;

        minutes = alertLayout.findViewById(R.id.tv_min);
        hours = alertLayout.findViewById(R.id.tv_hours);

        inc_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int min = Integer.parseInt(minutes.getText().toString());
                int hrs = Integer.parseInt(hours.getText().toString());
                if (min > 0) {
                    hrs++;
                    hours.setText("" + hrs);
                    minutes.setText("00");
                } else {
                    min += 30;
                    minutes.setText("" + min);
                }

                hrs = Integer.parseInt(hours.getText().toString());
                min = Integer.parseInt(minutes.getText().toString());
                total_time = (hrs * 60 + min);
                //TODO: calculate cost here
            }
        });

        dec_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int min = Integer.parseInt(minutes.getText().toString());
                int hrs = Integer.parseInt(hours.getText().toString());
                if (min > 0) {
                    min -= 30;
                } else {
                    if (hrs > 1) {
                        hrs--;
                        min = 30;
                    } else if (hrs == 1) {
//                        hrs=1;
//                        min=0;
                        Toast.makeText(AddPassActivity.this, "Time already set to miniimum.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AddPassActivity.this, "Time already set to zero.", Toast.LENGTH_SHORT).show();
                    }
                }
                if (hrs == 0) {
                    hours.setText("00");
                } else {
                    hours.setText("" + hrs);
                }
                if (min == 0) {
                    minutes.setText("00");
                } else {
                    minutes.setText("" + min);
                }
                hrs = Integer.parseInt(hours.getText().toString());
                min = Integer.parseInt(minutes.getText().toString());
                total_time = (hrs * 60 + min);
                //Todo: calculate price here
            }
        });


        alert.setCancelable(false);

        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, int id) {
                String hour = hours.getText().toString().trim();
                String mins = minutes.getText().toString().trim();
                etDuration.setText(hour + ":" + mins);
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        Dialog d = alert.create();
        d.show();


    }

    private void showMessage(String titile, String Message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Log.d("UI thread", "I am the UI thread");
                progressDialog.setTitle(titile);
                progressDialog.setMessage(Message);
                progressDialog.setCancelable(false); // disable dismiss by tapping outside of the dialog
                progressDialog.show();
            }
        });

    }

    private void hideMessage() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Log.d("UI thread", "I am the UI thread from Barcode Fragment");
                progressDialog.hide();

            }
        });
        return;
    }


    private void showTimePicker() {
        TimePickerDialog picker = new TimePickerDialog(AddPassActivity.this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                        String am_pm = (sHour > 12) ? "AM" : "PM";
                        String singleDigitHour = (sHour < 10) ? "0" : "";
                        String singleDigitMinute = (sMinute < 10) ? ":0" : ":";
                        etTime.setText(singleDigitHour + sHour + singleDigitMinute + sMinute);
                    }
                }, 10, 0, true);
        picker.show();
    }


    private void addPass() {
        showMessage("Adding", "Adding your pass to the database");
        String proof = etProof.getText().toString().trim();
        String destination = etDestination.getText().toString().trim();
        String vehicleNumber = etVehicleNumber.getText().toString().trim();
        String purpose = etPurpose.getText().toString().trim();
        String time = etTime.getText().toString().trim();
        String duration = etDuration.getText().toString().trim();
        String passTypeText = etPassType.getText().toString().trim(), passengerCountText = etPassengerCount.getText().toString().trim();
        String name = etName.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        int passType = 0, passengerCount = 1;

        if (proof.length() != 0 && destination.length() != 0 && vehicleNumber.length() != 0 && purpose.length() != 0 && time.length() != 0 && duration.length() != 0 && passTypeText.length() != 0 && passengerCountText.length() != 0 && name.length() != 0 && date.length() != 0) {

            passType = Integer.parseInt(etPassType.getText().toString().trim());
            passengerCount = Integer.parseInt(etPassengerCount.getText().toString().trim());

            String uid = sharedPrefManager.getString(SharedPrefManager.Key.USER_ID);
            Pass pass = new Pass(proof, destination, vehicleNumber, purpose, time, duration, 0, uid, passType, seniorCitizen, passengerCount, urgency, urgentMessage, name, date);
            Call<PassGenerationResult> createPassCall = apiInterface.createPass(getAccessToken(this), pass);
            createPassCall.enqueue(new Callback<PassGenerationResult>() {

                @Override
                public void onResponse(Call<PassGenerationResult> call, Response<PassGenerationResult> response) {
                    PassGenerationResult passGenerationResult = response.body();
                    Log.d(TAG, "onResponse: " + passGenerationResult);
                    if (passGenerationResult.getStatus() == 200) {
                        Toast.makeText(AddPassActivity.this, "Added" + passGenerationResult, Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Log.d(TAG, "onResponse: " + response.message() + " " + response.body().getStatus());
                        Toast.makeText(AddPassActivity.this, "Failed Addition" + getAccessToken(AddPassActivity.this), Toast.LENGTH_SHORT).show();
                    }
                    hideMessage();
                }

                @Override
                public void onFailure(Call<PassGenerationResult> call, Throwable t) {
                    hideMessage();
                }
            });

        } else {
            hideMessage();
            checkAndDisplayError(proof, destination, vehicleNumber, purpose, time, duration, passType + "", passengerCount + "");
        }

    }

    private void setTypes() {
        LayoutInflater inflater = getLayoutInflater();

        View alertLayout = inflater.inflate(R.layout.dialog_pass_type, null);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setView(alertLayout);

        final RadioButton rbIncity = alertLayout.findViewById(R.id.pass_type_incity);
        final RadioButton rbInState = alertLayout.findViewById(R.id.pass_type_instate);
        RadioButton reOustideState = alertLayout.findViewById(R.id.pass_type_outsate);
//
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, int id) {
                if (rbIncity.isChecked()) {
                    setPassTypeData(0);
                } else if (rbInState.isChecked()) {
                    setPassTypeData(1);
                } else if (reOustideState.isChecked()) {
                    setPassTypeData(2);
                }

            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        Dialog d = alert.create();
        d.show();
    }

    private void setPassTypeData(int i) {
        etPassType.setText(i + "");
    }


    private void checkAndDisplayError(String proof, String destination, String vehicleNumber, String purpose, String time, String duration, String passType, String passengerCount) {
        if (proof.length() == 0) {
            etProof.setError("This field cannot be empty");
        }
        if (destination.length() == 0) {
            etDestination.setError("This field cannot be empty");
        }
        if (vehicleNumber.length() == 0) {
            etVehicleNumber.setError("This field cannot be empty");
        }
        if (purpose.length() == 0) {
            etPurpose.setError("This field cannot be empty");
        }
        if (time.length() == 0) {
            etTime.setError("This field cannot be empty");
        }
        if (duration.length() == 0) {
            etDuration.setError("This field cannot be empty");
        }
        if (passType.length() == 0 || passType.equalsIgnoreCase("0")) {
            etPassType.setError("This field cannot be empty");
        }
        if (passengerCount.length() == 0 || passengerCount.equalsIgnoreCase("1")) {
            etPassengerCount.setError("This field cannot be empty");
        }
        if (etUrgency.length() == 0) {
            etUrgency.setError("This field cannot be empty");
        }
        if (etDate.length() == 0) {
            etDate.setError("This field cannot be empty");
        }
        if (etName.length() == 0) {
            etName.setError("This field cannot be empty");
        }


    }

    private void intialiseRetrofit() {
        apiInterface = APIClient.getApiClient().create(ApiInterface.class);
    }

    private void initialiseAllViews() {
        sharedPrefManager = SharedPrefManager.getInstance(this);

        etName = findViewById(R.id.add_pass_name);
        etDate = findViewById(R.id.add_pass_date);
        etProof = findViewById(R.id.add_pass_proof);
        etDestination = findViewById(R.id.add_pass_destination);
        etVehicleNumber = findViewById(R.id.add_pass_vehicle);
        etPurpose = findViewById(R.id.add_pass_purpose);
        etTime = findViewById(R.id.add_pass_time);
        etDuration = findViewById(R.id.add_pass_duration);
        etPassType = findViewById(R.id.add_pass_type);
        etPassengerCount = findViewById(R.id.add_pass_passanger_count);
        etUrgency = findViewById(R.id.add_pass_urgencyStatus);
        btnAdd = findViewById(R.id.add_pass_add);
        progressDialog = new ProgressDialog(this);
    }
}
