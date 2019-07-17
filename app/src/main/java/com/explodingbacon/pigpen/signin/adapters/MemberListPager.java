package com.explodingbacon.pigpen.signin.adapters;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.explodingbacon.pigpen.signin.beans.Member;
import com.explodingbacon.pigpen.signin.fragments.MemberListFragment;

import java.util.ArrayList;
import java.util.List;

public class MemberListPager extends FragmentStatePagerAdapter {
    List<Member> members;
    List<Member> students;
    List<Member> adults;

    MemberListFragment studentFragment;
    MemberListFragment adultFragment;

    public MemberListPager(FragmentManager fm) {
        super(fm);
        this.students = new ArrayList<>();
        this.adults = new ArrayList<>();

        this.studentFragment = MemberListFragment.getInstance(students);
        this.adultFragment = MemberListFragment.getInstance(adults);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                return studentFragment;
            case 1:
                return adultFragment;
        }

        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    public void updateMembers(List<Member> members) {
        this.members = members;
        students.clear();
        adults.clear();

        for (Member m : members) {
            if (m.getPosition().equals("stu")) {
                students.add(m);
            } else if (m.getPosition().equals("mtr")) {
                adults.add(m);
            }
        }

        studentFragment.updateMembers(students);
        adultFragment.updateMembers(adults);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Students";
            case 1:
                return "Adults";
            default:
                return super.getPageTitle(position);
        }
    }
}
