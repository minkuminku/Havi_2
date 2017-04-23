package com.punbook.mayankgupta.havi;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.ListViewCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;
import com.punbook.mayankgupta.havi.dummy.User;

import java.util.HashMap;
import java.util.Map;

import static com.punbook.mayankgupta.havi.MainActivity.SEPERATOR;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GalleryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GalleryFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String USER_PATH = "param1";
    private static final String GENDER = "param2";
    private static final String AGE = "param3";
    private static final String NUMBER = "param4";

    // Database Constants
    private static final String GENDER_KEY = "gender";
    private static final String AGE_KEY = "age";
    private static final String MOBILE_NUMBER_KEY = "mobile";

    // TODO: Rename and change types of parameters
    private String mUserPath;
    private String mGender;
    private String mAge;
    private String mNumber;

    /*String gender = "NULL";
    String age = "NULL";*/

    public GalleryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param userPath Parameter 1.
     * @param user   Parameter 2.
     * @return A new instance of fragment GalleryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GalleryFragment newInstance(String userPath, User user) {
        GalleryFragment fragment = new GalleryFragment();
        Bundle args = new Bundle();
        args.putString(USER_PATH, userPath);
        args.putString(GENDER, user.getGender()==null?"":user.getGender());
        args.putString(AGE, user.getAge()==null?"":user.getAge());
        args.putString(NUMBER, user.getMobile()==null?"":user.getMobile());
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
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("GALLERY", mUserPath == null ? "NULL" : mUserPath);

        final View view = inflater.inflate(R.layout.fragment_gallery, container, false);

        final Button updateButton = (Button) view.findViewById(R.id.update_payment_button);


        Spinner genderSpinner = (Spinner) view.findViewById(R.id.gender_spinner);
        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.gender_array, android.R.layout.simple_spinner_item);
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
                R.array.age_array, android.R.layout.simple_spinner_item);
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



        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put(SEPERATOR + GENDER_KEY, mGender);
                childUpdates.put(SEPERATOR + AGE_KEY, mAge);
                childUpdates.put(SEPERATOR + MOBILE_NUMBER_KEY, mobileNumber.getText().toString());
                Log.d("GALLERY", " Child UPDATES " + childUpdates);
                Log.d("GALLERY", " path " + mUserPath);


                if(org.apache.commons.lang3.StringUtils.isNotBlank(mUserPath)) {
                    FirebaseDatabase.getInstance().getReference().child(mUserPath).updateChildren(childUpdates);
                }
                //updateButton.setEnabled(false);

            }
        });


        return view;
    }

    private void checkButtonStatus(Button updateButton) {

        if (mGender.equalsIgnoreCase("none") || mAge.equalsIgnoreCase("none") || mNumber.length()<10) {
            updateButton.setEnabled(false);
        } else {
            updateButton.setEnabled(true);
        }

    }

}

