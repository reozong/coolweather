package com.zhenzong.coolweather.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zhenzong.coolweather.R;
import com.zhenzong.coolweather.bean.City;
import com.zhenzong.coolweather.bean.County;
import com.zhenzong.coolweather.bean.Province;
import com.zhenzong.coolweather.util.HttpUtil;
import com.zhenzong.coolweather.util.JsonUtil;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.zhenzong.coolweather.constant.Global.CHINA_CITY;


public class ChooseAreaFragment extends Fragment {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;
    private ProgressDialog progressDialog;
    private TextView title;
    private Button backButton;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();
    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;
    private Province selectedProvince;
    private City selectedCity;
    //    private County selectedCounty;
    private int currentLevel;


//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//    private String mParam1;
//    private String mParam2;

//    private OnFragmentInteractionListener mListener;
//
//    public ChooseAreaFragment() {
//        // Required empty public constructor
//    }


//    public static ChooseAreaFragment newInstance(String param1, String param2) {
//        ChooseAreaFragment fragment = new ChooseAreaFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }


//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
//        }
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.choose_area, container, false);
        title = (TextView) view.findViewById(R.id.title_text);
        backButton = (Button) view.findViewById(R.id.back_button);
        listView = (ListView) view.findViewById(R.id.listview);
        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (currentLevel) {
                    case LEVEL_PROVINCE:
                        selectedProvince = provinceList.get(position);
                        queryCities();
                        break;
                    case LEVEL_CITY:
                        selectedCity = cityList.get(position);
                        queryCounties();
                        break;
                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (currentLevel) {
                    case LEVEL_CITY:
                        queryCities();
                        break;
                    case LEVEL_COUNTY:
                        queryCounties();
                        break;
                }
            }
        });

        queryProvinces();
    }

    /**
     * 查询全国所有的省份，优先从本地数据库查询，如果没有查询到就去请求服务器数据
     */
    private void queryProvinces() {
        title.setText("中国");
        backButton.setVisibility(View.GONE);
        provinceList = DataSupport.findAll(Province.class);
        if (provinceList.size() > 0) { //数据库有数据
            dataList.clear();
            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
//            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        } else { //本地数据库没有数据就请求服务器
            requestServer(CHINA_CITY, LEVEL_PROVINCE);
        }
    }


    /**
     * 查询省内所有的市，优先从本地数据库查询，如果没有查询到就去请求服务器数据
     */
    private void queryCities() {
        title.setText(selectedProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        cityList = DataSupport.where("provinceid = ?", String.valueOf(selectedProvince.getId())).find(City.class);
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
//            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
        } else {
            String address = CHINA_CITY + "/" + selectedProvince.getProvinceCode();
            requestServer(address, LEVEL_CITY);
        }
    }

    /**
     * 查询市内所有的县，优先从本地数据库查询，如果没有查询到就去请求服务器数据
     */
    private void queryCounties() {
        title.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countyList = DataSupport.where("cityid = ?", String.valueOf(selectedCity.getId())).find(County.class);
        if (countyList.size() > 0) {
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
//            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
        } else {
            String address = CHINA_CITY + "/" + selectedProvince.getProvinceCode() + "/" + selectedCity.getCityCode();
            requestServer(address, LEVEL_COUNTY);
        }
    }

    /**
     * 根据传入的地址、类型从服务器查询省、市、县数据
     *
     * @param address
     * @param type
     */
    private void requestServer(String address, final int type) {
        showProgressDialog();
        HttpUtil.sendHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(), "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().toString();
                boolean result = false;
                switch (type) {
                    case LEVEL_PROVINCE:
                        result = JsonUtil.handleProvinceResponse(json);
                        break;
                    case LEVEL_CITY:
                        result = JsonUtil.handleCityResponse(json, selectedProvince.getId());
                        break;
                    case LEVEL_COUNTY:
                        result = JsonUtil.handleCountyResponse(json, selectedCity.getId());
                        break;
                }
                if (result) { //数据获取成功
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            switch (type) {
                                case LEVEL_PROVINCE:
                                    queryProvinces();
                                    break;
                                case LEVEL_CITY:
                                    queryCities();
                                    break;
                                case LEVEL_COUNTY:
                                    queryCounties();
                                    break;
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * 显示加载框
     */
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     * 关闭加载框
     */
    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }
//
//    /**
//     *
//     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }

}
