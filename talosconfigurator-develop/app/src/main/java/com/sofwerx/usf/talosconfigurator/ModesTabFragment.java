package com.sofwerx.usf.talosconfigurator;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import java.util.ArrayList;

/*
    This class serves as a container for the the HudFragment and the ButtonsTabFragment
 */

public class ModesTabFragment extends Fragment implements View.OnClickListener {

    private static final String HUD_STATE = "hud_state";
    private static String configMode;
    private Button btnConfig;
    private Button btnClearMode;
    private HudFragment hudFrag;
    private ButtonsTabFragment btnFrag;

    public ModesTabFragment() {}

    public static ModesTabFragment newInstance(ArrayList<ModeElement> hud) {
        ModesTabFragment fragment = new ModesTabFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(HUD_STATE, hud);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            ArrayList<ModeElement> hudState = getArguments().getParcelableArrayList(HUD_STATE);
            hudFrag = HudFragment.newInstance(hudState);
            btnFrag = new ButtonsTabFragment();
        }
        else {
            hudFrag = new HudFragment();
            btnFrag = new ButtonsTabFragment();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_modes_tab, container, false);
        btnConfig = (Button) rootView.findViewById(R.id.button_config);
        btnConfig.setOnClickListener(this);
        btnClearMode = (Button) rootView.findViewById(R.id.clear_mode_btn);
        btnClearMode.setOnClickListener(this);
        configMode = getString(R.string.hud_mode);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.mode_container, hudFrag);
        transaction.add(R.id.mode_container, btnFrag);
        transaction.hide(btnFrag);
        transaction.show(hudFrag);
        transaction.commit();
    }

    /*
        This function toggles the hud fragment and the buttons config fragment
     */
    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.button_config:
                //switch view from buttons_mode to hud_mode
                if(configMode == null || configMode.equals(getString(R.string.buttons_mode))){

                    FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                    transaction.hide(btnFrag);
                    transaction.show(hudFrag);
                    transaction.commit();

                    btnConfig.setText(getString(R.string.buttons_mode));
                    configMode = getString(R.string.hud_mode);
                }

                //switch view from hud_mode to buttons_mode
                else if(configMode.equals(getString(R.string.hud_mode))) {



                       // FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                       // transaction.hide(hudFrag);
                       // transaction.show(btnFrag);
                       // transaction.commit();

                    //it is more important that the button frag shows every time than keeping it state

                        btnFrag = new ButtonsTabFragment();
                        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                        transaction.add(R.id.mode_container,btnFrag);
                        transaction.hide(hudFrag);
                        transaction.show(btnFrag);
                        transaction.commit();

                    btnConfig.setText(getString(R.string.hud_mode));
                    configMode = getString(R.string.buttons_mode);
                }

                break;

            case R.id.clear_mode_btn:
                Log.d("bruh", "clear mode");
                hudFrag.clear();
                getArguments().putParcelableArrayList(HUD_STATE, new ArrayList<ModeElement>());


                //clear buttons
                break;


        }

    }

    public HudFragment getHudFragment() {
        return hudFrag;
    }

}
