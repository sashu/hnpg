package sa.com.hnpg.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import sa.com.hnpg.R;
import sa.com.hnpg.bean.HLocation;
import sa.com.hnpg.bean.LocationInfo;
import sa.com.hnpg.services.ServiceResponse;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ViewMyPostsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ViewMyPostsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ViewMyPostsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    LocationArrayAdapter adapter;
    ListView listview = null;
    private List<LocationInfo> locationList = new ArrayList<LocationInfo>();

    private OnFragmentInteractionListener mListener;

    public ViewMyPostsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ViewMyPostsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ViewMyPostsFragment newInstance(String param1, String param2) {
        ViewMyPostsFragment fragment = new ViewMyPostsFragment();
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
        View view = inflater.inflate(R.layout.fragment_view_my_posts, container, false);
        listview = (ListView) view.findViewById(R.id.listview);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                LocationInfo info = locationList.get(position);
                Uri gmmIntentUri = Uri.parse("geo:" + info.getLatitude() + "," + info.getLongitude());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                mapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(mapIntent);
            }
        });
        new HttpLoadLocationTask(getActivity()).execute();
        return view;
    }

    private class HttpLoadLocationTask extends AsyncTask<Void, Void, ServiceResponse> {
        Activity activity = null;

        public HttpLoadLocationTask(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected ServiceResponse doInBackground(Void... requestBody) {
            try {
                final String url = "http://54.86.143.9:8080/hnpg/service/location/findall.htm";
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                ResponseEntity greeting = restTemplate.getForEntity(url, ServiceResponse.class);
                return (ServiceResponse)greeting.getBody();
            } catch (Exception e) {
                Toast.makeText(getActivity().getApplicationContext(), "Could not save Location Cuase : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("LocationFragment", e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(ServiceResponse response) {
            if(response == null){
                Toast.makeText(getActivity().getApplicationContext(), "Could not save Location", Toast.LENGTH_SHORT).show();
            }else {
                ArrayList<LinkedHashMap<String,Object>> list = (ArrayList<LinkedHashMap<String,Object>>)response.getData();
                for (LinkedHashMap data:list) {
                    LocationInfo info = new LocationInfo();
                    info.setName((String) data.get("NAME"));
                    info.setCity((String) data.get("CITY"));

                    String mobile = (String) data.get("MOBILE");
                    String email = (String) data.get("EMAIL");

                    info.setMobile(mobile);
                    info.setEmail(email);

                    LinkedHashMap location  = (LinkedHashMap)data.get("LOCATION");

                    HLocation hloc = new HLocation();
                    if(location!=null) {
                        hloc.setLatitude((Double) location.get("latitude"));
                        hloc.setLongitude((Double) location.get("longitude"));
                    }
                    info.setLatitude(hloc.getLatitude() + "");
                    info.setLongitude((hloc.getLongitude()) + "");

                    locationList.add(info);
                }
                adapter = new LocationArrayAdapter(getContext(),locationList);
                listview.setAdapter(adapter);
            }
        }
    }

    private class LocationArrayAdapter extends ArrayAdapter<LocationInfo> {

        private Context context;
        private List<LocationInfo> locationList;

        public LocationArrayAdapter(Context context,List<LocationInfo> objects) {
            super(context, 0, objects);
            this.context = context;
            this.locationList = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            LocationInfo location = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.location_list_item, parent, false);
            }
            // Lookup view for data population
            TextView tvName = (TextView) convertView.findViewById(R.id.locationName);
            TextView tvHome = (TextView) convertView.findViewById(R.id.locationCity);
            // Populate the data into the template view using the data object
            tvName.setText(location.getName()+" | "+location.getCity());
            tvHome.setText(location.getMobile()+ " | "+location.getEmail());
            // Return the completed view to render on screen
            return convertView;
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
