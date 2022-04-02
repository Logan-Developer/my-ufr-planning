package us.logaming.myufrplanning.ui.choosegroup;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import us.logaming.myufrplanning.R;

/**
 * View for the list of added groups.
 */
public class GroupsListFragment extends Fragment {

    public GroupsListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_groups_list, container, false);
    }
}