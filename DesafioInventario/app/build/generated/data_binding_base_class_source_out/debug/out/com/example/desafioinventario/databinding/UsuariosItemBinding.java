// Generated by view binder compiler. Do not edit!
package com.example.desafioinventario.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.desafioinventario.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class UsuariosItemBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final CardView cardView3;

  @NonNull
  public final View divider4;

  @NonNull
  public final ImageView imgUsuarioItem;

  @NonNull
  public final TextView lblRolesUsuarioItem;

  @NonNull
  public final TextView txtRolesItem;

  @NonNull
  public final TextView txtUsernameItem;

  private UsuariosItemBinding(@NonNull ConstraintLayout rootView, @NonNull CardView cardView3,
      @NonNull View divider4, @NonNull ImageView imgUsuarioItem,
      @NonNull TextView lblRolesUsuarioItem, @NonNull TextView txtRolesItem,
      @NonNull TextView txtUsernameItem) {
    this.rootView = rootView;
    this.cardView3 = cardView3;
    this.divider4 = divider4;
    this.imgUsuarioItem = imgUsuarioItem;
    this.lblRolesUsuarioItem = lblRolesUsuarioItem;
    this.txtRolesItem = txtRolesItem;
    this.txtUsernameItem = txtUsernameItem;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static UsuariosItemBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static UsuariosItemBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.usuarios_item, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static UsuariosItemBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.cardView3;
      CardView cardView3 = ViewBindings.findChildViewById(rootView, id);
      if (cardView3 == null) {
        break missingId;
      }

      id = R.id.divider4;
      View divider4 = ViewBindings.findChildViewById(rootView, id);
      if (divider4 == null) {
        break missingId;
      }

      id = R.id.imgUsuarioItem;
      ImageView imgUsuarioItem = ViewBindings.findChildViewById(rootView, id);
      if (imgUsuarioItem == null) {
        break missingId;
      }

      id = R.id.lblRolesUsuarioItem;
      TextView lblRolesUsuarioItem = ViewBindings.findChildViewById(rootView, id);
      if (lblRolesUsuarioItem == null) {
        break missingId;
      }

      id = R.id.txtRolesItem;
      TextView txtRolesItem = ViewBindings.findChildViewById(rootView, id);
      if (txtRolesItem == null) {
        break missingId;
      }

      id = R.id.txtUsernameItem;
      TextView txtUsernameItem = ViewBindings.findChildViewById(rootView, id);
      if (txtUsernameItem == null) {
        break missingId;
      }

      return new UsuariosItemBinding((ConstraintLayout) rootView, cardView3, divider4,
          imgUsuarioItem, lblRolesUsuarioItem, txtRolesItem, txtUsernameItem);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
