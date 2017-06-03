package com.punbook.mayankgupta.chocolatebox;


import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;
import com.punbook.mayankgupta.chocolatebox.dummy.User;

import java.util.HashMap;
import java.util.Map;

import static com.punbook.mayankgupta.chocolatebox.MainActivity.SEPERATOR;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PaymentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PaymentFragment extends Fragment {
    private static final String USER_PATH = "param1";
    private static final String GENDER = "param2";
    private static final String AGE = "param3";
    private static final String NUMBER = "param4";
    private static final String PINCODE = "param5";

    // Database Constants
    private static final String GENDER_KEY = "gender";
    private static final String AGE_KEY = "age";
    private static final String MOBILE_NUMBER_KEY = "mobile";
    private static final String PIN_CODE_KEY = "pincode";

    private String mUserPath;
    private String mGender;
    private String mAge;
    private String mNumber;
    private String mPinCode;


    public PaymentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param userPath Parameter 1.
     * @param user     Parameter 2.
     * @return A new instance of fragment PaymentFragment.
     */
    public static PaymentFragment newInstance(String userPath, User user) {
        PaymentFragment fragment = new PaymentFragment();
        Bundle args = new Bundle();
        args.putString(USER_PATH, userPath);
        args.putString(GENDER, user.getGender() == null ? "" : user.getGender());
        args.putString(AGE, user.getAge() == null ? "" : user.getAge());
        args.putString(NUMBER, user.getMobile() == null ? "" : user.getMobile());
        args.putString(PINCODE, user.getPincode() == null ? "" : user.getPincode());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUserPath = getArguments().getString(USER_PATH);
            mGender = getArguments().getString(GENDER);
            mAge = getArguments().getString(AGE);
            mNumber = getArguments().getString(NUMBER);
            mPinCode = getArguments().getString(PINCODE);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("GALLERY", mUserPath == null ? "NULL" : mUserPath);

        final View view = inflater.inflate(R.layout.fragment_payments, container, false);

        final Button updateButton = (Button) view.findViewById(R.id.update_payment_button);

        if(mGender.isEmpty()){
            updateButton.setText("Save");
        }else {
            updateButton.setText("Update");
        }


        Spinner genderSpinner = (Spinner) view.findViewById(R.id.gender_spinner);
        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.gender_array, R.layout.task_spinners);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);

        if (org.apache.commons.lang3.StringUtils.isNotBlank(mGender)) {
            int spinnerPosition = genderAdapter.getPosition(mGender);
            genderSpinner.setSelection(spinnerPosition);
        }


        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mGender = (String) parent.getItemAtPosition(position);
                Log.d("GALLERY", mGender);

                checkButtonStatus(updateButton);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d("GALLERY", "Gender Not Selected");
            }
        });

        Spinner ageSpinner = (Spinner) view.findViewById(R.id.age_spinner);
        ArrayAdapter<CharSequence> ageAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.age_array, R.layout.task_spinners);
        ageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ageSpinner.setAdapter(ageAdapter);

        if (org.apache.commons.lang3.StringUtils.isNotBlank(mAge)) {
            int spinnerPosition = ageAdapter.getPosition(mAge);
            ageSpinner.setSelection(spinnerPosition);
        }

        ageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mAge = (String) parent.getItemAtPosition(position);
                Log.d("GALLERY", mAge);

                checkButtonStatus(updateButton);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d("GALLERY", "Age Not Selected");
            }
        });

        final TextView mobileNumber = (TextView) view.findViewById(R.id.userMobileNumber);

        mobileNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Log.d("GALLERY", "BEFORE  text changes");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //  Log.d("GALLERY", "ON  text changes");
            }

            @Override
            public void afterTextChanged(Editable s) {
                mNumber = s.toString();
                checkButtonStatus(updateButton);
            }
        });

        if (org.apache.commons.lang3.StringUtils.isNotBlank(mNumber)) {
            mobileNumber.setText(mNumber);
        }


        final TextView pinCode = (TextView) view.findViewById(R.id.userPinCode);

        pinCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Log.d("GALLERY", "BEFORE  text changes");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //  Log.d("GALLERY", "ON  text changes");
            }

            @Override
            public void afterTextChanged(Editable s) {
                mPinCode = s.toString();
                checkButtonStatus(updateButton);
            }
        });

        if (org.apache.commons.lang3.StringUtils.isNotBlank(mPinCode)) {
            pinCode.setText(mPinCode);
        }


        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put(SEPERATOR + GENDER_KEY, mGender);
                childUpdates.put(SEPERATOR + AGE_KEY, mAge);
                childUpdates.put(SEPERATOR + MOBILE_NUMBER_KEY, mobileNumber.getText().toString());
                childUpdates.put(SEPERATOR + PIN_CODE_KEY, pinCode.getText().toString());
                Log.d("GALLERY", " Child UPDATES " + childUpdates);
                Log.d("GALLERY", " path " + mUserPath);


                if (org.apache.commons.lang3.StringUtils.isNotBlank(mUserPath) && checkButtonStatus(updateButton)) {
                    FirebaseDatabase.getInstance().getReference().child(mUserPath).updateChildren(childUpdates);
                    Snackbar.make(view, "Updating.....", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    Snackbar.make(view, "Could not update, complete all details!!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }

                if(updateButton.getText().toString().equalsIgnoreCase("Save") && checkButtonStatus(updateButton)){
                    updateButton.setText("Saved");
                }else if(checkButtonStatus(updateButton)){
                    updateButton.setText("Updated");
                }

            }
        });


        return view;
    }

    private boolean checkButtonStatus(Button updateButton) {

        if (mGender.equalsIgnoreCase("none") || mAge.equalsIgnoreCase("none") || mNumber.length() < 10 || mPinCode.length() < 6) {

            updateButton.setTextColor(getResources().getColor(R.color.black));
            updateButton.setBackgroundColor(getResources().getColor(R.color.grey_light));
            return false;
        } else {

            updateButton.setTextColor(getResources().getColor(R.color.tw__solid_white));
            updateButton.setBackgroundColor(getResources().getColor(R.color.authui_colorPrimary));
            return true;
        }

    }

}

