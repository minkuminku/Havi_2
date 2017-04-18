package com.punbook.mayankgupta.havi;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;
import com.punbook.mayankgupta.havi.dummy.Status;
import com.punbook.mayankgupta.havi.dummy.Task;

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
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String GENDER_KEY = "gender";
    private static final String AGE_KEY = "age";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    String gender = "NULL";
    String age = "NULL";

    public GalleryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GalleryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GalleryFragment newInstance(String param1, String param2) {
        GalleryFragment fragment = new GalleryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("GALLERY",mParam1);

        final View view =  inflater.inflate(R.layout.fragment_gallery, container, false);

        Spinner spinner = (Spinner) view.findViewById(R.id.gender_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.gender_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gender = (String) parent.getItemAtPosition(position);
               Log.d("GALLERY", gender);
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

        ageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                age = (String) parent.getItemAtPosition(position);
                Log.d("GALLERY", age);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d("GALLERY", "Age Not Selected");
            }
        });

        final Button updateButton = (Button) view.findViewById(R.id.update_payment_button);

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               // TextView textView1 =(TextView) view.findViewById(R.id.task_comments);
              //  String comments =  textView1.getText().toString();
              //  Toast.makeText(getContext(),"submiting " + mParam3,Toast.LENGTH_SHORT).show();
                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put(   SEPERATOR + GENDER_KEY, "Female");
                childUpdates.put(  SEPERATOR + AGE_KEY, "21");
                Log.d("GALLERY", " Child UPDATES "  + childUpdates);
                Log.d("GALLERY", " path "  + mParam1);

                //TODO : add correct path for updating db
                FirebaseDatabase.getInstance().getReference().child(mParam1).updateChildren(childUpdates);
                //updateButton.setEnabled(false);

            }
        });


        return view;
    }

}

