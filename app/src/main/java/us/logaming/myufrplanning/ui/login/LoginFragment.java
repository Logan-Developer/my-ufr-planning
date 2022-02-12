package us.logaming.myufrplanning.ui.login;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;
import java.util.concurrent.Executors;

import us.logaming.myufrplanning.R;
import us.logaming.myufrplanning.ui.firstconfig.FirstConfigDoneFragment;
import us.logaming.myufrplanning.ui.settings.SettingsActivity;

public class LoginFragment extends Fragment {
    private LoginViewModel viewModel;

    private TextInputLayout textInputLogin, textInputPassword;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_login, container, false);

        MaterialButton btnGoBack = root.findViewById(R.id.btn_go_back_login);
        MaterialButton btnConfirm = root.findViewById(R.id.btn_confirm_login);
        this.textInputLogin = root.findViewById(R.id.text_input_login);
        this.textInputPassword = root.findViewById(R.id.text_input_password);

        if (requireActivity().getLocalClassName().endsWith(SettingsActivity.class.getSimpleName())) {
            btnGoBack.setVisibility(View.GONE);
        }
        else {
            btnGoBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());
        }
        this.viewModel = new LoginViewModel(Executors.newFixedThreadPool(4));

        btnConfirm.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
            if (isLoginFieldEmpty() && isPasswordFieldEmpty()) {
                sharedPreferences.edit()
                        .putString(requireContext().getString(R.string.preference_user_id_key), null)
                        .putString(requireContext().getString(R.string.preference_connection_token_key), null)
                        .putString(requireContext().getString(R.string.preference_user_display_name_key),
                                requireContext().getString(R.string.preference_user_display_name_default_value))
                        .apply();
                getParentFragmentManager().popBackStack();
            }
            else if (isLoginFieldEmpty() || isPasswordFieldEmpty()){
                Toast.makeText(getContext(), requireContext().getString(R.string.error_login_one_field_filled), Toast.LENGTH_SHORT).show();
            }
            else {
                viewModel.getUser(getLoginString(), getPasswordString()).observe(getViewLifecycleOwner(), loggedInUserItem -> {
                    if (loggedInUserItem == null) {
                        Toast.makeText(getContext(), requireContext().getString(R.string.error_login_incorrect), Toast.LENGTH_SHORT).show();
                    }
                    else {
                        sharedPreferences.edit()
                                .putString(requireContext().getString(R.string.preference_user_id_key), loggedInUserItem.getUserId())
                                .putString(requireContext().getString(R.string.preference_connection_token_key), loggedInUserItem.getConnectionToken())
                                .putString(requireContext().getString(R.string.preference_user_display_name_key), loggedInUserItem.getDisplayName())
                                .apply();

                        if (requireActivity().getLocalClassName().endsWith(SettingsActivity.class.getSimpleName())) {
                            getParentFragmentManager().popBackStack();
                        }
                        else {
                            getParentFragmentManager().beginTransaction().replace(R.id.container_first_config, FirstConfigDoneFragment.newInstance())
                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                    .addToBackStack(getString(R.string.first_config_back_stack))
                                    .commit();
                        }
                    }
                });
            }
        });
        return root;
    }

    private String getLoginString() {
        return Objects.requireNonNull(this.textInputLogin.getEditText()).getText().toString();
    }

    private String getPasswordString() {
        return Objects.requireNonNull(this.textInputPassword.getEditText()).getText().toString();
    }

    private boolean isLoginFieldEmpty() {
        return getLoginString().equals("");
    }

    private boolean isPasswordFieldEmpty() {
        return getPasswordString().equals("");
    }
}