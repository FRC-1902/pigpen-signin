package com.explodingbacon.pigpen.signin.adapters;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.explodingbacon.pigpen.signin.beans.Member;
import com.explodingbacon.pigpen.signin.fragments.MemberListFragment;

import java.util.ArrayList;
import java.util.List;

public class MemberListPager extends FragmentPagerAdapter {
    List<Member> members;
    List<Member> students;
    List<Member> adults;
    List<Member> inactive;

    MemberListFragment studentFragment;
    MemberListFragment adultFragment;
    MemberListFragment inactiveFragment;

    public MemberListPager(FragmentManager fm) {
        super(fm);
        this.students = new ArrayList<>();
        this.adults = new ArrayList<>();
        this.inactive = new ArrayList<>();

        this.studentFragment = MemberListFragment.getInstance(students);
        this.adultFragment = MemberListFragment.getInstance(adults);
        this.inactiveFragment = MemberListFragment.getInstance(inactive);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                return studentFragment;
            case 1:
                return adultFragment;
            case 2:
                return inactiveFragment;
        }

        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

    public void updateMembers(List<Member> members) {
        this.members = members;
        students.clear();
        adults.clear();
        inactive.clear();

        for (Member m : members) {
            if (m.getActive()) {
                if (m.getPosition().equals("stu")) {
                    students.add(m);
                } else if (m.getPosition().equals("mtr")) {
                    adults.add(m);
                }
            } else if (m.getPosition().equals("stu") || m.getPosition().equals("mtr")) {
                inactive.add(m);
            }
        }

        studentFragment.updateMembers(students);
        adultFragment.updateMembers(adults);
        inactiveFragment.updateMembers(inactive);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Students";
            case 1:
                return "Adults";
            case 2:
                return "Inactive";
            default:
                return super.getPageTitle(position);
        }
    }
}
