package us.logaming.myufrplanning.ui.firstconfig;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;

import us.logaming.myufrplanning.R;
import us.logaming.myufrplanning.ui.choosegroup.ChooseGroupFragment;

public class FirstConfigWelcomeFragment extends Fragment {

    public static FirstConfigWelcomeFragment newInstance() {
        return new FirstConfigWelcomeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_first_config_welcome, container, false);

        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                requireActivity().finish();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(onBackPressedCallback);

        MaterialButton getStartedButton = root.findViewById(R.id.btn_first_config_get_started);
        getStartedButton.setOnClickListener(v -> getParentFragmentManager().beginTransaction().replace(R.id.container_first_config, ChooseGroupFragment.newInstance())
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit());
        return root;
    }
}