package tk.pathfinder.UI;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

// Should not take 29 lines to do this...
public class Alert {
    private AlertDialog dialog;

    public Alert(String title, String message, Context context){
        AlertDialog.Builder bldr = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        dialog = bldr.create();
    }

    public void show(){
        dialog.show();
    }
}
