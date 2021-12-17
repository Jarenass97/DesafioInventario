// Generated by view binder compiler. Do not edit!
package com.example.desafioinventario.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.desafioinventario.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class UsuariosModifierBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final CheckBox ckbEncargadoModifier;

  @NonNull
  public final CheckBox ckbJefeModifier;

  @NonNull
  public final CheckBox ckbProfesorModifier;

  @NonNull
  public final EditText edEmailModifier;

  @NonNull
  public final EditText edPassModifier;

  @NonNull
  public final EditText edUsernameModifier;

  private UsuariosModifierBinding(@NonNull ConstraintLayout rootView,
      @NonNull CheckBox ckbEncargadoModifier, @NonNull CheckBox ckbJefeModifier,
      @NonNull CheckBox ckbProfesorModifier, @NonNull EditText edEmailModifier,
      @NonNull EditText edPassModifier, @NonNull EditText edUsernameModifier) {
    this.rootView = rootView;
    this.ckbEncargadoModifier = ckbEncargadoModifier;
    this.ckbJefeModifier = ckbJefeModifier;
    this.ckbProfesorModifier = ckbProfesorModifier;
    this.edEmailModifier = edEmailModifier;
    this.edPassModifier = edPassModifier;
    this.edUsernameModifier = edUsernameModifier;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static UsuariosModifierBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static UsuariosModifierBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.usuarios_modifier, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static UsuariosModifierBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.ckbEncargadoModifier;
      CheckBox ckbEncargadoModifier = ViewBindings.findChildViewById(rootView, id);
      if (ckbEncargadoModifier == null) {
        break missingId;
      }

      id = R.id.ckbJefeModifier;
      CheckBox ckbJefeModifier = ViewBindings.findChildViewById(rootView, id);
      if (ckbJefeModifier == null) {
        break missingId;
      }

      id = R.id.ckbProfesorModifier;
      CheckBox ckbProfesorModifier = ViewBindings.findChildViewById(rootView, id);
      if (ckbProfesorModifier == null) {
        break missingId;
      }

      id = R.id.edEmailModifier;
      EditText edEmailModifier = ViewBindings.findChildViewById(rootView, id);
      if (edEmailModifier == null) {
        break missingId;
      }

      id = R.id.edPassModifier;
      EditText edPassModifier = ViewBindings.findChildViewById(rootView, id);
      if (edPassModifier == null) {
        break missingId;
      }

      id = R.id.edUsernameModifier;
      EditText edUsernameModifier = ViewBindings.findChildViewById(rootView, id);
      if (edUsernameModifier == null) {
        break missingId;
      }

      return new UsuariosModifierBinding((ConstraintLayout) rootView, ckbEncargadoModifier,
          ckbJefeModifier, ckbProfesorModifier, edEmailModifier, edPassModifier,
          edUsernameModifier);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
