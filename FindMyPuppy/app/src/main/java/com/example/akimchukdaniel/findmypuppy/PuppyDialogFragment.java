package com.example.akimchukdaniel.findmypuppy;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PuppyDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("We found a potential match! Would like you to view the details?")
                .setPositiveButton("Take me to the puppy!", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Cursor c = ((MakeReport)getActivity()).c;
                        Intent intent = new Intent(getActivity(), PuppyDetailActivity.class);
                        intent.putExtra("id", c.getInt(c.getColumnIndex(LostPuppy.LostPuppyEntry._ID)));
                        intent.putExtra("name", c.getString(c.getColumnIndex(LostPuppy.LostPuppyEntry.COLUMN_NAME_NAME)));
                        intent.putExtra("breed", c.getString(c.getColumnIndex(LostPuppy.LostPuppyEntry.COLUMN_NAME_BREED)));
                        intent.putExtra("fur", c.getString(c.getColumnIndex(LostPuppy.LostPuppyEntry.COLUMN_NAME_FUR_COLOR)));
                        intent.putExtra("sex", c.getString(c.getColumnIndex(LostPuppy.LostPuppyEntry.COLUMN_NAME_SEX)));
                        intent.putExtra("eye", c.getString(c.getColumnIndex(LostPuppy.LostPuppyEntry.COLUMN_NAME_EYE)));
                        intent.putExtra("location", c.getString(c.getColumnIndex(LostPuppy.LostPuppyEntry.COLUMN_NAME_LAST_LOCATION)));
                        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                        String[] dateStr = c.getString(c.getColumnIndex(LostPuppy.LostPuppyEntry.COLUMN_NAME_LAST_TIME)).split(",");
                        Date date = new Date(Integer.parseInt(dateStr[2]) - 1900, Integer.parseInt(dateStr[0]), Integer.parseInt(dateStr[1]));
                        intent.putExtra("date", sdf.format(date));
                        intent.putExtra("lostfound", c.getString(c.getColumnIndex(LostPuppy.LostPuppyEntry.COLUMN_NAME_LOSTFOUND)));
                        intent.putExtra("phone", c.getString(c.getColumnIndex(LostPuppy.LostPuppyEntry.COLUMN_NAME_PHONE)));
                        intent.putExtra("reporter", c.getString(c.getColumnIndex(LostPuppy.LostPuppyEntry.COLUMN_NAME_REPORTER)));
                        startActivity(intent);
                    }
                })
                .setNegativeButton("No thanks", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
