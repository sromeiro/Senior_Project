package com.sofwerx.usf.talosconfigurator;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.Toast;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class FileTabFragment extends Fragment implements View.OnClickListener, DialogInterface {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private Button btn_save;
    private Button btn_new;
    private Button btn_load;
    private Button btn_delete;
    private EditText filename;
    private TALOSConfiguration currentConfiguration;
    private ArrayAdapter<String> listAdapter;
    final ArrayList<String> listy = new ArrayList<String>();
    private ListView listview;

    public FileTabFragment() {}

    public static FileTabFragment newInstance(int tabNumber) {
        FileTabFragment fragment = new FileTabFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, tabNumber);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_file_tab, container, false);

        currentConfiguration = ((MainScreenActivity)getActivity()).getCurrentConfiguration();

        // Setup the view elements
        btn_save = (Button) view.findViewById(R.id.btn_save);
        filename = (EditText) view.findViewById(R.id.filename);
        btn_new = (Button) view.findViewById(R.id.btn_new);
        btn_load = (Button) view.findViewById(R.id.btn_load);
        btn_delete = (Button) view.findViewById(R.id.btn_delete);
        btn_new = (Button) view.findViewById(R.id.btn_new);
        btn_load = (Button) view.findViewById(R.id.btn_load);

        // Setup the file list
        listy.addAll(getExistingFilesList());
        listview = (ListView) view.findViewById(R.id.file_list_view);
        listAdapter = new ArrayAdapter<String>(getActivity().getBaseContext(),android.R.layout.simple_list_item_1, listy);
        listview.setAdapter(listAdapter);

        // On click populate the filenames elected to the filename box
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                filename.setText(listy.get(position));
            }
        });

        // Hide keyboard when user taps elsewhere
        filename.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        // Set the onClickListener to the Fragment
        btn_save.setOnClickListener(this);
        btn_new.setOnClickListener(this);
        btn_load.setOnClickListener(this);
        btn_delete.setOnClickListener(this);

        // Inflate the layout for this fragment
        return view;
    }

    // Override Parent Activity onClick so the Fragment can contain button logic
    @Override
    public void onClick(View v) {
        String sanitizedFilename = sanitizeFilename(filename.getText().toString());
        filename.setText(sanitizedFilename);
        // Switch against the ID of the View clicked
        switch (v.getId()) {
            case R.id.btn_save:
                save(sanitizedFilename);
                break;
            case R.id.btn_load:
                load(sanitizedFilename);
                break;
            case R.id.btn_new:
                ((MainScreenActivity) getActivity()).promptNewFile();
                break;
            case R.id.btn_delete:
                delete(sanitizedFilename);
                break;
            default:
                break;
        }
    }

    public void reloadFileList() {
        listy.clear();
        listy.addAll(getExistingFilesList());
        listview.setAdapter(listAdapter);
    }

    public void createDefaultXML() {
        if (!doesFileExist("default.xml")) {
            saveFile("default.xml");
        }
    }

    // When save button is pressed

    public void save(String inFilename) {
        if (doesFileExist(inFilename)) {
            if (inFilename.toLowerCase().contains("default.xml")) {
                ((MainScreenActivity)getActivity()).makeShortToast("Cannot overwrite default.xml");
            } else {
                promptOverwrite();
            }
        } else {
            saveFile(inFilename);
        }
    }

    public void saveFile(String inFilename) {
        File f = getAppFileDirectory();
        File exportFile = new File(f, inFilename);
        try {
            exportFile.createNewFile();

            Serializer ser = new Persister();
            ser.write(currentConfiguration, exportFile);

            ((MainScreenActivity) getActivity()).makeShortToast("File Saved as " + inFilename);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("saveFile", "Failed to create " + inFilename + " to disk. " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            // Display error to user
        }

        reloadFileList();
    }

    public String sanitizeFilename(String in) {
        in = in.trim();
        if (in.endsWith(".xml")) {
            return in;
        } else {
            if (in.endsWith(".")) {

            }
            return in + ".xml";
        }
    }

    public void load(String fn) {
        if (doesFileExist(fn)) {
            File f = new File(Environment.getExternalStorageDirectory(), "talos");
            File defaultFile = new File(f, fn);
            try {
                FileInputStream fins = new FileInputStream(defaultFile);
                ((MainScreenActivity) getActivity()).loadFile(fins);

                //Toast toast = Toast.makeText(getActivity().getApplicationContext(), "File '" + fn + "' Has Been Loaded", Toast.LENGTH_SHORT);
                //toast.show();
            } catch (FileNotFoundException e) {
                // Error and allow function to contiune below and load the embedded default.xml
                e.printStackTrace();
            }
        } else {
            Toast toast = Toast.makeText(getActivity().getApplicationContext(), "File '" + fn + "' Does Not Exist", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void delete(String inFilename) {
        if (doesFileExist(inFilename)) {
            if (inFilename.toLowerCase().contains("default.xml")) {
                ((MainScreenActivity)getActivity()).makeShortToast("Cannot delete default.xml");
            } else {
                promptDelete();
            }
        } else {
            ((MainScreenActivity)getActivity()).makeShortToast(inFilename + " does not exist");
        }
    }

    public void deleteFile(String inFilename) {
        File f = getAppFileDirectory();
        File delFile = new File(f, inFilename);
        delFile.delete();

        ((MainScreenActivity)getActivity()).makeShortToast(inFilename + " deleted");

        reloadFileList();
    }

    // Prompt user if they want to Delete the file
    public void promptDelete() {
        String sanitizedFile = sanitizeFilename(filename.getText().toString());

        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setMessage("DELETE " + sanitizedFile + "?");

        dialog.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteFile(sanitizeFilename(filename.getText().toString()));
            }
        });

        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ((MainScreenActivity)getActivity()).makeShortToast("Delete cancelled");
            }
        });

        dialog.show();
    }

    public boolean doesFileExist(String fn) {
        File f = getAppFileDirectory();
        File nf = new File(f, fn);
        return nf.exists();
    }

    public File getAppFileDirectory() {
        File f = new File(Environment.getExternalStorageDirectory(), "talos");
        if (!f.exists()) {
            if (!f.mkdirs()) {
                // TODO: Error to user
                Log.e("File", "Failed to make directory");
            }
        }

        if (!f.canWrite()) {
            // TODO: Error to user
            Log.e("File", "Cannot write to app directory /talos");
        }

        if (!f.canRead()) {
            // TODO: Error to user
            Log.e("File", "Cannot read app directory /talos");
        }

        return f;
    }

    // Prompt user if they want to overwrite the file
    public void promptOverwrite() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog.setMessage("OVERWRITE EXISTING FILE?");

        dialog.setPositiveButton("OVERWRITE", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                saveFile(sanitizeFilename(filename.getText().toString()));
            }
        });

        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ((MainScreenActivity)getActivity()).makeShortToast("Save cancelled");
            }
        });

        dialog.show();
    }

    // Returns an array of File objects corresponding to files
    public File[] getExistingFiles() {
        File f = new File(Environment.getExternalStorageDirectory(), "talos");

        if (!f.exists()) {
            if (!f.mkdirs()) {
                Log.e("File", "Failed to make directory");
                // Display error to user
            }
        }
        return f.listFiles();
    }

    // Returns a list of strings of filenames including extensions
    public List<String> getExistingFilesList() {
        List<String> list = new ArrayList<String>();
        File[] Files = getExistingFiles();
        for (File f : getExistingFiles()) {
            f.getName();
            list.add(f.getName());
        }
        return list;
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager)(getActivity()).getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void cancel() {

    }

    @Override
    public void dismiss() {

    }
}
