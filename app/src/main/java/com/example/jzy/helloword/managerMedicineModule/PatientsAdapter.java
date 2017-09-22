package com.example.jzy.helloword.managerMedicineModule;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.jzy.helloword.R;
import com.example.jzy.helloword.xmlrpcLib.XMLRPCClient;
import com.example.jzy.helloword.xmlrpcLib.XMLRPCException;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by xiashu on 17-9-12.
 */

public class PatientsAdapter extends RecyclerView.Adapter<PatientsAdapter.ViewHolder> {
    private List<Patient> mPatients;
    Context context;
    private String medicineBoxIP;
    private String userInfoURL;
    private int queryState;

    private int putMedicineState;

    private final static int  HAVE_TAKEN_MEDICINE = 1;
    private final static int  DONT_TAKEN_MEDICINE = 0;
    private final static int  PATIETN_DONT_TAKEN_MEDICINE = 2;

    private static int FLAG = 0;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView patient_id;
        TextView patient_name;
        ListView medicine_list;

        public ViewHolder(View itemView) {
            super(itemView);
            patient_id = (TextView) itemView.findViewById(R.id.patient_id);
            patient_name = (TextView) itemView.findViewById(R.id.patient_name);
            medicine_list = (ListView) itemView.findViewById(R.id.medicine_list);
        }
    }

    public PatientsAdapter(List<Patient> patientList, Context context, String medicineBoxIP, String userInfoURL) {
        mPatients = patientList;
        this.context = context;
        this.medicineBoxIP = medicineBoxIP;
        this.userInfoURL = userInfoURL;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_inforcard, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }


    private void patientDontTakeMedDigShow(final int position){
        final Patient patient=mPatients.get(position);
        final MaterialDialog mMaterialDialog = new MaterialDialog(context);

//                                    if(putMedicineState == PATIETN_DONT_TAKEN_MEDICINE)
        mMaterialDialog.setMessage(patient.getName() + "的药未拿取，请通知病人" )
                .setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        putMedicine(position);
                        mMaterialDialog.dismiss();

                    }
                });
        mMaterialDialog.show();
    }

    private void queryPutMedicineDigShow(final int position){
        final MaterialDialog MaterialDialog = new MaterialDialog(context);
        MaterialDialog.setMessage("请打开药盒为该病人放药")
                .setPositiveButton("放置已完成", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        putMedicine(position);
                        MaterialDialog.dismiss();

                    }
                })
                .setNegativeButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MaterialDialog.dismiss();
                    }
                });
        MaterialDialog.show();
    }

    @Override
    public void onBindViewHolder(PatientsAdapter.ViewHolder holder, final int position) {


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Random random=new Random();
                qureyBoxStatus(position);
                //wait for network thread get the result,can add ProgressDialog remind user
                while (FLAG != 1) {
                }
                //假设药盒已满
                if(queryState == DONT_TAKEN_MEDICINE)
                {
                   final MaterialDialog mMaterialDialog = new MaterialDialog(context);
                    mMaterialDialog.setMessage("药盒已满")
                            .setPositiveButton("确定", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    mMaterialDialog.dismiss();
                                }
                            })
                            .setNegativeButton("取消", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mMaterialDialog.dismiss();
                                }
                            });

                    mMaterialDialog.show();
                }
                //假设可以成功放药
                else{
                    if(putMedicineState == HAVE_TAKEN_MEDICINE){
                        final MaterialDialog mMaterialDialog = new MaterialDialog(context);
                        mMaterialDialog.setMessage("确定要为该病人放药吗")
                                .setPositiveButton("确定", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mMaterialDialog.dismiss();
                                        queryPutMedicineDigShow(position);
                                    }
                                })
                                .setNegativeButton("取消", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mMaterialDialog.dismiss();
                                    }
                                });
                        mMaterialDialog.show();
                    }else if(putMedicineState == PATIETN_DONT_TAKEN_MEDICINE){
                        patientDontTakeMedDigShow(position);
                    }
                }

            }
        });

        Patient patient = mPatients.get(position);
        holder.patient_id.setText(patient.getId());
        holder.patient_name.setText(patient.getName());



        ArrayList<MedicineInfo> medicineInfos=patient.getMedicineInfos();
        int count=medicineInfos.size();
        String[] name=new String[count];
        String[] num=new String[count];
        String[] info=new String[count];

        for(int i=0;i<count;i++)
        {
            MedicineInfo medicineInfo=medicineInfos.get(i);
            name[i]=medicineInfo.getMedicineName();
            num[i]=medicineInfo.getMedicineCount();
            info[i]=medicineInfo.getMedicineDosage();

        }

      /*  String[] name={"阿莫西林","青霉素","清热感冒颗粒","板蓝根"};
        int[] num={1,3,4,2};
        String[] info={"3次/日，1粒/次","无","3次/日，2包/次，冲服","3次/日，1包/次"};*/
        List<Map<String,Object>> list_map=new ArrayList<Map<String,Object>>();


        for (int i = 0; i < count; i++) {
            Map<String,Object> items=new HashMap<String,Object>();
            items.put("name",name[i]);
            items.put("num",num[i]);
            items.put("info",info[i]);
           // medicines.add(new medicine_item("药品"+i,(i+2),"一日三次，每次两粒"));
            list_map.add(items);
        }
        SimpleAdapter simpleAdapter=new SimpleAdapter(context,list_map,R.layout.listitem_medicine,
                new String[]{"name","num","info"},new int[]{R.id.m_name,R.id.m_num,R.id.m_infor});

        holder.medicine_list.setAdapter(simpleAdapter);
        setListVIewHeight(holder.medicine_list);

    }

    private void qureyBoxStatus(int position){
        final Patient patient=mPatients.get(position);
        new Thread(new Runnable() {
            @Override
            public void run() {
                XMLRPCClient client = new XMLRPCClient(medicineBoxIP);
                try {
                    queryState = (Integer) client.call("QueryAvailable");
                    Log.i("XMLRPC Test", "QueryMedicine queryState  = " + queryState);
                    if(queryState != DONT_TAKEN_MEDICINE){
                        putMedicineState = (Integer) client.call("Put",patient.getPatientRFID());
                    }
                    FLAG = 1;
                } catch (XMLRPCException e) {
                    Log.i("XMLRPC Test", "Error", e);
                }
            }
        }).start();
    }

    private void putMedicine(int position){
        final Patient patient=mPatients.get(position);
        new Thread(new Runnable(){
            @Override
            public void run() {

                try {
//                    String userInfoURL = context.getResources().getString(R.string.pref_user_info_ip_default);
                    String URL = userInfoURL;
                    URL += "/UpdateUIDMID";
                    URL url = new URL(URL);
                    Log.i("Sys", "URL:" + URL);
                    JSONObject queryJson = new JSONObject();

                    //获取药物id列表
//                    ArrayList<String> medicine_id_list=new ArrayList<String>();
                    JSONArray  medicine_id_list = new JSONArray();
                    ArrayList<MedicineInfo> medicineInfos=patient.getMedicineInfos();
                    for(int i=0;i<medicineInfos.size();i++)
                    {
                        medicine_id_list.put(medicineInfos.get(i).getMedicineId());
//                        medicine_id_list.add(medicineInfos.get(i).getMedicineId());
                    }

                    Calendar calendar=Calendar.getInstance();

                    queryJson.put("user_id",patient.getId());
                    Log.i("Sys", "patientID:" + patient.getId());
                    Log.i("Sys", "patientName:" + patient.getName());
                    queryJson.put("medicine_id_arraylist",medicine_id_list);
                    queryJson.put("date_yyyy",String.valueOf(calendar.get(Calendar.YEAR)));
                    queryJson.put("date_mm",String.format("%02d",calendar.get(Calendar.MONTH)+1));
                    queryJson.put("date_dd",String.valueOf(calendar.get(Calendar.DATE)));


                    URLConnection conn = url.openConnection();
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Content-Type", "application/json");
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                    wr.write(queryJson.toString());
                    wr.flush();
                    Log.i("Sys", "jsonObject:" + queryJson.toString());
                    wr.close();

                    InputStream responseStream = conn.getInputStream();
                    String response = drainStream(responseStream);
                    JSONObject responseJSON = new JSONObject(response);
                    JSONObject result = responseJSON.getJSONObject("result");
                    int isSuccess = result.getInt("updateSuccess");

                    Log.i("Sys","isSuccess:"+isSuccess);

                }catch (Exception ex){
                    Log.i("Send Infor", "Error", ex);
                }
            }
        }).start();


    }

    private static String drainStream(InputStream in) {
        Scanner s = new Scanner(in).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    //根据子listview中控件计算得到listview的高度
    public static void setListVIewHeight(ListView listView){
        ListAdapter listAdapter=listView.getAdapter();
        if(listAdapter==null)
            return;

        int len=listAdapter.getCount();
        int totalHeight=0;
        for(int i=0;i<len;i++){
            View listItem=listAdapter.getView(i,null,listView);
            listItem.measure(0,0);
            totalHeight+=listItem.getMeasuredHeight()+listView.getDividerHeight()/2;
        }
        ViewGroup.LayoutParams params=listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1))+60;
        listView.setLayoutParams(params);
    }


    @Override
    public int getItemCount() {
        return mPatients.size();
    }


}