package com.sofwerx.usf.talosconfigurator;



import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class ButtonsTabFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

    public ButtonsTabFragment() {}

    private List<String> getSpinnerValues(){

        TALOSConfiguration currentConfiguration = ((MainScreenActivity)getActivity()).getCurrentConfiguration();
        List<ButtonFunction> buttonFuncs = currentConfiguration.getControlsConfiguration().getButtonFunctions();
        List<String> spinner = new ArrayList<String>();
        //add functions parsed from file
        for(int i = 0; i <buttonFuncs.size(); i++){
            spinner.add(buttonFuncs.get(i).getName());
        }

        return spinner;

    }

    private int getAssignedSpinnerValue(int mode, int key) {

        TALOSConfiguration currentConfiguration = ((MainScreenActivity)getActivity()).getCurrentConfiguration();
        List<ButtonMode> modes = currentConfiguration.getControlsConfiguration().getButtonSettings().getButtonModes();
        String func_name = "";
        int valuePos = 0;


            for(int j = 0; j < modes.get(mode).getButtonIntents().size() ;j++) {
                if (key == modes.get(mode).getButtonIntents().get(j).getKey()) {
                    func_name = modes.get(mode).getButtonIntents().get(j).getFunctionName();
                    break;
                }

            }

        List<ButtonFunction> buttonFuncs = currentConfiguration.getControlsConfiguration().getButtonFunctions();
        for(int k = 0; k < buttonFuncs.size(); k++){
            if(buttonFuncs.get(k).getName().equals(func_name)){
                return k;

            }
        }

        return valuePos;
    }

    private List<int[][]> getControllerLayout() {

        TALOSConfiguration currentConfiguration = ((MainScreenActivity)getActivity()).getCurrentConfiguration();
        List<ControlModule> controllers = currentConfiguration.getControlsConfiguration().getControlModules();
        List<int[][]> controlsLayouts = new ArrayList<>();

        for(int i = 0; i<controllers.size(); i++) {
            controllers.get(i).setLayout();
            controlsLayouts.add(controllers.get(i).getLayout());
        }

        return controlsLayouts;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        TALOSConfiguration currentConfiguration = ((MainScreenActivity)getActivity()).getCurrentConfiguration();
        //get button functions for spinner values
        List<String> spinnerValues = getSpinnerValues();

        List<int[][]> controllers = getControllerLayout();
        int numControllers = controllers.size();



        LinearLayout rootView = new LinearLayout(getContext());
        rootView.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);
        rootView.setPadding(100,0,100,50);
        rootView.setOrientation(LinearLayout.VERTICAL);
        rootView.setScrollContainer(true);

        ScrollView scroller = new ScrollView(this.getActivity());
        LinearLayout.LayoutParams scrollParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        scrollParams.setMargins(0,50,0,0);
        scroller.setLayoutParams(scrollParams);
        scroller.setPadding(0,150,0,100);


        scroller.addView(rootView);

        for (int c = 0; c < numControllers; c++) {
            LinearLayout controllerView = new LinearLayout(getContext());
            controllerView.setOrientation(LinearLayout.HORIZONTAL);

            GradientDrawable border = new GradientDrawable();
            border.setStroke(4,getResources().getColor(R.color.colorPrimaryDark));
            controllerView.setBackground(border);


            LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lparams.gravity=Gravity.CENTER_HORIZONTAL;
            TextView controllerName = new TextView(this.getActivity());
            controllerName.setLayoutParams(lparams);
            controllerName.setText(currentConfiguration.getControlsConfiguration().getControlModules().get(c).getModuleName());
            controllerName.setTextSize(20);
            controllerView.addView(controllerName);
            controllerView.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);
            controllerView.setPadding(0,5,0,55);


            int n = controllers.get(c).length;//see below comment
            int m = controllers.get(c)[0].length;//these values decide the number of spinners in one grid
            for (int i = 0; i < n; i++) {

                LinearLayout.LayoutParams lparams1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                LinearLayout controllerCol = new LinearLayout(this.getActivity());
                lparams1.gravity = Gravity.CENTER_HORIZONTAL;
                controllerCol.setLayoutParams(lparams1);
                controllerCol.setOrientation(LinearLayout.VERTICAL);
                controllerCol.setPadding(10,55,0,5);


                for(int j = 0; j < m; j++) {
                    if(controllers.get(c)[i][j] == 1) {

                        //create spinner
                        Spinner spin = new Spinner(this.getActivity());
                        controllerCol.addView(spin);
                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) spin.getLayoutParams();
                        params.width = 400;
                        params.height = 100;
                        spin.setLayoutParams(params);

                        //add options to spinner
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, spinnerValues);
                        spin.setAdapter(adapter);

                        //add border to spinner
                        GradientDrawable border2 = new GradientDrawable();
                        border2.setStroke(4,getResources().getColor(R.color.colorPrimaryDark));
                        spin.setBackground(border2);

                        int mode = 0;
                        if(((MainScreenActivity) getActivity()).getCurrentTabIndex() > 0)
                            mode = ((MainScreenActivity) getActivity()).getCurrentTabIndex() - 1;
                        int key = 0;

                        for(int k = 0; k < currentConfiguration.getControlsConfiguration().getControlModules().get(c).getModuleButtons().size(); k++) {
                            if (currentConfiguration.getControlsConfiguration().getControlModules().get(c).getModuleButtons().get(k).getXPosition()-1 == i
                                    && currentConfiguration.getControlsConfiguration().getControlModules().get(c).getModuleButtons().get(k).getYPosition()-1 == j) {
                                key = currentConfiguration.getControlsConfiguration().getControlModules().get(c).getModuleButtons().get(k).getKeyNumber();
                            }
                        }

                        int assignedFunc = getAssignedSpinnerValue(mode, key);
                        spin.setSelection(assignedFunc);
                    }
                    else{

                        //create spacing the size of a spinner for spaces in the grid with no button
                        View hiddenSpin = new View(this.getActivity());
                        controllerCol.addView(hiddenSpin);
                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) hiddenSpin.getLayoutParams();
                        params.width = 400;
                        params.height = 100;
                        hiddenSpin.setLayoutParams(params);

                    }
                    //add vertical space between spots in the controller grid
                    View spacer = new View(this.getActivity());
                    LinearLayout.LayoutParams spacingParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT); ;
                    spacingParams.height = 10;
                    spacingParams.width = 10;
                    spacer.setLayoutParams(spacingParams);
                    controllerCol.addView(spacer);
                }
                controllerView.addView(controllerCol);


            }
            rootView.addView(controllerView);
            //add space between controllers
            View spacer = new View(this.getActivity());
            rootView.addView(spacer);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) spacer.getLayoutParams();
            params.width = 400;
            params.height = 30;
            spacer.setLayoutParams(params);
        }
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_buttons_tab, container, false);
        return scroller;

    }




}
