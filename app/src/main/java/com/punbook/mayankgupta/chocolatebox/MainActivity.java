package com.punbook.mayankgupta.chocolatebox;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.punbook.mayankgupta.chocolatebox.dummy.DummyContent;
import com.punbook.mayankgupta.chocolatebox.dummy.Task;
import com.punbook.mayankgupta.chocolatebox.dummy.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ItemFragment.OnListFragmentInteractionListener,
        TaskFragment.OnFragmentInteractionListener {

    private static final String TAG = "MainActivity";
    private static final int RC_SIGN_IN = 123;
    public static final String DB_ROOT = "users";
    public static final String SEPERATOR = "/";
    public static final String TASKS = "tasks";
    public static final String TASK_FRAGMENT_TAG = "TASK_FRAG_TAG";
    public static final String ITEM_FRAGMENT_TAG = "TASK_ITEM_FRAG_TAG";
    public static final String PAYMENT_FRAGMENT_TAG = "PAYMENT_FRAG_TAG";
    private static final int REQUEST_INVITE = 333;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUserDatabaseReference;
    private DatabaseReference mUserTaskDatabaseReference;
    private DatabaseReference mTasksDatabaseReference;
    private ValueEventListener mTaskEventListener;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private String uniqueUserId;

    private final List<Task> tasks = new ArrayList<>();

    private String userTableTaskPath;
    private String userTablePath;
    private String userEmail;

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    private User mUser;

    private View loadingIndicator;
    private Button retryButton;


    public String getUserTablePath() {
        return userTablePath;
    }

    public void setUserTablePath(String userTablePath) {
        this.userTablePath = userTablePath;
    }


    @Override
    protected void onRestart() {
        super.onRestart();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mTasksDatabaseReference = mFirebaseDatabase.getReference().child("tasks");

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.VISIBLE);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        final TextView userName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.userName);
        final TextView userEmail = (TextView) navigationView.getHeaderView(0).findViewById(R.id.userEmail);

        retryButton = (Button) findViewById(R.id.retryButton);
        retryButton.setVisibility(View.GONE);

        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setVisible(false);
                finish();
                startActivity(getIntent());
            }
        });

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in

                    userName.setText(user.getDisplayName());
                    setUserEmail(user.getEmail());
                    userEmail.setText(getUserEmail());

                    //Toast.makeText(getApplicationContext(), "Signed in in activity", Toast.LENGTH_SHORT).show();
                    uniqueUserId = user.getUid();

                    setUserTablePath(DB_ROOT + SEPERATOR + uniqueUserId);
                    setUserTableTaskPath(DB_ROOT + SEPERATOR + uniqueUserId + SEPERATOR + TASKS);
                    mUserDatabaseReference = mFirebaseDatabase.getReference().child(getUserTablePath());
                    mUserTaskDatabaseReference = mFirebaseDatabase.getReference().child(getUserTableTaskPath());
                    mUserDatabaseReference.child("username").setValue(user.getEmail());

                    mUserDatabaseReference.child("token").setValue(FirebaseInstanceId.getInstance().getToken());

                    onSignedInInitialize();


                    new UpdateProgressBarTask().execute();


                } else {
                    // User is signed out
                    onSignedOutCleanup();

                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setLogo(R.drawable.chocobox)
                                    .setProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };


    }

    public void setProcessBar(int visibility) {
        loadingIndicator.setVisibility(visibility);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                // Sign-in succeeded, set up the UI
                //Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                // Sign in was canceled by the user, finish the activity
               // Toast.makeText(this, "Sign in canceled", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    public String getUserTableTaskPath() {
        return userTableTaskPath;
    }

    public void setUserTableTaskPath(String userTableTaskPath) {
        this.userTableTaskPath = userTableTaskPath;
    }

    private void onSignedInInitialize() {
        attachDatabaseReadListener();
    }

    private void onSignedOutCleanup() {
        tasks.clear();
        DummyContent.ITEMS.clear();
        setmUser(null);

        final FragmentManager fm = getSupportFragmentManager();
        for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
        detachDatabaseReadListener();
    }

    private void attachDatabaseReadListener() {
        if (mTaskEventListener == null) {
            mTaskEventListener = new ValueEventListener() {

                private int counter = 1;


                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {


                    tasks.clear();

                    if (dataSnapshot.hasChild(TASKS)) {  // if login for first time

                        setmUser(dataSnapshot.getValue(User.class));

                        Log.d(TAG, mUser.toString());

                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {  // iterate all keys in Users db


                            if (dataSnapshot1.getKey().equals(TASKS)) {
                                for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()) { // iterate all tasks
                                    Task taskMessage = dataSnapshot2.getValue(Task.class);
                                    taskMessage.setPostKey(dataSnapshot2.getKey());
                                    taskMessage.setId("" + counter++);
                                    tasks.add(taskMessage);
                                }
                            }

                        }

                    } else {
                        attachOneTimeTaskListener();
                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                    throw new IllegalArgumentException(" on cancelled, log only");

                }
            };

            mUserDatabaseReference.addValueEventListener(mTaskEventListener);

        }
    }

    private void attachOneTimeTaskListener() {

        mTasksDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                    Task welcomeTask = dataSnapshot1.getValue(Task.class);
                    mUserTaskDatabaseReference.push().setValue(welcomeTask);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void detachDatabaseReadListener() {
        if (mTaskEventListener != null) {
            mUserDatabaseReference.removeEventListener(mTaskEventListener);
            mTaskEventListener = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
        tasks.clear();
        DummyContent.ITEMS.clear();
        detachDatabaseReadListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    //default one
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            AuthUI.getInstance().signOut(this);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_payments) {


            if (getmUser() != null) {

                PaymentFragment paymentFragment = PaymentFragment.newInstance(getUserTablePath(), getmUser());
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                //Toast.makeText(this, paymentFragment.getTag(), Toast.LENGTH_SHORT).show();
                //Log.i("MYTAG", "" + paymentFragment.getId());
                //Log.i("MYTAG", "end" + paymentFragment.getTag());



           /* fragmentTransaction.replace(R.id.content_main, paymentFragment, TAG);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();*/

                boolean fragmentPopped = fragmentManager.popBackStackImmediate(PAYMENT_FRAGMENT_TAG, 0);

                if (!fragmentPopped) { //fragment not in back stack, create it.
                    fragmentTransaction.replace(R.id.content_main, paymentFragment);
                    fragmentTransaction.addToBackStack(PAYMENT_FRAGMENT_TAG);
                    fragmentTransaction.commit();
                }

            }


        } else if (id == R.id.nav_tasks) {


            showTasks(tasks);


        } else if (id == R.id.nav_send) {

            // TODO : Change Email Address to Support email
            final String subject = "[Chocolate Box]".concat("[ ").concat(getUserEmail()).concat(" ]");

            composeEmail(new String[]{"punbook@gmail.com"}, subject);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showTasks(List<Task> tasks) {

        Collections.sort(tasks, new Comparator<Task>() {
            @Override
            public int compare(Task o1, Task o2) {
                if (o1.getStartDate() > o2.getStartDate()) {
                    return -1;
                } else if (o1.getStartDate() < o2.getStartDate()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });

        ItemFragment itemFragment = ItemFragment.newInstance(0, tasks);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            /*fragmentTransaction.replace(R.id.content_main, itemFragment, itemFragment.getTag());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();*/

        boolean fragmentPopped = fragmentManager.popBackStackImmediate(ITEM_FRAGMENT_TAG, 0);

        if (!fragmentPopped) { //fragment not in back stack, create it.
            fragmentTransaction.replace(R.id.content_main, itemFragment);
            fragmentTransaction.addToBackStack(ITEM_FRAGMENT_TAG);
            fragmentTransaction.commit();
        }
    }

    public User getmUser() {
        return mUser;
    }

    public void setmUser(User mUser) {
        this.mUser = mUser;
    }

    private void composeEmail(String[] addresses, String subject) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }


    @Override
    public void onListFragmentInteraction(Task item) {
        //Toast.makeText(this, item.getStatus().toString(), Toast.LENGTH_SHORT).show();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        // TaskFragment taskFragment = TaskFragment.newInstance(item.getStatus().toString(), item.getSummary(), item.getPostKey(), item.getComments(), getUserTableTaskPath());
        TaskFragment taskFragment = TaskFragment.newInstance(item, getUserTableTaskPath());

        // THIS IS CRUTIAL FOR BACK BUTTON FUNCTIONING, IT WILL AVOID ADDING TO STACK multiple times
        // we can also do this to above fragments if we face mem issue or back issue
        boolean fragmentPopped = fragmentManager.popBackStackImmediate(TASK_FRAGMENT_TAG, 0);

        if (!fragmentPopped) { //fragment not in back stack, create it.
            fragmentTransaction.replace(R.id.content_main, taskFragment);
            fragmentTransaction.addToBackStack(TASK_FRAGMENT_TAG);
            fragmentTransaction.commit();
        }


    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        //Toast.makeText(this, "TASK FRAGMENT CLICKED", Toast.LENGTH_SHORT).show();
    }


    private class UpdateProgressBarTask extends AsyncTask<View, Integer, Integer> {


        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param params The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected Integer doInBackground(View... params) {
            int i = 10;
            while (getmUser() == null && i > 0) {

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                i = i - 1;
            }


            return View.INVISIBLE;
        }

        protected void onPostExecute(Integer result) {
            setProcessBar(result);


            if (getmUser() != null) {
                retryButton.setVisibility(View.GONE);
                PaymentFragment paymentFragment = PaymentFragment.newInstance(getUserTablePath(), getmUser());
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();


                boolean fragmentPopped = fragmentManager.popBackStackImmediate(PAYMENT_FRAGMENT_TAG, 0);

                if (!fragmentPopped) { //fragment not in back stack, create it.
                    fragmentTransaction.replace(R.id.content_main, paymentFragment);
                    fragmentTransaction.addToBackStack(PAYMENT_FRAGMENT_TAG);
                    fragmentTransaction.commit();
                }

            } else {
                TextView textView = (TextView) findViewById(R.id.empty_view);
                textView.setText("No Internet Connection");
                retryButton.setVisibility(View.VISIBLE);
            }

        }

    }

}