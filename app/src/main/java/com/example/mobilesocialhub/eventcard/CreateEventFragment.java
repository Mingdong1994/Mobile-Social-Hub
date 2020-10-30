package com.example.mobilesocialhub.eventcard;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.mobilesocialhub.R;
import com.example.mobilesocialhub.databinding.ActivityCreateEventBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class CreateEventFragment extends Fragment implements View.OnClickListener, DatePickerDialog.OnDateSetListener,
        com.wdullaer.materialdatetimepicker.time.TimePickerDialog.OnTimeSetListener {
    ActivityCreateEventBinding mBinding;
    FirebaseDatabase database;
    EditText eventNameText;
    EditText eventPlaceText;
    TextView eventDateText;
    TextView eventTimeText;
    Button datePickBtn;
    Button timePickBtn;
    Button submitBtn;
    DatabaseReference eventRef;
    String datePublished;
    FragmentManager fm;
    OnButtonClick onButtonClick;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fm = getFragmentManager();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater,R.layout.activity_create_event,  container, false);
        database = FirebaseDatabase.getInstance();
        eventRef = database.getReference().child("Events");
        eventNameText = mBinding.nameTextView;
        eventPlaceText = mBinding.placeTextView;
        eventDateText = mBinding.dateText;
        eventTimeText = mBinding.timeText;
        datePickBtn = mBinding.datePickBtn;
        timePickBtn = mBinding.timePickBtn;
        submitBtn = mBinding.createEventBtn;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(System.currentTimeMillis());
        datePublished = simpleDateFormat.format(date);

        submitBtn.setOnClickListener(this);
        datePickBtn.setOnClickListener(this);
        timePickBtn.setOnClickListener(this);
        return mBinding.getRoot();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.create_event_btn:
                String address = eventPlaceText.getText().toString();
                String usernamePublished = eventNameText.getText().toString();
                String datePublished = this.datePublished;
                String eventDate = this.eventDateText.getText().toString();
                String eventTime = this.eventTimeText.getText().toString();
                String uuid = UUID.randomUUID().toString();
                Event newEvent = new Event(usernamePublished, datePublished, eventDate, eventTime, address, uuid);
                eventRef.child(uuid).setValue(newEvent);
                Toast.makeText(v.getContext(), "Event has been created", Toast.LENGTH_LONG).show();
                if (this.onButtonClick != null) {
                    this.onButtonClick.onclick(v);
                }
                break;
            case R.id.date_pick_btn:
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        this,
                        now.get(Calendar.YEAR), // Initial year selection
                        now.get(Calendar.MONTH), // Initial month selection
                        now.get(Calendar.DAY_OF_MONTH) // Inital day selection
                );
                dpd.show(getFragmentManager(), "Datepickerdialog");
                break;
            case R.id.time_pick_btn:
                com.wdullaer.materialdatetimepicker.time.TimePickerDialog tpd = com.wdullaer.materialdatetimepicker.time.TimePickerDialog.newInstance(
                        this,
                        true
                );
                tpd.show(getFragmentManager(), "Datepickerdialog");
                break;


        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = new String(year+"-" + monthOfYear + "-" + dayOfMonth);
        eventDateText.setText(date);
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        String time = new String(hourOfDay+ ":" + minute);
        eventTimeText.setText(time);
    }

    public CreateEventFragment.OnButtonClick getOnButtonClick() {
        return this.onButtonClick;
    }

    public void setOnButtonClick(CreateEventFragment.OnButtonClick onButtonClick) {
        this.onButtonClick = onButtonClick;
    }

    public interface OnButtonClick{
        void onclick(View view);
    }
}