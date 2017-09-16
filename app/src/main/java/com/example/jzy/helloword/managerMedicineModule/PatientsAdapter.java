package com.example.jzy.helloword.managerMedicineModule;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.jzy.helloword.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xiashu on 17-9-12.
 */

public class PatientsAdapter extends RecyclerView.Adapter<PatientsAdapter.ViewHolder> {
    private List<Patient> mPatients;
    Context context;

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

    public PatientsAdapter(List<Patient> patientList, Context context) {
        mPatients = patientList;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_inforcard, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(PatientsAdapter.ViewHolder holder, int position) {
        Patient patient = mPatients.get(position);
        holder.patient_id.setText(patient.getId());
        holder.patient_name.setText(patient.getName());


        String[] name={"阿莫西林","青霉素","清热感冒颗粒","板蓝根"};
        int[] num={1,3,4,2};
        String[] info={"3次/日，1粒/次","无","3次/日，2包/次，冲服","3次/日，1包/次"};
        List<Map<String,Object>> list_map=new ArrayList<Map<String,Object>>();


        for (int i = 0; i < 4; i++) {
            Map<String,Object> items=new HashMap<String,Object>();
            items.put("name",name[i]);
            items.put("num",num[i]+"盒");
            items.put("info",info[i]);
           // medicines.add(new medicine_item("药品"+i,(i+2),"一日三次，每次两粒"));
            list_map.add(items);
        }
        SimpleAdapter simpleAdapter=new SimpleAdapter(context,list_map,R.layout.listitem_medicine,
                new String[]{"name","num","info"},new int[]{R.id.m_name,R.id.m_num,R.id.m_infor});

        holder.medicine_list.setAdapter(simpleAdapter);
        setListVIewHeight(holder.medicine_list);
      //  holder.medicine_list.



      /*  List<String> medicineList = new ArrayList<String>();

        medicineList.add("阿莫西林       2盒     3次/日，1包/次");
        medicineList.add("青霉素         1支     无");
        medicineList.add("清热解毒颗粒    1盒     3次/日，1包/次，冲服");
        holder.medicine_list.setAdapter(new ArrayAdapter<String>(context, R.layout.listitem_medicine_1, medicineList));
*/
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


    public class medicine_item {
        private String name;
        private int num;
        private String infor;

        public medicine_item(String name, int num, String infor) {
            this.name = name;
            this.num = num;
            this.infor = infor;
        }
    }

}