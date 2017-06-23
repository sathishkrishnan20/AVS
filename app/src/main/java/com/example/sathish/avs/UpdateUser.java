package com.example.sathish.avs;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UpdateUser extends AppCompatActivity {


    private EditText mUserName, mUniqueName, mMoileNo, mEmail, mJob,mDOB,
            mAddresshomeNo,mAddressPlace, mAddressStreet, mAddressDistrict, maddressState, mAddressCountry, mAddressPinCode;
    private Spinner mBloodGroup;
    private RadioGroup mGenderGroup;
    Button register;

    private Button chooseImage;
    private ImageView userImageView;

    private static String dbName = "name";
    private static String dbUniquename = "uniqueName";
    private static String dbMobile = "mobileNo";
    private static String dbEmail = "email";
    private static String dbJob = "job";
    private static String dbDOB = "dob";
    private static String dbBloodGroup = "bloodgroup";
    private static String dbGender = "gender";
    private static String dbAddress = "address";
    private static String dbImage = "image";


    private String strName, strUniqueName, strMobileNo, strEmail, strJob,strDOB, strBloodGroup, strAddress, strGender;
   private static String networkUrlForUpdateMember = NetworkURL.url+"updateMember.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user);


        mUserName = (EditText) findViewById(R.id.name1_update);
        mUniqueName = (EditText) findViewById(R.id.uniqueUserName1_update);
        mMoileNo = (EditText) findViewById(R.id.mobileNo1_update);

        mEmail = (EditText) findViewById(R.id.email1_update);
        mJob = (EditText) findViewById(R.id.job1_update);
        mDOB = (EditText) findViewById(R.id.DOB1_update);
        mJob = (EditText) findViewById(R.id.job1_update);

        mBloodGroup = (Spinner) findViewById(R.id.bgspinner_updtate);
        mGenderGroup = (RadioGroup)findViewById(R.id.gender_update);

        RadioButton radioButtonForMale = (RadioButton) findViewById(R.id.Male_update);
        RadioButton radioButtonForFeMale = (RadioButton) findViewById(R.id.FeMale_update);

        mAddresshomeNo = (EditText) findViewById(R.id.userAddressHomeNo_update);
        mAddressPlace = (EditText) findViewById(R.id.userAddressPlace_update);
        mAddressStreet = (EditText) findViewById(R.id.userAddressStreet_update);
        mAddressDistrict = (EditText) findViewById(R.id.userAddressDistrict_update);
        maddressState = (EditText) findViewById(R.id.userAddressState_update);
        mAddressCountry = (EditText) findViewById(R.id.userAddressCountry_update);
        mAddressPinCode = (EditText) findViewById(R.id.userAddressPincode_updte);


        register = (Button) findViewById(R.id.registerBtn1_update);
        chooseImage = (Button) findViewById(R.id.chooseImageFileBtn_update);
        userImageView = (ImageView) findViewById(R.id.userImageView_update);


        HashMap userDetails = (HashMap) getIntent().getExtras().get("userDetailsHashMap");
        mUserName.setText(userDetails.get(dbName).toString());
        mUniqueName.setText(userDetails.get(dbUniquename).toString());
        mMoileNo.setText(userDetails.get(dbMobile).toString());
        mEmail.setText(userDetails.get(dbEmail).toString());
        mJob.setText(userDetails.get(dbJob).toString());
        mDOB.setText(userDetails.get(dbDOB).toString());

        if(userDetails.get(dbGender).toString().equals("M"))
            radioButtonForMale.setChecked(true);
        else if(userDetails.get(dbGender).toString().equals("F"))
            radioButtonForFeMale.setChecked(true);

        int BloodGroupPosition = 0;
        switch (userDetails.get(dbBloodGroup).toString()) {
            case "A+":
                BloodGroupPosition = 0;
                break;
            case "A-":
                BloodGroupPosition = 1;
                break;
            case "B+":
                BloodGroupPosition = 2;
                break;
            case "B-":
                BloodGroupPosition = 3;
                break;
            case "O+":
                BloodGroupPosition = 4;
                break;
            case "O-":
                BloodGroupPosition = 5;
                break;
            case "OB+":
                BloodGroupPosition = 6;
                break;
            case "OB-":
                BloodGroupPosition = 7;
                break;
        }

        mBloodGroup.setSelection(BloodGroupPosition);

        mAddresshomeNo.setText(userDetails.get(dbAddress).toString().split(",")[0]);
        mAddressStreet.setText(userDetails.get(dbAddress).toString().split(",")[1]);
        mAddressPlace.setText(userDetails.get(dbAddress).toString().split(",")[2]);
        mAddressDistrict.setText(userDetails.get(dbAddress).toString().split(",")[3]);
        maddressState.setText(userDetails.get(dbAddress).toString().split(",")[4]);
        mAddressCountry.setText(userDetails.get(dbAddress).toString().split(",")[5]);
        mAddressPinCode.setText(userDetails.get(dbAddress).toString().split(",")[6]);

        Picasso.with(getApplicationContext()).load(userDetails.get(dbImage).toString()).error(R.drawable.error).placeholder(R.drawable.placeholder).resize(600,360).into(userImageView); //this is optional the image to display while the url image is downloading.error(0)         //this is also optional if some error has occurred in downloading the image this image would be displayed

        chooseImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                showFileChooser();
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                validationCheck();
            }

        });


    }

    private int PICK_IMAGE_REQUEST = 1;
    private Bitmap bitmap;
    int imageUploadCount = 0;

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //Setting the Bitmap to ImageView
                userImageView.setImageBitmap(bitmap);
                imageUploadCount = 1;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
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
        if (!network.isOnline(UpdateUser.this)) {
            Toast.makeText(UpdateUser.this, "No Network Connection", Toast.LENGTH_SHORT).show();
            return;
        }

        if(strHomeNo.isEmpty())
            strHomeNo = "Null";
        if(strStreet.isEmpty())
            strStreet = "Null";

        AwesomeValidation mAwesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        mAwesomeValidation.addValidation(UpdateUser.this, R.id.name1, "[a-zA-Z .]+", R.string.err_name);
        mAwesomeValidation.addValidation(UpdateUser.this, R.id.userAddressPlace, "[a-zA-Z .]+", R.string.err_address_place);
        mAwesomeValidation.addValidation(UpdateUser.this, R.id.userAddressDistrict, "[a-zA-Z .]+", R.string.err_address_district);
        mAwesomeValidation.addValidation(UpdateUser.this, R.id.userAddressState, "[a-zA-Z .]+", R.string.err_address_state);
        mAwesomeValidation.addValidation(UpdateUser.this, R.id.userAddressCountry, "[a-zA-Z .]+", R.string.err_address_country);


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
        if (imageUploadCount == 0) {
            Toast.makeText(this, "Please Choose the the Image", Toast.LENGTH_LONG).show();
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

        final ProgressDialog loading =new ProgressDialog(UpdateUser.this);
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
        StringRequest stringRequest=new StringRequest(Request.Method.POST, networkUrlForUpdateMember,
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
                String userImageString = getStringImage(bitmap);

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
                params.put(dbImage, userImageString);
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
