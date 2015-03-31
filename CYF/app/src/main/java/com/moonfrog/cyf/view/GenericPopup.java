package com.moonfrog.cyf.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.moonfrog.cyf.R;

/**
 * Created by srinath on 31/03/15.
 */
public class GenericPopup extends Dialog {
    public String mainText = "";
    public boolean showFBButtonInsteadOfClose = false;
    public String closeButtonText = "";

    private Context parentContext = null;

    private OnPopupCloseListener closeListener;

    public GenericPopup(final Context context) {
        super(context);
        parentContext = context;
        initialize();
    }

    public GenericPopup(final Context context, String mainText_val) {
        super(context);
        parentContext = context;
        mainText = mainText_val;
        initialize();
    }

    public GenericPopup(final Context context, String mainText_val, boolean showFBButtonInsteadOfClose_val) {
        super(context);
        parentContext = context;
        mainText = mainText_val;
        showFBButtonInsteadOfClose = showFBButtonInsteadOfClose_val;
        initialize();
    }

    public GenericPopup(final Context context, String mainText_val, boolean showFBButtonInsteadOfClose_val, String closeButtonText_val) {
        super(context);
        parentContext = context;
        mainText = mainText_val;
        showFBButtonInsteadOfClose = showFBButtonInsteadOfClose_val;
        closeButtonText = closeButtonText_val;
        initialize();
    }

    private void initialize() {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //this.setTitle("You lost!");
        this.setContentView(R.layout.generic_popup);

        TextView text = (TextView)findViewById(R.id.txtView);
        text.setText(mainText);

        Button dialogButton = (Button)findViewById(R.id.btn_close_popup);
        if(showFBButtonInsteadOfClose) {
            dialogButton.setText("");
            dialogButton.setBackgroundDrawable(parentContext.getResources().getDrawable(R.drawable.messenger_button_white_bg_round));
        }
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });

        this.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                close();
            }
        });
    }

    public void close() {
        dismiss();
        closeListener.OnClose(this);
    }

    public void setOnPopupCloseListener(OnPopupCloseListener listener) {
        closeListener = listener;
    }

    public static interface OnPopupCloseListener {
        void OnClose(GenericPopup popup);
    }
}
