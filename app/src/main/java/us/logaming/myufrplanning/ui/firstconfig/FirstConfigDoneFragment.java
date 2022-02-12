package us.logaming.myufrplanning.ui.firstconfig;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;

import us.logaming.myufrplanning.MainActivity;
import us.logaming.myufrplanning.R;

public class FirstConfigDoneFragment extends Fragment {

    public static FirstConfigDoneFragment newInstance() {
        return new FirstConfigDoneFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_first_config_done, container, false);

        MaterialButton btnGoBack = root.findViewById(R.id.btn_first_config_go_back);
        MaterialButton btnDone = root.findViewById(R.id.btn_first_config_done);

        btnGoBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());
        btnDone.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), MainActivity.class);
            startActivity(intent);
            requireActivity().finish();
        });
        return root;
    }
}