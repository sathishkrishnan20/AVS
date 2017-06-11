package com.example.sathish.avs;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.example.sathish.avs.util.Network;
import com.example.sathish.avs.util.NetworkURL;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AddMembers extends AppCompatActivity  {

    private EditText mUserName, mUniqueName, mMoileNo, mEmail, mJob,mDOB,
                     mAddresshomeNo,mAddressPlace, mAddressStreet, mAddressDistrict, maddressState, mAddressCountry, mAddressPinCode;
    private Spinner mBloodGroup;
    private RadioGroup mGenderGroup;
    Button register;

    private static String dbName = "name";
    private static String dbUniquename = "uniqueName";
    private static String dbMobile = "mobileNo";
    private static String dbEmail = "email";
    private static String dbJob = "job";
    private static String dbDOB = "dob";
    private static String dbBloodGroup = "bloodgroup";
    private static String dbGender = "gender";
    private static String dbAddress = "address";


    private String strName, strUniqueName, strMobileNo, strEmail, strJob,strDOB, strBloodGroup, strAddress, strGender;

    private static String networkUrlForAddMember = NetworkURL.url+"addMember.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_members);

        mUserName = (EditText) findViewById(R.id.name1);
        mUniqueName = (EditText) findViewById(R.id.uniqueUserName1);
        mMoileNo = (EditText) findViewById(R.id.mobileNo1);

        mEmail = (EditText) findViewById(R.id.email1);
        mJob = (EditText) findViewById(R.id.job1);
        mDOB = (EditText) findViewById(R.id.DOB1);
        mJob = (EditText) findViewById(R.id.job1);

         mBloodGroup = (Spinner) findViewById(R.id.bgspinner);
         mGenderGroup = (RadioGroup)findViewById(R.id.gender);


        mAddresshomeNo = (EditText) findViewById(R.id.userAddressHomeNo);
        mAddressPlace = (EditText) findViewById(R.id.userAddressPlace);
        mAddressStreet = (EditText) findViewById(R.id.userAddressStreet);
        mAddressDistrict = (EditText) findViewById(R.id.userAddressDistrict);
        maddressState = (EditText) findViewById(R.id.userAddressState);
        mAddressCountry = (EditText) findViewById(R.id.userAddressCountry);
        mAddressPinCode = (EditText) findViewById(R.id.userAddressPincode);


        register = (Button) findViewById(R.id.registerBtn1);

        register.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view)
            {
               validationCheck();
            }

        });
    }

    public void validationCheck() {

            strName = mUserName.getText().toString().trim();
            strUniqueName = mUniqueName.getText().toString().trim();
            strMobileNo = mMoileNo.getText().toString().trim();
            strEmail = mEmail.getText().toString().trim();
            strJob = mJob.getText().toString().trim();
            strDOB =mDOB.getText().toString().trim();

            int selectedId= mGenderGroup.getCheckedRadioButtonId();
            RadioButton radioSexButton=(RadioButton)findViewById(selectedId);
            strGender = radioSexButton.getText().toString();
            strBloodGroup = mBloodGroup.getSelectedItem().toString();

            String strHomeNo = mAddresshomeNo.getText().toString().trim();
            String strStreet = mAddressStreet.getText().toString().trim();
            String strPlace = mAddressPlace.getText().toString().trim();
            String strDistrict = mAddressDistrict.getText().toString().trim();

            String strState = maddressState.getText().toString().trim();
            String strCountry = mAddressCountry.getText().toString().trim();
            String strPincode = mAddressPinCode.getText().toString().trim();

            Network network = new Network();
            if (!network.isOnline(AddMembers.this)) {
                Toast.makeText(AddMembers.this, "No Network Connection", Toast.LENGTH_SHORT).show();
                return;
            }

            if(strHomeNo.isEmpty())
                strHomeNo = "Null";
            if(strStreet.isEmpty())
                strStreet = "Null";

            AwesomeValidation mAwesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
            mAwesomeValidation.addValidation(AddMembers.this, R.id.name1, "[a-zA-Z .]+", R.string.err_name);
            mAwesomeValidation.addValidation(AddMembers.this, R.id.userAddressPlace, "[a-zA-Z .]+", R.string.err_address_place);
            mAwesomeValidation.addValidation(AddMembers.this, R.id.userAddressDistrict, "[a-zA-Z .]+", R.string.err_address_district);
            mAwesomeValidation.addValidation(AddMembers.this, R.id.userAddressState, "[a-zA-Z .]+", R.string.err_address_state);
            mAwesomeValidation.addValidation(AddMembers.this, R.id.userAddressCountry, "[a-zA-Z .]+", R.string.err_address_country);


            if (strName.isEmpty()) {
                Toast.makeText(this, "Please enter Valid Name", Toast.LENGTH_LONG).show();
                return;
            }

            if (strUniqueName.isEmpty()) {
                Toast.makeText(this, "Please enter UserName", Toast.LENGTH_LONG).show();

                return;
            }
            if (strMobileNo.isEmpty()) {
                Toast.makeText(this, "Please enter the Mobile Number", Toast.LENGTH_LONG).show();
                return;
            }
            if (strPincode.isEmpty()) {
                Toast.makeText(this, "Please enter the PinCode", Toast.LENGTH_LONG).show();
                return;
        }
        strAddress = strHomeNo + "," + strStreet + "," +strPlace + "," + strDistrict + "," + strState + "," +strCountry + ","+strPincode;

            if(mAwesomeValidation.validate()) {
              registerUser();
            }




    }
    int progressStatus=1;
    boolean isCanceled =false;
    public void registerUser()
    {

        final ProgressDialog loading =new ProgressDialog(AddMembers.this);
        loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loading.setTitle("Please Wait..");
        loading.setMessage("Loading.........");
        loading.setIndeterminate(false);
        loading.setCancelable(false);

        loading.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener(){
            // Set a click listener for progress dialog cancel button
            @Override
            public void onClick(DialogInterface dialog, int which){
                progressStatus = 0;
                isCanceled = true;
                loading.dismiss();
                // Tell the system about cancellation
            }
        });

        loading.show();

        if(isCanceled) {
            progressStatus = 1;
            return;
        }
        StringRequest stringRequest=new StringRequest(Request.Method.POST, networkUrlForAddMember,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response)
                    {
                        loading.dismiss();

                        showJson(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        loading.dismiss();
                        if (error.networkResponse == null) {
                            if (error.getClass().equals(TimeoutError.class)) {
                                // Show timeout error message
                                Toast.makeText(getApplicationContext(), "Please Check Your Network Connection", Toast.LENGTH_LONG).show();
                            }
                        }
                        else
                            Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                        resetField();
                    }
                })
        {
            @Override
            protected Map<String,String> getParams()
            {
                Map <String,String> params=new HashMap<String,String>();
                params.put(dbName,strName);
                params.put(dbUniquename,strUniqueName);
                params.put(dbMobile, strMobileNo);
                params.put(dbEmail,strEmail);
                params.put(dbJob,strJob);
                params.put(dbDOB,strDOB);
                params.put(dbBloodGroup, strBloodGroup);
                params.put(dbGender, strGender);
                params.put(dbAddress, strAddress);
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue rq= Volley.newRequestQueue(this);
        rq.add(stringRequest);

    }
    private void resetField()
    {
        mUserName.setText("");
        mUniqueName.setText("");
        mMoileNo.setText("");

    }
    private void showJson(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray("result");
            JSONObject responseData = result.getJSONObject(0);
            Toast.makeText(this,responseData.getString("message"),Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),e.toString(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

}
