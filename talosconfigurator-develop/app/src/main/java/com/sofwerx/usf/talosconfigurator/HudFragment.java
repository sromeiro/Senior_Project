package com.sofwerx.usf.talosconfigurator;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.app.Activity;
import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;


public class HudFragment extends Fragment {
  String[] backgrounds={"Nightvision Green","Thermal White"};
    public String Vision;
    private static final String HUD_ITEMS_STATE = "hud_items_state";
    ExpandableListView expandableListView;
    private ImageView leftHud;
    private ImageView rightHud;
    private Grid hud1;
    private Grid hud2;
    private Grid hud3;
    private Grid hud4;
    private ArrayList<ModeElement> hudItems;
    private List<Screen> screens;
    private List<ConfigurableElement> configElements;
    private View home;
    private int elementId;

    private List<IdMapper> viewsHud1;
    private List<IdMapper> viewsHud2;
    private List<IdMapper> viewsHud3;
    private List<IdMapper> viewsHud4;

    private HudGridManager gridManager;

    public HudFragment() {
        // Required empty public constructor
    }

    /*
        This function returns an instance of a HudFragment given a list of mode elements
     */
    public static HudFragment newInstance(ArrayList<ModeElement> fragmentState) {
        HudFragment fragment = new HudFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(HUD_ITEMS_STATE, fragmentState);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        elementId = 0;
        viewsHud1 = new ArrayList<>();
        viewsHud2 = new ArrayList<>();
        viewsHud3 = new ArrayList<>();
        viewsHud4 = new ArrayList<>();



        // Inflate the layout for this fragment
        hudItems = new ArrayList<ModeElement>();
        MainScreenActivity main = (MainScreenActivity) getActivity();
        screens = main.getCurrentConfiguration().getDisplayDef().getScreens();
        configElements = main.getCurrentConfiguration().getConfigurableElements();

        //getting the hudItems from the arguments allows to save the state of the hud fragment
        if(getArguments() != null) hudItems = getArguments().getParcelableArrayList(HUD_ITEMS_STATE);
        final View rootView = inflater.inflate(R.layout.fragment_hud, container, false);
        home = rootView;
        //creating the list of elements
        expandableListView = (ExpandableListView) rootView.findViewById(R.id.ExList1);
        ExpandableListViewAdapter adapter = new ExpandableListViewAdapter(getActivity().getBaseContext(),((MainScreenActivity) getActivity()).getCurrentConfiguration());
        expandableListView.setAdapter(adapter);
//Getting the instance of Spinner and applying OnItemSelectedListener on it
      Spinner spin = (Spinner) rootView.findViewById(R.id.spinner_HUD_Color);
      ArrayAdapter<String> ad = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, backgrounds);
      ad.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
      spin.setAdapter(ad);
      spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

