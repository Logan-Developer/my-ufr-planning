package us.logaming.myufrplanning.ui.firstconfig;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;

import us.logaming.myufrplanning.MainActivity;
import us.logaming.myufrplanning.R;
import us.logaming.myufrplanning.ui.login.LoginFragment;

public class FirstConfigDoneFragment extends Fragment {

    public static FirstConfigDoneFragment newInstance() {
        return new FirstConfigDoneFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_first_config_done, container, false);

        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                getParentFragmentManager().beginTransaction().replace(R.id.container_first_config, LoginFragment.newInstance())
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(onBackPressedCallback);

        MaterialButton btnGoBack = root.findViewById(R.id.btn_first_config_go_back);
        MaterialButton btnDone = root.findViewById(R.id.btn_first_config_done);

        btnGoBack.setOnClickListener(v -> onBackPressedCallback.handleOnBackPressed());
        btnDone.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
            sharedPreferences.edit().putBoolean(getString(R.string.preference_start_first_config_key), false).apply();

            Intent intent = new Intent(getContext(), MainActivity.class);
            startActivity(intent);
            requireActivity().finish();
        });
        return root;
    }
}