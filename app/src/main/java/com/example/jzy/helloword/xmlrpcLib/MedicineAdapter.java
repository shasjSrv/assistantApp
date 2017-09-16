package com.example.jzy.helloword.xmlrpcLib;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.jzy.helloword.Patient;
import com.example.jzy.helloword.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xiashu on 17-9-16.
 */

public class MedicineAdapter extends RecyclerView.Adapter<MedicineAdapter.ViewHolder> {
    private List<medicine_item> mMedicines;
    Context context;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView medicine_name;
        TextView medicine_num;
        TextView medicine_infor;

        public ViewHolder(View itemView) {
            super(itemView);
            medicine_name = (TextView) itemView.findViewById(R.id.m_name);
            medicine_num = (TextView) itemView.findViewById(R.id.m_num);
            medicine_infor = (TextView) itemView.findViewById(R.id.m_infor);
        }

    }

    public MedicineAdapter(List<medicine_item> medicineList, Context context) {
        mMedicines = medicineList;
        this.context = context;
    }

    @Override
    public MedicineAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listitem_inforcard, parent, false);
        MedicineAdapter.ViewHolder holder = new MedicineAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MedicineAdapter.ViewHolder holder, int position) {
        medicine_item medicine = mMedicines.get(position);
        holder.medicine_name.setText(medicine.getName());
        holder.medicine_num.setText(medicine.getNum()+"");
        holder.medicine_infor.setText(medicine.getInfor());


      /*  List<String> medicineList = new ArrayList<String>();

        medicineList.add("阿莫西林       2盒     3次/日，1包/次");
        medicineList.add("青霉素         1支     无");
        medicineList.add("清热解毒颗粒    1盒     3次/日，1包/次，冲服");
        holder.medicine_list.setAdapter(new ArrayAdapter<String>(context, R.layout.listitem_medicine_1, medicineList));
*/
    }


    @Override
    public int getItemCount() {
        return mMedicines.size();
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

        public String getName() {
            return name;
        }

        public int getNum() {
            return num;
        }

        public String getInfor() {
            return infor;
        }
    }

}