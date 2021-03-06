// Generated by view binder compiler. Do not edit!
package com.example.desafioinventario.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.desafioinventario.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class DispositivosItemBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final View divider5;

  @NonNull
  public final TextView txtParam1Dispositivo;

  @NonNull
  public final TextView txtParam2Dispositivo;

  private DispositivosItemBinding(@NonNull ConstraintLayout rootView, @NonNull View divider5,
      @NonNull TextView txtParam1Dispositivo, @NonNull TextView txtParam2Dispositivo) {
    this.rootView = rootView;
    this.divider5 = divider5;
    this.txtParam1Dispositivo = txtParam1Dispositivo;
    this.txtParam2Dispositivo = txtParam2Dispositivo;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static DispositivosItemBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static DispositivosItemBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.dispositivos_item, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static DispositivosItemBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.divider5;
      View divider5 = ViewBindings.findChildViewById(rootView, id);
      if (divider5 == null) {
        break missingId;
      }

      id = R.id.txtParam1Dispositivo;
      TextView txtParam1Dispositivo = ViewBindings.findChildViewById(rootView, id);
      if (txtParam1Dispositivo == null) {
        break missingId;
      }

      id = R.id.txtParam2Dispositivo;
      TextView txtParam2Dispositivo = ViewBindings.findChildViewById(rootView, id);
      if (txtParam2Dispositivo == null) {
        break missingId;
      }

      return new DispositivosItemBinding((ConstraintLayout) rootView, divider5,
          txtParam1Dispositivo, txtParam2Dispositivo);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