        public void onItemSelected(AdapterView<?> arg0, View arg1, int position,long id) {
          Toast.makeText(getContext(), backgrounds[position], Toast.LENGTH_LONG).show();
          Log.d(TAG, "onItemSelected: " + backgrounds[position]);
          Vision = backgrounds[position];
           if (backgrounds[position] == "Nightvision Green"){
            hud1.setBackgroundColor(Color.parseColor("#ff669900") );
            hud2.setBackgroundColor(Color.parseColor("#ff669900") );
            hud3.setBackgroundColor(Color.parseColor("#ff669900") );
            hud4.setBackgroundColor(Color.parseColor("#ff669900") );

          }
          if (backgrounds[position] == "Thermal White"){
            hud1.setBackgroundColor(Color.parseColor("#fff3f3f3") );
            hud2.setBackgroundColor(Color.parseColor("#fff3f3f3") );
            hud3.setBackgroundColor(Color.parseColor("#fff3f3f3") );
            hud4.setBackgroundColor(Color.parseColor("#fff3f3f3") );
          }
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
          // TODO Auto-generated method stub

        }
      });

        leftHud = (ImageView) rootView.findViewById(R.id.left_hud);
        rightHud = (ImageView) rootView.findViewById(R.id.right_hud);

        hud1 = (Grid) rootView.findViewById(R.id.hud1);
        hud2 = (Grid) rootView.findViewById(R.id.hud2);
        hud3 = (Grid) rootView.findViewById(R.id.hud3);
        hud4 = (Grid) rootView.findViewById(R.id.hud4);




        //the grid manager makes sure that items added to hud2 are also added to hud3
        //and vice versa
        gridManager = new HudGridManager(hud1, hud2, hud3, hud4);

        hud1.setOnDragListener(new HudOnDragListener(this));
        hud2.setOnDragListener(new HudOnDragListener(this));
        hud3.setOnDragListener(new HudOnDragListener(this));
        hud4.setOnDragListener(new HudOnDragListener(this));

        hud1.setOnTouchListener(new HudOnTouchListener(this));
        hud2.setOnTouchListener(new HudOnTouchListener(this));
        hud3.setOnTouchListener(new HudOnTouchListener(this));
        hud4.setOnTouchListener(new HudOnTouchListener(this));

        loadGridWhenReady();

        Spinner left_ld = (Spinner) rootView.findViewById(R.id.spinner_left_lookdown);
        Spinner right_ld = (Spinner) rootView.findViewById(R.id.spinner_right_lookdown);

        List<String> spinnerValuesLDL = new ArrayList<>();
        for(int i = 0; i < main.getCurrentConfiguration().getLookdownConfigurableElements().size(); i++) {
            spinnerValuesLDL.add(main.getCurrentConfiguration().getLookdownConfigurableElements().get(i).getName());
        }

        ArrayAdapter<String> adapterLD = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, spinnerValuesLDL);
        left_ld.setAdapter(adapterLD);
        right_ld.setAdapter(adapterLD);

        List<ModeElement> elements = null;
        if(main.getCurrentTabIndex() > 0 ) {
            elements = main.getCurrentConfiguration().getModes().get(main.getCurrentTabIndex()-1).getElements();
           if(elements!= null) {
                for (int j = 0; j < elements.size(); j++) {
                    if(elements.get(j).getPosition().getScreen()!=null) {
                        if (elements.get(j).getPosition().getScreen().get(0).equals("ld1")) {
                            if (elements.get(j).isActive()) {
                                for (int k = 0; k < main.getCurrentConfiguration().getLookdownConfigurableElements().size(); k++) {
                                    if (elements.get(j).getPosition().getScreen().get(0) == main.getCurrentConfiguration().getLookdownConfigurableElements().get(k).getName())
                                        left_ld.setSelection(k);
                                }
                            }
                            if (elements.get(j).getPosition().getScreen().get(0).equals("ld2")) {
                                for (int v = 0; v < main.getCurrentConfiguration().getLookdownConfigurableElements().size(); v++) {
                                    if (elements.get(j).getPosition().getScreen().get(0) == main.getCurrentConfiguration().getLookdownConfigurableElements().get(v).getName())
                                        right_ld.setSelection(v);
                                }
                            }
                        }
                    }
                }
            }
        }
        left_ld.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            Boolean initialDisplay = true;
           @Override
          public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!initialDisplay) {
                    String selectedEle = parent.getSelectedItem().toString();
                    selectLdlElement(selectedEle);
                  Log.d(TAG, "onItemSelected: " + selectedEle);
                  AlertDialog.Builder alertadd = new AlertDialog.Builder(getActivity());
                  LayoutInflater factory = LayoutInflater.from(getActivity());
                  final View time = factory.inflate(R.layout.time_hud, null);
                  alertadd.setView(time);
                  alertadd.setNeutralButton("Confirm", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dlg, int sumthin) {

                    }
                  });

                  alertadd.show();
                }
                else{
                    initialDisplay = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

                // sometimes you need nothing here
            }
        });
        return rootView;
    }

    public void selectLdlElement(String eleName){
        TALOSConfiguration currentConfig = ((MainScreenActivity) getActivity()).getCurrentConfiguration();
        if(((MainScreenActivity) getActivity()).getCurrentTabIndex() > 0) {
        List<ModeElement> modeElementsList = currentConfig.getModes().get(((MainScreenActivity) getActivity()).getCurrentTabIndex() - 1).getElements();
            if (modeElementsList != null) {
                for (int i = 0; i < modeElementsList.size(); i++) {
                    if (modeElementsList.get(i).getPosition().getScreen() != null) {
                        if (modeElementsList.get(i).getPosition().getScreen().get(0).equals("ld1")) {
                            modeElementsList.get(i).setName(eleName);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onViewCreated(View view, Bundle saveInstanceState) {
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelableArrayList(HUD_ITEMS_STATE,hudItems);
    }


  public void onItemSelected(AdapterView<?> arg0, View arg1, int position,long id) {
    Log.d(TAG, "onItemSelected: tapped");
    Toast.makeText(getActivity().getApplicationContext(), backgrounds[position], Toast.LENGTH_LONG).show();
  }

    /*
        This function load the mode elements to the screen when a fragment is initialized
     */
    public  void loadModeElements() {
        ConstraintLayout layout = (ConstraintLayout) home.findViewById(R.id.hud_layout);
        List<TextView> elements;
        for(ModeElement hudItem: hudItems) {
            Position itemPosition = hudItem.getPosition();
            Format elementFormat = getFormat(hudItem);
            if(elementFormat != null) {
                List<String> huds = itemPosition.getScreen();

                for(String hud: huds) {
                    switch(hud.toLowerCase()) {
                        case "hud1":
                            elements = hud1.addItemToGrid(hudItem, elementFormat);
                            if(elements != null) {
                                addElementToLayout(layout, elements);
                                viewsHud1.add(new IdMapper(itemPosition, setElementsIds(elements)));
                            }
                            break;
                        case "hud2":
                            elements = hud2.addItemToGrid(hudItem, elementFormat);
                            if(elements != null) {
                                addElementToLayout(layout, elements);
                                setElementsIds(elements);
                                viewsHud2.add(new IdMapper(itemPosition, setElementsIds(elements)));

                                elements = hud3.addItemToGrid(hudItem, elementFormat);
                                addElementToLayout(layout, elements);
                                setElementsIds(elements);
                                viewsHud3.add(new IdMapper(itemPosition, setElementsIds(elements)));
                            }
                            break;

                        case "hud3":
                            elements = hud3.addItemToGrid(hudItem, elementFormat);
                            if(elements != null) {
                                addElementToLayout(layout, elements);
                                setElementsIds(elements);
                                viewsHud3.add(new IdMapper(itemPosition, setElementsIds(elements)));

                                elements = hud2.addItemToGrid(hudItem, elementFormat);
                                addElementToLayout(layout, elements);
                                setElementsIds(elements);
                                viewsHud2.add(new IdMapper(itemPosition, setElementsIds(elements)));
                            }
                            break;

                        case "hud4":
                            elements = hud4.addItemToGrid(hudItem, elementFormat);
                            if(elements != null) {
                                addElementToLayout(layout, elements);
                                setElementsIds(elements);
                                viewsHud4.add(new IdMapper(itemPosition, setElementsIds(elements)));
                            }

                            break;
                    }

                }
            }
        }
    }

    /*
        This functions adds a new element ont he grid to the mode, so that it can
        be later saved to an xml
     */
    public void addItemToMode(String value, Position position, String format) {
        ModeElement fragState = new ModeElement(value, position, format);
        hudItems.add(fragState);
        updateTAlosConfigElements();
    }

    /*
        This function initializes the grid dimensions and and positions of the cells
     */
    public void loadGrid() {
        for(Screen screen: screens) {
            int rows = screen.getMaxXCell();
            int columns = screen.getMaxYCell();
            switch(screen.getName().toLowerCase()) {
                case "hud1":
                    hud1.setGridDimentions(rows, columns, (int)hud1.getY(), (int)hud1.getX(), hud1.getWidth(), hud1.getHeight());
                    break;

                case "hud2":
                    hud2.setGridDimentions(rows, columns,(int)hud2.getY(), (int)hud2.getX(), hud2.getWidth(), hud2.getHeight());
                    break;

                case "hud3":
                    hud3.setGridDimentions(rows, columns, (int)hud3.getY(), (int)hud3.getX(), hud3.getWidth(), hud3.getHeight());
                    break;

                case "hud4":
                    hud4.setGridDimentions(rows, columns, (int)hud4.getY(), (int)hud4.getX(), hud4.getWidth(), hud4.getHeight());
                    break;

            }
        }
    }

    /*
        This function adds the element to the layout (screen)
     */
    private void addElementToLayout(ConstraintLayout layout, List<TextView> element) {
        if(element == null) return;

        for(TextView view: element) {
            layout.addView(view, view.getWidth(), view.getLineHeight());
        }
    }

    /*
        This function returns the format object of a given element
     */
    public Format getFormat(ModeElement modeElement) {

        for(ConfigurableElement element: configElements) {
            if(element.getName().equals(modeElement.getName())) {
                List<Format> formats = element.getFormats();
                if(formats == null) return null;
                for(Format format: formats) {
                    if(format.getName().equals(modeElement.getFormat())) {
                        return format;
                    }
                }
            }
        }
        return null;
    }

    /*
        this function returns the format object of a given the format name
     */
    public Format getFormat(String formatName) {
        for(ConfigurableElement element: configElements) {
            for(Format format: element.getFormats()) {
                if(format.getName().equals(formatName)) {
                    return format;
                }
            }
        }
        return null;
    }

    /*
        this function loads the elements to the screen when the layout of the hud fragment
        is FULLY loaded. This is important since the width and the height of the grid views are
        unknown until the layout is done loading
     */
    public void loadGridWhenReady() {
        hud1.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener(){
            @Override
            public void onGlobalLayout() {
                hud1.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                loadGrid();
                loadModeElements();
            }
        });
    }

    public  void removeElementFromHud(Grid hud, MotionEvent event) {
        int x = (int)event.getX() + (int)hud.getX();
        int y = (int)event.getY() + (int)hud.getY();

        Position elementPosition = hud.findElement(x, y);
        if(elementPosition != null) {
            hud.removeElement(elementPosition); //clears cellstaken
            //remove Textviews from the screen
            deleteElementView(elementPosition, hud.getName());
            removeModeElement(elementPosition);

        }


    }

    public void updateViewsTracker(String hud, Position position, List<Integer> ids) {
        switch(hud){
            case "hud1":
                viewsHud1.add(new IdMapper(position, ids));
                break;
            case "hud2":
                viewsHud2.add(new IdMapper(position, ids));
                break;

            case "hud3":
                viewsHud3.add(new IdMapper(position, ids));
                break;

            case "hud4":
                viewsHud4.add(new IdMapper(position, ids));
        }
    }

    private void deleteElementView(Position position, String hudName) {
        Log.d("bruh", "removing view");
        List<Integer> ids = new ArrayList<>();
        ConstraintLayout layout = (ConstraintLayout)home.findViewById(R.id.hud_layout);
        switch(hudName.toLowerCase()) {
            case "hud1":
                ids = searchForViewIds(viewsHud1, position);
                break;
            case "hud2":
                ids = searchForViewIds(viewsHud2, position);
                break;

            case "hud3":
                ids = searchForViewIds(viewsHud3, position);
                break;

            case "hud4":
                ids = searchForViewIds(viewsHud4, position);
                break;

            default:
                return;
        }

        if(ids != null) {
            for(Integer id: ids) {
                TextView view = (TextView)home.findViewById(id);
                Log.d("bruh", "view Text: " + view.getText());
                layout.removeView(view);
            }
        }
    }

    private List<Integer> searchForViewIds(List<IdMapper> idMappers, Position position) {
        List<Integer> ids = new ArrayList<>();
        int x = position.getxPos();
        int y = position.getyPos();
        for(IdMapper idmapper: idMappers) {
           if(idmapper.getX() == x && idmapper.getY() == y) {
               return idmapper.getIds();
           }
        }
        return ids;
    }
    private List<Integer> setElementsIds(List<TextView> elements) {
        Log.d("bruh", "setting elements Ids");
        if(elements == null) return null;
        List<Integer> ids = new ArrayList<Integer>();
        for(TextView view: elements) {
            view.setId(elementId);
            ids.add(elementId);
            elementId++;
        }

        return ids;
    }

    private void removeModeElement(Position position) {
        Log.d("bruh", "remove element from scrren");
        for(int i = 0; i < hudItems.size(); i++) {
            ModeElement element = hudItems.get(i);
            Position elementPosition = element.getPosition();
            int elementXCell = elementPosition.getxPos();
            int elementYCell = elementPosition.getyPos();

            if(elementXCell == position.getxPos() && elementYCell == position.getyPos()){
                hudItems.remove(i);
                updateTAlosConfigElements();
                break;
            }
        }
    }



    private void updateTAlosConfigElements() {
        MainScreenActivity main = (MainScreenActivity) getActivity();
        int index = main.getCurrentTabIndex() - 1;
        TALOSConfiguration talosConfig = main.getCurrentConfiguration();

        Mode currentMode = talosConfig.getModeByIndex(index);
        currentMode.setElements(hudItems);

    }

    public void clear() {
        hudItems = new ArrayList<ModeElement>();
        getArguments().putParcelableArrayList(HUD_ITEMS_STATE, hudItems);
        updateTAlosConfigElements();
        MainScreenActivity main = (MainScreenActivity)getActivity();
        int index = main.getCurrentTabIndex() - 1;
        main.getModesAdapter().clearMode(index);
        removeAllElementViews();
        gridManager.clearGrids();

    }


    public void removeAllElementViews() {
        ConstraintLayout layout = (ConstraintLayout)home.findViewById(R.id.hud_layout);
        for(IdMapper mapper: viewsHud1) {
            for(Integer id: mapper.getIds()) {
                TextView view = (TextView)home.findViewById(id);
                layout.removeView(view);
            }

        }
        for(IdMapper mapper: viewsHud2) {
            for(Integer id: mapper.getIds()) {
                TextView view = (TextView)home.findViewById(id);
                layout.removeView(view);
            }

        }
        for(IdMapper mapper: viewsHud3) {
            for(Integer id: mapper.getIds()) {
                TextView view = (TextView)home.findViewById(id);
                layout.removeView(view);
            }

        }
        for(IdMapper mapper: viewsHud4) {
            for(Integer id: mapper.getIds()) {
                TextView view = (TextView)home.findViewById(id);
                layout.removeView(view);
            }

        }

        viewsHud1 = new ArrayList<>();
        viewsHud2 = new ArrayList<>();
        viewsHud3 = new ArrayList<>();
        viewsHud4 = new ArrayList<>();



    }



    public HudGridManager getGridManager() {
        return gridManager;
    }

    public int getElementsIds() {
        return elementId;
    }

    public void setElementIds(int id) {
        elementId = id;
    }

}
