package com.punbook.mayankgupta.havi;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;
import com.punbook.mayankgupta.havi.dummy.Status;
import com.punbook.mayankgupta.havi.dummy.Task;

import java.util.HashMap;
import java.util.Map;

import static android.app.AlertDialog.Builder;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TaskFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TaskFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TaskFragment extends Fragment {

    private static final String SEPERATOR = "/";

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private static final String ARG_PARAM4 = "param4";
    private static final String ARG_PARAM5 = "param5";

    private static int counter = 0;

    private String mStatus;
    private String mSummary;
    private String mPostKey;
    private String mComments;
    private String mTaskSubmitPath;

    private OnFragmentInteractionListener mListener;

    public TaskFragment() {
        // Required empty public constructor
    }

   /* public static TaskFragment newInstance(String param1, String param2, String param3, String param4, String param5) {
        TaskFragment fragment = new TaskFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        args.putString(ARG_PARAM4, param4);
        args.putString(ARG_PARAM5, param5);

        fragment.setArguments(args);
        return fragment;
    }*/

    public static TaskFragment newInstance(Task task, String taskSubmitPath) {
        TaskFragment fragment = new TaskFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, task.getStatus().toString());
        args.putString(ARG_PARAM2, task.getSummary());
        args.putString(ARG_PARAM3, task.getPostKey());
        args.putString(ARG_PARAM4, task.getComments());
        args.putString(ARG_PARAM5, taskSubmitPath);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mStatus = getArguments().getString(ARG_PARAM1);
            mSummary = getArguments().getString(ARG_PARAM2);
            mPostKey = getArguments().getString(ARG_PARAM3);
            mComments = getArguments().getString(ARG_PARAM4);
            mTaskSubmitPath = getArguments().getString(ARG_PARAM5);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_task, container, false);

        final TextView taskStatus = (TextView) view.findViewById(R.id.task_status);
        taskStatus.setText(mStatus);

        switch (Status.parse(mStatus)) {
            case ACTIVE:
                taskStatus.setBackgroundColor(getResources().getColor(R.color.authui_colorPrimary));
                break;
            case EXPIRED:
                taskStatus.setBackgroundColor(getResources().getColor(R.color.black));
                break;

            case PAID:
                taskStatus.setBackgroundColor(getResources().getColor(R.color.gold));
                break;

            case SUBMITTED:
                taskStatus.setBackgroundColor(getResources().getColor(R.color.green));
                break;

        }

        TextView task_summary = (TextView) view.findViewById(R.id.task_summary);
        task_summary.setText(mSummary);

        TextView task_comments = (TextView) view.findViewById(R.id.task_comments);
        task_comments.setText(mComments);

        final Button submitButton = (Button) view.findViewById(R.id.task_submit_button);

        final EditText editText = (EditText) view.findViewById(R.id.task_comments);

        if (Status.parse(mStatus) == Status.ACTIVE) {
            submitButton.setEnabled(true);
            editText.setEnabled(true);
            submitButton.setVisibility(View.VISIBLE);

        } else {
            submitButton.setEnabled(false);
            editText.setEnabled(false);
            submitButton.setVisibility(View.GONE);
        }


        final Builder alertDialog = new Builder(getContext());

        alertDialog.setTitle("SUBMIT TASK");

        alertDialog.setMessage("Are you sure you want to submit this task ?");

        // alertDialog.setIcon(R.drawable.delete);

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                TextView textView1 = (TextView) view.findViewById(R.id.task_comments);
                String comments = textView1.getText().toString();
                Toast.makeText(getContext(), "submiting " + mPostKey, Toast.LENGTH_SHORT).show();
                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put(SEPERATOR + mPostKey + SEPERATOR + Task.STATUS_KEY, Status.SUBMITTED);
                childUpdates.put(SEPERATOR + mPostKey + SEPERATOR + Task.COMMENTS_KEY, comments);
                FirebaseDatabase.getInstance().getReference().child(mTaskSubmitPath).updateChildren(childUpdates);
                submitButton.setEnabled(false);
                submitButton.setVisibility(View.GONE);
                taskStatus.setText(Status.SUBMITTED.toString());
                taskStatus.setBackgroundColor(getResources().getColor(R.color.green));

            }
        });

        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.show();
            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
