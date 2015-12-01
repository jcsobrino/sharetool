package jcsobrino.tddm.uoc.sharetool.view;

import android.app.Dialog;

/**
 * Notifica sobre la opción seleccioanda en un diálogo
 * Created by JoséCarlos on 22/11/2015.
 */
public interface NoticeDialogListener {

    public void onDialogPositiveClick(Dialog dialog);
    public void onDialogNegativeClick(Dialog dialog);
}
