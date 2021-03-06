package com.easytask.easytask.frontend.views;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.easytask.easytask.R;
import com.easytask.easytask.frontend.controllers.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Silas on 27-09-2017.
 */

public class CreateUserFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private Button return_btn, create_user_btn;
    private EditText usernameET, passwordET, confirmPassET, nameET, addressET, zipCodeET, cityET, phoneET;
    private String username, password, confirmPass, name, adress, zipCode, city, phone;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference database;
    private ProgressDialog progressDialog;
    private LoadViewTask loadViewTask;
    private SharedPreferences sharedPrefs;
    private CheckBox task_doer, task_creator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_user, container, false);
        return view;

    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        /* Makes sure that the fragment isn't pushed up by the screen keyboard*/
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        /* Get database and firebase references */
        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();

        /* Instantiate views */
        usernameET = (EditText) view.findViewById(R.id.create_email_tbox);
        passwordET = (EditText) view.findViewById(R.id.create_pwd1_tbox);
        confirmPassET = (EditText) view.findViewById(R.id.create_pwd2_tbox);
        nameET = (EditText) view.findViewById(R.id.create_name);
        addressET = (EditText) view.findViewById(R.id.create_addresse);
        zipCodeET = (EditText) view.findViewById(R.id.create_zipcode);
        cityET = (EditText) view.findViewById(R.id.create_city);
        phoneET = (EditText) view.findViewById(R.id.create_phonenumber);
        task_creator = (CheckBox) view.findViewById(R.id.task_creator_checkbox);
        task_doer = (CheckBox) view.findViewById(R.id.task_doer_checkbox);
        return_btn = (Button) view.findViewById(R.id.create_return_btn);
        create_user_btn = (Button) view.findViewById(R.id.create_user_btn);

        /* Add button handlers */
        return_btn.setOnClickListener(this);
        create_user_btn.setOnClickListener(this);

        // Radio button listeners
        task_doer.setOnCheckedChangeListener(this);
        task_creator.setOnCheckedChangeListener(this);

        /* Hides keyboard when user clicks on the layout */
        InputMethodManager inputManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }




    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            if (buttonView.getId() == R.id.task_doer_checkbox) {
                task_creator.setChecked(false);
            }
            if (buttonView.getId() == R.id.task_creator_checkbox) {
                task_doer.setChecked(false);
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (view == return_btn) {
            removeFragment();
        }else if(view == create_user_btn ) {

            password = passwordET.getText().toString().replaceAll("^\\s+", "").replaceAll("\\s+$", "");
            username = usernameET.getText().toString().replaceAll("^\\s+", "").replaceAll("\\s+$", "");
            confirmPass = confirmPassET.getText().toString().replaceAll("^\\s+", "").replaceAll("\\s+$", "");
            name = nameET.getText().toString().replaceAll("^\\s+", "").replaceAll("\\s+$", "");
            adress = addressET.getText().toString().replaceAll("^\\s+", "").replaceAll("\\s+$", "");
            zipCode = zipCodeET.getText().toString().replaceAll("^\\s+", "").replaceAll("\\s+$", "");
            city = cityET.getText().toString().replaceAll("^\\s+", "").replaceAll("\\s+$", "");
            phone = phoneET.getText().toString().replaceAll("^\\s+", "").replaceAll("\\s+$", "");


            String passWordReplace = password.replace(" ","");
            String usernameReplace = username.replace(" ","");
            String confirmPassReplace = confirmPass.replace(" ","");
            String nameReplace = name.replace(" ","");
            String adressReplace = adress.replace(" ","");
            String zipCodeReplace = zipCode.replace(" ","");
            String cityReplace = city.replace(" ","");
            String phoneReplace = phone.replace(" ","");

            if (username != "" && password != "" && confirmPass != "" && name != "" && adress != "" && zipCode != "" && city != "" && phone != "" && usernameReplace != "" && passWordReplace != "" && confirmPassReplace != "" && nameReplace != "" && adressReplace != "" && zipCodeReplace != "" && cityReplace != "" && phoneReplace != "" && task_creator.isChecked() != task_doer.isChecked()) {

                username = usernameET.getText().toString();
                if (password.equals(confirmPass)) {
//                Toast.makeText(getContext(), "Passwords match", Toast.LENGTH_SHORT).show();

                    loadViewTask = new LoadViewTask();
                    loadViewTask.execute();
                } else {
                    Toast.makeText(getContext(), "Passwords does not match!", Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(getContext(), "Husk at udfylde alle felter", Toast.LENGTH_LONG).show();
            }
        }
    }



    public void createFirebaseAuthUser() {
        /* firebase method to create new user*/

        firebaseAuth.createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
//                            Toast.makeText(getContext(), "User created!!", Toast.LENGTH_LONG).show();
                            startNextActivity();
                        } else {
//                            Toast.makeText(getContext(), "Failed to create user.", Toast.LENGTH_LONG).show();
                        }

                    }
                });

    }


    private void startNextActivity() {
        if (firebaseAuth.getCurrentUser() != null) {
//            SharedPreferences.Editor editor = sharedPrefs.edit();
//            editor.putString("email", usernameET.getText().toString()).commit();
//            editor.putString("password", passwordET.getText().toString()).commit();
//            editor.commit();
            if(task_creator.isChecked()) {
                createNewUserInDatabase(username, true, name, adress, zipCode, city, phone);
            }else {
                createNewUserInDatabase(username, false, name, adress, zipCode, city, phone);
            }
            removeFragment();
            startActivity(new Intent(getContext(), MainActivity.class));
        }
    }


    private void createNewUserInDatabase(String email, boolean taskCreator, String name, String address, String zipCode, String city, String phonenumber) {
        User user = new User(email, taskCreator, name, address, zipCode, city, phonenumber);
        if(firebaseAuth.getCurrentUser()!= null) {
            database.child("users").child(firebaseAuth.getCurrentUser().getUid()).setValue(user);
           // database.child("users").child(firebaseAuth.getCurrentUser().getUid()).child("name").setValue(nameET.getText());
        }
    }


    public void removeFragment() {
        if (getFragmentManager().findFragmentById(R.id.fragment_container_login).isVisible()) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.push_up_in, R.anim.push_up_out);
            ft.remove(getFragmentManager().findFragmentById(R.id.fragment_container_login));
            ft.commit();
            LoginActivity.mainLoginBtn.setEnabled(true);

        }
    }



    private class LoadViewTask extends AsyncTask<Void, Integer, Void> {

        @Override
        protected void onCancelled() {
            progressDialog.dismiss();
        }

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(getActivity(), "Gemmer data",
                    "Vent venligst...", false, false);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                synchronized (this) {
                    createFirebaseAuthUser();
                    int count = 0;

                    // Checker om brugeren er null
                    while(!checkUser()) {
                        this.wait(500);

                        count++;
                        if(count >= 200) {
                            loadViewTask.cancel(true);
                            loadViewTask = null;
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
//
            return null;
        }

        // Efter ventetid
        @Override
        protected void onPostExecute(Void result) {
            progressDialog.dismiss();
//            startNextActivity();
        }
    }

    private boolean checkUser() {
        return firebaseAuth.getCurrentUser() != null;
    }

}



