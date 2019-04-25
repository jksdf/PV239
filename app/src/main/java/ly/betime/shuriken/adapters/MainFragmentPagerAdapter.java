package ly.betime.shuriken.adapters;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import ly.betime.shuriken.R;
import ly.betime.shuriken.fragments.AlarmsFragment;
import ly.betime.shuriken.fragments.CalendarFragment;
import ly.betime.shuriken.fragments.SettingsFragment;

public class MainFragmentPagerAdapter extends FragmentPagerAdapter {

    private Context mContext;

    public MainFragmentPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new AlarmsFragment();
            case 1:
                return new CalendarFragment();
            default:
                return new SettingsFragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position)
    {
        switch (position) {
            case 0:
                return mContext.getString(R.string.alarms);
            case 1:
                return mContext.getString(R.string.calendar);
            default:
                return mContext.getString(R.string.settings);
        }
    }
}
