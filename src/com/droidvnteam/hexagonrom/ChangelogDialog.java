package com.droidvnteam.hexagonrom;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;
import com.droidvnteam.R;
/**
 * Created by Nhok on 6/11/2016.
 */
public class ChangelogDialog extends DialogFragment {

    private ChangelogDialog		dialog;
    public ChangelogDialog() {
        super();
        dialog = this;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_changelog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        builder.setTitle("Change log");

         WebView wv = (WebView) view.findViewById(R.id.webView);
        wv.loadUrl("file:///android_asset/changelog.html");
        return builder.create();
    }
}
