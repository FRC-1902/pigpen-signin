package com.explodingbacon.pigpen.signin.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.explodingbacon.pigpen.signin.R;
import com.explodingbacon.pigpen.signin.adapters.MemberListAdapter;
import com.explodingbacon.pigpen.signin.beans.Member;

import java.util.List;

public class MemberListFragment extends Fragment {
    List<Member> members;

    RecyclerView recycler;
    MemberListAdapter adapter;

    public static MemberListFragment getInstance(List<Member> members, MemberListAdapter.OnMemberClickedListener listener) {
        MemberListFragment instance = new MemberListFragment();
        instance.members = members;
        instance.adapter = new MemberListAdapter(listener);
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!(getActivity() instanceof MemberListAdapter.OnMemberClickedListener)) {
            throw new RuntimeException("Parent activity must implement OnMemberClickedListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_member_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View root, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);

        recycler = root.findViewById(R.id.recycler);
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    public void updateMembers(List<Member> members) {
        adapter.setMembers(members);

        if (getActivity() != null)
            getActivity().runOnUiThread(() -> adapter.notifyDataSetChanged());
    }
}
