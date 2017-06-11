package com.example.sathish.avs;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.icu.util.RangeValueIterator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;


import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.sathish.avs.util.Network;
import com.example.sathish.avs.util.NetworkURL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Objects;

import static android.view.View.VISIBLE;

public class ViewMembers extends AppCompatActivity {

    private GestureDetector mGesture;
    static final int SWIPE_MIN_DISTANCE = 120;
    static final int SWIPE_THRESHOLD_VELOCITY = 200;

    private TextView userName, userUniqueName, userEmail, userMobileNo, userDob,userBloodGroup, userGender;
    private ImageView userImage;
    private ProgressBar progressBar1;
    LinearLayout layout ;

    String dataArray = "";
    private static String GET_URl = NetworkURL.url+"viewUserLst.php";
    private static String networkUrlForAddMonthlyDetails = NetworkURL.url+"addMonthlyDetails.php";
    private static String networkUrlForGetMonthlyDetails = NetworkURL.url+"monthlyValueLst.php";
    private static String networkUrlForRemoveUser = NetworkURL.url+"removeUserFromLst.php";

    JSONArray result = new JSONArray();
    ArrayList uniqueUserLst = new ArrayList();
    int TRACK = 0;
    int resultCount = 0;
    String userId ="";
    Button storeDetailsBtn;
    String strAddress = "";

    private ListView searchListView;
    ArrayAdapter searchUserAdapter;

