package com.explodingbacon.pigpen.signin.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.explodingbacon.pigpen.signin.R;
import com.explodingbacon.pigpen.signin.beans.Member;

import java.util.ArrayList;
import java.util.List;

public class MemberListAdapter extends RecyclerView.Adapter<MemberListAdapter.ViewHolder> {

    List<Member> members;
    OnMemberClickedListener listener;

    public MemberListAdapter(OnMemberClickedListener listener) {
        this.members = new ArrayList<>();
        this.listener = listener;
    }

    public void setMembers(List<Member> members) {
        this.members = members;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.view_member_name, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.bind(members.get(i));
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        Member member;
        View itemView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this::onClick);
            name = itemView.findViewById(R.id.name);
            this.itemView = itemView;
        }

        public void bind(Member member) {
            this.member = member;
            name.setText(member.getName());
            if (member.getIsIn()) {
                itemView.setBackgroundColor(Color.parseColor("#5550b04d"));
                name.setTextColor(Color.BLACK);
            } else {
                itemView.setBackgroundColor(Color.rgb(255, 255, 255));
                name.setTextColor(Color.BLACK);
            }
        }

        public void onClick(View view) {
            listener.onMemberClicked(member);
        }
    }

    public interface OnMemberClickedListener {
        void onMemberClicked(Member member);
    }
}
