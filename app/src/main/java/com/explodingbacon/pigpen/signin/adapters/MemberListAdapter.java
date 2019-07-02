package com.explodingbacon.pigpen.signin.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
        notifyDataSetChanged();
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this::onClick);
            name = itemView.findViewById(R.id.name);
        }

        public void bind(Member member) {
            this.member = member;
            name.setText(member.getName());
        }

        public void onClick(View view) {
            listener.onMemberClicked(member);
        }
    }

    public interface OnMemberClickedListener {
        void onMemberClicked(Member member);
    }
}
