package sa.com.hnpg.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import sa.com.hnpg.HomeActivity;
import sa.com.hnpg.R;
import sa.com.hnpg.bean.HLocation;
import sa.com.hnpg.callback.LocationUtilListener;
import sa.com.hnpg.location.LocationUtils;
import sa.com.hnpg.services.ServiceResponse;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddLocationFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddLocationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddLocationFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Button backBtn;
    private Button saveBtn;
    private RadioButton rdHostel;
    private RadioButton rdPG;
    private EditText name;
    private EditText addLine1;
    private EditText addLine2;
    private EditText city;
    private EditText state;
    private EditText country;
    private EditText zip;
    private EditText mobile;
    private EditText email;
    private Map<String,Object> dataMap = new HashMap<String,Object>();
    private LocationUtils locationUtils;

    private Context context;

    private OnFragmentInteractionListener mListener;

    public AddLocationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddLocationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddLocationFragment newInstance(String param1, String param2) {
        AddLocationFragment fragment = new AddLocationFragment();
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
        View view = inflater.inflate(R.layout.fragment_add_location, container, false);
        backBtn = (Button)view.findViewById(R.id.btnCancelLocation);
        saveBtn = (Button)view.findViewById(R.id.btnSaveLocation);
        rdHostel = (RadioButton)view.findViewById(R.id.rdHostel);
        rdPG = (RadioButton)view.findViewById(R.id.rdPG);
        name = (EditText)view.findViewById(R.id.hnpgName);
        addLine1 = (EditText)view.findViewById(R.id.adressLine1);
        addLine2 = (EditText)view.findViewById(R.id.adressLine2);
        city=(EditText)view.findViewById(R.id.city);
        state=(EditText)view.findViewById(R.id.state);
        country=(EditText)view.findViewById(R.id.country);
        zip = (EditText)view.findViewById(R.id.zip);
        mobile = (EditText)view.findViewById(R.id.mobile);
        email = (EditText)view.findViewById(R.id.email);

        locationUtils = LocationUtils.getInstance(getContext(),getActivity());
        locationUtils.loadPermissions(0, Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION);
        locationUtils.connect(new LocationUtilListener() {
            @Override
            public void onLocationFound(HLocation location) {
                dataMap.put("LOCATION",location);
            }
        });

        rdHostel.setChecked(true);
        dataMap.put("TYPE", "Hostel");

        rdPG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRadioButtonClicked(v);
            }
        });

        rdHostel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRadioButtonClicked(v);
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    setDataMap();
                    new HttpAddLocationTask(getActivity()).execute(dataMap);
                } catch (Exception e) {
                    Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity().getApplicationContext(), "Back Button Clicked", Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }

    public void onRadioButtonClicked(View view){
        //rdHostel.setChecked(false);
        boolean checked = ((RadioButton)(view)).isChecked();

        switch (view.getId()){
            case R.id.rdHostel:
                if(checked){
                    rdPG.setChecked(false);
                    dataMap.put("TYPE","Hostel");
                }
            case R.id.rdPG:{
                if(checked){
                    rdHostel.setChecked(false);
                    dataMap.put("TYPE","PG");
                }
            }
        }
    }

    public void setDataMap(){
        dataMap.put("NAME",name.getText().toString());
        dataMap.put("ADD1",addLine1.getText().toString());
        dataMap.put("ADD2",addLine2.getText().toString());
        dataMap.put("CITY",city.getText().toString());
        dataMap.put("STATE",state.getText().toString());
        dataMap.put("COUNTRY",country.getText().toString());
        dataMap.put("MOBILE",mobile.getText().toString());
        dataMap.put("EMAIL",email.getText().toString());
        dataMap.put("ZIP",zip.getText().toString());
    }

    public void clearDataMap() {
        dataMap.clear();

        name.setText("");
        addLine1.setText("");
        addLine2.setText("");
        city.setText("");
        state.setText("");
        country.setText("");
        mobile.setText("");
        email.setText("");
        zip.setText("");
    }

    private class HttpAddLocationTask extends AsyncTask<Object, Void, ServiceResponse> {
        Activity activity = null;

        public HttpAddLocationTask(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected ServiceResponse doInBackground(Object... requestBody) {
            try {
                final String url = "http://54.86.143.9:8080/hnpg/service/location/insert.htm";
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                HttpEntity<?> entity = new HttpEntity<>(dataMap);
                ResponseEntity greeting = restTemplate.postForEntity(url,entity,ServiceResponse.class);
                return (ServiceResponse)greeting.getBody();
            } catch (Exception e) {
                Toast.makeText(getActivity().getApplicationContext(), "Could not save Location Cuase : "+e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("LocationFragment", e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(ServiceResponse response) {
            if(response == null){
                Toast.makeText(getActivity().getApplicationContext(), "Could not save Location", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(getActivity().getApplicationContext(), "Location Saved", Toast.LENGTH_SHORT).show();
                clearDataMap();
            }
        }
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