    ArrayList userNameLstArray = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_members);
        mGesture = new GestureDetector(this, mOnGesture);
        //userImage = (ImageView) findViewById(R.id.imageViewShow);
        userName = (TextView) findViewById(R.id.userName);
        userUniqueName = (TextView) findViewById(R.id.userUniqueName);
        userEmail = (TextView) findViewById(R.id.userEmail);
        userMobileNo = (TextView) findViewById(R.id.userMobileNo);
        userDob = (TextView) findViewById(R.id.userDOB);
        userBloodGroup = (TextView) findViewById(R.id.userBg);
        userGender = (TextView) findViewById(R.id.userGender);
        progressBar1 = (ProgressBar ) findViewById(R.id.progressBarViewMember);

        searchListView = (ListView)findViewById(R.id.list_view_user);
        searchListView.setVisibility(View.GONE);
        searchListView.setVisibility(View.INVISIBLE);

        layout = (LinearLayout) findViewById(R.id.relativeMonthlyDeails);

        Button addMonthBtn =(Button)findViewById(R.id.addMonth);
        storeDetailsBtn =(Button)findViewById(R.id.viewDetails);
        getUserDetails();
        addMonthBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View child = getLayoutInflater().inflate(R.layout.add_month_linear, null);
                layout.addView(child);
            }
        });

        RelativeLayout monthyDetailsLayout = (RelativeLayout)findViewById(R.id.monthyDetailsRelativeLayout);
        monthyDetailsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(searchListView.getVisibility()== VISIBLE)
                    searchListView.setVisibility(View.GONE);
            }
        });



    }

    int progressStatus=1;
    boolean isCanceled =false;
    public void addDetailsToDB()
    {

        final ProgressDialog loading =new ProgressDialog(ViewMembers.this);
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
        isCanceled = false;
        StringRequest stringRequest=new StringRequest(Request.Method.POST, networkUrlForAddMonthlyDetails,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response)
                    {
                        dataArray = "";
                        loading.dismiss();
                        showJson(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        dataArray = "";
                        loading.dismiss();

                        if (error.networkResponse == null) {
                            if (error.getClass().equals(TimeoutError.class)) {
                                // Show timeout error message
                                Toast.makeText(getApplicationContext(), "Please Check Your Network Connection", Toast.LENGTH_LONG).show();
                            }
                        }
                        else
                            Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();

                    }
                })
        {
            @Override
            protected Map<String,String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("monthlyDetails", dataArray);
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue rq= Volley.newRequestQueue(this);
        rq.add(stringRequest);

    }

    private void showJson(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray("result");
            JSONObject responseData = result.getJSONObject(0);
            Toast.makeText(this,responseData.getString("message"),Toast.LENGTH_LONG).show();
            layout.removeAllViewsInLayout();
            getMonthlyDetailsFromDb();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),e.toString(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    public void getUserDetails() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, GET_URl,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        getDetailsFromJSON(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar1.setVisibility(View.GONE);
                if (error.networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        Toast.makeText(getApplicationContext(), "Please Check Your Network Connection", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue rq = Volley.newRequestQueue(this);
        rq.add(stringRequest);
    }

   

    private void getDetailsFromJSON(String response) {
        try {

            JSONObject jsonObject = new JSONObject(response);
            result = jsonObject.getJSONArray("result");
            uniqueUserLst = getUniqueValues(result);
            setDetailsToUI(TRACK);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),e.toString(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private int getUserResultFirstCount()
    {
        int value =0;
        try {
            for( int count = 0; count < result.length(); count++ ) {
                JSONObject responseData = result.getJSONObject(count);
                String firstCountUserId = responseData.getString("id");
                if (userId.equals(firstCountUserId)) {
                    value = count;
                    break;
                }
            }

        } catch (Exception e) {
            Toast.makeText(this, e.toString(),Toast.LENGTH_LONG).show();
        }
        return value;
    }

    private void setDetailsToUI(int count){
        try {

           userId = String.valueOf(uniqueUserLst.get(count));
            resultCount = getUserResultFirstCount();

            for( resultCount = resultCount; resultCount < result.length(); resultCount++ ) {
                JSONObject responseData = result.getJSONObject(resultCount);
                String getUserId = responseData.getString("id");

                if(!userId.equals(getUserId)) {
                    break;
                }

                userUniqueName.setText(responseData.getString("uniqueName"));
                userMobileNo.setText(responseData.getString("mobileNo"));
                int masterId = Integer.parseInt(responseData.getString("masterId"));
                String value = responseData.getString("value");

                switch (masterId) {
                    case 1:
                        userName.setText(value);
                        break;
                    case 2:
                        userEmail.setText(value);
                        break;
                    case 3:
                        //userName.setText(value);
                        break;
                    case 4:
                        userDob.setText(value);
                        break;
                    case 5:
                        userBloodGroup.setText(value);
                        break;
                    case 6:
                        userGender.setText(value);
                        break;
                    case 7:
                        strAddress = value;
                        break;
                }
            }
            getMonthlyDetailsFromDb();

        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(), "error here" +e.toString(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void getMonthlyDetailsFromDb() {
          progressBar1.setVisibility(View.VISIBLE);
         StringRequest stringRequest=new StringRequest(Request.Method.POST, networkUrlForGetMonthlyDetails,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response)
                {
                    showMonthlyDetailsJson(response);
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error)
                {

                    if (error.networkResponse == null) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            // Show timeout error message
                            Toast.makeText(getApplicationContext(), "Please Check Your Network Connection", Toast.LENGTH_LONG).show();
                        }
                    }
                    else
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                }
            })
    {
        @Override
        protected Map<String,String> getParams()
        {
            Map <String,String> params=new HashMap<String,String>();
            params.put("userId",userId);
            return params;
        }
    };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    RequestQueue rq= Volley.newRequestQueue(this);
        rq.add(stringRequest);

}

    JSONArray monthlyResultJsonArray = new JSONArray();
    private void showMonthlyDetailsJson(String response) {
        try {
            progressBar1.setVisibility(View.GONE);
            JSONObject jsonObject = new JSONObject(response);
            monthlyResultJsonArray = jsonObject.getJSONArray("result");
            setMonthlySubscriptionDetailsToUI();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),e.toString(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }



    private void setMonthlySubscriptionDetailsToUI() {
        try {
            View v = null;

            for (int resultCount = 0; resultCount < monthlyResultJsonArray.length(); resultCount++) {
                JSONObject responseData = monthlyResultJsonArray.getJSONObject(resultCount);

                 View child = getLayoutInflater().inflate(R.layout.add_month_linear, null);
                  layout.addView(child);

                v = layout.getChildAt(resultCount);
                Spinner monthSpinner = (Spinner)((LinearLayout) v).getChildAt(0);
                Spinner yearSpinner = (Spinner)((LinearLayout) v).getChildAt(1);
                EditText tv = (EditText) ((LinearLayout) v).getChildAt(2);

                int month = Integer.parseInt(responseData.getString("month"));
                float money = Float.parseFloat(responseData.getString("money"));
                int year = 0;
                switch (Integer.parseInt(responseData.getString("year")))
                {
                    case 2017:
                        year = 0;
                        break;
                    case 2018:
                        year = 1;
                        break;
                    case 2019:
                        year = 2;
                        break;
                    case 2020:
                        year = 3;
                        break;
                    case 2021:
                        year = 4;
                        break;
                    case 2022:
                        year = 5;
                        break;
                    case 2023:
                        year = 6;
                        break;
                    case 2024:
                        year = 7;
                        break;
                    case 2025:
                        year = 8;
                        break;
                    case 2026:
                        year = 9;
                        break;
                }

                monthSpinner.setSelection(month);
                yearSpinner.setSelection(year);
                tv.setText(String.valueOf(money));
            }


            storeDetailsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int childCount = layout.getChildCount();


                    for(int i=0; i< childCount; i++) {
                        final View v = layout.getChildAt(i);
                        Spinner monthSpinner = (Spinner)((LinearLayout) v).getChildAt(0);
                        Spinner yearSpinner = (Spinner)((LinearLayout) v).getChildAt(1);
                        EditText tv = (EditText) ((LinearLayout) v).getChildAt(2);
                        String curDate =  getDateTime();
                        String strYear = yearSpinner.getSelectedItem().toString();
                        int month = monthSpinner.getSelectedItemPosition();
                        Float strMonthMoney = Float.parseFloat(tv.getText().toString().trim());
                        String activeFlag = "";
                        if(strMonthMoney == 0)
                            activeFlag = "N";
                        else
                            activeFlag = "Y";

                       DecimalFormat df2 = new DecimalFormat(".##");



                        dataArray = dataArray +"("+Integer.parseInt(userId) + ", " +(month)+", " +strYear+ ", "+ df2.format(strMonthMoney) + ", '"+activeFlag+"', '" + curDate + "', '"+ curDate+ "'),";
                    }
                    dataArray = dataArray.substring(0, dataArray.length() - 1);

                    addDetailsToDB();
                }

            });

        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }

    }



    private void removeUserFromLst()
    {
        final ProgressDialog loading =new ProgressDialog(ViewMembers.this);
        loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loading.setTitle("Please Wait..");
        loading.setMessage("Loading.........");
        loading.setIndeterminate(false);
        loading.setCancelable(false);

        loading.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener(){
            // Set a click listener for progress dialog cancel button
            @Override
            public void onClick(DialogInterface dialog, int which){
                // dismiss the progress dialog
                isCanceled = true;
                loading.dismiss();
            }
        });

        loading.show();

        if(isCanceled) {
            return;
        }
        isCanceled =false;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, networkUrlForRemoveUser,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ViewMembers.this);
                        alertDialog.setTitle("Thank you");
                        alertDialog.setMessage(response.split(";")[0]);

                        alertDialog.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent =new Intent(getApplicationContext(), ViewMembers.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                        alertDialog.show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();
                        if (error.networkResponse == null) {
                            if (error.getClass().equals(TimeoutError.class)) {
                                // Show timeout error message
                                Toast.makeText(getApplicationContext(), "Please Check Your Network Connection", Toast.LENGTH_LONG).show();
                            }
                        }
                        else
                            Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            protected Map<String, String> getParams() throws AuthFailureError {
                //  String tempImage = getStringImage(bitmap);

                Map<String, String> params = new Hashtable<String, String>();
                params.put("userId", String.valueOf(userId));
                return params;
            }
        };


        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue rq = Volley.newRequestQueue(this);
        rq.add(stringRequest);

    }

    private MenuItem searchMenuItem;
    private SearchView mSearchView;

    private boolean isSearchResultEmpty = false;
    ArrayList search_result_arraylist = new ArrayList();

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.view_member, menu);

        searchMenuItem = menu.findItem(R.id.action_search_user);
        mSearchView = (SearchView) searchMenuItem.getActionView();

        mSearchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                searchUserAdapter = new ArrayAdapter<String>(ViewMembers.this,android.R.layout.simple_list_item_1, userNameLstArray);
                searchListView.setAdapter(searchUserAdapter);
                searchListView.setVisibility(View.VISIBLE);
            }
        });



        searchListView.setVisibility(View.VISIBLE);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String searchText) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String searchText) {

                search_result_arraylist.clear();
                //searchArrayListWithoutCount.clear();

                for(int i =0 ;i < userNameLstArray.size();i++){
                    if(userNameLstArray.get(i).toString().contains(searchText)) {
                        search_result_arraylist.add(userNameLstArray.get(i).toString());
                        //searchArrayListWithoutCount.add(templePlaceListWithoutCount.get(i).toString());
                    }
                }
                if(search_result_arraylist.isEmpty())
                {
                    isSearchResultEmpty = true;
                    search_result_arraylist.add("Sorry We Cannot Find Any temples");
                    searchListView.setAdapter(searchUserAdapter);
                    //   lv.setVisibility(VISIBLE);

                }
                else {
                    isSearchResultEmpty = false;
                    //searchUserAdapter = new ArrayAdapter<String>(ViewMembers.this,android.R.layout.simple_list_item_1, searchArrayListWithoutCount);
                    searchUserAdapter = new ArrayAdapter<String>(ViewMembers.this,android.R.layout.simple_list_item_1, search_result_arraylist);
                    searchListView.setAdapter(searchUserAdapter);

                }
                return false;
            }

        });

        searchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(isSearchResultEmpty)
                {
                    mSearchView.setVisibility(View.GONE);
                    searchListView.setVisibility(View.GONE);
                    mSearchView.setVisibility(VISIBLE);
                }
                else {

                    String item = (String) search_result_arraylist.get(position);
                    //String templePlacePathi = item.split(";")[0];
                    TRACK = userNameLstArray.indexOf(item);
                    resetFields();
                    setDetailsToUI(TRACK);

                    mSearchView.setVisibility(View.GONE);

                    searchListView.setVisibility(View.GONE);
                    mSearchView.setVisibility(VISIBLE);
                }
            }

        });


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if (id == R.id.action_address) {
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(ViewMembers.this);
            alertDialog.setTitle("Address");
            alertDialog.setMessage(strAddress.split(",")[0] + ", " +strAddress.split(",")[1]+"\n"+strAddress.split(",")[2]+"\n"+strAddress.split(",")[3]+"\n"+strAddress.split(",")[4]+"\n"+strAddress.split(",")[5]+"\n"+strAddress.split(",")[6]);
            alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            alertDialog.show();
        }

        else if(id ==R.id.action_search_user)
        {
            mSearchView.setVisibility(VISIBLE);
            searchUserAdapter = new ArrayAdapter<String>(ViewMembers.this,android.R.layout.simple_list_item_1, userNameLstArray);
            searchListView.setAdapter(searchUserAdapter);
            //isSearchButonPressed =true;
            searchListView.setVisibility(View.VISIBLE);
        }
        else if(id ==R.id.action_remove_user)
        {
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(ViewMembers.this);
            alertDialog.setTitle("Remove");
            alertDialog.setMessage("Are you Sure to Remove User");
            alertDialog.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    removeUserFromLst();


                }
            });
            alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.cancel();

                }
            });
            alertDialog.show();

        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean handled = super.dispatchTouchEvent(ev);
        handled = mGesture.onTouchEvent(ev);
        return handled;
    }

    private GestureDetector.OnGestureListener mOnGesture = new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {


            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {

                if(TRACK== uniqueUserLst.size()-1)
                {
                    Toast.makeText(getApplicationContext(),"You Reached a limit", Toast.LENGTH_SHORT).show();
                }

                moveNext();
                //    Toast.makeText(getApplicationContext(), "Left to Right Swap Performed", Toast.LENGTH_SHORT).show();
                return true;
            } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {

                //   Toast.makeText(getApplicationContext(), " Right to Left Swap Performed", Toast.LENGTH_SHORT).show();
                if( TRACK==0) {
                    Toast.makeText(getApplicationContext(),"You Reached a limit", Toast.LENGTH_SHORT).show();
                }

                movePrevious();
                return true;
            }

            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            // Toast.makeText(getApplicationContext(), " Down Swap Performed", Toast.LENGTH_SHORT).show();

            return false;
        }
    };

    private void moveNext(){


        if( TRACK < uniqueUserLst.size() -1) {
            TRACK++;
            resetFields();
           // scrollView.fullScroll(ScrollView.FOCUS_UP);
            setDetailsToUI(TRACK);
        }
    }

    private void movePrevious(){

        if(TRACK > 0){
            TRACK--;
            resetFields();
            //resultCount = resultCount - (tempCount *2);
           // scrollView.fullScroll(ScrollView.FOCUS_UP);
            setDetailsToUI(TRACK);
        }
    }
    private void resetFields() {
        userName.setText("");
        userUniqueName.setText("");
        userBloodGroup.setText("");
        userDob.setText("");
        userMobileNo.setText("");
        userEmail.setText("");
        userGender.setText("");
        layout.removeAllViewsInLayout();
        layout.removeAllViews();
    }

    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }


    private ArrayList getUniqueValues(JSONArray jsonData) {

        ArrayList<String> array =new ArrayList<String>();
        ArrayList<String> UserNameArray =new ArrayList<String>();
        ArrayList<String> uniqueArray =new ArrayList<String>();
        try {

            for(int count =0; count < jsonData.length(); count++ ) {
                JSONObject data = jsonData.getJSONObject(count);
                array.add(data.getString("id"));
                UserNameArray.add(data.getString("uniqueName"));
            }
            for(String s : array) {
                if(!uniqueArray.contains(s)) {
                    uniqueArray.add(s);
                }
            }

            for(String s : UserNameArray) {
                if(!userNameLstArray.contains(s)) {
                    userNameLstArray.add(s);
                }
            }
         return  uniqueArray;

        } catch (JSONException e) {
            Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show();
            return  uniqueArray;
        }
    }

}
