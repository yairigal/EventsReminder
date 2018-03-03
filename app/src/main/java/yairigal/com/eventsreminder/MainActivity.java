package yairigal.com.eventsreminder;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.app.Dialog;
import com.tsongkha.spinnerdatepicker.DatePickerDialog;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    DataObjects items;
    ArrayAdapter adp;
    android.support.design.widget.FloatingActionButton add;
    View root;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        root = findViewById(android.R.id.content);
        MultiDex.install(this);
        setupListView();
        initFirebaseListener();
        setupButton();
    }
    private void onPreExecute() {
        findViewById(R.id.LoadingSpinner).setVisibility(View.VISIBLE);
        findViewById(R.id.listView).setVisibility(View.GONE);
    }
    private void initFirebaseListener() {
        onPreExecute();
        FirebaseAccess.getFirebaseReference().addValueEventListener(new ValueEventListener() {
            public DataObjects values;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                values = new DataObjects(new DataObjects.myCallback() {
                    @Override
                    public void onAddandRemove(DataObjects d) {

                    }
                });
                for (DataSnapshot dss:dataSnapshot.getChildren()) {
                    values.add(dss.getValue(DataObject.class));
                }
                onPostExecute();
            }


            private void onPostExecute() {
                items.clear();
                items.addAll(values);
                adp.notifyDataSetChanged();
                findViewById(R.id.LoadingSpinner).setVisibility(View.GONE);
                findViewById(R.id.listView).setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void setupButton() {
        add = findViewById(R.id.fab);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SpinnerDatePickerDialogBuilder()
                        .context(MainActivity.this)
                        .defaultDate(2017, 0, 1)
                        .callback(new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(com.tsongkha.spinnerdatepicker.DatePicker view, final int year, final int monthOfYear, final int dayOfMonth) {
                                final Dialog mDialog = new Dialog(MainActivity.this);
                                mDialog.applyStyle(0)
                                        .positiveAction("ADD")
                                        .negativeAction("CANCEL")
                                        .title("Add Reminder")
                                        .setContentView(R.layout.add_item_layout);
                                mDialog.positiveActionClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        EditText title = mDialog.findViewById(R.id.titleItem);

                                        final DataObject obj;
                                        items.add(obj = new DataObject(new Date(year, monthOfYear + 1, dayOfMonth), title.getText().toString()));
                                        adp.notifyDataSetChanged();
                                        mDialog.dismiss();
                                        Snackbar.make(root, title.getText().toString() + " Added", Snackbar.LENGTH_LONG).setAction("UNDO", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                items.remove(obj);
                                                adp.notifyDataSetChanged();
                                            }
                                        }).show();
                                    }
                                });

                                mDialog.negativeActionClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        mDialog.dismiss();
                                    }
                                });
                                mDialog.show();


                            }
                        }).build().show();
            }
        });
    }

    private void setupListView() {
        items = new DataObjects(new DataObjects.myCallback() {
            @Override
            public void onAddandRemove(DataObjects obj) {
                FirebaseAccess.writeData(obj);
            }
        });
        listView = findViewById(R.id.listView);
        listView.setAdapter(adp = new ArrayAdapter<DataObject>(this, R.layout.child_item, items) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                if (convertView == null)
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.child_item, parent, false);

                DataObject currentItem = getItem(position);
                ((TextView) convertView.findViewById(R.id.item_name)).setText(currentItem.title);
                String time = currentItem.getDisplayString();
                ((TextView) convertView.findViewById(R.id.item_time)).setText(time);
                //Here we can do changes to the convertView, such as set a text on a TextView
                //or an image on an ImageView.
                return convertView;
            }

            @Override
            public int getCount() {
                if (items == null)
                    return 0;
                return items.size();
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, final View viewFather, final int i, long l) {
                final Dialog mDialog = new Dialog(MainActivity.this);
                mDialog.applyStyle(0)
                        .positiveAction("EDIT")
                        .negativeAction("REMOVE")
                        .title("Choose Action")
                        .positiveActionClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                //TODO edit
                            }
                        })
                        .negativeActionClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                removeItem(i, mDialog, viewFather);
                            }
                        })
                        .cancelable(true)
                        .show();
                return false;
            }
        });
    }

    private void removeItem(int i, Dialog mDialog, View viewFather) {
        final DataObject item = items.remove(i);
        mDialog.dismiss();
        adp.notifyDataSetChanged();
        showRemoveSnackbar(item, viewFather, i);
    }

    private void showRemoveSnackbar(final DataObject item, View viewFather, final int i) {
        Snackbar.make(viewFather, item.title + " Removed", Snackbar.LENGTH_LONG).setAction("UNDO", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                items.add(i, item);
                adp.notifyDataSetChanged();
            }
        }).show();
    }


}
