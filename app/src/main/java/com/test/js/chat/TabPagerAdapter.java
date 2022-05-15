package com.test.js.chat;

/**
 * Created by lenovo on 2017-06-28.
 */

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;

import com.test.js.chat.Home.HomeFragment.Friend_Fragment;
import com.test.js.chat.Home.HomeFragment.Live_Fragment;

public class TabPagerAdapter extends FragmentPagerAdapter {

    public TabPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        switch (position) {
            case 0 :
                return "홈" ;
            case 1 :
                return "채팅" ;
            case 2 :
                return "친구";
            case 3 :
                return "Live";
            default:
                return null;
        }
    }
    public RoomList_Fragment rf = null;
    @Override
    public Fragment getItem(int position) {


        switch (position) {
            case 0:
                return Project_Fragment.newInstance();
            case 1:
                rf = new RoomList_Fragment();
                return rf;
            case 2:
                return Friend_Fragment.newInstance();
            case 3:
                return Live_Fragment.newInstance();
            default:
                return null;
        }

    }
}
