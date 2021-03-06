package com.easytask.easytask.frontend.views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.easytask.easytask.R;
import com.easytask.easytask.frontend.controllers.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import es.dmoral.toasty.Toasty;

/**
 * Created by Burim on 02-11-2017.
 */

public class EditTaskFragment extends Fragment implements View.OnClickListener {

    private Button return_btn, edit_task_btn;
    private EditText titleET, descriptionET, paymentET;
    private String title, description, payment, userId, taskId;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference database;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_task, container, false);
        return view;

    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        /* Makes sure that the fragment isn't pushed up by the screen keyboard*/
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        firebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();

        titleET = (EditText) view.findViewById(R.id.edit_task1_tbox);
        descriptionET = (EditText) view.findViewById(R.id.edit_task2_tbox);
        paymentET = (EditText) view.findViewById(R.id.edit_task3_tbox);


        return_btn = (Button) view.findViewById(R.id.edit_return_btn);
        edit_task_btn = (Button) view.findViewById(R.id.edit_task_btn);

        /* Add button handlers */
        return_btn.setOnClickListener(this);
        edit_task_btn.setOnClickListener(this);

        taskId = getArguments().getString("taskid");

        database.addListenerForSingleValueEvent(new ValueEventListener() {


            @Override
            public void onDataChange(DataSnapshot snap) {


                titleET.setText(snap.child("tasks").child(taskId).child("title").getValue().toString());
                descriptionET.setText(snap.child("tasks").child(taskId).child("description").getValue().toString());
                paymentET.setText(snap.child("tasks").child(taskId).child("payment").getValue().toString());

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        /* Hides keyboard when user clicks on the layout */
        InputMethodManager inputManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void writeNewTask(String title, String description, String payment, String taskId, String userId) {
        if(firebaseAuth.getCurrentUser()!= null) {
            database.child("tasks").child(taskId).child("title").setValue(title);
            database.child("tasks").child(taskId).child("description").setValue(description);
            database.child("tasks").child(taskId).child("payment").setValue(payment,  new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        Toasty.error(getContext(), "Kunne ikke opdatere! Prøv igen.", Toast.LENGTH_SHORT).show();
                        edit_task_btn.setEnabled(true);
                    } else {
                        Toasty.success(getContext(), "Opgaven blev opdateret!", Toast.LENGTH_SHORT, true).show();
                        hideFragment();

                    }
                }
            });



        }
    }

    @Override
    public void onClick(View view) {
        if (view == return_btn) {
            hideFragment();
        }else if(view == edit_task_btn) {

            edit_task_btn.setEnabled(false);

            title = titleET.getText().toString().replaceAll("^\\s+", "").replaceAll("\\s+$", "");
            description = descriptionET.getText().toString().replaceAll("^\\s+", "").replaceAll("\\s+$", "");
            payment = paymentET.getText().toString().replaceAll("^\\s+", "").replaceAll("\\s+$", "");

            String titleReplace = title.replaceAll("\\s+","");
            String descriptionReplace = description.replaceAll("\\s+","");
            String paymentReplace = payment.replaceAll("\\s+","");


            firebaseAuth = FirebaseAuth.getInstance();

            if(firebaseAuth.getCurrentUser() == null){
                Intent intent1 = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent1);
                getActivity().finish();
            }
            else {
                userId = firebaseAuth.getCurrentUser().getUid();
            }

            if (title != "" && description != "" && payment != "" && titleReplace != "" && descriptionReplace != "" && paymentReplace != "") {


            database.addListenerForSingleValueEvent(new ValueEventListener() {



                @Override
                public void onDataChange(DataSnapshot snap) {

                    writeNewTask(title, description, payment, taskId, userId);

                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    edit_task_btn.setEnabled(true);
                }
            });

        } else {
            Toasty.error(getContext(), "Ups Husk at udfyld alle felter", Toast.LENGTH_LONG).show();
                edit_task_btn.setEnabled(true);
        }

        }
    }


    public void hideFragment() {
        if (getFragmentManager().findFragmentById(R.id.fragment_container_main).isVisible()) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.push_up_in, R.anim.push_up_out);
            ft.remove(getFragmentManager().findFragmentById(R.id.fragment_container_main));
            ft.commit();

        }
    }
}
