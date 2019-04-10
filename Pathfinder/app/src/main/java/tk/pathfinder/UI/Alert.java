package tk.pathfinder.UI;

import android.app.AlertDialog;
import android.content.Context;

/**
 * An Android alert dialog with an OK button.
 */
// Should not take 29 lines to do this...
public class Alert {
    private AlertDialog dialog;

    public Alert(String title, String message, Context context){
        AlertDialog.Builder bldr = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        dialog = bldr.create();
    }

    public void show(){
        dialog.show();
    }
}
